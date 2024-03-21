package com.cj.model.func.modular.FloodPredict.model;


import com.cj.model.func.modular.FloodPredict.entity.TrainResult;
import lombok.Data;

import java.util.Random;

@Data
public class Elman implements NeuralNetwork {


	private int[] layernum;// 神经网络各层节点数

	private static double LEARN_RATE; // 学习率.
	private static int TRAINING_REPS;

	private double[][][] layer_weight;// 各层节点权重

	// TODO 检验期的修改
//    private static double forecastInput[] ;

	private static double hidden[][];
	private static double hidden2[][];
	private static double target[][];
	private static double actual[][];
	private static double actual2[][];
	private static double context[];

	// Unit errors.
	private static double erro[][];
	private static double errh[][];

	static double epsilon; // 误差精度

	public Elman(double[][] input, int[] Layernum, double lEARN_RATE, int tRAINING_REPS, double Epsilon) {
		this.LEARN_RATE = lEARN_RATE;
		this.TRAINING_REPS = tRAINING_REPS;
		this.epsilon = Epsilon;
		layernum = new int[Layernum.length];
		layer_weight = new double[Layernum.length][][];
		hidden = new double[input.length + 1][Layernum[1]];
		erro = new double[input.length][Layernum[3]];
		errh = new double[input.length][Layernum[1]];
		target = new double[input.length][Layernum[3]];
		actual = new double[input.length + 1][Layernum[3]];
		context = new double[Layernum[2]];

		Random random = new Random();
		layer_weight[0] = new double[Layernum[0] + 1][Layernum[1]];
		layer_weight[1] = new double[Layernum[2] + 1][Layernum[1]];
		layer_weight[2] = new double[Layernum[3] + 1][Layernum[2]];
		layer_weight[3] = new double[Layernum[1] + 1][Layernum[3]];

		for (int l = 0; l < Layernum.length; l++) {
			layernum[l] = Layernum[l];
			for (int j = 0; j < layer_weight[l].length; j++)
				for (int i = 0; i < layer_weight[l][j].length; i++)
					layer_weight[l][j][i] = random.nextDouble() - 0.5;// 随机初始化权重
		}
	}

