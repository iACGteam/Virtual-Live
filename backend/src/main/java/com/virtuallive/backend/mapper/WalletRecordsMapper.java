package com.virtuallive.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.virtuallive.entity.WalletRecords;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface WalletRecordsMapper extends BaseMapper<WalletRecords> {
    // 分页查询钱包记录，支持时间和类型筛选
    List<WalletRecords> getWalletRecords(Page<WalletRecords> page, 
                                         @Param("userId") Long userId, 
                                         @Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime,
                                         @Param("type") Integer type);
}