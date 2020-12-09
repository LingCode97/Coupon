package top.yelow.coupon.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: GlobalExceptionAdvice
 * @Author: 耶low
 * @Date: 2020-11-05 14:34
 * @Version: 1.0
 */
@RestControllerAdvice
public class GlobalExceptionAdvice {
    //对couponException进行统一处理
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest request, CouponException ex){
        CommonResponse<String> response=new CommonResponse<>(-1,"business error");
        response.setData(ex.getMessage());
        return response;
    }

}
