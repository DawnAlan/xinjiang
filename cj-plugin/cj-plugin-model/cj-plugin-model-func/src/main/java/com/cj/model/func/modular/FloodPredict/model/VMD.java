package com.cj.model.func.modular.FloodPredict.model;

import com.cj.model.func.modular.FloodPredict.utils.ExcelTool;
import org.apache.commons.math3.complex.Complex;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.IOException;
import java.util.Arrays;
/**
 *
 * % Input and Parameters:
 * % ---------------------
 *
 * % Input Parameters:
 * % signal：要分解的时域信号--自己给
 * % alpha： 惩罚因子，也称平衡参数--2000
 * % tau：噪声容忍度--0
 * % K：分解的模态数--自己定
 * % DC：直流分量,一般为--0
 * % init：初始化中心频率--1
 * %       0 = all omegas start at 0
 * %       1 = all omegas start uniformly distributed
 * %       2 = all omegas initialized randomly
 * % tol：收敛准则容忍度；通常在1e-6左右--1e-7
 * %
 * % Output Parameters:
 * % u：分解模式的集合
 * % u_hat：模式的频谱
 * % omega：估计模式中心频率
 *
 * @return子序列 这个模型只返回了分解子序列的集合--u，其它两个可根据自己需求设置输出；其中行数K为分解层数，列数T为信号数
 */
public class VMD {
        public static void main(String[] args) throws IOException, InvalidFormatException {
            Object[][] input = ExcelTool.readExcel("D:\\14年前数据.xlsx","Sheet1");
            double[] signal=new double[input.length];
            for (int i = 0; i < input.length; i++) {
                signal[i]=(double) input[i][1];
            }
            int K=6;
            double[][] a =vmd(signal,K);
            Object[][] result = new Object[a[0].length][a.length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    result[j][i]=a[i][j];
                }
            }
            ExcelTool.writeObjectExcel("D:\\14年前数据.xlsx","Sheet2",result);
            System.out.println(a);
    }
    public static double[][] vmd (double[] signal, int K) {
        //初始参数一般不做修改，如果分解拟合度较差，可以选择缩小alpha值
        int alpha = 10;
        double tau = 0;
        int DC = 0;
        int init = 1;
        double tol = 1e-7;


        int save_T = signal.length;
        double fs = 1.0 / save_T;

        // 通过镜像扩展信号
        int T = save_T;
        double[] f_mirror = new double[2 * T];

        if (save_T % 2 == 0) {

            for (int i = 0; i < T / 2; i++) {
                f_mirror[i] = signal[T / 2 - i-1];
            }

            for (int i = T / 2; i < 3 * T / 2; i++) {
                f_mirror[i] = signal[i - T / 2];
            }

            for (int i = 3 * T / 2; i < 2 * T ; i++) {
                f_mirror[i] = signal[5 * T / 2 - i - 1];
            }
        } else {

            for (int i = 0; i < T / 2; i++) {
                f_mirror[i] = signal[T / 2 - i ];
            }

            f_mirror[(T-1)/2]=0;

            for (int i = (T+1) / 2; i < 3 * T / 2+1; i++) {
                f_mirror[i] = signal[i - (T+1) / 2];
            }

            for (int i = 3 * T / 2+1; i < 2 * T ; i++) {
                f_mirror[i] = signal[5 * T / 2 - i ];
            }
        }
        double[] f = f_mirror;



        // 处理时域 0 到 T（镜像信号）
        T = f.length;
        double[] t = new double[T];
        for (int i = 0; i < T; i++) {
            t[i] = (i + 1) / (double)T;
        }

        double[] freqs = new double[T];
        for (int i = 0; i < T; i++) {
            freqs[i] = t[i] - 0.5 - 1.0/ T;
        }

        /**
         * 注意N的值
         */
        int N = 100;

        int[] Alpha = new int[K];
        for (int i = 0; i < K; i++) {
            Alpha[i] = alpha;
        }


        Complex[] paddedSignal=fft(f);//傅里叶变换
        Complex[] f_hat = fftShift(paddedSignal);
        Complex[] f_hat_plus = Arrays.copyOf(f_hat, f_hat.length);
        for (int i = 0; i < f_hat.length/2; i++) {
            f_hat_plus[i] = Complex.ZERO;
        }


        Complex[][][] u_hat_plus = new Complex[N][f_hat.length][K];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < f_hat.length; j++) {
                for (int z = 0; z < K ; z++) {
                    u_hat_plus[i][j][z] = Complex.ZERO;
                }
            }
        }

        double[][] omega_plus = new double[N][K];
        switch (init) {
            case 1:
                for (int i = 0; i < K; i++) {
                    double real = (0.5 / K) * i;
                    omega_plus[0][i] = real;
                }
                break;
            case 2:
                double[] randomVals = new double[K];
                for (int i = 0; i < K; i++) {
                    randomVals[i] = Math.log(fs) + (Math.log(0.5) - Math.log(fs)) * Math.random();
                }
                Arrays.sort(randomVals);
                for (int i = 0; i < K; i++) {
                    double real = Math.exp(randomVals[i]);
                    omega_plus[0][i] = real;
                }
                break;
            default:
                for (int i = 0; i < K; i++) {
                    omega_plus[0][i] = 0;
                }
        }

