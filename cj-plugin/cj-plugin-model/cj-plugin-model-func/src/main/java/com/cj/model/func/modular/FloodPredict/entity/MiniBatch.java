package com.cj.model.func.modular.FloodPredict.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MiniBatch {
	private int num;
	private List<double[][]> input = new ArrayList();
	private List<double[][]> output = new ArrayList();

}
