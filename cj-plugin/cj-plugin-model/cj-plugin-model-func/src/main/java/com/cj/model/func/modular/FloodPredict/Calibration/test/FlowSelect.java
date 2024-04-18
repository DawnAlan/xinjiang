package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.OneCalibrationParam;
import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.*;


public class FlowSelect {

    TimeUtils timeUtils = new TimeUtils();
    public static void main(String[] args) throws IOException, InvalidFormatException {
        FlowSelect flowSelect = new FlowSelect();
        Object[][] Flood = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\月尺度来水过程.xlsx","Sheet2");
        Object flood = flowSelect.getFloodSelect(Flood);
        if (flood instanceof String){
            System.out.println(flood);
        }else {
            Object dateList = flowSelect.floodList((Object[][]) flood);
            if (dateList instanceof String){
                System.out.println(dateList);
            }else {
                List<Object[]> floodDate = (List<Object[]>) dateList;
                for (int i = 0; i < floodDate.size(); i++) {

                }
            }
        }

//        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\月尺度来水过程.xlsx","洪水过程",flood);
    }

    //获取3个断面洪水持续时间
    public List<Object[]> getFloodDate(OneCalibrationParam input) throws IOException, InvalidFormatException {
        List<Object[]>result = new ArrayList<>();
        FlowSelect flowSelect  = new FlowSelect();
        List<LzzGaugingStation> threeFlow =new ArrayList<>();
        List<LzzGaugingStation> lzzFlow = new ArrayList<>();
        List<IrrigatedPlatformDataInfo> qjFlow = new ArrayList<>();
        for (int i = 0; i < input.getLzzHydrologyParam().getThreeGaugingStation().size(); i++) //3号桥流量
        {
            if (input.getLzzHydrologyParam().getThreeGaugingStation().get(i).getGatherTime().after(input.getStartTime())){
                threeFlow.add(input.getLzzHydrologyParam().getThreeGaugingStation().get(i));
            }
        }
        for (int i = 0; i < input.getLzzHydrologyParam().getLzzInput().size(); i++) //楼庄子流量
        {
            if (input.getLzzHydrologyParam().getLzzInput().get(i).getGatherTime().after(input.getStartTime())){
                lzzFlow.add(input.getLzzHydrologyParam().getLzzInput().get(i));
            }
        }
        for (int i = 0; i < input.getIrrigatedHydrologyParam().getTthInput().size(); i++) //头屯河进库流量
        {
            if (input.getIrrigatedHydrologyParam().getTthInput().get(i).getMonitorTime().after(input.getStartTime())){
                qjFlow.add(input.getIrrigatedHydrologyParam().getTthInput().get(i));
            }
        }
        Date dateStart = input.getStartTime();
        Date dateEnd = input.getEndTime();
        if (input.getLocation().equals("3号桥")){
            Object[][] threeObjectFlow = flowSelect.getFlow(threeFlow,new ArrayList<>(),dateStart,dateEnd);
            List<Object[]> threeDateList = flowSelect.floodList(flowSelect.getFloodSelect(threeObjectFlow));
            result=threeDateList;
        }
        else if (input.getLocation().equals("楼庄子")){
            Object[][] lzzObjectFlow = flowSelect.getFlow(lzzFlow,new ArrayList<>(),dateStart,dateEnd);
            List<Object[]> lzzDateList = flowSelect.floodList(flowSelect.getFloodSelect(lzzObjectFlow));
            result = lzzDateList;
        } else if (input.getLocation().equals("楼头区间")) {
            Object[][] qjObjectFlow = flowSelect.getFlow(new ArrayList<>(),qjFlow,dateStart,dateEnd);
            List<Object[]> qjDateList = flowSelect.floodList(flowSelect.getFloodSelect(qjObjectFlow));
            result=qjDateList;
        }
        return result;
    }

