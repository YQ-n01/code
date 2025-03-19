package com.example.storage;

import com.example.model.ParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Component
public class ParamStorage {

    private static final List<ParameterData> all = new CopyOnWriteArrayList<>();

    private static SimpMessagingTemplate messaging;

    @Autowired
    public void setMessaging(SimpMessagingTemplate template) {
        messaging = template;
    }

    public static void set(ParameterData data) {
        all.add(data);
        System.out.println("📦 参数已加入缓存：" + data.timestamp + " AMP=" + data.amp);

        if (messaging != null) {
            messaging.convertAndSend("/topic/params", data);
            System.out.println("📤 已推送参数到前端：" + data.timestamp);
        } else {
            System.out.println("❌ messaging is null，WebSocket 推送失败！");
        }
    }

    public static ParameterData getLatest() {
        return all.isEmpty() ? null : all.get(all.size() - 1);
    }

    public static List<ParameterData> getAll() {
        return all;
    }


    public static void clear() {
        all.clear();
    }
}
