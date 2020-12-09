package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import top.yelow.coupon.constant.CouponCategory;
import top.yelow.coupon.constant.DistributeTarget;
import top.yelow.coupon.constant.ProductLine;

/**
 * @ClassName: TemplateRequest
 * @Author: 耶low
 * @Date: 2020-11-08 10:47
 * @Version: 1.0
 * 优惠券模板创建的请求对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {
    private String name;
    private String logo;
    //描述
    private String desc;

    //优惠券分类
    private String category;

    private Integer productLine;

    private Integer count;

    private Long userId;

    //分发的目标用户
    private Integer target;

    //优惠券的规则
    private TemplateRule rule;

    //校验对象的合法性
    public boolean validate() {
        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        return stringValid && enumValid && count > 0 && userId > 0 && rule.validate();
    }
}
