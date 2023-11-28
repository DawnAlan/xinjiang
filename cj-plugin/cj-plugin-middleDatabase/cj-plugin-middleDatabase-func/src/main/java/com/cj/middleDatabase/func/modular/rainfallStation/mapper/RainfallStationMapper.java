package com.cj.middleDatabase.func.modular.rainfallStation.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.rainfallStation.entity.RainfallStation;

/**
 * (RainfallStation)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-23 15:25:47
 */
@DS("master")
public interface RainfallStationMapper extends BaseMapper<RainfallStation> {

}

