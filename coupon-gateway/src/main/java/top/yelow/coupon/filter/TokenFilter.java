package top.yelow.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: TokenFilter
 * @Author: 耶low
 * @Date: 2020-11-04 10:30
 * @Version: 1.0
 * 验证请求Token
 */
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        HttpServletRequest request=context.getRequest();
        log.info(String.format("%s request to %s",request.getMethod(),request.getRequestURL().toString()));
        Object token=request.getParameter("token");
        if(null==token) {
            log.error("error token is empty");
            return fail(401,"error token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
