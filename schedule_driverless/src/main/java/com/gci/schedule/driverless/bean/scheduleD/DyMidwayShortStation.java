package com.gci.schedule.driverless.bean.scheduleD;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author:cjc
 * @Description:
 * @Date:2020/7/16
 * @Modified by:
 */
public class DyMidwayShortStation {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date runDate;
    private Long busId;
    private String busName;
    private Long upMidStationId;
    private String upMidStationName;
    private Long downMidStationId;
    private String downMidStationName;
    private Long routeId;
    private String userName;
    private Long lastStationId;
    private String type; // 0-删除基础数据,动态数据 1-只删除动态数据
    private Long taskId;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date midWayPlanTime;

    private String midWayTaskName;
    //方向
    private String startDirection;
    //车序
    private Integer startOrderNumber;

    public String getMidWayTaskName() {
        return midWayTaskName;
    }

    public void setMidWayTaskName(String midWayTaskName) {
        this.midWayTaskName = midWayTaskName;
    }

    public Date getMidWayPlanTime() {
        return midWayPlanTime;
    }

    public void setMidWayPlanTime(Date midWayPlanTime) {
        this.midWayPlanTime = midWayPlanTime;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLastStationId() {
        return lastStationId;
    }

    public void setLastStationId(Long lastStationId) {
        this.lastStationId = lastStationId;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
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
        this.busName = busName;
    }

    public Long getUpMidStationId() {
        return upMidStationId;
    }

    public void setUpMidStationId(Long upMidStationId) {
        this.upMidStationId = upMidStationId;
    }

    public String getUpMidStationName() {
        return upMidStationName;
    }

    public void setUpMidStationName(String upMidStationName) {
        this.upMidStationName = upMidStationName;
    }

    public Long getDownMidStationId() {
        return downMidStationId;
    }

    public void setDownMidStationId(Long downMidStationId) {
        this.downMidStationId = downMidStationId;
    }

    public String getDownMidStationName() {
        return downMidStationName;
    }

    public void setDownMidStationName(String downMidStationName) {
        this.downMidStationName = downMidStationName;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(String startDirection) {
        this.startDirection = startDirection;
    }

    public Integer getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Integer startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }
}