	@Override
	public void init(double[][] centers) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Elman神经网络";
	}

	@Override
	public void train(double[][] input, double[][] realOutput, int trainNum) {
		// TODO Auto-generated method stub

		double err = 0.0;
		int sample = 0;
		int iterations = 0;
		boolean stopLoop = false;

		for (int j = 0; j < input.length; j++) {

			for (int i = 0; i < layernum[3]; i++) {
				target[j][i] = realOutput[j][0];
			}
		}
		feedForward(false, input);
		while (!stopLoop) {

			backPropagate(input);
			feedForward(false, input);

			// err = 0.0;
			for (int j = 0; j < input.length; j++) {
				for (int i = 0; i < layernum[3]; i++) {
					err += Math.sqrt(target[j][i] - actual[j][i]);
				}
			}
			err = 0.5 * err;

			// log.info(err+"");
			if ((iterations > TRAINING_REPS) || (err < epsilon)) {
				stopLoop = true;
			}
			iterations++;

		}

	}

	@Override
	public TrainResult simOutput(double[][] input, double[][] realOutput) {
		// TODO Auto-generated method stub
		TrainResult trainResult = new TrainResult();
		double[][] output = new double[input.length][];
		feedForward(true, input);
		output = actual2;
		trainResult.setSimResult(output);
		return trainResult;
	}

	private void feedForward(boolean flag, double[][] input) {
		if (flag == false) {
			double sum = 0.0;

			// Calculate input and context connections to hidden layer.
			for (int s = 0; s < input.length; s++) {
				for (int hid = 0; hid < layernum[1]; hid++) {
					sum = 0.0;
					for (int inp = 0; inp < layernum[0]; inp++) // from input to hidden...
					{
						sum += input[s][inp] * layer_weight[0][inp][hid];
					} // inp

					for (int con = 0; con < layernum[2]; con++) // from context to hidden...
					{
						sum += context[con] * layer_weight[1][con][hid];
					} // con

					sum += layer_weight[0][layernum[0]][hid]; // Add in bias.
					sum += layer_weight[1][layernum[2]][hid];
					hidden[s][hid] = sigmoid(sum);

					if (hidden[s][hid] > 0.999) {
						hidden[s][hid] = 0.999;
					} else if (hidden[s][hid] < 0.0001) {
						hidden[s][hid] = 0.0001;
					}
				} // hid
			}
			// Calculate the hidden to output layer.
			for (int s = 0; s < input.length; s++) {
				for (int out = 0; out < layernum[3]; out++) {
					sum = 0.0;
					for (int hid = 0; hid < layernum[1]; hid++) {
						sum += hidden[s][hid] * layer_weight[3][hid][out];
					} // hid

					sum += layer_weight[3][layernum[1]][out]; // Add in bias.

					actual[s][out] = sigmoid(sum);
//            System.out.println("sum"+"  "+sum+"  "+"actual"+"  "+actual[s][out]);
					if (actual[s][out] > 0.999) {
						actual[s][out] = 0.999;
					} else if (actual[s][out] < 0.0001) {
						actual[s][out] = 0.0001;
					}
				} // out
			}
			// Copy outputs of the hidden to context layer.
			for (int s = 0; s < input.length; s++) {
				for (int con = 0; con < layernum[2]; con++) {
					context[con] = hidden[s][con];
				}
			}
		}
		if (flag == true) {
			double sum = 0.0;
			hidden2 = new double[input.length][layernum[1]];
			actual2 = new double[input.length][layernum[3]];
			// Calculate input and context connections to hidden layer.
			for (int s = 0; s < input.length; s++) {
				for (int hid = 0; hid < layernum[1]; hid++) {
					sum = 0.0;
					for (int inp = 0; inp < layernum[0]; inp++) // from input to hidden...
					{
						sum += input[s][inp] * layer_weight[0][inp][hid];
					} // inp

					for (int con = 0; con < layernum[2]; con++) // from context to hidden...
					{
						sum += context[con] * layer_weight[1][con][hid];
					} // con

					sum += layer_weight[0][layernum[0]][hid]; // Add in bias.
					sum += layer_weight[1][layernum[2]][hid];
					hidden2[s][hid] = sigmoid(sum);
					if (hidden2[s][hid] > 0.999) {
						hidden2[s][hid] = 0.999;
					} else if (hidden2[s][hid] < 0.0001) {
						hidden2[s][hid] = 0.0001;
					}
				} // hid
			}
			// Calculate the hidden to output layer.
			for (int s = 0; s < input.length; s++) {
				for (int out = 0; out < layernum[3]; out++) {
					sum = 0.0;
					for (int hid = 0; hid < layernum[1]; hid++) {
						sum += hidden2[s][hid] * layer_weight[3][hid][out];
					} // hid

					sum += layer_weight[3][layernum[1]][out]; // Add in bias.
					actual2[s][out] = sigmoid(sum);
					if (actual2[s][out] > 0.999) {
						actual2[s][out] = 0.999;
					} else if (actual2[s][out] < 0.0001) {
						actual2[s][out] = 0.0001;
					}
				} // out
			}
			// Copy outputs of the hidden to context layer.
			for (int s = 0; s < input.length; s++) {
				for (int con = 0; con < layernum[2]; con++) {
					context[con] = hidden2[s][con];
				}
			}
		}
		return;
	}

	private void backPropagate(double[][] input) {
		// Calculate the output layer error (step 3 for output cell).
		for (int s = 0; s < input.length; s++) {
			for (int out = 0; out < layernum[3]; out++) {
				erro[s][out] = (target[s][out] - actual[s][out]) * sigmoidDerivative(actual[s][out]);
			}
		}
		// Calculate the hidden layer error (step 3 for hidden cell).
		for (int s = 0; s < input.length; s++) {
			for (int hid = 0; hid < layernum[1]; hid++) {
				errh[s][hid] = 0.0;
				for (int out = 0; out < layernum[3]; out++) {
					errh[s][hid] += erro[s][out] * layer_weight[3][hid][out];
				} // out
				errh[s][hid] *= sigmoidDerivative(hidden[s][hid]);
			} // hid
		}
		// Update the weights for the output layer (step 4).
		for (int s = 0; s < input.length; s++) {
			for (int out = 0; out < layernum[3]; out++) {
				for (int hid = 0; hid < layernum[1]; hid++) {
					layer_weight[3][hid][out] += (LEARN_RATE * erro[s][out] * hidden[s][hid]);
				} // hid

				layer_weight[3][layernum[1]][out] += (LEARN_RATE * erro[s][out]); // Update the bias.
			} // out
		}
		// Update the weights for the hidden layer (step 4).
		for (int s = 0; s < input.length; s++) {
			for (int hid = 0; hid < layernum[1]; hid++) {
				for (int inp = 0; inp < layernum[0]; inp++) {
					layer_weight[0][inp][hid] += (LEARN_RATE * errh[s][hid] * input[s][inp]);
				} // inp

				layer_weight[0][layernum[0]][hid] += (LEARN_RATE * errh[s][hid]); // Update the bias.
			} // hid
		}
		return;
	}

	private double sigmoid(double val) {
		return (1.0 / (1.0 + Math.exp(-val)));
	}

	private double sigmoidDerivative(double val) {
		return (val * (1.0 - val));
	}

	@Override
	public double[][] getDNNLayer() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public double[][][] getDNNWeight() {
		// TODO 自动生成的方法存根
		return null;
	}

	private void feedForward1(boolean flag, double[][] input, double[][][] layer_weight, int[] Layernum) {
		double[][] hidden = new double[input.length + 1][Layernum[1]];
		double[][] actual = new double[input.length + 1][Layernum[3]];
		int[] layernum = new int[Layernum.length];
		double[] context = new double[Layernum[2]];
		for (int l = 0; l < Layernum.length; l++)
			layernum[l] = Layernum[l];

		if (flag == false) {
			double sum = 0.0;

			// Calculate input and context connections to hidden layer.
			for (int s = 0; s < input.length; s++) {
				for (int hid = 0; hid < layernum[1]; hid++) {
					sum = 0.0;
					for (int inp = 0; inp < layernum[0]; inp++) // from input to hidden...
					{
						sum += input[s][inp] * layer_weight[0][inp][hid];
					} // inp

					for (int con = 0; con < layernum[2]; con++) // from context to hidden...
					{
						sum += context[con] * layer_weight[1][con][hid];
					} // con

					sum += layer_weight[0][layernum[0]][hid]; // Add in bias.
					sum += layer_weight[1][layernum[2]][hid];
					hidden[s][hid] = sigmoid(sum);

					if (hidden[s][hid] > 0.999) {
						hidden[s][hid] = 0.999;
					} else if (hidden[s][hid] < 0.0001) {
						hidden[s][hid] = 0.0001;
					}
				} // hid
			}
			// Calculate the hidden to output layer.
			for (int s = 0; s < input.length; s++) {
				for (int out = 0; out < layernum[3]; out++) {
					sum = 0.0;
					for (int hid = 0; hid < layernum[1]; hid++) {
						sum += hidden[s][hid] * layer_weight[3][hid][out];
					} // hid

					sum += layer_weight[3][layernum[1]][out]; // Add in bias.

					actual[s][out] = sigmoid(sum);
//	            System.out.println("sum"+"  "+sum+"  "+"actual"+"  "+actual[s][out]);
					if (actual[s][out] > 0.999) {
						actual[s][out] = 0.999;
					} else if (actual[s][out] < 0.0001) {
						actual[s][out] = 0.0001;
					}
				} // out
			}
			// Copy outputs of the hidden to context layer.
			for (int s = 0; s < input.length; s++) {
				for (int con = 0; con < layernum[2]; con++) {
					context[con] = hidden[s][con];
				}
			}
		}
		if (flag == true) {
			double sum = 0.0;
			hidden2 = new double[input.length][layernum[1]];
			actual2 = new double[input.length][layernum[3]];
			// Calculate input and context connections to hidden layer.
			for (int s = 0; s < input.length; s++) {
				for (int hid = 0; hid < layernum[1]; hid++) {
					sum = 0.0;
					for (int inp = 0; inp < layernum[0]; inp++) // from input to hidden...
					{
						sum += input[s][inp] * layer_weight[0][inp][hid];
					} // inp

					for (int con = 0; con < layernum[2]; con++) // from context to hidden...
					{
						sum += context[con] * layer_weight[1][con][hid];
					} // con

					sum += layer_weight[0][layernum[0]][hid]; // Add in bias.
					sum += layer_weight[1][layernum[2]][hid];
					hidden2[s][hid] = sigmoid(sum);
					if (hidden2[s][hid] > 0.999) {
						hidden2[s][hid] = 0.999;
					} else if (hidden2[s][hid] < 0.0001) {
						hidden2[s][hid] = 0.0001;
					}
				} // hid
			}
			// Calculate the hidden to output layer.
			for (int s = 0; s < input.length; s++) {
				for (int out = 0; out < layernum[3]; out++) {
					sum = 0.0;
					for (int hid = 0; hid < layernum[1]; hid++) {
						sum += hidden2[s][hid] * layer_weight[3][hid][out];
					} // hid

					sum += layer_weight[3][layernum[1]][out]; // Add in bias.
					actual2[s][out] = sigmoid(sum);
					if (actual2[s][out] > 0.999) {
						actual2[s][out] = 0.999;
					} else if (actual2[s][out] < 0.0001) {
						actual2[s][out] = 0.0001;
					}
				} // out
			}
			// Copy outputs of the hidden to context layer.
			for (int s = 0; s < input.length; s++) {
				for (int con = 0; con < layernum[2]; con++) {
					context[con] = hidden2[s][con];
				}
			}
		}
		return;
	}

	public TrainResult simOutput1(double[][] input, double[][] realOutput, double[][][] layer_weight, int[] Layernum) {
		// TODO Auto-generated method stub
		TrainResult trainResult = new TrainResult();
		double[][] output = new double[input.length][];
		feedForward1(true, input, layer_weight, Layernum);
		output = actual2;
		trainResult.setSimResult(output);
		return trainResult;
	}

}
