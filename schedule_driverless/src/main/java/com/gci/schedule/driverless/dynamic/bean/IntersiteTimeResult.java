package com.gci.schedule.driverless.dynamic.bean;

import com.alibaba.fastjson.annotation.JSONField;

//     "pminute": "40",
//     "route_id": "710",
//     "next_station_id": "10003646",
//     "avgtime": "1851",
//     "station_id": "165768",
//     "route_station_name": "广州大道北(南湖山庄)总站(总站)",
//     "order_number": "1",
//     "direction": "0",
//     "phour": "5"
//avgtime 就是站间行驶时间 单位秒
//pminute 是 分钟段
//10 表示 0~10
//20 表示10~20 分钟

/**
 * @Author: zhouyanjie
 * @Description:志武站间耗时返回结果
 * @Date: Created in 2019/6/13  10:27
 */
public class IntersiteTimeResult {

    @JSONField(name="pminute")
    private String pminute;

    @JSONField(name="route_id")
    private String routeId;

    @JSONField(name="next_station_id")
    private String nextStationId;

    @JSONField(name="avgtime")
    private String avgtime;

    @JSONField(name="station_id")
    private String stationId;

    @JSONField(name="routeStationName")
    private String routeStationName;

    @JSONField(name="order_number")
    private String orderNumber;

    @JSONField(name="direction")
    private String direction;

    @JSONField(name="phour")
    private String phour;

    public String getPminute() {
        return pminute;
    }

    public void setPminute(String pminute) {
        this.pminute = pminute;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getNextStationId() {
        return nextStationId;
    }

    public void setNextStationId(String nextStationId) {
        this.nextStationId = nextStationId;
    }

    public String getAvgtime() {
        return avgtime;
    }

    public void setAvgtime(String avgtime) {
        this.avgtime = avgtime;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getRouteStationName() {
        return routeStationName;
    }

    public void setRouteStationName(String routeStationName) {
        this.routeStationName = routeStationName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPhour() {
        return phour;
    }

    public void setPhour(String phour) {
        this.phour = phour;
    }
}
