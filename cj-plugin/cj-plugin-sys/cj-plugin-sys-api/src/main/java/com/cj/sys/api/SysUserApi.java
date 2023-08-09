
package com.cj.sys.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 用户Api
 *
 * @author xuyuxiang
 * @date 2022/6/6 11:33
 **/
public interface SysUserApi {

    /**
     * 根据用户id获取用户对象，没有则返回null
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    JSONObject getUserByIdWithoutException(String userId);

    /**
     * 根据用户id获取用户对象列表，没有的则为空，都没有则返回空集合
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList);

    /**
     * 根据用户id获取用户对象，没有则抛出异常
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    JSONObject getUserByIdWithException(String userId);

    /**
     * 根据用户id获取用户对象列表，只要有一个没有则抛出异常
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    List<JSONObject> getUserListByIdWithException(List<String> userIdList);

    /**
     * 获取用户拥有角色
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<String> ownRole(String userId);

    /**
     * 给用户授权角色
     *
     * @author xuyuxiang
     * @date 2022/8/1 18:28
     */
    void grantRole(String userId, List<String> roleIdList);

    /**
     * 根据组织id集合获取组织下用户id集合
     *
     * @author xuyuxiang
     * @date 2022/6/6 11:40
     **/
    List<String> getUserIdListByOrgIdList(List<String> orgIdList);

    /**
     * 根据职位id集合获取职位下用户id集合
     *
     * @author xuyuxiang
     * @date 2022/6/6 11:44
     **/
    List<String> getUserIdListByPositionIdList(List<String> positionIdList);

    /**
     * 根据用户id和组织id和职位id获取上级主管id
     *
     * @author xuyuxiang
     * @date 2022/6/6 14:50
     **/
    String getSupervisorIdByUserIdAndOrgIdAndPositionId(String userId, String orgId, String positionId);

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<JSONObject> userSelector(String orgId, String searchKey);
}
