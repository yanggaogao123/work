package com.gci.schedule.driverless.service.schedule;



import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsSingle;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleParamsSingleService {
	int updateByTemplateId(Map<String, Object> params);
	ScheduleParamsSingle getByTemplateId(Integer templateId);
	List<ScheduleParamsSingle> selectList();

    List<ScheduleParamsSingle> selectByRouteId(Long routeId);

    List<Integer> getShiftsTypeByRouteIdAndPlanDate(Integer routeId, Date date, Integer ptemplateId);
}
