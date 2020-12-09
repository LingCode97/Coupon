package top.yelow.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @ClassName: CouponStatus
 * @Author: 耶low
 * @Date: 2020-11-12 10:38
 * @Version: 1.0
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {
    USABLE("可用的",1),
    USED("已使用的",2),
    EXPIRED("过期的(未被使用的)",3);

    private String description;
    private Integer code;

    //根据code获取CouponStatus
    public static CouponStatus of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code+" not exists"));
    }
}
