package top.yelow.coupon.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.yelow.coupon.annotation.IgnoreResponseAdvice;
import top.yelow.coupon.vo.CommonResponse;

/**
 * @ClassName: CommonResponseDataAdvice
 * @Author: 耶low
 * @Date: 2020-11-05 14:13
 * @Version: 1.0
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //如果当前类和方法标识了IgnoreResponseAdvice注解，则不需要处理（因为该注解的作用就是忽略统一响应格式）
        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)
                ||methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class))
            return false;
        //如果返回true，就会执行beforeBodyWrite
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //定义一个自定义的响应对象
        CommonResponse<Object> response=new CommonResponse<>(0,"");
        if(null==o)
            return response;
        else if(o instanceof CommonResponse)
            response=(CommonResponse<Object>) o;
        else response.setData(o);
        return response;
    }
}
