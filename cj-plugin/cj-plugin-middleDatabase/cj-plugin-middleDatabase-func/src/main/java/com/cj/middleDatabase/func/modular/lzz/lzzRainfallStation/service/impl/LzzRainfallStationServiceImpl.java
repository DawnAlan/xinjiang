package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.mapper.LzzRainfallStationMapper;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (LzzRainfallStation)表服务实现类
 *
 * @author makejava
 * @since 2023-12-05 17:56:38
 */
@Service("lzzRainfallStationService")
@DS("master")
public class LzzRainfallStationServiceImpl extends ServiceImpl<LzzRainfallStationMapper, LzzRainfallStation> implements LzzRainfallStationService {

    @Override
    public List<LzzRainfallStation> selectInfoByCondition(String id, String time, String startTime, String endTime) {
        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(time)){
            List<LzzRainfallStation> lzzRainfallStations = this.baseMapper.selectInfoByCondition3(id, startTime, endTime);
            return lzzRainfallStations;
        }
        if(StringUtils.isNotEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<LzzRainfallStation> lzzRainfallStations = this.baseMapper.selectInfoByCondition2(id, time);
            return lzzRainfallStations;
        }
        if(StringUtils.isEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<LzzRainfallStation> lzzRainfallStations = this.baseMapper.selectInfoByCondition1(id);
            return lzzRainfallStations;
        }
        return null;
    }

    @Override
    public List<LzzRainfallStation> selectYesterday(String name, String time) {
        return this.baseMapper.selectYesterday(name, time);
    }

    @Override
    public List<RealTimeRainfallRes> getRealTimeRainfall(String startTime, String endTime) {
        return this.baseMapper.getRealTimeRainfall(startTime, endTime);
    }

    @Override
    public List<LzzRainfallStation> selectHistoryList(String name, String startTime, String endTime) {
        return this.baseMapper.selectHistoryList(name, startTime, endTime);
    }
}

