package com.cj.model.func.modular.FloodPredict.entity;

import java.util.List;

/**
 * 聚类中心
 * @author leileilei
 *
 */
public class Center {
	
	double[] center;
	List<double[]> list;
	
	public Center(double[] center){
		this.center = center;
	}
	
	public double[] getCenter() {
		return center;
	}

	public void setCenter(double[] center) {
		this.center = center;
	}

	public List<double[]> getList() {
		return list;
	}

	public void setList(List<double[]> list) {
		this.list = list;
	}

}
