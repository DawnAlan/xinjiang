package com.cj.project.modular.instruments.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.api.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.param.ProjectInstrumentsAddParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsEditParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsIdParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsPageParam;

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
    Page<ProjectInstruments> page(ProjectInstrumentsPageParam projectInstrumentsPageParam);

    /**
     * 添加项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void add(ProjectInstrumentsAddParam projectInstrumentsAddParam);

    /**
     * 编辑项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void edit(ProjectInstrumentsEditParam projectInstrumentsEditParam);

    /**
     * 删除项目仪器表
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    void delete(List<ProjectInstrumentsIdParam> projectInstrumentsIdParamList);

    /**
     * 获取项目仪器表详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     */
    ProjectInstruments detail(ProjectInstrumentsIdParam projectInstrumentsIdParam);

    /**
     * 获取项目仪器表详情
     *
     * @author Lb
     * @date  2023/09/02 18:12
     **/
    ProjectInstruments queryEntity(String id);
}
