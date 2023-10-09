package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

//线路运营参数
public class ScheduleParamsRoute {

    private Long routeId;

    private Integer singleRunMax; //单班车最大运营时长

    private Integer doubleRunMax; //双班车最大运营时长

    private String earlyEndTime; //早收开始时间

    private String upLastestBeginTime; //上行最晚出车时间

    private String downLastestBeginTime; //下行最晚出车时间

    private String upDirection; //0:上行无出车终站;1:上行有出车终站

    private String downDirection; //0:下行无出车终站;1:下行有出车终站

    private Date earlyEndTimeDate;

    private Date upLastestBeginTimeDate;

    private Date downLastestBeginTimeDate;

    private Integer vehicleContent; //车内容量
    
    private Integer maxBusNumber;//最大配车数
    
    private Integer endDirection;//停场方向(0:上行总站;1:下行总站;2:两边总站)

    private Integer upBeginNum;//上行停场车辆数

    private Integer downBeginNum;//下行停场车辆数

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Integer isRegularBus; //是否定点班车

    private Integer isBackInsert; //是否反插

    private Integer arriveStaStopTimeUp; //上行到站即走停站时间

    private Integer arriveStaStopTimeDown;  //下行到站即走停站时间
    //上下行行车时间起始和结束范围
    private Integer upDriverTimeStart;
    private Integer upDriverTimeEnd;
    private Integer downDriverTimeStart;
    private Integer downDriverTimeEnd;

    private Integer enduranceMileage; //补电设置续航里程

    private Integer doubleRunMaxMor; // 双班（早班）运营时长
    private Integer doubleRunMaxNig; // 双班（晚班）运营时长
    private String handoverBeginTime; // 交接班开始时间
    private String handoverEndTime; // 交接班结束时间

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Integer getSingleRunMax() {
        return singleRunMax;
    }

    public void setSingleRunMax(Integer singleRunMax) {
        this.singleRunMax = singleRunMax;
    }

    public Integer getDoubleRunMax() {
        return doubleRunMax;
    }

    public void setDoubleRunMax(Integer doubleRunMax) {
        this.doubleRunMax = doubleRunMax;
    }

    public String getEarlyEndTime() {
        return earlyEndTime;
    }

    public void setEarlyEndTime(String earlyEndTime) {
        this.earlyEndTime = earlyEndTime;
    }

    public String getUpLastestBeginTime() {
        return upLastestBeginTime;
    }

    public void setUpLastestBeginTime(String upLastestBeginTime) {
        this.upLastestBeginTime = upLastestBeginTime;
    }

    public String getDownLastestBeginTime() {
        return downLastestBeginTime;
    }

    public void setDownLastestBeginTime(String downLastestBeginTime) {
        this.downLastestBeginTime = downLastestBeginTime;
    }

    public String getUpDirection() {
        return upDirection;
    }

    public void setUpDirection(String upDirection) {
        this.upDirection = upDirection;
    }

    public String getDownDirection() {
        return downDirection;
    }

    public void setDownDirection(String downDirection) {
        this.downDirection = downDirection;
    }

    public Date getEarlyEndTimeDate() {
        return earlyEndTimeDate;
    }

    public void setEarlyEndTimeDate(Date earlyEndTimeDate) {
        this.earlyEndTimeDate = earlyEndTimeDate;
    }

    public Date getUpLastestBeginTimeDate() {
        return upLastestBeginTimeDate;
    }

    public void setUpLastestBeginTimeDate(Date upLastestBeginTimeDate) {
        this.upLastestBeginTimeDate = upLastestBeginTimeDate;
    }

    public Date getDownLastestBeginTimeDate() {
        return downLastestBeginTimeDate;
    }

    public void setDownLastestBeginTimeDate(Date downLastestBeginTimeDate) {
        this.downLastestBeginTimeDate = downLastestBeginTimeDate;
    }

