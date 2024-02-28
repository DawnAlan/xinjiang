package dataExtraction.ghd.dao;

import dataExtraction.ghd.entity.TthDeviceAbnormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TthDeviceAbnormalDao extends JpaRepository<TthDeviceAbnormal,String> {

    @Query(value = "select w.* from tth_device_abnormal w where w.devic_id = ?1 ", nativeQuery = true)
    TthDeviceAbnormal findNull(String id);

}
