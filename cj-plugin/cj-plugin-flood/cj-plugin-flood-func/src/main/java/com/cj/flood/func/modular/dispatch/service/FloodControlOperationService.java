package com.cj.flood.func.modular.dispatch.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationAddReq;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationListReq;
import com.cj.flood.func.modular.dispatch.bean.res.FloodControlOperationListRes;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;

import java.util.List;
import java.util.Map;

/**
 * 防洪调度表(FloodControlOperation)表服务接口
 *
 * @author makejava
 * @since 2023-11-09 15:49:47
 */
public interface FloodControlOperationService extends IService<FloodControlOperation> {

    RestResponse<Map<String, List<PredictionProcessDto>>> selectDetails(String id);

    RestResponse add(FloodControlOperationAddReq req);

    RestResponse<IPage<FloodControlOperationListRes>> selectList(FloodControlOperationListReq req);

    RestResponse<Map<String,Object>> containmentCalculator(String ids);

}

