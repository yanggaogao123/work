package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.Route;

public interface RouteService {

	public String getRouteUpDownInfo(Integer routeId);

	public Route getRouteByRouteId(Long routeId);

}
