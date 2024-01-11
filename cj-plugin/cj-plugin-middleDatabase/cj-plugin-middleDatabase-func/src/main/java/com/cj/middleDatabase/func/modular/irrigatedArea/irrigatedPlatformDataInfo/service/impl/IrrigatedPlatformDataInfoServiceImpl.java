package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.mapper.IrrigatedPlatformDataInfoMapper;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表服务实现类
 *
 * @author makejava
 * @since 2023-12-06 19:26:07
 */
@Service("irrigatedPlatformDataInfoService")
public class IrrigatedPlatformDataInfoServiceImpl extends ServiceImpl<IrrigatedPlatformDataInfoMapper, IrrigatedPlatformDataInfo> implements IrrigatedPlatformDataInfoService {

    @Override
    public List<SelectInfoByIrrigationNameListRes> selectInfoByIrrigationNameList(String[] name) {
        List<SelectInfoByIrrigationNameListRes> list = new ArrayList<>();
        for(String n:name){
            SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameListRes = this.baseMapper.selectInfoByIrrigationNameList(n);
            list.add(selectInfoByIrrigationNameListRes);
        }
        return list;
    }

    @Override
    public List<IrrigatedPlatformDataInfo> selectInfoByCondition(String id, String time, String startTime, String endTime) {
        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(time)){
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = this.baseMapper.selectInfoByCondition1(id, startTime, endTime);
            return irrigatedPlatformDataInfos;
        }
        if(StringUtils.isNotEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = this.baseMapper.selectInfoByCondition2(id, time);
            return irrigatedPlatformDataInfos;
        }
        if(StringUtils.isEmpty(time) && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)){
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = this.baseMapper.selectInfoByCondition3(id);
            return irrigatedPlatformDataInfos;
        }
        return null;
    }

    @Override
    public IrrigatedPlatformDataInfo selectOneByCondition(String name, String time) {
        return this.baseMapper.selectOneByCondition(name,time);
    }

    @Override
    public IrrigatedPlatformDataInfo selectOneByCondition1(String name, String time) {
        return this.baseMapper.selectOneByCondition1(name,time);
    }

    @Override
    public List<IrrigatedPlatformDataInfo> selectOneByCondition2(String name, String time) {
        return this.baseMapper.selectOneByCondition2(name,time);
    }

    @Override
    public List<RealTimeRainfallRes> getRealTimeRainfall(String startTime, String endTime) {
        return this.baseMapper.getRealTimeRainfall(startTime,endTime);
    }

    @Override
    public List<IrrigatedPlatformDataInfo> selectInfoByTime(String time,String name) {
        return this.baseMapper.selectInfoByTime(time,name);
    }

    @Override
    public List<IrrigatedPlatformDataInfo> selectHistoryList(String name, String startTime, String endTime) {
        return this.baseMapper.selectHistoryList(name,startTime,endTime);
    }
}

