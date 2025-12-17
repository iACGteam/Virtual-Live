package com.virtuallive.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtuallive.entity.RoleCard;
import org.apache.ibatis.annotations.Param;

public interface RoleCardMapper extends BaseMapper<RoleCard> {
    // 根据用户ID获取角色卡
    RoleCard getRoleCardByUserId(@Param("userId") Long userId);
}
