package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.*;

/**
 * @Author: allan
 * @Date: 2019/4/19 16:49
 */
public interface SchedulePlanService {

	void generateNew(ScheduleParamPreset scheduleParamPreset);

	SchedulePlanResult generate(ScheduleParamPreset scheduleParamPreset);

}
