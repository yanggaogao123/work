package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessRouteSta;

import java.util.List;

public interface RouteStationService {
	String getListByRouteId(Long routeId);

	List<DyDriverlessRouteSta> selectByRouteId(Long routeId);

}
