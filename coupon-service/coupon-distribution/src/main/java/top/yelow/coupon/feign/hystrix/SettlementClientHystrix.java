package top.yelow.coupon.feign.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.feign.SettlementClient;
import top.yelow.coupon.vo.CommonResponse;
import top.yelow.coupon.vo.SettlementInfo;

/**
 * @ClassName: SettlementClientHystrix
 * @Author: 耶low
 * @Date: 2020-11-19 11:48
 * @Version: 1.0
 * 结算微服务Feign熔断降级策略
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);

        return new CommonResponse<>(
            -1,
            "[eureka-client-coupon-settlement] request error",
            settlementInfo
        );
    }
}
