package com.gci.schedule.driverless.dynamic.bean;

public class Station {

	private Long stationId;
	
	private String stationName;
	
	private Double latitudedDis;   
	
	private Double longitudedDis;

	public Long getStationId() {
		return stationId;
	}

	public void setStationId(Long stationId) {
		this.stationId = stationId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public Double getLatitudedDis() {
		return latitudedDis;
	}

	public void setLatitudedDis(Double latitudedDis) {
		this.latitudedDis = latitudedDis;
	}

	public Double getLongitudedDis() {
		return longitudedDis;
	}

	public void setLongitudedDis(Double longitudedDis) {
		this.longitudedDis = longitudedDis;
	}

}
