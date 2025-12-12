package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Favorite;
import com.virtuallive.backend.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    
    // 查询用户是否收藏了某个内容
    Optional<Favorite> findByUserAndContentTypeAndContentId(User user, Favorite.ContentType contentType, Integer contentId);
    
    // 查询用户是否收藏
    boolean existsByUserAndContentTypeAndContentId(User user, Favorite.ContentType contentType, Integer contentId);
    
    // 统计某个内容的收藏数
    long countByContentTypeAndContentId(Favorite.ContentType contentType, Integer contentId);
    
    // 获取用户的收藏列表
    Page<Favorite> findByUserAndContentTypeOrderByCreatedAtDesc(User user, Favorite.ContentType contentType, Pageable pageable);
    
    // 删除收藏记录
    void deleteByUserAndContentTypeAndContentId(User user, Favorite.ContentType contentType, Integer contentId);
}
