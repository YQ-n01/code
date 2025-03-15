package com.example.matlab;

import gcccal.gcccal; // MATLAB 生成的 Java 类

public class gcccalWrapper {
    private gcccal matlabFunction;  // MATLAB 计算对象

    public gcccalWrapper() {
        try {
            matlabFunction = new gcccal();  // 初始化 MATLAB 计算类
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object[] compute(String filePath1, String filePath2,String filePath3, String filePath4, double fs) {
        try {
            System.out.println("📂 传递给 MATLAB 的参数：");
            System.out.println("✅ 文件1: " + filePath1);
            System.out.println("✅ 文件2: " + filePath2);
            System.out.println("📢 采样率 fs: " + fs);

            Object[] result = new Object[2]; // MATLAB 需要返回 5 个值
            Object[] inputs = new Object[] {filePath1, filePath2, filePath3, filePath4, fs}; // 传递参数

            matlabFunction.GCCtest(result, inputs); // 调用 MATLAB 计算

            return result; // 返回 MATLAB 计算结果
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
