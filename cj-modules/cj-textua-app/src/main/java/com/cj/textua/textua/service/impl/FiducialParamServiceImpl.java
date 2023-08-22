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
import com.cj.textua.textua.entity.FiducialParam;
import com.cj.textua.textua.mapper.FiducialParamMapper;
import com.cj.textua.textua.param.FiducialParamAddParam;
import com.cj.textua.textua.param.FiducialParamEditParam;
import com.cj.textua.textua.param.FiducialParamIdParam;
import com.cj.textua.textua.param.FiducialParamPageParam;
import com.cj.textua.textua.service.FiducialParamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 考证参数信息表(跟字段配置表关联)Service接口实现类
 *
 * @author yancheng
 * @date  2023/08/21 20:50
 **/
@Service
public class FiducialParamServiceImpl extends ServiceImpl<FiducialParamMapper, FiducialParam> implements FiducialParamService {

    @Override
    public Page<FiducialParam> page(FiducialParamPageParam fiducialParamPageParam) {
        QueryWrapper<FiducialParam> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isAllNotEmpty(fiducialParamPageParam.getSortField(), fiducialParamPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(fiducialParamPageParam.getSortOrder());
            queryWrapper.orderBy(true, fiducialParamPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(fiducialParamPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FiducialParam::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(FiducialParamAddParam fiducialParamAddParam) {
        FiducialParam fiducialParam = BeanUtil.toBean(fiducialParamAddParam, FiducialParam.class);
        this.save(fiducialParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FiducialParamEditParam fiducialParamEditParam) {
        FiducialParam fiducialParam = this.queryEntity(fiducialParamEditParam.getId());
        BeanUtil.copyProperties(fiducialParamEditParam, fiducialParam);
        this.updateById(fiducialParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FiducialParamIdParam> fiducialParamIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(fiducialParamIdParamList, FiducialParamIdParam::getId));
    }

    @Override
    public FiducialParam detail(FiducialParamIdParam fiducialParamIdParam) {
        return this.queryEntity(fiducialParamIdParam.getId());
    }

    @Override
    public FiducialParam queryEntity(String id) {
        FiducialParam fiducialParam = this.getById(id);
        if(ObjectUtil.isEmpty(fiducialParam)) {
            throw new CommonException("考证参数信息表(跟字段配置表关联)不存在，id值为：{}", id);
        }
        return fiducialParam;
    }
}
