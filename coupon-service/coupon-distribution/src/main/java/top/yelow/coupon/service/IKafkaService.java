package top.yelow.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @interfaceName: IKafkaService
 * @Author: 耶low
 * @Date: 2020-11-12 11:28
 * @Version: 1.0
 * Kafka相关的服务接口定义
 */
public interface IKafkaService {

    //消费优惠券Kafka消息
    void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record);
}
