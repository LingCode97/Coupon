package top.yelow.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: AccessLogFilter
 * @Author: 耶low
 * @Date: 2020-11-04 11:04
 * @Version: 1.0
 */
@Slf4j
@Component
public class AccessLogFilter extends AbstractPostZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request=context.getRequest();
        Long startTime=(Long)context.get("startTime");
        String uri=request.getRequestURI();
        long duration=System.currentTimeMillis()-startTime;
        //从网关通过的请求都会打印日志记录，包含uri和请求时延
        log.info("uri:{},duration:{}",uri,duration);
        return success();
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER;
    }
}
