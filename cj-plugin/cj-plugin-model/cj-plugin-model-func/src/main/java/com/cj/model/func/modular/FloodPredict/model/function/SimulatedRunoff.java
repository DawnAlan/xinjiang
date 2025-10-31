package com.cj.model.func.modular.FloodPredict.model.function;

import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiModel;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;
import com.cj.model.func.modular.FloodPredict.entity.RainFallDto;
import com.cj.model.func.modular.FloodPredict.utils.InputUtils;
import com.cj.model.func.modular.FloodPredict.utils.TimeUtils;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.*;

public class SimulatedRunoff {
    TimeUtils tu = new TimeUtils();
    String floodLevel = "一年一遇";//洪水等级
    String floodSource;//洪水来源
    String floodTime;//洪水传播时间
    String floodComposition;//洪水组成
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<RainFallDto> bylch = new ArrayList<>();
    List<RainFallDto> dngh = new ArrayList<>();
    List<RainFallDto> hgh = new ArrayList<>();
    List<RainFallDto> jpsh = new ArrayList<>();
    List<RainFallDto> ksgh = new ArrayList<>();
    List<RainFallDto> mkgh = new ArrayList<>();
    List<RainFallDto> sedwh = new ArrayList<>();
    List<RainFallDto> wmgh = new ArrayList<>();
    List<RainFallDto> zedh = new ArrayList<>();
    List<RainFallDto> zcch = new ArrayList<>();
    List<RainFallDto> xqzh = new ArrayList<>();
    List<RainFallDto> tjydh = new ArrayList<>();
    List<RainFallDto> ggh = new ArrayList<>();
    List<RainFallDto> hjgh = new ArrayList<>();
    public List<Flood> simulation(ForecastInputParam param) {
        double preFlow = param.getPreFlow();
        double preRain = param.getPreRainFall();
        List<RainFallDto> rain = param.getRainFallDtos();
        int l = param.getPeriodStepNumber();
        for (RainFallDto rainFallDto : rain) {
            if (rainFallDto.getArea().equals("八一林场自动雨量站")) {
                bylch.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("东南沟自动雨量站")) {
                dngh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("黑沟自动雨量站")) {
                hgh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("加普沙自动雨量站")) {
                jpsh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("喀什沟自动雨量站")) {
                ksgh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("煤矿沟自动雨量站")) {
                mkgh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("萨尔达万自动雨量站")) {
                sedwh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("无名沟自动雨量站")) {
                wmgh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("宰尔德自动雨量站")) {
                zedh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("制材厂自动雨量站")) {
                zcch.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("小渠子雨量站")) {
                xqzh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("团结一队雨量站")) {
                tjydh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("甘沟雨量站")) {
                ggh.add(rainFallDto);
            }
            if (rainFallDto.getArea().equals("头屯河水库雨量站")) {
                hjgh.add(rainFallDto);
            }
        }
        int n = bylch.size();
        //对降雨时段进行更改
        if (l > n){
            RainFallDto suppleData = new RainFallDto();
            suppleData.setArea("补充数据");
            suppleData.setTemperature(0.0);
            suppleData.setRainFall(0.0);
            Date date = tu.addCalendar(param.getPreStartTime(),"小时", n);
            for (int i = 0; i < l - n; i++) {
                suppleData.setDate(sdf.format(tu.addCalendar(date,"小时",i)));
                bylch.add(suppleData);
                dngh.add(suppleData);
                hgh.add(suppleData);
                jpsh.add(suppleData);
                ksgh.add(suppleData);
                mkgh.add(suppleData);
                sedwh.add(suppleData);
                wmgh.add(suppleData);
                zedh.add(suppleData);
                zcch.add(suppleData);
                xqzh.add(suppleData);
                tjydh.add(suppleData);
                ggh.add(suppleData);
                hjgh.add(suppleData);
            }
        }
        //参数赋值
        ShanbeiParam shanbeiparam = new ShanbeiParam();
        shanbeiparam.setFB(0.008);//后续需改
        shanbeiparam.setWM(250.0);
        shanbeiparam.setKC(1.0);
        shanbeiparam.setFC(20.0);
        shanbeiparam.setFM(80.0);
        shanbeiparam.setK(0.2);
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
        areaMap.put("小渠子雨量站",44.04);
        areaMap.put("团结一队雨量站",82.56);
        areaMap.put("甘沟雨量站",75.84);
        areaMap.put("头屯河水库雨量站",56.54);
        Map<String,Integer> lMap = new HashMap<>();
        lMap.put("八一林场自动雨量站", 12);
        lMap.put("东南沟自动雨量站", 10);
        lMap.put("黑沟自动雨量站",1);
        lMap.put("加普沙自动雨量站",12);
        lMap.put("喀什沟自动雨量站",1);
        lMap.put("煤矿沟自动雨量站",4);
        lMap.put("萨尔达万自动雨量站",4);
        lMap.put("无名沟自动雨量站",6);
        lMap.put("宰尔德自动雨量站",6);
        lMap.put("制材厂自动雨量站",0);
        lMap.put("小渠子雨量站",3);
        lMap.put("团结一队雨量站",2);
        lMap.put("甘沟雨量站",1);
        lMap.put("头屯河水库雨量站",0);
        Map<String, Double> csMap = new HashMap<>();
        csMap.put("八一林场自动雨量站", 0.85);
        csMap.put("东南沟自动雨量站", 0.85);
        csMap.put("黑沟自动雨量站", 0.6);
        csMap.put("加普沙自动雨量站", 0.85);
        csMap.put("喀什沟自动雨量站", 0.6);
        csMap.put("煤矿沟自动雨量站", 0.7);
        csMap.put("萨尔达万自动雨量站", 0.7);
        csMap.put("无名沟自动雨量站", 0.7);
        csMap.put("宰尔德自动雨量站", 0.7);
        csMap.put("制材厂自动雨量站", 0.6);
        csMap.put("小渠子雨量站", 0.8);
        csMap.put("团结一队雨量站", 0.8);
        csMap.put("甘沟雨量站", 0.8);
        csMap.put("头屯河水库雨量站", 0.8);
        Map<String, Double> bMap = new HashMap<>();
        bMap.put("八一林场自动雨量站", 0.2);
        bMap.put("东南沟自动雨量站", 0.2);
        bMap.put("黑沟自动雨量站", 0.1);
        bMap.put("加普沙自动雨量站", 0.2);
        bMap.put("喀什沟自动雨量站", 0.1);
        bMap.put("煤矿沟自动雨量站", 0.1);
        bMap.put("萨尔达万自动雨量站", 0.1);
        bMap.put("无名沟自动雨量站", 0.1);
        bMap.put("宰尔德自动雨量站", 0.1);
        bMap.put("制材厂自动雨量站", 0.2);
        bMap.put("小渠子雨量站", 0.1);
        bMap.put("团结一队雨量站", 0.1);
        bMap.put("甘沟雨量站", 0.1);
        bMap.put("头屯河水库雨量站", 0.1);
        Map<String,double[]> flow = new HashMap<>();
        Map<String, Integer> lTime = new HashMap<>();
        double[] rainQ = new double[l];
        switch (param.getLocation()) {
            case "3号桥":
            case "楼庄子":
            {
                shanbeiparam.setB(bMap.get("八一林场自动雨量站"));
                shanbeiparam.setCS(csMap.get("八一林场自动雨量站"));
                shanbeiparam.setArea(areaMap.get("八一林场自动雨量站"));
                shanbeiparam.setL(lMap.get("八一林场自动雨量站"));
                double[] bylcq = getSubBasinQ(shanbeiparam, bylch, preRain);
                flow.put("八一林场自动雨量站", bylcq);
                lTime.put("八一林场自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += bylcq[i];
                }
                shanbeiparam.setB(bMap.get("东南沟自动雨量站"));
                shanbeiparam.setCS(csMap.get("东南沟自动雨量站"));
                shanbeiparam.setArea(areaMap.get("东南沟自动雨量站"));
                shanbeiparam.setL(lMap.get("东南沟自动雨量站"));
                double[] dngq = getSubBasinQ(shanbeiparam, dngh, preRain);
                flow.put("东南沟自动雨量站", dngq);
                lTime.put("东南沟自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += dngq[i];
                }
                shanbeiparam.setB(bMap.get("加普沙自动雨量站"));
                shanbeiparam.setCS(csMap.get("加普沙自动雨量站"));
                shanbeiparam.setArea(areaMap.get("加普沙自动雨量站"));
                shanbeiparam.setL(lMap.get("加普沙自动雨量站"));
                double[] jpsq = getSubBasinQ(shanbeiparam, jpsh, preRain);
                flow.put("加普沙自动雨量站", jpsq);
                lTime.put("加普沙自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += jpsq[i];
                }
                shanbeiparam.setB(bMap.get("煤矿沟自动雨量站"));
                shanbeiparam.setCS(csMap.get("煤矿沟自动雨量站"));
                shanbeiparam.setArea(areaMap.get("煤矿沟自动雨量站"));
                shanbeiparam.setL(lMap.get("煤矿沟自动雨量站"));
                double[] mkgq = getSubBasinQ(shanbeiparam, mkgh, preRain);
                flow.put("煤矿沟自动雨量站", mkgq);
                lTime.put("煤矿沟自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += mkgq[i];
                }
                shanbeiparam.setB(bMap.get("萨尔达万自动雨量站"));
                shanbeiparam.setCS(csMap.get("萨尔达万自动雨量站"));
                shanbeiparam.setArea(areaMap.get("萨尔达万自动雨量站"));
                shanbeiparam.setL(lMap.get("萨尔达万自动雨量站"));
                double[] sedwq = getSubBasinQ(shanbeiparam, sedwh, preRain);
                flow.put("萨尔达万自动雨量站", sedwq);
                lTime.put("萨尔达万自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += sedwq[i];
                }
                shanbeiparam.setB(bMap.get("无名沟自动雨量站"));
                shanbeiparam.setCS(csMap.get("无名沟自动雨量站"));
                shanbeiparam.setArea(areaMap.get("无名沟自动雨量站"));
                shanbeiparam.setL(lMap.get("无名沟自动雨量站"));
                double[] wmgq = getSubBasinQ(shanbeiparam, wmgh, preRain);
                flow.put("无名沟自动雨量站", wmgq);
                lTime.put("无名沟自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += wmgq[i];
                }
                shanbeiparam.setB(bMap.get("宰尔德自动雨量站"));
                shanbeiparam.setCS(csMap.get("宰尔德自动雨量站"));
                shanbeiparam.setArea(areaMap.get("宰尔德自动雨量站"));
                shanbeiparam.setL(lMap.get("宰尔德自动雨量站"));
                double[] zedq = getSubBasinQ(shanbeiparam, zedh, preRain);
                flow.put("宰尔德自动雨量站", zedq);
                lTime.put("宰尔德自动雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += zedq[i];
                }
                if (param.getLocation().equals("楼庄子")){
                    shanbeiparam.setB(bMap.get("喀什沟自动雨量站"));
                    shanbeiparam.setCS(csMap.get("喀什沟自动雨量站"));
                    shanbeiparam.setArea(areaMap.get("喀什沟自动雨量站"));
                    shanbeiparam.setL(lMap.get("喀什沟自动雨量站"));
                    double[] ksgq = getSubBasinQ(shanbeiparam, ksgh, preRain);
                    flow.put("喀什沟自动雨量站", ksgq);
                    lTime.put("喀什沟自动雨量站",Math.max(shanbeiparam.getL(),0));
                    for (int i = 0; i < rainQ.length; i++) {
                        rainQ[i] += ksgq[i];
                    }
                    shanbeiparam.setB(bMap.get("黑沟自动雨量站"));
                    shanbeiparam.setCS(csMap.get("黑沟自动雨量站"));
                    shanbeiparam.setArea(areaMap.get("黑沟自动雨量站"));
                    shanbeiparam.setL(lMap.get("黑沟自动雨量站"));
                    double[] hgq = getSubBasinQ(shanbeiparam, hgh, preRain);
                    flow.put("黑沟自动雨量站", hgq);
                    lTime.put("黑沟自动雨量站",Math.max(shanbeiparam.getL(),0));
                    for (int i = 0; i < rainQ.length; i++) {
                        rainQ[i] += hgq[i];
                    }
                    shanbeiparam.setB(bMap.get("制材厂自动雨量站"));
                    shanbeiparam.setCS(csMap.get("制材厂自动雨量站"));
                    shanbeiparam.setArea(areaMap.get("制材厂自动雨量站"));
                    shanbeiparam.setL(lMap.get("制材厂自动雨量站"));
                    double[] zccq = getSubBasinQ(shanbeiparam, zcch, preRain);
                    flow.put("制材厂自动雨量站", zccq);
                    lTime.put("制材厂自动雨量站",Math.max(shanbeiparam.getL(),0));
                    for (int i = 0; i < rainQ.length; i++) {
                        rainQ[i] += zccq[i];
                    }
                }
                break;
            }
            case "楼头区间":
                shanbeiparam.setWM(180.0);
                shanbeiparam.setB(csMap.get("小渠子雨量站"));
                shanbeiparam.setCS(csMap.get("小渠子雨量站"));
                shanbeiparam.setArea(areaMap.get("小渠子雨量站"));
                shanbeiparam.setL(lMap.get("小渠子雨量站"));
                double[] xqzq = getSubBasinQ(shanbeiparam, xqzh, preRain);
                flow.put("小渠子雨量站", xqzq);
                lTime.put("小渠子雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += xqzq[i];
                }
                shanbeiparam.setB(csMap.get("团结一队雨量站"));
                shanbeiparam.setCS(csMap.get("团结一队雨量站"));
                shanbeiparam.setArea(areaMap.get("团结一队雨量站"));
                shanbeiparam.setL(lMap.get("团结一队雨量站"));
                double[] tjydq = getSubBasinQ(shanbeiparam, tjydh, preRain);
                flow.put("团结一队雨量站", tjydq);
                lTime.put("团结一队雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += tjydq[i];
                }
                shanbeiparam.setB(csMap.get("甘沟雨量站"));
                shanbeiparam.setCS(csMap.get("甘沟雨量站"));
                shanbeiparam.setArea(areaMap.get("甘沟雨量站"));
                shanbeiparam.setL(lMap.get("甘沟雨量站"));
                double[] ggq = getSubBasinQ(shanbeiparam, ggh, preRain);
                flow.put("甘沟雨量站", ggq);
                lTime.put("甘沟雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += ggq[i];
                }
                shanbeiparam.setB(csMap.get("头屯河水库雨量站"));
                shanbeiparam.setCS(csMap.get("头屯河水库雨量站"));
                shanbeiparam.setArea(areaMap.get("头屯河水库雨量站"));
                shanbeiparam.setL(lMap.get("头屯河水库雨量站"));
                double[] hjgq = getSubBasinQ(shanbeiparam, hjgh, preRain);
                flow.put("头屯河水库雨量站", hjgq);
                lTime.put("头屯河水库雨量站",Math.max(shanbeiparam.getL(),0));
                for (int i = 0; i < rainQ.length; i++) {
                    rainQ[i] += hjgq[i];
                }
                break;
        }
        //获得径流序列包含了降水融雪地下水
        Object[][] shortFlow = mixedFlood(rainQ,preFlow,param.getPreStartTime());
        floodSource = new SubBasinForecast().getFloodSources(flow,param);//洪水来源
        floodComposition = getFloodComposition(param, preFlow, rainQ);//洪水组成
        floodLevel = new SubBasinForecast().getFloodLevel(shortFlow, param.getLocation());//洪水等级
        Map<String, Integer> updatedMap = new HashMap<>();
        for (Map.Entry<String, Integer> mapEntry : lTime.entrySet()) {
            String key = mapEntry.getKey();
            Integer value = mapEntry.getValue();
            switch (floodLevel) {
                case "二十年一遇":
                case "五十年一遇":
                    updatedMap.put(key, value - 1);
                    break;
                case "百年一遇":
                    updatedMap.put(key, value - 2);
                    break;
                case "千年一遇":
                    updatedMap.put(key, value - 3);
                    break;
            }
        }
        lTime.putAll(updatedMap); // 更新原始 Map
        floodTime = new SubBasinForecast().getFloodTime(lTime);//洪水汇流时间
        //将Object转化为Flood类型
        double[] surface = pointToSurface(param);
        return setShortFlood(shortFlow, param, surface);
    }

