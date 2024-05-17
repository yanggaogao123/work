package com.gci.schedule.driverless.bean.scheduleD;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author: allan
 * @Date: 2019/4/19 16:19
 */
public class Schedule {

    //排班ID
    private Long scheduleId;
    //车辆编码
    private String busCode;
    //车序
    private Long busNumber;
    //出场方向(车辆首轮出场方向)
    private String firstDirection;
    //开始时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date tripBeginTime;
    //结束时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date tripEndTime;
    //任务方向
    private String direction;
    //任务类型
    private String serviceType;
    //任务名称
    private String serviceName;

    private Integer busId;

    private String busName;

    private Long firstRouteStaId ; //开始站点

    private Long lastRouteStaId ; //结束站点

    private String firstRouteStaName ; //开始站点名称

    private String lastRouteStaName ; //结束站点名称

    private Double runMileage;

    public Double getRunMileage() {
        return runMileage;
    }

    public void setRunMileage(Double runMileage) {
        this.runMileage = runMileage;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public Long getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Long busNumber) {
        this.busNumber = busNumber;
    }

    public String getFirstDirection() {
        return firstDirection;
    }

    public void setFirstDirection(String firstDirection) {
        this.firstDirection = firstDirection;
    }

    public Date getTripBeginTime() {
        return tripBeginTime;
    }

    public void setTripBeginTime(Date tripBeginTime) {
        this.tripBeginTime = tripBeginTime;
    }

    public Date getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(Date tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
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

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
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
        this.firstRouteStaName = firstRouteStaName;
    }

    public String getLastRouteStaName() {
        return lastRouteStaName;
    }

    public void setLastRouteStaName(String lastRouteStaName) {
        this.lastRouteStaName = lastRouteStaName;
    }
}
