package top.yelow.coupon.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.yelow.coupon.constant.CouponCategory;
import top.yelow.coupon.constant.RuleFlag;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.vo.SettlementInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExecuteManager
 * @Author: 耶low
 * @Date: 2020-11-27 16:01
 * @Version: 1.0
 * 优惠券结算规则执行管理器
 * 根据用户的请求找到对应的Executor去做结算
 */
@Slf4j
@Component
public class ExecuteManager implements BeanPostProcessor {

    private static Map<RuleFlag,RuleExecutor> executorMap=new HashMap<>(RuleFlag.values().length);

    /**
     * 优惠券结算规则计算的入口
     * */
    public SettlementInfo computeRule(SettlementInfo settlementInfo) throws CouponException {
        SettlementInfo result=null;
        //单类优惠券
        if(settlementInfo.getCouponAndTemplateInfos().size()==1){
            //获取优惠券的类别
            CouponCategory category=CouponCategory.of(
                    settlementInfo.getCouponAndTemplateInfos().get(0).getTemplate().getCategory()
            );
            switch (category){
                case MANJIAN:
                    result=executorMap.get(RuleFlag.MANJIAN).computeRule(settlementInfo);
                    break;
                case ZHEKOU:
                    result=executorMap.get(RuleFlag.ZHEKOU).computeRule(settlementInfo);
                    break;
                case LIJIAN:
                    result=executorMap.get(RuleFlag.LIJIAN).computeRule(settlementInfo);
                    break;
            }
        }else{
            //多品类优惠券
            List<CouponCategory> categories=new ArrayList<>(settlementInfo.getCouponAndTemplateInfos().size());
            settlementInfo.getCouponAndTemplateInfos().forEach(ct->
                    categories.add(CouponCategory.of(ct.getTemplate().getCategory()))
            );
            if(categories.size()!=2){
                throw new CouponException("Not support for more Template category");
            }else{
                if(categories.contains(CouponCategory.MANJIAN)&&categories.contains(CouponCategory.ZHEKOU)){
                    result=executorMap.get(RuleFlag.MANJIAN_ZHEKOU).computeRule(settlementInfo);
                }else{
                    throw new CouponException("Not support for other Template category");
                }
            }
        }
        return result;
    }
    /**
     * bean初始化之前去执行
     * */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if(!(bean instanceof  RuleExecutor)){
            return bean;
        }
        RuleExecutor executor=(RuleExecutor)bean;
        RuleFlag ruleFlag=executor.ruleConfig();
        if(executorMap.containsKey(ruleFlag)){
            throw new IllegalStateException("There is already an executor"+" for rule flag:"+ruleFlag);
        }
        log.info("Load executor {} for rule flag {}.",executor.getClass(),ruleFlag);
        executorMap.put(ruleFlag,executor);

        return null;
    }

    /**
     * bean初始化之后执行
     * */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }
}
