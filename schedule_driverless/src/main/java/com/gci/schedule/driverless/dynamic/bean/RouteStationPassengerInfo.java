package com.gci.schedule.driverless.dynamic.bean;

import java.util.List;

//线路客流列表
public class RouteStationPassengerInfo {

    private String runDayNum;

    private String direction;

    private Long routeId;

    private List<List<RouteStationPassenger>> routeStationPassenger2DList ;

    public String getRunDayNum() {
        return runDayNum;
    }

    public void setRunDayNum(String runDayNum) {
        this.runDayNum = runDayNum;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public List<List<RouteStationPassenger>> getRouteStationPassenger2DList() {
        return routeStationPassenger2DList;
    }

    public void setRouteStationPassenger2DList(List<List<RouteStationPassenger>> routeStationPassenger2DList) {
        this.routeStationPassenger2DList = routeStationPassenger2DList;
    }
}