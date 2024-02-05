package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;

public interface AllService {

    RestResponse deleteAll(String date);

    RestResponse statistics(A3StatisticsReq req);
}
