package top.yelow.coupon.service;

import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @interfaceName: ITemplateBaseService
 * @Author: 耶low
 * @Date: 2020-11-08 15:23
 * @Version: 1.0
 * 优惠券基础服务接口定义(CURD)
 */
public interface ITemplateBaseService {
    /**
     * 根据优惠券模板id获取模板信息
     * @param id 模板id
     * @return {@link CouponTemplate} 优惠券模板实体类
     * */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * 查找所有可用的优惠券模板
     * @return {@link List<CouponTemplateSDK>}
     * */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板ids到CouponTempLateSDK的映射
     * @param ids 模板的id
     * @return {@link Map<Integer,CouponTemplateSDK>}
     * */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
