package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.model.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import sun.misc.VM;

import java.io.IOException;

import java.util.Date;




public class MachineForecast {
    boolean isHistory=false;

    DataUtils dataUtils = new DataUtils();

    TimeUtils timeUtils = new TimeUtils();
    /**
     * 数据驱动模型预报主函数
     * @param inputTemp
     * @param param
     * @return
     * @throws IOException
     */
    public Object[][] machineForecast(Object[][] inputTemp, ForecastInputParam param) throws IOException {
        MachineModel machineModel = new MachineModel();
        machineModel.paramSet(inputTemp, param);//设置输入
        param.setIsRealtime(true);
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();

        //数值的赋值
        int K = param.vmdK;//分解层数
        int history_day= param.getHistory_day();//影响因子个数
        int outputNumber = inputTemp.length - history_day  + 1;

        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][]vmdOutput;

        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        VMD vmd = new VMD();
        vmdOutput=vmd.vmd(vmdInput,K);

        //输入赋值
        Object[][] de_result =new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()+1][K+1];
        Object[][] preResult = new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()][2];//分解后的预测值
        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] peakFlood;
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i+history_day-1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0]=vmdreaResult[i][0]+vmdOutput[j][i+history_day-1];
            }
        }

        for (int k = 0; k < K; k++) {
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
            //读取模型参数
            Object[][] paraTemp=ExcelTool.readExcel(paraPath, "模型参数");
            Object[][] input = new Object[inputTemp.length][2];

            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];
                input[i][1] = vmdOutput[k][i];
            }

            if(!isHistory)
            {
                //径流预报
                peakFlood = realTimeForecast(param, input, maxminOldTemp, paraTemp);
                de_result[0][0]="时间";
                de_result[0][k+1]="预报流量";
                for (int i = 1; i < peakFlood.length+1; i++) {
                    de_result[i][0]=peakFlood[i-1][0];
                    de_result[i][k+1]=peakFlood[i-1][1];
                }
            }
            else
            {
                historyImitate(param,input,maxminOldTemp,paraTemp);
            }

        }
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            preResult[i][1] = 0.0;//初始值
        }
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            for (int j = 0; j < K; j++) {
                preResult[i][0] = de_result[i + 1][0];
                preResult[i][1] = (double) preResult[i][1] + (double) de_result[i + 1][j + 1];
            }
        }
        dataUtils.resultProcessing(preResult , param);//恢复为径流量
        peakFlood = setLongFloodxlsx(preResult , param);
        return peakFlood;
    }

    /**
     * 历史径流的模拟
     * @param input
     * @param maxminOld
     * @param paraTemp
     */
    public void historyImitate(ForecastInputParam param, Object[][] input, Object[][] maxminOld, Object[][] paraTemp) {
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.longTermForecast(param,  input, maxminOld, paraTemp);
        for (int i = 0; i < results.getResult().size(); i++) {
            System.out.println("历史模拟" + "                          " + "实测");
            System.out.println(results.getResult().get(i).getSimOutput() + "      " + results.getResult().get(i).getRealOutput());
        }
    }

    /**
     * 实时预报代码
     * @param input
     * @param param
     * @param maxminOld
     * @param paraTemp
     * @return
     */
    public Object[][] realTimeForecast(ForecastInputParam param, Object[][] input, Object[][] maxminOld, Object[][] paraTemp) {
        LongForecast longForecast = new LongForecast();
        boolean isRealtime=true;
        boolean isHistory=false;
        //日期赋值
        Date startDate = param.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        switch (param.getPeriod()) {
            case "月":
                dates = timeUtils.getMonthDateList(startDate, param.getPeriodStepNumber());
//                dates = TimeUtils.getSelectMonthDateList(startDate, param.getPeriodStepNumber());
                break;
            case "旬":
                dates = timeUtils.getDateList(startDate, param.getPeriodStepNumber(), 10, 0);
                break;
            case "日":
                dates = timeUtils.getDateList(startDate, param.getPeriodStepNumber() * param.getPeriodStepSize(), 1, 0);
                break;
            default:
                dates = timeUtils.getDateList(startDate, param.getPeriodStepNumber() * param.getPeriodStepSize(), 0, 1);
                break;
        }
        /**
         * 当前只实现了提供往前数据，下一天开始预报的功能；可二次开发实现隔天预报，需要判断dates！
         */
        //预报赋值
        Object[][] predict = new Object[param.getPeriodStepSize() * param.getPeriodStepNumber() + 1][2];
        predict[0][1] = input[input.length-1][1];
        for (int A = 1; A < param.getPeriodStepSize()*param.getPeriodStepNumber() + 1; A++) {
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
            ModelSaveEntity pre_results = longForecast.longTermForecast(param,  pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - param.getHistory_day() + A).getSimOutput();
            predict[A][1] = input[0][1];
        }

        Object[][] result = new Object[param.getPeriodStepSize()*param.getPeriodStepNumber()][2];//把前面predict的最后一行去掉
        for (int i = 0; i < param.getPeriodStepSize()*param.getPeriodStepNumber(); i++) {
            for (int j = 0; j < 2; j++) {
                result[i][j]=predict[i][j];
            }
        }
        return result;
    }

    /**
     * 中长期返回表格
     * @param predict
     * @param param
     * @return
     */
    public Object[][] setLongFloodxlsx(Object[][]predict, ForecastInputParam param){
        //表头赋值
        Object[][] longFlood=new Object[param.getPeriodStepNumber()][15];
        //连续列的赋值
        for (int i = 0; i < param.getPeriodStepNumber(); i++) {
            longFlood[i][0]=param.getLocation();//断面位置
            if (param.getPeriod().equals("月")){
                longFlood[i][1]=2592000;
            }
            else if (param.getPeriod().equals("旬")) {
                longFlood[i][1]=864000;
            }
            else if (param.getPeriod().equals("日")) {
                longFlood[i][1]=86400;
            }
            else if (param.getPeriod().equals("小时")){
                longFlood[i][1]=3600*param.getPeriodStepSize();
            }//尺度
            longFlood[i][2]=0;//洪号
            Date testStartDate = (Date) predict[i * param.getPeriodStepSize()][0];
            longFlood[i][3]=testStartDate;//时间
            longFlood[i][4]=Math.round((double) predict[i * param.getPeriodStepSize()][1] * 100.0) / 100.0;//预报流量
            longFlood[i][5]=0.0;//水位
        }
        //中长期预报的来水量
        for (int i = 0; i < param.getPeriodStepNumber(); i++) {
            if (param.getPeriod().equals("日")){
                longFlood[i][9]=Math.round(3600*24*param.getPeriodStepSize()*(double)predict[i*param.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (param.getPeriod().equals("旬")){
                int days = timeUtils.getDays(predict, param, i);
                longFlood[i][9]=Math.round(3600*24*days*(double)predict[i*param.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (param.getPeriod().equals("月")){
                // 获取该月份的天数
                Date time = (Date) predict[i*param.getPeriodStepSize()][0];
                int days = 30;
                longFlood[i][9]=Math.round(3600*24*days*(double)predict[i*param.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
        }
        double peakflood = 0;
        int t = 0;
        for (int i = 0; i < predict.length; i++) {
            if (peakflood <= (double) predict[i][1]) {
                peakflood = (double) predict[i][1];//洪峰
                t = i ;
            }
        }
        longFlood[0][6] = peakflood;//洪峰
        longFlood[0][7] = predict[t][0];//洪峰时间
        longFlood[0][12]= judgingYearLeve(longFlood,param);//洪水等级
        for (int i = 1; i < longFlood.length; i++) {
            longFlood[i][6] = longFlood[0][6];
            longFlood[i][7] = longFlood[0][7];
            longFlood[i][12]= longFlood[0][12];
        }
        //出库流量
        for (int i = 0; i < longFlood.length; i++) {
            longFlood[i][13]=longFlood[i][4];//出库流量
            longFlood[i][14]=0;//超过汛限水位
        }
        return longFlood;
    }

    /**
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     * @param input
     * @param param
     * @return
     */
    public String judgingYearLeve(Object[][] input, ForecastInputParam param){
        String result = "";
        double[] water = new double[input.length];
        for (int i = 0; i < water.length; i++) {
            water[i]= (double) input[i][9];
        }
        if (param.getPeriod().equals("月")){
            double waterSum =0.0;
            for (double v : water) {
                waterSum += v;
            }
            waterSum = waterSum/10000;
            if (param.getLocation().equals("3号桥")||param.getLocation().equals("楼庄子")){
                if (waterSum >=2.476){
                    result = "丰水年";
                }
                if (waterSum<2.246&&waterSum>=1.998){
                    result = "平水年";
                }
                if (waterSum<1.998){
                    result = "枯水年";
                }
            }
            if (param.getLocation().equals("楼头区间")){
                if (waterSum >=0.1443){
                    result = "丰水年";
                }
                if (waterSum<0.1443&&waterSum>=0.1164){
                    result = "平水年";
                }
                if (waterSum<0.1164){
                    result = "枯水年";
                }
            }
        }else {
            result = "平水年" ;
        }
        return result;
    }

}

