package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Describe HyfLongerMaxMin entity 
              
 * @author LiuLiBin
 * @email 970985518@qq.com
 * @date 2019-05-17 19:29:06
 */
@Data  
@Entity
@Table(name="TTH_LONGER_MAX_MIN")
public class TthMaxMinEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String resName;
    @Id
	private String period;
    @Id
	private Double dataIndex;
    @Id
	private String userName;

	private Double maxValue;

	private Double minValue;

	private String inputIndex;

}
