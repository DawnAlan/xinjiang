package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.ParamsSetVO;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.model.MachineForecast.setLongFloodxlsx;
import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.getSpecificDate;


public class SnowMeltModel {

    /**
     * 融雪模型训练
     * @param snowInput
     * @param param
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public List<TemporaryXlsx> snowTrain(Object[][] snowInput, ForcastInputParam param) throws IOException, InvalidFormatException {
        List<TemporaryXlsx> result = new ArrayList();
        ParamsSetVO pvo = pvoSet(snowInput, param);//设置输入

        //数值的赋值
        int length = snowInput.length;
        int trainLength = length / 4 * 3;//训练集个数
        int historyDay= pvo.getHistory_day();//前期天数
        int outputNumber = snowInput.length - historyDay * 2 ;

        Object[][] modelparaTemp = new Object[10][10];//初始化模型参数


        //输入赋值
        Object[][] de_result =new Object[outputNumber+1][2];
        Object[][] longResult =new Object[outputNumber+1][9];
        double[][] reaResult = new double[outputNumber][1];//真实值

        for (int i = 0; i < trainLength-historyDay; i++) {
            reaResult[i][0]=Double.parseDouble(snowInput[i + historyDay][1].toString());
        }
        for (int i = trainLength-historyDay; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(snowInput[i + historyDay * 2 ][1].toString());
        }

        List<List<Double>> paramResult=new ArrayList<>();
        double[][] maxmin = new double[pvo.getHistory_factor() * pvo.getHistory_day() + 1 ][2];
        Object[][] para = new Object[modelparaTemp.length][modelparaTemp[0].length];
        double[][] maxminOld = null;
        List<Double> paramdim1=new ArrayList<>();
        List<Double> paramdim2=new ArrayList<>();
        List<Double> paramdim3=new ArrayList<>();
        List<Double> paramvalue=new ArrayList<>();

        for (int i = 0; i < modelparaTemp.length; i++) {
            for (int j = 0; j < modelparaTemp[0].length; j++) {
                para[i][j] = modelparaTemp[i][j];
            }
        }

        /**
         *  模型训练
         */
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.longTermForecast(pvo, false, true, snowInput, maxminOld, para);

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
        String pathParam = "D:\\tth_system\\end\\file\\"+location+"融雪-PARAM.xlsx";
        ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
//        File tempFileParam = File.createTempFile(Option + pvo.getNetClass()+"-PARAM",".xlsx");
//        String pathParam= tempFileParam.getAbsolutePath();
//        ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
        temxParam.setPath(pathParam);
        temxParam.setSheetName("模型参数");
        paramList.add(temxParam);
        results.setParamxlsx(paramList);
        //最大最小值写入
        TemporaryXlsx temxMaxmin=new TemporaryXlsx();
        List<TemporaryXlsx> maxminList=new ArrayList<>();
        String pathMaxmin = "D:\\tth_system\\end\\file\\"+location+"融雪最大最小值.xlsx";
        ExcelTool.writeDoubleExcel(pathMaxmin, "最大最小值", maxmin);
//        File tempFileMaxmin = File.createTempFile(Option +"最大最小值",".xlsx");
//        String pathMaxmin= tempFileMaxmin.getAbsolutePath();
//        ExcelTool.writeDoubleExcel(pathMaxmin, "最大最小值", maxmin);
        temxMaxmin.setPath(pathMaxmin);
        temxMaxmin.setSheetName("最大最小值");
        maxminList.add(temxMaxmin);
        results.setMaxminxlsx(maxminList);


