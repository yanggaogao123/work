package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsClasses;

import java.util.List;
import java.util.Map;

public interface ScheduleParamsClassesService {
	public List<ScheduleParamsClasses> getByTemplateId(Integer templateId);

    List<ScheduleParamsClasses> selectList();

    List<ScheduleParamsClasses> selectByRouteId(Long routeId);
}
