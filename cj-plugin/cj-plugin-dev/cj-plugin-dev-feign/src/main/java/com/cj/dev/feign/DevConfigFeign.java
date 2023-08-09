
package com.cj.dev.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cj.common.consts.FeignConstant;

/**
 * 配置Feign接口
 *
 * @author dongxiayu
 * @date 2022/11/22 11:10
 */
@FeignClient(name= FeignConstant.WEB_APP, contextId = "DevConfigFeign")
public interface DevConfigFeign {

    /**
     * 根据键获取值
     *
     * @author dongxiayu
     * @date 2022/11/12 11:11
     **/
    @RequestMapping("/feign/dev/config/getValueByKey")
    String getValueByKey(@RequestParam("key") String key);

}