package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CommentDto;
import com.virtuallive.backend.model.entity.Comment;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.Video;
import com.virtuallive.backend.repository.CommentRepository;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    
    public Page<CommentDto> getComments(Integer videoId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Comment> comments;
        if ("hot".equals(sort)) {
            comments = commentRepository.findByPost_PostIdAndIsDeletedFalseOrderByLikesDesc(videoId, pageable);
        } else {
            comments = commentRepository.findByPost_PostIdAndIsDeletedFalseOrderByCreatedAtDesc(videoId, pageable);
        }
        
        return comments.map(this::convertToDto);
    }
    
    @Transactional
    public CommentDto addComment(Integer videoId, Integer userId, String content) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Comment comment = Comment.builder()
                .post(video)
                .user(user)
                .content(content)
                .likes(0)
                .isDeleted(false)
                .build();
        
        comment = commentRepository.save(comment);
        
        // 更新评论数
        video.setCommentsCount(video.getCommentsCount() + 1);
        videoRepository.save(video);
        
        return convertToDto(comment);
    }
    
    @Transactional
    public void likeComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }
    
    /**
     * 回复评论
     */
    @Transactional
    public CommentDto replyComment(Integer videoId, Integer userId, Integer parentCommentId, String content) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("父评论不存在"));
        
        Comment reply = Comment.builder()
                .post(video)
                .user(user)
                .parentComment(parentComment)
                .content(content)
                .likes(0)
                .isDeleted(false)
                .build();
        
        reply = commentRepository.save(reply);
        
        // 更新评论数
        video.setCommentsCount(video.getCommentsCount() + 1);
        videoRepository.save(video);
        
        return convertToDto(reply);
    }
    
    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 检查是否是评论作者
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评论");
        }
        
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        
        // 更新评论数
        Video video = comment.getPost();
        if (video.getCommentsCount() > 0) {
            video.setCommentsCount(video.getCommentsCount() - 1);
            videoRepository.save(video);
        }
    }
    
    /**
     * 获取评论的回复列表
     */
    public Page<CommentDto> getReplies(Integer parentCommentId, int page, int size) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> replies = commentRepository
                .findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(parentComment, pageable);
        
        return replies.map(this::convertToDto);
    }
    
    /**
     * 获取评论统计信息
     */
    public long getCommentCount(Integer videoId) {
        return commentRepository.countByPost_PostIdAndIsDeletedFalse(videoId);
    }
    
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = CommentDto.builder()
                .id(comment.getCommentId())
                .content(comment.getContent())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getUserId())
                .username(comment.getUser().getUsername())
                .userAvatar(comment.getUser().getAvatarUrl())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .build();
        
        // 如果有父评论，添加父评论信息
        if (comment.getParentComment() != null) {
            dto.setParentUsername(comment.getParentComment().getUser().getUsername());
        }
        
        return dto;
    }
}
