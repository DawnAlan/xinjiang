package com.cj.model.func.modular.FloodPredict.CorrectModel;

public class SeriesCorrection {
	
	static final int lvdinglen = 70;//
	static final int INTwarmUp = 115;// 预热期
	
	// 串联校正
	/**
	 * 短期水文模型串联校正入口
	 * @param real_value 实测流量，包括预热期115天的数据，45天为短期的预热期，70天为串联校正率定的时间
	 * @param foreflow 预测流量，包括预热期115天的数据
	 * @param order 为预测流量比实测流量长的长度，即预见期的长度
	 * 对误差序列建立AR模型
	 * 如果order=2
	 * aXt-1+bXt-2=Xt+2;
	 * @return
	 */
		public double[] Series(double[] real_value, double[] foreflow, int order) {

			int startpoint = (INTwarmUp - lvdinglen);
			int LenForlvding = lvdinglen;
			double[] real_lvding = new double[LenForlvding]; // 实测序列
			double[] esti_lvding = new double[LenForlvding]; // 预报序列
			double[] real_jianyan = new double[real_value.length - INTwarmUp]; // 实测序列
			double[] esti_jianyan = new double[foreflow.length - INTwarmUp]; // 预报序列
			double[] correc_estimate = new double[foreflow.length - INTwarmUp];
			double[] coefi = new double[2]; // 存储AR模型的系数

			for (int j = 0; j < LenForlvding; j++) {
				esti_lvding[j] = foreflow[startpoint + j];
				real_lvding[j] = real_value[startpoint + j];
			}

			for (int j = 0; j < esti_jianyan.length; j++) {
				esti_jianyan[j] = foreflow[INTwarmUp + j];
			}

			for (int j = 0; j < real_jianyan.length; j++) {
				real_jianyan[j] = real_value[INTwarmUp + j];
			}

			coefi = ParaForSeries(real_lvding, esti_lvding, order);
			correc_estimate = RunSeries(real_jianyan, esti_jianyan, order, coefi);

			return correc_estimate;

		}
		
		// 串联校正参数计算
		public double[] ParaForSeries(double[] real_value, double[] foreflow, int order) {

			int real_length = real_value.length;
			int esti_length = foreflow.length; // 预报序列长度
			double[] real = new double[real_length]; // 实测序列
			double[] esti = new double[esti_length]; // 预报序列
			double[] error = new double[real_length]; // 误差序列
			double[][] lag = new double[error.length - 2 - order][2];// 比实际长度少2个点
			
			double[] coefi = new double[2]; // 存储所有阶数的AR模型的系数

			// esti数组赋值
			for (int j = 0; j < esti_length; j++) {
				esti[j] = foreflow[j];

			}

			// real和error数组赋值
			for (int i = 0; i < real_length; i++) {
				real[i] = real_value[i];
				error[i] = esti[i] - real[i];
			}

			// 滞后项？
			for (int m = 1; m < 3; m++) {
				for (int k = 0; k < lag.length; k++) {
					lag[k][m - 1] = error[k + 2 - m];
				}
			}

			double[] nolag = new double[error.length - 2 - order];
			double[][] cal_lag = new double[error.length - 2 - order][2];

			// nolag赋值
			for (int m = 0; m < nolag.length; m++) {
				nolag[m] = error[m + 2 + order];
			}

			// cal_lag赋值
			for (int i = 0; i < cal_lag.length; i++) {
				for (int j = 0; j < cal_lag[0].length; j++)
					cal_lag[i][j] = lag[i][j];
			}

			// 计算自回归系数
			coefi = coefficient(cal_lag, nolag);// 利用最小二乘法求串联系数

			return coefi;

		}
		
		public double[] RunSeries(double[] real_value, double[] foreflow, int order, double[] para) {

			int real_length = real_value.length;
			int esti_length = foreflow.length; // 预报序列长度
			double[] real = new double[real_length]; // 实测序列
			double[] esti = new double[esti_length]; // 预报序列
			double[] error = new double[real_length]; // 误差序列
			double[][] lag = new double[esti.length - 2 - order][2];// 比实际长度少2个点
			double[] correc_estimate = new double[esti_length];

			// esti数组赋值
			for (int j = 0; j < esti_length; j++) {
				esti[j] = foreflow[j];

			}

			// real和error数组赋值
			for (int i = 0; i < real_length; i++) {
				real[i] = real_value[i];
				error[i] = esti[i] - real[i];
			}

			// 滞后项？
			for (int m = 1; m < 3; m++) {
				for (int k = 0; k < lag.length; k++) {
					lag[k][m - 1] = error[k + 2 - m];
				}
			}
			double[] correc = new double[esti.length - 2 - order];

			// 计算自回归模型的校正后的误差项
			for (int row = 0; row < correc.length; row++) {
				for (int col = 0; col < lag[0].length; col++) {
					correc[row] += para[col] * lag[row][col];
				}
			}

			for (int row = 0; row < correc_estimate.length; row++) {
				if (row < 2 + order) {
					correc_estimate[row] = esti[row];
				} else {
					correc_estimate[row] = esti[row] - correc[row - order - 2];
				}
			}

			return correc_estimate;
		}
		
		// 最小二乘法求解串联系数
		public double[] coefficient(double[][] lag, double[] nolag) {
			double[][] T_lag = new double[lag[0].length][lag.length];
			for (int m = 0; m < lag[0].length; m++) {
				for (int k = 0; k < lag.length; k++) {
					T_lag[m][k] = lag[k][m];
				}
			}

			double[][] a = new double[2][2];
			double[] b = new double[2];

			// 计算a
			for (int r = 0; r < T_lag.length; r++) {
				for (int c = 0; c < lag[0].length; c++) {
					for (int k = 0; k < T_lag[0].length; k++) {
						a[r][c] += T_lag[r][k] * lag[k][c];
					}

				}
			}
			// 计算b
			for (int r = 0; r < 2; r++) {
				for (int k = 0; k < T_lag[0].length; k++) {
					b[r] += T_lag[r][k] * nolag[k];
				}

			}

			// 高斯消元法求解线性方程组，得到线性方程解系数
			int k = 0;
			while (a.length > 1) {

				for (int i = k + 1; i < a.length; i++) {
					a[i][k] = a[i][k] / a[k][k];
				}
				for (int row = k + 1; row < a.length; row++) {
					for (int col = k + 1; col < a.length; col++) {
						a[row][col] = a[row][col] - a[row][k] * a[k][col];
					}
					b[row] = b[row] - a[row][k] * b[k];
				}
				if (k != a.length - 1)
					k++;
				else
					break;
			}
			if (a.length == 1) {
				b[0] = b[0] / a[0][0];
			} else {
				b[a.length - 1] = b[a.length - 1] / a[a.length - 1][a.length - 1];
				for (int row = a.length - 2; row > -1; row--) {
					for (int col = row + 1; col < a.length; col++) {
						b[row] -= a[row][col] * b[col];
					}
					b[row] = (1 / a[row][row]) * b[row];

				}
			}
			return b;
		}

}
