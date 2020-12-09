package top.yelow.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @ClassName: AbstractPostZuulFilter
 * @Author: 耶low
 * @Date: 2020-11-04 10:25
 * @Version: 1.0
 */
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
