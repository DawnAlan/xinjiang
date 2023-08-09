
package com.cj.dev.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cj.common.consts.FeignConstant;

/**
 * 开发工具模块综合Feign
 *
 * @author dongxiayu
 * @date 2022/11/22 22:46
 */
@FeignClient(name= FeignConstant.WEB_APP, contextId = "DevFeign")
public interface DevFeign {

    /**
     * 初始化ID类型的租户开发工具模块数据
     *
     * @author dongxiayu
     * @date 2022/9/26 14:25
     **/
    @RequestMapping("/feign/dev/initTenDataForCategoryId")
    void initTenDataForCategoryId(@RequestParam("tenId") String tenId, @RequestParam("tenName") String tenName);

    /**
     * 删除ID类型的租户开发工具模块数据
     *
     * @author dongxiayu
     * @date 2022/9/26 14:25
     **/
    @RequestMapping("/feign/dev/removeTenDataForCategoryId")
    void removeTenDataForCategoryId(@RequestParam("tenId") String tenId);

}
