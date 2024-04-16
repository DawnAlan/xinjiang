package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.model.func.modular.FloodPredict.Calibration.ShanBeiCalibration;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationOutput;
import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParamNew;
import com.cj.model.func.modular.FloodPredict.entity.IrrigatedHydrologyParam;
import com.cj.model.func.modular.FloodPredict.entity.LzzHydrologyParam;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.model.test.Test.objectToList;


public class TestCalibration {
    public static void main(String[] args) throws ParseException, IOException, InvalidFormatException {
        CalibrationParam input = new CalibrationParam();
        input.setIsAutomatic(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = sdf.parse("2022-07-18 00:00:00");
        Date endTime = sdf.parse("2022-07-21 00:00:00");
        input.setStartTime(startTime);
        input.setEndTime(endTime);
        ForecastInputParamNew paramForecastInputParamNew = new ForecastInputParamNew();
        paramForecastInputParamNew =objectToList(paramForecastInputParamNew);//读取表格
        LzzHydrologyParam lzzHydrologyParam = paramForecastInputParamNew.getLzzHydrologyParam();
        IrrigatedHydrologyParam irrigatedHydrologyParam= paramForecastInputParamNew.getIrrigatedHydrologyParam();
        input.setIrrigatedHydrologyParam(irrigatedHydrologyParam);
        input.setLzzHydrologyParam(lzzHydrologyParam);
        input.setLocation("楼庄子");

        Object[][] imitateFlow = new Object[72][2];
        for (int i = 0; i < 72; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            imitateFlow[i][0]=startTime;
            imitateFlow[i][1]=0.0;
            calendar.add(Calendar.HOUR_OF_DAY,1);
            startTime = calendar.getTime();
        }
        input.setImitateFlow(imitateFlow);
        ShanBeiCalibration shanBeiCalibration = new ShanBeiCalibration();
        System.out.println("开始率定参数");
        CalibrationOutput result = shanBeiCalibration.calibration(input);
    }
}
