package com.gci.schedule.driverless.dynamic.bean;

public class TriplogTask {
	
	private Long fromStationId;
	
	private String fromStationName;
	
	private Long toStationId;
	
	private String toStationName;
	
	private int direction;
	
	private int statisticsTimes;

	public Long getFromStationId() {
		return fromStationId;
	}

	public void setFromStationId(Long fromStationId) {
		this.fromStationId = fromStationId;
	}

	public String getFromStationName() {
		return fromStationName;
	}

	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}

	public Long getToStationId() {
		return toStationId;
	}

	public void setToStationId(Long toStationId) {
		this.toStationId = toStationId;
	}

	public String getToStationName() {
		return toStationName;
	}

	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getStatisticsTimes() {
		return statisticsTimes;
	}

	public void setStatisticsTimes(int statisticsTimes) {
		this.statisticsTimes = statisticsTimes;
	}

}
