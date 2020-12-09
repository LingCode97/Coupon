package top.yelow.coupon.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.feign.hystrix.SettlementClientHystrix;
import top.yelow.coupon.vo.CommonResponse;
import top.yelow.coupon.vo.SettlementInfo;

/**
 * @interfaceName: SettlementClient
 * @Author: 耶low
 * @Date: 2020-11-19 11:30
 * @Version: 1.0
 * 优惠券结算微服务Feign接口定义
 */
@FeignClient(value = "eureka-client-coupon-settlement",fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * 优惠券规则计算
     * */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException;
}
