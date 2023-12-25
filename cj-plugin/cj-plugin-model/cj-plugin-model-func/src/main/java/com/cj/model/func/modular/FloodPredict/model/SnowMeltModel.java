package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.ParamsSetVO;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.MathUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SnowMeltModel {
    //模型训练
    public List<TemporaryXlsx> SnowTrain(Object[][] snowInput, ForcastInputParam param) throws IOException, InvalidFormatException {
        List<TemporaryXlsx> result = new ArrayList();
        ParamsSetVO pvo = pvoSet(snowInput, param);//设置输入

        //数值的赋值
        int length = snowInput.length;
        int trainLength = length / 4 * 3;//训练集个数
        int influence_factor= pvo.getInfluence_factor();//影响因子个数
        int outputNumber = snowInput.length - influence_factor * 2 ;

        Object[][] inputTemp = snowInput;//输入数据
        Object[][] modelparaTemp = new Object[10][10];//初始化模型参数


        //输入赋值
        Object[][] de_result =new Object[outputNumber+1][2];
        Object[][] longResult =new Object[outputNumber+1][9];
        double[][] reaResult = new double[outputNumber][1];//真实值

        for (int i = 0; i < trainLength-influence_factor; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i + influence_factor][1].toString());
        }
        for (int i = trainLength-influence_factor; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i + influence_factor * 2 ][1].toString());
        }

        List<List<Double>> paramResult=new ArrayList<>();
        double[][] maxmin = new double[pvo.getInfluence_factor() * 3 + 1 ][2];
        Object[][] input = new Object[inputTemp.length][4];//多少列可以根据输入来改变
        Object[][] para = new Object[modelparaTemp.length][modelparaTemp[0].length];
        double[][] maxminOld = null;
        List<Double> paramdim1=new ArrayList<>();
        List<Double> paramdim2=new ArrayList<>();
        List<Double> paramdim3=new ArrayList<>();
        List<Double> paramvalue=new ArrayList<>();

        for (int i = 0; i < inputTemp.length; i++) {
            input[i][0] = inputTemp[i][0];//时间
            input[i][1] = inputTemp[i][1];//径流
            input[i][2] = inputTemp[i][2];//温度
            input[i][3] = inputTemp[i][3];//降水
            }
        for (int i = 0; i < modelparaTemp.length; i++) {
                for (int j = 0; j < modelparaTemp[0].length; j++) {
                    para[i][j] = modelparaTemp[i][j];
                }
            }

            /**
             *  模型训练
             */
            LongForecast longForecast = new LongForecast();
            ModelSaveEntity results = longForecast.LongTermForecast(pvo, false, true, input, maxminOld, para);

            for (int i = 0; i < results.getParams().size(); i++) {
                paramdim1.add(Double.parseDouble(results.getParams().get(i).getParamDim1())) ;
                paramdim2.add(Double.parseDouble(results.getParams().get(i).getParamDim2()));
                paramdim3.add(Double.parseDouble(results.getParams().get(i).getParamDim3()));
                paramvalue.add(results.getParams().get(i).getValue());
            }
            paramResult.add(paramdim1);
            paramResult.add(paramdim2);
            paramResult.add(paramdim3);
            paramResult.add(paramvalue);

            for (int i = 0; i < maxmin.length; i++) {
                maxmin[i][0] = results.getMaxmin().get(i).getMaxValue();//最大值
                maxmin[i][1] = results.getMaxmin().get(i).getMinValue();//最小值
            }
            //模型参数写入
            TemporaryXlsx temxParam=new TemporaryXlsx();
            List<TemporaryXlsx> paramList=new ArrayList<>();

            String location = pvo.getForecastDuanmian();
            String Option = location + "融雪";
            File tempFileParam = File.createTempFile(Option + pvo.getNetClass()+"-PARAM",".xlsx");
            String pathParam= tempFileParam.getAbsolutePath();
            ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
            temxParam.setPath(pathParam);
            temxParam.setSheetName("模型参数");
            paramList.add(temxParam);
            results.setParamxlsx(paramList);