    @SneakyThrows
    public double[] getSubBasinQ(ShanbeiParam param, List<RainFallDto> preData, Double preRain){
        ShanBeiModel shanBeiModel = new ShanBeiModel();
        //蒸发量、降雨量赋值
        Object[][] preRE = new Object[preData.size()][3];
        for (int i = 0; i < preData.size(); i++) {
            preRE[i][0] = sdf.parse(preData.get(i).getDate());
            preRE[i][1] = 0.2;
            preRE[i][2] = preData.get(i).getRainFall();
        }
        //前期累计雨量
        Object[][] hisRain = new Object[20][2];
        for (int i = 0; i < hisRain.length; i++) {
            hisRain[i][0] = "模拟降雨前期时间";
            hisRain[i][1] = preRain/ hisRain.length;
        }
        //模型计算过程
        shanBeiModel.InputData(param, preRE, hisRain);
        shanBeiModel.InitialMoistureContentCalculation();
        shanBeiModel.RunoffYieldCalculation_UnevenInfiltration();
        shanBeiModel.ConfluenceCalculation2();
        return shanBeiModel.Q;
    }

    public double[] pointToSurface(ForecastInputParam param){
        int l = param.getPeriodStepNumber();
        double[] result = new double[l];
        switch (param.getLocation()) {
            case "3号桥":
                for (int i = 0; i < l; i++) {
                    result[i] = bylch.get(i).getRainFall() * 0.34 + dngh.get(i).getRainFall() * 0.16 + jpsh.get(i).getRainFall() * 0.15 +
                            wmgh.get(i).getRainFall() * 0.019 + mkgh.get(i).getRainFall() * 0.029 + zedh.get(i).getRainFall() * 0.042 + sedwh.get(i).getRainFall() * 0.019;
                }
                break;
            case "楼庄子":
                for (int i = 0; i < l; i++) {
                    result[i] = bylch.get(i).getRainFall() * 0.34 + dngh.get(i).getRainFall() * 0.16 + jpsh.get(i).getRainFall() * 0.15 +
                            wmgh.get(i).getRainFall() * 0.019 + mkgh.get(i).getRainFall() * 0.029 + zedh.get(i).getRainFall() * 0.042 +
                            sedwh.get(i).getRainFall() * 0.019 + ksgh.get(i).getRainFall() * 0.08 + zcch.get(i).getRainFall() * 0.12 + hgh.get(i).getRainFall() * 0.04;
                }
                break;
            case "楼头区间":
                for (int i = 0; i < l; i++) {
                    result[i] = tjydh.get(i).getRainFall() * 0.17 + hjgh.get(i).getRainFall() * 0.32 + xqzh.get(i).getRainFall() * 0.29 + ggh.get(i).getRainFall() * 0.22;
                }
                break;
        }
        return result;
    }

