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

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id}")
    List<LzzGaugingStation> selectInfoByCondition1(@Param("id") String id);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id} AND TO_CHAR(GATHER_TIME,'YYYY-MM-DD') = #{time}")
    List<LzzGaugingStation> selectInfoByCondition2(@Param("id")String id, @Param("time")String time);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TREE_ID = #{id} AND TO_CHAR(GATHER_TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime}")
    List<LzzGaugingStation> selectInfoByCondition3(@Param("id")String id,@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("SELECT * FROM LZZ_GAUGING_STATION WHERE TO_CHAR(GATHER_TIME,'yyyy-MM-dd hh24:MI') = #{time} AND STATION_NAME = #{name}")
    LzzGaugingStation selectInfoByNameAndTime(@Param("time")String time, @Param("name")String name);

}

