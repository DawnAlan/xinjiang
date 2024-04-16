package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
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

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
    List<DayWaterSituationStatisticsTableHd> selectInfoList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
    List<DayWaterSituationStatisticsTableHd> selectInfoAfterDayList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TABLE_HEAD_ID = #{tableHeadId} and  TIME != '昨日均' and TIME != '今日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between  #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableHd> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableHd> selectListHave(@Param("time")String time, @Param("recordTime")String recordTime);

    @Select("select ID,table_head_id,v,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE RECORD_TIME = #{date} and TIME != '今日均' and TIME != '昨日均' limit #{num} order by time desc ")
    List<DayWaterSituationStatisticsTableHd> selectListForTodayWaterSituation(@Param("date")String date,@Param("num") Integer num);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_HD where TIME = '今日均'")
    List<DayWaterSituationStatisticsTableHd> selectAllListToday();

    @Select("select ID,RECORD_TIME,TIME,TABLE_HEAD_ID,V,END_TABLE_LIST from DAY_WATER_SITUATION_STATISTICS_TABLE_HD WHERE RECORD_TIME = #{recordTime}  and TIME = '08:00'")
    List<DayWaterSituationStatisticsTableHd> selectForApproval(@Param("recordTime")String recordTime);

}

