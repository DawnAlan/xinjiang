package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.entity.Flood;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MachineModel {

    DataUtils dataUtils = new DataUtils();

    InputUtils inputUtils = new InputUtils();

    TimeUtils timeUtils = new TimeUtils();

    /**
     * 数据驱动模型参数确定
     *
     * @param modelTrainInput
     * @param param
     * @return
     */
    public ForecastInputParam paramSet(Object[][] modelTrainInput, ForecastInputParam param) {
        //输入时段数的确定
        String period = param.getPeriod();
        param.setHistory_factor(1);
        switch (period) {
            case "月":
                param.setHistory_day(4);
                break;
            case "旬":
                param.setHistory_day(18);
                break;
            case "日":
                param.setHistory_day(30);
                break;
            case "小时":
                param.setHistory_day(24);
                break;
        }
        Integer[] inputIndex = new Integer[param.history_day];
        for (int i = 0; i < inputIndex.length; i++) {
            inputIndex[i] = i;
        }
        param.setInputIndex(inputIndex);//输入时段数
        //一些基础参数
        param.setNetClass(param.getModel());
        param.setMobp(0.08);
        param.setMinRate(0.0001);
        param.setMaxRate(0.03);
        String layers = param.getHistory_day() + ",14,14,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        param.setLayerCount(layers);
        param.setTrainNum(20000);
        param.setERROR(0.00001);
        param.setQ_max(200.0);
        param.setQ_min(0.0);
        //输入时间的确定
        Date startDate = (Date) modelTrainInput[param.history_day - 1][0];
        param.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) modelTrainInput[modelTrainInput.length / 4 * 3 - 1][0];
        param.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) modelTrainInput[modelTrainInput.length / 4 * 3 + param.history_day - 1][0];
        param.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) modelTrainInput[modelTrainInput.length - 1][0];
        param.setTestSetEndTime(testEndDate);//测试集结束时期
        param.setIsSnowMeltModel(false);
        return param;
    }

    /**
     * 数据驱动模型训练
     *
     * @param modelTrainInput
     * @param param
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void modelTrain(Object[][] modelTrainInput, ForecastInputParam param) throws IOException, InvalidFormatException {
        paramSet(modelTrainInput, param);//设置输入
        param.setIsRealtime(false);
        //数值的赋值
        int K = param.vmdK;//分解层数
        int length = modelTrainInput.length;
        int trainLength = length / 4 * 3;//训练集个数
        int historyDay = param.getHistory_day();//前期天数
        int outputNumber = modelTrainInput.length - historyDay * 2 + 2;

        Object[][] inputTemp = modelTrainInput;//输入数据，第一列为时间，第二列为历史径流
        Object[][] modelparaTemp = new Object[10][10];//初始化模型参数
        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][] vmdOutput;
        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        VMD vmd = new VMD();
        vmdOutput = vmd.vmd(vmdInput, K);

        //输入赋值
        Object[][] de_result = new Object[outputNumber + 1][K + 1];
        double[][] preResult = new double[outputNumber][1];//分解后的预测值

        List<List<Double>> paramResult = new ArrayList<>();
        double[][] maxmin = new double[param.getHistory_day() + 1][2 * param.getVmdK()];
        //K个子序列逐步训练
        for (int k = 0; k < K; k++) {
            Object[][] input = new Object[inputTemp.length][2];//多少列可以根据输入来改变
            Object[][] para = new Object[modelparaTemp.length][modelparaTemp[0].length];
            Object[][] maxminOld = new Object[maxmin.length][maxmin[0].length];
            List<Double> paramdim1 = new ArrayList<>();
            List<Double> paramdim2 = new ArrayList<>();
            List<Double> paramdim3 = new ArrayList<>();
            List<Double> paramvalue = new ArrayList<>();

            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];//时间
                input[i][1] = vmdOutput[k][i];
            }
            for (int i = 0; i < modelparaTemp.length; i++) {
                for (int j = 0; j < modelparaTemp[0].length; j++) {
                    para[i][j] = modelparaTemp[i][j];
                }
            }
            for (int i = 0; i < maxmin.length; i++) {
                for (int j = 0; j < maxmin[0].length; j++) {
                    maxminOld[i][j] = maxmin[i][j];
                }
            }

            /**
             *  分解后模型训练
             */
            LongForecast longForecast = new LongForecast();
            ModelSaveEntity results = longForecast.longTermForecast(param, input, maxminOld, para);

            for (int i = 0; i < results.getParams().size(); i++) {
                paramdim1.add(Double.parseDouble(results.getParams().get(i).getParamDim1()));
                paramdim2.add(Double.parseDouble(results.getParams().get(i).getParamDim2()));
                paramdim3.add(Double.parseDouble(results.getParams().get(i).getParamDim3()));
                if (results.getParams().get(i).getValue().isNaN()) {
                    results.getParams().get(i).setValue(1.0);
                }
                paramvalue.add(results.getParams().get(i).getValue());
            }
            paramResult.add(paramdim1);
            paramResult.add(paramdim2);
            paramResult.add(paramdim3);
            paramResult.add(paramvalue);

            for (int i = 0; i < maxmin.length; i++) {
                maxmin[i][2 * k] = results.getMaxmin().get(i).getMaxValue();//最大值
                maxmin[i][2 * k + 1] = results.getMaxmin().get(i).getMinValue();//最小值
            }

            if (k >= param.getVmdK() - 1) {
                //模型参数写入
                TemporaryXlsx temxParam = new TemporaryXlsx();
                List<TemporaryXlsx> paramList = new ArrayList<>();
                String period = param.getPeriod();
                String location = param.getLocation();
                String pathParam = inputUtils.paramPath + location + period + "-PARAM.xlsx";
                ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
                temxParam.setPath(pathParam);
                temxParam.setSheetName("模型参数");
                paramList.add(temxParam);
                results.setParamxlsx(paramList);
                //最大最小值写入
                TemporaryXlsx temxMaxmin = new TemporaryXlsx();
                List<TemporaryXlsx> maxminList = new ArrayList<>();
                String pathMaxmin = inputUtils.paramPath + location + period + "最大最小值.xlsx";
                ExcelTool.writeDoubleExcel(pathMaxmin, "最大最小值", maxmin);
                temxMaxmin.setPath(pathMaxmin);
                temxMaxmin.setSheetName("最大最小值");
                maxminList.add(temxMaxmin);
                results.setMaxminxlsx(maxminList);
            }

            //分解后数据储存
            de_result[0][0] = "时间";
            de_result[0][k + 1] = "预报流量";
            for (int i = 0; i < results.getResult().size(); i++) {
                de_result[i + 1][0] = results.getResult().get(i).getResultDate();
                de_result[i + 1][k + 1] = results.getResult().get(i).getSimOutput();
            }

        }


        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        //最终数据输出
        for (int i = 0; i < outputNumber; i++) {
            for (int j = 0; j < K; j++) {
                preResult[i][0] = preResult[i][0] + Double.parseDouble(de_result[i + 1][j + 1].toString());
            }
        }
        for (int i = 0; i < trainLength - historyDay + 1; i++) {
            reaResult[i][0] = Double.parseDouble(inputTemp[i + historyDay - 1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0] = vmdreaResult[i][0] + vmdOutput[j][i + historyDay - 1];
            }
        }
        for (int i = trainLength - historyDay + 1; i < outputNumber; i++) {
            reaResult[i][0] = Double.parseDouble(inputTemp[i + historyDay * 2 - 2][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0] = vmdreaResult[i][0] + vmdOutput[j][i + historyDay * 2 - 2];
            }
        }
        trainResult(de_result, reaResult, preResult, vmdreaResult, param);
    }

    /**
     * 数据驱动模型预报主函数
     *
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    public List<Flood> machineForecast(Object[][] inputTemp, ForecastInputParam param) throws IOException {
        MachineModel machineModel = new MachineModel();
        List<Flood> result = new ArrayList<>();
        machineModel.paramSet(inputTemp, param);//设置输入
        param.setIsRealtime(true);
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();

        //数值的赋值
        int K = param.vmdK;//分解层数
        int history_day = param.getHistory_day();//影响因子个数
        int outputNumber = inputTemp.length - history_day + 1;
        int l = param.getPeriodStepNumber() * param.getPeriodStepSize();
        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][] vmdOutput;

        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        VMD vmd = new VMD();
        vmdOutput = vmd.vmd(vmdInput, K);

        //输入赋值
        Object[][] de_result = new Object[l + 1][K + 1];
        Object[][] preResult = new Object[l][2];//分解后的预测值
        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] peakFlood;
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0] = Double.parseDouble(inputTemp[i + history_day - 1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0] = vmdreaResult[i][0] + vmdOutput[j][i + history_day - 1];
            }
        }

        for (int k = 0; k < K; k++) {
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
            //读取模型参数
            Object[][] paraTemp = ExcelTool.readExcel(paraPath, "模型参数");
            Object[][] input = new Object[inputTemp.length][2];
            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];
                input[i][1] = vmdOutput[k][i];
            }

            //径流预报
            peakFlood = realTimeForecast(param, input, maxminOldTemp, paraTemp);
            de_result[0][0] = "时间";
            de_result[0][k + 1] = "预报流量";
            for (int i = 1; i < peakFlood.length + 1; i++) {
                de_result[i][0] = peakFlood[i - 1][0];
                de_result[i][k + 1] = peakFlood[i - 1][1];
            }
        }
        for (int i = 0; i < l; i++) {
            preResult[i][1] = 0.0;//初始值
        }
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < K; j++) {
                preResult[i][0] = de_result[i + 1][0];
                preResult[i][1] = (double) preResult[i][1] + (double) de_result[i + 1][j + 1];
            }
        }
        dataUtils.resultProcessing(preResult, param);//恢复为径流量
        result = setLongFlood(preResult, param);
        return result;
    }

    /**
     * 实时预报代码
     *
     * @param input
     * @param param
     * @param maxminOld
     * @param paraTemp
     * @return
     */
    public Object[][] realTimeForecast(ForecastInputParam param, Object[][] input, Object[][] maxminOld, Object[][] paraTemp) {
        LongForecast longForecast = new LongForecast();
        param.setIsRealtime(true);
        int l = param.getPeriodStepNumber() * param.getPeriodStepSize();
        //日期赋值
        Date startDate = param.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        switch (param.getPeriod()) {
            case "月":
                dates = timeUtils.getMonthDateList(startDate, l);
                break;
            case "旬":
                dates = timeUtils.getDateList(startDate, l, 10, 0);
                break;
            case "日":
                dates = timeUtils.getDateList(startDate, l, 1, 0);
                break;
            default:
                dates = timeUtils.getDateList(startDate, l, 0, 1);
                break;
        }
        /**
         * 提供往前数据，下一天开始预报的功能
         */
        //预报赋值
        Object[][] predict = new Object[param.getPeriodStepSize() * param.getPeriodStepNumber() + 1][2];
        predict[0][1] = input[input.length - 1][1];
        for (int A = 1; A < param.getPeriodStepSize() * param.getPeriodStepNumber() + 1; A++) {
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
            ModelSaveEntity pre_results = longForecast.longTermForecast(param, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - param.getHistory_day() + A).getSimOutput();
            predict[A][1] = input[0][1];
        }
        Object[][] result = new Object[param.getPeriodStepSize() * param.getPeriodStepNumber()][2];//把前面predict的最后一行去掉
        for (int i = 0; i < param.getPeriodStepSize() * param.getPeriodStepNumber(); i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = predict[i][j];
            }
        }
        return result;
    }

    /**
     * 中长期返回表格
     *
     * @param predict
     * @param param
     * @return
     */
    public List<Flood> setLongFlood(Object[][] predict, ForecastInputParam param) {
        List<Flood> result = new ArrayList<>();
        double peakFlood = 0;
        int t = 0;
        for (int i = 0; i < predict.length; i++) {
            if (peakFlood <= (double) predict[i][1]) {
                peakFlood = (double) predict[i][1];//洪峰
                t = i;
            }
        }
        int days = 0;
        int timeLength = 0;
        switch (param.getPeriod()) {
            case "月":
                days = 30;
                timeLength = 30 * 24 * 3600;
                break;
            case "旬":
                days = 10;
                timeLength = 10 * 24 * 3600;
                break;
            case "日":
                days = 1;
                timeLength = param.getPeriodStepSize() * 24 * 3600;
                break;
        }
        //连续列的赋值
        for (int i = 0; i < param.getPeriodStepNumber(); i++) {
            Flood flood = new Flood();
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
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     *
     * @param input
     * @param param
     * @return
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

    /**
     * 方便检验训练成果
     *
     * @param de_result
     * @param reaResult
     * @param preResult
     * @param vmdreaResult
     * @param pvo
     * @return
     */
    public Object[][] trainResult(Object[][] de_result, double[][] reaResult, double[][] preResult,
                                  double[][] vmdreaResult, ForecastInputParam pvo) throws IOException, InvalidFormatException {
        Object[][] longResult = new Object[de_result.length][9];
        longResult[0][0] = "时间";
        longResult[0][1] = "实测流量";
        longResult[0][2] = "预报流量";
        longResult[0][3] = "分解流量";
        longResult[0][4] = "评价指标";
        longResult[1][4] = pvo.getNetClass() + "模型训练集";
        longResult[2][4] = pvo.getNetClass() + "模型测试集";
        longResult[0][5] = "均方差";
        longResult[0][6] = "平均相对误差";
        longResult[0][7] = "一致性系数";
        longResult[0][8] = "合格率";
        double rmse_train = 0;
        double dc_train = 0;
        double mre_train = 0;
        double qr_train = 0;
        double rmse_test = 0;
        double dc_test = 0;
        double mre_test = 0;
        double qr_test = 0;
        //结果赋值
        for (int i = 1; i < de_result.length; i++) {
            longResult[i][0] = de_result[i][0];
//            longResult[i][0] = i;
            longResult[i][1] = reaResult[i - 1][0];//实测流量
            longResult[i][2] = preResult[i - 1][0];//预报流量
            longResult[i][3] = vmdreaResult[i - 1][0];//vmd实测流量
        }
        String Option = pvo.getLocation() + pvo.getPeriod();
        double[][] trainPreResult = new double[reaResult.length / 4 * 3][1];
        double[][] testPreResult = new double[reaResult.length / 4][1];
        double[][] trainResult = new double[reaResult.length / 4 * 3][1];
        double[][] testResult = new double[reaResult.length / 4][1];
        for (int i = 0; i < reaResult.length / 4 * 3; i++) {
            trainResult[i][0] = reaResult[i][0];
            trainPreResult[i][0] = preResult[i][0];
        }
        for (int i = 0; i < reaResult.length / 4; i++) {
            testResult[i][0] = reaResult[i + reaResult.length / 4 * 3][0];
            testPreResult[i][0] = preResult[i + reaResult.length / 4 * 3][0];
        }
        rmse_train = MathUtils.RMSE(trainPreResult, trainResult);
        mre_train = MathUtils.MRE(trainPreResult, trainResult);
        dc_train = MathUtils.DC(trainPreResult, trainResult);
        qr_train = MathUtils.QualifyRate(trainPreResult, trainResult);
        rmse_test = MathUtils.RMSE(testPreResult, testResult);
        mre_test = MathUtils.MRE(testPreResult, testResult);
        dc_test = MathUtils.DC(testPreResult, testResult);
        qr_test = MathUtils.QualifyRate(testPreResult, testResult);
        DecimalFormat df = new DecimalFormat("#.###");
        longResult[1][5] = df.format(rmse_train);
        longResult[1][6] = df.format(mre_train);
        longResult[1][7] = df.format(dc_train);
        longResult[1][8] = df.format(qr_train * 100) + "%";
        longResult[2][5] = df.format(rmse_test);
        longResult[2][6] = df.format(mre_test);
        longResult[2][7] = df.format(dc_test);
        longResult[2][8] = df.format(qr_test * 100) + "%";
        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
        System.out.println("----------------------------------");
        System.out.printf("%-10.3f %-10.3f %-10.3f %-10.3f\n", rmse_test, mre_test, dc_test, qr_test);
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\" + pvo.getNetClass() + "-RESULT.xlsx", Option, longResult);
        return longResult;
    }

}

