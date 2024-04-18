package com.cj.model.func.modular.FloodPredict.utils;




import com.cj.model.func.modular.FloodPredict.model.entity.Center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Cluster {
	/**
	 * 根据Kmeans确定隐含层的类中心
	 * 
	 * @param data
	 *            输入数据集
	 * @param num_gaussian
	 *            聚类的数量
	 * @return 返回隐含层高斯中心
	 */
	public double[][] kmeans(double[][] data, int num_gaussian) {

		// 聚类中心，每行是一个中心
		double[][] centers = new double[num_gaussian][data[0].length];

		// 初始化中心
		System.arraycopy(data, 0, centers, 0, centers.length);

		// Prepare the loop
		HashMap<double[], Integer> old_assignments = null;
		boolean changed = true;

		// 聚类中心没有改变的时候，聚类结束

		while (changed) {

			changed = false;
			HashMap<double[], Integer> assignments = new HashMap(); // 保存分配的中心
																		// 以及数据
			double[][] new_centers = new double[num_gaussian][data[0].length];

			// Assignments
			int[] center_count = new int[num_gaussian];
			for (double[] f : data) {

				// 找到最近的中心
				int min_index = 0;
				double min_dis = euclidean_distance(centers[0], f);// || Xj - Cij ||欧式距离
				for (int i = 1; i < centers.length; i++) {
					double distance = euclidean_distance(centers[i], f);// || Xj - Cij ||欧式距离
					if (distance < min_dis) {//找到当前输入样本距离最近的隐含层中心
						min_dis = distance;
						min_index = i;
					}
				}

				// 将当前输入样本以及所对应的隐含层序号存储
				assignments.put(f, min_index);

				// 判断当前样本聚类中心是否变化
				if (old_assignments == null || old_assignments.get(f) != min_index) {
					changed = true;
				}

				// 新的聚类中心，这个类中心没有计算均值
				for (int i = 0; i < f.length; i++) {
					new_centers[min_index][i] += f[i];
				}

				// 计算该类中心的数据量
				center_count[min_index]++;

			}

			// 类中心计算均值
			for (int i = 0; i < num_gaussian; i++) {
				for (int j = 0; j < new_centers[i].length; j++) {

					// Average the centers
					if (center_count[i] != 0) {
						new_centers[i][j] /= center_count[i];
					}
				}
			}

			// 更新
			old_assignments = assignments;
			centers = new_centers;
		}

		// 返回最好的类中心
		return centers;
	}

	/**
	 * 计算欧拉距离
	 *
	 * @param a
	 *            One vector
	 * @param b
	 *            Another vector
	 * @return 欧拉距离
	 */
	public double euclidean_distance(double[] a, double[] b) {
		double sum = 0.0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.pow(a[i] - b[i], 2);
		}
		return Math.sqrt(sum);
	}
	
	/**
	 * 平均移位聚类，该方法不用指定隐含层参数
	 * @param data 输入数据
	 * @param width 中心半径
	 * @param shiftError 偏移终止误差
	 * @return 聚类中心
	 */
	public double[][] mean_shift_kmeans(double[][] data, double width, double shiftError){
		List<Center> centers = new ArrayList();
		boolean[] flags = new boolean[data.length];
		int dataDim = data[0].length;
		int dataNum = data.length;
		//遍历所有数据
		for(int i = 0; i < dataNum; i ++){
			//第一次寻找中心
			if(i == 0){
				double[] lastShift = new double[dataDim];
				Center center = new Center(data[i]);
				double error = Double.MAX_VALUE;
				flags[i] = true;
				//更新终止条件，shift值收敛
				
				while(error > shiftError){
					error = 0;
					//更新中心值（第一次lastShift为0，没有变化）
					double[] newCenter =new double[dataDim];
					for(int j = 0; j < dataDim; j++){
						newCenter[j] = center.getCenter()[j] + lastShift[j];
					}
					center.setCenter(newCenter);
					center.setList(new ArrayList());
					
					double[] shift = new double[dataDim];
					//计算所有数据与当前center的距离
					for(int j = 0; j < dataNum; j++){
						double distence = euclidean_distance(center.getCenter(), data[j]);
						if(distence < width){
							flags[j] = true;
							center.getList().add(data[j]);
						}
					}
					//计算shift值
					for(int z = 0; z < dataDim; z++){
						double[] guassion = new double[center.getList().size()];
						double totalGuass = 0;
						for(int j = 0; j < center.getList().size(); j++){
							guassion[j] = MathUtils.GuassionFunc(center.getList().get(j)[z] - center.getCenter()[z], width);
							totalGuass += guassion[j];
							shift[z] += center.getList().get(j)[z] * guassion[j]; 
						}
						shift[z] = shift[z] / totalGuass - center.getCenter()[z];
						
					}
					//计算本次shift的大小，并更新lastShift
					for(int j = 0; j < dataDim; j++){
						error += Math.pow(shift[j], 2);
						lastShift[j] = shift[j];
					}
					error = Math.sqrt(error);
				}
				centers.add(center);
			}else{
				if(flags[i] == false){
					double[] lastShift = new double[dataDim];
					double error = Double.MAX_VALUE;
					Center center = new Center(data[i]);
					flags[i] = true;
					//更新终止条件，shift值收敛
					
					while(error > shiftError){
						error = 0;
						//更新中心值（第一次lastShift为0，没有变化）
						double[] newCenter =new double[dataDim];
						for(int j = 0; j < dataDim; j++){
							newCenter[j] = center.getCenter()[j] + lastShift[j];
						}
						center.setCenter(newCenter);
						center.setList(new ArrayList());
						
						double[] shift = new double[dataDim];
						//计算所有数据与当前center的距离
						for(int j = 0; j < dataNum; j++){
							double distence = euclidean_distance(center.getCenter(), data[j]);
							if(distence < width){
								flags[j] = true;
								center.getList().add(data[j]);
							}
						}
						//计算shift值
						for(int z = 0; z < dataDim; z++){
							double[] guassion = new double[center.getList().size()];
							double totalGuass = 0;
							for(int j = 0; j < center.getList().size(); j++){
								guassion[j] = MathUtils.GuassionFunc(center.getList().get(j)[z] - center.getCenter()[z], width);
								totalGuass += guassion[j];
								shift[z] += center.getList().get(j)[z] * guassion[j]; 
							}
							shift[z] = shift[z] / totalGuass - center.getCenter()[z];
							
						}
						//计算本次shift的大小，并更新lastShift
						for(int j = 0; j < dataDim; j++){
							error += Math.pow(shift[j], 2);
							lastShift[j] = shift[j];
						}
						error = Math.sqrt(error);
					}
					//判断当前聚类中心是否可归类到原有的中心
					double minDistence = Double.MAX_VALUE;
					int indexOfminDistence = 0;
					for(int j = 0; j < centers.size(); j ++){
						double distence = euclidean_distance(centers.get(j).getCenter(), center.getCenter());
						if(distence < minDistence){
							minDistence = distence;
							indexOfminDistence = j;
						}
					}
					if(minDistence < width){
						double[] originCenter = centers.get(indexOfminDistence).getCenter();
						for(int z = 0; z < originCenter.length; z++){
							originCenter[z] = (originCenter[z] + center.getCenter()[z]) / 2;
						}
						centers.get(indexOfminDistence).setCenter(originCenter);
					}else{
						centers.add(center);
					}
				}
			}
		}
		//结果转存数组，返回
		double[][] result = new double[centers.size()][];
		for(int i = 0; i < centers.size(); i++){
			result[i] = centers.get(i).getCenter();
		}
		
		return result;
	}
}
