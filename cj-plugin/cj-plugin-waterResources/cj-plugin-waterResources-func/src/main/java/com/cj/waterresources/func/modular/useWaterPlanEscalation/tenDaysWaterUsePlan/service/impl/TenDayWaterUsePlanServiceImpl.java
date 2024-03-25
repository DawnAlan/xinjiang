package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanImportTableReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.mapper.TenDayWaterUsePlanMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanOwnerService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 旬用水计划(TenDayWaterUsePlan)表服务实现类
 *
 * @author makejava
 * @since 2023-12-01 19:41:08
 */
@Service("tenDayWaterUsePlanService")
public class TenDayWaterUsePlanServiceImpl extends ServiceImpl<TenDayWaterUsePlanMapper, TenDayWaterUsePlan> implements TenDayWaterUsePlanService {

    @Autowired
    private TenDayWaterUsePlanOwnerService tenDayWaterUsePlanOwnerService;

    @Override
    public RestResponse<List<TenDayWaterUsePlan>> selectList(TenDayWaterUsePlanSelectReq req) {
        List<TenDayWaterUsePlan> list = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getIrrigatedArea()),TenDayWaterUsePlan::getIrrigatedArea, req.getIrrigatedArea()).
                eq(StringUtils.isNotEmpty(req.getUseWaterUser()),TenDayWaterUsePlan::getUseWaterUser, req.getUseWaterUser()).
                eq(StringUtils.isNotEmpty(req.getCropType()),TenDayWaterUsePlan::getCropType, req.getCropType()).
                eq(req.getYear() !=null,TenDayWaterUsePlan::getYear, req.getYear()).
                eq(req.getMonth() !=null,TenDayWaterUsePlan::getMonth, req.getMonth()).
                eq(StringUtils.isNotEmpty(req.getTenDays()),TenDayWaterUsePlan::getTenDays, req.getTenDays()).
                list();
        if(null != list && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @SneakyThrows
    @Override
    public RestResponse add(TenDayWaterUsePlanImportParamReq req, MultipartFile file) {
        List<TenDayWaterUsePlan> tenDayWaterUsePlanList = new ArrayList<>();
        List<TenDayWaterUsePlanOwner> tenDayWaterUsePlanOwnerList = new ArrayList<>();
        List<TenDayWaterUsePlanImportTableReq> tenDayWaterUsePlanImportTableReqs = ExcelUtils.importExcelForCrop(file, TenDayWaterUsePlanImportTableReq.class);
        for(TenDayWaterUsePlanImportTableReq table:tenDayWaterUsePlanImportTableReqs){
            TenDayWaterUsePlan tenDayWaterUsePlan = new TenDayWaterUsePlan();
            BeanUtils.copyProperties(table,table);
            tenDayWaterUsePlan.setTenDays(req.getTenDays());
            tenDayWaterUsePlan.setArea(req.getArea());
            tenDayWaterUsePlan.setYear(req.getYear());
            tenDayWaterUsePlan.setMonth(req.getMonth());
            tenDayWaterUsePlan.setUseWaterUser(req.getUseWaterUser());
            tenDayWaterUsePlan.setId(UUIDUtils.getUUID());
            tenDayWaterUsePlan.setCreateTime(new Date());
            tenDayWaterUsePlanList.add(tenDayWaterUsePlan);
        }

        boolean save = this.saveBatch(tenDayWaterUsePlanList);
        if(save){
            for(TenDayWaterUsePlan tenDayWaterUsePlan:tenDayWaterUsePlanList){
                TenDayWaterUsePlanOwner tenDayWaterUsePlanOwner = new TenDayWaterUsePlanOwner();
                BeanUtils.copyProperties(tenDayWaterUsePlan,tenDayWaterUsePlanOwner);
                tenDayWaterUsePlanOwner.setWaterDemandOwner(formatDouble(tenDayWaterUsePlan.getIrrigatedArea()*tenDayWaterUsePlan.getIrrigationQuota()*tenDayWaterUsePlan.getIrrigationCount()/10000));
                tenDayWaterUsePlanOwnerList.add(tenDayWaterUsePlanOwner);
            }
            boolean b = tenDayWaterUsePlanOwnerService.saveBatch(tenDayWaterUsePlanOwnerList);
            if(b){
                return RestResponse.ok();
            }else {
                return RestResponse.no("新增失败");
            }
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(TenDayWaterUsePlanImportParamReq req) {
        boolean update = this.lambdaUpdate().eq(TenDayWaterUsePlan::getArea, req.getArea())
                .eq(TenDayWaterUsePlan::getUseWaterUser, req.getUseWaterUser())
                .eq(TenDayWaterUsePlan::getYear, req.getYear())
                .eq(TenDayWaterUsePlan::getMonth, req.getMonth())
                .eq(TenDayWaterUsePlan::getTenDays, req.getTenDays())
                .remove();
        if(update){
            tenDayWaterUsePlanOwnerService.lambdaUpdate().eq(TenDayWaterUsePlanOwner::getArea, req.getArea())
                    .eq(TenDayWaterUsePlanOwner::getUseWaterUser, req.getUseWaterUser())
                    .eq(TenDayWaterUsePlanOwner::getYear, req.getYear())
                    .eq(TenDayWaterUsePlanOwner::getMonth, req.getMonth())
                    .eq(TenDayWaterUsePlanOwner::getTenDays, req.getTenDays())
                    .remove();
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    private Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        String format = df.format(value);
        double v = Double.parseDouble(format);
        return v;
    }
}

