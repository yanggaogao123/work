package com.gci.schedule.driverless.dynamic.test;

import java.util.Date;

public class Plan {

	private Date planTime;
	
	private boolean shortLine;
	
	private Long lastRouteStaId;
	
	private int direction;
	
	public Plan(Date planTime, int direction) {
		super();
		this.planTime = planTime;
		this.direction=direction;
	}

	public Plan(Date planTime, boolean shortLine, int direction) {
		super();
		this.planTime = planTime;
		this.shortLine = shortLine;
		this.direction=direction;
	}

	public Plan(Date planTime, boolean shortLine, int direction, Long lastRouteStaId) {
		super();
		this.planTime = planTime;
		this.shortLine = shortLine;
		this.lastRouteStaId = lastRouteStaId;
		this.direction=direction;
	}

	public Date getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}

	public boolean isShortLine() {
		return shortLine;
	}

	public void setShortLine(boolean shortLine) {
		this.shortLine = shortLine;
	}

	public Long getLastRouteStaId() {
		return lastRouteStaId;
	}

	public void setLastRouteStaId(Long lastRouteStaId) {
		this.lastRouteStaId = lastRouteStaId;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

}
