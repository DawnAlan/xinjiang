package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对口率日水情统计表(DayWaterSituationStatisticsTableDkl)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:58:23
 */
public interface DayWaterSituationStatisticsTableDklMapper extends BaseMapper<DayWaterSituationStatisticsTableDkl> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_DKL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableDkl> selectList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_DKL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableDkl> selectList1(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_DKL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
        //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableDkl> selectInfoList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_DKL WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_DKL WHERE TABLE_HEAD_ID = #{tableHeadId} TIME = '昨日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between = #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableDkl> selectList2(@Param("tableHeadId")String tableHeadId,@Param("startTime")String startTime,@Param("endTime")String endTime);

}

