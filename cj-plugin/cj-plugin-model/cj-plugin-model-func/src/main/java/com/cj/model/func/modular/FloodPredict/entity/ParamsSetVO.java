package com.cj.model.func.modular.FloodPredict.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 输入参数，其中部分参数如果未设置，则为默认值；
 * @author li
 *
 */
@Data
public class ParamsSetVO {
	
	
	private String forecastDuanmian;//下游断面,必要
	
	private String netClass;//神经网络模式，必要
	
	private String clusterMethod;//聚类方式,在径向基网络下，必要
	
	private String forecastPeriod;//预报步长，必要
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date dataSetStartTime;//率定数据集开始时间，必要
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date dateSetEndTime;//率定数据集结束时间，必要
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date testSetStartTime;//测试集开始时间，必要
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date testSetEndTime;//测试集结束时间，必要

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date preStartTime;//预报开始时间，必要

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date preEndTime;//预报结束时间，必要
	
	private int[] inputIndex;//输入节点的序号,必要

	private double q_max;//流量阈值，最大值
	
	private double q_min;//流量阈值，最小值
	
	private String layerCount;//神经网络节点,必要
	
	private double ERROR;//训练误差阈值
	
	private int trainNum;//训练次数
	
	private double width;//mean-shift算法半径
	
	private double shiftError;//mean-shift算法偏差

	private double rate;//学习率
	
	private double mobp;//动量系数
	
	private double maxRate;//最大学习率
	
	private double minRate;//最小学习率
	
	private double gamma;//支持向量机参数1
	
	private double c;//支持向量机参数2
	
	public double maxGamma;//
	
	public double minGamma;
	
	public double maxC;
	
	public double minC;
	
	public int forward;//前沿时段

//	public int scalength;//影响因子的数量
	public int influence_factor;//影响因子的数量

	public int periodStepSize;//时段步长，中长期默认为1

	public int periodStepNumber;//时段数量

	public Boolean isSnowMeltModel;//是否为融雪模型

}
