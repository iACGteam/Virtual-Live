package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Like;
import com.virtuallive.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    
    // 查询用户是否点赞了某个内容
    Optional<Like> findByUserAndContentTypeAndContentId(User user, Like.ContentType contentType, Integer contentId);
    
    // 查询用户是否点赞
    boolean existsByUserAndContentTypeAndContentId(User user, Like.ContentType contentType, Integer contentId);
    
    // 统计某个内容的点赞数
    long countByContentTypeAndContentId(Like.ContentType contentType, Integer contentId);
    
    // 获取用户的点赞列表
    Page<Like> findByUserAndContentTypeOrderByCreatedAtDesc(User user, Like.ContentType contentType, Pageable pageable);
    
    // 删除点赞记录
    void deleteByUserAndContentTypeAndContentId(User user, Like.ContentType contentType, Integer contentId);
}
