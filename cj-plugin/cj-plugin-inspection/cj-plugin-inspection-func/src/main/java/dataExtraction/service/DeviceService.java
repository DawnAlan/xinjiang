package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.dao.DeviceServiceDao;
import dataExtraction.ghd.dao.TthDeviceAbnormalDao;
import dataExtraction.ghd.entity.TthDeviceAbnormal;
import dataExtraction.ghd.entity.WpdDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {

    @Autowired
    private DeviceServiceDao deviceServiceDao;
    @Autowired
    TthDeviceAbnormalDao tthDeviceAbnormalDao;

    public JSONObject queryDevice(String nameORcode) {
        JSONObject jsonObject = new JSONObject();
        List<WpdDevice> list = deviceServiceDao.findAllVal(nameORcode);
        jsonObject.put("allTop",list);
        //异常标识
        /*String type = "1";
        List<WpdDevice> listType = deviceServiceDao.findAllType(type,nameORcode);
        jsonObject.put("abnormalTop",listType);*/
        return jsonObject;
    }

    public Object save(WpdDevice wpdDevice) {

        String code = wpdDevice.getDevcode();

        WpdDevice map = deviceServiceDao.findAllCode(code);
        if (map==null){
            wpdDevice.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        }else {
            wpdDevice.setId(map.getId());
        }
        deviceServiceDao.save(wpdDevice);
        return wpdDevice;
    }

    public Object edit(WpdDevice wpdDevice) {
        deviceServiceDao.save(wpdDevice);
        return wpdDevice;
    }

    public Object deleteId(String id) {
        deviceServiceDao.deleteById(id);
        return id;
    }

    public JSONArray deviceAbnorma() {
        JSONArray jsonArray = new JSONArray();
        List<TthDeviceAbnormal> tthDeviceAbnormals = tthDeviceAbnormalDao.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TthDeviceAbnormal tth:tthDeviceAbnormals) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",tth.getId());
            jsonObject.put("name",tth.getName());
            jsonObject.put("deviceId",tth.getDeviceId());
            jsonObject.put("time",tth.getTime().format(formatter));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public JSONObject deviceAbnormaNumber() {
        List<TthDeviceAbnormal> tthDeviceAbnormals = tthDeviceAbnormalDao.findAll();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number",tthDeviceAbnormals.size());

        JSONArray jsonArray = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TthDeviceAbnormal tth:tthDeviceAbnormals) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",tth.getId());
            jsonObject1.put("name",tth.getName());
            jsonObject1.put("deviceId",tth.getDeviceId());
            jsonObject1.put("time",tth.getTime().format(formatter));
            jsonArray.add(jsonObject1);
        }
        jsonObject.put("data",jsonArray);
        return jsonObject;
    }
}
