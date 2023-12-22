package com.cj.model.func.modular.FloodPredict.model;



import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.getSpecificDate;
import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.pointToSurface;

public class PhysicalForcast {

    public Object[][] getphysicalresult(ForcastInputParam param, List<List<PredictInputData>> Data, Object[][] snowData) throws IOException {
        String path = "D:\\tth_system\\end\\file\\陕北-PARAM.xlsx";
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        String sheetPara=param.getLocation()+"参数";
        //陕北模型输入、蒸散发和前期雨量
        List<PredictInputData> PreFlow = Data.get(0);
        List<PredictInputData> PointPreREDataList=Data.get(1);
        List<PredictInputData> PointHistoryRDataList=Data.get(2);
        List<PredictInputData> preREDataList = pointToSurface(PointPreREDataList);
        List<PredictInputData> historyRDataList = pointToSurface(PointHistoryRDataList);
        Object[][] preREData = new Object[preREDataList.size()][3];
        for (int i = 0; i < preREDataList.size(); i++) {
            preREData[i][0]=preREDataList.get(i).getDates();
            preREData[i][1]=preREDataList.get(i).getTemperature();
            preREData[i][2]=preREDataList.get(i).getRainfall();
        }
        preREData=temToEva(preREData);//将温度转为蒸发量
        Object[][] historyRData = new Object[historyRDataList.size()][2];
        for (int i = 0; i < historyRDataList.size(); i++) {
            historyRData[i][0]=historyRDataList.get(i).getDates();
            historyRData[i][1]=historyRDataList.get(i).getRainfall();
        }
        shanBeiModel.InputData(path,sheetPara,preREData,historyRData);
        shanBeiModel.InitialMoistureContentCalculation();
        shanBeiModel.RunoffYieldCalculation_UnevenInfiltration();
        shanBeiModel.ConfluenceCalculation();

        //获得径流序列包含了降水融雪地下水.是前48小时的后续可以改
        Object[][]shortFlow = mixedFlood(param, shanBeiModel.Q, PreFlow,snowData);
        //将Object转化为Flood类型
        Object[][] peakFlood=setPeakFlood(shortFlow,param);
        peakFlood[0][10]=floodSources(PointPreREDataList);//洪水来源
        peakFlood[0][11]=floodComposition(param,PreFlow,shanBeiModel.Q,snowData);//洪水组成
        peakFlood[0][12]="1年一遇";//洪水等级
        return peakFlood;
    }


