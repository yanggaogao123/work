package com.gci.schedule.driverless.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class LatestBusTrip {

	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date tripBeginTime;//班次开出时间
	
	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date tripEndTime;//班次到达时间
	
	private int direction;
	
	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date standardTripBeginTime;//标准开出时间
	
	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date tripBeginTimeLatest;//最晚发班时间
	
	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date tripEndTimeLatest;//最晚到达时间
	
	private int arriveEarlyEndTrip;//末班车提前到达时间
	
	private int restTimeRemainTrip;//剩余班次停站时间
	
	private int runTripOrder;//趟次号

	
	public LatestBusTrip() {
		super();
	}

	public LatestBusTrip(Date tripEndTime, int direction, Date standardTripBeginTime, int arriveEarlyEndTrip) {
		super();
		this.tripBeginTime=standardTripBeginTime;
		this.tripEndTime = tripEndTime;
		this.direction = direction;
		this.standardTripBeginTime = standardTripBeginTime;
		this.arriveEarlyEndTrip = arriveEarlyEndTrip;
	}

	public void setRestTimeRemainTrip(int restTimeRemainTrip) {
		this.restTimeRemainTrip = restTimeRemainTrip;
	}

	public int getRestTimeRemainTrip() {
		return restTimeRemainTrip;
	}

	public Date getTripBeginTime() {
		return tripBeginTime;
	}

	public void setTripBeginTime(Date tripBeginTime) {
		this.tripBeginTime = tripBeginTime;
	}

	public Date getTripEndTime() {
		return tripEndTime;
	}

	public void setTripEndTime(Date tripEndTime) {
		this.tripEndTime = tripEndTime;
	}

	public Date getStandardTripBeginTime() {
		return standardTripBeginTime;
	}

	public void setStandardTripBeginTime(Date standardTripBeginTime) {
		this.standardTripBeginTime = standardTripBeginTime;
	}

	public int getArriveEarlyEndTrip() {
		return arriveEarlyEndTrip;
	}

	public void setArriveEarlyEndTrip(int arriveEarlyEndTrip) {
		this.arriveEarlyEndTrip = arriveEarlyEndTrip;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Date getTripBeginTimeLatest() {
		return tripBeginTimeLatest;
	}

	public void setTripBeginTimeLatest(Date tripBeginTimeLatest) {
		this.tripBeginTimeLatest = tripBeginTimeLatest;
	}

	public int getRunTripOrder() {
		return runTripOrder;
	}

	public void setRunTripOrder(int runTripOrder) {
		this.runTripOrder = runTripOrder;
	}

	public Date getTripEndTimeLatest() {
		return tripEndTimeLatest;
	}

	public void setTripEndTimeLatest(Date tripEndTimeLatest) {
		this.tripEndTimeLatest = tripEndTimeLatest;
	}

}
