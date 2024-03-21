package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.ParamsSetVO;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.MathUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MachineModel {

    //模型训练
    public List<TemporaryXlsx> ModelTrain(Object[][] modelTrainInput, ForcastInputParam param)
            throws IOException, InvalidFormatException {
        List<TemporaryXlsx> result = new ArrayList();
        ParamsSetVO pvo = pvoSet(modelTrainInput, param);//设置输入

        //数值的赋值
        param.setVmdK(6);
        int K = param.vmdK;//分解层数
        int length = modelTrainInput.length;
        int trainLength = length / 4 * 3;//训练集个数
        int historyDay= pvo.getHistory_day();//前期天数
        int outputNumber = modelTrainInput.length - historyDay * 2 + 2;

        Object[][] inputTemp = modelTrainInput;//输入数据，第一列为时间，第二列为历史径流
        Object[][] modelparaTemp = new Object[10][10];//初始化模型参数
        inputTemp=inputProcessing(inputTemp,param);
        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][]vmdOutput;
        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        vmdOutput=VMD.vmd(vmdInput,K);

        //输入赋值
        Object[][] de_result =new Object[outputNumber+1][K+1];
        double[][] preResult = new double[outputNumber][1];//分解后的预测值
        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值

        for (int i = 0; i < trainLength-historyDay+1; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i + historyDay - 1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0]=vmdreaResult[i][0]+vmdOutput[j][i + historyDay - 1];
            }
        }
        for (int i = trainLength-historyDay+1; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i + historyDay * 2 - 2][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0]=vmdreaResult[i][0]+vmdOutput[j][i+historyDay*2-2];
            }
        }

        List<List<Double>> paramResult=new ArrayList<>();

        double[][] maxmin = new double[pvo.getHistory_day() + 1][2 *  param.getVmdK()];
        //K个子序列逐步训练
        for (int k = 0; k < K; k++) {
            Object[][] input = new Object[inputTemp.length][2];//多少列可以根据输入来改变
            Object[][] para = new Object[modelparaTemp.length][modelparaTemp[0].length];
            double[][] maxminOld = null;
            List<Double> paramdim1=new ArrayList<>();
            List<Double> paramdim2=new ArrayList<>();
            List<Double> paramdim3=new ArrayList<>();
            List<Double> paramvalue=new ArrayList<>();

            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];//时间
                input[i][1] = vmdOutput[k][i];
            }
            for (int i = 0; i < modelparaTemp.length; i++) {
                for (int j = 0; j < modelparaTemp[0].length; j++) {
                    para[i][j] = modelparaTemp[i][j];
                }
            }

            /**
             *  分解后模型训练
             */
            LongForecast longForecast = new LongForecast();
            ModelSaveEntity results = longForecast.LongTermForecast(pvo, false, true, input, maxminOld, para);

            for (int i = 0; i < results.getParams().size(); i++) {
                paramdim1.add(Double.parseDouble(results.getParams().get(i).getParamDim1()));
                paramdim2.add(Double.parseDouble(results.getParams().get(i).getParamDim2()));
                paramdim3.add(Double.parseDouble(results.getParams().get(i).getParamDim3()));
                if (results.getParams().get(i).getValue().isNaN()){
                    results.getParams().get(i).setValue(1.0);
                }
                paramvalue.add(results.getParams().get(i).getValue());
            }
            paramResult.add(paramdim1);
            paramResult.add(paramdim2);
            paramResult.add(paramdim3);
            paramResult.add(paramvalue);

            for (int i = 0; i < maxmin.length; i++) {
                maxmin[i][2*k] = results.getMaxmin().get(i).getMaxValue();//最大值
                maxmin[i][2*k+1] = results.getMaxmin().get(i).getMinValue();//最小值
            }

            if (k >= param.getVmdK() - 1){
                //模型参数写入
                TemporaryXlsx temxParam=new TemporaryXlsx();
                List<TemporaryXlsx> paramList=new ArrayList<>();
                String period = pvo.getForecastPeriod();
                String location = pvo.getForecastDuanmian();
                String Option = location + period;
                File tempFileParam = File.createTempFile(Option + pvo.getNetClass()+"-PARAM",".xlsx");
                String pathParam= tempFileParam.getAbsolutePath();
                ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
                temxParam.setPath(pathParam);
                temxParam.setSheetName("模型参数");
                paramList.add(temxParam);
                results.setParamxlsx(paramList);
                //最大最小值写入
                TemporaryXlsx temxMaxmin=new TemporaryXlsx();
                List<TemporaryXlsx> maxminList=new ArrayList<>();
                File tempFileMaxmin = File.createTempFile(Option +"最大最小值",".xlsx");
                String pathMaxmin= tempFileMaxmin.getAbsolutePath();
                ExcelTool.writeDoubleExcel(pathMaxmin, "最大最小值", maxmin);
                temxMaxmin.setPath(pathMaxmin);
                temxMaxmin.setSheetName("最大最小值");
                maxminList.add(temxMaxmin);
                results.setMaxminxlsx(maxminList);
            }

            //分解后数据储存
            de_result[0][0]="时间";
            de_result[0][k+1]="预报流量";
            for (int i = 0; i < results.getResult().size(); i++) {
                de_result[i+1][0]=results.getResult().get(i).getResultDate();
                de_result[i+1][k+1]=results.getResult().get(i).getSimOutput();
            }
            if (k >= param.getVmdK() - 1){
                result.add(results.getParamxlsx().get(0));
                result.add(results.getMaxminxlsx().get(0));
            }
        }

        //最终数据输出
