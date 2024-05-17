package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.test.Bus;

public class ScheduleInfo4Mj {

	//线路总营运里程
    private Double runMileage;
    //高峰营运里程
    private Double highPeakMileage;
    //总车次
    private Integer tripNumber;
    //短线车次
    private Integer tripNumberShort;
    //高峰车次
    private Integer highPeakTripNumber;
    //运营时间最长车辆
    private Bus runTimeMaxBus;
    //运营时间最短车辆
    private Bus runTimeMinBus;
    //结果信息
    private String resultMsg;
    
	public Double getRunMileage() {
		return runMileage;
	}
	public void setRunMileage(Double runMileage) {
		this.runMileage = runMileage;
	}
	public Double getHighPeakMileage() {
		return highPeakMileage;
	}
	public void setHighPeakMileage(Double highPeakMileage) {
		this.highPeakMileage = highPeakMileage;
	}
	public Integer getTripNumber() {
		return tripNumber;
	}
	public void setTripNumber(Integer tripNumber) {
		this.tripNumber = tripNumber;
	}
	public Integer getTripNumberShort() {
		return tripNumberShort;
	}
	public void setTripNumberShort(Integer tripNumberShort) {
		this.tripNumberShort = tripNumberShort;
	}
	public Integer getHighPeakTripNumber() {
		return highPeakTripNumber;
	}
	public void setHighPeakTripNumber(Integer highPeakTripNumber) {
		this.highPeakTripNumber = highPeakTripNumber;
	}
	public Bus getRunTimeMaxBus() {
		return runTimeMaxBus;
	}
	public void setRunTimeMaxBus(Bus runTimeMaxBus) {
		this.runTimeMaxBus = runTimeMaxBus;
	}
	public Bus getRunTimeMinBus() {
		return runTimeMinBus;
	}
	public void setRunTimeMinBus(Bus runTimeMinBus) {
		this.runTimeMinBus = runTimeMinBus;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
}
