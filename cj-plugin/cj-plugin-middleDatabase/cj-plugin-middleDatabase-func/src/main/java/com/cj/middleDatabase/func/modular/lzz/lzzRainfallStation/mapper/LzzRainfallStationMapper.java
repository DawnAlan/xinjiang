package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @Select("SELECT STATION_NAME,ROUND(AVG(RAINFALL),2) as RAINFALL FROM LZZ_RAINFALL_STATION WHERE TO_CHAR(TIME,'YYYY-MM-DD hh24') BETWEEN #{startTime} AND #{endTime} GROUP BY STATION_NAME")
    List<RealTimeRainfallRes> getRealTimeRainfall(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE STATION_NAME = #{name} AND TO_CHAR(TIME,'YYYY-MM-DD hh24:MI') BETWEEN #{startTime} AND #{endTime}")
    List<LzzRainfallStation> selectHistoryList(@Param("name")String name, @Param("startTime")String startTime, @Param("endTime")String endTime);

}

