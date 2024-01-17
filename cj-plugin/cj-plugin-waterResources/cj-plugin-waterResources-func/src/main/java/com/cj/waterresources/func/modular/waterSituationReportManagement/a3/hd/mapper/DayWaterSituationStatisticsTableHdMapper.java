package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 河东管理站日水情统计表(DayWaterSituationStatisticsTableHd)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:58:47
 */
public interface DayWaterSituationStatisticsTableHdMapper extends BaseMapper<DayWaterSituationStatisticsTableHd> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableHd> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);
}

