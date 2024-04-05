package com.cj.model.func.modular.watertransfer.model;

import com.cj.common.exception.CommonException;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.watertransfer.entity.DataInflowPrevent;
import com.cj.model.func.modular.watertransfer.method.FindValue;
import com.cj.model.func.modular.watertransfer.method.InputWay;
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
    static double[] minOutflow;
    private Reservoir [] reservoirs;

    public ArrayList ResourceOptimizationshort_daysTest(WaterTransferReq waterTransferReq) throws Exception {
        //应用POA  进行水资源优化
        //设计调度时段  和   来水过程
        //配置水库


        setReservoir(waterTransferReq.getCurve(),reservoirs);
        minOutflow=new double[2];


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
        //xnum为本旬开始天数-1
        int xnum=0;
        if (date[2]>20)
            {
                xnum=date[2]-21;
            }
        else
            {
                xnum=((date[2]-1)%10);
            }

        int id= waterTransferReq.getId();
       int yearid=date[0];
        int yearN = yearid ;
        if((yearN%4 == 0 && yearN % 100!=0) || (yearN%400==0))
        {
            isLeapYear = true;
            monthday[1] = 29;
        }
        int  monthNum=(int)monthday[date[1]-1];
        if (waterTransferReq.getEcologyFlowLzz().length==12){
            minOutflow[0]= waterTransferReq.getEcologyFlowLzz()[date[1]-1];
            minOutflow[1]= waterTransferReq.getEcologyFlowTth()[date[1]-1];
        }

        daynum[2]=monthNum-20;
        // 第几旬
        int whichDecade=0;
        if (date[2]<=10)
        {
            whichDecade=0;
            period=10-date[2]+1;
        }

        if (date[2]>10&&date[2]<=20)
        {
            whichDecade=1;
            period=20-date[2]+1;
        }
        if (date[2]>20)
        {
            whichDecade=2;
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
        double[][]levelLimit=new double[2][waterTransferReq.getFloodWaterLevelLzz().length];
        double[][]levelMin=new double[2][waterTransferReq.getFloodWaterLevelLzz().length];
        levelLimit[0] =waterTransferReq.getFloodWaterLevelLzz();
        levelLimit[1] =waterTransferReq.getFloodWaterLevelTth();
        levelMin[0] =waterTransferReq.getMinWaterLevelLzz();
        levelMin[1] =waterTransferReq.getMinWaterLevelTth();

        String []reservoirsName=new String[]{"楼庄子水库","头屯河水库"};
        for (int t=0;t<reservoirs.length;t++){
            if (levelBegin[t]<levelMin[t][date[1]-1]){
                throw new CommonException("请检查"+reservoirsName[t]+"调度开始初水位设置是否合理，小于"+reservoirsName[t]+"调度最小水位：死水位");
            }
            if (levelBegin[t]>levelLimit[t][date[1]-1]){
                throw new CommonException("请检查"+reservoirsName[t]+"调度开始初水位设置是否合理，大于"+reservoirsName[t]+"调度最大水位：动态汛限水位");
            }
            if (levelEnd[t]<levelMin[t][date[1]-1]){
                throw new CommonException("请检查"+reservoirsName[t]+"调度结束末水位设置是否合理，小于"+reservoirsName[t]+"调度最小水位：死水位");
            }
            if (levelEnd[t]>levelLimit[t][date[1]-1]){
                throw new CommonException("请检查"+reservoirsName[t]+"调度结束末水位设置是否合理，大于"+reservoirsName[t]+"调度最大水位：动态汛限水位");
            }

        }

         id= waterTransferReq.getId();
        int calStep=(int)daynum[whichDecade];

        Map<String, Object> data1 = new HashMap<>();
        double[][] inflow=new double[2][period];

        data1=SetInflow(waterTransferReq, waterTransferReq.getData(),86400);
        inflow[0]=(double[])data1.get("楼庄子流量");
        inflow[1]=(double[])data1.get("头屯河流量");
        if (isAllElementsNonNegative(inflow)==false){
            throw new CommonException("请检查预报流量是否合理，存在小于0的流量");
        }
        Map<String, Object> dataDemand = new HashMap<>();
        Map<String, Object> dataMonth=new HashMap<>();
        Map<String, Object> dataYear=new HashMap<>();
        dataYear=InputWay.setwaterdemand(waterTransferReq,date[1]-1);
        dataMonth=InputWay.setwaterdemandTendays(waterTransferReq,whichDecade,dataYear,date[1]-1);
        dataDemand= InputWay.setWaterdemandDay(waterTransferReq,dataMonth,xnum,(int)daynum[whichDecade],whichDecade);

        String[]nameAgricultureEast=(String[]) dataDemand.get("河东灌溉站点名");
        String[] nameAgricultureWest=(String[]) dataDemand.get("河西灌溉站点名");
        String[]nameGreenEast=(String[]) dataDemand.get("河东绿化站点名");
        String[]nameGreenWest=(String[]) dataDemand.get("河西绿化站点名");
        String[]nameGreenQushou=(String[]) dataDemand.get("渠首绿化站点名");
        String[]nameIndustryQushou=(String[]) dataDemand.get("渠首工业站点名");
        String[]nameAgricultureQushou=(String[]) dataDemand.get("渠首农业站点名");

        double[][] waterDemand1= new double[5][calStep];

        double[][]waterdemand31=new double[1+nameAgricultureWest.length][calStep];
        double[][]waterdemand41=new double[2+nameAgricultureQushou.length+nameAgricultureEast.length][calStep];


        double [][]waterDemand=new double[5][period];
//        waterDemand=req.getWaterDemand_day();
//        double [][]inflow=new   double[2][period];
        double[][]waterdemand3=new double[1+nameAgricultureWest.length][period];
        double[][]waterdemand4=new double[2+nameAgricultureQushou.length+nameAgricultureEast.length][period];

        double [][]waterDemandIndustry1=new double[1+nameIndustryQushou.length][calStep];
        double [][]waterDemandGreenWest1=new double[nameGreenWest.length][calStep];
        double [][]waterDemandGreenEast1=new double[nameGreenEast.length][calStep];
        double [][]waterDemandGreenQushou1=new double[nameGreenQushou.length][calStep];
        //      八钢加渠首工业

        for (int x=0;x<waterDemandIndustry1.length;x++)
        {
            if (x==0)
            {
                waterDemandIndustry1[0]=(double[]) dataDemand.get("八钢");
            }
            else
            {
                waterDemandIndustry1[x]=(double[]) dataDemand.get(nameIndustryQushou[x-1]);
            }
        }

//      楼庄子水厂
        waterDemand1[0]=(double[]) dataDemand.get("楼庄子水厂");
//      红岩取水
        waterDemand1[1]= (double[]) dataDemand.get("红岩");
//      八钢加渠首工业
        for (int t=0;t<waterDemandIndustry1[0].length;t++){
            for (int x=0;x<waterDemandIndustry1.length;x++){
                waterDemand1[2][t]+=waterDemandIndustry1[x][t];
            }
        }

//       河西灌溉

//       河西绿化细分
        for (int x=0;x<nameGreenWest.length;x++)
        {
            waterDemandGreenWest1[x]= (double[]) dataDemand.get(nameGreenWest[x]);
        }
        for (int t=0;t<waterDemandGreenWest1[0].length;t++){
            for (int x=0;x<waterDemandGreenWest1.length;x++){
                waterdemand31[0][t]+=waterDemandGreenWest1[x][t];
            }
        }
        for (int x=0;x<waterdemand31.length;x++)
        {
            if (x==0)
            {

            }
            else
            {
                waterdemand31[x]= (double[]) dataDemand.get(nameAgricultureWest[x-1]);
            }
        }
//       河东灌溉
//       河东绿化细分
        for (int x=0;x<nameGreenEast.length;x++)
        {
            waterDemandGreenEast1[x]= (double[]) dataDemand.get(nameGreenEast[x]);
        }
        for (int t=0;t<waterDemandGreenEast1[0].length;t++){
            for (int x=0;x<waterDemandGreenEast1.length;x++){
                waterdemand41[0][t]+=waterDemandGreenEast1[x][t];
            }
        }
        //       渠首绿化细分
        for (int x=0;x<nameGreenQushou.length;x++)
        {
            waterDemandGreenQushou1[x]= (double[]) dataDemand.get(nameGreenQushou[x]);
        }
        for (int t=0;t<waterDemandGreenQushou1[0].length;t++)
        {
            for (int x=0;x<waterDemandGreenQushou1.length;x++){
                waterdemand41[1][t]+=waterDemandGreenQushou1[x][t];
            }
        }
        for (int x=0;x<waterdemand41.length;x++)
        {
            if (x<2)
            {

            }
            if (x>=2&&x<2+nameAgricultureQushou.length)
            {
                waterdemand41[x]= (double[]) dataDemand.get(nameAgricultureQushou[x-2]);
            }
            if (x>=2+nameAgricultureQushou.length){
                waterdemand41[x]=(double[]) dataDemand.get(nameAgricultureEast[x-2-nameAgricultureQushou.length]);
            }
        }

        for (int t=0;t<waterDemand1[0].length;t++){
            for (int x=0;x<waterdemand31.length;x++){
                waterDemand1[3][t]+=waterdemand31[x][t];
            }
        }
        for (int t=0;t<waterDemand1[0].length;t++){
            for (int x=0;x<waterdemand41.length;x++){
                waterDemand1[4][t]+=waterdemand41[x][t];
            }
        }
        checkWaterDemand(waterDemand1);
        checkWaterDemand(waterdemand31);
        checkWaterDemand(waterdemand41);
        checkWaterDemand(waterDemandIndustry1);
        checkWaterDemand(waterDemandGreenWest1);
        checkWaterDemand(waterDemandGreenEast1);
        checkWaterDemand(waterDemandGreenQushou1);

        double [][]waterDemandIndustry=new double[1+nameIndustryQushou.length][period];
        double [][]waterDemandGreenWest=new double[nameGreenWest.length][period];
        double [][]waterDemandGreenEast=new double[nameGreenEast.length][period];
        double [][]waterDemandGreenQushou=new double[nameGreenQushou.length][period];
        for (int x= xnum;x<calStep;x++){
            waterDemand[0][x-xnum]=waterDemand1[0][x];
            waterDemand[1][x-xnum]=waterDemand1[1][x];
            waterDemand[2][x-xnum]=waterDemand1[2][x];
            waterDemand[3][x-xnum]=waterDemand1[3][x];
            waterDemand[4][x-xnum]=waterDemand1[4][x];
        }

        for (int x1=0;x1<waterdemand3.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterdemand3[x1][x - xnum] = waterdemand31[x1][x];
            }
        }
        for (int x1=0;x1<waterdemand4.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterdemand4[x1][x - xnum] = waterdemand41[x1][x];
            }
        }
        for (int x1=0;x1<waterDemandIndustry.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterDemandIndustry[x1][x - xnum] = waterDemandIndustry1[x1][x];
            }
        }
        for (int x1=0;x1<waterDemandGreenQushou.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterDemandGreenQushou[x1][x - xnum] = waterDemandGreenQushou1[x1][x];
            }
        }
        for (int x1=0;x1<waterDemandGreenEast.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterDemandGreenEast[x1][x - xnum] = waterDemandGreenEast1[x1][x];
            }
        }
        for (int x1=0;x1<waterDemandGreenWest.length;x1++) {
            for (int x = xnum; x < calStep; x++) {
                waterDemandGreenWest[x1][x - xnum] = waterDemandGreenWest1[x1][x];
            }
        }
        if (inflow[0].length!=waterDemand[0].length){
            throw new CommonException("请检查来水预报时段与需水计划是否对应，两者时段不相符");
        }
        double inflowWater=0;
        for (int r = 0; r < 2; r++) {
            for (int t = 0; t < period; t++) {
                inflowWater+= inflow[r][t]  * delatT / 1e4;
            }
        }
        double storage=storageAndDischarge(levelBegin,levelEnd);
        if (storage>inflowWater){
            throw new CommonException("请降低水库蓄水目标，来水总水量小于蓄水目标");
        }

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
        double[] fit=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[0];

        //下边是优化算法得参数配置、优化目标配置，罚函数配置、约束处理配置（下边是优化问题）
        //计算约束违反程度  以及  目标适应度

        double[] fitness_term = fit;

        //全局最优解
        double[][] wl_best = new double[RNum][period+1];
        double[] fitness_best = new double[fit.length];
        fitness_best[2] = Double.MAX_VALUE;
        fitness_best[1] = Double.MAX_VALUE;

        double[][] wl_term_best = new double[RNum][period+1];
        //迭代过程最优解
        for (int x=0;x<RNum;x++){
            for (int ttt=0;ttt<1+period;ttt++){
                wl_term_best[x][ttt]=wl_term[x][ttt];
            }
        }

