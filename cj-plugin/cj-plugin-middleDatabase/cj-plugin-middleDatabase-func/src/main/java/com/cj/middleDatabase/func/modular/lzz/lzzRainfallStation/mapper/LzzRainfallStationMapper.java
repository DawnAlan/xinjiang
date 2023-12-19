package com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id}")
    List<LzzRainfallStation> selectInfoByCondition1(@Param("id") String id);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id} AND TO_CHAR(TIME,'YYYY-MM-DD') = #{time}")
    List<LzzRainfallStation> selectInfoByCondition2(@Param("id")String id, @Param("time")String time);

    @Select("SELECT * FROM LZZ_RAINFALL_STATION WHERE TREE_ID = #{id} AND TO_CHAR(TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime}")
    List<LzzRainfallStation> selectInfoByCondition3(@Param("id")String id,@Param("startTime")String startTime, @Param("endTime")String endTime);

}

