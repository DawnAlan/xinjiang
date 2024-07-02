package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.*;

public class MachineDataUtils {

    TimeUtils tu =new TimeUtils();

    @SneakyThrows
    public ForecastInputParam paramConvert(ForecastInputParamNew forecastParam) {
        ForecastInputParam param = new ForecastInputParam();
        //模型类型
        param.setModel("Elman神经网络");
        param.setIsRealtime(true);
        Boolean isAverage = forecastParam.getPeriodTimeType() == 3 || forecastParam.getPeriodTimeType() == 4;
        param.setIsAverage(isAverage);
        if (forecastParam.getIsTrain()==null){
            param.setIsTrain(false);
        }
        else {
            param.setIsTrain(forecastParam.getIsTrain());
        }
        param.setVmdK(12);
        param.setIsShortForecast(forecastParam.getModelType() == 3);
        //率定时间
        //        param.setCalibrationTime(param.getPreStartTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.setCalibrationTime(sdf.parse("2024-06-01 00:00:00"));
//        param.setCalibrationTime(forecastParam.getPredictionTime());
        param.setPreStartTime(forecastParam.getPredictionTime());
        //时段
        if (forecastParam.getPeriodTimeType() == 1) {
            param.setPeriod("月");
        }
        else if (forecastParam.getPeriodTimeType() == 2) {
            param.setPeriod("旬");
        }
        else if (forecastParam.getPeriodTimeType() == 3) {
            param.setPeriod("日");
        }
        else if (forecastParam.getPeriodTimeType() == 4) {
            param.setPeriod("日");
        }
        param.setIsSimulation(forecastParam.getIsSimulation() != null && forecastParam.getIsSimulation());
        param.setIsReferenceWater(forecastParam.getIsReferenceWater() != null && forecastParam.getIsReferenceWater());
        //预报长度
        int l = forecastParam.getPeriodTimeStep();
        int n = forecastParam.getPeriodTimeNum();
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        param.setPredict_day(1);
        param.setPreFlow(forecastParam.getPreFlow());
        param.setPreRainFall(forecastParam.getPreRainFall());
        param.setRainFallDtos(forecastParam.getRainFallDtos());
        param.setParamMap(forecastParam.getParamMap());
        param.setBasinStr(forecastParam.getBasinStr());
        param.setPreRainTem(forecastParam.getPreRainTem());
        return param;
    }
    /*
     * 输入数据区分部分（训练、测试、区分融雪、区分丰枯）
     */

    /**
     * 模型预报时的输入
     */
    public List<double[][]> inputData_Real(List<double[]> dataList, ForecastInputParam param) {
        /*
         * 前n旬流量
         */
        int a = dataList.get(0).length;
        int n = param.getHistory_day();//输入数据
        String[] layerNum = param.getLayerCount().split(",");
        int m = Integer.parseInt((layerNum[layerNum.length - 1]));//输出数据
        int l = a - n - m + 1;//行数
        List<double[][]> result = new ArrayList<>();
        double[][] data_date = new double[l][m];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < m; j++) {
                data_date[i][j]=dataList.get(0)[n+i+j];//时间戳
            }
        }
        result.add(data_date);
        double[][] data_input = new double[l][n];
        for (int i = 0; i < data_input.length; i++) {
            //前N旬流量
            System.arraycopy(dataList.get(1), i, data_input[i], 0, n);
        }
        result.add(data_input);
        double[][] data_output = new double[l][m];
        for (int i = 0; i < data_output.length; i++) {
            for (int j = 0; j < m; j++) {
                data_output[i][j] = dataList.get(1)[i + n + j];
            }
        }
        result.add(data_output);
        return result;
    }

    /**
     * 训练模型时的输入
     */
    public List<double[][]> inputData_Train(List<double[]> dataList, ForecastInputParam param, boolean isTest) {

        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        int c = a - b;
        int n = param.getHistory_day();
        String[] layerNum = param.getLayerCount().split(",");
        int m = Integer.parseInt((layerNum[layerNum.length - 1]));//输出数据
        int l = b - n - m + 1;//行数
        int ll = c - n - m + 1;//行数
        /*
         * 前n旬流量+平均流量
         */
//		 一般训练期：检验期=3:1
        double[][] trainData_date = new double[l][m];
        double[][] trainData_input = new double[l][n];
        double[][] trainData_output = new double[l][m];
        List<double[][]> train = new ArrayList<>();
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < m; j++) {
                trainData_date[i][j]=dataList.get(0)[n+i+j];//时间戳
            }
        }
        train.add(trainData_date);
        for (int i = 0; i < l; i++) {
            //前N旬流量
            System.arraycopy(dataList.get(1), i, trainData_input[i], 0, n);
        }
        train.add(trainData_input);
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < m; j++) {
                trainData_output[i][j]=dataList.get(1)[i + n+j];
            }
        }
        train.add(trainData_output);

        double[][] testData_date = new double[ll][m];
        double[][] testData_input = new double[ll][n];
        double[][] testData_output = new double[ll][m];
        List<double[][]> test = new ArrayList<>();
        for (int i = 0; i < ll; i++) {
            for (int j = 0; j < m; j++) {
                testData_date[i][j] = dataList.get(0)[i +j+ b+n];//时间戳
            }
        }
        test.add(testData_date);
        for (int i = 0; i < ll; i++) {
            for (int j = 0; j < n; j++) {
                testData_input[i][j] = dataList.get(1)[i +b+ j];//前N旬流量
            }
        }
        test.add(testData_input);
        for (int i = 0; i < ll; i++) {
            for (int j = 0; j < m; j++) {
                testData_output[i][j]=dataList.get(1)[i+b+n+j];
            }
        }
        test.add(testData_output);

        if (isTest) {
            return test;
        } else {
            return train;
        }

    }

    /**
     * 融雪模型数据输入
     */
    public List<double[][]> inputData_Real_Snow(List<double[]> dataList, ForecastInputParam param,int a,int b) {
        int n = param.getHistory_day();
        int m = param.getHistory_factor();
        Map<String,Integer> factors = param.getFactors();

        double[][] data_date = new double [a - b - n][1];
        double[][] data_input = new double[a - b  - n][m];
        double[][] data_output = new double[a - b  - n][1];
        List<double[][]> result = new ArrayList<>();
        for (int i = b; i < a-n; i++) {
            data_date[i-b][0] = dataList.get(0)[i+n];//时间戳
        }
        result.add(data_date);
        for (int i = b; i < a - n; i++) {
            if (factors.containsKey("径流")&&factors.containsKey("温度")){
                int f = factors.get("径流");
                //流量
                if (f >= 0) System.arraycopy(dataList.get(1), i, data_input[i - b], 0, f);
                int t = factors.get("温度");
                for (int j = 0; j < t; j++) {
                    data_input[i-b][f+j] = dataList.get(3)[i + n - t + j + 1];//温度
                }
                if (factors.containsKey("降水")){
                    int r = factors.get("降水");
                    for (int j = 0; j < r; j++) {
                        data_input[i-b][f+t+j] = dataList.get(4)[i + n - r + j + 1];//降水
                    }
                }
            }
            else if (!factors.containsKey("径流") && factors.containsKey("温度") && factors.containsKey("降水")) {
                int t = factors.get("温度");
                //温度
                if (t >= 0) System.arraycopy(dataList.get(3), i + n - t, data_input[i - b], 0, t);
                int r = factors.get("降水");
                //流量
                if (r >= 0) System.arraycopy(dataList.get(4), i + n - r , data_input[i - b], t , r);
            }
        }
        result.add(data_input);
        for (int i = b; i < a - n; i++){
            data_output[i-b][0] = dataList.get(1)[i + n];//预报径流
        }
        result.add(data_output);

        return result;
    }

    /**
     * 融雪模型训练数据输入
     */
    public List<double[][]> inputData_Train_Snow(List<double[]> dataList, ForecastInputParam param, boolean isTest) {
        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        //一般训练期：检验期=3:1
        List<double[][]> train = inputData_Real_Snow(dataList,param,b,0);
        List<double[][]> test = inputData_Real_Snow(dataList,param,a,b);
        if (isTest) {
            return test;
        } else {
            return train;
        }
    }

