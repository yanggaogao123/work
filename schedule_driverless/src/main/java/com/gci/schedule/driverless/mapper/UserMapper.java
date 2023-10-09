package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.schedule.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    
	public User getUserByUserIdAndMenuId(Long userId, Long menuId);
}