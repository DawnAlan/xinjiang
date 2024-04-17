package com.cj.model.func.modular.FloodPredict.model.network;


import com.cj.model.func.modular.FloodPredict.model.entity.MiniBatch;
import com.cj.model.func.modular.FloodPredict.model.entity.Params;
import com.cj.model.func.modular.FloodPredict.model.entity.TrainResult;
import com.cj.model.func.modular.FloodPredict.utils.MathUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data

public class DNN_ADAM implements NeuralNetwork {

	private double[][] layer;// 神经网络各层节点
	private double[][][] layer_Batch;// 每批的节点值
	private double[][] layerErr;// 神经网络各节点误差
	private double[][][] layerErr_Batch;// 每批的各节点误差
	private double[][][] layer_weight;// 各层节点权重
	private double[][] layer_input;
	private double[][][] layer_input_Batch;
	private double[][][] layer_weight_delta;// 各层节点权重动量
	private double mobp;// 动量系数
	private double rate;// 学习系数
	private double max_rate;
	private double min_rate;
	private int batchNum;
	private double rou1 = 0.9;
	private double rou2 = 0.999;
	private double e = 0.001;
	private double delta = Math.pow(10, -8);
	private double[][][] s;
	private double[][][] r;

	Params params =new Params();

	@Override
	public String getName() {
		// TODO 自动生成的方法存根
		return "深度神经网络";
	}

	public DNN_ADAM(int[] layernum, double rate, double mobp, double max_rate, double min_rate, int batchNum) {
		this.mobp = mobp;
		this.rate = rate;
		this.max_rate = max_rate;
		this.min_rate = min_rate;
		layer = new double[layernum.length][];
		this.batchNum = batchNum;

		layer_Batch = new double[batchNum][layernum.length][];
		layerErr_Batch = new double[batchNum][layernum.length][];
		layer_input_Batch = new double[batchNum][layernum.length][];

		layer_input = new double[layernum.length][];
		layerErr = new double[layernum.length][];
		layer_weight = new double[layernum.length][][];
		layer_weight_delta = new double[layernum.length][][];
		Random random = new Random();
		for (int l = 0; l < layernum.length; l++) {
			for (int i = 0; i < batchNum; i++) {
				layer_Batch[i][l] = new double[layernum[l]];
				layerErr_Batch[i][l] = new double[layernum[l]];
				layer_input_Batch[i][l] = new double[layernum[l]];
			}
			layer[l] = new double[layernum[l]];
			layer_input[l] = new double[layernum[l]];
			layerErr[l] = new double[layernum[l]];
			if (l + 1 < layernum.length) {
				layer_weight[l] = new double[layernum[l] + 1][layernum[l + 1]];
				layer_weight_delta[l] = new double[layernum[l] + 1][layernum[l + 1]];
				for (int j = 0; j < layernum[l] + 1; j++)
					for (int i = 0; i < layernum[l + 1]; i++){
						//layer_weight[l][j][i] = 1 - 2 * random.nextDouble();// 随机初始化权重
						//layer_weight[l][j][i] =  Math.sqrt(6/layernum[l]) - 2 * Math.sqrt(6/layernum[l]) * random.nextDouble();// 随机初始化权重
						double n =  layernum[l];
						layer_weight[l][j][i] = random.nextGaussian() * Math.sqrt(2/n);
					}				        
			}
		}
	}

	// 逐层向前计算输出
	private double[][] computeOut(double[][] in) {
		double[][] result = new double[in.length][];
		for (int m = 0; m < in.length; m++) {
			double[] singleResult = new double[layer[layer.length - 1].length];
			for (int l = 1; l < layer_Batch[m].length; l++) {
				for (int j = 0; j < layer_Batch[m][l].length; j++) {
					double z = layer_weight[l - 1][layer[l - 1].length][j];
					for (int i = 0; i < layer_Batch[m][l - 1].length; i++) {
						layer_Batch[m][l - 1][i] = l == 1 ? in[m][i] : layer_Batch[m][l - 1][i];
						z += layer_weight[l - 1][i][j] * layer_Batch[m][l - 1][i];
					}
					//layer_Batch[m][l][j] = 1 / (1 + Math.exp(-z));
					if(l == layer_Batch[m].length - 1){
						layer_Batch[m][l][j] = 1 / (1 + Math.exp(-z));
					}else{	
						//layer_Batch[m][l][j] = MathUtils.SoftPlus(z);
						layer_Batch[m][l][j] = MathUtils.Relu(z);
						//layer_Batch[m][l][j] = MathUtils.LeakyRelu(z);
					}
					layer_input_Batch[m][l][j] = z;
					// layer[l][j] = Math.max(0, layer_input[l][j]);
				}
			}
			System.arraycopy(layer_Batch[m][layer.length - 1], 0, singleResult, 0, layer[layer.length - 1].length);
			result[m] = singleResult;
		}
		return result;
	}

