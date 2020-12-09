package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import top.yelow.coupon.constant.PeriodType;

/**
 * @ClassName: TemplateRule
 * @Author: 耶low
 * @Date: 2020-11-06 15:47
 * @Version: 1.0
 * 优惠券规则对象定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule {

    //优惠券过期规则
    private Expiration expiration;

    //优惠券折扣规则
    private Discount discount;

    //每个人最多能领取几张
    private Integer limitation;

    //使用范围限制
    private Usage usage;

    //可以哪些优惠券叠加使用(同类型优惠券一定不能叠加)
    private String weight;

    //检测是否有效的方法
    public boolean validate() {
        return expiration.validate() && discount.validate() &&
                limitation > 0 && usage.validate() && StringUtils.isNotEmpty(weight);
    }

    //有效期规则
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Expiration {
        //对应到PeriodType的code字段
        private Integer period;
        //有效间隔：只对PeriodType中的SHIFT类型有效
        private Integer gap;
        //失效日期
        private Long deadline;

        boolean validate() {
            return null != PeriodType.of(period) && gap > 0 && deadline > 0;
        }
    }

    //折扣
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount {
        //额度要求,包括满减、折扣、立减
        private Integer quota;
        //基准额度，需要满多少才能减，只适用于满减类型
        private Integer base;

        boolean validate() {
            return quota > 0 && base > 0;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        //适用于哪个省份
        private String province;
        //适用于哪个城市
        private String city;
        //适用于哪个商品类型,如文娱、生鲜、家具等等。会为这个定义一个枚举
        private String goodsType;

        boolean validate() {
            return StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city) && StringUtils.isNotEmpty(goodsType);
        }
    }
}
