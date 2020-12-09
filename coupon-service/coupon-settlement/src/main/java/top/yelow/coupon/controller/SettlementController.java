package top.yelow.coupon.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.yelow.coupon.exception.CouponException;
import top.yelow.coupon.executor.ExecuteManager;
import top.yelow.coupon.vo.SettlementInfo;

/**
 * @ClassName: SettlementController
 * @Author: 耶low
 * @Date: 2020-11-28 09:43
 * @Version: 1.0
 * 结算服务的控制器
 */
@Slf4j
@RestController
public class SettlementController {
    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager) {
        this.executeManager = executeManager;
    }

    /**
     * 优惠券结算
     * */
    @PostMapping("/settlement/computer")
    public SettlementInfo computerRule(@RequestBody SettlementInfo settlementInfo) throws CouponException{
        log.info("settlement:{}", JSON.toJSONString(settlementInfo));
        return executeManager.computeRule(settlementInfo);
    }
}
