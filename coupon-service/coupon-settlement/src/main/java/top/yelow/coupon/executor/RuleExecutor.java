package top.yelow.coupon.executor;

import top.yelow.coupon.constant.RuleFlag;
import top.yelow.coupon.vo.SettlementInfo;

/**
 * @interfaceName: RuleExecutor
 * @Author: 耶low
 * @Date: 2020-11-26 10:31
 * @Version: 1.0
 * 优惠券模板规则处理器接口定义
 */
public interface RuleExecutor {
    /**
     * 规则类型标记
     * */
    RuleFlag ruleConfig();

    /**
     * <h2>优惠券规则计算</h2>
     * @param settlementInfo {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * */
    SettlementInfo computeRule(SettlementInfo settlementInfo);
}
