package com.example.controller;

import com.example.service.TcpService;
import com.example.model.ParameterData;
import com.example.storage.ParamStorage;
import com.example.internal.FpgaSignalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/signal")
public class SignalController {

    private final TcpService tcpService;

    @Autowired
    public SignalController(TcpService tcpService) {
        this.tcpService = tcpService;
    }

    @PostMapping("/startAll")
    public String startAll() {
        tcpService.startAllCollections();
        return "✅ 已触发全部采集任务";
    }

    @PostMapping("/stop")
    public String stop() {
        tcpService.stopCollection();
        return "⛔ 已停止采集任务";
    }

    @GetMapping("/params/latest")
    public ParameterData getLatest(@RequestParam String ip) {
        return ParamStorage.getLatestByIp(ip);
    }

    @GetMapping("/params/all")
    public List<ParameterData> getAllParams() {
        return ParamStorage.getAll();
    }

    @DeleteMapping("/params/clear")
    public String clearParams() {
        ParamStorage.clear();
        return "✅ 已清除所有参数记录";
    }

    // ✅ 新增：设置采集参数接口
    @PostMapping("/setParams")
    public String setParams(@RequestBody Map<String, String> body) {
        String ip = body.get("ip");
        String paramStr = body.get("params");

        if (ip == null || paramStr == null) {
            return "❌ 参数格式错误，缺少 ip 或 params";
        }

        Map<String, String> paramMap = parseParamsToMap(paramStr);

        // 调用 FPGA 配置方法（你要确保 FpgaSignalHelper 有这个方法）
        FpgaSignalHelper.setSystemParameters(ip, paramMap);

        return "✅ 已发送参数设置请求到 " + ip;
    }

    // 工具方法：将 "key=value&key2=value2" 字符串解析成 Map
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
