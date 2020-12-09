package top.yelow.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import top.yelow.coupon.constant.Constant;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.dao.CouponDao;
import top.yelow.coupon.entity.Coupon;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.feign.SettlementClient;
import top.yelow.coupon.feign.TemplateClient;
import top.yelow.coupon.service.IRedisService;
import top.yelow.coupon.service.IUserService;
import top.yelow.coupon.vo.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: UserServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-25 14:48
 * @Version: 1.0
 * 用户服务相关的接口实现
 * 所有的操作过程，状态都保存在Redis中，并通过Kafka把消息保存到MySQL中
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    private final CouponDao couponDao;
    private final IRedisService redisService;
    private final TemplateClient templateClient;
    private final SettlementClient settlementClient;
    //Kafka客户端
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService, TemplateClient templateClient, SettlementClient settlementClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    //根据userId和status获取优惠券信息
    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupon(userId, status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)) {
            log.debug("coupon cache is not empty:{},{}", userId, status);
            preTarget = curCached;
        } else {
            log.debug("coupon cache is empty,get coupon form db:{},{}", userId, status);
            //如果从缓存中查不到优惠券信息，就从数据库中查询
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            if (CollectionUtils.isEmpty(dbCoupons)) {
                log.debug("current user do not have coupon:{},{}", userId, status);
                return dbCoupons;
            }
            //填充dbCoupons的templateSDK字段（因为Coupon实体类中templateSDK字段并不保存到数据库，只是存在缓存中）
            Map<Integer, CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
            ).getData();
            dbCoupons.forEach(dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId())));
            preTarget = dbCoupons;
            //将数据库中的数据保存到缓存
            redisService.addCouponToCache(userId, preTarget, status);
        }

        //剔除无效优惠券(存在id=-1的)
        preTarget = preTarget.stream().filter(c -> c.getId() != -1).collect(Collectors.toList());
        //如果我们获取的时可用的优惠券，可用优惠券里面可能会存在过期的优惠券，所以我们需要剔除
        if (CouponStatus.of(status) == CouponStatus.USABLE) {
            CouponClassify classify = CouponClassify.classify(preTarget);
            //如果可用优惠券里面存在过期的就需要把过期的存到缓存里面
            if (CollectionUtils.isNotEmpty(classify.getExpired())) {
                log.info("Add expired coupons to cache from FindCouponsByStatus:{},{}", userId, status);
                redisService.addCouponToCache(userId, classify.getExpired(), CouponStatus.EXPIRED.getCode());
                //并用kafka做异步处理
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.EXPIRED.getCode(),
                        classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
                )));
            }
            return classify.getUsable();
        }
        return preTarget;
    }

    //根据userID查询可以领取的优惠券
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKs = templateClient.finAllUsableTemplate().getData();

        //过滤过期的优惠券模板
        templateSDKs.stream().filter(t -> t.getRule().getExpiration().getDeadline() > curTime).collect(Collectors.toList());
        log.info("Find usable template count:{}", templateSDKs.size());

        Map<Integer, Pair<Integer, CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKs.size());
        templateSDKs.forEach(
                t -> limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(), t)
                )
        );
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponByStatus(userId, CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));

        //根据template的Rule判断是否可以领取优惠券
        limit2Template.forEach((k,v)->{
            int limitation=v.getLeft();
            CouponTemplateSDK templateSDK=v.getRight();
            if(templateId2Coupons.containsKey(k)&&templateId2Coupons.get(k).size()>=limitation){
                return;
            }
            result.add(templateSDK);
        });

        return result;
    }

    //用户领取优惠券
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {

        Map<Integer,CouponTemplateSDK> id2Template=templateClient.findIds2TemplateSDK(
                Collections.singletonList(request.getTemplateSDK().getId())
        ).getData();

        //检查传入的参数是否合法
        //1.优惠券模板是否存在
        if(id2Template.size()<=0){
            log.error("Can not acquire template from templateClient:{}",request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire template from templateClient");
        }
        //2.用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons=findCouponByStatus(
                request.getUserId(),CouponStatus.USABLE.getCode()
        );
        Map<Integer,List<Coupon>> templateId2Coupons=userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        //如果当前用户已经拥有这张优惠券且超过领取上限，就不能领取
        if(templateId2Coupons.containsKey(request.getTemplateSDK().getId())
                &&templateId2Coupons.get(request.getTemplateSDK().getId()).size()
                >=request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed template assign limitation:{}",request.getTemplateSDK().getId());
            throw new CouponException("Exceed template assign limitation");
        }

        //通过前面的合法性校验，开始获取优惠码
        String couponCode=redisService.tryToAcquireCouponCodeFromCache(request.getTemplateSDK().getId());
        //如果没有领取到优惠券，就抛出异常（比如抢某种有限制数量的优惠券，可能会出现领取失败的情况）
        if(StringUtils.isEmpty(couponCode)){
            log.error("Can not acquire coupon code:{}",request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire coupon code");
        }
        //领取到优惠券码后开始构造优惠券
        Coupon newCoupon=new Coupon(
                request.getTemplateSDK().getId(),request.getUserId(),couponCode,CouponStatus.USABLE
        );
        //保存到数据库，并返回主键
        newCoupon=couponDao.save(newCoupon);

        //填充Coupon对象的CouponTemplateSDK
        newCoupon.setTemplateSDK(request.getTemplateSDK());

        redisService.addCouponToCache(
                request.getUserId(),Collections.singletonList(newCoupon),CouponStatus.USABLE.getCode()
        );

        return newCoupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
