package dataExtraction.service;

import dataExtraction.ghd.dao.WpdMaintenanceDoa;
import dataExtraction.ghd.entity.WpdMaintenance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WpdMaintenanceService {

    @Autowired
    private WpdMaintenanceDoa wpdMaintenanceDoa;

    public Object queryEmbellish(String devId) {
        List<WpdMaintenance> list = wpdMaintenanceDoa.findId(devId);
        List<Map<String,Object>> listAll = new ArrayList<>();
        for (WpdMaintenance wpdMaintenance: list) {
            Map<String,Object> map = new LinkedHashMap<>();
            String time = wpdMaintenance.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            map.put(time,wpdMaintenance);
            listAll.add(map);
        }
        return listAll;
    }

    public Object save(WpdMaintenance wpdMaintenance) {
        wpdMaintenance.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        wpdMaintenanceDoa.save(wpdMaintenance);
        return wpdMaintenance;
    }

    public Object edit(WpdMaintenance wpdMaintenance) {
        wpdMaintenanceDoa.save(wpdMaintenance);
        return wpdMaintenance;
    }

    public Object deleteId(String id) {
        wpdMaintenanceDoa.deleteById(id);
        return id;
    }
}
