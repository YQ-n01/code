package com.example.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

@Component
public class WaveformStorage {

    private static final List<Double> cache = new CopyOnWriteArrayList<>();
    private static final int THRESHOLD = 2000;

    private static SimpMessagingTemplate messaging;

    @Autowired
    public void setMessaging(SimpMessagingTemplate template) {
        messaging = template;
    }

    public static void addPoints(List<Double> points) {
        cache.addAll(points);

        if (cache.size() >= THRESHOLD) {
            List<Double> toSend = new ArrayList<>(cache);
            messaging.convertAndSend("/topic/waveform", toSend);
            cache.clear();
        }
    }
}
