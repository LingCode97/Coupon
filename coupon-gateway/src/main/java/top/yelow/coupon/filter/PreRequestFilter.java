package top.yelow.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: PreRequestFilter
 * @Author: 耶low
 * @Date: 2020-11-04 10:51
 * @Version: 1.0
 * @作用: 存储客户端发起请求的时间戳
 */
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter {
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        return null;
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
