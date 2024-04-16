package com.cj.model.func.modular.FloodPredict.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data  
@Entity
@Table(name="TTH_PARA")
public class TthParaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String forecastDuanmian;
    @Id
	private String period;
    @Id
	private String modelName;//模型名
    @Id
	private String paramName;//参数名称
    @Id
	private String paramDim1;//参数第一维
    @Id
	private String paramDim2;//参数第二维
    @Id
	private String paramDim3;//参数第三维
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetStart;//训练期的开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date datasetEnd;//训练期的结束时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetStart;//检验期的开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date testDatasetEnd;//检验期的结束时间
    @Id
	private String userName;

	private Double value;//参数具体值

	private String inputIndex;//输入层的具体值

	private Double outputNum;//输出层的个数

}
