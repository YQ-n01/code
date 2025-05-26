package com.example.storage;

import com.example.model.ParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ParamStorage {

    // 🌟 改造为：按 IP 存储参数列表
    private static final Map<String, List<ParameterData>> paramMap = new ConcurrentHashMap<>();

    private static SimpMessagingTemplate messaging;

    @Autowired
    public void setMessaging(SimpMessagingTemplate template) {
        messaging = template;
    }

    // ✅ 按 IP 存储并推送
    public static void addParam(String ip, ParameterData data) {
        paramMap.computeIfAbsent(ip, k -> Collections.synchronizedList(new ArrayList<>())).add(data);
        System.out.println("📦 [" + ip + "] 参数已加入缓存：" + data.timestamp + " AMP=" + data.amp);

        if (messaging != null) {
            // 推送到 /topic/params/{ip}
            messaging.convertAndSend("/topic/params/" + ip.replace(".", "_"), data);
            System.out.println("📤 [" + ip + "] 已推送参数到前端：" + data.timestamp);
        } else {
            System.out.println("❌ messaging is null，WebSocket 推送失败！");
        }
    }

    // ✅ 获取指定 IP 最新参数
    public static ParameterData getLatestByIp(String ip) {
        List<ParameterData> list = paramMap.get(ip);
        if (list == null || list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    // ✅ 获取所有历史参数（按 IP 分组返回）
    public static Map<String, List<ParameterData>> getAllByIp() {
        return Collections.unmodifiableMap(paramMap);
    }

    // ✅ 获取所有参数（平铺合并）
    public static List<ParameterData> getAll() {
        List<ParameterData> all = new ArrayList<>();
        for (List<ParameterData> list : paramMap.values()) {
            all.addAll(list);
        }
        return all;
    }

    // ✅ 清空所有
    public static void clear() {
        paramMap.clear();
    }
}
