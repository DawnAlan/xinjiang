package com.cj.flood.func.modular.prediction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastListReq;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author July Lion
* @description 针对表【INCOMING_WATER_FORECAST(来水预报)】的数据库操作Service
* @createDate 2023-11-03 11:17:56
*/
public interface IncomingWaterForecastService extends IService<IncomingWaterForecast> {

    RestResponse add(IncomingWaterForecastAddReq req);

    RestResponse delete(String ids);

    RestResponse update(IncomingWaterForecast incomingWaterForecast);

    RestResponse<IPage<IncomingWaterForecast>> selectList(IncomingWaterForecastListReq req);

    RestResponse<IncomingWaterForecastDetailsRes> selectDetails(String id);

    List<IncomingWaterForecast> getPredictionListByTimeType(Integer timeType);

    Map<String,Object> getPredictionListByName(String id, String reservoir);
}
