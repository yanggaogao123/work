package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

//停站时长设定(多时段)
public class ScheduleParamsAnchor {

    private Long routeId;

    private String direction;

    private String beginTime; //开始时间

    private String endTime; //结束时间

    private Short anchorDurationMin; //最小停站时长

    private Short anchorDurationMax; //最大停站时长

    private Short busOccupancy; //满载率

    private String peakType;

    private Date beginTimeDate;

    private Date endTimeDate;

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;


    public Short getBusOccupancy() {
        return busOccupancy;
    }

    public void setBusOccupancy(Short busOccupancy) {
        this.busOccupancy = busOccupancy;
    }

    public String getPeakType() {
        return peakType;
    }

    public void setPeakType(String peakType) {
        this.peakType = peakType;
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
    
    public int getDirectionIntValue() {
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

    public Short getAnchorDurationMin() {
        return anchorDurationMin;
    }


    public Short getAnchorDurationMax() {
        return anchorDurationMax;
    }

    public void setAnchorDurationMax(Short anchorDurationMax) {
        this.anchorDurationMax = anchorDurationMax;
    }

    public void setAnchorDurationMin(Short anchorDurationMin) {
        this.anchorDurationMin = anchorDurationMin;
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

    public Integer getIntBeginTime(){
        return Integer.parseInt(this.getBeginTime());
    }
}