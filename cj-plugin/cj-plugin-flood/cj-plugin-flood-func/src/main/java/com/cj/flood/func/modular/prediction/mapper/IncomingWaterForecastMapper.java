package com.cj.flood.func.modular.prediction.mapper;

import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
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
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = 20)) AND MODEL_TYPE != 3")
    List<IncomingWaterForecast> getPredictionListForTenDays();
    @Select("select * \n" +
            "from INCOMING_WATER_FORECAST \n" +
            "where (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'MM'))- TO_NUMBER(TO_CHAR(END_TIME,'MM')) = 0) \n" +
            "AND (TO_NUMBER(TO_CHAR(PREDICTION_TIME,'DD'))- TO_NUMBER(TO_CHAR(END_TIME,'DD')) = -1)")
    List<IncomingWaterForecast> getPredictionListForDay();


    @Select("select CASE SUBSTR(time, 0, 2)\n" +
            "WHEN 24 THEN to_date(to_char(ADD_DAYS(RECORD_TIME, 1), 'yyyy-mm-dd'), 'yyyy-mm-dd')\n" +
            "ELSE to_date(TO_CHAR(RECORD_TIME, 'yyyy-mm-dd') || ' ' || SUBSTR(time, 0, 2), 'yyyy-mm-dd hh24:mi')\n" +
            "end as dates,v as flow,\n" +
            "CASE TABLE_HEAD_ID\n" +
            "WHEN '2ac7d9b44ab5497092e77d0dccf5e116' THEN 'flow'\n" +
            "ELSE 'level'\n" +
            "END as dataType\n" +
            "from tth.DAY_WATER_SITUATION_STATISTICS_TABLE_TTH \n" +
            "where TIME not like '%日均' and (RECORD_TIME between #{startTime} and #{endTime}) and TABLE_HEAD_ID = '2ac7d9b44ab5497092e77d0dccf5e116' \n" +
            "UNION ALL\n" +
            "select CASE SUBSTR(time, 0, 2)\n" +
            "WHEN 24 THEN to_date(to_char(ADD_DAYS(RECORD_TIME, 1), 'yyyy-mm-dd'), 'yyyy-mm-dd')\n" +
            "ELSE to_date(TO_CHAR(RECORD_TIME, 'yyyy-mm-dd') || ' ' || SUBSTR(time, 0, 2), 'yyyy-mm-dd hh24:mi')\n" +
            "end as dates,v as flow,\n" +
            "CASE TABLE_HEAD_ID\n" +
            "WHEN '2ac7d9b44ab5497092e77d0dccf5e116' THEN 'flow'\n" +
            "ELSE 'level'\n" +
            "END as dataType\n" +
            "from tth.DAY_WATER_SITUATION_STATISTICS_TABLE_TTH \n" +
            "where TIME not like '%日均' and (RECORD_TIME between #{startTime} and #{endTime}) and TABLE_HEAD_ID = '6d939b2a589940f08dcbf2f1f6b5807a' ")
    List<PredictInputData> selectResultTthByPrediction(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select CASE SUBSTR(time, 0, 2)\n" +
            "WHEN 24 THEN to_date(to_char(ADD_DAYS(RECORD_TIME, 1), 'yyyy-mm-dd'), 'yyyy-mm-dd')\n" +
            "ELSE to_date(TO_CHAR(RECORD_TIME, 'yyyy-mm-dd') || ' ' || SUBSTR(time, 0, 2), 'yyyy-mm-dd hh24:mi')\n" +
            "end as dates,v as flow,\n" +
            "CASE TABLE_HEAD_ID\n" +
            "WHEN '73f33822cf2c48caa4302dabf769b29b' THEN 'flow'\n" +
            "ELSE 'level'\n" +
            "END as dataType\n" +
            "from tth.DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ\n" +
            "where TIME not like '%日均' and (RECORD_TIME between #{startTime} and #{endTime}) and TABLE_HEAD_ID = '73f33822cf2c48caa4302dabf769b29b' \n" +
            "UNION ALL\n" +
            "select CASE SUBSTR(time, 0, 2)\n" +
            "WHEN 24 THEN to_date(to_char(ADD_DAYS(RECORD_TIME, 1), 'yyyy-mm-dd'), 'yyyy-mm-dd')\n" +
            "ELSE to_date(TO_CHAR(RECORD_TIME, 'yyyy-mm-dd') || ' ' || SUBSTR(time, 0, 2), 'yyyy-mm-dd hh24:mi')\n" +
            "end as dates,v as flow,\n" +
            "CASE TABLE_HEAD_ID\n" +
            "WHEN '73f33822cf2c48caa4302dabf769b29b' THEN 'flow'\n" +
            "ELSE 'level'\n" +
            "END as dataType\n" +
            "from tth.DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ \n" +
            "where TIME not like '%日均' and (RECORD_TIME between #{startTime} and #{endTime}) and TABLE_HEAD_ID = 'eeb9ee530bb1465094facd31a8d9d154'  ")
    List<PredictInputData> selectResultLzzByPrediction(@Param("startTime") String startTime, @Param("endTime") String endTime);
}




