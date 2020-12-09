package top.yelow.coupon.converter;

import top.yelow.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @ClassName: ProductLineConverter
 * @Author: 耶low
 * @Date: 2020-11-08 10:04
 * @Version: 1.0
 */
@Converter
public class ProductLineConverter implements AttributeConverter<ProductLine,Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer integer) {
        return ProductLine.of(integer);
    }
}
