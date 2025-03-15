package com.example.service;

import com.example.dto.AudioProcessingResult;
import com.example.matlab.PdcalWrapper;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWArray;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class MatlabService {
    private final PdcalWrapper pdcalWrapper;

    public MatlabService() {
        this.pdcalWrapper = new PdcalWrapper();
    }

    public AudioProcessingResult processAudio(String filePath1, String filePath2, double fs) {
        Object[] matlabResults = null; // ✅ 在 try 代码块外部声明变量

        try {
            System.out.println("📂 传递给 MATLAB 的参数：");
            System.out.println("✅ 文件1: " + filePath1);
            System.out.println("✅ 文件2: " + filePath2);
            System.out.println("📢 采样率 fs: " + fs);

            // ✅ 通过 pdcalWrapper 调用 MATLAB 计算
            matlabResults = pdcalWrapper.compute(filePath1, filePath2, fs);

            // ✅ 确保 MATLAB 计算成功
            if (matlabResults == null || matlabResults.length < 5) {
                System.err.println("❌ MATLAB 计算返回了空值！");
                return new AudioProcessingResult(null, null, null, null, null);
            }

            // ✅ 解析 MATLAB 计算结果
            double[] F = extractColumnVector((MWNumericArray) matlabResults[0], "F");
            double[] phaseSpectrum1 = extractColumnVector((MWNumericArray) matlabResults[1], "phaseSpectrum1");
            double[] phaseSpectrum2 = extractColumnVector((MWNumericArray) matlabResults[2], "phaseSpectrum2");
            double[] phaseDifference = extractColumnVector((MWNumericArray) matlabResults[3], "phaseDifference");
            double[][] filteredSegments = extractMatrix((MWNumericArray) matlabResults[4], "filteredSegments");

            return new AudioProcessingResult(F, phaseSpectrum1, phaseSpectrum2, phaseDifference, filteredSegments);
        } catch (Exception e) {
            e.printStackTrace();
            return new AudioProcessingResult(null, null, null, null, null);
        } finally {
            // ✅ 释放 MATLAB 资源
            if (matlabResults != null) {
                for (Object result : matlabResults) {
                    MWArray.disposeArray(result);
                }
            }
        }
    }

    // ✅ 解析 Nx1 MATLAB 矩阵，转换为 double[]
    private double[] extractColumnVector(MWNumericArray array, String varName) {
        if (array == null || array.isEmpty()) {
            System.out.println("❌ MATLAB 返回的 " + varName + " 为空！");
            return new double[0];
        }

        int[] dimensions = array.getDimensions();
        System.out.println("✅ MATLAB 传回的 " + varName + " 维度: " + Arrays.toString(dimensions));

        if (dimensions.length == 2 && dimensions[1] == 1) { // Nx1 解析
            double[][] matrix = (double[][]) array.toDoubleArray();
            double[] columnVector = new double[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                columnVector[i] = matrix[i][0];
            }
            return columnVector;
        } else { // 直接转换
            return array.getDoubleData();
        }
    }

    // ✅ 解析 MATLAB 返回的 2D 矩阵
    private double[][] extractMatrix(MWNumericArray array, String varName) {
        if (array == null || array.isEmpty()) {
            System.out.println("❌ MATLAB 返回的 " + varName + " 为空！");
            return new double[0][0];
        }

        int[] dimensions = array.getDimensions();
        System.out.println("✅ MATLAB 传回的 " + varName + " 维度: " + Arrays.toString(dimensions));

        return (double[][]) array.toDoubleArray();
    }
}
