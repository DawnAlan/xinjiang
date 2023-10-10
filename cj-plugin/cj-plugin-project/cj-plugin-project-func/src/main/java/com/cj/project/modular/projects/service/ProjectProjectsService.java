/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cj.project.modular.projects.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.modular.projects.entity.ProjectProjects;
import com.cj.project.modular.projects.param.ProjectProjectsAddParam;
import com.cj.project.modular.projects.param.ProjectProjectsEditParam;
import com.cj.project.modular.projects.param.ProjectProjectsIdParam;
import com.cj.project.modular.projects.param.ProjectProjectsPageParam;

import java.util.List;

/**
 * 项目Service接口
 *
 * @author Lb
 * @date  2023/09/01 12:29
 **/
public interface ProjectProjectsService extends IService<ProjectProjects> {

    /**
     * 获取项目分页
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    Page<ProjectProjects> page(ProjectProjectsPageParam projectProjectsPageParam);

    /**
     * 获取项目树
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    List<Tree<String>> tree();


    /**
     * 添加项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    void add(ProjectProjectsAddParam projectProjectsAddParam);

    /**
     * 编辑项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    void edit(ProjectProjectsEditParam projectProjectsEditParam);

    /**
     * 删除项目
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    void delete(List<ProjectProjectsIdParam> projectProjectsIdParamList);

    /**
     * 获取项目详情
     *
     * @author Lb
     * @date  2023/09/01 12:29
     */
    ProjectProjects detail(ProjectProjectsIdParam projectProjectsIdParam);

    /**
     * 获取项目详情
     *
     * @author Lb
     * @date  2023/09/01 12:29
     **/
    ProjectProjects queryEntity(String id);

    /**
     * 获取项目列表
     * @author lb
     */
    List<ProjectProjects> getProjectsList(String code, String name);
    List<ProjectProjects> getAllProjectsList();
}
