package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WpdAttributeDao extends JpaRepository<WpdAttribute,String> {

    @Query(value = "select * from wpd_attribute w where w.NDCD = ?1 and w.DATACD = ?2", nativeQuery = true)
    WpdAttribute findMin(String ndcdId,String type);
}
