package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.test.RouteSchedule;
import com.gci.schedule.driverless.dynamic.test.ScheduleParam;

public class SchedulePlanResult {
	
	private ScheduleParamPreset scheduleParamPreset;
	
	private RouteSchedule routeSchedule;
	
	private ScheduleParam scheduleParam;
	
	private SchedulePlan4Mj schedulePlan4Mj;

	public ScheduleParamPreset getScheduleParamPreset() {
		return scheduleParamPreset;
	}

	public void setScheduleParamPreset(ScheduleParamPreset scheduleParamPreset) {
		this.scheduleParamPreset = scheduleParamPreset;
	}
	
	public RouteSchedule getRouteSchedule() {
		return routeSchedule;
	}

	public void setRouteSchedule(RouteSchedule routeSchedule) {
		this.routeSchedule = routeSchedule;
	}

	public ScheduleParam getScheduleParam() {
		return scheduleParam;
	}

	public void setScheduleParam(ScheduleParam scheduleParam) {
		this.scheduleParam = scheduleParam;
	}

	public SchedulePlan4Mj getSchedulePlan4Mj() {
		return schedulePlan4Mj;
	}

	public void setSchedulePlan4Mj(SchedulePlan4Mj schedulePlan4Mj) {
		this.schedulePlan4Mj = schedulePlan4Mj;
	}
	
}
