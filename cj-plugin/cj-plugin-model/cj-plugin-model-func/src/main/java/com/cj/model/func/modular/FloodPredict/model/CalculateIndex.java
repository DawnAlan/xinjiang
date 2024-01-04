package com.cj.model.func.modular.FloodPredict.model;
/*
 * 预报精度指标计算
 * real 实测流量
 * estimate 预报流量
 *
 */
public class CalculateIndex {
	// 计算平均绝对误差
		public double MAE(double[] real, double[] estimate) {

			double[] absolute_error = new double[real.length];
			double average_ae = 0;
			for (int j = 0; j < real.length; j++) {
				absolute_error[j] = Math.abs(estimate[j] - real[j]);
				average_ae += absolute_error[j];
			}
			average_ae = average_ae / real.length;

			return average_ae;
		}

		
		// 计算平均相对误差
		private double MRE(double[] real, double[] estimate) {

			double[] ralative_error = new double[real.length];
			double average_re = 0;
			for (int j = 0; j < real.length; j++) {
				ralative_error[j] = Math.abs(estimate[j] - real[j]) / real[j];
				average_re += ralative_error[j];
			}
			average_re = average_re / real.length;

			return average_re;
		}

		
		// 计算确定性系数
		public double DC(double[] real, double[] estimate)

		{
			double dc = 0;
			double a = 0;
			double b = 0;
			double c = 0;

			for (int j = 0; j < real.length; j++) {
				a += real[j];
			}
			a = a / real.length;

			for (int j = 0; j < real.length; j++) {
				b += (real[j] - a) * (real[j] - a);
			}

			for (int j = 0; j < real.length; j++) {
				c += (real[j] - estimate[j]) * (real[j] - estimate[j]);
			}
			dc = 1 - c / b;

			return dc;

		}

		
		// 计算合格率
		public double QR(double[] real, double[] estimate) {

			double qr = 0;
			double[] per = new double[real.length];
			double count = 0;
			for (int j = 0; j < real.length; j++) {
				per[j] = Math.abs(real[j] - estimate[j]) / real[j];
				if (per[j] < 0.2)
					count = count + 1;
			}
			qr = count / (real.length);

			return qr;
		}

		
		// 计算均方根误差
		public double RMSE(double[] real, double[] estimate) {

			double rmse = 0;
			for (int j = 0; j < real.length; j++) {
				rmse += (real[j] - estimate[j]) * (real[j] - estimate[j]);
			}

			rmse = Math.sqrt(rmse / real.length);

			return rmse;

		}

		
		// 计算精度等级
		public String AG(double[] real, double[] estimate) {

			double dc = 0;
			String accuracy_grade = "";
			dc = DC(real, estimate);
			if (dc > 0.90)
				accuracy_grade = "甲";
			else if ((dc < 0.90) && (dc) >= 0.70)
				accuracy_grade = "乙";
			else if ((dc < 0.70) && (dc) >= 0.50)
				accuracy_grade = "丙";
			else
				accuracy_grade = "丙级以下";

			return accuracy_grade;
		}
		
		//计算连续等级评分
		public double CRPS(double[] realpro, double[] estimatepro) {
			
			double crps = 0;
			for (int i = 0; i < realpro.length; i++) {
				crps += (realpro[i] - estimatepro[i]) * (realpro[i] - estimatepro[i]) / realpro.length;
			}
			return crps;
		}
		//计算集合平均
		public double EM(double[] estimate) {
			double em = 0;
			for (int i = 0; i < estimate.length; i++) {
				em += estimate[i] / estimate.length;
			}
			return em;
		}
		//计算NS系数
		public double NS(double[] real, double[] estimate) {
			double ns = 0;
			double temp0 = 0;
			double temp1 = 0;
			double temp2 = 0;
			for (int i = 0; i < real.length; i++) {
				temp0 += real[i] / real.length;
				temp1 += Math.pow((real[i] - estimate[i]), 2);
				temp2 += Math.pow((real[i] - temp0), 2);
				ns = temp1 / temp2;
			}
			return ns;
		}
		
		//计算标准差
		public double[] SD(double[] real, double[] estimate) {
			
			double[] sd = new double[3];
			double realavg = 0;
			double estimateavg = 0;
			for (int i = 0; i < real.length; i++) {
				realavg += real[i] / real.length;
				estimateavg += estimate[i] / estimate.length;
				sd[0] += (real[i] - realavg) * (real[i] - realavg) / real.length;
				sd[1] += (estimate[i] - estimateavg) * (estimate[i] - estimateavg) / estimate.length;
			}
			sd[0] = Math.pow(sd[0], 0.5);
			sd[1] = Math.pow(sd[1], 0.5);
			sd[2] = sd[0] - sd[1];
			return sd;
		}
		

}
