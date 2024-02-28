package dataExtraction.water.dao;

import dataExtraction.water.entity.LZZ_PLATFORM_TREE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LZZPLATFORMTREEDao extends JpaRepository<LZZ_PLATFORM_TREE, String> {

    @Query(value = "select * from LZZ_PLATFORM_TREE where p_id = ?1", nativeQuery = true)
    List<LZZ_PLATFORM_TREE> findAll(String type);
}
