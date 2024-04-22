package com.gci.schedule.driverless.dynamic.bean;

import java.util.Date;

//线路客流
public class RouteStationPassenger implements Cloneable{

    private String runDayNum;

    private Short runTimeNum;

    private Long routeId;

    private Long routeStaId;

    private Short upNumber;

    private Short downNumber;

    private Short currentNumber;

    private String direction;

    private String  routeStationName ; //站点名称

    private  Short orderNumber ; //站序

    private String  userId ; //操作人

    private Date updateDate ; //更新时间

    private String odType;//od_type :1 为 不是竞争客流， od_type =2 为竞争客流

    private Long stationId;//站点id

    private String  stationName ; //站点名称

    private Integer remanentCapacity;//剩余空间


    //------------------------
    private Short gapNum;//当前线路缺口

    private Short pureCurrentNumber;//忠实客流

    private Short competeCurrentNumber;//竞争客流

    private Short competeRouteGapNum;//竞争线路缺口

    private Double rate;//二次分配比例

    private Short otherRouteGapNum;//其他线路缺口

    private Short finalCurrentNum;//最终客流

    private Integer totalGapNum;//排班线路+竞争线路缺口总和

    private RouteStationPassenger otherRouteStationPassenger;//其他线路客流类

    private Integer pureCapacity;//满载量

    private Integer vehicleContent;//车内容量

    private Integer busOccupancy;//满载率

    private Integer pureTripNum;//基础班次

    private Long parentRouteId;//关联线路id

    private Date runDate;//日期

    private String runTimeNumStr;//

    private String sDirection;//排班线路方向

    private Long uniqueRouteId;// unique_route_id

    private RouteStationPassenger routeStationPassengerTurn;//短线掉头点

    private RouteSta routeStaTurn;//短线掉头点

    private int subNumber = 0;

    public Short getCompeteCurrentNumber() {
        return competeCurrentNumber;
    }

    public void setCompeteCurrentNumber(Short competeCurrentNumber) {
        this.competeCurrentNumber = competeCurrentNumber;
    }

    public Long getUniqueRouteId() {
        return uniqueRouteId;
    }

    public void setUniqueRouteId(Long uniqueRouteId) {
        this.uniqueRouteId = uniqueRouteId;
    }

    public String getsDirection() {
        return sDirection;
    }

    public void setsDirection(String sDirection) {
        this.sDirection = sDirection;
    }

    public String getRunTimeNumStr() {
        return runTimeNumStr;
    }

