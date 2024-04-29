package com.gci.schedule.driverless.dynamic.util;

import java.util.List;

public class MathUtil {

	public static int avg(List<Integer> list) {
		if(list.isEmpty()) {
			return 0;
		}else {
			int sum=0;
			for(Integer i:list) {
				sum+=i;
			}
			int avg=sum/list.size();
			return avg;
		}
	}
	
	public static int max(List<Integer> list) {
		if(list.isEmpty()) {
			return 0;
		}else {
			int max=0;
			for(Integer i:list) {
				if(i>max)
					max=i;
			}
			return max;
		}
	}
}
