package com.cj.model.func.modular.FloodPredict.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Describe SimMaxMin的实体类
 * @author Li
 */
@Data  
@Entity
@Table(name="SIM_MAX_MIN")

public class SimMaxMinEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@Id
	private String forecastDuanmian;

	/**
	 * $column.comments
	 */
	@Id
	private String period;
	/**
	 * $column.comments
	 */

	/**
	 * $column.comments
	 */
	@Id
	private Double dataIndex;
	
	@Id
	private String inputIndex;
	/**
	 * $column.comments
	 */
	private Double maxValue;
	/**
	 * 输入输出值的最值
	 */
	private Double minValue;


}
