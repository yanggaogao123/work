package com.gci.schedule.driverless.dynamic.test;

import java.util.Comparator;

public class PlanTimeComparator implements Comparator<Plan> {  
	
    public int compare(Plan plan1, Plan plan2) {// 实现接口中的方法  
    	return plan1.getPlanTime().compareTo(plan2.getPlanTime());
    } 

}  