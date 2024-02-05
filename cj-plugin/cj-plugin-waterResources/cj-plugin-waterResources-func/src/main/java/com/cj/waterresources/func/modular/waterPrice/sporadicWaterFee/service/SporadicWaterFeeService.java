package com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.bean.req.SporadicWaterFeeSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.entity.SporadicWaterFee;

import java.util.List;

/**
 * 零星水费(SporadicWaterFee)表服务接口
 *
 * @author makejava
 * @since 2024-02-01 08:58:08
 */
public interface SporadicWaterFeeService extends IService<SporadicWaterFee> {

    RestResponse add(SporadicWaterFee sporadicWaterFee);

    RestResponse delete(String ids);

    RestResponse update(SporadicWaterFee sporadicWaterFee);

    RestResponse<List<SporadicWaterFee>> selectList(SporadicWaterFeeSelectListReq req);

}

