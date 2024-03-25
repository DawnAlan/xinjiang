package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropImportTableReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanCropMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropOwnerService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

    @Autowired
    private MonthWaterUsePlanCropOwnerService monthWaterUsePlanCropOwnerService;

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


    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(MonthCropImportParamReq req, MultipartFile file) {
        List<MonthWaterUsePlanCrop> monthWaterUsePlanCropList = new ArrayList<>();
        List<MonthCropImportTableReq> monthCropImportTableReqs = ExcelUtils.importExcelForCrop(file, MonthCropImportTableReq.class);
        for(MonthCropImportTableReq table :monthCropImportTableReqs){
            MonthWaterUsePlanCrop crop = new MonthWaterUsePlanCrop();
            BeanUtils.copyProperties(table,crop);
            crop.setId(UUIDUtils.getUUID());
            crop.setDel(0);
            crop.setMonth(req.getMonth());
            crop.setArea(req.getArea());
            crop.setUnit(req.getUnit());
            crop.setUnitId(req.getUnitId());
            crop.setYear(req.getYear());
            crop.setCreateTime(new Date());
            monthWaterUsePlanCropList.add(crop);
        }
        boolean b = this.saveBatch(monthWaterUsePlanCropList);
        if(b){
            List<MonthWaterUsePlanCropOwner> monthWaterUsePlanCropOwnerList = new ArrayList<>();
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;

            for(MonthWaterUsePlanCrop crop:monthWaterUsePlanCropList){
                earlyOctober +=crop.getEarlyOctoberWaterDemand()==null?0.0:crop.getEarlyOctoberWaterDemand();
                midDay +=crop.getMidDayWaterDemand()==null?0.0:crop.getMidDayWaterDemand();
                laterOctober +=crop.getLaterOctoberWaterDemand()==null?0.0:crop.getLaterOctoberWaterDemand();
            }
            boolean update = monthWaterUsePlanService.lambdaUpdate().
                    set(MonthWaterUsePlan::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlan::getMidDay, midDay).
                    set(MonthWaterUsePlan::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlan::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlan::getYear,req.getYear()).
                    eq(MonthWaterUsePlan::getMonth,req.getMonth()).
                    eq(MonthWaterUsePlan::getArea, req.getArea()).
                    eq(MonthWaterUsePlan::getUnitId, req.getUnitId()).update();
            if(update){
                for(MonthWaterUsePlanCrop crop:monthWaterUsePlanCropList){
                    MonthWaterUsePlanCropOwner owner = new MonthWaterUsePlanCropOwner();
                    BeanUtils.copyProperties(crop,owner);
                    owner.setEarlyOctoberWaterDemandOwner(formatDouble(crop.getWaterDemand()*crop.getIrrigationQuota()*crop.getEarlyOctoberCount()/10000));
                    owner.setMidDayWaterDemandOwner(formatDouble(crop.getWaterDemand()*crop.getIrrigationQuota()*crop.getMidDayCount()/10000));
                    owner.setLaterOctoberWaterDemandOwner(formatDouble(crop.getWaterDemand()*crop.getIrrigationQuota()*crop.getLaterOctoberCount()/10000));
                    owner.setTotalCountWaterDemandOwner(
                            owner.getEarlyOctoberWaterDemandOwner()+owner.getMidDayWaterDemandOwner()+owner.getLaterOctoberWaterDemandOwner()
                    );
                    owner.setWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigatedQuota()/10000));
                    monthWaterUsePlanCropOwnerList.add(owner);
                }
                return monthWaterUsePlanCropOwnerService.add(monthWaterUsePlanCropOwnerList);
            }else {
                return RestResponse.no("上传失败");
            }
        }else {
            return RestResponse.no("上传失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(MonthCropImportParamReq req) {
        boolean remove = this.lambdaUpdate().eq(MonthWaterUsePlanCrop::getYear, req.getYear()).eq(MonthWaterUsePlanCrop::getMonth, req.getMonth()).
                eq(MonthWaterUsePlanCrop::getUnitId, req.getUnitId()).eq(MonthWaterUsePlanCrop::getArea, req.getArea()).remove();
        if(remove){
            Double earlyOctober = 0.0;
            Double midDay = 0.0;
            Double laterOctober = 0.0;
            List<MonthWaterUsePlanCrop> list = this.lambdaQuery().eq(MonthWaterUsePlanCrop::getYear, req.getYear()).
                    eq(MonthWaterUsePlanCrop::getArea, req.getArea()).
                    eq(MonthWaterUsePlanCrop::getUnitId, req.getUnitId()).
                    eq(MonthWaterUsePlanCrop::getDel, 0).
                    eq(MonthWaterUsePlanCrop::getMonth, req.getMonth()).list();
            if(null != list && list.size()>0){
                for(MonthWaterUsePlanCrop crop:list){
                    earlyOctober +=crop.getEarlyOctoberWaterDemand()==null?0.0:crop.getEarlyOctoberWaterDemand();
                    midDay +=crop.getMidDayWaterDemand()==null?0.0:crop.getMidDayWaterDemand();
                    laterOctober +=crop.getLaterOctoberWaterDemand()==null?0.0:crop.getLaterOctoberWaterDemand();
                }
            }
            boolean update1 = monthWaterUsePlanService.lambdaUpdate().
                    set(MonthWaterUsePlan::getEarlyOctober, earlyOctober).
                    set(MonthWaterUsePlan::getMidDay, midDay).
                    set(MonthWaterUsePlan::getLaterOctober, laterOctober).
                    set(MonthWaterUsePlan::getTotal,earlyOctober+midDay+laterOctober).
                    eq(MonthWaterUsePlan::getYear,req.getYear()).
                    eq(MonthWaterUsePlan::getMonth,req.getMonth()).
                    eq(MonthWaterUsePlan::getArea, req.getArea()).
                    eq(MonthWaterUsePlan::getUnitId, req.getUnitId()).update();
            if(update1){
                return monthWaterUsePlanCropOwnerService.delete(req);
            }else {
                return RestResponse.no("删除失败(非供水科)干渠");
            }
        }else {
            return RestResponse.no("删除失败(非供水科)作物");
        }
    }

    private Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        String format = df.format(value);
        double v = Double.parseDouble(format);
        return v;
    }
}

