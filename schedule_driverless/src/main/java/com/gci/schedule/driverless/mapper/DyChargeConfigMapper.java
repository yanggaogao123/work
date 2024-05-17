package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.DyChargeConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyChargeConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyChargeConfig record);

    int insertSelective(DyChargeConfig record);

    DyChargeConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChargeConfig record);

    int updateByPrimaryKey(DyChargeConfig record);

    @Cacheable(unless = "#result == null", cacheNames = "selectDyChargeConfigList")
    List<DyChargeConfig> selectAll();
}