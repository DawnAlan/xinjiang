package com.cj.approval.core.context;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.sys.api.SysUserApi;
import com.cj.sys.feign.SysUserFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户Api上下文Bean
 *
 * @author dongxiayu
 * @date 2022/11/22 16:29
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SysUserApiContextBean implements SysUserApi {

    private final SysUserFeign sysUserFeign;

    /**
     * 根据用户id获取用户对象，没有则返回null
     *
     * @param userId
     * @author dongxiayu
     * @date 2022/6/20 18:19
     */
    @Override
    public JSONObject getUserByIdWithoutException(String userId) {
        String feignResp = sysUserFeign.getUserByIdWithoutException(userId);
        JSONObject resp = JSONUtil.parseObj(feignResp);
        return resp;
    }

    /**
     * 根据用户id获取用户对象列表，没有的则为空，都没有则返回空集合
     *
     * @param userIdList
     * @author dongxiayu
     * @date 2022/6/20 18:19
     */
    @Override
    public List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList) {
        String feignResp = sysUserFeign.getUserListByIdListWithoutException(userIdList);
        JSONArray jsonArray = new JSONArray(feignResp);
        List<JSONObject> resp = jsonArray.toList(JSONObject.class);
        return resp;
    }

    /**
     * 根据用户id获取用户对象，没有则抛出异常
     *
     * @param userId
     * @author dongxiayu
     * @date 2022/6/20 18:19
     */
    @Override
    public JSONObject getUserByIdWithException(String userId) {
        String feignResp = sysUserFeign.getUserByIdWithoutException(userId);
        JSONObject resp = JSONUtil.parseObj(feignResp);
        return resp;
    }

    /**
     * 根据用户id获取用户对象列表，只要有一个没有则抛出异常
     *
     * @param userIdList
     * @author dongxiayu
     * @date 2022/6/20 18:19
     */
    @Override
    public List<JSONObject> getUserListByIdWithException(List<String> userIdList) {
        String feignResp = sysUserFeign.getUserListByIdWithException(userIdList);
        JSONArray jsonArray = new JSONArray(feignResp);
        List<JSONObject> resp = jsonArray.toList(JSONObject.class);
        return resp;
    }

    /**
     * 获取用户拥有角色
     *
     * @param userId
     * @author dongxiayu
     * @date 2022/5/13 21:00
     */
    @Override
    public List<String> ownRole(String userId) {
        String feignResp = sysUserFeign.ownRole(userId);
        JSONArray jsonArray = new JSONArray(feignResp);
        List<String> resp = jsonArray.toList(String.class);
        return resp;
    }

    /**
     * 给用户授权角色
     *
     * @param userId
     * @param roleIdList
     * @author dongxiayu
     * @date 2022/8/1 18:28
     */
    @Override
    public void grantRole(String userId, List<String> roleIdList) {
        sysUserFeign.grantRole(userId, roleIdList);
    }

    /**
     * 根据组织id集合获取组织下用户id集合
     *
     * @param orgIdList
     * @author dongxiayu
     * @date 2022/6/6 11:40
     */
    @Override
    public List<String> getUserIdListByOrgIdList(List<String> orgIdList) {
        String feignResp = sysUserFeign.getUserIdListByOrgIdList(orgIdList);
        JSONArray jsonArray = new JSONArray(feignResp);
        List<String> resp = jsonArray.toList(String.class);
        return resp;
    }

    /**
     * 根据职位id集合获取职位下用户id集合
     *
     * @param positionIdList
     * @author dongxiayu
     * @date 2022/6/6 11:44
     */
    @Override
    public List<String> getUserIdListByPositionIdList(List<String> positionIdList) {
        String feignResp = sysUserFeign.getUserIdListByPositionIdList(positionIdList);
        JSONArray jsonArray = new JSONArray(feignResp);
        List<String> resp = jsonArray.toList(String.class);
        return resp;
    }

    /**
     * 根据用户id和组织id和职位id获取上级主管id
     *
     * @param userId
     * @param orgId
     * @param positionId
     * @author dongxiayu
     * @date 2022/6/6 14:50
     */
    @Override
    public String getSupervisorIdByUserIdAndOrgIdAndPositionId(String userId, String orgId, String positionId) {
        return sysUserFeign.getSupervisorIdByUserIdAndOrgIdAndPositionId(userId, orgId, positionId);
    }

    /**
     * 获取用户选择器
     *
     * @param orgId
     * @param searchKey
     * @author dongxiayu
     * @date 2022/4/24 20:08
     */
    @Override
    public Page<JSONObject> userSelector(String orgId, String searchKey) {
        String feignResp = sysUserFeign.userSelector(orgId, searchKey);
        Page<JSONObject> resp =  (Page<JSONObject>)JSONUtil.toBean(feignResp,Page.class);
        return resp;
    }
}