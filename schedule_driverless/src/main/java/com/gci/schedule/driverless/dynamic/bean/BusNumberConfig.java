package com.gci.schedule.driverless.dynamic.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.Map;

//配车配置表
public class BusNumberConfig {

    private Long routeId;//线路ID

    private String routeCode;//线路编码

    @JSONField(name = "planEndTime", format = "yyyy-MM-dd")
    private Date planDate;//运营日期

    private Integer busNumberOptimal;//配车数(最优)

    private Integer singleBusNumberOptimal;//单班配车数(最优)

    private Integer busNumberPreset;//配车数(预设)

    private Integer singleBusNumberPreset;//单班配车数(预设)

    private Integer upFirstBusNumberOptimal;//上行出车配车数(最优)

    private Integer downFirstBusNumberOptimal;//下行出车配车数(最优)

    private Integer upFirstBusNumberPreset;//上行出车配车数(预设)

    private Integer downFirstBusNumberPreset;//下行出车配车数(预设)

    private Integer singleBusNumberOptimalUp; //上行单班车最优配车数

    private Integer singleBusNumberOptimalDown; //下行单班车最优配车数

    private Integer singleBusNumberPresetUp; //上行单班车预设配车数

    private Integer singleBusNumberPresetDown; //下行单班车预设配车数

    private Date updateTime; //最后一次生成计划的时间
    
    private Date updateTimeOptimal; //最优计划最后生成时间

    private Date updateTimePreset; //预设计划最后生成时间

    /**
     * 挂车模板ID
     */
    private Long templateId;

    // 班别：{方向：数量}
    private Map<String, Map<String, Integer>> shiftCountMap;

    public Integer getSingleBusNumberOptimalUp() {
        return singleBusNumberOptimalUp;
    }

    public void setSingleBusNumberOptimalUp(Integer singleBusNumberOptimalUp) {
        this.singleBusNumberOptimalUp = singleBusNumberOptimalUp;
    }

    public Integer getSingleBusNumberOptimalDown() {
        return singleBusNumberOptimalDown;
    }

    public void setSingleBusNumberOptimalDown(Integer singleBusNumberOptimalDown) {
        this.singleBusNumberOptimalDown = singleBusNumberOptimalDown;
    }

    public Integer getSingleBusNumberPresetUp() {
        return singleBusNumberPresetUp;
    }

    public void setSingleBusNumberPresetUp(Integer singleBusNumberPresetUp) {
        this.singleBusNumberPresetUp = singleBusNumberPresetUp;
    }

    public Integer getSingleBusNumberPresetDown() {
        return singleBusNumberPresetDown;
    }

    public void setSingleBusNumberPresetDown(Integer singleBusNumberPresetDown) {
        this.singleBusNumberPresetDown = singleBusNumberPresetDown;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Integer getBusNumberOptimal() {
        return busNumberOptimal;
    }

    public void setBusNumberOptimal(Integer busNumberOptimal) {
        this.busNumberOptimal = busNumberOptimal;
    }

    public Integer getSingleBusNumberOptimal() {
        return singleBusNumberOptimal;
    }

    public void setSingleBusNumberOptimal(Integer singleBusNumberOptimal) {
        this.singleBusNumberOptimal = singleBusNumberOptimal;
    }

    public Integer getBusNumberPreset() {
        return busNumberPreset;
    }

    public void setBusNumberPreset(Integer busNumberPreset) {
        this.busNumberPreset = busNumberPreset;
    }

    public Integer getSingleBusNumberPreset() {
        return singleBusNumberPreset;
    }

    public void setSingleBusNumberPreset(Integer singleBusNumberPreset) {
        this.singleBusNumberPreset = singleBusNumberPreset;
    }

    public Integer getUpFirstBusNumberOptimal() {
        return upFirstBusNumberOptimal;
    }

    public void setUpFirstBusNumberOptimal(Integer upFirstBusNumberOptimal) {
        this.upFirstBusNumberOptimal = upFirstBusNumberOptimal;
    }

    public Integer getDownFirstBusNumberOptimal() {
        return downFirstBusNumberOptimal;
    }

    public void setDownFirstBusNumberOptimal(Integer downFirstBusNumberOptimal) {
        this.downFirstBusNumberOptimal = downFirstBusNumberOptimal;
    }

    public Integer getUpFirstBusNumberPreset() {
        return upFirstBusNumberPreset;
    }

    public void setUpFirstBusNumberPreset(Integer upFirstBusNumberPreset) {
        this.upFirstBusNumberPreset = upFirstBusNumberPreset;
    }

    public Integer getDownFirstBusNumberPreset() {
        return downFirstBusNumberPreset;
    }

    public void setDownFirstBusNumberPreset(Integer downFirstBusNumberPreset) {
        this.downFirstBusNumberPreset = downFirstBusNumberPreset;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTimeOptimal() {
        return updateTimeOptimal;
    }

    public void setUpdateTimeOptimal(Date updateTimeOptimal) {
        this.updateTimeOptimal = updateTimeOptimal;
    }

    public Date getUpdateTimePreset() {
        return updateTimePreset;
    }

    public void setUpdateTimePreset(Date updateTimePreset) {
        this.updateTimePreset = updateTimePreset;
    }

    public Map<String, Map<String, Integer>> getShiftCountMap() {
        return shiftCountMap;
    }

    public void setShiftCountMap(Map<String, Map<String, Integer>> shiftCountMap) {
        this.shiftCountMap = shiftCountMap;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}