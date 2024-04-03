package com.gci.schedule.driverless.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RouteFirstAndLatestTime {
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	private Date planDate;

	private Long routeId;
	
	private int direction;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	private Date firstTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
	private Date latestTime;
	
	private Integer busNumberOptimal;//配车数(最优)
	
	private Integer busNumberPreset;//配车数(预设)
	
	private int orderNumber;//序号，如21:00-01:00为1,04:00-06:00为2

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	public Date getLatestTime() {
		return latestTime;
	}

	public void setLatestTime(Date latestTime) {
		this.latestTime = latestTime;
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

	public Integer getBusNumberPreset() {
		return busNumberPreset;
	}

	public void setBusNumberPreset(Integer busNumberPreset) {
		this.busNumberPreset = busNumberPreset;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

}
