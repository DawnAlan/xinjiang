package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.MathUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.Tools.array2String;


public class LongForecast {
    /**主函数
     * 输入参数，模拟或预报，模型输入，最大最小值
     * @param paramsSetVO
     * @param isRealTime
     * @param isHistory
     * @param input
     * @param maxminOld
     * @param para
     * @return
     */
    public ModelSaveEntity longTermForecast(ParamsSetVO paramsSetVO, boolean isRealTime, boolean isHistory, Object[][] input, double[][] maxminOld, Object[][] para) {
        //模型参数
       double[][] DNNpara = new double[para.length][para[0].length];
        if (isRealTime){
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < para[0].length; j++) {
                DNNpara[i][j]= (double) para[i][j];
            }
        }
        }else {
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < para[0].length; j++) {
                    DNNpara=(double[][])para[i][j];
            }
        }
        }


        if (paramsSetVO.getTrainNum() == 0) {
            paramsSetVO.setTrainNum(10000);
        }
        if (paramsSetVO.getERROR() == 0) {
            paramsSetVO.setERROR(0.00001);
        }
        if (paramsSetVO.getQ_max() == 0) {
            paramsSetVO.setQ_max(10000);
        }
        if (paramsSetVO.getQ_min() == 0) {
            paramsSetVO.setQ_min(0);
        }
        if (paramsSetVO.getClusterMethod() == null) {
            paramsSetVO.setClusterMethod("adam");
        }
        if (paramsSetVO.getMaxRate() == 0) {
            paramsSetVO.setMaxRate(0.01);
        }
        if (paramsSetVO.getMinRate() == 0) {
            paramsSetVO.setMinRate(0.0001);
        }

        Params.paramSet(paramsSetVO);// 参数设置
        String trainModel = paramsSetVO.getNetClass();
        String clusterMethod = Params.cluster;
        List<double[]> datalist = new ArrayList<double[]>();

        /**
         输入数据处理
         */
        double[] timeData;//时间戳
        double[] flowData;// 流量
        double[] meanFlowData;// 月平均流量
        double[] temperatureData;//温度
        double[] rainData;//降水

        //时间戳
        timeData = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            Date date = (Date) input[i][0];
            timeData[i] = date.getTime();
        }
        //前期流量
        flowData = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            flowData[i] = (double) input[i][1];
        }
        // 前面期间的平均流量
        meanFlowData = new double[input.length - paramsSetVO.getHistory_day() + 1];
        for (int i = 0; i < input.length - paramsSetVO.getHistory_day() + 1; i++) {
            double sum = 0;
            for (int j = 0; j < paramsSetVO.getHistory_day() - 1; j++) {
                sum += flowData[ i + j ];
            }
            meanFlowData[i] = sum / (paramsSetVO.getHistory_day() - 1);
        }
        //温度
        temperatureData = new double[input.length];
        if(input[0].length>=3){
            for (int i = 0; i < input.length; i++) {
                temperatureData[i]= (double) input[i][2];
            }
        }

        datalist.add(timeData);
        datalist.add(flowData);
        datalist.add(meanFlowData);
        datalist.add(temperatureData);
        //降水
        rainData = new double[input.length];
        if (input[0].length>=4){
            for (int i = 0; i < input.length; i++) {
                if (input[i][3] instanceof String){
                    input[i][3] = 0.0;
                }
                rainData[i]= (double) input[i][3];
            }
            datalist.add(rainData);
        }


        //训练集测试集划分
        double[][] trainData = null;
        double[][] testData = null;
        double[][] data = null;
        //融雪径流
        if(paramsSetVO.getIsSnowMeltModel()){
            try {
                if (isRealTime) {
                    data = DataUtils.inputData_Real_Snow(datalist, paramsSetVO);
                } else {
                    trainData = DataUtils.inputData_Train_Snow(datalist, paramsSetVO, false);
                    testData = DataUtils.inputData_Train_Snow(datalist, paramsSetVO, true);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //普通情况下预报
        else {
            try {
                if (isRealTime) {
                    data = DataUtils.inputData_Real(datalist, paramsSetVO);
                } else {
                    trainData = DataUtils.inputData_Train(datalist, paramsSetVO, false);
                    testData = DataUtils.inputData_Train(datalist, paramsSetVO, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ModelSaveEntity result;


        //训练模型或者预报
        if (isRealTime) {
            if (isHistory) {
                result = real_time_Forecast(data, clusterMethod, trainModel, paramsSetVO, false, false, maxminOld,  DNNpara);
            }
            else {
                result = real_time_Forecast(data, clusterMethod, trainModel, paramsSetVO, false, true, maxminOld,  DNNpara);
            }
        }
        else {
            result = trainPocess(trainData, testData, clusterMethod, trainModel, paramsSetVO, false);//率定参数
        }
        Params.paramReset();

        return result;
    }

    /**
     * 神经网络的训练
     * @param trainData     训练数据
     * @param testData      测试数据
     * @param clusterMethod 聚类方法
     * @return 训练完成的神经网络
     */
    private ModelSaveEntity trainPocess(double[][] trainData, double[][] testData, String clusterMethod, String trainModel, ParamsSetVO pvo, boolean isCascade) {

        TrainResult trainResult = new TrainResult();//检验的结果
        TrainResult trainResult1 = new TrainResult();//训练的结果
        List<TthResultEntity> resultList = new ArrayList();
        ModelSaveEntity modelSaveEntity = new ModelSaveEntity();
        ModelSaveEntity modelSaveEntity1 = new ModelSaveEntity();
        ModelSaveEntity modelSaveEntity2 = new ModelSaveEntity();
        NeuralNetwork trainModels = null;

        //数据归一化
        List<double[][]> normInput= trainNormalInput(trainData,testData,pvo);
        double[][] norm_input=normInput.get(0);
        double[][] norm_output=normInput.get(1);
        double[][] norm_testInput=normInput.get(2);
        double[][] norm_testOutput=normInput.get(3);
        double[][] maxAndMinOfinput=normInput.get(4);
        double[][] maxAndMinOfoutput=normInput.get(5);
        double[][] trainInput=normInput.get(6);
        double[][] testInput=normInput.get(7);
        double[][] trainOutput=normInput.get(8);
        double[][] testOutput=normInput.get(9);

        List<SimMaxMinEntity> maxAndMinSaveEntity = maxAndMinSave(pvo, maxAndMinOfinput, maxAndMinOfoutput);
        modelSaveEntity.setMaxmin(maxAndMinSaveEntity);

        if (trainModel.equals("深度神经网络")) {
            if (clusterMethod.equals("adam")) {
                trainModels = new DNN_ADAM(Params.dnnNet, Params.rate, Params.mobp, Params.maxRate, Params.minRate,
                        Params.batch);
            }
        }
        else if (trainModel.equals("Elman神经网络")) {
            trainModels = new Elman(norm_input, Params.elmanNet, Params.LEARN_RATE, Params.TRAINING_REPS,
                    Params.epsilon);
        }
        else {
            trainResult = new TrainResult();
            trainResult.setErrorMessage("未选择训练方式");
            return null;
        }

        /**
         训练集储存 */
        //训练数据
        trainModels.train(norm_input, norm_output, Params.trainNum);
        String inputIndex = array2String(pvo.getInputIndex());
        //反归一化处理
        trainResult1 = trainModels.simOutput(norm_input, norm_output);

        double[][] result1 = MathUtils.reNormal(trainResult1.getSimResult(), maxAndMinOfoutput);

        double rmse1 = MathUtils.RMSE(trainOutput, result1);
        double dc1 = MathUtils.DC(trainOutput, result1);
        double mre1 = MathUtils.MRE(trainOutput, result1);
        double qr1 = MathUtils.QualifyRate(trainOutput, result1);


        //储存DNN、Elman训练期的具体结果
        for (int i = 0; i < trainOutput.length; i++) {
            TthResultEntity hlr1 = new TthResultEntity();
            hlr1.setForecastDuanmian(pvo.getForecastDuanmian());
            hlr1.setDatasetStart(pvo.getDataSetStartTime());
            hlr1.setDatasetEnd(pvo.getDateSetEndTime());
            hlr1.setTestDatasetStart(pvo.getTestSetStartTime());
            hlr1.setTestDatasetEnd(pvo.getTestSetEndTime());
            hlr1.setPeriod(pvo.getForecastPeriod());
            hlr1.setModelName(pvo.getNetClass() + "_训练");
            hlr1.setOutputNum(1.0);
            hlr1.setOutputIndex(0.0);
            if (isCascade && pvo.getForecastPeriod().equals("小时")) {
                hlr1.setInputIndex("all");
            } else {
                hlr1.setInputIndex(inputIndex);
            }
            hlr1.setInputIndex(inputIndex);
            hlr1.setRealOutput(trainOutput[i][0]);
            hlr1.setSimOutput(result1[i][0]);
            Date date = new Date((long) trainData[i][0]);
            hlr1.setResultDate(date);
            hlr1.setUserName("hust");
            resultList.add(hlr1);
        }

        /**
         * 测试集储存*/

        //反归一化处理
        trainResult = trainModels.simOutput(norm_testInput, norm_testOutput);
        double[][] result = MathUtils.reNormal(trainResult.getSimResult(), maxAndMinOfoutput);

        double rmse = MathUtils.RMSE(testOutput, result);
        double dc = MathUtils.DC(testOutput, result);
        double mre = MathUtils.MRE(testOutput, result);
        double qr = MathUtils.QualifyRate(testOutput, result);

        //储存DNN、elman检验期的具体结果
        for (int i = 0; i < testOutput.length; i++) {
            TthResultEntity hlr = new TthResultEntity();
            hlr.setForecastDuanmian(pvo.getForecastDuanmian());
            hlr.setDatasetStart(pvo.getDataSetStartTime());
            hlr.setDatasetEnd(pvo.getDateSetEndTime());
            hlr.setTestDatasetStart(pvo.getTestSetStartTime());
            hlr.setTestDatasetEnd(pvo.getTestSetEndTime());
            hlr.setPeriod(pvo.getForecastPeriod());
            hlr.setModelName(pvo.getNetClass());
            hlr.setOutputNum(1.0);
            hlr.setOutputIndex(0.0);
            if (isCascade && pvo.getForecastPeriod().equals("小时")) {
                hlr.setInputIndex("all");
            } else {
                hlr.setInputIndex(inputIndex);
            }
            hlr.setInputIndex(inputIndex);
            hlr.setRealOutput(testOutput[i][0]);
            hlr.setSimOutput(result[i][0]);
            Date date = new Date((long)(testData[i][0]));
            hlr.setResultDate(date);
            hlr.setUserName("hust");
            resultList.add(hlr);
        }


        // 将DNN、Elman模型结果和模型参数存入
        //測試集
        modelSaveEntity1 = modelSave(trainModels, pvo, isCascade, 1);
        modelSaveEntity1.getModels().get(0).setRmse(rmse);
        modelSaveEntity1.getModels().get(0).setMre(mre);
        modelSaveEntity1.getModels().get(0).setDc(dc);
        modelSaveEntity1.getModels().get(0).setQr(qr);
        modelSaveEntity1.getModels().get(0).setQMax(pvo.getQ_max());
        modelSaveEntity1.getModels().get(0).setQMin(pvo.getQ_min());

        //训练集
        modelSaveEntity2 = modelSave(trainModels, pvo, isCascade, 0);
        modelSaveEntity2.getModels().get(0).setRmse(rmse1);
        modelSaveEntity2.getModels().get(0).setMre(mre1);
        modelSaveEntity2.getModels().get(0).setDc(dc1);
        modelSaveEntity2.getModels().get(0).setQr(qr1);
        modelSaveEntity2.getModels().get(0).setQMax(pvo.getQ_max());
        modelSaveEntity2.getModels().get(0).setQMin(pvo.getQ_min());

        List<TthModelEntity> hlme1 = modelSaveEntity1.getModels();
        List<TthModelEntity> hlme2 = modelSaveEntity2.getModels();
        List<TthModelEntity> hlme = new ArrayList();

        hlme.add(hlme2.get(0));
        hlme.add(hlme1.get(0));

        modelSaveEntity.setModels(hlme);
        modelSaveEntity.setParams(modelSaveEntity1.getParams());
        modelSaveEntity.setResult(resultList);
        return modelSaveEntity;
    }

    /**
     * 储存DNN、Elman模型结果和模型参数
     * @param model
     * @param psvo
     * @param isCascade
     * @param category
     * @return
     */
    private ModelSaveEntity modelSave(NeuralNetwork model, ParamsSetVO psvo, boolean isCascade, int category) {
        ModelSaveEntity modelSaveEntity = null;
        String inputIndex = array2String(psvo.getInputIndex());
        if (model.getName() == null || model.getName().equals("")) {
            System.out.println("未发现模型");
        }
        else {
            String[] layerNum = psvo.getLayerCount().split(",");
            double outputNum = Double.parseDouble(layerNum[layerNum.length - 1]);
            TthModelEntity mle = new TthModelEntity();
            if (category == 0) {
                mle.setModelName(model.getName() + "_训练");
            } else if (category == 1) {
                mle.setModelName(model.getName());
            } else if (category == 2) {
                mle.setModelName(model.getName() + "_实时");
            }
            mle.setPeriod(psvo.getForecastPeriod());
            mle.setDatasetStart(psvo.getDataSetStartTime());
            mle.setDatasetEnd(psvo.getDateSetEndTime());
            mle.setTestDatasetStart(psvo.getTestSetStartTime());
            mle.setTestDatasetEnd(psvo.getTestSetEndTime());
            mle.setOutputNum(outputNum);
            mle.setForecastDuanmian(psvo.getForecastDuanmian());
            if (isCascade && psvo.getForecastPeriod().equals("小时")) {
                mle.setInputIndex("all");
            } else {
                mle.setInputIndex(inputIndex);
            }
            mle.setErrorad(Params.ERROR);
            mle.setTrainNum((double) Params.trainNum);
            mle.setWidth(Params.width);
            mle.setShiftError(Params.shiftError);
            mle.setMaxRate(Params.maxRate);
            mle.setMinRate(Params.minRate);
            mle.setMobp(Params.mobp);
            mle.setGamma(Params.gamma);
            mle.setC(Params.c);
            mle.setClusterad(Params.cluster);
            Date createTime = new Date();
            mle.setUpdatead(createTime);
            mle.setUserName("hust");
            String everyLayer = "";
            List<TthParaEntity> paramList = new ArrayList();
            List<TthModelEntity> layerList = new ArrayList();

            if (model.getName().equals("深度神经网络")) {
                double[][] layer = model.getDNNLayer();
                for (int i = 0; i < layer.length; i++) {
                    if (i != layer.length - 1) {
                        everyLayer += layer[i].length + ",";
                    } else {
                        everyLayer += layer[i].length + "";
                    }
                }
                double[][][] weight = model.getDNNWeight();
                for (int i = 0; i < weight.length - 1; i++) {
                    for (int j = 0; j < weight[i].length; j++) {
                        for (int z = 0; z < weight[i][j].length; z++) {
                            TthParaEntity params = new TthParaEntity();
                            params.setModelName(model.getName());
                            params.setPeriod(psvo.getForecastPeriod());
                            params.setDatasetStart(psvo.getDataSetStartTime());
                            params.setDatasetEnd(psvo.getDateSetEndTime());
                            params.setTestDatasetStart(psvo.getTestSetStartTime());
                            params.setTestDatasetEnd(psvo.getTestSetEndTime());
                            params.setOutputNum(outputNum);
                            params.setForecastDuanmian(psvo.getForecastDuanmian());
                            if (isCascade && psvo.getForecastPeriod().equals("小时")) {
                                params.setInputIndex("all");
                            } else {
                                params.setInputIndex(inputIndex);
                            }
                            params.setParamName("dnn_weight");
                            params.setParamDim1(i + "");
                            params.setParamDim2(j + "");
                            params.setParamDim3(z + "");
                            params.setValue(weight[i][j][z]);
                            params.setUserName("hust");
                            paramList.add(params);
                        }
                    }
                }
            } else if (model.getName().equals("Elman神经网络")) {
                mle.setErrorad(Params.epsilon);
                mle.setTrainNum((double) Params.TRAINING_REPS);
                mle.setMaxRate(Params.LEARN_RATE);
                Elman elman = (Elman) model;
                int[] layer = elman.getLayernum();
                for (int i = 0; i < layer.length; i++) {
                    if (i != layer.length - 1) {
                        everyLayer += layer[i] + ",";
                    } else {
                        everyLayer += layer[i] + "";
                    }
                }
                double[][][] weight = elman.getLayer_weight();
                for (int i = 0; i < weight.length; i++) {
                    for (int j = 0; j < weight[i].length; j++) {
                        for (int z = 0; z < weight[i][j].length; z++) {
                            TthParaEntity params = new TthParaEntity();
                            params.setModelName(model.getName());
                            params.setPeriod(psvo.getForecastPeriod());
                            params.setDatasetStart(psvo.getDataSetStartTime());
                            params.setDatasetEnd(psvo.getDateSetEndTime());
                            params.setTestDatasetStart(psvo.getTestSetStartTime());
                            params.setTestDatasetEnd(psvo.getTestSetEndTime());
                            params.setOutputNum(outputNum);
                            params.setForecastDuanmian(psvo.getForecastDuanmian());
                            params.setInputIndex(inputIndex);
                            params.setParamName("elman_weight");
                            params.setParamDim1(i + "");
                            params.setParamDim2(j + "");
                            params.setParamDim3(z + "");
                            params.setValue(weight[i][j][z]);
                            params.setUserName("hust");
                            paramList.add(params);
                        }
                    }
                }
            }
            mle.setLayerCount(everyLayer);
            mle.setUserName("hust");
            layerList.add(mle);
            modelSaveEntity = new ModelSaveEntity();
            modelSaveEntity.setModels(layerList);
            modelSaveEntity.setParams(paramList);
        }
        return modelSaveEntity;

    }

    /**
     * 储存数据的最大最小值
     * @param pvo
     * @param maxAndMin_input
     * @param maxAndMin_output
     * @return
     */
    private List<SimMaxMinEntity> maxAndMinSave(ParamsSetVO pvo, double[][] maxAndMin_input, double[][] maxAndMin_output) {
        String inputIndex = array2String(pvo.getInputIndex());
        List<SimMaxMinEntity> list = new ArrayList();
        int inputLen = maxAndMin_input[0].length;
        int outputLen = maxAndMin_output[0].length;
        for (int i = 0; i < inputLen; i++) {
            SimMaxMinEntity mme = new SimMaxMinEntity();
            mme.setForecastDuanmian(pvo.getForecastDuanmian());
            mme.setPeriod(pvo.getForecastPeriod());
            mme.setDataIndex((double) i);
            mme.setMaxValue(maxAndMin_input[0][i]);
            mme.setMinValue(maxAndMin_input[1][i]);
            mme.setInputIndex(inputIndex);
            list.add(mme);
        }
        for (int i = 0; i < outputLen; i++) {
            SimMaxMinEntity mme = new SimMaxMinEntity();
            mme.setForecastDuanmian(pvo.getForecastDuanmian());
            mme.setPeriod(pvo.getForecastPeriod());
            mme.setDataIndex((double) (i + inputLen));
            mme.setMaxValue(maxAndMin_output[0][i]);
            mme.setMinValue(maxAndMin_output[1][i]);
            mme.setInputIndex(inputIndex);
            list.add(mme);
        }
        return list;
    }

    /**
     * 处理elman、DNN参数作为实时预报、历史模拟输入
     * @param DNNpara
     * @return
     */
    public double[][][] getParams(double[][] DNNpara) {
        double n = (double) DNNpara[DNNpara.length - 1][0];
        n = n + 1;
        double[][][] layer_weight = new double[(int) n][][];
        for (int i = 0; i < n; i++) {
            List<Double> hlp1 = new ArrayList();
            double n1 = 0;
            double n2 = 0;
            for (int j = 0; j < DNNpara.length; j++) {
                double value = DNNpara[j][0];
                if (i == (int) value) {
                    hlp1.add(DNNpara[j][3]);
                    n1 = DNNpara[j][1] + 1;
                    n2 = DNNpara[j][2] + 1;
                }
            }
            layer_weight[i] = new double[(int) n1][(int) n2];
            for (int j = 0; j < n1; j++) {
                for (int k = 0; k < n2; k++) {
                    layer_weight[i][j][k] = hlp1.get(k + j * (int) n2);
                }
            }
        }
        return layer_weight;
    }

    /**
     * 实时预报的代码
     * @param data
     * @param clusterMethod
     * @param trainModel
     * @param paramsSetVO
     * @param isCascade
     * @param isRealTime
     * @param maxminOld
     * @param DNNpara
     * @return
     */
    private ModelSaveEntity real_time_Forecast(double[][] data, String clusterMethod, String trainModel, ParamsSetVO paramsSetVO, boolean isCascade, boolean isRealTime, double[][] maxminOld, double[][] DNNpara) {
        TrainResult trainResult = new TrainResult();
        ModelSaveEntity modelSaveEntity = new ModelSaveEntity();
        NeuralNetwork trainModels = null;
        Params.paramSet(paramsSetVO);

        List<double[][]> normalInput=predictNormalInput(data,maxminOld,paramsSetVO);
        double[][] norm_input=normalInput.get(0);
        double[][] norm_output=normalInput.get(1);
        double[][] maxAndMinOfoutput=normalInput.get(2);
        double[][] output=normalInput.get(3);

        if ((trainModel.equals("深度神经网络")) && (clusterMethod.equals("adam"))) {
            trainModels = new DNN_ADAM(Params.dnnNet, Params.rate, Params.mobp, Params.maxRate, Params.minRate,
                    Params.batch);
            double[][][] layer_weight = getParams(DNNpara);//输入率定好的参数
            trainResult = trainModels.simOutput1(norm_input, norm_output, layer_weight, Params.dnnNet);//用率定好的参数模拟
        } else if (trainModel.equals("Elman神经网络")) {
            trainModels = new Elman(norm_input, Params.elmanNet, Params.LEARN_RATE, Params.TRAINING_REPS,
                    Params.epsilon);
            double[][][] layer_weight = getParams(DNNpara);//输入率定好的参数
            trainResult = trainModels.simOutput1(norm_input, norm_output, layer_weight, Params.elmanNet);//用率定好的参数模拟
        }
        else {
            trainResult = new TrainResult();
            trainResult.setErrorMessage("未选择训练方式");
            return null;
        }

        double[][] result = MathUtils.reNormal(trainResult.getSimResult(), maxAndMinOfoutput);
        double rmse = MathUtils.RMSE(output, result);
        double dc = MathUtils.DC(output, result);
        double mre = MathUtils.MRE(output, result);
        double qr = MathUtils.QualifyRate(output, result);

        Date startDate = paramsSetVO.getDataSetStartTime();
        int outputNum = result[0].length;
        Date[][] dates;
        if (paramsSetVO.getForecastPeriod().equals("月")) {
            dates = TimeUtils.getMonthDateList(startDate, result.length);
        } else if (paramsSetVO.getForecastPeriod().equals("旬")) {
            dates = TimeUtils.getDateList(startDate, result.length, 10, 0);
        } else if (paramsSetVO.getForecastPeriod().equals("日")) {
            dates = TimeUtils.getDateList(startDate, result.length, 1, 0);
        } else {
            dates = TimeUtils.getDateList(startDate, result.length, 0, 1);
        }
        List<TthResultEntity> resultList = new ArrayList();
        String inputIndex1 = array2String(paramsSetVO.getInputIndex());
        //储存模型结果（评价指标等）
        for (int i = 0; i < output.length; i++) {
            TthResultEntity hlr = new TthResultEntity();
            hlr.setForecastDuanmian(paramsSetVO.getForecastDuanmian());
            hlr.setDatasetStart(paramsSetVO.getDataSetStartTime());
            hlr.setDatasetEnd(paramsSetVO.getDateSetEndTime());
            hlr.setTestDatasetStart(paramsSetVO.getTestSetStartTime());
            hlr.setTestDatasetEnd(paramsSetVO.getTestSetEndTime());
            hlr.setPeriod(paramsSetVO.getForecastPeriod());
            hlr.setModelName(paramsSetVO.getNetClass() + "_实时");
            hlr.setOutputNum(1.0);
            hlr.setOutputIndex(0.0);
            if (isCascade && paramsSetVO.getForecastPeriod().equals("小时")) {
                hlr.setInputIndex("all");
            } else {
                hlr.setInputIndex(inputIndex1);
            }
            hlr.setInputIndex(inputIndex1);
            hlr.setRealOutput(output[i][0]);
            hlr.setSimOutput(result[i][0]);
            hlr.setResultDate(dates[i][0]);
            if (i == output.length - 1) {
                if (isRealTime) {
                    hlr.setRainfall(null);
                } else {
                    hlr.setRainfall(0.0);
                }
            } else {
                hlr.setRainfall(0.0);
            }
            hlr.setUserName("hust");
            resultList.add(hlr);
        }

        //储存模型的具体结果（每一个时段的预报值、实测值等等）
//        List<TthResultEntity> result2Save = DataUtils.getHyfTrainResult(output, result, data, dates, paramsSetVO,
//                isCascade);
        modelSaveEntity = modelSave(trainModels, paramsSetVO, isCascade, 2);
        modelSaveEntity.getModels().get(0).setRmse(rmse);
        modelSaveEntity.getModels().get(0).setMre(mre);
        modelSaveEntity.getModels().get(0).setDc(dc);
        modelSaveEntity.getModels().get(0).setQr(qr);
        modelSaveEntity.getModels().get(0).setQMax(paramsSetVO.getQ_max());
        modelSaveEntity.getModels().get(0).setQMin(paramsSetVO.getQ_min());

        modelSaveEntity.setModels(modelSaveEntity.getModels());
        modelSaveEntity.setResult(resultList);

        return modelSaveEntity;
    }

    /**
     * 训练数据归一化
     * @param trainData
     * @param testData
     * @param pvo
     * @return
     */
    private List<double[][]> trainNormalInput(double[][] trainData, double[][] testData, ParamsSetVO pvo){
        List<double[][]> normalInput =new ArrayList<>();
        int h = pvo.getHistory_factor();
        int n =(trainData[0].length-2)/h;
        //如果是融雪径流
        if (pvo.getIsSnowMeltModel()){
            double[][] trainInputFlow = new double[trainData.length][pvo.getHistory_factor()];
            double[][] trainInputTemperature = new double[trainData.length][pvo.getHistory_factor()];
            double[][] trainInputRain = new double[trainData.length][pvo.getHistory_factor()];
            double[][] trainOutput = new double[trainData.length][1];

            double[][] testInputFlow = new double[testData.length][pvo.getHistory_factor()];
            double[][] testInputTemperature = new double[testData.length][pvo.getHistory_factor()];
            double[][] testInputRain = new double[testData.length][pvo.getHistory_factor()];
            double[][] testOutput = new double[testData.length][1];


            for (int i = 0; i < trainData.length; i++) {
                for (int j = 0; j < pvo.getHistory_factor(); j++) {
                    trainInputFlow[i][j] = trainData[i][j+1];
                    if (n>=2){
                        trainInputTemperature[i][j] = trainData[i][j+1+h];
                        if (n>=3){
                            trainInputRain[i][j] = trainData[i][j+1+2*h];
                        }
                    }
                }
                trainOutput[i][0] = trainData[i][pvo.getHistory_factor()*n+1];
            }

            for (int i = 0; i < testData.length; i++) {
                for (int j = 0; j < pvo.getHistory_factor(); j++) {
                    testInputFlow[i][j] = testData[i][j+1];
                    if (n>=2){
                        testInputTemperature[i][j] = testData[i][j+1+h];
                        if (n>=3){
                            testInputRain[i][j] = testData[i][j+1+2*h];
                        }
                    }

                }
                testOutput[i][0] = testData[i][pvo.getHistory_factor()*n+1];
            }

            //遍历数据的最大最小值，便于归一化处理
            double[][] maxAndMinOfinputFlow = MathUtils.findMaxAndMin(trainInputFlow);
            double[][] maxAndMinOfinputTemperature=new double[0][];
            double[][] maxAndMinOfinputRain =new double[0][];
            int l = maxAndMinOfinputFlow[0].length;
            if (n>=2){
                maxAndMinOfinputTemperature = MathUtils.findMaxAndMin(trainInputTemperature);
                l = l + maxAndMinOfinputTemperature[0].length;
                if (n>=3){
                    maxAndMinOfinputRain = MathUtils.findMaxAndMin(trainInputRain);
                    l = l + maxAndMinOfinputRain[0].length;
                }
            }
            double[][] maxAndMinOfoutput = MathUtils.findMaxAndMin(trainOutput);
            double[][] maxAndMinOfinput=new double[maxAndMinOfinputFlow.length][l];
            for (int i = 0; i < maxAndMinOfinputFlow[0].length; i++) {
                for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                    maxAndMinOfinput[j][i]=maxAndMinOfinputFlow[j][i];
                }
            }
            if (n>=2){
                for (int i = 0; i < maxAndMinOfinputTemperature[0].length; i++) {
                    for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                        maxAndMinOfinput[j][i+maxAndMinOfinputFlow[0].length]=maxAndMinOfinputTemperature[j][i];
                    }
                }
                if (n>=3){
                    for (int i = 0; i < maxAndMinOfinputRain[0].length; i++) {
                        for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                            maxAndMinOfinput[j][i+maxAndMinOfinputFlow[0].length+maxAndMinOfinputTemperature[0].length]=maxAndMinOfinputRain[j][i];
                        }
                    }
                }
            }

            double[][] maxAndMinOfinput_testFlow = MathUtils.findMaxAndMin(testInputFlow);
            double[][] maxAndMinOfinput_testTemperature=new double[0][];
            double[][] maxAndMinOfinput_testRain =new double[0][];
            if (n>=2){
                maxAndMinOfinput_testTemperature = MathUtils.findMaxAndMin(testInputTemperature);
                if (n>=3){
                    maxAndMinOfinput_testRain = MathUtils.findMaxAndMin(testInputRain);
                }
            }
            double[][] maxAndMinOfoutput_test = MathUtils.findMaxAndMin(testOutput);

            for (int i = 0; i < maxAndMinOfinputFlow[0].length; i++) {// 输入

                if (maxAndMinOfinputFlow[0][i] < maxAndMinOfinput_testFlow[0][i]) {
                    maxAndMinOfinputFlow[0][i] = maxAndMinOfinput_testFlow[0][i];
                }

                if (maxAndMinOfinputFlow[1][i] > maxAndMinOfinput_testFlow[1][i]) {
                    maxAndMinOfinputFlow[1][i] = maxAndMinOfinput_testFlow[1][i];
                }
            }
            if(n>=2){
                for (int i = 0; i < maxAndMinOfinputTemperature[0].length; i++) {// 输入

                    if (maxAndMinOfinputTemperature[0][i] < maxAndMinOfinput_testTemperature[0][i]) {
                        maxAndMinOfinputTemperature[0][i] = maxAndMinOfinput_testTemperature[0][i];
                    }

                    if (maxAndMinOfinputTemperature[1][i] > maxAndMinOfinput_testTemperature[1][i]) {
                        maxAndMinOfinputTemperature[1][i] = maxAndMinOfinput_testTemperature[1][i];
                    }
                }
                if (n>=3){
                    for (int i = 0; i < maxAndMinOfinputRain[0].length; i++) {// 输入

                        if (maxAndMinOfinputRain[0][i] < maxAndMinOfinput_testRain[0][i]) {
                            maxAndMinOfinputRain[0][i] = maxAndMinOfinput_testRain[0][i];
                        }

                        if (maxAndMinOfinputRain[1][i] > maxAndMinOfinput_testRain[1][i]) {
                            maxAndMinOfinputRain[1][i] = maxAndMinOfinput_testRain[1][i];
                        }
                    }
                }
            }

            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {// 输出

                if (maxAndMinOfoutput[0][i] < maxAndMinOfoutput_test[0][i]) {
                    maxAndMinOfoutput[0][i] = maxAndMinOfoutput_test[0][i];
                }

                if (maxAndMinOfoutput[1][i] > maxAndMinOfoutput_test[1][i]) {
                    maxAndMinOfoutput[1][i] = maxAndMinOfoutput_test[1][i];
                }
            }

            // 归一化
            double[][] norm_inputFlow = MathUtils.normalization(trainInputFlow, maxAndMinOfinputFlow);
            double[][] norm_inputTemperature = new double[0][];
            double[][] norm_inputRain = new double[0][];
            if (n>=2){
                norm_inputTemperature = MathUtils.normalization(trainInputTemperature, maxAndMinOfinputTemperature);
                if (n>=3){
                    norm_inputRain = MathUtils.normalization(trainInputRain, maxAndMinOfinputRain);
                }
            }
            double[][] norm_output = MathUtils.normalization(trainOutput, maxAndMinOfoutput);

            double[][] norm_testInputFlow = MathUtils.normalization(testInputFlow, maxAndMinOfinputFlow);
            double[][] norm_testInputTemperature = new double[0][];
            double[][] norm_testInputRain= new double[0][];
            if(n>=2){
                norm_testInputTemperature = MathUtils.normalization(testInputTemperature, maxAndMinOfinputTemperature);
                if (n>=3){
                    norm_testInputRain = MathUtils.normalization(testInputRain, maxAndMinOfinputRain);
                }
            }
            double[][] norm_testOutput = MathUtils.normalization(testOutput, maxAndMinOfoutput);

            double[][] norm_input=new double[norm_inputFlow.length][norm_inputFlow[0].length*n];
            for (int i = 0; i < norm_inputFlow.length; i++) {
                for (int j = 0; j < norm_inputFlow[0].length; j++) {
                    norm_input[i][j]=norm_inputFlow[i][j];
                }
            }
            if (n>=2){
                for (int i = 0; i < norm_inputTemperature.length; i++) {
                    for (int j = 0; j < norm_inputTemperature[0].length; j++) {
                        norm_input[i][j+norm_inputFlow[0].length]=norm_inputTemperature[i][j];
                    }
                }
                if (n>=3){
                    for (int i = 0; i < norm_inputRain.length; i++) {
                        for (int j = 0; j < norm_inputRain[0].length; j++) {
                            norm_input[i][j+norm_inputFlow[0].length+norm_inputTemperature[0].length]=norm_inputRain[i][j];
                        }
                    }
                }
            }


            double[][] norm_testInput=new double[norm_testInputFlow.length][norm_testInputFlow[0].length*n];
            for (int i = 0; i < norm_testInputFlow.length; i++) {
                for (int j = 0; j < norm_testInputFlow[0].length; j++) {
                    norm_testInput[i][j]=norm_testInputFlow[i][j];
                }
            }
            if (n>=2){
                for (int i = 0; i < norm_testInputTemperature.length; i++) {
                    for (int j = 0; j < norm_testInputTemperature[0].length; j++) {
                        norm_testInput[i][j+norm_testInputFlow[0].length]=norm_testInputTemperature[i][j];
                    }
                }
                if(n>=3){
                    for (int i = 0; i < norm_testInputRain.length; i++) {
                        for (int j = 0; j < norm_testInputRain[0].length; j++) {
                            norm_testInput[i][j+norm_testInputFlow[0].length+norm_testInputTemperature[0].length]=norm_testInputRain[i][j];
                        }
                    }
                }
            }
            normalInput.add(norm_input);
            normalInput.add(norm_output);
            normalInput.add(norm_testInput);
            normalInput.add(norm_testOutput);
            normalInput.add(maxAndMinOfinput);
            normalInput.add(maxAndMinOfoutput);
            normalInput.add(trainInputFlow);
            normalInput.add(testInputFlow);
            normalInput.add(trainOutput);
            normalInput.add(testOutput);
        }
        else {
            double[][] trainInput = new double[trainData.length][pvo.getHistory_day()];
            double[][] trainOutput = new double[trainData.length][1];
            double[][] testInput = new double[testData.length][pvo.getHistory_day()];
            double[][] testOutput = new double[testData.length][1];


            for (int i = 0; i < trainData.length; i++) {
                for (int j = 0; j < pvo.getHistory_day(); j++) {
                    trainInput[i][j] = trainData[i][j+1];
                }
                trainOutput[i][0] = trainData[i][pvo.getHistory_day()+1];
            }

            for (int i = 0; i < testData.length; i++) {
                for (int j = 0; j < pvo.getHistory_day(); j++) {
                    testInput[i][j] = testData[i][j+1];
                }
                testOutput[i][0] = testData[i][pvo.getHistory_day()+1];
            }

            //遍历数据的最大最小值，便于归一化处理
            double[][] maxAndMinOfinput = MathUtils.findMaxAndMin(trainInput);
            double[][] maxAndMinOfoutput = MathUtils.findMaxAndMin(trainOutput);

            double[][] maxAndMinOfinput_test = MathUtils.findMaxAndMin(testInput);
            double[][] maxAndMinOfoutput_test = MathUtils.findMaxAndMin(testOutput);

            for (int i = 0; i < maxAndMinOfinput[0].length; i++) {// 输入

                if (maxAndMinOfinput[0][i] < maxAndMinOfinput_test[0][i]) {
                    maxAndMinOfinput[0][i] = maxAndMinOfinput_test[0][i];
                }

                if (maxAndMinOfinput[1][i] > maxAndMinOfinput_test[1][i]) {
                    maxAndMinOfinput[1][i] = maxAndMinOfinput_test[1][i];
                }
            }
            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {// 输出

                if (maxAndMinOfoutput[0][i] < maxAndMinOfoutput_test[0][i]) {
                    maxAndMinOfoutput[0][i] = maxAndMinOfoutput_test[0][i];
                }

                if (maxAndMinOfoutput[1][i] > maxAndMinOfoutput_test[1][i]) {
                    maxAndMinOfoutput[1][i] = maxAndMinOfoutput_test[1][i];
                }
            }

            // 归一化
            double[][] norm_input = MathUtils.normalization(trainInput, maxAndMinOfinput);
            double[][] norm_output = MathUtils.normalization(trainOutput, maxAndMinOfoutput);
            double[][] norm_testInput = MathUtils.normalization(testInput, maxAndMinOfinput);
            double[][] norm_testOutput = MathUtils.normalization(testOutput, maxAndMinOfoutput);
            normalInput.add(norm_input);
            normalInput.add(norm_output);
            normalInput.add(norm_testInput);
            normalInput.add(norm_testOutput);
            normalInput.add(maxAndMinOfinput);
            normalInput.add(maxAndMinOfoutput);
            normalInput.add(trainInput);
            normalInput.add(testInput);
            normalInput.add(trainOutput);
            normalInput.add(testOutput);
        }

        return normalInput;
    }

    /**
     * 预报数据归一化
     * @param data
     * @param maxminOld
     * @param pvo
     * @return
     */
    private List<double[][]> predictNormalInput(double[][] data,double[][]maxminOld,ParamsSetVO pvo){
        List<double[][]> normalInput =new ArrayList<>();
        int h = pvo.getHistory_factor();
        int n =(data[0].length-2)/h;

        if(pvo.getIsSnowMeltModel()){
            double[][] input = new double[data.length][h*n];
            double[][] output = new double[data.length][1];

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j <h; j++) {
                    input[i][j] = data[i][j+1];
                    if (n>=2){
                        input[i][j+h] = data[i][j+1+h];
                        if (n>=3){
                            input[i][j+h*2] = data[i][j+1+2*h];
                        }
                    }
                }
                output[i][0] = data[i][h*n+1];
            }


            double[][] maxAndMinOfinput1 = MathUtils.findMaxAndMin(input);
            double[][] maxAndMinOfoutput1 = MathUtils.findMaxAndMin(output);
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            double[][] maxAndMinOfinput = new double[maxAndMinOfinput1.length][maxAndMinOfinput1[0].length];
            double[][] maxAndMinOfoutput = new double[maxAndMinOfoutput1.length][maxAndMinOfoutput1[0].length];

            //输入层各层最大最小值  li
            for (int i = 0; i < maxAndMinOfinput[0].length; i++) {
                maxAndMinOfinput[0][i] = maxminOld[i][0];//最大值
                maxAndMinOfinput[1][i] = maxminOld[i][1];//最小值
            }

            //输出层各层最大最小值
            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {
                maxAndMinOfoutput[0][i] = (double) maxminOld[i + maxAndMinOfinput[0].length][0];//最大值
                maxAndMinOfoutput[1][i] = (double) maxminOld[i + maxAndMinOfinput[0].length][1];//最小值
            }

            for (int i = 0; i < maxAndMinOfinput[0].length; i++) {
                if (maxAndMinOfinput[0][i] < maxAndMinOfinput1[0][i]) {
                    maxAndMinOfinput[0][i] = maxAndMinOfinput1[0][i];
                }
                if (maxAndMinOfinput[1][i] > maxAndMinOfinput1[1][i]) {
                    maxAndMinOfinput[1][i] = maxAndMinOfinput1[1][i];
                }
            }
            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {
                if (maxAndMinOfoutput[0][i] < maxAndMinOfoutput1[0][i]) {
                    maxAndMinOfoutput[0][i] = maxAndMinOfoutput1[0][i];
                }
                if (maxAndMinOfoutput[1][i] > maxAndMinOfoutput1[1][i]) {
                    maxAndMinOfoutput[1][i] = maxAndMinOfoutput1[1][i];
                }
            }
            double[][] norm_input = MathUtils.normalization(input, maxAndMinOfinput);
            double[][] norm_output = MathUtils.normalization(output, maxAndMinOfoutput);

            normalInput.add(norm_input);
            normalInput.add(norm_output);
            normalInput.add(maxAndMinOfoutput);
            normalInput.add(output);
        }

        else {
            double[][] input = new double[data.length][pvo.getHistory_day()];
            double[][] output = new double[data.length][1];
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j <pvo.getHistory_day(); j++) {
                    input[i][j] = data[i][j+1];
                }
                output[i][0] = data[i][pvo.getHistory_day()+1];
            }
            double[][] maxAndMinOfinput1 = MathUtils.findMaxAndMin(input);
            double[][] maxAndMinOfoutput1 = MathUtils.findMaxAndMin(output);
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            double[][] maxAndMinOfinput = new double[maxAndMinOfinput1.length][maxAndMinOfinput1[0].length];
            double[][] maxAndMinOfoutput = new double[maxAndMinOfoutput1.length][maxAndMinOfoutput1[0].length];

            //输入层各层最大最小值  li
            for (int i = 0; i < maxAndMinOfinput[0].length; i++) {
                maxAndMinOfinput[0][i] = maxminOld[i][0];//最大值
                maxAndMinOfinput[1][i] = maxminOld[i][1];//最小值
            }

            //输出层各层最大最小值
            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {
                maxAndMinOfoutput[0][i] = (double) maxminOld[i + maxAndMinOfinput[0].length][0];//最大值
                maxAndMinOfoutput[1][i] = (double) maxminOld[i + maxAndMinOfinput[0].length][1];//最小值
            }

            for (int i = 0; i < maxAndMinOfinput[0].length; i++) {
                if (maxAndMinOfinput[0][i] < maxAndMinOfinput1[0][i]) {
                    maxAndMinOfinput[0][i] = maxAndMinOfinput1[0][i];
                }
                if (maxAndMinOfinput[1][i] > maxAndMinOfinput1[1][i]) {
                    maxAndMinOfinput[1][i] = maxAndMinOfinput1[1][i];
                }
            }
            for (int i = 0; i < maxAndMinOfoutput[0].length; i++) {
                if (maxAndMinOfoutput[0][i] < maxAndMinOfoutput1[0][i]) {
                    maxAndMinOfoutput[0][i] = maxAndMinOfoutput1[0][i];
                }
                if (maxAndMinOfoutput[1][i] > maxAndMinOfoutput1[1][i]) {
                    maxAndMinOfoutput[1][i] = maxAndMinOfoutput1[1][i];
                }
            }
            double[][] norm_input = MathUtils.normalization(input, maxAndMinOfinput);
            double[][] norm_output = MathUtils.normalization(output, maxAndMinOfoutput);
            normalInput.add(norm_input);
            normalInput.add(norm_output);
            normalInput.add(maxAndMinOfoutput);
            normalInput.add(output);
        }
        return normalInput;
    }
}
