package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.common.util.UUIDUtils;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.modular.FloodPredict.entity.DataWrite;
import com.cj.model.func.modular.FloodPredict.entity.PredictInputData;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 通用Excel操作方法，定制时请自行书写
 */
@Component
public class ExcelTool {
    public static void writeDoubleExcel(String path, String sheetName, double[][] data) throws IOException, InvalidFormatException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
// 先判断工作簿是否存在，不存在则创建，存在则清空
        if (sheet != null) {
            try {
                // 清空工作表中的数据
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        sheet.removeRow(row);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 在单元格中写入数据
            sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[i][j]);
                }
            }
        } else {
            sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[i][j]);
                }
            }
        }
        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeExcel(String path, List<DataWrite> historyData){
        XSSFWorkbook workbook = null;

        // 检查文件是否存在
        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(path);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 创建一个新的工作表
            workbook = new XSSFWorkbook();
        }
        for (int i = 0; i < historyData.size(); i++) {
            fillObjectSheet(workbook,historyData.get(i).getSheetName(),historyData.get(i).getData());
        }

        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fillObjectSheet(XSSFWorkbook workbook, String sheetName, Object[][] data)  {
        assert workbook != null;
        boolean sheetExists = false;
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex != -1) {
            sheetExists = true;
        }
        XSSFSheet sheet;
        if (sheetExists) {
            sheet = workbook.getSheet(sheetName);
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
// 先判断工作簿是否存在，不存在则创建，存在则清空
        if (sheet != null) {
            try {
                // 清空工作表中的数据
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        sheet.removeRow(row);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 在单元格中写入数据
            fillSheet(workbook, sheet, data);
        } else {
            sheet = workbook.createSheet(sheetName);
            fillSheet(workbook, sheet, data);
        }
    }

    public static void writeObjectExcel(String path, String sheetName, Object[][] data)  {
        XSSFWorkbook workbook = null;

        // 检查文件是否存在
        File file = new File(path);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(path);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 创建一个新的工作表
            workbook = new XSSFWorkbook();
        }

        assert workbook != null;
        boolean sheetExists = false;
        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex != -1) {
            sheetExists = true;
        }
        XSSFSheet sheet;
        if (sheetExists) {
            sheet = workbook.getSheet(sheetName);
        } else {
            sheet = workbook.createSheet(sheetName);
        }
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
// 先判断工作簿是否存在，不存在则创建，存在则清空
        if (sheet != null) {
            try {
                // 清空工作表中的数据
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        sheet.removeRow(row);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 在单元格中写入数据
            fillSheet(workbook, sheet, data);
        } else {
            sheet = workbook.createSheet(sheetName);
            fillSheet(workbook, sheet, data);
        }
        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把List<PredictInputData>格式写表格方便查看
     *
     * @param path
     * @param sheetName
     * @param inputDataList
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static void writeListPredictListExcel(String path, String sheetName, List<PredictInputData> inputDataList) throws IOException, InvalidFormatException {
        Object[][] inputData = new Object[inputDataList.size() + 1][5];
        inputData[0][0] = "站点";
        inputData[0][1] = "日期";
        inputData[0][2] = "径流";
        inputData[0][3] = "温度";
        inputData[0][4] = "降水";
        for (int i = 1; i < inputDataList.size(); i++) {
            inputData[i][0] = inputDataList.get(i).getLocation();
            inputData[i][1] = inputDataList.get(i).getDates();
            inputData[i][2] = inputDataList.get(i).getFlow();
            inputData[i][3] = inputDataList.get(i).getTemperature();
            inputData[i][4] = inputDataList.get(i).getRainfall();
        }
        writeObjectExcel(path, sheetName, inputData);
    }

    public static void writeList2DoubleExcel(String path, String sheetName, List<List<Double>> data) throws IOException, InvalidFormatException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
// 先判断工作簿是否存在，不存在则创建，存在则清空
        if (sheet != null) {
            try {
                // 清空工作表中的数据
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        sheet.removeRow(row);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 在单元格中写入数据
            sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < data.get(0).size(); i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data.get(j).get(i));
                }
            }
        } else {
            sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < data.get(0).size(); i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data.get(j).get(i));
                }
            }
        }
        try {
            FileOutputStream fop = new FileOutputStream(path);
            workbook.write(fop);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFloodExcel(String path, String sheetName, Object[][] data) throws IOException {

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet(sheetName);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

        Row row0 = sheet.createRow(0);
        Cell cell0 = row0.createCell(0);
        cell0.setCellValue("LOCATION");//断面位置
        Cell cell1 = row0.createCell(1);
        cell1.setCellValue("SCALE");//预报尺度
        Cell cell2 = row0.createCell(2);
        cell2.setCellValue("PEAK_INDEX");//洪号
        Cell cell3 = row0.createCell(3);
        cell3.setCellValue("TIME");//时间
        Cell cell4 = row0.createCell(4);
        cell4.setCellValue("PRE_Q");//预报流量
        Cell cell5 = row0.createCell(5);
        cell5.setCellValue("WATER_LEVEL");//水位
        Cell cell6 = row0.createCell(6);
        cell6.setCellValue("PEAK_FLOOD");//洪峰
        Cell cell7 = row0.createCell(7);
        cell7.setCellValue("PEAK_TIME");//峰现时间
        Cell cell8 = row0.createCell(8);
        cell8.setCellValue("PEAK_DURATION");//洪峰持续时间
        Cell cell9 = row0.createCell(9);
        cell9.setCellValue("FLOOD_VOLUME");//洪量
        Cell cell10 = row0.createCell(10);
        cell10.setCellValue("Q_COMPOSITION");//洪量组成
        Cell cell11 = row0.createCell(11);
        cell11.setCellValue("Q_CAUSE");//洪水来源
        Cell cell12 = row0.createCell(12);
        cell12.setCellValue("FLOOD_LEVEL");//洪水等级
        Cell cell13 = row0.createCell(13);
        cell13.setCellValue("OUT_Q");//出库径流
        Cell cell14 = row0.createCell(14);
        cell14.setCellValue("WARNING_TIME");//超过警戒水位时间
        Cell cell15 = row0.createCell(15);
        cell15.setCellValue("RAIN_PROCESS");//雨情
        // 先判断工作簿是否存在，不存在则创建，存在则继续填写
        if (sheet != null) {
            int nextRowNum = sheet.getLastRowNum() + 1;
            // 在单元格中写入数据
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + nextRowNum);
                for (int j = 0; j < data[0].length; j++) {
                    Cell cell = row.createCell(j);
                    if (data[i][j] instanceof String) {
                        cell.setCellValue((String) data[i][j]);
                    } else if (data[i][j] instanceof Double) {
                        cell.setCellValue((Double) data[i][j]);
                    } else if (data[i][j] instanceof Integer) {
                        cell.setCellValue((Integer) data[i][j]);
                    } else if (data[i][j] instanceof Date) {
                        cell.setCellValue((Date) data[i][j]);
                        cell.setCellStyle(cellStyle);
                    }
                }
            }
        } else {
            sheet = wb.createSheet(sheetName);
            fillSheet(wb, sheet, data);
        }

        FileOutputStream fileOut = new FileOutputStream(path);
        wb.write(fileOut);
        fileOut.close();

        try {
            FileOutputStream fop = new FileOutputStream(path);
            wb.write(fop);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void fillSheet(Workbook wb, Sheet sheet, Object[][] data) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                if (data[i][j] instanceof String) {
                    cell.setCellValue((String) data[i][j]);
                } else if (data[i][j] instanceof Double) {
                    cell.setCellValue((Double) data[i][j]);
                } else if (data[i][j] instanceof Integer) {
                    cell.setCellValue((Integer) data[i][j]);
                } else if (data[i][j] instanceof Date) {
                    cell.setCellValue((Date) data[i][j]);
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }

    public static Map<String,Object[][]> readExcel(String path, String name) throws IOException {
        Map<String,Object[][]> result = new HashMap<>();
        String savePath = System.getProperty("java.io.tmpdir")+"temp"+ UUIDUtils.getUUID() +".xlsx";
        String filePath = path+name+".xlsx";
        downloadFile(filePath, savePath);
        InputStream fis = new FileInputStream(savePath);
        ZipSecureFile.setMinInflateRatio(-1.0d);
        Workbook wb = new XSSFWorkbook(fis);
        int numberOfSheets = wb.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = wb.getSheetAt(i);
            result.put(sheet.getSheetName(),readSheet(sheet));
        }
        return result;
    }

