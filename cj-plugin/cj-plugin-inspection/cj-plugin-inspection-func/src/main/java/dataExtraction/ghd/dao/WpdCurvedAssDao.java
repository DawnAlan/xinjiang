package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdCurvedAss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WpdCurvedAssDao extends JpaRepository<WpdCurvedAss,String> {

    @Query(value = "select w.* from wpd_curved_ass w where w.ndcd_id = ?1 and w.enable = 'true'", nativeQuery = true)
    WpdCurvedAss findAllId(String ndcdId);

    @Query(value = "select w.* from wpd_curved_ass w where w.ndcd_id = ?1 ORDER BY w.enable desc", nativeQuery = true)
    List<WpdCurvedAss> findIAll(String ndcdId);

    @Query(value = "select w.* from wpd_curved_ass w where w.id = ?1 ", nativeQuery = true)
    WpdCurvedAss findId(String id);
}
