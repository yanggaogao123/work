package com.gci.schedule.driverless.dynamic.util;

import com.gci.schedule.driverless.dynamic.test.Trip;

import java.util.Comparator;

public class TripEndTimeComparator implements Comparator<Trip> {  
	
    public int compare(Trip rb1, Trip rb2) {// 实现接口中的方法  
    	return rb1.getTripEndTime().compareTo(rb2.getTripEndTime());
    } 

}  