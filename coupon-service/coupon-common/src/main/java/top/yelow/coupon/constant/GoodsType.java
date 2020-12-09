package top.yelow.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.yelow.coupon.exception.CouponException;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @ClassName: GoodsType
 * @Author: 耶low
 * @Date: 2020-11-13 15:49
 * @Version: 1.0
 * 商品类型枚举
 */
@Getter
@AllArgsConstructor
public enum GoodsType {
    WENYU("文娱", 1),
    SHENGXIAN("生鲜", 2),
    JIAJU("家具", 3),
    OTHERS("其他", 4),
    ALL("全品类", 5);

    private String description;
    private Integer code;

    public static GoodsType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }
}
