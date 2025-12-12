package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.CommentDto;
import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/video/{videoId}")
    public R<Page<CommentDto>> getComments(
            @PathVariable Integer videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "time") String sort) {
        try {
            Page<CommentDto> comments = commentService.getComments(videoId, page, size, sort);
            return R.ok(comments);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PostMapping("/video/{videoId}")
    public R<CommentDto> addComment(
            @PathVariable Integer videoId,
            @RequestBody Map<String, Object> body) {
        try {
            Integer userId = Integer.valueOf(body.get("userId").toString());
            String content = body.get("content").toString();
            CommentDto comment = commentService.addComment(videoId, userId, content);
            return R.ok("评论成功", comment);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PostMapping("/{commentId}/like")
    public R<Void> likeComment(@PathVariable Integer commentId) {
        try {
            commentService.likeComment(commentId);
            return R.ok("点赞成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 回复评论
     */
    @PostMapping("/{parentCommentId}/reply")
    public R<CommentDto> replyComment(
            @PathVariable Integer parentCommentId,
            @RequestBody Map<String, Object> body) {
        try {
            Integer videoId = Integer.valueOf(body.get("videoId").toString());
            Integer userId = Integer.valueOf(body.get("userId").toString());
            String content = body.get("content").toString();
            
            CommentDto comment = commentService.replyComment(videoId, userId, parentCommentId, content);
            return R.ok("回复成功", comment);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public R<Void> deleteComment(
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return R.ok("删除成功", null);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{commentId}/replies")
    public R<Page<CommentDto>> getReplies(
            @PathVariable Integer commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CommentDto> replies = commentService.getReplies(commentId, page, size);
            return R.ok(replies);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取评论总数
     */
    @GetMapping("/video/{videoId}/count")
    public R<Long> getCommentCount(@PathVariable Integer videoId) {
        try {
            long count = commentService.getCommentCount(videoId);
            return R.ok(count);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
