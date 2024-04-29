package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsDriverless;

import java.util.List;

public interface ScheduleParamsDriverlessMapper {

    List<ScheduleParamsDriverless> getByTemplateId(Integer templateId);

}