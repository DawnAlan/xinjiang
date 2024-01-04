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
@Table(name="TTH_RESULT")
public class TthResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String forecastDuanmian;
    @Id
	private String period;
    @Id
	private String modelName;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date resultDate;//当前预报的流量对应的时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetStart;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetEnd;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetStart;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetEnd;
    @Id
	private String userName;

	private double[] time;

	private double realOutput;//实测流量值

	private double simOutput;//预报流量值

	private Double outputNum;

	private Double outputIndex;

	private String inputIndex;//输入节点的序号,必要

	private Double rainfall;//温度值


}
