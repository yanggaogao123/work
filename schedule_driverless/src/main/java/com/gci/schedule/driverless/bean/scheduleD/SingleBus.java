package com.gci.schedule.driverless.bean.scheduleD;

import java.util.Date;

public class SingleBus {

	private Long routeId;
	
	private String routeCode;
	
	private Date planDate;
	
	private Long busId;
	
	private String busName;
	
	private Long stationIdStop;
	
	private Date middleStopTime;
	
	private Date offDutyTime;
	
	private int startDirection;
	
	private int startOrderNumber;

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

	public Long getBusId() {
		return busId;
	}

	public void setBusId(Long busId) {
		this.busId = busId;
	}

	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public Long getStationIdStop() {
		return stationIdStop;
	}

	public void setStationIdStop(Long stationIdStop) {
		this.stationIdStop = stationIdStop;
	}

	public Date getMiddleStopTime() {
		return middleStopTime;
	}

	public void setMiddleStopTime(Date middleStopTime) {
		this.middleStopTime = middleStopTime;
	}

	public Date getOffDutyTime() {
		return offDutyTime;
	}

	public void setOffDutyTime(Date offDutyTime) {
		this.offDutyTime = offDutyTime;
	}

	public int getStartDirection() {
		return startDirection;
	}

	public void setStartDirection(int startDirection) {
		this.startDirection = startDirection;
	}

	public int getStartOrderNumber() {
		return startOrderNumber;
	}

	public void setStartOrderNumber(int startOrderNumber) {
		this.startOrderNumber = startOrderNumber;
	}

}
