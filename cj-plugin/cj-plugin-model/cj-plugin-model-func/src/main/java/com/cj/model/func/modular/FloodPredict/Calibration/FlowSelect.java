package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import com.cj.model.func.modular.FloodPredict.entity.Hydrology;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class FlowSelect {

    TimeUtils tu = new TimeUtils();

    //获取断面洪水持续时间
    public  List<Date[]> getFloodDate(CalibrationParam input, Hydrology hydrology)  {
        List<Date[]> result = new ArrayList<>();
        List<PredictInputData> flow;
        String station = hydrology.getIncludingWater().get(0);
        if (input.getIsSelected()){
            for (int i = 0; i < input.getTime().size(); i++) {
                Date start = input.getTime().get(i)[0];
                Date end = input.getTime().get(i)[1];
                flow = input.getFlowData().get(station).stream()
                        .filter(data -> data.getDates().after(start)&&data.getDates().before(end))
                        .collect(Collectors.toList());
                Object[][] objectFlow = getFlow(flow, start, end);
                Object[][] flowSelect = getFloodSelect(hydrology.getStationName(), objectFlow,start,end);
                result.addAll(floodList(hydrology.getStationName(),flowSelect,start,end));
            }
        }else {
            result = input.getTime();
        }

        return result;
    }

    /**
     * 将数据库中数据转换为Object数组以便于进行洪水切片
     */
    private Object[][] getFlow(List<PredictInputData> flow, Date dateStart, Date dateEnd) {
        int durationLong = tu.duration(dateStart, dateEnd, "小时");
        List<Date> dateList = new ArrayList<>();
        Object[][] hisF = new Object[durationLong][2];
        for (PredictInputData predictInputData : flow) {
            dateList.add(predictInputData.getDates());
        }
        for (int i = 0; i < durationLong; i++) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(dateStart);
            for (PredictInputData data : flow) {
                if (tu.DateCompare(dateStart, data.getDates(), "小时")) {
                    hisF[i][0] = data.getDates();
                    hisF[i][1] = data.getFlow() == null ? 0.0 : data.getFlow();
                    break;
                } else {
                    hisF[i][0] = dateStart;
                    int n = tu.findNearestTime(dateList, dateStart);
                    hisF[i][1] = flow.get(n).getFlow() == null ? 0.0 : flow.get(n).getFlow();
                }
            }
            calendar1.add(Calendar.HOUR_OF_DAY, 1);
            dateStart = calendar1.getTime();
        }
        return hisF;
    }

    /**
     * 通过基准线筛选洪水过程
     * @return 洪水过程或者在所选时段内无较大来水时为空
     */
    private Object[][] getFloodSelect(String location,Object[][] predict,Date start,Date end) {
        Object[][] flood = new Object[predict.length][3];
        double max = 0.0;
        double min = 1000000.0;

        for (Object[] objects : predict) {
            if (max <= (double) objects[1]) {
                max = (double) objects[1];//洪峰
            }
            if (min >= (double) objects[1]) {
                min = (double) objects[1];//最小值
            }
        }
        double dt = max - min;//差值
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateDuration = sdf.format(start)+sdf.format(end);

        if (max <= 10 || dt <= 8 || max / min <= 2)//如果洪峰小于10或者来水变幅很小
        {
            throw new RuntimeException(location+dateDuration+"内无较大来水，无法对模型参数进行有效优化");
        }

        if (max >= 500)//如果洪峰大于300
        {
            throw new RuntimeException(location+dateDuration+"内历史数据有误，最大洪峰超过500立方米每秒");
        }
        double line = min + dt * 0.4;//洪水标准线
        for (int i = 0; i < predict.length; i++)//找到所有大于标准线的来水
        {
            //预报流量
            //时间
            if ((double) predict[i][1] > line) {
                flood[i][0] = 1;
            } else {
                flood[i][0] = 0;
            }
            flood[i][1] = predict[i][0];//时间
            flood[i][2] = predict[i][1];//预报流量
        }
        int m = 0;//洪峰的数量
        List<Integer> loc = new ArrayList<>();//记录变化的位置
        for (int i = 0; i < predict.length - 1; i++) {
            if (flood[i][0] != flood[i + 1][0]) {
                m++;
            }
            if (flood[i][0] != flood[i + 1][0]) {
                loc.add(i);
            }
        }
        int remainder = m % 2;
        m = m / 2 + remainder;//洪峰数量
        if ((int) flood[0][0] == 1 && (int) flood[flood.length - 1][0] == 1)//开始是洪水并且结束是洪水
        {
            m = m + 1;
        }
        for (int i = 0; i < predict.length; i++) {
            if ((int) flood[0][0] == 1 && (int) flood[flood.length - 1][0] != 1)//开始为洪水，结束不为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0) + 1; k++) {
                    flood[k][0] = number;
                }//第一个洪峰赋值
                for (int j = 1; j < m; j++) {
                    number++;
                    for (int k = loc.get(2 * j - 1); k <= loc.get(2 * j) + 1; k++) {
                        flood[k][0] = number;
                    }
                }
                break;
            }
            if ((int) flood[0][0] != 1 && (int) flood[flood.length - 1][0] == 1)//开始不为洪水，结束为洪水
            {
                int number = 1;
                for (int j = 0; j < m - 1; j++) {
                    for (int k = loc.get(2 * j); k <= loc.get(2 * j + 1) + 1; k++) {
                        flood[k][0] = number;
                    }
                    number++;
                }
                for (int k = loc.get(2 * m - 2); k < flood.length; k++) {
                    flood[k][0] = number;
                }
                break;
            }
            if ((int) flood[0][0] == 1 && (int) flood[flood.length - 1][0] == 1)//开始为洪水，结束为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0) + 1; k++) {
                    flood[k][0] = number;
                }
                number++;
                for (int j = 0; j < m - 2; j++) {
                    for (int k = loc.get(2 * j + 1); k <= loc.get(2 * j + 2) + 1; k++) {
                        flood[k][0] = number;
                    }
                    number++;
                }
                for (int k = loc.get(2 * m - 3); k < flood.length; k++) {
                    flood[k][0] = number;
                }
                break;
            }
            if ((int) flood[0][0] != 1 && (int) flood[flood.length - 1][0] != 1)//开始不为洪水，结束不为洪水
            {
                int number = 1;
                for (int j = 0; j < m; j++) {
                    for (int k = loc.get(2 * j); k <= loc.get(2 * j + 1) + 1; k++) {
                        flood[k][0] = number;
                    }
                    number++;
                }
                break;
            }
        }
        //洪水过程组合
        int x = 1;
        for (int i = 0; i < flood.length - 1; i++) {
            if (((int) flood[i][0] == 0 && (int) flood[i + 1][0] != 0) || ((int) flood[i][0] != 0 && (int) flood[i + 1][0] != 0)) {
                flood[i + 1][0] = x;
            }
            if (((int) flood[i][0] != 0 && (int) flood[i + 1][0] == 0)) {
                flood[i + 1][0] = 0;
                x++;
            }
            if (((int) flood[i][0] == 0 && (int) flood[i + 1][0] == 0)) {
                flood[i + 1][0] = 0;
            }
        }
        return flood;
    }

    /**
     * 获取超过X个小时的来水过程
     *
     * @param flow 来水过程
     * @return 几个来水过程的开始时间和结束时间或者在未获得可用洪水数据时返回空
     */
    private List<Date[]> floodList(String location,Object[][] flow,Date start, Date end) {
        //判断数据库中是否有数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateDuration = sdf.format(start)+sdf.format(end);
        if (flow == null) {
            throw new RuntimeException(location+"未从本数据集中获得"+dateDuration+"可用洪水数据");
        } else {
            // 判断数组中的元素是否全部为 null 或者长度为 0 的一维数组
            boolean isEmpty = true;
            for (Object[] subArray : flow) {
                if (subArray != null && subArray.length > 0) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                throw new RuntimeException(location+"未从本数据集中获得"+dateDuration+"可用洪水数据");
            }
        }
        List<Object[][]> flowResult = new ArrayList<>();
        List<Object[]> flowList = new ArrayList<>();
        List<Date[]> result = new ArrayList<>();
        int number = 0;
        for (int i = 0; i < flow.length - 1; i++) {
            if (((int) flow[i][0] == 0 && (int) flow[i + 1][0] != 0) || ((int) flow[i][0] != 0 && (int) flow[i + 1][0] != 0)) {
                flowList.add(flow[i]);
                number++;
            } else {
                if (number >= 10) {//判断一场洪水是否超过10个小时
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
        if (flowResult.isEmpty()) {
            throw new RuntimeException(location+dateDuration+"洪水持续时间过短");
        }
        //获取不同时间来水的开始和结束时间
        for (Object[][] objects : flowResult) {
            Date[] dateObject = new Date[2];
            Date startTime ;
            Date endTime ;
            startTime = (Date) objects[0][1];
            endTime = (Date) objects[objects.length - 1][1];
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            cal.add(Calendar.HOUR_OF_DAY, -InputUtils.beforeHours);
            startTime = cal.getTime();
            dateObject[0] = startTime;
            dateObject[1] = endTime;
            result.add(dateObject);
        }
        return result;
    }
}
