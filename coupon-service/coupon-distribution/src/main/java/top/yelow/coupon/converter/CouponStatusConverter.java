package top.yelow.coupon.converter;

import top.yelow.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @ClassName: CouponStatusConverter
 * @Author: 耶low
 * @Date: 2020-11-12 10:56
 * @Version: 1.0
 * 优惠券状态枚举属性转换器
 */
@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer integer) {
        return CouponStatus.of(integer);
    }
}