    public Object[][] mixedFlood(double[] Q,double preFlow,Date date){
        Object[][] result = new Object[Q.length][2];
        for (int i = 0; i < Q.length; i++) {
            result[i][0] = tu.addCalendar(date,"小时",i);
            result[i][1] = Q[i] + preFlow;
        }
        return result;
    }

    /**
     * 求洪水来源
     * @param preFlow   基础流量
     * @param Q 降雨产生
     */
    public String getFloodComposition(ForecastInputParam param, double preFlow, double[] Q) {
        String result = "";
        double shanbeiFlow = 0.0;
        double preFlowSum;
        int number = Q.length;
        double base = (param.getLocation().equals("楼头区间")?0.16:1.28)*number;
        for (double v : Q) {
            shanbeiFlow += v;
        }

        preFlowSum = preFlow * Q.length;
        double Sum = preFlowSum + shanbeiFlow + base;
        double shanbei = Math.round((float) shanbeiFlow / Sum * 100) / 100.0;
        double rong = Math.round((float) preFlowSum / Sum * 100) / 100.0;
        double di = Math.round((float) base / Sum * 100) / 100.0;
        if (Sum == 0.0) {
            result += "地下水:1.00";
        } else {
            result += "降水:" + shanbei + "," + "融雪:" + rong + "," + "地下水:" + di;
        }

        return result;
    }

