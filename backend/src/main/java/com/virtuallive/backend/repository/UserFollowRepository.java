package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.model.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    
    // 查询关注关系
    Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);
    
    // 检查是否关注
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    // 获取粉丝列表
    Page<UserFollow> findByFollowingOrderByFollowDateDesc(User following, Pageable pageable);
    
    // 获取关注列表
    Page<UserFollow> findByFollowerOrderByFollowDateDesc(User follower, Pageable pageable);
    
    // 统计粉丝数
    long countByFollowing(User following);
    
    // 统计关注数
    long countByFollower(User follower);
    
    // 删除关注关系
    void deleteByFollowerAndFollowing(User follower, User following);
}
