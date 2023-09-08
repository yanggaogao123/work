package com.gci.schedule.driverless.mapper;


import com.gci.schedule.driverless.bean.bs.DyChargeStandbyBus;

import java.util.List;

public interface DyChargeStandbyBusMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyChargeStandbyBus record);

    int insertSelective(DyChargeStandbyBus record);

    DyChargeStandbyBus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChargeStandbyBus record);

    int updateByPrimaryKey(DyChargeStandbyBus record);

    List<DyChargeStandbyBus> selectList(DyChargeStandbyBus record);
}