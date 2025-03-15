package com.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import com.example.service.MatlabService;
import com.example.dto.AudioProcessingResult;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final MatlabService matlabService;

    public AudioController(MatlabService matlabService) {
        this.matlabService = matlabService;
    }

    @PostMapping("/process")
    public AudioProcessingResult processAudio(@RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2,
                                              @RequestParam("fs") String fs) {
        try {
            double sampleRate = Double.parseDouble(fs);

            // ✅ 保存 `.wav` 文件到临时目录
            File tempFile1 = Files.createTempFile("audio1_", ".wav").toFile();
            File tempFile2 = Files.createTempFile("audio2_", ".wav").toFile();
            file1.transferTo(tempFile1);
            file2.transferTo(tempFile2);

            return matlabService.processAudio(tempFile1.getAbsolutePath(), tempFile2.getAbsolutePath(), sampleRate);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return new AudioProcessingResult(null, null, null, null, null);
        }
    }

}
