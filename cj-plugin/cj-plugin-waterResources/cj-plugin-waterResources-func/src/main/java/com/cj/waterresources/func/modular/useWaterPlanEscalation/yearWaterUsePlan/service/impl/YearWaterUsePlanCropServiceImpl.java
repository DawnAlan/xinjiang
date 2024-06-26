package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportTableReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.PlanComparedToActualByYearRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanCropForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.NeedWaterVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.PlanComparedToActualByYearVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanCropMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropOwnerService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private YearWaterUsePlanCropOwnerService yearWaterUsePlanCropOwnerService;

    @Autowired
    private AllService allService;


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

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(YearCropImportParamReq req, MultipartFile file) {
        this.lambdaUpdate().
                eq(YearWaterUsePlanCrop::getYear,req.getYear()).
                eq(YearWaterUsePlanCrop::getArea,req.getArea()).
                eq(YearWaterUsePlanCrop::getUnitId,req.getUnitId()).remove();
        List<YearWaterUsePlanCrop> yearWaterUsePlanCropList = new ArrayList<>();
        List<YearWaterUsePlanCropOwner> yearWaterUsePlanCropOwnerList = new ArrayList<>();
        List<YearCropImportTableReq> yearCropImportTableReqs = ExcelUtils.importExcelForCrop(file, YearCropImportTableReq.class);
        for(YearCropImportTableReq table:yearCropImportTableReqs){
            if(StringUtils.isEmpty(table.getIrrigatedCrop())){
                continue;
            }
            YearWaterUsePlanCrop crop = new YearWaterUsePlanCrop();
            BeanUtils.copyProperties(table,crop);
            crop.setId(UUIDUtils.getUUID());
            crop.setArea(req.getArea());
            crop.setYear(req.getYear());
            crop.setUnit(req.getUnit());
            crop.setUnitId(req.getUnitId());
            crop.setBindId(req.getBindId());
            crop.setCreateTime(new Date());
            crop.setDel(0);
            yearWaterUsePlanCropList.add(crop);
        }
        boolean b = this.saveBatch(yearWaterUsePlanCropList);
        if(b){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;

            for(YearWaterUsePlanCrop crop:yearWaterUsePlanCropList){
                AprilTotal +=crop.getAprilTotalWaterDemand()==null?0.0:crop.getAprilTotalWaterDemand();
                MayTotal +=crop.getMayTotalWaterDemand()==null?0.0:crop.getMayTotalWaterDemand();
                JuneTotal +=crop.getJuneTotalWaterDemand()==null?0.0:crop.getJuneTotalWaterDemand();
                JulyTotal +=crop.getJulyTotalWaterDemand()==null?0.0:crop.getJulyTotalWaterDemand();
                AugustTotal +=crop.getAugustTotalWaterDemand()==null?0.0:crop.getAugustTotalWaterDemand();
                SeptemberTotal +=crop.getSeptemberTotalWaterDemand()==null?0.0:crop.getSeptemberTotalWaterDemand();
                OctoberTotal +=crop.getOctoberTotalWaterDemand()==null?0.0:crop.getOctoberTotalWaterDemand();
                NovemberTotal +=crop.getNovemberTotalWaterDemand()==null?0.0:crop.getNovemberTotalWaterDemand();
            }
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
                    eq(YearWaterUsePlanTrunkCanal::getYear,req.getYear()).
                    eq(YearWaterUsePlanTrunkCanal::getArea, req.getArea()).eq(YearWaterUsePlanTrunkCanal::getUnitId, req.getUnitId()).update();
            if(update){
                for(YearWaterUsePlanCrop crop:yearWaterUsePlanCropList){
                    YearWaterUsePlanCropOwner owner = new YearWaterUsePlanCropOwner();
                    BeanUtils.copyProperties(crop,owner);
                    //上旬
                    owner.setAprilEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAprilEarlyOctoberCount()/10000));
                    owner.setMayEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getMayEarlyOctoberCount()/10000));
                    owner.setJuneEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJuneEarlyOctoberCount()/10000));
                    owner.setJulyEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJulyEarlyOctoberCount()/10000));
                    owner.setAugustEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAugustEarlyOctoberCount()/10000));
                    owner.setSeptemberEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getSeptemberEarlyOctoberCount()/10000));
                    owner.setOctoberEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getOctoberEarlyOctoberCount()/10000));
                    owner.setNovemberEarlyOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getNovemberEarlyOctoberCount()/10000));

                    //中旬
                    owner.setAprilMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAprilMidDayCount()/10000));
                    owner.setMayMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getMayMidDayCount()/10000));
                    owner.setJuneMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJuneMidDayCount()/10000));
                    owner.setJulyMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJulyMidDayCount()/10000));
                    owner.setAugustMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAugustMidDayCount()/10000));
                    owner.setSeptemberMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getSeptemberMidDayCount()/10000));
                    owner.setOctoberMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getOctoberMidDayCount()/10000));
                    owner.setNovemberMidDayWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getNovemberMidDayCount()/10000));

                    //下旬
                    owner.setAprilLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAprilLaterOctoberCount()/10000));
                    owner.setMayLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getMayLaterOctoberCount()/10000));
                    owner.setJuneLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJuneLaterOctoberCount()/10000));
                    owner.setJulyLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getJulyLaterOctoberCount()/10000));
                    owner.setAugustLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getAugustLaterOctoberCount()/10000));
                    owner.setSeptemberLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getSeptemberLaterOctoberCount()/10000));
                    owner.setOctoberLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getOctoberLaterOctoberCount()/10000));
                    owner.setNovemberLaterOctoberWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigationQuota()*crop.getNovemberLaterOctoberCount()/10000));
                    
                    //合计
                    owner.setAprilTotalWaterDemandOwner(
                            owner.getAprilEarlyOctoberWaterDemandOwner()+owner.getAprilMidDayWaterDemandOwner()+owner.getAprilLaterOctoberWaterDemandOwner()
                    );
                    owner.setMayTotalWaterDemandOwner(
                            owner.getMayEarlyOctoberWaterDemandOwner()+owner.getMayMidDayWaterDemandOwner()+owner.getMayLaterOctoberWaterDemandOwner()
                    );
                    owner.setJuneTotalWaterDemandOwner(
                            owner.getJuneEarlyOctoberWaterDemandOwner()+owner.getJuneMidDayWaterDemandOwner()+owner.getJuneLaterOctoberWaterDemandOwner()
                    );
                    owner.setJulyTotalWaterDemandOwner(
                            owner.getJulyEarlyOctoberWaterDemandOwner()+owner.getJulyMidDayWaterDemandOwner()+owner.getJulyLaterOctoberWaterDemandOwner()
                    );
                    owner.setAugustTotalWaterDemandOwner(
                            owner.getAugustEarlyOctoberWaterDemandOwner()+owner.getAugustMidDayWaterDemandOwner()+owner.getAugustLaterOctoberWaterDemandOwner()
                    );
                    owner.setSeptemberTotalWaterDemandOwner(
                            owner.getSeptemberEarlyOctoberWaterDemandOwner()+owner.getSeptemberMidDayWaterDemandOwner()+owner.getSeptemberLaterOctoberWaterDemandOwner()
                    );
                    owner.setOctoberTotalWaterDemandOwner(
                            owner.getOctoberEarlyOctoberWaterDemandOwner()+owner.getOctoberMidDayWaterDemandOwner()+owner.getOctoberLaterOctoberWaterDemandOwner()
                    );
                    owner.setNovemberTotalWaterDemandOwner(
                            owner.getNovemberEarlyOctoberWaterDemandOwner()+owner.getNovemberMidDayWaterDemandOwner()+owner.getNovemberLaterOctoberWaterDemandOwner()
                    );

                    //总需水
                    owner.setWaterDemandOwner(formatDouble(crop.getIrrigatedArea()*crop.getIrrigatedQuota()/10000));
                    yearWaterUsePlanCropOwnerList.add(owner);
                }
                yearWaterUsePlanCropOwnerService.lambdaUpdate().
                        eq(YearWaterUsePlanCropOwner::getYear,req.getYear()).
                        eq(YearWaterUsePlanCropOwner::getArea,req.getArea()).
                        eq(YearWaterUsePlanCropOwner::getUnitId,req.getUnitId()).remove();
                RestResponse restResponse = yearWaterUsePlanCropOwnerService.addList(yearWaterUsePlanCropOwnerList);
                if(restResponse.getCode()==200){
                    return RestResponse.ok("插入成功");
                }else {
                    return RestResponse.no("插入失败");
                }
            }else {
                return RestResponse.no("插入失败");
            }
        }else {
            return RestResponse.no("插入失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(YearCropImportParamReq req) {
        boolean update = this.lambdaUpdate().
                eq(YearWaterUsePlanCrop::getYear,req.getYear()).
                eq(YearWaterUsePlanCrop::getArea,req.getArea()).
                eq(YearWaterUsePlanCrop::getUnitId,req.getUnitId()).remove();
        if(update){
            Double AprilTotal = 0.0;
            Double MayTotal = 0.0;
            Double JuneTotal = 0.0;
            Double JulyTotal = 0.0;
            Double AugustTotal = 0.0;
            Double SeptemberTotal = 0.0;
            Double OctoberTotal = 0.0;
            Double NovemberTotal = 0.0;
            List<YearWaterUsePlanCrop> list = this.lambdaQuery().eq(YearWaterUsePlanCrop::getYear, req.getYear()).eq(YearWaterUsePlanCrop::getArea, req.getArea()).
                    eq(YearWaterUsePlanCrop::getUnitId, req.getUnitId()).eq(YearWaterUsePlanCrop::getDel, 0).list();
            if(null != list && list.size()>0){
                for(YearWaterUsePlanCrop crop:list){
                    AprilTotal +=crop.getAprilTotalWaterDemand()==null?0.0:crop.getAprilTotalWaterDemand();
                    MayTotal +=crop.getMayTotalWaterDemand()==null?0.0:crop.getMayTotalWaterDemand();
                    JuneTotal +=crop.getJuneTotalWaterDemand()==null?0.0:crop.getJuneTotalWaterDemand();
                    JulyTotal +=crop.getJulyTotalWaterDemand()==null?0.0:crop.getJulyTotalWaterDemand();
                    AugustTotal +=crop.getAugustTotalWaterDemand()==null?0.0:crop.getAugustTotalWaterDemand();
                    SeptemberTotal +=crop.getSeptemberTotalWaterDemand()==null?0.0:crop.getSeptemberTotalWaterDemand();
                    OctoberTotal +=crop.getOctoberTotalWaterDemand()==null?0.0:crop.getOctoberTotalWaterDemand();
                    NovemberTotal +=crop.getNovemberTotalWaterDemand()==null?0.0:crop.getNovemberTotalWaterDemand();
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
                    eq(YearWaterUsePlanTrunkCanal::getYear,req.getYear()).
                    eq(YearWaterUsePlanTrunkCanal::getArea, req.getArea()).eq(YearWaterUsePlanTrunkCanal::getUnitId, req.getUnitId()).update();
            if(update1){
                return yearWaterUsePlanCropOwnerService.delete(req);
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

    @Override
    public RestResponse<List<PlanComparedToActualByYearRes>> planComparedToActual(Integer planYear, Integer actualYear,Integer month) {
        List<PlanComparedToActualByYearRes> resultList = new ArrayList<>();
        List<PlanComparedToActualByYearVo> planYearList = this.baseMapper.planComparedToActual(planYear, getMonthName(month));
        Map<String, List<PlanComparedToActualByYearVo>> collect = planYearList.stream().collect(Collectors.groupingBy(PlanComparedToActualByYearVo::getArea));
        Set<String> unitNameList = collect.keySet();
        for(String unitName : unitNameList){
            PlanComparedToActualByYearRes res = new PlanComparedToActualByYearRes();
            res.setUnitName(unitName);
            if(unitName.contains("八钢工业")){
                Double planValue = collect.get(unitName).stream().filter(t -> t.getUnit().contains("八钢流量") && t.getV() != null).map(PlanComparedToActualByYearVo::getV).reduce(Double::sum).orElse(0.00);
                res.setPlanValue(NumberUtil.holdDecimal(planValue,2));
            }else {
                Double planValue = collect.get(unitName).stream().filter(t -> t.getV() != null).map(PlanComparedToActualByYearVo::getV).reduce(Double::sum).orElse(0.00);
                res.setPlanValue(NumberUtil.holdDecimal(planValue,2));
            }
            Map<String, Double> stringDoubleMap = allService.planComparedToActualForYear(planYear, month, collect.get(unitName).stream().map(PlanComparedToActualByYearVo::getBindId).collect(Collectors.toList()), unitName);
            res.setActualValue(NumberUtil.holdDecimal(stringDoubleMap.get(unitName)*8.64,2));
            res.setPlanSubtractActualValue(NumberUtil.holdDecimal(res.getActualValue()-res.getPlanValue(),2));
            Map<String, Double> stringDoubleMap1 = allService.planComparedToActualForYear(actualYear, month, collect.get(unitName).stream().map(PlanComparedToActualByYearVo::getBindId).collect(Collectors.toList()), unitName);
            res.setActualValueForContrastYear(NumberUtil.holdDecimal(stringDoubleMap1.get(unitName)*8.64,2));
            res.setPlanSubtractActualValueForContrastYear(NumberUtil.holdDecimal(res.getActualValue()-res.getActualValueForContrastYear(),2));
            res.setContrast(NumberUtil.holdDecimal((res.getActualValueForContrastYear()==null ||res.getActualValueForContrastYear()==0.00)?0.00: (res.getPlanSubtractActualValueForContrastYear()/res.getActualValueForContrastYear())*100,2));
            resultList.add(res);
        }
        return RestResponse.ok(resultList);
    }

    @Override
    public RestResponse<NeedWaterVo> needWaterByPlan(Integer planYear) {
        return RestResponse.ok(this.baseMapper.needWaterByPlan(planYear));
    }

    private Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        String format = df.format(value);
        double v = Double.parseDouble(format);
        return v;
    }

    private String getMonthName(Integer value) {
        switch (value){
            case 0:
                return "AMOUNT_COUNT";
            case 1:
                return "JANUARY";
            case 2:
                return "FEBRUARY";
            case 3:
                return "MARCH";
            case 4:
                return "APRIL";
            case 5:
                return "MAY";
            case 6:
                return "JUNE";
            case 7:
                return "JULY";
            case 8:
                return "AUGUST";
            case 9:
                return "SEPTEMBER";
            case 10:
                return "OCTOBER";
            case 11:
                return "NOVEMBER";
            case 12:
                return "DECEMBER";
            default:
                return "AMOUNT_COUNT";
        }
    }
}

