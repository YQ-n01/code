package com.example.controller;

import com.example.internal.FpgaSignalHelper;
import com.example.model.ParameterData;
import com.example.service.TcpService;
import com.example.storage.ParamStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/signal")
public class SignalController {

    private final TcpService tcpService;

    @Autowired
    public SignalController(TcpService tcpService) {
        this.tcpService = tcpService;
    }

    // ✅ 启动采集
    @PostMapping("/start")
    public String start(@RequestBody Map<String, String> body) {
        String ipSuffix = body.get("ip");
        if (ipSuffix == null || ipSuffix.trim().isEmpty()) {
            return "❌ IP 后缀不能为空";
        }

        String fullIp = ipSuffix.trim();
        tcpService.startCollection(fullIp);
        return "✅ 已触发采集，目标 FPGA IP: " + fullIp;
    }

    // ✅ 停止采集
    @PostMapping("/stop")
    public String stop() {
        tcpService.stopCollection();
        return "⛔ 已请求停止采集";
    }

    // ✅ 获取最新一条参数数据（用于前端实时展示）
    @GetMapping("/params/latest")
    public ParameterData getLatest() {
        return ParamStorage.getLatest();
    }

    // ✅ 获取所有历史参数（用于表格、分析）
    @GetMapping("/params/all")
    public List<ParameterData> getAllParams() {
        return ParamStorage.getAll();
    }

    // ✅ 清除后台所有参数记录
    @DeleteMapping("/params/clear")
    public String clearParams() {
        ParamStorage.clear();
        return "✅ 所有参数记录已清除";
    }

    // ✅ 设置采集参数
    @PostMapping("/setParams")
    public String setParams(@RequestBody Map<String, String> body) {
        String ipSuffix = body.get("ip");
        String paramStr = body.get("params");

        if (ipSuffix == null || paramStr == null) {
            return "❌ 参数格式错误，缺少 ip 或 params";
        }

        String fullIp = ipSuffix.trim();
        Map<String, String> paramMap = parseParamsToMap(paramStr);

        FpgaSignalHelper.setSystemParameters(fullIp, paramMap);
        return "✅ 已发送参数设置请求到 " + fullIp;
    }

    // ✅ 工具方法：字符串转 Map
    private Map<String, String> parseParamsToMap(String paramStr) {
        Map<String, String> map = new HashMap<>();
        if (paramStr == null || paramStr.isEmpty()) return map;

        String[] pairs = paramStr.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
