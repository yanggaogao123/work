package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;

public interface DySchedulePlanDriverlessMapper {
    int insert(DySchedulePlanDriverless record);

    int insertSelective(DySchedulePlanDriverless record);
}