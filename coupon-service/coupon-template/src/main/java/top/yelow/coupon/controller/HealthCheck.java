package top.yelow.coupon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yelow.coupon.exception.CouponException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: HealthCheck
 * @Author: 耶low
 * @Date: 2020-11-11 09:31
 * @Version: 1.0
 * 健康检查接口
 */
@Slf4j
@RestController
public class HealthCheck {

    private final DiscoveryClient client;

    private final Registration registration;
    @Autowired
    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    /**
     * 健康检查接口
     * 127.0.0.1:7001/coupon-template/health
     * */
    @GetMapping("/health")
    public String health(){
        log.debug("view health api");
        return "CouponTemplate Is OK!";
    }

    @GetMapping("/exception")
    public String exception() throws CouponException{
        log.debug("view exception api");
        throw new CouponException("CouponTemplate Has Some Problem");
    }

    @GetMapping("/info")
    public List<Map<String,Object>> info(){
        //大约需要2分钟才能获取到服务注册信息
        List<ServiceInstance> instances=client.getInstances(registration.getServiceId());
        List<Map<String,Object>> result=new ArrayList<>(instances.size());
        instances.forEach(i->{
            Map<String,Object> info=new HashMap<>();
            info.put("serviceId",i.getServiceId());
            info.put("instanceId",i.getInstanceId());
            info.put("port",i.getPort());
            info.put("host",i.getHost());
            result.add(info);
        });
        return result;
    }

}
