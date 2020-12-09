package top.yelow.coupon.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.yelow.coupon.feign.hystrix.TemplateClientHystrix;
import top.yelow.coupon.vo.CommonResponse;
import top.yelow.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @interfaceName: TemplateClient
 * @Author: 耶low
 * @Date: 2020-11-19 11:21
 * @Version: 1.0
 * 优惠券模板微服务 Feign接口定义
 */

@FeignClient(value = "eureka-client-coupon-template",fallback = TemplateClientHystrix.class)
public interface TemplateClient {

    /**
     * 查找所有可用的优惠券模板
     * */
    @RequestMapping(value = "/coupon-template/template/sdk/all",method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> finAllUsableTemplate();

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     * */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",method = RequestMethod.GET)
    CommonResponse<Map<Integer,CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids);


}
