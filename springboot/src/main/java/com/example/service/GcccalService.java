package com.example.service;

import com.example.dto.GccProcessingResult;
import com.example.matlab.gcccalWrapper;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import org.springframework.stereotype.Service;

@Service
public class GcccalService {
    private final gcccalWrapper gcccalWrapper;

    public GcccalService() {
        this.gcccalWrapper = new gcccalWrapper();
    }

    public GccProcessingResult processAudio(String filePath1, String filePath2, String filePath3, String filePath4, double fs) {
        System.out.println("📂 传递给 MATLAB 的参数：");
        System.out.println("✅ 文件1: " + filePath1);
        System.out.println("✅ 文件2: " + filePath2);
        System.out.println("✅ 文件3: " + filePath3);
        System.out.println("✅ 文件4: " + filePath4);
        System.out.println("📢 采样率 fs: " + fs);

        // ✅ 调用 MATLAB 计算
        Object[] matlabResults = gcccalWrapper.compute(filePath1, filePath2, filePath3, filePath4, fs);

        // ✅ 确保 MATLAB 计算返回值不为空
        if (matlabResults == null || matlabResults.length < 2) {
            System.err.println("❌ MATLAB 计算返回了空值！");
            return new GccProcessingResult(null, null);
        }

        try {
            // ✅ 解析 MATLAB 返回的 `MWNumericArray`
            MWNumericArray resultArray1 = (MWNumericArray) matlabResults[0];
            MWNumericArray resultArray2 = (MWNumericArray) matlabResults[1];

            // ✅ 确保 MATLAB 返回的 `MWNumericArray` 不为空
            if (resultArray1 == null || resultArray2 == null) {
                System.err.println("❌ MATLAB 计算返回了 NULL 结果！");
                return new GccProcessingResult(null, null);
            }

            // ✅ 正确解析 `MWNumericArray` 为 `double[]`
            double[] gccResult1 = resultArray1.getDoubleData();
            double[] gccResult2 = resultArray2.getDoubleData();

            System.out.println("✅ MATLAB 计算成功，返回值如下：");
            System.out.println("🔢 计算结果 1: " + java.util.Arrays.toString(gccResult1));
            System.out.println("🔢 计算结果 2: " + java.util.Arrays.toString(gccResult2));

            return new GccProcessingResult(gccResult1, gccResult2);

        } catch (ClassCastException e) {
            e.printStackTrace();
            return new GccProcessingResult(null, null);
        } finally {
            // ✅ 释放 MATLAB 计算资源
            if (matlabResults[0] instanceof MWNumericArray) {
                MWNumericArray.disposeArray(matlabResults[0]);
            }
            if (matlabResults[1] instanceof MWNumericArray) {
                MWNumericArray.disposeArray(matlabResults[1]);
            }
        }
    }
}
