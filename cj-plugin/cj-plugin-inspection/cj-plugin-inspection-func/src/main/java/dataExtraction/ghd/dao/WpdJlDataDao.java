package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdJlData;
import dataExtraction.ghd.entity.WpdJlDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WpdJlDataDao extends JpaRepository<WpdJlData, WpdJlDataId> {

    @Query(value = "select w.* from wpd_jl_data w where w.stcd = ?1 ORDER BY w.TM desc LIMIT 1", nativeQuery = true)
    WpdJlData fingMonLimit(String stcd);

    @Query(value = "select w.* from wpd_jl_data w where w.stcd = ?2 and DATE_FORMAT(?1,'%Y-%m-%d %H:%i') = DATE_FORMAT(w.tm,'%Y-%m-%d %H:%i')", nativeQuery = true)
    WpdJlData fingMon(String timeNew, String stcd);

    @Query(value = "select * from wpd_jl_data w where w.stcd = ?2 and DATE_FORMAT(?1,'%Y-%m-%d %H:%i') = DATE_FORMAT(w.tm,'%Y-%m-%d %H:%i') ORDER BY w.TM desc", nativeQuery = true)
    WpdJlData fingAll(String time2, String deviceId);

    @Query(value = "select w.* from wpd_jl_data w where w.stcd = ?1 " +
            "and DATE_FORMAT(w.tm,'%Y-%m-%d %H')>=DATE_FORMAT(?2,'%Y-%m-%d %H') " +
            "and DATE_FORMAT(w.tm,'%Y-%m-%d %H')<=DATE_FORMAT(?3,'%Y-%m-%d %H') ORDER BY w.TM ", nativeQuery = true)
    List<WpdJlData> fingList(String ndcdId, String beginTime, String endTime);

    @Query(value = "select w.* from wpd_jl_data w where w.stcd = ?2 and DATE_FORMAT(?1,'%Y-%m-%d %H:%i:%s') = DATE_FORMAT(w.tm,'%Y-%m-%d %H:%i:%s')", nativeQuery = true)
    WpdJlData fingMonS(String timeNew, String stcd);

    @Query(value = "select w.STCD,w.TM,TRUNCATE(sum(w.RZ)/count(w.STCD),3) as RZ,w.INQ,w.W,w.BLRZ,w.OTQ,w.RWCHRCD,w.RWPTN,w.INQDR," +
            "w.MSQMT,w.MINLEVEL,w.SQ_MONITOR_FLOW_RATE,w.SQ_TOTAL_FLOW,w.AVG_WATER_DEEP,w.AVG_FLOW," +
            "w.YESTERDAY_AVG_FLOW,w.WATER_DAILY,w.YESTERDAY_WATER_DAILY,w.YEAR_WATER_DAILY,w.VOLTAGE,w.SQ_CAPACITY from wpd_jl_data w where w.stcd = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m-%d %H') = DATE_FORMAT(w.tm,'%Y-%m-%d %H') " +
            "and w.rz is not null", nativeQuery = true)
    WpdJlData fingMonRzH(String timeNew, String stcd);

    @Query(value = "select w.STCD,w.TM,w.RZ,TRUNCATE(sum( w.INQ)/count(w.STCD),3) as INQ,w.W,w.BLRZ,w.OTQ,w.RWCHRCD,w.RWPTN,w.INQDR," +
            "w.MSQMT,w.MINLEVEL,w.SQ_MONITOR_FLOW_RATE,w.SQ_TOTAL_FLOW,w.AVG_WATER_DEEP,w.AVG_FLOW," +
            "w.YESTERDAY_AVG_FLOW,w.WATER_DAILY,w.YESTERDAY_WATER_DAILY,w.YEAR_WATER_DAILY,w.VOLTAGE,w.SQ_CAPACITY from wpd_jl_data w where w.stcd = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m-%d %H') = DATE_FORMAT(w.tm,'%Y-%m-%d %H') " +
            "and w.INQ is not null", nativeQuery = true)
    WpdJlData fingMonInqH(String timeNew, String stcd);

    @Query(value = "select w.STCD,w.TM,TRUNCATE(sum(w.RZ)/count(w.STCD),3) as RZ,w.INQ,w.W,w.BLRZ,w.OTQ,w.RWCHRCD,w.RWPTN,w.INQDR," +
            "w.MSQMT,w.MINLEVEL,w.SQ_MONITOR_FLOW_RATE,w.SQ_TOTAL_FLOW,w.AVG_WATER_DEEP,w.AVG_FLOW," +
            "w.YESTERDAY_AVG_FLOW,w.WATER_DAILY,w.YESTERDAY_WATER_DAILY,w.YEAR_WATER_DAILY,w.VOLTAGE,w.SQ_CAPACITY from wpd_jl_data w where w.stcd = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m-%d') = DATE_FORMAT(w.tm,'%Y-%m-%d') " +
            "and w.rz is not null", nativeQuery = true)
    WpdJlData fingMonRzD(String timeNew, String stcd);

    @Query(value = "select w.STCD,w.TM,w.RZ,TRUNCATE(sum( w.INQ)/count(w.STCD),3) as INQ,w.W,w.BLRZ,w.OTQ,w.RWCHRCD,w.RWPTN,w.INQDR," +
            "w.MSQMT,w.MINLEVEL,w.SQ_MONITOR_FLOW_RATE,w.SQ_TOTAL_FLOW,w.AVG_WATER_DEEP,w.AVG_FLOW," +
            "w.YESTERDAY_AVG_FLOW,w.WATER_DAILY,w.YESTERDAY_WATER_DAILY,w.YEAR_WATER_DAILY,w.VOLTAGE,w.SQ_CAPACITY from wpd_jl_data w where w.stcd = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m-%d') = DATE_FORMAT(w.tm,'%Y-%m-%d') " +
            "and w.INQ is not null", nativeQuery = true)
    WpdJlData fingMonInqD(String timeNew, String stcd);

    @Query(value = "select w.* from wpd_jl_data w where w.stcd = ?1 ORDER BY w.TM desc LIMIT 1", nativeQuery = true)
    WpdJlData findData(String id);
/*
    @Query(value = "select w.STCD," +
            "w.TM," +
            "IFNULL(w.RZ,0) as RZ," +
            "IFNULL(w.INQ,0) as INQ," +
            "IFNULL(w.W,0) as W," +
            "w.BLRZ,IFNULL(w.OTQ,0) as OTQ," +
            "w.RWCHRCD,w.RWPTN,w.INQDR,w.MSQMT,w.MINLEVEL from wpd_st_rsvr_r w where w.stcd = ?1 and w.tm>=?2 and w.tm<=?3 ORDER BY w.TM desc", nativeQuery = true)
    List<Map<String, Object>> fingListMap(String ndcdId, String beginTime, String endTime);
    List<WpdJlData> fingListMap(String ndcdId, String beginTime, String endTime);*/
}
