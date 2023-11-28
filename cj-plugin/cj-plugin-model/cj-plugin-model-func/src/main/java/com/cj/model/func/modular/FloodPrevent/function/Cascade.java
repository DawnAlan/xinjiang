package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.common.util.UUIDUtils;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfLZZ;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Cascade {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public static List<ResOption> calculator(ReqFloodPrevent reqFloodPrevent) throws Exception {
        ModelOfLZZ modelOfLZZ = new ModelOfLZZ(reqFloodPrevent);
        ModelOfTTH modelOfTTH = new ModelOfTTH(reqFloodPrevent);

        List<List<Double>> LZZ_op1=modelOfLZZ.Calculate_S1();
        List<List<Double>> LZZ_op2=modelOfLZZ.MinLevel();
        List<List<Double>> LZZ_op3=modelOfLZZ.MinDischarge();

        modelOfTTH.setQ_Input(LZZ_op1.get(3));
        List<List<Double>> TTH_op1=modelOfTTH.Calculate_S2();

        modelOfTTH.setQ_Input(LZZ_op2.get(3));
        List<List<Double>> TTH_op2=modelOfTTH.MinLevel();

        modelOfTTH.setQ_Input(LZZ_op3.get(3));
        List<List<Double>> TTH_op3=modelOfTTH.MinDischarge();

        File tempFile1 = File.createTempFile("option1",".xlsx");
        String path1= tempFile1.getAbsolutePath();
        List<Option> option1 = new ArrayList<>();
        for (int i = 0; i < LZZ_op1.get(0).size(); i++) {
            Option option = new Option();
            option.setName("楼庄子");
            option.setType("常规调度");
            option.setTime(modelOfLZZ.getTime().get(i));
            option.setQIn(LZZ_op1.get(0).get(i));
            option.setH1(LZZ_op1.get(1).get(i));
            option.setH2(LZZ_op1.get(2).get(i));
            option.setQOut(LZZ_op1.get(3).get(i));
            option.setQ1(LZZ_op1.get(4).get(i));
            option.setQ2(LZZ_op1.get(5).get(i));
            option.setQ3(LZZ_op1.get(6).get(i));
            option.setV(LZZ_op1.get(7).get(i));
            option1.add(option);
        }
        for (int i = 0; i < TTH_op1.get(0).size(); i++) {
            Option option = new Option();
            option.setName("头屯河");
            option.setType("常规调度");
            option.setTime(modelOfTTH.getTime().get(i));
            option.setQIn(TTH_op1.get(0).get(i));
            option.setH1(TTH_op1.get(1).get(i));
            option.setH2(TTH_op1.get(2).get(i));
            option.setQOut(TTH_op1.get(3).get(i));
            option.setQ1(TTH_op1.get(4).get(i));
            option.setQ2(TTH_op1.get(5).get(i));
            option.setQ3(TTH_op1.get(6).get(i));
            option.setV(TTH_op1.get(7).get(i));
            option1.add(option);
        }

        File tempFile2 = File.createTempFile("option2",".xlsx");
        String path2= tempFile2.getAbsolutePath();
        List<Option> option2 = new ArrayList<>();
        for (int i = 0; i < LZZ_op2.get(0).size(); i++) {
            Option option = new Option();
            option.setName("楼庄子");
            option.setType("最小拦蓄");
            option.setTime(modelOfLZZ.getTime().get(i));
            option.setQIn(LZZ_op2.get(0).get(i));
            option.setH1(LZZ_op2.get(1).get(i));
            option.setH2(LZZ_op2.get(2).get(i));
            option.setQOut(LZZ_op2.get(3).get(i));
            option.setQ1(LZZ_op2.get(4).get(i));
            option.setQ2(LZZ_op2.get(5).get(i));
            option.setQ3(LZZ_op2.get(6).get(i));
            option.setV(LZZ_op2.get(7).get(i));
            option2.add(option);
        }
        for (int i = 0; i < TTH_op2.get(0).size(); i++) {
            Option option = new Option();
            option.setName("头屯河");
            option.setType("最小拦蓄");
            option.setTime(modelOfLZZ.getTime().get(i));
            option.setQIn(TTH_op2.get(0).get(i));
            option.setH1(TTH_op2.get(1).get(i));
            option.setH2(TTH_op2.get(2).get(i));
            option.setQOut(TTH_op2.get(3).get(i));
            option.setQ1(TTH_op2.get(4).get(i));
            option.setQ2(TTH_op2.get(5).get(i));
            option.setQ3(TTH_op2.get(6).get(i));
            option.setV(TTH_op2.get(7).get(i));
            option2.add(option);
        }

        File tempFile3 = File.createTempFile("option3",".xlsx");
        String path3= tempFile3.getAbsolutePath();
        List<Option> option3 = new ArrayList<>();
        for (int i = 0; i < LZZ_op3.get(0).size(); i++) {
            Option option = new Option();
            option.setName("楼庄子");
            option.setType("最大削峰");
            option.setTime(modelOfLZZ.getTime().get(i));
            option.setQIn(LZZ_op3.get(0).get(i));
            option.setH1(LZZ_op3.get(1).get(i));
            option.setH2(LZZ_op3.get(2).get(i));
            option.setQOut(LZZ_op3.get(3).get(i));
            option.setQ1(LZZ_op3.get(4).get(i));
            option.setQ2(LZZ_op3.get(5).get(i));
            option.setQ3(LZZ_op3.get(6).get(i));
            option.setV(LZZ_op3.get(7).get(i));
            option3.add(option);
        }
        for (int i = 0; i < TTH_op3.get(0).size(); i++) {
            Option option = new Option();
            option.setName("头屯河");
            option.setType("最大削峰");
            option.setTime(modelOfLZZ.getTime().get(i));
            option.setQIn(TTH_op3.get(0).get(i));
            option.setH1(TTH_op3.get(1).get(i));
            option.setH2(TTH_op3.get(2).get(i));
            option.setQOut(TTH_op3.get(3).get(i));
            option.setQ1(TTH_op3.get(4).get(i));
            option.setQ2(TTH_op3.get(5).get(i));
            option.setQ3(TTH_op3.get(6).get(i));
            option.setV(TTH_op3.get(7).get(i));
            option3.add(option);
        }

        Write(path1,option1);
        Write(path2,option2);
        Write(path3,option3);


        List<ResOption> result = new ArrayList<>();
        ResOption resOption1 = new ResOption();
        resOption1.setPath(path1);
        resOption1.setName(reqFloodPrevent.getProgrammeName()+"-常规调度");
        ResOption resOption2 = new ResOption();
        resOption2.setPath(path2);
        resOption2.setName(reqFloodPrevent.getProgrammeName()+"-最小拦蓄");
        ResOption resOption3 = new ResOption();
        resOption3.setPath(path3);
        resOption3.setName(reqFloodPrevent.getProgrammeName()+"-最大削峰");

        result.add(resOption1);
        result.add(resOption2);
        result.add(resOption3);

        return result;
    }
    public static void Write(String path,List<Option> options) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("name");
        row0.createCell(1).setCellValue("type");
        row0.createCell(2).setCellValue("time");
        row0.createCell(3).setCellValue("qIn");
        row0.createCell(4).setCellValue("h1");
        row0.createCell(5).setCellValue("h2");
        row0.createCell(6).setCellValue("qOut");
        row0.createCell(7).setCellValue("q1");
        row0.createCell(8).setCellValue("q2");
        row0.createCell(9).setCellValue("q3");
        row0.createCell(10).setCellValue("v");
        for (int i = 0; i < options.size(); i++) {
            Option line = options.get(i);
            XSSFRow row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(line.getName());
            row.createCell(1).setCellValue(line.getType());
            row.createCell(2).setCellValue(sdf.format(line.getTime()));
            row.createCell(3).setCellValue(line.getQIn());
            row.createCell(4).setCellValue(line.getH1());
            row.createCell(5).setCellValue(line.getH2());
            row.createCell(6).setCellValue(line.getQOut());
            row.createCell(7).setCellValue(line.getQ1());
            row.createCell(8).setCellValue(line.getQ2());
            row.createCell(9).setCellValue(line.getQ3());
            row.createCell(10).setCellValue(line.getV());
        }
        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        }catch (IOException e) {
            e.printStackTrace();
        }







    }
}
