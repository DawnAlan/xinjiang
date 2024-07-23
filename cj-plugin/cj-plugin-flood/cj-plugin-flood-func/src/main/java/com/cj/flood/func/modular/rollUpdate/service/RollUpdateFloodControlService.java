package com.cj.flood.func.modular.rollUpdate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateFloodControl;

import java.util.List;
import java.util.Map;

/**
 * (RollUpdateFloodControl)表服务接口
 *
 * @author makejava
 * @since 2024-07-19 14:59:38
 */
public interface RollUpdateFloodControlService extends IService<RollUpdateFloodControl> {

    void add(String incomingWaterId, ModelRollUpdate modelRollUpdate);

    Map<String, List<PredictionProcessDto>> selectDetails(String id);

}

