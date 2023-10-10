package com.cj.project.modular.fiducial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.entity.FiducialPara;
import com.cj.project.modular.fiducial.param.*;
import com.cj.project.modular.fiducial.result.FiducialResult;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialParaService;
import com.cj.project.modular.fiducial.service.FiducialService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 测点考证表Service接口实现类
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Service
public class FiducialServiceImpl implements FiducialService {

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private FiducialParaService fiducialParaService;

    @Override
    public Page<FiducialResult> page(Page<FiducialBase> fiducialBasePage) {
        Page<FiducialResult> result = new Page<>();
        BeanUtils.copyProperties(fiducialBasePage,result);
        /*result.setTotal(fiducialBasePage.getTotal());
        result.setSize(fiducialBasePage.getSize());
        result.setTotal(fiducialBasePage.getTotal());
        result.setCurrent(fiducialBasePage.getCurrent());
        result.setOrders(fiducialBasePage.getOrders());
        result.setMaxLimit(fiducialBasePage.getMaxLimit());*/

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(String projectCode, String instrumentID, Map<String,Object> fieldMaps) {
        //Base
        FiducialBase fiducialBase = BeanUtil.toBean(fieldMaps, FiducialBase.class);
        fiducialBase.setInstrumentType(instrumentID);
        fiducialBaseService.add(fiducialBase);
        String fiducialId = fiducialBase.getId();
        //分离Base、para字段
        Map<String,Object> paramsMap = fieldMaps;
        Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
        for (Field field : baseFields) {
            if(fieldMaps.containsKey(field.getName()))
                paramsMap.remove(field.getName());
        }
        //para
        List<FiducialParaAddParam> paraFieldList = new ArrayList<>();
        for (String mapkey :
                paramsMap.keySet()){
            FiducialParaAddParam para = new FiducialParaAddParam();
            para.setPointId(fiducialId);
            para.setFieldKey(mapkey);
            para.setFieldValue(paramsMap.get(mapkey).toString());
            paraFieldList.add(para);
        }
        fiducialParaService.adds(paraFieldList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void adds(List<FiducialAddParam> fiducialAddParamList) {
        for (FiducialAddParam fiducialAddParam : fiducialAddParamList
             ) {
            Map<String,Object> fieldMaps = fiducialAddParam.getDetail();
            //Base
            FiducialBase fiducialBase = BeanUtil.toBean(fieldMaps, FiducialBase.class);
            fiducialBase.setInstrumentType(fiducialAddParam.getInstrumentType());
            fiducialBaseService.add(fiducialBase);
            String fiducialId = fiducialBase.getId();
            //分离Base、para字段
            Map<String,Object> paramsMap = fieldMaps;
            Field[] baseFields = ReflectUtil.getFields(FiducialBase.class);
            for (Field field : baseFields) {
                if(fieldMaps.containsKey(field.getName()))
                    paramsMap.remove(field.getName());
            }
            //para
            List<FiducialParaAddParam> paraFieldList = new ArrayList<>();
            for (String mapkey :
                    paramsMap.keySet()){
                FiducialParaAddParam para = new FiducialParaAddParam();
                para.setPointId(fiducialId);
                para.setFieldKey(mapkey);
                para.setFieldValue(paramsMap.get(mapkey).toString());
                paraFieldList.add(para);
            }
            fiducialParaService.adds(paraFieldList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FiducialBaseEditParam fiducialBaseEditParam) {
        /*FiducialBase fiducialBase = this.queryEntity(fiducialBaseEditParam.getId());
        BeanUtil.copyProperties(fiducialBaseEditParam, fiducialBase);
        this.updateById(fiducialBase);*/
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FiducialIdParam> fiducialIdParamList) {
        // 执行删除Base
        fiducialBaseService.delete(fiducialIdParamList);
        // 执行删除Para
        fiducialParaService.deleteByPoint(fiducialIdParamList);
    }

    @Override
    public FiducialResult ToFiducial(FiducialBase fiducialBase, List<FiducialPara> fiducialParas) {
        FiducialResult fiducialResult = new FiducialResult();
        fiducialResult.setId(fiducialBase.getId());
        fiducialResult.setProjectCode(fiducialBase.getProjectCode());
        fiducialResult.setInstrumentType(fiducialBase.getInstrumentType());
        fiducialResult.setPointName(fiducialBase.getPointName());
        fiducialResult.setPointAlias(fiducialBase.getPointAlias());
        Map<String, Object> detail = BeanUtil.beanToMap(fiducialBase);
        //Para
        for (FiducialPara para : fiducialParas
        ) {
            detail.put(para.getFieldKey(),para.getFieldValue());
        }
        fiducialResult.setDetail(detail);

        return fiducialResult;
    }

}
