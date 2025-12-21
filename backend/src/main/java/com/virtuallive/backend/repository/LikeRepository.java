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

    @Query("SELECT v FROM Like l, Video v JOIN FETCH v.author WHERE l.contentId = v.postId AND l.contentType = :contentType AND l.user.userId = :userId AND (v.tags IS NULL OR v.tags NOT LIKE '%__PRIVATE__%') ORDER BY l.createdAt DESC")
    Page<com.virtuallive.backend.model.entity.Video> findLikedVideos(@org.springframework.data.repository.query.Param("userId") Integer userId, @org.springframework.data.repository.query.Param("contentType") Like.ContentType contentType, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Like l, Video v WHERE l.contentId = v.postId AND l.contentType = 'post' AND v.author.userId = :userId")
    long countVideoLikesByAuthorId(@org.springframework.data.repository.query.Param("userId") Integer userId);
}
