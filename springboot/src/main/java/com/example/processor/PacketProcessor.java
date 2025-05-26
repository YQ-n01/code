package com.example.processor;

import com.example.model.ParameterData;
import com.example.storage.ParamStorage;
import com.example.storage.WaveformStorage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketProcessor {

    public static void processAndStorePacket(String sourceIp, BufferedWriter rawFile, BufferedWriter paramFile,
                                             BufferedWriter waveformFile, byte[] packet) throws IOException {
        String hexData = bytesToHex(packet);
        rawFile.write(hexData);
        rawFile.newLine();
        rawFile.newLine();
        rawFile.flush();

        int typeByte = packet[8] & 0xFF;
        if (typeByte == 0x00) {
            String parsedParams = parseParameterPacket(sourceIp, packet);
            paramFile.write(parsedParams);
            paramFile.newLine();
            paramFile.flush();
            System.out.println("📥 [" + sourceIp + "] 存储参数文件");
        } else if (typeByte == 0x01) {
            processWaveformData(sourceIp, waveformFile, packet);
            System.out.println("📥 [" + sourceIp + "] 存储波形数据");
        }
    }

    private static void processWaveformData(String sourceIp, BufferedWriter waveformFile, byte[] packet) throws IOException {
        if (packet.length <= 48) return;
        byte[] waveformData = Arrays.copyOfRange(packet, 48, packet.length);

        double scale = 1.0;
        StringBuilder line = new StringBuilder();
        ByteBuffer byteBuffer = ByteBuffer.wrap(waveformData).order(ByteOrder.LITTLE_ENDIAN);

        List<Double> points = new ArrayList<>();

        while (byteBuffer.remaining() >= 2) {
            short value = byteBuffer.getShort();
            double voltage = value * scale / 30000.0 / 10.0;
            voltage = round6(voltage);
            points.add(voltage);
            line.append(String.format("%.6f ", voltage));
        }

        waveformFile.write(line.toString().trim());
        waveformFile.newLine();
        waveformFile.flush();

        // ✅ 按 IP 存入缓存和推送
        WaveformStorage.addPoints(sourceIp, points);
    }

    private static String parseParameterPacket(String sourceIp, byte[] packet) {
        if (packet.length < 76) return "⚠️ 参数包长度不足 76 字节，解析失败！";

        ByteBuffer buffer = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(4);

        byte[] deviceIdBytes = new byte[4];
        buffer.get(deviceIdBytes);
        String deviceId = new String(deviceIdBytes).trim();

        buffer.getInt(); // dataType
        buffer.getInt(); // dataLength
        buffer.getInt(); // version

        long seconds = Integer.toUnsignedLong(buffer.getInt());
        long nanos = Integer.toUnsignedLong(buffer.getInt());

        double amp = buffer.getDouble();
        double power = buffer.getDouble();
        double rms = buffer.getDouble();
        double asl = buffer.getDouble();

        amp = round3(amp);
        power = round3(power);
        rms = round3(rms);
        asl = round3(asl);

        long tr = Integer.toUnsignedLong(buffer.getInt());
        long riseCount = Integer.toUnsignedLong(buffer.getInt());
        long duration = Integer.toUnsignedLong(buffer.getInt());
        long ringCount = Integer.toUnsignedLong(buffer.getInt());

        String formattedTime = formatEpochToTime(seconds, nanos);

        ParameterData param = new ParameterData();
        param.timestamp = formattedTime;
        param.amp = amp;
        param.power = power;
        param.rms = rms;
        param.asl = asl;
        param.tr = tr;
        param.riseCount = riseCount;
        param.duration = duration;
        param.ringCount = ringCount;

        ParamStorage.addParam(sourceIp, param);

        return String.format(
                "到达时间: %s, AMP: %.3f, Power: %.3f, RMS: %.3f, ASL: %.3f, tr: %d, 上升计数: %d, 持续时间: %d, 振铃计数: %d",
                formattedTime, amp, power, rms, asl, tr, riseCount, duration, ringCount
        );
    }

    private static String formatEpochToTime(long seconds, long nanos) {
        long millis = seconds * 1000 + nanos / 1_000_000;
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime time = instant.atZone(ZoneId.of("Asia/Shanghai"));
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X ", b));
        }
        return hexString.toString().trim();
    }

    private static double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private static double round6(double value) {
        return Math.round(value * 1_000_000.0) / 1_000_000.0;
    }
}
