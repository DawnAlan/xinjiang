
package com.cj.sys.modular.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.sys.modular.resource.entity.SysModule;
import com.cj.sys.modular.resource.param.module.SysModuleAddParam;
import com.cj.sys.modular.resource.param.module.SysModuleEditParam;
import com.cj.sys.modular.resource.param.module.SysModuleIdParam;
import com.cj.sys.modular.resource.param.module.SysModulePageParam;

import java.util.List;

/**
 * 模块Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:03
 **/
public interface SysModuleService extends IService<SysModule> {

    /**
     * 获取模块分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysModule> page(SysModulePageParam sysModulePageParam);

    /**
     * 添加模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysModuleAddParam sysModuleAddParam);

    /**
     * 编辑模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysModuleEditParam sysModuleEditParam);

    /**
     * 删除模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysModuleIdParam> sysModuleIdParamList);

    /**
     * 获取模块详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysModule detail(SysModuleIdParam sysModuleIdParam);

    /**
     * 获取模块详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysModule queryEntity(String id);
}
