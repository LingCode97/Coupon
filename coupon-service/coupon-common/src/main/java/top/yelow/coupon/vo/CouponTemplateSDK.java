package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CouponTemplateSDK
 * @Author: 耶low
 * @Date: 2020-11-08 15:18
 * @Version: 1.0
 * 微服务之间用的优惠券模板信息定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {
    private Integer id;
    private String name;
    private String logo;
    private String desc;
    private String category;
    private Integer productLine;
    private String key;
    private Integer target;
    private TemplateRule rule;
}
