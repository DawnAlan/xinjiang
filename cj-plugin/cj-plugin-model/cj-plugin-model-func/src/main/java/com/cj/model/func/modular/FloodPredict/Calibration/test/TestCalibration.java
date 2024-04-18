package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiCalibration;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationOutput;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.IrrigatedHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.LzzHydrologyParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.model.test.Test.objectToList;


public class TestCalibration {
    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {


        CalibrationParam input = new CalibrationParam();
        input.setIsAutomatic(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse("2022-07-01 00:00:00");
        Date endTime = sdf.parse("2022-08-30 00:00:00");
        input.setEndTime(endTime);
        ForecastInputParamNew paramForecastInputParamNew = new ForecastInputParamNew();
        Date startTime1 = sdf.parse("2022-06-01 00:00:00");
        input.setStartTime(startTime1);
        paramForecastInputParamNew =objectToList(paramForecastInputParamNew);//读取表格
        input.setStartTime(startTime);
        LzzHydrologyParam lzzHydrologyParam = paramForecastInputParamNew.getLzzHydrologyParam();
        IrrigatedHydrologyParam irrigatedHydrologyParam= paramForecastInputParamNew.getIrrigatedHydrologyParam();
        input.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        input.setLzzHydrologyParam(lzzHydrologyParam);
        Map<String,ShanbeiParam> ShanbeiParamMap = new HashMap<>();
        ShanbeiParam shanbeiParam = new ShanbeiParam();
        shanbeiParam.setArea(1174.0);//流域面积
        shanbeiParam.setFB(0.08);//不透水面积的比例，透水面积比例为1-FB
        shanbeiParam.setWM(102.0);//张力水蓄水容量，或最大蓄水量 60-80mm
        shanbeiParam.setKC(1.0);//蒸散发折减系数 KC
        shanbeiParam.setFC(30.0);//流域土壤稳定下渗率 0.3-0.5 mm/min
        shanbeiParam.setFM(62.0);//流域土壤最大下渗率 1-2 mm/min
        shanbeiParam.setK(0.022);//K,霍尔顿下渗曲线方程中的土质系数 0.04~0.05/min
        shanbeiParam.setB(0.3);//B反映下渗能力在透水面积上的分布特性 1~5
        shanbeiParam.setCS(0.966);//CS 为地面径流消退系数 0.1~1
        shanbeiParam.setL(5);//L为汇流滞时（时段数）
        ShanbeiParamMap.put("楼庄子",shanbeiParam);
        input.setHistoryParam(ShanbeiParamMap);

        ShanBeiCalibration shanBeiCalibration = new ShanBeiCalibration();
        System.out.println("开始率定参数");
//        Object[][] result = shanBeiCalibration.oneSnowFlow("楼庄子",startTime,144);

        Map<String,CalibrationOutput> result = shanBeiCalibration.calibration(input);
    }
}
