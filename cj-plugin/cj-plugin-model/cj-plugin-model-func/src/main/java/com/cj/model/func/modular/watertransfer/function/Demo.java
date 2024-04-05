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

        List<ContrastNewRes> resList = new ArrayList<>();
        String schemeOptimization=new String();
        Date time[]=getTime(reqList.get(0).getExcelDemoData());
        String[] schemeName=new String[reqList.size()];
        DecimalFormat da = new DecimalFormat("#.00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            resList.add(res);
        }
        double[][]waterSupplyLzz=new double[reqList.size()][time.length];
        double[][]waterSupplyTth=new double[reqList.size()][time.length];
        double[][]deltaWaterLzz=new double[reqList.size()][time.length];
        double[][]deltaWaterTth=new double[reqList.size()][time.length];
        double[][]waterBalanceLzz=new double[reqList.size()][time.length];
        double[][]waterBalanceTth=new double[reqList.size()][time.length];

        //获取每个方案楼庄子蓄泄、头屯河蓄泄、以及两水库总蓄泄
        for (int i=0;i<reqList.size();i++){

            schemeName[i]=reqList.get(i).getName();
            waterSupplyLzz[i] =getData(reqList.get(i).getExcelDemoData(),"楼庄子","供水量");
            waterSupplyTth[i] =getData(reqList.get(i).getExcelDemoData(),"头屯河","供水量");//
            deltaWaterLzz[i] =getData(reqList.get(i).getExcelDemoData(),"楼庄子","蓄水量");
            deltaWaterTth[i] =getData(reqList.get(i).getExcelDemoData(),"头屯河","蓄水量");//
            waterBalanceLzz[i] =getData(reqList.get(i).getExcelDemoData(),"楼庄子","水量平衡");
            waterBalanceTth[i] =getData(reqList.get(i).getExcelDemoData(),"头屯河","水量平衡");

        }
        Map<String, Map<String, Object>>  supplyAndStorage= new HashMap<>();
        Map<String, Map<String, Object>>  waterBalance= new HashMap<>();

        Map<String, Object>  appraise3= new HashMap<>();
        WaterResourceAssessment appraise=new WaterResourceAssessment();
        appraise3=appraise.WaterResourceAssessment( reqListAppraise, data);
        appraise3.get("方案评价");
        Map<String, Object>  appraise11= new HashMap<>();

        appraise11.put("方案名称",schemeName);
        appraise11.put("时间",time);
        appraise11.put("楼庄子蓄水量",deltaWaterLzz);
        appraise11.put("头屯河蓄水量",deltaWaterTth);
        appraise11.put("楼庄子供水量",waterSupplyLzz);
        appraise11.put("头屯河供水量",waterSupplyTth);
        appraise11.put("楼庄子水量平衡",waterBalanceLzz);
        appraise11.put("头屯河水量平衡",waterBalanceTth);
        appraise11.put("方案优选",appraise3.get("方案评价"));

      return appraise11;
    }
    public Map<String,Object> getReservoirStorage(Map<String,Object> demo,String name) throws Exception {
        Map<String,Object>  result= new HashMap<>();
        double[][] storageLzz= (double[][]) demo.get("楼庄子蓄水量");
        double[][] storageTth= (double[][]) demo.get("头屯河蓄水量");
        double[][] supplyTth= (double[][]) demo.get("头屯河供水量");
        double[][] supplyLzz= (double[][]) demo.get("楼庄子供水量");
        if (name.equals("楼庄子")){
            for (int i=0;i<storageLzz.length;i++){
                result.put(i+"蓄水量",storageLzz[i]);
                result.put(i+"供水量",supplyLzz[i]);
            }
        }
        if (name.equals("头屯河")){
            for (int i=0;i<storageLzz.length;i++){
                result.put(i+"蓄水量",storageTth[i]);
                result.put(i+"供水量",supplyTth[i]);
            }
        }
        return result;
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
