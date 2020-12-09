package top.yelow.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import top.yelow.coupon.constant.CouponCategory;
import top.yelow.coupon.constant.DistributeTarget;
import top.yelow.coupon.constant.ProductLine;
import top.yelow.coupon.converter.CouponCategoryConverter;
import top.yelow.coupon.converter.DistributeTargetConverter;
import top.yelow.coupon.converter.ProductLineConverter;
import top.yelow.coupon.converter.RuleConverter;
import top.yelow.coupon.serialization.CouponTemplateSerialize;
import top.yelow.coupon.vo.TemplateRule;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: CouponTemplate
 * @Author: 耶low
 * @Date: 2020-11-08 09:35
 * @Version: 1.0
 * 优惠券实体类定义:基础属性+规则属性
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="coupon_template")
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;

    @Column(name = "available",nullable = false)
    private Boolean available;
    @Column(name = "expired",nullable = false)
    private Boolean expired;
    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "logo",nullable = false)
    private String logo;

    //优惠券描述
    @Column(name = "intro",nullable = false)
    private String desc;
    //优惠券分类
    @Column(name = "category",nullable = false)
    @Convert(converter = CouponCategoryConverter.class)
    private CouponCategory category;

    //产品线
    @Column(name = "product_line",nullable = false)
    @Convert(converter = ProductLineConverter.class)
    private ProductLine productLine;

    @Column(name = "coupon_count",nullable = false)
    private Integer count;
    @CreatedDate
    @Column(name = "create_time",nullable = false)
    private Date createTime;

    //创建用户
    @Column(name = "user_id",nullable = false)
    private Long userId;

    //优惠券模板编码
    @Column(name = "template_key",nullable = false)
    private String key;
    @Column(name = "target",nullable = false)
    @Convert(converter = DistributeTargetConverter.class)
    private DistributeTarget target;
    @Column(name = "rule",nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;

    public CouponTemplate(String name,String logo,String desc,String category,Integer productLine,Integer count,
                          Long userId,Integer target,TemplateRule rule){
        this.available=false;
        this.expired=false;
        this.name=name;
        this.logo=logo;
        this.desc=desc;
        this.category=CouponCategory.of(category);
        this.productLine=ProductLine.of(productLine);
        this.count=count;
        this.userId=userId;
        //优惠券模板编码的规则：4（产品线和类型）+8（日期）+4(id)，由于这里只是模板实体类，没有具体的id，所以先不加上后4位id，序列化具体的
        //实体类信息时再加上
        this.key=productLine.toString()+category+new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target=DistributeTarget.of(target);
        this.rule=rule;
    }
}
