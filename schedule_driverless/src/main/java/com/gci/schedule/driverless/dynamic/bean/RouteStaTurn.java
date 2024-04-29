package com.gci.schedule.driverless.dynamic.bean;

public class RouteStaTurn {

	private Long routeId;
	
	private Long lastRouteStaId;//短线末站ID
	
	private Long nextFirstRouteStaId;//回头短线首站ID
	
	private String lastRouteStaName;//短线末站站名(简称)
	
	private String nextFirstRouteStaName;//回头首站站名(简称)
	
	private Integer turnAroundTime;//调头耗时（分钟）
	
	private int direction;//方向

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Long getLastRouteStaId() {
		return lastRouteStaId;
	}

	public void setLastRouteStaId(Long lastRouteStaId) {
		this.lastRouteStaId = lastRouteStaId;
	}

	public Long getNextFirstRouteStaId() {
		return nextFirstRouteStaId;
	}

	public void setNextFirstRouteStaId(Long nextFirstRouteStaId) {
		this.nextFirstRouteStaId = nextFirstRouteStaId;
	}

	public String getLastRouteStaName() {
		return lastRouteStaName;
	}

	public void setLastRouteStaName(String lastRouteStaName) {
		this.lastRouteStaName = lastRouteStaName;
	}

	public String getNextFirstRouteStaName() {
		return nextFirstRouteStaName;
	}

	public void setNextFirstRouteStaName(String nextFirstRouteStaName) {
		this.nextFirstRouteStaName = nextFirstRouteStaName;
	}

	public Integer getTurnAroundTime() {
		return turnAroundTime;
	}

	public void setTurnAroundTime(Integer turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
}
