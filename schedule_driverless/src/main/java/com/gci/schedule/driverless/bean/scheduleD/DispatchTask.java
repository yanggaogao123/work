package com.gci.schedule.driverless.bean.scheduleD;

/**
 * @Author:cjc
 * @Description:v_dispatch_task
 * @Date:2021/2/4
 * @Modified by: lvruikai
 */
public class DispatchTask {
    private Long routeSubId;
    private String routeSubName;
    private String direction;
    private String serviceName;
    private String serviceType;
    private String serviceNumber;
    private Long firstStationId;
    private String firstStationName;

    private Long lastStationId;

    private String lastStationName;

    private String name;

    public String getName() {
        return routeSubName + '[' + (routeSubId > 0 ? 'T' : 'S') + ':' + routeSubId + ']';
    }

    public Long getRouteSubId() {
        return routeSubId;
    }

    public void setRouteSubId(Long routeSubId) {
        this.routeSubId = routeSubId;
    }

    public String getRouteSubName() {
        return routeSubName;
    }

    public void setRouteSubName(String routeSubName) {
        this.routeSubName = routeSubName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public void setFirstStationId(Long firstStationId) {
        this.firstStationId = firstStationId;
    }

    public String getFirstStationName() {
        return firstStationName;
    }

    public void setFirstStationName(String firstStationName) {
        this.firstStationName = firstStationName;
    }

    public Long getLastStationId() {
        return lastStationId;
    }

    public void setLastStationId(Long lastStationId) {
        this.lastStationId = lastStationId;
    }

    public String getLastStationName() {
        return lastStationName;
    }

    public void setLastStationName(String lastStationName) {
        this.lastStationName = lastStationName;
    }
}
