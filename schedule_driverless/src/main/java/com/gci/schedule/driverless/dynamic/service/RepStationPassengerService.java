package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.RouteStationPassenger;
import com.gci.schedule.driverless.dynamic.bean.RouteStationPassengerInfo;

import java.util.List;

public interface RepStationPassengerService {

    RouteStationPassengerInfo getRouteStationPassangerInfo(String runDayNum,String direction,Long routeId);

	RouteStationPassengerInfo dealWithRouteStationPassengerInfo(List<RouteStationPassenger> list, String runDayNum, String direction, Long routeId);

}
