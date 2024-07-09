package com.cj.waterresources.func.modular.waterSituationDataMaintenance.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListByIdsReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.UpdateInfoReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;

import java.util.List;
import java.util.Map;

public interface WaterSituationService {
    RestResponse<Map<String,Object>> selectTree();

    RestResponse selectInfoList(SelectInfoListReq req);
    RestResponse<List<HydrographRes>> selectInfoList1(SelectInfoListReq req);

    RestResponse update(UpdateInfoReq req);

    RestResponse selectInfoListAll(SelectInfoListReq req);
    RestResponse selectInfoListAllNew(SelectInfoListNewReq req);
    RestResponse selectInfoListByIdsNew(SelectInfoListByIdsReq req);

    RestResponse selectTodayWaterSituation(String date);
}