	private double[] computeOut(double[] in) {
		for (int l = 1; l < layer.length; l++) {
			for (int j = 0; j < layer[l].length; j++) {
				double z = layer_weight[l - 1][layer[l - 1].length][j];
				for (int i = 0; i < layer[l - 1].length; i++) {
					layer[l - 1][i] = l == 1 ? in[i] : layer[l - 1][i];
					z += layer_weight[l - 1][i][j] * layer[l - 1][i];//看l算法等于1
				}
				layer_input[l][j] = z;
				if(l == layer.length - 1){
					layer[l][j] = 1 / (1 + Math.exp(-z));
					
				}else{
					layer[l][j] = MathUtils.Relu(z);
					//layer[l][j] = MathUtils.LeakyRelu(z);
				}
				
				// layer[l][j] = Math.max(0, layer_input[l][j]);
			}
		}
		double[] result = new double[layer[layer.length - 1].length];
		System.arraycopy(layer[layer.length - 1], 0, result, 0, layer[layer.length - 1].length);
		return result;
	}

	private void initParams() {
		s = new double[layer_weight.length][][];
		r = new double[layer_weight.length][][];
		for (int i = 0; i < layer_weight.length - 1; i++) {
			s[i] = new double[layer_weight[i].length][];
			r[i] = new double[layer_weight[i].length][];
			for (int j = 0; j < layer_weight[i].length; j++) {
				s[i][j] = new double[layer_weight[i][j].length];
				r[i][j] = new double[layer_weight[i][j].length];
			}
		}
	}

/**
 * mini_batch,每个值更新
 */
/*	public void train(double[][] input, double[][] realOutput, int trainNum) {
		// TODO 自动生成的方法存根
		MiniBatch mb = DataUtils.separateByBatch(this.batchNum, input, realOutput);
		for (int n = 0; n < trainNum / 1000; n++) {
			for (int m = 0; m < mb.getNum(); m++) {
				int t = 0;
				double error = Double.MAX_VALUE;
				initParams();
				double[][] realResult = mb.getOutput().get(m);
				double[][] input_batch = mb.getInput().get(m);
				while (t++ < trainNum) {
					error = 0;
					for (int z = 0; z < input_batch.length; z++) {
						double[] result_batch = computeOut(input_batch[z]);
						for (int j = 0; j < realResult[z].length; j++) {
							error += Math.pow(realResult[z][j] - result_batch[j], 2) / 2;
						}
						updateWeight(realResult[z], t);
					}
					System.out.println(t + " " +error / realResult.length );
					if (error / realResult.length < Params.ERROR) {
						break;
					}
				}

			}
		}
	}*/
	/**
	 * mini_batch,每批更新
	 */
	public void train(double[][] input, double[][] realOutput, int ITER) {	
		MiniBatch mb = separateByBatch(batchNum, input, realOutput);
		for (int t = 0; t < params.trainNum; t++) {
			initParams();
			double error = 0;	
			rate = max_rate - (max_rate - min_rate) * t / ITER;
			for (int m = 0; m < mb.getNum(); m++) {
				double batchError = 0;
				//double[][][] layerErr_Batch
				double[][] batch_in = mb.getInput().get(m);
				double[][] batch_out = mb.getOutput().get(m);
				double[][] out = computeOut(batch_in);
				for (int i = 0; i < out.length; i++) {
					for (int j = 0; j < out[i].length; j++) {
						batchError += Math.pow(out[i][j] - batch_out[i][j], 2) / 2;
					}
				}
				updateWeight(batch_out,t);
				batchError = batchError / (batch_out[0].length * batch_in.length);
				error += batchError;				
			}
			error = error / mb.getNum();
//			log.info(t + " " +error );
			if(error < params.ERROR){
				break;
			}
		}
	}
	/**
	 * 不分批
	 */
	/*public void train(double[][] input, double[][] realOutput, int trainNum) {
		// TODO 自动生成的方法存根
		for (int n = 0; n < Params.trainNum; n++) {
			double error = 0;
			initParams();
			for (int i = 0; i < input.length; i++) {
				double[] out = computeOut(input[i]);
				for (int j = 0; j < out.length; j++) {
					error += Math.pow(out[j] - realOutput[i][j], 2) / 2;
				}
				updateWeight(realOutput[i],n);

			}
			System.out.println(error / (realOutput[0].length * input.length) + "  " +n);
			if (error / (realOutput[0].length * input.length) < Params.ERROR) {
				break;
			}
		}
	}*/

	// 逐层反向计算误差并修改权重
	private void updateWeight(double[][] tar, int t)

