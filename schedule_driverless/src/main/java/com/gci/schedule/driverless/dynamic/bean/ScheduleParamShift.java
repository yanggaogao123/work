package com.gci.schedule.driverless.dynamic.bean;

import java.util.Date;

/**
 * 班别
 *
 * @author pdl
 */
public class ScheduleParamShift {

    //线路ID
    private Long routeId;

    //模板ID
    private Long templateId;

    //班别类型 0：早半班 1：晚半班 2：中班 3.双班中停
    private Integer shiftType;

    //班别名称
    private String shiftName;

    //序号
    private Integer orderNumber;

    //上班时间
    private String startTime;

    //下班时间
    private String endTime;

    private String updateUser;

    private Date updateTime;

    public ScheduleParamShift() {
    }

    public ScheduleParamShift(Long routeId, Long templateId, Integer shiftType, Integer orderNumber, String startTime, String endTime, String updateUser) {
        this.routeId = routeId;
        this.templateId = templateId;
        this.shiftType = shiftType;
        this.orderNumber = orderNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.updateUser = updateUser;
        switch (shiftType) {
            case 0:
                this.shiftName = "早半班";
                break;
            case 1:
                this.shiftName = "晚半班";
                break;
            case 2:
                this.shiftName = "中班";
                break;
            case 3:
                this.shiftName = "双班中停";
                break;
        }
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getShiftType() {
        return shiftType;
    }

    public void setShiftType(Integer shiftType) {
        this.shiftType = shiftType;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
