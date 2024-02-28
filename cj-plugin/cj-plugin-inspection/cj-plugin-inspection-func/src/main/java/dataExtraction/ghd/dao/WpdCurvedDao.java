package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdCurved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface WpdCurvedDao extends JpaRepository<WpdCurved,String> {

    @Query(value = "select w.* from wpd_curved w where w.id = ?1 ", nativeQuery = true)
    List<WpdCurved> findAllId(String daraId);

    @Query(value = "select * from wpd_curved w where w.id = ?1 order by w.v0", nativeQuery = true)
    List<WpdCurved> findByOrderv0(String dataId);

    @Query(value = "select w.* from wpd_curved w where w.id = ?1 and w.v0 = ?2 ", nativeQuery = true)
    WpdCurved findByOrderv(String dataId, double level);


    @Modifying
    @Transactional
    @Query(value = "delete from wpd_curved w where w.id = ?1 ", nativeQuery = true)
    void deleteIdAll(String dataId);
}
