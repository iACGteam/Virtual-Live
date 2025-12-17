package com.virtuallive.backend.service;

import com.virtuallive.backend.model.dto.R; // 适配backend包的通用返回体
import com.virtuallive.backend.model.entity.UserWallet;
import com.virtuallive.backend.model.entity.WalletRecords;
import com.virtuallive.backend.repository.UserWalletRepository;
import com.virtuallive.backend.repository.WalletRecordsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 钱包变动记录服务实现类（移除充值功能，JPA风格适配）
 */
@Service
@RequiredArgsConstructor // 替代@Autowired，通过构造器注入Repository
public class WalletRecordsService {

    private static final Logger log = LoggerFactory.getLogger(WalletRecordsService.class);

    // 注入JPA Repository（替代MyBatis Mapper）
    private final UserWalletRepository userWalletRepository;
    private final WalletRecordsRepository walletRecordsRepository;

    /**
     * 获取钱包核心信息（JPA实现：按用户ID查询钱包）
     */
    public R<Map<String, Object>> getWalletInfo(Long userId) {
        try {
            if (userId == null || userId <= 0) {
                log.warn("获取钱包信息失败：用户ID为空或非法，userId={}", userId);
                return R.error("用户ID不能为空且必须为正数");
            }
            
            // JPA方式：按用户ID查询钱包（替代MyBatis的selectByUserId）
            Optional<UserWallet> walletOptional = userWalletRepository.findByUserId(userId);
            
            if (walletOptional.isPresent()) {
                UserWallet wallet = walletOptional.get();
                Map<String, Object> data = new HashMap<>();
                // BigDecimal精度计算（避免Double丢失精度）
                BigDecimal balance = BigDecimal.valueOf(wallet.getBalance());
                BigDecimal totalEarned = BigDecimal.valueOf(wallet.getTotalEarned());
                BigDecimal totalSpent = BigDecimal.valueOf(wallet.getTotalSpent());
                BigDecimal totalIncome = totalEarned.subtract(totalSpent); // 累计收益=总收入-总支出
                
                data.put("balance", balance);
                data.put("total_earned", totalEarned);
                data.put("total_spent", totalSpent);
                data.put("total_income", totalIncome);
                // 补充返回钱包ID和更新时间（注册时间）
                data.put("wallet_id", wallet.getWalletId());
                data.put("update_time", wallet.getUpdateTime());
                
                log.info("获取用户ID为{}的钱包信息成功，钱包ID={}", userId, wallet.getWalletId());
                return R.success(data, "获取成功");
            } else {
                log.warn("用户ID为{}的钱包不存在", userId);
                return R.error("钱包不存在");
            }
        } catch (Exception e) {
            log.error("获取用户ID为{}的钱包信息失败: {}", userId, e.getMessage(), e);
            return R.error("获取钱包信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取钱包变动记录（分页，JPA实现）
     */
    public R<Page<WalletRecords>> getWalletRecords(Long userId, Date startTime, Date endTime, Integer type, long current, long size) {
        try {
            if (userId == null || userId <= 0) {
                log.warn("获取钱包变动记录失败：用户ID为空或非法，userId={}", userId);
                return R.error("用户ID不能为空且必须为正数");
            }
            
            // JPA分页配置（页码从0开始，对应MyBatis的current-1）
            Pageable pageable = PageRequest.of((int) (current - 1), (int) size, Sort.by(Sort.Direction.DESC, "createTime"));
            
            // 调用Repository的分页查询方法（需在WalletRecordsRepository中定义）
            Page<WalletRecords> page = walletRecordsRepository.findWalletRecordsByCondition(userId, startTime, endTime, type, pageable);
            
            log.info("获取用户ID为{}的钱包变动记录成功，当前页={}，总条数={}", userId, current, page.getTotalElements());
            return R.success(page, "获取成功");
        } catch (Exception e) {
            log.error("获取用户ID为{}的钱包变动记录失败: {}", userId, e.getMessage(), e);
            return R.error("获取钱包变动记录失败：" + e.getMessage());
        }
    }

    /**
     * 初始化钱包记录（用户注册时调用，记录初始余额来源）
     * 仅记录初始10000元余额，无充值逻辑
     */
    @Transactional(rollbackFor = Exception.class) // 事务保证
    public void initWalletRecord(Long userId) {
        // JPA方式：查询钱包（不存在则抛异常）
        UserWallet wallet = userWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户ID为" + userId + "的钱包不存在，无法初始化记录"));
        
        // 构建初始余额记录（Builder模式，对齐目标格式）
        WalletRecords record = WalletRecords.builder()
                .userId(userId)
                .amount(10000.00) // 初始余额
                .type(0) // 0:收入
                .createTime(wallet.getUpdateTime()) // 与钱包更新时间（注册时间）一致
                .remark("注册赠送初始余额")
                .build();
        
        // JPA保存记录（替代MyBatis的insert）
        walletRecordsRepository.save(record);
        
        log.info("用户ID为{}的钱包初始记录创建成功，初始余额10000元", userId);
    }
}
