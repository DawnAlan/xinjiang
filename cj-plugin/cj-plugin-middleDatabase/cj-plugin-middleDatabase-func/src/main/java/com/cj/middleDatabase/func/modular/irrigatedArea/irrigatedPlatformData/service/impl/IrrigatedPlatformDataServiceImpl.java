package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.mapper.IrrigatedPlatformDataMapper;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.entity.IrrigatedPlatformData;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.service.IrrigatedPlatformDataService;
import org.springframework.stereotype.Service;

/**
 * 灌区平台所有数据(IrrigatedPlatformData)表服务实现类
 *
 * @author makejava
 * @since 2023-12-06 12:34:01
 */
@Service("irrigatedPlatformDataService")
public class IrrigatedPlatformDataServiceImpl extends ServiceImpl<IrrigatedPlatformDataMapper, IrrigatedPlatformData> implements IrrigatedPlatformDataService {

}

