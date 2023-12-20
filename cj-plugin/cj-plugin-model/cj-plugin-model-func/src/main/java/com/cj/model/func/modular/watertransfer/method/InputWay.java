package com.cj.model.func.modular.watertransfer.method;


import com.cj.model.func.modular.watertransfer.entity.Waterdemand;
import com.cj.model.func.modular.watertransfer.req.WaterTransferReq;

import java.util.*;
import java.util.stream.Collectors;

public class InputWay {


    /**
     * 年需水计划
     *
     * @param req
     * @return
     */

    public static Map<String, Object> setwaterdemand(WaterTransferReq req, int monthNum) {

        List<Waterdemand> waterDemandData = req.getWaterDemandData();
        String[] nameIndustryQushou = getName(waterDemandData, "渠首工业");

        String[] nameAgricultureEast = getName(waterDemandData, "河东农业");

        String[] nameAgricultureWest = getName(waterDemandData, "河西农业");

        String[] nameGreenEast = getName(waterDemandData, "河东绿化");

        String[] nameGreenWest = getName(waterDemandData, "河西绿化");

        String[] nameAgricultureQushou = getName(waterDemandData, "渠首农业");

        String[] nameGreenQushou = getName(waterDemandData, "渠首绿化");
        Map<String, Object> data1 = new HashMap<>();
        //渠首工业
        double[][] demandIndustryQushou = new double[nameIndustryQushou.length][12];
        for (int x = 0; x < nameIndustryQushou.length; x++) {
            demandIndustryQushou[x] = setDataYearCity(waterDemandData, nameIndustryQushou[x], "渠首工业");
            double n = getDataMonthPlan(waterDemandData, nameIndustryQushou[x], "渠首工业");
            if (n != -100) {
                demandIndustryQushou[x][monthNum] = n;
            }
            data1.put(nameIndustryQushou[x], demandIndustryQushou[x]);
        }
        //东干灌溉
        double[][] demandAgricultureEast = new double[nameAgricultureEast.length][12];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demandAgricultureEast[x] = setDataYearIrrigate(waterDemandData, nameAgricultureEast[x], "河东农业");
            double n = getDataMonthPlan(waterDemandData, nameAgricultureEast[x], "河东农业");
            if (n != -100) {
                demandAgricultureEast[x][monthNum] = n;
            }
            data1.put(nameAgricultureEast[x], demandAgricultureEast[x]);
        }
        //西干灌溉
        double[][] demandAgricultureWest = new double[nameAgricultureWest.length][12];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demandAgricultureWest[x] = setDataYearIrrigate(waterDemandData, nameAgricultureWest[x], "河西农业");
            double n = getDataMonthPlan(waterDemandData, nameAgricultureWest[x], "河西农业");
            if (n != -100) {
                demandAgricultureWest[x][monthNum] = n;
            }
            data1.put(nameAgricultureWest[x], demandAgricultureWest[x]);
        }
        //河东绿化
        double[][] demandGreenEast = new double[nameGreenEast.length][12];
        for (int x = 0; x < nameGreenEast.length; x++) {
            demandGreenEast[x] = setDataYearGreen(waterDemandData, nameGreenEast[x], "河东绿化");
            double n = getDataMonthPlan(waterDemandData, nameGreenEast[x], "河东绿化");
            if (n != -100) {
                demandGreenEast[x][monthNum] = n;
            }
            data1.put(nameGreenEast[x], demandGreenEast[x]);
        }
        //河西绿化
        double[][] demandGreenWest = new double[nameGreenWest.length][12];
        for (int x = 0; x < nameGreenWest.length; x++) {
            demandGreenWest[x] = setDataYearGreen(waterDemandData, nameGreenWest[x], "河西绿化");
            double n = getDataMonthPlan(waterDemandData, nameGreenWest[x], "河西绿化");
            if (n != -100) {
                demandGreenWest[x][monthNum] = n;
            }
            data1.put(nameGreenWest[x], demandGreenWest[x]);
        }
        //渠首农业
        double[][] demandAgricultureQushou = new double[nameAgricultureQushou.length][12];
        for (int x = 0; x < nameAgricultureQushou.length; x++) {
            demandAgricultureQushou[x] = setDataYearIrrigate(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            double n = getDataMonthPlan(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            if (n != -100) {
                demandAgricultureQushou[x][monthNum] = n;
            }
            data1.put(nameAgricultureQushou[x], demandAgricultureQushou[x]);
        }
        //渠首绿化
        double[][] demandGreenQushou = new double[nameGreenQushou.length][12];
        for (int x = 0; x < nameGreenQushou.length; x++) {
            demandGreenQushou[x] = setDataYearGreen(waterDemandData, nameGreenQushou[x], "渠首绿化");
            double n = getDataMonthPlan(waterDemandData, nameGreenQushou[x], "渠首绿化");
            if (n != -100) {
                demandGreenQushou[x][monthNum] = n;
            }
            data1.put(nameGreenQushou[x], demandGreenQushou[x]);
        }
        double[] demand_bagang = setDataYearCity(waterDemandData, "八钢", "八钢工业");
        double n1 = getDataMonthPlan(waterDemandData, "八钢", "八钢工业");
        if (n1 != -100) {
            demand_bagang[monthNum] = n1;
        }

        double[] demand_hongyan = setDataYearCity(waterDemandData, "红岩", "红岩城市");
        double n2 = getDataMonthPlan(waterDemandData, "红岩", "红岩城市");
        if (n2 != -100) {
            demand_hongyan[monthNum] = n2;
        }

        double[] demand_lzz = setDataYearCity(waterDemandData, "楼庄子水厂", "楼庄子水厂");
        double n3 = getDataMonthPlan(waterDemandData, "楼庄子水厂", "楼庄子水厂");
        if (n3 != -100) {
            demand_lzz[monthNum] = n3;
        }
        data1.put("八钢", demand_bagang);
        data1.put("红岩", demand_hongyan);
        data1.put("楼庄子水厂", demand_lzz);
        data1.put("河东灌溉站点名", nameAgricultureEast);
        data1.put("河西灌溉站点名", nameAgricultureWest);
        data1.put("河东绿化站点名", nameGreenEast);
        data1.put("河西绿化站点名", nameGreenWest);
        data1.put("渠首绿化站点名", nameGreenQushou);
        data1.put("渠首工业站点名", nameIndustryQushou);
        data1.put("渠首农业站点名", nameAgricultureQushou);

        return data1;

    }

    /**
     * @param req      入参
     * @param xnum     本月第几旬
     * @param monthNum 本月第几月
     * @return
     */
    public static Map<String, Object> setwaterdemandTendays(WaterTransferReq req, int xnum, int monthNum) {

        double[] monthday = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        List<Waterdemand> waterDemandData = req.getWaterDemandData();

        String[] nameIndustryQushou = getName(waterDemandData, "渠首工业");

        String[] nameAgricultureEast = getName(waterDemandData, "河东农业");

        String[] nameAgricultureWest = getName(waterDemandData, "河西农业");

        String[] nameGreenEast = getName(waterDemandData, "河东绿化");

        String[] nameGreenWest = getName(waterDemandData, "河西绿化");

        String[] nameAgricultureQushou = getName(waterDemandData, "渠首农业");

        String[] nameGreenQushou = getName(waterDemandData, "渠首绿化");

        Map<String, Object> data1 = new HashMap<>();

        //东干灌溉
        double[][] demandAgricultureEast = new double[nameAgricultureEast.length][3];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demandAgricultureEast[x] = setDataMonth(waterDemandData, nameAgricultureEast[x], "河东农业");
            double n = getDataRealTime(waterDemandData, nameAgricultureEast[x], "河东管理站");
            if (n != -100) {
                demandAgricultureEast[x][xnum] = n;
            }
            data1.put(nameAgricultureEast[x], demandAgricultureEast[x]);
        }
        //西干灌溉
        double[][] demandAgricultureWest = new double[nameAgricultureWest.length][3];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demandAgricultureWest[x] = setDataMonth(waterDemandData, nameAgricultureWest[x], "河西农业");
            double n = getDataRealTime(waterDemandData, nameAgricultureWest[x], "河西管理站");
            if (n != -100) {
                demandAgricultureWest[x][xnum] = n;
            }
            data1.put(nameAgricultureWest[x], demandAgricultureWest[x]);
        }
        //渠首农业
        double[][] demandAgricultureQushou = new double[nameAgricultureQushou.length][3];
        for (int x = 0; x < nameAgricultureQushou.length; x++) {
            demandAgricultureQushou[x] = setDataMonth(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            double n = getDataRealTime(waterDemandData, nameAgricultureQushou[x], "渠首管理站");
            if (n != -100) {
                demandAgricultureQushou[x][xnum] = n;
            }
            data1.put(nameAgricultureQushou[x], demandAgricultureQushou[x]);
        }
        //河东绿化
        double[][] demandGreenEast = new double[nameGreenEast.length][3];
        for (int x = 0; x < nameGreenEast.length; x++) {
            demandGreenEast[x] = setDataMonth(waterDemandData, nameGreenEast[x], "河东绿化");
            data1.put(nameGreenEast[x], demandGreenEast[x]);
        }
        //河西绿化
        double[][] demandGreenWest = new double[nameGreenWest.length][3];
        for (int x = 0; x < nameGreenWest.length; x++) {
            demandGreenWest[x] = setDataMonth(waterDemandData, nameGreenWest[x], "河西绿化");
            data1.put(nameGreenWest[x], demandGreenWest[x]);
        }
        //渠首绿化
        double[][] demandGreenQushou = new double[nameGreenQushou.length][3];
        for (int x = 0; x < nameGreenQushou.length; x++) {
            demandGreenQushou[x] = setDataMonth(waterDemandData, nameGreenQushou[x], "渠首绿化");
            data1.put(nameGreenQushou[x], demandGreenQushou[x]);
        }
        //渠首工业
        double[][] demandIndustryQushou = new double[nameIndustryQushou.length][3];
        for (int x = 0; x < nameIndustryQushou.length; x++) {
            demandIndustryQushou[x] = setDataMonth(waterDemandData, nameIndustryQushou[x], "渠首工业");
            data1.put(nameIndustryQushou[x], demandIndustryQushou[x]);
        }

        double[] demand_bagang = setDataMonth(waterDemandData, "八钢", "八钢工业");
        double[] demand_hongyan = setDataMonth(waterDemandData, "红岩", "红岩城市");
        double[] demand_lzz = setDataMonth(waterDemandData, "楼庄子水厂", "楼庄子水厂");

        data1.put("八钢", demand_bagang);
        data1.put("红岩", demand_hongyan);
        data1.put("楼庄子水厂", demand_lzz);
        data1.put("河东灌溉站点名", nameAgricultureEast);
        data1.put("河西灌溉站点名", nameAgricultureWest);
        data1.put("河东绿化站点名", nameGreenEast);
        data1.put("河西绿化站点名", nameGreenWest);
        data1.put("渠首绿化站点名", nameGreenQushou);
        data1.put("渠首工业站点名", nameIndustryQushou);
        data1.put("渠首农业站点名", nameAgricultureQushou);

        return data1;

    }
