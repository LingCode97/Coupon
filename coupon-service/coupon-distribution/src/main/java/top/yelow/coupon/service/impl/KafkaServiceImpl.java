package top.yelow.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import top.yelow.coupon.constant.Constant;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.dao.CouponDao;
import top.yelow.coupon.entity.Coupon;
import top.yelow.coupon.service.IKafkaService;
import top.yelow.coupon.vo.CouponKafkaMessage;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName: KafkaServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-16 10:23
 * @Version: 1.0
 * Kafka相关服务接口实现
 * 作用是将缓存中的信息异步的同步到DB中
 */
@Service
@Slf4j
public class KafkaServiceImpl implements IKafkaService {

    private final CouponDao couponDao;

    @Autowired
    public KafkaServiceImpl(CouponDao couponDao) {
        this.couponDao = couponDao;
    }

    @Override
    @KafkaListener(topics = {Constant.TOPIC},groupId = "coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage=Optional.ofNullable(record.value());
        //如果存在消息
        if(kafkaMessage.isPresent()){
            Object message=kafkaMessage.get();
            CouponKafkaMessage couponKafkaMessage= JSON.parseObject(
                    message.toString(),
                    CouponKafkaMessage.class
            );
            log.info("Receive couponKafkaMessage:{}",message.toString());
            CouponStatus status=CouponStatus.of(couponKafkaMessage.getStatus());
            switch (status){
                case EXPIRED:
                    processExpiredCoupons(couponKafkaMessage,status);
                    break;
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponKafkaMessage,status);
                    break;
            }
        }
    }

    /**
     * 处理已使用的优惠券
     * */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage,CouponStatus status){
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     * 处理已过期的优惠券
     * */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage,CouponStatus status){
        processCouponsByStatus(kafkaMessage,status);
    }

    /**
     * 根据状态去处理优惠券信息
     * */
    private void processCouponsByStatus(CouponKafkaMessage kafkaMessage,CouponStatus status){
        List<Coupon> coupons=couponDao.findAllById(kafkaMessage.getIds());
        if(CollectionUtils.isEmpty(coupons)||coupons.size()!=kafkaMessage.getIds().size()){
            log.error("Can not find right coupon info:{}",JSON.toJSONString(kafkaMessage));
            return;
        }
        //把查询出来的优惠券设置成对应的状态，再保存回数据库
        coupons.forEach(c->c.setStatus(status));
        log.info("CouponKafkaMessage op coupon count:{}",couponDao.saveAll(coupons).size());
    }
}
