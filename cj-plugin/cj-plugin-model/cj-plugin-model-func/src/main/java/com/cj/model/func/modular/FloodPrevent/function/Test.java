package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        String dataPath = "C:\\Users\\22627\\Desktop\\1.xlsx";
        String curvePath = "C:\\Users\\22627\\Desktop\\曲线表.xlsx";

        ReqFloodPrevent reqFloodPrevent = new ReqFloodPrevent();

        Map<String, List<DataFloodPrevent>> data = new HashMap<>();
        List<DataFloodPrevent> lzz = new ArrayList<>();
        List<DataFloodPrevent> lat = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(dataPath);
        XSSFSheet sheet1 = workbook.getSheetAt(0);
        XSSFSheet sheet2 = workbook.getSheetAt(0);
        int num = sheet1.getLastRowNum();
        for (int i = 1; i < num+1; i++) {
            XSSFRow row1 = sheet1.getRow(i);
            XSSFRow row2 = sheet2.getRow(i);
            DataFloodPrevent d1 = new DataFloodPrevent();
            d1.setTime(row1.getCell(0).getDateCellValue());
            d1.setPre(row1.getCell(1).getNumericCellValue());
            d1.setScale(3600);

            DataFloodPrevent d2 = new DataFloodPrevent();
            d2.setTime(row2.getCell(0).getDateCellValue());
            d2.setPre(row2.getCell(1).getNumericCellValue());
            d2.setScale(3600);
            lzz.add(d1);
            lat.add(d2);
        }
        workbook.close();
        data.put("lzz",lzz);
        data.put("lat",lat);

        List<CurveParam> curve = new ArrayList<>();
        workbook = new XSSFWorkbook(curvePath);
        XSSFSheet sheet=workbook.getSheetAt(0);
        for (int i = 1; i < 264; i++) {
            XSSFRow row = sheet.getRow(i);
            CurveParam curveParam = new CurveParam();
            curveParam.setId((int)row.getCell(1).getNumericCellValue());
            curveParam.setLevel(row.getCell(3).getNumericCellValue());
            curveParam.setValue(row.getCell(4).getNumericCellValue());
            curve.add(curveParam);
        }



        reqFloodPrevent.setData(data);
        reqFloodPrevent.setCurveParam(curve);

        reqFloodPrevent.setH1_begin(1394.5);
        reqFloodPrevent.setH1_end(1394.75);
        reqFloodPrevent.setH2_begin(988);
        reqFloodPrevent.setH2_end(988);
        reqFloodPrevent.setStep1(10);
        reqFloodPrevent.setStep2(0.05);
        reqFloodPrevent.setLimitLevels_lzz(new double[]{1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5});
        reqFloodPrevent.setLimitLevels_tth(new double[]{988,988,988,988,988,988,987,988,988,988,988,988});


        Cascade.calculator(reqFloodPrevent);
    }




}
