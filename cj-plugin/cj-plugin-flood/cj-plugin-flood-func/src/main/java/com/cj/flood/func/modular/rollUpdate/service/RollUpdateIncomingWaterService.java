package com.cj.flood.func.modular.rollUpdate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.res.IncomingWaterForecastDetailsRes;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateIncomingWater;

import java.util.Date;

/**
 * 滚动更新来水预报模型结果表(RollUpdateIncomingWater)表服务接口
 *
 * @author makejava
 * @since 2024-07-19 14:59:57
 */
public interface RollUpdateIncomingWaterService extends IService<RollUpdateIncomingWater> {

    String add(Date time, ModelRollUpdate modelRollUpdate, String user);
    IncomingWaterForecastDetailsRes selectDetails(String id);

}

