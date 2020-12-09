package top.yelow.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @ClassName: AbstractPreZuulFilter
 * @Author: 耶low
 * @Date: 2020-11-04 10:24
 * @Version: 1.0
 */
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
