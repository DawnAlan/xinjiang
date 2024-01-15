package com.cj.project.modular.instruments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.api.instruments.dto.ProjectInstrumentsPageDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.api.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.mapper.ProjectInstrumentsMapper;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;

import java.util.List;

/**
 * 项目仪器表Service接口实现类
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
@Service
public class ProjectInstrumentsServiceImpl extends ServiceImpl<ProjectInstrumentsMapper, ProjectInstruments> implements ProjectInstrumentsService {

    @Override
    public Page<ProjectInstruments> page(ProjectInstrumentsPageDto projectInstrumentsPageDto) {
        QueryWrapper<ProjectInstruments> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectInstrumentsPageDto.getProjectCode())) {
            queryWrapper.lambda().eq(ProjectInstruments::getProjectCode, projectInstrumentsPageDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(projectInstrumentsPageDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ProjectInstruments::getInstrumentType, projectInstrumentsPageDto.getInstrumentType());
        }
        if(ObjectUtil.isAllNotEmpty(projectInstrumentsPageDto.getSortField(), projectInstrumentsPageDto.getSortOrder())) {
            CommonSortOrderEnum.validate(projectInstrumentsPageDto.getSortOrder());
            queryWrapper.orderBy(true, projectInstrumentsPageDto.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(projectInstrumentsPageDto.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ProjectInstruments::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<ProjectInstruments> getList(String projectCode, String monitorName, String instrumentType, String instrumentMetaType) {
        return null;
    }

    @Override
    public List<Tree<String>> tree(String projectCode) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProjectInstruments projectInstruments) {
        this.save(projectInstruments);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ProjectInstruments projectInstruments) {
        this.queryEntity(projectInstruments.getId());
        this.updateById(projectInstruments);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> idList) {
        // 执行删除
        this.removeByIds(idList);
    }

    @Override
    public ProjectInstruments detail(String id) {
        return this.queryEntity(id);
    }

    @Override
    public ProjectInstruments queryEntity(String id) {
        ProjectInstruments projectInstruments = this.getById(id);
        if(ObjectUtil.isEmpty(projectInstruments)) {
            throw new CommonException("项目仪器表不存在，id值为：{}", id);
        }
        return projectInstruments;
    }
}
