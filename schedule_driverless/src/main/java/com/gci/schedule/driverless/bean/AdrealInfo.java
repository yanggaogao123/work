package com.gci.schedule.driverless.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 进出站详情
 */
public class AdrealInfo {

    private  Integer adFlag ;//进出站类型

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private  Date adTime ;//进出站时间

    private Integer busId; //车辆Id

    private String busCode; //车辆编码

    private String busName;//车辆名称

    private String busNameFull;//车辆全称

    private  Integer busNumber ;//车序

    private  Integer firstDirection; //出场方向

    private  Integer scheduleId; //排班ID

    private  Integer routeStationId;//线路站点ID

    private  String routeStationName;//线路站点名称

    private  Integer upNumber;//上车人数

    private  Integer downNumber;//下车人数

    private  Integer currentNumber;//车内人数

    private Double  fullLoadRatio;	//满载率

    private String serviceType ; //任务类型

    private String serviceName ; //任务名称

    private  Integer direction; //方向

    private Boolean singleBus;

    public Integer getAdFlag() {
        return adFlag;
    }

    public void setAdFlag(Integer adFlag) {
        this.adFlag = adFlag;
    }

    public Date getAdTime() {
        return adTime;
    }

    public void setAdTime(Date adTime) {
        this.adTime = adTime;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusNameFull() {
        return busNameFull;
    }

    public void setBusNameFull(String busNameFull) {
        this.busNameFull = busNameFull;
    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    public Integer getFirstDirection() {
        return firstDirection;
    }

    public void setFirstDirection(Integer firstDirection) {
        this.firstDirection = firstDirection;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getRouteStationId() {
        return routeStationId;
    }

    public void setRouteStationId(Integer routeStationId) {
        this.routeStationId = routeStationId;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName;
    }

    public Integer getUpNumber() {
        return upNumber;
    }

    public void setUpNumber(Integer upNumber) {
        this.upNumber = upNumber;
    }

    public Integer getDownNumber() {
        return downNumber;
    }

    public void setDownNumber(Integer downNumber) {
        this.downNumber = downNumber;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }

    public Double getFullLoadRatio() {
        return fullLoadRatio;
    }

    public void setFullLoadRatio(Double fullLoadRatio) {
        this.fullLoadRatio = fullLoadRatio;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Boolean getSingleBus() {
        return singleBus;
    }

    public void setSingleBus(Boolean singleBus) {
        this.singleBus = singleBus;
    }
}
