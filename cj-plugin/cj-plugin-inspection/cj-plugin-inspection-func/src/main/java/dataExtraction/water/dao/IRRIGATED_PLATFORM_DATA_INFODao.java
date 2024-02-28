package dataExtraction.water.dao;

import dataExtraction.water.entity.IRRIGATED_PLATFORM_DATA_INFO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface IRRIGATED_PLATFORM_DATA_INFODao extends JpaRepository<IRRIGATED_PLATFORM_DATA_INFO,String> {

    @Query(value = "select * from IRRIGATED_PLATFORM_DATA_INFO where monitor_id = ?1 " +
            "and DATE_FORMAT(MONITOR_TIME,'%Y-%m-%d %H') >= DATE_FORMAT(?2,'%Y-%m-%d %H') " +
            "and DATE_FORMAT(MONITOR_TIME,'%Y-%m-%d %H') <= DATE_FORMAT(?3,'%Y-%m-%d %H')", nativeQuery = true)
    List<Map<String,Object>> findAllPid(String id, String beginTime, String endTime);

    @Query(value = "select * from IRRIGATED_PLATFORM_DATA_INFO ORDER BY MONITOR_TIME DESC LIMIT 0,1000", nativeQuery = true)
    List<IRRIGATED_PLATFORM_DATA_INFO> findAllData();

    @Query(value = "select * from IRRIGATED_PLATFORM_DATA_INFO where DATE_FORMAT(MONITOR_TIME,'%Y-%m-%d %H') >= DATE_FORMAT(?1,'%Y-%m-%d %H') " +
            "and DATE_FORMAT(MONITOR_TIME,'%Y-%m-%d %H') <= DATE_FORMAT(?2,'%Y-%m-%d %H') ORDER BY MONITOR_TIME desc", nativeQuery = true)
    List<Map<String,Object>> findAllData111(String beginTime, String endTime);
}
