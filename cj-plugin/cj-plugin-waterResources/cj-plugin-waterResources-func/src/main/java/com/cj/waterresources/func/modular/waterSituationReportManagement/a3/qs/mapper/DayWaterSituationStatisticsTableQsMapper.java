package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 渠首管理站日水情统计表(DayWaterSituationStatisticsTableQs)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:59:55
 */
public interface DayWaterSituationStatisticsTableQsMapper extends BaseMapper<DayWaterSituationStatisticsTableQs> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableQs> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);
}

