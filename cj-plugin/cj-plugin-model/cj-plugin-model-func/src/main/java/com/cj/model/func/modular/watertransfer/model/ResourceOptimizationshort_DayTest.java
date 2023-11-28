package com.cj.model.func.modular.watertransfer.model;




import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.watertransfer.entity.DataInflowPrevent;
import com.cj.model.func.modular.watertransfer.method.FindValue;
import com.cj.model.func.modular.watertransfer.method.Reservoir;
import com.cj.model.func.modular.watertransfer.method.WaterTransfer;
import com.cj.model.func.modular.watertransfer.req.WaterTransferReq;

import java.text.DecimalFormat;
import java.util.*;


public class ResourceOptimizationshort_DayTest
{
//    private static double irrigate1;
//	private static int generation1;
    static double[] monthday = {31,28,31,30,31,30,31,31,30,31,30,31};
    //POA  初始化参数设计
    static double discreteAccuracy = 0.01;
//    private static double period;
    double [][]outflow_term;
    double convergencePrecision = 0.001;// 相邻两代   相差不到   万分之一
    //        double convergencePrecision_down = 0.0001;
    static double delatT = 24 * 3600;
    static double penaltyFactor = 1e8;
    static boolean isLeapYear = false;
    static double[]daynum={10,10,10};
    private static int period=10;
    static double[] minoutflow = {0.74,0.74,0.74,1.48,1.48,1.48,1.48,1.48,1.48,0.74,0.74,0.74};
    static double minOutflow;
    public ArrayList ResourceOptimizationshort_daysTest(WaterTransferReq waterTransferReq) throws Exception {
        //应用POA  进行水资源优化
        //设计调度时段  和   来水过程
        //配置水库

        Reservoir []reservoirs = new Reservoir[2];
        setReservoir(waterTransferReq.getCurve(),reservoirs);
        if (waterTransferReq.getName()==1){
            waterTransferReq.setId(2);
        }
        if (waterTransferReq.getName()==2){
            waterTransferReq.setId(1);
        }
        if (waterTransferReq.getName()==3){
            waterTransferReq.setId(3);
        }
//        req.SetReservoirs();
        int RNum = 2;

        Date time= waterTransferReq.getStartTime();
        Calendar CC=Calendar.getInstance();
        CC.setTime(time);
        int[]date=new int[3];
        date[0]=CC.get(Calendar.YEAR);
        date[1]=CC.get(Calendar.MONTH)+1;
        date[2]=CC.get(Calendar.DAY_OF_MONTH);
        int xnum=(date[2]%10)-1;
        int id= waterTransferReq.getId();
       int yearid=date[0];
        int yearN = yearid ;
        if((yearN%4 == 0 && yearN % 100!=0) || (yearN%400==0))
        {
            isLeapYear = true;
            monthday[1] = 29;
        }
        int  monthNum=(int)monthday[date[1]-1];
        minOutflow=minoutflow[date[1]-1];
        daynum[2]=monthNum-20;
        if (date[2]<=10)
        {
            period=10-date[2]+1;
        }
        if (date[2]>10&&date[2]<=20)
        {
            period=20-date[2]+1;

        }
        if (date[2]>20)
        {
            period=monthNum-date[2]+1;
        }




        //设置初始解+适应度评估
        //配置初始解
        double[][] wl_term = new double[2][period+1];//这个水位是坝上水位
        double[][] outflow_term = new double[2][period];
        double[][] outflow_down_term = new double[2][period];
//        double[][] fitness_term = new double[2][6];
        double[] levelBegin = {waterTransferReq.getLevelBeginLzz(), waterTransferReq.getLevelBeginTth()};
        double[] levelEnd =  {waterTransferReq.getLevelEndLzz(), waterTransferReq.getLevelEndTth()};
         id= waterTransferReq.getId();

        double[][] waterDemand1= new double[5][period];
        double [][]inflow1=new  double[2][period];
        double[][]waterdemand31=new double[6][period];
        double[][]waterdemand41=new double[9][period];
        Map<String, Object> data1 = new HashMap<>();
        double[][] inflow=new double[2][period];

        data1=SetInflow(waterTransferReq, waterTransferReq.getData(),86400);
        inflow[0]=(double[])data1.get("楼庄子流量");
        inflow[1]=(double[])data1.get("头屯河流量");
        double [][]waterDemand=new double[5][period];
//        waterDemand=req.getWaterDemand_day();
//        double [][]inflow=new   double[2][period];
        double[][]waterdemand3=new double[6][period];
        double[][]waterdemand4=new double[8][period];
//        inflow1[0]=new double[]{3.352,2.861,2.78,0.981,2.617,3.352,3.352,3.148,3.376,3.434};
//        inflow1[1]=new double[]{0,0,0,0,0,0,0,0,0,0};
        waterDemand1[0]= new double[]{8,8,8,8,8,8,8,8,8,8};
        waterDemand1[1]= new double[]{3.05,2.6,2.53,0.89,2.38,3.05,3.05,3.11,3.07,3.12};
        waterDemand1[2]= new double[]{7.18,6.13,5.96,2.1,5.61,7.18,7.18,7.32,7.24,7.36};

        waterDemand1[3]= new double[]{13.71,11.7,11.37,4.01,10.7,13.71,13.71,13.98,13.81,14.05};
        waterDemand1[4]= new double[]{13.71,11.7,11.37,4.01,10.7,13.71,13.71,13.98,13.81,14.05};


        waterdemand31[0]= new double[]{3.32,2.83,2.75,0.97,2.59,3.32,3.32,3.38,3.34,3.4};
        waterdemand31[1]=new double[]{2.74,2.34,2.27,0.8,2.14,2.74,2.74,2.8,2.76,2.81};
        waterdemand31[2]= new double[]{3.84,3.28,3.18,1.12,3,3.84,3.84,3.91,3.87,3.93};
        waterdemand31[3]=new double[]{2.44,2.08,2.02,0.71,1.9,2.44,2.44,2.49,2.46,2.5};
        waterdemand31[4]=new double[]{0.96,0.82,0.8,0.28,0.75,0.96,0.96,0.98,0.97,0.98};
        waterdemand31[5]=new double[]{0.41,0.35,0.34,0.12,0.32,0.41,0.41,0.42,0.41,0.42};


        waterdemand41[0]= new double[]{1.56,1.33,1.3,0.46,1.22,1.56,1.56,1.59,1.57,1.6};
        //jihua1
        waterdemand41[1]= new double[]{2.25,1.92,1.87,0.66,1.76,2.25,2.25,2.3,2.27,2.31};
        waterdemand41[2]= new double[]{5.4,4.61,4.48,1.58,4.21,5.4,5.4,5.51,5.44,5.53};
        waterdemand41[3]= new double[]{2.55,2.18,2.12,0.75,1.99,2.55,2.55,2.60,2.57,2.62};

        waterdemand41[4]= new double[]{0.69,0.59,0.58,0.2,0.54,0.69,0.69,0.71,0.7,0.71};
        waterdemand41[5]= new double[]{0.61,0.52,0.51,0.18,0.48,0.61,0.61,0.63,0.62,0.63};
        waterdemand41[6]= new double[]{0.38,0.33,0.32,0.11,0.3,0.38,0.38,0.39,0.39,0.39};
        waterdemand41[7]= new double[]{0.25,0.21,0.2,0.07,0.19,0.25,0.25,0.25,0.25,0.25};


//        for (int x= xnum;x<period;x++){
//
//            inflow[0][x-xnum]=inflow1[0][x];
//            inflow[1][x-xnum]=inflow1[1][x];
//        }


        for (int x= xnum;x<period+xnum;x++){
            waterDemand[0][x-xnum]=waterDemand1[0][x];
            waterDemand[1][x-xnum]=waterDemand1[1][x];
            waterDemand[2][x-xnum]=waterDemand1[2][x];
            waterDemand[3][x-xnum]=waterDemand1[3][x];
            waterDemand[4][x-xnum]=waterDemand1[4][x];
        }

        for (int x1=0;x1<waterdemand3.length;x1++) {
            for (int x = xnum; x < period; x++) {
                waterdemand3[x1][x - xnum] = waterdemand31[x1][x];
            }
        }
        for (int x1=0;x1<waterdemand4.length;x1++) {
            for (int x = xnum; x < period; x++) {
                waterdemand4[x1][x - xnum] = waterdemand41[x1][x];
            }
        }
//        if (req.getInflow()!=null) {
//            inflow = req.getInflow();
//        }
//        else {
//            System.out.println("数据不足");
//        }
        for (int r = 0; r < RNum; r++)
        {
            wl_term[r][0] = levelBegin[r];
            for (int t = 1; t < period; t++)
            {
                wl_term[r][t] = Math.max(levelBegin[r],levelEnd[r]);
            }
            wl_term[r][period] = levelEnd[r];
        }


        //初始解模拟计算
        // 计算调度过程(模拟模型)用坝上水位为输入
        double[] fit=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[0];

        //下边是优化算法得参数配置、优化目标配置，罚函数配置、约束处理配置（下边是优化问题）
        //计算约束违反程度  以及  目标适应度

        double[] fitness_term = fit;

        //全局最优解
        double[][] wl_best = new double[RNum][period+1];
        double[] fitness_best = new double[3];
        fitness_best[2] = Double.MAX_VALUE;

        double[][] wl_term_best = new double[RNum][period+1];
        //迭代过程最优解
        for (int x=0;x<RNum;x++){
            for (int ttt=0;ttt<1+period;ttt++){
                wl_term_best[x][ttt]=wl_term[x][ttt];
            }
        }

//        double[][] wl_term_best = wl_term.clone();

        double[] fitness_term_best = fitness_term.clone();

        int Bindex = 0;//迭代计数器
        //这里以下是优化过程,这里比较适应度精度

        if (id==3)
        {
            RNum=1;
            while ( compareAB(fitness_term_best, fitness_best) == -1 )
            {
                Bindex++;
                wl_best = wl_term_best.clone();
                fitness_best = fitness_term_best.clone();

                for (int n=0;n<RNum;n++)
                {

                    for (int tt = 1; tt < period; tt++)
                    {
                        //初始  和  最终水位  不参与调整
                        double maxLevel = waterTransferReq.getReservoirs()[n].levelFloodDesign;
//                double maxLevel = reservoir.levelFloodCheck;
                        double minLevel = waterTransferReq.getReservoirs()[n].levelDead;
                        int wlNum = (int) ((maxLevel - minLevel) / discreteAccuracy + 1);//变量离散过程

                        for (int i = 0; i < wlNum; i++)//调用模拟模型计算
                        {
                            double termWL = minLevel + discreteAccuracy * i;
                            for (int x=0;x<RNum;x++){
                                for (int ttt=0;ttt<1+period;ttt++){
                                    wl_term[x][ttt]=wl_term_best[x][ttt];
                                }
                            }
//                       wl_term = wl_best.clone();

                            wl_term[n][tt] = termWL;

                            fitness_term = calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[0];

                            if (compareAB(fitness_term, fitness_term_best) == -1)
                            {
                                fitness_term_best = fitness_term.clone();
                                for (int x=0;x<RNum;x++){
                                    for (int ttt=0;ttt<1+period;ttt++){
                                        wl_term_best[x][ttt]=wl_term[x][ttt];
                                    }
                                }
//                            wl_term_best = wl_term.clone();
                            }
                            else
                            {

                            }
                        }
                    }
                }


//                System.out.println("第\t" + Bindex +
//                        "\t次\t约束违反程度\t" + fitness_term_best[0] +
//                        "\t供水缺额\t" + fitness_term_best[1] +
//                        "\t供水缺额+惩罚\t" + fitness_term_best[2]
//                );


            }
        }
        else
        {
            while ( compareAB(fitness_term_best, fitness_best) == -1 )
            {
                Bindex++;
                wl_best = wl_term_best.clone();
                fitness_best = fitness_term_best.clone();

                for (int n=0;n<RNum;n++)
                {

                    for (int tt = 1; tt < period; tt++)
                    {
                        //初始  和  最终水位  不参与调整
                        double maxLevel = waterTransferReq.getReservoirs()[n].levelFloodDesign;
//                double maxLevel = reservoir.levelFloodCheck;
                        double minLevel = waterTransferReq.getReservoirs()[n].levelDead;
                        int wlNum = (int) ((maxLevel - minLevel) / discreteAccuracy + 1);//变量离散过程

                        for (int i = 0; i < wlNum; i++)//调用模拟模型计算
                        {
                            double termWL = minLevel + discreteAccuracy * i;
                            for (int x=0;x<RNum;x++){
                                for (int ttt=0;ttt<1+period;ttt++){
                                    wl_term[x][ttt]=wl_term_best[x][ttt];
                                }
                            }
//                       wl_term = wl_best.clone();

                            wl_term[n][tt] = termWL;

                            fitness_term = calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[0];

                            if (compareAB(fitness_term, fitness_term_best) == -1)
                            {
                                fitness_term_best = fitness_term.clone();
                                for (int x=0;x<RNum;x++){
                                    for (int ttt=0;ttt<1+period;ttt++){
                                        wl_term_best[x][ttt]=wl_term[x][ttt];
                                    }
                                }
//                            wl_term_best = wl_term.clone();
                            }
                            else
                            {

                            }
                        }
                    }
                }


//                System.out.println("第\t" + Bindex +
//                        "\t次\t约束违反程度\t" + fitness_term_best[0] +
//                        "\t供水缺额\t" + fitness_term_best[1] +
//                        "\t供水缺额+惩罚\t" + fitness_term_best[2]
//                );


            }

        }

        System.out.println("————————————————————————————————————输出最终  调度结果——————————————————————————————————");
//        wl_term = wl_term_best.clone();
        RNum=2;
        for (int x=0;x<RNum;x++){
            for (int ttt=0;ttt<1+period;ttt++){
                wl_term[x][ttt]=wl_term_best[x][ttt];
            }
        }
        double[][] capacity_proportion = new double[2][period];
        double[][]ReservoirWaterdemand= new double[2][period];
        double[][]ReservoirWatersupply= new double[2][period];
        double[][]inflowWater_supply= new double[2][period];
        double[] watersupply_lzz = new double[period];

        double[][] water_shortage = new double[2][period];
        double[] inflow_toutunhe ;
        int[] time1=new int[period];
        double[][]waterSupply=new double[waterDemand.length][period];
        double[][]levelbegin=new double[2][period];
        double[][]levelend=new double[2][period];
        double[][] inflow_water = new double[2][period];
        double[][] outflow_water = new double[2][period];
        double[] inflowwater_toutunhe = new double[period];
        double[][] deltawater = new double[2][period];
        double [][]endCapacity=new double[2][period];
        double []waterdemand_all=new double[period];
        double []waterSupply_all=new double[period];

        double[][]preSupplyWater=new double[2][period];
        double[][]supply_water_two=new double[2][period];

//        waterdemand3=req.getWaterDemand_east_day();
//        waterdemand4=req.getWaterDemand_west_day();

        double[][]waterSupply3=new double[waterdemand3.length][period];
        double[][]waterSupply4=new double[waterdemand4.length][period];
        double[][] proportion = new double[5][period];
        fitness_term = calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[0];
        outflow_term[0]= calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[1];
        outflow_term[1]= calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[2];
        water_shortage[0]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[3];
        water_shortage[1]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[4];
        inflow_toutunhe=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[5];
        waterSupply[0]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[6];
        waterSupply[1]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[7];
        waterSupply[2]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[8];
        waterSupply[3]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[9];
        waterSupply[4]=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[10];
         watersupply_lzz=calFit(id, waterTransferReq.getReservoirs(),period,wl_term,waterDemand,inflow)[11];
        DecimalFormat da=new DecimalFormat("#.00");

        for (int n1=0;n1<period;n1++){

            supply_water_two[0][n1]=Double.parseDouble(da.format(watersupply_lzz[n1]));
            supply_water_two[1][n1]=Double.parseDouble(da.format(waterSupply[1][n1]+waterSupply[2][n1]+waterSupply[3][n1]+waterSupply[4][n1]));
            waterdemand_all[n1]=waterDemand[0][n1]+waterDemand[1][n1]+waterDemand[2][n1]+waterDemand[3][n1]+waterDemand[4][n1];
            waterSupply_all[n1]=waterSupply[0][n1]+waterSupply[1][n1]+waterSupply[2][n1]+waterSupply[3][n1]+waterSupply[4][n1];
            ReservoirWaterdemand[0][n1]=waterDemand[0][n1];
            ReservoirWaterdemand[1][n1]= waterdemand_all[n1]-waterDemand[0][n1];
            ReservoirWatersupply[0][n1]=Double.parseDouble(da.format(watersupply_lzz[n1]));
            ReservoirWatersupply[1][n1]=Double.parseDouble(da.format(waterSupply_all[n1]-waterSupply[0][n1]));
        }

        for (int x=0;x<period;x++){
            double sup=waterSupply_all[x];
            double eco=minOutflow*delatT/1e4;
            double  all_num=eco+sup;

            proportion[0][x]=(eco)/(all_num);
            proportion[1][x]=(waterSupply[0][x]+waterSupply[1][x])/(all_num);
            proportion[2][x]=(waterSupply[2][x])/(all_num);
            proportion[3][x]=0.75*(waterSupply[3][x]+waterSupply[4][x])/(all_num);
            proportion[4][x]=0.25*(waterSupply[3][x]+waterSupply[4][x])/(all_num);

            proportion[0][x]= Double.parseDouble(da.format(proportion[0][x]));
            proportion[1][x]= Double.parseDouble(da.format(proportion[1][x]));
            proportion[2][x]= Double.parseDouble(da.format(proportion[2][x]));
            proportion[3][x]= Double.parseDouble(da.format(proportion[3][x]));
            proportion[4][x]= Double.parseDouble(da.format(proportion[4][x]));

        }


        DecimalFormat da1=new DecimalFormat("#.00");

        for (int m=0;m<waterdemand3.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemand[3][n]==0)
                {
                    waterSupply3[m][n]=0;
                }
                else {
                    waterSupply3[m][n] = Double.parseDouble(da1.format((waterSupply[3][n] / waterDemand[3][n]) * waterdemand3[m][n]));
                }
            }
        }
        for (int m=0;m<waterdemand4.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemand[4][n]==0)
                {
                    waterSupply4[m][n]=0;
                }
                else {
                    waterSupply4[m][n] = Double.parseDouble(da1.format((waterSupply[4][n] / waterDemand[4][n]) * waterdemand4[m][n]));
                }
            }
        }
