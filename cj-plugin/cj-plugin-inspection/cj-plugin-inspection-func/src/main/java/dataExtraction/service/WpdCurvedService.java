package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.dao.WpdCurvedAssDao;
import dataExtraction.ghd.dao.WpdCurvedDao;
import dataExtraction.ghd.entity.WpdCurved;
import dataExtraction.ghd.entity.WpdCurvedAss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WpdCurvedService {

    @Autowired
    WpdCurvedAssDao wpdCurvedAssDao;
    @Autowired
    WpdCurvedDao wpdCurvedDao;

    public WpdCurvedAss addCurve(JSONObject jsonObject) {
        WpdCurvedAss wpdCurvedAss = new WpdCurvedAss();
        wpdCurvedAss.setName(jsonObject.getString("name"));
        wpdCurvedAss.setNdcdId(jsonObject.getString("ndcdId"));
        String id = UUID.randomUUID().toString().replace("-","");
        if (jsonObject.get("id")!=null && !jsonObject.get("id").equals("")){
            id = jsonObject.get("id").toString();
        }
        String dataId = UUID.randomUUID().toString().replace("-","");
        if (jsonObject.get("dataId")!=null && !jsonObject.get("dataId").equals("")){
            dataId = jsonObject.get("dataId").toString();
        }
        String pd = "false";
        if (jsonObject.get("enable")!=null && !jsonObject.get("enable").equals("")){
            pd = jsonObject.get("enable").toString();
        }

        WpdCurvedAss wpdCurvedAsses = wpdCurvedAssDao.findAllId(jsonObject.getString("ndcdId"));
        if (wpdCurvedAsses==null){
            pd = "true";
        }

        wpdCurvedAss.setDataId(dataId);
        wpdCurvedAss.setId(id);
        wpdCurvedAss.setEnable(pd);
        wpdCurvedAss.setTime(LocalDateTime.now());
        wpdCurvedDao.deleteIdAll(dataId);
        wpdCurvedAssDao.save(wpdCurvedAss);
        if (jsonObject.get("data")!=null && !jsonObject.get("data").equals("")){
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                WpdCurved wpdCurved = new WpdCurved();
                wpdCurved.setId(dataId);
                wpdCurved.setV0(json.getDouble("v0"));
                wpdCurved.setV1(json.getDouble("v1"));
                wpdCurvedDao.saveAndFlush(wpdCurved);
            }
        }
        return wpdCurvedAss;
    }

    public WpdCurvedAss modify(String id, String ndcdId) {
        WpdCurvedAss wpdCurvedAsses = wpdCurvedAssDao.findAllId(ndcdId);
        wpdCurvedAsses.setEnable("false");
        wpdCurvedAssDao.save(wpdCurvedAsses);
        WpdCurvedAss wpdCuAss = wpdCurvedAssDao.findId(id);
        wpdCuAss.setEnable("true");
        wpdCurvedAssDao.save(wpdCuAss);
        return wpdCuAss;
    }

    public JSONObject queryQuXT(String id) {
        JSONObject jsonObject = new JSONObject();
        WpdCurvedAss wpdCurvedAsses = wpdCurvedAssDao.findId(id);
        List<WpdCurved> wpdCurveds = wpdCurvedDao.findAllId(wpdCurvedAsses.getDataId());
        List<Object> listLevel = new ArrayList<>();
        List<Object> listKr = new ArrayList<>();
        for (WpdCurved wpdC: wpdCurveds) {
            listLevel.add(wpdC.getV0());
            listKr.add(wpdC.getV1());
        }
        jsonObject.put("level",listLevel);
        jsonObject.put("flow",listKr);
        jsonObject.put("tab",wpdCurveds);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("id",wpdCurvedAsses.getId());
        jsonObject1.put("name",wpdCurvedAsses.getName());
        jsonObject.put("drop",jsonObject1);

        return jsonObject;
    }

    public JSONArray dropDown(String ndcdId) {
        JSONArray jsonArray = new JSONArray();
        List<WpdCurvedAss> wpdCurvedAsses = wpdCurvedAssDao.findIAll(ndcdId);
        for (WpdCurvedAss wpc: wpdCurvedAsses) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",wpc.getId());
            jsonObject.put("name",wpc.getName());
            jsonObject.put("ndcdId",wpc.getNdcdId());
            jsonObject.put("enable",wpc.getEnable());
            jsonObject.put("dataId",wpc.getDataId());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public JSONObject regimenShowQS(String ndcdId) {
        JSONObject jsonObject = new JSONObject();
        WpdCurvedAss wpdCurvedAsses = wpdCurvedAssDao.findAllId(ndcdId);
        List<WpdCurved> wpdCurveds = wpdCurvedDao.findAllId(wpdCurvedAsses.getDataId());
        List<Object> listLevel = new ArrayList<>();
        List<Object> listKr = new ArrayList<>();
        for (WpdCurved wpdC: wpdCurveds) {
            listLevel.add(wpdC.getV0());
            listKr.add(wpdC.getV1());
        }
        jsonObject.put("level",listLevel);
        jsonObject.put("flow",listKr);
        jsonObject.put("tab",wpdCurveds);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("id",wpdCurvedAsses.getId());
        jsonObject1.put("name",wpdCurvedAsses.getName());
        jsonObject.put("drop",jsonObject1);

        return jsonObject;

    }

    public BigDecimal queryLevelFlow(String ndcdId, double level) {

        BigDecimal xyNew = new BigDecimal(0).setScale(3,BigDecimal.ROUND_HALF_UP);
        BigDecimal z = new BigDecimal(level);
            WpdCurvedAss wpdCurvedAss = wpdCurvedAssDao.findAllId(ndcdId);
            List<WpdCurved> list = wpdCurvedDao.findByOrderv0(wpdCurvedAss.getDataId());
            WpdCurved lists = wpdCurvedDao.findByOrderv(wpdCurvedAss.getDataId(),level);
            if (lists!=null){
                xyNew = new BigDecimal(lists.getV1()).setScale(3,BigDecimal.ROUND_HALF_UP);
                return xyNew;
            }
            boolean typeY = true;
            boolean typeZ = true;
            for (int i = 0; i < list.size(); i++) {
                WpdCurved wpdCurved = list.get(i);
                if (wpdCurved.getV0()>level){
                    if (i==0){
                        typeY = false;
                        break;
                    }
                    WpdCurved wpdCurved_1 = list.get(i-1);
                    //公式 x = yarr[i-1]+(xaxle-xarr[i-1])/{(xarr[i-2]-xarr[i-1])/(yarr[i-2]-yarr[i-1])}
                    xyNew = new BigDecimal(wpdCurved_1.getV1()).add(z.subtract(new BigDecimal(wpdCurved_1.getV0())).divide(new BigDecimal(wpdCurved.getV0()).subtract(new BigDecimal(wpdCurved_1.getV0())).divide(new BigDecimal(wpdCurved.getV1()).subtract(new BigDecimal(wpdCurved_1.getV1())),3, RoundingMode.HALF_UP),3, RoundingMode.HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP);
                    typeY = false;
                    typeZ = false;
                    break;
                }
            }
            //右极限
            if (typeY){
                //公式 x =yarr[yarr.length-1]+(xaxle-xarr[xarr.length-1])/{(xarr[xarr.length-1]-xarr[xarr.length-2])/(yarr[yarr.length-1]-yarr[yarr.length-2])}
                xyNew = new BigDecimal(list.get(list.size()-1).getV1()).add(z.subtract(new BigDecimal(list.get(list.size()-1).getV0())).multiply(new BigDecimal(list.get(list.size()-1).getV1()).subtract(new BigDecimal(list.get(list.size()-2).getV1()))).divide(new BigDecimal(list.get(list.size()-1).getV0()).subtract(new BigDecimal(list.get(list.size()-2).getV0())),3, RoundingMode.HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP);
                typeZ = false;
            }
            //左
            if (typeZ){
                //公式 x = yarr[0]-(xarr[0]-xaxle)/{(xarr[1]-xarr[0])/(yarr[1]-yarr[0])}
                xyNew = new BigDecimal(list.get(0).getV1()).subtract(new BigDecimal(list.get(0).getV0()).subtract(z).multiply(new BigDecimal(list.get(1).getV1()).subtract(new BigDecimal(list.get(0).getV1()))).divide(new BigDecimal(list.get(1).getV0()).subtract(new BigDecimal(list.get(0).getV0())),3, RoundingMode.HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP);
            }
            return xyNew;
    }
}
