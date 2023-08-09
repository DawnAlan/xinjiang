
package com.cj.biz.modular.position.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.biz.modular.position.entity.BizPosition;
import com.cj.biz.modular.position.param.*;

import java.util.List;

/**
 * 岗位Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface BizPositionService extends IService<BizPosition> {

    /**
     * 获取岗位分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizPosition> page(BizPositionPageParam bizPositionPageParam);

    /**
     * 添加岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(BizPositionAddParam bizPositionAddParam);

    /**
     * 编辑岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(BizPositionEditParam bizPositionEditParam);

    /**
     * 删除岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<BizPositionIdParam> bizPositionIdParamList);

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    BizPosition detail(BizPositionIdParam bizPositionIdParam);

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    BizPosition queryEntity(String id);

    /**
     * 根据机构id和岗位名称获取岗位id，有则返回，无则创建
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    String getPositionIdByPositionNameWithCreate(String orgId, String positionName);

    /* ====岗位部分所需要用到的选择器==== */

    /**
     * 获取机构树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizPosition> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam);
}
