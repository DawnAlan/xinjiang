package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.WpdDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceServiceDao extends JpaRepository<WpdDevice, String> {
    //@Query(value = "select * from wpd_device w where w.stcd = ?1 and if(?2 != '',w.devname=?2 or w.devcode=?2,1=1) ORDER BY ENDTIME desc", nativeQuery = true)
    /*@Query(value = "select * from wpd_device w where 1=1 " +
            "and if(?1 != '',w.devname LIKE concat('%',?1,'%') or w.devcode LIKE concat('%',?1,'%'),1=1) " +
            "and if(?2 != '',w.devtype LIKE concat('%',?2,'%') or w.devtype LIKE concat('%',?2,'%'),1=1) ORDER BY ENDTIME desc", nativeQuery = true)
    List<WpdDevice> findAllVal(String nameORcode,String devType);*/

    @Query(value = "select * from wpd_device w where 1=1 " +
            "and if(?1 != '',w.devname LIKE concat('%',?1,'%') or w.devtype LIKE concat('%',?1,'%'),1=1)" +
            " ORDER BY ENDTIME desc", nativeQuery = true)
    List<WpdDevice> findAllVal(String nameORcode);

    @Query(value = "select * from wpd_device w where 1=1 and w.devstate = ?1 and if(?2 != '',w.devname=?2 or w.devcode=?2,1=1) ORDER BY ENDTIME desc", nativeQuery = true)
    List<WpdDevice> findAllType(String type,String nameORcode);

    @Query(value = "select * from wpd_device w where w.devcode = ?1", nativeQuery = true)
    WpdDevice findAllCode(String code);
}
