package com.gci.schedule.driverless.bean.bs;

import java.util.Date;

public class DyChargeBigdataRecord {
    private Long id;

    private Long busId;

    private String busName;

    private Double startSoc;

    private Double predictmileage;

    private Double remainRunMileage;

    private Double chargeNeedSoc;

    private Double chargeStartSoc;

    private Double chargeEndSoc;

    private Double chargeDuration;

    private Date chargeStartTime;

    private Date chargeEndTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getStartSoc() {
        return startSoc;
    }

    public void setStartSoc(Double startSoc) {
        this.startSoc = startSoc;
    }

    public Double getPredictmileage() {
        return predictmileage;
    }

    public void setPredictmileage(Double predictmileage) {
        this.predictmileage = predictmileage;
    }

    public Double getRemainRunMileage() {
        return remainRunMileage;
    }

    public void setRemainRunMileage(Double remainRunMileage) {
        this.remainRunMileage = remainRunMileage;
    }

    public Double getChargeNeedSoc() {
        return chargeNeedSoc;
    }

    public void setChargeNeedSoc(Double chargeNeedSoc) {
        this.chargeNeedSoc = chargeNeedSoc;
    }

    public Double getChargeStartSoc() {
        return chargeStartSoc;
    }

    public void setChargeStartSoc(Double chargeStartSoc) {
        this.chargeStartSoc = chargeStartSoc;
    }

    public Double getChargeEndSoc() {
        return chargeEndSoc;
    }

    public void setChargeEndSoc(Double chargeEndSoc) {
        this.chargeEndSoc = chargeEndSoc;
    }

    public Double getChargeDuration() {
        return chargeDuration;
    }

    public void setChargeDuration(Double chargeDuration) {
        this.chargeDuration = chargeDuration;
    }

    public Date getChargeStartTime() {
        return chargeStartTime;
    }

    public void setChargeStartTime(Date chargeStartTime) {
        this.chargeStartTime = chargeStartTime;
    }

    public Date getChargeEndTime() {
        return chargeEndTime;
    }

    public void setChargeEndTime(Date chargeEndTime) {
        this.chargeEndTime = chargeEndTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}