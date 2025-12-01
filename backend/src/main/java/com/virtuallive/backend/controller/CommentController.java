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
}
