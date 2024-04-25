package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.model.func.modular.watertransfer.entity.Waterdemand;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.bean.req.DayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 日用水计划(DayWaterUsePlan)表服务接口
 *
 * @author makejava
 * @since 2023-12-07 17:27:08
 */
public interface DayWaterUsePlanService extends IService<DayWaterUsePlan> {

    RestResponse add(DayWaterUsePlan dayWaterUsePlan);

    RestResponse update(DayWaterUsePlan dayWaterUsePlan,Boolean flag);

    RestResponse<DayWaterUsePlan> select(DayWaterUsePlanSelectReq req);

    RestResponse<List<SelectInfoByIrrigationNameListRes>> selectValue(String names,String station);

    List getWaterNeedDetail(Date startTime, Date endTime);
}

