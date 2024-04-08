package com.cj.model.func.modular.watertransfer.function;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.watertransfer.entity.ExcelDemo;
import com.cj.model.func.modular.watertransfer.req.AppraiseReq;
import com.cj.model.func.modular.watertransfer.req.DemoReq;
import com.cj.model.func.modular.watertransfer.res.ContrastNewRes;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Demo {

    public Map<String,  Object> demo(List<DemoReq> reqList, List<AppraiseReq> reqListAppraise, List<CurveParam> data) throws Exception {

        List<ContrastNewRes> tthDeltaWaterList = new ArrayList<>();
        List<ContrastNewRes> tthWaterSupplyList = new ArrayList<>();
        List<ContrastNewRes> tthWaterBalance = new ArrayList<>();
        List<ContrastNewRes> lzzDeltaWaterList = new ArrayList<>();
        List<ContrastNewRes> lzzWaterSupplyList = new ArrayList<>();
        List<ContrastNewRes> lzzWaterBalance = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //头屯河蓄水量
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("头屯河");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getDeltaWater).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            tthDeltaWaterList.add(res);
        }
        //头屯河供水量
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("头屯河");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getWaterSupply).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            tthWaterSupplyList.add(res);
        }
        //头屯河水量平衡
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("头屯河")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("头屯河");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getWaterBalance).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            tthWaterBalance.add(res);
        }
        //楼庄子蓄水量
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("楼庄子");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getDeltaWater).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            lzzDeltaWaterList.add(res);
        }
        //楼庄子供水量
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("楼庄子");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getWaterSupply).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            lzzWaterSupplyList.add(res);
        }
        //楼庄子水量平衡
        for(int i=0; i<reqList.get(0).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).size();i++){
            ContrastNewRes res = new ContrastNewRes();
            Map<Integer,Object> dataResult = new HashMap<>();
            for(int j=0;j<reqList.size();j++){
                res.setTime(sdf.format(reqList.get(j).getExcelDemoData().stream().filter(t->t.getStationName().equals("楼庄子")).collect(Collectors.toList()).get(i).getTime()));
                dataResult.put(j,reqList.get(j).getExcelDemoData().stream().filter(t-> {
                    try {
                        return t.getTime().compareTo(sdf.parse(res.getTime()))==0 && t.getStationName().equals("楼庄子");
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).map(ExcelDemo::getWaterBalance).reduce(Double::sum).orElse(0.00));
            }
            res.setData(dataResult);
            lzzWaterBalance.add(res);
        }
        Object  appraise3= new WaterResourceAssessment().WaterResourceAssessment( reqListAppraise, data).get("方案评价");
        Map<String, Object>  appraise12= new HashMap<>();
        appraise12.put("头屯河",tthWaterBalance);
        appraise12.put("楼庄子",lzzWaterBalance);
        Map<String, Object>  appraise13= new HashMap<>();
        appraise13.put("蓄水量",tthDeltaWaterList);
        appraise13.put("供水量",tthWaterSupplyList);
        Map<String, Object>  appraise14= new HashMap<>();
        appraise14.put("蓄水量",lzzDeltaWaterList);
        appraise14.put("供水量",lzzWaterSupplyList);
        Map<String, Object>  appraise15= new HashMap<>();
        appraise15.put("头屯河",appraise13);
        appraise15.put("楼庄子",appraise14);
        Map<String, Object>  appraise11= new HashMap<>();
        appraise11.put("水量平衡",appraise12);
        appraise11.put("水库供蓄对比",appraise15);
        appraise11.put("方案优选",appraise3);
      return appraise11;
    }

    /**
     * 获得方案时间
     * @param data
     * @return
     */
    public Date[] getTime(List<ExcelDemo> data)
    {
        List<Date> Time = new ArrayList<>();

        Time = data.stream().filter(n -> n.getStationName().equals("楼庄子")).map(ExcelDemo::getTime).distinct().collect(Collectors.toList());
        Date[]time=new Date[Time.size()];
        for (int i = 0; i < Time.size(); i++)
        {
           time[i]=Time.get(i);
        }
        return time;
    }

    /**
     * 获得示预数据
     * @param data
     * @param stationName
     * @param type
     * @return
     */
    public  double[] getData(List<ExcelDemo> data, String stationName, String type) {
        List<Double> data1=new ArrayList<>();

        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).getStationName().equals(stationName)){
                if (type.equals("水量平衡")){
                    data1.add(data.get(i).getWaterBalance());
                }
                if (type.equals("供水量")){
                    data1.add(data.get(i).getWaterSupply());
                }
                if (type.equals("蓄水量")){
                    data1.add(data.get(i).getDeltaWater());
                }
            }
        }
        double[]data2=new double[data1.size()];
        for (int i=0;i<data1.size();i++){
            data2[i]=data1.get(i);
        }
        return data2;
    }

}
