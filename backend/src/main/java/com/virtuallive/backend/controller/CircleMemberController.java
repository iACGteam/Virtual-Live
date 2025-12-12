package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.CircleMember;
import com.virtuallive.backend.service.CircleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 圈子成员控制器
 */
@RestController
@RequestMapping("/api/v1/circles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CircleMemberController {
    
    private final CircleMemberService circleMemberService;
    
    /**
     * 加入/退出圈子
     */
    @PostMapping("/{circleId}/join")
    public R<Map<String, Object>> toggleMembership(
            @PathVariable Integer circleId,
            @RequestBody Map<String, Integer> body) {
        try {
            Integer userId = body.get("userId");
            boolean isMember = circleMemberService.toggleMembership(userId, circleId);
            long memberCount = circleMemberService.getMemberCount(circleId);
            
            return R.ok("操作成功", Map.of(
                    "isMember", isMember,
                    "memberCount", memberCount
            ));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 检查是否是圈子成员
     */
    @GetMapping("/{circleId}/check")
    public R<Boolean> checkMembership(
            @PathVariable Integer circleId,
            @RequestParam Integer userId) {
        try {
            boolean isMember = circleMemberService.isMember(userId, circleId);
            return R.ok(isMember);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    /**
     * 获取圈子成员列表
     */
    @GetMapping("/{circleId}/members")
    public R<Page<CircleMember>> getCircleMembers(
            @PathVariable Integer circleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<CircleMember> members = circleMemberService.getCircleMembers(circleId, page, size);
            return R.ok(members);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
}
