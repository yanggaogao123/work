package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.*;

import java.util.Date;

public interface SimulationService {

	public  Double getIntersiteTime (IntersiteTimeParams intersiteTimeParams);

	Integer getWasteTime(long routeId, long fromRouteStaId, long toRouteStaId, Date tripLogBeginTime, Long routeFromId, Long routeToId, Integer wasteTime);

	void clearCache(Long routeId);
}
