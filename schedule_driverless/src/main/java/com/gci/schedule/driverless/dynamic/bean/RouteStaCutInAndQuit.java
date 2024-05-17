package com.gci.schedule.driverless.dynamic.bean;

public class RouteStaCutInAndQuit {

	private Long fromStationIdCutIn;
	
	private String fromStationNameCutIn;
	
	private Long toStationIdCutIn;
	
	private String toStationNameCutIn;
	
	private int directionCutIn;
	
	private Long toStationIdQuit;
	
	private String toStationNameQuit;
	
	private int directionQuit;
	
	private int statisticsTimes;

	public Long getFromStationIdCutIn() {
		return fromStationIdCutIn;
	}

	public void setFromStationIdCutIn(Long fromStationIdCutIn) {
		this.fromStationIdCutIn = fromStationIdCutIn;
	}

	public String getFromStationNameCutIn() {
		return fromStationNameCutIn;
	}

	public void setFromStationNameCutIn(String fromStationNameCutIn) {
		this.fromStationNameCutIn = fromStationNameCutIn;
	}

	public Long getToStationIdCutIn() {
		return toStationIdCutIn;
	}

	public void setToStationIdCutIn(Long toStationIdCutIn) {
		this.toStationIdCutIn = toStationIdCutIn;
	}

	public String getToStationNameCutIn() {
		return toStationNameCutIn;
	}

	public void setToStationNameCutIn(String toStationNameCutIn) {
		this.toStationNameCutIn = toStationNameCutIn;
	}

	public int getDirectionCutIn() {
		return directionCutIn;
	}

	public void setDirectionCutIn(int directionCutIn) {
		this.directionCutIn = directionCutIn;
	}

	public Long getToStationIdQuit() {
		return toStationIdQuit;
	}

	public void setToStationIdQuit(Long toStationIdQuit) {
		this.toStationIdQuit = toStationIdQuit;
	}

	public String getToStationNameQuit() {
		return toStationNameQuit;
	}

	public void setToStationNameQuit(String toStationNameQuit) {
		this.toStationNameQuit = toStationNameQuit;
	}

	public int getDirectionQuit() {
		return directionQuit;
	}

	public void setDirectionQuit(int directionQuit) {
		this.directionQuit = directionQuit;
	}

	public int getStatisticsTimes() {
		return statisticsTimes;
	}

	public void setStatisticsTimes(int statisticsTimes) {
		this.statisticsTimes = statisticsTimes;
	}

}
