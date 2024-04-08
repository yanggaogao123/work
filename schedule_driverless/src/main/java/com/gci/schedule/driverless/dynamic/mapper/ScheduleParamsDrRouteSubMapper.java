package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsDrRouteSub;

import java.util.List;

public interface ScheduleParamsDrRouteSubMapper {

    List<ScheduleParamsDrRouteSub> getByTemplateId(Integer templateId);

}