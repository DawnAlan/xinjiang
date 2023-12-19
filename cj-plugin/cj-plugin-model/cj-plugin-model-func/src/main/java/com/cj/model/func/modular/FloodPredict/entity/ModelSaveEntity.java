package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;
import java.util.List;

@Data
public class ModelSaveEntity {
	private List<TthModelEntity> models;

	private List<TthParaEntity> params;

	private List<TthResultEntity> result;

	private List<SimMaxMinEntity> maxmin;

	private List<TemporaryXlsx> maxminxlsx;

	private List<TemporaryXlsx> paramxlsx;

}
