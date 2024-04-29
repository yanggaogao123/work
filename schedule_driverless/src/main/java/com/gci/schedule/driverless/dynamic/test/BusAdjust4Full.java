package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.LatestBusTrip;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusAdjust4Full extends BusReverseAdjust4Full{

	public BusAdjust4Full(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		super(scheduleParam, routeSchedule);
		genetateLatestBusTripList();
	}
	
	private boolean isResetLatestBus(Bus latestBusOld, int direction) {
		List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses();
		Trip latestBusTrip=null;
		for(Trip trip:busQueue) {
			if(trip.getBus()==latestBusOld){
				latestBusTrip=trip;
				break;
			}
		}
		if(latestBusTrip!=null) {
			if(latestBusTrip.getDirection()!=direction) {
				Date latestTime=scheduleParam.getLatestTime(direction);//可能过对面做尾车
				if(latestBusTrip.getTripEndTime()!=null&&
					!latestBusTrip.getTripEndTime().before(latestTime)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void updateLatestBusTripList(int direction,Trip latestBusTrip) {
		LatestBusTrip latestBusTripMatch=null;
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(1-direction);
		for(int i=0;i<latestBusTripList.size();i++) {
			LatestBusTrip trip=latestBusTripList.get(i);
			if(trip.getDirection()==1-latestBusTrip.getDirection()) {
				if(!trip.getTripBeginTime().before(latestBusTrip.getNextObuTimeMin())) {
					latestBusTripMatch=trip;
					break;
				}
			}
		}
		if(latestBusTripMatch!=null) {
			int diff=DateUtil.getMinuteInterval(latestBusTrip.getNextObuTimeMin(), latestBusTripMatch.getTripBeginTime());
			for(int i=0;i<latestBusTripList.size();) {
				LatestBusTrip trip=latestBusTripList.get(i);
				if(trip==latestBusTripMatch) {
					break;
				}
				latestBusTripList.remove(i);
			}
			System.out.println("aaaa");
			for(int i=0;i<latestBusTripList.size();i++) {
				LatestBusTrip trip=latestBusTripList.get(i);
				Date tripBeginTimeOld=trip.getTripBeginTime();
				if((latestBusTripList.size()-i-1)!=0){
					int intervalDiff=diff/(latestBusTripList.size()-i-1);
					trip.setTripBeginTime(DateUtil.getDateAddMinute(trip.getTripBeginTime(), intervalDiff-diff));
				}
				System.out.println(tripBeginTimeOld+"====>"+trip.getTripBeginTime());
				diff=DateUtil.getMinuteInterval(trip.getTripBeginTime(), tripBeginTimeOld);
			}
			System.out.println("aaa");
			
		}else {
			System.out.println("aaa");
		}
	
	}
	
	private Trip getLatestBusTrip4Full(int direction) {
		List<DirectionObuTimeRange> obuTimeRangeList=obuTimeRangeMap.get(1-direction);
		if(obuTimeRangeList==null)
			return null;
		DirectionObuTimeRange obuTimeRange=null;
		for(DirectionObuTimeRange obuTimeRangeTemp:obuTimeRangeList) {
			Trip lastTrip=routeSchedule.getTripLast(obuTimeRangeTemp.getDirection());
			if(lastTrip.getTripBeginTime().after(obuTimeRangeTemp.getEndTime())) {//当前已经过了边界
				obuTimeRange=obuTimeRangeTemp;
			}
		}
		if(obuTimeRange==null)
			return null;
		List<Trip> tripList=routeSchedule.getTripList(obuTimeRange.getDirection());
		Trip latestBusTrip=null;
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			if(!trip.getBus().isSingleClass()&&!trip.isQuitMark()&&(trip.getBus().getShiftType()==null||
					trip.getBus().getShiftType().getShiftType()==ShiftType.EVENING_SHIFT.getValue())) {//双班或晚班
				if(DateUtil.isInTimeRange(trip.getTripBeginTime(), obuTimeRange.getBeginTime(), obuTimeRange.getEndTime())||
						trip.getTripBeginTime().equals(obuTimeRange.getEndTime())) {//在时段区间
					if(!trip.isShortLine()) {//非短线
						latestBusTrip=trip;
						break;
					}
				}
			}
		}
		return latestBusTrip;
	}

	@Override
	public Date getObuTime(Trip tripArrival,int direction) {
		if(latestBusMain==null)
			return null;
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		List<Trip> busQueue=new ArrayList<Trip>();
		for(Trip trip:busQueueTo) {
			if(!trip.isQuitMark())
				busQueue.add(trip);
		}
		for(Trip trip:busQueueFrom) {
			if(trip.isQuitMark())
				continue;
			if(trip.getBus().isSingleClass()&&trip.getTripBeginTime()!=null) {
				Date arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, trip.getTripBeginTime());
				if(!arrivalTime.before(scheduleParam.getMiddleStopOffDutyBeginTime())) {
					continue;
				}
			}
			busQueue.add(trip);
		}
		Trip latestBusTrip=null;
		int busNumber=0;
		for(Trip trip:busQueue) {
			busNumber++;
			if(trip.getBus()==latestBusMain) {
				latestBusTrip=trip;
				break;
			}
			
		}
		if(latestBusTrip!=null) {
			int latestBusDrection=Direction.DOWN.getValue();
			if(mainDirection!=Direction.NODIRECTION.getValue())
				latestBusDrection=1-mainDirection;
			Date latestBusObuTime=null;
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusDrection);
			if(latestBusTripList.isEmpty()||(latestBusTripList.size()==1&&latestBusDrection==direction)) {//对面尾车已发
				busNumber=0;
				for(Trip trip:busQueue) {
					busNumber++;
					if(trip.getBus()==latestBusSecondary) {
						latestBusTrip=trip;
						break;
					}
				}
				latestBusObuTime=scheduleParam.getLatestTime(direction);
			}else {
				LatestBusTrip latestBusTripReference=latestBusTripList.get(0);
				if(latestBusTripReference.getDirection()==latestBusTrip.getDirection()) {//方向错位了
					latestBusTripList.remove(0);
					latestBusTripReference=latestBusTripList.get(0);
				}
				if(latestBusTrip.getDirection()==direction) {
					if(latestBusTripList.size()>1) {
						latestBusTripReference=latestBusTripList.get(1);
					}
				}
				latestBusObuTime=latestBusTripReference.getTripBeginTime();
				if(busNumber==1) {//发对应尾车班次
					latestBusTripList.remove(0);
					if(!latestBusTripList.isEmpty()) {
						if(latestBusObuTime.before(latestBusTrip.getNextObuTimeMin())){//落后标准时间
							Date obuTimeNext=latestBusTripList.get(0).getTripBeginTime();
						}
					}
				}
			}
			Trip tripLast=routeSchedule.getTripLast(direction);
			int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestBusObuTime)/busNumber;
			Date obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
			/*if(latestBusSecondary!=null&&busQueue.get(0).getBus()==latestBusSecondary) {
				latestBusTripList=latestBusTripMap.get(1-latestBusDrection);
				Trip latestBusTripReference=latestBusTripList.get(0);
				if(latestBusTripReference.getDirection()!=direction) {
					latestBusTripList.remove(0);
					latestBusTripReference=latestBusTripList.get(0);
				}
				if(obuTime.after(latestBusTripReference.getTripBeginTime())) {
					obuTime=latestBusTripReference.getTripBeginTime();
				}
				latestBusTripList.remove(0);
			}*/
			return obuTime;
		}
		return null;
	}
}
