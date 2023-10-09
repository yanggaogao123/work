package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.scheduleD.Route;
import com.gci.schedule.driverless.bean.scheduleD.RouteUpDownInfo;

import java.util.List;
import java.util.Map;

public interface RouteService {
    List<RouteUpDownInfo> getRouteUpDownInfo(Long routeId);
    List<Map> getRouteListNew(String organId);
    Route getRouteByRouteId(Long routeId);
}
