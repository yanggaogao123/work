package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessBusConfig;

import java.util.List;

public interface DyDriverlessBusConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyDriverlessBusConfig record);

    int insertSelective(DyDriverlessBusConfig record);

    DyDriverlessBusConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyDriverlessBusConfig record);

    int updateByPrimaryKey(DyDriverlessBusConfig record);

    List<DyDriverlessBusConfig> getBusConfigList(DyDriverlessBusConfig record);


}