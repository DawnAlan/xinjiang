package com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectTodayWaterSituationRes;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.mapper.LzzGaugingStationMapper;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 水位站数据表(LzzGaugingStation)表服务实现类
 *
 * @author makejava
 * @since 2023-12-05 17:55:46
 */
@Service("lzzGaugingStationService")
@DS("master")
public class LzzGaugingStationServiceImpl extends ServiceImpl<LzzGaugingStationMapper, LzzGaugingStation> implements LzzGaugingStationService {

    @Override
    public List<LzzGaugingStation> selectInfoByCondition(String id, String time, String startTime, String endTime) {
        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(time)){
            List<LzzGaugingStation> lzzGaugingStationList = this.baseMapper.selectInfoByCondition3(id, startTime, endTime);
            return lzzGaugingStationList;
        }
        if(StringUtils.isNotEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<LzzGaugingStation> lzzGaugingStationList = this.baseMapper.selectInfoByCondition2(id, time);
            return lzzGaugingStationList;
        }
        if(StringUtils.isEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<LzzGaugingStation> lzzGaugingStationList = this.baseMapper.selectInfoByCondition1(id);
            return lzzGaugingStationList;
        }
        return null;
    }

    @Override
    public LzzGaugingStation selectInfoByNameAndTime(String time, String name) {
        return this.baseMapper.selectInfoByNameAndTime(time,name);
    }

    @Override
    public LzzGaugingStation selectInfoByTime(String time,String name) {
        return this.baseMapper.selectInfoByTime(time,name);
    }

    @Override
    public List<LzzGaugingStation> selectHistoryList(String name, String startTime, String endTime) {
        return this.baseMapper.selectHistoryList(name,startTime,endTime);
    }

    @Override
    public List<LzzGaugingStation> getCurrent(String dateTime) {
        return this.baseMapper.getCurrent(dateTime);
    }

    @Override
    public List<SelectTodayWaterSituationRes> selectTodayWaterSituation(List<String> ids, String date) {
        return this.baseMapper.selectTodayWaterSituation(ids,date);
    }
}

