package dataExtraction.water.dao;

import dataExtraction.water.entity.LZZ_GAUGING_STATION;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LZZ_GAUGING_STATIONDao extends JpaRepository<LZZ_GAUGING_STATION,String> {
    @Query(value = "select * from LZZ_GAUGING_STATION where TREE_ID = ?1 ", nativeQuery = true)
    List<LZZ_GAUGING_STATION> findAllId(String id, String beginTime, String endTime);
}
