package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanCropForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanCropMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表服务实现类
 *
 * @author makejava
 * @since 2023-12-01 18:26:28
 */
@Service("yearWaterUsePlanCropService")
public class YearWaterUsePlanCropServiceImpl extends ServiceImpl<YearWaterUsePlanCropMapper, YearWaterUsePlanCrop> implements YearWaterUsePlanCropService {

    @Autowired
    private YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;

    @Override
    public RestResponse<List<YearWaterUsePlanCrop>> selectList(YearCropSelectListReq req) {
        List<YearWaterUsePlanCrop> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getUnit()),YearWaterUsePlanCrop::getUnit, req.getUnit()).
                eq(StringUtils.isNotEmpty(req.getCropType()),YearWaterUsePlanCrop::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getArea()),YearWaterUsePlanCrop::getArea, req.getArea()).
                eq(req.getYear()!=null,YearWaterUsePlanCrop::getYear, req.getYear()).
                eq(YearWaterUsePlanCrop::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse update(YearWaterUsePlanCrop yearWaterUsePlanCrop) {
        yearWaterUsePlanCrop.setUpdateTime(new Date());
        yearWaterUsePlanCrop.setAprilTotal(
                (yearWaterUsePlanCrop.getAprilEarlyOctober()==null?0:yearWaterUsePlanCrop.getAprilEarlyOctober())+
                (yearWaterUsePlanCrop.getAprilMidDay()==null?0:yearWaterUsePlanCrop.getAprilMidDay())+
                (yearWaterUsePlanCrop.getAprilLaterOctober()==null?0:yearWaterUsePlanCrop.getAprilLaterOctober())
        );
        yearWaterUsePlanCrop.setMayTotal(
                (yearWaterUsePlanCrop.getMayEarlyOctober()==null?0:yearWaterUsePlanCrop.getMayEarlyOctober())+
                        (yearWaterUsePlanCrop.getMayMidDay()==null?0:yearWaterUsePlanCrop.getMayMidDay())+
                        (yearWaterUsePlanCrop.getMayLaterOctober()==null?0:yearWaterUsePlanCrop.getMayLaterOctober())
        );
        yearWaterUsePlanCrop.setJuneTotal(
                (yearWaterUsePlanCrop.getJuneEarlyOctober()==null?0:yearWaterUsePlanCrop.getJuneEarlyOctober())+
                        (yearWaterUsePlanCrop.getJuneMidDay()==null?0:yearWaterUsePlanCrop.getJuneMidDay())+
                        (yearWaterUsePlanCrop.getJuneLaterOctober()==null?0:yearWaterUsePlanCrop.getJuneLaterOctober())
        );
        yearWaterUsePlanCrop.setJulyTotal(
                (yearWaterUsePlanCrop.getJulyEarlyOctober()==null?0:yearWaterUsePlanCrop.getJulyEarlyOctober())+
                        (yearWaterUsePlanCrop.getJulyMidDay()==null?0:yearWaterUsePlanCrop.getJulyMidDay())+
                        (yearWaterUsePlanCrop.getJulyLaterOctober()==null?0:yearWaterUsePlanCrop.getJulyLaterOctober())
        );
        yearWaterUsePlanCrop.setAugustTotal(
                (yearWaterUsePlanCrop.getAugustEarlyOctober()==null?0:yearWaterUsePlanCrop.getAugustEarlyOctober())+
                        (yearWaterUsePlanCrop.getAugustMidDay()==null?0:yearWaterUsePlanCrop.getAugustMidDay())+
                        (yearWaterUsePlanCrop.getAugustLaterOctober()==null?0:yearWaterUsePlanCrop.getAugustLaterOctober())
        );
        yearWaterUsePlanCrop.setSeptemberTotal(
                (yearWaterUsePlanCrop.getSeptemberEarlyOctober()==null?0:yearWaterUsePlanCrop.getSeptemberEarlyOctober())+
                        (yearWaterUsePlanCrop.getSeptemberMidDay()==null?0:yearWaterUsePlanCrop.getSeptemberMidDay())+
                        (yearWaterUsePlanCrop.getSeptemberLaterOctober()==null?0:yearWaterUsePlanCrop.getSeptemberLaterOctober())
        );
        yearWaterUsePlanCrop.setOctoberTotal(
                (yearWaterUsePlanCrop.getOctoberEarlyOctober()==null?0:yearWaterUsePlanCrop.getOctoberEarlyOctober())+
                        (yearWaterUsePlanCrop.getOctoberMidDay()==null?0:yearWaterUsePlanCrop.getOctoberMidDay())+
                        (yearWaterUsePlanCrop.getOctoberLaterOctober()==null?0:yearWaterUsePlanCrop.getOctoberLaterOctober())
        );
        yearWaterUsePlanCrop.setNovemberTotal(
                (yearWaterUsePlanCrop.getNovemberEarlyOctober()==null?0:yearWaterUsePlanCrop.getNovemberEarlyOctober())+
                        (yearWaterUsePlanCrop.getNovemberMidDay()==null?0:yearWaterUsePlanCrop.getNovemberMidDay())+
                        (yearWaterUsePlanCrop.getNovemberLaterOctober()==null?0:yearWaterUsePlanCrop.getNovemberLaterOctober())
        );
        boolean b = this.updateById(yearWaterUsePlanCrop);
        if(b){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;
            List<YearWaterUsePlanCrop> list = this.lambdaQuery().eq(YearWaterUsePlanCrop::getYear, yearWaterUsePlanCrop.getYear()).eq(YearWaterUsePlanCrop::getArea, yearWaterUsePlanCrop.getArea()).
                    eq(YearWaterUsePlanCrop::getUnitId, yearWaterUsePlanCrop.getUnitId()).eq(YearWaterUsePlanCrop::getDel, 0).ne(YearWaterUsePlanCrop::getId,yearWaterUsePlanCrop.getId()).list();
            if(null != list && list.size()>0){
                for(YearWaterUsePlanCrop crop:list){
                    AprilTotal +=crop.getAprilTotal()==null?0.0:crop.getAprilTotal();
                    MayTotal +=crop.getMayTotal()==null?0.0:crop.getMayTotal();
                    JuneTotal +=crop.getJuneTotal()==null?0.0:crop.getJuneTotal();
                    JulyTotal +=crop.getJulyTotal()==null?0.0:crop.getJulyTotal();
                    AugustTotal +=crop.getAugustTotal()==null?0.0:crop.getAugustTotal();
                    SeptemberTotal +=crop.getSeptemberTotal()==null?0.0:crop.getSeptemberTotal();
                    OctoberTotal +=crop.getOctoberTotal()==null?0.0:crop.getOctoberTotal();
                    NovemberTotal +=crop.getNovemberTotal()==null?0.0:crop.getNovemberTotal();
                }
            }
            AprilTotal +=yearWaterUsePlanCrop.getAprilTotal();
            MayTotal +=yearWaterUsePlanCrop.getMayTotal();
            JuneTotal +=yearWaterUsePlanCrop.getJuneTotal();
            JulyTotal +=yearWaterUsePlanCrop.getJulyTotal();
            AugustTotal +=yearWaterUsePlanCrop.getAugustTotal();
            SeptemberTotal +=yearWaterUsePlanCrop.getSeptemberTotal();
            OctoberTotal +=yearWaterUsePlanCrop.getOctoberTotal();
            NovemberTotal +=yearWaterUsePlanCrop.getNovemberTotal();
            boolean update = yearWaterUsePlanTrunkCanalService.lambdaUpdate().
                    set(YearWaterUsePlanTrunkCanal::getApril, AprilTotal).
                    set(YearWaterUsePlanTrunkCanal::getMay, MayTotal).
                    set(YearWaterUsePlanTrunkCanal::getJune, JuneTotal).
                    set(YearWaterUsePlanTrunkCanal::getJuly, JulyTotal).
                    set(YearWaterUsePlanTrunkCanal::getAugust, AugustTotal).
                    set(YearWaterUsePlanTrunkCanal::getSeptember, SeptemberTotal).
                    set(YearWaterUsePlanTrunkCanal::getOctober, OctoberTotal).
                    set(YearWaterUsePlanTrunkCanal::getNovember, NovemberTotal).
                    set(YearWaterUsePlanTrunkCanal::getAmountCount,AprilTotal+MayTotal+JuneTotal+JulyTotal+AugustTotal+SeptemberTotal+OctoberTotal+NovemberTotal).
                    eq(YearWaterUsePlanTrunkCanal::getYear,yearWaterUsePlanCrop.getYear()).
                    eq(YearWaterUsePlanTrunkCanal::getArea, yearWaterUsePlanCrop.getArea()).eq(YearWaterUsePlanTrunkCanal::getUnitId, yearWaterUsePlanCrop.getUnitId()).update();
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
    public RestResponse add(YearWaterUsePlanCrop yearWaterUsePlanCrop) {
        List<YearWaterUsePlanCrop> list = this.lambdaQuery().eq(YearWaterUsePlanCrop::getArea, yearWaterUsePlanCrop.getArea()).
                eq(YearWaterUsePlanCrop::getUnit, yearWaterUsePlanCrop.getUnit()).
                eq(YearWaterUsePlanCrop::getDel,0).eq(YearWaterUsePlanCrop::getYear,yearWaterUsePlanCrop.getYear()).
                eq(YearWaterUsePlanCrop::getIrrigatedCrop, yearWaterUsePlanCrop.getIrrigatedCrop()).list();
        if(null!= list && list.size()>0){
            return RestResponse.no("该作物已存在，请勿重复添加");
        }
        yearWaterUsePlanCrop.setId(UUIDUtils.getUUID());
        yearWaterUsePlanCrop.setDel(0);
        yearWaterUsePlanCrop.setCreateTime(new Date());
        boolean save = this.save(yearWaterUsePlanCrop);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        YearWaterUsePlanCrop yearWaterUsePlanCrop = this.getById(id);
        boolean update = this.lambdaUpdate().set(YearWaterUsePlanCrop::getDel, 1).eq(YearWaterUsePlanCrop::getId, id).update();
        if(update){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;
            List<YearWaterUsePlanCrop> list = this.lambdaQuery().eq(YearWaterUsePlanCrop::getYear, yearWaterUsePlanCrop.getYear()).eq(YearWaterUsePlanCrop::getArea, yearWaterUsePlanCrop.getArea()).
                    eq(YearWaterUsePlanCrop::getUnitId, yearWaterUsePlanCrop.getUnitId()).eq(YearWaterUsePlanCrop::getDel, 0).ne(YearWaterUsePlanCrop::getId,yearWaterUsePlanCrop.getId()).list();
            if(null != list && list.size()>0){
                for(YearWaterUsePlanCrop crop:list){
                    AprilTotal +=crop.getAprilTotal()==null?0.0:crop.getAprilTotal();
                    MayTotal +=crop.getMayTotal()==null?0.0:crop.getMayTotal();
                    JuneTotal +=crop.getJuneTotal()==null?0.0:crop.getJuneTotal();
                    JulyTotal +=crop.getJulyTotal()==null?0.0:crop.getJulyTotal();
                    AugustTotal +=crop.getAugustTotal()==null?0.0:crop.getAugustTotal();
                    SeptemberTotal +=crop.getSeptemberTotal()==null?0.0:crop.getSeptemberTotal();
                    OctoberTotal +=crop.getOctoberTotal()==null?0.0:crop.getOctoberTotal();
                    NovemberTotal +=crop.getNovemberTotal()==null?0.0:crop.getNovemberTotal();
                }
            }
            boolean update1 = yearWaterUsePlanTrunkCanalService.lambdaUpdate().
                    set(YearWaterUsePlanTrunkCanal::getApril, AprilTotal).
                    set(YearWaterUsePlanTrunkCanal::getMay, MayTotal).
                    set(YearWaterUsePlanTrunkCanal::getJune, JuneTotal).
                    set(YearWaterUsePlanTrunkCanal::getJuly, JulyTotal).
                    set(YearWaterUsePlanTrunkCanal::getAugust, AugustTotal).
                    set(YearWaterUsePlanTrunkCanal::getSeptember, SeptemberTotal).
                    set(YearWaterUsePlanTrunkCanal::getOctober, OctoberTotal).
                    set(YearWaterUsePlanTrunkCanal::getNovember, NovemberTotal).
                    set(YearWaterUsePlanTrunkCanal::getAmountCount,AprilTotal+MayTotal+JuneTotal+JulyTotal+AugustTotal+SeptemberTotal+OctoberTotal+NovemberTotal).
                    eq(YearWaterUsePlanTrunkCanal::getYear,yearWaterUsePlanCrop.getYear()).
                    eq(YearWaterUsePlanTrunkCanal::getArea, yearWaterUsePlanCrop.getArea()).eq(YearWaterUsePlanTrunkCanal::getUnitId, yearWaterUsePlanCrop.getUnitId()).update();
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

    @Override
    public SelectYearWaterUsePlanCropForSum selectListForSum(Integer year, String area) {
        return this.baseMapper.selectListForSum(year, area);
    }
}

