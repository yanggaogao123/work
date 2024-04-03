package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsEat;

import java.util.List;

public interface ScheduleParamsEatDynamicMapper {

    List<ScheduleParamsEat> getByTemplateId(Integer templateId);

}