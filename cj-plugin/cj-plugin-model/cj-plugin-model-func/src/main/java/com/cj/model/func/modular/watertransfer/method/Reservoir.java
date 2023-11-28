package com.cj.model.func.modular.watertransfer.method;

import lombok.Data;


import java.io.IOException;



@Data
public class Reservoir
{
    public String name;

    // **************************电站基本参数*********************************
    /** 校核洪水位 */
    public double levelFloodCheck;
    /** 设计洪水位 */
    public double levelFloodDesign;
    /** 防洪高水位 */
    public double levelFloodControl;
    /** 正常蓄水位 */
    public double levelNormal;
    /** 防洪限制水位 */
    public double levelFloodLimiting;
    /** 死水位 */
    public double levelDead;

    /** 总库容(亿立方) */
    public double storageTotal;
    /** 调洪库容 */
    public double storageControl;
    /** 防洪库容 */
    public double storageProtect;
    /** 调节库容 */
    public double storageRegulating;
    /** 死库容 */
    public double storageDead;

    /** 装机容量(万千瓦) */
    public double powerInstalled;
    /** 保证出力(万千瓦) */
    public double outputGuaranteed;

    /** （输入）最大水头 **/
    public double headMax;
    /** （输入）最小水头 **/
    public double headMin;
    /** （输入）最大下泄 **/
    public double outflowMax;
    /** （输入）最小下泄 **/
    public double outflowMin;
    /** （输入）到下游电站的水流时滞 **/
    public int flowDelay;
    /** （输入）日水位涨幅 **/
    public double levelDayChangeUp;
    /** （输入）日水位跌幅 **/
    public double levelDayChangeDown;
    /** （输入）小时水位涨幅 **/
    public double levelHourChangeUp;
    /** （输入）小时水位跌幅 **/
    public double levelHourChangeDown;
    /** （输入）日下游水位变幅 **/
    public double levelDownDayChange;
    /** （输入）小时下游水位变幅 **/
    public double levelDownHourChange;

    public int  id;
    //读取库容水位  关系
    public double[] wlc_wl;
    public double[] wlc_c;

    //读取下泄流量  下游水位  关系
    public double[] qdwl_q;
    public double[] qdwl_dwl;

    //读取水头  预想出力  关系
    public double[] hpo_h;
    public double[] hpo_po;

    //读取水位  下泄能力  关系
    public double[] wlob_wl;
    public double[] wlob_ob;

    public Reservoir()
    {

    }

}
