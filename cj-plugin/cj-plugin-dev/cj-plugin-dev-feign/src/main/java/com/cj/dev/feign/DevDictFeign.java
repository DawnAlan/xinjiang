package com.cj.dev.feign;


import com.cj.common.consts.FeignConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 配置Feign接口
 *
 * @author LB
 * @date 2023/09/19 11:10
 */
@FeignClient(name= FeignConstant.WEB_APP, contextId = "DevDictFeign")
public interface DevDictFeign {

    /**
     * 根据字典值查询其下所有子字典
     * @param value 字典的值 DictValue
     * @return
     */
    @RequestMapping("/feign/dev/dict/getDictByValue")
    String getDictByValue(@RequestParam("value") String value);



    /**
     * 根据字典名称查询其下所有子字典
     * @param name 字典的名称 DictLabel
     * @return
     */
    @RequestMapping("/feign/dev/dict/getDictByName")
    String getDictByName(@RequestParam("name") String name);

}
