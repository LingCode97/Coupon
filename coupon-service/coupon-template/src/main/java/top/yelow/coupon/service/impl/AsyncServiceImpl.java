package top.yelow.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.yelow.coupon.constant.Constant;
import top.yelow.coupon.dao.CouponTemplateDao;
import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.service.IAsyncService;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: AsyncServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-09 16:03
 * @Version: 1.0
 */
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {
    private final CouponTemplateDao templateDao;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> couponCodes = buildCouponCode(template);
        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE, template.getId().toString());
        log.info("Push CouponCode to Redis:{}", redisTemplate.opsForList().rightPushAll(redisKey, couponCodes));

        template.setAvailable(true);
        templateDao.save(template);

        watch.stop();
        log.info("Construct CouponCode by Template Cost:{}",watch.elapsed(TimeUnit.MILLISECONDS));

        //TODO 可以通知相关人员，优惠券模板已经可以使用
    }

    //构造优惠券码
    private Set<String> buildCouponCode(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());

        //前四位
        String prefix4 = template.getProductLine().getCode().toString()
                + template.getCategory().getCode();
        //时间
        String date = new SimpleDateFormat("yyyyMMdd").format(template.getCreateTime());

        for (int i = 0; i != template.getCount(); ++i) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        while (result.size() < template.getCount()) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        watch.stop();
        log.info("Build Coupon Code Cost:{}ms", watch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    //构建后14位，根据时间和随机值来构建
    private String buildCouponCodeSuffix14(String date) {
        char[] base = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        //获取date里面的每个字符，这是java8的写法
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        //shuffle算法可以打乱集合里面的字符
        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Object::toString).collect(Collectors.joining());

        String suffix8 = RandomStringUtils.random(1, base) + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
