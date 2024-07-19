package com.cj.model.func.modular.FloodPredict.Calibration;

import com.cj.model.func.modular.FloodPredict.Calibration.entity.*;
import com.cj.model.func.modular.FloodPredict.Calibration.pso.*;
import com.cj.model.func.modular.FloodPredict.entity.*;
import com.cj.model.func.modular.FloodPredict.model.function.SnowMeltModel;
import com.cj.model.func.modular.FloodPredict.utils.*;
import com.cj.model.func.modular.entity.Flood;
import java.util.*;
import java.util.stream.DoubleStream;




public class ShanBeiCalibration {
    //参与率定的数据
    private List<CalibrationData> dataList = new ArrayList<>();
    //流域信息
    private Hydrology hydrology  = new Hydrology();
    //固定的参数
    private Map<String,ShanbeiParam> paramStatic = new HashMap<>();
    private final DataUtils du = new DataUtils();
    private final TimeUtils tu = new TimeUtils();

    public Map<String, CalibrationOutput> calibration(CalibrationParam input) {
        FloodBasin floodBasin = input.getFloodBasin();
        for (Hydrology station: floodBasin.getHydrologies()){
            if (station.getStationName().equals(input.getLocation())){
                hydrology = station;
            }
        }
        paramStatic = floodBasin.getParamMap();
        //导入输入数据
        getTimesData(input);
        //获取率定结果
        Map<String, CalibrationOutput> result = new HashMap<>();
        if (hydrology.getPosition()==0){//上游
            String station = hydrology.getIncludingWater().get(0);
            if (input.getFlowData()==null||input.getFlowData().get(station).isEmpty()){
                result.put(input.getLocation(), new CalibrationOutput() {{setError("未获取"+station+"历史数据");}});
            }else {
                result.put(input.getLocation(), getCalResult(input));
            }
        }else {
            String station0 = hydrology.getIncludingWater().get(1);
            String station1 = hydrology.getIncludingWater().get(2);
            if (input.getFlowData()==null||input.getFlowData().get(station0).isEmpty()||input.getFlowData().get(station1).isEmpty()){
                final String station;
                if (input.getFlowData()==null||input.getFlowData().get(station0).isEmpty()) station = station0;
                else station = station1;
                result.put(input.getLocation(), new CalibrationOutput() {{setError("未获取"+ station +"历史数据");}});
            }else {
                result.put(input.getLocation(), getCalResult(input));
            }
        }
        return result;
    }

    public CalibrationOutput getCalResult(CalibrationParam input) {
        CalibrationOutput output = new CalibrationOutput();
        try {
            output = stationCalibration(input);
        } catch (RuntimeException e) {
            e.printStackTrace();
            output.setError(e.getMessage());
        }
        return output;
    }

