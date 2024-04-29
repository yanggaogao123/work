package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsDrInout;

import java.util.List;

public interface ScheduleParamsDrInoutMapper {

    List<ScheduleParamsDrInout> getByTemplateId(Integer templateId);

}