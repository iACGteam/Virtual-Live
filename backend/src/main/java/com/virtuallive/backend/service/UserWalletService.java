package com.virtuallive.backend.service;

import com.virtuallive.backend.model.entity.UserWallet;
import com.virtuallive.backend.repository.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * 用户钱包核心服务（JPA风格，对齐目标架构）
 */
@Service
@RequiredArgsConstructor // 构造器注入Repository，替代@Autowired
public class UserWalletService { // 去掉Impl后缀，符合目标格式（ViewHistoryService）

    private static final Logger log = LoggerFactory.getLogger(UserWalletService.class);

    // 注入JPA Repository（替代MyBatis-Plus的Mapper）
    private final UserWalletRepository userWalletRepository;

    /**
     * 根据用户ID查询钱包（兼容原有方法签名，返回非空对象）
     */
    @Override // 若保留原接口，需确保接口在backend包下
    public UserWallet getWalletByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("查询钱包失败：用户ID为空或非法，userId={}", userId);
            return null;
        }
        Optional<UserWallet> walletOptional = userWalletRepository.findByUserId(userId);
        if (walletOptional.isPresent()) {
            UserWallet wallet = walletOptional.get();
            log.info("查询用户ID为{}的钱包成功，钱包ID={}", userId, wallet.getWalletId());
            return wallet;
        } else {
            log.warn("用户ID为{}的钱包不存在", userId);
            return null;
        }
    }

    /**
     * 初始化用户钱包（核心方法：接收注册时间，保证updateTime与注册时间一致）
     * @param userId 用户ID
     * @param registerTime 注册时间（从用户注册逻辑传递）
     * @return 是否初始化成功
     */
    @Transactional(rollbackFor = Exception.class) // 事务保证原子性
    public boolean initWallet(Long userId, Date registerTime) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            log.error("初始化钱包失败：用户ID为空或非法，userId={}", userId);
            return false;
        }
        if (registerTime == null) {
            log.error("初始化钱包失败：注册时间为空，userId={}", userId);
            return false;
        }

        // 2. 幂等性校验：避免重复创建钱包
        if (userWalletRepository.findByUserId(userId).isPresent()) {
            log.info("用户ID为{}已存在钱包，无需重复初始化", userId);
            return true; // 已存在视为初始化成功（幂等）
        }

        // 3. Builder模式构建钱包（对齐目标格式的ViewHistory.builder()）
        UserWallet wallet = UserWallet.builder()
                .userId(userId)
                .balance(10000.0) // 初始余额10000
                .totalEarned(10000.0) // 修正：累计收入=初始余额（原代码为0.0）
                .totalSpent(0.0) // 累计支出0
                .updateTime(registerTime) // 关键：更新时间=注册时间（而非当前时间）
                .build();

        // 4. JPA保存（替代MyBatis-Plus的save方法）
        try {
            userWalletRepository.save(wallet);
            log.info("用户ID为{}的钱包初始化成功，钱包ID={}，初始余额10000元", userId, wallet.getWalletId());
            return true;
        } catch (Exception e) {
            log.error("用户ID为{}的钱包初始化失败：保存数据库失败，原因={}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 兼容原有无注册时间的方法（兜底，保持接口兼容）
     */
    @Override
    public boolean initWallet(Long userId) {
        log.warn("初始化钱包未传递注册时间，使用当前系统时间作为updateTime，userId={}", userId);
        return initWallet(userId, new Date());
    }
}
