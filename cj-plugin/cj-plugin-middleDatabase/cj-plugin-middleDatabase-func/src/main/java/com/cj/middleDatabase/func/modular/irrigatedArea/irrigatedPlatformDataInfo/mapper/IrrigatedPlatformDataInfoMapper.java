package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-06 19:26:06
 */
public interface IrrigatedPlatformDataInfoMapper extends BaseMapper<IrrigatedPlatformDataInfo> {

    @Select("select YESTERDAY_AVG_FLOW,MONITOR_NAME FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} order by MONITOR_TIME desc limit 1")
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameList(@Param("name") String name);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime} order by MONITOR_TIME ASC")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition1(@Param("id") String id, @Param("startTime") String startTime,@Param("endTime") String endTime);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME ASC")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition2(@Param("id") String id, @Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id} order by MONITOR_TIME ASC")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition3(@Param("id") String id);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME DESC limit 1")
    IrrigatedPlatformDataInfo selectOneByCondition(@Param("name") String name, @Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24:MI') = #{time} order by MONITOR_TIME DESC limit 1")
    IrrigatedPlatformDataInfo selectOneByCondition1(@Param("name") String name, @Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24:MI') = #{time} order by MONITOR_TIME DESC limit 1")
    List<IrrigatedPlatformDataInfo> selectOneByConditionNotName( @Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME DESC")
    List<IrrigatedPlatformDataInfo> selectOneByCondition2(@Param("name") String name, @Param("time")String time);

    @Select("SELECT MONITOR_NAME as stationName,ROUND(AVG(YQ_RAIN_FALL_ONE),2) as RAINFALL FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME like CONCAT('%','雨量站') and (TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24') BETWEEN #{startTime} AND #{endTime} ) GROUP BY MONITOR_NAME")
    List<RealTimeRainfallRes> getRealTimeRainfall(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24') = #{time} ORDER BY MONITOR_TIME DESC")
    List<IrrigatedPlatformDataInfo> selectInfoByTime(@Param("time")String time,@Param("name") String name);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24:MI') BETWEEN #{startTime} AND #{endTime}")
    List<IrrigatedPlatformDataInfo> selectHistoryList(@Param("name")String name, @Param("startTime")String startTime, @Param("endTime")String endTime);
    List<IrrigatedPlatformDataInfo> getRealTimeWaterLevel(@Param("time")String time,@Param("id") List<String> id,@Param("num") Integer num);

    @Select("select YESTERDAY_AVG_FLOW,MONITOR_NAME FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME desc limit 1")
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameListForHistory(@Param("name") String name,@Param("time")String time);
}

