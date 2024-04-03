package com.gci.schedule.driverless.dynamic.bean;

import java.util.Date;

/**
 * 补电设置
 *
 * @author pdl
 */
public class ScheduleParamsEndurance {

    private Long routeId;

    private Long templateId;

    private Long stationId;

    private String stationName;

    private String beginTime;

    private String endTime;

    private Integer busNumber;

    private String updateUser;

    private Date updateTime;

    //续航里程
    private Integer enduranceMileage;

    private Integer supplyTime;//补电时长

    public Integer getSupplyTime() {
        return supplyTime;
    }

    public void setSupplyTime(Integer supplyTime) {
        this.supplyTime = supplyTime;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getEnduranceMileage() {
        return enduranceMileage;
    }

    public void setEnduranceMileage(Integer enduranceMileage) {
        this.enduranceMileage = enduranceMileage;
    }
}
