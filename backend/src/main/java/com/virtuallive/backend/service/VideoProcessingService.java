package com.virtuallive.backend.service;

import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 视频处理服务
 * 用于提取视频第一帧作为封面，获取视频时长等
 */
@Service
@Slf4j
public class VideoProcessingService {

    private static final String THUMBNAIL_DIR = "thumbnails/";

    /**
     * 从视频URL提取第一帧作为封面
     * @param videoUrl 视频URL（支持本地文件或HTTP/HTTPS链接）
     * @return 封面图片的保存路径
     */
    public String extractThumbnail(String videoUrl) {
        Demuxer demuxer = null;
        File tempVideoFile = null;

        try {
            String filePath;
            // 如果是HTTP/HTTPS链接，先下载到临时文件
            if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
                tempVideoFile = downloadVideoToTemp(videoUrl);
                filePath = tempVideoFile.getAbsolutePath();
            } else {
                filePath = videoUrl;
            }

            demuxer = Demuxer.make();
            demuxer.open(filePath, null, false, true, null, null);

            // 查找视频流
            int numStreams = demuxer.getNumStreams();
            int videoStreamId = -1;
            Decoder videoDecoder = null;

            for (int i = 0; i < numStreams; i++) {
                DemuxerStream stream = demuxer.getStream(i);
                Decoder decoder = stream.getDecoder();
                if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                    videoStreamId = i;
                    videoDecoder = decoder;
                    break;
                }
            }

            if (videoStreamId == -1) {
                log.warn("视频中未找到视频流: {}", videoUrl);
                return null;
            }

            videoDecoder.open(null, null);

            MediaPicture picture = MediaPicture.make(
                    videoDecoder.getWidth(),
                    videoDecoder.getHeight(),
                    videoDecoder.getPixelFormat());

            MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(
                    MediaPictureConverterFactory.HUMBLE_BGR_24,
                    picture);

            BufferedImage image = null;
            MediaPacket packet = MediaPacket.make();

            // 读取第一帧
            while (demuxer.read(packet) >= 0) {
                if (packet.getStreamIndex() == videoStreamId) {
                    int offset = 0;
                    int bytesRead = 0;

                    do {
                        bytesRead += videoDecoder.decode(picture, packet, offset);
                        if (picture.isComplete()) {
                            image = converter.toImage(image, picture);
                            break;
                        }
                        offset += bytesRead;
                    } while (offset < packet.getSize());

                    if (image != null) {
                        break;
                    }
                }
            }

            if (image == null) {
                log.warn("无法从视频中提取帧: {}", videoUrl);
                return null;
            }

            // 保存缩略图
            String thumbnailPath = saveThumbnail(image);

            demuxer.close();

            log.info("成功提取视频封面: {} -> {}", videoUrl, thumbnailPath);
            return thumbnailPath;

        } catch (Exception e) {
            log.error("提取视频封面失败: {}", videoUrl, e);
            return null;
        } finally {
            // 清理资源
            if (demuxer != null) {
                try {
                    demuxer.close();
                } catch (Exception e) {
                    log.warn("关闭Demuxer失败", e);
                }
            }
            if (tempVideoFile != null && tempVideoFile.exists()) {
                if (!tempVideoFile.delete()) {
                    log.warn("无法删除临时文件: {}", tempVideoFile.getPath());
                }
            }
        }
    }

    /**
     * 获取视频时长（秒）
     * @param videoUrl 视频URL
     * @return 视频时长（秒），失败返回null
     */
    public Integer getVideoDuration(String videoUrl) {
        Demuxer demuxer = null;
        File tempVideoFile = null;

        try {
            String filePath;
            // 如果是HTTP/HTTPS链接，先下载到临时文件
            if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
                tempVideoFile = downloadVideoToTemp(videoUrl);
                filePath = tempVideoFile.getAbsolutePath();
            } else {
                filePath = videoUrl;
            }

            demuxer = Demuxer.make();
            demuxer.open(filePath, null, false, true, null, null);

            // 获取视频时长（微秒）转换为秒
            long durationInMicroseconds = demuxer.getDuration();
            int durationInSeconds = (int) (durationInMicroseconds / 1_000_000);

            demuxer.close();

            log.info("获取视频时长: {} -> {} 秒", videoUrl, durationInSeconds);
            return durationInSeconds;

        } catch (Exception e) {
            log.error("获取视频时长失败: {}", videoUrl, e);
            return null;
        } finally {
            if (demuxer != null) {
                try {
                    demuxer.close();
                } catch (Exception e) {
                    log.warn("关闭Demuxer失败", e);
                }
            }
            if (tempVideoFile != null && tempVideoFile.exists()) {
                if (!tempVideoFile.delete()) {
                    log.warn("无法删除临时文件: {}", tempVideoFile.getPath());
                }
            }
        }
    }

    /**
     * 处理视频：提取封面和获取时长
     * @param videoUrl 视频URL
     * @return VideoInfo对象，包含封面路径和时长
     */
    public VideoInfo processVideo(String videoUrl) {
        String thumbnailUrl = extractThumbnail(videoUrl);
        Integer duration = getVideoDuration(videoUrl);

        return VideoInfo.builder()
                .thumbnailUrl(thumbnailUrl)
                .duration(duration)
                .build();
    }

    /**
     * 下载网络视频到临时文件
     */
    private File downloadVideoToTemp(String videoUrl) throws IOException {
        File tempFile = File.createTempFile("video_", ".tmp");
        tempFile.deleteOnExit();

        URI uri = URI.create(videoUrl);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    /**
     * 保存缩略图
     */
    private String saveThumbnail(BufferedImage image) throws IOException {
        // 创建缩略图目录
        Path thumbnailDir = Paths.get(THUMBNAIL_DIR);
        if (!Files.exists(thumbnailDir)) {
            Files.createDirectories(thumbnailDir);
        }

        // 生成唯一文件名
        String filename = UUID.randomUUID() + ".jpg";
        Path thumbnailPath = thumbnailDir.resolve(filename);

        // 保存图片
        File outputFile = thumbnailPath.toFile();
        ImageIO.write(image, "jpg", outputFile);

        // 返回相对路径或URL
        return "/" + THUMBNAIL_DIR + filename;
    }

    /**
     * 视频信息类
     */
    @lombok.Data
    @lombok.Builder
    public static class VideoInfo {
        private String thumbnailUrl;  // 封面URL
        private Integer duration;      // 时长（秒）
    }
}

