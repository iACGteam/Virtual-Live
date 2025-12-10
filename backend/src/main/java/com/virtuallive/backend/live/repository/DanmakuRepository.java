package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.Danmaku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanmakuRepository extends JpaRepository<Danmaku, Integer> {
}