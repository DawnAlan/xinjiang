package com.cj.model.func.modular.FloodPredict.model;



import com.cj.model.func.modular.FloodPredict.entity.ForcastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfLZZ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cj.model.func.modular.FloodPredict.utils.DataUtils.*;


public class PhysicalForcast {
    String floodLevel="一年一遇";
    public Object[][] getphysicalresult(ForcastInputParam param, List<List<PredictInputData>> Data, Object[][] snowData)
            throws IOException {
        String path = "D:\\tth_system\\end\\file\\陕北-PARAM.xlsx";
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        String sheetPara=param.getLocation()+"参数";
        //陕北模型输入、蒸散发和前期雨量
        List<PredictInputData> PreFlow = Data.get(0);
        List<PredictInputData> PointPreREDataList=Data.get(1);
        List<PredictInputData> PointHistoryRDataList=Data.get(2);
        List<PredictInputData> preREDataList = pointToSurface(PointPreREDataList);
        List<PredictInputData> historyRDataList = pointToSurface(PointHistoryRDataList);
        Object[][] preREData = new Object[preREDataList.size()][3];
        for (int i = 0; i < preREDataList.size(); i++) {
            preREData[i][0]=preREDataList.get(i).getDates();
            preREData[i][1]=preREDataList.get(i).getTemperature();
            preREData[i][2]=preREDataList.get(i).getRainfall();
        }
        preREData=temToEva(preREData);//将温度转为蒸发量
        Object[][] historyRData = new Object[historyRDataList.size()][2];
        for (int i = 0; i < historyRDataList.size(); i++) {
            historyRData[i][0]=historyRDataList.get(i).getDates();
            historyRData[i][1]=historyRDataList.get(i).getRainfall();
        }
        shanBeiModel.InputData(path,sheetPara,preREData,historyRData);
        shanBeiModel.InitialMoistureContentCalculation();
        shanBeiModel.RunoffYieldCalculation_UnevenInfiltration();
        shanBeiModel.ConfluenceCalculation();

        //获得径流序列包含了降水融雪地下水.是前48小时的后续可以改
        Object[][]shortFlow = mixedFlood(param, shanBeiModel.Q, shanBeiModel.L,Data,snowData);
        //将Object转化为Flood类型
        Object[][] peakFlood=setPeakFlood(shortFlow,param);
        peakFlood[0][10]=floodSources(PointPreREDataList,param);//洪水来源
        peakFlood[0][11]=floodComposition(param,PreFlow,shanBeiModel.Q,snowData);//洪水组成
        if (param.getLocation().equals("楼庄子")){
            peakFlood[0][12]=floodLevel(shortFlow,"楼庄子");//洪水等级
            floodLevel = floodLevel(shortFlow,"楼庄子");
        }else {
            peakFlood[0][12]=floodLevel;
        }
        for (int i = 1; i < peakFlood.length; i++) {
            peakFlood[i][10]=peakFlood[0][10];
            peakFlood[i][11]=peakFlood[0][11];
            peakFlood[i][12]=peakFlood[0][12];

        }
        return peakFlood;
    }


