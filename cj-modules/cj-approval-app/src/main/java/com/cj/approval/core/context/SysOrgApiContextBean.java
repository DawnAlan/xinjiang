package com.cj.approval.core.context;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.sys.api.SysOrgApi;
import com.cj.sys.api.SysRoleApi;
import com.cj.sys.feign.SysOrgFeign;
import com.cj.sys.feign.SysRoleFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色API上下文Bean
 *
 * @author dongxiayu
 * @date 2022/11/22 10:55
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SysOrgApiContextBean implements SysOrgApi {

    private final SysOrgFeign sysOrgFeign;


    @Override
    public String getNameById(String orgId) {
        return sysOrgFeign.getNameById(orgId);
    }

    @Override
    public String getSupervisorIdByOrgId(String orgId) {
        return sysOrgFeign.getSupervisorIdByOrgId(orgId);
    }

    @Override
    public List<Tree<String>> orgTreeSelector() {
        String s = sysOrgFeign.orgTreeSelector();
        return null;
    }

    @Override
    public Page<JSONObject> orgListSelector(String parentId) {
        String s = sysOrgFeign.orgListSelector(parentId);
        Page bean = BeanUtil.toBean(s, Page.class);
        return bean;
    }
}