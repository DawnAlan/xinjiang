package com.cj.model.func.modular.FloodPredict.utils;
import java.util.Arrays;

public class Tools {
    /**将日平均径流插值成每6小时时刻的径流
     * @param array	日平均序列
     * @return		输出为每天0，6，12，18，24时刻的值
     */
    public static double[] interpolation(double[] array){
        int arrayLength  = array.length;
        int resultLength = 4*arrayLength+1;
        double [] result = new double[resultLength];
        result[0] = array[0];
        result[1] = array[0];
        result[resultLength-1] = array[arrayLength-1];
        result[resultLength-2] = array[arrayLength-1];
        result[resultLength-3] = array[arrayLength-1];
        for(int i = 0;i < arrayLength-1;i++){
            result[4*i+2] = array[i];
            result[4*i+3] = (3*array[i]+array[i+1])/4;
            result[4*i+4] = (array[i]+array[i+1])/2;
            result[4*i+5] = (array[i]+3*array[i+1])/4;
        }
        return result;
    }


    /**时刻插值，但不做线性插值
     * @param array
     * @return
     */
    public static double[] interpolationEqu(double[] array){
        int arrayLength  = array.length;
        int resultLength = 4*arrayLength+1;
        double [] result = new double[resultLength];
        for(int i = 0;i < arrayLength;i++){
            result[4*i]   = array[i];
            result[4*i+1] = array[i];
            result[4*i+2] = array[i];
            result[4*i+3] = array[i];
        }
        result[resultLength-1] = array[arrayLength - 1];
        return result;
    }


    /**将日平均径流插值成6小时的平均径流
     * @param array		待插值序列
     * @return			插值后的序列
     */
    public static double[] interpolationAvg(double[] array){
        int arrayLength  = array.length;
        int resultLength = 4*arrayLength;
        double [] result = new double[resultLength];
        result[0] = array[0];
        result[1] = array[0];
        for(int i = 0;i < arrayLength-1;i++){
            result[4*i+2] = (7*array[i]+array[i+1])/8;
            result[4*i+3] = (5*array[i]+3*array[i+1])/8;
            result[4*i+4] = (3*array[i]+5*array[i+1])/8;
            result[4*i+5] = (array[i]+7*array[i+1])/8;
        }
        result[resultLength-2] = array[arrayLength-1];
        result[resultLength-1] = array[arrayLength - 1];
        return result;
    }





    /**数组转置
     * @param array  二维数组
     * @return		 转置的二维数组
     */
    public static double[][] transposition(double[][] array){
        int length1 = array.length;
        int length2 = array[0].length;
        double[][] result = new double[length2][length1];
        for(int i=0;i<length2;i++)
            for(int j=0;j<length1;j++)
                result[i][j] = array[j][i];
        return result;
    }

    /**数组转置
     * @param array		三维数组
     * @return			第一维不变，转置的三维数组
     */
    public static double[][][] transposition(double[][][] array){
        int length1 = array.length;
        int length2 = array[0].length;
        int length3 = array[0][0].length;
        double[][][] result = new double[length1][length3][length2];
        for(int k=0;k<length1;k++)
            for(int i=0;i<length3;i++)
                for(int j=0;j<length2;j++)
                    result[k][i][j] = array[k][j][i];
        return result;
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


    /**连接多个泛型数组
     * @param first 第一个
     * @param rest 	剩下的
     * @return
     */
    @SafeVarargs
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**在二维数组中挑选最大X个
     * @param array 原数组
     * @param x		最大X个
     * @return		最大X个的二维数组
     */
    public static double[][] selectX(double[][] array,int x){
        int length1 = array.length;
        double[][] result = new double[length1][];
        for(int i=0;i<length1;i++){
            double max = 0;
            int index = 0;
            int length2 = array[i].length;
            for(int j=0;j<length2-x;j++){
                double sum = 0;
                for(int k=0;k<x;k++)
                    sum = sum + array[i][j+k];
                if(sum > max){
                    max = sum;
                    index = j;
                }
            }
            result[i] = Arrays.copyOfRange(array[i], index, index+x);
        }

        return result;
    }
}
