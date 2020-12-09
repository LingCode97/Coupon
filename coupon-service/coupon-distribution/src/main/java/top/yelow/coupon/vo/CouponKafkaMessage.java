package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: CouponKafkaMessage
 * @Author: 耶low
 * @Date: 2020-11-16 10:29
 * @Version: 1.0
 * 优惠券Kafka消息对象定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponKafkaMessage {
    //优惠券状态
    private Integer status;
    //Coupon主键
    private List<Integer> ids;

}
