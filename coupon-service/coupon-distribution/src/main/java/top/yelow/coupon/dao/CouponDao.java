package top.yelow.coupon.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.entity.Coupon;

import java.util.List;

/**
 * @interfaceName: CouponDao
 * @Author: 耶low
 * @Date: 2020-11-12 11:10
 * @Version: 1.0
 */
public interface CouponDao extends JpaRepository<Coupon,Integer> {

    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
