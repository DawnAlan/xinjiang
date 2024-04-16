package com.cj.model.func.modular.FloodPredict.Calibration.pso;

import java.util.stream.DoubleStream;

import static com.google.common.base.Preconditions.*;

/**
 * 参数验证工具类
 */
public class  ParameterValidation {

    /**
     * 计算模型预测结果的纳什效率系数
     *
     * @param obs 实际观测值序列
     * @param pre 模型预测值序列
     * @return 纳什效率系数
     */
    public static double NashSutcliffeEfficiency(double[] obs, double[] pre) {
        int n = CheckIdenticalLength(obs, pre);
        double avg = DoubleStream.of(obs).average().getAsDouble();
        double s1 = 0, s2 = 0;
        for (int i = 0; i < n; ++i) {
            s1 += Math.pow(obs[i] - pre[i], 2);
            s2 += Math.pow(obs[i] - avg, 2);
        }
        return 1 - s1 / s2;
    }
    /**
     * 确保所有的待检验的数组长度一致
     *
     * @param arrays 待检验的数组
     * @return 如果所有数组长度一致则返回数组的长度
     */
    public static int CheckIdenticalLength(double[]... arrays) {
        checkNotNull(arrays);
        checkElementIndex(0, arrays.length);
        boolean identical = true;
        for (int i = 1; i < arrays.length; ++i)
            if (arrays[i].length != arrays[0].length)
                identical = false;
        checkArgument(identical, "序列长度不一致");
        return arrays[0].length;
    }
}
