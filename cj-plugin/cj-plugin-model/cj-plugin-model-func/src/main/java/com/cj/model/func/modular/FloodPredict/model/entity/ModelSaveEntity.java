package com.cj.model.func.modular.FloodPredict.model.entity;

import com.cj.model.func.modular.FloodPredict.entity.TemporaryXlsx;
import lombok.Data;

import java.util.List;

@Data
public class ModelSaveEntity {
	private List<TthModelEntity> models;

	private List<TthParaEntity> params;

	private List<TthResultEntity> result;

	private List<SimMaxMinEntity> maxmin;

	private TemporaryXlsx tempXlsx;


}
