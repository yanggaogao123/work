package com.gci.schedule.driverless.dynamic.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定点单环线排班
 * @author lindeyong
 *
 */
public class ScheduleGenerate4SingleLoopFixedTime extends ScheduleGenerate4SingleLoop{
		
	public ScheduleGenerate4SingleLoopFixedTime(int direction, ScheduleParam scheduleParam) {
		super(direction, scheduleParam);
	}

	public RouteSchedule generate() {
		this.routeSchedule=new RouteSchedule(scheduleParam);
		initPlanQueue();
		procFirstRound();
		procArrivalPlan();
		return routeSchedule;
	}
	
	private void procFirstRound() {
		Integer firstRoundBusNumber=null;
		if(scheduleParam.getBusNumberPreset()==null) {
			firstRoundBusNumber=getFirstRoundBusNumber();
		}else {
			firstRoundBusNumber=scheduleParam.getBusNumberPreset(direction);
		}
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		for(int i=0;i<firstRoundBusNumber;i++) {
			int startOrderNumber=routeSchedule.newBusOrder(direction);
			Bus bus=new Bus(direction, startOrderNumber);
			Trip trip=new Trip(bus, direction, null);
			busQueue.add(trip);//把车加入待发车队列
		}
	}
	
	private int getFirstRoundBusNumber() {
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date arrivalTime=scheduleParam.getArrivalTime(firstTime, direction);
		List<Plan> planList=planMap.get(direction);
		int busNumber=0;
		for(Plan plan:planList) {
			if(plan.getPlanTime().after(arrivalTime)) {
				break;
			}
			busNumber++;
		}
		return busNumber;
	}
	
	private void initPlanQueue(){
		 Date firstTime=scheduleParam.getFirstTime(direction);
		 Date latestTime=scheduleParam.getLatestTime(direction);
		 Date planTime=firstTime;
		 List<Plan> planList=new ArrayList<Plan>();
		 while(!planTime.after(latestTime)) {
			 Plan plan=new Plan(planTime, direction);
			 planList.add(plan);
			 int interval=scheduleParam.getMaxInterval(planTime, direction);
			 planTime=DateUtil.getDateAddMinute(planTime, interval);
		 }
		 Date planTimeLatest=planList.get(planList.size()-1).getPlanTime();
		 if(planTimeLatest.after(latestTime)) {
			 Plan plan=new Plan(latestTime, direction);//补上末班车
			 planList.add(plan);
		 }
		 planMap.put(direction, planList);
	}
	
	@Override
	protected void classNumberAdjust(Date hour) {
		
	}

}
