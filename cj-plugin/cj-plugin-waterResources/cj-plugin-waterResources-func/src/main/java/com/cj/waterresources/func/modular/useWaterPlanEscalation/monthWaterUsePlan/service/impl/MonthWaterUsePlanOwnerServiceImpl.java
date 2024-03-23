package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanOwnerMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlanOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * (MonthWaterUsePlanOwner)表服务实现类
 *
 * @author makejava
 * @since 2024-03-23 17:57:24
 */
@Service("monthWaterUsePlanOwnerService")
public class MonthWaterUsePlanOwnerServiceImpl extends ServiceImpl<MonthWaterUsePlanOwnerMapper, MonthWaterUsePlanOwner> implements MonthWaterUsePlanOwnerService {

    @Override
    public RestResponse<List<MonthWaterUsePlanOwner>> selectList(MonthWaterUsePlanSelectListReq req) {
        List<MonthWaterUsePlanOwner> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getArea()), MonthWaterUsePlanOwner::getArea, req.getArea()).
                eq(req.getYear() != null, MonthWaterUsePlanOwner::getYear, req.getYear()).
                eq(req.getMonth() != null,MonthWaterUsePlanOwner::getMonth,req.getMonth()).
                eq(MonthWaterUsePlanOwner::getDel, 0).list();
        if(list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse add(MonthWaterUsePlanOwner monthWaterUsePlanOwner) {
        List<MonthWaterUsePlanOwner> list = this.lambdaQuery().eq(MonthWaterUsePlanOwner::getYear, monthWaterUsePlanOwner.getYear()).
                eq(MonthWaterUsePlanOwner::getMonth, monthWaterUsePlanOwner.getMonth()).
                eq(MonthWaterUsePlanOwner::getArea, monthWaterUsePlanOwner.getArea()).
                eq(MonthWaterUsePlanOwner::getDel,0).
                eq(MonthWaterUsePlanOwner::getUnit, monthWaterUsePlanOwner.getUnit()).list();
        if(null != list && list.size()>0){
            return RestResponse.no("请勿重复添加单位");
        }
        monthWaterUsePlanOwner.setId(UUIDUtils.getUUID());
        monthWaterUsePlanOwner.setCreateTime(new Date());
        monthWaterUsePlanOwner.setDel(0);
        boolean save = this.save(monthWaterUsePlanOwner);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }
}

