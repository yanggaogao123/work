package com.gci.schedule.driverless.bean.scheduleD;

public class DyDriverlessConfig {
    private Long id;

    private Long routeId;

    private String routeName;

    private Long supportRouteId;

    private String supportRouteName;

    private Integer supportPassengerNum;

    //是否无人车支援，0否1是
    private Integer isDriverless;

    //释义见枚举类SimulationType
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsDriverless() {
        return isDriverless;
    }

    public void setIsDriverless(Integer isDriverless) {
        this.isDriverless = isDriverless;
    }

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

    public Integer getSupportPassengerNum() {
        return supportPassengerNum;
    }
    public void setSupportPassengerNum(Integer supportPassengerNum) {
        this.supportPassengerNum = supportPassengerNum;
    }

}