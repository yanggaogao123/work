package com.gci.schedule.driverless.bean;

import java.util.Date;
import java.util.List;

/**
 * 计划线路管理Vo类
 * @author pdl
 */
public class ScheduleRouteConfigVo extends ScheduleRouteConfigParams{

    //周转时间更新状态 0：未在更新 1：在更新中
    private Integer wasteTimeUpdateStatus;

    //周转时间最后更新时间
    private Date wasteTimeUpdateTime;

    //周转时间最后一次是否更新成功 0:未成功 1：成功
    private Integer wasteTimeUpdateSuccess;

    //客流数据更新状态 0：未在更新 1：在更新中
    private Integer passengerFlowUpdateStatus;

    //客流数据最后更新时间
    private Date passengerFlowUpdateTime;

    //客流数据最后一次是否更新成功 0:未成功 1：成功
    private Integer passengerFlowUpdateSuccess;

    //调度开关 0：全关 1：只开首轮 2：只开到站 3：全开
    private Integer dispatchAuto;

    //周转时间更新详情，key从周日到周一是1到7，value：1是成功0是失败
    private List<Integer> wasteTimeWeekUpdateFlag;

    public List<Integer> getWasteTimeWeekUpdateFlag() {
        return wasteTimeWeekUpdateFlag;
    }

    private Date arrivalTimeUpdateTime;

    private Integer arrivalTimeStatus;

    private String routeCode;

    @Override
    public String getRouteCode() {
        return routeCode;
    }

    @Override
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public Date getArrivalTimeUpdateTime() {
        return arrivalTimeUpdateTime;
    }

    public void setArrivalTimeUpdateTime(Date arrivalTimeUpdateTime) {
        this.arrivalTimeUpdateTime = arrivalTimeUpdateTime;
    }

    public Integer getArrivalTimeStatus() {
        return arrivalTimeStatus;
    }

    public void setArrivalTimeStatus(Integer arrivalTimeStatus) {
        this.arrivalTimeStatus = arrivalTimeStatus;
    }

    public void setWasteTimeWeekUpdateFlag(List<Integer> wasteTimeWeekUpdateFlag) {
        this.wasteTimeWeekUpdateFlag = wasteTimeWeekUpdateFlag;
    }

    public Integer getWasteTimeUpdateSuccess() {
        return wasteTimeUpdateSuccess;
    }

    public void setWasteTimeUpdateSuccess(Integer wasteTimeUpdateSuccess) {
        this.wasteTimeUpdateSuccess = wasteTimeUpdateSuccess;
    }

    public Integer getPassengerFlowUpdateSuccess() {
        return passengerFlowUpdateSuccess;
    }

    public void setPassengerFlowUpdateSuccess(Integer passengerFlowUpdateSuccess) {
        this.passengerFlowUpdateSuccess = passengerFlowUpdateSuccess;
    }

    public Integer getDispatchAuto() {
        return dispatchAuto;
    }

    public void setDispatchAuto(Integer dispatchAuto) {
        this.dispatchAuto = dispatchAuto;
    }

    public Integer getWasteTimeUpdateStatus() {
        return wasteTimeUpdateStatus;
    }

    public void setWasteTimeUpdateStatus(Integer wasteTimeUpdateStatus) {
        this.wasteTimeUpdateStatus = wasteTimeUpdateStatus;
    }

    public Integer getPassengerFlowUpdateStatus() {
        return passengerFlowUpdateStatus;
    }

    public void setPassengerFlowUpdateStatus(Integer passengerFlowUpdateStatus) {
        this.passengerFlowUpdateStatus = passengerFlowUpdateStatus;
    }

    public Date getWasteTimeUpdateTime() {
        return wasteTimeUpdateTime;
    }

    public void setWasteTimeUpdateTime(Date wasteTimeUpdateTime) {
        this.wasteTimeUpdateTime = wasteTimeUpdateTime;
    }

    public Date getPassengerFlowUpdateTime() {
        return passengerFlowUpdateTime;
    }

    public void setPassengerFlowUpdateTime(Date passengerFlowUpdateTime) {
        this.passengerFlowUpdateTime = passengerFlowUpdateTime;
    }
}