//            ExcelTool.writeListExcel("D:\\204\\2.头屯河\\径流预报数据文件\\"+ Option + pvo.getNetClass()+"-PARAM.xlsx", Option, paramResult);
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
//            ExcelTool.writeDoubleExcel("D:\\204\\2.头屯河\\径流预报数据文件\\"+ Option +"最大最小值.xlsx", Option, maxmin);


            //分解后数据储存
            de_result[0][0]="时间";
            de_result[0][1]="预报流量";
            for (int i = 0; i < results.getResult().size(); i++) {
                de_result[i+1][0]=results.getResult().get(i).getResultDate();
                de_result[i+1][1]=results.getResult().get(i).getSimOutput();
            }
            result.add(results.getParamxlsx().get(0));
            result.add(results.getMaxminxlsx().get(0));

//        System.out.println("实测\t\t\t预报");
//        System.out.println("-------------------------");
        //最终数据输出
//        for(int i = 0; i < outputNumber; i++){
//            System.out.printf(String.format("%-10.3f %-10.3f\n ",reaResult[i][0], (double)de_result[i+1][1]));
//        }

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
        for (int i = 1; i < outputNumber+1; i++) {
            longResult[i][0] = i;
            longResult[i][1] = reaResult[i-1][0];//实测流量
            longResult[i][2] = de_result[i-1][1];//预报流量

        }
        double[][] trainPreResult = new double[reaResult.length / 4 * 3][1];
        double[][] testPreResult = new double[reaResult.length / 4][1];
        double[][] trainResult = new double[reaResult.length / 4 * 3][1];
        double[][] testResult = new double[reaResult.length / 4][1];
        for (int i = 0; i < reaResult.length / 4 * 3; i++) {
            trainResult[i][0]= reaResult[i][0];
            trainPreResult[i][0] = (double) de_result[i+1][1];
        }
        for (int i = 0; i < reaResult.length/4; i++) {
            testResult[i][0]= reaResult[i+reaResult.length/4*3][0];
            testPreResult[i][0] = (double) de_result[i+reaResult.length/4*3+1][1];
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

//        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
//        System.out.println("-------------------------");
//        System.out.printf(" %-10.3f %-10.3f %-10.3f %-10.3f", rmse_test,mre_test,dc_test,qr_test);
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\"+"融雪模型"+pvo.getNetClass()+"-RESULT.xlsx", Option, longResult);
        return result;
    }
    public Object[][] Forcast(Object[][] inputTemp,  ForcastInputParam param ) throws IOException, InvalidFormatException, ParseException {

        ParamsSetVO pvo = pvoSet(inputTemp, param);//设置输入
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();
        int preNumber = param.getPeriodStepNumber()*param.getPeriodStepSize()/24+1;

        //数值的赋值
        int influence_factor= pvo.getInfluence_factor();//影响因子个数
        int outputNumber = inputTemp.length - influence_factor;


        //输入赋值
        Object[][] de_result =new Object[preNumber+1][2];
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] snowFlood ;
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i+influence_factor][1].toString());
        }


        //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
        Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
        //读取模型参数
        Object[][] paraTemp=ExcelTool.readExcel(paraPath, "模型参数");
        double[][] maxminOld = new double[maxminOldTemp.length][2];
        Object[][] para=new Object[paraTemp.length][4];
        Object[][] input = new Object[inputTemp.length][4];

        for (int i = 0; i < maxminOldTemp.length; i++) {
                for (int j = 0; j < 2; j++) {
                    maxminOld[i][j] = Double.parseDouble(maxminOldTemp[i][j].toString());
                }
        }
        for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < 4; j++) {
                    para[i][j] = paraTemp[i][j];
                }
        }
        for (int i = 0; i < inputTemp.length; i++) {
            input[i][0] = inputTemp[i][0];//时间
            input[i][1] = inputTemp[i][1];//径流
            input[i][2] = inputTemp[i][2];//温度
            input[i][3] = inputTemp[i][3];//降水
        }
        if (pvo.getNetClass().equals("SVM")){
                double g = Double.parseDouble(paraTemp[0][4].toString());
                double c = Double.parseDouble(paraTemp[1][4].toString());
                double p = Double.parseDouble(paraTemp[2][4].toString());
                double r = 0.02;
                pvo.setC(c);
                pvo.setGamma(g);
                pvo.setMobp(p);
                pvo.setMaxRate(r);
        }


        //径流预报
        snowFlood = RealTimeForcast(input, pvo, maxminOld, para);
