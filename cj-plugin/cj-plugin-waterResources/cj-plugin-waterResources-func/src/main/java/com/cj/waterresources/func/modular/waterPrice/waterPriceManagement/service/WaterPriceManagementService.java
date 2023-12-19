package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceUpdateReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;

import java.util.List;

/**
 * 水价管理(WaterPriceManagement)表服务接口
 *
 * @author makejava
 * @since 2023-11-29 10:44:40
 */
public interface WaterPriceManagementService extends IService<WaterPriceManagement> {

    RestResponse<List<WaterPriceSelectListRes>> waterPriceSelectList(WaterPriceSelectListReq req);

    RestResponse updateWaterPrice(WaterPriceUpdateReq req);

    RestResponse deleteWaterPrice(String id);
}

