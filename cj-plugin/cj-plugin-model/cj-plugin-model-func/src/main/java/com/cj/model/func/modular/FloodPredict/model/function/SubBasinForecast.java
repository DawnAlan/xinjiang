package com.cj.model.func.modular.FloodPredict.model.function;


import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiModel;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.utils.DataUtils;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.entity.Flood;

import java.util.*;


public class SubBasinForecast {
    String floodLevel = "一年一遇";//洪水等级
    String floodSource;//洪水来源
    String floodTime;//洪水传播时间
    String floodComposition;//洪水组成
    DataUtils dataUtils = new DataUtils();
    TimeUtils timeUtils = new TimeUtils();
    int beforeHours = InputUtils.beforeHours;//前期落地雨时间
    private Hydrology hydrology = new Hydrology();

    /**
     * 获得场次洪水预报数据
     */
    public List<Flood> getShortResult(ForecastInputParam param, InputDataSet Data, Object[][] snowData) {
        FloodBasin floodBasin = param.getFloodBasin();
        Map<String, ShanbeiParam> paramMap;
        if (param.getParamMap().isEmpty()) {
            paramMap = param.getFloodBasin().getParamMap();
        } else {
            paramMap = param.getParamMap().get(param.getLocation().equals("楼头区间") ? "头屯河" : param.getLocation());
        }
        for (Hydrology station : floodBasin.getHydrologies()) {
            if (station.getStationName().equals(param.getLocation())) {
                hydrology = station;
            }
        }
        //陕北模型输入、蒸散发和前期雨量
        List<PredictInputData> PreFlow = Data.getFlowData();
        Map<String, double[]> flow = new HashMap<>();
        Map<String, Integer> lTime = new HashMap<>();
        List<String> rainStation = hydrology.getRainStation();
        int l = param.getPeriodStepNumber() * param.getPeriodStepSize() + InputUtils.beforeHours;
        double[] rainQ = new double[l];
        for (int i = 0; i < rainStation.size(); i++) {
            String station = rainStation.get(i);
            ShanbeiParam shanbeiparam = paramMap.get(station);
            List<RainFallDto> hourRain = Data.getRainHourData().get(station);//蒸散发和降雨
            List<RainFallDto> dayRain = Data.getRainDayData().get(station);//前期雨量
            double max = hourRain.stream()
                    .mapToDouble(RainFallDto::getRainFall)
                    .max()
                    .orElse(0.0);
            setParams(shanbeiparam, max);
            if (param.getLocation().equals("3号桥")) {
                shanbeiparam.setL(Math.max(shanbeiparam.getL() - 1, 0));
            }
            double[] subBasinQ = getSubBasinQ(shanbeiparam, hourRain, dayRain);//子流域产流量
            for (int j = 0; j < rainQ.length; j++) {
                rainQ[j] += subBasinQ[j];//总产流量
            }
            lTime.put(station, shanbeiparam.getL());//记录汇流时间
            flow.put(station, subBasinQ);//记录个子流域产流
        }
        //获得径流序列包含了降水融雪地下水
        Object[][] shortFlow = mixedFlood(param, rainQ, Data, snowData);
        floodLevel = getFloodLevel(shortFlow, param.getLocation());//洪水等级
        floodSource = getFloodSources(flow, param);//洪水来源
        floodTime = getFloodTime(lTime);//洪水传播时间
        floodComposition = getFloodComposition(param, PreFlow, rainQ, snowData);//洪水组成
        //将Object转化为Flood类型
        List<RainFallDto> surface = dataUtils.pointToSurface(Data.getRainHourData(), param.getLocation());
        double[] surfaceRain = new double[l];
        for (int i = 0; i < surfaceRain.length; i++) {
            surfaceRain[i] = surface.get(i).getRainFall();
        }
        return setShortFlood(shortFlow, param, surfaceRain);
    }

    public void setParams(ShanbeiParam param, Double max) {
        if (max >= 16) {
            param.setL(Math.max(param.getL() - 3, 0));
            param.setCS(param.getCS() - 0.1);
        } else if (max >= 8) {
            param.setL(Math.max(param.getL() - 1, 0));
            param.setCS(param.getCS() - 0.05);
        } else {
            param.setL(param.getL());
            param.setCS(param.getCS());
        }
    }

