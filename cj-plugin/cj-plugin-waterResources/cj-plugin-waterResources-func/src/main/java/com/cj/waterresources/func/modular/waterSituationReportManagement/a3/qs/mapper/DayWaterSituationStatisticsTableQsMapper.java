package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
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

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
    //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableQs> selectInfoList(@Param("date")String date);
    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
        //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableQs> selectInfoAfterDayList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TABLE_HEAD_ID = #{tableHeadId} and TIME != '昨日均' and TIME != '今日均'  and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableQs> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD')  = #{recordTime}")
    List<DayWaterSituationStatisticsTableQs> selectListForLh(@Param("time")String time, @Param("recordTime")String recordTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableQs> selectListHave(@Param("time")String time, @Param("recordTime")String recordTime);

    @Select("select ID,table_head_id,v,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE RECORD_TIME = #{date} and TIME != '今日均' and TIME != '昨日均' limit #{num} order by time desc ")
    List<DayWaterSituationStatisticsTableQs> selectListForTodayWaterSituation(@Param("date")String date, @Param("num") Integer num);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_QS where TIME = '今日均'")
    List<DayWaterSituationStatisticsTableQs> selectAllListToday();

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE RECORD_TIME = #{recordTime}  and TIME = '18:00'")
    List<DayWaterSituationStatisticsTableQs> selectForApproval(@Param("recordTime")String recordTime);

    List<DayWaterSituationStatisticsTableQs> selectListByTime(@Param("waterLevelIds")List<String> waterLevelIds, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_QS WHERE RECORD_TIME = #{time}  and TIME = '08:00' order by time desc limit 1")
    String selectEndTableList(@Param("time")String time);
}

