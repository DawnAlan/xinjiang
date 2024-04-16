package com.cj.model.func.modular.FloodPredict.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Describe SimTrainResult的实体类
 * @author Li
 */
@Data  
@Entity
@Table(name="SIM_TRAIN_RESULT")

public class SimTrainResultEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
    @Id
	private String forecastDuanmian;
    @Id
	private String period;
    @Id
	private String modelName;
    @Id
	private String inputIndex;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Id
	private Date resultDate;
    
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
	private Double outputNum;
    @Id
	private Double outputIndex;

	private Double realOutput;
	
	private Double simOutput;


}