    public List<Flood> setShortFlood(Object[][] predict, ForecastInputParam param, double[] rain) {
        List<Flood> result = new ArrayList<>();
        int n = param.getPeriodStepNumber();
        int l = param.getPeriodStepSize();
        List<Object[][]> floodInformation = new SubBasinForecast().getFloodInformation(predict);
        Object[][] floodIndex = floodInformation.get(0);//洪号
        Object[][] floodNature = floodInformation.get(1);//洪水信息
        Object[][] water_outQ = new Object[n][3];//水位、出库流量、汛限水位
        if (param.getLocation().equals("楼庄子")) {
            Object[][] input = new Object[n][2];
            for (int i = 0; i < n; i++) {
                input[i][0] = predict[i * l][0];
                input[i][1] = predict[i * l][1];
            }
//            for (int i = 0; i < n; i++) {
//                water_outQ[i][0] = new SubBasinForecast().getWaterLevel(predict, param)[i];
//                water_outQ[i][1] = predict[i * l][1];
//                water_outQ[i][2] = (((double) predict[i][1] > 60.0) ? 1 : 0);
//            }
            int timeLength = 3600 * l;
            ReqCurve reqCurve  = new ReqCurve();
            List<Option> lzzOutList = LZZ.Calculate(param.getBasinStr(),input, timeLength,reqCurve,param.getIsReferenceWater());
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = lzzOutList.get(i).getH1();
                water_outQ[i][1] = lzzOutList.get(i).getQOut();
                water_outQ[i][2] = (((double) water_outQ[i][0] > 1394.5) ? 1 : 0);
            }
        } else {
            for (int i = 0; i < n; i++) {
                water_outQ[i][0] = new SubBasinForecast().getWaterLevel(predict, param)[i];
                water_outQ[i][1] = predict[i * l][1];
                water_outQ[i][2] = (((double) predict[i][1] > 60.0) ? 1 : 0);
            }
        }

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
            flood.setPeakVolume((Double) floodNature[7][1]);
            flood.setFloodVolumeOne((Double) floodNature[4][1]);//特征洪量
            flood.setFloodVolumeThree((Double) floodNature[5][1]);
            flood.setFloodVolumeSeven((Double) floodNature[6][1]);
            flood.setQCause(floodSource);//洪水来源
            flood.setQComposition(floodComposition);//洪水组成
            flood.setRainProcess(Math.round(rain[i]) * 100 / 100.0);//雨情
            flood.setWaterLevel((double) water_outQ[i][0]);//相应水位
            flood.setOutQ((double) water_outQ[i][1]);//出库流量
            flood.setWarningTime((Integer) water_outQ[i][2]);//是否超过汛限水位
            flood.setFloodLevel(floodLevel);//洪水等级
            flood.setConfluenceTime(floodTime);//汇流时间
            result.add(flood);
        }
        return result;
    }

}
