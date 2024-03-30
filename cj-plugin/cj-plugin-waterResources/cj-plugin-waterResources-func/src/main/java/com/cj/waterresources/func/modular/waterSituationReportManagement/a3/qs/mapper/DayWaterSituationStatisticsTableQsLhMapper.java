package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (DayWaterSituationStatisticsTableQsLh)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-21 10:58:50
 */
public interface DayWaterSituationStatisticsTableQsLhMapper extends BaseMapper<DayWaterSituationStatisticsTableQsLh> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableQsLh> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
        //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableQsLh> selectInfoList(@Param("date")String date);
    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
        //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableQsLh> selectInfoAfterDayList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TABLE_HEAD_ID = #{tableHeadId} and TIME != '昨日均' and TIME != '今日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableQsLh> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableQsLh> selectListHave(@Param("time")String time, @Param("recordTime")String recordTime);

    @Select("select table_head_id,v,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_QS_LH WHERE RECORD_TIME = #{date} and TIME != '今日均' and TIME != '昨日均' limit #{num} order by time desc ")
    List<DayWaterSituationStatisticsTableQsLh> selectListForTodayWaterSituation(@Param("date")String date, @Param("num") Integer num);
}

