package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.*;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.RealTimeEngineeringSituationDataRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.TodayWaterSituationRes;

import java.util.List;
import java.util.Map;

public interface AllService {

    RestResponse deleteAll(String date);

    RestResponse statistics(A3StatisticsReq req);

    RestResponse selectInfoList(SelectInfoListReq req);
    List<HydrographRes> selectInfoListAllNew(SelectInfoListNewReq req);

    RestResponse selectListForIndustrialWaterFee(SelectListForIndustrialWaterFeeReq req);

    RestResponse selectReportForms(ReportFormsReq req);

    RestResponse selectFloodRetentionCapacity(String date);
    RestResponse selectFloodRetentionCapacityNew(String date,String ids);
    RestResponse<Map<String,List<TodayWaterSituationRes>>> selectTodayWaterSituation(String date);

    RestResponse selectTodayWaterSituationForFlood(String date,String ids);

    RestResponse selectTodayRainfall(String date,Integer hour);

    RestResponse updateInfoDate();

    RestResponse updateDkl(String startTime,String endTime);

    RestResponse selectTodayWaterSituationSelectById(SelectTodayWaterSituationSelectByIdReq req);

    RestResponse selectReservoirHistoryList(SelectReservoirHistoryListReq req);

    RestResponse<List<RealTimeEngineeringSituationDataRes>> getRealTimeWaterLevelData(String date);

    RestResponse selectCapacityOutPutDetail(String date, String ids);
}