    public Integer getVehicleContent() {
        return vehicleContent;
    }

    public void setVehicleContent(Integer vehicleContent) {
        this.vehicleContent = vehicleContent;
    }

	public Integer getMaxBusNumber() {
		return maxBusNumber;
	}

	public void setMaxBusNumber(Integer maxBusNumber) {
		this.maxBusNumber = maxBusNumber;
	}

	public Integer getEndDirection() {
		return endDirection;
	}

	public void setEndDirection(Integer endDirection) {
		this.endDirection = endDirection;
	}

    public Integer getUpBeginNum() {
        return upBeginNum;
    }

    public void setUpBeginNum(Integer upBeginNum) {
        this.upBeginNum = upBeginNum;
    }

    public Integer getDownBeginNum() {
        return downBeginNum;
    }

    public void setDownBeginNum(Integer downBeginNum) {
        this.downBeginNum = downBeginNum;
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

    public Integer getIsRegularBus() {
        return isRegularBus;
    }

    public void setIsRegularBus(Integer isRegularBus) {
        this.isRegularBus = isRegularBus;
    }

    public Integer getIsBackInsert() {
        return isBackInsert;
    }

    public void setIsBackInsert(Integer isBackInsert) {
        this.isBackInsert = isBackInsert;
    }

    public Integer getArriveStaStopTimeUp() {
        return arriveStaStopTimeUp;
    }

    public void setArriveStaStopTimeUp(Integer arriveStaStopTimeUp) {
        this.arriveStaStopTimeUp = arriveStaStopTimeUp;
    }

    public Integer getArriveStaStopTimeDown() {
        return arriveStaStopTimeDown;
    }

    public void setArriveStaStopTimeDown(Integer arriveStaStopTimeDown) {
        this.arriveStaStopTimeDown = arriveStaStopTimeDown;
    }

    public Integer getUpDriverTimeStart() {
        return upDriverTimeStart;
    }

    public void setUpDriverTimeStart(Integer upDriverTimeStart) {
        this.upDriverTimeStart = upDriverTimeStart;
    }

    public Integer getUpDriverTimeEnd() {
        return upDriverTimeEnd;
    }

    public void setUpDriverTimeEnd(Integer upDriverTimeEnd) {
        this.upDriverTimeEnd = upDriverTimeEnd;
    }

    public Integer getDownDriverTimeStart() {
        return downDriverTimeStart;
    }

    public void setDownDriverTimeStart(Integer downDriverTimeStart) {
        this.downDriverTimeStart = downDriverTimeStart;
    }

    public Integer getDownDriverTimeEnd() {
        return downDriverTimeEnd;
    }

    public void setDownDriverTimeEnd(Integer downDriverTimeEnd) {
        this.downDriverTimeEnd = downDriverTimeEnd;
    }

    public Integer getEnduranceMileage() {
        return enduranceMileage;
    }

    public void setEnduranceMileage(Integer enduranceMileage) {
        this.enduranceMileage = enduranceMileage;
    }

    public Integer getDoubleRunMaxMor() {
        return doubleRunMaxMor;
    }

    public void setDoubleRunMaxMor(Integer doubleRunMaxMor) {
        this.doubleRunMaxMor = doubleRunMaxMor;
    }

    public Integer getDoubleRunMaxNig() {
        return doubleRunMaxNig;
    }

    public void setDoubleRunMaxNig(Integer doubleRunMaxNig) {
        this.doubleRunMaxNig = doubleRunMaxNig;
    }

    public String getHandoverBeginTime() {
        return handoverBeginTime;
    }

    public void setHandoverBeginTime(String handoverBeginTime) {
        this.handoverBeginTime = handoverBeginTime;
    }

    public String getHandoverEndTime() {
        return handoverEndTime;
    }

    public void setHandoverEndTime(String handoverEndTime) {
        this.handoverEndTime = handoverEndTime;
    }
}