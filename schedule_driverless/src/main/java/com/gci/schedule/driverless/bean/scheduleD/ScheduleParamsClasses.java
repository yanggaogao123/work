package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

//最低发班设置
public class ScheduleParamsClasses {

    private Long routeId;

    private String direction;

    private String beginTime;

    private String endTime;

    private Date beginTimeDate;

    private Date endTimeDate;

    private Short classesNumMin; //最小发班数

    private Long maxDispatchInterval; //最大发班间隔


    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Integer isRegularBus;

    public Integer getIsRegularBus() {
        return isRegularBus;
    }

    public void setIsRegularBus(Integer isRegularBus) {
        this.isRegularBus = isRegularBus;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getDirection() {
        return direction;
    }
    
    public Integer getDirectionIntValue() {
        return Integer.valueOf(direction);
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? null : direction.trim();
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

    public Short getClassesNumMin() {
        return classesNumMin;
    }

    public void setClassesNumMin(Short classesNumMin) {
        this.classesNumMin = classesNumMin;
    }

    public Long getMaxDispatchInterval() {
        return maxDispatchInterval;
    }

    public void setMaxDispatchInterval(Long maxDispatchInterval) {
        this.maxDispatchInterval = maxDispatchInterval;
    }

    public Date getBeginTimeDate() {
        return beginTimeDate;
    }

    public void setBeginTimeDate(Date beginTimeDate) {
        this.beginTimeDate = beginTimeDate;
    }

    public Date getEndTimeDate() {
        return endTimeDate;
    }

    public void setEndTimeDate(Date endTimeDate) {
        this.endTimeDate = endTimeDate;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIntBeginTime() {
        return Integer.parseInt(this.beginTime);
    }
}