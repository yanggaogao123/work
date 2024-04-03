package com.gci.schedule.driverless.dynamic.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * @author pdl
 */
public class TripLogSimpleVo {

    @JSONField(name="route_id")
    private Long routeId;

    @JSONField(name="arrival_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalTime;

    @JSONField(name="depart_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date departTime;

    @JSONField(name="triplog_begin_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripLogBeginTime;

    @JSONField(name="triplog_end_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripLogEndTime;

    @JSONField(name="route_station_name")
    private String routeStationName;

    @JSONField(name="route_sta_id")
    private Long routeStaId;

    @JSONField(name="order_number")
    private Integer orderNumber;

    @JSONField(name="direction")
    private String direction;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public Date getTripLogBeginTime() {
        return tripLogBeginTime;
    }

    public void setTripLogBeginTime(Date tripLogBeginTime) {
        this.tripLogBeginTime = tripLogBeginTime;
    }

    public Date getTripLogEndTime() {
        return tripLogEndTime;
    }

    public void setTripLogEndTime(Date tripLogEndTime) {
        this.tripLogEndTime = tripLogEndTime;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName;
    }

    public Long getRouteStaId() {
        return routeStaId;
    }

    public void setRouteStaId(Long routeStaId) {
        this.routeStaId = routeStaId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "TripLogSimpleVo{" +
                "routeId=" + routeId +
                ", arrivalTime=" + arrivalTime +
                ", departTime=" + departTime +
                ", tripLogBeginTime=" + tripLogBeginTime +
                ", tripLogEndTime=" + tripLogEndTime +
                ", routeStationName='" + routeStationName + '\'' +
                ", routeStaId=" + routeStaId +
                ", orderNumber=" + orderNumber +
                ", direction='" + direction + '\'' +
                '}';
    }
}
