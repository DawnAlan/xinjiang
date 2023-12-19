package com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.mapper.StorageCapacityCurveMapper;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.StorageCapacityCurveService;
import org.springframework.stereotype.Service;

/**
 * 库容曲线表(StorageCapacityCurve)表服务实现类
 *
 * @author makejava
 * @since 2023-12-02 16:39:05
 */
@Service("storageCapacityCurveService")
@DS("master")
public class StorageCapacityCurveServiceImpl extends ServiceImpl<StorageCapacityCurveMapper, StorageCapacityCurve> implements StorageCapacityCurveService {

}

