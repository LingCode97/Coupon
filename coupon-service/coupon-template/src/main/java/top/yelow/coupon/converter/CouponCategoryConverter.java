package top.yelow.coupon.converter;

import top.yelow.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @ClassName: CouponCategoryConverter
 * @Author: 耶low
 * @Date: 2020-11-08 09:59
 * @Version: 1.0
 */
@Converter
public class CouponCategoryConverter implements AttributeConverter<CouponCategory,String> {
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}