//        System.out.println("\n时间\t预报值");
//        System.out.println("-------------------------");
//        for(int i = 0; i < preNumber; i++){
//            System.out.printf("Formatted Date: %s%n %-10.2f\n", snowFlood[i][0], snowFlood[i][1]);
//        }
        return snowFlood;
    }
    public  Object[][] RealTimeForcast(Object[][] input, ParamsSetVO pvo,  double[][] maxminOld,  Object[][] paraTemp) throws IOException{
        LongForecast longForecast = new LongForecast();
        boolean isRealtime=true;
        boolean isHistory=false;
        int preNumber = pvo.getPeriodStepNumber()* pvo.getPeriodStepSize()/24+1;
        //日期赋值
        Date startDate = pvo.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        dates = TimeUtils.getDateList(startDate, preNumber, 1, 0, 1);

        /**
         * 当前只实现了提供往前数据，下一天开始预报的功能；可二次开发实现隔天预报，需要判断dates！
         */
        //预报赋值
        Object[][] predict = new Object[preNumber + 1][2];
        predict[0][1] = input[input.length-1][1];
        for (int A = 1; A < preNumber + 1; A++) {
            Object[][] pre_input = new Object[input.length + A][input[0].length];
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < input[0].length; j++) {
                    pre_input[i][j] = input[i][j];
                }
            }
            for (int a = 0; a < A; a++) {
                predict[a][0] = dates[a][0];
                pre_input[input.length + a][0] = dates[a][0];
                pre_input[input.length + a][1] = predict[a][1];
                pre_input[input.length + a][2] = pre_input[input.length-1][2];
                pre_input[input.length + a][3] = pre_input[input.length-1][3];
            }
            ModelSaveEntity pre_results = longForecast.LongTermForecast(pvo, isRealtime, isHistory, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - pvo.getInfluence_factor() - 1 + A).getSimOutput();
            predict[A][1] = input[0][1];
        }

        Object[][] predict1 = new Object[preNumber][2];//把前面predict的最后一行去掉
        for (int i = 0; i < preNumber; i++) {
            for (int j = 0; j < 2; j++) {
                predict1[i][j]=predict[i][j];
            }
        }
        return predict1;
    }
    public ParamsSetVO pvoSet (Object[][] snowInput, ForcastInputParam param) {
        ParamsSetVO pvo = new ParamsSetVO();

        //输入时段数的确定
        pvo.setInfluence_factor(3);

        int[] inputIndex = new int[pvo.influence_factor * 3];//3温度、3降水、3径流
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
        pvo.setMaxRate(0.03);//这个会影响精度！！!
        String layers = pvo.getInfluence_factor() +",11,11,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        pvo.setLayerCount(layers);
        pvo.setTrainNum(6000);
        pvo.setERROR(0.00001);
        pvo.setQ_max(200);
        pvo.setQ_min(0);
        //输入时间的确定
        Date startDate = (Date) snowInput[pvo.influence_factor - 1][0];
        pvo.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) snowInput[snowInput.length / 4 * 3-1][0];
        pvo.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) snowInput[snowInput.length / 4 * 3+pvo.influence_factor-1][0];
        pvo.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) snowInput[snowInput.length-1][0];
        pvo.setTestSetEndTime(testEndDate);//测试集结束时期
        //预报时段的确定
        pvo.setPeriodStepNumber(param.getPeriodStepNumber());//预报时段数
        pvo.setPeriodStepSize(param.getPeriodStepSize());//预报时段步长
        pvo.setPreStartTime(param.getPreStartTime());//预报时间
        pvo.setIsSnowMeltModel(true);
        return pvo;
    }
}
