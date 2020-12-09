package top.yelow.coupon.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.yelow.coupon.constant.RuleFlag;
import top.yelow.coupon.executor.AbstractExecutor;
import top.yelow.coupon.executor.RuleExecutor;
import top.yelow.coupon.vo.CouponTemplateSDK;
import top.yelow.coupon.vo.SettlementInfo;

/**
 * @ClassName: ZheKouExecutor
 * @Author: 耶low
 * @Date: 2020-11-26 11:25
 * @Version: 1.0
 */
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * <h2>优惠券规则计算</h2>
     * @param settlementInfo {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum=retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability=processGoodsTypeNotSatisfy(settlementInfo,goodsSum);
        //判断商品类型是否符合优惠券类型要求
        if(null!=probability){
            log.debug("ZheKou template is not match goodsType");
            return probability;
        }

        //获取折扣
        CouponTemplateSDK templateSDK=settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate();
        double quota=(double) templateSDK.getRule().getDiscount().getQuota();

        //折扣优惠券没有满减那样的门槛，直接使用
        settlementInfo.setCost(
                retain2Decimals((goodsSum*(quota*1.0/100)))>minCost()?
                        retain2Decimals((goodsSum*(quota*1.0/100))):minCost()
        );
        return settlementInfo;
    }
}
