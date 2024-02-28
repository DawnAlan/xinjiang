package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdStPptnR;
import dataExtraction.ghd.entity.WpdStPptnRId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年12月13日
 */
public interface WpdStPptnRDao extends JpaRepository<WpdStPptnR, WpdStPptnRId> {

    /**
     * 查询出所有雨量测站的编码stcd
     *
     * @return
     */
    @Query(value = "select w.id.stcd from WpdStPptnR w group by w.id.stcd")
    List<String> queryStcdGroupByStcd();

    /**
     * 根据stcd查询所有测站数据
     *
     * @param stcd
     * @return
     */
    @Query(value = "select w from WpdStPptnR w where w.id.stcd = :stcd")
    List<WpdStPptnR> queryByStcd(@Param("stcd") String stcd);

    /**
     * 根据stcd 查询时间范围内的监测数据
     *
     * @param stcd
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = "select w from WpdStPptnR w where w.id.stcd = :stcd and :beginTime <= w.id.tm " +
            "and :endTime >= w.id.tm order by w.id.tm asc")
    List<WpdStPptnR> queryMonitorDataByStcd(@Param("stcd") String stcd,
                                            @Param("beginTime") LocalDateTime beginTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 根据stcd 删除对应的测站所有数据
     *
     * @param stcd
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class, transactionManager = "ghdTransactionManagerPrimary")
    @Query(value = "delete from WpdStPptnR w where w.id.stcd = :stcd")
    Integer deleteByStcd(@Param("stcd") String stcd);


    /////

    /**
     * 根据STCD 查询对应分段时间的监测数据
     * @param time
     * @param ids
     * @return
     */
    @Query(value = "SELECT STCD," +
            "  SUM(CASE WHEN TM >= (?1) AND TM < DATE_ADD(DATE_FORMAT((?1), '%Y-%m-%d %H:00:00'), INTERVAL 1 HOUR) THEN DRP ELSE 0 END) AS total_drp1," +
            "  SUM(CASE WHEN TM >= (?1) AND TM < DATE_ADD(DATE_FORMAT((?1), '%Y-%m-%d %H:00:00'), INTERVAL 3 HOUR) THEN DRP ELSE 0 END) AS total_drp3," +
            "  SUM(CASE WHEN TM >= (?1) AND TM < DATE_ADD(DATE_FORMAT((?1), '%Y-%m-%d %H:00:00'), INTERVAL 6 HOUR) THEN DRP ELSE 0 END) AS total_drp6," +
            "  SUM(CASE WHEN TM >= (?1) AND TM < DATE_ADD(DATE_FORMAT((?1), '%Y-%m-%d %H:00:00'), INTERVAL 12 HOUR) THEN DRP ELSE 0 END) AS total_drp12," +
            "  SUM(CASE WHEN TM >= (?1) AND TM < DATE_ADD(DATE_FORMAT((?1), '%Y-%m-%d %H:00:00'), INTERVAL 24 HOUR) THEN DRP ELSE 0 END) AS total_drp24" +
            "  FROM st_pptn_r" +
            "  WHERE STCD IN (?2)" +
            "  GROUP BY STCD",nativeQuery = true)
    List<Object[]> queryMonitorSegmentedData(LocalDateTime time,List<String> ids);


//    @Query(value = "SELECT * FROM `st_pptn_r` where TM >= (?1) and TM <= (?2) and STCD in (?3)",nativeQuery = true)
//    List<WpdStPptnR> queryMonitorDataByStcdAndTime2(LocalDateTime beginTime, LocalDateTime endTime, List<String> ids);


    @Query(value = "SELECT STCD,TM,DRP FROM `st_pptn_r` where TM >= (?1) and TM <= (?2) and STCD in(?3)",nativeQuery = true)
    List<Object[]> queryMonitorDataByStcdAndTime(LocalDateTime beginTime,LocalDateTime endTime,List<String> ids);


    @Query(value = "SELECT * FROM `st_pptn_r` where TM >= (?1) and TM <= (?2) and STCD = (?3)",nativeQuery = true)
    List<WpdStPptnR> queryMonitorDataByStcdAndTime1(LocalDateTime beginTime, LocalDateTime endTime, String id);


    @Query(value = "SELECT * FROM `st_pptn_r` where TM >= (?1) and TM <= (?2) and STCD in (?3)",nativeQuery = true)
    List<Map<String,Object>> queryMonitorDataByStcdAndTime2(LocalDateTime beginTime, LocalDateTime endTime, List<String> ids);

    @Query(value = "SELECT * FROM wpd_st_pptn_r where TM = ?1 and STCD = ?2",nativeQuery = true)
    WpdStPptnR fingMon(String time2, String id);

