package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 月用水计划(MonthWaterUsePlan)表服务实现类
 *
 * @author makejava
 * @since 2023-12-07 16:48:27
 */
@Service("monthWaterUsePlanService")
public class MonthWaterUsePlanServiceImpl extends ServiceImpl<MonthWaterUsePlanMapper, MonthWaterUsePlan> implements MonthWaterUsePlanService {

    @Override
    public RestResponse add(MonthWaterUsePlan monthWaterUsePlan) {
        List<MonthWaterUsePlan> list = this.lambdaQuery().eq(MonthWaterUsePlan::getYear, monthWaterUsePlan.getYear()).
                eq(MonthWaterUsePlan::getMonth, monthWaterUsePlan.getMonth()).
                eq(MonthWaterUsePlan::getArea, monthWaterUsePlan.getArea()).
                eq(MonthWaterUsePlan::getDel,0).
                eq(MonthWaterUsePlan::getUnit, monthWaterUsePlan.getUnit()).list();
        if(null != list && list.size()>0){
            return RestResponse.no("请勿重复添加单位");
        }
        monthWaterUsePlan.setId(UUIDUtils.getUUID());
        monthWaterUsePlan.setCreateTime(new Date());
        monthWaterUsePlan.setDel(0);
        boolean save = this.save(monthWaterUsePlan);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(MonthWaterUsePlan::getDel, 1).eq(MonthWaterUsePlan::getId, id).update();
        if(update){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse update(MonthWaterUsePlan monthWaterUsePlan) {
        MonthWaterUsePlan byId = this.getById(monthWaterUsePlan.getId());
        if(byId.getUnit().equals(monthWaterUsePlan.getUnit())){
            monthWaterUsePlan.setUpdateTime(new Date());
            boolean b = this.updateById(monthWaterUsePlan);
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }else {
            List<MonthWaterUsePlan> list = this.lambdaQuery().eq(MonthWaterUsePlan::getYear, monthWaterUsePlan.getYear()).
                    eq(MonthWaterUsePlan::getMonth, monthWaterUsePlan.getMonth()).
                    eq(MonthWaterUsePlan::getArea, monthWaterUsePlan.getArea()).eq(MonthWaterUsePlan::getUnit, monthWaterUsePlan.getUnit()).list();
            if(null != list && list.size()>0){
                return RestResponse.no("请勿重复添加单位");
            }
            monthWaterUsePlan.setUpdateTime(new Date());
            boolean b = this.updateById(monthWaterUsePlan);
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
    }

    @Override
    public RestResponse<List<MonthWaterUsePlan>> selectList(MonthWaterUsePlanSelectListReq req) {
        List<MonthWaterUsePlan> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getArea()), MonthWaterUsePlan::getArea, req.getArea()).
                eq(req.getYear() != null, MonthWaterUsePlan::getYear, req.getYear()).
                eq(req.getMonth() != null,MonthWaterUsePlan::getMonth,req.getMonth()).
                eq(MonthWaterUsePlan::getDel, 0).list();
        if(list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