    /**
     * 把预报洪水过程写成规范表格形式
     * @param predict
     * @param param
     * @return
     */
    public Object[][] setPeakFlood(Object[][]predict, ForcastInputParam param){
        Object[][] peakFloodXlsx=new Object[param.getPeriodStepNumber()][14];
        List<Object[][]> floodInformation = selectPeakFlood(predict);
        Object[][] floodIndex = floodInformation.get(0);
        //连续列的赋值
        for (int i = 0; i < param.getPeriodStepNumber(); i++) {
            peakFloodXlsx[i][0]=param.getLocation();//断面位置
            int timeScale=3600 * param.getPeriodStepSize();
            peakFloodXlsx[i][1]=Integer.toString(timeScale);//尺度
            peakFloodXlsx[i][2]=floodIndex[i* param.getPeriodStepSize()][0];//洪号
            peakFloodXlsx[i][3]=predict[i * param.getPeriodStepSize()][0];//时间
            peakFloodXlsx[i][4]=Math.round((double) predict[i * param.getPeriodStepSize()][1] * 100.0) / 100.0;//预报流量
            double[] waterLevel=getWaterLevel(predict,param);
            peakFloodXlsx[i][5]=waterLevel[i * param.getPeriodStepSize()];//相应水位
            Object[][] floodNature = floodInformation.get(1);
            peakFloodXlsx[i][6]=floodNature[2][1];//洪峰
            peakFloodXlsx[i][7]=floodNature[3][1];//峰现时间
            peakFloodXlsx[i][8]=floodNature[1][1];//洪峰持续时间
            peakFloodXlsx[i][9]=floodNature[0][1];//洪量

        }
        if (param.getLocation().equals("楼庄子")){
            Object[][] input = new Object[peakFloodXlsx.length][3];
            for (int i = 0; i < peakFloodXlsx.length; i++) {
                input[i][0] = peakFloodXlsx[i][0];
                input[i][1] = peakFloodXlsx[i][3];
                input[i][2] = peakFloodXlsx[i][4];
            }
            int timeLength =Integer.parseInt((String) peakFloodXlsx[1][1]);
            ModelOfLZZ lzzOut = new ModelOfLZZ(input,timeLength);
            List<Option> lzzOutList = lzzOut.Calculate_S1();
            //连续列的赋值
            for (int i = 0; i < peakFloodXlsx.length; i++) {
                peakFloodXlsx[i][5] = lzzOutList.get(i).getH1();
                peakFloodXlsx[i][13]=lzzOutList.get(i).getQOut();//出库流量
            }
        }else {
            for (int i = 0; i < peakFloodXlsx.length; i++) {
                peakFloodXlsx[i][13]=peakFloodXlsx[i][4];//出库流量
            }
        }
        return peakFloodXlsx;
        }

