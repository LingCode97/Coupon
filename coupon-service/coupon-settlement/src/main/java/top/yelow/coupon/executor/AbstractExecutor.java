package top.yelow.coupon.executor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import top.yelow.coupon.vo.GoodsInfo;
import top.yelow.coupon.vo.SettlementInfo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: AbstractExecutor
 * @Author: 耶low
 * @Date: 2020-11-26 10:40
 * @Version: 1.0
 * 规则执行器抽象类，定义通用方法
 */
public abstract class AbstractExecutor {

    /**
     * 校验商品类型与优惠券是否匹配
     * 1.这里只实现单品类优惠券的校验，多品类的优惠券需要重载此方法
     * 2.商品只要有一个优惠券要求类型匹配即可使用，如商品是A、B、C三个类型，优惠券只要求A类型即可使用该优惠券
     */
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType = JSON.parseObject(
                settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate().getRule().getUsage().getGoodsType()
                , List.class
        );
        //类型存在交集即可
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType, templateGoodsType));
    }

    /**
     * 处理商品类型与优惠券类型限制不匹配的情况
     * */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlementInfo,double goodsSum){
        boolean isGoodsTypeSatisfy=isGoodsTypeSatisfy(settlementInfo);
        //通过上面的方法校验优惠券是否匹配，如果不匹配进入下面的方法
        if(!isGoodsTypeSatisfy){
            //结算金额设置为原价，并清空优惠券
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.EMPTY_LIST);
            return settlementInfo;
        }
        return null;
    }

    /**
     * 商品总价
     * */
    protected double goodsCostSum(List<GoodsInfo> goodsInfos){
        return goodsInfos.stream().mapToDouble(
                g->g.getPrice()*g.getCount()
        ).sum();
    }

    /**
     * 保留两位小数
     * */
    protected double retain2Decimals(double value){
        return new BigDecimal(value).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    /**
     * 最小支付费用
     * 有些场景下有的优惠力度非常大，可能会出现支付负数的情况
     * 这里定义一下最小的支付金额
     * */
    protected double minCost(){
        return 0.1;
    }
}
