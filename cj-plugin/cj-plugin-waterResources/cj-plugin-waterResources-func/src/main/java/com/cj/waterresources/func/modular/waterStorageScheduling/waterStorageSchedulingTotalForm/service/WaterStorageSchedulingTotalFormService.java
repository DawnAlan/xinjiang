package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;

/**
 * 供水计划管理总表(WaterStorageSchedulingTotalForm)表服务接口
 *
 * @author makejava
 * @since 2024-02-18 09:43:28
 */
public interface WaterStorageSchedulingTotalFormService extends IService<WaterStorageSchedulingTotalForm> {

    RestResponse add(WaterStorageSchedulingTotalForm waterStorageSchedulingTotalForm);

    RestResponse remove(String id);

}

