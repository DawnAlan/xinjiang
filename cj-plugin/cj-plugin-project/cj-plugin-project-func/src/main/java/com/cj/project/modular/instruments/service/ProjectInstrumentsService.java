package com.cj.project.modular.instruments.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.api.instruments.dto.ProjectInstrumentsPageDto;
import com.cj.project.api.instruments.entity.ProjectInstruments;

import java.util.List;

/**
 * 项目仪器表Service接口
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
public interface ProjectInstrumentsService extends IService<ProjectInstruments> {

    /**
     * 获取项目仪器表分页
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    Page<ProjectInstruments> page(ProjectInstrumentsPageDto projectInstrumentsPageDto);

    List<ProjectInstruments> getList(String projectCode,String monitorName,String instrumentType,String instrumentMetaType);
    List<Tree<String>> tree(String projectCode);

    /**
     * 添加项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void add(ProjectInstruments projectInstruments);

    /**
     * 编辑项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void edit(ProjectInstruments projectInstruments);

    /**
     * 删除项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void delete(List<String> idList);

    /**
     * 获取项目仪器表详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    ProjectInstruments detail(String id);

    /**
     * 获取项目仪器表详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     **/
    ProjectInstruments queryEntity(String id);
}
