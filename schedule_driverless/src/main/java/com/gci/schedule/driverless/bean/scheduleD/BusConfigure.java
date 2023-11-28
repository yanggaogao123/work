package com.gci.schedule.driverless.bean.scheduleD;

/**
 * @Author: allan
 * @Date: 2019/5/9 11:02
 *  线路配车情况
 */
public class BusConfigure {

    //总配车数
    private Integer busNumber;
    //单班车数
    private Integer singleBusNumber;

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

    public Integer getSingleBusNumber() {
        return singleBusNumber;
    }

    public void setSingleBusNumber(Integer singleBusNumber) {
        this.singleBusNumber = singleBusNumber;
    }
}