//        System.out.println("最终结果\t约束违反程度\t" + fitness_term_best[0] +
//                "\t供水缺额\t" + fitness_term_best[1] +
//                "\t供水缺额+惩罚\t" + fitness_term_best[2]
//        );


//        System.out.print("时段\t");
//        System.out.print("初水位\t");
//        System.out.print("末水位\t");
//        System.out.print("来水\t");
//        System.out.print("来水水量\t");
//        System.out.print("下泄\t");
//        System.out.print("出库水量\t");
//        System.out.print("库容变化水量\t");
//        System.out.print("供水计划\t");
//        System.out.print("供水水量\t");
//        System.out.println("供水缺额\t");
        double[]capacity=new double[]{7374,2030};


        for (int r=0;r<RNum;r++)
        {
            for (int t = 0; t < period; t++) {
                time1[t]=t+1;
                levelbegin[r][t]=Double.parseDouble(da1.format(wl_term[r][t]));
                levelend[r][t]=Double.parseDouble(da1.format(wl_term[r][t+1]));
                endCapacity[r][t]=Double.parseDouble(da1.format(FindValue.FindV2ByV1(waterTransferReq.getReservoirs()[r].wlc_wl, waterTransferReq.getReservoirs()[r].wlc_c, wl_term[r][t+1])));
                capacity_proportion[r][t]=Double.parseDouble(da1.format(endCapacity[r][t]/capacity[r]));
                if (r==0)
                {
                    inflow_water[r][t]=Double.parseDouble(da1.format(inflow[r][t]*delatT/1e4));
                    outflow_water[r][t]=Double.parseDouble(da1.format(outflow_term[r][t]*delatT/1e4));
                    deltawater[r][t]=Double.parseDouble(da1.format((inflow[r][t]-outflow_term[r][t])*delatT/1e4));
                    preSupplyWater[r][t]=Double.parseDouble(da1.format(inflow_water[r][t]+FindValue.FindV2ByV1(waterTransferReq.getReservoirs()[r].wlc_wl, waterTransferReq.getReservoirs()[r].wlc_c, wl_term[r][t])-
                            FindValue.FindV2ByV1(waterTransferReq.getReservoirs()[r].wlc_wl, waterTransferReq.getReservoirs()[r].wlc_c, 1353.3)));
                    inflowWater_supply[r][t]=inflow_water[r][t]-ReservoirWatersupply[r][t];

                }
                else {
                    inflow_water[r][t]=Double.parseDouble(da1.format(inflow[r][t]*delatT/1e4));
                    inflowwater_toutunhe[t]=Double.parseDouble(da1.format(inflow_toutunhe[t]*delatT/1e4));
                    outflow_water[r][t]=Double.parseDouble(da1.format(outflow_term[r][t]*delatT/1e4));
                    deltawater[r][t]=Double.parseDouble(da1.format((inflow_toutunhe[t]-outflow_term[r][t])*delatT/1e4));
                    preSupplyWater[r][t]=Double.parseDouble(da1.format(inflowwater_toutunhe[t]+FindValue.FindV2ByV1(waterTransferReq.getReservoirs()[r].wlc_wl, waterTransferReq.getReservoirs()[r].wlc_c, wl_term[r][t])-
                            FindValue.FindV2ByV1(waterTransferReq.getReservoirs()[r].wlc_wl, waterTransferReq.getReservoirs()[r].wlc_c, 973)));
                    inflowWater_supply[r][t]=inflowwater_toutunhe[t]-ReservoirWatersupply[r][t];

                }

            }

        }
        ArrayList<WaterTransfer> result = new ArrayList<>();

        WaterTransfer waterTransfer = new WaterTransfer();
