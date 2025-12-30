package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.Danmaku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanmakuRepository extends JpaRepository<Danmaku, Integer> {
    List<Danmaku> findByVideoIdOrderByCreatedAtAsc(Integer videoId);
    
    // 获取某场直播的最新弹幕 (用于历史记录)
    List<Danmaku> findTop50BySessionIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer sessionId);

    // 删除某场直播所有弹幕（在直播结束后清理）
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("delete from Danmaku d where d.sessionId = :sessionId")
    void deleteBySessionId(@org.springframework.data.repository.query.Param("sessionId") Integer sessionId);
}