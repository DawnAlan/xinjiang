
package com.cj.sys.modular.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.sys.modular.resource.entity.SysSpa;
import com.cj.sys.modular.resource.param.spa.SysSpaAddParam;
import com.cj.sys.modular.resource.param.spa.SysSpaEditParam;
import com.cj.sys.modular.resource.param.spa.SysSpaIdParam;
import com.cj.sys.modular.resource.param.spa.SysSpaPageParam;

import java.util.List;

/**
 * 单页面Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:03
 **/
public interface SysSpaService extends IService<SysSpa> {

    /**
     * 获取单页面分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysSpa> page(SysSpaPageParam sysSpaPageParam);

    /**
     * 添加单页面
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysSpaAddParam sysSpaAddParam);

    /**
     * 编辑单页面
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysSpaEditParam sysSpaEditParam);

    /**
     * 删除单页面
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysSpaIdParam> sysSpaIdParamList);

    /**
     * 获取单页面详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysSpa detail(SysSpaIdParam sysSpaIdParam);

    /**
     * 获取单页面详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysSpa queryEntity(String id);
}
