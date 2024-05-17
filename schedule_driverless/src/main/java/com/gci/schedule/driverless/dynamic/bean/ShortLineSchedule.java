package com.gci.schedule.driverless.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShortLineSchedule {
	
	@JsonIgnore
	private String runTime;
	
	@JsonIgnore
	private int direction;

	@JsonIgnore
	private Long routeStaId;
	
	@JsonIgnore
	private String routeStationName ; //站点名称
	
	private int shortLineClasses;//短线班次数

	private RouteStaTurn routeStaTurn;//短线调头信息
	
	@JsonIgnore
	private int classesNumber;//后面站点通过班次数

	public int getClassesNumber() {
		return classesNumber;
	}

	public void setClassesNumber(int classesNumber) {
		this.classesNumber = classesNumber;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public Long getRouteStaId() {
		return routeStaId;
	}

	public void setRouteStaId(Long routeStaId) {
		this.routeStaId = routeStaId;
	}

	public String getRouteStationName() {
		return routeStationName;
	}

	public void setRouteStationName(String routeStationName) {
		this.routeStationName = routeStationName;
	}

	public int getShortLineClasses() {
		return shortLineClasses;
	}

	public void setShortLineClasses(int shortLineClasses) {
		this.shortLineClasses = shortLineClasses;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public RouteStaTurn getRouteStaTurn() {
		return routeStaTurn;
	}

	public void setRouteStaTurn(RouteStaTurn routeStaTurn) {
		this.routeStaTurn = routeStaTurn;
	}

	@Override
	public String toString() {
		return "ShortLineSchedule [runTime=" + runTime + ", direction=" + direction + ", routeStaId=" + routeStaId
				+ ", routeStationName=" + routeStationName + ", shortLineClasses=" + shortLineClasses
				+ ", getRunTime()=" + getRunTime() + ", getRouteStaId()=" + getRouteStaId() + ", getRouteStationName()="
				+ getRouteStationName() + ", getShortLineClasses()=" + getShortLineClasses() + ", getDirection()="
				+ getDirection() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
}
