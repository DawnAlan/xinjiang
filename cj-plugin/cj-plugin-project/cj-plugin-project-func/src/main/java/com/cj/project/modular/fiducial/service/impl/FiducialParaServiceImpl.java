package com.cj.project.modular.fiducial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.fiducial.param.FiducialIdParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.project.modular.fiducial.entity.FiducialPara;
import com.cj.project.modular.fiducial.mapper.FiducialParaMapper;
import com.cj.project.modular.fiducial.param.FiducialParaAddParam;
import com.cj.project.modular.fiducial.param.FiducialParaIdParam;
import com.cj.project.modular.fiducial.service.FiducialParaService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考证参数表Service接口实现类
 *
 * @author Lb
 * @date  2023/09/04 19:57
 **/
@Service
public class FiducialParaServiceImpl extends ServiceImpl<FiducialParaMapper, FiducialPara> implements FiducialParaService {

    /*@Override
    public Page<FiducialPara> page(FiducialParaPageParam fiducialParaPageParam) {
        QueryWrapper<FiducialPara> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(fiducialParaPageParam.getPointId())) {
            queryWrapper.lambda().eq(FiducialPara::getPointId, fiducialParaPageParam.getPointId());
        }
        if(ObjectUtil.isAllNotEmpty(fiducialParaPageParam.getSortField(), fiducialParaPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(fiducialParaPageParam.getSortOrder());
            queryWrapper.orderBy(true, fiducialParaPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(fiducialParaPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FiducialPara::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }*/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(FiducialParaAddParam fiducialParaAddParam) {
        FiducialPara fiducialPara = BeanUtil.toBean(fiducialParaAddParam, FiducialPara.class);
        this.save(fiducialPara);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void adds(List<FiducialParaAddParam>  fiducialParaAddParams) {
        List<FiducialPara> fiducialParas = new ArrayList<>();
        for (FiducialParaAddParam para : fiducialParaAddParams
             ) {
            FiducialPara fiducialPara = BeanUtil.toBean(para, FiducialPara.class);
            fiducialParas.add(fiducialPara);
        }
        this.saveBatch(fiducialParas);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FiducialParaIdParam> fiducialParaIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(fiducialParaIdParamList, FiducialParaIdParam::getId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByPoint(List<FiducialIdParam> fiducialIdParamList) {
        // 执行删除
        List<String> points = fiducialIdParamList.stream().map(FiducialIdParam::getId).collect(Collectors.toList());
        this.remove(new QueryWrapper<FiducialPara>().lambda().in(FiducialPara::getPointId,points));
    }

    @Override
    public List<FiducialPara> getList(FiducialIdParam fiducialIdParam) {
        return this.list(new QueryWrapper<FiducialPara>().lambda().eq(FiducialPara::getPointId,fiducialIdParam.getId())
                .orderByDesc(FiducialPara::getPointId));
    }

}