	{
		int l = layer.length - 1;
		for (int m = 0; m < tar.length; m++) {
			for (int j = 0; j < layerErr[l].length; j++) {
				layerErr_Batch[m][l][j] = layer_Batch[m][l][j] * (1 - layer_Batch[m][l][j])
						* (tar[m][j] - layer_Batch[m][l][j]);
/*				layerErr_Batch[m][l][j] = MathUtils.Derivate_SoftPlust(layer_input_Batch[m][l][j]) 
						* (tar[m][j] - layer_Batch[m][l][j]);*/
			}
		}

		while (l-- > 0) {
			for (int j = 0; j < layerErr_Batch[0][l].length; j++) {
				double[] z = new double[tar.length];
				for (int i = 0; i < layerErr_Batch[0][l + 1].length; i++) {
					double g = 0;
					for (int m = 0; m < tar.length; m++) {
						z[m] = z[m] + l > 0 ? layerErr_Batch[m][l + 1][i] * layer_weight[l][j][i] : 0;
						g += layerErr_Batch[m][l + 1][i] * layer_Batch[m][l][j] / tar.length;// 隐含层调整
					}
					s[l][j][i] = rou1 * s[l][j][i] + (1 - rou1) * g;
					r[l][j][i] = rou2 * r[l][j][i] + (1 - rou2) * g * g;
					double s_modi = s[l][j][i] / (1 - Math.pow(rou1, t + 1));
					double r_modi = r[l][j][i] / (1 - Math.pow(rou2, t + 1));
					double weight_delta = e * s_modi / (Math.pow(r_modi, 0.5) + delta);
					layer_weight[l][j][i] += weight_delta;// 隐含层权重调整
					if (j == layerErr_Batch[0][l].length - 1) {
						double g_new = 0;
						for (int m = 0; m < tar.length; m++) {
							g_new += layerErr_Batch[m][l + 1][i] / tar.length;// 隐含层调整
						}
						s[l][j + 1][i] = rou1 * s[l][j + 1][i] + (1 - rou1) * g_new;
						r[l][j + 1][i] = rou2 * r[l][j + 1][i] + (1 - rou2) * g_new * g_new;
						double s_modi_new = s[l][j + 1][i] / (1 - Math.pow(rou1, t + 1));
						double r_modi_new = r[l][j + 1][i] / (1 - Math.pow(rou2, t + 1));
						double weight_delta_new = e * s_modi_new / (Math.pow(r_modi_new, 0.5) + delta);
						layer_weight[l][j + 1][i] += weight_delta_new;
					}
				}
				for (int m = 0; m < tar.length; m++) {
					//layerErr_Batch[m][l][j] = z[m] * layer_Batch[m][l][j] * (1 - layer_Batch[m][l][j]);// 记录误差
					//layerErr_Batch[m][l][j] = z[m] * MathUtils.Derivate_SoftPlust(layer_input_Batch[m][l][j]);
					layerErr_Batch[m][l][j] = z[m] * MathUtils.Derivate_Relu(layer_input_Batch[m][l][j]);
					//layerErr_Batch[m][l][j] = z[m] * MathUtils.Derivate_LeakyRelu(layer_input_Batch[m][l][j]);	
				}

			}
		}
	}

	// 逐层反向计算误差并修改权重
	private void updateWeight(double[] tar, int t) {
		int l = layer.length - 1;
		for (int j = 0; j < layerErr[l].length; j++) {
			layerErr[l][j] = layer[l][j] * (1 - layer[l][j]) * (tar[j] - layer[l][j]);
			// layerErr[l][j] = (tar[j] - layer[l][j]);
		}
		while (l-- > 0) {
			for (int j = 0; j < layerErr[l].length; j++) {
				double z = 0;
				for (int i = 0; i < layerErr[l + 1].length; i++) {
					double g = 0;
					z = z + l > 0 ? layerErr[l + 1][i] * layer_weight[l][j][i] : 0;
					g = layerErr[l + 1][i] * layer[l][j];// 隐含层调整

					s[l][j][i] = rou1 * s[l][j][i] + (1 - rou1) * g;
					r[l][j][i] = rou2 * r[l][j][i] + (1 - rou2) * g * g;
					double s_modi = s[l][j][i] / (1 - Math.pow(rou1, t + 1));
					double r_modi = r[l][j][i] / (1 - Math.pow(rou2, t + 1));
					double weight_delta = e * s_modi / (Math.pow(r_modi, 0.5) + delta);
					layer_weight[l][j][i] += weight_delta;// 隐含层权重调整
					if (j == layerErr[l].length - 1) {
						double g_new = 0;
						g_new = layerErr[l + 1][i];// 隐含层调整
						s[l][j + 1][i] = rou1 * s[l][j + 1][i] + (1 - rou1) * g_new;
						r[l][j + 1][i] = rou2 * r[l][j + 1][i] + (1 - rou2) * g_new * g_new;
						double s_modi_new = s[l][j + 1][i] / (1 - Math.pow(rou1, t + 1));
						double r_modi_new = r[l][j + 1][i] / (1 - Math.pow(rou2, t + 1));
						double weight_delta_new = e * s_modi_new / (Math.pow(r_modi_new, 0.5) + delta);
						layer_weight[l][j + 1][i] += weight_delta_new;
					}
				}
				//layerErr[l][j] = z * layer[l][j] * (1 - layer[l][j]);// 记录误差
				layerErr[l][j] = z * MathUtils.Derivate_Relu(layer_input[l][j]);// 记录误差
			}
		}
	}

