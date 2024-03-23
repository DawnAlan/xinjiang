package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.SelectListForIndustrialWaterFeeReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 16:01:12
 */
public interface DayWaterSituationStatisticsTableTthMapper extends BaseMapper<DayWaterSituationStatisticsTableTth> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableTth> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TABLE_HEAD_ID = #{req.headId}  and  (TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{req.startTime} and #{req.endTime}) and TIME = '今日均'")
    List<DayWaterSituationStatisticsTableTth> selectListForIndustrialWaterFee(@Param("req") SelectListForIndustrialWaterFeeReq req);

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '昨日均'")
    List<DayWaterSituationStatisticsTableTth> selectInfoAfterDayList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date} and TIME = '今日均'")
    List<DayWaterSituationStatisticsTableTth> selectInfoList(@Param("date")String date);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TABLE_HEAD_ID = #{tableHeadId} and TIME != '昨日均' and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') between #{startTime} and #{endTime}")
    List<DayWaterSituationStatisticsTableTth> selectList2(@Param("tableHeadId")String tableHeadId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_TTH WHERE TIME = #{time} and  TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{recordTime}")
    List<DayWaterSituationStatisticsTableTth> selectListHave(@Param("recordTime")String recordTime, @Param("time")String time);
}

