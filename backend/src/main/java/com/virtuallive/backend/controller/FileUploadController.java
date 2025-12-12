package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 * 支持图片、视频、音频等多媒体文件上传
 */
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class FileUploadController {
    
    private final FileStorageService fileStorageService;
    
    /**
     * 上传单个图片
     */
    @PostMapping("/image")
    public R<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileStorageService.storeImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("filename", file.getOriginalFilename());
            return R.ok("图片上传成功", result);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return R.error("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传多个图片
     */
    @PostMapping("/images")
    public R<List<Map<String, String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<Map<String, String>> results = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = fileStorageService.storeImage(file);
                Map<String, String> result = new HashMap<>();
                result.put("url", imageUrl);
                result.put("filename", file.getOriginalFilename());
                results.add(result);
            }
            return R.ok("图片上传成功", results);
        } catch (Exception e) {
            log.error("图片批量上传失败", e);
            return R.error("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传视频
     */
    @PostMapping("/video")
    public R<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = fileStorageService.storeVideo(file);
            return R.ok("视频上传成功", result);
        } catch (Exception e) {
            log.error("视频上传失败", e);
            return R.error("视频上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传音频
     */
    @PostMapping("/audio")
    public R<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            String audioUrl = fileStorageService.storeAudio(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", audioUrl);
            result.put("filename", file.getOriginalFilename());
            return R.ok("音频上传成功", result);
        } catch (Exception e) {
            log.error("音频上传失败", e);
            return R.error("音频上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传通用文件（文档等）
     */
    @PostMapping("/file")
    public R<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", file.getOriginalFilename());
            return R.ok("文件上传成功", result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败: " + e.getMessage());
        }
    }
}