// if DC mode imposed, set its omega to 0
        if (DC!=0) {
            omega_plus[0][0] = 0.0;
        }



// start with empty dual variables
        Complex[][] lambda_hat = new Complex[N][f_hat.length];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < f_hat.length; j++) {
                lambda_hat[i][j] = Complex.ZERO;
            }
        }

// other inits
        Complex uDiff = new Complex(tol + Math.ulp(1.0),tol + Math.ulp(1.0)); // update step 这里nDiff为复数
        double uDiff_abs=tol + Math.ulp(1.0);
        Complex[] sum_uk = new Complex[f_hat.length]; // accumulator
        int n = 0;// loop counter
        for (int j = 0; j < f_hat.length; j++) {
            sum_uk[j] = Complex.ZERO;
        }


//         ----------- Main loop for iterative updates
        while (uDiff_abs > tol && n < N-1) {
            // Update first mode accumulator
            int k = 0;

            for (int i = 0; i < f_hat.length; i++) {
                sum_uk[i] = u_hat_plus[n][i][K-1].add(sum_uk[i]).subtract(u_hat_plus[n][i][0]);
            }

            for (int i = 0; i < f_hat.length; i++) {
                Complex numerator = f_hat_plus[i].subtract(sum_uk[i]).subtract(lambda_hat[n][i].divide(2));
                double denominator = 1 + Alpha[k] * Math.pow(freqs[i] - omega_plus[n][k], 2);
                u_hat_plus[n+1][i][k] = numerator.divide(denominator);
            }


            // Update spectrum of first mode through Wiener filter of residuals
            // Update first omega if not held at 0
            if (DC==0) {
                double[][] hfreqs=new double[1][T/2];
                double[][] lu_hat_sq=new double[T/2][1];
                double u_hat_sq_sum = 0.0;
                for (int j = T/2 ; j < T; j++) {
                    u_hat_sq_sum += Math.pow((u_hat_plus[n+1][j][k].abs()), 2);
                }

                for (int i = T/2 ; i < freqs.length; i++) {
                    hfreqs[0][i-T/2]=freqs[i];
                    lu_hat_sq[i-T/2][0]=Math.pow((u_hat_plus[n+1][i][k].abs()), 2);
                }
                double[][] product = matrixMultiplication(hfreqs,lu_hat_sq);
                omega_plus[n+1][k] = product[0][0]/u_hat_sq_sum;
            }


            // Update of any other mode
            for (k = 1; k < K; k++) {
                // Accumulator
                for (int i = 0; i <T; i++) {
                    sum_uk[i] = u_hat_plus[n+1][i][k-1].add(sum_uk[i]).subtract(u_hat_plus[n][i][k]);
                }

                // Mode spectrum

                for (int i = 0; i < f_hat.length; i++) {
                    Complex numerator = f_hat_plus[i].subtract(sum_uk[i]).subtract(lambda_hat[n][i].divide(2));
                    double denominator = 1 + Alpha[k] * Math.pow(freqs[i] - omega_plus[n][k], 2);
                    u_hat_plus[n+1][i][k] = numerator.divide(denominator);
                }


                // Center frequencies
                double[][] hfreqs=new double[1][T/2];
                double[][] lu_hat_sq=new double[T/2][1];
                double u_hat_sq_sum = 0.0;
                for (int j = T/2 ; j < T; j++) {
                    u_hat_sq_sum += Math.pow((u_hat_plus[n+1][j][k].abs()), 2);
                }

                for (int i = T/2 ; i < freqs.length; i++) {
                    hfreqs[0][i-T/2]=freqs[i];
                    lu_hat_sq[i-T/2][0]=Math.pow((u_hat_plus[n+1][i][k].abs()), 2);
                }
                double[][] product = matrixMultiplication(hfreqs,lu_hat_sq);
                omega_plus[n+1][k] = product[0][0]/u_hat_sq_sum;
            }

            // Dual ascent
            for (int i = 0; i < T; i++) {
                Complex sum = Complex.ZERO;
                for (int j = 0; j < K; j++) {
                    sum = sum.add(u_hat_plus[n+1][i][j]);
                }
                lambda_hat[n+1][i] = lambda_hat[n][i].add(((sum).subtract(f_hat_plus[i])).multiply(tau));
            }

            // Loop counter
            n++;

            // Check convergence

            uDiff = new Complex(tol + Math.ulp(1.0),tol + Math.ulp(1.0));

            for (int i = 0; i < K; i++) {
                Complex[][] hdiff = new Complex[1][T];
                Complex[][] ldiff = new Complex[T][1];

                for (int j = 0; j < T; j++) {
                    Complex u_hat_diff = u_hat_plus[n][j][i].subtract(u_hat_plus[n-1][j][i]);
                    hdiff[0][j]= u_hat_diff;
                    ldiff[j][0] = u_hat_diff;
                }

                Complex[][] product =complexMatrixMultiplication(hdiff, ldiff);
                Complex comT= new Complex(T,0);
                Complex I=new Complex(1,0);

                uDiff=uDiff.add(I.divide(comT).multiply(product[0][0]));

            }
            uDiff_abs = uDiff.abs();
        }

