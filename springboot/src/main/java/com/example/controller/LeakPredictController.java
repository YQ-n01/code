package com.example.controller;

import com.example.dto.LeakResultDTO;
import com.example.service.LeakPredictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/leak")
public class LeakPredictController {

    private final LeakPredictService leakPredictService;

    public LeakPredictController(LeakPredictService leakPredictService) {
        this.leakPredictService = leakPredictService;
    }

    @PostMapping("/predict")
    public ResponseEntity<LeakResultDTO> predictLeak(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("fs") String fs,
                                                     @RequestParam("outputFolder") String outputFolder) {
        try {
            double sampleRate = Double.parseDouble(fs);

            File tempInput = Files.createTempFile("leak_input_", ".wav").toFile();
            file.transferTo(tempInput);

            // 🔧 调用 MATLAB
            LeakResultDTO result = leakPredictService.predictLeak(
                    tempInput.getAbsolutePath(),
                    sampleRate,
                    outputFolder,
                    ""
            );

            // 🔍 统计 outputFolder 下的 spectrogram 图片数量
            File folder = new File(outputFolder);
            File[] images = folder.listFiles((dir, name) -> name.startsWith("spectrogram_") && name.endsWith(".png"));
            int latestIndex = images != null ? images.length : 0;

            // 🌐 拼接图片访问地址（假设后端映射了静态路径）
            String imageUrl = "http://localhost:9999/spectrogram_" + latestIndex + ".png";
            result.setImageUrl(imageUrl);
            System.out.println("🖼️ 返回图片地址: " + imageUrl);


            return ResponseEntity.ok(result);

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new LeakResultDTO("InvalidInput", 0.0));
        }
    }
}
