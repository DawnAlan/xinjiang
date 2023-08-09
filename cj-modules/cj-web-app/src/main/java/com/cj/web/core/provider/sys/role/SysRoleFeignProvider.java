package com.cj.web.core.provider.sys.role;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cj.sys.feign.SysRoleFeign;
import com.cj.sys.modular.role.provider.SysRoleApiProvider;

import java.util.List;

/**
 * 角色API Feign接口提供者
 *
 * @author dongxiayu
 * @date 2022/11/22 10:36
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class SysRoleFeignProvider implements SysRoleFeign {

    private final SysRoleApiProvider sysRoleApiProvider;

    /**
     * 判断组织下是否存在角色
     *
     * @param orgIdList
     * @author dongxiayu
     * @date 2022/8/2 11:16
     */
    @Override
    @RequestMapping("/feign/sys/role/orgHasRole")
    public boolean orgHasRole(@RequestParam(value = "orgIdList",required = false) List<String> orgIdList) {
        return sysRoleApiProvider.orgHasRole(orgIdList);
    }

    /**
     * 获取角色选择器
     *
     * @param orgId
     * @param category
     * @param searchKey
     * @author dongxiayu
     * @date 2022/7/22 14:49
     */
    @SuppressWarnings("ALL")
    @Override
    @RequestMapping("/feign/sys/role/roleSelector")
    public String roleSelector(@RequestParam(value = "orgId",required = false) String orgId, @RequestParam(value = "category",required = false) String category, @RequestParam(value = "searchKey",required = false) String searchKey,@RequestParam(value = "dataScopeList",required = false)  List<String> dataScopeList){
        return JSONUtil.toJsonStr(sysRoleApiProvider.roleSelector(orgId, category, searchKey, dataScopeList));
    }

    /**
     * 代码生成菜单按钮授权
     *
     * @param menuId
     * @author dongxiayu
     * @date 2022/11/1 15:58
     */
    @Override
    @RequestMapping("/feign/sys/role/grantForGenMenuAndButton")
    public void grantForGenMenuAndButton(@RequestParam(value = "menuId",required = false) String menuId) {
        sysRoleApiProvider.grantForGenMenuAndButton(menuId);
    }
}