package com.cj.model.func.modular.FloodPredict.model;

import libsvm.svm_node;

import java.util.Vector;

public class transport {
	
	public static Vector<svm_node[]> transportx(double[][] data) {
//		读取n*m的数组，n行样本，m为输入的维数。
		Vector<svm_node[]> result = new Vector<svm_node[]>(); 
		int hang = data.length;
		int lie = data[0].length;
		for (int i = 0; i < hang; i++) {
			svm_node[] x = new svm_node[lie];
			for (int j = 0; j < lie; j++) {
				x[j] = new svm_node();
				x[j].index = j + 1;
				x[j].value = data[i][j];
			}
			result.addElement(x);
//			vy.addElement(datay[i][0]);
		}
		
		return result;
		
	}
	public static Vector<Double> transporty(double[][] datay) {
//		读取输出，只有一列。
		Vector<Double> resulty = new Vector<Double>();
		int hangy = datay.length;
//		int liey = datay[0].length;
		for (int i = 0; i < hangy; i++) {
			resulty.addElement(datay[i][0]);
		}
		return resulty;
		
	}
	

}
