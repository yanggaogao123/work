package com.gci.schedule.driverless.bean.bs;

import java.util.Date;

public class BsRoute {
    private Long routeId;

    private Long version;

    private String auditResult;

    private Date auditTime;

    private String auditUser;

    private Date beginDate;

    private String createUser;

    private Date dateCreated;

    private String direction;

    private String downFirstTime;

    private String downLatestTime;

    private Date endDate;

    private Long firstStationId;

    private String firstStationName;

    private String isActive;

    private Long lastStationId;

    private String lastStationName;

    private Date lastUpdated;

    private Double mileage;

    private String operatorType;

    private Long organId;

    private Long organRunId;

    private String recordStatus;

    private String remark;

    private String routeCode;

    private String routeName;

    private String routeVersion;

    private String runningPlace;

    private String shortName;

    private String tripsType;

    private String upFirstTime;

    private String upLatestTime;

    private Long stationTarget;

    private Long sort;

    private String lastOperator;

    private String operation;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult == null ? null : auditResult.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser == null ? null : auditUser.trim();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? null : direction.trim();
    }

    public String getDownFirstTime() {
        return downFirstTime;
    }

    public void setDownFirstTime(String downFirstTime) {
        this.downFirstTime = downFirstTime == null ? null : downFirstTime.trim();
    }

    public String getDownLatestTime() {
        return downLatestTime;
    }

    public void setDownLatestTime(String downLatestTime) {
        this.downLatestTime = downLatestTime == null ? null : downLatestTime.trim();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public void setFirstStationId(Long firstStationId) {
        this.firstStationId = firstStationId;
    }

    public String getFirstStationName() {
        return firstStationName;
    }

    public void setFirstStationName(String firstStationName) {
        this.firstStationName = firstStationName == null ? null : firstStationName.trim();
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive == null ? null : isActive.trim();
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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType == null ? null : operatorType.trim();
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getOrganRunId() {
        return organRunId;
    }

    public void setOrganRunId(Long organRunId) {
        this.organRunId = organRunId;
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

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode == null ? null : routeCode.trim();
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName == null ? null : routeName.trim();
    }

    public String getRouteVersion() {
        return routeVersion;
    }

    public void setRouteVersion(String routeVersion) {
        this.routeVersion = routeVersion == null ? null : routeVersion.trim();
    }

    public String getRunningPlace() {
        return runningPlace;
    }

    public void setRunningPlace(String runningPlace) {
        this.runningPlace = runningPlace == null ? null : runningPlace.trim();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName == null ? null : shortName.trim();
    }

    public String getTripsType() {
        return tripsType;
    }

    public void setTripsType(String tripsType) {
        this.tripsType = tripsType == null ? null : tripsType.trim();
    }

    public String getUpFirstTime() {
        return upFirstTime;
    }

    public void setUpFirstTime(String upFirstTime) {
        this.upFirstTime = upFirstTime == null ? null : upFirstTime.trim();
    }

    public String getUpLatestTime() {
        return upLatestTime;
    }

    public void setUpLatestTime(String upLatestTime) {
        this.upLatestTime = upLatestTime == null ? null : upLatestTime.trim();
    }

    public Long getStationTarget() {
        return stationTarget;
    }

    public void setStationTarget(Long stationTarget) {
        this.stationTarget = stationTarget;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public void setLastOperator(String lastOperator) {
        this.lastOperator = lastOperator == null ? null : lastOperator.trim();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }
}