package dataExtraction.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dataExtraction.ghd.dao.WpdCurvedAssDao;
import dataExtraction.ghd.dao.WpdCurvedDao;
import dataExtraction.ghd.dao.WpdJlDataDao;
import dataExtraction.ghd.dao.WpdRrInfoDao;
import dataExtraction.ghd.entity.WpdJlData;
import dataExtraction.ghd.entity.WpdRrInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class RegimenShowService {

    @Autowired
    DataShowService dataShowService;
    @Autowired
    WpdJlDataDao wpdJlDataDao;
    @Autowired
    WpdRrInfoDao wpdRrInfoDao;
    @Autowired
    WpdCurvedAssDao wpdCurvedAssDao;
    @Autowired
    WpdCurvedDao wpdCurvedDao;

    public List<Map<String,Object>> queryDeviceList(String typeName, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
        String typeOf = "0";
        String name = "雨量站";
        List<WpdRrInfo> wpdRrInfos = wpdRrInfoDao.findLast(typeName,typeOf,name);

        List<Map<String,Object>> wpdJlDataNew = new ArrayList<>();
        for (WpdRrInfo wfo: wpdRrInfos) {
            Map<String,Object> mapNew = new LinkedHashMap<>();
            WpdJlData wpdJlData = wpdJlDataDao.fingMonLimit(wfo.getId());
            if (wpdJlData!=null){
                if (wpdJlData!=null){
                    mapNew.put("id",wfo.getId());
                    mapNew.put("name",wfo.getName());
                    mapNew.put("tm",wpdJlData.getId().getTm().format(formatter));
                    if (wpdJlData.getRz()!=null && wpdJlData.getRz()>0){
                        mapNew.put("rz",wpdJlData.getRz());
                    }else {
                        mapNew.put("rz",-9999999);
                    }
                    if (wpdJlData.getInq()!=null && wpdJlData.getInq()>0){
                        mapNew.put("inq",wpdJlData.getInq());
                    }else {
                        mapNew.put("inq",-9999999);
                    }
                    if (wpdJlData.getSqTotalFlow()!=null && wpdJlData.getSqTotalFlow()>0){
                        mapNew.put("otq",wpdJlData.getSqTotalFlow());
                    }else {
                        mapNew.put("otq",-9999999);
                    }
                    if (wpdJlData.getW()!=null && wpdJlData.getW()>0){
                        mapNew.put("w",wpdJlData.getW());
                    }else {
                        mapNew.put("w",-9999999);
                    }
                    wpdJlDataNew.add(mapNew);
                }
            }
        }
        return wpdJlDataNew;
    }

    public Map<String, Object> queryLevel(String deviceId, String beginTime, String endTime) {
        JSONArray listTm = new JSONArray();
        JSONArray listRZ = new JSONArray();
        JSONArray listINQ = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<WpdJlData> wpdJlData = wpdJlDataDao.fingList(deviceId,beginTime,endTime);
        for (WpdJlData wpdData: wpdJlData) {
            String dateTimeStr = wpdData.getId().getTm().format(formatter);
            listTm.add(dateTimeStr);
            if(wpdJlData==null){
                listRZ.add(-9999999);
                listINQ.add(-9999999);
                //listTgtq.add(-9999999);
                continue;
            }
            if (wpdData.getRz()!=null){
                listRZ.add(wpdData.getRz());
            }else{
                listRZ.add(-9999999);
            }
            if (wpdData.getInq()!=null){
                listINQ.add(wpdData.getInq());
            }else{
                listINQ.add(-9999999);
            }
        }

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("tm",listTm);
        map.put("z",listRZ);
        map.put("sq",listINQ);
        //map.put("tgtq",listTgtq);
        return map;
    }

    public JSONObject historyQueryRain(String ndcdId, String beginTime, String endTime,String type,String timeType) throws ParseException {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        JSONObject jsonObject = new JSONObject();
        Integer LinTime = 0;
        if (timeType.equals("date")){
            LinTime = dataShowService.day(beginTime,endTime)+1;
        }
        if (timeType.equals("hour")){
            LinTime = dataShowService.hour(beginTime,endTime)+1;
        }
        List<String> listTime = new ArrayList<>();
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < LinTime; i++) {
            Date dts = sdfTime.parse(beginTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dts);
            if (timeType.equals("date")){
                cal.add(Calendar.DATE, +i);
            }
            if (timeType.equals("hour")){
                cal.add(Calendar.HOUR, +i);
            }
            String ollTime = sdfTime.format(cal.getTime());//时间
            Date dtsoll= sdfTime.parse(ollTime);
            String yTime = String.format("%tY", dtsoll);
            String mTime = String.format("%tm", dtsoll);
            String dTime = String.format("%td", dtsoll);
            String timeNew = yTime+"-"+mTime+"-"+dTime+" 08:00:00";
            if (timeType.equals("hour")){
                timeNew = ollTime;
            }
            WpdJlData wpdJlData = wpdJlDataDao.fingMon(timeNew,ndcdId);
            listTime.add(timeNew);
            list.add(-9999999);
            if (wpdJlData==null){
                list.set(i,-9999999);
                continue;
            }

            if (type.equals("level") && wpdJlData.getRz()!=null){//水位
                list.set(i,wpdJlData.getRz());
            }
            if(type.equals("inFlow") && wpdJlData.getInq()!=null){//入库流量
                list.set(i,wpdJlData.getInq());
            }
            if(type.equals("outFlow") && wpdJlData.getOtq()!=null){//出库流量
                list.set(i,wpdJlData.getOtq());
            }
        }
        Date dtsNew = sdfTime.parse(endTime);
        Integer YtimeNew =  Integer.parseInt(String.format("%tY", dtsNew));
        jsonObject.put(String.valueOf(YtimeNew),list);
        jsonObject.put("time",listTime);
        return jsonObject;
    }

    public JSONObject historyQueryRainLn(String ndcdId, String beginTime, String endTime, String time, String type, String timeType) throws ParseException{
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        JSONObject jsonObject = new JSONObject();
        List<String> listTimeH = new ArrayList<>();
        //判断是否为跨年
        Date hDateB = sdfTime.parse(beginTime);
        Date hDateE = sdfTime.parse(endTime);
        Integer HiTimeB = Integer.parseInt(String.format("%tY", hDateB));
        Integer HiTimeE = Integer.parseInt(String.format("%tY", hDateE));
        Integer H = HiTimeE-HiTimeB;
        //求历史时刻数据
        List<Map<String,Object>> listTime = new LinkedList<>();
        String[] arr = time.split(",");
        if (!arr[0].equals("")){
            for (int i = 0; i < arr.length; i++) {
                Map<String,Object> mapTime = new LinkedHashMap<>();
                String yTimeB = arr[i];
                String yTimeE = arr[i];
                if (H>0){
                    yTimeB = String.valueOf(Integer.parseInt(arr[i])-1);
                }
                //选择年-当前月到选择年底差几个月
                Date dtsoll= sdfTime.parse(beginTime);
                String mTime = String.format("%tm", dtsoll);
                String dTime = String.format("%td", dtsoll);
                String ollTime = "";
                //历史结束年份
                Date dtsollEnd= sdfTime.parse(endTime);
                String mTimeEnd = String.format("%tm", dtsollEnd);
                String dTimeEnd = String.format("%td", dtsollEnd);
                String ollTimeEnd = "";
                Integer intOll = 0;
                if (timeType.equals("date")){
                    ollTime = yTimeB+"-"+mTime+"-"+dTime+" 08:00:00";
                    ollTimeEnd = yTimeE+"-"+mTimeEnd+"-"+dTimeEnd+" 08:00:00";
                    intOll = dataShowService.day(ollTime,ollTimeEnd);
                }
                if (timeType.equals("hour")){
                    String hTime = String.format("%tH", dtsoll);
                    String hTimeEnd = String.format("%tH", dtsollEnd);
                    ollTime = yTimeB+"-"+mTime+"-"+dTime+" "+hTime+":00:00";
                    ollTimeEnd = yTimeE+"-"+mTimeEnd+"-"+dTimeEnd+" "+hTimeEnd+":00:00";
                    intOll = dataShowService.hour(ollTime,ollTimeEnd)+1;
                }
                mapTime.put("year",arr[i]);
                List<String> list = new ArrayList<>();
                for (int j = 0; j < intOll; j++) {
                    Date dts = sdfTime.parse(ollTime);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dts);
                    if (timeType.equals("date")){
                        cal.add(Calendar.DATE, +j);
                    }
                    if (timeType.equals("hour")){
                        cal.add(Calendar.HOUR, +j);
                    }
                    String timeNew = sdfTime.format(cal.getTime());//时间

                    Date dts2 = sdfTime.parse(endTime);
                    if (yTimeE.equals(String.format("%tY",dts2))){
                        listTimeH.add(sdfTime.format(cal.getTime()));
                    }

                    list.add(timeNew);
                }
                mapTime.put("time",list);
                listTime.add(mapTime);
            }
        }
        for (int i = 0; i < listTime.size(); i++) {
            String year = listTime.get(i).get("year").toString();
            List<String> list = (List<String>) listTime.get(i).get("time");
            List<Object> listAll = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(list.size());
            for (int j = 0; j < list.size(); j++) {
                listAll.add(-9999999);
                int finalJ = j;
                Thread t = new Thread(() -> {
                    WpdJlData wpdJlData = new WpdJlData();
                    if (timeType.equals("date")){
                        if (type.equals("level")){
                            wpdJlData = wpdJlDataDao.fingMonRzD(list.get(finalJ),ndcdId);
                            if (wpdJlData != null && wpdJlData.getRz()!=null){
                                listAll.set(finalJ,wpdJlData.getRz());
                            }
                        }else if (wpdJlData.getInq()!=null && type.equals("inFlow")){
                            wpdJlData = wpdJlDataDao.fingMonInqD(list.get(finalJ),ndcdId);
                            if (wpdJlData != null && wpdJlData.getRz()!=null){
                                listAll.set(finalJ,wpdJlData.getInq());
                            }
                        }/*else if (wpdJlData.getOtq()!=null && type.equals("outFlow")){
                            wpdJlData = wpdJlDataDao.fingMonS(timeNew,ndcdId);
                            listAll.set(j,wpdJlData.getOtq());
                        }*/
                    }else if (timeType.equals("hour")){
                        if (type.equals("level")){
                            wpdJlData = wpdJlDataDao.fingMonRzH(list.get(finalJ),ndcdId);
                            if (wpdJlData != null && wpdJlData.getRz()!=null){
                                listAll.set(finalJ,wpdJlData.getRz());
                            }
                        }else if (type.equals("inFlow")){
                            wpdJlData = wpdJlDataDao.fingMonInqH(list.get(finalJ),ndcdId);
                            if (wpdJlData != null && wpdJlData.getRz()!=null){
                                listAll.set(finalJ,wpdJlData.getInq());
                            }
                        }
                    }
                    latch.countDown();
                });
                t.start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            jsonObject.put(year,listAll);
        }

        jsonObject.put("time",listTimeH);
        return jsonObject;
    }

}
