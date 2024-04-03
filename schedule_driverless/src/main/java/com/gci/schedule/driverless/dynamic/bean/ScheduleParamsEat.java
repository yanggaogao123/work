package com.gci.schedule.driverless.dynamic.bean;

import java.util.Date;

/**
 * @Author: allan
 * @Date: 2019/5/6 18:49
 */
public class ScheduleParamsEat {

    private Long id;
    private Long routeId;
    //吃饭总站
    private String direction;
    //开始时间
    private String beginTime;
    //吃饭时间
    private Short eatTime;

    private Date beginTimeDate;

    private Date endTimeDate;

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

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

    public String getDirection() {
        return direction;
    }
    
    public int getDirectionIntValue() {
        return Integer.valueOf(direction);
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public Short getEatTime() {
        return eatTime;
    }

    public void setEatTime(Short eatTime) {
        this.eatTime = eatTime;
    }

    public Date getBeginTimeDate() {
        return beginTimeDate;
    }

    public void setBeginTimeDate(Date beginTimeDate) {
        this.beginTimeDate = beginTimeDate;
    }

    public Date getEndTimeDate() {
        return endTimeDate;
    }

    public void setEndTimeDate(Date endTimeDate) {
        this.endTimeDate = endTimeDate;
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
}
