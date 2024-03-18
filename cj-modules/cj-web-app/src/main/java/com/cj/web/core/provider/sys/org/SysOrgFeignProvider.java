package com.cj.web.core.provider.sys.org;

import cn.hutool.json.JSONUtil;
import com.cj.sys.modular.org.provider.SysOrgApiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cj.sys.feign.SysOrgFeign;

/**
 * 组织Feign提供者
 *
 * @author dongxiayu
 * @date 2022/11/22 23:57
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class SysOrgFeignProvider implements SysOrgFeign {

    private final SysOrgApiProvider sysOrgApiProvider;

    /**
     * 根据id获取名称
     *
     * @param orgId
     * @author dongxiayu
     * @date 2022/8/4 10:12
     */
    @Override
    @RequestMapping("/feign/sys/org/getNameById")
    public String getNameById(@RequestParam(value = "orgId",required = false) String orgId) {
        return sysOrgApiProvider.getNameById(orgId);
    }

    @Override
    @RequestMapping("/feign/sys/org/getIdByName")
    public String getIdByName(@RequestParam(value = "name",required = false) String name) {
        return  sysOrgApiProvider.getIdByName(name);
    }

    /**
     * 根据组织id获取部门主管id
     *
     * @param orgId
     * @author dongxiayu
     * @date 2022/6/6 14:50
     */
    @Override
    @RequestMapping("/feign/sys/org/getSupervisorIdByOrgId")
    public String getSupervisorIdByOrgId(@RequestParam(value = "orgId",required = false) String orgId) {
        return sysOrgApiProvider.getSupervisorIdByOrgId(orgId);
    }

    /**
     * 获取组织树选择器
     *
     * @author dongxiayu
     * @date 2022/7/22 14:46
     **/
    @Override
    @RequestMapping("/feign/sys/org/orgTreeSelector")
    public String orgTreeSelector() {
        return JSONUtil.toJsonStr(sysOrgApiProvider.orgTreeSelector());
    }

    /**
     * 获取组织列表选择器
     *
     * @param parentId
     * @author dongxiayu
     * @date 2022/7/22 14:45
     */
    @Override
    @RequestMapping("/feign/sys/org/orgListSelector")
    public String orgListSelector(@RequestParam(value = "parentId",required = false) String parentId) {
        return JSONUtil.toJsonStr(sysOrgApiProvider.orgListSelector(parentId));
    }
}