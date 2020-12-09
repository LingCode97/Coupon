package top.yelow.coupon.converter;

import com.alibaba.fastjson.JSON;
import top.yelow.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @ClassName: RuleConverter
 * @Author: 耶low
 * @Date: 2020-11-08 10:07
 * @Version: 1.0
 */
@Converter
public class RuleConverter implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule rule) {
        return JSON.toJSONString(rule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String s) {
        return JSON.parseObject(s,TemplateRule.class);
    }
}