//        System.out.println("实测\t\t\t分解\t\t\t预报");
//        System.out.println("-------------------------");
        for(int i = 0; i < outputNumber; i++){
            for (int j = 0; j < K; j++) {
                preResult[i][0]=preResult[i][0]+Double.parseDouble(de_result[i+1][j+1].toString());
            }
//            System.out.printf(" %-10.3f %-10.3f %-10.3f\n", reaResult[i][0],vmdreaResult[i][0],preResult[i][0]);
        }
//        Trainresult(de_result, reaResult, preResult, vmdreaResult, pvo);
        return result;
        }

    /**
     * 参数确定
     * @param modelTrainInput
     * @param param
     * @return
     */
    public static ParamsSetVO pvoSet (Object[][] modelTrainInput, ForcastInputParam param) {
        ParamsSetVO pvo = new ParamsSetVO();
        //输入时段数的确定
        String period=param.getPeriod();
        pvo.setHistory_factor(1);
        switch (period) {
            case "月":
            case "旬":
            case "日":
                pvo.setHistory_day(7);
                break;
            case "小时":
                pvo.setHistory_day(24);
                break;
        }
        int[] inputIndex = new int[pvo.history_day];
        for (int i = 0; i < inputIndex.length; i++) {
            inputIndex[i] = i;
        }
        pvo.setInputIndex(inputIndex);//输入时段数
        //一些基础参数
        pvo.setForecastDuanmian(param.getLocation());
        pvo.setForecastPeriod(param.getPeriod());
        pvo.setNetClass(param.getModel());
        pvo.setC(10);
        pvo.setGamma(10);
        pvo.setMobp(10);
        pvo.setMaxRate(0.032);//这个会影响精度！！!
        String layers = pvo.getHistory_day() +",11,11,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        pvo.setLayerCount(layers);
        pvo.setTrainNum(6000);
        pvo.setERROR(0.00001);
        pvo.setQ_max(200);
        pvo.setQ_min(0);
        //输入时间的确定
        Date startDate = (Date) modelTrainInput[pvo.history_day-1][0];
        pvo.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) modelTrainInput[modelTrainInput.length/4*3-1][0];
        pvo.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) modelTrainInput[modelTrainInput.length/4*3+pvo.history_day-1][0];
        pvo.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) modelTrainInput[modelTrainInput.length-1][0];
        pvo.setTestSetEndTime(testEndDate);//测试集结束时期
        pvo.setIsSnowMeltModel(false);
        return pvo;
    }

    /**
     * 输入数据的处理，求与均值之间的偏差
     * @param input
     * @param param
     * @return
     */
    public  Object[][] inputProcessing(Object[][] input,ForcastInputParam param){
        int month = 0;
        for (int i = 0; i < input.length; i++) {
            if (param.getLocation().equals("3号桥")||param.getLocation().equals("楼庄子")){
                Date date = (Date) input[i][0];
                month = DataUtils.getSpecificDate(date).get("月");
                switch (month){
                    case 1:
                        input[i][1]=(1.29-(double) input[i][1])/1.29;
                        break;
                    case 2:
                        input[i][1]=(1.19-(double) input[i][1])/1.19;
                        break;
                    case 3:
                        input[i][1]=(1.77-(double) input[i][1])/1.77;
                        break;
                    case 4:
                        input[i][1]=(2.78-(double) input[i][1])/2.78;
                        break;
                    case 5:
                        input[i][1]=(8.13-(double) input[i][1])/8.13;
                        break;
                    case 6:
                        input[i][1]=(17.92-(double) input[i][1])/17.92;
                        break;
                    case 7:
                        input[i][1]=(22.05-(double) input[i][1])/22.05;
                        break;
                    case 8:
                        input[i][1]=(16.13-(double) input[i][1])/16.13;
                        break;
                    case 9:
                        input[i][1]=(7.84-(double) input[i][1])/7.84;
                        break;
                    case 10:
                        input[i][1]=(3.85-(double) input[i][1])/3.85;
                        break;
                    case 11:
                        input[i][1]=(2.23-(double) input[i][1])/2.23;
                        break;
                    case 12:
                        input[i][1]=(1.52-(double) input[i][1])/1.52;
                        break;
                }
            }
            else if (param.getLocation().equals("楼头区间")){
                double proportion = 0.058;
                Date date = (Date) input[i][0];
                month = DataUtils.getSpecificDate(date).get("月");
                switch (month){
                    case 1:
                        input[i][1]=(1.29*proportion-(double) input[i][1])/1.29*proportion;
                        break;
                    case 2:
                        input[i][1]=(1.19*proportion-(double) input[i][1])/1.19*proportion;
                        break;
                    case 3:
                        input[i][1]=(1.77*proportion-(double) input[i][1])/1.77*proportion;
                        break;
                    case 4:
                        input[i][1]=(2.78*proportion-(double) input[i][1])/2.78*proportion;
                        break;
                    case 5:
                        input[i][1]=(8.13*proportion-(double) input[i][1])/8.13*proportion;
                        break;
                    case 6:
                        input[i][1]=(17.92*proportion-(double) input[i][1])/17.92*proportion;
                        break;
                    case 7:
                        input[i][1]=(22.05*proportion-(double) input[i][1])/22.05*proportion;
                        break;
                    case 8:
                        input[i][1]=(16.13*proportion-(double) input[i][1])/16.13*proportion;
                        break;
                    case 9:
                        input[i][1]=(7.84*proportion-(double) input[i][1])/7.84*proportion;
                        break;
                    case 10:
                        input[i][1]=(3.85*proportion-(double) input[i][1])/3.85*proportion;
                        break;
                    case 11:
                        input[i][1]=(2.23*proportion-(double) input[i][1])/2.23*proportion;
                        break;
                    case 12:
                        input[i][1]=(1.52*proportion-(double) input[i][1])/1.52*proportion;
                        break;
                }
            }
        }
        return input;
    }

    /**
     * 方便检验训练成果
     * @param de_result
     * @param reaResult
     * @param preResult
     * @param vmdreaResult
     * @param pvo
     * @return
     */
    public static Object[][] Trainresult(Object[][] de_result, double[][] reaResult,double[][] preResult, double[][] vmdreaResult,ParamsSetVO pvo) throws IOException, InvalidFormatException {
        Object[][] longResult =new Object[de_result.length][9];
        longResult[0][0]="时间";
        longResult[0][1]="实测流量";
        longResult[0][2]="预报流量";
        longResult[0][3]="分解流量";
        longResult[0][4]="评价指标";
        longResult[1][4]=pvo.getNetClass()+"模型训练集";
        longResult[2][4]=pvo.getNetClass()+"模型测试集";
        longResult[0][5]="均方差";
        longResult[0][6]="平均相对误差";
        longResult[0][7]="一致性系数";
        longResult[0][8]="合格率";
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
            longResult[i][1] = reaResult[i-1][0];//实测流量
            longResult[i][2] = preResult[i-1][0];//预报流量
            longResult[i][3] = vmdreaResult[i-1][0];//vmd实测流量
        }
        String Option = pvo.getForecastDuanmian() + pvo.getForecastPeriod();
        double[][] trainPreResult = new double[reaResult.length / 4 * 3][1];
        double[][] testPreResult = new double[reaResult.length / 4][1];
        double[][] trainResult = new double[reaResult.length / 4 * 3][1];
        double[][] testResult = new double[reaResult.length / 4][1];
        for (int i = 0; i < reaResult.length / 4 * 3; i++) {
            trainResult[i][0]= reaResult[i][0];
            trainPreResult[i][0] = preResult[i][0];
        }
        for (int i = 0; i < reaResult.length/4; i++) {
            testResult[i][0]= reaResult[i+reaResult.length/4*3][0];
            testPreResult[i][0] = preResult[i+reaResult.length/4*3][0];
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
        longResult[1][5]=df.format(rmse_train);
        longResult[1][6]=df.format(mre_train);
        longResult[1][7]=df.format(dc_train);
        longResult[1][8]=df.format(qr_train*100)+"%";
        longResult[2][5]=df.format(rmse_test);
        longResult[2][6]=df.format(mre_test);
        longResult[2][7]=df.format(dc_test);
        longResult[2][8]=df.format(qr_test*100)+"%";
        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
        System.out.println("----------------------------------");
        System.out.printf(" %-10.3f %-10.3f %-10.3f %-10.3f", rmse_test,mre_test,dc_test,qr_test);
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\"+pvo.getNetClass()+"-RESULT.xlsx", Option, longResult);
        return longResult;
    }

}

