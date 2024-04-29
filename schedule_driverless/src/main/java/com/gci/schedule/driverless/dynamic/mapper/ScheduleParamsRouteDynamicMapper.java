package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsRoute;

public interface ScheduleParamsRouteDynamicMapper {

    ScheduleParamsRoute getByTemplateId(Integer templateId);

}