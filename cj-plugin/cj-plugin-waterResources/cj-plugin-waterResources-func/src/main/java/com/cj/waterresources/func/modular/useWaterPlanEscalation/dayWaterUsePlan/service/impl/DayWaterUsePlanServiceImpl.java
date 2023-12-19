package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.bean.req.DayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.mapper.DayWaterUsePlanMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日用水计划(DayWaterUsePlan)表服务实现类
 *
 * @author makejava
 * @since 2023-12-07 17:27:09
 */
@Service("dayWaterUsePlanService")
public class DayWaterUsePlanServiceImpl extends ServiceImpl<DayWaterUsePlanMapper, DayWaterUsePlan> implements DayWaterUsePlanService {

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Override
    public RestResponse add(DayWaterUsePlan dayWaterUsePlan) {
        dayWaterUsePlan.setId(UUIDUtils.getUUID());
        dayWaterUsePlan.setCreateTime(new Date());
        dayWaterUsePlan.setDel(0);
        boolean save = this.save(dayWaterUsePlan);
        if(save){
            return RestResponse.ok("上报成功");
        }else {
            return RestResponse.no("上报失败");
        }
    }

    @Override
    public RestResponse update(DayWaterUsePlan dayWaterUsePlan) {
        dayWaterUsePlan.setUpdateTime(new Date());
        boolean b = this.updateById(dayWaterUsePlan);
        if(b){
            return RestResponse.ok("更新成功");
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse<DayWaterUsePlan> select(DayWaterUsePlanSelectReq req) {
        DayWaterUsePlan one = this.baseMapper.selectOne(req);
        if(null!=one){
            return RestResponse.ok(one);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse<List<SelectInfoByIrrigationNameListRes>> selectValue(String names) {
        List<SelectInfoByIrrigationNameListRes> resList = irrigatedPlatformDataInfoService.selectInfoByIrrigationNameList(names.split(","));
        if(null != resList && resList.size()>0){
            return RestResponse.ok(resList);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

