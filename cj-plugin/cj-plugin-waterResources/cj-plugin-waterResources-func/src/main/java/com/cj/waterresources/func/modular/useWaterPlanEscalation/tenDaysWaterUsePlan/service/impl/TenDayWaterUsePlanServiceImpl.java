package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.mapper.TenDayWaterUsePlanMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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

    @Override
    public RestResponse<List<TenDayWaterUsePlan>> selectList(TenDayWaterUsePlanSelectReq req) {
        List<TenDayWaterUsePlan> list = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getIrrigatedArea()),TenDayWaterUsePlan::getIrrigatedArea, req.getIrrigatedArea()).
                eq(StringUtils.isNotEmpty(req.getUseWaterUser()),TenDayWaterUsePlan::getUseWaterUser, req.getUseWaterUser()).
                eq(StringUtils.isNotEmpty(req.getCropType()),TenDayWaterUsePlan::getCropType, req.getCropType()).
                eq(req.getYear() !=null,TenDayWaterUsePlan::getYear, req.getYear()).
                eq(req.getMonth() !=null,TenDayWaterUsePlan::getMonth, req.getMonth()).
                eq(StringUtils.isNotEmpty(req.getTenDays()),TenDayWaterUsePlan::getTenDays, req.getTenDays()).
                eq(TenDayWaterUsePlan::getDel,0).
                list();
        if(null != list && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse add(TenDayWaterUsePlan tenDayWaterUsePlan) {
        List<TenDayWaterUsePlan> list = this.lambdaQuery().eq(TenDayWaterUsePlan::getIrrigatedArea, tenDayWaterUsePlan.getIrrigatedArea()).
                eq(TenDayWaterUsePlan::getUseWaterUser, tenDayWaterUsePlan.getUseWaterUser()).
                eq(TenDayWaterUsePlan::getIrrigatedCrop, tenDayWaterUsePlan.getIrrigatedCrop()).list();
        if(null!= list && list.size()>0){
            return RestResponse.no("该作物已存在，请勿重复添加");
        }
        tenDayWaterUsePlan.setId(UUIDUtils.getUUID());
        tenDayWaterUsePlan.setDel(0);
        tenDayWaterUsePlan.setCreateTime(new Date());
        boolean save = this.save(tenDayWaterUsePlan);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse update(TenDayWaterUsePlan tenDayWaterUsePlan) {
        tenDayWaterUsePlan.setUpdateTime(new Date());
        boolean b = this.updateById(tenDayWaterUsePlan);
        if(b){
            return RestResponse.ok("更新成功");
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(TenDayWaterUsePlan::getDel, 1).eq(TenDayWaterUsePlan::getId, id).update();
        if(update){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }
}

