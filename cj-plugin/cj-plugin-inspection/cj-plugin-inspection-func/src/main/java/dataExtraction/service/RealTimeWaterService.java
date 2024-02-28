package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.dao.WpdAttributeDao;
import dataExtraction.ghd.dao.WpdJlDataDao;
import dataExtraction.ghd.dao.WpdRrInfoDao;
import dataExtraction.ghd.entity.WpdAttribute;
import dataExtraction.ghd.entity.WpdJlData;
import dataExtraction.ghd.entity.WpdRrInfo;
import dataExtraction.ghd.entity.WpdRunDataId;
import dataExtraction.response.ResultObject;
import dataExtraction.response.ResultState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RealTimeWaterService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WpdAttributeDao wpdAttributeDao;
    @Autowired
    BasicService basicService;
    @Autowired
    WpdJlDataDao wpdJlDataDao;
    @Autowired
    WpdRrInfoDao wpdRrInfoDao;

    public JSONArray queryRegionLevel(String typeName){
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String timeNew = sdfTime.format(c.getTime());
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllDataNot(typeName,infoName);
        for (WpdRrInfo wfo: wpdRrInfos) {
            String stcd = wfo.getId();
            String name = wfo.getName();
            //查询历史最低水位
            WpdJlData wpdJlDataRzH = wpdJlDataDao.fingMonRzH(timeNew,stcd);
            WpdJlData wpdJlDataInqH = wpdJlDataDao.fingMonInqH(timeNew,stcd);
            JSONObject jsonObjectH = new JSONObject();
            String type = "RD_RR_MINZ";
            WpdAttribute wpdAttribute = wpdAttributeDao.findMin(stcd,type);
            String typeFlow = "RD_RR_MINOUTQ";
            WpdAttribute wpdAttributeFlow = wpdAttributeDao.findMin(stcd,typeFlow);
            double minlevel = -9999999;
            double minFlow = -9999999;
            if (wpdAttribute!=null){
                minlevel = wpdAttribute.getTmv();
            }
            if (wpdAttributeFlow!=null){
                minFlow = wpdAttributeFlow.getTmv();
            }
            jsonObjectH.put("minLevel",minlevel);
            jsonObjectH.put("minFlow",minFlow);
            jsonObjectH.put("id",stcd);
            jsonObjectH.put("name",name);
            jsonObjectH.put("time",timeNew);
            if (wpdJlDataRzH!=null){
                jsonObjectH.put("currentLevel",wpdJlDataRzH.getRz());
            }else {
                jsonObjectH.put("currentLevel",-9999999);
            }
            if (wpdJlDataInqH!=null){
                jsonObjectH.put("currentFlow",wpdJlDataInqH.getInq());
            }else {
                jsonObjectH.put("currentFlow",-9999999);
            }
            jsonArray.add(jsonObjectH);
        }
     return jsonArray;
    }

    public ResultObject save(JSONObject jsonObject) {
        ResultObject resultObject = new ResultObject();
        String type = "";
        if(Double.valueOf(jsonObject.getString("minflow"))!=-9999999 && !Double.valueOf(jsonObject.getString("minflow")).equals("")){
            type = "RD_RR_MINOUTQ";
            WpdAttribute wpdAttribute = new WpdAttribute();
            WpdRunDataId wpdRunDataId = new WpdRunDataId();
            wpdRunDataId.setNdcd(jsonObject.getString("ndcdId"));
            wpdRunDataId.setTm(LocalDateTime.parse("2024-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            wpdRunDataId.setDatacd(type);
            wpdAttribute.setId(wpdRunDataId);
            wpdAttribute.setTmv(Double.valueOf(jsonObject.getString("minflow")));
            wpdAttributeDao.save(wpdAttribute);
        }
        if(Double.valueOf(jsonObject.getString("minlevel"))!=-9999999 && !Double.valueOf(jsonObject.getString("minlevel")).equals("")){
            type = "RD_RR_MINZ";
            WpdAttribute wpdAttribute = new WpdAttribute();
            WpdRunDataId wpdRunDataId = new WpdRunDataId();
            wpdRunDataId.setNdcd(jsonObject.getString("ndcdId"));
            wpdRunDataId.setTm(LocalDateTime.parse("2024-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            wpdRunDataId.setDatacd(type);
            wpdAttribute.setId(wpdRunDataId);
            wpdAttribute.setTmv(Double.valueOf(jsonObject.getString("minlevel")));
            wpdAttributeDao.save(wpdAttribute);
        }

        resultObject.setState(ResultState.SUCCESS);
        resultObject.setMessage("保存成功");
        return resultObject;
    }

    public JSONArray queryRREarlyl(String typeName) {
        JSONArray jsonArray = new JSONArray();
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllDataNot(typeName,infoName);
        String type = "RD_RR_MINZ";
        String typeFlow = "RD_RR_MINOUTQ";
        for (WpdRrInfo wfo: wpdRrInfos) {
            JSONObject jsonObject = new JSONObject();
            String stcd = wfo.getId();
            String name = wfo.getName();
            //最低水位
            WpdAttribute wpdAttribute = wpdAttributeDao.findMin(stcd,type);
            WpdAttribute wpdAttributeFlow = wpdAttributeDao.findMin(stcd,typeFlow);
            int s = 0;
            if (wpdAttribute!=null){
                double minlevel = wpdAttribute.getTmv();

                WpdJlData wpdJlData = wpdJlDataDao.fingMonLimit(stcd);
                jsonObject.put("id",stcd);
                jsonObject.put("name",name);
                if (wpdJlData!=null && wpdJlData.getRz()!=null && wpdJlData.getRz()<=minlevel){
                    jsonObject.put("currentLevel",wpdJlData.getRz());
                    jsonObject.put("currentFlow",null);
                    s = s+1;
                }
            }
            if (wpdAttributeFlow!=null){
                double minFlow = wpdAttributeFlow.getTmv();

                WpdJlData wpdJlData = wpdJlDataDao.fingMonLimit(stcd);
                jsonObject.put("id",stcd);
                jsonObject.put("name",name);
                if (wpdJlData!=null && wpdJlData.getInq()!=null && wpdJlData.getInq()<=minFlow){
                    if (jsonObject.get("currentLevel")==null){
                        jsonObject.put("currentLevel",null);
                    }
                    jsonObject.put("currentFlow",wpdJlData.getInq());
                    s = s+1;
                }
            }
            if (s>0){
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    public JSONObject queryForeignEarlyl() {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String type = "RD_RR_MINZ";
        String typeFlow = "RD_RR_MINOUTQ";
        String infoName = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findAllNotName(infoName);
        for (WpdRrInfo wfo: wpdRrInfos) {
            JSONObject jsonObject = new JSONObject();
            String stcd = wfo.getId();
            String name = wfo.getName();
            //最低水位
            WpdAttribute wpdAttribute = wpdAttributeDao.findMin(stcd,type);
            WpdAttribute wpdAttributeFlow = wpdAttributeDao.findMin(stcd,typeFlow);
            int s = 0;
            if (wpdAttribute!=null){
                double minlevel = wpdAttribute.getTmv();

                WpdJlData wpdJlData = wpdJlDataDao.fingMonLimit(stcd);
                jsonObject.put("id",stcd);
                jsonObject.put("name",name);
                if (wpdJlData!=null && wpdJlData.getRz()!=null && wpdJlData.getRz()<=minlevel){
                    jsonObject.put("currentLevel",wpdJlData.getRz());
                    jsonObject.put("currentFlow",null);
                    s = s+1;
                }
            }
            if (wpdAttributeFlow!=null){
                double minFlow = wpdAttributeFlow.getTmv();

                WpdJlData wpdJlData = wpdJlDataDao.fingMonLimit(stcd);
                jsonObject.put("id",stcd);
                jsonObject.put("name",name);
                if (wpdJlData!=null && wpdJlData.getInq()!=null && wpdJlData.getInq()<=minFlow){
                    if (jsonObject.get("currentLevel")==null){
                        jsonObject.put("currentLevel",null);
                    }
                    jsonObject.put("currentFlow",wpdJlData.getInq());
                    s = s+1;
                }
            }
            if (s>0){
                jsonArray.add(jsonObject);
            }
        }
        json.put("value",jsonArray);
        json.put("size",jsonArray.size());
        return json;
    }
}
