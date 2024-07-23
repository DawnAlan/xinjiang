package com.cj.flood.func.modular.rollUpdate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.rollUpdate.bean.req.ModelRollUpdateSelectListReq;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;

/**
 * 模型滚动更新表(ModelRollUpdate)表服务接口
 *
 * @author makejava
 * @since 2024-07-19 14:59:18
 */
public interface ModelRollUpdateService extends IService<ModelRollUpdate> {

    RestResponse add(ModelRollUpdate modelRollUpdate);

    RestResponse update(ModelRollUpdate modelRollUpdate);

    RestResponse stop(String ids);

    RestResponse start(String ids);

    RestResponse delete(String ids);

    RestResponse selectList(ModelRollUpdateSelectListReq req);

    RestResponse selectModelResultList(String id);

    RestResponse selectDetailsById(String id);

}

