package com.gci.schedule.driverless.dynamic.bean;

//满载率设定(多时段)
public class ScheduleParamsOccupancy {

	private Long routeId;

    private String direction;

    private String beginTime;

    private String endTime;
    
    private Long fromRouteStaId;//起始站点ID
    
    private Long toRouteStaId;//截止终点ID

    private Short busOccupancy; //满载率

	public Integer getIntBeginTime(){
		return Integer.parseInt(this.getBeginTime());
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getFromRouteStaId() {
		return fromRouteStaId;
	}

	public void setFromRouteStaId(Long fromRouteStaId) {
		this.fromRouteStaId = fromRouteStaId;
	}

	public Long getToRouteStaId() {
		return toRouteStaId;
	}

	public void setToRouteStaId(Long toRouteStaId) {
		this.toRouteStaId = toRouteStaId;
	}

	public Short getBusOccupancy() {
		return busOccupancy;
	}

	public void setBusOccupancy(Short busOccupancy) {
		this.busOccupancy = busOccupancy;
	}
    
}