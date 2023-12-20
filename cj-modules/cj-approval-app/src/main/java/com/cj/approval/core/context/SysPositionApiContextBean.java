package com.cj.approval.core.context;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.sys.api.SysPositionApi;
import com.cj.sys.api.SysRoleApi;
import com.cj.sys.feign.SysPositionFeign;
import com.cj.sys.feign.SysRoleFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色API上下文Bean
 *
 * @author dongxiayu
 * @date 2022/11/22 10:55
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SysPositionApiContextBean implements SysPositionApi {

    private final SysPositionFeign sysPositionFeign;


    @Override
    public String getNameById(String positionId) {
        return sysPositionFeign.getNameById(positionId);
    }

    @Override
    public Page<JSONObject> positionSelector(String orgId, String searchKey) {
        String s = sysPositionFeign.positionSelector(orgId, searchKey);
        Page bean = BeanUtil.toBean(s, Page.class);
        return bean;
    }
}