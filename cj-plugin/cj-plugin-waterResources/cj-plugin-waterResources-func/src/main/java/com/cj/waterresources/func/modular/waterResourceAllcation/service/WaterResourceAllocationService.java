package com.cj.waterresources.func.modular.waterResourceAllcation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.IncomingWaterForecastDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.ViewModelReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationQueryReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.ViewModelRes;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;

import java.util.List;

/**
 * 水资源调配模型表(WaterResourceAllocation)表服务接口
 *
 * @author makejava
 * @since 2023-11-14 17:34:50
 */
public interface WaterResourceAllocationService extends IService<WaterResourceAllocation> {

    RestResponse<List<IncomingWaterForecastDto>> getIncomingWaterForecastListByTime(String startTime, String endTime, Integer bucketType);

    RestResponse generativeModel(WaterResourceAllocationAddReq req);


    RestResponse getAllocationPage(WaterResourceAllocationQueryReq req);

    RestResponse delById(List<String> ids);

    RestResponse compare(List<String> ids);

    RestResponse<List<ViewModelRes>> viewModel(ViewModelReq id);

    RestResponse updateAllocation(WaterResourceAllocation waterResourceAllocation);

    RestResponse getWaterResourceAllocationDetails(String id);

    RestResponse contrast(String idA,String idB);

    RestResponse waterQuantityCalculation(String id);

    RestResponse getRealTimeReservoirLevel(String reservoir);
}

