package top.yelow.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠券分类
 * **/
@Getter
@AllArgsConstructor
public enum CouponCategory {
    MANJIAN("满减券","001"),
    ZHEKOU("折扣券","002"),
    LIJIAN("立减券","003");
    //优惠券描述信息
    private String description;
    //优惠券编码
    private String code;
    //根据传入的编码获取对应类型的优惠券，如果code不存在就返回报错信息
    public static CouponCategory of(String code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code+" not exists!"));
    }
}