    /**
     * 把预报洪水过程写成规范表格形式
     * @param predict
     * @param param
     * @return
     */
    public Object[][] setPeakFlood(Object[][]predict, ForcastInputParam param){
        //表头赋值
//        param.setPeriodStepNumber(predict.length);
        Object[][] peakFloodXlsx=new Object[param.getPeriodStepNumber()][13];

        //连续列的赋值
        for (int i = 0; i < param.getPeriodStepNumber(); i++) {
            peakFloodXlsx[i][0]=param.getLocation();//断面位置
            int timeScale=3600 * param.getPeriodStepSize();
            peakFloodXlsx[i][1]=Integer.toString(timeScale);//尺度
            peakFloodXlsx[i][3]=predict[i * param.getPeriodStepSize()][0];//时间
            peakFloodXlsx[i][4]=Math.round((double) predict[i * param.getPeriodStepSize()][1] * 100.0) / 100.0;//预报流量
            double[] waterLevel=getWaterLevel(predict);
            peakFloodXlsx[i][5]=waterLevel[i * param.getPeriodStepSize()];//相应水位
        }
            //场次洪水洪水要素赋值
            double[] peakFlood = selectPeakFlood(predict);
            peakFloodXlsx[0][6]=Math.round(peakFlood[1] * 100.0) / 100.0;//洪峰
            peakFloodXlsx[0][7]=predict[(int)peakFlood[2]][0];//峰现时间
            int hours = (int) peakFlood[3];
            int minutes = (int) ((peakFlood[3] - hours) * 60);
            peakFloodXlsx[0][8]=hours+"h"+minutes+"min";//洪峰持续时间
            peakFloodXlsx[0][9]=Math.round(peakFlood[4] /10000 * 100.0) / 100.0;//洪量
            peakFlood[0] = 1;//洪号，这里是指峰值最大的洪峰
        /**
         * 洪号：这里容易出问题记得检查
         */
//        if (param.getPeriodStepNumber()>((int) peakFlood[2] / param.getPeriodStepSize())){
//            for (int i = ((int) peakFlood[2] / param.getPeriodStepSize()); i >= ((int) peakFlood[2] - (int) peakFlood[5] )/ param.getPeriodStepSize(); i--) {
//                peakFloodXlsx[i][2] = peakFlood[0];
//            }
//            for (int i = ((int) peakFlood[2] / param.getPeriodStepSize()); i <= ((int) peakFlood[2] + (int) peakFlood[6] + 1)/ param.getPeriodStepSize(); i++) {
//                peakFloodXlsx[i][2] = peakFlood[0];
//            }
//        }
//            Object[][] predictAfter = new Object[predict.length - (int) peakFlood[2] - (int) peakFlood[6] - 1][2];
//            int j = 0;
//            for (int i = (int) peakFlood[2] + (int) peakFlood[6] + 1 ; i < predict.length; i++) {
//                predictAfter[j][0]=predict[i][0];
//                predictAfter[j][1]=predict[i][1];
//                j++;
//            }
//            double[] peakFloodAfter = selectPeakFlood(predictAfter);
//            peakFloodAfter[0] = 2;//洪号
//            for (int i =(int) peakFlood[2] + (int) peakFlood[6]+ (int) peakFloodAfter[2]; i >(int) peakFlood[2] + (int) peakFlood[6]+ (int) peakFloodAfter[2] - (int) peakFloodAfter[5] - 1; i--) {
//                peakFloodXlsx[i][2] = peakFloodAfter[0];
//            }
//            for (int i =(int) peakFlood[2] + (int) peakFlood[6]+ (int) peakFloodAfter[2]; i < (int) peakFlood[2] + (int) peakFlood[6] + (int) peakFloodAfter[2] + (int) peakFloodAfter[6] + 1; i++) {
//                peakFloodXlsx[i][2] = peakFloodAfter[0];
//            }
//
//            Object[][] predictBefore = new Object[predict.length - (int) peakFlood[2] - (int) peakFlood[5] - 1][2];
//            int z = 0;
//            for (int i = (int) peakFlood[2] - (int) peakFlood[5] ; i > 0; i--) {
//                predictBefore[z][0]=predict[i-1][0];
//                predictBefore[z][1]=predict[i-1][1];
//                z++;
//            }
//            double[] peakFloodBefore = selectPeakFlood(predictBefore);
//            peakFloodBefore[0] = 3;//洪号
//            for (int i =(int) peakFlood[2] - (int) peakFlood[5]+ (int) peakFloodBefore[2]; i >(int) peakFlood[2] + (int) peakFlood[6]+ (int) peakFloodBefore[2] - (int) peakFloodBefore[5] - 1; i--) {
//                peakFloodXlsx[i][2] = peakFloodBefore[0];
//            }
//            for (int i =(int) peakFlood[2] + (int) peakFlood[6]+ (int) peakFloodBefore[2]; i < (int) peakFlood[2] + (int) peakFlood[6] + (int) peakFloodBefore[2] + (int) peakFloodBefore[6] + 1; i++) {
//                peakFloodXlsx[i][2] = peakFloodBefore[0];
//            }

        return peakFloodXlsx;
        }

