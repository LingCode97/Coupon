package top.yelow.coupon.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @ClassName: TemplateBaseTest
 * @Author: 耶low
 * @Date: 2020-11-11 15:36
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {
    @Autowired
    private ITemplateBaseService baseService;

    @Test
    public void testBuildTemplateInfo() throws Exception{
        //正确的模板id
        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(10)));
        //错误的模板id
        System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(1)));
    }

    @Test
    public void testFindAllUsableTemplate() throws Exception{
        System.out.println(JSON.toJSONString(baseService.findAllUsableTemplate()));
    }

    @Test
    public void testFindIds2TemplateSDK() throws Exception{
        System.out.println(JSON.toJSONString(baseService.findIds2TemplateSDK(Arrays.asList(10,11,12))));
    }

}
