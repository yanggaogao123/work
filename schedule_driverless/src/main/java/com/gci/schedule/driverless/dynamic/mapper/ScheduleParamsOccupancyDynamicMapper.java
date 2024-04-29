package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsOccupancy;

import java.util.List;

public interface ScheduleParamsOccupancyDynamicMapper {

    List<ScheduleParamsOccupancy> getByTemplateId(Integer templateId);

}