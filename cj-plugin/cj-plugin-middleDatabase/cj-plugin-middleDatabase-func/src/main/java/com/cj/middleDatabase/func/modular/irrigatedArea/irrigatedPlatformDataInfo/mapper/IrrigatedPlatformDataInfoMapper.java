package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
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

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME DESC")
    List<IrrigatedPlatformDataInfo> selectOneByConditionByTime( @Param("time")String time);

    @Select("SELECT MONITOR_NAME,YQ_RAIN_FALL_ONE,YQ_RAIN_FALL_THREE,YQ_RAIN_FALL_SIX,YQ_RAIN_FALL_TWELVE,YQ_RAIN_FALL_TWENTY_FOUR FROM tth.IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME like CONCAT('%','雨量站') and TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24')= #{time} order by MONITOR_TIME desc limit 3")
    List<IrrigatedPlatformDataInfo> getRealTimeRainfall(@Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} ORDER BY MONITOR_TIME DESC")
    List<IrrigatedPlatformDataInfo> selectInfoByTime(@Param("time")String time,@Param("name") String name);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME like concat ('%',#{name},'%')  AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD hh24:MI') BETWEEN #{startTime} AND #{endTime}")
    List<IrrigatedPlatformDataInfo> selectHistoryList(@Param("name")String name, @Param("startTime")String startTime, @Param("endTime")String endTime);
    List<IrrigatedPlatformDataInfo> getRealTimeWaterLevel(@Param("time")String time,@Param("id") List<String> id,@Param("num") Integer num);

    @Select("select YESTERDAY_AVG_FLOW,MONITOR_NAME FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME desc limit 1")
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameListForHistory(@Param("name") String name,@Param("time")String time);

    @Select("select * from  tth.irrigated_platform_data_info where (MONITOR_NAME, MONITOR_TIME) in\n" +
            " (SELECT MONITOR_NAME, max(MONITOR_TIME)\n" +
            " FROM irrigated_platform_data_info \n" +
            " WHERE (monitor_time BETWEEN #{startTime} AND #{endTime} AND monitor_name IN ('头屯河水库水位','入库流量','出库流量'))\n" +
            " group by MONITOR_NAME)")
    List<IrrigatedPlatformDataInfo> getCurrentDate(@Param("startTime")String startTime, @Param("endTime")String endTime);
}

