package com.example.matlab;

import preLeakcal.predictLeaktest;


public class LeakPredictWrapper {
    private predictLeaktest matlabFunction;  // MATLAB 计算对象

    public LeakPredictWrapper() {
        try {
            matlabFunction = new predictLeaktest();  // 初始化 MATLAB 计算类
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object[] compute(String filePath1, double fs, String outPath, String modelPath) {
        try {

            Object[] result = new Object[2]; // MATLAB 需要返回 2 个值
            Object[] inputs = new Object[] {filePath1, fs, outPath,""}; // 传递参数

            matlabFunction.predictLeaktest(result, inputs); // 调用 MATLAB 计算

            return result; // 返回 MATLAB 计算结果
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
