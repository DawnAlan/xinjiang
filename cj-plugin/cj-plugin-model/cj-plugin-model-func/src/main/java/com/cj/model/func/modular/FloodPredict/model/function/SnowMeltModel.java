package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.*;


public class SnowMeltModel {
    TimeUtils tu = new TimeUtils();
    MachineDataUtils mdu = new MachineDataUtils();


    /**
     * 融雪模型参数设置
     *
     * @param snowInput
     * @param param
     * @return
     */
    public ForecastInputParam paramSet(Object[][] snowInput, ForecastInputParam param) {
        Integer[] inputIndex;
        //输入时段数的确定
        //温度、径流
        Map<String,Integer> factor = new HashMap<>();
        if (!param.getIsSnowMeltModel()) {
            factor.put("径流",5);
            factor.put("降水",2);
            factor.put("温度",2);
            param.setFactors(factor);
            param.setHistory_day(5);
            param.setHistory_factor(9);
        } else {
            factor.put("径流",5);
            factor.put("温度",1);
            param.setFactors(factor);
            param.setHistory_day(5);
            param.setHistory_factor(6);
        }
        inputIndex = new Integer[param.getHistory_factor()];//温度、降水、径流
        for (int i = 0; i < inputIndex.length; i++) {
            inputIndex[i] = i;
        }
        param.setInputIndex(inputIndex);//输入时段数
        //一些基础参数
        param.setNetClass("Elman神经网络");
        param.setModel("Elman神经网络");
        param.setPeriod("日");
        param.setVmdK(1);
        param.setMinRate(0.0001);
        param.setMaxRate(0.04);
        param.setMobp(2.0);
        String layers = param.getHistory_factor() + ",30,30,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        param.setLayerCount(layers);
        param.setTrainNum(20000);
        param.setERROR(0.00001);
        param.setQ_max(200.0);
        param.setQ_min(0.0);
        //输入时间的确定
        Date startDate = (Date) snowInput[param.getHistory_day() - 1][0];
        param.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) snowInput[snowInput.length / 4 * 3 - 1][0];
        param.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) snowInput[snowInput.length / 4 * 3 + param.getHistory_day() - 1][0];
        param.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) snowInput[snowInput.length - 1][0];
        param.setTestSetEndTime(testEndDate);//测试集结束时期
        return param;
    }

    /**
     * 融雪模型训练
     *
     * @param snowInput
     * @param param
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public void snowTrain(Object[][] snowInput, ForecastInputParam param) {
        param.setIsRealtime(false);
        paramSet(snowInput, param);//设置输入
        List<List<Double>> paramResult = new ArrayList<>();
        Object[][] para = new Object[10][10];//初始化模型参数
        Object[][] maxminTemp = new Object[10][10];//初始化模型参数
        double[][] maxmin = new double[param.getHistory_factor()+1][2];
        //模型训练
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.longTermForecast(param, snowInput, maxminTemp, para);
        //模型参数的存储
        results = mdu.setModelParams(results,param,0,paramResult,maxmin);
        //模型合格率
        trainResult(results,param);
    }

    /**
     * 融雪模型预报
     *
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    @SneakyThrows
    public List<Flood> snowForecast(Object[][] inputTemp, ForecastInputParam param) {
        List<Flood> result = new ArrayList<>();
//        InputUtils.getData2(param.getFilePath());
        paramSet(inputTemp, param);//设置输入
        String location = param.getLocation().equals("3号桥")?"楼庄子": param.getLocation();
        String period = param.getPeriod();
        if (param.getPeriod().equals("日")){
            period = "日-"+ mdu.judgeKPF(param.getPreStartTime());
        }
        period = param.getIsSnowMeltModel()?"融雪":period;
        Object[][] snowFlood;
        //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
        Object[][] maxminOldTemp = InputUtils.machineMaxMin.get(location+period+"-最大最小值");
        //读取模型参数
        Object[][] para = InputUtils.machineParam.get(location+period+"-模型参数");
        //径流预报
        snowFlood = realTimeForecast(inputTemp, param, maxminOldTemp, para);
        if (param.getIsAverage()){
            snowFlood = mdu.resultProcessingDay(snowFlood,location);
        }
        result = mdu.setLongFlood(snowFlood, param);
        return result;
    }

    /**
     * 实现实时预报
     *
     * @param input
     * @param param
     * @param maxminOld
     * @param paraTemp
     * @return
     */
    public Object[][] realTimeForecast(Object[][] input, ForecastInputParam param, Object[][] maxminOld, Object[][] paraTemp) {
        LongForecast longForecast = new LongForecast();
        int preNumber;
        if (param.getIsSnowMeltModel()){
            preNumber = param.getPeriodStepNumber() * param.getPeriodStepSize() / 24 + 1;
        }else {
            preNumber = param.getPeriodStepNumber() * param.getPeriodStepSize();
        }
        param.setIsRealtime(true);
        //预报期时间、流量赋值
        Date startDate = param.getPreStartTime();
        Date[][] dates = tu.getSelectDateList(startDate, preNumber, 1, 0);
        List<PredictInputData> preRainTem = param.getPreRainTem();
        //预报赋值
        Object[][] predict = new Object[preNumber + 1][2];
        predict[0][1] = input[input.length - 1][1];
        for (int A = 1; A < preNumber + 1; A++) {
            Object[][] pre_input = new Object[input.length + A][input[0].length];
            for (int i = 0; i < input.length; i++) {
                System.arraycopy(input[i], 0, pre_input[i], 0, input[i].length);
            }
            for (int a = 0; a < A; a++) {
                predict[a][0] = dates[a][0];
                pre_input[input.length + a][0] = dates[a][0];
                pre_input[input.length + a][1] = predict[a][1];
                double temperature = 0.0;
                double rainfall=0.0;
                for (int i = 0; i < preRainTem.size(); i++) {
                    if (tu.DateCompare(dates[a][0],preRainTem.get(i).getDates(),"日")&&param.getLocation().equals(preRainTem.get(i).getLocation())){
                        temperature = preRainTem.get(i).getTemperature();
                        rainfall = preRainTem.get(i).getRainfall();
                    }
                }
                pre_input[input.length + a][2] = temperature;
                if (!param.getIsSnowMeltModel()) {
                    pre_input[input.length + a][3] = rainfall;
                }
            }
            ModelSaveEntity pre_results = longForecast.longTermForecast(param, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - param.getHistory_day() - 1 + A).getSimOutput()[0];
            predict[A][1] = input[0][1];
        }

        Object[][] result = new Object[preNumber][2];//把前面predict的最后一行去掉
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j] = predict[i][j];
            }
        }
        return result;
    }

    public void trainResult(ModelSaveEntity results,ForecastInputParam param){
        int l =results.getResult().size();
        int p =param.getPredict_day();
        Object[][] result0 = new Object[l][3];
        double[][] real = new double[l][p];
        double[][] pre = new double[l][p];
        Object[][] realObject = new Object[l][2];
        Object[][] preObject = new Object[l][2];
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < p; j++) {
                real[i][j] = results.getResult().get(i).getRealOutput()[j];
                pre[i][j] = results.getResult().get(i).getSimOutput()[j];
                result0[i * p + j][0] = results.getResult().get(i).getResultDate()[j];
                result0[i * p + j][1] = results.getResult().get(i).getRealOutput()[j];
                result0[i * p + j][2] = results.getResult().get(i).getSimOutput()[j];
            }
        }
        if (param.getIsAverage()){
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < p; j++) {
                    realObject[i * p + j][0]= results.getResult().get(i).getResultDate()[j];
                    realObject[i * p + j][1]= results.getResult().get(i).getRealOutput()[j];
                    preObject[i * p + j][0]= results.getResult().get(i).getResultDate()[j];
                    preObject[i * p + j][1]= results.getResult().get(i).getSimOutput()[j];
                }
            }
            realObject = mdu.resultProcessingDay(realObject,param.getLocation());
            preObject = mdu.resultProcessingDay(preObject,param.getLocation());
            for (int i = 0; i < l; i++) {
                for (int j = 0; j < p; j++) {
                    result0[i * p + j][1] = realObject[i * p + j][1];
                    result0[i * p + j][2] = preObject[i * p + j][1];
                    real[i][j] = (double) realObject[i][1];
                    pre[i][j] = (double) preObject[i][1];
                }
            }
        }
        double rmse = MathUtils.RMSE(real,pre);
        double mre = MathUtils.MRE(real,pre);
        double dc = MathUtils.DC(real,pre);
        double qr = MathUtils.QualifyRate(real,pre);
        Object[][] result1 = new Object[result0.length+1][4];
        result1[0][0] = "时间";
        result1[0][1] = "真实";
        result1[0][2] = "预报";
        result1[0][3] = "rmse:"+rmse;
        result1[1][3] = "mre:"+mre;
        result1[2][3] = "dc:"+dc;
        result1[3][3] = "qr:"+qr;
        for (int i = 0; i < result0.length; i++) {
            for (int j = 0; j < 3; j++) {
                result1[i+1][j] = result0[i][j];
            }
        }
        System.out.println("均方差\t平均相对误差\t一致性系数\t合格率");
        System.out.println("----------------------------------");
        System.out.printf("%-10.3f %-10.3f %-10.3f %-10.3f\n", rmse, mre, dc, qr);
        String period = param.getIsSnowMeltModel()?"融雪":"日";
        String location = param.getLocation();
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\" + param.getNetClass() + "-RESULT.xlsx", location+period, result1);
    }
}
