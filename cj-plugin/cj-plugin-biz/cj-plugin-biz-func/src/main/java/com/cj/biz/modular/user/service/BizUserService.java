
package com.cj.biz.modular.user.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.biz.modular.org.entity.BizOrg;
import com.cj.biz.modular.position.entity.BizPosition;
import com.cj.biz.modular.user.entity.BizUser;
import com.cj.biz.modular.user.param.*;
import com.cj.biz.modular.user.result.BizUserRoleResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 人员Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface BizUserService extends IService<BizUser> {

    /**
     * 获取人员分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizUser> page(BizUserPageParam bizUserPageParam);

    /**
     * 添加人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(BizUserAddParam bizUserAddParam);

    /**
     * 编辑人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(BizUserEditParam bizUserEditParam);

    /**
     * 删除人员
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<BizUserIdParam> bizUserIdParamList);

    /**
     * 获取人员详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    BizUser detail(BizUserIdParam bizUserIdPara);

    /**
     * 获取人员详情
     *
     * @author xuyuxiang
     * @date 2022/7/26 17:21
     **/
    BizUser queryEntity(String id);

    /**
     * 禁用人员
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:20
     **/
    void disableUser(BizUserIdParam bizUserIdParam);

    /**
     * 启用人员
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:21
     **/
    void enableUser(BizUserIdParam bizUserIdParam);

    /**
     * 重置人员密码
     *
     * @author xuyuxiang
     * @date 2022/7/5 18:22
     **/
    void resetPassword(BizUserIdParam bizUserIdParam);

    /**
     * 获取人员拥有角色
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<String> ownRole(BizUserIdParam bizUserIdParam);

    /**
     * 给人员授权角色
     *
     * @author xuyuxiang
     * @date 2022/4/29 11:13
     **/
    void grantRole(BizUserGrantRoleParam bizUserGrantRoleParam);

    /**
     * 用户导出
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException;

    /**
     * 导出用户个人信息
     *
     * @author xuyuxiang
     * @date 2022/8/8 13:16
     **/
    void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException;

    /* ====人员部分所需要用到的选择器==== */

    /**
     * 获取机构树选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取机构列表选择器
     *
     * @author xuyuxiang
     * @date 2022/7/22 13:34
     **/
    Page<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam);

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam);

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/5/13 21:00
     */
    Page<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam);

    /**
     * 获取人员选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam);
}
