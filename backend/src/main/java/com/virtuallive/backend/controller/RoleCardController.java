package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.entity.RoleCard;
import com.virtuallive.backend.service.RoleCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role-cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoleCardController {
    
    private final RoleCardService roleCardService;
    
    @GetMapping("/user/{userId}")
    public R<List<RoleCard>> getUserRoleCards(@PathVariable Integer userId) {
        try {
            // 确保只返回该用户的角色卡
            List<RoleCard> cards = roleCardService.getUserRoleCards(userId);
            return R.ok(cards);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PostMapping("/user/{userId}")
    public R<RoleCard> createRoleCard(
            @PathVariable Integer userId,
            @RequestBody RoleCard roleCard) {
        try {
            RoleCard created = roleCardService.createRoleCard(userId, roleCard);
            return R.ok("创建成功", created);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PutMapping("/user/{userId}/{cardId}")
    public R<RoleCard> updateRoleCard(
            @PathVariable Integer userId,
            @PathVariable Integer cardId,
            @RequestBody RoleCard roleCard) {
        try {
            RoleCard updated = roleCardService.updateRoleCard(userId, cardId, roleCard);
            return R.ok("更新成功", updated);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