//        double[][] wl_term_best = wl_term.clone();

        double[] fitness_term_best = fitness_term.clone();
        double maxLevel = 0;
//                double maxLevel = reservoir.levelFloodCheck;
        double minLevel = 0;
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

                    for (int tt = 1; tt < period+1; tt++)
                    {
                        //初始  和  最终水位  不参与调整
                        if (tt==period){
                            maxLevel = levelLimit[n][date[1]-2];
                            minLevel = levelEnd [n];
                        }
                        else{
                            maxLevel = levelLimit[n][date[1]-1];
                            minLevel = levelMin[n][date[1]-1];
                        }
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

                            fitness_term = calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[0];

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

                    for (int tt = 1; tt < period+1; tt++)
                    {
                        //初始  和  最终水位  不参与调整
                        if (tt==period){
                            maxLevel = levelLimit[n][date[1]-2];
                            minLevel = levelEnd [n];
                        }
                        else{
                            maxLevel = levelLimit[n][date[1]-1];
                            minLevel = levelMin[n][date[1]-1];
                        }
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

                            fitness_term = calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[0];

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


        double[][]waterSupply3=new double[waterdemand3.length][period];
        double[][]waterSupply4=new double[waterdemand4.length][period];
        double[][] proportion = new double[5][period];
        fitness_term = calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[0];
        outflow_term[0]= calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[1];
        outflow_term[1]= calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[2];
        water_shortage[0]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[3];
        water_shortage[1]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[4];
        inflow_toutunhe=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[5];
        waterSupply[0]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[6];
        waterSupply[1]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[7];
        waterSupply[2]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[8];
        waterSupply[3]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[9];
        waterSupply[4]=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[10];
         watersupply_lzz=calFit(id, reservoirs,period,wl_term,waterDemand,inflow)[11];
        DecimalFormat da=new DecimalFormat("#.00");
//        if (fitness_term[0]>0.05){
//            throw new CommonException("请检查水位设置是否合理，请适当降低两水库末水位");
//        }

        double[]allwater=new double[period];
        double[][]ecologyWater=new double[2][period];
        double[][]ecologyFlow=new double[2][period];
        double[][]ecologyWaterNeed=new double[2][period];
        for (int i=0;i<ecologyWater.length;i++){
            for (int x=0;x<period;x++){
                double eco=Double.parseDouble(da.format(minOutflow[i]*delatT/1e4));
                ecologyWater[i][x]=eco;
                ecologyWaterNeed[i][x]=eco;
            }
        }
        for (int x=0;x<period;x++)
        {
            if (waterDemand[0][x]>waterSupply[0][x]&&ecologyWater[0][x]>0){

                double ecologyWater1=ecologyWater[0][x]-(waterDemand[0][x]-waterSupply[0][x]);
                if (ecologyWater1>=0){
                    waterSupply[0][x]=waterDemand[0][x];
                    ecologyWater[0][x]=ecologyWater1;
                }
                else if (ecologyWater1<0){
                    waterSupply[0][x]=ecologyWater[0][x]+waterSupply[0][x];
                    ecologyWater[0][x]=0;
                }
            }

            if (waterDemand[2][x]>waterSupply[2][x]&&ecologyWater[1][x]>0){

                double ecologyWater1=ecologyWater[1][x]-(waterDemand[2][x]-waterSupply[2][x]);
                if (ecologyWater1>=0){
                    waterSupply[2][x]=waterDemand[2][x];
                    ecologyWater[1][x]=ecologyWater1;
                }
                else if (ecologyWater1<0){
                    waterSupply[2][x]=ecologyWater[1][x]+waterSupply[2][x];
                    ecologyWater[1][x]=0;
                }
            }

            if (waterDemand[1][x]>waterSupply[1][x]&&ecologyWater[1][x]>0){

                double ecologyWater1=ecologyWater[1][x]-(waterDemand[1][x]-waterSupply[1][x]);
                if (ecologyWater1>=0){
                    waterSupply[1][x]=waterDemand[1][x];
                    ecologyWater[1][x]=ecologyWater1;
                }
                else if (ecologyWater1<0){
                    waterSupply[1][x]=ecologyWater[1][x]+waterSupply[1][x];
                    ecologyWater[1][x]=0;
                }
            }

        }

        for (int n1=0;n1<period;n1++){

            supply_water_two[0][n1]=Double.parseDouble(da.format(watersupply_lzz[n1]));
            supply_water_two[1][n1]=Double.parseDouble(da.format(waterSupply[1][n1]+waterSupply[2][n1]+waterSupply[3][n1]+waterSupply[4][n1]));
            waterdemand_all[n1]=waterDemand[0][n1]+waterDemand[1][n1]+waterDemand[2][n1]+waterDemand[3][n1]+waterDemand[4][n1];
            waterSupply_all[n1]=waterSupply[0][n1]+waterSupply[1][n1]+waterSupply[2][n1]+waterSupply[3][n1]+waterSupply[4][n1];
            ReservoirWaterdemand[0][n1]=waterDemand[0][n1];
            ReservoirWaterdemand[1][n1]= waterdemand_all[n1]-waterDemand[0][n1];
            ReservoirWatersupply[0][n1]=Double.parseDouble(da.format(watersupply_lzz[n1]+ecologyWater[0][n1]));
            ReservoirWatersupply[1][n1]=Double.parseDouble(da.format(waterSupply_all[n1]-waterSupply[0][n1]+ecologyWater[1][n1]));
        }




        DecimalFormat da1=new DecimalFormat("#.00");

        for (int m=0;m<waterdemand3.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemand[3][n]==0)
                {
                    waterSupply3[m][n]=0;
                }
                else {
                    if (waterSupply[3][n]>waterDemand[3][n]-waterdemand3[0][n]&&waterSupply[3][n]<waterDemand[3][n]){
                        double westGreen ;
                        if (waterdemand3[0][n]!=0){
                            westGreen=1-(waterDemand[3][n]-waterSupply[3][n]) / waterdemand3[0][n];
                        }
                        else {
                            westGreen=1;
                        }
                        if (m==0){
                            waterSupply3[m][n]=Double.parseDouble(da1.format((westGreen*waterdemand3[m][n])));
                        }
                        else{
                            waterSupply3[m][n]=Double.parseDouble(da1.format((waterdemand3[m][n])));
                        }
                    }
                    else if(waterSupply[3][n]<=waterDemand[3][n]-waterdemand3[0][n]){
                        double westIrr ;
                        if (waterDemand[3][n]-waterdemand3[0][n]!=0){
                            westIrr=waterSupply[3][n] / (waterDemand[3][n]-waterdemand3[0][n]);
                        }
                        else {
                            westIrr=1;
                        }
                        if (m==0){
                            waterSupply3[m][n]=0;
                        }
                        else{
                            waterSupply3[m][n]=Double.parseDouble(da1.format((westIrr*waterdemand3[m][n])));
                        }
                    }
                    else{
                        waterSupply3[m][n] = Double.parseDouble(da1.format((waterSupply[3][n] / waterDemand[3][n]) * waterdemand3[m][n]));
                    }
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
                    if (waterSupply[4][n]>(waterDemand[4][n]-waterdemand4[0][n]-waterdemand4[1][n])&&waterSupply[4][n]<waterDemand[4][n]){
                        double eastGreen ;
                        if (waterdemand4[0][n]+waterdemand4[1][n]!=0){
                            eastGreen=1-(waterDemand[4][n]-waterSupply[4][n]) / (waterdemand4[0][n]+waterdemand4[1][n]);
                        }
                        else {
                            eastGreen=1;
                        }
                        if (m==0||m==1){
                            waterSupply4[m][n]=Double.parseDouble(da1.format((eastGreen*waterdemand4[m][n])));
                        }
                        else{
                            waterSupply4[m][n]=Double.parseDouble(da1.format((waterdemand4[m][n])));
                        }
                    }
                    else if(waterSupply[4][n]<=(waterDemand[4][n]-waterdemand4[0][n]-waterdemand4[1][n])){
                        double eastIrr ;
                        if (waterDemand[4][n]-waterdemand4[0][n]-waterdemand4[1][n]!=0){
                            eastIrr=waterSupply[4][n] / (waterDemand[4][n]-waterdemand4[0][n]-waterdemand4[1][n]);
                        }
                        else {
                            eastIrr=1;
                        }
                        if (m==0||m==1){
                            waterSupply4[m][n]=0;
                        }
                        else{
                            waterSupply4[m][n]=Double.parseDouble(da1.format((eastIrr*waterdemand4[m][n])));
                        }
                    }
                    else {
                        waterSupply4[m][n] = Double.parseDouble(da1.format((waterSupply[4][n] / waterDemand[4][n]) * waterdemand4[m][n]));
                    }
                }
            }
        }

        for (int x=0;x<period;x++){
            double sup=waterSupply_all[x];
            double eco=ecologyWater[0][x];
            double  all_num=eco+sup;
            allwater[x]=all_num;

            proportion[0][x] = (eco) / (all_num);
            proportion[1][x] = (waterSupply[0][x] + waterSupply[1][x]) / (all_num);
            proportion[2][x] = (waterSupply[2][x]) / (all_num);
            proportion[4][x] =  (waterSupply3[0][x]+waterSupply4[0][x]+waterSupply4[1][x]) / (all_num);
            proportion[3][x] =  1-proportion[0][x]-proportion[1][x]-proportion[2][x]-proportion[4][x];

            proportion[0][x] = Double.parseDouble(da.format(proportion[0][x]));
            proportion[1][x] = Double.parseDouble(da.format(proportion[1][x]));
            proportion[2][x] = Double.parseDouble(da.format(proportion[2][x]));
            proportion[3][x] = Double.parseDouble(da.format(proportion[3][x]));
            proportion[4][x] = Double.parseDouble(da.format(proportion[4][x]));

        }
