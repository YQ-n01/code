package com.example.service;

import com.example.handler.ClientHandler;
import com.example.internal.FpgaSignalHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;

@Service
public class TcpService {

    private static final int PORT = 300;
    private ServerSocket serverSocket;

    // ✅ 采集中状态标记
    private volatile boolean collecting = false;

    // ✅ 当前 FPGA 设备的 IP 地址
    private String currentFpgaIp;

    // ✅ 启动采集逻辑
    public void startCollection(String fpgaIp) {
        if (collecting) {
            System.out.println("⚠️ 当前已有采集任务在运行，不能重复启动");
            return;
        }

        collecting = true;
        currentFpgaIp = fpgaIp;

        new Thread(() -> {
            try {
                // 发送开始命令
                FpgaSignalHelper.startSignalCollection(currentFpgaIp);
                System.out.println("✅ 已发送 FPGA 启动采集命令");

                // 启动 TCP server
                serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName("192.168.0.20"));
                serverSocket.setSoTimeout(200000); // 最多等待 20 秒连接
                System.out.println("🚀 TCP 服务监听端口：" + PORT);

                while (collecting) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("📡 接收到客户端连接：" + socket.getInetAddress());

                        new Thread(new ClientHandler(socket)).start();
                    } catch (SocketTimeoutException e) {
                        System.out.println("⏰ 20 秒内未连接客户端，自动停止采集");
                        stopCollection();
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("⚠️ 采集服务异常：" + e.getMessage());
                stopCollection();
            }
        }).start();
    }

    // ✅ 停止采集
    public void stopCollection() {
        if (!collecting) {
            System.out.println("ℹ️ 当前没有正在运行的采集任务");
            return;
        }

        collecting = false;

        try {
            FpgaSignalHelper.stopSignalCollection(currentFpgaIp);
            System.out.println("⛔ 已发送 FPGA 停止采集命令");

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("✅ TCP 服务已关闭");
            }
        } catch (IOException e) {
            System.out.println("⚠️ 停止采集失败：" + e.getMessage());
        }
    }

    // ✅ 提供当前状态
    public boolean isCollecting() {
        return collecting;
    }

}
