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
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameIndustryQushou[x], "渠首工业");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandIndustryQushou[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameIndustryQushou[x], demandIndustryQushou[x]);
        }
        //东干灌溉
        double[][] demandAgricultureEast = new double[nameAgricultureEast.length][12];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demandAgricultureEast[x] = setDataYearIrrigate(waterDemandData, nameAgricultureEast[x], "河东农业");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameAgricultureEast[x], "河东农业");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandAgricultureEast[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameAgricultureEast[x], demandAgricultureEast[x]);
        }
        //西干灌溉
        double[][] demandAgricultureWest = new double[nameAgricultureWest.length][12];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demandAgricultureWest[x] = setDataYearIrrigate(waterDemandData, nameAgricultureWest[x], "河西农业");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameAgricultureWest[x], "河西农业");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandAgricultureWest[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameAgricultureWest[x], demandAgricultureWest[x]);
        }
        //河东绿化
        double[][] demandGreenEast = new double[nameGreenEast.length][12];
        for (int x = 0; x < nameGreenEast.length; x++) {
            demandGreenEast[x] = setDataYearGreen(waterDemandData, nameGreenEast[x], "河东绿化");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameGreenEast[x], "河东绿化");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandGreenEast[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameGreenEast[x], demandGreenEast[x]);
        }
        //河西绿化
        double[][] demandGreenWest = new double[nameGreenWest.length][12];
        for (int x = 0; x < nameGreenWest.length; x++) {
            demandGreenWest[x] = setDataYearGreen(waterDemandData, nameGreenWest[x], "河西绿化");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameGreenWest[x], "河西绿化");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandGreenWest[x][month.get(i).intValue()] =  waterData.get(i);
                }
            }
            data1.put(nameGreenWest[x], demandGreenWest[x]);
        }
        //渠首农业
        double[][] demandAgricultureQushou = new double[nameAgricultureQushou.length][12];
        for (int x = 0; x < nameAgricultureQushou.length; x++) {
            demandAgricultureQushou[x] = setDataYearIrrigate(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandAgricultureQushou[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameAgricultureQushou[x], demandAgricultureQushou[x]);
        }
        //渠首绿化
        double[][] demandGreenQushou = new double[nameGreenQushou.length][12];
        for (int x = 0; x < nameGreenQushou.length; x++) {
            demandGreenQushou[x] = setDataYearGreen(waterDemandData, nameGreenQushou[x], "渠首绿化");
            List<List<Double>> update=new ArrayList<>();
            update = getDataMonthPlan(waterDemandData, nameGreenQushou[x], "渠首绿化");
            List<Double> month = update.get(0);
            List<Double> waterData = update.get(1);
            for (int i=0;i<month.size();i++){
                if (waterData.get(i)>0){
                    demandGreenQushou[x][month.get(i).intValue()] = waterData.get(i);
                }
            }
            data1.put(nameGreenQushou[x], demandGreenQushou[x]);
        }

        double[] demand_bagang = setDataYearCity(waterDemandData, "月水量(万m³)", "八钢工业");
        List<List<Double>> update1=new ArrayList<>();
        update1 = getDataMonthPlan(waterDemandData, "水量", "八钢工业");

        List<Double> month1 = update1.get(0);
        List<Double> waterData1 = update1.get(1);
        for (int i=0;i<month1.size();i++){
            if (waterData1.get(i)>0){
                demand_bagang[month1.get(i).intValue()] = waterData1.get(i);
            }
        }

        double[] demand_hongyan = setDataYearCity(waterDemandData, "月水量", "红岩");
        List<List<Double>> update2=new ArrayList<>();
        update2 = getDataMonthPlan(waterDemandData, "水量", "红岩");
        List<Double> month2 = update2.get(0);
        List<Double> waterData2 = update2.get(1);
        for (int i=0;i<month2.size();i++){
            if (waterData2.get(i)>0){
                demand_hongyan[month2.get(i).intValue()] = waterData2.get(i);
            }
        }

        double[] demand_lzz = setDataYearCity(waterDemandData, "月水量", "楼庄子水厂");
        List<List<Double>> update3=new ArrayList<>();
        update3 = getDataMonthPlan(waterDemandData, "水量", "楼庄子水厂");
        List<Double> month3 = update3.get(0);
        List<Double> waterData3 = update3.get(1);
        for (int i=0;i<month3.size();i++){
            if (waterData3.get(i)>0){
                demand_lzz[month3.get(i).intValue()] = waterData3.get(i);
            }
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
     * 月逐旬计划
     * @param req      入参
     * @param xnum     本月第几旬-1
     * @param monthNum 本月第几月-1
     * @return
     */
    public static Map<String, Object> setwaterdemandTendays(WaterTransferReq req, int xnum,Map<String, Object> dataYear ,int monthNum) {



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
            if (isAllZeros(demandAgricultureEast[x])==true){
                double[]datayear= (double[])dataYear.get(nameAgricultureEast[x]);
                for (int i=0;i<demandAgricultureEast[x].length;i++){
                    if (dataYear.get(nameAgricultureEast[x])==null){
                       break;
                    }
                    demandAgricultureEast[x][i]= datayear[monthNum]/3;
                }
            }
            List<List<Double>> update=new ArrayList<>();
            update = getDataRealTime(waterDemandData, nameAgricultureEast[x], "河东管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            for (int ii=0;ii<tenDays.size();ii++){
                if (waterData.get(ii)>0)
                {
                    demandAgricultureEast[x][tenDays.get(ii).intValue()] = waterData.get(ii);
                }
            }
            data1.put(nameAgricultureEast[x], demandAgricultureEast[x]);
        }
        //西干灌溉
        double[][] demandAgricultureWest = new double[nameAgricultureWest.length][3];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demandAgricultureWest[x] = setDataMonth(waterDemandData, nameAgricultureWest[x], "河西农业");

            if (isAllZeros(demandAgricultureWest[x])==true){
                double[]datayear= (double[])dataYear.get(nameAgricultureWest[x]);
                for (int i=0;i<demandAgricultureWest[x].length;i++){
                    if (dataYear.get(nameAgricultureWest[x])==null){
                        break;
                    }
                    demandAgricultureWest[x][i]= datayear[monthNum]/3;
                }
            }
            List<List<Double>> update=new ArrayList<>();
            update = getDataRealTime(waterDemandData, nameAgricultureWest[x], "河西管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            for (int ii=0;ii<tenDays.size();ii++){
                if (waterData.get(ii)>0)
                {
                    demandAgricultureWest[x][tenDays.get(ii).intValue()] = waterData.get(ii);
                }
            }
            data1.put(nameAgricultureWest[x], demandAgricultureWest[x]);
        }
        //渠首农业
        double[][] demandAgricultureQushou = new double[nameAgricultureQushou.length][3];
        for (int x = 0; x < nameAgricultureQushou.length; x++) {
            demandAgricultureQushou[x] = setDataMonth(waterDemandData, nameAgricultureQushou[x], "渠首农业");
            if (isAllZeros(demandAgricultureQushou[x])==true){
                double[]datayear= (double[])dataYear.get(nameAgricultureQushou[x]);
                for (int i=0;i<demandAgricultureQushou[x].length;i++){
                    if (dataYear.get(nameAgricultureQushou[x])==null){
                        break;
                    }
                    demandAgricultureQushou[x][i]= datayear[monthNum]/3;
                }
            }
            List<List<Double>> update=new ArrayList<>();
            update = getDataRealTime(waterDemandData, nameAgricultureQushou[x], "渠首管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            for (int ii=0;ii<tenDays.size();ii++){
                if (waterData.get(ii)>0)
                {
                    demandAgricultureQushou[x][tenDays.get(ii).intValue()] = waterData.get(ii);
                }
            }
            data1.put(nameAgricultureQushou[x], demandAgricultureQushou[x]);
        }
        //河东绿化
        double[][] demandGreenEast = new double[nameGreenEast.length][3];
        for (int x = 0; x < nameGreenEast.length; x++) {
            demandGreenEast[x] = setDataMonth(waterDemandData, nameGreenEast[x], "河东绿化");
            if (isAllZeros(demandGreenEast[x])==true){
                double[]datayear= (double[])dataYear.get(nameGreenEast[x]);
                for (int i=0;i<demandGreenEast[x].length;i++){
                    if (dataYear.get(nameGreenEast[x])==null){
                        break;
                    }
                    demandGreenEast[x][i]= datayear[monthNum]/3;
                }
            }
            data1.put(nameGreenEast[x], demandGreenEast[x]);
        }
        //河西绿化
        double[][] demandGreenWest = new double[nameGreenWest.length][3];
        for (int x = 0; x < nameGreenWest.length; x++) {
            demandGreenWest[x] = setDataMonth(waterDemandData, nameGreenWest[x], "河西绿化");
            if (isAllZeros(demandGreenWest[x])==true){
                double[]datayear= (double[])dataYear.get(nameGreenWest[x]);
                for (int i=0;i<demandGreenWest[x].length;i++){
                    if (dataYear.get(nameGreenWest[x])==null){
                        break;
                    }
                    demandGreenWest[x][i]= datayear[monthNum]/3;
                }
            }
            data1.put(nameGreenWest[x], demandGreenWest[x]);
        }
        //渠首绿化
        double[][] demandGreenQushou = new double[nameGreenQushou.length][3];
        for (int x = 0; x < nameGreenQushou.length; x++) {
            demandGreenQushou[x] = setDataMonth(waterDemandData, nameGreenQushou[x], "渠首绿化");
            if (isAllZeros(demandGreenQushou[x])==true){
                double[]datayear= (double[])dataYear.get(nameGreenQushou[x]);
                for (int i=0;i<demandGreenQushou[x].length;i++){
                    if (dataYear.get(nameGreenQushou[x])==null){
                        break;
                    }
                    demandGreenQushou[x][i]= datayear[monthNum]/3;
                }
            }
            data1.put(nameGreenQushou[x], demandGreenQushou[x]);
        }
        //渠首工业
        double[][] demandIndustryQushou = new double[nameIndustryQushou.length][3];
        for (int x = 0; x < nameIndustryQushou.length; x++) {
            demandIndustryQushou[x] = setDataMonth(waterDemandData, nameIndustryQushou[x], "渠首工业");
            if (isAllZeros(demandIndustryQushou[x])==true){
                double[]datayear= (double[])dataYear.get(nameIndustryQushou[x]);
                for (int i=0;i<demandIndustryQushou[x].length;i++){
                    if (dataYear.get(nameIndustryQushou[x])==null){
                        break;
                    }
                    demandIndustryQushou[x][i]= datayear[monthNum]/3;
                }
            }
            data1.put(nameIndustryQushou[x], demandIndustryQushou[x]);
        }

        double[] demand_bagang = new double[3];
        double[] demand_hongyan= new double[3];
        double[] demand_lzz =new double[3];

            demand_bagang=setDataMonth(waterDemandData, "水量", "八钢工业");

            demand_hongyan=setDataMonth(waterDemandData, "水量", "红岩");

            demand_lzz=setDataMonth(waterDemandData, "水量", "楼庄子水厂");

        if (isAllZeros(demand_bagang)==true){
            double[]datayear= (double[])dataYear.get("八钢");
            for (int i=0;i<demand_bagang.length;i++){
                if (dataYear.get("八钢")==null){
                    break;
                }

                demand_bagang[i]= datayear[monthNum]/3;
            }
        }

        if (isAllZeros(demand_hongyan)==true){
            double[]datayear= (double[])dataYear.get("红岩");
            for (int i=0;i<demand_hongyan.length;i++){
                if (dataYear.get("红岩")==null){
                    break;
                }
                demand_hongyan[i]= datayear[monthNum]/3;
            }
        }
        if (isAllZeros(demand_lzz)==true){
            double[]datayear= (double[])dataYear.get("楼庄子水厂");
            for (int i=0;i<demand_lzz.length;i++){
                if (dataYear.get("楼庄子水厂")==null){
                    break;
                }
                demand_lzz[i]= datayear[monthNum]/3;
            }
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
     * 判断是否都为0
     * @param array
     * @return
     */
    public static boolean isAllZeros(double[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得旬逐日计划
     * @param dataMonth 月逐旬需水数据
     * @param dayNum    本旬第几天-1
     * @param step      本旬总天数
     * @param id        本月第几旬-1
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
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureQushou[x],"渠首管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureQushou[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureQushou[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureQushou[x][x1] = demand[id] / num;
                        }
                    }
                }

            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureQushou[x], "渠首管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureQushou[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenQushou[x], "渠首管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenQushou[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenEast[x], "河东管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenEast[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenWest[x], "河西管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenWest[x][day.get(i).intValue()] = waterDataday.get(i);
                }
            }
            data.put(nameGreenWest[x], demendGreenWest[x]);
        }

        //河东农业
        double[][] demendAgricultureEast = new double[nameAgricultureEast.length][step];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureEast[x]);
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureEast[x],"河东管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureEast[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureEast[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureEast[x][x1] = demand[id] / num;
                        }
                    }
                }
            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureEast[x], "河东管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureEast[x][day.get(i).intValue()] =  waterDataday.get(i);
                }
            }
            data.put(nameAgricultureEast[x], demendAgricultureEast[x]);
        }

        //河西农业
        double[][] demendAgricultureWest = new double[nameAgricultureWest.length][step];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureWest[x]);
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureWest[x],"河西管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureWest[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureWest[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureWest[x][x1] = demand[id] / num;
                        }
                    }
                }
            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureWest[x], "河西管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureWest[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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

    /**
     * 日前模型数据
     * @param req
     * @param dataMonth
     * @param dayNum
     * @param step
     * @param id
     * @return
     */
    public static Map<String, Object> setDay(WaterTransferReq req, Map<String, Object> dataMonth, int dayNum, int step, int id) {
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

        String[][] nameAgricultureEastDay = new String[nameAgricultureEast.length][1];
        String[][] nameAgricultureWestDay = new String[nameAgricultureWest.length][1] ;
        String[][] nameGreenEastDay = new String[nameGreenEast.length][1];
        String[][] nameGreenWestDay = new String[nameGreenWest.length][1] ;
        String[][] nameAgricultureQushouDay = new String[nameAgricultureQushou.length][1];
        String[][] nameGreenQushouDay = new String[nameGreenQushou.length][1];

        String[][] idAgricultureEastDay = new String[nameAgricultureEast.length][1];
        String[][] idAgricultureWestDay = new String[nameAgricultureWest.length][1] ;
        String[][] idGreenEastDay = new String[nameGreenEast.length][1];
        String[][] idGreenWestDay = new String[nameGreenWest.length][1] ;
        String[][] idAgricultureQushouDay = new String[nameAgricultureQushou.length][1];
        String[][] idGreenQushouDay = new String[nameGreenQushou.length][1];

        double[][] demandAgricultureEastDay = new double[nameAgricultureEast.length][1];
        double[][] demandAgricultureWestDay = new double[nameAgricultureWest.length][1] ;
        double[][] demandGreenEastDay = new double[nameGreenEast.length][1];
        double[][] demandGreenWestDay = new double[nameGreenWest.length][1] ;
        double[][] demandAgricultureQushouDay = new double[nameAgricultureQushou.length][1];
        double[][] demandGreenQushouDay = new double[nameGreenQushou.length][1];
        for (int i=0;i<nameAgricultureEastDay.length;i++){
            nameAgricultureEastDay[i]=getNameDay(waterDemandData,"河东管理站",nameAgricultureEast[i])[0];
            idAgricultureEastDay[i]=getNameDay(waterDemandData,"河东管理站",nameAgricultureEast[i])[1];
            demandAgricultureEastDay[i]=getDemandDay(waterDemandData,"河东管理站",nameAgricultureEastDay[i]);
        }

        for (int i=0;i<nameAgricultureWestDay.length;i++){
            nameAgricultureWestDay[i]=getNameDay(waterDemandData,"河西管理站",nameAgricultureWest[i])[0];
            idAgricultureWestDay[i]=getNameDay(waterDemandData,"河西管理站",nameAgricultureWest[i])[1];
            demandAgricultureWestDay[i]=getDemandDay(waterDemandData,"河西管理站",nameAgricultureWestDay[i]);
        }
        for (int i=0;i<nameGreenEastDay.length;i++){
            nameGreenEastDay[i]=getNameDay(waterDemandData,"河东管理站",nameGreenEast[i])[0];
            idGreenEastDay[i]=getNameDay(waterDemandData,"河东管理站",nameGreenEast[i])[1];
            demandGreenEastDay[i]=getDemandDay(waterDemandData,"河东管理站",nameGreenEastDay[i]);
        }
        for (int i=0;i<nameGreenWestDay.length;i++){
            nameGreenWestDay[i]=getNameDay(waterDemandData,"河西管理站",nameGreenWest[i])[0];
            idGreenWestDay[i]=getNameDay(waterDemandData,"河西管理站",nameGreenWest[i])[1];
            demandGreenWestDay[i]=getDemandDay(waterDemandData,"河西管理站",nameGreenWestDay[i]);

        }
        for (int i=0;i<nameAgricultureQushouDay.length;i++){
            nameAgricultureQushouDay[i]=getNameDay(waterDemandData,"渠首管理站",nameAgricultureQushou[i])[0];
            idAgricultureQushouDay[i]=getNameDay(waterDemandData,"渠首管理站",nameAgricultureQushou[i])[1];
            demandAgricultureQushouDay[i]=getDemandDay(waterDemandData,"渠首管理站",nameAgricultureQushouDay[i]);
        }
        for (int i=0;i<nameGreenQushouDay.length;i++){
            nameGreenQushouDay[i]=getNameDay(waterDemandData,"渠首管理站",nameGreenQushou[i])[0];
            idGreenQushouDay[i]=getNameDay(waterDemandData,"渠首管理站",nameGreenQushou[i])[1];
            demandGreenQushouDay[i]=getDemandDay(waterDemandData,"渠首管理站",nameGreenQushouDay[i]);
        }


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
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureQushou[x],"渠首管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureQushou[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureQushou[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureQushou[x][x1] = demand[id] / num;
                        }
                    }
                }

            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureQushou[x], "渠首管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureQushou[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenQushou[x], "渠首管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenQushou[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenEast[x], "河东管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenEast[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameGreenWest[x], "河西管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendGreenWest[x][day.get(i).intValue()] = waterDataday.get(i);
                }
            }
            data.put(nameGreenWest[x], demendGreenWest[x]);
        }

        //河东农业
        double[][] demendAgricultureEast = new double[nameAgricultureEast.length][step];
        for (int x = 0; x < nameAgricultureEast.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureEast[x]);
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureEast[x],"河东管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureEast[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureEast[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureEast[x][x1] = demand[id] / num;
                        }
                    }
                }
            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureEast[x], "河东管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureEast[x][day.get(i).intValue()] =  waterDataday.get(i);
                }
            }
            data.put(nameAgricultureEast[x], demendAgricultureEast[x]);
        }

        //河西农业
        double[][] demendAgricultureWest = new double[nameAgricultureWest.length][step];
        for (int x = 0; x < nameAgricultureWest.length; x++) {
            demand = (double[]) dataMonth.get(nameAgricultureWest[x]);
            List<List<Double>> update=new ArrayList<>();
            update=getDataRealTime(waterDemandData,nameAgricultureWest[x],"河西管理站");
            List<Double> tenDays = update.get(0);
            List<Double> waterData = update.get(1);
            if (tenDays.size()==0){
                for (int x1 = 0; x1 < step; x1++) {
                    demendAgricultureWest[x][x1] = demand[id] / num;
                }
            }
            for (int i = 0; i < tenDays.size(); i++) {
                if (tenDays.get(i).intValue()==id){
                    if (waterData.get(i)>0)
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureWest[x][x1] = waterData.get(i) / num;
                        }
                    }
                    else
                    {
                        for (int x1 = 0; x1 < step; x1++) {
                            demendAgricultureWest[x][x1] = demand[id] / num;
                        }
                    }
                }
            }
            List<List<Double>> updateDay=new ArrayList<>();
            updateDay = getDataRealDay(waterDemandData, nameAgricultureWest[x], "河西管理站");
            List<Double> day = updateDay.get(0);
            List<Double> waterDataday = updateDay.get(1);
            for (int i = 0; i < day.size(); i++) {
                if (waterDataday.get(i)>=0) {
                    demendAgricultureWest[x][day.get(i).intValue()] = waterDataday.get(i);
                }
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

        data.put("河东灌溉子级站点名", nameAgricultureEastDay);
        data.put("河西灌溉子级站点名", nameAgricultureWestDay);
        data.put("河东绿化子级站点名", nameGreenEastDay);
        data.put("河西绿化子级站点名", nameGreenWestDay);
        data.put("渠首绿化子级站点名", nameGreenQushouDay);
        data.put("渠首农业子级站点名", nameAgricultureQushouDay);

        data.put("idAgricultureEastSub", idAgricultureEastDay);
        data.put("idAgricultureWestSub", idAgricultureWestDay);
        data.put("idGreenEastSub", idGreenEastDay);
        data.put("idGreenWestSub", idGreenWestDay);
        data.put("idGreenQushouSub", idGreenQushouDay);
        data.put("idAgricultureQushouSub", idAgricultureQushouDay);

        data.put("河东灌溉子级需水", demandAgricultureEastDay);
        data.put("河西灌溉子级需水", demandAgricultureWestDay);
        data.put("河东绿化子级需水", demandGreenEastDay);
        data.put("河西绿化子级需水", demandGreenWestDay);
        data.put("渠首绿化子级需水", demandGreenQushouDay);
        data.put("渠首农业子级需水", demandAgricultureQushouDay);
        return data;
    }
    /**
     * 获得当月用水计划
     * @param data
     * @param name
     * @param tableName
     * @return
     */
    public static List<List<Double>> getDataMonthPlan(List<Waterdemand> data, String name, String tableName) {

        List<Date> dateNum = new ArrayList<>();
        List<Double> month = new ArrayList<>();
        List<Double> waterData = new ArrayList<>();
        List<List<Double>> update=new ArrayList<>();
        dateNum = data.stream().filter(n -> n.getArea().equals(tableName)&&n.getUnit().equals(name)&&n.getUseWaterPlan().equals("month")).
                map(Waterdemand::getDate).distinct().collect(Collectors.toList());
        dateNum.sort(null);
        Calendar CC = Calendar.getInstance();

        for (int i=0;i<dateNum.size();i++){
            CC.setTime(dateNum.get(i));
            month.add((double)CC.get(Calendar.MONTH));
        }
        for (int x=0;x<dateNum.size();x++){
                double n = -100;
                for (int i = 0; i < data.size(); i++) {
                    String UseWaterPlan = data.get(i).getUseWaterPlan();
                    String Area = data.get(i).getArea();
                    String Unit = data.get(i).getUnit();
                    String ColName = data.get(i).getColName();
                    if (UseWaterPlan != null && Area != null && Unit != null && ColName != null) {
                        if (data.get(i).getUseWaterPlan().equals("month")&&data.get(i).getArea().equals(tableName)
                        &&data.get(i).getUnit().equals(name)&&data.get(i).getColName().equals("合计")&&
                                data.get(i).getDate().equals(dateNum.get(x))) {
                            n = data.get(i).getWaterDemendData();
                            if (n>=0){
                                waterData.add(n);
                            }
                            else {
                                waterData.add(0.0);
                            }

                        }
                    }
                }
        }
        update.add(month);
        update.add(waterData);
        return update;
    }

    /**
     * 获得当旬的用水计划
     *
     * @param data
     * @param name
     * @return
     */
    public static List<List<Double>> getDataRealTime(List<Waterdemand> data, String name, String tableName) {

        List<Date> dateNum = new ArrayList<>();
        List<Double> tenDays = new ArrayList<>();
        List<Double> waterData = new ArrayList<>();
        List<List<Double>> update=new ArrayList<>();
        dateNum = data.stream().filter(n -> n.getArea().equals(tableName)&&n.getUnit().equals(name)&&n.getUseWaterPlan().equals("tenDays")).
                map(Waterdemand::getDate).distinct().collect(Collectors.toList());
        dateNum.sort(null);
        for (int i=0;i<dateNum.size();i++){
            tenDays.add(getTenDay(dateNum.get(i)));
        }

        for (int x=0;x<dateNum.size();x++) {
            double n = -100;
            for (int i = 0; i < data.size(); i++) {
                String UseWaterPlan = data.get(i).getUseWaterPlan();
                String Area = data.get(i).getArea();
                String Unit = data.get(i).getUnit();
                if (UseWaterPlan != null && Area != null && Unit != null) {
                    if (data.get(i).getUseWaterPlan().equals("tenDays")&&data.get(i).getArea().equals(tableName)
                   &&data.get(i).getUnit().equals(name)&&data.get(i).getDate().equals(dateNum.get(x))) {
                        n = data.get(i).getWaterDemendData();
                        if (n>=0){
                            waterData.add(n);
                        }
                        else {
                            waterData.add(0.0);
                        }
                    }
                }
            }
        }
        update.add(tenDays);
        update.add(waterData);
        return update;
    }

    /**
     * 获得当前第几旬
     * @param date
     * @return
     */
    public static double getTenDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_MONTH);
        if (i < 11)
            return 0;
        else if (i < 21)
            return 1;
        else
            return 2;
    }

    /**
     * 获得本旬第几天
     * @param date
     * @return
     */
    public static double getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        int i = c.get(Calendar.DAY_OF_MONTH);
        double day=0;
        if (i < 11)
            return i-1;
        else if (i < 21)
            return i-11;
        else
            return i-21;
    }
    /**
     * 获得当天的用水计划
     *
     * @param data
     * @param name
     * @param tableName
     * @return
     */
    public static List<List<Double>> getDataRealDay(List<Waterdemand> data, String name, String tableName) {

        List<Date> dateNum = new ArrayList<>();
        List<Double> day = new ArrayList<>();
        List<Double> waterData = new ArrayList<>();
        List<List<Double>> update=new ArrayList<>();
        dateNum = data.stream().filter(n -> n.getArea().equals(tableName)&&n.getUnit().equals(name)&&n.getUseWaterPlan().equals("day")).
                map(Waterdemand::getDate).distinct().collect(Collectors.toList());
        dateNum.sort(null);
        for (int i=0;i<dateNum.size();i++){
            day.add(getDay(dateNum.get(i)));
        }

        for (int x=0;x<dateNum.size();x++) {
            double x1 = 0;
            for (int i = 0; i < data.size(); i++) {
                    String UseWaterPlan=data.get(i).getUseWaterPlan();
                    String Area=data.get(i).getArea();
                    String Unit=data.get(i).getUnit();
                    String ColName=data.get(i).getColName();
                    if (UseWaterPlan!=null&&Area!=null&&Unit!=null&&ColName!=null){
                        if (data.get(i).getUseWaterPlan().equals("day")&&data.get(i).getArea().equals(tableName)&&data.get(i).getUnit().equals(name)
                        &&data.get(i).getColName().equals("flow")&&data.get(i).getDate().equals(dateNum.get(x))) {
                            x1 += data.get(i).getWaterDemendData() * 8.64;
                        }
                    }
                }
            if (x1>=0){
                waterData.add(x1);
            }
            else {
                waterData.add(0.0);
            }
        }
        update.add(day);
        update.add(waterData);
        return update;
    }

    /**
     * 获得子级站点需水数据
     * @param data
     * @param name 子级站点名称
     * @param tableName 表名
     * @return
     */
    public static double[] getDemandDay(List<Waterdemand> data, String tableName, String[] name) {
        double[] n  =new double[name.length];
        List<Double> demand = new ArrayList<>();
        List<String> unitID = new ArrayList<>();
        for (int ii=0;ii<name.length;ii++) {
            double x = 0;
            for (int i = 0; i < data.size(); i++) {
                String UseWaterPlan = data.get(i).getUseWaterPlan();
                String Area = data.get(i).getArea();
                String subArea = data.get(i).getSubArea();
                String ColName = data.get(i).getColName();
                if (UseWaterPlan != null && Area != null && subArea != null && ColName != null) {
                    if (data.get(i).getUseWaterPlan().equals("day")) {
                        if (data.get(i).getArea().equals(tableName)) {
                            if (data.get(i).getSubArea().equals(name[ii])) {
                                if (data.get(i).getColName().equals("flow")) {
                                    x += data.get(i).getWaterDemendData()*8.64;
                                    if (x>=0){
                                        demand.add(x);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        if (demand.size()==n.length){
            for (int i = 0; i < n.length; i++) {
                n[i] = demand.get(i);
            }
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
            String UseWaterPlan=data.get(i).getUseWaterPlan();
            String Area=data.get(i).getArea();
            String Unit=data.get(i).getUnit();
            String ColName=data.get(i).getColName();
            if (UseWaterPlan!=null&&Area!=null&&Unit!=null&&ColName!=null){
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
        }
        double[] demanddata = new double[]{0,0,0};
        if (demand.size()!=3){

        }
        else {
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
            String UseWaterPlan = data.get(i).getUseWaterPlan();
            String Area = data.get(i).getArea();
            String Unit = data.get(i).getUnit();
            String ColName = data.get(i).getColName();
            if (UseWaterPlan != null && Area != null && Unit != null && ColName != null) {
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
        }
        double[] demanddata = new double[12];
        if (demand.size()!=10)
        {
            for (int i = 0; i < demanddata.length; i++)
            {
                demanddata[i] =0;
            }
            System.out.println("警告：请判断不同尺度同一供水点是否名字是否相同或者没有该供水点数据，建议提供有效的数据");
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

        demand = data.stream().filter(n -> n.getArea().equals(tableName)).map(Waterdemand::getUnit).distinct().collect(Collectors.toList());

        if (demand.size()==0){
            String[] demanddata={"无数据"};
            return demanddata;
        }
        else {
            String[] demanddata = new String[demand.size()];
            for (int i = 0; i < demand.size(); i++) {
                demanddata[i] = demand.get(i);
            }
            return demanddata;
        }
    }


    /**
     * 获得每天上报计划的名字和站点对应的id
     * @param data
     * @param tableName
     * @return
     */
    public static String[][] getNameDay(List<Waterdemand> data, String tableName,String unit) {
        List<String> demand = new ArrayList<>();
        List<String> demandID = new ArrayList<>();
        demand = data.stream().filter(n -> n.getArea().equals(tableName)&&n.getUseWaterPlan().equals("day")&&n.getUnit().equals(unit)).map(Waterdemand::getSubArea).distinct().collect(Collectors.toList());
        demandID=data.stream().filter(n -> n.getArea().equals(tableName)&&n.getUseWaterPlan().equals("day")&&n.getUnit().equals(unit)).map(Waterdemand::getUnitId).distinct().collect(Collectors.toList());
        String[][] demanddata = new String[2][demand.size()];

        for (int i = 0; i < demand.size(); i++) {
            demanddata[0][i] = demand.get(i);
            demanddata[1][i]=demandID.get(i);
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
            String UseWaterPlan = data.get(i).getUseWaterPlan();
            String Area = data.get(i).getArea();
            String Unit = data.get(i).getUnit();
            String ColName = data.get(i).getColName();
            if (UseWaterPlan != null && Area != null && Unit != null && ColName != null) {
                if (data.get(i).getUseWaterPlan().equals("year") && data.get(i).getArea().equals(tableName) && data.get(i).getUnit().equals(name)) {
                    if (month.contains(data.get(i).getColName())) {
                        demand.add(data.get(i).getWaterDemendData());
                    }
                }
            }
        }
        double[] demanddata = new double[12];
        if (demand.size()!=12)
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
            String UseWaterPlan = data.get(i).getUseWaterPlan();
            String Area = data.get(i).getArea();
            String Unit = data.get(i).getUnit();
            String ColName = data.get(i).getColName();
            if (UseWaterPlan != null && Area != null && Unit != null && ColName != null) {
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
        }
        double[] demanddata = new double[12];
        if (demand.size()!=8)
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
