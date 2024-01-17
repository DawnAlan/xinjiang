package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 上游雨量日水情统计表(DayWaterSituationStatisticsTableSyyl)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 16:00:15
 */
public interface DayWaterSituationStatisticsTableSyylMapper extends BaseMapper<DayWaterSituationStatisticsTableSyyl> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_SYYL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableSyyl> selectList(@Param("date")String date);

    @Select("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_SYYL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Integer deleteByTime(@Param("date")String date);
}