//        绿化配水
        double[][]waterSupplyGreenEast=new double[nameGreenEast.length][period];
        double[][]waterSupplyGreenWest=new double[nameGreenWest.length][period];
        double[][]waterSupplyGreenQushou=new double[nameGreenQushou.length][period];
//        工业配水
        double[][]waterSupplyIndustry=new double[1+nameIndustryQushou.length][period];

        for (int m=0;m<waterSupplyGreenEast.length;m++){
            for (int n=0;n<period;n++){
                if (waterdemand4[0][n]==0){
                    waterSupplyGreenEast[m][n]=0;
                }
                else {
                    waterSupplyGreenEast[m][n] = Double.parseDouble(da1.format((waterSupply4[0][n] / waterdemand4[0][n]) * waterDemandGreenEast[m][n]));
                }
            }
        }
        for (int m=0;m<waterSupplyGreenWest.length;m++){
            for (int n=0;n<period;n++){
                if (waterdemand3[0][n]==0){
                    waterSupplyGreenWest[m][n]=0;
                }
                else {
                    waterSupplyGreenWest[m][n] = Double.parseDouble(da1.format((waterSupply3[0][n] / waterdemand3[0][n]) * waterDemandGreenWest[m][n]));
                }
            }
        }
        for (int m=0;m<waterSupplyGreenQushou.length;m++){
            for (int n=0;n<period;n++) {
                if (waterdemand4[1][n] == 0) {
                    waterSupplyGreenQushou[m][n] = 0;
                }
                else{
                    waterSupplyGreenQushou[m][n] = Double.parseDouble(da1.format((waterSupply4[1][n] / waterdemand4[1][n]) * waterDemandGreenQushou[m][n]));
                }
            }
        }