    /**
     *
     * @param predict 预报洪水过程
     * @return 记录好的洪号，洪峰，峰现时间，持续时间，洪量
     */
    public double[] selectPeakFlood(Object[][] predict){
        //记录洪号，洪峰，峰现时间，持续时间，洪量
        double[] peakflood = new double[7];
        int t = 0;
        for (int i = 0; i < predict.length; i++) {
            if (peakflood[1] <= (double) predict[i][1]) {
                peakflood[1] = (double) predict[i][1];//洪峰
                t = i ;
                peakflood[2] = t;//代表第几个时段出现的洪峰，从0开始
            }
        }
        //洪峰持续时间
        double peak = 0.6;//判断洪峰
        // 将小时数拆分成整数部分和小数部分
        int wholeHours2 = 0;
        double minutes2 = 0;//洪峰后
//        for (int i = 1; (double) predict[t + i][1] >= peak * peakflood[1]; i++) {
//            wholeHours2 = i;
//        }
//        if (t + wholeHours2 + 2 > predict.length){
//            minutes2=0;
//        }else {
//            minutes2 = (((double)predict[t + wholeHours2][1] - peak * peakflood[1]) / ((double)predict[t + wholeHours2][1] - (double)predict[t + 1 + wholeHours2][1]));
//        }
//        int wholeHours1 = 0;
//        double minutes1 = 0;//洪峰前
//        if (t == 0){
//            wholeHours1 = 0;
//            minutes1 = 0;
//        } else {
//            for (int i = 1; (double) predict[t - i][1] >= peak * peakflood[1]; i++) {
//                wholeHours1 = i;
//            }
//            if (t - wholeHours1  < 1){
//                minutes1=0;
//            }else {
//                minutes1 = (((double)predict[t - wholeHours1][1] - peak * peakflood[1]) / ((double)predict[t - wholeHours1][1] - (double)predict[t - 1 - wholeHours1][1]));
//            }
//        }
//        peakflood[3] = wholeHours1 + wholeHours2 + minutes1 + minutes2;//洪峰持续时间x.xx小时
//        //洪量
//        double v1= 0;//洪峰前流量
//        double v2= 0;//洪峰后流量
//        for (int i = 0; i < wholeHours2 ; i++) {
//            v2 += 3600 * (((double)predict[t + i][1] + (double)predict[t + 1 + i][1]) / 2);
//        }
//        v2 += 3600 * (minutes2 * (double)predict[t + wholeHours2][1]);
//        if (t == 0){
//            v1=0;
//        } else {
//            for (int i = 0; i < wholeHours1 ; i++) {
//                v1 += 3600 * (((double)predict[t - i][1] + (double)predict[t - 1 - i][1]) / 2);
//            }
//            v1 += 3600 * (minutes1 * (double)predict[t - wholeHours1][1]);
//        }
//        peakflood[4]= v1 +v2;//洪量
//        peakflood[5]= wholeHours1;//前沿时间
//        peakflood[6]= wholeHours2;//后续时间
        peakflood[4]= 10;//洪量
        peakflood[5]= 0.5;//前沿时间
        peakflood[6]= 0.5;//后续时间
        return peakflood;
    }

    /**
     *
     * @param predict 预报流量
     * @return 相应水位
     */
    public double[] getWaterLevel(Object[][] predict){
        //水位流量关系
        double[] waterLevel=new double[predict.length];
        for (int i = 0; i < predict.length; i++) {
            waterLevel[i]=(double) predict[i][1]*10+900;//这里用水位流量曲线
        }
        return waterLevel;
    }



