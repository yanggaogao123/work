package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessConfig;

import java.util.List;

public interface DyDriverlessConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyDriverlessConfig record);

    int insertSelective(DyDriverlessConfig record);

    DyDriverlessConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyDriverlessConfig record);

    int updateByPrimaryKey(DyDriverlessConfig record);

    List<DyDriverlessConfig> selectByRouteId(Long routeId);

    List<DyDriverlessConfig> selectByRouteIdAndSupportId(DyDriverlessConfig record);

}