    @Query(value = "SELECT SUM(DRP) as drp FROM wpd_st_pptn_r where DATE_FORMAT(?1,'%Y-%m-%d') = DATE_FORMAT(tm,'%Y-%m-%d') and STCD = ?2",nativeQuery = true)
    Double fingDaySum(String time, String id);

    @Query(value = "SELECT SUM(DRP) AS total_drp FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?2 and TM >= DATE_SUB(?1, INTERVAL 1 HOUR) AND TM < ?1 ",nativeQuery = true)
    Double queryDaySumData1(String time,String id);

    @Query(value = "SELECT SUM(DRP) AS total_drp FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?2 and TM >= DATE_SUB(?1, INTERVAL 3 HOUR) AND TM < ?1 ",nativeQuery = true)
    Double queryDaySumData3(String time,String id);

    @Query(value = "SELECT SUM(DRP) AS total_drp FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?2 and TM >= DATE_SUB(?1, INTERVAL 6 HOUR) AND TM < ?1 ",nativeQuery = true)
    Double queryDaySumData6(String time,String id);

    @Query(value = "SELECT SUM(DRP) AS total_drp FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?2 and TM >= DATE_SUB(?1, INTERVAL 12 HOUR) AND TM < ?1 ",nativeQuery = true)
    Double queryDaySumData12(String time,String id);

    @Query(value = "SELECT SUM(DRP) AS total_drp FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?3 and tm >= DATE_FORMAT(?1,'%Y-%m-%d') AND tm <= DATE_FORMAT(?2,'%Y-%m-%d')",nativeQuery = true)
    Double querySum(String beginTime,String endTime,String id);

    @Query(value = "SELECT max(DRP) as DRP,DATE_FORMAT(TM,'%Y-%m-%d %H:00:00') as TM FROM wpd_st_pptn_r" +
            "  WHERE STCD = ?3 and tm >= DATE_FORMAT(?1,'%Y-%m-%d') AND tm <= DATE_FORMAT(?2,'%Y-%m-%d')",nativeQuery = true)
    Map<String, Object> queryMaxData(String beginTime, String endTime, String id);

    @Query(value = "SELECT SUM(DRP) as drp FROM wpd_st_pptn_r where DATE_FORMAT(?1,'%Y-%m-%d') = DATE_FORMAT(tm,'%Y-%m-%d') and STCD = ?2",nativeQuery = true)
    Double fingYearDaySum(String time, String id);

    @Query(value = "SELECT SUM(DRP) as drp FROM wpd_st_pptn_r where DATE_FORMAT(?1,'%Y-%m') = DATE_FORMAT(tm,'%Y-%m') and STCD = ?2",nativeQuery = true)
    Double fingMonthSum(String time, String id);

    @Query(value = "SELECT SUM(DRP) as drp FROM wpd_st_pptn_r " +
            "where DATE_FORMAT(?1,'%m') = DATE_FORMAT(tm,'%m') and STCD = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m') > DATE_FORMAT(tm,'%Y-%m') ",nativeQuery = true)
    Double fingManyDrp(String time, String id);

    @Query(value = "SELECT tm FROM wpd_st_pptn_r " +
            "where DATE_FORMAT(?1,'%m') = DATE_FORMAT(tm,'%m') and STCD = ?2 " +
            "and DATE_FORMAT(?1,'%Y-%m') > DATE_FORMAT(tm,'%Y-%m') GROUP BY DATE_FORMAT(tm,'%Y')",nativeQuery = true)
    List<String> fingManyYearDrp(String time, String id);

    @Query(value = "SELECT SUM(DRP) as drp FROM wpd_st_pptn_r " +
            "where DATE_FORMAT(?1,'%Y') = DATE_FORMAT(tm,'%Y') and STCD = ?2",nativeQuery = true)
    Double fingYearDrp(String time, String id);

    @Query(value = "SELECT SUM(drp)as drp,DATE_FORMAT(TM,'%Y-%m-%d') as TM FROM wpd_st_pptn_r " +
            "where DATE_FORMAT(?1,'%Y') = DATE_FORMAT(tm,'%Y') and STCD = ?2 " +
            "GROUP BY DATE_FORMAT(TM,'%Y-%m-%d') ORDER BY DRP desc LIMIT 1",nativeQuery = true)
    Map<String, Object> queryYearMaxData(String time, String ndcdId);

    /*@Query(value = "SELECT SUM(DRP) as DRP,tm FROM wpd_st_pptn_r " +
            "where STCD = ?2 and DATE_FORMAT(?1,'%Y') = DATE_FORMAT(tm,'%Y') " +
            "GROUP BY DATE_FORMAT(tm,'%Y-%m-%d')",nativeQuery = true)
    List<Map<String,Object>> fingYearAllData(String time, String id);*/
}
