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
        setReservoir(data, reservoirs);
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
            dischargeLzz[i] =storageAndDischarge(reqList.get(i))[0];
            dischargeTth[i] =storageAndDischarge(reqList.get(i))[1];
            storgeAll[i]=storageAndDischarge(reqList.get(i))[2];
            inflowWater[i]=Double.parseDouble(da.format(getInflowWater(reqList.get(i).getExcel1Data())));
            waterDemand[i]=Double.parseDouble(da.format(getWaterDemand(reqList.get(i).getExcel1Data())));
            wasteWater[i]=Double.parseDouble(da.format(getWasteWater(reqList.get(i).getExcel1Data())));
            ecologyWater[i]=Double.parseDouble(da.format(getEcologyWater(reqList.get(i).getExcel2Data())));
            waterLack[i]=Double.parseDouble(da.format(getSum(reqList.get(i).getExcel2Data())[1]));
        }

        double bestUtilizationRate=(inflowWater[0]-storgeAll[0]-wasteWater[0])/(inflowWater[0]-storgeAll[0]);
        double x=0;
        for (int i=0;i<reqList.size();i++)
        {
            if (reqList.get(i).getName().contains("单库调度"))
            {
                x=i;
                double utilizationRate=1;
                if (inflowWater[i] - storgeAll[i] > 0) {
                    utilizationRate = (inflowWater[i] - storgeAll[i] - wasteWater[i]) / (inflowWater[i] - storgeAll[i]);
                } else {
                    utilizationRate = 1;
                }
                waterRate[i] = Double.parseDouble(da.format(utilizationRate));
            }
        }
        double n=0;
        for (int i=0;i<reqList.size();i++){

            if (i!=x)
            {
                double utilizationRate = 0;
                if (inflowWater[i] - storgeAll[i] > 0) {
                    utilizationRate = (inflowWater[i] - storgeAll[i] - wasteWater[i]) / (inflowWater[i] - storgeAll[i]);
                } else {
                    utilizationRate = 1;
                }
                waterRate[i] = Double.parseDouble(da.format(utilizationRate));
                if (utilizationRate > bestUtilizationRate) {
                    bestUtilizationRate = utilizationRate;
                    n = i;
                } else if (utilizationRate == bestUtilizationRate) {
                    if (waterLack[i] < waterLack[(int) n]) {
                        n = i;
                    }
                }
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
            double ecoWater=Double.parseDouble(da.format(ecologyWater[i]+wasteWater[i]));
             appraise+= schemeName[i]+"：在调度区间内来水预报总水量为"+inflowWater[i]+"万m³,"+"生态水量为"+ecoWater+"万m³,"+"各单位需水总量为"+
            waterDemand[i]+"万m³,"+"各单位供水缺额总量为"+ waterLack[i]+"万m³,"+"在楼庄子的蓄水量为"+dischargeLzz[i]+"万m³,头屯河蓄水量为"+dischargeTth[i]+"万m³,总蓄水量为"+storgeAll[i]+
                     "万m³,可供水量利用率为"+waterRate[i]+";";
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
     * @return
     */
    public  double[] storageAndDischarge(AppraiseReq req) {

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

        if (data!=null){
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
        }
        else{
            this.reservoirs[0].wlc_wl=new double[]{1326,1330,1335,1340,1345,1350,1355,1360,1365,1370,1375,1380,1385,1390,1395,1400
                    ,1405,1410,1415,1420};
            this.reservoirs[0].wlc_c=new double[]{0,3,28,92,221,417,680,1022,1464,2018,2686,3468,4378,5440,6656,8021,9532,11195,13022,15028};
            this.reservoirs[1].wlc_c=new double[]{0.03,0.28,0.81,1.59,3.31,6.78,11.84,18.36,26.52,36.29,61.6,95.28,137.82,191.56,256.88,335.89,428.83
                    ,549.14,694.74,863.67,1063.26,1297.03,1576.8,1838.71};
            this.reservoirs[1].wlc_wl=new double[]{955,956,957,958,959,960,961,962,963,964,966,968,970,972,974,976,978,980,982,984,986,988,990,992};
            this.reservoirs[0].wlob_wl=new double[]{1335,1336,1340,1345,1355,1365,1370,1380,1380.5,1381,1381.5,1382,1382.5,1383,1383.5,1384,
                    1384.5,1385,1385.5,1386,1386.5,1387,1387.5,1388,1388.5,1389,1389.5,1390,1390.5,1391,1391.5,1392,1392.5,1393,1394,1395,1396,1397};
            this.reservoirs[0].wlob_ob=new double[]{0,15,50.55,98.52,154.94,195.72,213.21,244.46,245.92,247.37,248.81,250.24,251.66,253.08,254.49,
                    257.19,259.89,262.58,265.27,267.94,270.61,273.27,275.92,278.57,281.21,283.85,286.47,289.1,291.71,294.33,296.92,299.52,302.11,
                    304.69,309.84,314.98,383.44,414.95};
            this.reservoirs[1].wlob_wl=new double[]{950,952,960,962,965,978,985,989.6,990,991,992.5,993.5};
            this.reservoirs[1].wlob_ob=new double[]{5.1,30.8,112.3,124,136,162.8,167.7,170.6,194.6,326.4,669.4,986.5};
        }
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
            if (data.get(i).getStationName().equals("头屯河生态用水")){
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
