package com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 水位站数据表(LzzGaugingStation)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-05 17:55:45
 */
@DS("master")
public interface LzzGaugingStationMapper extends BaseMapper<LzzGaugingStation> {

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id} order by GATHER_TIME ASC")
    List<LzzGaugingStation> selectInfoByCondition1(@Param("id") String id);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id} AND TO_CHAR(GATHER_TIME,'YYYY-MM-DD') = #{time} order by GATHER_TIME ASC")
    List<LzzGaugingStation> selectInfoByCondition2(@Param("id")String id, @Param("time")String time);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id} AND TO_CHAR(GATHER_TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime} order by GATHER_TIME ASC")
    List<LzzGaugingStation> selectInfoByCondition3(@Param("id")String id,@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TO_CHAR(GATHER_TIME,'yyyy-MM-dd hh24:MI') = #{time} AND STATION_NAME = #{name}")
    LzzGaugingStation selectInfoByNameAndTime(@Param("time")String time, @Param("name")String name);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TO_CHAR(GATHER_TIME,'yyyy-MM-dd hh24') = #{time} AND STATION_NAME = #{name}")
    LzzGaugingStation selectInfoByTime(@Param("time")String time,@Param("name")String name);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE STATION_NAME = #{name} AND TO_CHAR(GATHER_TIME,'YYYY-MM-DD hh24') BETWEEN #{startTime} AND #{endTime}")
    List<LzzGaugingStation> selectHistoryList(@Param("name")String name, @Param("startTime")String startTime,  @Param("endTime")String endTime);

    @Select("select * from TTH.LZZ_GAUGING_STATION where (STATION_NAME, GATHER_TIME) in(\n" +
            "select STATION_NAME, max(GATHER_TIME) from TTH.LZZ_GAUGING_STATION where STATION_NAME in ('楼庄子出库水位站','楼庄子入库水位站','楼庄子库水位站')\n" +
            "and to_char(gather_time, 'yyyy-mm-dd') = #{dateTime}\n" +
            "group by STATION_NAME)")
    List<LzzGaugingStation> getCurrent(@Param("dateTime")String dateTime);
}

