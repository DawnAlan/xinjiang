package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanCropMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * 月用水计划作物表(MonthWaterUsePlanCrop)表服务实现类
 *
 * @author makejava
 * @since 2024-01-04 18:10:46
 */
@Service("monthWaterUsePlanCropService")
public class MonthWaterUsePlanCropServiceImpl extends ServiceImpl<MonthWaterUsePlanCropMapper, MonthWaterUsePlanCrop> implements MonthWaterUsePlanCropService {

    @Autowired
    private MonthWaterUsePlanService monthWaterUsePlanService;

    @Override
    public RestResponse<List<MonthWaterUsePlanCrop>> selectList(MonthCropSelectListReq req) {
        List<MonthWaterUsePlanCrop> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getUnit()),MonthWaterUsePlanCrop::getUnit, req.getUnit()).
                eq(StringUtils.isNotEmpty(req.getCropType()),MonthWaterUsePlanCrop::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getArea()),MonthWaterUsePlanCrop::getArea, req.getArea()).
                eq(req.getYear()!=null,MonthWaterUsePlanCrop::getYear, req.getYear()).
                eq(req.getMonth()!=null,MonthWaterUsePlanCrop::getMonth, req.getMonth()).
                eq(MonthWaterUsePlanCrop::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse update(MonthWaterUsePlanCrop monthWaterUsePlanCrop) {
        monthWaterUsePlanCrop.setUpdateTime(new Date());
        monthWaterUsePlanCrop.setTotal(
                (monthWaterUsePlanCrop.getEarlyOctober()==null?0.0:monthWaterUsePlanCrop.getEarlyOctober())+
                (monthWaterUsePlanCrop.getMidDay()==null?0.0:monthWaterUsePlanCrop.getMidDay())+
                (monthWaterUsePlanCrop.getLaterOctober()==null?0.0:monthWaterUsePlanCrop.getLaterOctober())
        );
        boolean b = this.updateById(monthWaterUsePlanCrop);
        if(b){
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;
            List<MonthWaterUsePlanCrop> list = this.lambdaQuery().eq(MonthWaterUsePlanCrop::getYear, monthWaterUsePlanCrop.getYear()).
                    eq(MonthWaterUsePlanCrop::getArea, monthWaterUsePlanCrop.getArea()).
                    eq(MonthWaterUsePlanCrop::getUnitId, monthWaterUsePlanCrop.getUnitId()).
                    eq(MonthWaterUsePlanCrop::getDel, 0).
                    eq(MonthWaterUsePlanCrop::getMonth, monthWaterUsePlanCrop.getMonth()).
                    ne(MonthWaterUsePlanCrop::getId,monthWaterUsePlanCrop.getId()).list();
            if(null != list && list.size()>0){
                for(MonthWaterUsePlanCrop crop:list){
                    earlyOctober +=crop.getEarlyOctober()==null?0.0:crop.getEarlyOctober();
                    midDay +=crop.getMidDay()==null?0.0:crop.getMidDay();
                    laterOctober +=crop.getLaterOctober()==null?0.0:crop.getLaterOctober();
                }
            }
            earlyOctober +=monthWaterUsePlanCrop.getEarlyOctober()==null?0.0:monthWaterUsePlanCrop.getEarlyOctober();
            midDay +=monthWaterUsePlanCrop.getMidDay()==null?0.0:monthWaterUsePlanCrop.getMidDay();
            laterOctober +=monthWaterUsePlanCrop.getLaterOctober()==null?0.0:monthWaterUsePlanCrop.getLaterOctober();
            boolean update = monthWaterUsePlanService.lambdaUpdate().
                    set(MonthWaterUsePlan::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlan::getMidDay, midDay).
                    set(MonthWaterUsePlan::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlan::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlan::getYear,monthWaterUsePlanCrop.getYear()).
                    eq(MonthWaterUsePlan::getMonth,monthWaterUsePlanCrop.getMonth()).
                    eq(MonthWaterUsePlan::getArea, monthWaterUsePlanCrop.getArea()).
                    eq(MonthWaterUsePlan::getUnitId, monthWaterUsePlanCrop.getUnitId()).update();
            if(update){
                return RestResponse.ok("更新成功");
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResponse.no("更新失败");
            }
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse add(MonthWaterUsePlanCrop monthWaterUsePlanCrop) {
        List<MonthWaterUsePlanCrop> list = this.lambdaQuery().eq(MonthWaterUsePlanCrop::getArea, monthWaterUsePlanCrop.getArea()).
                eq(MonthWaterUsePlanCrop::getUnit, monthWaterUsePlanCrop.getUnit()).
                eq(MonthWaterUsePlanCrop::getDel,0).
                eq(MonthWaterUsePlanCrop::getYear,monthWaterUsePlanCrop.getYear()).
                eq(MonthWaterUsePlanCrop::getMonth, monthWaterUsePlanCrop.getMonth()).
                eq(MonthWaterUsePlanCrop::getIrrigatedCrop, monthWaterUsePlanCrop.getIrrigatedCrop()).list();
        if(null!= list && list.size()>0){
            return RestResponse.no("该作物已存在，请勿重复添加");
        }
        monthWaterUsePlanCrop.setId(UUIDUtils.getUUID());
        monthWaterUsePlanCrop.setDel(0);
        monthWaterUsePlanCrop.setCreateTime(new Date());
        boolean save = this.save(monthWaterUsePlanCrop);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        MonthWaterUsePlanCrop monthWaterUsePlanCrop = this.getById(id);
        boolean update = this.lambdaUpdate().set(MonthWaterUsePlanCrop::getDel, 1).eq(MonthWaterUsePlanCrop::getId, id).update();
        if(update){
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;
            List<MonthWaterUsePlanCrop> list = this.lambdaQuery().eq(MonthWaterUsePlanCrop::getYear, monthWaterUsePlanCrop.getYear()).
                    eq(MonthWaterUsePlanCrop::getArea, monthWaterUsePlanCrop.getArea()).
                    eq(MonthWaterUsePlanCrop::getUnitId, monthWaterUsePlanCrop.getUnitId()).
                    eq(MonthWaterUsePlanCrop::getDel, 0).
                    eq(MonthWaterUsePlanCrop::getMonth, monthWaterUsePlanCrop.getMonth()).
                    ne(MonthWaterUsePlanCrop::getId,monthWaterUsePlanCrop.getId()).list();
            if(null != list && list.size()>0){
                for(MonthWaterUsePlanCrop crop:list){
                    earlyOctober +=crop.getEarlyOctober()==null?0.0:crop.getEarlyOctober();
                    midDay +=crop.getMidDay()==null?0.0:crop.getMidDay();
                    laterOctober +=crop.getLaterOctober()==null?0.0:crop.getLaterOctober();
                }
            }
            boolean update1 = monthWaterUsePlanService.lambdaUpdate().
                    set(MonthWaterUsePlan::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlan::getMidDay, midDay).
                    set(MonthWaterUsePlan::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlan::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlan::getYear,monthWaterUsePlanCrop.getYear()).
                    eq(MonthWaterUsePlan::getMonth,monthWaterUsePlanCrop.getMonth()).
                    eq(MonthWaterUsePlan::getArea, monthWaterUsePlanCrop.getArea()).
                    eq(MonthWaterUsePlan::getUnitId, monthWaterUsePlanCrop.getUnitId()).update();
            if(update1){
                return RestResponse.ok("删除成功");
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return RestResponse.no("删除失败");
            }
        }else {
            return RestResponse.no("删除失败");
        }
    }
}

