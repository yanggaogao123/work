package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsClasses;

import java.util.List;

public interface ScheduleParamsClassesDynamicMapper {

    List<ScheduleParamsClasses> getByTemplateId(Integer templateId);

}