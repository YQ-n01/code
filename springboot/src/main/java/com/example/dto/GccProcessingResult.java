package com.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Arrays;

@Getter
@Setter
@ToString
public class GccProcessingResult {
    private double[] gccResult1;
    private double[] gccResult2;

    // ✅ 默认构造方法，确保 Spring Boot 反序列化 JSON 时不会报错
    public GccProcessingResult() {
    }

    // ✅ 带参数的构造方法
    public GccProcessingResult(double[] gccResult1, double[] gccResult2) {
        this.gccResult1 = gccResult1;
        this.gccResult2 = gccResult2;
    }

    // ✅ 重写 `toString()` 方法，方便日志输出
    @Override
    public String toString() {
        return "GccProcessingResult{" +
                "gccResult1=" + Arrays.toString(gccResult1) +
                ", gccResult2=" + Arrays.toString(gccResult2) +
                '}';
    }
}
