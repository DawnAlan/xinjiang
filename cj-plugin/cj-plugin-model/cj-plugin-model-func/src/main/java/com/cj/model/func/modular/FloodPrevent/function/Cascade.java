package com.cj.model.func.modular.FloodPrevent.function;

import com.cj.common.util.UUIDUtils;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.*;
import com.cj.model.func.modular.FloodPrevent.model.Model;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cj.model.func.modular.FloodPredict.utils.ExcelTool.downloadFile;

public class Cascade{

    public static List<ResOption> calculator(String basinStr , ReqFloodPrevent reqFloodPrevent) throws Exception {
        Basin basin = new Basin();
        //配置文件
        Read(basin,basinStr);
        //前端传来的数据
        for (Reservoir reservoir : basin.getReservoirs()) {
            //水库名
            String reservoirName = reservoir.getName();
            //水库起调水位
            try{
                reservoir.setH_begin(reqFloodPrevent.getBeginLevels().get(reservoirName));
            }
            catch (Exception e){
                throw new Exception("输入初始水位异常");
            }
            //时段长度、时间戳
            try{
                List<DataFloodPrevent> list = reqFloodPrevent.getIntervals().get(reservoirName);
                reservoir.setT_Delta(list.get(0).getScale());
                List<Date> dates = new ArrayList<>();
                for (DataFloodPrevent dataFloodPrevent : list) {
                    dates.add(dataFloodPrevent.getTime());
                }
                reservoir.setTime(dates);
            }
            catch (Exception e){
                throw new Exception("输入预报数据异常");
            }
            //动态汛限水位
            try{
                if (reqFloodPrevent.getLimitLevels() != null && reqFloodPrevent.getLimitLevels().size() == 12) {
                    reservoir.setLimitLevels(reqFloodPrevent.getLimitLevels().get(reservoirName));
                }
            }
            catch (Exception e){
                throw new Exception("输入汛限水位异常");
            }
            //动态生态流量
            try{
                if (reqFloodPrevent.getEco() != null && reqFloodPrevent.getEco().size() == 12) {
                    reservoir.setEco(reqFloodPrevent.getEco().get(reservoirName));
                }
            }
            catch (Exception e){
                throw new Exception("输入生态流量异常");
            }
            //目标函数权重
            try{
                reservoir.setWeight(reqFloodPrevent.getWeights().get(reservoirName));
            }
            catch (Exception e){
                throw new Exception("模型精度异常");
            }
        }
        //自动更新和转化的参数
        for (Reservoir reservoir : basin.getReservoirs()) {
            //库容曲线数组
            List<CurveParam> curve1 = reservoir.getCapacityCurve();
            double[][] curve2 = new double[2][curve1.size()];
            for (int i = 0; i < curve1.size(); i++) {
                curve2[0][i]=curve1.get(i).getLevel();
                curve2[1][i]=curve1.get(i).getValue();
            }
            reservoir.setLV_Curve(curve2);
            //闸门曲线数组
            Map<String,double[][]> curve_temp = new LinkedHashMap<>();
            List<Gate> gates = reservoir.getGates();
            for (Gate gate : gates) {
                String gateName = gate.getName();
                List<CurveParam> curve = gate.getCurve();
                double[][] curve3 = new double[2][curve.size()];
                for (int j = 0; j < curve.size(); j++) {
                    curve3[0][j] = curve.get(j).getLevel();
                    curve3[1][j] = curve.get(j).getValue();
                }
                curve_temp.put(gateName, curve3);
            }
            reservoir.setLQ_Curves(curve_temp);
            //汛限水位列表
            List<Double> limits = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                limits.add(reservoir.getLimitLevels()[i]);
            }
            reservoir.setLimits(limits);
            //生态流量列表
            List<Double> MinQ = new ArrayList<>();
            for (int i = 0; i < reservoir.getTime().size(); i++) {
                Date date = reservoir.getTime().get(i);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                int mon= calendar.get(Calendar.MONTH);
                MinQ.add(reservoir.getEco()[mon]);
            }
            reservoir.setMinQ(MinQ);
            //特征库容
            reservoir.setDeadVolume(Model.GetV(reservoir,reservoir.getDeadLevel()));
            reservoir.setLimitVolume(Model.GetV(reservoir,reservoir.getLimitLevel()));
            reservoir.setNormalVolume(Model.GetV(reservoir,reservoir.getNormalLevel()));
            reservoir.setHeightVolume(Model.GetV(reservoir,reservoir.getHeightLevel()));
            reservoir.setDesignVolume(Model.GetV(reservoir,reservoir.getDesignLevel()));
            reservoir.setProofVolume(Model.GetV(reservoir,reservoir.getProofLevel()));
        }
        //方案计算
        List<Option> option1 = new ArrayList<>();
        List<Option> option2 = new ArrayList<>();
        List<Option> option3 = new ArrayList<>();
        List<Double> out_S1= new ArrayList<>();
        List<Double> out_S2= new ArrayList<>();
        List<Double> out_S3= new ArrayList<>();
        for (int i = 0; i < basin.getReservoirs().size(); i++) {
            //水库
            Reservoir reservoir = basin.getReservoirs().get(i);
            //水库名
            String reservoirName = reservoir.getName();
            //断面上游区间流量
            List<DataFloodPrevent> q_Interval;
            try{
                q_Interval  = reqFloodPrevent.getIntervals().get(reservoirName);
            }
            catch (Exception e){
                throw new Exception("输入断面预报数据异常");
            }
            //常规调度
            Model.setQInput(reservoir,q_Interval,out_S1);
            List<Option> options_s1 = Model.Calculate_S1(reservoir);
            //灵活调度
            Model.setQInput(reservoir,q_Interval,out_S2);
            List<Option> options_s2 = Model.Calculate_S2(reservoir);
            //优化调度
            Model.setQInput(reservoir,q_Interval,out_S3);
            List<Option> options_s3 = Model.Calculate_S3(reservoir);
            //保存出库流量
            out_S1= new ArrayList<>();
            out_S2= new ArrayList<>();
            out_S3= new ArrayList<>();
            for (int j = 0; j < options_s1.size(); j++) {
                out_S1.add(options_s1.get(j).getQOut());
                out_S2.add(options_s2.get(j).getQOut());
                out_S3.add(options_s3.get(j).getQOut());
            }
            //保存调度结果
            option1.addAll(options_s1);
            option2.addAll(options_s2);
            option3.addAll(options_s3);
        }
        //调度结果保存
        File tempFile1 = File.createTempFile("option1",".xlsx");
        String path1= tempFile1.getAbsolutePath();
        Write(path1,option1);
        File tempFile2 = File.createTempFile("option2",".xlsx");
        String path2= tempFile2.getAbsolutePath();
        Write(path2,option2);
        File tempFile3 = File.createTempFile("option3",".xlsx");
        String path3= tempFile3.getAbsolutePath();
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



