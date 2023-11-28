package com.gci.schedule.driverless.bean.scheduleD;

import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gci.schedule.driverless.util.DateUtil;

import java.math.BigDecimal;
import java.util.Date;

public class DySchedulePlanDriverless {
    private Long scheduleId;

    private Long routeId;

    private String routeCode;

    private Long supportRouteId;

    private Date planDate;

    private Date planTime;

    private String startDirection;

    private Short startOrderNumber;

    private Date tripEndTime;

    private String serviceType;

    private String serviceName;

    private String direction;

    private String busCode;

    private Long busId;

    private String busName;

    private Long firstRouteStaId;

    private Long lastRouteStaId;

    private String firstRouteStaName;

    private String lastRouteStaName;

    private Double runMileage;

    private String peakType;

    private Date firstRoundPlanTime;

    private Long firstRoundTaskId;

    private Short syncPlan;

    private Integer classes;

    private Integer supportClasses;

    private Integer interval;

    private Integer stopTime;

    private Date passengerData;

    private Integer passengerNum;

    @JsonIgnore
    private String busNameFull;//车辆全称

    private Boolean singleBus;

    private Integer status;

    //周转时间
    private Double fullTime;

    public int getPlanTimeInt(){
        return Convert.toInt(DateUtil.date2Str(this.planTime,DateUtil.hhmm));
    }

    public Double getFullTime() {
        return fullTime;
    }

    public void setFullTime(Double fullTime) {
        this.fullTime = fullTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBusNameFull() {
        return busNameFull;
    }

    public void setBusNameFull(String busNameFull) {
        this.busNameFull = busNameFull;
    }

    public Boolean isSingleBus() {
        return singleBus;
    }

    public void setSingleBus(Boolean singleBus) {
        this.singleBus = singleBus;
    }

    public Long getSupportRouteId() {
        return supportRouteId;
    }

    public void setSupportRouteId(Long supportRouteId) {
        this.supportRouteId = supportRouteId;
    }

    public Date getPassengerData() {
        return passengerData;
    }

    public void setPassengerData(Date passengerData) {
        this.passengerData = passengerData;
    }

    public Integer getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(Integer passengerNum) {
        this.passengerNum = passengerNum;
    }

    public Integer getClasses() {
        return classes;
    }

    public void setClasses(Integer classes) {
        this.classes = classes;
    }

    public Integer getSupportClasses() {
        return supportClasses;
    }

    public void setSupportClasses(Integer supportClasses) {
        this.supportClasses = supportClasses;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getStopTime() {
        return stopTime;
    }

    public void setStopTime(Integer stopTime) {
        this.stopTime = stopTime;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode == null ? null : routeCode.trim();
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(String startDirection) {
        this.startDirection = startDirection == null ? null : startDirection.trim();
    }

    public Short getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Short startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }

    public Date getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(Date tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null ? null : serviceType.trim();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? null : direction.trim();
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode == null ? null : busCode.trim();
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName == null ? null : busName.trim();
    }

    public Long getFirstRouteStaId() {
        return firstRouteStaId;
    }

    public void setFirstRouteStaId(Long firstRouteStaId) {
        this.firstRouteStaId = firstRouteStaId;
    }

    public Long getLastRouteStaId() {
        return lastRouteStaId;
    }

    public void setLastRouteStaId(Long lastRouteStaId) {
        this.lastRouteStaId = lastRouteStaId;
    }

    public String getFirstRouteStaName() {
        return firstRouteStaName;
    }

    public void setFirstRouteStaName(String firstRouteStaName) {
        this.firstRouteStaName = firstRouteStaName == null ? null : firstRouteStaName.trim();
    }

    public String getLastRouteStaName() {
        return lastRouteStaName;
    }

    public void setLastRouteStaName(String lastRouteStaName) {
        this.lastRouteStaName = lastRouteStaName == null ? null : lastRouteStaName.trim();
    }

    public Double getRunMileage() {
        return runMileage;
    }

    public void setRunMileage(Double runMileage) {
        this.runMileage = runMileage;
    }

    public String getPeakType() {
        return peakType;
    }

    public void setPeakType(String peakType) {
        this.peakType = peakType == null ? null : peakType.trim();
    }

    public Date getFirstRoundPlanTime() {
        return firstRoundPlanTime;
    }

    public void setFirstRoundPlanTime(Date firstRoundPlanTime) {
        this.firstRoundPlanTime = firstRoundPlanTime;
    }

    public Long getFirstRoundTaskId() {
        return firstRoundTaskId;
    }

    public void setFirstRoundTaskId(Long firstRoundTaskId) {
        this.firstRoundTaskId = firstRoundTaskId;
    }

    public Short getSyncPlan() {
        return syncPlan;
    }

    public void setSyncPlan(Short syncPlan) {
        this.syncPlan = syncPlan;
    }
}