    /**
     * 求洪水组成，各个雨量站代表的汇流面贡献多少水量
     * @param pointData
     * @return
     */
    public String floodSources (List<PredictInputData> pointData){
        String stationName = pointData.get(0).getRainStation();
        int number = 0 ;
        for (int i = 0; i < pointData.size(); i++) {
            if (pointData.get(i).getRainStation().equals(stationName)){
                number++;
            }
        }
        PredictInputData hourData ;
        List<PredictInputData> hourDatalist = new ArrayList<>();
        List<List<PredictInputData>> hourDataList = new ArrayList<>();
        for (int j = 0; j < number; j++) {
            hourDatalist = new ArrayList<>();
            for (int i = 0; i < pointData.size(); i++) {
                if (pointData.get(i).getDates()==pointData.get(j).getDates()){
                    hourData=pointData.get(i);
                    hourDatalist.add(hourData);
                }
            }
            hourDataList.add(hourDatalist);
        }
        //number代表几个时段
        Object[][] rainSum = new Object[hourDatalist.size()][2];
        for (int i = 0; i < rainSum.length; i++) {
            rainSum[i][1]=0.0;
        }
        for (int i = 0; i < number; i++) {
            PredictInputData hourResult=new PredictInputData();
            hourDatalist=hourDataList.get(i);
            Object[][] rainFall =new Object[hourDatalist.size()][2];//几个雨量站的雨量
            for (int j = 0; j < rainFall.length; j++) {
                rainFall[j][1]=0.0;
            }
            hourResult.setDates(hourDatalist.get(0).getDates());
            //hourDatalist为同一时间段不同雨量站
            for (int j = 0; j < hourDatalist.size(); j++) {
                int staNum=0;
                if (hourDatalist.get(j).getRainStation().equals("八一林场自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("加普沙自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("东南沟自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("宰尔德自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("无名沟自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("萨尔达万自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("煤矿沟自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("黑沟自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("喀什沟自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("制材厂自动雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("小渠子雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("团结一队雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
                if (hourDatalist.get(j).getRainStation().equals("头屯河水库雨量站")){
                    rainFall[staNum][0] = hourDatalist.get(j).getRainStation();
                    rainFall[staNum][1] = (double)rainFall[staNum][1]+hourDatalist.get(j).getRainfall()*0.1;
                    staNum++;
                }
            }
            //各个时间段雨量站降雨总和
            for (int j = 0; j < rainFall.length; j++) {
                rainSum[j][0]=rainFall[j][0];
                rainSum[j][1]=(double)rainSum[j][1]+(double) rainFall[j][1];
            }
        }
        double sum = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i = 0; i < rainSum.length; i++) {
            sum += (double)rainSum[i][1];
        }
        if (sum!=0){
            for (int i = 0; i < rainSum.length; i++) {
                rainSum[i][1]=(double)rainSum[i][1]/sum;
            }
        }else {
            for (int i = 0; i < rainSum.length; i++) {
                rainSum[i][1]=0;
            }
        }
        String result=new String();
        for (int i = 0; i < rainSum.length; i++) {
            if (rainSum[i][1] instanceof Integer){
                Integer num = (Integer) rainSum[i][1];
                Double doubleNum = num.doubleValue();
                rainSum[i][1]=doubleNum;
            }
        }
        for (int i = 0; i < rainSum.length-1; i++) {
            result += rainSum[i][0]+":"+df.format((double) rainSum[i][1]*100)+",";
        }
        result += rainSum[rainSum.length-1][0]+":"+df.format((double) rainSum[rainSum.length-1][1]*100);

        return result;
    }

    /**
     * 求洪水来源
     * @param param
     * @param PreFlow 基础流量
     * @param Q_shanbei 降雨产生
     * @param snowData 融雪产生
     * @return
     */
    public String floodComposition (ForcastInputParam param,List<PredictInputData> PreFlow,
                                    double[]Q_shanbei,Object[][]snowData){
        String result = new String();
        double snowFlow = 0.0;
        double preFlowSum = 0.0;
        int preFlowNum = 0;
        double preFlow = 0.0;
        double shanbeiFlow = 0.0;

        if (param.getIsSnowMeltModel()){
            int number = Q_shanbei.length;
            for (int i = 0; i < snowData.length; i++) {
                snowFlow += (double) snowData[i][1];
            }
            snowFlow = snowFlow/snowData.length/24*number;
            for (int i = 0; i < Q_shanbei.length; i++) {
                shanbeiFlow =shanbeiFlow +  Q_shanbei[i];
            }
            for (int i = 0; i < PreFlow.size(); i++) {
                Date time = PreFlow.get(i).getDates();
                int year = getSpecificDate(time).get("年");

                Date time2 = param.getPreStartTime();
                int year2 = getSpecificDate(time2).get("年");
                if (year==year2){
                    Date time3 = PreFlow.get(i).getDates();
                    int month = getSpecificDate(time3).get("月");
                    if (month>=1&&month<=4){
                        preFlowSum = preFlowSum + PreFlow.get(i).getFlow();
                        preFlowNum++;
                    }
                    preFlow = preFlowSum/preFlowNum;
                }
            }
             double Sum = snowFlow+preFlow+shanbeiFlow;
            DecimalFormat df = new DecimalFormat("#.##");
            result += "降水:"+ df.format(shanbeiFlow/Sum*100)+","+"融雪:"+df.format(snowFlow/Sum*100)+","+"地下水:"+df.format(preFlow/Sum*100);
        }
        else {
            for (int i = 0; i < Q_shanbei.length; i++) {
                shanbeiFlow =shanbeiFlow +  Q_shanbei[i];
            }
            for (int i = 0; i < PreFlow.size(); i++) {
                Date time = PreFlow.get(i).getDates();
                int year = getSpecificDate(time).get("年");
                Date time2 = param.getPreStartTime();
                int year2 = getSpecificDate(time2).get("年");
                if (year==year2){
                    Date time3 = PreFlow.get(i).getDates();
                    int month = getSpecificDate(time3).get("月");
                    if (month>=1&&month<=5){
                        preFlowSum = preFlowSum + PreFlow.get(i).getFlow();
                        preFlowNum++;
                    }
                    preFlow = preFlowSum/preFlowNum*Q_shanbei.length;
                }
            }
            double Sum = preFlow+shanbeiFlow;
            DecimalFormat df = new DecimalFormat("#.##");
            result += "降水:"+ df.format(shanbeiFlow/Sum*99.5)+","+"融雪:"+df.format(preFlow/Sum*99.5)+","+"地下水:"+0.05;
        }
        return result;
    }

    /**
     * 温度转化为蒸发量
     * @param data
     * @return
     */
    public static Object[][] temToEva(Object[][] data){
        Object[][] preData =new Object[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            preData[i][0]=data[i][0];
            preData[i][1]=0.0036;//温度转为蒸发量
            preData[i][2]=data[i][2];
        }
        return preData;
    }

    /**
     * 陕北模型计算所得降水数据与前期径流数据、融雪数据整合
     * @param param
     * @param shanBeiQ 降水所得
     * @param preFlow 前期径流这里取前十天平均径流
     * @param snowFlow 融雪径流
     * @return
     */
    public static Object[][] mixedFlood (ForcastInputParam param,double[] shanBeiQ,
                                         List<PredictInputData> preFlow, Object[][] snowFlow){
        Object[][] result= new Object[shanBeiQ.length][2];
        //减去前几天获得历史模拟的

        Date currentDate = param.getPreStartTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int hoursToSubtract = shanBeiQ.length; // 要减去的小时数
        calendar.add(Calendar.HOUR_OF_DAY, -hoursToSubtract);
        Date date = calendar.getTime();
        //获得的径流序列是前多少小时的后续可以改
        Date[][] dates = TimeUtils.getDateList(date, shanBeiQ.length, 0, 1, 1);
        for (int i = 0; i < shanBeiQ.length ; i++){
            result[i][0]=dates[i][0];
            Double snowAverage = 0.0;
            Double snowSum = 0.0;
            if (param.getIsSnowMeltModel()){
                for (int j = 0; j < snowFlow.length; j++) {
                    snowSum = snowSum + (double) snowFlow[i][1];
                }
                snowAverage = snowSum / snowFlow.length;
                result[i][1] = shanBeiQ[i] + snowAverage;//将陕北模型和融雪模型结果相加
            }else {
                Double baseAVe = 0.0;
                for (int j = 0; j < 10; j++) {
                    Double baseFlow = 0.0;
                    int n = preFlow.size() -1- j;
                    if (preFlow.get(n).getFlow() != null){
                        baseFlow = preFlow.get(n).getFlow();
                    }else {
                        n--;
                    }
                    baseAVe += baseFlow;
                }
                baseAVe = baseAVe / 10;
                result[i][1]=shanBeiQ[i] + baseAVe ;//降水加上前期径流
            }
        }
        return result;
    }
}
