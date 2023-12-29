package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 河西管理站日水情统计表(DayWaterSituationStatisticsTableHx)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:59:12
 */
public interface DayWaterSituationStatisticsTableHxMapper extends BaseMapper<DayWaterSituationStatisticsTableHx> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableHx> selectList(@Param("date")String date);
}

