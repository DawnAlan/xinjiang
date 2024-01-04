package com.cj.model.func.modular.FloodPredict.entity;



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
	
	public static void paramSet(ParamsSetVO psvo) {
		
		if(psvo.getNetClass().equals("Elman神经网络")){
			if (psvo.getERROR() != 0) {
				Params.epsilon = psvo.getERROR();
			}

			if (psvo.getTrainNum() != 0) {
				Params.TRAINING_REPS = psvo.getTrainNum();
			}
			if (psvo.getMaxRate() != 0) {
				Params.LEARN_RATE = psvo.getMaxRate();
			}
		}
		
		if(psvo.getLayerCount() != null && !psvo.getLayerCount().equals("")){
			String[] layer = psvo.getLayerCount().split(",");
			int[] layers = new int[layer.length];
			for(int i = 0; i < layer.length; i++){
				layers[i] = Integer.parseInt(layer[i]);
			}
			Params.hiddenLayerNum = layers[1];
			Params.outputLayerNum = layers[layers.length-1];
			Params.dnnNet = layers;
			Params.elmanNet = layers;
		}
		
		if(psvo.getInputIndex() != null){
			Params.inputLayerNum = psvo.getInputIndex().length;
			Params.dnnNet[0] = psvo.getInputIndex().length;
		}

		if(psvo.getClusterMethod() != null && !psvo.getClusterMethod().equals("")){
			Params.cluster = psvo.getClusterMethod();
		}
		
		if (psvo.getERROR() != 0) {
			Params.ERROR = psvo.getERROR();
		}

		if (psvo.getTrainNum() != 0) {
			Params.trainNum = psvo.getTrainNum();
		}
		if (psvo.getWidth() != 0) {
			Params.width = psvo.getWidth();
		}
		if (psvo.getShiftError() != 0) {
			Params.shiftError = psvo.getShiftError();
		}
		if (psvo.getRate() != 0) {
			Params.rate = psvo.getRate();
		}
		if (psvo.getMobp() != 0) {
			Params.mobp = psvo.getMobp();
		}
		if (psvo.getMaxRate() != 0) {
			Params.maxRate = psvo.getMaxRate();
		}
		if (psvo.getMinRate() != 0) {
			Params.minRate = psvo.getMinRate();
		}
		if(psvo.getGamma() != 0){
			Params.gamma = psvo.getGamma();
		}
		if(psvo.getC() != 0){
			Params.c = psvo.getC();
		}
		
		if(psvo.getMaxGamma() != 0){
			Params.maxGamma = psvo.getMaxGamma();
		}
		if(psvo.getMinGamma() != 0){
			Params.minGamma = psvo.getMinGamma();
		}
		if(psvo.getMaxC() != 0){
			Params.maxC = psvo.getMaxC();
		}
		if(psvo.getMinC() != 0){
			Params.minC = psvo.getMinC();
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
