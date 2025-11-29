package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByPost_PostIdAndIsDeletedFalseOrderByCreatedAtDesc(Long postId, Pageable pageable);
    
    Page<Comment> findByPost_PostIdAndIsDeletedFalseOrderByLikesDesc(Long postId, Pageable pageable);
    
    List<Comment> findByParentComment_CommentIdAndIsDeletedFalse(Long parentId);
    
    int countByPost_PostIdAndIsDeletedFalse(Long postId);
}
