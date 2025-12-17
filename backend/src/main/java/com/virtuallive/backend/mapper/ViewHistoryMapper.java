package com.virtuallive.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.virtuallive.entity.ViewHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ViewHistoryMapper extends BaseMapper<ViewHistory> {
    // 获取用户30天内的观看记录
    List<ViewHistory> getRecentViewHistory(@Param("userId") Long userId);
}
