package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import org.apache.ibatis.annotations.Delete;
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

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);
    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
    List<DayWaterSituationStatisticsTableHx> selectInfoList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
    List<DayWaterSituationStatisticsTableHx> selectInfoAfterDayList(@Param("date")String date);


    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TABLE_HEAD_ID = #{tableHeadId} and  TIME != '昨日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between = #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableHx> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HX WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableHx> selectListHave(@Param("time")String time, @Param("recordTime")String recordTime);
}

