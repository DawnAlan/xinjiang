package com.cj.model.func.modular.watertransfer.function;
import com.cj.common.exception.CommonException;
import com.cj.model.func.modular.watertransfer.entity.Excel1;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import com.cj.model.func.modular.watertransfer.method.Reservoir;
import com.cj.model.func.modular.watertransfer.req.AppraiseReq;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DayAssessment {
    private Reservoir[] reservoirs;
    public Map<String, Object> dayAssessment(List<AppraiseReq> reqList) throws Exception {
        String appraise=new String();
        String schemeOptimization=new String();
        List<Date> startTime=new ArrayList<>();
        List<Date> endTime=new ArrayList<>();
        List<String> timePeriodType=new ArrayList<>();
        String[] schemeName=new String[reqList.size()];
        for (AppraiseReq req:reqList){
            startTime.add(req.getStartTime()) ;
            endTime.add(req.getEndTime());
            timePeriodType.add(req.getPeriod());
        }
        if (areAllDatesEqual(startTime)==false){
            throw new CommonException("请检查各方案调度开始时间是否相同");
        }
        if (areAllDatesEqual(endTime)==false){
            throw new CommonException("请检查各方案调度结束时间是否相同");
        }
        if (areAllDateTypeEqual(timePeriodType)==false){
            throw new CommonException("请检查各方案时段类型是否相同");
        }

        double[]wasteWater=new double[reqList.size()];
        double[]water=new double[reqList.size()];
        double[]waterLack=new double[reqList.size()];

        //获取每个方案楼庄子蓄泄、头屯河蓄泄、以及两水库总蓄泄
        for (int i=0;i<reqList.size();i++){
            schemeName[i]=reqList.get(i).getName();
            water[i]=getSum(reqList.get(i).getExcel2Data())[0];
            wasteWater[i]=getWasteWater(reqList.get(i).getExcel1Data());
            waterLack[i]=getSum(reqList.get(i).getExcel2Data())[1];
        }
        DecimalFormat da = new DecimalFormat("#.00");
        double bestUtilizationRate=waterLack[0];
        double n=0;
        for (int i=0;i<reqList.size();i++){
           double utilizationRate= waterLack[i];
           if (utilizationRate<=bestUtilizationRate)
           {
               bestUtilizationRate=utilizationRate;
               n=i;
           }
        }

        Map<String, Object>  appraise11= new HashMap<>();
        appraise11.put("方案名称",schemeName);
        appraise11.put("弃水水量",wasteWater);
        appraise11.put("供水水量",water);
        appraise11.put("供水缺额",waterLack);


        for (int i=0;i<reqList.size();i++){
             appraise+= schemeName[i]+"：在调度区间内供水水量为"+water[i]+"万m³,"+"供水缺额为"+waterLack[i]+"万m³,"+getStance(reqList.get(i).getExcel2Data(),"楼庄子生活")+","+
                     getStance(reqList.get(i).getExcel2Data(),"红岩生活")+","+getStance(reqList.get(i).getExcel2Data(),"八钢工业")+";";
        }
        schemeOptimization="推荐方案："+schemeName[(int)n]+",供水缺额较小;";
        appraise=appraise+schemeOptimization;
        appraise11.put("方案评价",appraise);

      return appraise11;
    }

    /**
     * 判断所有时间是否相同
     * @param dates
     * @return
     */
    public static boolean areAllDatesEqual(List<Date> dates) {
        if (dates == null || dates.isEmpty()) {
            return false; // 列表为空，无法比较
        }

        Date firstDate = dates.get(0); // 获取第一个日期
        for (int i = 1; i < dates.size(); i++) {
            if (!firstDate.equals(dates.get(i))) {
                return false; // 发现不同的日期，说明不是所有日期都相同
            }
        }
        return true; // 所有日期都与第一个日期相同
    }
    /**
     * 判断所有时段类型是否相同
     * @param dates
     * @return
     */
    public static boolean areAllDateTypeEqual(List<String> dates) {
        if (dates == null || dates.isEmpty()) {
            return false; // 列表为空，无法比较
        }

        String firstDate = dates.get(0); // 获取第一个时段类型
        for (int i = 1; i < dates.size(); i++) {
            if (!firstDate.equals(dates.get(i))) {
                return false; // 发现不同的时段类型，说明不是所有时段类型都相同
            }
        }
        return true; // 所有时段类型都与第一个相同
    }



    /**
     * 获得方案时间
     * @param data
     * @return
     */
    public Date[] getTime(List<Excel2> data)
    {
        List<Date> Time = new ArrayList<>();

        Time = data.stream().filter(n -> n.getStationType().equals("楼庄子生活")).map(Excel2::getTime).distinct().collect(Collectors.toList());
        Date[]time=new Date[Time.size()];
        for (int i = 0; i < Time.size(); i++)
        {
           time[i]=Time.get(i);
        }
        return time;
    }

    /**
     * 返回来水预报总水量
     * @param data
     * @return
     */
    public  double getInflowWater(List<Excel1> data) {
        double inflowWater=0;

        for (int i = 0; i < data.size(); i++)
        {
            inflowWater+=data.get(i).getInflowWater();
        }
        return inflowWater;
    }

    /**
     * 获得弃水数据
     * @param data
     * @return
     */
    public  double getWasteWater(List<Excel1> data) {
        double wasteWater=0;

        for (int i = 0; i < data.size(); i++)
        {
            wasteWater=data.get(i).getWasteWater();
        }
        return wasteWater;
    }

    /**
     * 获得需水计划总数据
     * @param data
     * @return
     */
    public  double getWaterDemand(List<Excel1> data) {
        double waterDemand=0;

        for (int i = 0; i < data.size(); i++)
        {
            waterDemand+=data.get(i).getWaterDemand();
        }
        return waterDemand;
    }

    /**
     * 计算生态用水量
     * @param data
     * @return
     */
    public  double getEcologyWater(List<Excel2> data) {
        double ecologyWater=0;

        for (int i = 0; i < data.size(); i++)
        {
            if (data.get(i).getStationName().equals("楼庄子生态用水")){
                ecologyWater+=data.get(i).getWater();
            }

        }
        return ecologyWater;
    }

    /**
     * 获得用水情况
     * @param data
     * @param location
     * @return
     */
    public String getStance(List<Excel2> data,String location)
    {
        List<Double> proportion = new ArrayList<>();
        List<Double> waterLack = new ArrayList<>();
        String x=location;
        if (location.equals("八钢工业"))
        {
            for (int i = 0; i < data.size(); i++)
            {
                if (data.get(i).getStationType().equals(location)&&data.get(i).getStationName().equals("八钢工业用水"))
                {
                    x=data.get(i).getStationName();
                    proportion.add(data.get(i).getProportion()) ;
                    waterLack.add(data.get(i).getWaterLack());
                }
            }
        }
        else
            {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getStationType().equals(location)) {
                    x = data.get(i).getStationName();
                    proportion.add(data.get(i).getProportion());
                    waterLack.add(data.get(i).getWaterLack());
                }
            }
        }
        List<Double> num = new ArrayList<>();

        Date[]time=getTime(data);
        for (int i = 0; i < waterLack.size(); i++)
        {
            if (waterLack.get(i)!=0)
            {
                double n=i;
                num.add(n);
            }
        }
        // 使用 SimpleDateFormat 定义日期格式
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> stringList = new ArrayList<>();
        String combinedStringWithDelimiter =new String();
        String result=new String();
        if (num.size()!=0){
        for (int i = 0; i < num.size(); i++)
        {
            double n=num.get(i);
            stringList.add(dateFormat.format(time[(int)n]));
        }
        String delimiter = ", "; // 自定义分隔符，例如逗号和空格
        combinedStringWithDelimiter = stringList.stream().collect(Collectors.joining(delimiter));
        result=x+"在该时间段内并未满足要求";
        }
        else
        {
            result=x+"在配水时间段内能够满足要求" ;
        }
        return result;
    }

    /**
     * 获得供水总数据及供水缺额
     * @param data1
     * @return
     */
    public double[] getSum(List<Excel2> data1)
    {
        double water=0;
        double waterLack=0;
        double[]sum=new double[2];
        for (int i = 0; i < data1.size(); i++)
        {
            if (data1.get(i).getStationType().equals("楼庄子生活"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("红岩生活"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("八钢工业"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("渠首工业"))
             {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("总西干渠"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("总东干渠"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
         }

        sum[0]=water;
        sum[1]=waterLack;
        return sum;
    }
}
