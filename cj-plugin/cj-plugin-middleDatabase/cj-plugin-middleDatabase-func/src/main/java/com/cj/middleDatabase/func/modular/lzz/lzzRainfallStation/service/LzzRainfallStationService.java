package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;

import java.util.Date;
import java.util.List;

/**
 * (LzzRainfallStation)表服务接口
 *
 * @author makejava
 * @since 2023-12-05 17:56:38
 */
@DS("master")
public interface LzzRainfallStationService extends IService<LzzRainfallStation> {


    List<LzzRainfallStation> selectInfoByCondition(String id,String time,String startTime,String endTime);

    List<LzzRainfallStation> selectYesterday1(String name,String time);
    List<LzzRainfallStation> selectYesterday(String time);

    List<RealTimeRainfallRes> getRealTimeRainfall(String startTime, String endTime);
    List<RealTimeRainfallRes> getRealTimeRainfallByDate(String date,Integer num);

    List<LzzRainfallStation> selectHistoryList(String name, String startTime, String endTime);

    List<LzzRainfallStation> getRecentlyRainfalls(String dateTime);
}