    public void setRunTimeNumStr(String runTimeNumStr) {
        this.runTimeNumStr = runTimeNumStr;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    public Long getParentRouteId() {
        return parentRouteId;
    }

    public void setParentRouteId(Long parentRouteId) {
        this.parentRouteId = parentRouteId;
    }

    public Integer getPureTripNum() {
        return pureTripNum;
    }

    public void setPureTripNum(Integer pureTripNum) {
        this.pureTripNum = pureTripNum;
    }

    public Integer getPureCapacity() {
        return pureCapacity;
    }

    public void setPureCapacity(Integer pureCapacity) {
        this.pureCapacity = pureCapacity;
    }

    public Integer getVehicleContent() {
        return vehicleContent;
    }

    public void setVehicleContent(Integer vehicleContent) {
        this.vehicleContent = vehicleContent;
    }

    public Integer getBusOccupancy() {
        return busOccupancy;
    }

    public void setBusOccupancy(Integer busOccupancy) {
        this.busOccupancy = busOccupancy;
    }

    public RouteStationPassenger getOtherRouteStationPassenger() {
        return otherRouteStationPassenger;
    }

    public void setOtherRouteStationPassenger(RouteStationPassenger otherRouteStationPassenger) {
        this.otherRouteStationPassenger = otherRouteStationPassenger;
    }

    public Integer getTotalGapNum() {
        return totalGapNum;
    }

    public void setTotalGapNum(Integer totalGapNum) {
        this.totalGapNum = totalGapNum;
    }

    public Short getFinalCurrentNum() {
        return finalCurrentNum;
    }

    public void setFinalCurrentNum(Short finalCurrentNum) {
        this.finalCurrentNum = finalCurrentNum;
    }

    public Short getOtherRouteGapNum() {
        return otherRouteGapNum;
    }

    public void setOtherRouteGapNum(Short otherRouteGapNum) {
        this.otherRouteGapNum = otherRouteGapNum;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Short getCompeteRouteGapNum() {
        return competeRouteGapNum;
    }

    public void setCompeteRouteGapNum(Short competeRouteGapNum) {
        this.competeRouteGapNum = competeRouteGapNum;
    }

    public Short getPureCurrentNumber() {
        return pureCurrentNumber;
    }

    public void setPureCurrentNumber(Short pureCurrentNumber) {
        this.pureCurrentNumber = pureCurrentNumber;
    }

    public Short getGapNum() {
        return gapNum;
    }

    public void setGapNum(Short gapNum) {
        this.gapNum = gapNum;
    }

    public Integer getRemanentCapacity() {
        return remanentCapacity;
    }

    public void setRemanentCapacity(Integer remanentCapacity) {
        this.remanentCapacity = remanentCapacity;
    }

    public String getOdType() {
        return odType;
    }

    public void setOdType(String odType) {
        this.odType = odType;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getRunDayNum() {
        return runDayNum;
    }

    public void setRunDayNum(String runDayNum) {
        this.runDayNum = runDayNum;
    }

    public Short getRunTimeNum() {
        return runTimeNum;
    }

    public void setRunTimeNum(Short runTimeNum) {
        this.runTimeNum = runTimeNum;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getRouteStaId() {
        return routeStaId;
    }

    public void setRouteStaId(Long routeStaId) {
        this.routeStaId = routeStaId;
    }

    public Short getUpNumber() {
        return upNumber;
    }

    public void setUpNumber(Short upNumber) {
        this.upNumber = upNumber;
    }

    public Short getDownNumber() {
        return downNumber;
    }

    public void setDownNumber(Short downNumber) {
        this.downNumber = downNumber;
    }

    public Short getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Short currentNumber) {
        this.currentNumber = currentNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName;
    }

    public Short getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Short orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public RouteStationPassenger getRouteStationPassengerTurn() {
        return routeStationPassengerTurn;
    }

    public void setRouteStationPassengerTurn(RouteStationPassenger routeStationPassengerTurn) {
        this.routeStationPassengerTurn = routeStationPassengerTurn;
    }

    public RouteSta getRouteStaTurn() {
        return routeStaTurn;
    }

    public void setRouteStaTurn(RouteSta routeStaTurn) {
        this.routeStaTurn = routeStaTurn;
    }

    public int getSubNumber() {
        return subNumber;
    }

    public void addSubNumber(Integer subNumber) {
        if (subNumber != null) {
            this.subNumber += subNumber;
        }
    }

    public int getCurrentNumberRealTime() {
        if (currentNumber == null) {
            return 0;
        }
        return currentNumber - subNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RouteStationPassenger){
            RouteStationPassenger obj1 = (RouteStationPassenger)obj;
            return compare(obj1,this);
        }else return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return 0;
    }


    private static boolean compare(RouteStationPassenger t1, RouteStationPassenger t2) {
       return t1.getRouteStaId().equals(t2.getRouteStaId()) && t1.getRouteId().equals(t2.getRouteId())
               && t1.getDirection().equals(t2.getDirection()) && t1.getRunDayNum().equals(t2.getRunDayNum())
               && t1.getRunTimeNum().equals(t2.getRunTimeNum());
    }

    public interface  ODType{//od_type :1 为 不是竞争客流， od_type =2 为竞争客流
        String purePassenger = "1";
        String competePassenger = "2";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RouteStationPassenger passenger = (RouteStationPassenger) super.clone();
        if (passenger.updateDate != null) {
            passenger.updateDate = (Date) passenger.updateDate.clone();
        }
        return passenger;
    }

    @Override
    public String toString() {
        return "RouteStationPassenger{" +
                "runDayNum='" + runDayNum + '\'' +
                ", runTimeNum=" + runTimeNum +
                ", routeId=" + routeId +
                ", parentRouteId=" + parentRouteId +
                ", routeStaId=" + routeStaId +
                ", upNumber=" + upNumber +
                ", downNumber=" + downNumber +
                ", currentNumber=" + currentNumber +
                ", direction='" + direction + '\'' +
                ", routeStationName='" + routeStationName + '\'' +
                ", orderNumber=" + orderNumber +
                ", userId='" + userId + '\'' +
                ", updateDate=" + updateDate +
                ", odType='" + odType + '\'' +
                ", stationId=" + stationId +
                ", stationName='" + stationName + '\'' +
                ", remanentCapacity=" + remanentCapacity +
                ", gapNum=" + gapNum +
                ", pureCurrentNumber=" + pureCurrentNumber +
                ", competeRouteGapNum=" + competeRouteGapNum +
                ", rate=" + rate +
                ", otherRouteGapNum=" + otherRouteGapNum +
                ", finalCurrentNum=" + finalCurrentNum +
                ", totalGapNum=" + totalGapNum +
                ", otherRouteStationPassenger=" + otherRouteStationPassenger +
                ", pureCapacity=" + pureCapacity +
                ", vehicleContent=" + vehicleContent +
                ", busOccupancy=" + busOccupancy +
                ", pureTripNum=" + pureTripNum +
                '}';
    }
}