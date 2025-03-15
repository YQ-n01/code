package com.example.matlab;

import pdcal.pdcal; // MATLAB 生成的 Java 类

public class PdcalWrapper {
    private pdcal matlabFunction;  // MATLAB 计算对象

    public PdcalWrapper() {
        try {
            matlabFunction = new pdcal();  // 初始化 MATLAB 计算类
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object[] compute(String filePath1, String filePath2, double fs) {
        try {

            Object[] result = new Object[5]; // MATLAB 需要返回 5 个值
            Object[] inputs = new Object[] {filePath1, filePath2, fs}; // 传递参数

            matlabFunction.pdtest(result, inputs); // 调用 MATLAB 计算

            return result; // 返回 MATLAB 计算结果
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
