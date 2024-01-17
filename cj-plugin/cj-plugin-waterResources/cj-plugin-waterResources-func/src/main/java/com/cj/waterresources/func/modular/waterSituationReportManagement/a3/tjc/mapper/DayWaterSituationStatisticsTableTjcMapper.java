package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.entity.DayWaterSituationStatisticsTableTjc;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 调节池日水情统计表(DayWaterSituationStatisticsTableTjc)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 16:00:34
 */
public interface DayWaterSituationStatisticsTableTjcMapper extends BaseMapper<DayWaterSituationStatisticsTableTjc> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TJC WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableTjc> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_TJC WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);
}

