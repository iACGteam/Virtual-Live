package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findByPost_PostIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer postId, Pageable pageable);

    Page<Comment> findByPost_PostIdAndIsDeletedFalseOrderByLikesDesc(Integer postId, Pageable pageable);

    List<Comment> findByParentComment_CommentIdAndIsDeletedFalse(Integer parentId);
    
    Page<Comment> findByParentCommentAndIsDeletedFalseOrderByCreatedAtAsc(Comment parentComment, Pageable pageable);

    long countByPost_PostIdAndIsDeletedFalse(Integer postId);
}
