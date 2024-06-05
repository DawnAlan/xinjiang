package com.cj.model.func.modular.FloodPredict.model.function;


import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiModel;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
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

    String floodComposition;//洪水组成

    DataUtils dataUtils = new DataUtils();

    TimeUtils timeUtils = new TimeUtils();
    int beforeHours = InputUtils.beforeHours;//前期落地雨时间

    /**
     * 获得场次洪水预报数据
     */
    public List<Flood> getShortResult(ForecastInputParam param, List<Map<String,List<PredictInputData>>> Data, Object[][] snowData) {
        ShanbeiParam shanbeiparam;
        //各个雨量站的面积和汇流时间
        Map<String,Double> areaMap = new HashMap<>();
        areaMap.put("八一林场自动雨量站", 411.23);
        areaMap.put("东南沟自动雨量站", 186.29);
        areaMap.put("黑沟自动雨量站",52.72);
        areaMap.put("加普沙自动雨量站",176.21);
        areaMap.put("喀什沟自动雨量站",98.41);
        areaMap.put("煤矿沟自动雨量站",35.52);
        areaMap.put("萨尔达万自动雨量站",22.55);
        areaMap.put("无名沟自动雨量站",22.98);
        areaMap.put("宰尔德自动雨量站",50.67);
        areaMap.put("制材厂自动雨量站",137.44);
        areaMap.put("小渠子雨量站",85.0);
        areaMap.put("团结一队雨量站",85.0);
        areaMap.put("头屯河水库雨量站",85.0);
        Map<String,Integer> lMap = new HashMap<>();
        lMap.put("八一林场自动雨量站", 14);
        lMap.put("东南沟自动雨量站", 14);
        lMap.put("黑沟自动雨量站",6);
        lMap.put("加普沙自动雨量站",18);
        lMap.put("喀什沟自动雨量站",4);
        lMap.put("煤矿沟自动雨量站",10);
        lMap.put("萨尔达万自动雨量站",10);
        lMap.put("无名沟自动雨量站",12);
        lMap.put("宰尔德自动雨量站",12);
        lMap.put("制材厂自动雨量站",2);
        lMap.put("小渠子雨量站",4);
        lMap.put("团结一队雨量站",2);
        lMap.put("头屯河水库雨量站",2);
        //陕北模型输入、蒸散发和前期雨量
        List<PredictInputData> PreFlow = Data.get(0).get("流量");
        List<PredictInputData> bylch = Data.get(1).get("八一林场自动雨量站");//蒸散发和降雨
        List<PredictInputData> bylcd = Data.get(2).get("八一林场自动雨量站");//前期雨量
        List<PredictInputData> dngh = Data.get(1).get("东南沟自动雨量站");//蒸散发和降雨
        List<PredictInputData> dngd = Data.get(2).get("东南沟自动雨量站");//前期雨量
        List<PredictInputData> hgh = Data.get(1).get("黑沟自动雨量站");//蒸散发和降雨
        List<PredictInputData> hgd = Data.get(2).get("黑沟自动雨量站");//前期雨量
        List<PredictInputData> jpsh = Data.get(1).get("加普沙自动雨量站");//蒸散发和降雨
        List<PredictInputData> jpsd = Data.get(2).get("加普沙自动雨量站");//前期雨量
        List<PredictInputData> ksgh = Data.get(1).get("喀什沟自动雨量站");//蒸散发和降雨
        List<PredictInputData> ksgd = Data.get(2).get("喀什沟自动雨量站");//前期雨量
        List<PredictInputData> mkgh = Data.get(1).get("煤矿沟自动雨量站");//蒸散发和降雨
        List<PredictInputData> mkgd = Data.get(2).get("煤矿沟自动雨量站");//前期雨量
        List<PredictInputData> sedwh = Data.get(1).get("萨尔达万自动雨量站");//蒸散发和降雨
        List<PredictInputData> sedwd = Data.get(2).get("萨尔达万自动雨量站");//前期雨量
        List<PredictInputData> wmgh = Data.get(1).get("无名沟自动雨量站");//蒸散发和降雨
        List<PredictInputData> wmgd = Data.get(2).get("无名沟自动雨量站");//前期雨量
        List<PredictInputData> zedh = Data.get(1).get("宰尔德自动雨量站");//蒸散发和降雨
        List<PredictInputData> zedd = Data.get(2).get("宰尔德自动雨量站");//前期雨量
        List<PredictInputData> zcch = Data.get(1).get("制材厂自动雨量站");//蒸散发和降雨
        List<PredictInputData> zccd = Data.get(2).get("制材厂自动雨量站");//前期雨量
        List<PredictInputData> xqzh = Data.get(1).get("小渠子雨量站");//蒸散发和降雨
        List<PredictInputData> xqzd = Data.get(2).get("小渠子雨量站");//前期雨量
        List<PredictInputData> tjydh = Data.get(1).get("团结一队雨量站");//蒸散发和降雨
        List<PredictInputData> tjydd = Data.get(2).get("团结一队雨量站");//前期雨量
        List<PredictInputData> tthh = Data.get(1).get("头屯河水库雨量站");//蒸散发和降雨
        List<PredictInputData> tthd = Data.get(2).get("头屯河水库雨量站");//前期雨量
        Map<String,double[]> flow = new HashMap<>();
        int l = param.getPeriodStepNumber()*param.getPeriodStepSize();
        double[] rainQ = new double[l];
        switch (param.getLocation()) {
            case "3号桥": {
                shanbeiparam = param.getParamMap().get(param.getLocation());
                shanbeiparam.setFB(0.008);//后续需改
                shanbeiparam.setWM(150.0);
                shanbeiparam.setKC(1.0);
                shanbeiparam.setFC(20.0);
                shanbeiparam.setFM(60.0);
                shanbeiparam.setK(0.2);
                shanbeiparam.setB(0.3);
                shanbeiparam.setCS(0.96);
                shanbeiparam.setArea(areaMap.get("八一林场自动雨量站"));
                shanbeiparam.setL(lMap.get("八一林场自动雨量站") - 2);
                double[] bylcq = getSubBasinQ(shanbeiparam, bylch, bylcd);
                flow.put("八一林场自动雨量站", bylcq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += bylcq[i];
                }
                shanbeiparam.setArea(areaMap.get("东南沟自动雨量站"));
                shanbeiparam.setL(lMap.get("东南沟自动雨量站") - 2);
                double[] dngq = getSubBasinQ(shanbeiparam, dngh, dngd);
                flow.put("东南沟自动雨量站", dngq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += dngq[i];
                }
                shanbeiparam.setArea(areaMap.get("加普沙自动雨量站"));
                shanbeiparam.setL(lMap.get("加普沙自动雨量站") - 2);
                double[] jpsq = getSubBasinQ(shanbeiparam, jpsh, jpsd);
                flow.put("加普沙自动雨量站", jpsq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += jpsq[i];
                }
                shanbeiparam.setArea(areaMap.get("煤矿沟自动雨量站"));
                shanbeiparam.setL(lMap.get("煤矿沟自动雨量站") - 2);
                double[] mkgq = getSubBasinQ(shanbeiparam, mkgh, mkgd);
                flow.put("煤矿沟自动雨量站", mkgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += mkgq[i];
                }
                shanbeiparam.setArea(areaMap.get("萨尔达万自动雨量站"));
                shanbeiparam.setL(lMap.get("萨尔达万自动雨量站") - 2);
                double[] sedwq = getSubBasinQ(shanbeiparam, sedwh, sedwd);
                flow.put("萨尔达万自动雨量站", sedwq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += sedwq[i];
                }
                shanbeiparam.setArea(areaMap.get("无名沟自动雨量站"));
                shanbeiparam.setL(lMap.get("无名沟自动雨量站") - 2);
                double[] wmgq = getSubBasinQ(shanbeiparam, wmgh, wmgd);
                flow.put("无名沟自动雨量站", wmgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += wmgq[i];
                }
                shanbeiparam.setArea(areaMap.get("宰尔德自动雨量站"));
                shanbeiparam.setL(lMap.get("宰尔德自动雨量站") - 2);
                double[] zedq = getSubBasinQ(shanbeiparam, zedh, zedd);
                flow.put("宰尔德自动雨量站", zedq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += zedq[i];
                }
                break;
            }
            case "楼庄子": {
                shanbeiparam = param.getParamMap().get(param.getLocation());
                shanbeiparam.setFB(0.008);//后续需改

                shanbeiparam.setWM(150.0);
                shanbeiparam.setKC(1.0);
                shanbeiparam.setFC(20.0);
                shanbeiparam.setFM(60.0);
                shanbeiparam.setK(0.2);
                shanbeiparam.setB(0.3);
                shanbeiparam.setCS(0.96);
                shanbeiparam.setArea(areaMap.get("八一林场自动雨量站"));
                shanbeiparam.setL(lMap.get("八一林场自动雨量站"));
                double[] bylcq = getSubBasinQ(shanbeiparam, bylch, bylcd);
                flow.put("八一林场自动雨量站", bylcq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += bylcq[i];
                }
                shanbeiparam.setArea(areaMap.get("东南沟自动雨量站"));
                shanbeiparam.setL(lMap.get("东南沟自动雨量站"));
                double[] dngq = getSubBasinQ(shanbeiparam, dngh, dngd);
                flow.put("东南沟自动雨量站", dngq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += dngq[i];
                }
                shanbeiparam.setArea(areaMap.get("黑沟自动雨量站"));
                shanbeiparam.setL(lMap.get("黑沟自动雨量站"));
                double[] hgq = getSubBasinQ(shanbeiparam, hgh, hgd);
                flow.put("黑沟自动雨量站", hgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += hgq[i];
                }
                shanbeiparam.setArea(areaMap.get("加普沙自动雨量站"));
                shanbeiparam.setL(lMap.get("加普沙自动雨量站"));
                double[] jpsq = getSubBasinQ(shanbeiparam, jpsh, jpsd);
                flow.put("加普沙自动雨量站", jpsq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += jpsq[i];
                }
                shanbeiparam.setArea(areaMap.get("喀什沟自动雨量站"));
                shanbeiparam.setL(lMap.get("喀什沟自动雨量站"));
                double[] ksgq = getSubBasinQ(shanbeiparam, ksgh, ksgd);
                flow.put("喀什沟自动雨量站", ksgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += ksgq[i];
                }
                shanbeiparam.setArea(areaMap.get("煤矿沟自动雨量站"));
                shanbeiparam.setL(lMap.get("煤矿沟自动雨量站"));
                double[] mkgq = getSubBasinQ(shanbeiparam, mkgh, mkgd);
                flow.put("煤矿沟自动雨量站", mkgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += mkgq[i];
                }
                shanbeiparam.setArea(areaMap.get("萨尔达万自动雨量站"));
                shanbeiparam.setL(lMap.get("萨尔达万自动雨量站"));
                double[] sedwq = getSubBasinQ(shanbeiparam, sedwh, sedwd);
                flow.put("萨尔达万自动雨量站", sedwq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += sedwq[i];
                }
                shanbeiparam.setArea(areaMap.get("无名沟自动雨量站"));
                shanbeiparam.setL(lMap.get("无名沟自动雨量站"));
                double[] wmgq = getSubBasinQ(shanbeiparam, wmgh, wmgd);
                flow.put("无名沟自动雨量站", wmgq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += wmgq[i];
                }
                shanbeiparam.setArea(areaMap.get("宰尔德自动雨量站"));
                shanbeiparam.setL(lMap.get("宰尔德自动雨量站"));
                double[] zedq = getSubBasinQ(shanbeiparam, zedh, zedd);
                flow.put("宰尔德自动雨量站", zedq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += zedq[i];
                }
                shanbeiparam.setArea(areaMap.get("制材厂自动雨量站"));
                shanbeiparam.setL(lMap.get("制材厂自动雨量站"));
                double[] zccq = getSubBasinQ(shanbeiparam, zcch, zccd);
                flow.put("制材厂自动雨量站", zccq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += zccq[i];
                }
                break;
            }
            case "楼头区间":
                shanbeiparam = param.getParamMap().get(param.getLocation());
                shanbeiparam.setFB(0.008);//后续需改

                shanbeiparam.setWM(150.0);
                shanbeiparam.setKC(1.0);
                shanbeiparam.setFC(20.0);
                shanbeiparam.setFM(60.0);
                shanbeiparam.setK(0.2);
                shanbeiparam.setB(0.3);
                shanbeiparam.setCS(0.96);
                shanbeiparam.setArea(areaMap.get("小渠子雨量站"));
                shanbeiparam.setL(lMap.get("小渠子雨量站"));
                double[] xqzq = getSubBasinQ(shanbeiparam, xqzh, xqzd);
                flow.put("小渠子雨量站", xqzq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += xqzq[i];
                }
                shanbeiparam.setArea(areaMap.get("团结一队雨量站"));
                shanbeiparam.setL(lMap.get("团结一队雨量站"));
                double[] tjydq = getSubBasinQ(shanbeiparam, tjydh, tjydd);
                flow.put("团结一队雨量站", tjydq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += tjydq[i];
                }
                shanbeiparam.setArea(areaMap.get("头屯河水库雨量站"));
                shanbeiparam.setL(lMap.get("头屯河水库雨量站"));
                double[] tthq = getSubBasinQ(shanbeiparam, tthh, tthd);
                flow.put("头屯河水库雨量站", tthq);
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += tthq[i];
                }
                break;
        }

        //获得径流序列包含了降水融雪地下水
        Object[][] shortFlow = mixedFlood(param, rainQ, Data, snowData);
        floodSource = getFloodSources(flow, param);//洪水来源
        floodComposition = getFloodComposition(param, PreFlow, rainQ, snowData);//洪水组成
        floodLevel = getFloodLevel(shortFlow, param.getLocation());//洪水等级
        //将Object转化为Flood类型
        List<PredictInputData> surface = dataUtils.pointToSurface(Data.get(1), param.getLocation());
        double[] surfaceRain = new double[l];
        for (int i = 0; i < surfaceRain.length; i++) {
            surfaceRain[i] = surface.get(i + beforeHours).getRainfall();
        }
        return setShortFlood(shortFlow, param, surfaceRain);
    }

    public double[] getSubBasinQ(ShanbeiParam param,List<PredictInputData> preData,List<PredictInputData> hisData){
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        //蒸发量、降雨量赋值
        Object[][] preREData = new Object[preData.size()][3];
        for (int i = 0; i < preData.size(); i++) {
            preREData[i][0] = preData.get(i).getDates();
            preREData[i][1] = preData.get(i).getTemperature();
            preREData[i][2] = preData.get(i).getRainfall();
        }
        preREData = dataUtils.temToEva(preREData);//将温度转为蒸发量
        //前期累计雨量
        Object[][] historyRData = new Object[hisData.size()][2];
        for (int i = 0; i < hisData.size(); i++) {
            historyRData[i][0] = hisData.get(i).getDates();
            historyRData[i][1] = hisData.get(i).getRainfall();
        }
        //模型计算过程
        shanBeiModel.InputData(param, preREData, historyRData);
        shanBeiModel.InitialMoistureContentCalculation();
        shanBeiModel.RunoffYieldCalculation_UnevenInfiltration();
        shanBeiModel.ConfluenceCalculation();
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
            ReqCurve reqCurve  = new ReqCurve();
            List<Option> lzzOutList = LZZ.Calculate(param.getBasinStr(),input, timeLength,reqCurve);
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = lzzOutList.get(i).getH1();
                water_outQ[i][1] = lzzOutList.get(i).getQOut();
                water_outQ[i][2] = (((double) water_outQ[i][0] > 1394.5) ? 1 : 0);
            }
        } else {
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = getWaterLevel(predict, param)[i];
                water_outQ[i][1] = predict[i * l][1];
                water_outQ[i][2] = (((double) predict[i][1] > 60.0) ? 1 : 0);
            }
        }