//      工业配水
        for (int m=0;m<waterDemandIndustry.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemand[2][n]==0){
                    waterSupplyIndustry[m][n]=0;
                }
                else {
                    if (waterSupply[2][n]>waterDemandIndustry[0][n]){
                        double qushouIndustry ;
                        if (waterDemand[2][n]-waterDemandIndustry[0][n]!=0){
                            qushouIndustry=(waterSupply[2][n]-waterDemandIndustry[0][n]) / (waterDemand[2][n]-waterDemandIndustry[0][n]);
                        }
                        else {
                            qushouIndustry=1;
                        }
                        if (m==0){
                            waterSupplyIndustry[m][n]=Double.parseDouble(da1.format((waterDemandIndustry[0][n])));
                        }
                        else{
                            waterSupplyIndustry[m][n]= Double.parseDouble(da1.format(qushouIndustry * waterDemandIndustry[m][n]));
                        }
                    }
                    else{
                        if (m==0){
                            waterSupplyIndustry[0][n]=waterSupply[2][n];}
                        else {
                            waterSupplyIndustry[m][n]=0;
                        }
                    }
//                    waterSupplyIndustry[m][n] = Double.parseDouble(da1.format((waterSupply[2][n] / waterDemand[2][n]) * waterDemandIndustry[m][n]));
                }
            }
        }
