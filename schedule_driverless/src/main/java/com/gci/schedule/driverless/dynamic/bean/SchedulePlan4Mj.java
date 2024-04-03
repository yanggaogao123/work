package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.ClassesNumber;

import java.util.ArrayList;
import java.util.List;

public class SchedulePlan4Mj {

	private List<RouteWasteTime> routeWasteTimeList;
	
	private List<RouteStationPassenger> highSectionPassengerList=new ArrayList<RouteStationPassenger>();
	
	private List<ClassesNumber> classesNumberList=new ArrayList<ClassesNumber>();

	private ScheduleInfo4Mj scheduleInfo4Mj;
	
	public void addClassesNumber(ClassesNumber classesNumber) {
		classesNumberList.add(classesNumber);
	}
	public void addHighSectionPassenger(RouteStationPassenger highSectionPassenger) {
		highSectionPassengerList.add(highSectionPassenger);
	}

	public List<RouteWasteTime> getRouteWasteTimeList() {
		return routeWasteTimeList;
	}

	public void setRouteWasteTimeList(List<RouteWasteTime> routeWasteTimeList) {
		this.routeWasteTimeList = routeWasteTimeList;
	}

	public List<RouteStationPassenger> getHighSectionPassengerList() {
		return highSectionPassengerList;
	}

	public void setHighSectionPassengerList(List<RouteStationPassenger> highSectionPassengerList) {
		this.highSectionPassengerList = highSectionPassengerList;
	}

	public List<ClassesNumber> getClassesNumberList() {
		return classesNumberList;
	}

	public void setClassesNumberList(List<ClassesNumber> classesNumberList) {
		this.classesNumberList = classesNumberList;
	}

	public ScheduleInfo4Mj getScheduleInfo4Mj() {
		return scheduleInfo4Mj;
	}
	public void setScheduleInfo4Mj(ScheduleInfo4Mj scheduleInfo4Mj) {
		this.scheduleInfo4Mj = scheduleInfo4Mj;
	}

}
