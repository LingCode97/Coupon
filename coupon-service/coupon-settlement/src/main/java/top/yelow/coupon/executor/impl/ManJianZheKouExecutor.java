package top.yelow.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import top.yelow.coupon.constant.CouponCategory;
import top.yelow.coupon.constant.RuleFlag;
import top.yelow.coupon.executor.AbstractExecutor;
import top.yelow.coupon.executor.RuleExecutor;
import top.yelow.coupon.vo.GoodsInfo;
import top.yelow.coupon.vo.SettlementInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: ManJianZheKouExecutor
 * @Author: 耶low
 * @Date: 2020-11-27 15:08
 * @Version: 1.0
 * 满减+折扣优惠券结算执行器
 * 更多的组合优惠可以现在RuleFlag里面定义，再在去实现
 */
@Slf4j
@Component
public class ManJianZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     */
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEKOU;
    }

    /**
     * <h2>优惠券规则计算</h2>
     *
     * @param settlementInfo {@link SettlementInfo}
     * @return {@link SettlementInfo}
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum=retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability=processGoodsTypeNotSatisfy(
                settlementInfo,goodsSum
        );
        if(null!=probability){
            log.debug("ManJian and ZheKou template in not match to goodsType!");
            return probability;
        }

        SettlementInfo.CouponAndTemplateInfo manJian=null;
        SettlementInfo.CouponAndTemplateInfo zheKou=null;

        for (SettlementInfo.CouponAndTemplateInfo ct : settlementInfo.getCouponAndTemplateInfos()) {
            if(CouponCategory.of(ct.getTemplate().getCategory())==CouponCategory.MANJIAN){
                manJian=ct;
            }else{
                zheKou=ct;
            }
        }
        //如果折扣和满减不能共用
        if(!isTemplateCanShared(manJian,zheKou)){
            log.debug("ManJian and ZheKou can not shared!");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.EMPTY_LIST);
        }
        //如果可以共用，开始真正的结算
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos=new ArrayList<>();
        double manJianBase=(double)manJian.getTemplate().getRule().getDiscount().getBase();
        double manJianQuota=(double)manJian.getTemplate().getRule().getDiscount().getQuota();

        //最终的价格
        double targetSum=goodsSum;

        //先计算满减
        if(targetSum>manJianBase){
            targetSum-=manJianQuota;
            ctInfos.add(manJian);
        }
        //再计算折扣
        double zheKouQuota=(double)zheKou.getTemplate().getRule().getDiscount().getQuota();
        targetSum*=zheKouQuota*1.0/100;
        ctInfos.add(zheKou);
        //返回结算结果
        settlementInfo.setCouponAndTemplateInfos(ctInfos);
        settlementInfo.setCost(retain2Decimals(targetSum>minCost()?targetSum:minCost()));
        return settlementInfo;
    }

    //校验两张优惠券是否可以共用
    @SuppressWarnings("all")
    private boolean isTemplateCanShared(
            SettlementInfo.CouponAndTemplateInfo manJian,SettlementInfo.CouponAndTemplateInfo zheKou
    ){
        String manjianKey=manJian.getTemplate().getKey()+String.format("%04d",manJian.getTemplate().getId());
        String zhekouKey=zheKou.getTemplate().getKey()+String.format("04d",zheKou.getTemplate().getId());
        List<String> allSharedKeysForManJian=new ArrayList<>();
        allSharedKeysForManJian.add(manjianKey);
        allSharedKeysForManJian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));

        List<String> allSharedKeysForZheKou=new ArrayList<>();
        allSharedKeysForZheKou.add(zhekouKey);
        allSharedKeysForZheKou.addAll(JSON.parseObject(
                zheKou.getTemplate().getRule().getWeight(),
                List.class
        ));
        return CollectionUtils.isSubCollection(
                Arrays.asList(manjianKey,zhekouKey),allSharedKeysForManJian)||
               CollectionUtils.isSubCollection(
                       Arrays.asList(manjianKey,zhekouKey),allSharedKeysForZheKou);
    }

    /**
     * 之前说了父类的该方法默认只实现单品类的判断，这里演示实现一下多品类的判断
     * @param settlementInfo
     */
    @Override
    @SuppressWarnings("all")
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlementInfo) {
        log.debug("Check ManJian and ZheKou is match or not!");

        List<Integer> goodsType = settlementInfo.getGoodsInfos()
                .stream().map(GoodsInfo::getType)
                .collect(Collectors.toList());
        List<Integer> templateGoodsType=new ArrayList<>();
        settlementInfo.getCouponAndTemplateInfos().forEach(ct->{
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });

        //如果想要使用多张优惠券，则商品的品类应该都在多张优惠券支持的品类中，如商品是家具，则优惠券A和B都应该包含家具品类
        //
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType,templateGoodsType));
    }
}
