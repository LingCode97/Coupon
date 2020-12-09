package top.yelow.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型枚举定义
 * */
@Getter
@AllArgsConstructor
public enum RuleFlag {
    MANJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券的计算规则"),

    //多类型优惠券定义
    MANJIAN_ZHEKOU("满减券+折扣券的计算规则");
    //TODO 可以定义更多组合

    private String description;

}
