package com.gci.schedule.driverless.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Author: zhouyanjie
 * @Description:站间耗时请求参数
 * @Date: Created in 2019/6/13  10:27
 */
public class IntersiteTimeParams {

    private String routeId;//线路Id

    private String direction;//方向

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private  Date adTime ;//进站时间

    private  Integer fromRouteStaId;//开始站点

    private  Integer toRouteStaId;//结束站点

    private  String fromRouteStaName;//开始站点名称

    private  String toRouteStaName;//结束站点名称

    //总站起始站点
    private Integer routeFromId;

    //总站结束站点
    private Integer routeToId;

    //分钟
    private Integer wasteTime;


    public IntersiteTimeParams() {
    }

    public IntersiteTimeParams(IntersiteTimeParams intersiteTimeParams) {
        this.routeId = intersiteTimeParams.routeId;
        this.direction = intersiteTimeParams.direction;
        this.adTime = intersiteTimeParams.adTime;
        this.fromRouteStaId = intersiteTimeParams.fromRouteStaId;
        this.toRouteStaId = intersiteTimeParams.toRouteStaId;
        this.routeFromId = intersiteTimeParams.routeFromId;
        this.routeToId = intersiteTimeParams.routeToId;
        this.routeFromId = intersiteTimeParams.routeFromId;
        this.wasteTime = intersiteTimeParams.wasteTime;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getAdTime() {
        return adTime;
    }

    public void setAdTime(Date adTime) {
        this.adTime = adTime;
    }

    public Integer getFromRouteStaId() {
        return fromRouteStaId;
    }

    public void setFromRouteStaId(Integer fromRouteStaId) {
        this.fromRouteStaId = fromRouteStaId;
    }

    public Integer getToRouteStaId() {
        return toRouteStaId;
    }

    public void setToRouteStaId(Integer toRouteStaId) {
        this.toRouteStaId = toRouteStaId;
    }

    public String getFromRouteStaName() {
        return fromRouteStaName;
    }

    public void setFromRouteStaName(String fromRouteStaName) {
        this.fromRouteStaName = fromRouteStaName;
    }

    public String getToRouteStaName() {
        return toRouteStaName;
    }

    public void setToRouteStaName(String toRouteStaName) {
        this.toRouteStaName = toRouteStaName;
    }

    public Integer getRouteFromId() {
        return routeFromId;
    }

    public void setRouteFromId(Integer routeFromId) {
        this.routeFromId = routeFromId;
    }

    public Integer getRouteToId() {
        return routeToId;
    }

    public void setRouteToId(Integer routeToId) {
        this.routeToId = routeToId;
    }

    public Integer getWasteTime() {
        return wasteTime;
    }

    public void setWasteTime(Integer wasteTime) {
        this.wasteTime = wasteTime;
    }
}