//        for (int i = 0; i < n; i++) {
//            water_outQ[i][0] = getWaterLevel(predict, param)[i];
//            water_outQ[i][1] = predict[i * l][1];
//            water_outQ[i][2] = (((double) predict[i][1] > 60.0) ? 1 : 0);
//        }
        //连续列的赋值
        for (int i = 0; i < n; i++) {
            Flood flood = new Flood();
            flood.setLocation(param.getLocation());//断面位置
            flood.setScale(String.valueOf(3600 * l));//尺度
            flood.setPeakIndex((Integer) floodIndex[i * l][0]);//洪号
            flood.setTime((Date) predict[i * l][0]);//时间
            flood.setPreQ(Math.round((double) predict[i * l][1] * 100.0) / 100.0);//预报流量
            flood.setPeakFlood((Double) floodNature[2][1]);//洪峰
            flood.setPeakTime((Date) floodNature[3][1]);//峰现时间
            flood.setPeakDuration((String) floodNature[1][1]);//洪峰持续时间
            flood.setFloodVolume((Double) floodNature[0][1]);//洪量
            flood.setQCause(floodSource);//洪水来源
            flood.setQComposition(floodComposition);//洪水组成
            flood.setRainProcess(Math.round(rain[i]) * 100 / 100.0);//雨情
            flood.setWaterLevel((double) water_outQ[i][0]);//相应水位
            flood.setOutQ((double) water_outQ[i][1]);//出库流量
            flood.setWarningTime((Integer) water_outQ[i][2]);//是否超过汛限水位
            flood.setFloodLevel(floodLevel);//洪水等级
            result.add(flood);
        }


        return result;
    }

    /**
     * 记录好的洪号，洪峰，峰现时间，持续时间，洪量
     */
    public List<Object[][]> getFloodInformation(Object[][] predict) {
        List<Object[][]> result = new ArrayList<>();
        if (predict.length==1){
            Object[][] flood = new Object[predict.length][3];
            flood[0][0]=0;
            flood[0][1]=predict[0][0];
            flood[0][2]=predict[0][1];
            Object[][] floodNature = new Object[4][2];
            floodNature[0][0] = "洪量";//万立方米
            floodNature[1][0] = "洪峰持续时间";
            floodNature[2][0] = "洪峰";
            floodNature[3][0] = "峰现时间";
            floodNature[0][1] = (double)predict[0][1]*3600*24*30/100000.0;//万立方米
            floodNature[1][1] = "1month";
            floodNature[2][1] = predict[0][1];
            floodNature[3][1] = predict[0][0];
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
        double line = (dt <= min * 0.6 ? 1000 : min + dt * 0.6);//洪水标准线
        for (int i = 0; i < predict.length; i++)//找到所有大于标准线的来水
        {
            if ((double) predict[i][1] > line) {
                flood[i][0] = 1;
                flood[i][1] = predict[i][0];//时间
                flood[i][2] = predict[i][1];//预报流量
            } else {
                flood[i][0] = 0;
                flood[i][1] = predict[i][0];//时间
                flood[i][2] = predict[i][1];//预报流量
            }
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
        /*
         * 以下为针对分好洪号后的洪水过程
         */
        Object[][] floodNature = new Object[4][2];
        floodNature[0][0] = "洪量";//万立方米
        floodNature[1][0] = "洪峰持续时间";
        floodNature[2][0] = "洪峰";
        floodNature[3][0] = "峰现时间";
        double Volume = 0.0;
        String duration;
//        String floodLevel = new String();
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
        //洪量
        for (Double aDouble : maxFlood) {
            Volume += aDouble * 3600 / 10000;//多少万立方米
        }
        Volume = Math.round(Volume * 100.0) / 100.0;
        floodNature[0][1] = Volume;
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
//        int t = 0;
        for (Double aDouble : maxFlood) {
            if (maxQ < aDouble) {
                maxQ = aDouble;
//                t++;
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
//        int temp = n + t - 1;
//        if (temp > 0) {
//            floodNature[3][1] = flood[temp][1];
//        } else {
//            floodNature[3][1] = flood[0][1];
//        }
        result.add(flood);
        result.add(floodNature);
        return result;
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
    public String getFloodSources(Map<String,double[]> pointData, ForecastInputParam param) {
        String result = "";
        int l = param.getPeriodStepNumber()*param.getPeriodStepSize();
        //三号桥断面返回三个地区的雨量比值
        switch (param.getLocation()) {
            case "3号桥": {
                double Sum;
                double qiaoSum = 0.0;
                double dongSum = 0.0;
                double sanSum = 0.0;
                for (int i = 0; i < l; i++) {
                    qiaoSum += pointData.get("八一林场自动雨量站")[i];
                    dongSum += (pointData.get("加普沙自动雨量站")[i] + pointData.get("东南沟自动雨量站")[i] +
                            pointData.get("宰尔德自动雨量站")[i] + pointData.get("无名沟自动雨量站")[i]);
                    sanSum += (pointData.get("萨尔达万自动雨量站")[i] + pointData.get("煤矿沟自动雨量站")[i]);
                }
                Sum = qiaoSum + dongSum + sanSum;
                if (Sum != 0.0) {
                    double qiao = Math.round((float) qiaoSum / Sum * 100) / 100.0;
                    double dong = Math.round((float) dongSum / Sum * 100) / 100.0;
                    double san = Math.round((1.00 - qiao - dong) * 100) / 100.0;
                    result = "乔楞格尔地区:" + qiao + "," + "东南沟地区:" + dong + "," + "3号桥地区:" + san;
                } else {
                    result = "乔楞格尔地区:0.34," + "东南沟地区:0.33," + "3号桥地区:0.33";
                }
                break;
            }
            case "楼庄子": {
                double Sum;
                double qiaoSum = 0.0;
                double dongSum = 0.0;
                double sanSum = 0.0;
                double zhiSum = 0.0;
                for (int i = 0; i < l; i++) {
                    qiaoSum += pointData.get("八一林场自动雨量站")[i];
                    dongSum += (pointData.get("加普沙自动雨量站")[i] + pointData.get("东南沟自动雨量站")[i] +
                            pointData.get("宰尔德自动雨量站")[i] + pointData.get("无名沟自动雨量站")[i]);
                    sanSum += (pointData.get("萨尔达万自动雨量站")[i] + pointData.get("煤矿沟自动雨量站")[i]);
                    zhiSum += (pointData.get("黑沟自动雨量站")[i] + pointData.get("喀什沟自动雨量站")[i] + pointData.get("制材厂自动雨量站")[i]);
                }
                Sum = qiaoSum + dongSum + sanSum + zhiSum;
                if (Sum != 0) {
                    double qiao = Math.round((float) qiaoSum / Sum * 100) / 100.0;
                    double dong = Math.round((float) dongSum / Sum * 100) / 100.0;
                    double san = Math.round((float) sanSum / Sum * 100) / 100.0;
                    double zhi = Math.round((1.00 - qiao - dong - san) * 100) / 100.0;
                    result = "乔楞格尔地区:" + qiao + "," + "东南沟地区:" + dong + "," + "3号桥地区:" + san + "," + "制材厂地区:" + zhi;
                } else {
                    result = "乔楞格尔地区:0.25," + "东南沟地区:0.25," + "3号桥地区:0.25," + "制材厂地区:0.25";
                }

                break;
            }
            case "楼头区间": {
                double Sum;
                double xiaoSum = 0.0;
                double tuanSum = 0.0;
                double toSum = 0.0;
                for (int i = 0; i < l; i++) {
                    xiaoSum += pointData.get("小渠子雨量站")[i];
                    tuanSum += pointData.get("团结一队雨量站")[i];
                    toSum += pointData.get("头屯河水库雨量站")[i];
                }
                Sum = xiaoSum + tuanSum + toSum;
                if (Sum != 0) {
                    double xiao = Math.round((float) xiaoSum / Sum * 100) / 100.0;
                    double tuan = Math.round((float) tuanSum / Sum * 100) / 100.0;
                    double to = Math.round((1.00 - xiao - tuan) * 100) / 100.0;
                    result = "小渠子沟:" + xiao + "," + "团结一队:" + tuan + "," + "头屯河坝前:" + to;
                } else {
                    result = "小渠子沟:0.34," + "团结一队:0.33," + "头屯河坝前:0.33";
                }
                break;
            }
        }
        return result;
    }

    /**
     * 求洪水来源
     * @param PreFlow   基础流量
     * @param Q_shanbei 降雨产生
     * @param snowData  融雪产生
     */
    public String getFloodComposition(ForecastInputParam param, List<PredictInputData> PreFlow, double[] Q_shanbei, Object[][] snowData) {
        String result = "";
        double snowFlow = 0.0;
        double preFlowSum = 0.0;
        double preFlow = 0.0;
        double shanbeiFlow = 0.0;
        int number = Q_shanbei.length;
        double base = (param.getLocation().equals("楼头区间")?0.16:1.28)*number;
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
                result += "地下水:1.00";
            }
            else {
                result += "降水:" + shanbei + "," + "融雪:" + rong + "," + "地下水:" + di;
            }
        }
        else {
            for (double v : Q_shanbei) {
                shanbeiFlow = shanbeiFlow + v;
            }
            for (PredictInputData predictInputData : PreFlow) {
                if (predictInputData.getFlow() == null) {
                    predictInputData.setFlow(0.0);
                }
                preFlow += predictInputData.getFlow();
            }
            int n = PreFlow.size() == 0 ? 1 : PreFlow.size();
            preFlow = preFlowSum / n * Q_shanbei.length;
            double Sum = preFlow + shanbeiFlow + base;
            double shanbei = Math.round((float) shanbeiFlow / Sum * 100) / 100.0;
            double rong = Math.round((float) preFlow / Sum * 100) / 100.0;
            double di = Math.round((float) base / Sum * 100) / 100.0;
            if (Sum == 0.0) {
                result += "地下水:1.00";
            } else {
                result += "降水:" + shanbei + "," + "融雪:" + rong + "," + "地下水:" + di;
            }
        }
        return result;
    }

    /**
     * 陕北模型计算所得降水数据与前期径流数据、融雪数据整合
     * @param shanBeiQ 降水所得
     * @param snowFlow 融雪径流
     * @return 预报的径流值
     */
    public Object[][] mixedFlood(ForecastInputParam param, double[] shanBeiQ, List<Map<String,List<PredictInputData>>> Data, Object[][] snowFlow) {
        List<PredictInputData> preFlow = Data.get(0).get("流量");
        List<PredictInputData> preTemperature = Data.get(1).get("制材厂自动雨量站");
        int l = param.getPeriodStepNumber()* param.getPeriodStepSize();
        Object[][] result = new Object[l][2];
        //减去汇流滞时
        Date currentDate = param.getPreStartTime();
        Date[][] dates = timeUtils.getDateList(currentDate, l, 0, 1);
        //基础流量
        Double baseAve;
        if (preFlow.size() != 0) {
            // 前期径流求均值
//            for (int j = 0; j < preFlow.size(); j++) {
//                Double baseFlow = 0.0;
//                if (preFlow.get(j).getFlow() != null) {
//                    baseFlow = preFlow.get(j).getFlow();
//                } else {
//                    baseFlow = 0.0;
//                }
//                baseAve += baseFlow;
//            }
//            baseAve = baseAve / preFlow.size();
            //直接用前一天径流作为前期流量
            int a = 0;
            for (PredictInputData predictInputData : preFlow) {
                if (currentDate.after(predictInputData.getDates())) {
                    a++;
                }
            }
            if (a==preFlow.size()){
                a--;
            }
            baseAve = preFlow.get(a).getFlow();
        } else {
            baseAve = 100.0;
        }

        if (baseAve > 50)//基础径流偏大
        {
            int month = timeUtils.getSpecificDate(param.getPreStartTime()).get("月");
            String location = param.getLocation();
            if (!location.equals("楼头区间")) {
                double[] baseFlow = new double[]{1.29,1.19,1.77,2.78,5.0,5.49,6.5,6.7,4.18,2.5,2.23,1.52};
                baseAve = baseFlow[month-1];
            }
            else {
                baseAve = 1.29;
            }
        }
        double[] snowDistribution = flowDistribution(param, snowFlow, baseAve, preTemperature);//融雪随温度分配曲线
        //获得混合后的径流序列
        for (int i = 0; i < shanBeiQ.length; i++) {
            result[i][0] = dates[i][0];
            if (param.getIsSnowMeltModel()) {
                result[i][1] = shanBeiQ[i] + snowDistribution[i];//将陕北模型和融雪模型结果相加
            } else {
                result[i][1] = shanBeiQ[i] + baseAve;//降水加上前期径流
            }
        }
        return result;
    }

    /**
     * 融雪径流减去基流后，根据温度进行分布
     */
    public double[] flowDistribution(ForecastInputParam param, Object[][] snowFlow, Double baseData, List<PredictInputData> input) {
        //融雪基流
        double averageData;
        double snowSum = 0.0;

        for (Object[] objects : snowFlow) {
            snowSum = snowSum + (double) objects[1];
        }
        averageData = snowSum / snowFlow.length;

        int l = param.getPeriodStepNumber()*param.getPeriodStepSize() + beforeHours;
        double[] flow = new double[l];
        double[] result = new double[param.getPeriodStepNumber()*param.getPeriodStepSize()];
        if (param.getLocation().equals("楼头区间")){
            if (l - beforeHours >= 0)
                System.arraycopy(flow, beforeHours, result, 0, l - beforeHours);
            return result;
        }
        double[] temperature = new double[l];
        for (int i = 0; i < l; i++) {
            temperature[i] = input.get(i).getTemperature();
        }
        int number = 0;
        for (int i = 0; i < temperature.length; i++) {
            if (!(temperature[i] >= 5)) {
                temperature[i] = 0.0;
            }
            else {
                number++;
            }
        }
        double sum = 0;
        for (double num : temperature) {
            sum += num;
        }

        if (number == 0) {//如果没有超过5摄氏度
            for (int i = 0; i < param.getPeriodStepNumber()*param.getPeriodStepSize(); i++) {
                result[i] = baseData;
            }
            return result;
        }
        if (averageData > baseData) {//若预报融雪大于基础流量，先去除基流影响再按温度分布
            double data = (averageData - baseData);
            for (int i = 0; i < l; i++) {
                if (sum == 0) {
                    flow[i] = data;
                }
                else {
                    if (temperature[i] < 0) {
                        temperature[i] = 0;
                    }
                    flow[i] = data * temperature[i] / sum;
                }
            }
            for (int i = 0; i < l; i++) {
                flow[i] += baseData;
            }
        } else {//若预报融雪小于基础流量，基流
            for (int i = 0; i < l; i++) {
                flow[i] = baseData;
            }
        }
        if (l - beforeHours >= 0)
            System.arraycopy(flow, beforeHours, result, 0, l - beforeHours);
        return result;
    }


}
