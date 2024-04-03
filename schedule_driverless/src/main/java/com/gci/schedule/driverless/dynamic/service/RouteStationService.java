package com.gci.schedule.driverless.dynamic.service;

import java.util.List;
import java.util.Map;

import com.gci.schedule.driverless.dynamic.bean.RouteSta;

public interface RouteStationService {
	public String getListByRouteId(Long routeId);

	public List<RouteSta> getRouteStaListByRouteId(Long routeId);

	public Map<Long, RouteSta> getRouteStaMapByRouteId(Long routeId);

	public void clearRouteStaCache();

}
