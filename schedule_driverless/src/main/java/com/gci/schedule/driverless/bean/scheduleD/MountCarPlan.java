package com.gci.schedule.driverless.bean.scheduleD;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author:cjc
 * @Description:排班计划管理,挂车按钮显示计划
 * @Date:2020/10/29
 * @Modified by:
 */
public class MountCarPlan {
    private  Integer scheduleId; //排班ID

    @JsonFormat(pattern ="yyyy-MM-dd",timezone = "GMT+8")
    private Date planDate;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date planTime;
    //线路id
    private Integer routeId;
    //车辆ID
    private Integer busId;
    //车辆名称
    private String busName;
    private String routeName;
    //方向
    private String startDirection;
    //车序
    private Integer startOrderNumber;
    //车辆编码
    private String busCode;
    //计划类型
    private Integer runMode;

    //车辆ID-参考日期
    private Integer oBusId;
    //车辆名称-参考日期
    private String oBusName;
    //方向-参考日期
    private String oStartDirection;
    //车序-参考日期
    private Integer oStartOrderNumber;

    private Integer firstStationId;
    private String firstStationName;

    //首轮排班任务
    private String serviceName;

    //中途出场
    private DyMidwayShortStation dyMidwayShortStation;

    //首轮发班时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date firstRoundPlanTime;

    //首轮发班子线路
    private Long firstRoundTaskId;

    //首轮发班子线路
    private String firstRoundTaskName;

    private Integer syncPlan;

    private String busNameStr;

    public Integer getSyncPlan() {
        return syncPlan;
    }

    public void setSyncPlan(Integer syncPlan) {
        this.syncPlan = syncPlan;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getFirstRoundPlanTime() {
        return firstRoundPlanTime;
    }

    public void setFirstRoundPlanTime(Date firstRoundPlanTime) {
        this.firstRoundPlanTime = firstRoundPlanTime;
    }

    public Long getFirstRoundTaskId() {
        return firstRoundTaskId;
    }

    public void setFirstRoundTaskId(Long firstRoundTaskId) {
        this.firstRoundTaskId = firstRoundTaskId;
    }

    public String getFirstRoundTaskName() {
        return firstRoundTaskName;
    }

    public void setFirstRoundTaskName(String firstRoundTaskName) {
        this.firstRoundTaskName = firstRoundTaskName;
    }

    public DyMidwayShortStation getDyMidwayShortStation() {
        return dyMidwayShortStation;
    }

    public void setDyMidwayShortStation(DyMidwayShortStation dyMidwayShortStation) {
        this.dyMidwayShortStation = dyMidwayShortStation;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(String startDirection) {
        this.startDirection = startDirection;
    }

    public Integer getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Integer startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }

    public Integer getoBusId() {
        return oBusId;
    }

    public void setoBusId(Integer oBusId) {
        this.oBusId = oBusId;
    }

    public String getoBusName() {
        return oBusName;
    }

    public void setoBusName(String oBusName) {
        this.oBusName = oBusName;
    }

    public String getoStartDirection() {
        return oStartDirection;
    }

    public void setoStartDirection(String oStartDirection) {
        this.oStartDirection = oStartDirection;
    }

    public Integer getoStartOrderNumber() {
        return oStartOrderNumber;
    }

    public void setoStartOrderNumber(Integer oStartOrderNumber) {
        this.oStartOrderNumber = oStartOrderNumber;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public Integer getRunMode() {
        return runMode;
    }

    public void setRunMode(Integer runMode) {
        this.runMode = runMode;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public String getBusNameStr() {
        return busNameStr;
    }

    public void setBusNameStr(String busNameStr) {
        this.busNameStr = busNameStr;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Integer getFirstStationId() {
        return firstStationId;
    }

    public void setFirstStationId(Integer firstStationId) {
        this.firstStationId = firstStationId;
    }

    public String getFirstStationName() {
        return firstStationName;
    }

    public void setFirstStationName(String firstStationName) {
        this.firstStationName = firstStationName;
    }
}
