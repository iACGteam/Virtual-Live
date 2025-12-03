package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.VideoDto;
import com.virtuallive.backend.model.dto.VideoUploadDto;
import com.virtuallive.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 社区帖子控制器
 * 处理社区内容的发布、编辑、删除、搜索等功能
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {
    
    private final PostService postService;
    
    /**
     * 获取社区帖子列表（分页）
     */
    @GetMapping
    public R<Page<VideoDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "time") String sort) {
        try {
            Page<VideoDto> posts = postService.getPosts(page, size, sort);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 全局搜索帖子（按标题、内容、标签）
     */
    @GetMapping("/search")
    public R<Page<VideoDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<VideoDto> posts = postService.searchPosts(keyword, page, size);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取单个帖子详情
     */
    @GetMapping("/{id}")
    public R<VideoDto> getPost(@PathVariable Integer id) {
        try {
            VideoDto post = postService.getPostById(id);
            return R.ok(post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的帖子列表
     */
    @GetMapping("/user/{userId}")
    public R<Page<VideoDto>> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<VideoDto> posts = postService.getUserPosts(userId, page, size);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 创建新帖子
     */
    @PostMapping
    public R<VideoDto> createPost(@Valid @RequestBody VideoUploadDto uploadDto) {
        try {
            VideoDto post = postService.createPost(uploadDto);
            return R.ok("发布成功", post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 更新帖子
     */
    @PutMapping("/{id}")
    public R<VideoDto> updatePost(
            @PathVariable Integer id,
            @Valid @RequestBody VideoUploadDto uploadDto) {
        try {
            VideoDto post = postService.updatePost(id, uploadDto);
            return R.ok("更新成功", post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 删除帖子（软删除）
     */
    @DeleteMapping("/{id}")
    public R<Void> deletePost(@PathVariable Integer id) {
        try {
            postService.deletePost(id);
            return R.ok("删除成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 点赞帖子
     */
    @PostMapping("/{id}/like")
    public R<Void> likePost(@PathVariable Integer id) {
        try {
            postService.likePost(id);
            return R.ok("点赞成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 上传图片/视频封面
     */
    @PostMapping("/upload/image")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = postService.uploadImage(file);
            return R.ok("上传成功", imageUrl);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 上传视频
     */
    @PostMapping("/upload/video")
    public R<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = postService.uploadVideo(file);
            return R.ok("上传成功", videoUrl);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
