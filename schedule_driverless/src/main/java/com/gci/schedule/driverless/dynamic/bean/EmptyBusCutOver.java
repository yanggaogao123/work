package com.gci.schedule.driverless.dynamic.bean;

/**
 * @author lindeyong
 * 空车切入
 */
public class EmptyBusCutOver {
	
	private Long firstRouteStaId;//空车切入站点ID
	
	private String firstRouteStaName;
	
	private ShortLineSchedule shortLineSchedule;
	
	private int classesNumber;//通过车次
	
	private int classesNumberCutOver;//短线切入车次

	public Long getFirstRouteStaId() {
		return firstRouteStaId;
	}

	public void setFirstRouteStaId(Long firstRouteStaId) {
		this.firstRouteStaId = firstRouteStaId;
	}

	public String getFirstRouteStaName() {
		return firstRouteStaName;
	}

	public void setFirstRouteStaName(String firstRouteStaName) {
		this.firstRouteStaName = firstRouteStaName;
	}

	public ShortLineSchedule getShortLineSchedule() {
		return shortLineSchedule;
	}

	public void setShortLineSchedule(ShortLineSchedule shortLineSchedule) {
		this.shortLineSchedule = shortLineSchedule;
	}

	public int getClassesNumber() {
		return classesNumber;
	}

	public void setClassesNumber(int classesNumber) {
		this.classesNumber = classesNumber;
	}

	public int getClassesNumberCutOver() {
		return classesNumberCutOver;
	}

	public void setClassesNumberCutOver(int classesNumberCutOver) {
		this.classesNumberCutOver = classesNumberCutOver;
	}
}