    /**
     * 需要前期降水、温度和径流，返回率定的参数和率定好的径流
     */
    private CalibrationOutput stationCalibration(CalibrationParam input) {
        CalibrationOutput results = new CalibrationOutput();
        Map<String,ShanbeiParam> paramMap;
        Interval[] regionIntervals = new Interval[0];
        // 定义模型各参数的有效范围
        switch (hydrology.getStationName()){
            case "3号桥":
                regionIntervals = new Interval[]{
                        new Interval(150, 250),//WM
                        new Interval(18, 30),//FC
                        new Interval(60, 100),//FM
                        new Interval(0.1, 0.3),//K
                        //加普沙
                        new Interval(0.8, 0.92),//CS
                        new Interval(10, 16),//L
                        //八一林场
                        new Interval(0.8, 0.92),//CS
                        new Interval(12, 18),//L
                        //无名河+宰尔德+东南沟
                        new Interval(0.8, 0.92),//CS
                        new Interval(8, 16),//L
                        //萨尔达万+煤矿沟
                        new Interval(0.6, 0.8),//CS
                        new Interval(4, 6),//L
                };
                break;
            case "楼庄子":
                regionIntervals = new Interval[]{
                        new Interval(150, 250),//WM
                        new Interval(18, 30),//FC
                        new Interval(60, 100),//FM
                        new Interval(0.1, 0.3),//K
                        //加普沙
                        new Interval(0.8, 0.92),//CS
                        new Interval(10, 18),//L
                        //八一林场
                        new Interval(0.8, 0.92),//CS
                        new Interval(12, 20),//L
                        //无名河+宰尔德+东南沟
                        new Interval(0.8, 0.92),//CS
                        new Interval(10, 18),//L
                        //萨尔达万+煤矿沟
                        new Interval(0.6, 0.8),//CS
                        new Interval(4, 8),//L
                        //黑沟+喀什沟
                        new Interval(0.6, 0.8),//CS
                        new Interval(0, 4),//L
                        //制材厂
                        new Interval(0.6, 0.8),//CS
                        new Interval(0, 2),//L
                };
                break;
            case "头屯河":
                regionIntervals = new Interval[]{
                        new Interval(150, 250),//WM
                        new Interval(18, 30),//FC
                        new Interval(60, 100),//FM
                        new Interval(0.1, 0.3),//K
                        //小渠子+团结一队
                        new Interval(0.7, 0.9),//CS
                        new Interval(0, 4),//L
                        //甘沟
                        new Interval(0.8, 0.92),//CS
                        new Interval(0, 3),//L
                        //头屯河水库
                        new Interval(0.7, 0.9),//CS
                        new Interval(0, 1),//L
                };
        }
        if (!input.getIsAutomatic()){
            paramMap = input.getManualParam();
        }else {
            // 创建PSO算法的问题域
            Domain domain = new Domain(regionIntervals, this::Evaluate, 1);
            // 创建PSO算法实例
            PSO pso = new PSO(domain);
            // 运行算法并存储结果
            PSOResult result = pso.Execute(100, 100);
            System.out.println(result);
            //子流域参数赋值
            paramMap = paramAssignment(result.Position);
        }
        //计算子流域汇流
        List<CalibrationFlow> flowList = new ArrayList<>();
        List<Double> hisList = new ArrayList<>();
        List<Double> preList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            double[] Q = getSubBasinQ(dataList.get(i),paramMap);
            double[] QHistory = getSubBasinQ(dataList.get(i),input.getHistoryParam());
            Object[][] hisF = dataList.get(i).getHisF();
            Object[][] baseF = dataList.get(i).getBaseF();
            for (int j = 0; j < Q.length; j++) {
                for (Object[] objects : baseF) {
                    if (tu.DateCompare((Date) hisF[j][0], (Date) objects[0], "日")) {
                        Q[j] += (double) objects[1];
                        QHistory[j] += (double) objects[1];
                    }
                }
            }
            if (hydrology.getPosition() != 0) {
                Object[][] outF = dataList.get(i).getOutF();
                for (int j = 0; j < Q.length; j++) {
                    Q[j] += (double) outF[j][1];
                    QHistory[j] += (double) outF[j][1];
                }
            }
            Object[][] rainFall = du.pointToSurfaceObject(dataList.get(i).getPreRE(), hydrology.getStationName());
            for (int j = 0; j < Q.length; j++) {
                //参数合格率计算
                preList.add(Q[j]);
                hisList.add((Double) hisF[j][1]);
                //结果输出
                CalibrationFlow calibrationFlow = new CalibrationFlow();
                calibrationFlow.setTime((Date) hisF[j][0]);
                calibrationFlow.setHistoryFlow((Double) hisF[j][1]);
                calibrationFlow.setPreParamFlow(QHistory[j]);
                calibrationFlow.setNewParamFlow(Q[j]);
                calibrationFlow.setHistoryRainfall((Double) rainFall[j][1]);
                flowList.add(calibrationFlow);
            }
        }
        //计算误差
        double[] hisData = new double[hisList.size()];
        double[] preData = new double[preList.size()];
        for (int i = 0; i < hisData.length; i++) {
            hisData[i] = hisList.get(i);
            preData[i] = preList.get(i);
        }
        Double error = qualifyRate(hisData, preData);
        for (Map.Entry<String,ShanbeiParam> entry : paramMap.entrySet()){
            ShanbeiParam param = entry.getValue();
            param.setQC(error);
        }
        //返回结果
        results.setFlowList(flowList);
        results.setParam(paramMap);
        return results;

    }

    public double Evaluate(double[] params) {
        List<Double> hisFlowList = new ArrayList<>();
        List<Double> preFlowList = new ArrayList<>();
        //子流域参数赋值
        Map<String,ShanbeiParam> paramMap = paramAssignment(params);
        //计算子流域汇流
        for (int i = 0; i < dataList.size(); i++) {
            double[] Q = getSubBasinQ(dataList.get(i),paramMap);
            Object[][] hisF = dataList.get(i).getHisF();
            Object[][] baseF = dataList.get(i).getBaseF();
            for (int j = 0; j < Q.length; j++) {
                for (Object[] objects : baseF) {
                    if (tu.DateCompare((Date) hisF[j][0], (Date) objects[0], "日")) {
                        Q[j] += (double) objects[1];
                    }
                }
            }
            if (hydrology.getPosition() != 0) {
                Object[][] outF = dataList.get(i).getOutF();
                for (int j = 0; j < Q.length; j++) {
                    Q[j] += (double) outF[j][1];
                }
            }
            //洪水过程对比
            for (int j = 0; j < hisF.length; j++) {
                hisFlowList.add((double) hisF[j][1]);
                preFlowList.add(Q[j]);
            }
        }
        double[] historyFlow = new double[hisFlowList.size()];
        double[] predictFlow = new double[preFlowList.size()];
        for (int j = 0; j < historyFlow.length; j++) {
            historyFlow[j] = hisFlowList.get(j);
            predictFlow[j] = preFlowList.get(j);
        }
        return NashSutcliffeEfficiency(historyFlow, predictFlow);

//        return RMSE(historyFlow, predictFlow);
    }

    /**
     * 子流域参数赋值
     * @param params
     * @return
     */
    public Map<String,ShanbeiParam> paramAssignment(double[] params){
        //子流域参数赋值
        ShanbeiParam param = new ShanbeiParam();
        Map<String,ShanbeiParam> paramMap = new HashMap<>();
        List<String> rainStations = hydrology.getRainStation();
        switch (hydrology.getStationName()){
            case "3号桥":
            case "楼庄子":
                param.setWM(params[0]);
                param.setFC(params[1]);
                param.setFM(params[2]);
                param.setK(params[3]);
                for (int i = 0; i < rainStations.size(); i++) {
                    param.setArea(paramStatic.get(rainStations.get(i)).getArea());
                    param.setB(paramStatic.get(rainStations.get(i)).getB());
                    param.setFB(paramStatic.get(rainStations.get(i)).getFB());
                    param.setK(paramStatic.get(rainStations.get(i)).getK());
                    if (rainStations.get(i).equals("加普沙自动雨量站")){
                        param.setCS(params[4]);
                        param.setL((int) params[5]);
                    }
                    if (rainStations.get(i).equals("八一林场自动雨量站")){
                        param.setCS(params[6]);
                        param.setL((int) params[7]);
                    }
                    if (rainStations.get(i).equals("无名沟自动雨量站")||rainStations.get(i).equals("宰尔德自动雨量站")||rainStations.get(i).equals("东南沟自动雨量站")){
                        param.setCS(params[8]);
                        param.setL((int) params[9]);
                    }
                    if (rainStations.get(i).equals("萨尔达万自动雨量站")||rainStations.get(i).equals("煤矿沟自动雨量站")){
                        param.setCS(params[10]);
                        param.setL((int) params[11]);
                    }
                    if (hydrology.getStationName().equals("楼庄子")){
                        if (rainStations.get(i).equals("黑沟自动雨量站")||rainStations.get(i).equals("喀什沟自动雨量站")){
                            param.setCS(params[12]);
                            param.setL((int) params[13]);
                        }
                        if (rainStations.get(i).equals("制材厂自动雨量站")){
                            param.setCS(params[14]);
                            param.setL((int) params[15]);
                        }
                    }
                    paramMap.put(rainStations.get(i),param);
                }
                break;
            case "头屯河":
                param.setWM(params[0]);
                param.setFC(params[1]);
                param.setFM(params[2]);
                param.setK(params[3]);
                for (int i = 0; i < rainStations.size(); i++) {
                    param.setArea(paramStatic.get(rainStations.get(i)).getArea());
                    param.setB(paramStatic.get(rainStations.get(i)).getB());
                    param.setFB(paramStatic.get(rainStations.get(i)).getFB());
                    param.setK(paramStatic.get(rainStations.get(i)).getK());
                    if (rainStations.get(i).equals("小渠子雨量站")||rainStations.get(i).equals("团结一队雨量站")){
                        param.setCS(params[4]);
                        param.setL((int) params[5]);
                    }
                    if (rainStations.get(i).equals("头屯河水库雨量站")){
                        param.setCS(params[6]);
                        param.setL((int) params[7]);
                    }
                    paramMap.put(rainStations.get(i),param);
                }
        }
        return paramMap;
    }

    /**
     * 获取多场洪水的数据进行率定
     */
    public void getTimesData(CalibrationParam inputData) {
        FlowSelect flowSelect = new FlowSelect();
        List<Object[]> dateList = flowSelect.getFloodDate(inputData,hydrology);
        for (Object[] objects : dateList) {//多场洪水参与率定
            ForecastInputParamNew forecastInputParamNew = new ForecastInputParamNew();
            Date startTime = (Date) objects[0];
            Date endTime = (Date) objects[1];
            forecastInputParamNew.setPredictionTime(startTime);//预报时间应当是先筛选出径流过程
            forecastInputParamNew.setPeriodTimeNum(tu.duration(startTime, endTime, "小时"));
            forecastInputParamNew.setPeriodTimeStep(1);
            forecastInputParamNew.setRainfall(inputData.getRainfall());
            forecastInputParamNew.setFlowData(inputData.getFlowData());
            forecastInputParamNew.setModelType(3);
            CalibrationData data = getOneTimeData(forecastInputParamNew);
            dataList.add(data);
        }
    }

    /**
     * 获得单个站点的参数率定输入数据（蒸发降雨，累积雨量，真实径流，前期流量）
     */
    public CalibrationData getOneTimeData(ForecastInputParamNew paramNew) {
        CalibrationData result = new CalibrationData();
        InputDataSet rainIntegration = du.rainIntegration(paramNew);//雨量站数据
        Map<String,List<PredictInputData>> flowData = du.flowIntegration(paramNew);//水位站数据
        List<String> rainStation = hydrology.getRainStation();
        //蒸发降雨数据
        Map<String,Object[][]> preRain = new HashMap<>();
        for (Map.Entry<String, List<RainFallDto>> entry : rainIntegration.getRainHourData().entrySet()) {
            String key = entry.getKey();
            List<RainFallDto> value = entry.getValue();
            Object[][] hour = new Object[value.size()][3];
            if (rainStation.contains(key)){
                for (int i = 0; i < value.size(); i++) {
                    hour[i][0] = value.get(i).getDate();
                    hour[i][1] = value.get(i).getTemperature();
                    hour[i][2] = value.get(i).getRainFall();
                }
                hour = du.temToEva(hour);
                preRain.put(key,hour);
            }
        }
        result.setPreRE(preRain);
        //前期雨量数据
        Map<String,Object[][]> hisRain = new HashMap<>();
        for (Map.Entry<String, List<RainFallDto>> entry : rainIntegration.getRainDayData().entrySet()) {
            String key = entry.getKey();
            List<RainFallDto> value = entry.getValue();
            Object[][] day = new Object[value.size()][2];
            if (rainStation.contains(key)){
                for (int i = 0; i < value.size(); i++) {
                    day[i][0] = value.get(i).getDate();
                    day[i][1] = value.get(i).getRainFall();
                }
                hisRain.put(key, day);
            }
        }
        result.setHisR(hisRain);
        //真实径流数据
        List<PredictInputData> flow;
        List<PredictInputData> outFlow;
        int l = paramNew.getPeriodTimeNum();
        Object[][] hisF = new Object[l][2];//真实径流
        Object[][] outF = new Object[l][2];//出库径流
        if (hydrology.getPosition()==0){
            flow = flowData.get(hydrology.getIncludingWater().get(0));//获取流量数据
        } else {
            flow = flowData.get(hydrology.getIncludingWater().get(2));//下游水库入库
            outFlow = flowData.get(hydrology.getIncludingWater().get(1));//上游出库
            int index = 0;
            for (PredictInputData data:outFlow){
                if (data.getDates().after(tu.addCalendar(paramNew.getPredictionTime(),"小时",-1))&&
                        data.getDates().before(tu.addCalendar(paramNew.getPredictionTime(), "小时", paramNew.getPeriodTimeNum()+1))){
                    outF[index]= new Object[]{data.getDates(),data.getFlow()};
                    index++;
                }
            }
            outF = getContinuousSequences(outF,paramNew.getPredictionTime(), paramNew.getPeriodTimeNum(), "小时", hydrology.getStationName());
            result.setOutF(outF);
        }
        int index = 0;
        for (PredictInputData data:flow){
            if (data.getDates().after(tu.addCalendar(paramNew.getPredictionTime(),"小时",-1))&&
                    data.getDates().before(tu.addCalendar(paramNew.getPredictionTime(), "小时", paramNew.getPeriodTimeNum()+1))){
                hisF[index]= new Object[]{data.getDates(),data.getFlow()};
                index++;
            }
        }
        hisF = getContinuousSequences(hisF,paramNew.getPredictionTime(), paramNew.getPeriodTimeNum(), "小时", hydrology.getStationName());
        result.setHisF(hisF);
        //前期径流，融雪期为融雪径流
        Object[][] baseF = new Object[paramNew.getPeriodTimeNum() / 24 + 1][2];
        int month = tu.getSpecificDate(paramNew.getPredictionTime()).get("月");
        if (month>=hydrology.getSnowMonth()[0]&&month<=hydrology.getSnowMonth()[1]) {//融雪期
            baseF = snowBase(hydrology.getIncludingStation().get(0),paramNew.getPredictionTime(),paramNew.getPeriodTimeNum());
        }else {//非融雪期
            double base = 0.0;
            int number = 0;
            for (PredictInputData data:flow){
                if (data.getDates().after(tu.addCalendar(paramNew.getPredictionTime(),"日",-2))&&
                data.getDates().before(tu.addCalendar(paramNew.getPredictionTime(),"小时",-InputUtils.beforeHours))){
                    base += data.getFlow();
                    number++;
                }
            }
            for (int i = 0; i < baseF.length; i++) {
                baseF[i][0]=tu.addCalendar(paramNew.getPredictionTime(), "日",i);
                if (number!=0){
                    baseF[i][1]=base/number;
                }else {
                    baseF[i][1]= du.setNullTemFlow(hydrology.getStationName(), paramNew.getPredictionTime());
                }
            }
        }
        result.setBaseF(baseF);
        return result;
    }

    /**
     * 获取连续时间序列数据
     * @param input
     * @param start
     * @param l
     * @return
     */
    public Object[][] getContinuousSequences(Object[][] input,Date start,int l,String period,String location){
        if (input.length == 0){
            throw new RuntimeException("未获取"+location+"数据");
        }
        Object[][] result = new Object[l][2];
        List<Date> dateList = new ArrayList<>();
        List<Double> dataList = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i] !=null && input[i][0] != null && input[i][1] != null){
                dateList.add((Date) input[i][0]);
                dataList.add((Double) input[i][1]);
            }
        }
        for (int i = 0; i < l; i++) {
            boolean existTime = false;
            Date date = tu.addCalendar(start,period,i);
            for (int j = 0; j < dateList.size(); j++) {
                if (tu.DateCompare(date, dateList.get(j),period)){
                    existTime = true;
                    result[i][0] = dateList.get(j);
                    result[i][1] = dataList.get(j);
                }
            }
            if (!existTime){
                result[i][0] = date;
                int m = tu.findNearestTime(dateList,date);
                if (m - 1 >= 0 && date.after(dateList.get(m-1)) && date.before(dateList.get(m))){
                    int length = tu.duration(dateList.get(m-1),dateList.get(m),period);
                    int duration = tu.duration(date,dateList.get(m),period);
                    result[i][1] = dataList.get(m-1)+(length-duration)*(dataList.get(m)-dataList.get(m-1))/length;
                } else {
                    result[i][1] = dataList.get(m);
                }
            }
        }
        return result;
    }

    /**
     * 获取融雪基础径流
     */
    public Object[][] snowBase(String location, Date startTime, int number) {
        Object[][] result = new Object[number / 24 + 1][2];
        Object[][] input = InputUtils.historyData.get(location+"日");
        int days = number / 24 + 1;
        for (int i = 0; i < days; i++) {
            int l = 0;//截至到预报时间目前的日尺度数据
            for (int j = 0; j < input.length; j++) {
                if (((Date) input[j][0]).before(startTime)) {
                    l++;
                }
            }
            Object[][] snowData = new Object[l][4];
            System.arraycopy(input, 0, snowData, 0, l);
            Object[][] snowMeltInput = new MachineDataUtils().snowMeltDate(snowData, location);
            SnowMeltModel snowMeltModel = new SnowMeltModel();
            ForecastInputParamNew paramNew = new ForecastInputParamNew();
            paramNew.setPeriodTimeNum(1);
            paramNew.setPeriodTimeStep(1);
            paramNew.setPredictionTime(startTime);
            du.getDaysData(paramNew);
            ForecastInputParam snowParam = new ForecastInputParam();
            snowParam.setLocation(location);
            snowParam.setPreStartTime(startTime);
            snowParam.setIsSnowMeltModel(true);
            snowParam.setIsAverage(true);
            snowParam.setPeriodStepNumber(1);
            snowParam.setPeriodStepSize(1);
            snowParam.setPreRainTem(paramNew.getPreRainTem());
            List<Flood> snowList = snowMeltModel.snowForecast(snowMeltInput, snowParam);
            result[i][0] = snowList.get(0).getTime();
            result[i][1] = snowList.get(0).getPreQ();
            startTime = tu.addCalendar(startTime,"日", 1);
        }
        return result;
    }

    public double[] getSubBasinQ(CalibrationData input,Map<String,ShanbeiParam> paramMap){
        List<String> rainStation = hydrology.getRainStation();
        double[] result = new double[input.getHisF().length];
        for (int i = 0; i < rainStation.size(); i++) {
            Object[][] preRE = input.getPreRE().get(rainStation.get(i));
            Object[][] hisR = input.getHisR().get(rainStation.get(i));
            ShanbeiParam param = paramMap.get(rainStation.get(i));
            ShanBeiModel model = new ShanBeiModel();
            model.InputData(param,preRE,hisR);
            model.InitialMoistureContentCalculation();
            model.RunoffYieldCalculation_UnevenInfiltration();
            model.ConfluenceCalculation();
            for (int j = 0; j < model.Q.length; j++) {
                result[j] += model.Q[j];
            }
        }
        return result;
    }

    /**
     * 以真实值的20%为许可误差计算合格率
     */
    public double qualifyRate(double[] real, double[] estimate) {
        int size = real.length;
        int[] qualifyNum = new int[real.length];
        double[] qr = new double[real.length];
        for (int j = 0; j < real.length; j++) {
            qualifyNum[j] = 0;
            if (Math.abs(estimate[j] - real[j]) / real[j] <= 0.2||Math.abs(estimate[j] - real[j]) <= 6) {
                qualifyNum[j]++;
            }
        }
        double sum = 0;
        for (int i = 0; i < qr.length; i++) {
            qr[i] = (double) qualifyNum[i] / size;
            sum += qr[i];
        }
        return sum * 100;
    }

    /**
     * 计算均方根误差
     */
    public double RMSE(double[] a, double[] b) {
        if (a.length != b.length) {
            return -1;
        }
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        result = Math.pow(result / a.length, 0.5);

        return result;
    }

    /**
     * 计算模型预测结果的纳什效率系数
     */
    public double NashSutcliffeEfficiency(double[] obs, double[] pre) {
        int n = obs.length;
        double avg = DoubleStream.of(obs).average().getAsDouble();
        double s1 = 0, s2 = 0;
        for (int i = 0; i < n; ++i) {
            s1 += Math.pow(obs[i] - pre[i], 2);
            s2 += Math.pow(obs[i] - avg, 2);
        }
        return 1 - s1 / s2;
    }
}
