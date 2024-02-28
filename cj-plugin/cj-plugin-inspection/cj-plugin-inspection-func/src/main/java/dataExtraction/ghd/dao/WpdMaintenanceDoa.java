package dataExtraction.ghd.dao;


import dataExtraction.ghd.entity.WpdMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WpdMaintenanceDoa extends JpaRepository<WpdMaintenance, String> {

    @Query(value = "select * from wpd_maintenance w where w.DEVID = ?1 ORDER BY TIME desc", nativeQuery = true)
    List<WpdMaintenance> findId(String devId);
}
