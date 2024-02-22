package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;

/**
 * 头屯河水库蓄水调度计划表(WaterStorageSchedulingTth)表服务接口
 *
 * @author makejava
 * @since 2023-12-12 10:20:46
 */
public interface WaterStorageSchedulingTthService extends IService<WaterStorageSchedulingTth> {

    RestResponse add(String formId);

    RestResponse edit(WaterStorageSchedulingTth waterStorageSchedulingTth);

}

