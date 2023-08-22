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
package com.cj.textua.textua.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.textua.textua.entity.FiducialBase;
import com.cj.textua.textua.mapper.FiducialBaseMapper;
import com.cj.textua.textua.param.FiducialBaseAddParam;
import com.cj.textua.textua.param.FiducialBaseEditParam;
import com.cj.textua.textua.param.FiducialBaseIdParam;
import com.cj.textua.textua.param.FiducialBasePageParam;
import com.cj.textua.textua.service.FiducialBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 测点信息基本表Service接口实现类
 *
 * @author yancheng
 * @date  2023/08/21 20:32
 **/
@Service
public class FiducialBaseServiceImpl extends ServiceImpl<FiducialBaseMapper, FiducialBase> implements FiducialBaseService {

    @Override
    public Page<FiducialBase> page(FiducialBasePageParam fiducialBasePageParam) {
        QueryWrapper<FiducialBase> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isAllNotEmpty(fiducialBasePageParam.getSortField(), fiducialBasePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(fiducialBasePageParam.getSortOrder());
            queryWrapper.orderBy(true, fiducialBasePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(fiducialBasePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FiducialBase::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(FiducialBaseAddParam fiducialBaseAddParam) {
        FiducialBase fiducialBase = BeanUtil.toBean(fiducialBaseAddParam, FiducialBase.class);
        this.save(fiducialBase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FiducialBaseEditParam fiducialBaseEditParam) {
        FiducialBase fiducialBase = this.queryEntity(fiducialBaseEditParam.getId());
        BeanUtil.copyProperties(fiducialBaseEditParam, fiducialBase);
        this.updateById(fiducialBase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FiducialBaseIdParam> fiducialBaseIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(fiducialBaseIdParamList, FiducialBaseIdParam::getId));
    }

    @Override
    public FiducialBase detail(FiducialBaseIdParam fiducialBaseIdParam) {
        return this.queryEntity(fiducialBaseIdParam.getId());
    }

    @Override
    public FiducialBase queryEntity(String id) {
        FiducialBase fiducialBase = this.getById(id);
        if(ObjectUtil.isEmpty(fiducialBase)) {
            throw new CommonException("测点信息基本表不存在，id值为：{}", id);
        }
        return fiducialBase;
    }
}
