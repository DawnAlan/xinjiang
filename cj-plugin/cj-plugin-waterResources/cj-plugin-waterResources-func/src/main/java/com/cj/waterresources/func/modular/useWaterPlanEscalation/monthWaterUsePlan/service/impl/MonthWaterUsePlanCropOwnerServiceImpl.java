package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanCropOwnerMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropOwnerService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * (MonthWaterUsePlanCropOwner)表服务实现类
 *
 * @author makejava
 * @since 2024-03-23 17:57:00
 */
@Service("monthWaterUsePlanCropOwnerService")
public class MonthWaterUsePlanCropOwnerServiceImpl extends ServiceImpl<MonthWaterUsePlanCropOwnerMapper, MonthWaterUsePlanCropOwner> implements MonthWaterUsePlanCropOwnerService {

    @Autowired
    private MonthWaterUsePlanOwnerService monthWaterUsePlanOwnerService;

    @Override
    public RestResponse<List<MonthWaterUsePlanCropOwner>> selectList(MonthCropSelectListReq req) {
        List<MonthWaterUsePlanCropOwner> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getUnit()),MonthWaterUsePlanCropOwner::getUnit, req.getUnit()).
                eq(StringUtils.isNotEmpty(req.getCropType()),MonthWaterUsePlanCropOwner::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getArea()),MonthWaterUsePlanCropOwner::getArea, req.getArea()).
                eq(req.getYear()!=null,MonthWaterUsePlanCropOwner::getYear, req.getYear()).
                eq(req.getMonth()!=null,MonthWaterUsePlanCropOwner::getMonth, req.getMonth()).
                eq(MonthWaterUsePlanCropOwner::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<MonthWaterUsePlanCropOwner> monthWaterUsePlanCropOwnerList) {
        MonthWaterUsePlanCropOwner req = monthWaterUsePlanCropOwnerList.get(0);
        boolean b = this.saveBatch(monthWaterUsePlanCropOwnerList);
        if(b){
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;
            List<MonthWaterUsePlanCropOwner> list = this.lambdaQuery().eq(MonthWaterUsePlanCropOwner::getYear, req.getYear()).
                    eq(MonthWaterUsePlanCropOwner::getArea, req.getArea()).
                    eq(MonthWaterUsePlanCropOwner::getUnitId, req.getUnitId()).
                    eq(MonthWaterUsePlanCropOwner::getDel, 0).
                    eq(MonthWaterUsePlanCropOwner::getMonth, req.getMonth()).list();
            if(null != list && list.size()>0){
                for(MonthWaterUsePlanCropOwner crop:list){
                    earlyOctober +=crop.getEarlyOctoberWaterDemandOwner()==null?0.0:crop.getEarlyOctoberWaterDemandOwner();
                    midDay +=crop.getMidDayWaterDemandOwner()==null?0.0:crop.getMidDayWaterDemandOwner();
                    laterOctober +=crop.getLaterOctoberWaterDemandOwner()==null?0.0:crop.getLaterOctoberWaterDemandOwner();
                }
            }
            boolean update1 = monthWaterUsePlanOwnerService.lambdaUpdate().
                    set(MonthWaterUsePlanOwner::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlanOwner::getMidDay, midDay).
                    set(MonthWaterUsePlanOwner::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlanOwner::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlanOwner::getYear,req.getYear()).
                    eq(MonthWaterUsePlanOwner::getMonth,req.getMonth()).
                    eq(MonthWaterUsePlanOwner::getArea, req.getArea()).
                    eq(MonthWaterUsePlanOwner::getUnitId, req.getUnitId()).update();
            if(update1){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("新增供水科专看干渠数据失败");
            }
        }else {
            return RestResponse.no("新增供水科专看作物数据失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(MonthCropImportParamReq req) {
        boolean remove = this.lambdaUpdate().eq(MonthWaterUsePlanCropOwner::getYear, req.getYear()).eq(MonthWaterUsePlanCropOwner::getMonth, req.getMonth()).
                eq(MonthWaterUsePlanCropOwner::getUnitId, req.getUnitId()).eq(MonthWaterUsePlanCropOwner::getArea, req.getArea()).remove();
        if(remove){
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;
            List<MonthWaterUsePlanCropOwner> list = this.lambdaQuery().eq(MonthWaterUsePlanCropOwner::getYear, req.getYear()).
                    eq(MonthWaterUsePlanCropOwner::getArea, req.getArea()).
                    eq(MonthWaterUsePlanCropOwner::getUnitId, req.getUnitId()).
                    eq(MonthWaterUsePlanCropOwner::getDel, 0).
                    eq(MonthWaterUsePlanCropOwner::getMonth, req.getMonth()).list();
            if(null != list && list.size()>0){
                for(MonthWaterUsePlanCropOwner crop:list){
                    earlyOctober +=crop.getEarlyOctoberWaterDemandOwner()==null?0.0:crop.getEarlyOctoberWaterDemandOwner();
                    midDay +=crop.getMidDayWaterDemandOwner()==null?0.0:crop.getMidDayWaterDemandOwner();
                    laterOctober +=crop.getLaterOctoberWaterDemandOwner()==null?0.0:crop.getLaterOctoberWaterDemandOwner();
                }
            }
            boolean update1 = monthWaterUsePlanOwnerService.lambdaUpdate().
                    set(MonthWaterUsePlanOwner::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlanOwner::getMidDay, midDay).
                    set(MonthWaterUsePlanOwner::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlanOwner::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlanOwner::getYear,req.getYear()).
                    eq(MonthWaterUsePlanOwner::getMonth,req.getMonth()).
                    eq(MonthWaterUsePlanOwner::getArea, req.getArea()).
                    eq(MonthWaterUsePlanOwner::getUnitId, req.getUnitId()).update();
            if(update1){
                return RestResponse.ok("删除成功");
            }else {
                return RestResponse.no("删除失败(供水科)干渠");
            }
        }else {
            return RestResponse.no("删除失败(供水科)作物");
        }
    }
}

