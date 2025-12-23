package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.entity.UserWallet;
import com.virtuallive.backend.live.repository.UserWalletRepository;
import com.virtuallive.backend.live.service.IUserService;
import com.virtuallive.backend.model.dto.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/live/user")
public class LiveUserController {

    @Autowired private IUserService userService;
    @Autowired private UserWalletRepository userWalletRepository;

    @GetMapping("/me")
    public R<Map<String, Object>> getMyInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String tokenHeader = request.getHeader("token");
        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : tokenHeader;

        if (token == null) return R.error(401, "未登录");

        UserInfoDTO user = userService.getUserByToken(token);
        if (user == null) return R.error(401, "无效Token");

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getUserId());
        result.put("username", user.getUsername());
        result.put("avatar", user.getAvatarUrl());
        result.put("followers", user.getFollowers());

        // 获取钱包余额
        BigDecimal balance = BigDecimal.ZERO;
        UserWallet wallet = userWalletRepository.findByUserId(user.getUserId().intValue()).orElse(null);
        if (wallet != null) {
            balance = wallet.getBalance();
        }
        result.put("balance", balance);

        return R.ok(result);
    }

    @GetMapping("/{userId}")
    public R<UserInfoDTO> getUserInfo(@PathVariable Integer userId) {
        UserInfoDTO user = userService.getUserById(userId);
        if (user == null) return R.error(404, "用户不存在");
        return R.ok(user);
    }

    @PostMapping("/wallet/recharge")
    public R<?> recharge(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String tokenHeader = request.getHeader("token");
        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : tokenHeader;

        if (token == null) return R.error(401, "未登录");
        UserInfoDTO user = userService.getUserByToken(token);
        if (user == null) return R.error(401, "无效Token");

        Object amountObj = body.get("amount");
        if (amountObj == null) return R.error(400, "金额不能为空");
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountObj.toString());
        } catch (Exception e) {
            return R.error(400, "金额格式错误");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return R.error(400, "充值金额必须大于0");
        }

        UserWallet wallet = userWalletRepository.findByUserId(user.getUserId().intValue())
                .orElseGet(() -> {
                    UserWallet w = new UserWallet();
                    w.setUserId(user.getUserId().intValue());
                    w.setBalance(BigDecimal.ZERO);
                    w.setTotalSpent(BigDecimal.ZERO);
                    return w;
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        userWalletRepository.save(wallet);

        return R.ok("充值成功", wallet.getBalance());
    }
}
