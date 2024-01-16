package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity.WaterSituationStatisticsTableToday;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 19:11:06
 */
public interface WaterSituationStatisticsTableTodayMapper extends BaseMapper<WaterSituationStatisticsTableToday> {

    @Select("select * from WATER_SITUATION_STATISTICS_TABLE_TODAY WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<WaterSituationStatisticsTableToday> selectList(@Param("date")String date);
}

