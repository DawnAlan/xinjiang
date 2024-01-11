package com.cj.flood.func.modular.prediction.mapper;

import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author July Lion
* @description 针对表【INCOMING_WATER_FORECAST(来水预报)】的数据库操作Mapper
* @createDate 2023-11-03 11:17:56
* @Entity com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast
*/
public interface IncomingWaterForecastMapper extends BaseMapper<IncomingWaterForecast> {

    //TO_CHAR(TIME,'YYYY-MM-DD hh24:MI')
    @Select("select * from INCOMING_WATER_FORECAST where TO_CHAR(PREDICTION_TIME,'YYYY') = #{year} AND PERIOD_TIME_TYPE = 1 ")
    List<IncomingWaterForecast> getPredictionListForYear(@Param("year") String year);
    @Select("select * from INCOMING_WATER_FORECAST where TO_CHAR(PREDICTION_TIME,'YYYY-MM') = #{time} AND PERIOD_TIME_TYPE = 2")
    List<IncomingWaterForecast> getPredictionListForMonth(@Param("time")String time);
    @Select("select * from INCOMING_WATER_FORECAST where TO_CHAR(PREDICTION_TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime} AND PERIOD_TIME_TYPE = 3")
    List<IncomingWaterForecast> getPredictionListForTenDays(@Param("startTime")String startTime,@Param("endTime")String endTime);
    @Select("select * from INCOMING_WATER_FORECAST where TO_CHAR(PREDICTION_TIME,'YYYY-MM-DD') = #{time} AND PERIOD_TIME_TYPE = 4")
    List<IncomingWaterForecast> getPredictionListForDay(String time);

}




