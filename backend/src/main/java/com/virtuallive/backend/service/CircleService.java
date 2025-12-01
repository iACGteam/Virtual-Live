package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.CircleDto;
import com.virtuallive.backend.model.entity.Circle;
import com.virtuallive.backend.repository.CircleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CircleService {
    
    private final CircleRepository circleRepository;
    
    public Page<CircleDto> getCircles(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Circle> circles;
        if ("hot".equals(sort) || "popular".equals(sort)) {
            circles = circleRepository.findPopularCircles(pageable);
        } else {
            circles = circleRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
        }
        
        return circles.map(this::convertToDto);
    }
    
    public Page<CircleDto> searchCircles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.searchCircles(keyword, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CircleDto> getCirclesByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.findByCategoryAndIsActiveTrue(category, pageable)
                .map(this::convertToDto);
    }
    
    public Page<CircleDto> getOfficialCircles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return circleRepository.findByIsOfficialTrueAndIsActiveTrueOrderByMemberCountDesc(pageable)
                .map(this::convertToDto);
    }
    
    public CircleDto getCircleById(Integer id) {
        Circle circle = circleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("圈子不存在"));
        return convertToDto(circle);
    }
    
    private CircleDto convertToDto(Circle circle) {
        return CircleDto.builder()
                .id(circle.getCircleId())
                .name(circle.getName())
                .description(circle.getDescription())
                .avatarUrl(circle.getAvatarUrl())
                .coverImageUrl(circle.getCoverImageUrl())
                .memberCount(circle.getMemberCount())
                .postCount(circle.getPostCount())
                .category(circle.getCategory())
                .isOfficial(circle.getIsOfficial())
                .createdAt(circle.getCreatedAt())
                .build();
    }
}
