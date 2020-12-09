package top.yelow.coupon.feign.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.yelow.coupon.feign.TemplateClient;
import top.yelow.coupon.vo.CommonResponse;
import top.yelow.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TemplateClientHystrix
 * @Author: 耶low
 * @Date: 2020-11-19 11:36
 * @Version: 1.0
 * 优惠券模板Feign接口的熔断降级策略
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {
    @Override
    public CommonResponse<List<CouponTemplateSDK>> finAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] finAllUsableTemplate request error");
        return new CommonResponse<>(
                -1, "[eureka-client-coupon-template] request error"
                , Collections.emptyList()
        );
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error");
        return new CommonResponse<>(
                -1, "[eureka-client-coupon-template] request error"
                , Collections.emptyMap()
        );
    }
}
