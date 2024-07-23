package com.cj.flood.func.modular.prediction.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.*;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;

import java.util.List;
import java.util.Map;

/**
 * 陕北模型参数(ModelParameters)表服务接口
 *
 * @author makejava
 * @since 2024-03-13 12:27:10
 */
public interface ModelParametersService extends IService<ModelParameters> {

    Map<String, ModelParameters> queryList(QueryListReq req);

    Map calibrate(CalibrateReq input);


    Boolean del(List<String> input);

    boolean setDefault(SetDefaultParametersReq req);

    boolean ls(ModelParametersReq input);

    RestResponse paramDetail(ModelParameterDetailReq req);

    Map queryDefaultList(String siteName);
}

