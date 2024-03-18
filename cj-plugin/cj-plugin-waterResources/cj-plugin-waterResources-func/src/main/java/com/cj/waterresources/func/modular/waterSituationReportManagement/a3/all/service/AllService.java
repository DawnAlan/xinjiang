package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.SelectListForIndustrialWaterFeeReq;

public interface AllService {

    RestResponse deleteAll(String date);

    RestResponse statistics(A3StatisticsReq req);

    RestResponse selectInfoList(SelectInfoListReq req);

    RestResponse selectListForIndustrialWaterFee(SelectListForIndustrialWaterFeeReq req);
}
