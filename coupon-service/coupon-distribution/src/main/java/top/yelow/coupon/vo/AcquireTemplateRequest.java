package top.yelow.coupon.vo;

import lombok.Data;

/**
 * @ClassName: AcquireTemplateRequest
 * @Author: 耶low
 * @Date: 2020-11-13 15:44
 * @Version: 1.0
 * 获取优惠券的请求对象定义
 */
@Data
public class AcquireTemplateRequest {
    //用户id
    private Long userId;
    //优惠券模板信息
    private CouponTemplateSDK templateSDK;

}
