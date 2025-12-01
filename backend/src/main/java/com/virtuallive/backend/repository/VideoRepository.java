package com.virtuallive.backend.repository;

import com.virtuallive.backend.model.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

    @EntityGraph(attributePaths = "author")
    Page<Video> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    @EntityGraph(attributePaths = "author")
    Page<Video> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);
    
    @EntityGraph(attributePaths = "author")
    @Query("SELECT v FROM Video v WHERE v.isDeleted = false ORDER BY v.views DESC")
    Page<Video> findPopularVideos(Pageable pageable);
    
    @EntityGraph(attributePaths = "author")
    List<Video> findByAuthor_UserIdAndIsDeletedFalse(Integer authorId);
}