    public double[] getSubBasinQ(ShanbeiParam param, List<RainFallDto> preData, List<RainFallDto> hisData) {
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        //蒸发量、降雨量赋值
        Object[][] preREData = new Object[preData.size()][3];
        for (int i = 0; i < preData.size(); i++) {
            preREData[i][0] = preData.get(i).getDate();
            preREData[i][1] = preData.get(i).getTemperature();
            preREData[i][2] = preData.get(i).getRainFall();
        }
        preREData = dataUtils.temToEva(preREData);//将温度转为蒸发量
        //前期累计雨量
        Object[][] historyRData = new Object[hisData.size()][2];
        for (int i = 0; i < hisData.size(); i++) {
            historyRData[i][0] = hisData.get(i).getDate();
            historyRData[i][1] = hisData.get(i).getRainFall();
        }
        //模型计算过程
        shanBeiModel.InputData(param, preREData, historyRData);
        shanBeiModel.InitialMoistureContentCalculation();
        shanBeiModel.RunoffYieldCalculation_UnevenInfiltration();
        shanBeiModel.ConfluenceCalculation2();
        double[] result = new double[shanBeiModel.Q.length];
        System.arraycopy(shanBeiModel.Q, 0, result, 0, result.length);
        return result;
    }


    /**
     * 把预报洪水过程写成规范表格形式
     */
    public List<Flood> setShortFlood(Object[][] predict, ForecastInputParam param, double[] rain) {
        List<Flood> result = new ArrayList<>();
        int n = param.getPeriodStepNumber();
        int l = param.getPeriodStepSize();
        List<Object[][]> floodInformation = getFloodInformation(predict);
        Object[][] floodIndex = floodInformation.get(0);//洪号
        Object[][] floodNature = floodInformation.get(1);//洪水信息
        Object[][] water_outQ = new Object[n][3];//水位、出库流量、汛限水位
        if (param.getLocation().equals("楼庄子")) {
            Object[][] input = new Object[n][2];
            for (int i = 0; i < n; i++) {
                input[i][0] = predict[i * l][0];
                input[i][1] = predict[i * l][1];
            }
            int timeLength = 3600 * l;
            ReqCurve reqCurve = new ReqCurve();
            List<Option> lzzOutList = LZZ.Calculate(param.getBasinStr(), input, timeLength, reqCurve, param.getIsReferenceWater());
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = lzzOutList.get(i).getH1();
                water_outQ[i][1] = lzzOutList.get(i).getQOut();
                water_outQ[i][2] = (((double) predict[i][1] > 110.0) ? 1 : 0);
                //water_outQ[i][2] = (((double) water_outQ[i][0] > 1394.5) ? 1 : 0);
            }
        } else {
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = getWaterLevel(predict, param)[i];
                water_outQ[i][1] = predict[i * l][1];
                water_outQ[i][2] = (((double) predict[i][1] > 110.0) ? 1 : 0);
            }
        }
        //连续列的赋值
        for (int i = 0; i < InputUtils.beforeHours; i++) {
            Flood flood = new Flood();
            flood.setLocation(param.getLocation());//断面位置
            flood.setScale(String.valueOf(3600 * l));//尺度
            flood.setPeakIndex(0);//洪号
            Date date = timeUtils.addCalendar(param.getPreStartTime(), "小时", -InputUtils.beforeHours + i);
            flood.setTime(date);//时间
            flood.setPreQ(Math.round((double) predict[i][1] * 100.0) / 100.0);//预报流量
            flood.setPeakFlood((Double) floodNature[2][1]);//洪峰
            flood.setPeakTime((Date) floodNature[3][1]);//峰现时间
            flood.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
            flood.setFloodVolume((Double) floodNature[0][1]);//洪量
            flood.setFloodVolumeOne((Double) floodNature[4][1]);//特征洪量
            flood.setFloodVolumeThree((Double) floodNature[5][1]);
            flood.setFloodVolumeSeven((Double) floodNature[6][1]);
            flood.setPeakVolume((Double) floodNature[7][1]);
            flood.setQCause(floodSource);//洪水来源
            flood.setQComposition(floodComposition);//洪水组成
            flood.setRainProcess(rain[i]);//雨情
            flood.setWarningTime(0);//是否超过汛限水位
            flood.setFloodLevel(floodLevel);//洪水等级
            flood.setConfluenceTime(floodTime);//汇流时间
            result.add(flood);
        }
        for (int i = 0; i < n; i++) {
            Flood flood = new Flood();
            flood.setLocation(param.getLocation());//断面位置
            flood.setScale(String.valueOf(3600 * l));//尺度
            flood.setPeakIndex((Integer) floodIndex[i * l][0]);//洪号
            flood.setTime((Date) predict[i * l + InputUtils.beforeHours][0]);//时间
            flood.setPreQ(Math.round((double) predict[i * l + InputUtils.beforeHours][1] * 100.0) / 100.0);//预报流量
            flood.setPeakFlood((Double) floodNature[2][1]);//洪峰
            flood.setPeakTime((Date) floodNature[3][1]);//峰现时间
            flood.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
            flood.setFloodVolume((Double) floodNature[0][1]);//洪量
            flood.setFloodVolumeOne((Double) floodNature[4][1]);//特征洪量
            flood.setFloodVolumeThree((Double) floodNature[5][1]);
            flood.setFloodVolumeSeven((Double) floodNature[6][1]);
            flood.setPeakVolume((Double) floodNature[7][1]);
            flood.setQCause(floodSource);//洪水来源
            flood.setQComposition(floodComposition);//洪水组成
            flood.setRainProcess(rain[i + InputUtils.beforeHours]);//雨情
            flood.setWaterLevel((double) water_outQ[i][0]);//相应水位
            flood.setOutQ((double) water_outQ[i][1]);//出库流量
            flood.setWarningTime((Integer) water_outQ[i][2]);//是否超过汛限水位
            flood.setFloodLevel(floodLevel);//洪水等级
            flood.setConfluenceTime(floodTime);//汇流时间
            result.add(flood);
        }
        return result;
    }

    /**
     * 记录好的洪号，洪峰，峰现时间，持续时间，洪量
     */
    public List<Object[][]> getFloodInformation(Object[][] predict) {
        List<Object[][]> result = new ArrayList<>();
        if (predict.length == 1) {
            Object[][] flood = new Object[predict.length][3];
            flood[0][0] = 1;
            flood[0][1] = predict[0][0];
            flood[0][2] = predict[0][1];
            Object[][] floodNature = new Object[8][2];
            floodNature[0][0] = "洪量";//万立方米
            floodNature[1][0] = "洪峰持续时间";
            floodNature[2][0] = "洪峰";
            floodNature[3][0] = "峰现时间";
            floodNature[4][0] = "1日洪量";//万立方米
            floodNature[5][0] = "3日洪量";//万立方米
            floodNature[6][0] = "7日洪量";//万立方米
            floodNature[7][0] = "洪峰洪量";
            floodNature[0][1] = (double) predict[0][1] * 3600 / 100000.0;//万立方米
            floodNature[1][1] = "1h";
            floodNature[2][1] = predict[0][1];
            floodNature[3][1] = predict[0][0];
            floodNature[4][1] = (double) predict[0][1] * 3600 / 100000.0;
            floodNature[5][1] = (double) predict[0][1] * 3600 / 100000.0;
            floodNature[6][1] = (double) predict[0][1] * 3600 / 100000.0;
            floodNature[7][1] = (double) predict[0][1] * 3600 / 10000.0;
            result.add(flood);
            result.add(floodNature);
            return result;
        }
        Object[][] flood = new Object[predict.length][3];
        double max = 0.0;
        double min = 1000000.0;
        for (Object[] value : predict) {
            if (max <= (double) value[1]) {
                max = (double) value[1];//洪峰
            }
            if (min >= (double) value[1]) {
                min = (double) value[1];//最小值
            }
        }
        double dt = max - min;//差值
        double line = Math.min(min + dt * 0.6, 40.0);//洪水标准线
        for (int i = 0; i < predict.length; i++)//找到所有大于标准线的来水
        {
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
            if ((int) flood[0][0] == 1 && (int) flood[flood.length - 1][0] == 1 && !loc.isEmpty())//开始为洪水，结束为洪水
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
        /*
         * 以下为针对分好洪号后的洪水过程
         */
        Object[][] floodNature = new Object[8][2];
        floodNature[0][0] = "洪量";//万立方米
        floodNature[1][0] = "洪峰持续时间";
        floodNature[2][0] = "洪峰";
        floodNature[3][0] = "峰现时间";
        floodNature[4][0] = "1日洪量";//万立方米
        floodNature[5][0] = "3日洪量";//万立方米
        floodNature[6][0] = "7日洪量";//万立方米
        floodNature[7][0] = "洪峰洪量";//万立方米
        double Volume = 0.0;
        String duration;
        double floodSum = 0.0;
        int Number = 0;//第几个洪水
        //判断第几个来水洪量最大
        for (int i = 1; i <= m; i++) {
            double sum = 0.0;
            for (Object[] objects : flood) {
                if ((int) objects[0] == i) {
                    sum += (double) objects[2];
                }
            }
            if (sum > floodSum) {
                floodSum = sum;
                Number = i;
            }
        }
        List<Double> maxFlood = new ArrayList<>();
        for (Object[] objects : flood) {
            if ((int) objects[0] == Number) {
                maxFlood.add((double) objects[2]);
            }
        }
        int beforeMin;
        int afterMin;
        double dVolume;
        double dMin;
        int hour;
        //洪峰洪量
        for (Double aDouble : maxFlood) {
            Volume += aDouble * 3600 / 10000;//多少万立方米
        }
        Volume = Math.round(Volume * 100.0) / 100.0;
        floodNature[7][1] = Volume;
        //总洪量
        double allVolume = 0.0;
        for (Object[] objects : flood) {
            allVolume += (double) objects[2];
        }
        allVolume = Math.round((allVolume * 3600 / 10000) * 100.0) / 100.0;//转换为万立方米
        floodNature[0][1] = allVolume;
        //持续时间
        if (maxFlood.get(0) > line)//开始为洪水
        {
            dVolume = maxFlood.get(maxFlood.size() - 2) - maxFlood.get(maxFlood.size() - 1);
            dMin = maxFlood.get(maxFlood.size() - 2) - line;
            afterMin = (int) (dMin / dVolume * 60);
            hour = maxFlood.size() - 1;
            duration = hour + "h" + afterMin + "min";
            floodNature[1][1] = duration;
        } else if (maxFlood.get(maxFlood.size() - 1) > line)//结束为洪水
        {
            dVolume = maxFlood.get(1) - maxFlood.get(0);
            dMin = maxFlood.get(1) - line;
            beforeMin = (int) (dMin / dVolume * 60);
            hour = maxFlood.size() - 1;
            duration = hour + "h" + beforeMin + "min";
            floodNature[1][1] = duration;
        } else {
            dVolume = maxFlood.get(1) - maxFlood.get(0);
            dMin = maxFlood.get(1) - line;
            beforeMin = (int) (dMin / dVolume * 60);
            dVolume = maxFlood.get(maxFlood.size() - 2) - maxFlood.get(maxFlood.size() - 1);
            dMin = maxFlood.get(maxFlood.size() - 2) - line;
            afterMin = (int) (dMin / dVolume * 60);
            hour = maxFlood.size() - 2;
            if (beforeMin + afterMin > 60) {
                hour = hour + 1;
                duration = hour + "h" + (beforeMin + afterMin - 60) + "min";
            } else {
                duration = hour + "h" + (beforeMin + afterMin) + "min";
            }
            floodNature[1][1] = duration;
        }
        //洪峰
        double maxQ = 0.0;
        int t = 0;
        for (Double aDouble : maxFlood) {
            if (maxQ < aDouble) {
                maxQ = aDouble;
                t++;
            }
        }
        floodNature[2][1] = maxQ;
        //峰现时间
        int n = 0;
        double maxq = 0.0;
        for (int i = 0; i < flood.length; i++) {
            if (maxq < (double) flood[i][2]) {
                maxq = (double) flood[i][2];
                n = i;
            }
        }
        floodNature[3][1] = flood[n][1];
        //1日、3日、7日洪量


        if (flood.length < 24) {//不足一天洪量
            floodNature[4][1] = allVolume;
            floodNature[5][1] = allVolume;
            floodNature[6][1] = allVolume;
        } else if (flood.length < 72) {
            double Volume1 = getMaxLengthVolume(flood, 24);
            floodNature[4][1] = Volume1;
            floodNature[5][1] = allVolume;
            floodNature[6][1] = allVolume;
        } else if (flood.length < 168) {
            double Volume1 = getMaxLengthVolume(flood, 24);
            double Volume3 = getMaxLengthVolume(flood, 72);
            floodNature[4][1] = Volume1;
            floodNature[5][1] = Volume3;
            floodNature[6][1] = allVolume;
        }else {
            double Volume1 = getMaxLengthVolume(flood, 24);
            double Volume3 = getMaxLengthVolume(flood, 72);
            double Volume7 = getMaxLengthVolume(flood, 168);
            floodNature[4][1] = Volume1;
            floodNature[5][1] = Volume3;
            floodNature[6][1] = Volume7;
        }
        result.add(flood);
        result.add(floodNature);
        return result;
    }

    /**
     * 滑动窗口法计算洪量
     *
     * @param flood
     * @param l
     * @return
     */
    public double getMaxLengthVolume(Object[][] flood, int l) {
        double maxVolume = 0.0;
        for (int i = 0; i < flood.length - l + 1; i++) {
            double volume = 0.0;
            for (int j = 0; j < l; j++) {
                volume += (double) flood[i + j][2];
            }
            if (maxVolume < volume) {
                maxVolume = volume;
            }
        }
        return Math.round((maxVolume * 3600 / 10000) * 100.0) / 100.0;//转换为万立方米

    }

    /**
     * 获得洪水等级
     */
    public String getFloodLevel(Object[][] input, String location) {
        String result = "一年一遇";
        Object[][] floodNature = getFloodInformation(input).get(1);
        double maxQ = (double) floodNature[2][1];
        //根据位置返回洪水等级
        switch (location) {
            case "楼庄子":
            case "3号桥":
                if (maxQ >= 944) {
                    result = "万年一遇";
                } else if (maxQ >= 750) {
                    result = "二千年一遇";
                } else if (maxQ >= 668) {
                    result = "千年一遇";
                } else if (maxQ >= 587) {
                    result = "五百年一遇";
                } else if (maxQ >= 530) {
                    result = "三百年一遇";
                } else if (maxQ >= 482) {
                    result = "二百年一遇";
                } else if (maxQ >= 405) {
                    result = "百年一遇";
                } else if (maxQ >= 330) {
                    result = "五十年一遇";
                } else if (maxQ >= 277) {
                    result = "三十年一遇";
                } else if (maxQ >= 236) {
                    result = "二十年一遇";
                } else if (maxQ >= 171) {
                    result = "十年一遇";
                } else if (maxQ >= 114) {
                    result = "五年一遇";
                } else {
                    result = "一年一遇";
                }
                break;
            case "楼头区间":
                if (maxQ >= 807) {
                    result = "千年一遇";
                } else if (maxQ >= 708) {
                    result = "五百年一遇";
                } else if (maxQ >= 581) {
                    result = "两百年一遇";
                } else if (maxQ >= 486) {
                    result = "百年一遇";
                } else if (maxQ >= 395) {
                    result = "五十年一遇";
                } else if (maxQ >= 329) {
                    result = "三十年一遇";
                } else if (maxQ >= 279) {
                    result = "二十年一遇";
                } else if (maxQ >= 198) {
                    result = "十年一遇";
                } else if (maxQ >= 126) {
                    result = "五年一遇";
                } else {
                    result = "一年一遇";
                }
                break;
            case "头屯河":
                if (maxQ >= 1013) {
                    result = "千年一遇";
                } else if (maxQ >= 883) {
                    result = "五百年一遇";
                } else if (maxQ >= 713) {
                    result = "两百年一遇";
                } else if (maxQ >= 590) {
                    result = "百年一遇";
                } else if (maxQ >= 470) {
                    result = "五十年一遇";
                } else if (maxQ >= 402) {
                    result = "三十年一遇";
                } else if (maxQ >= 320) {
                    result = "二十年一遇";
                } else if (maxQ >= 219) {
                    result = "十年一遇";
                } else if (maxQ >= 134) {
                    result = "五年一遇";
                } else {
                    result = "一年一遇";
                }
                break;
        }
        return result;
    }

    /**
     * 相应水位
     */
    public double[] getWaterLevel(Object[][] predict, ForecastInputParam param) {
        //水位流量关系
        double[] waterLevel = new double[predict.length];
        if (param.getLocation().equals("楼头区间")) {
            for (int i = 0; i < predict.length; i++) {
                waterLevel[i] = (double) predict[i][1] * 0.0235 + 988;//这里用水位流量曲线
            }
        } else if (param.getLocation().equals("3号桥")) {
            for (int i = 0; i < predict.length; i++) {
                waterLevel[i] = (double) predict[i][1] * 0.0235 + 0.3664;//这里用水位流量曲线
            }
        }
        return waterLevel;
    }

    /**
     * 子流域权重
     * 求洪水组成，各个雨量站代表的汇流面贡献多少水量
     */
    public String getFloodSources(Map<String, double[]> pointData, ForecastInputParam param) {
        StringBuilder result = new StringBuilder();
        double Sum = 0.0;
        double sub;
        Map<String, Double> subSquare = new HashMap<>();
        switch (param.getLocation()) {
            case "3号桥": {
                subSquare.put("宰尔德", 0.06);
                subSquare.put("东南沟", 0.21);
                subSquare.put("萨尔达万", 0.02);
                subSquare.put("煤矿沟", 0.04);
                subSquare.put("无名沟", 0.03);
                subSquare.put("八一林场", 0.45);
                subSquare.put("加普沙", 0.19);
                break;
            }
            case "楼庄子": {
                subSquare.put("宰尔德", 0.03);
                subSquare.put("东南沟", 0.16);
                subSquare.put("萨尔达万", 0.02);
                subSquare.put("煤矿沟", 0.03);
                subSquare.put("无名沟", 0.02);
                subSquare.put("八一林场", 0.33);
                subSquare.put("加普沙", 0.18);
                subSquare.put("喀什沟", 0.08);
                subSquare.put("制材厂", 0.12);
                subSquare.put("黑沟", 0.03);
                break;
            }
            case "楼头区间":
                subSquare.put("团结一队", 0.17);
                subSquare.put("小渠子", 0.32);
                subSquare.put("头屯河水库", 0.29);
                subSquare.put("甘沟", 0.22);
                subSquare.put("楼庄子库区", 0.22);
        }
        for (Map.Entry<String, double[]> entry : pointData.entrySet()) {
            double[] value = entry.getValue();
            for (double v : value) {
                Sum += v;
            }
        }
        if (pointData.containsKey("制材厂自动雨量站")) {
            result.append("3号桥").append(":").append(",");
        }
        for (Map.Entry<String, double[]> entry : pointData.entrySet()) {
            double subSum = 0.0;
            String key = entry.getKey();
            String location;
            if (key.contains("自动雨量站")) {
                location = key.replaceAll("自动雨量站", "");
            } else {
                location = key.replaceAll("雨量站", "");
            }
            double[] value = entry.getValue();
            for (double v : value) {
                subSum += v;
            }
            if (Sum != 0.0) {
                sub = Math.round((float) subSum / Sum * 100) / 100.0;
            } else {
                sub = subSquare.get(location);
            }
            result.append(location).append(":").append(sub).append(",");
        }
        return result.toString();
    }

    /**
     * 求洪水来源
     *
     * @param PreFlow   基础流量
     * @param Q_shanbei 降雨产生
     * @param snowData  融雪产生
     */
    public String getFloodComposition(ForecastInputParam param, List<PredictInputData> PreFlow, double[] Q_shanbei, Object[][] snowData) {
        String result = "";
        double snowFlow = 0.0;
        double preFlowSum = 0.0;
        double preFlow;
        double shanbeiFlow = 0.0;
        int number = Q_shanbei.length;
        double base = (param.getLocation().equals("楼头区间") ? 0.0 : 1.28) * number;
        if (param.getIsSnowMeltModel()) {
            for (Object[] snowDatum : snowData) {
                snowFlow += (double) snowDatum[1];
            }
            snowFlow = snowFlow / snowData.length * number;
            for (double v : Q_shanbei) {
                shanbeiFlow = shanbeiFlow + v;
            }
            double Sum = snowFlow + shanbeiFlow + base;
            double shanbei = Math.round((float) shanbeiFlow / Sum * 100) / 100.0;
            double rong = Math.round((float) snowFlow / Sum * 100) / 100.0;
            double di = Math.round((float) base / Sum * 100) / 100.0;
            if (Sum == 0.0) {
                result += "降水:0.00,融雪:0.00,地下水:1.00";
            } else {
                result += "降水:" + shanbei + "," + "融雪:" + rong + "," + "地下水:" + di;
            }
        } else {
            for (double v : Q_shanbei) {
                shanbeiFlow = shanbeiFlow + v;
            }
            for (PredictInputData predictInputData : PreFlow) {
                if (predictInputData.getFlow() == null) {
                    predictInputData.setFlow(0.0);
                }
                preFlowSum += predictInputData.getFlow();
            }
            int n = PreFlow.size() == 0 ? 1 : PreFlow.size();
            preFlow = preFlowSum / n * Q_shanbei.length;
            double rongDate = (preFlow - base) > 0 ? (preFlow - base) : 0.0;
            double Sum = preFlow + shanbeiFlow;
            double shanbei = Math.round((float) shanbeiFlow / Sum * 100) / 100.0;
            double rong = Math.round((float) rongDate / Sum * 100) / 100.0;
            base = Math.min(base, preFlow);
            double di = Math.round((float) base / Sum * 100) / 100.0;
            if (Sum == 0.0) {
                result += "降水:0.00,融雪:0.00,地下水:1.00";
            } else {
                result += "降水:" + shanbei + "," + "融雪:" + rong + "," + "地下水:" + di;
            }
        }
        return result;
    }

    /**
     * 求洪水汇流时间
     */
    public String getFloodTime(Map<String, Integer> lData) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : lData.entrySet()) {
            String key = entry.getKey();
            String location;
            if (key.contains("自动雨量站")) {
                location = key.replaceAll("自动雨量站", "");
            } else {
                location = key.replaceAll("雨量站", "");
            }
            String time = lData.get(key) > 0 ? lData.get(key) + "h" : "1h以内";
            result.append(location).append(":").append(time).append(",");
        }
        if (lData.containsKey("制材厂自动雨量站")) {
            String time = lData.get("制材厂自动雨量站") > 0 ? lData.get("制材厂自动雨量站") + "h" : "1h以内";
            result.append("3号桥").append(":").append(time).append(",");
        }
        return result.toString();
    }

    /**
     * 陕北模型计算所得降水数据与前期径流数据、融雪数据整合
     *
     * @param shanBeiQ 降水所得
     * @param snowFlow 融雪径流
     * @return 预报的径流值
     */
    public Object[][] mixedFlood(ForecastInputParam param, double[] shanBeiQ, InputDataSet Data, Object[][] snowFlow) {
        List<PredictInputData> preFlow = Data.getFlowData();
        List<RainFallDto> preTemperature = Data.getRainHourData().get("制材厂自动雨量站");
        int l = param.getPeriodStepNumber() * param.getPeriodStepSize() + InputUtils.beforeHours;
        Object[][] result = new Object[l][2];
        Date warmUpStart = timeUtils.addCalendar(param.getPreStartTime(), "小时", -InputUtils.beforeHours);
        Date[][] dates = timeUtils.getDateList(warmUpStart, l, 0, 1);
        double[] snowDistribution = flowDistribution(param, snowFlow, preFlow, preTemperature);//融雪随温度分配曲线
        //获得混合后的径流序列
        for (int i = 0; i < shanBeiQ.length; i++) {
            result[i][0] = dates[i][0];
            result[i][1] = shanBeiQ[i] + snowDistribution[i];//将陕北模型和融雪模型结果相加
        }
        return result;
    }

    /**
     * 融雪径流减去基流后，根据温度进行分布
     */
    public double[] flowDistribution(ForecastInputParam param, Object[][] snowFlow, List<PredictInputData> preFlow, List<RainFallDto> input) {
        //减去汇流滞时
        Date currentDate = param.getPreStartTime();
        Date beforeDate1 = timeUtils.addCalendar(param.getPreStartTime(), "小时", -InputUtils.beforeHours);
        Date beforeDate0 = timeUtils.addCalendar(param.getPreStartTime(), "日", -5);
        int month = timeUtils.getSpecificDate(param.getPreStartTime()).get("月");
        int num = param.getPeriodStepNumber() * param.getPeriodStepSize() + InputUtils.beforeHours;
        String location = param.getLocation();
        double[] result = new double[num];
        //基础流量
        Double baseAve;
        //当前径流量
        Double currentFlow;
        if (!preFlow.isEmpty()) {
            //基础流量
            int a = 0;
            double sum = 0.0;
            for (PredictInputData predictInputData : preFlow) {
                if (predictInputData.getDates().after(beforeDate0) && predictInputData.getDates().before(beforeDate1)) {
                    sum += predictInputData.getFlow();
                    a++;
                }
            }
            if (a == 0) {
                a++;
            }
            baseAve = sum / a;
        } else {
            if (!location.equals("楼头区间")) {
                double[] baseFlow = new double[]{1.29, 1.19, 1.77, 2.78, 5.0, 5.49, 6.5, 6.7, 4.18, 2.5, 2.23, 1.52};
                baseAve = baseFlow[month - 1];
            } else {
                baseAve = 1.29;
            }
        }
        //此时的径流
        if (!preFlow.isEmpty()) {
            //前期流量
            int a = 0;
            for (PredictInputData predictInputData : preFlow) {
                if (predictInputData.getDates().before(currentDate)) {
                    a++;
                }
            }
            if (a == 0) {
                a++;
            }
            currentFlow = preFlow.get(a - 1).getFlow();
        } else {
            if (!location.equals("楼头区间")) {
                double[] baseFlow = new double[]{1.29, 1.19, 1.77, 2.78, 5.0, 5.49, 6.5, 6.7, 4.18, 2.5, 2.23, 1.52};
                currentFlow = baseFlow[month - 1];
            } else {
                currentFlow = 1.29;
            }
        }
        for (int i = 0; i < InputUtils.beforeHours; i++) {//前期来流采用真实历史数据
            Date time = timeUtils.addCalendar(beforeDate1, "小时", i);
            if (preFlow.isEmpty()) {
                result[i] = Math.min(currentFlow, baseAve);
            } else {
                PredictInputData start = preFlow.stream()
                        .filter(d -> d.getDates().before(time))
                        .sorted(Comparator.comparing(PredictInputData::getDates).reversed())
                        .findFirst()
                        .orElse(preFlow.get(0));
                PredictInputData end = preFlow.stream()
                        .filter(d -> d.getDates().after(time))
                        .findFirst()
                        .orElse(preFlow.get(preFlow.size() - 1));
                result[i] = Linear(start, end, time);
            }
        }

        if (param.getIsSnowMeltModel()) {
            //融雪基流
            double averageData;
            double snowSum = 0.0;
            for (Object[] objects : snowFlow) {
                snowSum = snowSum + (double) objects[1];
            }
            averageData = snowSum / snowFlow.length;
            int l = num;
            double[] flow = new double[l];
            if (param.getLocation().equals("楼头区间")) {
                System.arraycopy(flow, beforeHours, result, beforeHours, l - beforeHours);
            } else {
                double[] temperature = new double[l];
                for (int i = 0; i < l; i++) {
                    temperature[i] = input.get(i).getTemperature();
                }
                int number = 0;
                for (int i = 0; i < temperature.length; i++) {
                    if (!(temperature[i] >= 5)) {
                        temperature[i] = 0.0;
                    } else {
                        number++;
                    }
                }
                double sum = 0;
                for (double tem : temperature) {
                    sum += tem;
                }

                if (number == 0) {//如果没有超过5摄氏度
                    for (int i = 0; i < num; i++) {
                        result[i] = baseAve;
                    }
                } else {
                    if (averageData > baseAve) {//若预报融雪大于基础流量，先去除基流影响再按温度分布
                        double data = (averageData - baseAve);
                        for (int i = 0; i < l; i++) {
                            if (sum == 0) {
                                flow[i] = data;
                            } else {
                                if (temperature[i] < 0) {
                                    temperature[i] = 0;
                                }
                                flow[i] = data * temperature[i] / sum;
                            }
                        }
                        for (int i = 0; i < l; i++) {
                            flow[i] += baseAve;
                        }
                    } else {//若预报融雪小于基础流量，基流
                        for (int i = 0; i < l; i++) {
                            flow[i] = baseAve;
                        }
                    }
                    System.arraycopy(flow, beforeHours, result, beforeHours, l - beforeHours);
                }
            }
        } else {
            for (int i = InputUtils.beforeHours; i < num; i++) {
                if (currentFlow <= baseAve) {
                    result[i] = currentFlow;
                } else {
                    result[i] = Math.max(currentFlow * Math.pow(0.99, i), baseAve);
                }
            }
        }
        return result;
    }

    private double Linear(PredictInputData start, PredictInputData end, Date time) {
        int l = timeUtils.duration(start.getDates(), end.getDates(), "小时");
        double df = l != 0 ? (end.getFlow() - start.getFlow()) / l : 0;
        int dt = time.before(start.getDates()) ? 0 : timeUtils.duration(start.getDates(), time, "小时");
        return start.getFlow() + df * dt;
    }
}
