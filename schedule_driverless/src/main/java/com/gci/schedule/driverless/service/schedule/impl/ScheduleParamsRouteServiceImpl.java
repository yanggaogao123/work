package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;
import com.gci.schedule.driverless.mapper.ScheduleParamsRouteMapper;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("scheduleParamsRouteService")
public class ScheduleParamsRouteServiceImpl implements ScheduleParamsRouteService {

	@Autowired
	private ScheduleParamsRouteMapper scheduleParamsRouteMapper;

	@Override
	public ScheduleParamsRoute getByTemplateId(Integer templateId) {
		return scheduleParamsRouteMapper.getByTemplateId(templateId);
	}


}
