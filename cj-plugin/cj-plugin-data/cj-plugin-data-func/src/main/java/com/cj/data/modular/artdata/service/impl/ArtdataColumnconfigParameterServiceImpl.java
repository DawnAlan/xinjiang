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
package com.cj.data.modular.artdata.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.data.api.artdata.entity.ArtdataColumnconfigParameter;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterAddParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterEditParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterIdParam;
import com.cj.data.api.artdata.param.ArtdataColumnconfigParameterPageParam;
import com.cj.data.modular.artdata.service.ArtdataColumnconfigParameterService;
import com.cj.data.modular.artdata.mapper.ArtdataColumnconfigParameterMapper;

import java.util.List;

/**
 * 模板列参数表Service接口实现类
 *
 * @author dd
 * @date  2024/01/12 17:25
 **/
@Service
public class ArtdataColumnconfigParameterServiceImpl extends ServiceImpl<ArtdataColumnconfigParameterMapper, ArtdataColumnconfigParameter> implements ArtdataColumnconfigParameterService {

    @Override
    public Page<ArtdataColumnconfigParameter> page(ArtdataColumnconfigParameterPageParam artdataColumnconfigParameterPageParam) {
        QueryWrapper<ArtdataColumnconfigParameter> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(artdataColumnconfigParameterPageParam.getColumnconfigId())) {
            queryWrapper.lambda().eq(ArtdataColumnconfigParameter::getColumnconfigId, artdataColumnconfigParameterPageParam.getColumnconfigId());
        }
        if(ObjectUtil.isAllNotEmpty(artdataColumnconfigParameterPageParam.getSortField(), artdataColumnconfigParameterPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(artdataColumnconfigParameterPageParam.getSortOrder());
            queryWrapper.orderBy(true, artdataColumnconfigParameterPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(artdataColumnconfigParameterPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ArtdataColumnconfigParameter::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ArtdataColumnconfigParameterAddParam artdataColumnconfigParameterAddParam) {
        ArtdataColumnconfigParameter artdataColumnconfigParameter = BeanUtil.toBean(artdataColumnconfigParameterAddParam, ArtdataColumnconfigParameter.class);
        this.save(artdataColumnconfigParameter);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ArtdataColumnconfigParameterEditParam artdataColumnconfigParameterEditParam) {
        ArtdataColumnconfigParameter artdataColumnconfigParameter = this.queryEntity(artdataColumnconfigParameterEditParam.getId());
        BeanUtil.copyProperties(artdataColumnconfigParameterEditParam, artdataColumnconfigParameter);
        this.updateById(artdataColumnconfigParameter);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ArtdataColumnconfigParameterIdParam> artdataColumnconfigParameterIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(artdataColumnconfigParameterIdParamList, ArtdataColumnconfigParameterIdParam::getId));
    }

    @Override
    public ArtdataColumnconfigParameter detail(ArtdataColumnconfigParameterIdParam artdataColumnconfigParameterIdParam) {
        return this.queryEntity(artdataColumnconfigParameterIdParam.getId());
    }

    @Override
    public ArtdataColumnconfigParameter queryEntity(String id) {
        ArtdataColumnconfigParameter artdataColumnconfigParameter = this.getById(id);
        if(ObjectUtil.isEmpty(artdataColumnconfigParameter)) {
            throw new CommonException("模板列参数表不存在，id值为：{}", id);
        }
        return artdataColumnconfigParameter;
    }
}
