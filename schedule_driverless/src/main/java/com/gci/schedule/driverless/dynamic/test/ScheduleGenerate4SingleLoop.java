package com.gci.schedule.driverless.dynamic.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单环线排班
 * @author lindeyong
 *
 */
public class ScheduleGenerate4SingleLoop extends ScheduleGenerate4Loop{
	
	protected int direction;
	
	public ScheduleGenerate4SingleLoop(int direction, ScheduleParam scheduleParam) {
		super(scheduleParam);
		this.direction=direction;
	}

	public RouteSchedule generate() {
		if(scheduleParam.getBusNumberPreset(direction)!=null) {
			scheduleParam.setBusNumberPreset(scheduleParam.getBusNumberPreset(direction));
			scheduleParam.setBusNumberSingle(scheduleParam.getBusNumberSinglePreset(direction));
		}
		planMap.put(direction, new ArrayList<Plan>());
		this.routeSchedule=new RouteSchedule(scheduleParam);
		scheduleParam.generateObuTime();
		if(scheduleParam.getBusNumberPreset()==null)
			routeSchedule.calculateBusConfigNew(scheduleParam);
		initSingleBusTripNumber();
		procFirstRound4Single();
		initSingleBus();
		procArrivalPlan();
		return routeSchedule;
	}
	
	protected void initSingleBusTripNumber() {
		int wasteTimeAvg=getWasteTimeHighAvg();
		scheduleParam.initSingleBusTripNumber(wasteTimeAvg);
	}
	
	/**
	 * 高峰周转时间
	 * @return
	 */
	protected int getWasteTimeHighAvg() {
		List<Date> highRushTimeList=new ArrayList<Date>();
		highRushTimeList.add(DateUtil.getDateHM("0700"));
		highRushTimeList.add(DateUtil.getDateHM("0730"));
		highRushTimeList.add(DateUtil.getDateHM("0800"));
		highRushTimeList.add(DateUtil.getDateHM("0830"));
		highRushTimeList.add(DateUtil.getDateHM("1700"));
		highRushTimeList.add(DateUtil.getDateHM("1730"));
		highRushTimeList.add(DateUtil.getDateHM("1800"));
		highRushTimeList.add(DateUtil.getDateHM("1830"));
		int wasteTimeSum=0;
		for (Date runTime:highRushTimeList) {
			Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(direction, runTime);
			int wasteTime=DateUtil.getMinuteInterval(runTime, obuTimeNextRound);
			wasteTimeSum+=wasteTime;
		}
		int wasteTimeAvg=wasteTimeSum/highRushTimeList.size();
		return wasteTimeAvg;
	}
	
	private void procFirstRound4Single() {
		Integer firstRoundBusNumber=null;
		if(scheduleParam.getBusNumberPreset()==null) {
			firstRoundBusNumber=getFirstRoundBusNumber(direction);
		}else {
			firstRoundBusNumber=scheduleParam.getBusNumberPreset(direction);
		}
		firstRoundBusNumberMap.put(direction, firstRoundBusNumber);
		if(scheduleParam.getBusNumberPreset()==null) {
			int busNumber=firstRoundBusNumber;
			while(busNumber<scheduleParam.getBusNumberConfig()) {
				firstRoundBusNumberAdjust();
				busNumber=firstRoundBusNumberMap.get(direction);
			}
		}
		initPlanQueue(direction);
		if(scheduleParam.getBusNumberPreset()!=null) {
			firstRoundPlanAdjust(direction);
		}
		firstRoundBusNumber=firstRoundBusNumberMap.get(direction);
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		for(int i=0;i<firstRoundBusNumber;i++) {
			int startOrderNumber=routeSchedule.newBusOrder(direction);
			Bus bus=new Bus(direction, startOrderNumber);
			Trip trip=new Trip(bus, direction, null);
			busQueue.add(trip);//把车加入待发车队列
		}
	}
	
	@Override
	protected List<ScheduleHalfHour> getScheduleHalfHourList(Date hour) {
		ScheduleHalfHour scheduleHalfHour=scheduleParam.getScheduleHalfHour(hour, direction);
		List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		if(scheduleHalfHour!=null){
			scheduleHalfHourList.add(scheduleHalfHour);
			if(scheduleHalfHour.getNextScheduleHalfHour()!=null) {
				scheduleHalfHourList.add(scheduleHalfHour.getNextScheduleHalfHour());
			}
		}
		return scheduleHalfHourList;
	}
	
	@Override
	protected boolean isTimeToAdjust(Plan plan) {
		if(DateUtil.isWholeHour(plan.getPlanTime())) {
			return true;
		}
		return false;
	}
}
