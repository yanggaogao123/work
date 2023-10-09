package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

//线路模板明细
public class DispatchTemplateDetail {

    private Integer routeId;

    private Integer templateId;

    private Integer applyDay; //适用日期(1:周日;2:周一;3:周二......7:周六)

    private Integer updateUser;

    private Date updateTime;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getApplyDay() {
        return applyDay;
    }

    public void setApplyDay(Integer applyDay) {
        this.applyDay = applyDay;
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
}