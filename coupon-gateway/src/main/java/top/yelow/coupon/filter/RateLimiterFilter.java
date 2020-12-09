package top.yelow.coupon.filter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: RateLimiterFilter
 * @Author: 耶low
 * @Date: 2020-11-04 10:39
 * @Version: 1.0
 * @作用: 限流过滤器
 */
@Slf4j
@Component
public class RateLimiterFilter extends AbstractPreZuulFilter {
    //限流器
    RateLimiter rateLimiter=RateLimiter.create(2.0);
    @Override
    protected Object cRun() {
        HttpServletRequest request=context.getRequest();
        if(rateLimiter.tryAcquire()){
            log.info("get rate token success");
            return success();
        }else {
            log.error("rate limit:{}",request.getRequestURI());
            return fail(402,"error:rate limit");
        }
    }

    @Override
    public int filterOrder() {
        return 2;
    }
}
