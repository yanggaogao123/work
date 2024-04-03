package com.gci.schedule.driverless.dynamic.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//单班车设定
public class ScheduleParamsSingle {

    private Long routeId;

    private String stopBeginTime; //中停开始时间

    private String stopEndTime; //中停截止时间

    private String runBeginTime; //复行开始时间

    private String endBeginTime; //收工开始时间

    private String earliestBusNumber; //最早车位

    private Date stopBeginTimeDate; //中停开始时间

    private Date stopEndTimeDate; //中停截止时间

    private Date runBeginTimeDate; //复行开始时间

    private Date endBeginTimeDate; //收工开始时间

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Integer upBusCount; //上行中停车数

    private Integer downBusCount; //下行中停车数

    private Integer minimumRestTime; //最短中停休息时间

    private List<ScheduleParamShift> shifts; //班别列表

    private Map<Integer, Date> runBeginTimeMap=new HashMap<Integer, Date>();

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getStopBeginTime() {
        return stopBeginTime;
    }

    public void setStopBeginTime(String stopBeginTime) {
        this.stopBeginTime = stopBeginTime;
    }

    public String getStopEndTime() {
        return stopEndTime;
    }

    public void setStopEndTime(String stopEndTime) {
        this.stopEndTime = stopEndTime;
    }

    public String getRunBeginTime() {
        return runBeginTime;
    }

    public void setRunBeginTime(String runBeginTime) {
        this.runBeginTime = runBeginTime;
    }

    public String getEndBeginTime() {
        return endBeginTime;
    }

    public void setEndBeginTime(String endBeginTime) {
        this.endBeginTime = endBeginTime;
    }

    public String getEarliestBusNumber() {
        return earliestBusNumber;
    }

    public void setEarliestBusNumber(String earliestBusNumber) {
        this.earliestBusNumber = earliestBusNumber;
    }

    public Date getStopBeginTimeDate() {
        return stopBeginTimeDate;
    }

    public void setStopBeginTimeDate(Date stopBeginTimeDate) {
        this.stopBeginTimeDate = stopBeginTimeDate;
    }

    public Date getStopEndTimeDate() {
        return stopEndTimeDate;
    }

    public void setStopEndTimeDate(Date stopEndTimeDate) {
        this.stopEndTimeDate = stopEndTimeDate;
    }

    public Date getRunBeginTimeDate() {
        return runBeginTimeDate;
    }

    public void setRunBeginTimeDate(Date runBeginTimeDate) {
        this.runBeginTimeDate = runBeginTimeDate;
    }

    public Date getEndBeginTimeDate() {
        return endBeginTimeDate;
    }

    public void setEndBeginTimeDate(Date endBeginTimeDate) {
        this.endBeginTimeDate = endBeginTimeDate;
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

    public Integer getUpBusCount() {
        return upBusCount;
    }

    public void setUpBusCount(Integer upBusCount) {
        this.upBusCount = upBusCount;
    }

    public Integer getDownBusCount() {
        return downBusCount;
    }

    public void setDownBusCount(Integer downBusCount) {
        this.downBusCount = downBusCount;
    }

    public Integer getMinimumRestTime() {
        return minimumRestTime;
    }

    public void setMinimumRestTime(Integer minimumRestTime) {
        this.minimumRestTime = minimumRestTime;
    }

    public List<ScheduleParamShift> getShifts() {
        return shifts;
    }

    public void setShifts(List<ScheduleParamShift> shifts) {
        this.shifts = shifts;
    }
    
    public void setRunBeginTime(int direction,Date runBeginTime) {
    	runBeginTimeMap.put(direction, runBeginTime);
    }
    
    public Date getRunBeginTime(int direction) {
    	return runBeginTimeMap.get(direction);
    }
    
}