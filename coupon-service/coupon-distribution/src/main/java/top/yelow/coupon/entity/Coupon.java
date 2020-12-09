package top.yelow.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.yelow.coupon.constant.CouponStatus;
import top.yelow.coupon.converter.CouponStatusConverter;
import top.yelow.coupon.serialization.CouponSerialize;
import top.yelow.coupon.vo.CouponTemplateSDK;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName: Coupon
 * @Author: 耶low
 * @Date: 2020-11-12 10:43
 * @Version: 1.0
 * 优惠券(用户领取的优惠券记录)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;
    //模板id
    @Column(name = "template_id",nullable = false)
    private Integer templateId;
    //领取人
    @Column(name = "user_id",nullable = false)
    private Long userId;
    //优惠券码
    @Column(name = "coupon_code",nullable = false)
    private String couponCode;
    //领取时间
    @CreatedDate
    @Column(name = "assign_time",nullable = false)
    private Date assignTime;
    //优惠券状态
    @Column(name = "status",nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    //对应的模板信息，因为这一列不是数据表中的列，所以需要Transient注解
    @Transient
    private CouponTemplateSDK templateSDK;

    //返回一个无效的coupon对象
    public static Coupon invalidCoupon(){
        Coupon coupon=new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    public Coupon(Integer templateId,Long userId,String couponCode,CouponStatus status){
        this.templateId=templateId;
        this.couponCode=couponCode;
        this.userId=userId;
        this.status=status;
    }
}
