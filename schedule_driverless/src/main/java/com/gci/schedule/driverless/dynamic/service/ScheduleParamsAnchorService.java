package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsAnchor;

import java.util.List;

public interface ScheduleParamsAnchorService {

	List<ScheduleParamsAnchor> getByTemplateId(Integer templateId);

}
