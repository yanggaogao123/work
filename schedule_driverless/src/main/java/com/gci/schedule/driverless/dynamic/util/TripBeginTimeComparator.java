package com.gci.schedule.driverless.dynamic.util;

import com.gci.schedule.driverless.dynamic.test.Trip;

import java.util.Comparator;

public class TripBeginTimeComparator implements Comparator<Trip> {  
	
    public int compare(Trip rb1, Trip rb2) {// 实现接口中的方法  
    	if(rb1.getTripBeginTime().equals(rb2.getTripBeginTime())) {
    		if(rb1.isShortLine()&&!rb2.isShortLine())
    			return -1;
    		if(rb2.isShortLine()&&!rb1.isShortLine()) 
    			return 1;
    		return Integer.valueOf(rb1.getDirection()).compareTo(rb2.getDirection());
    	}
    	return rb1.getTripBeginTime().compareTo(rb2.getTripBeginTime());
    } 

}  