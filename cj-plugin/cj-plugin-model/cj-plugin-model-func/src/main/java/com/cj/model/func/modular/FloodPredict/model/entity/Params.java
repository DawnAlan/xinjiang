package com.cj.model.func.modular.FloodPredict.model.entity;


import com.cj.model.func.modular.FloodPredict.entity.ForecastInputParam;

/**
 * 模型参数默认值
 * @author leileilei
 *
 */
public class Params {
	
	public static String layerCount = "7,30,1";
	
	public static String cluster = "kmeans";

	public static double ERROR = 0.0001;
	
	public static int trainNum = 20000;
	
	public static int inputLayerNum = 7;
	
	public static int hiddenLayerNum = 30;
	
	public static int outputLayerNum = 1;
	
	public static double width = 0.08;
	
	public static double shiftError = 0.001;
	
	public static int[] dnnNet = {inputLayerNum, 30, outputLayerNum};
	
	public static int[] elmanNet = {inputLayerNum, 10,10, outputLayerNum};
	
	public static double rate = 0.1;
	
	public static double mobp = 0.8;
	
	public static double maxRate = 0.01;
	
	public static double minRate = 0.0001;
	
	public static double maxGamma = 32;
	
	public static double minGamma = Math.pow(2, -15);
	
	public static double maxC = Math.pow(2, 15);
	
	public static double minC = Math.pow(2, -5);
	
	public static double gamma;
	
	public static double c;
	
	public static int batch = 1024;
	
	public static double LEARN_RATE = 0.006;   
	
	public static int TRAINING_REPS = 7000;
	
	public static double epsilon = 0.001;
	
	public static void paramSet(ForecastInputParam forecastInputParam) {
		
		if(forecastInputParam.getNetClass().equals("Elman神经网络")){
			if (forecastInputParam.getERROR() != 0) {
				Params.epsilon = forecastInputParam.getERROR();
			}

			if (forecastInputParam.getTrainNum() != 0) {
				Params.TRAINING_REPS = forecastInputParam.getTrainNum();
			}
			if (forecastInputParam.getMaxRate() != 0) {
				Params.LEARN_RATE = forecastInputParam.getMaxRate();
			}
		}
		
		if(forecastInputParam.getLayerCount() != null && !forecastInputParam.getLayerCount().equals("")){
			String[] layer = forecastInputParam.getLayerCount().split(",");
			int[] layers = new int[layer.length];
			for(int i = 0; i < layer.length; i++){
				layers[i] = Integer.parseInt(layer[i]);
			}
			Params.hiddenLayerNum = layers[1];
			Params.outputLayerNum = layers[layers.length-1];
			Params.dnnNet = layers;
			Params.elmanNet = layers;
		}
		
		if(forecastInputParam.getInputIndex() != null){
			Params.inputLayerNum = forecastInputParam.getInputIndex().length;
			Params.dnnNet[0] = forecastInputParam.getInputIndex().length;
		}

		if(forecastInputParam.getClusterMethod() != null && !forecastInputParam.getClusterMethod().equals("")){
			Params.cluster = forecastInputParam.getClusterMethod();
		}
		
		if (forecastInputParam.getERROR() != 0) {
			Params.ERROR = forecastInputParam.getERROR();
		}

		if (forecastInputParam.getTrainNum() != 0) {
			Params.trainNum = forecastInputParam.getTrainNum();
		}
		if (forecastInputParam.getWidth() != 0) {
			Params.width = forecastInputParam.getWidth();
		}
		if (forecastInputParam.getShiftError() != 0) {
			Params.shiftError = forecastInputParam.getShiftError();
		}
		if (forecastInputParam.getRate() != 0) {
			Params.rate = forecastInputParam.getRate();
		}
		if (forecastInputParam.getMobp() != 0) {
			Params.mobp = forecastInputParam.getMobp();
		}
		if (forecastInputParam.getMaxRate() != 0) {
			Params.maxRate = forecastInputParam.getMaxRate();
		}
		if (forecastInputParam.getMinRate() != 0) {
			Params.minRate = forecastInputParam.getMinRate();
		}
		
		if(forecastInputParam.getMaxGamma() != 0){
			Params.maxGamma = forecastInputParam.getMaxGamma();
		}
		if(forecastInputParam.getMinGamma() != 0){
			Params.minGamma = forecastInputParam.getMinGamma();
		}
		if(forecastInputParam.getMaxC() != 0){
			Params.maxC = forecastInputParam.getMaxC();
		}
		if(forecastInputParam.getMinC() != 0){
			Params.minC = forecastInputParam.getMinC();
		}

	}
	
	public static void paramReset(){
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
