package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateJoin;

import java.util.List;

public interface ScheduleTemplateService {
	List<ScheduleTemplateJoin> getJoinTemplateListByRouteId(Integer routeId);
}
