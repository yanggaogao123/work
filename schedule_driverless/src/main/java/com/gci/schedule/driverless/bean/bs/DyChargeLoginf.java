package com.gci.schedule.driverless.bean.bs;

import java.util.Date;

public class DyChargeLoginf {
    private Long id;

    private Long routeId;

    private Long busId;

    private String busName;

    private Date updateTime;

    private String remark;

    private Long organId;

    private Long firstStationId;

    private Long lastStationId;

    private String lastStationName;

    private String firstStationName;

    private Date tripBeginTime;

    private String emplpoyeeName;

    private String status;

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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public void setFirstStationId(Long firstStationId) {
        this.firstStationId = firstStationId;
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
        this.lastStationName = lastStationName == null ? null : lastStationName.trim();
    }

    public String getFirstStationName() {
        return firstStationName;
    }

    public void setFirstStationName(String firstStationName) {
        this.firstStationName = firstStationName == null ? null : firstStationName.trim();
    }

    public Date getTripBeginTime() {
        return tripBeginTime;
    }

    public void setTripBeginTime(Date tripBeginTime) {
        this.tripBeginTime = tripBeginTime;
    }

    public String getEmplpoyeeName() {
        return emplpoyeeName;
    }

    public void setEmplpoyeeName(String emplpoyeeName) {
        this.emplpoyeeName = emplpoyeeName == null ? null : emplpoyeeName.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}