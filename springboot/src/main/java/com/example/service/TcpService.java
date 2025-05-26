package com.example.service;

import com.example.config.SensorProperties;
import com.example.handler.ClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class TcpService {

    private final SensorProperties sensorProperties;

    @Autowired
    public TcpService(SensorProperties sensorProperties) {
        this.sensorProperties = sensorProperties;
    }

    private static final int PORT = 300;
    private volatile boolean collecting = false;

    public void startAllCollections() {
        if (collecting) {
            System.out.println("⚠️ 已有采集任务运行中");
            return;
        }

        collecting = true;
        for (String ip : sensorProperties.getIps()) {
            startCollection(ip);
        }
    }

    public void startCollection(String ip) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ip))) {
                System.out.println("🚀 监听本地网卡 " + ip + ":" + PORT);

                while (collecting) {
                    try {
                        Socket socket = serverSocket.accept();
                        String sensorIp = socket.getInetAddress().getHostAddress();
                        System.out.println("📡 接收到传感器 [" + sensorIp + "] 连接");
                        new Thread(new ClientHandler(socket, sensorIp)).start();
                    } catch (IOException e) {
                        System.out.println("⚠️ [" + ip + "] 接收失败：" + e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.out.println("⚠️ [" + ip + "] 监听失败：" + e.getMessage());
            }
        }).start();
    }

    public void stopCollection() {
        if (!collecting) {
            System.out.println("ℹ️ 没有采集任务在运行");
            return;
        }

        collecting = false;
        System.out.println("✅ 已请求停止所有采集任务");
    }

    public boolean isCollecting() {
        return collecting;
    }
}
