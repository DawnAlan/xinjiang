package com.cj.model.func.modular.FloodPrevent.function;


import com.alibaba.fastjson.JSONObject;
import com.cj.model.func.modular.FloodPrevent.entity.Option;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class GateDetail {
    public static Map<String,Map<String, Map<String, List<Object>>>> GateDetailCalculator(Map<String, List<Option>> Options){

        List<String> reservoirNames=getReservoirNames(Options);
        Set<String> keySet = Options.keySet();

        Map<String,Map<String, Map<String, List<Object>>>> result = new LinkedHashMap<>();
        for(String reservoirName:reservoirNames){
            Map<String, Map<String, List<Object>>> reservoirResult = new LinkedHashMap<>();
            if(reservoirName.equals("楼庄子")){
                Map<String, List<Object>> Time_lzz = new LinkedHashMap<>();
                Map<String, List<Object>> Q1_lzz = new LinkedHashMap<>();
                Map<String, List<Object>> Q2_lzz = new LinkedHashMap<>();
                Map<String, List<Object>> G1_lzz = new LinkedHashMap<>();
                Map<String, List<Object>> G2_lzz = new LinkedHashMap<>();
                for (String optionName:keySet){
                    List<Option> options = Options.get(optionName);
                    List<Object> time_lzz = new ArrayList<>();
                    List<Object> q1_lzz = new ArrayList<>();
                    List<Object> q2_lzz = new ArrayList<>();
                    List<Object> g1_lzz = new ArrayList<>();
                    List<Object> g2_lzz = new ArrayList<>();
                    for (Option option:options) {
                        if(option.getName().equals(reservoirName)){
                            double H = (option.getH1()+option.getH2())/2;
                            List<Double> singleList = JSONObject.parseArray(option.getQSingleString(), Double.class);
                            time_lzz.add(option.getTime());
                            q1_lzz.add(singleList.get(0));
                            q2_lzz.add(singleList.get(1));
                            g1_lzz.add(getGate(H,singleList)[0]);
                            g2_lzz.add(getGate(H,singleList)[1]);
                        }
                    }
                    Time_lzz.put(optionName,time_lzz);
                    Q1_lzz.put(optionName,q1_lzz);
                    Q2_lzz.put(optionName,q2_lzz);
                    G1_lzz.put(optionName,aver(g1_lzz,8));
                    G2_lzz.put(optionName,aver(g2_lzz,8));
                }
                reservoirResult.put("时间",Time_lzz);
                reservoirResult.put("溢洪洞",Q1_lzz);
                reservoirResult.put("泄洪冲沙导流洞",Q2_lzz);
                reservoirResult.put("溢洪洞开度",G1_lzz);
                reservoirResult.put("泄洪冲沙导流洞开度",G2_lzz);
            }
            if(reservoirName.equals("头屯河")){
                Map<String, List<Object>> Time_tth = new LinkedHashMap<>();
                Map<String, List<Object>> Q1_tth = new LinkedHashMap<>();
                Map<String, List<Object>> Q2_tth = new LinkedHashMap<>();
                Map<String, List<Object>> Q3_tth = new LinkedHashMap<>();
                Map<String, List<Object>> G1_tth = new LinkedHashMap<>();
                Map<String, List<Object>> G2_tth = new LinkedHashMap<>();
                Map<String, List<Object>> G3_tth = new LinkedHashMap<>();
                for (String optionName:keySet){
                    List<Option> options = Options.get(optionName);
                    List<Object> time_tth = new ArrayList<>();
                    List<Object> q1_tth = new ArrayList<>();
                    List<Object> q2_tth = new ArrayList<>();
                    List<Object> q3_tth = new ArrayList<>();
                    List<Object> g1_tth = new ArrayList<>();
                    List<Object> g2_tth = new ArrayList<>();
                    List<Object> g3_tth = new ArrayList<>();
                    for (Option option:options) {
                        if(option.getName().equals(reservoirName)){
                            double H = (option.getH1()+option.getH2())/2;
                            List<Double> singleList = JSONObject.parseArray(option.getQSingleString(), Double.class);
                            time_tth.add(option.getTime());
                            q1_tth.add(singleList.get(0));
                            q2_tth.add(singleList.get(1));
                            q3_tth.add(singleList.get(2));
                            g1_tth.add(getGate(H,singleList)[0]);
                            g2_tth.add(getGate(H,singleList)[1]);
                            g3_tth.add(getGate(H,singleList)[2]);
                        }
                    }
                    Time_tth.put(optionName,time_tth);
                    Q1_tth.put(optionName,q1_tth);
                    Q2_tth.put(optionName,q2_tth);
                    Q3_tth.put(optionName,q3_tth);
                    G1_tth.put(optionName,aver(g1_tth,8));
                    G2_tth.put(optionName,aver(g2_tth,8));
                    G3_tth.put(optionName,aver(g3_tth,8));
                }
                reservoirResult.put("时间",Time_tth);
                reservoirResult.put("放水涵洞",Q1_tth);
                reservoirResult.put("泄水隧洞",Q2_tth);
                reservoirResult.put("溢洪道",Q3_tth);
                reservoirResult.put("放水涵洞开度",G1_tth);
                reservoirResult.put("泄水隧洞开度",G2_tth);
                reservoirResult.put("溢洪道开度",G3_tth);
            }
            result.put(reservoirName,reservoirResult);
        }
        return result;
    }

    public static List<String> getReservoirNames(Map<String, List<Option>> options){
        List<String> result = new ArrayList<>();

        Set<String> optionNames = options.keySet();
        for (String optionName:optionNames){
            List<Option> option1 = options.get(optionName);
            for (Option option : option1) {
                String reservoirName = option.getName();
                if (!result.contains(reservoirName)) {
                    result.add(reservoirName);
                }
            }
        }

        return result;
    }

    public static double[] getGate(double H,List<Double> singleList){
        double[] result = new double[singleList.size()];
        if(singleList.size()==2){
            double[][] curve1 = new double[][]{
                    {	0	,	1	,	2	,	3	,	4	,	5	,	6	},
                    {	1383	,	0	,	0	,	0	,	0	,	0	,	0	},
                    {	1383.5	,	0	,	0	,	0	,	0	,	0	,	0	},
                    {	1384.5	,	2.61	,	2.61	,	2.61	,	2.61	,	2.61	,	2.61	},
                    {	1385.5	,	2.61	,	5.22	,	5.22	,	5.22	,	5.22	,	5.22	},
                    {	1386.5	,	2.61	,	5.22	,	7.83	,	7.83	,	7.83	,	7.83	},
                    {	1387.5	,	2.61	,	5.22	,	7.82	,	10.43	,	10.43	,	10.43	},
                    {	1388.5	,	2.61	,	5.22	,	7.82	,	10.43	,	13.04	,	13.04	},
                    {	1389.5	,	2.61	,	5.22	,	7.83	,	10.43	,	13.04	,	15.65	},
                    {	1390.5	,	3.04	,	6.09	,	9.13	,	12.17	,	15.22	,	18.26	},
                    {	1391.5	,	3.48	,	6.96	,	10.44	,	13.91	,	17.39	,	20.87	},
                    {	1392.5	,	3.91	,	7.83	,	11.74	,	15.65	,	19.57	,	23.48	},
                    {	1393	,	4.13	,	8.26	,	12.39	,	16.52	,	20.65	,	24.78	},
                    {	1394	,	4.57	,	9.13	,	13.7	,	18.26	,	22.83	,	27.39	},
                    {	1394.5	,	4.78	,	9.57	,	14.35	,	19.13	,	23.92	,	28.7	},
                    {	1395	,	5	,	10	,	15	,	20	,	25	,	30	},
                    {	1395.5	,	10.5	,	21	,	31.5	,	41.99	,	52.49	,	62.99	},
                    {	1396	,	16	,	31.99	,	47.99	,	63.98	,	79.98	,	95.97	},
                    {	1396.44	,	20.83	,	41.67	,	62.5	,	83.33	,	104.17	,	125	},
                    {	1397.39	,	20.83	,	41.67	,	62.5	,	83.33	,	104.17	,	125	},
                    {	1397.41	,	41.1	,	82.2	,	123.3	,	164.4	,	205.5	,	246.6	},
                    {	1397.5	,	41.67	,	83.33	,	125	,	166.67	,	208.33	,	250	},
                    {	1397.6	,	42.17	,	84.33	,	126.5	,	168.67	,	210.83	,	253	},
                    {	1397.63	,	42.67	,	85.33	,	128	,	170.67	,	213.33	,	256	},
                    {	1398	,	50	,	100	,	150	,	200	,	250	,	300	}
            };
            double[][] curve2 = new double[][]{
                    {	0	,	1	,	2	,	3	,	3.5	},
                    {	1353.3	,	51.62	,	98.23	,	143.36	,	158.5	},
                    {	1351.3	,	52.64	,	100.24	,	146.46	,	162.1	},
                    {	1349.3	,	55.44	,	105.78	,	155	,	172	},
                    {	1347.3	,	58.1	,	111.05	,	163.09	,	181.4	},
                    {	1345.3	,	60.65	,	116.07	,	170.79	,	190.3	},
                    {	1343.3	,	63.1	,	120.89	,	178.16	,	198.8	},
                    {	1341.3	,	65.45	,	125.52	,	185.24	,	207	},
                    {	1339.3	,	67.73	,	129.99	,	192.06	,	214.8	},
                    {	1337.3	,	69.92	,	134.31	,	198.64	,	222.4	},
                    {	1335.3	,	72.06	,	138.49	,	205.02	,	229.7	},
                    {	1333.3	,	74.13	,	142.56	,	211.2	,	236.8	},
                    {	1331.3	,	76.14	,	146.51	,	217.2	,	243.7	},
                    {	1329.3	,	78.1	,	150.35	,	223.05	,	250.4	},
                    {	1327.3	,	80.02	,	154.1	,	228.74	,	257	},
                    {	1325.3	,	81.89	,	157.76	,	234.29	,	263.3	},
                    {	1323.3	,	83.72	,	161.34	,	239.72	,	269.5	},
                    {	1384	,	85.51	,	164.84	,	245.03	,	275.6	},
                    {	1386	,	87.26	,	168.26	,	250.22	,	281.6	},
                    {	1388	,	88.97	,	171.62	,	255.31	,	287.4	},
                    {	1390	,	90.66	,	174.92	,	260.3	,	293.1	},
                    {	1392	,	92.31	,	178.15	,	265.2	,	298.7	},
                    {	1394.5	,	94.34	,	182.11	,	271.19	,	305.5	},
                    {	1396	,	95.54	,	184.45	,	274.73	,	309.6	},
                    {	1397.21	,	96.49	,	186.31	,	277.54	,	312.9	},
                    {	1397.42	,	96.66	,	186.63	,	278.03	,	313.4	},
                    {	1397.63	,	96.82	,	186.95	,	278.51	,	314	}
            };
            result[0]=getOpening(H,singleList.get(0),curve1);
            result[1]=getOpening(H,singleList.get(1),curve2);
        }
        else if(singleList.size()==3){
            double[][] curve1 = new double[][]{
                    {	0	,	1	,	2	,	3	,	4	},
                    {	950.14	,	5.8	,	5.8	,	5.8	,	5.8	},
                    {	951.14	,	8.5	,	17	,	17	,	17	},
                    {	952.14	,	10	,	20	,	30.8	,	30.8	},
                    {	960	,	28.075	,	56	,	84	,	112.3	},
                    {	962	,	30	,	60	,	90	,	120	},
                    {	965	,	30	,	60	,	90	,	120	},
                    {	978	,	30	,	60	,	90	,	120	},
                    {	985	,	30	,	60	,	90	,	120	},
                    {	989.6	,	30	,	60	,	90	,	120	},
                    {	990	,	30	,	60	,	90	,	120	},
                    {	991	,	30	,	60	,	90	,	120	},
                    {	992.5	,	30	,	60	,	90	,	120	},
                    {	993.5	,	30	,	60	,	90	,	120	},

            };
            double[][] curve2 = new double[][]{
                    {	0	,	1	,	2	},
                    {	950	,	0	,	0	},
                    {	952	,	0	,	0	},
                    {	960	,	0	,	0	},
                    {	961	,	0	,	0	},
                    {	962	,	4	,	4	},
                    {	963	,	6	,	8	},
                    {	964	,	8	,	12	},
                    {	965	,	12	,	16	},
                    {	978	,	21.4	,	42.8	},
                    {	985	,	23.85	,	47.7	},
                    {	989.6	,	25.3	,	50.6	},
                    {	990	,	25.45	,	50.9	},
                    {	991	,	25.75	,	51.5	},
                    {	992.5	,	26.2	,	52.4	},
                    {	993.5	,	26.5	,	53	}
            };
            result[0]=getOpening(H,singleList.get(0),curve1);
            result[1]=getOpening(H,singleList.get(1),curve2);
            result[2]=BigDecimal.valueOf(Math.max(0,H-989.6)).setScale(2, RoundingMode.UP).doubleValue();
        }
        return result;
    }

    public static double getOpening(double H,double Q,double[][] curve){
        double result = 0;

        int x = 1;
        double minH = curve[1][0];
        double maxH = curve[curve.length-1][0];
        double[] Qs =new double[curve[0].length];
        Qs[0]=H;
        if(H<=minH) {
            x=0;
            for (int i = 1; i < Qs.length; i++) {
                Qs[i]=curve[2][i]+(H-minH)*(curve[2][i]-curve[1][i])/(curve[2][0]-curve[1][0]);
            }
        }
        else if(H>=maxH){
            x=curve.length-1;
            for (int i = 1; i < Qs.length; i++) {
                Qs[i]=curve[x][i]+(H-maxH)*(curve[x][i]-curve[x-1][i])/(curve[x][0]-curve[x-1][0]);
            }
        }
        else{
            for (int j = 1; j < curve.length; j++) {
                if(H>curve[j][0]){
                    x=j;
                }
                else{
                    break;
                }
            }
            for (int i = 1; i < Qs.length; i++) {
                Qs[i]=curve[x][i]+(H-curve[x][0])*(curve[x+1][i]-curve[x][i])/(curve[x+1][0]-curve[x][0]);
            }

        }

        int y=0;
        double minQ=Qs[1];
        double maxQ=Qs[Qs.length-1];
        if(Q<=minQ){
            if(Qs[1]-0==0){
                result=0;
            }
            else{
                result=curve[0][1]+(Q-Qs[1])*(curve[0][1]-0)/(Qs[1]-0);
            }
        }
        else if(Q>=maxQ){
            y= Qs.length-1;
            result=curve[0][y];
        }
        else{
            for (int i = 1; i < Qs.length; i++) {
                if(Q>Qs[i]){
                    y=i;

                }
                else{
                    break;
                }
            }
            if(Qs[y+1]-Qs[y]==0){
                result=0;
            }
            else{
                result=curve[0][y]+(Q-Qs[y])*(curve[0][y+1]-curve[0][y])/(Qs[y+1]-Qs[y]);
            }
        }

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.UP).doubleValue();
    }

    public static List<Object> aver(List<Object> doubles,int n){
        int count = 0;
        double average = 0;
        for (int i = 0; i < doubles.size(); i++) {
            if(count<n){
                count++;
                average+=(double)doubles.get(i);
            }

            if(count==n){
                average=BigDecimal.valueOf(average/n).setScale(2, RoundingMode.UP).doubleValue();
                for (int j = 0; j < n; j++) {
                    doubles.set(i-j,average);
                }
                count=0;
                average=0;
            }
        }
        if(count!=0){
            double aver = 0;
            for (int i = 0; i < count; i++) {
                aver+=(double)doubles.get(doubles.size()-1-i);
            }
            aver=BigDecimal.valueOf(aver/n).setScale(2, RoundingMode.UP).doubleValue();
            for (int i = 0; i < count; i++) {
                doubles.set(doubles.size()-1-i,aver);
            }

        }

        return doubles;
    }

}
