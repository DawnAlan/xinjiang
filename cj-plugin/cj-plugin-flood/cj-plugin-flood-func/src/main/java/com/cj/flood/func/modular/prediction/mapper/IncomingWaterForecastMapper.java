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
    @Select("select * \n" +
            "from INCOMING_WATER_FORECAST \n" +
            "where TO_CHAR(PREDICTION_TIME,'MM-DD') = '01-01' AND TO_CHAR(END_TIME,'MM-DD')= '01-01' ")
    List<IncomingWaterForecast> getPredictionListForYear();
    @Select("select * \n" +
            "from INCOMING_WATER_FORECAST \n" +
            "where (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'MM'))- TO_NUMBER(TO_CHAR(END_TIME,'MM')) = -1) \n" +
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = 0) and TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))=1 and TO_NUMBER(TO_CHAR(END_TIME,'DD'))=1")
    List<IncomingWaterForecast> getPredictionListForMonth();
    @Select("select * \n" +
            "from INCOMING_WATER_FORECAST \n" +
            "where ((TO_NUMBER(TO_CHAR(PREDICTION_TIME,'MM'))- TO_NUMBER(TO_CHAR(END_TIME,'MM')) = 0) \n" +
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = -10)) or \n" +
            "((TO_NUMBER(TO_CHAR(PREDICTION_TIME,'MM'))- TO_NUMBER(TO_CHAR(END_TIME,'MM')) = -1) \n" +
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = 20))")
    List<IncomingWaterForecast> getPredictionListForTenDays();
    @Select("select * \n" +
            "from INCOMING_WATER_FORECAST \n" +
            "where (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'MM'))- TO_NUMBER(TO_CHAR(END_TIME,'MM')) = 0) \n" +
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = -1)")
    List<IncomingWaterForecast> getPredictionListForDay();

}




