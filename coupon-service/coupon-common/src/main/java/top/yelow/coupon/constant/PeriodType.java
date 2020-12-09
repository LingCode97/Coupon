package top.yelow.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @ClassName: PeriodType
 * @Author: 耶low
 * @Date: 2020-11-06 15:40
 * @Version: 1.0
 * 有效期类型枚举
 */
@Getter
@AllArgsConstructor
public enum  PeriodType {

    REGULAR("固定的(固定日期)",1),
    SHIFT("变动的(以领取之日开始计算)",2);

    //描述信息
    private String description;
    //编码
    private Integer code;

    public static PeriodType of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean->bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code+" not exists!"));
    }
}
