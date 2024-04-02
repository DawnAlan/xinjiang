package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * (LzzRainfallStation)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-05 17:56:37
 */
@DS("master")
public interface LzzRainfallStationMapper extends BaseMapper<LzzRainfallStation> {

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id} order by TIME ASC")
    List<LzzRainfallStation> selectInfoByCondition1(@Param("id") String id);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id} AND TO_CHAR(TIME,'YYYY-MM-DD') = #{time} order by TIME ASC")
    List<LzzRainfallStation> selectInfoByCondition2(@Param("id")String id, @Param("time")String time);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id} AND TO_CHAR(TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime} order by TIME ASC")
    List<LzzRainfallStation> selectInfoByCondition3(@Param("id")String id,@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE STATION_NAME = #{name} AND TO_CHAR(TIME,'YYYY-MM-DD') = #{time}")
    List<LzzRainfallStation> selectYesterday(@Param("name")String name, @Param("time")String time);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TO_CHAR(TIME,'YYYY-MM-DD') = #{time}")
    List<LzzRainfallStation> selectYesterday1(@Param("time")String time);

    @Select("SELECT STATION_NAME,ROUND(SUM(RAINFALL),2) as RAINFALL FROM LZZ_RAINFALL_STATION WHERE TO_CHAR(TIME,'YYYY-MM-DD hh24') BETWEEN #{startTime} AND #{endTime} GROUP BY STATION_NAME")
    List<RealTimeRainfallRes> getRealTimeRainfall(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT STATION_NAME, RAINFALL FROM LZZ_RAINFALL_STATION WHERE RECORD_TIME = #{date} limit #{num} order by time desc")
    List<RealTimeRainfallRes> getRealTimeRainfallByDate(@Param("date")String date,@Param("num")Integer num);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE STATION_NAME like concat('%',#{name},'%')  AND TO_CHAR(TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime}")
    List<LzzRainfallStation> selectHistoryList(@Param("name")String name, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from TTH.LZZ_RAINFALL_STATION where (TREE_ID,time) in\n" +
        "(select TREE_ID,max(TIME) from TTH.LZZ_RAINFALL_STATION where TREE_ID in (select id from TTH.LZZ_PLATFORM_TREE where name like '%雨量%')\n" +
        "and time <= to_date(#{dateTime},'yyyy-mm-dd hh24:mi:ss')\n" +
        "group by TREE_ID)")
    List<LzzRainfallStation> getRecentlyRainfalls(@Param("dateTime")String dateTime);
}

