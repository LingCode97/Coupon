package top.yelow.coupon.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yelow.coupon.dao.CouponTemplateDao;
import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.service.IAsyncService;
import top.yelow.coupon.service.IBuildTemplateService;
import top.yelow.coupon.vo.TemplateRequest;

/**
 * @ClassName: BuildTemplateServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-09 16:32
 * @Version: 1.0
 */
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    private final IAsyncService asyncService;

    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(IAsyncService asyncService, CouponTemplateDao templateDao) {
        this.asyncService = asyncService;
        this.templateDao = templateDao;
    }

    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {

        //参数合法性校验
        if(!request.validate()){
            throw new CouponException("BuildTemplate Param is Not Valid");
        }

        if(null!=templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template");
        }

        //构造CouponTemplate并保存到数据库中
        CouponTemplate template=requestToTemplate(request);
        template=templateDao.save(template);

        //根据优惠券模板异步生产优惠券码
        asyncService.asyncConstructCouponByTemplate(template);

        return template;
    }
    //将TemplateRequest转换位CouponTemplate
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule()
        );
    }
}
