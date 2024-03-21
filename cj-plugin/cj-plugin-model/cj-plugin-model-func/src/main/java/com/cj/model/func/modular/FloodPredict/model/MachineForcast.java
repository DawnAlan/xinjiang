package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.ParamsSetVO;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.model.MachineModel.pvoSet;

public class MachineForcast {
    boolean isHistory=false;
    public List<Object[][]> Forcast(Object[][] inputTemp, ForcastInputParam param) throws IOException {

        List<Object[][]> result = new ArrayList<>();
        ParamsSetVO pvo = pvoSet(inputTemp, param);//设置输入
        pvo.setPreStartTime(param.getPreStartTime());//预报时间
        //预报时段的确定
        pvo.setPeriodStepNumber(param.getPeriodStepNumber());//预报时段数
        pvo.setPeriodStepSize(param.getPeriodStepSize());//预报时段步长
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();

        //数值的赋值
        int K = param.vmdK;//分解层数
        int influence_factor= pvo.getHistory_factor();//影响因子个数
        int history_day = pvo.getHistory_day();
        int outputNumber = inputTemp.length - influence_factor  + 1;

        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][]vmdOutput=new double[K][inputTemp.length];

        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        vmdOutput=VMD.vmd(vmdInput,K);

        //输入赋值
        Object[][] de_result =new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()+1][K+1];
        Object[][] preResult = new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()][2];//分解后的预测值
        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] peakFlood;
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i+influence_factor-1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0]=vmdreaResult[i][0]+vmdOutput[j][i+influence_factor-1];
            }
        }

        for (int k = 0; k < K; k++) {
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
            //读取模型参数
            Object[][] paraTemp=ExcelTool.readExcel(paraPath, "模型参数");
            double[][] maxminOld = new double[maxminOldTemp.length][2];
            Object[][] para=new Object[paraTemp.length][4];
            Object[][] input = new Object[inputTemp.length][2];

            for (int i = 0; i < maxminOldTemp.length; i++) {
                for (int j = 0; j < 2; j++) {
                    maxminOld[i][j] = Double.parseDouble(maxminOldTemp[i][2*k+j].toString());
                }
            }
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < 4; j++) {
                    para[i][j] = paraTemp[i][4*k+j];
                }
            }
            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];
                input[i][1] = vmdOutput[k][i];
            }

            if(!isHistory)
            {
                //径流预报
                peakFlood = RealTimeForcast(input, pvo, maxminOld, para);
                de_result[0][0]="时间";
                de_result[0][k+1]="预报流量";
                for (int i = 1; i < peakFlood.length+1; i++) {
                    de_result[i][0]=peakFlood[i-1][0];
                    de_result[i][k+1]=peakFlood[i-1][1];
                }
            }
            else
            {
                HistoryImitate(pvo,input,maxminOld,paraTemp);
            }

        }
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            preResult[i][1] = 0.0;//初始值
        }
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            for (int j = 0; j < K; j++) {
                preResult[i][0] = de_result[i + 1][0];
                preResult[i][1] = (double) preResult[i][1] + (double) de_result[i + 1][j + 1];
            }
        }
        resultProcessing(preResult , pvo);//恢复为径流量
        peakFlood = setFloodXlsx(preResult , pvo);
        result.add(peakFlood);
        return result;
    }

    //历史模拟
    public void HistoryImitate(ParamsSetVO pvo,Object[][] input,double[][] maxminOld,Object[][] paraTemp) {
        boolean isRealtime = true;
        boolean isHistory =true;
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.LongTermForecast(pvo, isRealtime, isHistory, input, maxminOld, paraTemp);
        for (int i = 0; i < results.getResult().size(); i++) {
            System.out.println("历史模拟" + "                          " + "实测");
            System.out.println(results.getResult().get(i).getSimOutput() + "      " + results.getResult().get(i).getRealOutput());
        }
    }
    //实时预报
    public  Object[][] RealTimeForcast(Object[][] input, ParamsSetVO pvo,  double[][] maxminOld,  Object[][] paraTemp) {
        LongForecast longForecast = new LongForecast();
        boolean isRealtime=true;
        boolean isHistory=false;
        //日期赋值
        Date startDate = pvo.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        switch (pvo.getForecastPeriod()) {
            case "月":
                dates = TimeUtils.getMonthDateList(startDate, pvo.getPeriodStepNumber());
//                dates = TimeUtils.getSelectMonthDateList(startDate, pvo.getPeriodStepNumber());
                break;
            case "旬":
                dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber(), 10, 0);
                break;
            case "日":
                dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber() * pvo.getPeriodStepSize(), 1, 0);
                break;
            default:
                dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber() * pvo.getPeriodStepSize(), 0, 1);
                break;
        }
        /**
         * 当前只实现了提供往前数据，下一天开始预报的功能；可二次开发实现隔天预报，需要判断dates！
         */
        //预报赋值
        Object[][] predict = new Object[pvo.getPeriodStepSize() * pvo.getPeriodStepNumber() + 1][2];
        predict[0][1] = input[input.length-1][1];
        for (int A = 1; A < pvo.getPeriodStepSize()*pvo.getPeriodStepNumber() + 1; A++) {
            Object[][] pre_input = new Object[input.length + A][2];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    pre_input[i][j] = input[i][j];
                }
            }
            for (int a = 0; a < A; a++) {
                predict[a][0] = dates[a][0];
                pre_input[input.length + a][0] = dates[a][0];
                pre_input[input.length + a][1] = predict[a][1];
            }
            ModelSaveEntity pre_results = longForecast.LongTermForecast(pvo, isRealtime, isHistory, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - pvo.getHistory_day() + A).getSimOutput();
            predict[A][1] = input[0][1];
        }

        Object[][] predict1 = new Object[pvo.getPeriodStepSize()*pvo.getPeriodStepNumber()][2];//把前面predict的最后一行去掉
        for (int i = 0; i < pvo.getPeriodStepSize()*pvo.getPeriodStepNumber(); i++) {
            for (int j = 0; j < 2; j++) {
                predict1[i][j]=predict[i][j];
            }
        }
        return predict1;
    }
    //洪峰的确定
    public static Object[][] setFloodXlsx(Object[][]predict, ParamsSetVO pvo){
        //表头赋值
        Object[][] peakFloodXlsx=new Object[pvo.getPeriodStepNumber()][14];
        //连续列的赋值
        for (int i = 0; i < pvo.getPeriodStepNumber(); i++) {
            peakFloodXlsx[i][0]=pvo.getForecastDuanmian();//断面位置
            if (pvo.getForecastPeriod().equals("月")){
                peakFloodXlsx[i][1]=2592000;
            }
            else if (pvo.getForecastPeriod().equals("旬")) {
                peakFloodXlsx[i][1]=864000;
            }
            else if (pvo.getForecastPeriod().equals("日")) {
                peakFloodXlsx[i][1]=86400;
            }
            else if (pvo.getForecastPeriod().equals("小时")){
                peakFloodXlsx[i][1]=3600*pvo.getPeriodStepSize();
            }//尺度
            peakFloodXlsx[i][2] = 0;//洪号
            Date testStartDate = (Date) predict[i * pvo.getPeriodStepSize()][0];
            peakFloodXlsx[i][3]=testStartDate;//时间
            peakFloodXlsx[i][4]=Math.round((double) predict[i * pvo.getPeriodStepSize()][1] * 100.0) / 100.0;//预报流量
            double[] waterLevel=getWaterLevel(predict);
            peakFloodXlsx[i][5]=waterLevel[i * pvo.getPeriodStepSize()];//相应水位
        }
        //中长期预报的来水量
        for (int i = 0; i < pvo.getPeriodStepNumber(); i++) {
            if (pvo.getForecastPeriod().equals("日")){
                peakFloodXlsx[i][9]=Math.round(3600*24*pvo.getPeriodStepSize()*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (pvo.getForecastPeriod().equals("旬")){
                int days = getDays(predict, pvo, i);
                peakFloodXlsx[i][9]=Math.round(3600*24*days*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (pvo.getForecastPeriod().equals("月")){
                // 获取该月份的天数
                Date time = (Date) predict[i*pvo.getPeriodStepSize()][0];
                int days = 30;
                peakFloodXlsx[i][9]=Math.round(3600*24*days*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
        }
        double peakflood = 0;
        int t = 0;
        for (int i = 0; i < predict.length; i++) {
            if (peakflood <= (double) predict[i][1]) {
                peakflood = (double) predict[i][1];//洪峰
                t = i ;
            }
        }

        peakFloodXlsx[0][6] = peakflood;//洪峰
        peakFloodXlsx[0][7] = predict[t][0];//洪峰时间
        peakFloodXlsx[0][12]=judgingYear(peakFloodXlsx,pvo);//洪水等级
        for (int i = 1; i < peakFloodXlsx.length; i++) {
            peakFloodXlsx[i][6] = peakFloodXlsx[0][6];
            peakFloodXlsx[i][7] = peakFloodXlsx[0][7];
            peakFloodXlsx[i][12]=peakFloodXlsx[0][12];
        }

        //出库流量
        for (int i = 0; i < peakFloodXlsx.length; i++) {
            peakFloodXlsx[i][13]=peakFloodXlsx[i][4];//出库流量
        }
        return peakFloodXlsx;
    }

    /**
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     * @param input
     * @param pvo
     * @return
     */
    public static String judgingYear(Object[][] input,ParamsSetVO pvo){
        String result = "";
        double[] water = new double[input.length];
        for (int i = 0; i < water.length; i++) {
            water[i]= (double) input[i][9];
        }
        if (pvo.getForecastPeriod().equals("月")){
            double waterSum =0.0;
            for (double v : water) {
                waterSum += v;
            }
            waterSum = waterSum/10000;
            if (pvo.getForecastDuanmian().equals("3号桥")||pvo.getForecastDuanmian().equals("楼庄子")){
                if (waterSum >=2.476){
                    result = "丰水年";
                }
                if (waterSum<2.246&&waterSum>=1.998){
                    result = "平水年";
                }
                if (waterSum<1.998){
                    result = "枯水年";
                }
            }
            if (pvo.getForecastDuanmian().equals("楼头区间")){
                if (waterSum >=0.1443){
                    result = "丰水年";
                }
                if (waterSum<0.1443&&waterSum>=0.1164){
                    result = "平水年";
                }
                if (waterSum<0.1164){
                    result = "枯水年";
                }
            }
        }else {
            result = "平水年" ;
        }
        return result;
    }
    //判断每一旬的天数
    private static int getDays(Object[][] predict, ParamsSetVO pvo, int i) {//存在问题
        Date date1= (Date) predict[i * pvo.getPeriodStepSize()][0];
        LocalDate date = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 获取年份、月份和天数
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        // 判断旬
        int days;
        if (day <= 10) {
            days = Math.min(day, 10);
        } else if (day <= 20) {
            days = Math.min(day - 10, 10);
        } else {
            int lastDayOfMonth = YearMonth.of(year, month).lengthOfMonth();
            days = Math.min(day - 20, (month == 2 && lastDayOfMonth == 29) ? 11 : 10);
        }
        return days;
    }
    //水位
    public static double[] getWaterLevel(Object[][] predict){
        //水位流量关系
        double[] waterLevel=new double[predict.length];
        for (int i = 0; i < predict.length; i++) {
            waterLevel[i]=(double) predict[i][1]*0.1+900;//这里用水位流量曲线
        }
        return waterLevel;
    }

    /**
     * 将最后预测的相对误差转换为实际径流
     * @param input
     * @return
     */
    public static Object[][] resultProcessing(Object[][] input,ParamsSetVO pvo){
        int month = 0;
        for (int i = 0; i < input.length; i++) {
            if (pvo.getForecastDuanmian().equals("3号桥")||pvo.getForecastDuanmian().equals("楼庄子")){
                Date date = (Date) input[i][0];
                month = DataUtils.getSpecificDate(date).get("月");
                switch (month){
                    case 1:
                        input[i][1]=(1-(double) input[i][1])*1.99;
                        if ((double) input[i][1]<0.66){
                            input[i][1]=0.66;
                        }
                        if ((double) input[i][1]>3){
                            input[i][1]=2.38;
                        }
                        break;
                    case 2:
                        input[i][1]=(1-(double) input[i][1])*1.79;
                        if ((double) input[i][1]<0.57){
                            input[i][1]=0.57;
                        }
                        if ((double) input[i][1]>3){
                            input[i][1]=1.88;
                        }
                        break;
                    case 3:
                        input[i][1]=(1-(double) input[i][1])*2.77;
                        if ((double) input[i][1]<0.68){
                            input[i][1]=0.68;
                        }
                        if ((double) input[i][1]>5){
                            input[i][1]=3.94;
                        }
                        break;
                    case 4:
                        input[i][1]=(1-(double) input[i][1])*3.78;
                        if ((double) input[i][1]<1.38){
                            input[i][1]=1.38;
                        }
                        if ((double) input[i][1]>5){
                            input[i][1]=4.06;
                        }
                        break;
                    case 5:
                        input[i][1]=(1-(double) input[i][1])*8.93;
                        if ((double) input[i][1]<3.61){
                            input[i][1]=3.61;
                        }
                        if ((double) input[i][1]>20){
                            input[i][1]=14.7;
                        }
                        break;
                    case 6:
                        input[i][1]=(1-(double) input[i][1])*18.92;
                        if ((double) input[i][1]<9.54){
                            input[i][1]=9.54;
                        }
                        if ((double) input[i][1]>35){
                            input[i][1]=32.99;
                        }
                        break;
                    case 7:
                        input[i][1]=(1-(double) input[i][1])*23.05;
                        if ((double) input[i][1]<12.1){
                            input[i][1]=12.1;
                        }
                        if ((double) input[i][1]>50){
                            input[i][1]=46.1;
                        }
                        break;
                    case 8:
                        input[i][1]=(1-(double) input[i][1])*17.13;
                        if ((double) input[i][1]<9.19){
                            input[i][1]=9.19;
                        }
                        if ((double) input[i][1]>30){
                            input[i][1]=27.2;
                        }
                        break;
                    case 9:
                        input[i][1]=(1-(double) input[i][1])*8.84;
                        if ((double) input[i][1]<3.62){
                            input[i][1]=3.62;
                        }
                        if ((double) input[i][1]>20){
                            input[i][1]=14.7;
                        }
                        break;
                    case 10:
                        input[i][1]=(1-(double) input[i][1])*4.85;
                        if ((double) input[i][1]<1.95){
                            input[i][1]=1.95;
                        }
                        if ((double) input[i][1]>10){
                            input[i][1]=6.66;
                        }
                        break;
                    case 11:
                        input[i][1]=(1-(double) input[i][1])*3.23;
                        if ((double) input[i][1]<1.13){
                            input[i][1]=1.13;
                        }
                        if ((double) input[i][1]>5){
                            input[i][1]=3.36;
                        }
                        break;
                    case 12:
                        input[i][1]=(1-(double) input[i][1])*1.82;
                        if ((double) input[i][1]<0.86){
                            input[i][1]=0.86;
                        }
                        if ((double) input[i][1]>5){
                            input[i][1]=3.16;
                        }
                        break;
                }
            }
            //各个月份的区间比例0.178571429	0.167701863	0.359649123	0.251428571	0.112087912	0.145833333	0.139564124	0.168034766	0.139506173	0.174358974	0.248	0.172043011
            else if (pvo.getForecastDuanmian().equals("楼头区间")){
                Date date = (Date) input[i][0];
                month = DataUtils.getSpecificDate(date).get("月");
                switch (month){
                    case 1:
                        input[i][1]=(1-(double) input[i][1])*1.29*0.178571429;
                        if ((double) input[i][1]<0.66*0.178571429){
                            input[i][1]=0.66*0.178571429;
                        }
                        if ((double) input[i][1]>3*0.178571429){
                            input[i][1]=2.38*0.178571429;
                        }
                        break;
                    case 2:
                        input[i][1]=(1-(double) input[i][1])*1.19*0.167701863;
                        if ((double) input[i][1]<0.57*0.167701863){
                            input[i][1]=0.57*0.167701863;
                        }
                        if ((double) input[i][1]>3*0.167701863){
                            input[i][1]=1.88*0.167701863;
                        }
                        break;
                    case 3:
                        input[i][1]=(1-(double) input[i][1])*1.77*0.359649123;
                        if ((double) input[i][1]<0.68*0.359649123){
                            input[i][1]=0.68*0.359649123;
                        }
                        if ((double) input[i][1]>5*0.359649123){
                            input[i][1]=3.94*0.359649123;
                        }
                        break;
                    case 4:
                        input[i][1]=(1-(double) input[i][1])*2.78*0.251428571;
                        if ((double) input[i][1]<1.38*0.251428571){
                            input[i][1]=1.38*0.251428571;
                        }
                        if ((double) input[i][1]>5*0.251428571){
                            input[i][1]=4.06*0.251428571;
                        }
                        break;
                    case 5:
                        input[i][1]=(1-(double) input[i][1])*8.13*0.112087912;
                        if ((double) input[i][1]<3.61*0.112087912){
                            input[i][1]=3.61*0.112087912;
                        }
                        if ((double) input[i][1]>20*0.112087912){
                            input[i][1]=14.7*0.112087912;
                        }
                        break;
                    case 6:
                        input[i][1]=(1-(double) input[i][1])*17.92*0.145833333;
                        if ((double) input[i][1]<9.54*0.145833333){
                            input[i][1]=9.54*0.145833333;
                        }
                        if ((double) input[i][1]>35*0.145833333){
                            input[i][1]=32.99*0.145833333;
                        }
                        break;
                    case 7:
                        input[i][1]=(1-(double) input[i][1])*22.05*0.139564124;
                        if ((double) input[i][1]<12.1*0.139564124){
                            input[i][1]=12.1*0.139564124;
                        }
                        if ((double) input[i][1]>50*0.139564124){
                            input[i][1]=46.1*0.139564124;
                        }
                        break;
                    case 8:
                        input[i][1]=(1-(double) input[i][1])*16.13*0.168034766;
                        if ((double) input[i][1]<9.19*0.168034766){
                            input[i][1]=9.19*0.168034766;
                        }
                        if ((double) input[i][1]>30*0.168034766){
                            input[i][1]=27.2*0.168034766;
                        }
                        break;
                    case 9:
                        input[i][1]=(1-(double) input[i][1])*7.84*0.139506173;
                        if ((double) input[i][1]<3.62*0.139506173){
                            input[i][1]=3.62*0.139506173;
                        }
                        if ((double) input[i][1]>20*0.139506173){
                            input[i][1]=14.7*0.139506173;
                        }
                        break;
                    case 10:
                        input[i][1]=(1-(double) input[i][1])*3.85*0.174358974;
                        if ((double) input[i][1]<1.95*0.174358974){
                            input[i][1]=1.95*0.174358974;
                        }
                        if ((double) input[i][1]>10*0.174358974){
                            input[i][1]=6.66*0.174358974;
                        }
                        break;
                    case 11:
                        input[i][1]=(1-(double) input[i][1])*2.23*0.248;
                        if ((double) input[i][1]<1.13*0.248){
                            input[i][1]=1.13*0.248;
                        }
                        if ((double) input[i][1]>5*0.248){
                            input[i][1]=3.36*0.248;
                        }
                        break;
                    case 12:
                        input[i][1]=(1-(double) input[i][1])*1.52*0.172043011;
                        if ((double) input[i][1]<0.86*0.172043011){
                            input[i][1]=0.86*0.172043011;
                        }
                        if ((double) input[i][1]>5*0.172043011){
                            input[i][1]=3.16*0.172043011;
                        }
                        break;
                }
            }
        }
        return input;
    }
}

