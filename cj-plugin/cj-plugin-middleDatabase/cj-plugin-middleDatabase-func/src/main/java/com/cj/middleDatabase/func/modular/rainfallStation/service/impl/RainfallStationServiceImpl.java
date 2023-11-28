package com.cj.middleDatabase.func.modular.rainfallStation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.rainfallStation.mapper.RainfallStationMapper;
import com.cj.middleDatabase.func.modular.rainfallStation.entity.RainfallStation;
import com.cj.middleDatabase.func.modular.rainfallStation.service.RainfallStationService;
import org.springframework.stereotype.Service;

/**
 * (RainfallStation)表服务实现类
 *
 * @author makejava
 * @since 2023-11-23 15:25:49
 */
@Service("rainfallStationService")
@DS("master")
public class RainfallStationServiceImpl extends ServiceImpl<RainfallStationMapper, RainfallStation> implements RainfallStationService {

}

