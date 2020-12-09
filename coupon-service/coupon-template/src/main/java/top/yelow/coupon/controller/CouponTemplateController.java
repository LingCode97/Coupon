package top.yelow.coupon.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.service.IBuildTemplateService;
import top.yelow.coupon.service.ITemplateBaseService;
import top.yelow.coupon.vo.CouponTemplateSDK;
import top.yelow.coupon.vo.TemplateRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CouponTemplateController
 * @Author: 耶low
 * @Date: 2020-11-11 09:48
 * @Version: 1.0
 * 优惠券模板相关功能的服务接口
 */
@Slf4j
@RestController
public class CouponTemplateController {
    private final IBuildTemplateService buildTemplateService;
    private final ITemplateBaseService templateBaseService;

    @Autowired
    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    //构建优惠券模板
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException{
        log.info("Build Template:{}", JSON.toJSONString(request));
        return buildTemplateService.buildTemplate(request);
    }

    //构造优惠券模板详情
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException{
        log.info("Build Template Info For:{}",id);
        return templateBaseService.buildTemplateInfo(id);
    }

    //查询所有可用的优惠券模板
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> finAllUsableTemplate(){
        log.info("Find All Usable Template");
        return templateBaseService.findAllUsableTemplate();
    }

    //获取模板ids到CouponTemplate的映射
    @GetMapping("/template/sdk/infos")
    public Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids){
        log.info("FindIds2TemplateSDK:{}",ids);
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
