package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;

public interface ScheduleParamsRouteService {
	ScheduleParamsRoute getByTemplateId(Integer templateId);
}