//    /**
//     * 获得丰水期和枯水期的预报开始时间和预报数量
//     * @return result.get(0)丰水期
//     * result.get(1)枯水期
//     */
//    public List<Object[]> getSelectedData(ForecastInputParam param) {
//        List<Object[]> result = new ArrayList<>();
//        Object[] Feng = new Object[2];
//        Object[] Ku = new Object[2];
//        Date dateStart = param.getPreStartTime();
//        int number = param.getPeriodStepNumber() * param.getPeriodStepSize();
//        Date[][] date = new Date[number][1];
//        int fengNumber = 0;
//        int kuNumber = 0;
//        int month;
//        switch (param.getPeriod()) {
//            case "月":
//                date = tu.getMonthDateList(dateStart, number,1);
//                break;
//            case "旬":
//                date = tu.getDateList(dateStart, number, 10, 0);
//                break;
//            case "日":
//                date = tu.getDateList(dateStart, number, 1, 0);
//                break;
//        }
//        for (int i = 0; i < number; i++) {
//            month = tu.getSpecificDate(date[i][0]).get("月");
//            if (month <= 10 && month >= 4) {
//                fengNumber++;
//            } else {
//                kuNumber++;
//            }
//        }
//        month = tu.getSpecificDate(date[0][0]).get("月");
//        if (month <= 10 && month >= 4) {
//            Feng[0] = date[0];
//            for (int i = 0; i < number; i++) {
//                month = tu.getSpecificDate(date[i][0]).get("月");
//                if (month == 11) {
//                    Ku[0] = date[i];
//                    break;
//                }
//            }
//        } else {
//            Ku[0] = date[0];
//            for (int i = 0; i < number; i++) {
//                month = tu.getSpecificDate(date[i][0]).get("月");
//                if (month == 4) {
//                    Feng[0] = date[i];
//                    break;
//                }
//            }
//        }
//        Feng[1] = fengNumber;
//        Ku[1] = kuNumber;
//        result.add(Feng);
//        result.add(Ku);
//        return result;
//    }

    /**
     * 筛选枯水期丰水期,4~10月为丰水期
     */
    public Object[][] SelectDate(Object[][] input, Date preStartTime) {
        List<Object[]> KuData = new ArrayList<>();
        Object[] kudata;
        List<Object[]> FengData = new ArrayList<>();
        Object[] fengdata;
        List<Object[]> PingData = new ArrayList<>();
        Object[] pingdata;
        for (Object[] objects : input) {
            Date time = (Date) objects[0];
            int month = tu.getSpecificDate(time).get("月");
            kudata = new Object[input[0].length];
            fengdata = new Object[input[0].length];
            pingdata = new Object[input[0].length];
            if (month == 1 || month == 2 || month == 3 || month == 11 || month == 12) {
                System.arraycopy(objects, 0, kudata, 0, input[0].length);
                KuData.add(kudata);
            } else if (month == 4 || month == 5 || month == 9 || month == 10) {
                System.arraycopy(objects, 0, pingdata, 0, input[0].length);
                PingData.add(pingdata);
            } else {
                System.arraycopy(objects, 0, fengdata, 0, input[0].length);
                FengData.add(fengdata);
            }
        }
        int month2 = tu.getSpecificDate(preStartTime).get("月");
        Object[][] longForecastInput;
        if (month2 == 1 || month2 == 2||month2 == 3 || month2 == 11|| month2 == 12) {
            longForecastInput = new Object[KuData.size()][input[0].length];
            for (int i = 0; i < KuData.size(); i++) {
                longForecastInput[i] = KuData.get(i);
            }
        } else if (month2 == 4 || month2 == 5||month2 == 9 || month2 == 10) {
            longForecastInput = new Object[PingData.size()][input[0].length];
            for (int i = 0; i < PingData.size(); i++) {
                longForecastInput[i] = PingData.get(i);
            }
        } else {
            longForecastInput = new Object[FengData.size()][input[0].length];
            for (int i = 0; i < FengData.size(); i++) {
                longForecastInput[i] = FengData.get(i);
            }
        }
        return longForecastInput;
    }

    /**
     * 筛选融雪期,楼头区间3月融雪，楼庄子上游5~7月份融雪
     *
     * @param input 历史径流+温度+降雨
     */
    public Object[][] snowMeltDate(Object[][] input, String location) {
        List<Object[]> Data = new ArrayList<>();
        Object[] data;
        int factor = 3;
        List<Object[]> snowData = new ArrayList<>();
        for (Object[] objects : input) {
            if (!(objects[3] instanceof String)) {
//                if ((double) input[i][3] == 0.0) {
//                    snowData.add(input[i]);
//                }
                snowData.add(objects);
            }
        }
        for (Object[] snowDatum : snowData) {
            Date time = (Date) snowDatum[0];
            int month = tu.getSpecificDate(time).get("月");
            data = new Object[factor];
            if (location.equals("楼头区间")) {
                if (month == 3) {
                    System.arraycopy(snowDatum, 0, data, 0, factor);
                    Data.add(data);
                }
            } else {
                if (month >= 5 && month <= 7) {
                    System.arraycopy(snowDatum, 0, data, 0, factor);
                    Data.add(data);
                }
            }
        }
        Object[][] rongXueInput = new Object[Data.size()][factor];
        for (int i = 0; i < Data.size(); i++) {
            rongXueInput[i] = Data.get(i);
        }
        return rongXueInput;
    }


    /*
     * 径流数据处理部分（距平值）
     */

    /**
     * 输入数据的处理，求与均值之间的偏差
     */
    public Object[][] inputProcessing(Object[][] input, String location) {
        int month;
        Object[][] result = new Object[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            result[i][0] = input[i][0];
            if (input[0].length>2&&input[i][2]!=null){
                result[i][2] = input[i][2];
            }
            if (input[0].length>3&&input[i][3]!=null){
                result[i][3] = input[i][3];
            }
            if (location.equals("3号桥") || location.equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        result[i][1] = (1.29 - (double) input[i][1]) / 1.29;
                        break;
                    case 2:
                        result[i][1] = (1.16 - (double) input[i][1]) / 1.16;
                        break;
                    case 3:
                        result[i][1] = (1.44 - (double) input[i][1]) / 1.44;
                        break;
                    case 4:
                        result[i][1] = (2.57 - (double) input[i][1]) / 2.57;
                        break;
                    case 5:
                        result[i][1] = (7.95 - (double) input[i][1]) / 7.95;
                        break;
                    case 6:
                        result[i][1] = (17.865 - (double) input[i][1]) / 17.865;
                        break;
                    case 7:
                        result[i][1] = (21.63 - (double) input[i][1]) / 21.63;
                        break;
                    case 8:
                        result[i][1] = (16.07 - (double) input[i][1]) / 16.07;
                        break;
                    case 9:
                        result[i][1] = (7.5 - (double) input[i][1]) / 7.5;
                        break;
                    case 10:
                        result[i][1] = (3.76 - (double) input[i][1]) / 3.76;
                        break;
                    case 11:
                        result[i][1] = (2.27 - (double) input[i][1]) / 2.27;
                        break;
                    case 12:
                        result[i][1] = (1.62 - (double) input[i][1]) / 1.62;
                        break;
                }
            } else if (location.equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        result[i][1] = (1.29 * 0.116 - (double) input[i][1]) / 1.29 * 0.116;
                        break;
                    case 2:
                        result[i][1] = (1.16 * 0.0957 - (double) input[i][1]) / 1.16 * 0.0957;
                        break;
                    case 3:
                        result[i][1] = (1.44 * 0.538 - (double) input[i][1]) / 1.44 * 0.538;
                        break;
                    case 4:
                        result[i][1] = (2.57 * 0.316 - (double) input[i][1]) / 2.57 * 0.316;
                        break;
                    case 5:
                        result[i][1] = (7.95 * 0.072 - (double) input[i][1]) / 7.95 * 0.072;
                        break;
                    case 6:
                        result[i][1] = (17.865 * 0.0484 - (double) input[i][1]) / 17.865 * 0.0484;
                        break;
                    case 7:
                        result[i][1] = (21.63 * 0.044 - (double) input[i][1]) / 21.63 * 0.044;
                        break;
                    case 8:
                        result[i][1] = (16.07 * 0.0395 - (double) input[i][1]) / 16.07 * 0.0395;
                        break;
                    case 9:
                        result[i][1] = (7.5 * 0.0419 - (double) input[i][1]) / 7.5 * 0.0419;
                        break;
                    case 10:
                        result[i][1] = (3.76 * 0.0383 - (double) input[i][1]) / 3.76 * 0.0383;
                        break;
                    case 11:
                        result[i][1] = (2.27 * 0.0365 - (double) input[i][1]) / 2.27 * 0.0365;
                        break;
                    case 12:
                        result[i][1] = (1.62 * 0.001524 - (double) input[i][1]) / 1.62 * 0.001524;
                        break;
                }
            }
        }
        return result;
    }

    /**
     * 将最后预测的相对误差转换为实际径流
     */
    public Object[][] resultProcessing(Object[][] input, String location) {
        int month;
        for (int i = 0; i < input.length; i++) {
            if (location.equals("3号桥") || location.equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1 - (double) input[i][1]) * 1.29;
                        if ((double) input[i][1] < 0.66) {
                            input[i][1] = 0.66;
                        }
                        if ((double) input[i][1] > 3) {
                            input[i][1] = 2.38;
                        }
                        break;
                    case 2:
                        input[i][1] = (1 - (double) input[i][1]) * 1.16;
                        if ((double) input[i][1] < 0.57) {
                            input[i][1] = 0.57;
                        }
                        if ((double) input[i][1] > 3) {
                            input[i][1] = 1.88;
                        }
                        break;
                    case 3:
                        input[i][1] = (1 - (double) input[i][1]) * 1.44;
                        if ((double) input[i][1] < 0.68) {
                            input[i][1] = 0.68;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.94;
                        }
                        break;
                    case 4:
                        input[i][1] = (1 - (double) input[i][1]) * 2.57;
                        if ((double) input[i][1] < 1.38) {
                            input[i][1] = 1.38;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 4.06;
                        }
                        break;
                    case 5:
                        input[i][1] = (1 - (double) input[i][1]) * 7.95;
                        if ((double) input[i][1] < 3.61) {
                            input[i][1] = 3.61;
                        }
                        if ((double) input[i][1] > 20) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 6:
                        input[i][1] = (1 - (double) input[i][1]) * 17.865;
                        if ((double) input[i][1] < 9.54) {
                            input[i][1] = 9.54;
                        }
                        if ((double) input[i][1] > 35) {
                            input[i][1] = 32.99;
                        }
                        break;
                    case 7:
                        input[i][1] = (1 - (double) input[i][1]) * 21.63;
                        if ((double) input[i][1] < 12.1) {
                            input[i][1] = 12.1;
                        }
                        if ((double) input[i][1] > 50) {
                            input[i][1] = 46.1;
                        }
                        break;
                    case 8:
                        input[i][1] = (1 - (double) input[i][1]) * 16.07;
                        if ((double) input[i][1] < 9.19) {
                            input[i][1] = 9.19;
                        }
                        if ((double) input[i][1] > 30) {
                            input[i][1] = 27.2;
                        }
                        break;
                    case 9:
                        input[i][1] = (1 - (double) input[i][1]) * 7.5;
                        if ((double) input[i][1] < 3.62) {
                            input[i][1] = 3.62;
                        }
                        if ((double) input[i][1] > 20) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 10:
                        input[i][1] = (1 - (double) input[i][1]) * 3.76;
                        if ((double) input[i][1] < 1.95) {
                            input[i][1] = 1.95;
                        }
                        if ((double) input[i][1] > 10) {
                            input[i][1] = 6.66;
                        }
                        break;
                    case 11:
                        input[i][1] = (1 - (double) input[i][1]) * 2.27;
                        if ((double) input[i][1] < 1.13) {
                            input[i][1] = 1.13;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.36;
                        }
                        break;
                    case 12:
                        input[i][1] = (1 - (double) input[i][1]) * 1.62;
                        if ((double) input[i][1] < 0.86) {
                            input[i][1] = 0.86;
                        }
                        if ((double) input[i][1] > 5) {
                            input[i][1] = 3.16;
                        }
                        break;
                }
            }
            //各个月份的区间比例
            else if (location.equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                double proportion;
                switch (month) {
                    case 1:
                        proportion = 0.116;
                        input[i][1] = (1 - (double) input[i][1]) * 1.29 * proportion;
                        if ((double) input[i][1] < 0.66 * proportion) {
                            input[i][1] = 0.66 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 2.38 * proportion;
                        }
                        break;
                    case 2:
                        proportion = 0.0957;
                        input[i][1] = (1 - (double) input[i][1]) * 1.16 * proportion;
                        if ((double) input[i][1] < 0.57 * proportion) {
                            input[i][1] = 0.57 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 1.88 * proportion;
                        }
                        break;
                    case 3:
                        proportion = 0.538;
                        input[i][1] = (1 - (double) input[i][1]) * 1.44 * proportion;
                        if ((double) input[i][1] < 0.68 * proportion) {
                            input[i][1] = 0.68 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.94 * proportion;
                        }
                        break;
                    case 4:
                        proportion = 0.316;
                        input[i][1] = (1 - (double) input[i][1]) * 2.57 * proportion;
                        if ((double) input[i][1] < 1.38 * proportion) {
                            input[i][1] = 1.38 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 4.06 * proportion;
                        }
                        break;
                    case 5:
                        proportion = 0.072;
                        input[i][1] = (1 - (double) input[i][1]) * 7.95 * proportion;
                        if ((double) input[i][1] < 3.61 * proportion) {
                            input[i][1] = 3.61 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 6:
                        proportion = 0.0484;
                        input[i][1] = (1 - (double) input[i][1]) * 17.865 * proportion;
                        if ((double) input[i][1] < 9.54 * proportion) {
                            input[i][1] = 9.54 * proportion;
                        }
                        if ((double) input[i][1] > 35 * proportion) {
                            input[i][1] = 32.99 * proportion;
                        }
                        break;
                    case 7:
                        proportion = 0.044;
                        input[i][1] = (1 - (double) input[i][1]) * 21.63 * proportion;
                        if ((double) input[i][1] < 12.1 * proportion) {
                            input[i][1] = 12.1 * proportion;
                        }
                        if ((double) input[i][1] > 50 * proportion) {
                            input[i][1] = 46.1 * proportion;
                        }
                        break;
                    case 8:
                        proportion = 0.0395;
                        input[i][1] = (1 - (double) input[i][1]) * 16.07 * proportion;
                        if ((double) input[i][1] < 9.19 * proportion) {
                            input[i][1] = 9.19 * proportion;
                        }
                        if ((double) input[i][1] > 30 * proportion) {
                            input[i][1] = 27.2 * proportion;
                        }
                        break;
                    case 9:
                        proportion = 0.0419;
                        input[i][1] = (1 - (double) input[i][1]) * 7.5 * proportion;
                        if ((double) input[i][1] < 3.62 * proportion) {
                            input[i][1] = 3.62 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 10:
                        proportion = 0.0383;
                        input[i][1] = (1 - (double) input[i][1]) * 3.76 * proportion;
                        if ((double) input[i][1] < 1.95 * proportion) {
                            input[i][1] = 1.95 * proportion;
                        }
                        if ((double) input[i][1] > 10 * proportion) {
                            input[i][1] = 6.66 * proportion;
                        }
                        break;
                    case 11:
                        proportion = 0.0365;
                        input[i][1] = (1 - (double) input[i][1]) * 2.27 * proportion;
                        if ((double) input[i][1] < 1.13 * proportion) {
                            input[i][1] = 1.13 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.36 * proportion;
                        }
                        break;
                    case 12:
                        proportion = 0.001524;
                        input[i][1] = (1 - (double) input[i][1]) * 1.62 * proportion;
                        if ((double) input[i][1] < 0.86 * proportion) {
                            input[i][1] = 0.86 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.16 * proportion;
                        }
                        break;
                }
            }
        }
        return input;
    }
    public Object[][] resultProcessingDay(Object[][] input, String location) {
        int month;
        for (int i = 0; i < input.length; i++) {
            if (location.equals("3号桥") || location.equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1 - (double) input[i][1]) * 1.29;
                        if ((double) input[i][1] < 0.828) {
                            input[i][1] = 0.828;
                        }
                        if ((double) input[i][1] > 3) {
                            input[i][1] = 2.58;
                        }
                        break;
                    case 2:
                        input[i][1] = (1 - (double) input[i][1]) * 1.16;
                        if ((double) input[i][1] < 0.829) {
                            input[i][1] = 0.829;
                        }
                        if ((double) input[i][1] > 2.25) {
                            input[i][1] = 2.25;
                        }
                        break;
                    case 3:
                        input[i][1] = (1 - (double) input[i][1]) * 1.44;
                        if ((double) input[i][1] < 0.879) {
                            input[i][1] = 0.879;
                        }
                        if ((double) input[i][1] > 2.65) {
                            input[i][1] = 2.65;
                        }
                        break;
                    case 4:
                        input[i][1] = (1 - (double) input[i][1]) * 2.57;
                        if ((double) input[i][1] < 1.057) {
                            input[i][1] = 1.057;
                        }
                        if ((double) input[i][1] > 8.73) {
                            input[i][1] = 8.73;
                        }
                        break;
                    case 5:
                        input[i][1] = (1 - (double) input[i][1]) * 7.95;
                        if ((double) input[i][1] < 1.65) {
                            input[i][1] = 1.65;
                        }
                        if ((double) input[i][1] > 34.9) {
                            input[i][1] = 34.9;
                        }
                        break;
                    case 6:
                        input[i][1] = (1 - (double) input[i][1]) * 17.865;
                        if ((double) input[i][1] < 4.4) {
                            input[i][1] = 4.4;
                        }
                        if ((double) input[i][1] > 84.9) {
                            input[i][1] = 84.9;
                        }
                        break;
                    case 7:
                        input[i][1] = (1 - (double) input[i][1]) * 21.63;
                        if ((double) input[i][1] < 2.85) {
                            input[i][1] = 2.85;
                        }
                        if ((double) input[i][1] > 63.3) {
                            input[i][1] = 63.3;
                        }
                        break;
                    case 8:
                        input[i][1] = (1 - (double) input[i][1]) * 16.07;
                        if ((double) input[i][1] < 5.7) {
                            input[i][1] = 5.7;
                        }
                        if ((double) input[i][1] > 50.7) {
                            input[i][1] = 50.7;
                        }
                        break;
                    case 9:
                        input[i][1] = (1 - (double) input[i][1]) * 7.5;
                        if ((double) input[i][1] < 2.51) {
                            input[i][1] = 2.51;
                        }
                        if ((double) input[i][1] > 28.3) {
                            input[i][1] = 28.3;
                        }
                        break;
                    case 10:
                        input[i][1] = (1 - (double) input[i][1]) * 3.76;
                        if ((double) input[i][1] < 1.71) {
                            input[i][1] = 1.71;
                        }
                        if ((double) input[i][1] > 7) {
                            input[i][1] = 7.0;
                        }
                        break;
                    case 11:
                        input[i][1] = (1 - (double) input[i][1]) * 2.27;
                        if ((double) input[i][1] < 1.32) {
                            input[i][1] = 1.32;
                        }
                        if ((double) input[i][1] > 3.86) {
                            input[i][1] = 3.86;
                        }
                        break;
                    case 12:
                        input[i][1] = (1 - (double) input[i][1]) * 1.62;
                        if ((double) input[i][1] < 1.07) {
                            input[i][1] = 1.07;
                        }
                        if ((double) input[i][1] > 3.82) {
                            input[i][1] = 3.82;
                        }
                        break;
                }
            }
            //各个月份的区间比例
            else if (location.equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                double proportion;
                switch (month) {
                    case 1:
                        proportion = 0.116;
                        input[i][1] = (1 - (double) input[i][1]) * 1.29 * proportion;
                        if ((double) input[i][1] < 0.66 * proportion) {
                            input[i][1] = 0.66 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 2.38 * proportion;
                        }
                        break;
                    case 2:
                        proportion = 0.0957;
                        input[i][1] = (1 - (double) input[i][1]) * 1.16 * proportion;
                        if ((double) input[i][1] < 0.57 * proportion) {
                            input[i][1] = 0.57 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 1.88 * proportion;
                        }
                        break;
                    case 3:
                        proportion = 0.538;
                        input[i][1] = (1 - (double) input[i][1]) * 1.44 * proportion;
                        if ((double) input[i][1] < 0.68 * proportion) {
                            input[i][1] = 0.68 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.94 * proportion;
                        }
                        break;
                    case 4:
                        proportion = 0.316;
                        input[i][1] = (1 - (double) input[i][1]) * 2.57 * proportion;
                        if ((double) input[i][1] < 1.38 * proportion) {
                            input[i][1] = 1.38 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 4.06 * proportion;
                        }
                        break;
                    case 5:
                        proportion = 0.072;
                        input[i][1] = (1 - (double) input[i][1]) * 7.95 * proportion;
                        if ((double) input[i][1] < 3.61 * proportion) {
                            input[i][1] = 3.61 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 6:
                        proportion = 0.0484;
                        input[i][1] = (1 - (double) input[i][1]) * 17.865 * proportion;
                        if ((double) input[i][1] < 9.54 * proportion) {
                            input[i][1] = 9.54 * proportion;
                        }
                        if ((double) input[i][1] > 35 * proportion) {
                            input[i][1] = 32.99 * proportion;
                        }
                        break;
                    case 7:
                        proportion = 0.044;
                        input[i][1] = (1 - (double) input[i][1]) * 21.63 * proportion;
                        if ((double) input[i][1] < 12.1 * proportion) {
                            input[i][1] = 12.1 * proportion;
                        }
                        if ((double) input[i][1] > 50 * proportion) {
                            input[i][1] = 46.1 * proportion;
                        }
                        break;
                    case 8:
                        proportion = 0.0395;
                        input[i][1] = (1 - (double) input[i][1]) * 16.07 * proportion;
                        if ((double) input[i][1] < 9.19 * proportion) {
                            input[i][1] = 9.19 * proportion;
                        }
                        if ((double) input[i][1] > 30 * proportion) {
                            input[i][1] = 27.2 * proportion;
                        }
                        break;
                    case 9:
                        proportion = 0.0419;
                        input[i][1] = (1 - (double) input[i][1]) * 7.5 * proportion;
                        if ((double) input[i][1] < 3.62 * proportion) {
                            input[i][1] = 3.62 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 10:
                        proportion = 0.0383;
                        input[i][1] = (1 - (double) input[i][1]) * 3.76 * proportion;
                        if ((double) input[i][1] < 1.95 * proportion) {
                            input[i][1] = 1.95 * proportion;
                        }
                        if ((double) input[i][1] > 10 * proportion) {
                            input[i][1] = 6.66 * proportion;
                        }
                        break;
                    case 11:
                        proportion = 0.0365;
                        input[i][1] = (1 - (double) input[i][1]) * 2.27 * proportion;
                        if ((double) input[i][1] < 1.13 * proportion) {
                            input[i][1] = 1.13 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.36 * proportion;
                        }
                        break;
                    case 12:
                        proportion = 0.001524;
                        input[i][1] = (1 - (double) input[i][1]) * 1.62 * proportion;
                        if ((double) input[i][1] < 0.86 * proportion) {
                            input[i][1] = 0.86 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.16 * proportion;
                        }
                        break;
                }
            }
        }
        return input;
    }
    /**
     * 去除最后预报的偏差较大的值
     */
    public void resultReasonable(Object[][] input, String location) {
        int month;
        for (int i = 0; i < input.length; i++) {
            if (location.equals("3号桥") || location.equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        if ((double) input[i][1] < 1.27) {
                            input[i][1] = 1.27;
                        }
                        if ((double) input[i][1] > 2.38) {
                            input[i][1] = 2.38;
                        }
                        break;
                    case 2:
                        if ((double) input[i][1] < 1.22) {
                            input[i][1] = 1.22;
                        }
                        if ((double) input[i][1] > 1.88) {
                            input[i][1] = 1.88;
                        }
                        break;
                    case 3:
                        if ((double) input[i][1] < 1.31) {
                            input[i][1] = 1.31;
                        }
                        if ((double) input[i][1] > 3.94) {
                            input[i][1] = 3.94;
                        }
                        break;
                    case 4:
                        if ((double) input[i][1] < 1.38) {
                            input[i][1] = 1.99;
                        }
                        if ((double) input[i][1] > 4.06) {
                            input[i][1] = 4.06;
                        }
                        break;
                    case 5:
                        if ((double) input[i][1] < 3.61) {
                            input[i][1] = 4.27;
                        }
                        if ((double) input[i][1] > 14.7) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 6:
                        if ((double) input[i][1] < 9.54) {
                            input[i][1] = 11.85;
                        }
                        if ((double) input[i][1] > 32.99) {
                            input[i][1] = 32.99;
                        }
                        break;
                    case 7:
                        if ((double) input[i][1] < 12.1) {
                            input[i][1] = 12.1;
                        }
                        if ((double) input[i][1] > 46.1) {
                            input[i][1] = 46.1;
                        }
                        break;
                    case 8:
                        if ((double) input[i][1] < 9.19) {
                            input[i][1] = 9.19;
                        }
                        if ((double) input[i][1] > 27.2) {
                            input[i][1] = 27.2;
                        }
                        break;
                    case 9:
                        if ((double) input[i][1] < 3.62) {
                            input[i][1] = 3.62;
                        }
                        if ((double) input[i][1] > 14.7) {
                            input[i][1] = 14.7;
                        }
                        break;
                    case 10:
                        if ((double) input[i][1] < 1.95) {
                            input[i][1] = 1.95;
                        }
                        if ((double) input[i][1] > 6.66) {
                            input[i][1] = 6.66;
                        }
                        break;
                    case 11:
                        if ((double) input[i][1] < 1.13) {
                            input[i][1] = 1.13;
                        }
                        if ((double) input[i][1] > 3.36) {
                            input[i][1] = 3.36;
                        }
                        break;
                    case 12:
                        if ((double) input[i][1] < 0.86) {
                            input[i][1] = 0.86;
                        }
                        if ((double) input[i][1] > 3.16) {
                            input[i][1] = 3.16;
                        }
                        break;
                }
            }
            //各个月份的区间比例
            else if (location.equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = tu.getSpecificDate(date).get("月");
                double proportion;
                switch (month) {
                    case 1:
                        proportion = 0.116;
                        if ((double) input[i][1] < 0.66 * proportion) {
                            input[i][1] = 0.66 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 2.38 * proportion;
                        }
                        break;
                    case 2:
                        proportion = 0.0957;
                        if ((double) input[i][1] < 0.57 * proportion) {
                            input[i][1] = 0.57 * proportion;
                        }
                        if ((double) input[i][1] > 3 * proportion) {
                            input[i][1] = 1.88 * proportion;
                        }
                        break;
                    case 3:
                        proportion = 0.538;
                        if ((double) input[i][1] < 0.68 * proportion) {
                            input[i][1] = 0.68 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.94 * proportion;
                        }
                        break;
                    case 4:
                        proportion = 0.316;
                        if ((double) input[i][1] < 1.38 * proportion) {
                            input[i][1] = 1.38 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 4.06 * proportion;
                        }
                        break;
                    case 5:
                        proportion = 0.072;
                        if ((double) input[i][1] < 3.61 * proportion) {
                            input[i][1] = 3.61 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 6:
                        proportion = 0.0484;
                        if ((double) input[i][1] < 9.54 * proportion) {
                            input[i][1] = 9.54 * proportion;
                        }
                        if ((double) input[i][1] > 35 * proportion) {
                            input[i][1] = 32.99 * proportion;
                        }
                        break;
                    case 7:
                        proportion = 0.044;
                        if ((double) input[i][1] < 12.1 * proportion) {
                            input[i][1] = 12.1 * proportion;
                        }
                        if ((double) input[i][1] > 50 * proportion) {
                            input[i][1] = 46.1 * proportion;
                        }
                        break;
                    case 8:
                        proportion = 0.0395;
                        if ((double) input[i][1] < 9.19 * proportion) {
                            input[i][1] = 9.19 * proportion;
                        }
                        if ((double) input[i][1] > 30 * proportion) {
                            input[i][1] = 27.2 * proportion;
                        }
                        break;
                    case 9:
                        proportion = 0.0419;
                        if ((double) input[i][1] < 3.62 * proportion) {
                            input[i][1] = 3.62 * proportion;
                        }
                        if ((double) input[i][1] > 20 * proportion) {
                            input[i][1] = 14.7 * proportion;
                        }
                        break;
                    case 10:
                        proportion = 0.0383;
                        if ((double) input[i][1] < 1.95 * proportion) {
                            input[i][1] = 1.95 * proportion;
                        }
                        if ((double) input[i][1] > 10 * proportion) {
                            input[i][1] = 6.66 * proportion;
                        }
                        break;
                    case 11:
                        proportion = 0.0365;
                        if ((double) input[i][1] < 1.13 * proportion) {
                            input[i][1] = 1.13 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.36 * proportion;
                        }
                        break;
                    case 12:
                        proportion = 0.001524;
                        if ((double) input[i][1] < 0.86 * proportion) {
                            input[i][1] = 0.86 * proportion;
                        }
                        if ((double) input[i][1] > 5 * proportion) {
                            input[i][1] = 3.16 * proportion;
                        }
                        break;
                }
            }
        }
    }


    /**
     * 根据本地文件的最末时间和预报时间判断是否需要补充数据
     */
    @SneakyThrows
    public String intervalData(ForecastInputParamNew paramForecastInputParamNew) {
        Date historyTime = InputUtils.historyDate;
        Date predictTime = paramForecastInputParamNew.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, -InputUtils.beforeDays);
        predictTime = calendar.getTime();
        String savePath = System.getProperty("java.io.tmpdir");
        TouTunHe touTunHe = new TouTunHe();
        Map<String, List<Map<String,List<PredictInputData>>>> stationsData = touTunHe.getOneStationDataList(paramForecastInputParamNew);
        //预报时间超过储存时间
        if (predictTime.after(historyTime) && ! stationsData.get("楼庄子").get(0).get("流量").isEmpty()) {
            //数据不足，补充新时段数据
            List<PredictInputData> Three = stationsData.get("3号桥").get(0).get("流量");
            List<PredictInputData> Lou = stationsData.get("楼庄子").get(0).get("流量");
            List<PredictInputData> Qu = stationsData.get("楼头区间").get(0).get("流量");
            List<DataWrite> data = new ArrayList<>();
            data.addAll(dataObject(Three,"3号桥"));
            data.addAll(dataObject(Lou,"楼庄子"));
            data.addAll(dataObject(Qu,"楼头区间"));
            ExcelTool.writeExcel(savePath+"HISTORY-DATA.xlsx",data);
            return savePath+"HISTORY-DATA.xlsx";
        }
        return "";
    }

    /**
     * 分断面进行输入数据处理
     */
    public List<DataWrite> dataObject(List<PredictInputData> input, String station) {
        List<DataWrite> result = new ArrayList<>();
        result.add(differentInput(input, station, "日"));
        result.add(differentInput(input, station, "旬"));
        result.add(differentInput(input, station, "月"));
        return result;
    }

    /**
     * 对数据进行整合处理
     */
    public DataWrite differentInput(List<PredictInputData> input, String station, String period) {
        DataWrite result = new DataWrite();
        String Option = station + period;
        Object[][] historyInput = InputUtils.historyData.get(Option);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = tu.ChangeDate(input, period);
        Object[][] machineInputData = listToObject(re, station);
        ForecastInputParam param = new ForecastInputParam();
        param.setPreStartTime(input.get(0).getDates());
        param.setPeriod(period);
        Object[][] data = dataIntegration(historyInput, machineInputData, param);
        result.setSheetName(Option);
        result.setData(data);
        return result;
    }

    public Object[][] dataIntegration(Object[][] historyInput, Object[][] preliminaryData, ForecastInputParam param) {
        Date dateEnd = (Date)historyInput[historyInput.length-1][0];
        Date dateStart = (param.getPreStartTime().before(dateEnd) ? param.getPreStartTime() : (Date) preliminaryData[0][0]);
        int duration = tu.duration(dateStart, dateEnd, param.getPeriod());
        if (duration < 0) {//输入数据在历史中没有
            duration = 0;
        }
        if (param.getPreStartTime().before(dateEnd)) {
            Object[][] result = new Object[historyInput.length - duration][historyInput[0].length];
            System.arraycopy(historyInput, 0, result, 0, historyInput.length - duration);
            return result;
        }
        return integration(historyInput, preliminaryData, duration,param);
    }

    /**
     * 输入数据
     */
    @SneakyThrows
    public Object[][] getDataInput(Object[][] historyInput, Object[][] preliminaryData, ForecastInputParam param) {
        Date dateEnd = InputUtils.historyDate;

        if (param.getPeriod().equals("日")) {//计算预报时间和历史记录时间的相差天数
            if (param.getPreStartTime().before(dateEnd)){
                return copyPartData(historyInput,param.getPreStartTime());
            }else {
                Date dateStart = (Date) preliminaryData[0][0];
                int duration = tu.duration(dateStart, dateEnd,"日");
                if (duration < 0) {//输入数据在历史中没有
                    duration = 0;
                }
                return integration(historyInput, preliminaryData, duration,param);
            }
        }
        else  {
            return copyPartData(historyInput,param.getCalibrationTime());
        }
    }

    public Object[][] copyPartData(Object[][] input,Date date){
        int l = 0;
        for (Object[] objects : input) {
            if (((Date) objects[0]).before(date)) {
                l++;
            }
        }
        Object[][] result = new Object[l][input[0].length];
        System.arraycopy(input, 0, result, 0, l);
        return result;
    }
    /**
     * 数据整合
     * @param historyInput    历史数据
     * @param preliminaryData 获取数据
     * @param dayDuration     之间的差距
     */
    public Object[][] integration(Object[][] historyInput, Object[][] preliminaryData, int dayDuration, ForecastInputParam param) {
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        if (param.getPeriod().equals("日")){
            input = new Object[hisLength + preLength - dayDuration][4];
            for (int i = 0; i < hisLength - dayDuration; i++) {
                System.arraycopy(historyInput[i], 0, input[i], 0, width);
            }
            for (int i = hisLength - dayDuration; i < hisLength + preLength - dayDuration; i++) {
                System.arraycopy(preliminaryData[i + dayDuration - hisLength], 0, input[i], 0, width);
            }
            return input;
        } else if (param.getPeriod().equals("旬")) {
            int days = tu.duration((Date)historyInput[historyInput.length-1][0],(Date) preliminaryData[0][0],"日");
            if (days<10){
                input = new Object[hisLength + preLength - 1][4];
                for (int i = 0; i < hisLength - 1; i++) {
                    System.arraycopy(historyInput[i], 0, input[i], 0, width);
                }
                for (int i = hisLength - 1; i < hisLength + preLength - 1; i++) {
                    System.arraycopy(preliminaryData[i + 1 - hisLength], 0, input[i], 0, width);
                }
            }else {
                input = new Object[hisLength + preLength][4];
                for (int i = 0; i < hisLength; i++) {
                    System.arraycopy(historyInput[i], 0, input[i], 0, width);
                }
                for (int i = hisLength; i < hisLength + preLength; i++) {
                    System.arraycopy(preliminaryData[i - hisLength], 0, input[i], 0, width);
                }
            }
            return input;
        }else {
            int months = tu.getSpecificDate((Date) preliminaryData[0][0]).get("月")-tu.getSpecificDate((Date) historyInput[historyInput.length-1][0]).get("月");
            if (months<1){
                input = new Object[hisLength + preLength - 1][4];
                for (int i = 0; i < hisLength - 1; i++) {
                    System.arraycopy(historyInput[i], 0, input[i], 0, width);
                }
                for (int i = hisLength - 1; i < hisLength + preLength - 1; i++) {
                    System.arraycopy(preliminaryData[i + 1 - hisLength], 0, input[i], 0, width);
                }
            }else {
                input = new Object[hisLength + preLength][4];
                for (int i = 0; i < hisLength; i++) {
                    System.arraycopy(historyInput[i], 0, input[i], 0, width);
                }
                for (int i = hisLength; i < hisLength + preLength; i++) {
                    System.arraycopy(preliminaryData[i - hisLength], 0, input[i], 0, width);
                }
            }
            return input;
        }

    }

    public Object[][] listToObject(List<PredictInputData> inputData, String location) {
        Object[][] machineInputData = new Object[inputData.size()][4];
        for (int i = 0; i < inputData.size(); i++) {
            machineInputData[i][0] = inputData.get(i).getDates();
            machineInputData[i][1] = (location.equals("楼头区间") ? inputData.get(i).getFlow() * setProportion(inputData.get(i).getDates()) : inputData.get(i).getFlow());
            machineInputData[i][2] = inputData.get(i).getTemperature();
            machineInputData[i][3] = inputData.get(i).getRainfall();
        }
        return machineInputData;
    }

    /**
     * 获取不同月份区间来水占头屯河比
     */
    public double setProportion(Date date) {
        int month = tu.getSpecificDate(date).get("月");
        double proportion = 0.058;
        switch (month) {
            case 1:
                proportion = 0.116;
                break;
            case 2:
                proportion = 0.0957;
                break;
            case 3:
                proportion = 0.538;
                break;
            case 4:
                proportion = 0.316;
                break;
            case 5:
                proportion = 0.072;
                break;
            case 6:
                proportion = 0.0484;
                break;
            case 7:
                proportion = 0.044;
                break;
            case 8:
                proportion = 0.0395;
                break;
            case 9:
                proportion = 0.0419;
                break;
            case 10:
                proportion = 0.0383;
                break;
            case 11:
                proportion = 0.0365;
                break;
            case 12:
                proportion = 0.001524;
                break;
        }
        return proportion;
    }
    /**
     * 中长期返回表格
     */
    public List<Flood> setLongFlood(Object[][] predict, ForecastInputParam param) {
        int l = param.getIsSnowMeltModel()?param.getPeriodStepNumber()* param.getPeriodStepSize()/24+1:param.getPeriodStepNumber()* param.getPeriodStepSize();
        List<Flood> result = new ArrayList<>();
        double peakFlood = 0;
        int t = 0;
        for (int i = 0; i < predict.length; i++) {
            if (peakFlood <= (double) predict[i][1]) {
                peakFlood = (double) predict[i][1];//洪峰
                t = i;
            }
        }
        int timeLength = 0;
        switch (param.getPeriod()){
            case "月":
                timeLength = 30*24*3600;
                break;
            case "旬":
                timeLength = 10*24*3600;
                break;
            case "日":
                timeLength = 24*3600;
        }
        //连续列的赋值
        for (int i = 0; i < l; i++) {
            Flood flood = new Flood();
            Date date = (Date) predict[i * param.getPeriodStepSize()][0];
            int days = getDays(param.getPeriod(),date);
            flood.setLocation(param.getLocation());
            flood.setScale(String.valueOf(timeLength));
            flood.setPeakIndex(0);
            flood.setTime((Date) predict[i * param.getPeriodStepSize()][0]);
            flood.setPreQ(Math.round((double) predict[i * param.getPeriodStepSize()][1] * 100.0) / 100.0);
            flood.setOutQ(Math.round((double) predict[i * param.getPeriodStepSize()][1] * 100.0) / 100.0);
            flood.setWaterLevel(0.0);
            flood.setPeakFlood(peakFlood);
            flood.setPeakTime((Date) predict[t][0]);
            flood.setFloodVolume(Math.round(3600 * 24 * days * param.getPeriodStepSize() * (double) predict[i * param.getPeriodStepSize()][1] / 10000 * 100.0) / 100.0);
            flood.setFloodLevel(judgingYearLeve(predict, param));
            flood.setRainProcess(0.0);
            result.add(flood);
        }
        return result;
    }
    /**
     * 获取各个尺度的日期
     * @param period
     * @param date
     * @return
     */
    public Integer getDays (String period,Date date){
        Integer[] days_Month = new Integer[]{31,28,31,30,31,30,31,31,30,31,30,31};
        Integer days = 1;
        switch (period){
            case "月":
                days = days_Month[tu.getSpecificDate(date).get("月")-1];
                break;
            case "旬":
                Integer month = tu.getSpecificDate(date).get("月")-1;
                Integer day = tu.getSpecificDate(date).get("日");
                if (day<=20){
                    days = 10;
                }else {
                    days = days_Month[month]-20;
                }
                break;
            case "日":
                days = 1;
                break;
        }
        return days;
    }
    /**
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     */
    public String judgingYearLeve(Object[][] input, ForecastInputParam param) {
        String result = "";
        double[] water = new double[input.length];
        if (param.getPeriod().equals("月")) {
            for (int i = 0; i < water.length; i++) {
                water[i] = (double) input[i][1] * 3600 * 24 * 30;
            }
            double waterSum = 0.0;
            for (double v : water) {
                waterSum += v;
            }
            waterSum = waterSum / 100000000;
            if (param.getLocation().equals("3号桥") || param.getLocation().equals("楼庄子")) {
                if (waterSum >= 2.476) {
                    result = "丰水年";
                }
                if (waterSum < 2.246 && waterSum >= 1.998) {
                    result = "平水年";
                }
                if (waterSum < 1.998) {
                    result = "枯水年";
                }
            }
            if (param.getLocation().equals("楼头区间")) {
                if (waterSum >= 0.1443) {
                    result = "丰水年";
                }
                if (waterSum < 0.1443 && waterSum >= 0.1164) {
                    result = "平水年";
                }
                if (waterSum < 0.1164) {
                    result = "枯水年";
                }
            }
        } else {
            result = "平水年";
        }
        return result;
    }
    @SneakyThrows
    public ModelSaveEntity setModelParams(ModelSaveEntity results, ForecastInputParam param, int k, List<List<Double>> paramResult ,List<List<Double>> maxminResult){
        List<Double> paramdim1 = new ArrayList<>();
        List<Double> paramdim2 = new ArrayList<>();
        List<Double> paramdim3 = new ArrayList<>();
        List<Double> paramvalue = new ArrayList<>();
        List<Double> context = new ArrayList<>();
        for (int i = 0; i < results.getParams().size(); i++) {
            paramdim1.add(Double.parseDouble(results.getParams().get(i).getParamDim1()));
            paramdim2.add(Double.parseDouble(results.getParams().get(i).getParamDim2()));
            paramdim3.add(Double.parseDouble(results.getParams().get(i).getParamDim3()));
            if (results.getParams().get(i).getValue().isNaN()) {
                results.getParams().get(i).setValue(1.0);
            }
            paramvalue.add(results.getParams().get(i).getValue());
        }
        for (int i = 0; i < results.getModels().get(0).getContext().length; i++) {
            context.add(results.getModels().get(0).getContext()[i]);
        }
        paramResult.add(paramdim1);
        paramResult.add(paramdim2);
        paramResult.add(paramdim3);
        paramResult.add(paramvalue);
        paramResult.add(context);
        String savePath = System.getProperty("java.io.tmpdir");
        List<Double> max = new ArrayList<>();
        List<Double> min = new ArrayList<>();
        for (int i = 0; i < results.getMaxmin().size(); i++) {
            max.add(results.getMaxmin().get(i).getMaxValue());//最大值
            min.add(results.getMaxmin().get(i).getMinValue());//最小值
        }
        maxminResult.add(max);
        maxminResult.add(min);
        if (k >= param.getVmdK() - 1) {
            //模型参数写入
            TemporaryXlsx temX = new TemporaryXlsx();
            String period = param.getPeriod();
            if (param.getPeriod().equals("日")){
                period = "日-"+ judgeKPF(param.getPreStartTime());
            }
            period = param.getIsSnowMeltModel()?"融雪":period;
            String location = param.getLocation();
            String pathParam = savePath + "MACHINE-PARAM.xlsx";
            String sheetName0 = location + period + "-模型参数";
            ExcelTool.writeList2DoubleExcel(pathParam, sheetName0, paramResult);
            temX.setUpdateParamPath(pathParam);
            //最大最小值写入
            String pathMaxmin = savePath + "MACHINE-MAXMIN.xlsx";
            String sheetName1 = location + period + "-最大最小值";
            ExcelTool.writeList2DoubleExcel(pathMaxmin, sheetName1, maxminResult);
            temX.setUpdateMaxPath(pathMaxmin);
            results.setTempXlsx(temX);
        }
        return results;
    }
    public String judgeKPF(Date date){
        String result;
        int month = tu.getSpecificDate(date).get("月");
        if (month == 1 || month == 2||month == 3 || month == 11|| month == 12){
            result = "丰";
        }
        else if (month == 4 || month == 5||month == 9 || month == 10) {
            result = "平";
        }
        else {
            result = "枯";
        }
        return result;
    }
}
