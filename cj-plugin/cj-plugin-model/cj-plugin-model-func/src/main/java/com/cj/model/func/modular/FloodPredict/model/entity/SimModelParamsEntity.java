package com.cj.model.func.modular.FloodPredict.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Describe SimModelParams的实体类
 * @author Li
 */
@Data  
@Entity
@Table(name="SIM_MODEL_PARAMS")

public class SimModelParamsEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
	@Id
	private String resName;
    @Id
	private String period;
    @Id
	private String modelName;
    @Id
	private String paramName;
    @Id
	private String paramDim1;
    @Id
	private String paramDim2;
    @Id
	private String paramDim3;
    @Id
	private String inputIndex;
    @Id
	private String ruleName;
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

	private Double value;


}
