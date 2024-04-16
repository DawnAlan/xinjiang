package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<TemporaryXlsx> snowTrain(Object[][] snowInput, ForecastInputParam param) throws IOException, InvalidFormatException {
        List<TemporaryXlsx> result = new ArrayList();
        paramSet(snowInput, param);//设置输入
        List<List<Double>> paramResult=new ArrayList<>();
        Object[][] para = new Object[10][10];//初始化模型参数
        Object[][] maxminTemp = new Object[10][10];//初始化模型参数
        List<Double> paramdim1=new ArrayList<>();
        List<Double> paramdim2=new ArrayList<>();
        List<Double> paramdim3=new ArrayList<>();
        List<Double> paramvalue=new ArrayList<>();


        /**
         *  模型训练
         */
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.longTermForecast(param, snowInput, maxminTemp, para);

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

        double[][] maxmin = new double[param.getHistory_factor() * param.getHistory_day() + 1 ][2];
        for (int i = 0; i < maxmin.length; i++) {
            maxmin[i][0] = results.getMaxmin().get(i).getMaxValue();//最大值
            maxmin[i][1] = results.getMaxmin().get(i).getMinValue();//最小值
        }
        //模型参数写入
        TemporaryXlsx temxParam=new TemporaryXlsx();
        List<TemporaryXlsx> paramList=new ArrayList<>();

        String location = param.getLocation();
        String pathParam = "D:\\tth_system\\end\\file\\"+location+"融雪-PARAM.xlsx";
        ExcelTool.writeList2DoubleExcel(pathParam, "模型参数", paramResult);
//        File tempFileParam = File.createTempFile(Option + param.getNetClass()+"-PARAM",".xlsx");
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

        result.add(results.getParamxlsx().get(0));
        result.add(results.getMaxminxlsx().get(0));

        return result;
    }

    /**
     * 融雪模型预报
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    public Object[][] snowForecast(Object[][] inputTemp, ForecastInputParam param) throws IOException{
        paramSet(inputTemp, param);//设置输入
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();
        Object[][] snowFlood ;
        //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
        Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
        //读取模型参数
        Object[][] para=ExcelTool.readExcel(paraPath, "模型参数");
        //径流预报
        snowFlood = realTimeForecast(inputTemp, param, maxminOldTemp, para);

        return snowFlood;
    }

    /**
     * 实现实时预报
     * @param input
     * @param param
     * @param maxminOld
     * @param paraTemp
     * @return
     */
    public Object[][] realTimeForecast(Object[][] input, ForecastInputParam param, Object[][] maxminOld, Object[][] paraTemp)  {
        LongForecast longForecast = new LongForecast();
        int preNumber = param.getPeriodStepNumber()* param.getPeriodStepSize()/24+1;

        //预报期时间、流量赋值
        Date startDate = param.getPreStartTime();
        Date[][] dates = TimeUtils.getSelectDateList(startDate, preNumber, 1, 0);

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
            ModelSaveEntity pre_results = longForecast.longTermForecast(param,  pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - param.getHistory_day() - 1 + A).getSimOutput();
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
    public ForecastInputParam paramSet(Object[][] snowInput, ForecastInputParam param) {
        Integer[] inputIndex;
        //输入时段数的确定
        if (snowInput[0].length>3){
            param.setHistory_day(7);
            param.setHistory_factor(3);
            inputIndex = new Integer[param.getHistory_day() * param.getHistory_factor()];//温度、降水、径流
        }else {
            param.setHistory_day(7);
            param.setHistory_factor(2);
            inputIndex = new Integer[param.getHistory_day() * param.getHistory_factor()];//温度、径流
        }

        for (int i = 0; i < inputIndex.length; i++) {
            inputIndex[i] = i;
        }
        param.setInputIndex(inputIndex);//输入时段数
        //一些基础参数
        param.setNetClass(param.getModel());
        param.setMobp(10.0);
        param.setMinRate(0.0);
        param.setMaxRate(0.03);//这个会影响精度！！!
        String layers = param.getHistory_factor()*param.getHistory_day() +",11,11,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        param.setLayerCount(layers);
        param.setTrainNum(6000);
        param.setERROR(0.00001);
        param.setQ_max(200.0);
        param.setQ_min(0.0);
        //输入时间的确定
        Date startDate = (Date) snowInput[param.getHistory_day() - 1][0];
        param.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) snowInput[snowInput.length / 4 * 3-1][0];
        param.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) snowInput[snowInput.length / 4 * 3+param.getHistory_day()-1][0];
        param.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) snowInput[snowInput.length-1][0];
        param.setTestSetEndTime(testEndDate);//测试集结束时期
        //预报时段的确定
        param.setPeriodStepNumber(param.getPeriodStepNumber()/24+1);//预报时段数
        param.setPeriodStepSize(param.getPeriodStepSize());//预报时段步长
        param.setPreStartTime(param.getPreStartTime());//预报时间
        param.setIsSnowMeltModel(true);
        return param;
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
