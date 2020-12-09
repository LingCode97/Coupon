package top.yelow.coupon.service.impl;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yelow.coupon.dao.CouponTemplateDao;
import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.service.ITemplateBaseService;
import top.yelow.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: TemplateBaseServiceImpl
 * @Author: 耶low
 * @Date: 2020-11-09 16:42
 * @Version: 1.0
 */
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {

    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()) {
            throw new CouponException("Template Is Not Exist:" + id);
        }
        return template.get();
    }

    @Override
    public List<CouponTemplateSDK> findAllUsableTemplate() {
        //查找可用状态，没有过期的
        List<CouponTemplate> templates = templateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream().map(this::templateSDK).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates=templateDao.findAllById(ids);
        return templates.stream().map(this::templateSDK).collect(Collectors.toMap(
                CouponTemplateSDK::getId, Function.identity()
        ));
    }

    //将CouponTemplate转换为templateSDK
    private CouponTemplateSDK templateSDK(CouponTemplate template){
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                template.getKey(),
                template.getTarget().getCode(),
                template.getRule()
        );
    }
}
