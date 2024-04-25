package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.SelectListForIndustrialWaterFeeReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:59:33
 */
public interface DayWaterSituationStatisticsTableLzzMapper extends BaseMapper<DayWaterSituationStatisticsTableLzz> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableLzz> selectList(@Param("date")String date);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TABLE_HEAD_ID = #{req.headIds} and  (TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between  #{req.startTime} and #{req.endTime}) and  TIME = '今日均'")
    List<DayWaterSituationStatisticsTableLzz> selectListForIndustrialWaterFee(@Param("req") SelectListForIndustrialWaterFeeReq req);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
    //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableLzz> selectInfoList(@Param("date")String date);

    @Select("select ID, RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
        //@Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableLzz> selectInfoAfterDayList(@Param("date")String date);

    @Select("select ID, RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TABLE_HEAD_ID = #{tableHeadId} and TIME != '昨日均' and TIME != '今日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableLzz> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableLzz> selectListHave(@Param("time")String time, @Param("recordTime")String recordTime);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableLzz> selectReportForms(@Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select ID,table_head_id,v,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE RECORD_TIME = #{date} and TIME != '今日均' and TIME != '昨日均' limit #{num} order by time desc ")
    List<DayWaterSituationStatisticsTableLzz> selectListForTodayWaterSituation(@Param("date")String date, @Param("num") Integer num);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ where TIME = '今日均'")
    List<DayWaterSituationStatisticsTableLzz> selectAllListToday();

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TABLE_HEAD_ID = #{tableHeadId} and RECORD_TIME = #{time}  and TIME != '昨日均' and TIME != '今日均' order by time desc limit 1")
    DayWaterSituationStatisticsTableLzz selectListForIndex(@Param("time")String time, @Param("tableHeadId") String tableHeadId);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TABLE_HEAD_ID = #{tableHeadId} and RECORD_TIME between  #{startTime} and #{endTime}  and TIME = '08:00' order by RECORD_TIME asc")
    List<DayWaterSituationStatisticsTableLzz> selectReservoirHistoryList(@Param("startTime")String startTime, @Param("endTime")String endTime,@Param("tableHeadId") String tableHeadId);

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE RECORD_TIME = #{recordTime}  and TIME = '18:00'")
    List<DayWaterSituationStatisticsTableLzz> selectForApproval(@Param("recordTime")String recordTime);

    @Select("select END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE RECORD_TIME = #{time}  and TIME = '08:00' order by time desc limit 1")
    String selectEndTableList(@Param("time")String time);
}

