package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: GoodsInfo
 * @Author: 耶low
 * @Date: 2020-11-13 15:55
 * @Version: 1.0
 * 商品信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {
    private Integer type;
    private Double price;
    private Integer count;

}
