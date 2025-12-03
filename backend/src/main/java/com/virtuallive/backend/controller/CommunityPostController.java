package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.*;
import com.virtuallive.backend.service.CommunityPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 社区帖子控制器
 * 处理社区内容的发布、浏览、编辑、删除等功能
 */
@RestController
@RequestMapping("/api/v1/community/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityPostController {
    
    private final CommunityPostService communityPostService;
    
    /**
     * 获取社区帖子列表（支持分页和排序）
     */
    @GetMapping
    public R<Page<CommunityPostDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sort,
            @RequestParam(required = false) Integer circleId) {
        try {
            Page<CommunityPostDto> posts = communityPostService.getPosts(page, size, sort, circleId);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 全局搜索社区帖子
     */
    @GetMapping("/search")
    public R<Page<CommunityPostDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CommunityPostDto> posts = communityPostService.searchPosts(keyword, page, size);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取单个帖子详情
     */
    @GetMapping("/{id}")
    public R<CommunityPostDto> getPost(@PathVariable Integer id) {
        try {
            CommunityPostDto post = communityPostService.getPostById(id);
            return R.ok(post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 创建社区帖子
     */
    @PostMapping
    public R<CommunityPostDto> createPost(@Valid @RequestBody CommunityPostCreateDto createDto) {
        try {
            CommunityPostDto post = communityPostService.createPost(createDto);
            return R.ok("发布成功", post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 更新社区帖子
     */
    @PutMapping("/{id}")
    public R<CommunityPostDto> updatePost(
            @PathVariable Integer id,
            @Valid @RequestBody CommunityPostUpdateDto updateDto) {
        try {
            CommunityPostDto post = communityPostService.updatePost(id, updateDto);
            return R.ok("更新成功", post);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 删除社区帖子（软删除）
     */
    @DeleteMapping("/{id}")
    public R<Void> deletePost(@PathVariable Integer id, @RequestParam Integer userId) {
        try {
            communityPostService.deletePost(id, userId);
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
            communityPostService.likePost(id);
            return R.ok("点赞成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的帖子列表
     */
    @GetMapping("/user/{userId}")
    public R<Page<CommunityPostDto>> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CommunityPostDto> posts = communityPostService.getUserPosts(userId, page, size);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取热门帖子（按浏览量和点赞数）
     */
    @GetMapping("/trending")
    public R<List<CommunityPostDto>> getTrendingPosts(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<CommunityPostDto> posts = communityPostService.getTrendingPosts(limit);
            return R.ok(posts);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
