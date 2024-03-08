package com.cj.waterresources.func.modular.waterSituationDataMaintenance.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.UpdateInfoReq;

import java.util.Map;

public interface WaterSituationService {
    RestResponse<Map<String,Object>> selectTree();

    RestResponse selectInfoList(SelectInfoListReq req);

    RestResponse update(UpdateInfoReq req);

    RestResponse selectInfoListAll(SelectInfoListReq req);
}
