package com.example.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WaveformStorage {

    // 🌟 按 IP 保存波形点缓存
    private static final Map<String, List<Double>> cacheMap = new ConcurrentHashMap<>();
    private static final int THRESHOLD = 2000;

    private static SimpMessagingTemplate messaging;

    @Autowired
    public void setMessaging(SimpMessagingTemplate template) {
        messaging = template;
    }

    // ✅ 按 IP 添加波形点
    public static void addPoints(String sourceIp, List<Double> points) {
        cacheMap.computeIfAbsent(sourceIp, k -> Collections.synchronizedList(new ArrayList<>())).addAll(points);

        List<Double> cache = cacheMap.get(sourceIp);
        if (cache.size() >= THRESHOLD) {
            List<Double> toSend = new ArrayList<>(cache);
            String sanitizedIp = sourceIp.replace(".", "_");

            messaging.convertAndSend("/topic/waveform/" + sanitizedIp, toSend);
            cache.clear();

            System.out.println("📤 [" + sourceIp + "] 推送波形数据，共 " + toSend.size() + " 点");
        }
    }
}
