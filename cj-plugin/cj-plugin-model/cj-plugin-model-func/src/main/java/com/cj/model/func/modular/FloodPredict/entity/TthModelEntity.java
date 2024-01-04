package com.cj.model.func.modular.FloodPredict.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data  
@Entity
@Table(name="TTH_LONGER_MODEL")
public class TthModelEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String forecastDuanmian;//断面
    @Id
	private String period;//预报时段
    @Id
	private String modelName;//模型名
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetStart;//训练期开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetEnd;//训练期结束时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetStart;//检验期开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetEnd;//检验期结束时间
    @Id
	private String userName;//用户名

	private String layerCount;//模型各层结点数

	private String inputIndex;//输入层的结点数

	private Double errorad;//错误大小

	private Double trainNum;//训练次数

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")

	private Date updatead;//当前时间

	private Double shiftError;//移位误差

	private String clusterad;//聚类方法

	private Double minRate;//最小学习率

	private Double width;

	private Double gamma;//SVM参数

	private Double c;//SVM参数

	private Double rmse;//均方根系数

	private Double mre;//平均相对误差

	private Double mobp;//SVM参数

	private Double maxRate;//最大学习率

	private Double outputNum;//输出层的结点数

	private Double dc;//一致性系数

	private Double qr;//合格率

	private Double qMax;//最大流量

	private Double qMin;//最小流量
}
