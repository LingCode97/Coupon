package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: CommonResponse
 * @Author: 耶low
 * @Date: 2020-11-05 14:07
 * @Version: 1.0
 * @作用: 通用响应对象定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public CommonResponse(Integer code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
