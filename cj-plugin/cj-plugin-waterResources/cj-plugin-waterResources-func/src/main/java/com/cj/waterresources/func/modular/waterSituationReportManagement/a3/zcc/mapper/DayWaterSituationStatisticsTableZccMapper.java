package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 制材厂日水情统计表(DayWaterSituationStatisticsTableZcc)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 16:01:31
 */
public interface DayWaterSituationStatisticsTableZccMapper extends BaseMapper<DayWaterSituationStatisticsTableZcc> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_ZCC WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableZcc> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_ZCC WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);
}

