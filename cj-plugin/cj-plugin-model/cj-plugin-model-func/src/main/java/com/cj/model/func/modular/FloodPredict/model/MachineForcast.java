package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.ModelSaveEntity;
import com.cj.model.func.modular.FloodPredict.entity.ParamsSetVO;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.getSpecificDate;

public class MachineForcast {
    boolean isHistory=false;
    public List<Object[][]> Forcast(Object[][] inputTemp, ForcastInputParam param) throws IOException, InvalidFormatException, ParseException {

        List<Object[][]> result = new ArrayList<>();
        ParamsSetVO pvo = pvoSet(inputTemp, param);//设置输入
        String paraPath = param.getXlsx().get(0).getPath();
        String maxminPath = param.getXlsx().get(1).getPath();

        //数值的赋值
        int K = param.vmdK;//分解层数
        int influence_factor= pvo.getInfluence_factor();//影响因子个数
        int outputNumber = inputTemp.length - influence_factor  + 1;

        //VMD分解
        double[] vmdInput = new double[inputTemp.length];
        double[][]vmdOutput=new double[K][inputTemp.length];
        for (int i = 0; i < inputTemp.length; i++) {
            vmdInput[i] = Double.parseDouble(inputTemp[i][1].toString());
        }
        vmdOutput=VMD.vmd(vmdInput,K);

        //输入赋值
        Object[][] de_result =new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()+1][K+1];
        Object[][] preResult = new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()][2];//分解后的预测值
        double[][] vmdreaResult = new double[outputNumber][1];//分解后的实际值
        double[][] reaResult = new double[outputNumber][1];//真实值
        Object[][] peakFlood = new Object[param.getPeriodStepNumber()*param.getPeriodStepSize()][13];
        for (int i = 0; i < outputNumber; i++) {
            reaResult[i][0]=Double.parseDouble(inputTemp[i+influence_factor-1][1].toString());
            for (int j = 0; j < K; j++) {
                vmdreaResult[i][0]=vmdreaResult[i][0]+vmdOutput[j][i+influence_factor-1];
            }
        }

        for (int k = 0; k < K; k++) {
            //读取训练数据时的最大最小值，与输入数据比较，实现与参数吻合的归一化处理
            Object[][] maxminOldTemp = ExcelTool.readExcel(maxminPath, "最大最小值");
            //读取模型参数
            Object[][] paraTemp=ExcelTool.readExcel(paraPath, "模型参数");
            double[][] maxminOld = new double[maxminOldTemp.length][2];
            Object[][] para=new Object[paraTemp.length][4];
            Object[][] input = new Object[inputTemp.length][2];

            for (int i = 0; i < maxminOldTemp.length; i++) {
                for (int j = 0; j < 2; j++) {
                    maxminOld[i][j] = Double.parseDouble(maxminOldTemp[i][2*k+j].toString());
                }
            }
            for (int i = 0; i < para.length; i++) {
                for (int j = 0; j < 4; j++) {
                    para[i][j] = paraTemp[i][4*k+j];
                }
            }
            for (int i = 0; i < inputTemp.length; i++) {
                input[i][0] = inputTemp[i][0];
                input[i][1] = vmdOutput[k][i];
            }

            if(!isHistory){
                //径流预报
                peakFlood = RealTimeForcast(input, pvo, maxminOld, para);
                de_result[0][0]="时间";
                de_result[0][k+1]="预报流量";
                for (int i = 1; i < peakFlood.length + 1; i++) {
                    de_result[i][0]=peakFlood[i-1][3];
                    de_result[i][k+1]=peakFlood[i-1][4];
                }
            }
            else {
                HistoryImitate(pvo,input,maxminOld,paraTemp);
            }
        }
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            preResult[i][1] = 0.0;//初始值
        }
