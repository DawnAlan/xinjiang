package dataExtraction.water.dao;

import dataExtraction.water.entity.LZZ_RAINFALL_STATION;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LZZ_RAINFALL_STATIONDao extends JpaRepository<LZZ_RAINFALL_STATION,String> {

    @Query(value = "select * from LZZ_RAINFALL_STATION where TREE_ID = ?1 " +
            "and DATE_FORMAT(time,'%Y-%m-%d %H') <= DATE_FORMAT(?2,'%Y-%m-%d %H') " +
            "and DATE_FORMAT(time,'%Y-%m-%d %H') <= DATE_FORMAT(?3,'%Y-%m-%d %H')", nativeQuery = true)
    List<LZZ_RAINFALL_STATION> findAllData(String pId, String beginTime, String endTime);

}
