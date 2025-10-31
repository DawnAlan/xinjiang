package com.cj.model.func.modular.FloodPredict.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cj.model.func.modular.FloodPredict.Calibration.entity.ShanbeiParam;
import com.cj.model.func.modular.FloodPredict.entity.FloodBasin;
import com.cj.model.func.modular.FloodPredict.entity.Hydrology;
import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import com.cj.model.func.modular.entity.Flood;
import lombok.SneakyThrows;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jetbrains.annotations.TestOnly;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cj.model.func.modular.FloodPredict.utils.TimeUtils.*;

public class Tools {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        Object[][] floodObject = new Object[Flood.size()][21];
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
            floodObject[i][16] = Flood.get(i).getConfluenceTime();
            floodObject[i][17] = Flood.get(i).getFloodVolumeOne();
            floodObject[i][18] = Flood.get(i).getFloodVolumeThree();
            floodObject[i][19] = Flood.get(i).getFloodVolumeSeven();
            floodObject[i][20] = Flood.get(i).getPeakVolume();
        }
        ExcelTool.writeFloodExcel(path, "预报结果", floodObject);
        TemporaryXlsx result = new TemporaryXlsx();
        result.setPath(path);
        result.setSheetName("预报结果");
        return result;
    }

    @SneakyThrows
    public static TemporaryXlsx resultToXlsxNoSave(List<Flood> Flood){
        // 显示指定路径创建临时文件
        //String tempDir = "D:\\tth_system\\file";
        String tempDir = "/file";
        File tempFileWithPath = new File(tempDir, "PRE_RESULT.xlsx");
        tempFileWithPath.createNewFile();
        //File tempFile = File.createTempFile("PRE_RESULT", ".xlsx");
        String path = tempFileWithPath.getAbsolutePath();
        Object[][] floodObject = new Object[Flood.size()][21];
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
            floodObject[i][16] = Flood.get(i).getConfluenceTime();
            floodObject[i][17] = Flood.get(i).getFloodVolumeOne();
            floodObject[i][18] = Flood.get(i).getFloodVolumeThree();
            floodObject[i][19] = Flood.get(i).getFloodVolumeSeven();
            floodObject[i][20] = Flood.get(i).getPeakVolume();
        }
        ExcelTool.writeFloodExcel(path, "预报结果", floodObject);
        TemporaryXlsx result = new TemporaryXlsx();
        result.setPath(path);
        result.setSheetName("预报结果");
        return result;
    }



    /**
     * 把输出的表格转为临时文件
     *
     * @param Flood
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    @SneakyThrows
    public static void testXlsx(List<Flood> Flood){
        Object[][] floodObject = new Object[Flood.size()][21];
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
            floodObject[i][16] = Flood.get(i).getConfluenceTime();
            floodObject[i][17] = Flood.get(i).getFloodVolumeOne();
            floodObject[i][18] = Flood.get(i).getFloodVolumeThree();
            floodObject[i][19] = Flood.get(i).getFloodVolumeSeven();
            floodObject[i][20] = Flood.get(i).getPeakVolume();
        }
        ExcelTool.writeFloodExcel("D:\\204\\2.头屯河\\后期维护\\25汛期参数\\场次洪水本地测试文件.xlsx", "预报结果", floodObject);
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

    @SneakyThrows
    public static FloodBasin readFloodBasin(String filePath) {
        FloodBasin basin = new FloodBasin();
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        String basinStr =  new String(encoded);
        JSONObject object = JSON.parseObject(basinStr);
        assert object != null;
        try{
            basin.setName(object.getString("name"));
            List<String> rainStation= JSON.parseArray(object.get("rainStation").toString(), String.class);
            List<Hydrology> reservoirs= JSON.parseArray(object.get("hydrologies").toString(), Hydrology.class);
            // 解析为 Map<String, Object>
            Map<String, Object> tempMap = JSON.parseObject(object.get("paramMap").toString(), Map.class);
            // 手动转换
            Map<String, ShanbeiParam> paramMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : tempMap.entrySet()) {
                ShanbeiParam param = JSON.parseObject(JSON.toJSONString(entry.getValue()), ShanbeiParam.class);
                paramMap.put(entry.getKey(), param);
            }
            // 解析为 Map<String, Object>
            Map<String, Object> tempMap1 = JSON.parseObject(object.get("paramRange").toString(), Map.class);
            // 手动转换
            Map<String, List<FloodBasin.Item>> paramRange = new HashMap<>();
            for (Map.Entry<String, Object> entry : tempMap1.entrySet()) {
                List<FloodBasin.Item> param = JSON.parseArray(JSON.toJSONString(entry.getValue()), FloodBasin.Item.class);
                paramRange.put(entry.getKey(), param);
            }
            basin.setRainStation(rainStation);
            basin.setHydrologies(reservoirs);
            basin.setParamMap(paramMap);
            basin.setParamRange(paramRange);
            return basin;
        }
        catch (Exception e){
            throw new RuntimeException("流域参数读取有误");
        }
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

    @SneakyThrows
    public static void getReferenceWaterExcel(){
        List<String> level = Arrays.asList("十年一遇","二十年一遇","五十年一遇","百年一遇","千年一遇");
        List<String> rainStation = Arrays.asList("加普沙自动雨量站","东南沟自动雨量站","宰尔德自动雨量站","无名沟自动雨量站","八一林场自动雨量站","萨尔达万自动雨量站","煤矿沟自动雨量站",
                "黑沟自动雨量站", "喀什沟自动雨量站","制材厂自动雨量站","甘沟雨量站","小渠子雨量站","团结一队雨量站","头屯河水库雨量站");
        for (String s : level) {
            Object[][] rainObject = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\模拟降雨.xlsx", s);
            Object[][] result = new Object[rainObject.length][(rainObject[0].length - 1) * 2 + 1];
            for (int j = 0; j < result.length; j++) {
                result[j][0] = rainObject[j][0];
                for (int i = 1; i < rainObject[0].length; i++) {
                    result[j][i*2] = rainObject[j][i];
                    result[0][i*2] = "\"" + rainStation.get(i-1) + "\"";
                }
            }
            for (int j = 1; j < result.length; j++) {
                for (int i = 1; i < rainObject[0].length; i++) {
                    result[j][i*2-1] = "\"" + rainStation.get(i-1) + "\"";
                }
            }
            ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\模拟降雨-前端格式.xlsx",s,result);
        }
    }
    @Test
    @SneakyThrows
    public void getDaysRainExcel(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeUtils tu = new TimeUtils();
        Object[][] rainHour = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年雨量站数据.xlsx","Hour");
        // 转换为按天汇总的数据
        int days = duration((Date) rainHour[1][0],(Date) rainHour[rainHour.length-1][0],"日");
        Object[][] rainDay = new Object[days+1][rainHour[0].length];
        for (int j = 1; j < rainHour[0].length; j++) {
            double sum = 0.0;
            int l = 1 ;
            for (int i = 1; i < rainHour.length-1; i++) {
                if (DateCompare((Date) rainHour[i][0],(Date) rainHour[i+1][0],"日")){
                    sum += (Double)rainHour[i][j];
                }else {
                    rainDay[l][0] = addCalendar((Date) rainHour[1][0],"日",l-1);
                    rainDay[l][j] = sum+(Double)rainHour[i][j];
                    l++;
                    sum = 0.0;
                }
            }
        }
        rainDay[0]=rainHour[0];
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年雨量站数据.xlsx","Day",rainDay);
    }



    /**
     * 将雨量数据转化为所需要格式，注意要先按时间、雨量站排序（同一小时的雨量站要集中在一起）
     */
    @Test
    @SneakyThrows
    public void getHoursRainExcel(){

        TimeUtils tu = new TimeUtils();
        Object[][] rain = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\雨量站数据7~9.xlsx","2024上游雨量站");
        Date start = sdf.parse("2024-07-01 00:00:00");
        Date end = sdf.parse("2024-09-07 00:00:00");
        int duration = tu.duration(start,end,"小时");
        Object[][] result = new Object[duration+1][11];
        result[0][0] = "时间";
        result[0][1] = "加普沙自动雨量站";
        result[0][2] = "东南沟自动雨量站";
        result[0][3] = "宰尔德自动雨量站";
        result[0][4] = "无名沟自动雨量站";
        result[0][5] = "八一林场自动雨量站";
        result[0][6] = "萨尔达万自动雨量站";
        result[0][7] = "煤矿沟自动雨量站";
        result[0][8] = "黑沟自动雨量站";
        result[0][9] = "喀什沟自动雨量站";
        result[0][10] = "制材厂自动雨量站";
        int l = 0;
        for (int i = 1; i < rain.length-1; i++) {
            System.out.println(i);
            Date time0 = (Date) rain[i-1][2];
            Date time = (Date) rain[i][2];
//            Date time0 = sdf.parse((String) rain[i-1][2]);
//            Date time = sdf.parse((String) rain[i][2]);
            if (!DateCompare(time0,time,"小时")) l += tu.duration(time0,time,"小时");
            int minute = tu.getSpecificDate(time).get("分钟");
            Date date = tu.addCalendar(start,"小时",l);

            if (DateCompare(time,date, "小时")){
                result[l+1][0] = date;
                if (minute<=30){
                    if (rain[i][0].equals("加普沙自动雨量站")){
                        if (result[l+1][1]!=null){
                            result[l+1][1] =(double)result[l+1][1] + (double)rain[i][1];
                        }else {
                            result[l+1][1] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("东南沟自动雨量站")){
                        if (result[l+1][2]!=null){
                            result[l+1][2] =(double)result[l+1][2] + (double)rain[i][1];
                        }else {
                            result[l+1][2] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("宰尔德自动雨量站")){
                        if (result[l+1][3]!=null){
                            result[l+1][3] =(double)result[l+1][3] + (double)rain[i][1];
                        }else {
                            result[l+1][3] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("无名沟自动雨量站")){
                        if (result[l+1][4]!=null){
                            result[l+1][4] =(double)result[l+1][4] + (double)rain[i][1];
                        }else {
                            result[l+1][4] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("八一林场自动雨量站")){
                        if (result[l+1][5]!=null){
                            result[l+1][5] =(double)result[l+1][5] + (double)rain[i][1];
                        }else {
                            result[l+1][5] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("萨尔达万自动雨量站")){
                        if (result[l+1][6]!=null){
                            result[l+1][6] =(double)result[l+1][6] + (double)rain[i][1];
                        }else {
                            result[l+1][6] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("煤矿沟自动雨量站")){
                        if (result[l+1][7]!=null){
                            result[l+1][7] =(double)result[l+1][7] + (double)rain[i][1];
                        }else {
                            result[l+1][7] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("黑沟自动雨量站")){
                        if (result[l+1][8]!=null){
                            result[l+1][8] =(double)result[l+1][8] + (double)rain[i][1];
                        }else {
                            result[l+1][8] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("喀什沟自动雨量站")){
                        if (result[l+1][9]!=null){
                            result[l+1][9] =(double)result[l+1][9] + (double)rain[i][1];
                        }else {
                            result[l+1][9] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("制材厂自动雨量站")){
                        if (result[l+1][10]!=null){
                            result[l+1][10] =(double)result[l+1][10] + (double)rain[i][1];
                        }else {
                            result[l+1][10] = (double)rain[i][1];
                        }
                    }
                } else {
                    if (rain[i][0].equals("加普沙自动雨量站")){
                        if (result[l+2][1]!=null){
                            result[l+2][1] = (double)result[l+2][1] + (double)rain[i][1];
                        }else {
                            result[l+2][1] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("东南沟自动雨量站")){
                        if (result[l+2][2]!=null){
                            result[l+2][2] = (double)result[l+2][2] + (double)rain[i][1];
                        }else {
                            result[l+2][2] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("宰尔德自动雨量站")){
                        if (result[l+2][3]!=null){
                            result[l+2][3] = (double)result[l+2][3] + (double)rain[i][1];
                        }else {
                            result[l+2][3] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("无名沟自动雨量站")){
                        if (result[l+2][4]!=null){
                            result[l+2][4] = (double)result[l+2][4] + (double)rain[i][1];
                        }else {
                            result[l+2][4] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("八一林场自动雨量站")){
                        if (result[l+2][5]!=null){
                            result[l+2][5] = (double)result[l+2][5] + (double)rain[i][1];
                        }else {
                            result[l+2][5] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("萨尔达万自动雨量站")){
                        if (result[l+2][6]!=null){
                            result[l+2][6] = (double)result[l+2][6] + (double)rain[i][1];
                        }else {
                            result[l+2][6] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("煤矿沟自动雨量站")){
                        if (result[l+2][7]!=null){
                            result[l+2][7] = (double)result[l+2][7] + (double)rain[i][1];
                        }else {
                            result[l+2][7] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("黑沟自动雨量站")){
                        if (result[l+2][8]!=null){
                            result[l+2][8] = (double)result[l+2][8] + (double)rain[i][1];
                        }else {
                            result[l+2][8] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("喀什沟自动雨量站")){
                        if (result[l+2][9]!=null){
                            result[l+2][9] = (double)result[l+2][9] + (double)rain[i][1];
                        }else {
                            result[l+2][9] = (double)rain[i][1];
                        }
                    }
                    if (rain[i][0].equals("制材厂自动雨量站")){
                        if (result[l+2][10]!=null){
                            result[l+2][10] = (double)result[l+2][10] + (double)rain[i][1];
                        }else {
                            result[l+2][10] = (double)rain[i][1];
                        }
                    }
                }
            }
        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年雨量站数据7~9.xlsx","2024上游雨量站",result);
    }
    @Test
    @SneakyThrows
    public void getHoursFlowExcel(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeUtils tu = new TimeUtils();
        Object[][] flow = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\流量站数据7~9.xlsx","2024上游流量站");
        Date start = sdf.parse("2024-07-01 00:00:00");
        Date end = sdf.parse("2024-09-07 00:00:00");
        int duration = tu.duration(start,end,"小时");
        Object[][] result = new Object[duration+1][11];
        result[0][0] = "时间";
        result[0][1] = "三号桥";
        result[0][2] = "楼庄子进库";
        Map<String,Object[][]> flowMap = Arrays.stream(flow)
                .collect(Collectors.groupingBy(
                        row -> row[0], // 按照第一列的名称分组
                        Collectors.mapping(
                                row -> (Object[]) row, // 将每行转换为 Object[]
                                Collectors.toList() // 收集到 List<Object[]>
                        )
                )).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().toArray(new Object[0][0]) // 转换 List<Object[]> 为 Object[][]
                ));
        Object[][] three = flowMap.get("3号桥水位站");
        Object[][] lzz = Arrays.stream(flowMap.get("楼庄子进库流量"))
                .collect(Collectors.groupingBy(
                        row -> truncateToHour((Date) row[2]), // 按小时分组
                        Collectors.averagingDouble(row -> (double) row[1]) // 计算每小时的均值
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // 按时间顺序排序
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .toArray(Object[][]::new);
        for (int i = 0; i < duration; i++) {
            Date date = tu.addCalendar(start,"小时",i);
            result[i+1][0] = date;

            for (int j = 0; j < three.length; j++) {
                Date date1 = (Date) three[j][2];
                if (DateCompare(date,date1,"小时")){
                    result[i+1][1] = three[j][1];
                }
            }

            for (int j = 0; j < lzz.length; j++) {
                Date date1 = (Date) lzz[j][0];
                if (DateCompare(date,date1,"小时")){
                    result[i+1][2] = lzz[j][1];
                }
            }

        }
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年流量站数据7~9.xlsx","2024上游流量站",result);

    }
    // 将 Date 对象截断到整点小时
    private static Date truncateToHour(Date date) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        zdt = zdt.withMinute(0).withSecond(0).withNano(0);
        return Date.from(zdt.toInstant());
    }


    /**
     * 获取连续时间序列数据
     */
    @Test
    @SneakyThrows
    public void getContinuousSequences(){
        Object[][] input = ExcelTool.readStringExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年流量站数据7~9.xlsx","lzz");
        Date start = sdf.parse("2024-07-01 00:00:00");
        Date end = sdf.parse("2024-09-06 16:00:00");
        int l = duration(start,end,"小时");
        // 计算两个日期之间的小时数
        String period = "小时";
        Object[][] result = new Object[l][2];
        List<Date> dateList = new ArrayList<>();
        List<Double> dataList = new ArrayList<>();
        for (int i = 1; i < input.length; i++) {
            if (input[i][0] != null && input[i].length>=2){
                dateList.add((Date) input[i][0]);
                dataList.add((Double) input[i][1]);
            }
        }
        for (int i = 1; i < l; i++) {
            boolean existTime = false;
            Date date = TimeUtils.addCalendar(start,period,i-1);
            for (int j = 1; j < input.length; j++) {
                if (input[j][0] != null && input[j].length>=2 && TimeUtils.DateCompare(date, (Date) input[j][0],period)){
                    existTime = true;
                    result[i] = input[j];
                }
            }
            if (!existTime){
                result[i][0] = date;
                int m = new TimeUtils().findNearestTime(dateList,date);
                if (m - 1 >= 0){
                    int duration = TimeUtils.duration(dateList.get(m-1),dateList.get(m),period);
                    int multiplier = TimeUtils.duration(dateList.get(m-1),date,period);
                    result[i][1] = dataList.get(m-1)+(dataList.get(m)-dataList.get(m-1))/duration*multiplier;
                } else if (m + 1< dateList.size()) {
                    int multiplier = TimeUtils.duration(dateList.get(m),date,period);
                    int duration = TimeUtils.duration(dateList.get(m),dateList.get(m+1),period);
                    result[i][1] = dataList.get(m)+(dataList.get(m+1)-dataList.get(m))/duration*multiplier;
                }else {
                    result[i][1] = dataList.get(m);
                }
            }
        }
        result[0] = input[0];
        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\资料\\3.场次数据\\24年流量站数据7~9.xlsx","lzzFlow",result);
    }

    public static String readJSONFromFile(String filePath) throws IOException {
        // Read file content as string
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded);
    }


}