//        System.out.println("\n时间\t预报值");
//        System.out.println("-------------------------");
        for(int i = 0; i < param.getPeriodStepNumber() * param.getPeriodStepSize(); i++){
            for (int j = 0; j < K; j++) {
                preResult[i][0] = de_result[i + 1][0];
                preResult[i][1] = (double) preResult[i][1] + (double) de_result[i + 1][j + 1];
            }
//            System.out.printf("Formatted Date: %s%n %-10.2f", preResult[i][0], preResult[i][1]);
        }
        peakFlood = setFloodXlsx(preResult , pvo);
        result.add(peakFlood);
        return result;
    }

    //历史模拟
    public void HistoryImitate(ParamsSetVO pvo,Object[][] input,double[][] maxminOld,Object[][] paraTemp) throws IOException{
        boolean isRealtime = true;
        boolean isHistory =true;
        LongForecast longForecast = new LongForecast();
        ModelSaveEntity results = longForecast.LongTermForecast(pvo, isRealtime, isHistory, input, maxminOld, paraTemp);
        for (int i = 0; i < results.getResult().size(); i++) {
//            System.out.println("历史模拟" + "                          " + "实测");
//            System.out.println(results.getResult().get(i).getSimOutput() + "      " + results.getResult().get(i).getRealOutput());
        }
    }
    //实时预报
    public  Object[][] RealTimeForcast(Object[][] input, ParamsSetVO pvo,  double[][] maxminOld,  Object[][] paraTemp) throws IOException{
        LongForecast longForecast = new LongForecast();
        boolean isRealtime=true;
        boolean isHistory=false;
        //日期赋值
        Date startDate = pvo.getPreStartTime();
        Date[][] dates;
        //预报期时间、流量赋值
        if (pvo.getForecastPeriod().equals("月")) {dates = TimeUtils.getMonthDateList(startDate,pvo.getPeriodStepNumber(), 1);}
        else if (pvo.getForecastPeriod().equals("旬")) {dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber(), 10, 0, 1);}//中长期的预报步长默认为1
        else if (pvo.getForecastPeriod().equals("日")) {dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber()* pvo.getPeriodStepSize(), 1, 0, 1);}
        else {dates = TimeUtils.getDateList(startDate, pvo.getPeriodStepNumber()* pvo.getPeriodStepSize(), 0, 1, 1);}
        /**
         * 当前只实现了提供往前数据，下一天开始预报的功能；可二次开发实现隔天预报，需要判断dates！
         */
        //预报赋值
        Object[][] predict = new Object[pvo.getPeriodStepSize() * pvo.getPeriodStepNumber() + 1][2];
        predict[0][1] = input[input.length-1][1];
        for (int A = 1; A < pvo.getPeriodStepSize()*pvo.getPeriodStepNumber() + 1; A++) {
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
            ModelSaveEntity pre_results = longForecast.LongTermForecast(pvo, isRealtime, isHistory, pre_input, maxminOld, paraTemp);
            predict[A - 1][1] = pre_results.getResult().get(input.length - pvo.getInfluence_factor() + A).getSimOutput();
            predict[A][1] = input[0][1];
        }

        Object[][] predict1 = new Object[pvo.getPeriodStepSize()*pvo.getPeriodStepNumber()][2];//把前面predict的最后一行去掉
        for (int i = 0; i < pvo.getPeriodStepSize()*pvo.getPeriodStepNumber(); i++) {
            for (int j = 0; j < 2; j++) {
                predict1[i][j]=predict[i][j];
            }
        }
        //洪峰的识别与记录
        Object[][] floodXlsx = setFloodXlsx(predict1 , pvo);
        return floodXlsx;
    }
    //洪峰的确定
    public Object[][] setFloodXlsx(Object[][]predict, ParamsSetVO pvo){
        //表头赋值
        Object[][] peakFloodXlsx=new Object[pvo.getPeriodStepNumber()][13];
        //连续列的赋值
        for (int i = 0; i < pvo.getPeriodStepNumber(); i++) {
            peakFloodXlsx[i][0]=pvo.getForecastDuanmian();//断面位置
            if (pvo.getForecastPeriod().equals("月")){
                peakFloodXlsx[i][1]=2592000;
            } else if (pvo.getForecastPeriod().equals("旬")) {
                peakFloodXlsx[i][1]=864000;
            } else if (pvo.getForecastPeriod().equals("日")) {
                peakFloodXlsx[i][1]=86400;
            } else if (pvo.getForecastPeriod().equals("小时")){
                peakFloodXlsx[i][1]=3600*pvo.getPeriodStepSize();
            }//尺度
            peakFloodXlsx[i][2] = 0;//洪号
            Date testStartDate = (Date) predict[i * pvo.getPeriodStepSize()][0];
            peakFloodXlsx[i][3]=testStartDate;//时间
            peakFloodXlsx[i][4]=Math.round((double) predict[i * pvo.getPeriodStepSize()][1] * 100.0) / 100.0;//预报流量
            double[] waterLevel=getWaterLevel(predict);
            peakFloodXlsx[i][5]=waterLevel[i * pvo.getPeriodStepSize()];//相应水位
        }
        //中长期预报的来水量
        for (int i = 0; i < pvo.getPeriodStepNumber(); i++) {
            if (pvo.getForecastPeriod().equals("日")){
                peakFloodXlsx[i][9]=Math.round(3600*24*pvo.getPeriodStepSize()*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (pvo.getForecastPeriod().equals("旬")){
                int days = getDays(predict, pvo, i);
                peakFloodXlsx[i][9]=Math.round(3600*24*days*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
            }
            if (pvo.getForecastPeriod().equals("月")){
                // 获取该月份的天数
                Date time = (Date) predict[i*pvo.getPeriodStepSize()][0];
                int days = getSpecificDate(time).get("日");
                peakFloodXlsx[i][9]=Math.round(3600*24*days*(double)predict[i*pvo.getPeriodStepSize()][1]/10000 * 100.0) / 100.0;
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

        peakFloodXlsx[0][6] = peakflood;//洪峰
        peakFloodXlsx[0][7] = predict[t][0];//洪峰时间
        peakFloodXlsx[0][12]=judgingYear(peakFloodXlsx,pvo);//洪水等级
        for (int i = 1; i < peakFloodXlsx.length; i++) {
            peakFloodXlsx[i][6] = peakFloodXlsx[0][6];
            peakFloodXlsx[i][7] = peakFloodXlsx[0][7];
            peakFloodXlsx[i][12]=peakFloodXlsx[0][12];
        }
        return peakFloodXlsx;
    }

    /**
     * 判断来水年的类别，丰平枯是根据历史来水量作为评判标准的
     * @param input
     * @param pvo
     * @return
     */
    public static String judgingYear(Object[][] input,ParamsSetVO pvo){
        String result = new String();
        double[] water = new double[input.length];
        for (int i = 0; i < water.length; i++) {
            water[i]= (double) input[i][9];
        }
        if (pvo.getForecastPeriod().equals("月")){
            double waterSum =0.0;
            for (int i = 0; i < water.length; i++) {
                waterSum += water[i];
            }
            waterSum = waterSum/10000;
            if (pvo.getForecastDuanmian().equals("3号桥")||pvo.getForecastDuanmian().equals("楼庄子")){
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
            if (pvo.getForecastDuanmian().equals("楼头区间")){
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
            result = null ;
        }
        return result;
    }
    //判断每一旬的天数
    private int getDays(Object[][] predict, ParamsSetVO pvo, int i) {//存在问题
        Date date1= (Date) predict[i * pvo.getPeriodStepSize()][0];
        LocalDate date = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // 获取年份、月份和天数
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        // 判断旬
        int days;
        if (day <= 10) {
            days = Math.min(day, 10);
        } else if (day <= 20) {
            days = Math.min(day - 10, 10);
        } else {
            int lastDayOfMonth = YearMonth.of(year, month).lengthOfMonth();
            days = Math.min(day - 20, (month == 2 && lastDayOfMonth == 29) ? 11 : 10);
        }
        return days;
    }
    //水位
    public double[] getWaterLevel(Object[][] predict){
        //水位流量关系
        double[] waterLevel=new double[predict.length];
        for (int i = 0; i < predict.length; i++) {
            waterLevel[i]=(double) predict[i][1]*0.1+900;//这里用水位流量曲线
        }
        return waterLevel;
    }
    //设置输入
    public ParamsSetVO pvoSet (Object[][] historyInput, ForcastInputParam param) {
        ParamsSetVO pvo = new ParamsSetVO();
        //输入时段数的确定
        String period=param.getPeriod();
        switch (period) {
            case "月":
            case "旬":
            case "日":
                pvo.setInfluence_factor(4);
                break;
            case "小时":
                pvo.setInfluence_factor(24);
                break;
        }
        int[] inputIndex = new int[pvo.influence_factor];
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
        pvo.setMaxRate(0.02);//这个会影响精度！！!
        String layers = pvo.getInfluence_factor() +",11,11,1";//，输入前几个时段径流，k为输入的因素数量输出未来流量
        pvo.setLayerCount(layers);
        pvo.setTrainNum(5000);
        pvo.setERROR(0.00001);
        pvo.setQ_max(2000);
        pvo.setQ_min(0);
        //输入时间的确定
        Date startDate = (Date) historyInput[pvo.influence_factor-1][0];
        pvo.setDataSetStartTime(startDate);//开始日期
        Date endDate = (Date) historyInput[historyInput.length/4*3-1][0];
        pvo.setDateSetEndTime(endDate);//结束日期
        Date testStartDate = (Date) historyInput[historyInput.length/4*3+pvo.influence_factor-1][0];
        pvo.setTestSetStartTime(testStartDate);//测试集开始时期
        Date testEndDate = (Date) historyInput[historyInput.length-1][0];
        pvo.setTestSetEndTime(testEndDate);//测试集结束时期
        pvo.setPreStartTime(param.getPreStartTime());//预报时间
        //预报时段的确定
        pvo.setPeriodStepNumber(param.getPeriodStepNumber());//预报时段数
        pvo.setPeriodStepSize(param.getPeriodStepSize());//预报时段步长
        pvo.setIsSnowMeltModel(false);
        return pvo;
    }
}

