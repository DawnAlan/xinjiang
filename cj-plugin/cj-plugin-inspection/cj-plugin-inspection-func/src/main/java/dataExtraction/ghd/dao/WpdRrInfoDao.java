package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdRrInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WpdRrInfoDao extends JpaRepository<WpdRrInfo, String> {

    @Query(value = "select w.* from wpd_rr_info w where w.p_id = ?1", nativeQuery = true)
    List<WpdRrInfo> findAllId(String id);

    @Query(value = "select w.* from wpd_rr_info w where w.id = ?1", nativeQuery = true)
    WpdRrInfo findId(String ndcdId);

    @Query(value = "select w.* from wpd_rr_info w where w.type_name = ?1 and w.name LIKE concat('%',?2,'%')", nativeQuery = true)
    List<WpdRrInfo> findAllData(String typeName,String infoName);

    @Query(value = "select w.* from wpd_rr_info w where w.type_name = ?1 and w.name not LIKE concat('%',?2,'%') and type_of = '0'", nativeQuery = true)
    List<WpdRrInfo> findAllDataNot(String typeName,String infoName);

    @Query(value = "select w.* from wpd_rr_info w where w.name LIKE concat('%',?1,'%')", nativeQuery = true)
    List<WpdRrInfo> findData(String infoName);

    @Query(value = "select w.* from wpd_rr_info w where w.type_name = ?1 and w.type_of = ?2", nativeQuery = true)
    List<WpdRrInfo> findAllInOf(String typeName, String typeOf);

    @Query(value = "select w.* from wpd_rr_info w where w.type_name = ?1 and w.type_of = ?2 and w.name not LIKE concat('%',?3,'%')", nativeQuery = true)
    List<WpdRrInfo> findLast(String typeName,String typeOf, String type);

    @Query(value = "select w.* from wpd_rr_info w where w.name not LIKE concat('%',?1,'%') and type_of = '0'", nativeQuery = true)
    List<WpdRrInfo> findAllNotName(String infoName);

    @Query(value = "select * from wpd_rr_info where id != ?1 and p_id != ?1 and name like CONCAT('%',?2,'%') ", nativeQuery = true)
    List<WpdRrInfo> findAllTree(String id,String name);

    @Query(value = "select * from wpd_rr_info where p_id = ?1 and name like CONCAT('%',?2,'%') ", nativeQuery = true)
    List<WpdRrInfo> findYlId(String id, String name);

    @Query(value = "select w.* from wpd_rr_info w where w.type_of = ?1 and w.name not LIKE concat('%',?2,'%')", nativeQuery = true)
    List<WpdRrInfo> findListAll(String typeOf, String type);
}
