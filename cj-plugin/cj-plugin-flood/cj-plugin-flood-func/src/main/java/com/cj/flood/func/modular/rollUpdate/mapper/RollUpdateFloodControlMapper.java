package com.cj.flood.func.modular.rollUpdate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateFloodControl;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableTth;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * (RollUpdateFloodControl)表数据库访问层
 *
 * @author makejava
 * @since 2024-07-19 14:59:38
 */
public interface RollUpdateFloodControlMapper extends BaseMapper<RollUpdateFloodControl> {
    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TABLE_HEAD_ID = #{tableHeadId} and RECORD_TIME = #{time}  and TIME != '昨日均' and TIME != '今日均' order by time desc limit 1")
    DayWaterSituationStatisticsTableLzz selectListForIndexLzz(@Param("time")String time, @Param("tableHeadId") String tableHeadId);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TABLE_HEAD_ID = #{tableHeadId} and RECORD_TIME = #{time}  and TIME != '昨日均' and TIME != '今日均' order by time desc limit 1")
    DayWaterSituationStatisticsTableTth selectListForIndexTth(@Param("time")String time, @Param("tableHeadId") String tableHeadId);
}

