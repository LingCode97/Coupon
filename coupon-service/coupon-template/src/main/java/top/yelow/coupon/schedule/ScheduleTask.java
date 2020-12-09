package top.yelow.coupon.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.yelow.coupon.dao.CouponTemplateDao;
import top.yelow.coupon.entity.CouponTemplate;
import top.yelow.coupon.vo.TemplateRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ScheduleTask
 * @Author: 耶low
 * @Date: 2020-11-11 09:18
 * @Version: 1.0
 * 定时清理已过期的优惠券模板
 */
@Slf4j
@Component
public class ScheduleTask {
    private final CouponTemplateDao templateDao;

    public ScheduleTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * 每小时清理一次
     * */
    @Scheduled(fixedRate = 60*60*1000)
    public void offlineCouponTemplate(){
        log.info("Start to Expire CouponTemplate");
        List<CouponTemplate> templates=templateDao.findAllByExpired(false);
        if(CollectionUtils.isEmpty(templates)){
            log.info("Done To Expire CouponTemplate");
            return;
        }
        Date cur=new Date();
        List<CouponTemplate> expiredTemplates=new ArrayList<>(templates.size());
        templates.forEach(t->{
            TemplateRule rule=t.getRule();
            if(rule.getExpiration().getDeadline()<cur.getTime()){
                t.setExpired(true);
                expiredTemplates.add(t);
            }
        });
        if(CollectionUtils.isNotEmpty(expiredTemplates)){
            log.info("Expired CouponTemplate Num:{}",templateDao.saveAll(expiredTemplates));
        }
        log.info("Done To Expire CouponTemplate");
    }
}
