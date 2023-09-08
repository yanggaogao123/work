package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.DyChangeTimeWindow;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface DyChangeTimeWindowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyChangeTimeWindow record);

    int insertSelective(DyChangeTimeWindow record);

    DyChangeTimeWindow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChangeTimeWindow record);

    int updateByPrimaryKey(DyChangeTimeWindow record);

    @Cacheable(unless = "#result == null", cacheNames = "selectDyChangeTimeWindowList")
    List<DyChangeTimeWindow> selectAll();
}