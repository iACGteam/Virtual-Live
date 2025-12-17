package com.virtuallive.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.UserProfileDto;
import com.virtuallive.backend.model.entity.Users;
import com.virtuallive.backend.model.entity.UserWallet;
import com.virtuallive.backend.model.entity.WalletRecords;
import com.virtuallive.backend.model.entity.RoleCard;
import com.virtuallive.backend.service.UserService;
import com.virtuallive.backend.service.UserWalletService;
import com.virtuallive.backend.service.WalletRecordsService;
import com.virtuallive.backend.service.ViewHistoryService;
import com.virtuallive.backend.service.RoleCardService;
import com.virtuallive.backend.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户中心控制器（适配新DTO风格 + 移除充值功能）
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users") // 统一接口前缀为/api/v1/users
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 跨域配置
public class UserController {

    private final UserService userService;
    private final UserWalletService userWalletService;
    private final WalletRecordsService walletRecordsService;
    private final ViewHistoryService viewHistoryService;
    private final RoleCardService roleCardService;

    /**
     * 获取用户基础信息
     */
    @GetMapping("/{userId}/info") // 适配RESTful规范
    public R<Users> getUserInfo(@PathVariable Integer userId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            log.warn("获取用户信息失败：用户ID非法，userId={}", userId);
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            Users user = userService.getUserInfo(Long.valueOf(userId));
            if (user != null) {
                log.info("获取用户{}基础信息成功", userId);
                return R.ok(user);
            } else {
                log.warn("获取用户信息失败：用户{}不存在", userId);
                return R.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户{}基础信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户钱包信息
     */
    @GetMapping("/{userId}/wallet")
    public R<UserWallet> getUserWallet(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            log.warn("获取用户钱包失败：用户ID非法，userId={}", userId);
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            UserWallet wallet = userWalletService.getWalletByUserId(Long.valueOf(userId));
            if (wallet != null) {
                log.info("获取用户{}钱包信息成功", userId);
                return R.ok(wallet);
            } else {
                log.warn("获取用户钱包失败：用户{}钱包不存在", userId);
                return R.error("钱包不存在");
            }
        } catch (Exception e) {
            log.error("获取用户{}钱包信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取关注列表
     */
    @GetMapping("/{userId}/following")
    public R<List<Users>> getFollowingList(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            log.warn("获取关注列表失败：用户ID非法，userId={}", userId);
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            List<Users> followingList = userService.getFollowingList(Long.valueOf(userId));
            log.info("获取用户{}关注列表成功，共{}条", userId, followingList.size());
            return R.ok(followingList);
        } catch (Exception e) {
            log.error("获取用户{}关注列表异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取粉丝列表
     */
    @GetMapping("/{userId}/follower")
    public R<List<Users>> getFollowerList(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            log.warn("获取粉丝列表失败：用户ID非法，userId={}", userId);
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            List<Users> followerList = userService.getFollowerList(Long.valueOf(userId));
            log.info("获取用户{}粉丝列表成功，共{}条", userId, followerList.size());
            return R.ok(followerList);
        } catch (Exception e) {
            log.error("获取用户{}粉丝列表异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户主页信息（复用原UserProfileDto）
     */
    @GetMapping("/{userId}/profile")
    public R<UserProfileDto> getUserProfile(@PathVariable Integer userId) {
        try {
            UserProfileDto profile = userService.getUserProfile(userId);
            return R.ok(profile);
        } catch (Exception e) {
            log.error("获取用户{}主页信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 修改用户头像和简介（适配UpdateProfileRequest DTO）
     */
    @PutMapping("/{userId}/profile")
    public R<UserProfileDto> updateProfile(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            // 从Token验证当前用户是否为本人（保持原权限逻辑）
            Long currentUserId = getCurrentUserId(httpRequest);
            if (currentUserId == null || !currentUserId.equals(Long.valueOf(userId))) {
                return R.error("无权修改他人信息");
            }

            UserProfileDto profile = userService.updateProfile(userId, request);
            log.info("用户{}更新个人信息成功", userId);
            return R.ok("更新成功", profile);
        } catch (Exception e) {
            log.error("用户{}更新个人信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户钱包核心信息
     */
    @GetMapping("/{userId}/wallet/info")
    public R<Map<String, Object>> getWalletInfo(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            Map<String, Object> walletInfo = walletRecordsService.getWalletInfo(Long.valueOf(userId));
            log.info("获取用户{}钱包核心信息成功", userId);
            return R.ok(walletInfo);
        } catch (Exception e) {
            log.error("获取用户{}钱包核心信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取钱包变动记录（分页+筛选）
     */
    @GetMapping("/{userId}/wallet/records")
    public R<Page<WalletRecords>> getWalletRecords(
            @PathVariable Integer userId,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            Integer type,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            Page<WalletRecords> page = walletRecordsService.getWalletRecords(
                    Long.valueOf(userId), startTime, endTime, type, current, size);
            log.info("获取用户{}钱包变动记录成功，当前页{}，共{}条", userId, current, page.getTotal());
            return R.ok(page);
        } catch (Exception e) {
            log.error("获取用户{}钱包变动记录异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户发布的作品
     */
    @GetMapping("/{userId}/works")
    public R<Page<?>> getUserWorks(
            @PathVariable Integer userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false, defaultValue = "0") Integer isPrivate,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            log.info("用户{}获取作品列表，标题关键词：{}，私密状态：{}", userId, title, isPrivate);
            // 后续对接works表后替换为实际逻辑
            return R.ok(new Page<>());
        } catch (Exception e) {
            log.error("获取用户{}作品列表异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户点赞的内容
     */
    @GetMapping("/{userId}/likes")
    public R<Map<String, Object>> getUserLikes(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            Map<String, Object> data = new Map<>() {{
                put("totalCount", 0);
                put("list", new Page<>());
            }};
            log.info("用户{}获取点赞内容成功", userId);
            return R.ok(data);
        } catch (Exception e) {
            log.error("获取用户{}点赞内容异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取30天内的观看记录
     */
    @GetMapping("/{userId}/view-history")
    public R<List<?>> getViewHistory(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            List<?> history = viewHistoryService.getRecentViewHistory(Long.valueOf(userId));
            log.info("用户{}获取观看记录成功，共{}条", userId, history.size());
            return R.ok(history);
        } catch (Exception e) {
            log.error("获取用户{}观看记录异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取用户角色卡信息
     */
    @GetMapping("/{userId}/role-card")
    public R<RoleCard> getRoleCard(@PathVariable Integer userId) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }

        try {
            Map<String, Object> result = roleCardService.getRoleCard(Long.valueOf(userId));
            Integer code = (Integer) result.get("code");
            if (code == 200) {
                log.info("用户{}获取角色卡信息成功", userId);
                return R.ok((RoleCard) result.get("data"));
            } else {
                return R.error((String) result.get("msg"));
            }
        } catch (Exception e) {
            log.error("获取用户{}角色卡信息异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 提交角色卡申请
     */
    @PostMapping("/{userId}/role-card/apply")
    public R<RoleCard> applyRoleCard(
            @PathVariable Integer userId,
            @RequestBody RoleCard roleCard) {
        if (userId == null || userId <= 0) {
            return R.error("用户ID不能为空且必须为正数");
        }
        if (roleCard == null) {
            return R.error("角色卡信息不能为空");
        }

        try {
            roleCard.setUserId(Long.valueOf(userId));
            Map<String, Object> result = roleCardService.applyRoleCard(roleCard);
            Integer code = (Integer) result.get("code");
            if (code == 200) {
                log.info("用户{}角色卡申请提交成功，待审核", userId);
                return R.ok((RoleCard) result.get("data"));
            } else {
                return R.error((String) result.get("msg"));
            }
        } catch (Exception e) {
            log.error("用户{}角色卡申请异常", userId, e);
            return R.error(e.getMessage());
        }
    }

    // -------------------------- 工具方法 --------------------------
    /**
     * 从请求头Token中解析当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                log.warn("Token无效：请求头Authorization缺失或格式错误");
                return null;
            }
            token = token.replace("Bearer ", "");
            return JwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("解析Token失败", e);
            return null;
        }
    }
}
