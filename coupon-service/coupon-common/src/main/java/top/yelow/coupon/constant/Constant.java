package top.yelow.coupon.constant;

/**
 * @ClassName: Constant
 * @Author: 耶low
 * @Date: 2020-11-09 15:57
 * @Version: 1.0
 * 通用常量定义
 */
public class Constant {
    //kafka消息的Topic
    public static final String TOPIC="user_coupon_op";

    //Redis Key前缀定义
    public static class RedisPrefix{
        //优惠券码key前缀
        public static final String COUPON_TEMPLATE="coupon_template_code_";
        //可用优惠券key前缀
        public static final String USER_COUPON_USABLE="user_coupon_usable_";
        //已用优惠券前缀
        public static final String USER_COUPON_USED="user_coupon_used_";
        //已过期优惠券的前缀
        public static final String USER_COUPON_EXPIRED="user_coupon_expired_";
    }
}
