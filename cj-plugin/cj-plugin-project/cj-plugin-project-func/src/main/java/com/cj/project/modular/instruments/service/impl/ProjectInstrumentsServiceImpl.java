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
package com.cj.project.modular.instruments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
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
