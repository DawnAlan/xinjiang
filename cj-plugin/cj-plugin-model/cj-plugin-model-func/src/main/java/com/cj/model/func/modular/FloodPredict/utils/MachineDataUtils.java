package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.common.util.UUIDUtils;
import com.cj.model.func.modular.FloodPredict.entity.DataWrite;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.model.TouTunHe;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

public class MachineDataUtils {

    TimeUtils timeUtils  =new TimeUtils();
    /**
     * 输入数据区分部分（训练、测试、区分融雪、区分丰枯）
     */

    /**
     * 模型预报时的输入
     *
     * @param dataList
     * @param
     * @return
     */
    public double[][] inputData_Real(List<double[]> dataList, ForecastInputParam param) {
        /**
         * 前n旬流量+平均流量
         */
        int a = dataList.get(0).length;
        int n = param.getHistory_day();
//		 一般训练期：检验期=3:1
        double[][] data = new double[a - n + 1][n + 2];
        for (int i = 0; i < a - n + 1; i++) {
            data[i][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                data[i][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            data[i][n] = dataList.get(2)[i];//平均流量
            data[i][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }
        return data;
    }

    /**
     * 训练模型时的输入
     *
     * @param dataList
     * @param
     * @param isTest
     * @return
     * @throws Exception
     */
    public double[][] inputData_Train(List<double[]> dataList, ForecastInputParam param, boolean isTest) {

        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        int c = a - b;
        int n = param.getHistory_day();
        /**
         * 前n旬流量+平均流量
         */
//		 一般训练期：检验期=3:1
        double[][] trainData = new double[b - n + 1][n + 2];// 第一维样本数据的长度，第二维输入节点输出节点的值
        double[][] testData = new double[c - n + 1][n + 2];

        for (int i = 0; i < b - n + 1; i++) {
            trainData[i][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                trainData[i][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            trainData[i][n] = dataList.get(2)[i];//平均流量
            trainData[i][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }


        for (int i = b; i < a - n + 1; i++) {
            testData[i - b][0] = dataList.get(0)[i + n - 1];//时间戳
            for (int j = 0; j < n - 1; j++) {
                testData[i - b][j + 1] = dataList.get(1)[i + j];//前N旬流量
            }
            testData[i - b][n] = dataList.get(2)[i];//平均流量
            testData[i - b][n + 1] = dataList.get(1)[i + n - 1];//预报径流
        }

        if (isTest) {
            return testData;
        } else {
            return trainData;
        }

    }

    /**
     * 融雪模型数据输入
     *
     * @param dataList
     * @param param
     * @return
     */
    public double[][] inputData_Real_Snow(List<double[]> dataList, ForecastInputParam param) {
        /**
         * 前3天流量+前3天温度+前3天降水
         */
        int a = dataList.get(0).length;
        int n = param.getHistory_day();
        int m = 0;//前m天的数据
        if (dataList.size() > 4) {//有降雨数据
            m = 3;
        } else {
            m = 2;
        }
        double[][] data = new double[a - n][n * m + 2];
        for (int i = 0; i < a - n; i++) {
            data[i][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                data[i][j + 1] = dataList.get(1)[i + j];//前N天流量
                data[i][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {//有降雨数据
                    data[i][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }

            data[i][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }
        return data;
    }

    /**
     * 融雪模型训练数据输入
     *
     * @param dataList
     * @param param
     * @param isTest
     * @return
     * @throws Exception
     */
    public double[][] inputData_Train_Snow(List<double[]> dataList, ForecastInputParam param, boolean isTest) {

        int a = dataList.get(0).length;
        int b = a / 4 * 3;
        int c = a - b;
        int n = param.getHistory_day();//前期天数
        int m = 0;
        if (dataList.size() > 4) {//有降雨数据
            m = 3;
        } else {
            m = 2;
        }
        /**
         * 前n旬流量+温度+降水
         */
//		 一般训练期：检验期=3:1
        double[][] trainData = new double[b - n][n * m + 2];
        double[][] testData = new double[c - n][n * m + 2];

        for (int i = 0; i < b - n; i++) {
            trainData[i][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                trainData[i][j + 1] = dataList.get(1)[i + j];//前N天流量
                trainData[i][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {
                    trainData[i][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }
            trainData[i][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }

        for (int i = b; i < a - n; i++) {
            testData[i - b][0] = dataList.get(0)[i + n];//时间戳
            for (int j = 0; j < n; j++) {
                testData[i - b][j + 1] = dataList.get(1)[i + j];//前N天流量
                testData[i - b][j + 1 + n] = dataList.get(3)[i + j];//温度
                if (dataList.size() > 4) {
                    testData[i - b][j + 1 + 2 * n] = dataList.get(4)[i + j];//降水
                }
            }
            testData[i - b][n * m + 1] = dataList.get(1)[i + n];//预报径流
        }
        if (isTest) {
            return testData;
        } else {
            return trainData;
        }
    }

    /**
     * 获得丰水期和枯水期的预报开始时间和预报数量
     *
     * @param param
     * @return result.get(0)丰水期
     * result.get(1)枯水期
     */
    public List<Object[]> getSelectedData(ForecastInputParam param) {
        List<Object[]> result = new ArrayList<>();
        Object[] Feng = new Object[2];
        Object[] Ku = new Object[2];
        Date dateStart = param.getPreStartTime();
        int number = param.getPeriodStepNumber() * param.getPeriodStepSize();
        Date[][] date = new Date[number][1];
        int fengNumber = 0;
        int kuNumber = 0;
        int month = 0;
        switch (param.getPeriod()) {
            case "月":
                date = timeUtils.getMonthDateList(dateStart, number);
                break;
            case "旬":
                date = timeUtils.getDateList(dateStart, number, 10, 0);
                break;
            case "日":
                date = timeUtils.getDateList(dateStart, number, 1, 0);
                break;
        }
        for (int i = 0; i < number; i++) {
            month = timeUtils.getSpecificDate(date[i][0]).get("月");
            if (month <= 9 && month >= 5) {
                fengNumber++;
            } else {
                kuNumber++;
            }
        }
        month = timeUtils.getSpecificDate(date[0][0]).get("月");
        if (month <= 9 && month >= 5) {
            Feng[0] = date[0];
            for (int i = 0; i < number; i++) {
                month = timeUtils.getSpecificDate(date[i][0]).get("月");
                if (month == 10) {
                    Ku[0] = date[i];
                    break;
                }
            }
        } else {
            Ku[0] = date[0];
            for (int i = 0; i < number; i++) {
                month = timeUtils.getSpecificDate(date[i][0]).get("月");
                if (month == 5) {
                    Feng[0] = date[i];
                    break;
                }
            }
        }
        Feng[1] = fengNumber;
        Ku[1] = kuNumber;
        result.add(Feng);
        result.add(Ku);
        return result;
    }

    /**
     * 筛选枯水期丰水期,5~9月为丰水期
     *
     * @param input
     * @param preStartTime
     * @return
     */
    public Object[][] SelectDate(Object[][] input, Date preStartTime) {
        List<Object[]> KuData = new ArrayList<>();
        Object[] kudata = new Object[input[0].length];
        List<Object[]> FengData = new ArrayList<>();
        Object[] fengdata = new Object[input[0].length];
        for (int i = 0; i < input.length; i++) {
            Date time = (Date) input[i][0];
            int month = timeUtils.getSpecificDate(time).get("月");
            kudata = new Object[input[0].length];
            fengdata = new Object[input[0].length];
            if (month <= 4 || month >= 10) {
                for (int j = 0; j < input[0].length; j++) {
                    kudata[j] = input[i][j];
                }
                KuData.add(kudata);
            } else {
                for (int j = 0; j < input[0].length; j++) {
                    fengdata[j] = input[i][j];
                }
                FengData.add(fengdata);
            }
        }
        Date time2 = preStartTime;
        int month2 = timeUtils.getSpecificDate(time2).get("月");
        Object[][] longForecastInput;
        if (month2 <= 4 || month2 >= 10) {
            longForecastInput = new Object[KuData.size()][input[0].length];
            for (int i = 0; i < KuData.size(); i++) {
                longForecastInput[i] = KuData.get(i);
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
     * @return
     */
    public Object[][] snowMeltDate(Object[][] input, String location) {
        List<Object[]> Data = new ArrayList<>();
        Object[] data;
        int factor = 3;
        List<Object[]> snowData = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (!(input[i][3] instanceof String)) {
//                if ((double) input[i][3] == 0.0) {
//                    snowData.add(input[i]);
//                }
                snowData.add(input[i]);
            }
        }
        for (int i = 0; i < snowData.size(); i++) {
            Date time = (Date) snowData.get(i)[0];
            int month = timeUtils.getSpecificDate(time).get("月");
            data = new Object[factor];
            if (location.equals("楼头区间")) {
                if (month == 3) {
                    for (int j = 0; j < factor; j++) {
                        data[j] = snowData.get(i)[j];
                    }
                    Data.add(data);
                }
            } else {
                if (month >= 5 && month <= 7) {
                    for (int j = 0; j < factor; j++) {
                        data[j] = snowData.get(i)[j];
                    }
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

    /**
     * 径流数据处理部分（距平值）
     */

    /**
     * 输入数据的处理，求与均值之间的偏差
     *
     * @param input
     * @param param
     * @return
     */
    public Object[][] inputProcessing(Object[][] input, ForecastInputParam param) {
        int month = 0;
        for (int i = 0; i < input.length; i++) {
            if (param.getLocation().equals("3号桥") || param.getLocation().equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1.29 - (double) input[i][1]) / 1.29;
                        break;
                    case 2:
                        input[i][1] = (1.16 - (double) input[i][1]) / 1.16;
                        break;
                    case 3:
                        input[i][1] = (1.44 - (double) input[i][1]) / 1.44;
                        break;
                    case 4:
                        input[i][1] = (2.57 - (double) input[i][1]) / 2.57;
                        break;
                    case 5:
                        input[i][1] = (7.95 - (double) input[i][1]) / 7.95;
                        break;
                    case 6:
                        input[i][1] = (17.865 - (double) input[i][1]) / 17.865;
                        break;
                    case 7:
                        input[i][1] = (21.63 - (double) input[i][1]) / 21.63;
                        break;
                    case 8:
                        input[i][1] = (16.07 - (double) input[i][1]) / 16.07;
                        break;
                    case 9:
                        input[i][1] = (7.5 - (double) input[i][1]) / 7.5;
                        break;
                    case 10:
                        input[i][1] = (3.76 - (double) input[i][1]) / 3.76;
                        break;
                    case 11:
                        input[i][1] = (2.27 - (double) input[i][1]) / 2.27;
                        break;
                    case 12:
                        input[i][1] = (1.62 - (double) input[i][1]) / 1.62;
                        break;
                }
            } else if (param.getLocation().equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                switch (month) {
                    case 1:
                        input[i][1] = (1.29 * 0.116 - (double) input[i][1]) / 1.29 * 0.116;
                        break;
                    case 2:
                        input[i][1] = (1.16 * 0.0957 - (double) input[i][1]) / 1.16 * 0.0957;
                        break;
                    case 3:
                        input[i][1] = (1.44 * 0.538 - (double) input[i][1]) / 1.44 * 0.538;
                        break;
                    case 4:
                        input[i][1] = (2.57 * 0.316 - (double) input[i][1]) / 2.57 * 0.316;
                        break;
                    case 5:
                        input[i][1] = (7.95 * 0.072 - (double) input[i][1]) / 7.95 * 0.072;
                        break;
                    case 6:
                        input[i][1] = (17.865 * 0.0484 - (double) input[i][1]) / 17.865 * 0.0484;
                        break;
                    case 7:
                        input[i][1] = (21.63 * 0.044 - (double) input[i][1]) / 21.63 * 0.044;
                        break;
                    case 8:
                        input[i][1] = (16.07 * 0.0395 - (double) input[i][1]) / 16.07 * 0.0395;
                        break;
                    case 9:
                        input[i][1] = (7.5 * 0.0419 - (double) input[i][1]) / 7.5 * 0.0419;
                        break;
                    case 10:
                        input[i][1] = (3.76 * 0.0383 - (double) input[i][1]) / 3.76 * 0.0383;
                        break;
                    case 11:
                        input[i][1] = (2.27 * 0.0365 - (double) input[i][1]) / 2.27 * 0.0365;
                        break;
                    case 12:
                        input[i][1] = (1.62 * 0.001524 - (double) input[i][1]) / 1.62 * 0.001524;
                        break;
                }
            }
        }
        return input;
    }

    /**
     * 将最后预测的相对误差转换为实际径流
     *
     * @param input
     * @return
     */
    public Object[][] resultProcessing(Object[][] input, ForecastInputParam param) {
        int month;
        for (int i = 0; i < input.length; i++) {
            if (param.getLocation().equals("3号桥") || param.getLocation().equals("楼庄子")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
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
            else if (param.getLocation().equals("楼头区间")) {
                Date date = (Date) input[i][0];
                month = timeUtils.getSpecificDate(date).get("月");
                double proportion = 0.058;
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


    public ForecastInputParam paramConvert(ForecastInputParamNew forecastParam) {
        ForecastInputParam param = new ForecastInputParam();
        //模型类型
        param.setIsRealtime(true);
        param.setIsShortForecast(forecastParam.getModelType() == 3);
        //预报时间
        Date date = forecastParam.getPredictionTime();
        param.setPreStartTime(date);
        //时段
        if (forecastParam.getPeriodTimeType() == 1) {
            param.setPeriod("月");
        } else if (forecastParam.getPeriodTimeType() == 2) {
            param.setPeriod("旬");
        } else if (forecastParam.getPeriodTimeType() == 3) {
            param.setPeriod("日");
        } else if (forecastParam.getPeriodTimeType() == 4) {
            param.setPeriod("日");
        }
        if (forecastParam.getIsSimulation() == null) {
            param.setIsSimulation(false);
        } else {
            param.setIsSimulation(forecastParam.getIsSimulation());
        }
        //预报长度
        int l = forecastParam.getPeriodTimeStep();
        int n = forecastParam.getPeriodTimeNum();
        param.setPeriodStepSize(l);
        param.setPeriodStepNumber(n);
        param.setPreFlow(forecastParam.getPreFlow());
        param.setPreRainFall(forecastParam.getPreRainFall());
        param.setParamMap(forecastParam.getParamMap());
        return param;
    }

    /**
     * 根据本地文件的最末时间和预报时间判断是否需要补充数据
     *
     * @param paramForecastInputParamNew
     * @throws IOException
     * @throws ParseException
     * @throws InvalidFormatException
     */
    public String intervalData(ForecastInputParamNew paramForecastInputParamNew)
            throws IOException, ParseException {
        Object[][] historyInput = InputUtils.historyData.get("楼庄子日");
        Date historyTime = (Date) historyInput[historyInput.length - 1][0];
        Date predictTime = paramForecastInputParamNew.getPredictionTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(predictTime);
        calendar.add(Calendar.DAY_OF_MONTH, -InputUtils.beforeDays);
        predictTime = calendar.getTime();
        String savePath = System.getProperty("java.io.tmpdir");
        //预报时间超过储存时间
        if (predictTime.after(historyTime)) {
            //数据不足，补充新时段数据
            TouTunHe touTunHe = new TouTunHe();
            Map<String, List<List<PredictInputData>>> stationsData = touTunHe.getOneStationDataList(paramForecastInputParamNew);
            List<List<PredictInputData>> Three = stationsData.get("3号桥");
            List<List<PredictInputData>> Lou = stationsData.get("楼庄子");
            List<List<PredictInputData>> Qu = stationsData.get("楼头区间");
            List<DataWrite> data = new ArrayList<>();
            data.addAll(dataObject(Three,"3号桥"));
            data.addAll(dataObject(Lou,"楼庄子"));
            data.addAll(dataObject(Qu,"楼头区间"));
            ExcelTool.writeExcel(savePath + File.separator + "HISTORY-DATA.xlsx",data);
        }
        return savePath + File.separator + "HISTORY-DATA.xlsx";
    }

    /**
     * 分断面进行输入数据处理
     *
     * @param input
     * @param station
     * @throws IOException
     * @throws InvalidFormatException
     */
    public List<DataWrite> dataObject(List<List<PredictInputData>> input, String station)
            throws IOException {
        List<DataWrite> result = new ArrayList<>();
        result.add(differentInput(input, station, "日"));
        result.add(differentInput(input, station, "旬"));
        result.add(differentInput(input, station, "月"));
        return result;
    }

    /**
     * 对数据进行整合处理
     *
     * @param input
     * @param station
     * @param period
     * @throws IOException
     * @throws InvalidFormatException
     */
    public DataWrite differentInput(List<List<PredictInputData>> input, String station, String period)
            throws IOException {
        DataWrite result = new DataWrite();
        String Option = station + period;
        Object[][] historyInput = InputUtils.historyData.get(Option);
        //数据驱动模型输入
        List<PredictInputData> machineData = input.get(0);
        //数据驱动模型数据输入尺度转换
        List<PredictInputData> re = timeUtils.ChangeDate(machineData, period);
        Object[][] machineInputData = listToObject(re, station);
        ForecastInputParam param = new ForecastInputParam();
        param.setPreStartTime(input.get(0).get(0).getDates());
        param.setPeriod(period);
        Object[][] data = dataIntegration(historyInput, machineInputData, param);
        result.setSheetName(Option);
        result.setData(data);
        return result;
    }

    /**
     * 历史数据与前期数据的整合
     *
     * @param historyInput
     * @param preliminaryData
     * @return
     */
    public Object[][] dataIntegration(Object[][] historyInput, Object[][] preliminaryData, ForecastInputParam param) {
        Date dateEnd = (Date) historyInput[historyInput.length - 1][0];
        Date dateStart = (param.getPreStartTime().before(dateEnd) ? param.getPreStartTime() : (Date) preliminaryData[0][0]);
        int duration = 0;
        if (param.getPeriod().equals("日")) {//计算预报时间和历史记录时间的相差天数
            duration = timeUtils.duration(dateStart, dateEnd, "日");
        }
        if (param.getPeriod().equals("旬")) {
            duration = timeUtils.xunDuration(dateStart, dateEnd);
        }
        if (param.getPeriod().equals("月")) {
            duration = timeUtils.duration(dateStart, dateEnd, "月");
        }
        if (duration < 0) {//输入数据在历史中没有
            duration = 0;
        }
        if (param.getPreStartTime().before(dateEnd)) {
            Object[][] result = new Object[historyInput.length - duration][historyInput[0].length];
            System.arraycopy(historyInput, 0, result, 0, historyInput.length - duration);
            return result;
        }
        Object[][] result = integration(historyInput, preliminaryData, duration);
        return result;
    }

    /**
     * 数据整合
     *
     * @param historyInput    历史数据
     * @param preliminaryData 获取数据
     * @param dayDuration     之间的差距
     * @return
     */
    public Object[][] integration(Object[][] historyInput, Object[][] preliminaryData, int dayDuration) {
        int hisLength = historyInput.length;
        int preLength = preliminaryData.length;
        int width = historyInput[0].length;
        Object[][] input;
        input = new Object[hisLength + preLength - dayDuration][4];
        for (int i = 0; i < hisLength - dayDuration; i++) {
            System.arraycopy(historyInput[i], 0, input[i], 0, width);
        }
        for (int i = hisLength - dayDuration; i < hisLength + preLength - dayDuration; i++) {
            System.arraycopy(preliminaryData[i + dayDuration - hisLength], 0, input[i], 0, width);
        }
        return input;
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
     *
     * @param
     * @return
     */
    public double setProportion(Date date) {
        int month = timeUtils.getSpecificDate(date).get("月");
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
}
