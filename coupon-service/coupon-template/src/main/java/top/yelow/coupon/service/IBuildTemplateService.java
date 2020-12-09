package top.yelow.coupon.service;

import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.vo.TemplateRequest;

/**
 * @interfaceName: IBuildTemplateService
 * @Author: 耶low
 * @Date: 2020-11-08 10:58
 * @Version: 1.0
 * 优惠券模板接口定义
 */
public interface IBuildTemplateService {
    //创建优惠券模板
    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;


}
