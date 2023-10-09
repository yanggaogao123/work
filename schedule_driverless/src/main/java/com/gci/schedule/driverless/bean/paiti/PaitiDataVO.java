package com.gci.schedule.driverless.bean.paiti;

import java.util.Date;

/**
 * @Author:
 * @Description: 派替返回数据
 * @Date:
 */
public class PaitiDataVO {
    private Integer startDirection;//发车方向
    private Integer startOrderNumber;//发车车序
    private String busName;//车辆名称
    private String stopPlaceName;//停场名称
    private String fromStationName;//开出站名
    private String startTime;//发车时间	HH:mm
    private String ckTime;  //签到时间	HH:mm:ss
    private String checkOutTime;//签出时间
    private Integer shiftType;   //	Integer	选填	班别	0：早半班；1：晚半班；2：中班；3：双班(中停);4：单班(中停)：5:双班
    private String routeCode;//	String	必填	线路编码
    private Date updateTime;
    private Date runDate;
    private String userName;
    private String routeName;
    private String organName;

    public Integer getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(Integer startDirection) {
        this.startDirection = startDirection;
    }

    public Integer getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Integer startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getStopPlaceName() {
        return stopPlaceName;
    }

    public void setStopPlaceName(String stopPlaceName) {
        this.stopPlaceName = stopPlaceName;
    }

    public String getFromStationName() {
        return fromStationName;
    }

    public void setFromStationName(String fromStationName) {
        this.fromStationName = fromStationName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCkTime() {
        return ckTime;
    }

    public void setCkTime(String ckTime) {
        this.ckTime = ckTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Integer getShiftType() {
        return shiftType;
    }

    public void setShiftType(Integer shiftType) {
        this.shiftType = shiftType;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }
}