//    public static Object[][] readExcel(InputStream fis, String sheetName) throws IOException {
//        ZipSecureFile.setMinInflateRatio(-1.0d);
//        Workbook wb = new XSSFWorkbook(fis);
//        Sheet sheet = wb.getSheet(sheetName);
//        return readSheet(sheet);
//    }
    public static Object[][] readStringExcel(String path, String sheetName) throws IOException {
        InputStream fis = new FileInputStream(path);
        ZipSecureFile.setMinInflateRatio(-1.0d);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheet(sheetName);
        return readSheet(sheet);
    }


    protected static Object[][] readSheet(Sheet sheet) {
        Object[][] output = null;
        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();
        // 从最后一行向前遍历，找到第一个非空行
        for (int i = rowEnd; i >= rowStart; i--) {
            if (sheet.getRow(i) != null) {
                rowEnd = i;
                break;
            }
        }
        output = new Object[rowEnd + 1][];
        for (int i = rowStart; i <= rowEnd; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            int cellStart = row.getFirstCellNum();
            int cellEnd = row.getLastCellNum();
            if (cellStart < 0) continue;
            output[i] = new Object[cellEnd];
            for (int j = cellStart; j < cellEnd; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) continue;
                CellType type = cell.getCellType();
                if (type == CellType.STRING) output[i][j] = cell.getStringCellValue();

                else if (type == CellType.NUMERIC) {
                    if (DateUtil.isCellDateFormatted(cell)) output[i][j] = cell.getDateCellValue();
                    else output[i][j] = cell.getNumericCellValue();
                } else if (type == CellType.BOOLEAN) output[i][j] = cell.getBooleanCellValue();
                else if (type == CellType.BLANK) output[i][j] = null;
                else if (type == CellType.FORMULA) output[i][j] = cell.getCellFormula();
                else if (type == CellType.ERROR) output[i][j] = null;
            }
        }
        return output;
    }
    @SneakyThrows
    public static String downloadFile(String fileUrl, String savePath) {
        URL url = new URL(fileUrl);
        InputStream inputStream = url.openStream();
        Paths.get(savePath,getFileName(fileUrl));
        Files.copy(inputStream, Paths.get(savePath));
        return savePath;
    }
    public static String getFileName(String fileUrl) {
        String[] parts = fileUrl.split("/");
        return parts[parts.length - 1];
    }
}
