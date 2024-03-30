package com.cj.model.func.modular.FloodPredict.utils;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Tools {

    /**
     * 把输出的表格转为临时文件
     * @param Flood
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static TemporaryXlsx ObjectToXlsx(Object[][] Flood) throws IOException {
        File tempFile = File.createTempFile("PRE_RESULT",".xlsx");
        String path= tempFile.getAbsolutePath();
        ExcelTool.writeFloodExcel(path, "预报结果", Flood);
        TemporaryXlsx result=new TemporaryXlsx();
        result.setPath(path);
        result.setSheetName("预报结果");
        return result;
    }

    /**
     * 相同列的Object相加
     * @param input
     * @return
     */
    public static Object[][] AddObject(List<Object[][]> input){
        int n = input.size();
        int rowNum = 0;
        int lineNum= 0;
        for (int i = 0; i < n; i++) {
            rowNum += input.get(i).length;
            if (lineNum<input.get(i)[0].length){
                lineNum=input.get(i)[0].length;
            }
        }
        Object[][] result = new Object[rowNum][lineNum];
        int row = 0;
        for (int i = 0; i < n; i++) {
            Object[][] inObject = input.get(i);
            for (int j = 0; j < inObject.length; j++) {
                for (int k = 0; k < inObject[0].length; k++) {
                    result[j+row][k]=inObject[j][k];
                }
            }
            row +=inObject.length;
        }
        return result;
    }

    public static String array2String(int[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1) {
                str += array[i];
            } else {
                str += array[i] + ",";
            }
        }
        return str;
    }

    /**求数组中的最大值
     * @param array		一维的数组
     * @return			数组最大值
     */
    public static double max(double[] array){
        int length = array.length;
        int tem = 0;
        for(int i = 1;i<length;i++)
            if(array[i] > array[tem]) tem = i;
        return array[tem];
    }

}
