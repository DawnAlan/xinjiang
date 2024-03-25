package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.mapper.TenDayWaterUsePlanOwnerMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (TenDayWaterUsePlanOwner)表服务实现类
 *
 * @author makejava
 * @since 2024-03-25 16:55:15
 */
@Service("tenDayWaterUsePlanOwnerService")
public class TenDayWaterUsePlanOwnerServiceImpl extends ServiceImpl<TenDayWaterUsePlanOwnerMapper, TenDayWaterUsePlanOwner> implements TenDayWaterUsePlanOwnerService {

    @Override
    public RestResponse<List<TenDayWaterUsePlanOwner>> selectList(TenDayWaterUsePlanSelectReq req) {
        List<TenDayWaterUsePlanOwner> list = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getIrrigatedArea()),TenDayWaterUsePlanOwner::getIrrigatedArea, req.getIrrigatedArea()).
                eq(StringUtils.isNotEmpty(req.getUseWaterUser()),TenDayWaterUsePlanOwner::getUseWaterUser, req.getUseWaterUser()).
                eq(StringUtils.isNotEmpty(req.getCropType()),TenDayWaterUsePlanOwner::getCropType, req.getCropType()).
                eq(req.getYear() !=null,TenDayWaterUsePlanOwner::getYear, req.getYear()).
                eq(req.getMonth() !=null,TenDayWaterUsePlanOwner::getMonth, req.getMonth()).
                eq(StringUtils.isNotEmpty(req.getTenDays()),TenDayWaterUsePlanOwner::getTenDays, req.getTenDays()).
                list();
        if(null != list && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

