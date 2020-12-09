package top.yelow.coupon.service;

import top.yelow.coupon.entity.Coupon;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.vo.AcquireTemplateRequest;
import top.yelow.coupon.vo.CouponTemplateSDK;
import top.yelow.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * @interfaceName: IUserService
 * @Author: 耶low
 * @Date: 2020-11-13 15:37
 * @Version: 1.0
 * 用户服务相关的接口定义
 */
public interface IUserService {

    //根据用户id和状态查询优惠券记录
    List<Coupon> findCouponByStatus(Long userId,Integer status) throws CouponException;

    //查看当前用户可以领取的优惠券(因为还没领取，所以查看的是优惠券模板信息)
    List<CouponTemplateSDK>  findAvailableTemplate(Long userId) throws CouponException;

    //用户领取优惠券
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    //用户结算(核销)                                                                                                                             )优惠券
    SettlementInfo settlement(SettlementInfo info) throws CouponException;
}