// discard empty space if converged early
        int M = Math.min(N, n);
        double[][] omega = new double[M][K];
        for (int i = 0; i <M ; i++) {
            for (int j = 0; j < K; j++) {
                omega[i][j]=omega_plus[i][j];
            }
        }
// 有一些不一致
        Complex[][] u_hat = new Complex[T][K];
        for (int i = T/2 ; i < T; i++) {
            for (int j = 0; j < K; j++) {
                u_hat[i][j] = u_hat_plus[N-1][i][j];
            }
        }
        for (int i = 1; i <T/2+1 ; i++) {
            for (int j = 0; j < K; j++) {
                u_hat[i][j] = u_hat_plus[N-1][T-i][j].conjugate();
            }
        }
        for (int j = 0; j < K; j++) {
            u_hat[0][j] = u_hat[T-1][j].conjugate();
        }

        double[][] u = new double[K][T];
        double[][] ou = new double[K][T/2];

        for (int k = 0; k < K; k++) {
            Complex[] u_h = new Complex[T];
            for (int i = 0; i < T; i++) {
                u_h[i] = u_hat[i][k];
            }
            Complex[] shifted = ifftshift(u_h);   // 对第 k 列进行循环移位
            Complex[] ifftResult = refft(shifted);  // 执行逆傅里叶变换

            for (int i = 0; i < T; i++) {
                u[k][i] = ifftResult[i].getReal();   // 取实部并赋值给 u 数组
            }
            for (int i = 0; i < T / 2; i++) {
                ou[k][i] =u[k][(int) (Math.ceil( (double)T / 4 ) + i)];
            }
        }

        Complex[][] u_hat_out = new Complex[T/2][K];
        double[] iu = new double[T/2];
        for (int k = 0; k < K; k++) {
            for (int i = 0; i < T/2; i++) {
                iu[i]=ou[k][i];
            }

            Complex[] fftResult=fft(iu);// 执行正向傅里叶变换
            Complex[] shifted = fftShift(fftResult);     // 对傅里叶变换结果进行移位
            for (int i = 0; i <T/2; i++) {
                u_hat_out[i][k] = shifted[i]; // 存储到 u_hat 数组中
            }
        }

        //输出子序列
        double[][] vmd_output =new double[K][save_T];
        for (int i = 0; i < K; i++) {
            System.arraycopy(ou[i], 0, vmd_output[i], 0, save_T);
        }

        //判断所有列是否全部为0
        boolean isAllZero = true;
        int row = 0; // 要判断的行索引
        for (int col = 0; col < vmd_output[row].length; col++) {
            if (vmd_output[row][col] != 0.0) {
                isAllZero = false;
                break; // 如果有一个元素不为0，则可以跳出循环
            }
        }
        if (isAllZero) {
            for (int i = 0; i < vmd_output.length ; i++) {
                for (int col = 0; col < vmd_output[row].length; col++) {
                    vmd_output[i][col] = signal[col]/K;
                }
            }

        }
        if (K == 1){
            System.arraycopy(signal, 0, vmd_output[0], 0, signal.length);
        }
        return vmd_output;
    }

    // 定义一个用于实现 FFT 移位的方法
    private static Complex[] fftShift(Complex[] input) {
        int n = input.length;
        int k = (n + 1) / 2;
        Complex[] output = new Complex[n];
        System.arraycopy(input, k, output, 0, n - k);
        System.arraycopy(input, 0, output, n - k, k);
        return output;
    }
    // 对数组进行循环移位操作
    public static Complex[] ifftshift(Complex[] array) {
        int n = array.length;
        int shift = (n + 1) / 2;
        Complex[] shifted = new Complex[n];
        System.arraycopy(array, shift, shifted, 0, n - shift);
        System.arraycopy(array, 0, shifted, n - shift, shift);

        return shifted;
    }
    private static int computeNextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
    private static Complex[] fft (double[] in){
        DoubleFFT_1D fft = new DoubleFFT_1D(in.length);
        double[] out = new double[2 * in.length];
        // 将输入数组复制到输出数组的前 n 个元素中
        for(int i = 0; i < out.length; i += 2){
            out[i] = in[i/2];
        }
        // 对输出数组应用 Bluestein's 算法
        fft.complexForward(out);
        Complex[] fftResult=new Complex[in.length];
        for (int i = 0; i < in.length; i++) {
            double real=out[2*i];
            double imag=out[2*i+1];
            fftResult[i]= new Complex(real, imag);
        }
        return fftResult;
    }
    private static Complex[] refft (Complex[] in){

        DoubleFFT_1D fftn = new DoubleFFT_1D(in.length);
        double [] ifftResult_double=new double[2* in.length];
        for (int i = 0; i < in.length; i++) {
            ifftResult_double[2*i]=in[i].getReal();
            ifftResult_double[2*i+1]=in[i].getImaginary();
        }
        fftn.complexInverse(ifftResult_double, true);
        Complex[] ifftResult = new Complex[in.length];   // double转为complex类型
        for (int i = 0; i < in.length; i++) {
            double real=ifftResult_double[2*i];
            double imag=ifftResult_double[2*i+1];
            ifftResult[i]= new Complex(real, imag);
        }
        return  ifftResult;
    }
    public static double[][] matrixMultiplication(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }
    public static Complex[][] complexMatrixMultiplication(Complex[][] matrix1, Complex[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int cols2 = matrix2[0].length;

        Complex[][] result = new Complex[rows1][cols2];

        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                Complex sum = Complex.ZERO;

                for (int k = 0; k < cols1; k++) {
                    Complex product = matrix1[i][k].multiply(matrix2[k][j]);
                    sum = sum.add(product);
                }
                result[i][j] = sum;
            }
        }

        return result;
    }
}