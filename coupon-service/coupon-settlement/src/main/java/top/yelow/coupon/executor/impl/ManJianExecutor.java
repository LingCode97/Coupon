package top.yelow.coupon.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.yelow.coupon.constant.RuleFlag;
import top.yelow.coupon.executor.AbstractExecutor;
import top.yelow.coupon.executor.RuleExecutor;
import top.yelow.coupon.vo.CouponTemplateSDK;
import top.yelow.coupon.vo.SettlementInfo;

import java.util.Collections;

/**
 * @ClassName: ManJianExecutor
 * @Author: 耶low
 * @Date: 2020-11-26 11:03
 * @Version: 1.0
 * 满减优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * 1.首先判断商品类型是否符合优惠券限制类型
     * 2.如果符合类型要求，接着判断商品价格是否满足满减要求
     * 3.如果不满足满减要求就直接返回原件，否则就返回满减后的金额
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));

        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo, goodsSum);
        //如果不符合满减要求
        if (null != probability) {
            log.debug("ManJian Template is not match to goodsType!");
            return probability;
        }
        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate();
        double base = (double) templateSDK.getRule().getDiscount().getBase();
        double quota= (double)templateSDK.getRule().getDiscount().getQuota();

        //如果不合符标准，直接返回商品总价
        if(goodsSum<base){
            log.debug("Current goods cost sum<Manjian coupon base!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.EMPTY_LIST);
            return settlementInfo;
        }

        //如果符合标准
        settlementInfo.setCost(retain2Decimals(
                (goodsSum-quota)>minCost()?goodsSum-quota:minCost()
        ));

        return settlementInfo;
    }
}
