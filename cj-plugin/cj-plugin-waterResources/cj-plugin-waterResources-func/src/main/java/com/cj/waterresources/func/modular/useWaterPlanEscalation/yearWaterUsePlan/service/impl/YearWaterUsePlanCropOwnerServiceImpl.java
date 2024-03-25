package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanalOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanCropOwnerMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropOwnerService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * (YearWaterUsePlanCropOwner)表服务实现类
 *
 * @author makejava
 * @since 2024-03-22 19:35:40
 */
@Service("yearWaterUsePlanCropOwnerService")
public class YearWaterUsePlanCropOwnerServiceImpl extends ServiceImpl<YearWaterUsePlanCropOwnerMapper, YearWaterUsePlanCropOwner> implements YearWaterUsePlanCropOwnerService {

    @Autowired
    private YearWaterUsePlanTrunkCanalOwnerService yearWaterUsePlanTrunkCanalOwnerService;

    @Override
    public RestResponse<List<YearWaterUsePlanCropOwner>> selectList(YearCropSelectListReq req) {
        List<YearWaterUsePlanCropOwner> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getUnit()),YearWaterUsePlanCropOwner::getUnit, req.getUnit()).
                eq(StringUtils.isNotEmpty(req.getCropType()),YearWaterUsePlanCropOwner::getCropType, req.getCropType()).
                eq(StringUtils.isNotEmpty(req.getArea()),YearWaterUsePlanCropOwner::getArea, req.getArea()).
                eq(req.getYear()!=null,YearWaterUsePlanCropOwner::getYear, req.getYear()).
                eq(YearWaterUsePlanCropOwner::getDel,0).
                list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse addList(List<YearWaterUsePlanCropOwner> yearWaterUsePlanCropOwnerList) {
        YearWaterUsePlanCropOwner req = yearWaterUsePlanCropOwnerList.get(0);
        boolean b = this.saveBatch(yearWaterUsePlanCropOwnerList);
        if(b){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;

            for(YearWaterUsePlanCropOwner crop:yearWaterUsePlanCropOwnerList){
                AprilTotal +=crop.getAprilTotalWaterDemandOwner()==null?0.0:crop.getAprilTotalWaterDemandOwner();
                MayTotal +=crop.getMayTotalWaterDemandOwner()==null?0.0:crop.getMayTotalWaterDemandOwner();
                JuneTotal +=crop.getJuneTotalWaterDemandOwner()==null?0.0:crop.getJuneTotalWaterDemandOwner();
                JulyTotal +=crop.getJulyTotalWaterDemandOwner()==null?0.0:crop.getJulyTotalWaterDemandOwner();
                AugustTotal +=crop.getAugustTotalWaterDemandOwner()==null?0.0:crop.getAugustTotalWaterDemandOwner();
                SeptemberTotal +=crop.getSeptemberTotalWaterDemandOwner()==null?0.0:crop.getSeptemberTotalWaterDemandOwner();
                OctoberTotal +=crop.getOctoberTotalWaterDemandOwner()==null?0.0:crop.getOctoberTotalWaterDemandOwner();
                NovemberTotal +=crop.getNovemberTotalWaterDemandOwner()==null?0.0:crop.getNovemberTotalWaterDemandOwner();
            }
            boolean update = yearWaterUsePlanTrunkCanalOwnerService.lambdaUpdate().
                    set(YearWaterUsePlanTrunkCanalOwner::getApril, AprilTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getMay, MayTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getJune, JuneTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getJuly, JulyTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getAugust, AugustTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getSeptember, SeptemberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getOctober, OctoberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getNovember, NovemberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getAmountCount,AprilTotal+MayTotal+JuneTotal+JulyTotal+AugustTotal+SeptemberTotal+OctoberTotal+NovemberTotal).
                    eq(YearWaterUsePlanTrunkCanalOwner::getYear,req.getYear()).
                    eq(YearWaterUsePlanTrunkCanalOwner::getArea, req.getArea()).eq(YearWaterUsePlanTrunkCanalOwner::getUnitId, req.getUnitId()).update();
            if(update){
                return RestResponse.ok();
            }else {
                return RestResponse.no("添加供水科干渠数据失败");
            }
        }else {
            return RestResponse.no("添加供水科作物数据失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(YearCropImportParamReq req) {
        boolean update = this.lambdaUpdate().
                eq(YearWaterUsePlanCropOwner::getYear,req.getYear()).
                eq(YearWaterUsePlanCropOwner::getArea,req.getArea()).
                eq(YearWaterUsePlanCropOwner::getUnitId,req.getUnitId()).remove();
        if(update){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;
            List<YearWaterUsePlanCropOwner> list = this.lambdaQuery().eq(YearWaterUsePlanCropOwner::getYear, req.getYear()).eq(YearWaterUsePlanCropOwner::getArea, req.getArea()).
                    eq(YearWaterUsePlanCropOwner::getUnitId, req.getUnitId()).eq(YearWaterUsePlanCropOwner::getDel, 0).list();
            if(null != list && list.size()>0){
                for(YearWaterUsePlanCropOwner crop:list){
                    AprilTotal +=crop.getAprilTotalWaterDemandOwner()==null?0.0:crop.getAprilTotalWaterDemandOwner();
                    MayTotal +=crop.getMayTotalWaterDemandOwner()==null?0.0:crop.getMayTotalWaterDemandOwner();
                    JuneTotal +=crop.getJuneTotalWaterDemandOwner()==null?0.0:crop.getJuneTotalWaterDemandOwner();
                    JulyTotal +=crop.getJulyTotalWaterDemandOwner()==null?0.0:crop.getJulyTotalWaterDemandOwner();
                    AugustTotal +=crop.getAugustTotalWaterDemandOwner()==null?0.0:crop.getAugustTotalWaterDemandOwner();
                    SeptemberTotal +=crop.getSeptemberTotalWaterDemandOwner()==null?0.0:crop.getSeptemberTotalWaterDemandOwner();
                    OctoberTotal +=crop.getOctoberTotalWaterDemandOwner()==null?0.0:crop.getOctoberTotalWaterDemandOwner();
                    NovemberTotal +=crop.getNovemberTotalWaterDemandOwner()==null?0.0:crop.getNovemberTotalWaterDemandOwner();
                }
            }
            boolean update1 = yearWaterUsePlanTrunkCanalOwnerService.lambdaUpdate().
                    set(YearWaterUsePlanTrunkCanalOwner::getApril, AprilTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getMay, MayTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getJune, JuneTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getJuly, JulyTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getAugust, AugustTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getSeptember, SeptemberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getOctober, OctoberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getNovember, NovemberTotal).
                    set(YearWaterUsePlanTrunkCanalOwner::getAmountCount,AprilTotal+MayTotal+JuneTotal+JulyTotal+AugustTotal+SeptemberTotal+OctoberTotal+NovemberTotal).
                    eq(YearWaterUsePlanTrunkCanalOwner::getYear,req.getYear()).
                    eq(YearWaterUsePlanTrunkCanalOwner::getArea, req.getArea()).eq(YearWaterUsePlanTrunkCanalOwner::getUnitId, req.getUnitId()).update();
            if(update1){
                return RestResponse.ok("删除成功");
            }else {
                return RestResponse.no("删除失败");
            }
        }else {
            return RestResponse.no("删除失败");
        }
    }

}

