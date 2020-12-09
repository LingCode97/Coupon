package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: SettlementInfo
 * @Author: 耶low
 * @Date: 2020-11-13 15:58
 * @Version: 1.0
 * 结算信息对象定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {
    //用户id
    private Long userId;
    //优惠券列表
    private List<CouponAndTemplateInfo> couponAndTemplateInfos;
    //商品信息
    private List<GoodsInfo> goodsInfos;
    //结算金额
    private Double cost;
    //是否使结算生效，即核销
    private Boolean employ;

    //优惠券和模板信息
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CouponAndTemplateInfo{
        private Integer id;
        private CouponTemplateSDK template;
    }
}
