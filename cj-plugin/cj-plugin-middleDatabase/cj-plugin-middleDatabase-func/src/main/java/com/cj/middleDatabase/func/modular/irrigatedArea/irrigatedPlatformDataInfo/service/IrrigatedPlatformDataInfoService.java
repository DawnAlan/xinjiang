package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;

import java.util.Date;
import java.util.List;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表服务接口
 *
 * @author makejava
 * @since 2023-12-06 19:26:07
 */
public interface IrrigatedPlatformDataInfoService extends IService<IrrigatedPlatformDataInfo> {

    List<SelectInfoByIrrigationNameListRes> selectInfoByIrrigationNameList(String[] name);
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationName(String name);
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameForHistory(String name,String time);

    List<IrrigatedPlatformDataInfo> selectInfoByCondition(String id,String time,String startTime,String endTime);
    IrrigatedPlatformDataInfo selectOneByCondition(String name,String time);

    IrrigatedPlatformDataInfo selectOneByCondition1(String name,String time);
    List<IrrigatedPlatformDataInfo> selectOneByConditionNotName(String time);

    List<IrrigatedPlatformDataInfo> selectOneByCondition2(String name,String time);
    List<IrrigatedPlatformDataInfo> selectOneByConditionByTime(String time);

    List<RealTimeRainfallRes> getRealTimeRainfall(String startTime, String endTime,List<String> ids);

    List<RealTimeRainfallRes> getRealTimeRainfallByDate(String date,List<String> ids);

    List<IrrigatedPlatformDataInfo>  selectInfoByTime(String time,String name);

    List<IrrigatedPlatformDataInfo> selectHistoryList(String name, String startTime, String endTime);

    List<IrrigatedPlatformDataInfo> getRealTimeWaterLevel(String station);

    List<IrrigatedPlatformDataInfo> getCurrentDate(String startTime, String endTime);

    List<IrrigatedPlatformDataInfo> getRecentlyRainfalls(String dateTime);
}

