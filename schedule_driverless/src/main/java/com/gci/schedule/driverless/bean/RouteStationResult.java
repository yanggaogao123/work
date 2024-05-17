package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;

//"orderNumber": 1,
//"routeId": 710,
//"stationDistance": 1.014,
//"stationMark": "0",
//"routeStationId": 1047182,
//"routeStationCode": "05600024",
//"routeStationName": "广州大道北(南湖山庄)总站(总站)",
//"stationId": 165768

/**
 * @Author: zhouyanjie
 * @Description:查询线路站点
 * @Date: Created in 2019/6/13  10:27
 */
public class RouteStationResult {

    @JSONField(name="orderNumber")
    private Long orderNumber;

    @JSONField(name="routeId")
    private Long routeId;

    @JSONField(name="stationDistance")
    private Double stationDistance;

    @JSONField(name="stationMark")
    private String stationMark;

    @JSONField(name="routeStationId")
    private Long routeStationId;

    @JSONField(name="routeStationCode")
    private String routeStationCode;

    @JSONField(name="routeStationName")
    private String routeStationName;

    @JSONField(name="stationId")
    private Long stationId;


    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Double getStationDistance() {
        return stationDistance;
    }

    public void setStationDistance(Double stationDistance) {
        this.stationDistance = stationDistance;
    }

    public String getStationMark() {
        return stationMark;
    }

    public void setStationMark(String stationMark) {
        this.stationMark = stationMark;
    }

    public Long getRouteStationId() {
        return routeStationId;
    }

    public void setRouteStationId(Long routeStationId) {
        this.routeStationId = routeStationId;
    }

    public String getRouteStationCode() {
        return routeStationCode;
    }

    public void setRouteStationCode(String routeStationCode) {
        this.routeStationCode = routeStationCode;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
}