//        waterTransfer.setTime(time1);
        waterTransfer.setLevelbegin(levelbegin);
        waterTransfer.setLevelend(levelend);
        waterTransfer.setInflow(inflow);
        waterTransfer.setInflow_water(inflow_water);
        waterTransfer.setOutflow(outflow_term);
        waterTransfer.setOutflow_water(outflow_water);
        waterTransfer.setInflow_toutunhe(inflow_toutunhe);
        waterTransfer.setInflow_water_toutunhe(inflowwater_toutunhe);
        waterTransfer.setWater_shortage(water_shortage);
        waterTransfer.setDeltawater(deltawater);
        waterTransfer.setEndCapacity(endCapacity);
        waterTransfer.setWaterdemand(waterDemand);
        waterTransfer.setWaterdemand_all(waterdemand_all);
        waterTransfer.setWaterSupply(waterSupply);
        waterTransfer.setWaterSupply_all(waterSupply_all);
        waterTransfer.setWaterSupply3(waterSupply3);
        waterTransfer.setWaterSupply4(waterSupply4);
        waterTransfer.setProportion(proportion);
        waterTransfer.setEvaluation(id);
        waterTransfer.setPreSupplyWater(preSupplyWater);
        waterTransfer.setCapacity_proportion(capacity_proportion);
        waterTransfer.setReservoirWaterdemand(ReservoirWaterdemand);
        waterTransfer.setReservoirWatersupply(ReservoirWatersupply);
        waterTransfer.setInflowWater_supply(inflowWater_supply);
        waterTransfer.setSupply_water_two(supply_water_two);
        result.add(waterTransfer);
        return  result;
    }

    public static double[][] calFit(int id, Reservoir[] reservoir, int period,
                                    double[][] wl_term, double[][] waterDemand , double[][] inflow ) throws Exception
    {

        //配置初始解
//        double[][] wl_term = new double[2][period+1];//这个水位是坝上水位
        double[][] outflow_term = new double[2][period];
        double[][] levelbegin = new double[2][period];
        double[][] levelend = new double[2][period];
        double[][] result ;
        int demandNum=5;
        //需水流量
        double[][] waterdemandQ = new double[demandNum][period];
        //各用水类型缺水额度
        double[][] water_shortage = new double[demandNum][period];
        //两水库缺水额度
        double[][] water_shortage1 = new double[2][period];
        double[] watershortage_allQ = new double[period];
        double[] inflow_toutunhe = new double[period];
        double[] maxOutflow = new double[2];
//        double[] minOutflow = new double[2];

//        double[][] outflow_down_term = new double[2][period];
//        double[][] fitness_term = new double[2][6];
        //初始解模拟计算
        // 计算调度过程(模拟模型)用坝上水位为输入
        for (int t1=0;t1<demandNum;t1++){
            for (int t2=0;t2<period;t2++){
                waterdemandQ[t1][t2]=waterDemand[t1][t2]*1e4/(delatT);
            }
        }
        for (int t = 0; t < period; t++)
        {

            double deltaV = FindValue.FindV2ByV1(reservoir[0].wlc_wl, reservoir[0].wlc_c, wl_term[0][t])
                    - FindValue.FindV2ByV1(reservoir[0].wlc_wl, reservoir[0].wlc_c, wl_term[0][t+1]);//库容时段差值
            double termOutflow = deltaV * 1e4 / (delatT) + inflow[0][t];//水量平衡计算

            double Outflowmax = FindValue.FindV2ByV1(reservoir[0].wlob_wl, reservoir[0].wlob_ob, wl_term[0][t]);
            DecimalFormat da1=new DecimalFormat("#.00");
            outflow_term[0][t] = Double.parseDouble(da1.format(termOutflow));
            //出流赋值
        }

        for (int t = 0; t < period; t++)
        {
            double deltaV = FindValue.FindV2ByV1(reservoir[1].wlc_wl, reservoir[1].wlc_c, wl_term[1][t])
                    - FindValue.FindV2ByV1(reservoir[1].wlc_wl, reservoir[1].wlc_c, wl_term[1][t+1]);//库容时段差值
//            double localInflow = inflow[1][t] - inflow[0][t];
            double  termOutflow=0;
            //0为楼庄子水厂需水，1为红岩渠城市用水，2工业用水，3西干，4东干
            if (outflow_term[0][t] >=waterdemandQ[0][t]+minOutflow) {
                termOutflow = deltaV * 1e4 / (delatT) + inflow[1][t] + outflow_term[0][t]-waterdemandQ[0][t];//水量平衡计算
                //上游实际来水
                inflow_toutunhe[t]= inflow[1][t] + outflow_term[0][t]-waterdemandQ[0][t];
            }
             if (outflow_term[0][t] >minOutflow&&outflow_term[0][t] <minOutflow+waterdemandQ[0][t]){
                termOutflow = deltaV * 1e4 / (delatT) + inflow[1][t] +minOutflow;
                //上游实际来水
                inflow_toutunhe[t]= inflow[1][t] + minOutflow;
            }
              if (outflow_term[0][t] >0&&outflow_term[0][t] <=minOutflow){
                termOutflow = deltaV * 1e4 / (delatT) + inflow[1][t] + outflow_term[0][t];
                //上游实际来水
                inflow_toutunhe[t]=inflow[1][t]+outflow_term[0][t];
            }
             if(outflow_term[0][t] <=0){
                termOutflow = deltaV * 1e4/ (delatT) + inflow[1][t];
                inflow_toutunhe[t]=inflow[1][t];
            }
            double Outflowmax = FindValue.FindV2ByV1(reservoir[1].wlob_wl, reservoir[1].wlob_ob, wl_term[1][t]);
            DecimalFormat da1=new DecimalFormat("#.00");
            outflow_term[1][t] = Double.parseDouble(da1.format(termOutflow));
            //出流赋值

        }

        //下边是优化算法得参数配置、优化目标配置，罚函数配置、约束处理配置（下边是优化问题）
        //计算约束违反程度  以及  目标适应度
        double constraintViolation = 0;//约束违反程度  采用平方值比较好
        double fitness1 = 0; double fitness2 = 0; // 供水缺额最小   发电量最大
        double fitness1_penalty = 0; double fitness2_penalty = 0; //   水库   罚函数
        //水库2的可供水量
        double[] water_Supply_tth = new double[period];
        //各用水类型实际供水
        double[][] waterSupply = new double[demandNum][period];
        double[] watersupply_lzz = new double[period];
        for (int t=0;t<period;t++){
            for (int tt=1;tt<waterDemand.length;tt++)
            {
                //水库2需水总流量
                watershortage_allQ[t]+=waterdemandQ[tt][t];
            }
        }
        for (int m=0;m<reservoir.length;m++)
        {

            for (int t = 0; t < period; t++)
            {
                //流量约束
                maxOutflow[m] = FindValue.FindV2ByV1(reservoir[m].wlob_wl, reservoir[m].wlob_ob, wl_term[m][t]);
//                minOutflow[m] = reservoir[m].outflowMin;

                if (outflow_term[m][t] > maxOutflow[m])//下泄流量约束处理,约束处理在罚函数里边体现
                {
                    constraintViolation += Math.pow((outflow_term[m][t] - maxOutflow[m]), 2);
                } else if (outflow_term[m][t] < minOutflow) {
                    constraintViolation += Math.pow((outflow_term[m][t] - minOutflow), 2);
                }

                //供水缺额计算  首先兼顾生态流量、城市用水、工业用水

                DecimalFormat da=new DecimalFormat("#.00");

                if (m == 0)
                {
                    if (outflow_term[m][t] > minOutflow&&outflow_term[m][t] < minOutflow+ waterdemandQ[0][t]) {
                        if (id==1||id==3)
                        {
                            fitness1 += (minOutflow + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        //水库1缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format( (minOutflow + waterdemandQ[m][t]  - outflow_term[m][t]) * (delatT)/ 1e4));
                        waterSupply[m][t]=Double.parseDouble(da.format(waterdemandQ[0][t]*(delatT) / 1e4-water_shortage1[m][t]));
                         watersupply_lzz[t]=waterSupply[m][t];

                    }
                    if (outflow_term[m][t] <= minOutflow) {
                        if (id==1||id==3)
                        {
                            fitness1 += (minOutflow + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        //水库1缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format( ( waterdemandQ[m][t]  ) * (delatT)/ 1e4));
                        waterSupply[m][t]=0;
                        watersupply_lzz[t]=0;

                    }
                    if (outflow_term[m][t] >=minOutflow+ waterdemandQ[0][t]) {

                        //水库1缺水额度
                        water_shortage1[m][t] =0 ;
                        waterSupply[m][t]=Double.parseDouble(da.format( ( waterdemandQ[m][t]  ) * (delatT)/ 1e4));
                        watersupply_lzz[t]=Double.parseDouble(da.format((outflow_term[m][t] -minOutflow)* (delatT)/ 1e4));

                    }


//                levelbegin[m][t] = wl_term[m][t];
//                levelend[m][t] = wl_term[m][t + 1];
                }

                else  if(m==1)
                {
                    if (outflow_term[m][t] > minOutflow&&outflow_term[m][t] < minOutflow + watershortage_allQ[t] )
                    {
                        if (id==1||id==3){
                            fitness1 += (minOutflow + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4;}
                        //水库2缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format((minOutflow +  watershortage_allQ[t]  - outflow_term[m][t]) * (delatT) / 1e4));
                        //水库2可供水量
                        water_Supply_tth[t]=(watershortage_allQ[t])*(delatT) / 1e4-water_shortage1[m][t];
                    }
                    if (outflow_term[m][t] <= minOutflow){
                        if (id==1||id==3){
                            fitness1 += (minOutflow + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4;}
                        //水库2缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format((  watershortage_allQ[t]  ) * (delatT) / 1e4));
                        //水库2可供水量
                        water_Supply_tth[t]=0;
                    }
                    if (outflow_term[m][t] >= minOutflow + watershortage_allQ[t] ){
                        water_Supply_tth[t]=waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t];
                        water_shortage1[m][t]=0;
                    }

                }

            }

        }
        //缺水时供水比例
        double[] f=new double[period];
        for (int t1=0;t1<period;t1++){
            f[t1]=1-water_shortage1[1][t1]/(watershortage_allQ[t1]* (delatT));
        }
        //供水量

        if (id==1||id==3){
            for (int t=0;t<period;t++)
            {
                if (water_Supply_tth[t]>=waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])
                {
                    waterSupply[1][t]= waterDemand[1][t];
                    waterSupply[2][t]= waterDemand[2][t];
                    waterSupply[3][t]= waterDemand[3][t];
                    waterSupply[4][t]= waterDemand[4][t];
                }
                if (water_Supply_tth[t]>=waterDemand[1][t]+waterDemand[2][t]&&water_Supply_tth[t]<waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t]){
                    waterSupply[1][t]= waterDemand[1][t];
                    waterSupply[2][t]= waterDemand[2][t];
                    waterSupply[3][t]= 0.5*(water_Supply_tth[t]-(waterDemand[1][t]+waterDemand[2][t]));
                    waterSupply[4][t]= 0.5*(water_Supply_tth[t]-(waterDemand[1][t]+waterDemand[2][t]));
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
                if (water_Supply_tth[t]>=waterDemand[1][t]&&water_Supply_tth[t]<waterDemand[1][t]+waterDemand[2][t]){
                    waterSupply[1][t]= waterDemand[1][t];
                    waterSupply[2][t]= water_Supply_tth[t]-waterDemand[1][t];
                    waterSupply[3][t]= 0;
                    waterSupply[4][t]= 0;
                }
                if (water_Supply_tth[t]>=0&&water_Supply_tth[t]<waterDemand[1][t]){
                    waterSupply[1][t]= water_Supply_tth[t];
                    waterSupply[2][t]= 0;
                    waterSupply[3][t]= 0;
                    waterSupply[4][t]= 0;
                }
//
            }
        }
        if (id==2){
            for (int t=0;t<period;t++)
            {
                if (water_Supply_tth[t]>=waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])
                {
                    waterSupply[1][t]= waterDemand[1][t];
                    waterSupply[2][t]= waterDemand[2][t];
                    waterSupply[3][t]= waterDemand[3][t];
                    waterSupply[4][t]= waterDemand[4][t];
                }
                if (water_Supply_tth[t]>=0.8*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])
                        &&water_Supply_tth[t]<waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t]){
                    waterSupply[1][t]=0.9* waterDemand[1][t];
                    waterSupply[2][t]=0.9* waterDemand[2][t];
                    waterSupply[3][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    waterSupply[4][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
                if (water_Supply_tth[t]>=0.5*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])
                        &&water_Supply_tth[t]<0.8*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])){
                    waterSupply[1][t]= 0.7*waterDemand[1][t];
                    waterSupply[2][t]=0.7* waterDemand[2][t];
                    waterSupply[3][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    waterSupply[4][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
                if (water_Supply_tth[t]>=0.2*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])
                        &&water_Supply_tth[t]<0.5*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t])){
                    waterSupply[1][t]= 0.4*waterDemand[1][t];
                    waterSupply[2][t]=0.4* waterDemand[2][t];
                    waterSupply[3][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    waterSupply[4][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
                if (water_Supply_tth[t]<0.2*(waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t]))
                {
                    waterSupply[1][t]= 0.1*waterDemand[1][t];
                    waterSupply[2][t]=0.1* waterDemand[2][t];
                    waterSupply[3][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    waterSupply[4][t]= 0.5*(water_Supply_tth[t]-(waterSupply[1][t]+waterSupply[2][t]));
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
//
            }
        }

        if (id==2){
            for (int m=0;m<waterDemand.length;m++)
                for (int n=0;n<period;n++)
                {
                    if (waterDemand[m][n]==0){

                    }
                    else {
                        fitness1 +=1-waterSupply[m][n]/waterDemand[m][n];
                    }
                }
        }



        fitness1_penalty = fitness1 + penaltyFactor * constraintViolation;//
        fitness2_penalty = fitness2 - penaltyFactor * constraintViolation;//
        double[] fitness = new double[]{constraintViolation, fitness1, fitness1_penalty};
        //适应度，1水库下泄流量，2水库下泄，1水库缺额，2水库缺额,2水库实际来水，0楼庄子供水，1红岩城市用水，2八钢工业，3西干，4东干
        result=new double[][]{fitness,outflow_term[0],outflow_term[1],water_shortage[0],water_shortage[1],inflow_toutunhe
                ,waterSupply[0],waterSupply[1],waterSupply[2],waterSupply[3],waterSupply[4],watersupply_lzz};
        return result;

    }

    public void setReservoir(List<CurveParam> data, Reservoir[] reservoir ){

        List<Double> capacity = new ArrayList<>();
        List<Double> level = new ArrayList<>();
        List<Double> outflow = new ArrayList<>();
        List<Double> outflow_level = new ArrayList<>();
        reservoir[0] = new Reservoir();
        reservoir[1] = new Reservoir();


        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId()==100){
                capacity.add(data.get(i).getValue());
                level.add(data.get(i).getLevel());
            }
            if (data.get(i).getId()==104)
            {
                outflow.add(data.get(i).getValue());
                outflow_level.add(data.get(i).getLevel());
            }
        }
        double[] wlc_wl=new double[level.size()];
        double[] wlc_c=new double[capacity.size()];
        for (int i = 0; i < wlc_wl.length; i++) {
            wlc_wl[i] = level.get(i);
            wlc_c[i]=capacity.get(i);
        }

        double[] wlob_wl=new double[outflow_level.size()];
        double[] wlob_ob=new double[outflow.size()];
        for (int i = 0; i < wlob_wl.length; i++) {
            wlob_wl[i] = outflow_level.get(i);
            wlob_ob[i]=outflow.get(i);
        }

        reservoir[0].wlc_c=wlc_c;
        reservoir[0].wlc_wl=wlc_wl;
        reservoir[0].wlob_wl=wlob_wl;
        reservoir[0].wlob_ob=wlob_ob;


        List<Double> capacity1 = new ArrayList<>();
        List<Double> level1 = new ArrayList<>();
        List<Double> outflow1 = new ArrayList<>();
        List<Double> outflow_level1 = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId()==200){
                capacity1.add(data.get(i).getValue());
                level1.add(data.get(i).getLevel());
            }
            if (data.get(i).getId()==204)
            {
                outflow1.add(data.get(i).getValue());
                outflow_level1.add(data.get(i).getLevel());
            }
        }
        double[] wlc_wl1=new double[level1.size()];
        double[] wlc_c1=new double[capacity1.size()];

        double[] wlob_wl1=new double[outflow_level1.size()];
        double[] wlob_ob1=new double[outflow1.size()];
        for (int i = 0; i < wlob_wl1.length; i++) {
            wlob_wl1[i] = outflow_level1.get(i);
            wlob_ob1[i]=outflow1.get(i);
        }
        for (int i = 0; i < wlc_wl1.length; i++) {
            wlc_wl1[i] = level1.get(i);
            wlc_c1[i]=capacity1.get(i);
        }
        reservoir[1].wlc_c=wlc_c1;
        reservoir[1].wlc_wl=wlc_wl1;
        reservoir[1].wlob_wl=wlob_wl1;
        reservoir[1].wlob_ob=wlob_ob1;

        reservoir[0].name = "楼庄子";
        reservoir[0].levelFloodControl = 1394.5;
        reservoir[0].levelNormal = 1394.5;
        reservoir[0].levelFloodLimiting = 105;
        reservoir[0].levelDead = 1353.3;
        reservoir[0].outflowMin = 1.48;
        reservoir[0].levelFloodDesign = 1397.41;
        reservoir[0].levelFloodCheck = 1397.63;

        reservoir[1].name = "头屯河";
        reservoir[1].levelFloodControl = 987;
        reservoir[1].levelNormal = 989.6;
        reservoir[1].levelFloodLimiting = 105;
        reservoir[1].levelDead = 975;
        reservoir[1].outflowMin = 1.48;
        reservoir[1].levelFloodDesign = 991.2;
        reservoir[1].levelFloodCheck = 992.54;
    }
    public  Map<String, Object> SetInflow(WaterTransferReq waterTransferReq, Map<String,List<DataInflowPrevent>> DataInflowPrevent, int t)
    {
        List<Double> inflow_lzz = new ArrayList<>();
        List<Double> inflow_tth = new ArrayList<>();

        List<Date> Time = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        List<DataInflowPrevent> data_FloodPrevent_all = waterTransferReq.getData().get("lzz");
        for (int i = 0; i < data_FloodPrevent_all.size(); i++)
        {
            if (data_FloodPrevent_all.get(i).getScale()==t)
            {
                Time.add (data_FloodPrevent_all.get(i).getTime());
                inflow_lzz.add(data_FloodPrevent_all.get(i).getPreQ()) ;
            }

        }
        List<DataInflowPrevent> data_FloodPrevent_all2 = waterTransferReq.getData().get("tth");
        for (int i = 0; i < data_FloodPrevent_all2.size(); i++)
        {
            if (data_FloodPrevent_all2.get(i).getScale()==t)
            {
                inflow_tth.add(data_FloodPrevent_all2.get(i).getPreQ()) ;
            }
        }
        double[]inflowlzz=new double[inflow_lzz.size()];
        double[]inflowtth=new double[inflow_tth.size()];

        for (int i=0;i<inflow_lzz.size();i++){
            inflowlzz[i]=inflow_lzz.get(i);
            inflowtth[i]=inflow_tth.get(i);
        }
        data1.put("时间",Time);
        data1.put("楼庄子流量",inflowlzz);
        data1.put("头屯河流量",inflowtth);
        return data1;
    }
    public  static  int compareAB(double[] fitnessA, double[] fitnessB)
    {
        // //约束违反程度、供水缺额、下泄惩罚后供水缺额适应度
        //比较  3  和  4
        int flag = -2;
        if (fitnessA[2]<fitnessB[2])
        {
            flag = -1;
            return  flag;
        }
        else if (fitnessA[2] == fitnessB[2])
        {
            if (fitnessA[1] < fitnessB[1])
            {
                flag = -1;
                return  flag;
            }
            else if (fitnessA[1] == fitnessB[1])
            {
                flag = 0;
                return  flag;
            }
            else
            {
                flag = 1;
                return  flag;
            }
        }
        else
        {
            flag = 1;
            return  flag;
        }
    }

}


