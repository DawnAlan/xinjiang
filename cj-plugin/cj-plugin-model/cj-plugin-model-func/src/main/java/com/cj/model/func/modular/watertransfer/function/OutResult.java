package com.cj.model.func.modular.watertransfer.function;

import com.cj.model.func.modular.watertransfer.method.WaterTransfer;
import com.cj.model.func.modular.watertransfer.model.ResourceOptimizationlong_MonthTest;
import com.cj.model.func.modular.watertransfer.model.ResourceOptimizationlong_TendaysTest;
import com.cj.model.func.modular.watertransfer.model.ResourceOptimizationshort_DayTest;
import com.cj.model.func.modular.watertransfer.req.WaterTransferReq;
import com.cj.model.func.modular.watertransfer.res.Option;
import com.cj.model.func.modular.watertransfer.res.Option_Water;
import com.cj.model.func.modular.watertransfer.res.ResOption;
import lombok.Data;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class OutResult {

    public static List<ResOption> calculator(WaterTransferReq req) throws Exception
    {
        if (req.getName()==1){
            req.setTypeName("供水比例最大");
        }
        if (req.getName()==2){
            req.setTypeName("缺额最小");
        }
        if (req.getName()==3){
            req.setTypeName("单库调度");
        }
        int step=req.getTimeCalStep();

        ArrayList <WaterTransfer>Out1 = new ArrayList();
        if (step==1)
        {
            ResourceOptimizationlong_MonthTest Test_Year  =new ResourceOptimizationlong_MonthTest();
            Out1=Test_Year.ResourceOptimizationlong_MonthTest(req);

        }
        if (step==2)
        {
            ResourceOptimizationlong_TendaysTest Test_Month=new ResourceOptimizationlong_TendaysTest();
            Out1=Test_Month.ResourceOptimizationlong_TendaysTest(req);

        }
        if (step==3)
        {
            ResourceOptimizationshort_DayTest Test_Tendays=new ResourceOptimizationshort_DayTest();
            Out1=Test_Tendays.ResourceOptimizationshort_daysTest(req);
        }

        File tempFile1 = File.createTempFile("database",".xlsx");
        String path1= tempFile1.getAbsolutePath();
        List<Option> option1 = new ArrayList<>();
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++) {
            Option option = new Option();
            option.setStationName("楼庄子");
            option.setTypeName(req.getTypeName());
            option.setTime(Out1.get(0).getTime()[i]);
            option.setLevelBegin(Out1.get(0).getLevelbegin()[0][i]);
            option.setLevelEnd(Out1.get(0).getLevelend()[0][i]);
            option.setCapacity(Out1.get(0).getEndCapacity()[0][i]);
            option.setCapacity_Proportion(Out1.get(0).getCapacity_proportion()[0][i]);
            option.setInflow(Out1.get(0).getInflow()[0][i]);
            option.setOutflow(Out1.get(0).getOutflow()[0][i]);
            option.setWaterDemand(Out1.get(0).getReservoirWaterdemand()[0][i]);
            option.setWaterSupply(Out1.get(0).getReservoirWatersupply()[0][i]);
            option.setInflow_water(Out1.get(0).getInflow_water()[0][i]);
            option.setWaterAvailability(Out1.get(0).getPreSupplyWater()[0][i]);
            option.setWaterBalance(Out1.get(0).getInflowWater_supply()[0][i]);
            option.setEcology_Proportion(Out1.get(0).getProportion()[0][i]);
            option.setCity_Proportion(Out1.get(0).getProportion()[1][i]);
            option.setIndustry_Proportion(Out1.get(0).getProportion()[2][i]);
            option.setIrrigate_Proportion(Out1.get(0).getProportion()[3][i]);
            option.setGreening_Proportion(Out1.get(0).getProportion()[4][i]);
            option.setDeltawater(Out1.get(0).getDeltawater()[0][i]);
            option1.add(option);
        }
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++) {
            Option option = new Option();
            option.setStationName("头屯河");
            option.setTypeName(req.getTypeName());
            option.setTime(Out1.get(0).getTime()[i]);
            option.setLevelBegin(Out1.get(0).getLevelbegin()[1][i]);
            option.setLevelEnd(Out1.get(0).getLevelend()[1][i]);
            option.setCapacity(Out1.get(0).getEndCapacity()[1][i]);
            option.setCapacity_Proportion(Out1.get(0).getCapacity_proportion()[1][i]);
            option.setInflow(Out1.get(0).getInflow()[1][i]);
            option.setOutflow(Out1.get(0).getOutflow()[1][i]);
            option.setWaterDemand(Out1.get(0).getReservoirWaterdemand()[1][i]);
            option.setWaterSupply(Out1.get(0).getReservoirWatersupply()[1][i]);
            option.setInflow_water(Out1.get(0).getInflow_water()[1][i]);
            option.setWaterAvailability(Out1.get(0).getPreSupplyWater()[1][i]);
            option.setWaterBalance(Out1.get(0).getInflowWater_supply()[1][i]);
            option.setEcology_Proportion(Out1.get(0).getProportion()[0][i]);
            option.setCity_Proportion(Out1.get(0).getProportion()[1][i]);
            option.setIndustry_Proportion(Out1.get(0).getProportion()[2][i]);
            option.setIrrigate_Proportion(Out1.get(0).getProportion()[3][i]);
            option.setGreening_Proportion(Out1.get(0).getProportion()[4][i]);
            option.setDeltawater(Out1.get(0).getDeltawater()[1][i]);
            option1.add(option);
        }

        File tempFile2 = File.createTempFile("WaterDistribution",".xlsx");
        String path2= tempFile2.getAbsolutePath();
        List<Option_Water> option2 = new ArrayList<>();
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
        {
            Option_Water option = new Option_Water();
            option.setTime(Out1.get(0).getTime()[i]);
            option.setTypeName(req.getTypeName());
            option.setStationType("楼庄子生活");
            option.setStationName("楼庄子城市用水");
            option.setWater(Out1.get(0).getWaterSupply()[0][i]);
            if (Out1.get(0).getWaterdemand()[0][i]==0){
                double n=1;
                option.setProportion(n);
            }
            else{
                option.setProportion(Out1.get(0).getWaterSupply()[0][i]/Out1.get(0).getWaterdemand()[0][i]);
            }
            option.setWaterLack(Out1.get(0).getWaterdemand()[0][i]-Out1.get(0).getWaterSupply()[0][i]);
            option2.add(option);
        }
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
        {
            Option_Water option = new Option_Water();
            option.setTime(Out1.get(0).getTime()[i]);
            option.setTypeName(req.getTypeName());
            option.setStationType("红岩生活");
            option.setStationName("红岩城市用水");
            option.setWater(Out1.get(0).getWaterSupply()[1][i]);
            if (Out1.get(0).getWaterdemand()[1][i]==0){
                double n=1;
                option.setProportion(n);
            }
            else{
                option.setProportion(Out1.get(0).getWaterSupply()[1][i]/Out1.get(0).getWaterdemand()[1][i]);
            }
            option.setWaterLack(Out1.get(0).getWaterdemand()[1][i]-Out1.get(0).getWaterSupply()[1][i]);
            option2.add(option);
        }

        for(int x=0;x<Out1.get(0).getNameQushou().length;x++)
        {
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++) {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("工业");
                if (x==0){
                    option.setStationName("八钢工业用水");
                }
                else{
                    option.setStationName(Out1.get(0).getNameQushou()[x-1]);
                }
                option.setWater(Out1.get(0).getWaterSupplyIndustry()[0][i]);
                option.setProportion(Out1.get(0).getProportionIndustry()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemandIndustry()[x][i]-Out1.get(0).getWaterSupplyIndustry()[x][i]);
                option2.add(option);
            }
        }
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
        {
            Option_Water option = new Option_Water();
            option.setTime(Out1.get(0).getTime()[i]);
            option.setTypeName(req.getTypeName());
            option.setStationType("总西干渠");
            option.setStationName("西干渠");
            option.setWater(Out1.get(0).getWaterSupply()[3][i]);
            if (Out1.get(0).getWaterdemand()[3][i]==0){
                double n=1;
                option.setProportion(n);
            }
            else{
                option.setProportion(Out1.get(0).getWaterSupply()[3][i]/Out1.get(0).getWaterdemand()[3][i]);
            }
            option.setWaterLack(Out1.get(0).getWaterdemand()[3][i]-Out1.get(0).getWaterSupply()[3][i]);
            option2.add(option);
        }
        for(int x=0;x<Out1.get(0).getNameWest().length;x++){
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
            {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("西干渠");
                option.setStationName(Out1.get(0).getNameWest()[x]);
                option.setWater(Out1.get(0).getWaterSupply3()[x][i]);
                option.setProportion(Out1.get(0).getProportion3()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemand3()[x][i]-Out1.get(0).getWaterSupply3()[x][i]);
                option2.add(option);
            }
        }
        for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
        {
            Option_Water option = new Option_Water();
            option.setTime(Out1.get(0).getTime()[i]);
            option.setTypeName(req.getTypeName());
            option.setStationType("总东干渠");
            option.setStationName("东干渠");
            option.setWater(Out1.get(0).getWaterSupply()[4][i]);
            if (Out1.get(0).getWaterdemand()[4][i]==0){
                double n=1;
                option.setProportion(n);
            }
            else{
                option.setProportion(Out1.get(0).getWaterSupply()[4][i]/Out1.get(0).getWaterdemand()[4][i]);
            }
            option.setWaterLack(Out1.get(0).getWaterdemand()[4][i]-Out1.get(0).getWaterSupply()[4][i]);
            option2.add(option);
        }

        for (int x=0;x<Out1.get(0).getNameEast().length;x++)
        {
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
            {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("东干渠");
                option.setStationName(Out1.get(0).getNameEast()[x]);
                option.setWater(Out1.get(0).getWaterSupply4()[x][i]);
                option.setProportion(Out1.get(0).getProportion4()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemand4()[x][i]-Out1.get(0).getWaterSupply4()[x][i]);
                option2.add(option);
            }
        }

        for(int x=0;x<Out1.get(0).getNameGreenQushou().length;x++)
        {
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
            {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("渠首绿化");
                option.setStationName(Out1.get(0).getNameGreenQushou()[x]);
                option.setWater(Out1.get(0).getWaterSupplyGreenQushou()[x][i]);
                option.setProportion(Out1.get(0).getProportionGreenQushou()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemandGreenQushou()[x][i]-Out1.get(0).getWaterSupplyGreenQushou()[x][i]);
                option2.add(option);
            }
        }
        for (int x=0;x<Out1.get(0).getNameGreenEast().length;x++)
        {
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
            {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("河东绿化");
                option.setStationName(Out1.get(0).getNameGreenEast()[x]);
                option.setWater(Out1.get(0).getWaterSupplyGreenEast()[x][i]);
                option.setProportion(Out1.get(0).getProportionGreenEast()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemandGreenEast()[x][i]-Out1.get(0).getWaterSupplyGreenEast()[x][i]);
                option2.add(option);
            }
        }
        for (int x=0;x<Out1.get(0).getNameGreenWest().length;x++)
        {
            for (int i = 0; i < Out1.get(0).getInflow()[0].length; i++)
            {
                Option_Water option = new Option_Water();
                option.setTime(Out1.get(0).getTime()[i]);
                option.setTypeName(req.getTypeName());
                option.setStationType("河西绿化");
                option.setStationName(Out1.get(0).getNameGreenWest()[x]);
                option.setWater(Out1.get(0).getWaterSupplyGreenWest()[x][i]);
                option.setProportion(Out1.get(0).getProportionGreenWest()[x][i]);
                option.setWaterLack(Out1.get(0).getWaterDemandGreenWest()[x][i]-Out1.get(0).getWaterSupplyGreenWest()[x][i]);
                option2.add(option);
            }
        }
        Write(path1,option1);
        Write_peishui(path2,option2);
        List<ResOption> result = new ArrayList<>();
        ResOption resOption1 = new ResOption();
        resOption1.setPath(path1);
        resOption1.setName("表1");
        ResOption resOption2 = new ResOption();
        resOption2.setPath(path2);
        resOption2.setName("配水详情");

        result.add(resOption1);
        result.add(resOption2);


        return result;
    }
    public static void Write(String path,List<Option> options) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("StationName");
        row0.createCell(1).setCellValue("Type");
        row0.createCell(2).setCellValue("Time");
        row0.createCell(3).setCellValue("LevelBegin");
        row0.createCell(4).setCellValue("LevelEnd");
        row0.createCell(5).setCellValue("Capacity");
        row0.createCell(6).setCellValue("Capacity_proportion");
        row0.createCell(7).setCellValue("Inflow");
        row0.createCell(8).setCellValue("Outflow");
        row0.createCell(9).setCellValue("WaterDemand");
        row0.createCell(10).setCellValue("WaterSupply");
        row0.createCell(11).setCellValue("Inflow_Water");
        row0.createCell(12).setCellValue("WaterAvailability");
        row0.createCell(13).setCellValue("waterBalance");
        row0.createCell(14).setCellValue("EcologyProportion");
        row0.createCell(15).setCellValue("City_Proportion");
        row0.createCell(16).setCellValue("IndustryProportion");
        row0.createCell(17).setCellValue("IrrigateProportion");
        row0.createCell(18).setCellValue("GreeningProportion");
        row0.createCell(19).setCellValue("deltawater");

        // 获取 CreationHelper 对象来帮助处理日期
        CreationHelper createHelper = workbook.getCreationHelper();

        // 创建 CellStyle 对象，并设置日期格式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        for (int i = 0; i < options.size(); i++) {
            Option line = options.get(i);
            XSSFRow row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(line.getStationName());
            row.createCell(1).setCellValue(line.getTypeName());
            row.createCell(2).setCellValue(line.getTime());
            row.getCell(2).setCellStyle(cellStyle);
            row.createCell(3).setCellValue(line.getLevelBegin());
            row.createCell(4).setCellValue(line.getLevelEnd());
            row.createCell(5).setCellValue(line.getCapacity());
            row.createCell(6).setCellValue(line.getCapacity_Proportion());
            row.createCell(7).setCellValue(line.getInflow());
            row.createCell(8).setCellValue(line.getOutflow());
            row.createCell(9).setCellValue(line.getWaterDemand());
            row.createCell(10).setCellValue(line.getWaterSupply());
            row.createCell(11).setCellValue(line.getInflow_water());
            row.createCell(12).setCellValue(line.getWaterAvailability());
            row.createCell(13).setCellValue(line.getWaterBalance());
            row.createCell(14).setCellValue(line.getEcology_Proportion());
            row.createCell(15).setCellValue(line.getCity_Proportion());
            row.createCell(16).setCellValue(line.getIndustry_Proportion());
            row.createCell(17).setCellValue(line.getIrrigate_Proportion());
            row.createCell(18).setCellValue(line.getGreening_Proportion());
            row.createCell(19).setCellValue(line.getDeltawater());
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

    public static void Write_peishui(String path,List<Option_Water> options) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("time");
        row0.createCell(1).setCellValue("typeName");
        row0.createCell(2).setCellValue("stationType");
        row0.createCell(3).setCellValue("stationName");
        row0.createCell(4).setCellValue("water");
        row0.createCell(5).setCellValue("proportion");
        row0.createCell(6).setCellValue("waterLack");


        // 获取 CreationHelper 对象来帮助处理日期
        CreationHelper createHelper = workbook.getCreationHelper();

        // 创建 CellStyle 对象，并设置日期格式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        for (int i = 0; i < options.size(); i++) {
            Option_Water line = options.get(i);
            XSSFRow row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(line.getTime());
            row.createCell(1).setCellValue(line.getTypeName());
            row.createCell(2).setCellValue(line.getStationType());
            row.createCell(3).setCellValue(line.getStationName());
            row.createCell(4).setCellValue(line.getWater());
            row.createCell(5).setCellValue(line.getProportion());
            row.createCell(6).setCellValue(line.getWaterLack());
            row.getCell(0).setCellStyle(cellStyle);
        }
        try
        {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }


}
