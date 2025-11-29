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
    
    public Page<CommentDto> getComments(Long videoId, int page, int size, String sort) {
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
    public CommentDto addComment(Long videoId, Long userId, String content) {
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
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
    }
    
    private CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getCommentId())
                .content(comment.getContent())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .userId(comment.getUser().getUserId())
                .username(comment.getUser().getUsername())
                .userAvatar(comment.getUser().getAvatarUrl())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .build();
    }
}