        //分解后数据储存
        de_result[0][0]="时间";
        de_result[0][1]="预报流量";
        for (int i = 0; i < results.getResult().size(); i++) {
            de_result[i+1][0]=results.getResult().get(i).getResultDate();
            de_result[i+1][1]=results.getResult().get(i).getSimOutput();
        }
        result.add(results.getParamxlsx().get(0));
        result.add(results.getMaxminxlsx().get(0));

//        longResult[0][0]="时间";
//        longResult[0][1]="实测流量";
//        longResult[0][2]="预报流量";
//        longResult[0][3]="分解流量";
//        longResult[0][4]="评价指标";
//        longResult[1][4]=pvo.getNetClass()+"模型训练集";
//        longResult[2][4]=pvo.getNetClass()+"模型测试集";
//        longResult[0][5]="均方差";
//        longResult[0][6]="平均相对误差";
//        longResult[0][7]="一致性系数";
//        longResult[0][8]="合格率";
//        double rmse_train = 0;
//        double dc_train = 0;
//        double mre_train = 0;
//        double qr_train = 0;
//        double rmse_test = 0;
//        double dc_test = 0;
//        double mre_test = 0;
//        double qr_test = 0;
//        //结果赋值
//        for (int i = 1; i < outputNumber+1; i++) {
//            longResult[i][0] = i;
//            longResult[i][1] = reaResult[i-1][0];//实测流量
//            longResult[i][2] = de_result[i-1][1];//预报流量
//        }
//        double[][] trainPreResult = new double[reaResult.length / 4 * 3][1];
//        double[][] testPreResult = new double[reaResult.length / 4][1];
//        double[][] trainResult = new double[reaResult.length / 4 * 3][1];
//        double[][] testResult = new double[reaResult.length / 4][1];
//        for (int i = 0; i < reaResult.length / 4 * 3; i++) {
//            trainResult[i][0]= reaResult[i][0];
//            trainPreResult[i][0] = (double) de_result[i+1][1];
//        }
//        for (int i = 0; i < reaResult.length/4; i++) {
//            testResult[i][0]= reaResult[i+reaResult.length/4*3][0];
//            testPreResult[i][0] = (double) de_result[i+reaResult.length/4*3+1][1];
//        }
//        rmse_train = MathUtils.RMSE(trainPreResult, trainResult);
//        mre_train = MathUtils.MRE(trainPreResult, trainResult);
//        dc_train = MathUtils.DC(trainPreResult, trainResult);
//        qr_train = MathUtils.QualifyRate(trainPreResult, trainResult);
//        rmse_test = MathUtils.RMSE(testPreResult, testResult);
//        mre_test = MathUtils.MRE(testPreResult, testResult);
//        dc_test = MathUtils.DC(testPreResult, testResult);
//        qr_test = MathUtils.QualifyRate(testPreResult, testResult);
//        DecimalFormat df = new DecimalFormat("#.###");
//        longResult[1][5]=df.format(rmse_train);
//        longResult[1][6]=df.format(mre_train);
//        longResult[1][7]=df.format(dc_train);
//        longResult[1][8]=df.format(qr_train*100)+"%";
//        longResult[2][5]=df.format(rmse_test);
//        longResult[2][6]=df.format(mre_test);
//        longResult[2][7]=df.format(dc_test);
//        longResult[2][8]=df.format(qr_test*100)+"%";

//        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
//        System.out.println("-------------------------");
//        System.out.printf(" %-10.3f %-10.3f %-10.3f %-10.3f", rmse_test,mre_test,dc_test,qr_test);
//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\"+"融雪模型"+pvo.getNetClass()+"-RESULT.xlsx", Option, longResult);
        return result;
    }

    /**
     * 融雪模型预报
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    public Object[][] snowForecast(Object[][] inputTemp, ForcastInputParam param) throws IOException{

        ParamsSetVO pvo = pvoSet(inputTemp, param);//设置输入
        int preNumber = 0;
        String location = param.getLocation();
        if (location.equals("3号桥")){
            location = "楼庄子";
        }
//        String paraPath = param.getXlsx().get(0).getPath();
        String paraPath = "D:\\tth_system\\end\\file\\"+location+"融雪-PARAM.xlsx";
//        String maxminPath = param.getXlsx().get(1).getPath();
        String maxminPath = "D:\\tth_system\\end\\file\\"+location+"融雪最大最小值.xlsx";
        if(inputTemp.length>3){
            preNumber = param.getPeriodStepNumber()*param.getPeriodStepSize();
        }else {
            preNumber = param.getPeriodStepNumber()*param.getPeriodStepSize()/24+1;
        }

        //数值的赋值
        int historyDay= pvo.getHistory_day();//影响因子个数
        int outputNumber = inputTemp.length - historyDay;

        //输入赋值
        Object[][] de_result =new Object[preNumber+1][2];
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] snowFlood ;
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i+historyDay][1].toString());
        }

        //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
        Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
        //读取模型参数
        Object[][] paraTemp=ExcelTool.readExcel(paraPath, "模型参数");
        double[][] maxminOld = new double[maxminOldTemp.length][2];
        Object[][] para=new Object[paraTemp.length][4];

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

        //径流预报
        snowFlood = realTimeForecast(inputTemp, pvo, maxminOld, para);

        if (inputTemp[0].length>3) {//短期预报
            if (param.getLocation().equals("楼头区间")){
                snowFlood = magnification(snowFlood);
            }
            snowFlood = setLongFloodxlsx(snowFlood,pvo);
        }
        return snowFlood;
    }

    /**
     * 实现实时预报
     * @param input
     * @param pvo
     * @param maxminOld
     * @param paraTemp
     * @return
     */
    public Object[][] realTimeForecast(Object[][] input, ParamsSetVO pvo, double[][] maxminOld, Object[][] paraTemp)  {
        LongForecast longForecast = new LongForecast();
        boolean isRealtime=true;
        boolean isHistory=false;
        int preNumber = 0;
        if (input[0].length>3){
            preNumber = pvo.getPeriodStepNumber()* pvo.getPeriodStepSize();
        }else {
            preNumber = pvo.getPeriodStepNumber()* pvo.getPeriodStepSize()/24+1;
        }

        //日期赋值
        Date startDate = pvo.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        dates = TimeUtils.getSelectDateList(startDate, preNumber, 1, 0);

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
                if (input[0].length>3){
                    pre_input[input.length + a][3] = pre_input[input.length-1][3];
                }
            }
            ModelSaveEntity pre_results = longForecast.longTermForecast(pvo, isRealtime, isHistory, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - pvo.getHistory_day() - 1 + A).getSimOutput();
            predict[A][1] = input[0][1];
        }

        Object[][] result = new Object[preNumber][2];//把前面predict的最后一行去掉
        for (int i = 0; i < preNumber; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j]=predict[i][j];
            }
        }
        return result;
    }

    /**
     * 融雪模型参数设置
     * @param snowInput
     * @param param
     * @return
     */
    public ParamsSetVO pvoSet (Object[][] snowInput, ForcastInputParam param) {
        ParamsSetVO pvo = new ParamsSetVO();
        int[] inputIndex;
        //输入时段数的确定
        if (snowInput[0].length>3){
            pvo.setHistory_day(3);
            pvo.setHistory_factor(3);
            inputIndex = new int[3 * 3];//3温度、3降水、3径流
        }else {
            pvo.setHistory_day(3);
            pvo.setHistory_factor(2);
            inputIndex = new int[3 * 2];//3温度、3径流
        }

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
        String layers = pvo.getHistory_factor()*pvo.getHistory_day() +",11,11,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        pvo.setLayerCount(layers);
        pvo.setTrainNum(6000);
        pvo.setERROR(0.00001);
        pvo.setQ_max(200);
        pvo.setQ_min(0);
        //输入时间的确定
        Date startDate = (Date) snowInput[pvo.getHistory_day() - 1][0];
        pvo.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) snowInput[snowInput.length / 4 * 3-1][0];
        pvo.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) snowInput[snowInput.length / 4 * 3+pvo.getHistory_day()-1][0];
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

    /**
     * 区间倍率
     * @param data
     * @return
     */
    public Object[][] magnification(Object[][] data){
        int l;
        for (int i = 0; i < data.length; i++) {
            l = getSpecificDate((Date) data[i][0]).get("月");
            data[i][1] = setQjMagnification(l);
        }
        return data;
    }


    /**
     * 根据月份来提供区间的倍率
     * @param month
     * @return
     */
    public static Double setQjMagnification(int month){
        Double result = 0.0;
        switch (month){
            case 1:{
                result = 0.178571429;
                break;
            }
            case 2:{
                result = 0.167701863;
                break;
            }
            case 3:{
                result = 0.359649123;
                break;
            }
            case 4:{
                result = 0.251428571;
                break;
            }
            case 5:{
                result = 0.112087912;
                break;
            }
            case 6:{
                result = 0.1458333;
                break;
            }
            case 7:{
                result = 0.139564124;
                break;
            }
            case 8:{
                result = 0.168034766;
                break;
            }
            case 9:{
                result = 0.13950613;
                break;
            }
            case 10:{
                result = 0.174358974;
                break;
            }
            case 11:{
                result = 0.248;
                break;
            }
            case 12:{
                result = 0.172043011;
                break;
            }
        }
        return result;
    }

}
