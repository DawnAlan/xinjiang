package com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;

/**
 * 库容曲线表(StorageCapacityCurve)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-02 16:39:04
 */
@DS("master")
public interface StorageCapacityCurveMapper extends BaseMapper<StorageCapacityCurve> {

}