//        绿化配水比例
        double[][]proportionGreenEast=new double[nameGreenEast.length][period];
        double[][]proportionGreenWest=new double[nameGreenWest.length][period];
        double[][]proportionGreenQushou=new double[nameGreenQushou.length][period];
//        工业配水比例
        double[][]proportionIndustry=new double[1+nameIndustryQushou.length][period];

        double[][] proportion3 = new double[waterdemand3.length][period];
        double[][] proportion4 = new double[waterdemand4.length][period];

        for (int m = 0; m < waterdemand3.length; m++) {
            for (int n = 0; n < period; n++) {
                if (waterdemand3[m][n] == 0) {
                    proportion3[m][n] = 1;
                } else {
                    proportion3[m][n] = Double.parseDouble(da1.format(waterSupply3[m][n] / waterdemand3[m][n]));
                    if (proportion3[m][n]>1){
                        proportion3[m][n] = 1;
                    }
                }
            }
        }
        for (int m = 0; m < waterdemand4.length; m++) {
            for (int n = 0; n < period; n++) {
                if (waterdemand4[m][n] == 0) {
                    proportion4[m][n] = 1;
                } else {
                    proportion4[m][n] = Double.parseDouble(da1.format(waterSupply4[m][n] / waterdemand4[m][n]));
                    if (proportion4[m][n]>1){
                        proportion4[m][n] = 1;
                    }
                }
            }
        }
        for (int m=0;m<proportionGreenEast.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemandGreenEast[m][n]==0)
                {
                    proportionGreenEast[m][n]=1;
                }
                else
                {
                    proportionGreenEast[m][n] = Double.parseDouble(da1.format(waterSupplyGreenEast[m][n]/ waterDemandGreenEast[m][n]));
                    if (proportionGreenEast[m][n]>1){
                        proportionGreenEast[m][n]=1;
                    }
                }
            }
        }
        for (int m=0;m<proportionGreenWest.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemandGreenWest[m][n]==0){
                    proportionGreenWest[m][n]=1;
                }
                else {
                    proportionGreenWest[m][n] = Double.parseDouble(da1.format(waterSupplyGreenWest[m][n]/ waterDemandGreenWest[m][n]));
                    if (proportionGreenWest[m][n]>1){
                        proportionGreenWest[m][n]=1;
                    }
                }
            }
        }

        for (int m=0;m<proportionGreenQushou.length;m++){
            for (int n=0;n<period;n++) {
                if (waterDemandGreenQushou[m][n] == 0) {
                    proportionGreenQushou[m][n] = 1;
                }
                else{
                    proportionGreenQushou[m][n] = Double.parseDouble(da1.format(waterSupplyGreenQushou[m][n] / waterDemandGreenQushou[m][n]));
                    if (proportionGreenQushou[m][n]>1){
                        proportionGreenQushou[m][n] = 1;
                    }
                }
            }
        }

        for (int m=0;m<proportionIndustry.length;m++){
            for (int n=0;n<period;n++){
                if (waterDemandIndustry[m][n]==0){
                    proportionIndustry[m][n]=1;
                }
                else {
                    proportionIndustry[m][n] = Double.parseDouble(da1.format(waterSupplyIndustry[m][n]/ waterDemandIndustry[m][n]));
                    if ( proportionIndustry[m][n]>1){
                        proportionIndustry[m][n]=1;
                    }
                }
            }
        }
        double[]capacity=new double[]{7374,2030};


        for (int r=0;r<RNum;r++)
        {
            for (int t = 0; t < period; t++) {
                time1[t]=t+1;
                ecologyFlow[r][t]=Double.parseDouble(da1.format( ecologyWater[r][t]*1e4/delatT));
                levelbegin[r][t]=Double.parseDouble(da1.format(wl_term[r][t]));
                levelend[r][t]=Double.parseDouble(da1.format(wl_term[r][t+1]));
                endCapacity[r][t]=Double.parseDouble(da1.format(FindValue.FindV2ByV1(reservoirs[r].wlc_wl, reservoirs[r].wlc_c, wl_term[r][t+1])));
                capacity_proportion[r][t]=Double.parseDouble(da1.format(endCapacity[r][t]/capacity[r]));
                if (r==0)
                {
                    inflow_water[r][t]=Double.parseDouble(da1.format(inflow[r][t]*delatT/1e4));
                    outflow_water[r][t]=Double.parseDouble(da1.format(outflow_term[r][t]*delatT/1e4));
                    deltawater[r][t]=Double.parseDouble(da1.format((inflow[r][t]-outflow_term[r][t])*delatT/1e4));
                    preSupplyWater[r][t]=Double.parseDouble(da1.format(inflow_water[r][t]+FindValue.FindV2ByV1(reservoirs[r].wlc_wl, reservoirs[r].wlc_c, wl_term[r][t])-
                            FindValue.FindV2ByV1(reservoirs[r].wlc_wl, reservoirs[r].wlc_c, 1353.3)));
                    inflowWater_supply[r][t]=inflow_water[r][t]-ReservoirWatersupply[r][t];

                }
                else {
                    inflow_water[r][t]=Double.parseDouble(da1.format(inflow[r][t]*delatT/1e4));
                    inflowwater_toutunhe[t]=Double.parseDouble(da1.format(inflow_toutunhe[t]*delatT/1e4));
                    outflow_water[r][t]=Double.parseDouble(da1.format(outflow_term[r][t]*delatT/1e4));
                    deltawater[r][t]=Double.parseDouble(da1.format((inflow_toutunhe[t]-outflow_term[r][t])*delatT/1e4));
                    preSupplyWater[r][t]=Double.parseDouble(da1.format(inflowwater_toutunhe[t]+FindValue.FindV2ByV1(reservoirs[r].wlc_wl, reservoirs[r].wlc_c, wl_term[r][t])-
                            FindValue.FindV2ByV1(reservoirs[r].wlc_wl, reservoirs[r].wlc_c, 973)));
                    inflowWater_supply[r][t]=inflowwater_toutunhe[t]-ReservoirWatersupply[r][t];

                }

            }

        }

        String[]nameEast=new String[2+nameAgricultureQushou.length+nameAgricultureEast.length];
        nameEast[0]="河东绿化总用水";
        nameEast[1]="渠首绿化总用水";
        for (int x=2;x<nameEast.length;x++){
            if (x<2+nameAgricultureQushou.length)
            {
                nameEast[x]=nameAgricultureQushou[x-2];
            }
            else
            {
                nameEast[x]=nameAgricultureEast[x-2-nameAgricultureQushou.length];
            }
        }
        String[]nameWest=new String[1+nameAgricultureWest.length];
        nameWest[0]="河西绿化总用水";
        for (int x=1;x<nameWest.length;x++){
            nameWest[x]=nameAgricultureWest[x-1];
        }
        ArrayList<WaterTransfer> result = new ArrayList<>();

        WaterTransfer waterTransfer = new WaterTransfer();
        waterTransfer.setTime((Date[])data1.get("时间"));
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

        waterTransfer.setPreSupplyWater(preSupplyWater);
        waterTransfer.setCapacity_proportion(capacity_proportion);
        waterTransfer.setReservoirWaterdemand(ReservoirWaterdemand);
        waterTransfer.setReservoirWatersupply(ReservoirWatersupply);
        waterTransfer.setInflowWater_supply(inflowWater_supply);
        waterTransfer.setSupply_water_two(supply_water_two);

        waterTransfer.setWaterSupplyGreenEast(waterSupplyGreenEast);
        waterTransfer.setWaterSupplyGreenWest(waterSupplyGreenWest);
        waterTransfer.setWaterSupplyGreenQushou(waterSupplyGreenQushou);
        waterTransfer.setWaterSupplyIndustry(waterSupplyIndustry);
        waterTransfer.setNameGreenQushou(nameGreenQushou);
        waterTransfer.setNameQushou(nameIndustryQushou);
        waterTransfer.setNameWest(nameWest);
        waterTransfer.setNameEast(nameEast);
        waterTransfer.setNameGreenWest(nameGreenWest);
        waterTransfer.setNameGreenEast(nameGreenEast);
        waterTransfer.setNameAgricultureEast(nameAgricultureEast);
        waterTransfer.setNameAgricultureQushou(nameAgricultureQushou);

        //各站点供水比例
        waterTransfer.setProportionGreenEast(proportionGreenEast);
        waterTransfer.setProportionGreenWest(proportionGreenWest);
        waterTransfer.setProportionGreenQushou(proportionGreenQushou);
        waterTransfer.setProportionIndustry(proportionIndustry);
        waterTransfer.setProportion3(proportion3);
        waterTransfer.setProportion4(proportion4);
        //各站点缺额
        waterTransfer.setWaterDemandGreenEast(waterDemandGreenEast);
        waterTransfer.setWaterDemandGreenWest(waterDemandGreenWest);
        waterTransfer.setWaterDemandGreenQushou(waterDemandGreenQushou);
        waterTransfer.setWaterDemandIndustry(waterDemandIndustry);
        waterTransfer.setWaterDemand3(waterdemand3);
        waterTransfer.setWaterDemand4(waterdemand4);

        waterTransfer.setAllWater(allwater);
        waterTransfer.setEcologyFlow(ecologyFlow);
        waterTransfer.setEcologyWater(ecologyWater);
        waterTransfer.setEcologyWaterNeed(ecologyWaterNeed);
        waterTransfer.setFitness(fitness_term);

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
            if (outflow_term[0][t] >=waterdemandQ[0][t]+minOutflow[0]) {
                termOutflow = deltaV * 1e4 / (delatT) + inflow[1][t] + outflow_term[0][t]-waterdemandQ[0][t];//水量平衡计算
                //上游实际来水
                inflow_toutunhe[t]= inflow[1][t] + outflow_term[0][t]-waterdemandQ[0][t];
            }
             if (outflow_term[0][t] >minOutflow[0]&&outflow_term[0][t] <minOutflow[0]+waterdemandQ[0][t]){
                termOutflow = deltaV * 1e4 / (delatT) + inflow[1][t] +minOutflow[0];
                //上游实际来水
                inflow_toutunhe[t]= inflow[1][t] + minOutflow[0];
            }
              if (outflow_term[0][t] >0&&outflow_term[0][t] <=minOutflow[0]){
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
                if (m==0){
                    if (outflow_term[m][t] > maxOutflow[m])//下泄流量约束处理,约束处理在罚函数里边体现
                    {
                        constraintViolation += Math.pow((outflow_term[m][t] - maxOutflow[m]), 2);
                    } else if (outflow_term[m][t] < minOutflow[m]+waterdemandQ[0][t]) {
                        constraintViolation += Math.pow((outflow_term[m][t] - minOutflow[m]-waterdemandQ[0][t]), 2);
                    }
                }
                if (m==1) {
                    if (outflow_term[m][t] > maxOutflow[m])//下泄流量约束处理,约束处理在罚函数里边体现
                    {
                        constraintViolation += Math.pow((outflow_term[m][t] - maxOutflow[m]), 2);
                    } else if (outflow_term[m][t] < minOutflow[m]+waterdemandQ[1][t]+waterdemandQ[2][t]) {
                        constraintViolation += Math.pow((outflow_term[m][t] - minOutflow[m]-waterdemandQ[1][t]-waterdemandQ[2][t]), 2);
                    }
                }
                //供水缺额计算  首先兼顾生态流量、城市用水、工业用水

                DecimalFormat da=new DecimalFormat("#.00");

                if (m == 0)
                {
                    if (outflow_term[m][t] > minOutflow[m]&&outflow_term[m][t] < minOutflow[m]+ waterdemandQ[0][t]) {
                        if (id==1||id==3)
                        {
                            fitness1 += (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        if (id==2)
                        {
                            fitness1 += (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4*
                                    (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        //水库1缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format( (minOutflow[m] + waterdemandQ[m][t]  - outflow_term[m][t]) * (delatT)/ 1e4));
                        waterSupply[m][t]=Double.parseDouble(da.format(waterdemandQ[0][t]*(delatT) / 1e4-water_shortage1[m][t]));
                         watersupply_lzz[t]=waterSupply[m][t];

                    }
                    if (outflow_term[m][t] <= minOutflow[m]) {
                        if (id==1||id==3)
                        {
                            fitness1 += (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        if (id==2)
                        {
                            fitness1 += (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4*
                                    (minOutflow[m] + waterdemandQ[0][t]  - outflow_term[m][t]) * (delatT) / 1e4;
                        }
                        //水库1缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format( ( waterdemandQ[m][t]  ) * (delatT)/ 1e4));
                        waterSupply[m][t]=0;
                        watersupply_lzz[t]=0;

                    }
                    if (outflow_term[m][t] >=minOutflow[m]+ waterdemandQ[0][t]) {

                        //水库1缺水额度
                        water_shortage1[m][t] =0 ;
                        waterSupply[m][t]=Double.parseDouble(da.format( ( waterdemandQ[m][t]  ) * (delatT)/ 1e4));
                        watersupply_lzz[t]=Double.parseDouble(da.format((outflow_term[m][t] -minOutflow[m])* (delatT)/ 1e4));

                    }


//                levelbegin[m][t] = wl_term[m][t];
//                levelend[m][t] = wl_term[m][t + 1];
                }

                else  if(m==1)
                {
                    if (outflow_term[m][t] > minOutflow[m]&&outflow_term[m][t] < minOutflow[m] + watershortage_allQ[t] )
                    {
                        if (id==1||id==3){
                            fitness1 += (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4;
                        }
                        if (id==2){
                            fitness1 += (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4*
                                    (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4 ;
                        }
                        //水库2缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format((minOutflow[m] +  watershortage_allQ[t]  - outflow_term[m][t]) * (delatT) / 1e4));
                        //水库2可供水量
                        water_Supply_tth[t]=(watershortage_allQ[t])*(delatT) / 1e4-water_shortage1[m][t];
                    }
                    if (outflow_term[m][t] <= minOutflow[m]){
                        if (id==1||id==3){
                            fitness1 += (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4;
                        }
                        if (id==2){
                            fitness1 += (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4*
                                    (minOutflow[m] + watershortage_allQ[t]  - outflow_term[m][t]) * (delatT)/ 1e4 ;
                        }
                        //水库2缺水额度
                        water_shortage1[m][t] =Double.parseDouble(da.format((  watershortage_allQ[t]  ) * (delatT) / 1e4));
                        //水库2可供水量
                        water_Supply_tth[t]=0;
                    }
                    if (outflow_term[m][t] >= minOutflow[m] + watershortage_allQ[t] ){
                        fitness2+=Double.parseDouble(da.format((outflow_term[m][t]-(minOutflow[m] + watershortage_allQ[t]))* delatT  / 1e4));

                        water_Supply_tth[t]=waterDemand[1][t]+waterDemand[2][t]+waterDemand[3][t]+waterDemand[4][t];
                        water_shortage1[m][t]=0;
                    }

                }

            }

        }

        //供水量

//        if (id==1||id==3){
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
                    if (waterDemand[3][t] + waterDemand[4][t]==0){
                        waterSupply[3][t] = 0;
                        waterSupply[4][t] = 0;
                    }

                    if (waterDemand[3][t] >= waterDemand[4][t]){
                        if (0.5 * (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]))>=waterDemand[4][t])
                        {
                            waterSupply[3][t] = (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]+waterDemand[4][t]));
                            waterSupply[4][t] = waterDemand[4][t];
                        }
                        else {
                            waterSupply[3][t] = 0.5*(water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]));
                            waterSupply[4][t] = 0.5*(water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]));
                        }
                    }
                    if (waterDemand[3][t] < waterDemand[4][t]){
                        if (0.5 * (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]))>=waterDemand[3][t])
                        {
                            waterSupply[3][t] = waterDemand[3][t] ;
                            waterSupply[4][t] = (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]+waterDemand[3][t]));
                        }
                        else {
                            waterSupply[3][t] = 0.5*(water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]));
                            waterSupply[4][t] = 0.5*(water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t]));
                        }
                    }
