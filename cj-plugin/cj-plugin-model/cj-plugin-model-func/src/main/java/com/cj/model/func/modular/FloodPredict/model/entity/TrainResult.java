package com.cj.model.func.modular.FloodPredict.model.entity;

import lombok.Data;

import java.util.List;

/**
 * 封装网络训练结果
 * @author leileilei
 *
 */
@Data
public class TrainResult {

	private double rmse;
	
	private double mre;
	
	private double dc;
	
	private double qr;
	
	private int trainDataNum;
	
	private int testDataNum;
	
	private double[][] simResult;
	
	private double[][] realResult;
	
	private ModelSaveEntity model2Save;
	
	private List<SimMaxMinEntity> maxAndMinSave;

	private List<TthResultEntity> result2Save;

	private List<TthModelEntity> result3Save;

	private List<TthParaEntity> result4Save;

	private String errorMessage;

}
