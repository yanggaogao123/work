package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;

import java.util.List;

public interface DySchedulePlanDriverlessMapper {
    int insert(DySchedulePlanDriverless record);

    void batchInsertSchedule(List<DySchedulePlanDriverless> list);

    int insertSelective(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> getScheduleList(DySchedulePlanDriverless record);
 }