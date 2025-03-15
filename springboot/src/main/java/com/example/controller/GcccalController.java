package com.example.controller;

import com.example.dto.GccProcessingResult;
import com.example.service.GcccalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/gcccal")
@CrossOrigin(origins = "*") // 允许跨域请求
public class GcccalController {

    private final GcccalService gcccalService;

    public GcccalController(GcccalService gcccalService) {
        this.gcccalService = gcccalService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processAudio(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("file3") MultipartFile file3,
            @RequestParam("file4") MultipartFile file4,
            @RequestParam("fs") double fs) {

        if (file1.isEmpty() || file2.isEmpty() || file3.isEmpty() || file4.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 文件上传失败：部分文件为空");
        }

        System.out.println("✅ 收到文件1: " + file1.getOriginalFilename());
        System.out.println("✅ 收到文件2: " + file2.getOriginalFilename());
        System.out.println("✅ 收到文件3: " + file3.getOriginalFilename());
        System.out.println("✅ 收到文件4: " + file4.getOriginalFilename());
        System.out.println("📢 采样率 fs: " + fs);

        File tempFile1 = null;
        File tempFile2 = null;
        File tempFile3 = null;
        File tempFile4 = null;

        try {
            tempFile1 = Files.createTempFile("audio1_", ".wav").toFile();
            tempFile2 = Files.createTempFile("audio2_", ".wav").toFile();
            tempFile3 = Files.createTempFile("audio3_", ".wav").toFile();
            tempFile4 = Files.createTempFile("audio4_", ".wav").toFile();

            Files.copy(file1.getInputStream(), tempFile1.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file2.getInputStream(), tempFile2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file3.getInputStream(), tempFile3.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(file4.getInputStream(), tempFile4.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // ✅ 调用 `GcccalService`
            GccProcessingResult result = gcccalService.processAudio(
                    tempFile1.getAbsolutePath(),
                    tempFile2.getAbsolutePath(),
                    tempFile3.getAbsolutePath(),
                    tempFile4.getAbsolutePath(),
                    fs
            );

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ 服务器错误：" + e.getMessage());
        } finally {
            // ✅ 删除临时文件
            if (tempFile1 != null) tempFile1.delete();
            if (tempFile2 != null) tempFile2.delete();
            if (tempFile3 != null) tempFile3.delete();
            if (tempFile4 != null) tempFile4.delete();
        }
    }
}
