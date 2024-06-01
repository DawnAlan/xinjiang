package com.cj.model.func.modular.FloodPredict.utils;

import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Tools {

    /**
     * 把输出的表格转为临时文件
     *
     * @param Flood
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @SneakyThrows
    public static TemporaryXlsx resultToXlsx(List<Flood> Flood){
        File tempFile = File.createTempFile("PRE_RESULT", ".xlsx");
        String path = tempFile.getAbsolutePath();
        Object[][] floodObject = new Object[Flood.size()][16];
        for (int i = 0; i < Flood.size(); i++) {
            floodObject[i][0] = Flood.get(i).getLocation();
            floodObject[i][1] = Flood.get(i).getScale();
            floodObject[i][2] = Flood.get(i).getPeakIndex();
            floodObject[i][3] = Flood.get(i).getTime();
            floodObject[i][4] = Flood.get(i).getPreQ();
            floodObject[i][5] = Flood.get(i).getWaterLevel();
            floodObject[i][6] = Flood.get(i).getPeakFlood();
            floodObject[i][7] = Flood.get(i).getPeakTime();
            floodObject[i][8] = Flood.get(i).getPeakDuration();
            floodObject[i][9] = Flood.get(i).getFloodVolume();
            floodObject[i][10] = Flood.get(i).getQComposition();
            floodObject[i][11] = Flood.get(i).getQCause();
            floodObject[i][12] = Flood.get(i).getFloodLevel();
            floodObject[i][13] = Flood.get(i).getOutQ();
            floodObject[i][14] = Flood.get(i).getWarningTime();
            floodObject[i][15] = Flood.get(i).getRainProcess();
        }
        ExcelTool.writeFloodExcel(path, "预报结果", floodObject);
        TemporaryXlsx result = new TemporaryXlsx();
        result.setPath(path);
        result.setSheetName("预报结果");
        return result;
    }

    /**
     * 相同列的Object相加
     *
     * @param input
     * @return
     */
    public static Object[][] AddObject(List<Object[][]> input) {
        int n = input.size();
        int rowNum = 0;
        int lineNum = 0;
        for (int i = 0; i < n; i++) {
            rowNum += input.get(i).length;
            if (lineNum < input.get(i)[0].length) {
                lineNum = input.get(i)[0].length;
            }
        }
        Object[][] result = new Object[rowNum][lineNum];
        int row = 0;
        for (int i = 0; i < n; i++) {
            Object[][] inObject = input.get(i);
            for (int j = 0; j < inObject.length; j++) {
                for (int k = 0; k < inObject[0].length; k++) {
                    result[j + row][k] = inObject[j][k];
                }
            }
            row += inObject.length;
        }
        return result;
    }

    public static String array2String(Integer[] array) {
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

    /**
     * 求数组中的最大值
     *
     * @param array 一维的数组
     * @return 数组最大值
     */
    public static double max(double[] array) {
        int length = array.length;
        int tem = 0;
        for (int i = 1; i < length; i++)
            if (array[i] > array[tem]) tem = i;
        return array[tem];
    }

}
