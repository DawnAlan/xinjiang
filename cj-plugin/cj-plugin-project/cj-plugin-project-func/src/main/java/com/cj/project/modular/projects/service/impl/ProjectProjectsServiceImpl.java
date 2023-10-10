package com.cj.project.modular.projects.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.projects.entity.ProjectProjects;
import com.cj.project.modular.projects.mapper.ProjectProjectsMapper;
import com.cj.project.modular.projects.param.ProjectProjectsAddParam;
import com.cj.project.modular.projects.param.ProjectProjectsEditParam;
import com.cj.project.modular.projects.param.ProjectProjectsIdParam;
import com.cj.project.modular.projects.param.ProjectProjectsPageParam;
import com.cj.project.modular.projects.service.ProjectProjectsService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目Service接口实现类
 *
 * @author Lb
 * @date  2023/09/01 12:29
 **/
@Service
public class ProjectProjectsServiceImpl extends ServiceImpl<ProjectProjectsMapper, ProjectProjects> implements ProjectProjectsService {


    @Override
    public Page<ProjectProjects> page(ProjectProjectsPageParam projectProjectsPageParam) {
        QueryWrapper<ProjectProjects> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectProjectsPageParam.getName())) {
            queryWrapper.lambda().like(ProjectProjects::getName, projectProjectsPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(projectProjectsPageParam.getCode())) {
            queryWrapper.lambda().eq(ProjectProjects::getCode, projectProjectsPageParam.getCode());
        }
        if(ObjectUtil.isAllNotEmpty(projectProjectsPageParam.getSortField(), projectProjectsPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(projectProjectsPageParam.getSortOrder());
            queryWrapper.orderBy(true, projectProjectsPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(projectProjectsPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ProjectProjects::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        List<ProjectProjects> projectsList = this.getAllProjectsList();
        List<TreeNode<String>> treeNodeList = projectsList.stream().map(projects ->
                        new TreeNode<>(projects.getId(), projects.getParentId(),
                                projects.getName(), projects.getSortCode()).setExtra(JSONUtil.parseObj(projects)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProjectProjectsAddParam projectProjectsAddParam) {
        ProjectProjects projectProjects = BeanUtil.toBean(projectProjectsAddParam, ProjectProjects.class);
        this.save(projectProjects);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ProjectProjectsEditParam projectProjectsEditParam) {
        ProjectProjects projectProjects = this.queryEntity(projectProjectsEditParam.getId());
        BeanUtil.copyProperties(projectProjectsEditParam, projectProjects);
        this.updateById(projectProjects);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ProjectProjectsIdParam> projectProjectsIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(projectProjectsIdParamList, ProjectProjectsIdParam::getId));
    }

    @Override
    public ProjectProjects detail(ProjectProjectsIdParam projectProjectsIdParam) {
        return this.queryEntity(projectProjectsIdParam.getId());
    }

    @Override
    public ProjectProjects queryEntity(String id) {
        ProjectProjects projectProjects = this.getById(id);
        if(ObjectUtil.isEmpty(projectProjects)) {
            throw new CommonException("项目不存在，id值为：{}", id);
        }
        return projectProjects;
    }

    @Override
    public List<ProjectProjects> getProjectsList(String code, String name) {

        return this.list(new LambdaQueryWrapper<ProjectProjects>()
                .eq(ObjectUtil.isNotEmpty(code),ProjectProjects::getCode,code)
                .like(ObjectUtil.isNotEmpty(code),ProjectProjects::getName,name)
                .orderByDesc(ProjectProjects::getSortCode));
    }

    @Override
    public List<ProjectProjects> getAllProjectsList() {
        return this.list(new LambdaQueryWrapper<ProjectProjects>()
                .orderByAsc(ProjectProjects::getSortCode));
    }
}
