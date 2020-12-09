package top.yelow.coupon.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.yelow.coupon.entity.CouponTemplate;

import java.util.List;

/**
 * @interfaceName: CouponTemplateDao
 * @Author: 耶low
 * @Date: 2020-11-08 10:37
 * @Version: 1.0
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {
    //根据模板名称查询模板
    CouponTemplate findByName(String name);

    //查询所有有效的模板信息
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available,Boolean expired);

    //根据有效期查询模板信息
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