    /**
     * 将数据库中数据转换为Object数组以便于进行洪水切片
     * @param flow
     * @param qjFlow
     * @param dateStart
     * @param dateEnd
     * @return
     */
    public Object[][] getFlow(List<LzzGaugingStation> flow, List<IrrigatedPlatformDataInfo> qjFlow, Date dateStart, Date dateEnd){
        int durationLong = timeUtils.duration(dateStart,dateEnd,"小时");
        List<Date> dateList = new ArrayList<>();
        Date date1 = dateStart;
        for (int i = 0; i < durationLong; i++) {
            dateList.add(date1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(date1);
            calendar1.add(Calendar.HOUR_OF_DAY, 1);
            date1 = calendar1.getTime();
        }
        Object[][] hisF = new Object[durationLong][2];
        if (qjFlow.isEmpty()){//3号桥和楼庄子的历史流量
            for (int i = 0; i < durationLong; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);
                for (int j = 0; j < flow.size(); j++) {
                    if (timeUtils.DateCompare(dateStart,flow.get(j).getGatherTime(),"小时")){
                        hisF[i][0]=flow.get(j).getGatherTime();
                        hisF[i][1]=flow.get(j).getFlow();
                        break;
                    }else {
                        hisF[i][0]=dateStart;
                        int n = timeUtils.findNearestTime(dateList,dateStart);
                        hisF[i][1]=flow.get(n).getFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);
                dateStart=calendar1.getTime();
            }
        }else {//楼头区间的历史流量
            for (int i = 0; i < durationLong; i++) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateStart);
                for (int j = 0; j < qjFlow.size(); j++) {
                    if (timeUtils.DateCompare(dateStart,qjFlow.get(j).getMonitorTime(),"小时")){
                        hisF[i][0]=qjFlow.get(j).getMonitorTime();
                        hisF[i][1]=qjFlow.get(j).getSqMonitorFlow();
                        break;
                    }else {
                        hisF[i][0]=dateStart;
                        int n = timeUtils.findNearestTime(dateList,dateStart);
                        if (n>=qjFlow.size()){
                            throw new RuntimeException("未从数据库中获取到该时段径流数据");
                        }
                        hisF[i][1]=qjFlow.get(n).getSqMonitorFlow();
                    }
                }
                calendar1.add(Calendar.HOUR_OF_DAY, 1);
                dateStart=calendar1.getTime();
            }
        }
        return hisF;
    }
    /**
     * 通过基准线筛选洪水过程
     * @param predict
     * @return 洪水过程或者在所选时段内无较大来水时为空
     */
    public Object[][] getFloodSelect(Object[][] predict) throws IOException, InvalidFormatException {
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
        if (max <= 10 || dt/min <= 2)//如果洪峰小于10或者来水变幅很小
        {
            throw new RuntimeException("所选时段内无较大来水，无法对模型参数进行有效优化");
        }
        if (max >=300)//如果洪峰大于300
        {
            throw new RuntimeException("所选时段内历史数据有误，最大洪峰超过300立方米每秒");
        }
        double line = min + dt * 0.3;//洪水标准线
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
        //洪水过程组合
        int x = 1;
        for (int i = 0; i < flood.length - 1; i++) {
            if (((int)flood[i][0]==0&&(int)flood[i+1][0]!=0)||((int)flood[i][0]!=0&&(int)flood[i+1][0]!=0)){
                flood[i+1][0] = x;
            }
            if (((int)flood[i][0]!=0&&(int)flood[i+1][0]==0)){
                flood[i+1][0] = 0;
                x++;
            }
            if (((int)flood[i][0]==0&&(int)flood[i+1][0]==0)){
                flood[i+1][0] = 0;
            }
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\月尺度来水过程.xlsx","洪水过程",flood);
        return flood;
    }

    /**
     * 获取超过72个小时的来水过程
     * @param flow 来水过程
     * @return 几个来水过程的开始时间和结束时间或者在未获得可用洪水数据时返回空
     */
    public List<Object[]> floodList (Object[][] flow){
        List<Object[][]> flowResult = new ArrayList<>();
        List<Object[]> flowList = new ArrayList<>();
        List<Object[]> result = new ArrayList<>();
        int number = 0;
        for (int i = 0; i < flow.length-1; i++) {
            if (((int)flow[i][0]==0&&(int)flow[i+1][0]!=0)||((int)flow[i][0]!=0&&(int)flow[i+1][0]!=0)){
                flowList.add(flow[i]);
                number++;
            }else {
                if (number>=72){//判断一场洪水是否超过72个小时
                    Object[][] resultObject = new Object[flowList.size()][3];
                    for (int j = 0; j < flowList.size(); j++) {
                        resultObject[j] = flowList.get(j);
                    }
                    flowResult.add(resultObject);
                }
                flowList = new ArrayList<>();
                number = 0;
            }
        }
        if (flowResult.isEmpty()){
            throw new RuntimeException("未从本数据集中获得可用洪水数据");
        }
        //获取不同时间来水的开始和结束时间
        for (int i = 0; i < flowResult.size(); i++) {
            Object[] dateObject = new Object[2];
            Object[][] oneFlow = flowResult.get(i);
            Date startTime = new Date();
            Date endTime = new Date();
            startTime = (Date) oneFlow[0][1];
            endTime = (Date) oneFlow[oneFlow.length-1][1];
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.HOUR_OF_DAY,-24);
            startTime = cal.getTime();
            dateObject[0] = startTime;
            dateObject[1] = endTime;
            result.add(dateObject);
        }
        return result;
    }
}
