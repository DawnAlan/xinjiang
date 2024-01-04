package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.TrainResult;

public interface NeuralNetwork {
	
	public String getName();
	
	
	public void train(double[][] input, double[][] realOutput, int trainNum);
	public void init(double[][] centers);
	
	public TrainResult simOutput(double[][] input, double[][] realOutput);
	
	public TrainResult simOutput1(double[][] input, double[][] realOutput,
                                  double[][][] layer_weight, int[] layernum);
	
	public double[][] getDNNLayer();
	
	public double[][][] getDNNWeight();

}
