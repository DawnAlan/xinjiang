package com.cj.model.func.modular.watertransfer.method;

public class FindValue {

    /**
     *
     * @param V1 查询序列自变量   皆为升序
     * @param V2 查询序列因变量   皆为升序
     * @param Value1 查询值自变量
     * @return
     */
    public static double FindV2ByV1(double[] V1,double[] V2,double Value1)
    {
        int size = V1.length;
        double Value2 = -1;

        for (int i = 0; i < size; i++)
        {
            if (i == 0)
            {
                if (Value1 <= V1[i])
                {
                    Value2 = V2[i];
                    break;
                }
            }
            else if (i == size - 1)
            {
                if (Value1 > V1[i-1] && Value1 <= V1[i])
                {
                    Value2 = V2[i-1]
                            + (V2[i] - V2[i-1]) / (V1[i] - V1[i-1])
                            * (Value1 - V1[i-1]);
                }
                else if (Value1 > V1[i])
                {
                    Value2 = V2[i];
                }
            }
            else
            {
                if (Value1 > V1[i-1] && Value1 <= V1[i])
                {
                    Value2 = V2[i-1]
                            + (V2[i] - V2[i-1]) / (V1[i] - V1[i-1])
                            * (Value1 - V1[i-1]);
                    break;
                }
            }
        }

        return Value2;
    }
}
