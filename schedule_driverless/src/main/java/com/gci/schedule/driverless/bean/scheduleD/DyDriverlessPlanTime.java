package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

public class DyDriverlessPlanTime {
    private Long id;

    private Long routeId;

    private Date runDate;

    private Long busId;

    private Long taskId;

    private Date tripBeginTime;

    private Date tripEndTime;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
}