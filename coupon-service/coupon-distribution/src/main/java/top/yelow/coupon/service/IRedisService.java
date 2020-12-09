package top.yelow.coupon.service;

import top.yelow.coupon.entity.Coupon;
import top.yelow.coupon.exception.CouponException;

import java.util.List;

/**
 * @interfaceName: IRedisService
 * @Author: 耶low
 * @Date: 2020-11-12 11:17
 * @Version: 1.0
 * Redis相关的操作服务接口定义
 */
public interface IRedisService {

    //根据userId和状态找到缓存的优惠券列表数据
    List<Coupon> getCachedCoupon(Long userId,Integer status);

    //保存空的优惠券到缓存，解决缓存穿透的问题
    void saveEmptyCouponListToCache(Long userId,List<Integer> status);

    //尝试从缓存中获取CouponCode
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    //将优惠券保存到缓存中
    Integer addCouponToCache(Long userId,List<Coupon> coupons,Integer status) throws CouponException;
}