	@Override
	public void init(double[][] centers) {
		// TODO 自动生成的方法存根

	}

	/*
	 * @Override public TrainResult simOutput(double[][] input, double[][]
	 * realOutput) { TrainResult trainResult = new TrainResult(); double[][]
	 * output = computeOut(input); trainResult.setSimResult(output); return
	 * trainResult; }
	 */

	public TrainResult simOutput(double[][] input, double[][] realOutput) {
		TrainResult trainResult = new TrainResult();
		double[][] output = new double[input.length][];
		for (int i = 0; i < input.length; i++) {
			output[i] = computeOut(input[i]);
		}
		trainResult.setSimResult(output);
		return trainResult;
	}

	@Override
	public double[][] getDNNLayer() {
		// TODO 自动生成的方法存根
		return this.layer;
	}

	@Override
	public double[][][] getDNNWeight() {
		// TODO 自动生成的方法存根
		return this.layer_weight;
	}
	
	private double[] computeOut1(double[] in,double[][][]layer_weight,int[] layernum) {
		double[][] layer = new double[layernum.length][];
		double[][]layer_input = new double[layernum.length][];
		for (int l = 0; l < layernum.length; l++) {
			layer[l] = new double[layernum[l]];
			layer_input[l] = new double[layernum[l]];
		}
			
		for (int l = 1; l < layer.length; l++) {
			for (int j = 0; j < layer[l].length; j++) {
				double z = layer_weight[l - 1][layer[l - 1].length][j];
				for (int i = 0; i < layer[l - 1].length; i++) {
					layer[l - 1][i] = l == 1 ? in[i] : layer[l - 1][i];
					z += layer_weight[l - 1][i][j] * layer[l - 1][i];//看l算法等于1
				}
				layer_input[l][j] = z;
				if(l == layer.length - 1){
					layer[l][j] = 1 / (1 + Math.exp(-z));
					
				}else{
					layer[l][j] = MathUtils.Relu(z);
				}
			}
		}
		double[] result = new double[layer[layer.length - 1].length];
		System.arraycopy(layer[layer.length - 1], 0, result, 0, layer[layer.length - 1].length);
		return result;
	}
	public TrainResult simOutput1(double[][] input, double[][] realOutput,
								  double[][][]layer_weight, int[] layernum) {
		TrainResult trainResult = new TrainResult();
		double[][] output = new double[input.length][];
		for (int i = 0; i < input.length; i++) {
			output[i] = computeOut1(input[i],layer_weight,layernum);
		}
		trainResult.setSimResult(output);
		return trainResult;
	}

	public static MiniBatch separateByBatch(int batchNum, double[][] inputData, double[][] outputData) {
		List<double[][]> input = new ArrayList();
		List<double[][]> output = new ArrayList();
		List<double[]> inputList = new ArrayList();
		List<double[]> outputList = new ArrayList();
		for (int i = 0; i < inputData.length; i++) {
			inputList.add(inputData[i]);
			outputList.add(outputData[i]);
		}

		int totalNum = inputList.size() / batchNum;
		if (totalNum >= 1) {
			for (int n = 0; n < totalNum; n++) {
				int size = inputList.size();
				double[][] input_batch = new double[batchNum][];
				double[][] output_batch = new double[batchNum][];
				for (int i = 0; i < batchNum; i++) {
					Random ran = new Random();
					int index = ran.nextInt(size);
					input_batch[i] = inputList.remove(index);
					output_batch[i] = outputList.remove(index);
					size--;
					if (size == 0) {
						break;
					}
				}
				input.add(input_batch);
				output.add(output_batch);
			}
		}

		if (inputList.size() != 0) {
			double[][] input_batch = new double[inputList.size()][];
			double[][] output_batch = new double[inputList.size()][];
			for (int i = 0; i < inputList.size(); i++) {
				input_batch[i] = inputList.get(i);
				output_batch[i] = outputList.get(i);
			}
			input.add(input_batch);
			output.add(output_batch);
		}

		MiniBatch mb = new MiniBatch();
		mb.setInput(input);
		mb.setOutput(output);
		mb.setNum(input.size());
		return mb;
	}

}
