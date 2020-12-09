package top.yelow.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.constant.PeriodType;
import top.yelow.coupon.entity.Coupon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: CouponClassify
 * @Author: 耶low
 * @Date: 2020-11-19 12:03
 * @Version: 1.0
 * 根据优惠券状态对用户优惠券进行分类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponClassify {

    //可用的
    private List<Coupon> usable;
    //已使用的
    private List<Coupon> used;
    //过期的
    private List<Coupon> expired;

    //对当前的优惠券进行分类
    public static CouponClassify classify(List<Coupon> coupons) {
        //把传入的优惠券装到对应的分类容器里面
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());

        coupons.forEach(c -> {
            //是否过期
            boolean isTimeExpired;
            long curTime = new Date().getTime();
            //首先确定是否过期
            if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())) {
                isTimeExpired = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
            }else{
                isTimeExpired= DateUtils.addDays(
                        c.getAssignTime(),c.getTemplateSDK().getRule().getExpiration().getGap()
                ).getTime()<=curTime;
            }
            //如果优惠券状态是使用过的，直接添加到used容器
            if(c.getStatus()== CouponStatus.USED){
                used.add(c);
            }else if(c.getStatus()==CouponStatus.EXPIRED||isTimeExpired){
                //如果前面判断isTimeExpired是过期的或者优惠券状态已经是过期的，添加到过期容器里面
                //为什么还需要isTimeExpired来判断是否过期，而不是直接根据getStatus？
                //因为优惠券过期状态的更新是异步的，存在延迟
                expired.add(c);
            }else {
                //既不是过期也不是使用过的就是还可以使用的，添加到可使用容器里面
                usable.add(c);
            }
        });
        return new CouponClassify(usable,used,expired);
    }
}
