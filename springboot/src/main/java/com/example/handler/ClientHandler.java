package com.example.handler;

import com.example.processor.PacketProcessor;
import com.example.utils.ByteUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private static final int MAX_PACKET_SIZE = 1514;

    // ✅ 固定包头（推荐写死或者从 ByteUtils 引入）
    private static final byte[] PACKET_HEADER = ByteUtils.PACKET_HEADER;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("📡 客户端连接：" + socket.getInetAddress());

        String filePrefix = createTimestampedFilePrefix();
        System.out.println("📁 当前数据路径前缀：" + filePrefix);

        File rawFilePath = Paths.get(filePrefix + "_raw_data.txt").toFile();
        File paramFilePath = Paths.get(filePrefix + "_parameters.txt").toFile();
        File waveformFilePath = Paths.get(filePrefix + "_waveform_data.txt").toFile();

        try (
                InputStream input = socket.getInputStream();
                DataInputStream dataInput = new DataInputStream(input);
                BufferedWriter rawFile = new BufferedWriter(new FileWriter(rawFilePath, true));
                BufferedWriter paramFile = new BufferedWriter(new FileWriter(paramFilePath, true));
                BufferedWriter waveformFile = new BufferedWriter(new FileWriter(waveformFilePath, true))
        ) {
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            ByteArrayOutputStream packetBuffer = new ByteArrayOutputStream();
            boolean waitingForFullPacket = false;
            int expectedPacketSize = 0;

            int bytesRead;
            while ((bytesRead = dataInput.read(buffer)) != -1) {
                packetBuffer.write(buffer, 0, bytesRead);
                byte[] allData = packetBuffer.toByteArray();

                while (allData.length >= 16) {
                    if (!waitingForFullPacket) {
                        int headerIndex = ByteUtils.findPacketHeader(allData, PACKET_HEADER);
                        if (headerIndex == -1) break;

                        if (allData.length >= headerIndex + 16) {
                            expectedPacketSize = ByteUtils.parsePacketLength(allData, headerIndex);
                            waitingForFullPacket = true;
                        } else break;

                        packetBuffer.reset();
                        packetBuffer.write(allData, headerIndex, allData.length - headerIndex);
                        allData = packetBuffer.toByteArray();
                    }

                    if (waitingForFullPacket && allData.length >= expectedPacketSize) {
                        byte[] completePacket = Arrays.copyOfRange(allData, 0, expectedPacketSize);

                        try {
                            PacketProcessor.processAndStorePacket(rawFile, paramFile, waveformFile, completePacket);
                        } catch (Exception ex) {
                            System.err.println("❌ 包处理异常: " + ex.getMessage());
                            ex.printStackTrace();
                        }

                        packetBuffer.reset();
                        packetBuffer.write(allData, expectedPacketSize, allData.length - expectedPacketSize);
                        allData = packetBuffer.toByteArray();
                        waitingForFullPacket = false;
                    } else {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("⚠️ 客户端数据处理异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("⛔ 客户端断开：" + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("❌ 关闭连接异常：" + e.getMessage());
            }
        }
    }

    // ✅ 每次运行都创建新路径
    private String createTimestampedFilePrefix() {
        LocalDate nowDate = LocalDate.now();
        String dateStr = nowDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss-SSS"));
        String dir = "data" + File.separator + dateStr;

        try {
            Files.createDirectories(Paths.get(dir));
            System.out.println("📂 创建目录成功：" + dir);
        } catch (IOException e) {
            System.out.println("⚠️ 创建数据目录失败：" + dir);
        }

        return dir + File.separator + dateStr + "_" + timeStr;
    }

}
