package com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;

/**
 * 库容曲线表(StorageCapacityCurve)表服务接口
 *
 * @author makejava
 * @since 2023-12-02 16:39:04
 */
@DS("master")
public interface StorageCapacityCurveService extends IService<StorageCapacityCurve> {

}

