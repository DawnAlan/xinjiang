package com.cj.model.func.modular.FloodPredict.model.function;


import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.entity.*;
import com.cj.model.func.modular.FloodPredict.model.network.DNN_ADAM;
import com.cj.model.func.modular.FloodPredict.model.network.Elman;
import com.cj.model.func.modular.FloodPredict.model.network.NeuralNetwork;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.MachineDataUtils;
import com.cj.model.func.modular.FloodPredict.utils.MathUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.Tools.array2String;


public class LongForecast {
    DataUtils dataUtils = new DataUtils();

    MachineDataUtils machineDataUtils = new MachineDataUtils();

    TimeUtils timeUtils = new TimeUtils();

    Params params = new Params();

    /**
     * 主函数
     * 输入参数，模拟或预报，模型输入，最大最小值
     *
     * @param param
     * @param input
     * @param maxminOldTemp
     * @param para
     * @return
     */
    public ModelSaveEntity longTermForecast(ForecastInputParam param, Object[][] input, Object[][] maxminOldTemp, Object[][] para) {
        //模型参数
        Boolean isRealTime = param.getIsRealtime();
        double[][] DNNpara = new double[para.length][para[0].length];
        double[][] maxminOld = new double[maxminOldTemp.length][2];

        if (isRealTime) {
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < para[0].length; j++) {
                    DNNpara[i][j] = (double) para[i][j];
                }
            }
            for (int k = 0; k < maxminOldTemp.length; k++) {
                for (int j = 0; j < 2; j++) {
                    maxminOld[k][j] = Double.parseDouble(maxminOldTemp[k][j].toString());
                }
            }
        } else {
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < para[0].length; j++) {
                    DNNpara[i][j] = 0.0;
                }
            }
            for (int k = 0; k < maxminOldTemp.length; k++) {
                for (int j = 0; j < 2; j++) {
                    maxminOld[k][j] = 0.0;
                }
            }
        }




        if (param.getTrainNum() == 0) {
            param.setTrainNum(10000);
        }
        if (param.getERROR() == 0) {
            param.setERROR(0.00001);
        }
        if (param.getQ_max() == 0) {
            param.setQ_max(10000.0);
        }
        if (param.getQ_min() == 0) {
            param.setQ_min(0.0);
        }
        if (param.getClusterMethod() == null) {
            param.setClusterMethod("adam");
        }
        if (param.getMaxRate() == 0) {
            param.setMaxRate(0.01);
        }
        if (param.getMinRate() == 0) {
            param.setMinRate(0.0001);
        }

        params.paramSet(param);// 参数设置
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
        meanFlowData = new double[input.length - param.getHistory_day() + 1];
        for (int i = 0; i < input.length - param.getHistory_day() + 1; i++) {
            double sum = 0;
            for (int j = 0; j < param.getHistory_day() - 1; j++) {
                sum += flowData[i + j];
            }
            meanFlowData[i] = sum / (param.getHistory_day() - 1);
        }
        //温度
        temperatureData = new double[input.length];
        if (input[0].length >= 3) {
            for (int i = 0; i < input.length; i++) {
                temperatureData[i] = (double) input[i][2];
            }
        }

        datalist.add(timeData);
        datalist.add(flowData);
        datalist.add(meanFlowData);
        datalist.add(temperatureData);
        //降水
        rainData = new double[input.length];
        if (input[0].length >= 4) {
            for (int i = 0; i < input.length; i++) {
                if (input[i][3] instanceof String) {
                    input[i][3] = 0.0;
                }
                rainData[i] = (double) input[i][3];
            }
            datalist.add(rainData);
        }


        //训练集测试集划分
        double[][] trainData = null;
        double[][] testData = null;
        double[][] data = null;
        //融雪径流
        if (param.getIsSnowMeltModel()) {
            try {
                if (isRealTime) {
                    data = machineDataUtils.inputData_Real_Snow(datalist, param);
                } else {
                    trainData = machineDataUtils.inputData_Train_Snow(datalist, param, false);
                    testData = machineDataUtils.inputData_Train_Snow(datalist, param, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //普通情况下预报
        else {
            try {
                if (isRealTime) {
                    data = machineDataUtils.inputData_Real(datalist, param);
                } else {
                    trainData = machineDataUtils.inputData_Train(datalist, param, false);
                    testData = machineDataUtils.inputData_Train(datalist, param, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ModelSaveEntity result;


        //训练模型或者预报
        if (isRealTime) {
            result = real_time_Forecast(param, data, maxminOld, DNNpara);
        } else {
            result = trainProcess(param, trainData, testData);
        }
//        params.paramReset();

        return result;
    }

    /**
     * 神经网络的训练
     *
     * @param trainData 训练数据
     * @param testData  测试数据
     * @return 训练完成的神经网络
     */
    private ModelSaveEntity trainProcess(ForecastInputParam param, double[][] trainData, double[][] testData) {

        TrainResult trainResult = new TrainResult();//检验的结果
        TrainResult trainResult1 = new TrainResult();//训练的结果
        List<TthResultEntity> resultList = new ArrayList();
        ModelSaveEntity modelSaveEntity = new ModelSaveEntity();
        ModelSaveEntity modelSaveEntity1 = new ModelSaveEntity();
        ModelSaveEntity modelSaveEntity2 = new ModelSaveEntity();
        NeuralNetwork trainModels = null;
        Boolean isCascade = false;
        String trainModel = param.getModel();
        String clusterMethod = "adam";
        //数据归一化
        List<double[][]> normInput = trainNormalInput(trainData, testData, param);
        double[][] norm_input = normInput.get(0);
        double[][] norm_output = normInput.get(1);
        double[][] norm_testInput = normInput.get(2);
        double[][] norm_testOutput = normInput.get(3);
        double[][] maxAndMinOfinput = normInput.get(4);
        double[][] maxAndMinOfoutput = normInput.get(5);
        double[][] trainInput = normInput.get(6);
        double[][] testInput = normInput.get(7);
        double[][] trainOutput = normInput.get(8);
        double[][] testOutput = normInput.get(9);

        List<SimMaxMinEntity> maxAndMinSaveEntity = maxAndMinSave(param, maxAndMinOfinput, maxAndMinOfoutput);
        modelSaveEntity.setMaxmin(maxAndMinSaveEntity);

        if (trainModel.equals("深度神经网络")) {
            if (clusterMethod.equals("adam")) {
                trainModels = new DNN_ADAM(params.dnnNet, params.rate, params.mobp, params.maxRate, params.minRate,
                        params.batch);
            }
        } else if (trainModel.equals("Elman神经网络")) {
            trainModels = new Elman(norm_input, params.elmanNet, params.LEARN_RATE, params.TRAINING_REPS,
                    params.epsilon);
        } else {
            trainResult = new TrainResult();
            trainResult.setErrorMessage("未选择训练方式");
            return null;
        }

        /**
         训练集储存 */
        //训练数据
        trainModels.train(norm_input, norm_output, params.trainNum);
        String inputIndex = array2String(param.getInputIndex());
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
            hlr1.setForecastDuanmian(param.getLocation());
            hlr1.setDatasetStart(param.getDataSetStartTime());
            hlr1.setDatasetEnd(param.getDateSetEndTime());
            hlr1.setTestDatasetStart(param.getTestSetStartTime());
            hlr1.setTestDatasetEnd(param.getTestSetEndTime());
            hlr1.setPeriod(param.getPeriod());
            hlr1.setModelName(param.getNetClass() + "_训练");
            hlr1.setOutputNum(1.0);
            hlr1.setOutputIndex(0.0);
            if (isCascade && param.getPeriod().equals("小时")) {
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
            hlr.setForecastDuanmian(param.getLocation());
            hlr.setDatasetStart(param.getDataSetStartTime());
            hlr.setDatasetEnd(param.getDateSetEndTime());
            hlr.setTestDatasetStart(param.getTestSetStartTime());
            hlr.setTestDatasetEnd(param.getTestSetEndTime());
            hlr.setPeriod(param.getPeriod());
            hlr.setModelName(param.getNetClass());
            hlr.setOutputNum(1.0);
            hlr.setOutputIndex(0.0);
            if (isCascade && param.getPeriod().equals("小时")) {
                hlr.setInputIndex("all");
            } else {
                hlr.setInputIndex(inputIndex);
            }
            hlr.setInputIndex(inputIndex);
            hlr.setRealOutput(testOutput[i][0]);
            hlr.setSimOutput(result[i][0]);
            Date date = new Date((long) (testData[i][0]));
            hlr.setResultDate(date);
            hlr.setUserName("hust");
            resultList.add(hlr);
        }


        // 将DNN、Elman模型结果和模型参数存入
        //測試集
        modelSaveEntity1 = modelSave(trainModels, param, isCascade, 1);
        modelSaveEntity1.getModels().get(0).setRmse(rmse);
        modelSaveEntity1.getModels().get(0).setMre(mre);
        modelSaveEntity1.getModels().get(0).setDc(dc);
        modelSaveEntity1.getModels().get(0).setQr(qr);
        modelSaveEntity1.getModels().get(0).setQMax(param.getQ_max());
        modelSaveEntity1.getModels().get(0).setQMin(param.getQ_min());

        //训练集
        modelSaveEntity2 = modelSave(trainModels, param, isCascade, 0);
        modelSaveEntity2.getModels().get(0).setRmse(rmse1);
        modelSaveEntity2.getModels().get(0).setMre(mre1);
        modelSaveEntity2.getModels().get(0).setDc(dc1);
        modelSaveEntity2.getModels().get(0).setQr(qr1);
        modelSaveEntity2.getModels().get(0).setQMax(param.getQ_max());
        modelSaveEntity2.getModels().get(0).setQMin(param.getQ_min());

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
     *
     * @param model
     * @param param
     * @param isCascade
     * @param category
     * @return
     */
    private ModelSaveEntity modelSave(NeuralNetwork model, ForecastInputParam param, boolean isCascade, int category) {
        ModelSaveEntity modelSaveEntity = null;
        String inputIndex = array2String(param.getInputIndex());
        if (model.getName() == null || model.getName().equals("")) {
            System.out.println("未发现模型");
        } else {
            String[] layerNum = param.getLayerCount().split(",");
            double outputNum = Double.parseDouble(layerNum[layerNum.length - 1]);
            TthModelEntity mle = new TthModelEntity();
            if (category == 0) {
                mle.setModelName(model.getName() + "_训练");
            } else if (category == 1) {
                mle.setModelName(model.getName());
            } else if (category == 2) {
                mle.setModelName(model.getName() + "_实时");
            }
            mle.setPeriod(param.getPeriod());
            mle.setDatasetStart(param.getDataSetStartTime());
            mle.setDatasetEnd(param.getDateSetEndTime());
            mle.setTestDatasetStart(param.getTestSetStartTime());
            mle.setTestDatasetEnd(param.getTestSetEndTime());
            mle.setOutputNum(outputNum);
            mle.setForecastDuanmian(param.getLocation());
            if (isCascade && param.getPeriod().equals("小时")) {
                mle.setInputIndex("all");
            } else {
                mle.setInputIndex(inputIndex);
            }
            mle.setErrorad(params.ERROR);
            mle.setTrainNum((double) params.trainNum);
            mle.setWidth(params.width);
            mle.setShiftError(params.shiftError);
            mle.setMaxRate(params.maxRate);
            mle.setMinRate(params.minRate);
            mle.setMobp(params.mobp);
            mle.setGamma(params.gamma);
            mle.setC(params.c);
            mle.setClusterad(params.cluster);
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
                            params.setPeriod(param.getPeriod());
                            params.setDatasetStart(param.getDataSetStartTime());
                            params.setDatasetEnd(param.getDateSetEndTime());
                            params.setTestDatasetStart(param.getTestSetStartTime());
                            params.setTestDatasetEnd(param.getTestSetEndTime());
                            params.setOutputNum(outputNum);
                            params.setForecastDuanmian(param.getLocation());
                            if (isCascade && param.getPeriod().equals("小时")) {
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
                mle.setErrorad(params.epsilon);
                mle.setTrainNum((double) params.TRAINING_REPS);
                mle.setMaxRate(params.LEARN_RATE);
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
                            params.setPeriod(param.getPeriod());
                            params.setDatasetStart(param.getDataSetStartTime());
                            params.setDatasetEnd(param.getDateSetEndTime());
                            params.setTestDatasetStart(param.getTestSetStartTime());
                            params.setTestDatasetEnd(param.getTestSetEndTime());
                            params.setOutputNum(outputNum);
                            params.setForecastDuanmian(param.getLocation());
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
     *
     * @param param
     * @param maxAndMin_input
     * @param maxAndMin_output
     * @return
     */
    private List<SimMaxMinEntity> maxAndMinSave(ForecastInputParam param, double[][] maxAndMin_input, double[][] maxAndMin_output) {
        String inputIndex = array2String(param.getInputIndex());
        List<SimMaxMinEntity> list = new ArrayList();
        int inputLen = maxAndMin_input[0].length;
        int outputLen = maxAndMin_output[0].length;
        for (int i = 0; i < inputLen; i++) {
            SimMaxMinEntity mme = new SimMaxMinEntity();
            mme.setForecastDuanmian(param.getLocation());
            mme.setPeriod(param.getPeriod());
            mme.setDataIndex((double) i);
            mme.setMaxValue(maxAndMin_input[0][i]);
            mme.setMinValue(maxAndMin_input[1][i]);
            mme.setInputIndex(inputIndex);
            list.add(mme);
        }
        for (int i = 0; i < outputLen; i++) {
            SimMaxMinEntity mme = new SimMaxMinEntity();
            mme.setForecastDuanmian(param.getLocation());
            mme.setPeriod(param.getPeriod());
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
     *
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
     *
     * @param data
     * @param maxminOld
     * @param DNNpara
     * @return
     */
    private ModelSaveEntity real_time_Forecast(ForecastInputParam param, double[][] data, double[][] maxminOld, double[][] DNNpara) {
        TrainResult trainResult;
        ModelSaveEntity modelSaveEntity;
        NeuralNetwork trainModels;
        params.paramSet(param);
        String trainModel = param.getModel();
        Boolean isRealTime = param.getIsRealtime();
        Boolean isCascade = false;
        List<double[][]> normalInput = predictNormalInput(data, maxminOld, param);
        double[][] norm_input = normalInput.get(0);
        double[][] norm_output = normalInput.get(1);
        double[][] maxAndMinOfoutput = normalInput.get(2);
        double[][] output = normalInput.get(3);

        if ((trainModel.equals("深度神经网络"))) {
            trainModels = new DNN_ADAM(params.dnnNet, params.rate, params.mobp, params.maxRate, params.minRate,
                    params.batch);
            double[][][] layer_weight = getParams(DNNpara);//输入率定好的参数
            trainResult = trainModels.simOutput1(norm_input, norm_output, layer_weight, params.dnnNet);//用率定好的参数模拟
        } else if (trainModel.equals("Elman神经网络")) {
            trainModels = new Elman(norm_input, params.elmanNet, params.LEARN_RATE, params.TRAINING_REPS,
                    params.epsilon);
            double[][][] layer_weight = getParams(DNNpara);//输入率定好的参数
            trainResult = trainModels.simOutput1(norm_input, norm_output, layer_weight, params.elmanNet);//用率定好的参数模拟
        } else {
            trainResult = new TrainResult();
            trainResult.setErrorMessage("未选择训练方式");
            return null;
        }

        double[][] result = MathUtils.reNormal(trainResult.getSimResult(), maxAndMinOfoutput);
        double rmse = MathUtils.RMSE(output, result);
        double dc = MathUtils.DC(output, result);
        double mre = MathUtils.MRE(output, result);
        double qr = MathUtils.QualifyRate(output, result);

        Date startDate = param.getDataSetStartTime();
        int outputNum = result[0].length;
        Date[][] dates;
        if (param.getPeriod().equals("月")) {
            dates = timeUtils.getMonthDateList(startDate, result.length);
        } else if (param.getPeriod().equals("旬")) {
            dates = timeUtils.getDateList(startDate, result.length, 10, 0);
        } else if (param.getPeriod().equals("日")) {
            dates = timeUtils.getDateList(startDate, result.length, 1, 0);
        } else {
            dates = timeUtils.getDateList(startDate, result.length, 0, 1);
        }
        List<TthResultEntity> resultList = new ArrayList();
        String inputIndex1 = array2String(param.getInputIndex());
        //储存模型结果（评价指标等）
        for (int i = 0; i < output.length; i++) {
            TthResultEntity hlr = new TthResultEntity();
            hlr.setForecastDuanmian(param.getLocation());
            hlr.setDatasetStart(param.getDataSetStartTime());
            hlr.setDatasetEnd(param.getDateSetEndTime());
            hlr.setTestDatasetStart(param.getTestSetStartTime());
            hlr.setTestDatasetEnd(param.getTestSetEndTime());
            hlr.setPeriod(param.getPeriod());
            hlr.setModelName(param.getNetClass() + "_实时");
            hlr.setOutputNum(1.0);
            hlr.setOutputIndex(0.0);
            if (isCascade && param.getPeriod().equals("小时")) {
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
        modelSaveEntity = modelSave(trainModels, param, isCascade, 2);
        modelSaveEntity.getModels().get(0).setRmse(rmse);
        modelSaveEntity.getModels().get(0).setMre(mre);
        modelSaveEntity.getModels().get(0).setDc(dc);
        modelSaveEntity.getModels().get(0).setQr(qr);
        modelSaveEntity.getModels().get(0).setQMax(param.getQ_max());
        modelSaveEntity.getModels().get(0).setQMin(param.getQ_min());

        modelSaveEntity.setModels(modelSaveEntity.getModels());
        modelSaveEntity.setResult(resultList);

        return modelSaveEntity;
    }

    /**
     * 训练数据归一化
     *
     * @param trainData
     * @param testData
     * @param param
     * @return
     */
    private List<double[][]> trainNormalInput(double[][] trainData, double[][] testData, ForecastInputParam param) {
        List<double[][]> normalInput = new ArrayList<>();
        int h = param.getHistory_day();
        int n = param.getHistory_factor();
        //如果是融雪径流
        if (param.getIsSnowMeltModel()) {
            double[][] trainInputFlow = new double[trainData.length][h];
            double[][] trainInputTemperature = new double[trainData.length][h];
            double[][] trainInputRain = new double[trainData.length][h];
            double[][] trainOutput = new double[trainData.length][1];

            double[][] testInputFlow = new double[testData.length][h];
            double[][] testInputTemperature = new double[testData.length][h];
            double[][] testInputRain = new double[testData.length][h];
            double[][] testOutput = new double[testData.length][1];


            for (int i = 0; i < trainData.length; i++) {
                for (int j = 0; j < h; j++) {
                    trainInputFlow[i][j] = trainData[i][j + 1];
                    if (n >= 2) {
                        trainInputTemperature[i][j] = trainData[i][j + 1 + h];
                        if (n >= 3) {
                            trainInputRain[i][j] = trainData[i][j + 1 + 2 * h];
                        }
                    }
                }
                trainOutput[i][0] = trainData[i][h * n + 1];
            }

            for (int i = 0; i < testData.length; i++) {
                for (int j = 0; j < h; j++) {
                    testInputFlow[i][j] = testData[i][j + 1];
                    if (n >= 2) {
                        testInputTemperature[i][j] = testData[i][j + 1 + h];
                        if (n >= 3) {
                            testInputRain[i][j] = testData[i][j + 1 + 2 * h];
                        }
                    }

                }
                testOutput[i][0] = testData[i][h * n + 1];
            }

            //遍历数据的最大最小值，便于归一化处理
            double[][] maxAndMinOfinputFlow = MathUtils.findMaxAndMin(trainInputFlow);
            double[][] maxAndMinOfinputTemperature = new double[0][];
            double[][] maxAndMinOfinputRain = new double[0][];
            int l = maxAndMinOfinputFlow[0].length;
            if (n >= 2) {
                maxAndMinOfinputTemperature = MathUtils.findMaxAndMin(trainInputTemperature);
                l = l + maxAndMinOfinputTemperature[0].length;
                if (n >= 3) {
                    maxAndMinOfinputRain = MathUtils.findMaxAndMin(trainInputRain);
                    l = l + maxAndMinOfinputRain[0].length;
                }
            }
            double[][] maxAndMinOfoutput = MathUtils.findMaxAndMin(trainOutput);
            double[][] maxAndMinOfinput = new double[maxAndMinOfinputFlow.length][l];
            for (int i = 0; i < maxAndMinOfinputFlow[0].length; i++) {
                for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                    maxAndMinOfinput[j][i] = maxAndMinOfinputFlow[j][i];
                }
            }
            if (n >= 2) {
                for (int i = 0; i < maxAndMinOfinputTemperature[0].length; i++) {
                    for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                        maxAndMinOfinput[j][i + maxAndMinOfinputFlow[0].length] = maxAndMinOfinputTemperature[j][i];
                    }
                }
                if (n >= 3) {
                    for (int i = 0; i < maxAndMinOfinputRain[0].length; i++) {
                        for (int j = 0; j < maxAndMinOfinputFlow.length; j++) {
                            maxAndMinOfinput[j][i + maxAndMinOfinputFlow[0].length + maxAndMinOfinputTemperature[0].length] = maxAndMinOfinputRain[j][i];
                        }
                    }
                }
            }

            double[][] maxAndMinOfinput_testFlow = MathUtils.findMaxAndMin(testInputFlow);
            double[][] maxAndMinOfinput_testTemperature = new double[0][];
            double[][] maxAndMinOfinput_testRain = new double[0][];
            if (n >= 2) {
                maxAndMinOfinput_testTemperature = MathUtils.findMaxAndMin(testInputTemperature);
                if (n >= 3) {
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
            if (n >= 2) {
                for (int i = 0; i < maxAndMinOfinputTemperature[0].length; i++) {// 输入

                    if (maxAndMinOfinputTemperature[0][i] < maxAndMinOfinput_testTemperature[0][i]) {
                        maxAndMinOfinputTemperature[0][i] = maxAndMinOfinput_testTemperature[0][i];
                    }

                    if (maxAndMinOfinputTemperature[1][i] > maxAndMinOfinput_testTemperature[1][i]) {
                        maxAndMinOfinputTemperature[1][i] = maxAndMinOfinput_testTemperature[1][i];
                    }
                }
                if (n >= 3) {
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
            if (n >= 2) {
                norm_inputTemperature = MathUtils.normalization(trainInputTemperature, maxAndMinOfinputTemperature);
                if (n >= 3) {
                    norm_inputRain = MathUtils.normalization(trainInputRain, maxAndMinOfinputRain);
                }
            }
            double[][] norm_output = MathUtils.normalization(trainOutput, maxAndMinOfoutput);

            double[][] norm_testInputFlow = MathUtils.normalization(testInputFlow, maxAndMinOfinputFlow);
            double[][] norm_testInputTemperature = new double[0][];
            double[][] norm_testInputRain = new double[0][];
            if (n >= 2) {
                norm_testInputTemperature = MathUtils.normalization(testInputTemperature, maxAndMinOfinputTemperature);
                if (n >= 3) {
                    norm_testInputRain = MathUtils.normalization(testInputRain, maxAndMinOfinputRain);
                }
            }
            double[][] norm_testOutput = MathUtils.normalization(testOutput, maxAndMinOfoutput);

            double[][] norm_input = new double[norm_inputFlow.length][norm_inputFlow[0].length * n];
            for (int i = 0; i < norm_inputFlow.length; i++) {
                for (int j = 0; j < norm_inputFlow[0].length; j++) {
                    norm_input[i][j] = norm_inputFlow[i][j];
                }
            }
            if (n >= 2) {
                for (int i = 0; i < norm_inputTemperature.length; i++) {
                    for (int j = 0; j < norm_inputTemperature[0].length; j++) {
                        norm_input[i][j + norm_inputFlow[0].length] = norm_inputTemperature[i][j];
                    }
                }
                if (n >= 3) {
                    for (int i = 0; i < norm_inputRain.length; i++) {
                        for (int j = 0; j < norm_inputRain[0].length; j++) {
                            norm_input[i][j + norm_inputFlow[0].length + norm_inputTemperature[0].length] = norm_inputRain[i][j];
                        }
                    }
                }
            }


            double[][] norm_testInput = new double[norm_testInputFlow.length][norm_testInputFlow[0].length * n];
            for (int i = 0; i < norm_testInputFlow.length; i++) {
                for (int j = 0; j < norm_testInputFlow[0].length; j++) {
                    norm_testInput[i][j] = norm_testInputFlow[i][j];
                }
            }
            if (n >= 2) {
                for (int i = 0; i < norm_testInputTemperature.length; i++) {
                    for (int j = 0; j < norm_testInputTemperature[0].length; j++) {
                        norm_testInput[i][j + norm_testInputFlow[0].length] = norm_testInputTemperature[i][j];
                    }
                }
                if (n >= 3) {
                    for (int i = 0; i < norm_testInputRain.length; i++) {
                        for (int j = 0; j < norm_testInputRain[0].length; j++) {
                            norm_testInput[i][j + norm_testInputFlow[0].length + norm_testInputTemperature[0].length] = norm_testInputRain[i][j];
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
        } else {
            double[][] trainInput = new double[trainData.length][param.getHistory_day()];
            double[][] trainOutput = new double[trainData.length][1];
            double[][] testInput = new double[testData.length][param.getHistory_day()];
            double[][] testOutput = new double[testData.length][1];


            for (int i = 0; i < trainData.length; i++) {
                for (int j = 0; j < param.getHistory_day(); j++) {
                    trainInput[i][j] = trainData[i][j + 1];
                }
                trainOutput[i][0] = trainData[i][param.getHistory_day() + 1];
            }

            for (int i = 0; i < testData.length; i++) {
                for (int j = 0; j < param.getHistory_day(); j++) {
                    testInput[i][j] = testData[i][j + 1];
                }
                testOutput[i][0] = testData[i][param.getHistory_day() + 1];
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
     *
     * @param data
     * @param maxminOld
     * @param param
     * @return
     */
    private List<double[][]> predictNormalInput(double[][] data, double[][] maxminOld, ForecastInputParam param) {
        List<double[][]> normalInput = new ArrayList<>();
        int h = param.getHistory_day();
        int n = param.getHistory_factor();

        if (param.getIsSnowMeltModel()) {
            double[][] input = new double[data.length][h * n];
            double[][] output = new double[data.length][1];

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < h; j++) {
                    input[i][j] = data[i][j + 1];
                    if (n >= 2) {
                        input[i][j + h] = data[i][j + 1 + h];
                        if (n >= 3) {
                            input[i][j + h * 2] = data[i][j + 1 + 2 * h];
                        }
                    }
                }
                output[i][0] = data[i][h * n + 1];
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
        } else {
            double[][] input = new double[data.length][param.getHistory_day()];
            double[][] output = new double[data.length][1];

            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < param.getHistory_day(); j++) {
                    input[i][j] = data[i][j + 1];
                }
                output[i][0] = data[i][param.getHistory_day() + 1];
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
