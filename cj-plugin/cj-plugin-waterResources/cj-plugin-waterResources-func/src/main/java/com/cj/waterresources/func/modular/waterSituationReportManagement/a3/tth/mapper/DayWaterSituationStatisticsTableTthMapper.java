package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 16:01:12
 */
public interface DayWaterSituationStatisticsTableTthMapper extends BaseMapper<DayWaterSituationStatisticsTableTth> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableTth> selectList(@Param("date")String date);

    @Select("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Integer deleteByTime(@Param("date")String date);
}

