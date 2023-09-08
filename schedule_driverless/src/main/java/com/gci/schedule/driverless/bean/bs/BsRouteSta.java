package com.gci.schedule.driverless.bean.bs;

import java.math.BigDecimal;
import java.util.Date;

public class BsRouteSta {
    private Long routeStaId;

    private Long version;

    private Long arrivalRadius;

    private String busStopCode;

    private String busStopFlag;

    private String createUser;

    private Date dateCreated;

    private Long departureRadius;

    private Date lastUpdated;

    private BigDecimal mileage;

    private Long orderNumber;

    private String recordStatus;

    private String remark;

    private Long routeId;

    private String routeStationName;

    private Long stationId;

    private String stationMark;

    private String updateUser;

    private Long roadsegId;

    private String operation;

    private String isActive;

    private Long stationIdBz;

    public Long getRouteStaId() {
        return routeStaId;
    }

    public void setRouteStaId(Long routeStaId) {
        this.routeStaId = routeStaId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getArrivalRadius() {
        return arrivalRadius;
    }

    public void setArrivalRadius(Long arrivalRadius) {
        this.arrivalRadius = arrivalRadius;
    }

    public String getBusStopCode() {
        return busStopCode;
    }

    public void setBusStopCode(String busStopCode) {
        this.busStopCode = busStopCode == null ? null : busStopCode.trim();
    }

    public String getBusStopFlag() {
        return busStopFlag;
    }

    public void setBusStopFlag(String busStopFlag) {
        this.busStopFlag = busStopFlag == null ? null : busStopFlag.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDepartureRadius() {
        return departureRadius;
    }

    public void setDepartureRadius(Long departureRadius) {
        this.departureRadius = departureRadius;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus == null ? null : recordStatus.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName == null ? null : routeStationName.trim();
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationMark() {
        return stationMark;
    }

    public void setStationMark(String stationMark) {
        this.stationMark = stationMark == null ? null : stationMark.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public Long getRoadsegId() {
        return roadsegId;
    }

    public void setRoadsegId(Long roadsegId) {
        this.roadsegId = roadsegId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive == null ? null : isActive.trim();
    }

    public Long getStationIdBz() {
        return stationIdBz;
    }

    public void setStationIdBz(Long stationIdBz) {
        this.stationIdBz = stationIdBz;
    }
}