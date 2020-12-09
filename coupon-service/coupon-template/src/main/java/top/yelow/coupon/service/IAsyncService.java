package top.yelow.coupon.service;

import top.yelow.coupon.entity.CouponTemplate;

/**
 * @ClassName: IAsyncService
 * @Author: 耶low
 * @Date: 2020-11-08 11:12
 * @Version: 1.0
 * 异步服务接口定义
 */
public interface IAsyncService {
    //根据模板异步创建优惠券码
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