    /**
     *
     * @param predict 预报洪水过程
     * @return 记录好的洪号，洪峰，峰现时间，持续时间，洪量
     */
    public static List<Object[][]> selectPeakFlood(Object[][] predict){
        List<Object[][]> result = new ArrayList<>();
        Object[][] flood = new Object[predict.length][3];
        double max =0.0;
        double min =1000000.0;
        for (int i = 0; i < predict.length; i++) {
            if (max <= (double) predict[i][1]) {
                max = (double) predict[i][1];//洪峰
            }
            if (min >= (double) predict[i][1]) {
                min = (double) predict[i][1];//最小值
            }
        }
        double dt = max-min;//差值
        double line = min+dt*0.4;//洪水标准线
        for (int i = 0; i < predict.length; i++)//找到所有大于标准线的来水
        {
            if ((double) predict[i][1]>line){
                flood[i][0]=1;
                flood[i][1]=predict[i][0];//时间
                flood[i][2]=predict[i][1];//预报流量
            }else {
                flood[i][0]=0;
                flood[i][1]=predict[i][0];//时间
                flood[i][2]=predict[i][1];//预报流量
            }
        }
        int m = 0;//洪峰的数量
        List<Integer> loc = new ArrayList<>();//记录变化的位置
        for (int i = 0; i < predict.length-1; i++) {
            if (flood[i][0]!=flood[i+1][0]){
                m++;
            }
            if (flood[i][0]!=flood[i+1][0]){
                loc.add(i);
            }
        }
        int remainder = m % 2;
        m = m/2+remainder;//洪峰数量
        if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]==1)//开始是洪水并且结束是洪水
        {
            m=m+1;
        }
        for (int i = 0; i < predict.length; i++) {
            if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]!=1)//开始为洪水，结束不为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0)+1; k++) {
                    flood[k][0]=number;
                }//第一个洪峰赋值
                for (int j = 1; j < m; j++) {
                    number++;
                    for (int k = loc.get(2*j-1); k <= loc.get(2*j)+1; k++) {
                        flood[k][0]=number;
                    }
                }
                break;
            }
            if ((int)flood[0][0]!=1&&(int)flood[flood.length-1][0]==1)//开始不为洪水，结束为洪水
            {
                int number = 1;
                for (int j = 0; j < m-1; j++) {
                    for (int k = loc.get(2*j); k <= loc.get(2*j+1)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                for (int k = loc.get(2*m-2); k < flood.length; k++) {
                    flood[k][0]=number;
                }
                break;
            }
            if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]==1)//开始为洪水，结束为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0)+1; k++) {
                    flood[k][0]=number;
                }
                number++;
                for (int j = 0; j < m-2; j++) {
                    for (int k = loc.get(2*j+1); k <= loc.get(2*j+2)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                for (int k = loc.get(2*m-3); k < flood.length; k++) {
                    flood[k][0]=number;
                }
                break;
            }
            if ((int)flood[0][0]!=1&&(int)flood[flood.length-1][0]!=1)//开始不为洪水，结束不为洪水
            {
                int number = 1;
                for (int j = 0; j < m; j++) {
                    for (int k = loc.get(2*j); k <= loc.get(2*j+1)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                break;
            }
        }
        /**
         * 以下为针对分好洪号后的洪水过程
         */
        Object[][] floodNature = new Object[4][2];
        floodNature[0][0]="洪量";//万立方米
        floodNature[1][0]="洪峰持续时间";
        floodNature[2][0]="洪峰";
        floodNature[3][0]="峰现时间";
        double Volume =0.0;
        String duration =new String();
        String floodLevel = new String();
        double floodSum = 0.0;
        int Number=0;//第几个洪水
        //判断第几个来水洪量最大
        for (int i = 1; i <= m; i++) {
            double sum =0.0;
            for (int j = 0; j < flood.length; j++) {
                if ((int)flood[j][0]==i){
                    sum+=(double) flood[j][2];
                }
            }
            if (sum>floodSum){
                floodSum=sum;
                Number=i;
            }
        }
        List<Double> maxFlood= new ArrayList<>();
        for (Object[] objects : flood) {
            if ((int) objects[0] == Number) {
                maxFlood.add((double) objects[2]);
            }
        }
        int beforeMin = 0;
        int afterMin = 0;
        double dVolume = 0.0;
        double dMin = 0.0;
        int hour = 0;
        //洪量
        for (int i = 0; i < maxFlood.size(); i++) {
            Volume += maxFlood.get(i)*3600/10000;//多少万立方米
        }
        Volume=Math.round(Volume * 100.0) / 100.0;
        floodNature[0][1]=Volume;
        //持续时间
        if (maxFlood.get(0)>line)//开始为洪水
        {
            dVolume = maxFlood.get(maxFlood.size()-2)-maxFlood.get(maxFlood.size()-1);
            dMin = maxFlood.get(maxFlood.size()-2)-line;
            afterMin = (int)(dMin/dVolume*60);
            hour = maxFlood.size()-1;
            duration = hour+"h"+afterMin+"min";
            floodNature[1][1]=duration;
        }else if (maxFlood.get(maxFlood.size()-1)>line)//结束为洪水
        {
            dVolume = maxFlood.get(1)-maxFlood.get(0);
            dMin = maxFlood.get(1)-line;
            beforeMin = (int)(dMin/dVolume*60);
            hour = maxFlood.size()-1;
            duration = hour+"h"+beforeMin+"min";
            floodNature[1][1]=duration;
        }else {
            dVolume = maxFlood.get(1)-maxFlood.get(0);
            dMin = maxFlood.get(1)-line;
            beforeMin = (int)(dMin/dVolume*60);
            dVolume = maxFlood.get(maxFlood.size()-2)-maxFlood.get(maxFlood.size()-1);
            dMin = maxFlood.get(maxFlood.size()-2)-line;
            afterMin = (int)(dMin/dVolume*60);
            hour = maxFlood.size()-2;
            if (beforeMin+afterMin>60){
                hour = hour+1;
                duration = hour+"h"+(beforeMin+afterMin-60)+"min";
            }else {
                duration = hour+"h"+(beforeMin+afterMin)+"min";
            }
            floodNature[1][1]=duration;
        }
        //洪峰
        double maxQ = 0.0;
        int t =0;
        for (int i = 0; i < maxFlood.size(); i++) {
            if (maxQ<maxFlood.get(i)){
                maxQ=maxFlood.get(i);
                t++;
            }
        }
        floodNature[2][1]=maxQ;
        //峰现时间
        int n=0;
        for (int i = 0; i < flood.length; i++) {
            if ((int)flood[i][0]==Number){
                n = i;
                break;
            }
        }
        int temp = n+t-1;
        if (temp>0){
            floodNature[3][1]=flood[temp][1];
        }else {
            floodNature[3][1]=flood[0][1];
        }
        result.add(flood);
        result.add(floodNature);
        return result;
    }

    /**
     * 获得洪水等级
     * @param input
     * @return
     */
    public static String floodLevel(Object[][] input,String location){
        String result = "一年一遇";
        Object[][] floodNature = selectPeakFlood(input).get(1);
        double maxQ = (double) floodNature[2][1];
        Object[][] flood = selectPeakFlood(input).get(0);
        double volume=0.0;
        double minVolume =0.0;
        //获得一日洪量
        if (flood.length<24){//获得一日洪量
            for (int i = 0; i < flood.length; i++) {
                volume +=(double)flood[i][2];
            }
            volume =volume/flood.length*24*3600/1000000;//10^6立方米
        }
        else {
            for (int i = 0; i < flood.length-24; i++) {
                for (int j = 0; j < 24; j++) {
                    minVolume +=(double)flood[i+j][2];
                }
                minVolume=minVolume*3600/1000000;
                if (minVolume>volume){
                    volume=minVolume;
                }
            }
        }
        //根据位置返回洪水等级
        if (location.equals("楼庄子")){
            if (maxQ>=944||volume>=37.7){
                result="万年一遇";
            }
            else if (maxQ>=750||volume>=30.0){
                result="二千年一遇";
            }
            else if (maxQ>=668||volume>=26.7){
                result="千年一遇";
            }
            else if (maxQ>=587||volume>=23.5){
                result="五百年一遇";
            }
            else if (maxQ>=530||volume>=21.2){
                result="三百年一遇";
            }
            else if (maxQ>=482||volume>=19.3){
                result="二百年一遇";
            }
            else if (maxQ>=405||volume>=16.2){
                result="百年一遇";
            }
            else if (maxQ>=330||volume>=13.3){
                result="五十年一遇";
            }
            else if (maxQ>=277||volume>=11.2){
                result="三十年一遇";
            }
            else if (maxQ>=236||volume>=9.7){
                result="二十年一遇";
            }
            else if (maxQ>=171||volume>=7.2) {
                result="十年一遇";
            }
            else if (maxQ>=114||volume>=5.1) {
                result="五年一遇";
            }
            else  {
                result="一年一遇";
            }
        }
        else if (location.equals("头屯河")) {
            if (maxQ>=1013||volume>=28){
                result="千年一遇";
            }
            else if (maxQ>=883||volume>=25.5){
                result="五百年一遇";
            }
            else if (maxQ>=713||volume>=22.2){
                result="两百年一遇";
            }
            else if (maxQ>=590||volume>=17.8){
                result="百年一遇";
            }
            else if (maxQ>=470||volume>=17.4){
                result="五十年一遇";
            }
            else if (maxQ>=402||volume>=15.9){
                result="三十年一遇";
            }
            else if (maxQ>=320||volume>=14.1){
                result="二十年一遇";
            }
            else if (maxQ>=219||volume>=11.7) {
                result="十年一遇";
            }
            else  {
                result="一年一遇";
            }
        }
        return result;
    }

    /**
     *
     * @param predict 预报流量
     * @return 相应水位
     */
    public  static double[] getWaterLevel(Object[][] predict,ForcastInputParam param){
        //水位流量关系
        double[] waterLevel=new double[predict.length];
        if (param.getLocation().equals("楼头区间")){
            for (int i = 0; i < predict.length; i++) {
                waterLevel[i]=(double) predict[i][1]*0.01+1000;//这里用水位流量曲线
            }
        }else {
            for (int i = 0; i < predict.length; i++) {
                waterLevel[i]=(double) predict[i][1]*0.01+1394.5;//这里用水位流量曲线
            }
        }

        return waterLevel;
    }

    /**
     * 后续更改（面雨量权重）
     * 求洪水组成，各个雨量站代表的汇流面贡献多少水量
     * @param pointData
     * @return
     */
    public String floodSources (List<PredictInputData> pointData,ForcastInputParam param){
        String result=new String();
        String stationName = pointData.get(0).getRainStation();
        int number = 0 ;
        for (int i = 0; i < pointData.size(); i++) {
            if (pointData.get(i).getRainStation().equals(stationName)){
                number++;
            }
        }
        PredictInputData hourData ;
        List<PredictInputData> hourDatalist = new ArrayList<>();
        List<List<PredictInputData>> hourDataList = new ArrayList<>();
        for (int j = 0; j < number; j++) {
            hourDatalist = new ArrayList<>();
            for (int i = 0; i < pointData.size(); i++) {
                Boolean dateCompare =DateCompare(pointData.get(j).getDates(),pointData.get(i).getDates(),"小时");
                if (dateCompare){
                    hourData=pointData.get(i);
                    hourDatalist.add(hourData);
                }
            }
            hourDataList.add(hourDatalist);
        }
        //number代表几个时段
        Object[][] rainSum = new Object[13][2];
        for (int i = 0; i < 13; i++) {
            rainSum[i][1]=0.0;
        }
        for (int i = 0; i < number; i++) {
            PredictInputData hourResult=new PredictInputData();
            hourDatalist=hourDataList.get(i);
            Object[][] rainFall =new Object[13][2];//13个雨量站的雨量
            for (int j = 0; j < 13; j++) {
                rainFall[j][1]=0.0;
            }
            hourResult.setDates(hourDatalist.get(0).getDates());
            //hourDatalist为同一时间段不同雨量站
            for (int j = 0; j < hourDataList.get(i).size(); j++) {
                if (hourDatalist.get(j).getRainStation().equals("八一林场自动雨量站")){
                    rainFall[0][0] = hourDatalist.get(j).getRainStation();
                    rainFall[0][1] = (double)rainFall[0][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("加普沙自动雨量站")){
                    rainFall[1][0] = hourDatalist.get(j).getRainStation();
                    rainFall[1][1] = (double)rainFall[1][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("东南沟自动雨量站")){
                    rainFall[2][0] = hourDatalist.get(j).getRainStation();
                    rainFall[2][1] = (double)rainFall[2][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("宰尔德自动雨量站")){
                    rainFall[3][0] = hourDatalist.get(j).getRainStation();
                    rainFall[3][1] = (double)rainFall[3][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("无名沟自动雨量站")){
                    rainFall[4][0] = hourDatalist.get(j).getRainStation();
                    rainFall[4][1] = (double)rainFall[4][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("萨尔达万自动雨量站")){
                    rainFall[5][0] = hourDatalist.get(j).getRainStation();
                    rainFall[5][1] = (double)rainFall[5][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("煤矿沟自动雨量站")){
                    rainFall[6][0] = hourDatalist.get(j).getRainStation();
                    rainFall[6][1] = (double)rainFall[6][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("黑沟自动雨量站")){
                    rainFall[7][0] = hourDatalist.get(j).getRainStation();
                    rainFall[7][1] = (double)rainFall[7][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("喀什沟自动雨量站")){
                    rainFall[8][0] = hourDatalist.get(j).getRainStation();
                    rainFall[8][1] = (double)rainFall[8][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("制材厂自动雨量站")){
                    rainFall[9][0] = hourDatalist.get(j).getRainStation();
                    rainFall[9][1] = (double)rainFall[9][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("小渠子雨量站")){
                    rainFall[10][0] = hourDatalist.get(j).getRainStation();
                    rainFall[10][1] = (double)rainFall[10][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("团结一队雨量站")){
                    rainFall[11][0] = hourDatalist.get(j).getRainStation();
                    rainFall[11][1] = (double)rainFall[11][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
                if (hourDatalist.get(j).getRainStation().equals("头屯河水库雨量站")){
                    rainFall[12][0] = hourDatalist.get(j).getRainStation();
                    rainFall[12][1] = (double)rainFall[12][1]+hourDatalist.get(j).getRainfall()*0.1;
                }
            }
            //各个时间段雨量站降雨总和
            for (int j = 0; j < 13; j++) {
                if (rainSum[j][0]==null){
                    rainSum[j][0]=rainFall[j][0];
                }
                rainSum[j][1]=(double)rainSum[j][1]+(double) rainFall[j][1];
            }
        }
        //三号桥断面返回三个地区的雨量比值
        if (param.getLocation().equals("3号桥")){
            double Sum =0.0;
            double qiaoSum=0.0;
            double dongSum=0.0;
            double sanSum=0.0;
            qiaoSum = (double)rainSum[0][1];
            dongSum = (double)rainSum[1][1]+(double)rainSum[2][1]+(double)rainSum[3][1]+(double)rainSum[4][1];
            sanSum = (double)rainSum[5][1]+(double)rainSum[6][1];
            Sum = qiaoSum+dongSum+sanSum;
            if (Sum!=0.0){
                double qiao =Math.round((float) qiaoSum/Sum*100)/100.0;
                double dong =Math.round((float) dongSum/Sum*100)/100.0;
                double san =Math.round((1.00-qiao-dong)*100)/100.0;
                result = "乔楞格尔地区:"+qiao+","+"东南沟地区:"+dong+","+"3号桥地区:"+san;
            } else {
                result = "乔楞格尔地区:0.34,"+"东南沟地区:0.33,"+"3号桥地区:0.33";
            }
        } else if (param.getLocation().equals("楼庄子")) {
            double Sum =0.0;
            double qiaoSum=0.0;
            double dongSum=0.0;
            double sanSum=0.0;
            double zhiSum =0.0;
            qiaoSum = (double)rainSum[0][1];
            dongSum = (double)rainSum[1][1]+(double)rainSum[2][1]+(double)rainSum[3][1]+(double)rainSum[4][1];
            sanSum = (double)rainSum[5][1]+(double)rainSum[6][1];
            zhiSum = (double)rainSum[7][1]+(double)rainSum[8][1]+(double)rainSum[9][1];
            Sum = qiaoSum+dongSum+sanSum+zhiSum;
            if (Sum!=0){
                double qiao =Math.round((float) qiaoSum/Sum*100)/100.0;
                double dong =Math.round((float) dongSum/Sum*100)/100.0;
                double san =Math.round((float) sanSum/Sum*100)/100.0;
                double zhi =Math.round((1.00-qiao-dong-san)*100)/100.0;
                result = "乔楞格尔地区:"+qiao+","+"东南沟地区:"+dong+","+"3号桥地区:"+san+","+"制材厂地区:"+zhi;
            }else {
                result = "乔楞格尔地区:0.25,"+"东南沟地区:0.25,"+"3号桥地区:0.25,"+"制材厂地区:0.25";
            }

        }else if(param.getLocation().equals("楼头区间")){
            double Sum =0.0;
            double xiaoSum=(double)rainSum[10][1];
            double tuanSum=(double)rainSum[11][1];
            double toSum=(double)rainSum[12][1];
            Sum = xiaoSum+tuanSum+toSum;
            if (Sum!=0){
                double xiao =Math.round((float) xiaoSum/Sum*100)/100.0;
                double tuan =Math.round((float) tuanSum/Sum*100)/100.0;
                double to =Math.round((1.00-xiao-tuan)*100)/100.0;
                result = "小渠子沟:"+xiao+","+"团结一队:"+tuan+","+"头屯河入库:"+to;
            }else {
                result = "小渠子沟:0.34,"+"团结一队:0.33,"+"头屯河入库:0.33";
            }
        }
        return result;
    }

    /**
     * 求洪水来源
     * @param param
     * @param PreFlow 基础流量
     * @param Q_shanbei 降雨产生
     * @param snowData 融雪产生
     * @return
     */
    public String floodComposition (ForcastInputParam param,List<PredictInputData> PreFlow,
                                    double[]Q_shanbei,Object[][]snowData){
        String result = new String();
        double snowFlow = 0.0;
        double preFlowSum = 0.0;
        int preFlowNum = 0;
        double preFlow = 0.0;
        double shanbeiFlow = 0.0;

        if (param.getIsSnowMeltModel()){
            int number = Q_shanbei.length;
            for (int i = 0; i < snowData.length; i++) {
                snowFlow += (double) snowData[i][1];
            }
            snowFlow = snowFlow/snowData.length/24*number;
            for (int i = 0; i < Q_shanbei.length; i++) {
                shanbeiFlow =shanbeiFlow +  Q_shanbei[i];
            }
            double Sum = snowFlow+preFlow+shanbeiFlow;
            double shanbei =Math.round((float) shanbeiFlow/Sum*95)/100.0;
            double rong = Math.round((float) snowFlow/Sum*95)/100.0;
            result += "降水:"+ shanbei+","+"融雪:"+rong+","+"地下水:0.05";
        }
        else {
            for (int i = 0; i < Q_shanbei.length; i++) {
                shanbeiFlow =shanbeiFlow +  Q_shanbei[i];
            }
            for (int i = 0; i < PreFlow.size(); i++) {
                if (PreFlow.get(i).getFlow().isNaN()){
                    PreFlow.get(i).setFlow(0.0);
                }
                Date time = PreFlow.get(i).getDates();
                int year = getSpecificDate(time).get("年");
                Date time2 = param.getPreStartTime();
                int year2 = getSpecificDate(time2).get("年");
                if (year==year2){
                    Date time3 = PreFlow.get(i).getDates();
                    int month = getSpecificDate(time3).get("月");
                    if (month>=1&&month<=5){
                        preFlowSum = preFlowSum + PreFlow.get(i).getFlow();
                        preFlowNum++;
                    }
                    preFlow = preFlowSum/preFlowNum*Q_shanbei.length;
                }
            }
            double Sum = preFlow+shanbeiFlow;
            double shanbei =Math.round((float) shanbeiFlow/Sum*95)/100.0;
            double rong = Math.round((float) preFlow/Sum*95)/100.0;
            result += "降水:"+ shanbei+","+"融雪:"+rong+","+"地下水:0.05";
        }
        return result;
    }

    /**
     * 温度转化为蒸发量
     * @param data
     * @return
     */
    public static Object[][] temToEva(Object[][] data){
        Object[][] preData =new Object[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            preData[i][0]=data[i][0];
            preData[i][1]=0.0036;//温度转为蒸发量
            preData[i][2]=data[i][2];
        }
        return preData;
    }

    /**
     * 陕北模型计算所得降水数据与前期径流数据、融雪数据整合
     * （后续更改）预报后所得时间
     * @param param
     * @param shanBeiQ 降水所得
     * @param Data data.get0为前期径流，data.get1为前10小时和预报雨量。
     * @param snowFlow 融雪径流
     * @return 预报的径流值
     */
    public static Object[][] mixedFlood (ForcastInputParam param,double[] shanBeiQ, int L,
                                         List<List<PredictInputData>> Data, Object[][] snowFlow){
        List<PredictInputData> preFlow = Data.get(0);
        //留前10小时作为落地雨
        int before = 10;
        Object[][] result= new Object[shanBeiQ.length-before][2];
        //减去汇流滞时
        Date currentDate = param.getPreStartTime();
        Date[][] dates = TimeUtils.getDateList(currentDate, shanBeiQ.length-before, 0, 1);
       //shanBeiq为实际预报的值
        double[] shanBeiq = new double[shanBeiQ.length-before];
        for (int i = L; i < shanBeiQ.length-before+L; i++) {
            shanBeiq[i-L]=shanBeiQ[i];
        }
        //基础流量
        Double baseAve = 0.0;
        for (int j = 0; j < 10; j++) {
            Double baseFlow = 0.0;
            int n = preFlow.size() -1- j;
            if (preFlow.get(n).getFlow() != null){
                baseFlow = preFlow.get(n).getFlow();
            }else {
                n--;
            }
            baseAve += baseFlow;
        }
        baseAve = baseAve / 10;
        //融雪基流
        Double snowAverage = 0.0;
        Double snowSum = 0.0;
        if (param.getIsSnowMeltModel()) {
            for (int j = 0; j < snowFlow.length; j++) {
                snowSum = snowSum + (double) snowFlow[j][1];
            }
            snowAverage = snowSum / snowFlow.length;
        }
        double[] snowDistribution0 = flowDistribution(param,snowAverage,baseAve,Data.get(1));//融雪随温度分配曲线
        double[] snowDistribution = new double[shanBeiQ.length-before];
        for (int i = L; i < shanBeiQ.length-before+L; i++) {
            snowDistribution[i-L]=snowDistribution0[i];
        }

        snowAverage = 0.0;
        double[] baseDistribution0 = flowDistribution(param,snowAverage,baseAve,Data.get(1));//基础径流随温度分配曲线
        double[] baseDistribution = new double[shanBeiQ.length-before];
        for (int i = L; i < shanBeiQ.length-before+L; i++) {
            baseDistribution[i-L]=baseDistribution0[i];
        }
        //获得混合后的径流序列
        for (int i = 0; i < shanBeiq.length ; i++){
            result[i][0]=dates[i][0];
            if (param.getIsSnowMeltModel()){
                result[i][1] = shanBeiq[i] + snowDistribution[i];//将陕北模型和融雪模型结果相加
            }else {
                result[i][1] = shanBeiq[i] + baseDistribution[i] ;//降水加上前期径流
            }
        }
        return result;
    }

    /**
     * 融雪径流减去基流后，根据温度进行分布
     * @param param
     * @param averageData
     * @param input
     * @return
     */
    public static double[] flowDistribution(ForcastInputParam param,Double averageData,Double baseData, List<PredictInputData> input) {
        int l = param.getPeriodStepNumber() + 10;
        double[] temperature = new double[l];
        for (int i = 0; i < l; i++) {
            temperature[i] = input.get(l).getTemperature();
        }
        double sum = 0;
        for (double num : temperature) {
            sum += num;
        }
        double mean = sum / temperature.length;
        double[] flow = new double[l];
        if (averageData>baseData){//若预报融雪大于基础流量，先去除基流影响再按温度分布
            Double data = averageData-baseData;
            for (int i = 0; i < l; i++) {
                flow[i] = data * temperature[i] / mean;
            }
            for (int i = 0; i < l; i++) {
                flow[i] += baseData;
            }
        }else {//若预报融雪小于基础流量，基流按温度分布
            for (int i = 0; i < l; i++) {
                flow[i] = baseData * temperature[i] / mean;
            }
        }

        return flow;
    }

}
