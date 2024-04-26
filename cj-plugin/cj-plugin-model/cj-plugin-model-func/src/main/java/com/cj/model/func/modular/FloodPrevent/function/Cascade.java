package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.Option;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfLZZ;
import com.cj.model.func.modular.FloodPrevent.model.ModelOfTTH;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Cascade{

    public static List<ResOption> calculator(ReqFloodPrevent reqFloodPrevent) throws Exception {
        ModelOfLZZ modelOfLZZ = new ModelOfLZZ(reqFloodPrevent);
        ModelOfTTH modelOfTTH = new ModelOfTTH(reqFloodPrevent);

        //楼庄子常规调度
        List<Option> LZZ_op1=modelOfLZZ.Calculate_S1();
        //头屯河常规调度
        modelOfTTH.setQ_Input2(LZZ_op1);
        List<Option> TTH_op1=modelOfTTH.Calculate_S1();
        //两库常规调度结果保存
        File tempFile1 = File.createTempFile("option1",".xlsx");
        String path1= tempFile1.getAbsolutePath();
        List<Option> option1 = new ArrayList<>();
        option1.addAll(LZZ_op1);
        option1.addAll(TTH_op1);
        Write(path1,option1);


        //楼庄子灵活调度
        List<Option> LZZ_op2=modelOfLZZ.Calculate_S2();
        //头屯河灵活调度
        modelOfTTH.setQ_Input2(LZZ_op2);
        List<Option> TTH_op2=modelOfTTH.Calculate_S2();
        //两库灵活调度结果保存
        File tempFile2 = File.createTempFile("option2",".xlsx");
        String path2= tempFile2.getAbsolutePath();
        List<Option> option2 = new ArrayList<>();
        option2.addAll(LZZ_op2);
        option2.addAll(TTH_op2);
        Write(path2,option2);



        //楼庄子预泄调度
        List<Option> LZZ_op3=modelOfLZZ.Calculate_S3();
        //头屯河预泄调度
        modelOfTTH.setQ_Input2(LZZ_op3);
        List<Option> TTH_op3=modelOfTTH.Calculate_S3();
        //两库预泄调度结果保存
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
        resOption2.setName(reqFloodPrevent.getProgrammeName()+"-灵活调度");
        result.add(resOption2);

        ResOption resOption3 = new ResOption();
        resOption3.setPath(path3);
        resOption3.setName(reqFloodPrevent.getProgrammeName()+"-预泄调度");
        result.add(resOption3);

        return result;
    }

    public static void Write(String path,List<Option> options) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

            double Q_in = BigDecimal.valueOf(line.getQIn()).setScale(3, RoundingMode.HALF_UP).doubleValue();
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

}
