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
package com.cj.biz.modular.columnconfig.service.impl;

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
import com.cj.biz.modular.columnconfig.entity.ArtdataColumnconfig;
import com.cj.biz.modular.columnconfig.mapper.ArtdataColumnconfigMapper;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigAddParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigEditParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigIdParam;
import com.cj.biz.modular.columnconfig.param.ArtdataColumnconfigPageParam;
import com.cj.biz.modular.columnconfig.service.ArtdataColumnconfigService;

import java.util.List;

/**
 * 格式配置表Service接口实现类
 *
 * @author dengdi
 * @date  2023/08/22 10:10
 **/
@Service
public class ArtdataColumnconfigServiceImpl extends ServiceImpl<ArtdataColumnconfigMapper, ArtdataColumnconfig> implements ArtdataColumnconfigService {

    @Override
    public Page<ArtdataColumnconfig> page(ArtdataColumnconfigPageParam artdataColumnconfigPageParam) {
        QueryWrapper<ArtdataColumnconfig> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(artdataColumnconfigPageParam.getPointname())) {
            queryWrapper.lambda().like(ArtdataColumnconfig::getPointname, artdataColumnconfigPageParam.getPointname());
        }
        if(ObjectUtil.isNotEmpty(artdataColumnconfigPageParam.getInstrumentname())) {
            queryWrapper.lambda().like(ArtdataColumnconfig::getInstrumentname, artdataColumnconfigPageParam.getInstrumentname());
        }
        if(ObjectUtil.isNotEmpty(artdataColumnconfigPageParam.getFilepath())) {
            queryWrapper.lambda().like(ArtdataColumnconfig::getFilepath, artdataColumnconfigPageParam.getFilepath());
        }
        if(ObjectUtil.isAllNotEmpty(artdataColumnconfigPageParam.getSortField(), artdataColumnconfigPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(artdataColumnconfigPageParam.getSortOrder());
            queryWrapper.orderBy(true, artdataColumnconfigPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(artdataColumnconfigPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ArtdataColumnconfig::getUid);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ArtdataColumnconfigAddParam artdataColumnconfigAddParam) {
        ArtdataColumnconfig artdataColumnconfig = BeanUtil.toBean(artdataColumnconfigAddParam, ArtdataColumnconfig.class);
        this.save(artdataColumnconfig);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ArtdataColumnconfigEditParam artdataColumnconfigEditParam) {
        ArtdataColumnconfig artdataColumnconfig = this.queryEntity(artdataColumnconfigEditParam.getUid());
        BeanUtil.copyProperties(artdataColumnconfigEditParam, artdataColumnconfig);
        this.updateById(artdataColumnconfig);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ArtdataColumnconfigIdParam> artdataColumnconfigIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(artdataColumnconfigIdParamList, ArtdataColumnconfigIdParam::getUid));
    }

    @Override
    public ArtdataColumnconfig detail(ArtdataColumnconfigIdParam artdataColumnconfigIdParam) {
        return this.queryEntity(artdataColumnconfigIdParam.getUid());
    }

    @Override
    public ArtdataColumnconfig queryEntity(String id) {
        ArtdataColumnconfig artdataColumnconfig = this.getById(id);
        if(ObjectUtil.isEmpty(artdataColumnconfig)) {
            throw new CommonException("格式配置表不存在，id值为：{}", id);
        }
        return artdataColumnconfig;
    }
}
