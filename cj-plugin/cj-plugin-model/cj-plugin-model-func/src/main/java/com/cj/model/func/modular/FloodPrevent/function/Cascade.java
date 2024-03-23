package com.cj.model.func.modular.FloodPrevent.function;

import com.alibaba.fastjson.JSONObject;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfLZZ;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Cascade {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static List<ResOption> calculator(ReqFloodPrevent reqFloodPrevent) throws Exception {
        ModelOfLZZ modelOfLZZ = new ModelOfLZZ(reqFloodPrevent);
        ModelOfTTH modelOfTTH = new ModelOfTTH(reqFloodPrevent);

        //楼庄子常规调度
        List<Option> LZZ_op1=modelOfLZZ.Calculate_S1();
        //头屯河常规调度
        modelOfTTH.setQ_Input2(LZZ_op1);
        List<Option> TTH_op1=modelOfTTH.Calculate_S2();
        //两库常规调度结果保存
        File tempFile1 = File.createTempFile("option1",".xlsx");
        String path1= tempFile1.getAbsolutePath();
        List<Option> option1 = new ArrayList<>();
        option1.addAll(LZZ_op1);
        option1.addAll(TTH_op1);
        Write(path1,option1);


        //两库常规调度时段末水位
        double endH_lzz = LZZ_op1.get(LZZ_op1.size()-1).getH2();
        modelOfLZZ.SetEndH(endH_lzz);
        double endH_tth = TTH_op1.get(TTH_op1.size()-1).getH2();
        modelOfTTH.SetEndH(endH_tth);



        //楼庄子最小拦蓄调度
        List<Option> LZZ_op2=modelOfLZZ.MinLevel(option1,"楼庄子");
        //头屯河最小拦蓄调度
        modelOfTTH.setQ_Input2(LZZ_op2);
        List<Option> TTH_op2=modelOfTTH.MinLevel(option1,"头屯河");
        //两库最小拦蓄调度结果保存
        File tempFile2 = File.createTempFile("option2",".xlsx");
        String path2= tempFile2.getAbsolutePath();
        List<Option> option2 = new ArrayList<>();
        option2.addAll(LZZ_op2);
        option2.addAll(TTH_op2);
        Write(path2,option2);


        //楼庄子最大削峰调度
        List<Option> LZZ_op3=modelOfLZZ.MinDischarge(option1,"楼庄子");
        //头屯河最大削峰调度
        modelOfTTH.setQ_Input2(LZZ_op3);
        List<Option> TTH_op3=modelOfTTH.MinDischarge(option1,"头屯河");
        //两库最大削峰调度结果保存
        File tempFile3 = File.createTempFile("option3",".xlsx");
        String path3= tempFile3.getAbsolutePath();
        List<Option> option3 = new ArrayList<>();
        option3.addAll(LZZ_op3);
        option3.addAll(TTH_op3);
        Write(path3,option3);



        //返回结果路径及文件名称
        List<ResOption> result = new ArrayList<>();
        ResOption resOption1 = new ResOption();
        resOption1.setPath(path1);
        resOption1.setName(reqFloodPrevent.getProgrammeName()+"-常规调度");
        result.add(resOption1);

        ResOption resOption2 = new ResOption();
        resOption2.setPath(path2);
        resOption2.setName(reqFloodPrevent.getProgrammeName()+"-最小拦蓄");
        result.add(resOption2);

        ResOption resOption3 = new ResOption();
        resOption3.setPath(path3);
        resOption3.setName(reqFloodPrevent.getProgrammeName()+"-最大削峰");
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
        row0.createCell(11).setCellValue("retain");
        row0.createCell(12).setCellValue("percentage1");
        row0.createCell(13).setCellValue("percentage2");
        row0.createCell(14).setCellValue("limits");
        for (int i = 0; i < options.size(); i++) {
            Option line = options.get(i);
            XSSFRow row = sheet.createRow(i+1);

            double Q_in = BigDecimal.valueOf(line.getQIn()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double H1=BigDecimal.valueOf(line.getH1()).setScale(2,RoundingMode.HALF_UP).doubleValue();
            double H2=BigDecimal.valueOf(line.getH2()).setScale(2,RoundingMode.HALF_UP).doubleValue();
            double Q_out=BigDecimal.valueOf(line.getQOut()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double Q1=BigDecimal.valueOf(line.getQ1()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double Q2=BigDecimal.valueOf(line.getQ2()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double Q3=BigDecimal.valueOf(line.getQ3()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double V=BigDecimal.valueOf(line.getV()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double retain=BigDecimal.valueOf(line.getRetain()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            row.createCell(0).setCellValue(line.getName());
            row.createCell(1).setCellValue(line.getType());
            row.createCell(2).setCellValue(sdf.format(line.getTime()));
            row.createCell(3).setCellValue(Q_in);
            row.createCell(4).setCellValue(H1);
            row.createCell(5).setCellValue(H2);
            row.createCell(6).setCellValue(Q_out);
            row.createCell(7).setCellValue(Q1);
            row.createCell(8).setCellValue(Q2);
            row.createCell(9).setCellValue(Q3);
            row.createCell(10).setCellValue(V);
            row.createCell(11).setCellValue(retain);
            row.createCell(12).setCellValue(line.getPercentage1());
            row.createCell(13).setCellValue(line.getPercentage2());
            List<Double> limits = line.getLimits();
            row.createCell(14).setCellValue(limits.toString());

        }
        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
            workbook.close();
        }catch (IOException e) {
            e.printStackTrace();
        }







    }
    public static double[] getPercentage_lzz(double V){

        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-6534.4))/724.93;
        result[1]=100*Math.max(0,(V-6534.4))/839.59;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
    }
    public static double[] getPercentage_tth(double V){
        double[] result = new double[2];
        result[0]=100*Math.max(0,(V-1297.03))/223.816;
        result[1]=100*Math.max(0,(V-1297.03))/541.681;
        result[0]=BigDecimal.valueOf(result[0]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        result[1]=BigDecimal.valueOf(result[1]).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return result;
    }
}