//                    else if (waterDemand[3][t] + waterDemand[4][t]!=0)
//                    {
//                        waterSupply[3][t] = waterDemand[3][t] * (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t])) / (waterDemand[3][t] + waterDemand[4][t]);
//                        waterSupply[4][t] = waterDemand[4][t] * (water_Supply_tth[t] - (waterDemand[1][t] + waterDemand[2][t])) / (waterDemand[3][t] + waterDemand[4][t]);
//                    }
                    if (waterSupply[3][t]<=0){
                        waterSupply[3][t]=0;
                    }
                    if (waterSupply[4][t]<=0){
                        waterSupply[4][t]=0;
                    }
                }
                if (water_Supply_tth[t]>=waterDemand[2][t]&&water_Supply_tth[t]<waterDemand[1][t]+waterDemand[2][t]){
                    waterSupply[1][t]= water_Supply_tth[t]-waterDemand[2][t];
                    waterSupply[2][t]= waterDemand[2][t];
                    waterSupply[3][t]= 0;
                    waterSupply[4][t]= 0;
                }
                if (water_Supply_tth[t]>=0&&water_Supply_tth[t]<waterDemand[2][t]){
                    waterSupply[1][t]= 0;
                    waterSupply[2][t]= water_Supply_tth[t];
                    waterSupply[3][t]= 0;
                    waterSupply[4][t]= 0;
                }
            }



        fitness1_penalty = fitness1 + penaltyFactor * constraintViolation;//
        fitness2_penalty = fitness2 - penaltyFactor * constraintViolation;//
        double[] fitness = new double[]{constraintViolation, fitness1, fitness1_penalty,fitness2,wl_term[0][period],wl_term[1][period]};
        //适应度，1水库下泄流量，2水库下泄，1水库缺额，2水库缺额,2水库实际来水，0楼庄子供水，1红岩城市用水，2八钢工业，3西干，4东干
        result=new double[][]{fitness,outflow_term[0],outflow_term[1],water_shortage[0],water_shortage[1],inflow_toutunhe
                ,waterSupply[0],waterSupply[1],waterSupply[2],waterSupply[3],waterSupply[4],watersupply_lzz};
        return result;

    }
    public  double storageAndDischarge(double[]levelBegin,double[]levelEnd) {
        DecimalFormat da1 = new DecimalFormat("#.00");
        double dischargeLzz = Double.parseDouble(da1.format(FindValue.FindV2ByV1(reservoirs[0].wlc_wl, reservoirs[0].wlc_c, levelEnd[0])-
                FindValue.FindV2ByV1(reservoirs[0].wlc_wl, reservoirs[0].wlc_c, levelBegin[0])));
        double dischargeTth = Double.parseDouble(da1.format(FindValue.FindV2ByV1(reservoirs[1].wlc_wl, reservoirs[1].wlc_c, levelEnd[1])-
                FindValue.FindV2ByV1(reservoirs[1].wlc_wl, reservoirs[1].wlc_c, levelBegin[1])));
        double[]storage=new double[3];
        storage[0]=dischargeLzz;
        storage[1]=dischargeTth;
        storage[2]=dischargeLzz+dischargeTth;
        return storage[2];
    }
    /**
     * 检查需水数据是否都大于等于0；
     * @param array
     */
    public static void checkWaterDemand(double[][] array){
        if (isAllElementsNonNegative(array)==false){
            throw new CommonException("请检查需水数据是否合理，存在小于0的需水数据");
        }
    }
    public static void checkWaterDemand(double[] array){
        if (isAllElementsNonNegative(array)==false){
            throw new CommonException("请检查需水数据是否合理，存在小于0的需水数据");
        }
    }
    /**
     * 判断数据是否都大于等于0，若是为true，否者为false
     * @param array
     * @return
     */
    public static boolean isAllElementsNonNegative(double[] array) {
        for (double element : array) {
            if (element < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements in the given two-dimensional array are greater than or equal to 0.
     *
     * @param array the two-dimensional array to check
     * @return true if all elements are greater than or equal to 0, false otherwise
     */
    public static boolean isAllElementsNonNegative(double[][] array) {
        for (double[] row : array) {
            if (!isAllElementsNonNegative(row)) {
                return false;
            }
        }
        return true;
    }
    public void setReservoir(List<CurveParam> data, Reservoir[] reservoir ){
        this.reservoirs = new Reservoir[2];
        this.reservoirs[0] = new Reservoir();
        this.reservoirs[1] = new Reservoir();
        List<Double> capacity = new ArrayList<>();
        List<Double> level = new ArrayList<>();
        List<Double> outflow = new ArrayList<>();
        List<Double> outflow_level = new ArrayList<>();



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

        this.reservoirs[0].wlc_c=wlc_c;
        this.reservoirs[0].wlc_wl=wlc_wl;
        this.reservoirs[0].wlob_wl=wlob_wl;
        this.reservoirs[0].wlob_ob=wlob_ob;


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
        this.reservoirs[1].wlc_c=wlc_c1;
        this.reservoirs[1].wlc_wl=wlc_wl1;
        this.reservoirs[1].wlob_wl=wlob_wl1;
        this.reservoirs[1].wlob_ob=wlob_ob1;

        this.reservoirs[0].name = "楼庄子";
        this.reservoirs[0].levelFloodControl = 1394.5;
        this.reservoirs[0].levelNormal = 1394.5;
        this.reservoirs[0].levelFloodLimiting = 105;
        this.reservoirs[0].levelDead = 1353.3;
        this.reservoirs[0].outflowMin = 1.48;
        this.reservoirs[0].levelFloodDesign = 1397.41;
        this.reservoirs[0].levelFloodCheck = 1397.63;

        this.reservoirs[1].name = "头屯河";
        this.reservoirs[1].levelFloodControl = 987;
        this.reservoirs[1].levelNormal = 989.6;
        this.reservoirs[1].levelFloodLimiting = 105;
        this.reservoirs[1].levelDead = 975;
        this.reservoirs[1].outflowMin = 1.48;
        this.reservoirs[1].levelFloodDesign = 991.2;
        this.reservoirs[1].levelFloodCheck = 992.54;
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
        Date[] time = new Date[inflow_tth.size()];

        for (int i=0;i<inflow_lzz.size();i++){
            inflowlzz[i]=inflow_lzz.get(i);
            inflowtth[i]=inflow_tth.get(i);
            time[i] = Time.get(i);
        }
        data1.put("时间",time);
        data1.put("楼庄子流量",inflowlzz);
        data1.put("头屯河流量",inflowtth);
        return data1;
    }
    public  static  int compareAB(double[] fitnessA, double[] fitnessB)
    {
        // //约束违反程度、供水缺额、弃水量、楼庄子蓄水、头屯河蓄水
        //比较2，1， 3  和  4，5
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
                if (fitnessA[3] < fitnessB[3])
                {
                    flag = -1;
                    return  flag;
                }
                else if (fitnessA[3] == fitnessB[3])
                {
                    if (fitnessA[4] > fitnessB[4])
                    {
                        flag = -1;
                        return  flag;
                    }
                    else if (fitnessA[4] == fitnessB[4])
                    {
                        if (fitnessA[5] > fitnessB[5])
                        {
                            flag = -1;
                            return  flag;
                        }
                        else if (fitnessA[5] == fitnessB[5])
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
        else
        {
            flag = 1;
            return  flag;
        }
    }

}


