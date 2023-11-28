
package com.cj.flood.core.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.cache.CommonCacheOperator;
import com.cj.common.enums.SysDataTypeEnum;
import com.cj.common.listener.CommonDataChangeListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统模块数据变化侦听器
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:44
 **/
@Component
public class SysDataChangeListener implements CommonDataChangeListener {

    public static final String ORG_CACHE_ALL_KEY = "sys-org:all";

    public static final String USER_CACHE_ALL_KEY = "sys-user:all";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Override
    public void doAddWithDataIdList(String dataType, List<String> dataIdList) {
        // 如果检测到机构增加，则将机构的数据缓存清除
        if(dataType.equals(SysDataTypeEnum.ORG.getValue())) {
            commonCacheOperator.remove(ORG_CACHE_ALL_KEY);
            // 并将该机构加入到当前登录用户的数据范围缓存
            SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
            saBaseLoginUser.getDataScopeList().forEach(dataScope -> dataScope.getDataScope().addAll(dataIdList));
            saBaseLoginUser.setDataScopeList(saBaseLoginUser.getDataScopeList());
            // 重新缓存当前登录用户信息
            StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
        }
        // 如果检测到用户增加，则将用户数据缓存清除
        if(dataType.equals(SysDataTypeEnum.USER.getValue())) {
            commonCacheOperator.remove(USER_CACHE_ALL_KEY);
        }
    }

    @Override
    public void doAddWithDataList(String dataType, JSONArray jsonArray) {
        // 此处可做额外处理
    }

    @Override
    public void doUpdateWithDataIdList(String dataType, List<String> dataIdList) {
        // 如果检测到机构更新，则将机构的数据缓存清除
        if(dataType.equals(SysDataTypeEnum.ORG.getValue())) {
            commonCacheOperator.remove(ORG_CACHE_ALL_KEY);
        }
        // 如果检测到用户更新，则将用户数据缓存清除
        if(dataType.equals(SysDataTypeEnum.USER.getValue())) {
            commonCacheOperator.remove(USER_CACHE_ALL_KEY);
        }
    }

    @Override
    public void doUpdateWithDataList(String dataType, JSONArray jsonArray) {
        // 此处可做额外处理
    }

    @Override
    public void doDeleteWithDataIdList(String dataType, List<String> dataIdList) {
        // 如果检测到机构增加，则将机构的数据缓存清除
        if(dataType.equals(SysDataTypeEnum.ORG.getValue())) {
            commonCacheOperator.remove(ORG_CACHE_ALL_KEY);
        }
        // 如果检测到用户删除，则将用户数据缓存清除，并将这些用户踢下线
        if(dataType.equals(SysDataTypeEnum.USER.getValue())) {
            commonCacheOperator.remove(USER_CACHE_ALL_KEY);
            dataIdList.forEach(StpUtil::kickout);
        }
    }
}
