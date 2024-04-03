package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.RouteSta;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsEndurance;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ServiceType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElectricitySupplement {
	
	private ScheduleParam scheduleParam;
	
	private RouteSchedule routeSchedule;
	
	private List<Bus> busNeed2ElecSupplyList=new ArrayList<Bus>();
	
	public ElectricitySupplement(ScheduleParam scheduleParam) {
		this.scheduleParam=scheduleParam;
	}
	
	public void setRouteSchedule(RouteSchedule routeSchedule) {
		this.routeSchedule = routeSchedule;
	}
	
	public boolean setNusNeed2ElecSupplyList(RouteSchedule routeSchedule) {
		if(scheduleParam.getEnduranceMileage()==null||scheduleParam.getElecSupplySettingList().isEmpty()) {//没有配置参数
			return false;
		}
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.isSingleClass())
				continue;
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			double runMileageTotal=0.0;
			for(Trip trip:busTripList) {
				double runMileage=scheduleParam.getRunMileage(trip);
				runMileageTotal+=runMileage;
			}
			System.out.println(bus.getBusName()+"\t"+runMileageTotal);
			if(runMileageTotal>scheduleParam.getEnduranceMileage()) {
				busNeed2ElecSupplyList.add(bus);
			}else {
				System.out.println("aaa");
			}
		}
		if(!busNeed2ElecSupplyList.isEmpty()) {
			busNeed2ElecSupplyList.clear();
			for(Bus bus:routeSchedule.getTripMap().keySet()) {
				if(bus.isSingleClass())
					continue;
				busNeed2ElecSupplyList.add(bus);
			}
			return true;
		}
		return false;
	}
	
	private boolean isElecSupplyBySetting(Trip trip) {
		RouteSta routeStaLast=scheduleParam.getRouteSta(trip.getLastRouteStaId());
		for(ScheduleParamsEndurance supplySetting:scheduleParam.getElecSupplySettingList()) {
			if(supplySetting.getStationId().equals(routeStaLast.getStationId())) {
				if(DateUtil.isInTimeRange(trip.getTripEndTime(), DateUtil.getDateHM(supplySetting.getBeginTime()), DateUtil.getDateHM(supplySetting.getEndTime()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setBusElecSupply(Trip trip) {
		if(busNeed2ElecSupplyList.contains(trip.getBus())){
			if(trip.getTripEndTime()==null||!isElecSupplyBySetting(trip)) {//不在充电时间范围
				return;
			}
			int elecSupplyTime=30;
			trip.setNextObuTimeMin(DateUtil.getDateAddMinute(trip.getTripEndTime(), elecSupplyTime));
			trip.setElecSupplyAfterTrip(true);
			busNeed2ElecSupplyList.remove(trip.getBus());
			Trip elecSupplyTrip=new Trip(trip.getBus(), Direction.NODIRECTION.getValue(), trip.getTripEndTime());
			elecSupplyTrip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), elecSupplyTime));
			elecSupplyTrip.setFirstRouteStaId(trip.getLastRouteStaId());
			elecSupplyTrip.setLastRouteStaId(trip.getLastRouteStaId());
			elecSupplyTrip.setServiceType(ServiceType.ELECTRICITY_SUPPLEMENT);
			List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
			busTripList.add(elecSupplyTrip);
		}
	}
	
	public void setBusElecSupply(int direction,List<Trip> busQueue) {
		if(busNeed2ElecSupplyList.isEmpty())
			return;
		int elecSupplyTime=30;//默认补电30分钟
		List<Trip> busQueueAvailable=new ArrayList<Trip>();
		for(Trip trip:busQueue) {
			if(!trip.isQuitMark()) {
				busQueueAvailable.add(trip);
			}
		}
		if(busNeed2ElecSupplyList.size()==1) {
			System.out.println("aaa");
		}
		Trip tripLastFull=routeSchedule.getTripFullLast(direction);
		Date obuTimeLast=tripLastFull.getTripBeginTime();
		for(int i=0;i<busQueueAvailable.size()-1;i++) {
			Trip trip=busQueueAvailable.get(i);
			if(trip.getDirection()==0&&DateUtil.getDateHM("1016").equals(trip.getTripEndTime())) {
				System.out.println("aaa");
			}
			Trip tripNext=busQueueAvailable.get(i+1);
			if(obuTimeLast.after(scheduleParam.getLatestTime(direction))) {
				return;
			}
			int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, direction);
			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
			/*if(tripNext.getBus().isSingleClass()) {//下台车是单班车
				if((i==0&&tripLastFull.getBus().isSingleClass())||(i>0&&busQueueAvailable.get(i-1).getBus().isSingleClass())) {//上台车是单班车，暂时不补电，避免两台单班车走在一起
					obuTimeLast=obuTime;
					continue;
				}
			}*/
			if(busNeed2ElecSupplyList.contains(trip.getBus())){
				if(trip.getTripEndTime()==null||!isElecSupplyBySetting(trip)) {//不在充电时间范围
					obuTimeLast=obuTime;
					continue;
				}
				boolean elecSupply=false;
				Date nextObuTimeMin=DateUtil.getDateAddMinute(trip.getTripEndTime(), elecSupplyTime);//补电后的发班时间
				if((!busNeed2ElecSupplyList.contains(tripNext.getBus())&&!tripNext.getNextObuTimeMin().after(obuTime))
						||!nextObuTimeMin.after(obuTime)) {//这台车充电，下台车不补电可以赶上,或者这台车可以赶上
					trip.setNextObuTimeMin(nextObuTimeMin);
					if(DateUtil.getDateHM("0955").equals(trip.getTripEndTime())||
							DateUtil.getDateHM("1005").equals(trip.getTripEndTime()))
						System.out.println("aa");
					busQueueAvailable.remove(trip);//先从原来位置删除
					busQueue.remove(trip);//先从原来位置删除
					for(int j=i+1;j<busQueueAvailable.size();j++) {
						tripNext=busQueueAvailable.get(j);
						if(!tripNext.getNextObuTimeMin().before(trip.getNextObuTimeMin())) {
							busQueueAvailable.add(j, trip);
							break;
						}
					}
					if(!busQueueAvailable.contains(trip)) {//加不回去，加到暂存队列
						routeSchedule.addShortTripBack(trip);
					}else {
						for(int j=0;j<busQueue.size();j++) {//把加气车加回去
							tripNext=busQueue.get(j);
							if(!tripNext.getNextObuTimeMin().before(trip.getNextObuTimeMin())) {
								busQueue.add(j, trip);
								break;
							}
						}
					}
					elecSupply=true;
				}
				if(elecSupply){
					trip.setElecSupplyAfterTrip(true);
					busNeed2ElecSupplyList.remove(trip.getBus());
					Trip elecSupplyTrip=new Trip(trip.getBus(), Direction.NODIRECTION.getValue(), trip.getTripEndTime());
					elecSupplyTrip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), elecSupplyTime));
					elecSupplyTrip.setFirstRouteStaId(trip.getLastRouteStaId());
					elecSupplyTrip.setLastRouteStaId(trip.getLastRouteStaId());
					elecSupplyTrip.setServiceType(ServiceType.ELECTRICITY_SUPPLEMENT);
					List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
					busTripList.add(elecSupplyTrip);
					if(busQueueAvailable.get(i)!=trip)//换过位
						i--;
				}
			}
			obuTimeLast=obuTime;
		}
	}
}
