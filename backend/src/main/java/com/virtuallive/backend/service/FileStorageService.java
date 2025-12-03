package com.virtuallive.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件存储服务
 * 处理图片、视频、音频等多媒体文件的上传和存储
 */
@Service
@Slf4j
public class FileStorageService {
    
    @Value("${file.upload.base-path:uploads}")
    private String baseUploadPath;
    
    @Value("${file.upload.image-path:images}")
    private String imagePath;
    
    @Value("${file.upload.video-path:videos}")
    private String videoPath;
    
    @Value("${file.upload.audio-path:audios}")
    private String audioPath;
    
    @Value("${file.upload.file-path:files}")
    private String filePath;
    
    private final VideoProcessingService videoProcessingService;
    
    public FileStorageService(VideoProcessingService videoProcessingService) {
        this.videoProcessingService = videoProcessingService;
    }
    
    /**
     * 存储图片文件
     */
    public String storeImage(MultipartFile file) throws IOException {
        validateImageFile(file);
        return storeFile(file, imagePath);
    }
    
    /**
     * 存储视频文件并提取元数据
     */
    public Map<String, Object> storeVideo(MultipartFile file) throws IOException {
        validateVideoFile(file);
        
        String videoUrl = storeFile(file, videoPath);
        
        Map<String, Object> result = new HashMap<>();
        result.put("url", videoUrl);
        result.put("filename", file.getOriginalFilename());
        result.put("size", file.getSize());
        
        // 尝试提取视频元数据
        try {
            VideoProcessingService.VideoInfo videoInfo = videoProcessingService.processVideo(videoUrl);
            if (videoInfo.getDuration() != null) {
                result.put("duration", videoInfo.getDuration());
            }
            if (videoInfo.getThumbnailUrl() != null) {
                result.put("thumbnailUrl", videoInfo.getThumbnailUrl());
            }
        } catch (Exception e) {
            log.warn("视频元数据提取失败: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 存储音频文件
     */
    public String storeAudio(MultipartFile file) throws IOException {
        validateAudioFile(file);
        return storeFile(file, audioPath);
    }
    
    /**
     * 存储通用文件
     */
    public String storeFile(MultipartFile file) throws IOException {
        return storeFile(file, filePath);
    }
    
    /**
     * 核心存储方法
     */
    private String storeFile(MultipartFile file, String subPath) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        
        // 创建存储目录
        Path uploadDir = Paths.get(baseUploadPath, subPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回相对URL路径
        String fileUrl = "/" + baseUploadPath + "/" + subPath + "/" + filename;
        log.info("文件上传成功: {}", fileUrl);
        
        return fileUrl;
    }
    
    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片文件");
        }
        
        // 限制文件大小（10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("图片文件大小不能超过10MB");
        }
    }
    
    /**
     * 验证视频文件
     */
    private void validateVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("只支持视频文件");
        }
        
        // 限制文件大小（500MB）
        long maxSize = 500 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("视频文件大小不能超过500MB");
        }
    }
    
    /**
     * 验证音频文件
     */
    private void validateAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new RuntimeException("只支持音频文件");
        }
        
        // 限制文件大小（50MB）
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("音频文件大小不能超过50MB");
        }
    }
}
