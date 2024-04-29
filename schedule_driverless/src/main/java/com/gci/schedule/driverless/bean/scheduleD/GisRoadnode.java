package com.gci.schedule.driverless.bean.scheduleD;

import java.math.BigDecimal;
import java.util.Date;

public class GisRoadnode {
    private Long roadnodeId;

    private Long roadsegId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal distance;

    private Long orderNum;

    private Date dateCreated;

    private Date lastUpdated;

    private String createUser;

    private String updateUser;

    private String remark;

    private Long version;

    private Short isStation;

    private BigDecimal roadId;

    private Long angleDegree;

    private String gpskey;

    public Long getRoadnodeId() {
        return roadnodeId;
    }

    public void setRoadnodeId(Long roadnodeId) {
        this.roadnodeId = roadnodeId;
    }

    public Long getRoadsegId() {
        return roadsegId;
    }

    public void setRoadsegId(Long roadsegId) {
        this.roadsegId = roadsegId;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Short getIsStation() {
        return isStation;
    }

    public void setIsStation(Short isStation) {
        this.isStation = isStation;
    }

    public BigDecimal getRoadId() {
        return roadId;
    }

    public void setRoadId(BigDecimal roadId) {
        this.roadId = roadId;
    }

    public Long getAngleDegree() {
        return angleDegree;
    }

    public void setAngleDegree(Long angleDegree) {
        this.angleDegree = angleDegree;
    }

    public String getGpskey() {
        return gpskey;
    }

    public void setGpskey(String gpskey) {
        this.gpskey = gpskey == null ? null : gpskey.trim();
    }
}