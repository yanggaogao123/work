package com.gci.schedule.driverless.bean.scheduleD;

public class DyDriverlessConfig {
    private Long id;

    private Long routeId;

    private String routeName;

    private Long supportRouteId;

    private String supportRouteName;

    private Long driverlessRouteId;

    private String driverlessRouteName;

    private Integer supportPassengerNum;

    private Integer driverlessPassengerNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSupportRouteId() {
        return supportRouteId;
    }

    public void setSupportRouteId(Long supportRouteId) {
        this.supportRouteId = supportRouteId;
    }

    public String getSupportRouteName() {
        return supportRouteName;
    }

    public void setSupportRouteName(String supportRouteName) {
        this.supportRouteName = supportRouteName == null ? null : supportRouteName.trim();
    }

    public Long getDriverlessRouteId() {
        return driverlessRouteId;
    }

    public void setDriverlessRouteId(Long driverlessRouteId) {
        this.driverlessRouteId = driverlessRouteId;
    }

    public String getDriverlessRouteName() {
        return driverlessRouteName;
    }

    public void setDriverlessRouteName(String driverlessRouteName) {
        this.driverlessRouteName = driverlessRouteName == null ? null : driverlessRouteName.trim();
    }

    public Integer getSupportPassengerNum() {
        return supportPassengerNum;
    }

    public void setSupportPassengerNum(Integer supportPassengerNum) {
        this.supportPassengerNum = supportPassengerNum;
    }

    public Integer getDriverlessPassengerNum() {
        return driverlessPassengerNum;
    }

    public void setDriverlessPassengerNum(Integer driverlessPassengerNum) {
        this.driverlessPassengerNum = driverlessPassengerNum;
    }
}