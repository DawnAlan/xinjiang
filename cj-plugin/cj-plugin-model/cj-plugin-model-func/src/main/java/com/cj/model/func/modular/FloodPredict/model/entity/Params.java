package com.cj.model.func.modular.FloodPredict.model.entity;


import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;

/**
 * 模型参数默认值
 * @author leileilei
 *
 */
public class Params {
	
	public String layerCount = "7,30,1";
	
	public String cluster = "kmeans";

	public double ERROR = 0.0001;
	
	public int trainNum = 20000;
	
	public int inputLayerNum = 7;
	
	public int hiddenLayerNum = 30;
	
	public int outputLayerNum = 1;
	
	public double width = 0.08;
	
	public double shiftError = 0.001;
	
	public int[] dnnNet = {inputLayerNum, 30, outputLayerNum};
	
	public int[] elmanNet = {inputLayerNum, 10,10, outputLayerNum};
	
	public double rate = 0.1;
	
	public double mobp = 0.8;
	
	public double maxRate = 0.01;
	
	public double minRate = 0.0001;
	
	public double maxGamma = 32;
	
	public double minGamma = Math.pow(2, -15);
	
	public double maxC = Math.pow(2, 15);
	
	public double minC = Math.pow(2, -5);
	
	public double gamma;
	
	public double c;
	
	public int batch = 1024;
	
	public double LEARN_RATE = 0.006;
	
	public int TRAINING_REPS = 7000;
	
	public double epsilon = 0.001;
	
	public void paramSet(ForecastInputParam forecastInputParam) {

		Params params = new Params();
		
		if(forecastInputParam.getNetClass().equals("Elman神经网络")){
			if (forecastInputParam.getERROR() != 0) {
				params.epsilon = forecastInputParam.getERROR();
			}

			if (forecastInputParam.getTrainNum() != 0) {
				params.TRAINING_REPS = forecastInputParam.getTrainNum();
			}
			if (forecastInputParam.getMaxRate() != 0) {
				params.LEARN_RATE = forecastInputParam.getMaxRate();
			}
		}
		
		if(forecastInputParam.getLayerCount() != null && !forecastInputParam.getLayerCount().equals("")){
			String[] layer = forecastInputParam.getLayerCount().split(",");
			int[] layers = new int[layer.length];
			for(int i = 0; i < layer.length; i++){
				layers[i] = Integer.parseInt(layer[i]);
			}
			params.hiddenLayerNum = layers[1];
			params.outputLayerNum = layers[layers.length-1];
			params.dnnNet = layers;
			params.elmanNet = layers;
		}
		
		if(forecastInputParam.getInputIndex() != null){
			params.inputLayerNum = forecastInputParam.getInputIndex().length;
			params.dnnNet[0] = forecastInputParam.getInputIndex().length;
		}

		if(forecastInputParam.getClusterMethod() != null && !forecastInputParam.getClusterMethod().equals("")){
			params.cluster = forecastInputParam.getClusterMethod();
		}
		
		if (forecastInputParam.getERROR() != 0) {
			params.ERROR = forecastInputParam.getERROR();
		}

		if (forecastInputParam.getTrainNum() != 0) {
			params.trainNum = forecastInputParam.getTrainNum();
		}
		if (forecastInputParam.getWidth() != 0) {
			params.width = forecastInputParam.getWidth();
		}
		if (forecastInputParam.getShiftError() != 0) {
			params.shiftError = forecastInputParam.getShiftError();
		}
		if (forecastInputParam.getRate() != 0) {
			params.rate = forecastInputParam.getRate();
		}
		if (forecastInputParam.getMobp() != 0) {
			params.mobp = forecastInputParam.getMobp();
		}
		if (forecastInputParam.getMaxRate() != 0) {
			params.maxRate = forecastInputParam.getMaxRate();
		}
		if (forecastInputParam.getMinRate() != 0) {
			params.minRate = forecastInputParam.getMinRate();
		}
		
		if(forecastInputParam.getMaxGamma() != 0){
			params.maxGamma = forecastInputParam.getMaxGamma();
		}
		if(forecastInputParam.getMinGamma() != 0){
			params.minGamma = forecastInputParam.getMinGamma();
		}
		if(forecastInputParam.getMaxC() != 0){
			params.maxC = forecastInputParam.getMaxC();
		}
		if(forecastInputParam.getMinC() != 0){
			params.minC = forecastInputParam.getMinC();
		}

	}
	
	public void paramReset(){
		ERROR = 0.00001;
		
		trainNum = 10000;
		
		inputLayerNum = 5;
		
		hiddenLayerNum = 30;
		
		outputLayerNum = 2;
		
		width = 0.08;
		
		shiftError = 0.001;
		
		int[] layer = {inputLayerNum, 30, outputLayerNum};
		
		dnnNet = layer;
		
		rate = 0.1;
		
		mobp = 0.8;
		
		maxRate = 0.01;
		
		minRate = 0.0001;
		
		LEARN_RATE = 0.006;   
		
		TRAINING_REPS = 7000;
		
		epsilon = 0.001;
		
		maxGamma = 32;
		
		minGamma = Math.pow(2, -15);
		
		maxC = Math.pow(2, 15);
		
		minC = Math.pow(2, -5);
	}
}