    public static void Read(Basin basin,String basinStr) {
        JSONObject object = JSON.parseObject(basinStr);
        assert object != null;
        try{
            basin.setName(object.getString("name"));
            List<Reservoir> reservoirs= JSON.parseArray(object.get("reservoirs").toString(), Reservoir.class);
            basin.setReservoirs(reservoirs);
        }
        catch (Exception e){
            throw new RuntimeException("流域参数读取有误");
        }
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
        row0.createCell(7).setCellValue("qSingle");
        row0.createCell(8).setCellValue("v");
        row0.createCell(9).setCellValue("retain");
        row0.createCell(10).setCellValue("percentage1");
        row0.createCell(11).setCellValue("percentage2");
        row0.createCell(12).setCellValue("limits");
        for (int i = 0; i < options.size(); i++) {
            Option line = options.get(i);
            XSSFRow row = sheet.createRow(i+1);

            double Q_in = BigDecimal.valueOf(line.getQIn()).setScale(3, RoundingMode.HALF_UP).doubleValue();
            double H1=BigDecimal.valueOf(line.getH1()).setScale(2,RoundingMode.HALF_UP).doubleValue();
            double H2=BigDecimal.valueOf(line.getH2()).setScale(2,RoundingMode.HALF_UP).doubleValue();
            double Q_out=BigDecimal.valueOf(line.getQOut()).setScale(3,RoundingMode.HALF_UP).doubleValue();

            double V=BigDecimal.valueOf(line.getV()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            double retain=BigDecimal.valueOf(line.getRetain()).setScale(3,RoundingMode.HALF_UP).doubleValue();
            row.createCell(0).setCellValue(line.getName());
            row.createCell(1).setCellValue(line.getType());
            row.createCell(2).setCellValue(sdf.format(line.getTime()));
            row.createCell(3).setCellValue(Q_in);
            row.createCell(4).setCellValue(H1);
            row.createCell(5).setCellValue(H2);
            row.createCell(6).setCellValue(Q_out);
            row.createCell(7).setCellValue(line.getQSingleString());
            row.createCell(8).setCellValue(V);
            row.createCell(9).setCellValue(retain);
            row.createCell(10).setCellValue(line.getPercentage1());
            row.createCell(11).setCellValue(line.getPercentage2());
            row.createCell(12).setCellValue(line.getLimitString());

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
