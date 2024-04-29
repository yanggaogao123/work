package com.gci.schedule.driverless.dynamic.bean;

public class HighSectionPassenger {
	
	private String runTime;
	
	private int direction;

	private int highSectionPassenger;//时段高断面人数；
	
	private int classesNumber;//班次数
	
	public HighSectionPassenger(String runTime, int direction, int highSectionPassenger, int classesNumber) {
		super();
		this.runTime = runTime;
		this.direction = direction;
		this.highSectionPassenger = highSectionPassenger;
		this.classesNumber = classesNumber;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getHighSectionPassenger() {
		return highSectionPassenger;
	}

	public void setHighSectionPassenger(int highSectionPassenger) {
		this.highSectionPassenger = highSectionPassenger;
	}

	public int getClassesNumber() {
		return classesNumber;
	}

	public void setClassesNumber(int classesNumber) {
		this.classesNumber = classesNumber;
	}
	
}
