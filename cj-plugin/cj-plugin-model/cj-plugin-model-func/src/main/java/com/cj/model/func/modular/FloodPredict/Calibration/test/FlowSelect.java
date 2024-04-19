package com.cj.model.func.modular.FloodPredict.Calibration.test;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FlowSelect {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        Object[][] Flood = ExcelTool.readExcel("D:\\204\\2.头屯河\\径流预报数据文件\\月尺度来水过程.xlsx","Sheet1");
        Object[][] flood = FlowSelect.getFloodInformation(Flood);

        ExcelTool.writeObjectExcel("D:\\204\\2.头屯河\\径流预报数据文件\\月尺度来水过程.xlsx","洪水过程",flood);
    }

    /**
     * 筛选洪水过程
     * @param predict
     * @return
     */
    public static Object[][] getFloodInformation(Object[][] predict){
        Object[][] flood = new Object[predict.length][3];
        double max =0.0;
        double min =1000000.0;
        for (int i = 0; i < predict.length; i++) {
            if (max <= (double) predict[i][1]) {
                max = (double) predict[i][1];//洪峰
            }
            if (min >= (double) predict[i][1]) {
                min = (double) predict[i][1];//最小值
            }
        }
        double dt = max-min;//差值
        double line = min+dt*0.3;//洪水标准线
        for (int i = 0; i < predict.length; i++)//找到所有大于标准线的来水
        {
            if ((double) predict[i][1]>line){
                flood[i][0]=1;
                flood[i][1]=predict[i][0];//时间
                flood[i][2]=predict[i][1];//预报流量
            }else {
                flood[i][0]=0;
                flood[i][1]=predict[i][0];//时间
                flood[i][2]=predict[i][1];//预报流量
            }
        }
        int m = 0;//洪峰的数量
        List<Integer> loc = new ArrayList<>();//记录变化的位置
        for (int i = 0; i < predict.length-1; i++) {
            if (flood[i][0]!=flood[i+1][0]){
                m++;
            }
            if (flood[i][0]!=flood[i+1][0]){
                loc.add(i);
            }
        }
        int remainder = m % 2;
        m = m/2+remainder;//洪峰数量
        if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]==1)//开始是洪水并且结束是洪水
        {
            m=m+1;
        }
        for (int i = 0; i < predict.length; i++) {
            if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]!=1)//开始为洪水，结束不为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0)+1; k++) {
                    flood[k][0]=number;
                }//第一个洪峰赋值
                for (int j = 1; j < m; j++) {
                    number++;
                    for (int k = loc.get(2*j-1); k <= loc.get(2*j)+1; k++) {
                        flood[k][0]=number;
                    }
                }
                break;
            }
            if ((int)flood[0][0]!=1&&(int)flood[flood.length-1][0]==1)//开始不为洪水，结束为洪水
            {
                int number = 1;
                for (int j = 0; j < m-1; j++) {
                    for (int k = loc.get(2*j); k <= loc.get(2*j+1)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                for (int k = loc.get(2*m-2); k < flood.length; k++) {
                    flood[k][0]=number;
                }
                break;
            }
            if ((int)flood[0][0]==1&&(int)flood[flood.length-1][0]==1)//开始为洪水，结束为洪水
            {
                int number = 1;
                for (int k = 0; k <= loc.get(0)+1; k++) {
                    flood[k][0]=number;
                }
                number++;
                for (int j = 0; j < m-2; j++) {
                    for (int k = loc.get(2*j+1); k <= loc.get(2*j+2)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                for (int k = loc.get(2*m-3); k < flood.length; k++) {
                    flood[k][0]=number;
                }
                break;
            }
            if ((int)flood[0][0]!=1&&(int)flood[flood.length-1][0]!=1)//开始不为洪水，结束不为洪水
            {
                int number = 1;
                for (int j = 0; j < m; j++) {
                    for (int k = loc.get(2*j); k <= loc.get(2*j+1)+1; k++) {
                        flood[k][0]=number;
                    }
                    number++;
                }
                break;
            }
        }
        //根据洪峰信息再度判断

        return flood;
    }

}
