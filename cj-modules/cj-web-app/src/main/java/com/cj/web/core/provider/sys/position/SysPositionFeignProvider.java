
package com.cj.web.core.provider.sys.position;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cj.sys.feign.SysPositionFeign;
import com.cj.sys.modular.position.provider.SysPositionApiProvider;

/**
 * 职位Feign提供者
 *
 * @author dongxiayu
 * @date 2022/11/23 0:07
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class SysPositionFeignProvider implements SysPositionFeign {

    private final SysPositionApiProvider sysPositionApiProvider;

    /**
     * 根据id获取名称
     *
     * @param positionId
     * @author dongxiayu
     * @date 2022/8/4 10:13
     */
    @Override
    @RequestMapping("/feign/sys/position/getNameById")
    public String getNameById(@RequestParam(value = "positionId",required = false) String positionId) {
        return sysPositionApiProvider.getNameById(positionId);
    }

    /**
     * 获取职位选择器
     *
     * @param orgId
     * @param searchKey
     * @author dongxiayu
     * @date 2022/7/22 14:47
     */
    @Override
    @RequestMapping("/feign/sys/position/positionSelector")
    public String positionSelector(@RequestParam(value = "orgId",required = false) String orgId, @RequestParam(value = "searchKey",required = false) String searchKey) {
        return JSONUtil.toJsonStr(sysPositionApiProvider.positionSelector(orgId, searchKey));
    }
}