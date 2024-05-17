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

    //主线路支援车出车站点名称
    private String mainStartStationName;

    private Long mainStartStationId;

    //主线路支援车收车站点名称
    private String mainEndStationName;

    private Long mainEndStationId;

    //子线路支援车出车站点名称
    private String subStartStationName;

    private Long subStartStationId;

    //子线路支援车出车站点名称
    private String subEndStationName;

    private Long subEndStationId;

    //主线路支援车到支援起点距离 单位：公里
    private Double mainStartStaionDistance;

    //主线路支援车到支援起点用时 单位：分钟
    private Long mainStartStationTime;

    //子线路支援车到支援起点距离 单位：公里
    private Double subStartStaionDistance;

    //子线路支援车到支援起点用时 单位：分钟
    private Long subStartStationTime;

    public Long getMainStartStationId() {
        return mainStartStationId;
    }

    public void setMainStartStationId(Long mainStartStationId) {
        this.mainStartStationId = mainStartStationId;
    }

    public Long getMainEndStationId() {
        return mainEndStationId;
    }

    public void setMainEndStationId(Long mainEndStationId) {
        this.mainEndStationId = mainEndStationId;
    }

    public Long getSubStartStationId() {
        return subStartStationId;
    }

    public void setSubStartStationId(Long subStartStationId) {
        this.subStartStationId = subStartStationId;
    }

    public Long getSubEndStationId() {
        return subEndStationId;
    }

    public void setSubEndStationId(Long subEndStationId) {
        this.subEndStationId = subEndStationId;
    }

    public String getMainStartStationName() {
        return mainStartStationName;
    }

    public void setMainStartStationName(String mainStartStationName) {
        this.mainStartStationName = mainStartStationName;
    }

    public String getMainEndStationName() {
        return mainEndStationName;
    }

    public void setMainEndStationName(String mainEndStationName) {
        this.mainEndStationName = mainEndStationName;
    }

    public String getSubStartStationName() {
        return subStartStationName;
    }

    public void setSubStartStationName(String subStartStationName) {
        this.subStartStationName = subStartStationName;
    }

    public String getSubEndStationName() {
        return subEndStationName;
    }

    public void setSubEndStationName(String subEndStationName) {
        this.subEndStationName = subEndStationName;
    }

    public Double getMainStartStaionDistance() {
        return mainStartStaionDistance;
    }

    public void setMainStartStaionDistance(Double mainStartStaionDistance) {
        this.mainStartStaionDistance = mainStartStaionDistance;
    }

    public Long getMainStartStationTime() {
        return mainStartStationTime;
    }

    public void setMainStartStationTime(Long mainStartStationTime) {
        this.mainStartStationTime = mainStartStationTime;
    }

    public Double getSubStartStaionDistance() {
        return subStartStaionDistance;
    }

    public void setSubStartStaionDistance(Double subStartStaionDistance) {
        this.subStartStaionDistance = subStartStaionDistance;
    }

    public Long getSubStartStationTime() {
        return subStartStationTime;
    }

    public void setSubStartStationTime(Long subStartStationTime) {
        this.subStartStationTime = subStartStationTime;
    }

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