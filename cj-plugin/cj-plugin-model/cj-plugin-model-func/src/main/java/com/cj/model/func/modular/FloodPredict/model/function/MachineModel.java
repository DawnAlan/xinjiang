package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MachineModel {

    MachineDataUtils machineDataUtils = new MachineDataUtils();

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
                param.setHistory_day(24);
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
        String layers = param.getHistory_day() + ",30,30," + param.getPredict_day();//，输入前几个时段径流，k为输入的因素数量输出未来流量
        param.setLayerCount(layers);
        param.setTrainNum(20000);
        param.setERROR(0.0001);
        param.setQ_max(1.0);
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
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public ModelSaveEntity modelTrain(Object[][] inputTemp, ForecastInputParam param) {
        paramSet(inputTemp, param);//设置输入
        param.setIsRealtime(false);
        int l = inputTemp.length;
        int K = param.vmdK;
        int outputNumber = l - 2 * (param.getHistory_day() + param.getPredict_day() - 1);
        double[][] vmdOutput = vmdOutput(inputTemp,K);//分解
        //输入赋值
        Object[][] de_result = new Object[outputNumber * param.getPredict_day()][K + 1];
        double[][] preResult = new double[outputNumber][K+param.getPredict_day()];//分解后的预测值
        List<List<Double>> paramResult = new ArrayList<>();
        double[][] maxmin = new double[param.getHistory_day() + param.getPredict_day()][2 * param.getVmdK()];
        Object[][] modelparaTemp = new Object[10][10];
        ModelSaveEntity results = new ModelSaveEntity();
        int[] historyDays = new int[K];
        if (param.getPeriod().equals("月")){
            historyDays = new int[]{70,12,6,6,12,6,6,30,6,6,30,16};
        } else if (param.getPeriod().equals("旬")) {
            historyDays = new int[]{40,30,10,16,6,6,6,30,6,36,8,38};
        }
        //K个子序列逐步训练
        for (int k = 0; k < K; k++) {
            String layer = historyDays[k] + ",30,30," + param.getPredict_day();//，输入前几个时段径流，k为输入的因素数量输出未来流量
            param.setLayerCount(layer);
            Object[][] input = new Object[l][2];//多少列可以根据输入来改变
            Object[][] para = new Object[modelparaTemp.length][modelparaTemp[0].length];
            Object[][] maxminOld = new Object[maxmin.length][maxmin[0].length];


            for (int i = 0; i < l; i++) {
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
            results = longForecast.longTermForecast(param, input, maxminOld, para);
            //模型参数的存储
//            results = machineDataUtils.setModelParams(results,param,k,paramResult,maxmin);
            //分解后数据储存
            for (int i = 0; i < results.getResult().size(); i++) {
                for (int j = 0; j < param.getPredict_day(); j++) {
                    de_result[i * param.getPredict_day() + j][0] = results.getResult().get(i).getResultDate()[j];
                    de_result[i * param.getPredict_day() + j][k + 1] = results.getResult().get(i).getSimOutput()[j];
                }
            }
        }
        //训练结果与指标的储存
        //trainResult(de_result, inputTemp,preResult, param);
        return results;
    }

    /**
     * 数据驱动模型预报主函数
     *
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    @SneakyThrows
    public List<Flood> machineForecast(Object[][] inputTemp, ForecastInputParam param) {
        MachineModel machineModel = new MachineModel();
        List<Flood> result = new ArrayList<>();
        machineModel.paramSet(inputTemp, param);//设置输入
        param.setIsRealtime(true);
        String location = param.getLocation().equals("3号桥") ? "楼庄子" : param.getLocation();
        String period = param.getPeriod();
        int m = 0;//输入时序的长度
        for (Object[] temp : inputTemp) {
            Date date = (Date) temp[0];
            if (date.before(param.getPreStartTime()) && date.before(param.getCalibrationTime())) {
                m++;
            }
        }
        int p = 0;//输出时序的长度，预报截止时间到率定时间的长度
        if (param.getPreStartTime().after(param.getCalibrationTime())){
            p = timeUtils.duration(param.getCalibrationTime(), param.getPreStartTime(), param.getPeriod())-1;
            p = Math.max(p, 0);
        }
        int n = param.getPeriodStepNumber();//前段输入的预报时段
        param.setPeriodStepNumber(p+n);
        //数值的赋值
        int K = param.vmdK;//分解层数
        int history_day = param.getHistory_day();//影响因子个数
        int outputNumber = inputTemp.length - history_day + 1;
        int l = param.getPeriodStepNumber() * param.getPeriodStepSize();
        double[][] vmdOutput = vmdOutput(inputTemp,K);//分解

//        InputUtils.getData(param.getFilePath());
//        InputUtils.getData2(param.getFilePath());
        //输入赋值
        Object[][] de_result = new Object[l + 1][K + 1];
        Object[][] preResult = new Object[l][2];//分解后的预测值
        for (int i = 0; i < l; i++) {preResult[i][1] = 0.0;}//初始值
        Object[][] result1 = new Object[l][K + 1];//de_result去除第一行
        double[][] vmdreaResult = new double[outputNumber][K + 1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] peakFlood;

        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0] = Double.parseDouble(inputTemp[i + history_day - 1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][j+1] = vmdOutput[j][i + history_day - 1];
                vmdreaResult[i][0] += vmdOutput[j][i + history_day - 1];
            }
        }
        int[] historyDays = new int[K];
        if (param.getPeriod().equals("月")){
            historyDays = new int[]{70,12,6,6,12,6,6,30,6,6,30,16};
        } else if (param.getPeriod().equals("旬")) {
            historyDays = new int[]{40,30,10,16,6,6,6,30,6,36,8,38};
        }
        for (int k = 0; k < K; k++) {
            String lay = historyDays[k] + ",30,30," + param.getPredict_day();//，输入前几个时段径流，k为输入的因素数量输出未来流量
            param.setLayerCount(lay);
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            Object[][] maxminOldTemp = InputUtils.machineMaxMin.get(location + period + "-最大最小值");
            //读取模型参数
            Object[][] paraTemp = InputUtils.machineParam.get(location + period + "-模型参数");
            Object[][] maxmin = new Object[maxminOldTemp.length][2];
            for (int i = 0; i < maxminOldTemp.length; i++) {
                for (int j = 0; j < 2; j++) {
                    maxmin[i][j] = maxminOldTemp[i][j+2*k];
                }
            }
            Object[][] para = new Object[paraTemp.length][5];
            String[] layer = param.getLayerCount().split(",");
            int[] layers = new int[layer.length];
            for(int i = 0; i < layer.length; i++){
                layers[i] = Integer.parseInt(layer[i]);
            }
            int layersNum = layers[2];
            for (int i = 0; i < paraTemp.length; i++) {
                if (i<layersNum){
                    for (int j = 0; j < 5; j++) {
                        para[i][j] = paraTemp[i][j+5*k];
                    }
                }else {
                    for (int j = 0; j < 4; j++) {
                        para[i][j] = paraTemp[i][j+5*k];
                    }
                }
            }
            Object[][] input = new Object[m][2];
            for (int i = 0; i < m; i++) {
                input[i][0] = inputTemp[i][0];
                input[i][1] = vmdOutput[k][i];
            }
            //径流预报
            peakFlood = realTimeForecast(param, input, maxmin, para);
            de_result[0][0] = "时间";
            de_result[0][k + 1] = "预报流量";
            for (int i = 1; i < peakFlood.length + 1; i++) {
                de_result[i][0] = peakFlood[i - 1][0];
                de_result[i][k + 1] = peakFlood[i - 1][1];
            }
        }
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < K; j++) {
                preResult[i][0] = de_result[i + 1][0];
                preResult[i][1] = (double) preResult[i][1] + (double) de_result[i + 1][j + 1];
            }
        }
        for (int i = 0; i < result1.length; i++) {
            for (int j = 0; j < result1[0].length; j++) {
                result1[i][j]=de_result[i+1][j];
            }
        }
        if (param.getIsAverage()){
            machineDataUtils.resultProcessing(preResult, param.getLocation());//恢复为径流量
        }
        machineDataUtils.resultReasonable(preResult,param.getLocation());
//        ExcelTool.writeLastingExcel("D:\\204\\2.头屯河\\径流预报数据文件\\" + param.getNetClass() + "-RESULT.xlsx", "分解预测值", result1);
        for (int i = 0; i < preResult.length; i++) {
            System.out.println(preResult[i][1]);
        }
        param.setPeriodStepNumber(n);
        Object[][] preResult_1 = new Object[n][2];//分解后的预测值
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2; j++) {
                preResult_1[i][j] = preResult[i+p][j];
            }
        }
        result = machineDataUtils.setLongFlood(preResult_1, param);
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
        int m = input.length - param.getHistory_day() - param.getPredict_day();
        //日期赋值
        Date startDate = param.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        switch (param.getPeriod()) {
            case "月":
                dates = timeUtils.getMonthDateList(startDate, l, param.getPredict_day());
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
        if (param.getPredict_day() == 1) {
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
                predict[A - 1][1] = pre_results.getResult().get(m + A).getSimOutput()[0];
                predict[A][1] = input[0][1];
            }
        } else {
            Object[][] pre_input = new Object[input.length + 1][2];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    pre_input[i][j] = input[i][j];
                }
            }
            pre_input[input.length][0] = dates[0][0];
            pre_input[input.length][1] = predict[0][1];
            ModelSaveEntity pre_results = longForecast.longTermForecast(param, pre_input, maxminOld, paraTemp);
            for (int i = 0; i < param.getPredict_day(); i++) {
                predict[i][0] = pre_results.getResult().get(m).getResultDate()[i];
                predict[i][1] = pre_results.getResult().get(m).getSimOutput()[i];
            }
        }

        Object[][] result = new Object[param.getPeriodStepSize() * param.getPeriodStepNumber()][2];//把前面predict的最后一行去掉
        for (int i = 0; i < param.getPeriodStepSize() * param.getPeriodStepNumber(); i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = predict[i][j];
            }
        }
        return result;
    }

     public double[][] vmdOutput(Object[][] inputTemp,int K){
         //VMD分解
         double[] vmdInput = new double[inputTemp.length];
         for (int i = 0; i < inputTemp.length; i++) {
             vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
         }
         VMD vmd = new VMD();
         double[][] vmdOutput = vmd.vmd(vmdInput, K);
         return vmdOutput;
     }


    /**
     * 方便检验训练成果
     *
     * @param de_result
     * @param preResult
     * @param param
     * @return
     */
    public void trainResult(Object[][] de_result, Object[][] inputTemp,double[][] preResult, ForecastInputParam param) {

        int l = inputTemp.length;
        //数值的赋值
        int K = param.vmdK;//分解层数
        int trainLength = l / 4 * 3;//训练集个数
        int historyDay = param.getHistory_day();//前期天数
        int outputNumber = l - 2 * (param.getHistory_day() + param.getPredict_day() - 1);

        double[][] vmdrealResult = new double[outputNumber][K+param.getPredict_day()];//分解后的实际值
        double[][] reaResult = new double[outputNumber][param.getPredict_day()];//真实值
        double[][] vmdOutput = vmdOutput(inputTemp,K);//分解
        //最终数据输出
        for (int i = 0; i < outputNumber; i++) {
            for (int k = 0; k < K; k++) {
                for (int j = 0; j < param.getPredict_day(); j++) {
                    preResult[i][param.getPredict_day()+k] = Double.parseDouble(de_result[i * param.getPredict_day() + j][k + 1].toString());
                    preResult[i][j] += Double.parseDouble(de_result[i * param.getPredict_day() + j][k + 1].toString());
                }
            }
        }
        for (int i = 0; i < trainLength - historyDay - param.getPredict_day() + 1; i++) {
            for (int j = 0; j < param.getPredict_day(); j++) {
                reaResult[i][j] = Double.parseDouble(inputTemp[i + historyDay + param.getPredict_day() - 1 + j][1].toString());
                for (int k = 0; k < K; k++) {
                    vmdrealResult[i][param.getPredict_day()+k] = vmdOutput[k][i + historyDay + param.getPredict_day() - 1 + j];
                    vmdrealResult[i][j] += vmdOutput[k][i + historyDay + param.getPredict_day() - 1 + j];
                }
            }
        }
        for (int i = trainLength - historyDay - param.getPredict_day() + 1; i < outputNumber; i++) {
            for (int j = 0; j < param.getPredict_day(); j++) {
                reaResult[i][j] = Double.parseDouble(inputTemp[i + (historyDay + param.getPredict_day() - 1) * 2 + j][1].toString());
                for (int k = 0; k < K; k++) {
                    vmdrealResult[i][param.getPredict_day()+k] = vmdOutput[k][i + (historyDay + param.getPredict_day() - 1) * 2 + j];
                    vmdrealResult[i][j] += vmdOutput[k][i + (historyDay + param.getPredict_day() - 1) * 2 + j];
                }
            }
        }

        Object[][] longResult = new Object[de_result.length][4];
        double rmse_train = 0;
        double dc_train = 0;
        double mre_train = 0;
        double qr_train = 0;
        double rmse_test = 0;
        double dc_test = 0;
        double mre_test = 0;
        double qr_test = 0;
        //结果赋值
        for (int i = 0; i < de_result.length; i++) {
            longResult[i][0] = de_result[i][0];
            int i1 = i / param.getPredict_day();
            longResult[i][1] = reaResult[i1][i - i1 * param.getPredict_day()];//实测流量
            longResult[i][2] = preResult[i1][i - i1 * param.getPredict_day()];//预报流量
            longResult[i][3] = vmdrealResult[i1][i - i1 * param.getPredict_day()];//vmd实测流量
        }
        if (param.getIsAverage()){
            Object[][] realObject = new Object[longResult.length][2];
            Object[][] preObject = new Object[longResult.length][2];
            Object[][] vmdObject = new Object[longResult.length][2];
            for (int i = 0; i < longResult.length; i++) {
                realObject[i][0]=longResult[i][0];
                realObject[i][1]=longResult[i][1];
                preObject[i][0]=longResult[i][0];
                preObject[i][1]=longResult[i][2];
                vmdObject[i][0]=longResult[i][0];
                vmdObject[i][1]=longResult[i][3];
            }
            realObject = machineDataUtils.resultProcessing(realObject, param.getLocation());
            preObject = machineDataUtils.resultProcessing(preObject, param.getLocation());
            vmdObject = machineDataUtils.resultProcessing(vmdObject, param.getLocation());
            for (int i = 0; i < longResult.length; i++) {
                longResult[i][1] = realObject[i][1];
                longResult[i][2] = preObject[i][1];
                longResult[i][3] = vmdObject[i][1];
            }
        }


        String Option = param.getLocation() + param.getPeriod();
        double[][] trainPreResult = new double[longResult.length / 4 * 3][1];
        double[][] testPreResult = new double[longResult.length / 4][1];
        double[][] trainResult = new double[longResult.length / 4 * 3][1];
        double[][] testResult = new double[longResult.length / 4][1];
        for (int i = 0; i < longResult.length / 4 * 3; i++) {
            trainResult[i][0] = (double) longResult[i][1];
            trainPreResult[i][0] = (double) longResult[i][2];
        }
        for (int i = 0; i < longResult.length / 4; i++) {
            testResult[i][0] = (double) longResult[i + longResult.length / 4 * 3][1];
            testPreResult[i][0] = (double) longResult[i + longResult.length / 4 * 3][2];
        }
        rmse_train = MathUtils.RMSE(trainResult,trainPreResult);
        mre_train = MathUtils.MRE(trainResult,trainPreResult);
        dc_train = MathUtils.DC(trainResult,trainPreResult);
        qr_train = MathUtils.QualifyRate(trainResult,trainPreResult);
        rmse_test = MathUtils.RMSE(testResult,testPreResult);
        mre_test = MathUtils.MRE(testResult,testPreResult);
        dc_test = MathUtils.DC(testResult,testPreResult);
        qr_test = MathUtils.QualifyRate(testResult,testPreResult);
        DecimalFormat df = new DecimalFormat("#.###");
        Object[][] result = new Object[longResult.length + 1][9];
        result[0][0] = "时间";
        result[0][1] = "实测流量";
        result[0][2] = "预报流量";
        result[0][3] = "分解流量";
        result[0][4] = "评价指标";
        result[1][4] = param.getNetClass() + "模型训练集";
        result[2][4] = param.getNetClass() + "模型测试集";
        result[0][5] = "均方差";
        result[0][6] = "平均相对误差";
        result[0][7] = "一致性系数";
        result[0][8] = "合格率";
        result[1][5] = df.format(rmse_train);
        result[1][6] = df.format(mre_train);
        result[1][7] = df.format(dc_train);
        result[1][8] = df.format(qr_train * 100) + "%";
        result[2][5] = df.format(rmse_test);
        result[2][6] = df.format(mre_test);
        result[2][7] = df.format(dc_test);
        result[2][8] = df.format(qr_test * 100) + "%";
        for (int i = 0; i < longResult.length; i++) {
            for (int j = 0; j < longResult[0].length; j++) {
                result[i + 1][j] = longResult[i][j];
            }
        }
        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
        System.out.println("----------------------------------");
        System.out.printf("%-10.3f %-10.3f %-10.3f %-10.3f\n", rmse_test, mre_test, dc_test, qr_test);
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\" + param.getNetClass() + "-RESULT.xlsx", Option, result);
        Object[][] vmdPredict = new Object[preResult.length+1][1+param.getVmdK()*2];
        double[][] vmd;
        double[][] pre;
        double dc_vmd;
        double qr_vmd;
        for (int j = 0; j < param.getVmdK(); j++){
            vmd = new double[preResult.length][1];
            pre = new double[preResult.length][1];
            for (int i = 0; i < preResult.length; i++) {
                vmd[i][0] = vmdrealResult[i][param.getPredict_day()+j];
                pre[i][0] = preResult[i][param.getPredict_day()+j];
                dc_vmd = MathUtils.DC(vmd,pre);
                qr_vmd = MathUtils.QualifyRate(vmd,pre);
                vmdPredict[0][2*j+1]="R:"+dc_vmd;
                vmdPredict[0][2*j+2]="QC:"+qr_vmd;
                vmdPredict[0][0]="评价指标";
                vmdPredict[i+1][0]=de_result[i][0];
                vmdPredict[i+1][2*j+1]=vmdrealResult[i][param.getPredict_day()+j];
                vmdPredict[i+1][2*j+2]=preResult[i][param.getPredict_day()+j];
            }
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\" + param.getNetClass() + "-RESULT.xlsx", Option+"分解", vmdPredict);
    }
}

