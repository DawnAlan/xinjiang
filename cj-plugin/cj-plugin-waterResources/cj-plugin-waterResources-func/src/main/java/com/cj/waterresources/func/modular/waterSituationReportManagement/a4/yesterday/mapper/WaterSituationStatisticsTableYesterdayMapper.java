package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity.WaterSituationStatisticsTableYesterday;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 昨日水情日报表(WaterSituationStatisticsTableYesterday)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 19:10:46
 */
public interface WaterSituationStatisticsTableYesterdayMapper extends BaseMapper<WaterSituationStatisticsTableYesterday> {

    @Select("select * from WATER_SITUATION_STATISTICS_TABLE_YESTERDAY WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<WaterSituationStatisticsTableYesterday> selectList(@Param("date")String date);
}

