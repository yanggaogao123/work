package com.gci.schedule.driverless.bean.scheduleD;

public class DyDriverlessRouteSta {
    private Long routeStaId;

    private String routeStationName;

    private String routeStationSimplename;

    private String stationMark;

    private Long stationId;

    private Long routeId;

    private String routeName;

    public Long getRouteStaId() {
        return routeStaId;
    }

    public void setRouteStaId(Long routeStaId) {
        this.routeStaId = routeStaId;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName == null ? null : routeStationName.trim();
    }

    public String getRouteStationSimplename() {
        return routeStationSimplename;
    }

    public void setRouteStationSimplename(String routeStationSimplename) {
        this.routeStationSimplename = routeStationSimplename == null ? null : routeStationSimplename.trim();
    }

    public String getStationMark() {
        return stationMark;
    }

    public void setStationMark(String stationMark) {
        this.stationMark = stationMark == null ? null : stationMark.trim();
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName == null ? null : routeName.trim();
    }
}