//    public static Map<String, Object> setwaterdemandDays(WaterTransferReq req,int xnum,int monthNum)
//    {
//
//    }

    /**
     * @param dataMonth 月逐旬需水数据
     * @param dayNum    本旬第几天
     * @param step      本旬总天数
     * @param id        本月第几旬
     * @return
     */
    public static Map<String, Object> setWaterdemandDay(WaterTransferReq req, Map<String, Object> dataMonth, int dayNum, int step, int id) {
        double[] monthday = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        double num = 10;
        num = step;
        List<Waterdemand> waterDemandData = req.getWaterDemandData();

        String[] nameIndustryQushou = (String[]) dataMonth.get("渠首工业站点名");
        String[] nameAgricultureEast = (String[]) dataMonth.get("河东灌溉站点名");
        String[] nameAgricultureWest = (String[]) dataMonth.get("河西灌溉站点名");
        String[] nameGreenEast = (String[]) dataMonth.get("河东绿化站点名");
        String[] nameGreenWest = (String[]) dataMonth.get("河西绿化站点名");
        String[] nameAgricultureQushou = (String[]) dataMonth.get("渠首农业站点名");
        String[] nameGreenQushou = (String[]) dataMonth.get("渠首绿化站点名");
        Map<String, Object> data = new HashMap<>();

        double[] demand = (double[]) dataMonth.get("楼庄子水厂");
        double[] demand1 = (double[]) dataMonth.get("红岩");
        double[] demand2 = (double[]) dataMonth.get("八钢");


        double[] demand_lzz = new double[step];
        double[] demand_bagang = new double[step];
        double[] demand_hongyan = new double[step];
        for (int x = 0; x < demand_lzz.length; x++) {
            demand_lzz[x] = demand[id] / num;
            demand_bagang[x] = demand2[id] / num;
            demand_hongyan[x] = demand1[id] / num;
        }
        data.put("楼庄子水厂", demand_lzz);
        data.put("八钢", demand_bagang);
        data.put("红岩", demand_hongyan);

        //渠首工业
        double[][] demandIndustryQushou = new double[nameIndustryQushou.length][step];
        for (int x = 0; x < nameIndustryQushou.length; x++) {
            demand = (double[]) dataMonth.get(nameIndustryQushou[x]);
            for (int x1 = 0; x1 < demand_lzz.length; x1++) {
                demandIndustryQushou[x][x1] = demand[id] / num;
            }
            data.put(nameIndustryQushou[x], demandIndustryQushou[x]);
        }

        //渠首农业
        double[][] demendAgricultureQushou = new double[nameAgricultureQushou.length][step];
        for (int x = 0; x < nameAgricultureQushou.length; x++) {

            demand = (double[]) dataMonth.get(nameAgricultureQushou[x]);
            double tenDay=getDataRealTime(waterDemandData,nameAgricultureQushou[x],"渠首管理站");
            if (tenDay!=-100)
            {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureQushou[x][x1] = tenDay / num;
                }
            }
            else
             {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureQushou[x][x1] = demand[id] / num;
                }
            }
            double n = getDataRealDay(waterDemandData, nameAgricultureQushou[x], "渠首管理站");
            if (n != -100) {
                demendAgricultureQushou[x][dayNum] = n;
            }
            data.put(nameAgricultureQushou[x], demendAgricultureQushou[x]);
        }
        // 渠首绿化
        double[][] demendGreenQushou = new double[nameGreenQushou.length][step];
        for (int x = 0; x < nameGreenQushou.length; x++) {
            demand = (double[]) dataMonth.get(nameGreenQushou[x]);
            for (int x1 = 0; x1 < step; x1++) {
                demendGreenQushou[x][x1] = demand[id] / num;
            }
            double n = getDataRealDay(waterDemandData, nameGreenQushou[x], "渠首管理站");
            if (n != -100) {
                demendGreenQushou[x][dayNum] = n;
            }
            data.put(nameGreenQushou[x], demendGreenQushou[x]);
        }
        //河东绿化
        double[][] demendGreenEast = new double[nameGreenEast.length][step];
        for (int x = 0; x < nameGreenEast.length; x++) {
            demand = (double[]) dataMonth.get(nameGreenEast[x]);
            for (int x1 = 0; x1 < step; x1++) {
                demendGreenEast[x][x1] = demand[id] / num;
            }
            double n = getDataRealDay(waterDemandData, nameGreenEast[x], "河东管理站");
            if (n != -100) {
                demendGreenEast[x][dayNum] = n;
            }
            data.put(nameGreenEast[x], demendGreenEast[x]);
        }
        //河西绿化
        double[][] demendGreenWest = new double[nameGreenWest.length][step];
        for (int x = 0; x < nameGreenWest.length; x++) {
            demand = (double[]) dataMonth.get(nameGreenWest[x]);
            for (int x1 = 0; x1 < step; x1++) {
                demendGreenWest[x][x1] = demand[id] / num;
            }
            double n = getDataRealDay(waterDemandData, nameGreenWest[x], "河西管理站");
            if (n != -100) {
                demendGreenWest[x][dayNum] = n;
            }
            data.put(nameGreenWest[x], demendGreenWest[x]);
        }

        //河东农业
        double[][] demendAgricultureEast = new double[nameAgricultureEast.length][step];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureEast[x]);
            double tenDay=getDataRealTime(waterDemandData,nameAgricultureEast[x],"河东管理站");
            if (tenDay!=-100)
            {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureEast[x][x1] = tenDay / num;
                }
            }
            else
            {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureEast[x][x1] = demand[id] / num;
                }
            }
            double n = getDataRealDay(waterDemandData, nameAgricultureEast[x], "河东管理站");
            if (n != -100) {
                demendAgricultureEast[x][dayNum] = n;
            }
            data.put(nameAgricultureEast[x], demendAgricultureEast[x]);
        }

        //河西农业
        double[][] demendAgricultureWest = new double[nameAgricultureWest.length][step];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureWest[x]);
            double tenDay=getDataRealTime(waterDemandData,nameAgricultureWest[x],"河西管理站");
            if (tenDay!=-100)
            {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureWest[x][x1] = tenDay / num;
                }
            }
            else
            {
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureWest[x][x1] = demand[id] / num;
                }
            }
            double n = getDataRealDay(waterDemandData, nameAgricultureWest[x], "河西管理站");
            if (n != -100) {
                demendAgricultureWest[x][dayNum] = n;
            }
            data.put(nameAgricultureWest[x], demendAgricultureWest[x]);
        }

        data.put("河东灌溉站点名", nameAgricultureEast);
        data.put("河西灌溉站点名", nameAgricultureWest);
        data.put("河东绿化站点名", nameGreenEast);
        data.put("河西绿化站点名", nameGreenWest);
        data.put("渠首绿化站点名", nameGreenQushou);
        data.put("渠首工业站点名", nameIndustryQushou);
        data.put("渠首农业站点名", nameAgricultureQushou);
        return data;
    }

    public static double getDataMonthPlan(List<Waterdemand> data, String name, String tableName) {
        double n = -100;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("month")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {
                        if (data.get(i).getColName().equals("合计")) {
                            n = data.get(i).getWaterDemendData();
                        }
                    }
                }
            }
        }
        return n;
    }

    /**
     * 获得当旬的用水计划
     *
     * @param data
     * @param name
     * @return
     */
    public static double getDataRealTime(List<Waterdemand> data, String name, String tableName) {
        double n = -100;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("tenDays")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {
                        n = data.get(i).getWaterDemendData();
                    }
                }
            }
        }
        return n;
    }

    /**
     * 获得当天的用水计划
     *
     * @param data
     * @param name
     * @param tableName
     * @return
     */
    public static double getDataRealDay(List<Waterdemand> data, String name, String tableName) {
        double n = -100;
        double x = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("day")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {
                        if (data.get(i).getColName().equals("flow"))
                        {
                            x += data.get(i).getWaterDemendData();
                        }
                    }
                }
            }
        }
        if (x != 0) {
            n = x;
        }
        return n;
    }

    /**
     * @param data      旬计划 灌溉、绿化需水数据
     * @param name      灌溉、绿化需水地
     * @param tableName 表名
     * @return
     */
    public static double[] setDataMonth(List<Waterdemand> data, String name, String tableName) {
        List<Double> demand = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("month")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {

                        if (data.get(i).getColName().equals("上旬")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("中旬")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("下旬")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                    }
                }
            }
        }
        double[] demanddata = new double[3];
        if (demand.size() > 0) {
            for (int i = 0; i < demanddata.length; i++) {
                demanddata[i] = demand.get(i);
            }
        }
        return demanddata;
    }

    /**
     * 绿化年计划
     *
     * @param data      绿化需水数据
     * @param name      绿化需水地
     * @param tableName 表名
     * @return
     */
    public static double[] setDataYearGreen(List<Waterdemand> data, String name, String tableName) {
        List<Double> demand = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("year")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {
                        // 一月用水

                        if (data.get(i).getColName().equals("二月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("三月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("四月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("五月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("六月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("七月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("八月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("九月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("十月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("十一月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }

                    }
                }
            }
        }
        double[] demanddata = new double[12];
        if (demand.size()==0)
        {
            for (int i = 0; i < demanddata.length; i++) {
                demanddata[i] =0;
            }
        }
        else
        {
        for (int i = 0; i < demanddata.length; i++) {
            demanddata[0] = 0;
            if (i >=1 && i < 11){
                demanddata[i] = demand.get(i-1);
            }
            demanddata[11] = 0;
        }
        }
        return demanddata;
    }

    /**
     * @param data 对应表格的数据
     * @return
     */
    public static String[] getName(List<Waterdemand> data, String tableName) {
        List<String> demand = new ArrayList<>();
//        String name=data.get(0).getUnit();
//        demand.add(name);
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i).getUseWaterPlan().equals("year"))
//            {
//                if (data.get(i).getArea().equals(tableName))
//                {
//                    if (!data.get(i).getUnit().equals(name)) {
//                        demand.add(data.get(i).getUnit());
//                    }
//                }
//            }
//        }
        demand = data.stream().filter(n -> n.getArea().equals(tableName)).map(Waterdemand::getUnit).distinct().collect(Collectors.toList());
        String[] demanddata = new String[demand.size()];
        for (int i = 0; i < demand.size(); i++) {
            demanddata[i] = demand.get(i);
        }
        return demanddata;
    }

    /**
     * 城市生活用水、工业用水需水
     *
     * @param data      用水数据
     * @param name      用水点
     * @param tableName 表名
     * @return
     */
    public static double[] setDataYearCity(List<Waterdemand> data, String name, String tableName) {
        List<String> month = Arrays.asList("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");
        List<Double> demand = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("year") && data.get(i).getArea().equals(tableName) && data.get(i).getUnit().equals(name)) {
                if (month.contains(data.get(i).getColName())) {
                    demand.add(data.get(i).getWaterDemendData());
                }
            }
        }
        double[] demanddata = new double[12];
        if (demand.size()==0)
        {
            for (int i = 0; i < demanddata.length; i++) {
                demanddata[i] =0;
            }
        }
        else
        {
        for (int i = 0; i < demanddata.length; i++) {
            demanddata[i] = demand.get(i);
        }
        }
        return demanddata;
    }

    /**
     * 灌溉year
     *
     * @param data      需水数据
     * @param name      灌溉需水地
     * @param tableName 表名
     * @return
     */
    public static double[] setDataYearIrrigate(List<Waterdemand> data, String name, String tableName) {
        List<Double> demand = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getUseWaterPlan().equals("year")) {
                if (data.get(i).getArea().equals(tableName)) {
                    if (data.get(i).getUnit().equals(name)) {
                        if (data.get(i).getColName().equals("四月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("五月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("六月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("七月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("八月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("九月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("十月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                        if (data.get(i).getColName().equals("十一月")) {
                            demand.add(data.get(i).getWaterDemendData());
                        }
                    }
                }
            }
        }
        double[] demanddata = new double[12];
        if (demand.size()==0)
        {
            for (int i = 0; i < demanddata.length; i++) {
                demanddata[i] =0;
            }
        }
        else
        {
        for (int i = 0; i < demanddata.length; i++) {
            if (i < 3) {
                demanddata[i] = 0;
            }
            if (i >= 3 && i < 11) {
                try {
                    demanddata[i] = demand.get(i - 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i == 11) {
                demanddata[i] = 0;
            }
        }
        }
        return demanddata;
    }
}
