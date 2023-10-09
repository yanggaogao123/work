package com.gci.schedule.driverless.bean;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 计划线路配置入参类
 * @author pdl
 */
@Validated
public class ScheduleRouteConfigParams {

    @NotNull
    private Integer routeId;
    @NotBlank
    private String routeName;
    @NotNull
    private Integer organId;
    @NotNull
    private Integer organRunId;

    private String organName;

    private String routeCode;

    private String updateUser;
    private Date updateTime;

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getOrganId() {
        return organId;
    }

    public void setOrganId(Integer organId) {
        this.organId = organId;
    }

    public Integer getOrganRunId() {
        return organRunId;
    }

    public void setOrganRunId(Integer organRunId) {
        this.organRunId = organRunId;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
