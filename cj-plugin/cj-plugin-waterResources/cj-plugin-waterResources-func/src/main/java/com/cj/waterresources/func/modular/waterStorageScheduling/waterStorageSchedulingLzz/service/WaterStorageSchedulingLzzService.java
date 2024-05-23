package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;

import java.util.List;

/**
 * 楼庄子水库蓄水调度计划表(WaterStorageSchedulingLzz)表服务接口
 *
 * @author makejava
 * @since 2023-12-12 10:20:21
 */
public interface WaterStorageSchedulingLzzService extends IService<WaterStorageSchedulingLzz> {


    RestResponse add(String formId);

    RestResponse edit(WaterStorageSchedulingLzz waterStorageSchedulingLzzList);
}

