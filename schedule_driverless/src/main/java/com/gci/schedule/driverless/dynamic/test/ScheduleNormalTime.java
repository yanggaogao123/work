package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.RouteStaTurn;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.util.TripBeginTimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ScheduleNormalTime {

	private RouteSchedule routeSchedule;
	
	private ScheduleParam scheduleParam;
	
	private BusAdjustBase busReverseAdjustBase;

	public ScheduleNormalTime(RouteSchedule routeSchedule, ScheduleParam scheduleParam) {
		super();
		this.routeSchedule = routeSchedule;
		this.scheduleParam = scheduleParam;
		if(scheduleParam.getEndDirection()==null) {
			Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
			Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
			int endDirection=Direction.UP.getValue();
			if(latestTimeUp.after(latestTimeDown)) {
				endDirection=Direction.DOWN.getValue();
			}
			Date latestTime=scheduleParam.getLatestTime(endDirection);
			Date latestTimeReverse=scheduleParam.getLatestTime(1-endDirection);
			Date arrivalTime=scheduleParam.getArrivalTime(latestTime, endDirection);
			if(arrivalTime.before(latestTimeReverse)) {
				scheduleParam.setEndDirection(endDirection);
			}else {
				scheduleParam.setEndDirection(Direction.NODIRECTION.getValue());
			}
		}
		this.busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
	}
	
	private List<Trip> getBusAvailable(int direction){
		List<Trip> busAvailable=new ArrayList<Trip>();
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		for(Trip trip:busQueueTo) {
			boolean quit=false;
			if(!trip.isQuitMark()) {
				Bus bus=trip.getBus();
				if(bus.isSingleClass()||bus.getShiftType()!=null) {//非双班车
					Date stopBeginTime=null;//收车开始时间
					Date stopEndTime=null;//收车结束时间
					if(bus.isSingleClass()) {
						stopBeginTime=scheduleParam.getMiddleStopBeginTime();
						stopEndTime=scheduleParam.getMiddleStopEndTime();
					}else {
						stopBeginTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
						stopEndTime=DateUtil.getDateAddMinute(stopBeginTime, 30);//下班窗口限制在30分钟
					}
					Date tripEndTime=trip.getTripEndTime();
					if(tripEndTime!=null) {
						int restTime=scheduleParam.getRestTime(tripEndTime, trip.getDirection());
						Date obuTime=DateUtil.getDateAddMinute(tripEndTime, restTime);//这边的发班时间
						if(scheduleParam.getEndDirection()==null) {//两边都可以收车
							if(!obuTime.before(stopBeginTime)) {//再发过了时间
								quit=true;
							}
							Date arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);//到对面到的时间
							if(arrivalTime.after(stopEndTime)) {//再过对面过了下班时间
								for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {//短线回来
									arrivalTime=scheduleParam.getArrivalTime(obuTime, routeStaTurn);
									if(DateUtil.isInTimeRange(arrivalTime, stopBeginTime, stopEndTime)) {//短线调位
										quit=true;
										break;
									}
								}
							}
						}else {
							if(scheduleParam.isEndDirectionTrip(trip)) {//过来可以收车，方向一致
								if(!obuTime.before(stopBeginTime)) {//再发过了时间
									quit=true;
								}
								Date arrivalTime=scheduleParam.getObuTimeNextRound(direction, obuTime);//在回来的时间
								if(arrivalTime==null||arrivalTime.after(stopEndTime)) {//再回来过了下班时间
									for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {//短线回来
										arrivalTime=scheduleParam.getArrivalTime(obuTime, routeStaTurn);
										if(DateUtil.isInTimeRange(arrivalTime, stopBeginTime, stopEndTime)) {//短线调位
											quit=true;
											break;
										}
									}
								}
							}
						}
					}
				}
				if(!quit) {
					busAvailable.add(trip);
				}
			}
		}
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		for(Trip trip:busQueueFrom) {
			if(!trip.isQuitMark()) {//到对面不退出
				Bus bus=trip.getBus();
				boolean quit=false;
				if(bus.isSingleClass()||bus.getShiftType()!=null) {
					Date stopBeginTime=null;//收车开始时间
					Date stopEndTime=null;//收车结束时间
					if(bus.isSingleClass()) {
						stopBeginTime=scheduleParam.getMiddleStopBeginTime();
						stopEndTime=scheduleParam.getMiddleStopEndTime();
					}else {
						stopBeginTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
						stopEndTime=DateUtil.getDateAddMinute(stopBeginTime, 30);
					}
					Date tripEndTime=trip.getTripEndTime();
					if(tripEndTime!=null) {
						int restTime=scheduleParam.getRestTime(tripEndTime, trip.getDirection());
						Date obuTimeReverse=DateUtil.getDateAddMinute(tripEndTime, restTime);//对面发班时间
						Date arrivalTime=scheduleParam.getArrivalTime(obuTimeReverse, 1-direction);
						restTime=scheduleParam.getRestTime(tripEndTime, 1-direction);
						Date obuTime=DateUtil.getDateAddMinute(tripEndTime, restTime);//到这边的发班时间
						arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);//再回对面的到达时间
						if(scheduleParam.getEndDirection()==null) {//两边都可收车
							if(!obuTimeReverse.before(stopBeginTime)) {//到对面已经可以下班
								quit=true;
							}
							if(!obuTime.before(stopBeginTime)) {//过来已经可以下班
								quit=true;
							}
							if(arrivalTime.after(stopEndTime)) {//再到对面过了下班时间
								for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
									arrivalTime=scheduleParam.getArrivalTime(obuTime, routeStaTurn);
									if(DateUtil.isInTimeRange(arrivalTime, stopBeginTime, stopEndTime)) {//短线调位
										quit=true;
										break;
									}
								}
							}
						}else {
							if(scheduleParam.isEndDirectionTrip(trip)) {//对面收车
								if(!obuTimeReverse.before(stopBeginTime)) {//到对面已经可以下班
									quit=true;
								}
								if(arrivalTime.after(stopEndTime)) {//再回来过了下班时间
									for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(1-direction)) {
										arrivalTime=scheduleParam.getArrivalTime(obuTimeReverse, routeStaTurn);
										if(DateUtil.isInTimeRange(arrivalTime, stopBeginTime, stopEndTime)) {//短线调位
											quit=true;
											break;
										}
									}
								}
							}else {
								if(!obuTime.before(stopBeginTime)) {
									quit=true;
								}
								arrivalTime=scheduleParam.getObuTimeNextRound(direction, obuTime);
								if(arrivalTime==null||arrivalTime.after(stopEndTime)) {
									for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
										arrivalTime=scheduleParam.getArrivalTime(obuTime, routeStaTurn);
										if(DateUtil.isInTimeRange(arrivalTime, stopBeginTime, stopEndTime)) {//短线调位
											quit=true;
											break;
										}
									}
								}
							}
						}
					}
				}
				if(!quit) {
					busAvailable.add(trip);
				}
			}
		}
		return busAvailable;
	}
	
	public void calculate() {
		setSingleBus(Direction.UP.getValue());
		setSingleBus(Direction.DOWN.getValue());
		List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
		List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
		Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
		while(true) {
			Trip lastTripUp=routeSchedule.getTripLast(Direction.UP.getValue());
			Trip lastTripDown=routeSchedule.getTripLast(Direction.DOWN.getValue());
			if(!lastTripUp.getTripBeginTime().before(latestTimeUp)&&
					!lastTripDown.getTripBeginTime().before(latestTimeDown)) {
				break;
			}
			Trip tripArrivalDown=null;
			if(!busQueueUp.isEmpty()) {
				tripArrivalDown=busQueueUp.get(0);
				if(tripArrivalDown.isQuitMark()) {
					busQueueUp.remove(tripArrivalDown);
					Bus bus=tripArrivalDown.getBus();
					if(bus.isSingleClass()&&!bus.isHasMiddleStop()) {
						routeSchedule.singleClassBusAdd(tripArrivalDown, Direction.DOWN.getValue());
						bus.setHasMiddleStop(true);
					}
					continue;
				}
			}
			Trip tripArrivalUp=null;
			if(!busQueueDown.isEmpty()) {
				tripArrivalUp=busQueueDown.get(0);
				if(tripArrivalUp.isQuitMark()) {
					busQueueDown.remove(tripArrivalUp);
					Bus bus=tripArrivalUp.getBus();
					if(bus.isSingleClass()&&!bus.isHasMiddleStop()) {
						routeSchedule.singleClassBusAdd(tripArrivalUp, Direction.UP.getValue());
						bus.setHasMiddleStop(true);
					}
					continue;
				}
			}
			int direction=Direction.UP.getValue();
			List<Trip> busQueue=busQueueDown;
			if(tripArrivalUp==null||!lastTripUp.getTripBeginTime().before(latestTimeUp)||(lastTripDown.getTripBeginTime().before(latestTimeDown)&&
					(tripArrivalDown!=null&&tripArrivalDown.getNextObuTimeMin().before(tripArrivalUp.getNextObuTimeMin())))) {//下行没车或上行末班车已发或下行末班车未发并上行车先到
				direction=Direction.DOWN.getValue();
				busQueue=busQueueUp;
			}
			if(busQueue.isEmpty())
				return;
			singleClassRecovery(direction);
			Trip tripArrival=busQueue.get(0);
			if(DateUtil.getDateHM("2108").equals(tripArrival.getTripEndTime()))
				System.out.println("aaa");
			Bus bus=tripArrival.getBus();
			Date obuTime=null;
			if(!busReverseAdjustBase.isLatestBusLastRound()&&tripArrival.getTripEndTime()!=null) {
				Integer restTime=scheduleParam.getLeaveImmediatelyInterval(direction);
				if(restTime!=null) {
					obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), restTime);
				}
			}
			if(obuTime==null) {
				if(tripArrival.getNextObuTimeMin().before(DateUtil.getDateHM("1300"))) {
					obuTime=getObuTimeBefore4Adjust(direction);
				}else {
					obuTime=getObuTime4Adjust(tripArrival, direction);
				}
			}
			if(obuTime==null) {
				obuTime=getObuTimeBefore4Adjust(direction);
				if(obuTime==null) {
					obuTime=tripArrival.getNextObuTimeMin();
				}
				//obuTime=tripArrival.getNextObuTimeMin();
			}
			if(obuTime.before(tripArrival.getNextObuTimeMin())) {
				if(tripArrival.getTripEndTime()!=null&&!tripArrival.getTripEndTime().before(obuTime)) {
					obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
				}
			}
			if(obuTime.after(scheduleParam.getLatestTime(direction))) {
				obuTime=scheduleParam.getLatestTime(direction);
			}
			routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
			busQueue.remove(tripArrival);
			Trip trip=new Trip(bus, direction, obuTime, scheduleParam, null);
			if(!scheduleParam.isEndDirectionTrip(trip)&&
				trip.getTripEndTime().after(scheduleParam.getLatestTime(1-direction))) {//按这个时间发车到对面过了末班车时间
				Trip tripFullLastReverse=routeSchedule.getTripFullLast(1-direction);
				Date latestTimeReverse=scheduleParam.getLatestTime(1-direction);
				if(tripFullLastReverse.getTripBeginTime().before(latestTimeReverse)) {
					List<Trip> busQueueTemp=routeSchedule.getBusQueue(direction);
					int busNumber=0;
					for(Trip tripTemp:busQueueTemp) {
						if(tripTemp.getTripEndTime()==null||tripTemp.getTripEndTime().before(latestTimeReverse)) {
							busNumber++;
						}
					}
					if(busNumber>0) {
						int interval=DateUtil.getMinuteInterval(tripFullLastReverse.getTripBeginTime(), latestTimeReverse)/busNumber;
						if(interval>scheduleParam.getMaxInterval(tripFullLastReverse.getTripBeginTime(), direction)) {//对面会断位
							int diff=DateUtil.getMinuteInterval(trip.getTripEndTime(), latestTimeReverse);
							if(tripArrival.getTripEndTime()!=null) {
								int restTime=DateUtil.getMinuteInterval(tripArrival.getTripEndTime(), trip.getTripBeginTime());
								if(restTime>=diff+5) {//提前5分钟过对面
									obuTime=DateUtil.getDateAddMinute(obuTime, -(diff+5));
								}else if(restTime>=diff+1){
									obuTime=DateUtil.getDateAddMinute(obuTime, 1);
								}
							}else {
								obuTime=DateUtil.getDateAddMinute(obuTime, -(diff+5));
							}
							if(!obuTime.equals(trip.getTripBeginTime())) {
								diff=DateUtil.getMinuteInterval(obuTime, trip.getTripBeginTime());
								trip.setTripBeginTime(obuTime);
								trip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), -diff));
							}
						}
					}
				}
			}
			if(tripArrival.getTripBeginTime()==null)
				routeSchedule.addMiddleStopTrip(trip);
			Trip tripFullLast=routeSchedule.getTripFullLast(direction);
			trip.setLastTrip(tripFullLast);
			routeSchedule.addTrip(trip);
			Integer restTime=scheduleParam.getLeaveImmediatelyInterval(1-direction);//到对面到站即走
			if(restTime!=null) {//到站即走
				/*if(!busReverseAdjustBase.isLatestBusLastRound()) {//非最后一轮
					obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), restTime);
				}else {
					//obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), restTime);
					obuTime=busReverseAdjustBase.getObuTime(1-direction);
				}*/
				if(scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {//双边停车
					if(busReverseAdjustBase.isLatestBusLastRound()) {
						if(bus==busReverseAdjustBase.getLatestBusMain()&&
							scheduleParam.isEndDirectionTrip(bus.getStartDirection(), 1-direction)) {//防止发错到站即走方向模板，可能发总站末班
							obuTime=scheduleParam.getLatestTime(1-direction);
						}else {
							obuTime=getLastRoundObuTime4SecondaryStation(direction);//主站方向
						}
					}else {
						Date obuTimeNext=scheduleParam.getObuTimeNextRound(direction, obuTime);
						if(obuTimeNext!=null&&obuTimeNext.after(scheduleParam.getLatestTime(direction))) {//对面最后一圈
							int busNumber=1;
							Date obuTimeTemp=obuTime;
							int interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), obuTime);
							for(Trip tripArrivalTemp:busQueue) {
								obuTimeTemp=DateUtil.getDateAddMinute(obuTimeTemp, interval);
								if(!obuTimeTemp.after(scheduleParam.getLatestTime(direction))) {
									obuTimeNext=scheduleParam.getMinObuTimeNext(direction, obuTimeTemp);
									if(!obuTimeNext.after(scheduleParam.getLatestTime(1-direction))) {
										busNumber++;
									}
								}
							}
							tripFullLast=routeSchedule.getTripFullLast(1-direction);
							interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), scheduleParam.getLatestTime(1-direction))/busNumber;
							obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
						}else {
							obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), restTime);
						}
					}
				}else {
					if(obuTime.equals(scheduleParam.getLatestTime(direction))) {//这台尾车
						if(trip.getTripEndTime().before(scheduleParam.getLatestTime(1-direction))) {//赶得上做末班车
							obuTime=scheduleParam.getLatestTime(1-direction);
						}else {//赶不上改上一班时间
							tripFullLast=routeSchedule.getTripFullLast(1-direction);
							tripFullLast.setTripBeginTime(scheduleParam.getLatestTime(1-direction));
							tripFullLast.setTripEndTime(scheduleParam.getArrivalTime(scheduleParam.getLatestTime(1-direction), 1-direction));
							continue;
						}
						
					}else {
						obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), restTime);
					}
				}
				
				busQueue=routeSchedule.getBusQueue(direction);
				busQueue.remove(trip);
				if(obuTime==null)
					continue;
				if(trip.getTripEndTime()!=null&&!obuTime.after(trip.getTripEndTime())) {
					if(trip.isElecSupplyAfterTrip()) {
						obuTime=trip.getNextObuTimeMin();
					}else {
						obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), 1);
					}
				}
				if(!obuTime.after(scheduleParam.getLatestTime(1-direction))){
					trip=new Trip(bus, 1-direction, obuTime, scheduleParam, null);
					routeSchedule.addTrip(trip);
				}
			}
		}
		
	}
	
	private Date getLastRoundObuTime4SecondaryStation(int direction) {
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		int busNumber=1;
		Trip lastTripFull=routeSchedule.getTripFullLast(direction);
		if(lastTripFull.getBus()!=busReverseAdjustBase.getLatestBusSecondary()) {
			for(Trip tripArrval:busQueueTo) {
				busNumber++;
				if(tripArrval.getBus()==busReverseAdjustBase.getLatestBusSecondary()) {
					break;
				}
			}
		}
		Date latestTime=scheduleParam.getLatestTime(1-direction);
		lastTripFull=routeSchedule.getTripFullLast(1-direction);
		if(lastTripFull.getTripBeginTime().equals(latestTime))
			return null;
		int interval=DateUtil.getMinuteInterval(lastTripFull.getTripBeginTime(), latestTime)/busNumber;
		Date obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
		return obuTime;
	}
	
	/*private void latestBusRestTimeOptimise(int direction) {
		List<Trip> tripList=routeSchedule.getTripList(Direction.UP.getValue());
		List<Trip> tripListReverse=routeSchedule.getTripList(Direction.DOWN.getValue());
		Trip latestTripUp=tripListUp.get(tripListUp.size()-1);
		if(latestTripUp.getBus()==tripListDown.get(tripListDown.size()-2).getBus()) {
			int interval=DateUtil.getMinuteInterval(tripListDown.get(tripListDown.size()-2).getTripBeginTime(), scheduleParam.getLatestTime(Direction.DOWN.getValue()));
			int maxInterval=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(scheduleParam.getLatestTime(Direction.DOWN.getValue()), -1), Direction.DOWN.getValue());
			if(interval>maxInterval) {
				
			}
		}
	}*/
	
	private Date getObuTime4Adjust(Trip tripArrival,int direction) {
		busReverseAdjustBase.setLatestBus();
		Date obuTime=busReverseAdjustBase.getObuTime(tripArrival, direction);
		return obuTime;
	}
	
	private Date getObuTimeBefore4Adjust(int direction) {
		List<Trip> busAvailableList=getBusAvailable(direction);
		Integer interval=null;
		List<Date> nextObuTimeMinList=new ArrayList<Date>();
		for(int i=1;i<busAvailableList.size()&&i<4;i++) {
			Trip trip=busAvailableList.get(i);
			Date nextObuTimeMin=trip.getNextObuTimeMin();
			if(trip.getDirection()==direction) {
				nextObuTimeMin=scheduleParam.getMinObuTimeNextLap(trip);
			}else {
				if(trip.getTripBeginTime()!=null&&
						!trip.getTripBeginTime().before(scheduleParam.getLatestTime(1-direction))) {//对面末班车
					if(!trip.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
						nextObuTimeMin=scheduleParam.getLatestTime(direction);
						trip.setNextObuTimeMin(nextObuTimeMin);
					}
				}
			}
			if(nextObuTimeMin==null||nextObuTimeMin.after(scheduleParam.getLatestTime(direction))) {
				if(busAvailableList.size()>1) {
					Trip tripLast=busAvailableList.get(i-1);
					if(tripLast.getDirection()!=direction&&!tripLast.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
						tripLast.setNextObuTimeMin(scheduleParam.getLatestTime(direction));
					}
				}
				if(!nextObuTimeMinList.isEmpty()) {
					nextObuTimeMinList.set(nextObuTimeMinList.size()-1, scheduleParam.getLatestTime(direction));
				}
				nextObuTimeMin=null;
			}
			if(nextObuTimeMin!=null)
				nextObuTimeMinList.add(nextObuTimeMin);
		}
		Trip tripLast=routeSchedule.getTripFullLast(direction);
		for(int i=0;i<nextObuTimeMinList.size();i++) {
			Date nextObuTimeMin=nextObuTimeMinList.get(i);
			if(nextObuTimeMin.after(tripLast.getTripBeginTime())) {
				int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), nextObuTimeMin)/(i+2);
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
		}
		Date obuTimeNext=scheduleParam.getObuTimeNextRound(direction, tripLast.getTripBeginTime());
		int intervalAvg=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), obuTimeNext)/busAvailableList.size();
		if(interval==null||interval<intervalAvg)
			interval=intervalAvg;
		Date obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
		return obuTime;
	}
	
	private void singleClassRecovery(int direction) {
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		Trip tripLast=routeSchedule.getTripLast(direction);
		int separate=1;
		List<Trip> singleClassQueue=routeSchedule.getSingleClassQueue(direction);
		if(!singleClassQueue.isEmpty()) {
			int j=0;
			for(int i=0;i<bus_queue_to.size();i++) {
				Trip trip=bus_queue_to.get(i);
				if(trip.getBus().isSingleClass()&&trip.getTripBeginTime()==null) {
					j=i+separate+1;//找到最后一台单班复行车后可复行位置
				}
			}
			if(j==0) {//到站队列里没有单班车
				if(tripLast.getBus().isSingleClass()) {//最后一班是单班
					j=separate;
				}
			}
			Date lastTripObuTime=tripLast.getTripBeginTime();
			Date recoveryBeginTime=scheduleParam.getMiddleStopRecoveryBeginTime();
			for(;j<bus_queue_to.size();j++) {
				Trip trip=bus_queue_to.get(j);
				if(j>0) {
					lastTripObuTime=bus_queue_to.get(j-1).getNextObuTimeMin();
				}
				if(!trip.getNextObuTimeMin().before(recoveryBeginTime)) {
					Date nextObuTimeMin=new Date((lastTripObuTime.getTime()+trip.getNextObuTimeMin().getTime())/2);
					if(nextObuTimeMin.before(recoveryBeginTime))
						continue;
					Trip preTrip=routeSchedule.singleClassBusPeek(direction);
					if(preTrip==null)
						break;
					int restTime=DateUtil.getMinuteInterval(nextObuTimeMin, preTrip.getTripEndTime());
					int middleStopRestTimeMin=scheduleParam.getMiddleStopMinimumRestTime();
					if(restTime<middleStopRestTimeMin)//没休息够
						continue;
					routeSchedule.singleClassBusRemove(direction, preTrip);
					Bus bus=preTrip.getBus();
					if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==9)
						System.out.println("aaa");
					trip=new Trip();
					trip.setBus(bus);
					trip.setNextObuTimeMin(nextObuTimeMin);
					bus_queue_to.add(j, trip);
					j=j+separate;//隔台复行
					if(singleClassQueue.isEmpty())
						break;
				}
			}
		
		}
	}
	
	
	private void setSingleBus(int direction) {
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.getStartDirection()==direction
				&&bus.isSingleClass()) {//已经设置过单班
				return;
			}
		}
		Integer busNumberSingle=scheduleParam.getBusNumberSinglePreset(direction);
		if(busNumberSingle!=null&&busNumberSingle!=0) {
			List<Trip> firstTripList=new ArrayList<Trip>();
			for(Bus bus:routeSchedule.getTripMap().keySet()) {
				if(bus.getStartDirection()==direction) {
					List<Trip> busTripList=routeSchedule.getTripList(bus);
					firstTripList.add(busTripList.get(0));
				}
			}
			Collections.sort(firstTripList, new TripBeginTimeComparator());
			int minOrderNumber=scheduleParam.getMiddleStopBeginOrderNumber();
			for(int i=firstTripList.size()-1;i>=minOrderNumber-1;i=i-2) {
				Trip firstTrip=firstTripList.get(i);
				Bus bus=firstTrip.getBus();
				bus.setSingleClass(true);
				busNumberSingle--;
				if(busNumberSingle==0)
					break;
			}
			if(busNumberSingle>0) {
				for(int i=firstTripList.size()-1;i>=0;i--) {
					Trip firstTrip=firstTripList.get(i);
					Bus bus=firstTrip.getBus();
					if(!bus.isSingleClass()) {
						bus.setSingleClass(true);
						busNumberSingle--;
						if(busNumberSingle==0)
							break;
					}
				}
			}
		}
		
	}
}
