package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.StationMark;

public class RouteSta {

	private Long routeStationId;
	
	private Long stationId;
	
	private Integer stationMark;
	
	private String routeStationName;
	
	private Double stationDistance;

	private Integer orderNumber;
	
	private String routeStationCode;

	private String stationName;

	//序号
	private int index;
	//竞争站台
	private boolean isCompeteStation;

	public boolean isCompeteStation() {
		return isCompeteStation;
	}

	public void setCompeteStation(boolean competeStation) {
		isCompeteStation = competeStation;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Long getRouteStationId() {
		return routeStationId;
	}

	public void setRouteStationId(Long routeStationId) {
		this.routeStationId = routeStationId;
	}

	public Integer getStationMark() {
		return stationMark;
	}

	public void setStationMark(Integer stationMark) {
		this.stationMark = stationMark;
	}

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public String getRouteStationName() {
		return routeStationName;
	}

	public void setRouteStationName(String routeStationName) {
		this.routeStationName = routeStationName;
	}

	public Double getStationDistance() {
		return stationDistance;
	}

	public void setStationDistance(Double stationDistance) {
		this.stationDistance = stationDistance;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRouteStationCode() {
		return routeStationCode;
	}

	public int getDirection() {
		if(stationMark == null || stationMark.intValue() < StationMark.DOWN_FIRST.getValue()) {
			return Direction.UP.getValue();
		}
		return Direction.DOWN.getValue();
	}

	public void setRouteStationCode(String routeStationCode) {
		this.routeStationCode = routeStationCode;
	}

}
