package com.cj.model.func.modular.watertransfer.function;
import com.cj.common.exception.CommonException;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.watertransfer.entity.Excel1;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import com.cj.model.func.modular.watertransfer.method.FindValue;
import com.cj.model.func.modular.watertransfer.method.Reservoir;
import com.cj.model.func.modular.watertransfer.req.AppraiseReq;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WaterResourceAssessment {
    private Reservoir[] reservoirs;
    public Map<String, Object> WaterResourceAssessment(List<AppraiseReq> reqList,List<CurveParam> data) throws Exception {
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
        DecimalFormat da = new DecimalFormat("#.00");
        double[]dischargeLzz=new double[reqList.size()];
        double[]dischargeTth=new double[reqList.size()];
        double[]storgeAll=new double[reqList.size()];
        double[]inflowWater=new double[reqList.size()];
        double[]wasteWater=new double[reqList.size()];
        double[]waterDemand=new double[reqList.size()];
        double[]ecologyWater=new double[reqList.size()];
        double[]waterLack=new double[reqList.size()];
        double[]waterRate=new double[reqList.size()];
        //获取每个方案楼庄子蓄泄、头屯河蓄泄、以及两水库总蓄泄
        for (int i=0;i<reqList.size();i++){
            schemeName[i]=reqList.get(i).getName();
            dischargeLzz[i] =storageAndDischarge(reqList.get(i),data)[0];
            dischargeTth[i] =storageAndDischarge(reqList.get(i),data)[1];
            storgeAll[i]=storageAndDischarge(reqList.get(i),data)[2];
            inflowWater[i]=Double.parseDouble(da.format(getInflowWater(reqList.get(i).getExcel1Data())));
            waterDemand[i]=Double.parseDouble(da.format(getWaterDemand(reqList.get(i).getExcel1Data())));
            wasteWater[i]=Double.parseDouble(da.format(getWasteWater(reqList.get(i).getExcel1Data())));
            ecologyWater[i]=Double.parseDouble(da.format(getEcologyWater(reqList.get(i).getExcel2Data())));
            waterLack[i]=Double.parseDouble(da.format(getSum(reqList.get(i).getExcel2Data())[1]));
        }

        double bestUtilizationRate=(inflowWater[0]-wasteWater[0])/inflowWater[0];
        double n=0;
        for (int i=0;i<reqList.size();i++){
           double utilizationRate= (inflowWater[i]-wasteWater[i])/inflowWater[i];
           waterRate[i]=Double.parseDouble(da.format(utilizationRate));
           if (utilizationRate>=bestUtilizationRate)
           {
               bestUtilizationRate=utilizationRate;
               n=i;
           }
        }

        Map<String, Object>  appraise11= new HashMap<>();
        appraise11.put("方案名称",schemeName);
        appraise11.put("楼庄子蓄水量",dischargeLzz);
        appraise11.put("头屯河蓄水量",dischargeTth);
        appraise11.put("两水库总蓄水量",storgeAll);
        appraise11.put("来水水量",inflowWater);
        appraise11.put("需水数据",waterDemand);
        appraise11.put("弃水水量",wasteWater);
        appraise11.put("生态水量",ecologyWater);
        appraise11.put("供水缺额",waterLack);
        appraise11.put("可用水量利用率",waterRate);

        for (int i=0;i<reqList.size();i++){
             appraise+= schemeName[i]+"：在调度区间内来水预报总水量为"+inflowWater[i]+"万m³,"+"生态需水量为"+ecologyWater[i]+"万m³,"+"各单位需水总量为"+
            waterDemand[i]+"万m³,"+"在楼庄子的蓄水量为"+dischargeLzz[i]+"万m³,头屯河蓄水量为"+dischargeTth[i]+"万m³,总蓄水量为"+storgeAll[i]+
                     "万m³,可供水量浪费量为"+wasteWater[i]+"万m³,可供水量利用率为"+waterRate[i]+";";
        }
        schemeOptimization="推荐方案："+schemeName[(int)n]+",可供水量利用率较高;";
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
     * 根据水位计算两个水库的蓄泄水量，0为楼庄子，1为头屯河，2为两库总蓄泄水量
     * @param req
     * @param data
     * @return
     */
    public  double[] storageAndDischarge(AppraiseReq req,List<CurveParam> data) {
        setReservoir(data, reservoirs);
        DecimalFormat da1 = new DecimalFormat("#.00");
        double dischargeLzz = Double.parseDouble(da1.format(FindValue.FindV2ByV1(reservoirs[0].wlc_wl, reservoirs[0].wlc_c, req.getLevelEndLzz())-
                FindValue.FindV2ByV1(reservoirs[0].wlc_wl, reservoirs[0].wlc_c, req.getLevelBeginLzz())));
        double dischargeTth = Double.parseDouble(da1.format(FindValue.FindV2ByV1(reservoirs[1].wlc_wl, reservoirs[1].wlc_c, req.getLevelEndTth())-
                FindValue.FindV2ByV1(reservoirs[1].wlc_wl, reservoirs[1].wlc_c, req.getLevelBeginTth())));
        double[]storage=new double[3];
        storage[0]=dischargeLzz;
        storage[1]=dischargeTth;
        storage[2]=dischargeLzz+dischargeTth;
        return storage;
    }

    /**
     * 获得水位库容曲线
     * @param data
     * @param reservoir
     */
    public void setReservoir(List<CurveParam> data, Reservoir[] reservoir) {
        this.reservoirs = new Reservoir[2];
        this.reservoirs[0] = new Reservoir();
        this.reservoirs[1] = new Reservoir();
        List<Double> capacity = new ArrayList<>();
        List<Double> level = new ArrayList<>();
        List<Double> outflow = new ArrayList<>();
        List<Double> outflow_level = new ArrayList<>();


        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == 100) {
                capacity.add(data.get(i).getValue());
                level.add(data.get(i).getLevel());
            }
            if (data.get(i).getId() == 104) {
                outflow.add(data.get(i).getValue());
                outflow_level.add(data.get(i).getLevel());
            }
        }
        double[] wlc_wl = new double[level.size()];
        double[] wlc_c = new double[capacity.size()];
        for (int i = 0; i < wlc_wl.length; i++) {
            wlc_wl[i] = level.get(i);
            wlc_c[i] = capacity.get(i);
        }

        double[] wlob_wl = new double[outflow_level.size()];
        double[] wlob_ob = new double[outflow.size()];
        for (int i = 0; i < wlob_wl.length; i++) {
            wlob_wl[i] = outflow_level.get(i);
            wlob_ob[i] = outflow.get(i);
        }

        this.reservoirs[0].wlc_c = wlc_c;
        this.reservoirs[0].wlc_wl = wlc_wl;
        this.reservoirs[0].wlob_wl = wlob_wl;
        this.reservoirs[0].wlob_ob = wlob_ob;


        List<Double> capacity1 = new ArrayList<>();
        List<Double> level1 = new ArrayList<>();
        List<Double> outflow1 = new ArrayList<>();
        List<Double> outflow_level1 = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId() == 200) {
                capacity1.add(data.get(i).getValue());
                level1.add(data.get(i).getLevel());
            }
            if (data.get(i).getId() == 204) {
                outflow1.add(data.get(i).getValue());
                outflow_level1.add(data.get(i).getLevel());
            }
        }
        double[] wlc_wl1 = new double[level1.size()];
        double[] wlc_c1 = new double[capacity1.size()];

        double[] wlob_wl1 = new double[outflow_level1.size()];
        double[] wlob_ob1 = new double[outflow1.size()];
        for (int i = 0; i < wlob_wl1.length; i++) {
            wlob_wl1[i] = outflow_level1.get(i);
            wlob_ob1[i] = outflow1.get(i);
        }
        for (int i = 0; i < wlc_wl1.length; i++) {
            wlc_wl1[i] = level1.get(i);
            wlc_c1[i] = capacity1.get(i);
        }
        this.reservoirs[1].wlc_c = wlc_c1;
        this.reservoirs[1].wlc_wl = wlc_wl1;
        this.reservoirs[1].wlob_wl = wlob_wl1;
        this.reservoirs[1].wlob_ob = wlob_ob1;

        this.reservoirs[0].name = "楼庄子";
        this.reservoirs[0].levelFloodControl = 1394.5;
        this.reservoirs[0].levelNormal = 1394.5;
        this.reservoirs[0].levelFloodLimiting = 105;
        this.reservoirs[0].levelDead = 1353.3;
        this.reservoirs[0].outflowMin = 1.48;
        this.reservoirs[0].levelFloodDesign = 1397.41;
        this.reservoirs[0].levelFloodCheck = 1397.63;

        this.reservoirs[1].name = "头屯河";
        this.reservoirs[1].levelFloodControl = 987;
        this.reservoirs[1].levelNormal = 989.6;
        this.reservoirs[1].levelFloodLimiting = 105;
        this.reservoirs[1].levelDead = 975;
        this.reservoirs[1].outflowMin = 1.48;
        this.reservoirs[1].levelFloodDesign = 991.2;
        this.reservoirs[1].levelFloodCheck = 992.54;
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
        for (int i = 0; i < proportion.size(); i++)
        {
            if (proportion.get(i)!=1)
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
        result=x+combinedStringWithDelimiter+"这些时间段并未完全满足要求";
        }
        else
        {
            result=x+"在配水时间段内都能满足要求" ;
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
            if (data1.get(i).getStationType().equals("生活"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("工业"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("渠首"))
             {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("西干渠"))
            {
                water+= data1.get(i).getWater();
                waterLack+= data1.get(i).getWaterLack();
            }
            if (data1.get(i).getStationType().equals("东干渠"))
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
