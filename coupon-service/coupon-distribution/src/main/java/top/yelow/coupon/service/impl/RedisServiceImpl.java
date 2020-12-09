package top.yelow.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.yelow.coupon.constant.Constant;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.entity.Coupon;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.service.IRedisService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: RedisServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-15 10:28
 * @Version: 1.0
 * Redis相关的服务接口实现
 */
@Service
@Slf4j
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Coupon> getCachedCoupon(Long userId, Integer status) {
        log.info("Get coupons from cache:{},{}", userId, status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStr = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o -> Objects.toString(o, null))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(couponStr)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStr.stream()
                .map(cs -> JSON.parseObject(cs, Coupon.class))
                .collect(Collectors.toList());
    }

    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save empty list to cache for user:{},Status:{}", userId, JSON.toJSONString(status));
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = status2RedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline exe result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, templateId.toString());
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire coupon code:{},{},{}", templateId, redisKey, couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add coupon to cache:{},{},{}", userId, JSON.toJSONString(coupons), status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case USABLE:
                result = addCouponToCacheForUsable(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 新增可用优惠券到缓冲中
     */
    private Integer addCouponToCacheForUsable(Long userId, List<Coupon> coupons) {
        log.debug("Add coupon to cache for usable");
        //创建一个Hash，以Hash的数据结构存到redis中
        Map<String, String> needCachedObject = new HashMap<>();
        //遍历Coupons，依次添加到hash表中
        coupons.forEach(c -> {
            needCachedObject.put(c.getId().toString(), JSON.toJSONString(c));
        });
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey, needCachedObject);
        log.info("Add {} coupons to cache:{},{}", needCachedObject.size(), userId, redisKey);

        //设置过期时间，第一个参数是key，第二个参数是根据自定义的方法获取随机过期时间，第三个参数是时间单位
        redisTemplate.expire(
                redisKey,
                getRandomExpirationTime(1, 2),
                TimeUnit.SECONDS
        );
        return needCachedObject.size();
    }

    /**
     * 将已使用的优惠券添加到缓存中
     */
    private Integer addCouponToCacheForUsed(Long userId, List<Coupon> coupons) throws CouponException {
        //如果status是used，代表用户的操作是使用当前的优惠券，将会影响到两个缓存信息：
        //1.USABLE,2.USED.因为使用之前一定是可用的
        log.debug("Add coupon to cache for used");
        Map<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        //构造USABLE和USED两个状态的redisKey
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        //获取用户当前可用的优惠券
        List<Coupon> curUsableCoupons = getCachedCoupon(userId, CouponStatus.USABLE.getCode());
        assert curUsableCoupons.size() > coupons.size();

        coupons.forEach(c -> needCachedForUsed.put(c.getId().toString(), JSON.toJSONString(c)));

        //校验当前优惠券是否与缓存匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        //isSubCollection的作用是判断paramIds是否是curUsableIds的子集
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons is not equal to cache:{},{},{}",
                    userId, JSON.toJSONString(paramIds), JSON.toJSONString(curUsableIds));
            throw new CouponException("CurCoupons is not equal to cache");
        }

        //paramIds为将要使用的优惠券，即传入进来的coupons转化为id的数据
        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已使用的优惠券添加到缓存
                redisOperations.opsForHash().putAll(redisKeyForUsed, needCachedForUsed);
                //2.可用的优惠券需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey.toArray());
                //3.重置过期时间
                redisOperations.expire(redisKeyForUsable, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForUsed, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("PipeLine exe result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 将过期的优惠券添加到缓存中
     * 过期的优惠券肯定是未使用的，因为已使用的优惠券永远是已使用，不存在过期，过期这个操作也会影响两个缓存
     */
    private Integer addCouponToCacheForExpired(Long userId, List<Coupon> coupons) throws CouponException {
        //影响到USABLE,EXPIRED
        Map<String, String> needCacheForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);
        //获取可用和过期优惠券信息
        List<Coupon> curUsableCoupons = getCachedCoupon(userId, CouponStatus.USABLE.getCode());

        coupons.forEach(c -> needCacheForExpired.put(
                c.getId().toString(), JSON.toJSONString(c)
        ));

        //校验当前优惠券参数是否与缓存中的数据匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(paramIds, curUsableIds)) {
            log.error("CurCoupons is not equal to cache:{},{},{}",
                    userId, JSON.toJSONString(curUsableIds), JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons is not equal to cache");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已过期的优惠券缓存起来
                redisOperations.opsForHash().putAll(redisKeyForExpired, needCacheForExpired);
                //2.可用的优惠券需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable, needCleanKey);
                //3.重置过期时间
                redisOperations.expire(redisKeyForExpired, getRandomExpirationTime(1, 2), TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("PipeLine exe Result:{}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    //根据status获取到对应的redisKey
    private String status2RedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USED, userId);
                break;
            case USABLE:
                redisKey = redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }

    //获取随机的过期时间，避免缓存雪崩
    private Long getRandomExpirationTime(Integer min, Integer max) {
        return RandomUtils.nextLong(min * 60 * 60, max * 60 * 60);
    }
}
