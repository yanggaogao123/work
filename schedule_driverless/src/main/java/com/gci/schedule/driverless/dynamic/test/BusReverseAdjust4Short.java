package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.LatestBusTrip;
import com.gci.schedule.driverless.dynamic.bean.RouteStaTurn;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;

import java.util.*;
import java.util.Map.Entry;

public class BusReverseAdjust4Short extends BusAdjustBase{
	
	private List<Trip> tripArrivalAdjustList=new ArrayList<Trip>();//原来的尾车赶不上，要短线调位

	public BusReverseAdjust4Short(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		super(scheduleParam, routeSchedule);
		if(scheduleParam.isTestLineShort()) {
			if(scheduleParam.getEndDirection()!=null&&
					scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {
				genetateLatestBusTripList();
				setLatestBusTripBeginTimeLatest();
				initObuTimeRange();
			}
		}
	}
	
	protected void updateLatestBusTripList(Trip latestBusTrip) {
		LatestBusTrip latestBusTripMatch=null;
		int direction=latestBusTrip.getBus().getStartDirection();
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);
		for(int i=0;i<latestBusTripList.size();i++) {
			LatestBusTrip trip=latestBusTripList.get(i);
			if(trip.getDirection()==1-latestBusTrip.getDirection()) {
				if(!trip.getTripBeginTime().before(latestBusTrip.getNextObuTimeMin())&&
						latestBusTripMatch==null) {
					latestBusTripMatch=trip;
					break;
				}
			}
		}
		if(latestBusTripMatch!=null) {
			int diff=DateUtil.getMinuteInterval(latestBusTrip.getNextObuTimeMin(), latestBusTripMatch.getTripBeginTime());
			int index=latestBusTripList.indexOf(latestBusTripMatch);
			if(latestBusTripList.size()-1-index>0) {
				int restTimeIncrease=diff/(latestBusTripList.size()-1-index);
				System.out.println(restTimeIncrease);
				if(restTimeIncrease>10) {//单程增加超过10分钟
					if(index>1) {
						LatestBusTrip preLatestBusTripMatch=latestBusTripList.get(index-2);
						for(int i=0;i<2;i++) {
							Trip preLatestBusTrip=getPreLatestBusTrip(latestBusTrip);
							if(preLatestBusTrip==null)
								break;
							if(preLatestBusTrip.getDirection()==latestBusTrip.getDirection()) {
								if(!preLatestBusTrip.getNextObuTimeMin().after(preLatestBusTripMatch.getTripBeginTime())) {
									latestBusTripMatch=preLatestBusTripMatch;
									latestBusMain=preLatestBusTrip.getBus();
									latestBusTrip=preLatestBusTrip;
									break;
								}
							}else {
								if(!preLatestBusTrip.getTripBeginTime().after(preLatestBusTripMatch.getTripBeginTime())) {
									latestBusTripMatch=latestBusTripList.get(index-1);
									latestBusMain=preLatestBusTrip.getBus();
									latestBusTrip=preLatestBusTrip;
									break;
								}
							}
						}
					}
				}
				for(int i=0;i<latestBusTripList.size();) {
					LatestBusTrip trip=latestBusTripList.get(i);
					if(trip==latestBusTripMatch)
						break;
					latestBusTripList.remove(trip);
				}
				for(int i=0;i<latestBusTripList.size()-1;i++) {
					LatestBusTrip trip=latestBusTripList.get(i);
					Date tripBeginTime=trip.getTripBeginTime();
					tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, (int)(-diff*(1-(i+1)*1.0/latestBusTripList.size())));
					trip.setTripBeginTime(tripBeginTime);
					System.out.println(trip.getTripBeginTime()+"====>"+trip.getTripBeginTime()+"\t"+DateUtil.getMinuteInterval(trip.getStandardTripBeginTime(), trip.getTripBeginTime()));
				}
			}
		}
	}
	
	private Trip getPreLatestBusTrip(Trip latestBusTrip) {
		Trip preLatestBusTrip=null;
		Bus latestBus=latestBusTrip.getBus();
		List<Trip> tripList=routeSchedule.getBusQueueDoubleAndEveningClasses();
		tripList.addAll(tripList);
		int index=tripList.lastIndexOf(latestBusTrip);
		if(index!=-1){
			for(int i=index-1;i>=0;i--) {
				Trip trip=tripList.get(i);
				if(trip==latestBusTrip) {
					break;
				}
				if(!trip.isShortLine()&&trip.getShortLineAdjust()==null
					&&trip.getBus().getStartDirection()==latestBus.getStartDirection()) {
					preLatestBusTrip=trip;//找前面一台做尾车
					break;
				}
			}
		}
		return preLatestBusTrip;
	}
	
	private Trip latestBusCheck(Bus latestBusOld) {
		List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses();
		Trip latestBusTrip=null;
		for(Trip trip:busQueue) {
			if(trip.getBus()==latestBusOld) {
				latestBusTrip=trip;
				break;
			}
		}
		if(latestBusTrip==null){//可能在补电或短线排不了位
			latestBusTrip=routeSchedule.getTripInBusQueueShortBack(latestBusOld);
		}
		if(latestBusTrip==null)
			return null;
		if(latestBusTrip.getTripBeginTime()==null) {//晚半班复行车，等下次有发班时间在对比
			return latestBusTrip;
		}
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusOld.getStartDirection());
		LatestBusTrip latestBusTripStandard=null;
		Integer diff=null;
		for(LatestBusTrip latestBusTripStandardTemp:latestBusTripList) {
			if(latestBusTrip.getDirection()==latestBusTripStandardTemp.getDirection()&&
					!latestBusTrip.getTripBeginTime().after(latestBusTripStandardTemp.getStandardTripBeginTime())) {
				int diffTemp=DateUtil.getMinuteInterval(latestBusTrip.getTripBeginTime(), latestBusTripStandardTemp.getStandardTripBeginTime());
				if(diff==null||diffTemp<diff) {
					latestBusTripStandard=latestBusTripStandardTemp;
					diff=diffTemp;
				}
			}
		}
		if(latestBusTripStandard!=null) {
			/*diff=DateUtil.getMinuteInterval(latestBusTrip.getTripBeginTime(), latestBusTripStandard.getStandardTripBeginTime());
			if(latestBusTrip.getTripBeginTime().after(latestBusTripStandard.getStandardTripBeginTime())
				&&diff>latestBusTripStandard.getArriveEarlyEndTrip()/2) {
				genetateLatestBusTripList(direction);
				latestBusOld=null;
			} else {
				int index=latestBusTripList.indexOf(latestBusTripStandard);//方向不同不能比较，注释
				if(index>0) {
					LatestBusTrip latestBusTripStandardLast=latestBusTripList.get(index-1);
					if(DateUtil.getMinuteInterval(latestBusTripStandardLast.getTripBeginTime(), latestBusTrip.getTripBeginTime())<
						DateUtil.getMinuteInterval(latestBusTripStandard.getTripBeginTime(), latestBusTrip.getTripBeginTime())){//更靠近反向的时间
						genetateLatestBusTripList(direction);
						latestBusOld=null;
					}
				}
			}*/
			/*int index=latestBusTripList.indexOf(latestBusTripStandard);
			if(index>0) {
				LatestBusTrip latestBusTripReverse=latestBusTripList.get(index-1);
				if(DateUtil.isInTimeRange(latestBusTripReverse.getTripBeginTime(), latestBusTrip.getTripBeginTime(), latestBusTripStandard.getStandardTripBeginTime())) {//出现错位，重新选尾车
					latestBusOld=null;
				}
			}*/
			if(latestBusTrip.getTripBeginTime().after(latestBusTripStandard.getTripBeginTimeLatest())) {
				latestBusOld=null;
			}
		}
		if(latestBusOld!=null)
			return latestBusTrip;
		return null;
	}

	private Trip getLastFullTrip(int startDirection,int direction) {
		List<Trip> tripList=routeSchedule.getTripList(Direction.UP.getValue());
		Trip lastTrip=null;
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			if(!trip.isShortLine()&&trip.getBus().getStartDirection()==startDirection&&
				trip.getBus().isShiftType(ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP,ShiftType.EVENING_SHIFT)) {
				lastTrip=trip;
				break;
			}
		}
		return lastTrip;
	}
	
	private DirectionObuTimeRange getObuTimeRange(int startDirection) {
		Trip lastTripUp=getLastFullTrip(startDirection, Direction.UP.getValue());
		Trip lastTripDown=getLastFullTrip(startDirection, Direction.DOWN.getValue());
		List<DirectionObuTimeRange> obuTimeRangeList=obuTimeRangeMap.get(startDirection);
		if(lastTripUp!=null) {
			Integer diffUp=null;
			DirectionObuTimeRange obuTimeRangeUp=null;//上行发到哪个轮次
			for(DirectionObuTimeRange obuTimeRange:obuTimeRangeList) {
				if(obuTimeRange.getDirection()==lastTripUp.getDirection()) {
					int diffTemp=DateUtil.getMinuteInterval(lastTripUp.getTripBeginTime(), obuTimeRange.getEndTime());
					if(diffUp==null||diffTemp<diffUp) {
						diffUp=diffTemp;
						obuTimeRangeUp=obuTimeRange;
					}
				}
			}
			Integer diffDown=null;
			if(obuTimeRangeUp!=null&&lastTripDown!=null) {
				DirectionObuTimeRange obuTimeRangeDown=null;//下行发到哪个轮次
				if(lastTripDown.getTripBeginTime().before(lastTripUp.getTripBeginTime())) {//下行后发
					for(DirectionObuTimeRange obuTimeRange:obuTimeRangeList) {
						if(obuTimeRange==obuTimeRangeUp)
							break;
						if(obuTimeRange.getDirection()==lastTripDown.getDirection()) {
							int diffTemp=DateUtil.getMinuteInterval(lastTripDown.getTripBeginTime(), obuTimeRange.getEndTime());
							if(diffDown==null||diffTemp<diffDown) {
								diffDown=diffTemp;
								obuTimeRangeDown=obuTimeRange;
							}
						}
					}
				}else {
					boolean pass=false;
					for(DirectionObuTimeRange obuTimeRange:obuTimeRangeList) {
						if(obuTimeRange==obuTimeRangeUp)
							pass=true;
						if(pass) {
							if(obuTimeRange.getDirection()==lastTripDown.getDirection()) {
								int diffTemp=DateUtil.getMinuteInterval(lastTripDown.getTripBeginTime(), obuTimeRange.getEndTime());
								if(diffDown==null||diffTemp<diffDown) {
									diffDown=diffTemp;
									obuTimeRangeDown=obuTimeRange;
								}
							}
						}
					}
				}
				if(diffUp<=diffDown)
					return obuTimeRangeUp;
				else {
					return obuTimeRangeDown;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param direction 末班车开出方向
	 * @return
	 */
	private Trip getLatestBusTrip(int direction,int latestBusDirection) {
		int startDirection=1-latestBusDirection;
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(startDirection);
		Trip tripFull=routeSchedule.getTripFullLast(direction);
		Trip tripFullLast=tripFull.getLastTripFull();
		LatestBusTrip latestBusTrip=null;
		for(LatestBusTrip latestBusTripTemp:latestBusTripList) {
			if(latestBusTripTemp.getDirection()==direction&&
					DateUtil.isInTimeRange(latestBusTripTemp.getTripBeginTime(), tripFullLast.getTripBeginTime(), tripFull.getTripBeginTime())) {//跨过尾车发班时间
				latestBusTrip=latestBusTripTemp;
				break;
			}
		}
		if(latestBusTrip!=null) {
			List<Trip> tripList=routeSchedule.getTripList(direction);
			for(int i=tripList.size()-1;i>=0;i--) {
				Trip trip=tripList.get(i);
				if(!trip.isQuitMark()&&!trip.isShortLine()&&trip.getShortLineAdjust()==null&&//回来不放短线
					!trip.getTripBeginTime().after(latestBusTrip.getStandardTripBeginTime())&&
						trip.getBus().getStartDirection()==startDirection&&
						trip.getBus().isShiftType(ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP,ShiftType.EVENING_SHIFT)) {
					List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
					if(!busTripList.isEmpty()) {
						Trip lastTrip=busTripList.get(busTripList.size()-1);
						if(!lastTrip.isQuitMark()&&!lastTrip.isShortLine()&&lastTrip.getShortLineAdjust()==null) {
							return trip;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param direction  发班方向
	 * @param latestBusDirection 末班车方向
	 * @param latestBusOld
	 * @return
	 */
	private Trip getLatestBusTrip(int direction,int latestBusDirection,Bus latestBusOld) {
		if(latestBusOld!=null) {
			Trip latestBusTrip=latestBusCheck(latestBusOld);
			if(latestBusTrip!=null) {//可能暂时不够，重新选尾车可能把车位搞乱，不重选
				/*List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);//检查最后一轮最少车数，如果不够车看能不能重选尾车
				if(latestBusMain!=null&&latestBusSecondary!=null&&latestBusTripList.size()>1) {
					Date latestTime=scheduleParam.getLatestTime(direction);
					int maxInterval=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(latestTime, -1), direction);
					int minBusNum=(int)Math.ceil(DateUtil.getMinuteInterval(latestBusTripList.get(latestBusTripList.size()-2).getTripBeginTime(), latestTime)*1.0/maxInterval);
					List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses();
					busQueue.addAll(busQueue);
					Bus latestBusReverse=null;
					if(latestBusOld==latestBusMain) {
						latestBusReverse=latestBusSecondary;
					}else {
						latestBusReverse=latestBusMain;
					}
					Integer busNum=null;
					for(Trip trip:busQueue) {
						if(trip.getBus()==latestBusReverse) {
							busNum=0;
						}else if(busNum!=null) {
							busNum++;
						}
						if(busNum!=null&&trip.getBus()==latestBusOld) {
							break;
						}
					}
					if(busNum>=minBusNum) {
						return latestBusTrip;
					}else {
						System.out.println("aaa");
					}
				}else {
					return latestBusTrip;
				}*/
				return latestBusTrip;
			}
		}
		Trip latestBusTrip=getLatestBusTrip(direction,latestBusDirection);
		if(latestBusTrip==null&&latestBusOld!=null) {
			List<Trip> busTripList=routeSchedule.getTripList(latestBusOld);
			return busTripList.get(busTripList.size()-1);
		}
		if(latestBusTrip!=null&&latestBusTrip.getBus()!=latestBusOld) {
			if(latestBusOld!=null) {
				List<Trip> busTripList=routeSchedule.getTripList(latestBusOld);
				System.out.println("aaa");
			}
		}
		return latestBusTrip;
		
		
		/*int startDirection=1-direction;
		DirectionObuTimeRange obuTimeRange=getObuTimeRange(startDirection);
		Trip latestBusTrip=null;
		if(obuTimeRange!=null) {
			List<Trip> tripList=routeSchedule.getTripList(obuTimeRange.getDirection());
			for(int i=tripList.size()-1;i>=0;i--) {
				Trip trip=tripList.get(i);
				if(!trip.isShortLine()&&trip.getBus().getStartDirection()==startDirection&&
						trip.getBus().isShiftType(ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP,ShiftType.EVENING_SHIFT)) {
					if(latestBusTrip!=null) {
						if(DateUtil.isInTimeRange(trip.getTripBeginTime(), latestBusTrip.getTripBeginTime(), obuTimeRange.getEndTime())) {
							latestBusTrip=trip;//更接近最晚时间
						}else {
							break;
						}
					}else {
						if(!trip.getTripBeginTime().after(obuTimeRange.getEndTime()))
							latestBusTrip=trip;
					}
				}
			}
		}
		if(latestBusTrip!=null) {
			return latestBusTrip;
		}
		List<DirectionObuTimeRange> obuTimeRangeList=obuTimeRangeMap.get(1-direction);
		for(DirectionObuTimeRange obuTimeRangeTemp:obuTimeRangeList) {
			Trip lastTrip=routeSchedule.getTripLast(obuTimeRangeTemp.getDirection());
			if(lastTrip.getTripBeginTime().after(obuTimeRangeTemp.getEndTime())) {//当前已经过了边界
				obuTimeRange=obuTimeRangeTemp;
			}
		}
		if(obuTimeRange==null)
			return null;
		List<Trip> tripList=routeSchedule.getTripList(obuTimeRange.getDirection());
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
			if(trip.isShortLine()||trip.isQuitMark())//过滤短线和退出车辆
				continue;
			if(trip!=busTripList.get(busTripList.size()-1))//不是最近一个班次
				continue;
			if(!trip.getBus().isSingleClass()&&!trip.isQuitMark()&&(trip.getBus().getShiftType()==null||
					(trip.getBus().getShiftType().getShiftType()==ShiftType.EVENING_SHIFT.getValue()&&
						!DateUtil.getDateHM(trip.getBus().getShiftType().getEndTime()).before(scheduleParam.getLatestTime(direction))))) {//双班或晚班
				if(DateUtil.isInTimeRange(trip.getTripBeginTime(), obuTimeRange.getBeginTime(), obuTimeRange.getEndTime())||
						trip.getTripBeginTime().equals(obuTimeRange.getEndTime())) {//在时段区间
					if(trip.getBus().getStartDirection()==1-direction) {//非短线，出车方向一致
						latestBusTrip=trip;
						break;
					}
				}
			}
		}
		if(latestBusTrip==null) {//对面发车的没有匹配上，看这边到站的能不能匹配上
			int index=obuTimeRangeList.indexOf(obuTimeRange);
			if(index<obuTimeRangeList.size()-1) {
				List<Trip> busQueueTo=routeSchedule.getBusQueue(obuTimeRange.getDirection());
				obuTimeRange=obuTimeRangeList.get(index+1);
				if(!busQueueTo.isEmpty()&&!busQueueTo.get(busQueueTo.size()-1).getNextObuTimeMin().before(obuTimeRange.getEndTime())) {//最后一台车的到站时间过了截止时间
					for(int i=busQueueTo.size()-1;i>=0;i--) {
						Trip trip=busQueueTo.get(i);
						if(!trip.getBus().isSingleClass()&&!trip.isQuitMark()&&(trip.getBus().getShiftType()==null||
								(trip.getBus().getShiftType().getShiftType()==ShiftType.EVENING_SHIFT.getValue()&&
								!DateUtil.getDateHM(trip.getBus().getShiftType().getEndTime()).before(scheduleParam.getLatestTime(direction))))) {//双班或晚班
							if(DateUtil.isInTimeRange(trip.getNextObuTimeMin(), obuTimeRange.getBeginTime(), obuTimeRange.getEndTime())||
									trip.getNextObuTimeMin().equals(obuTimeRange.getEndTime())) {//在时段区间
								if(!trip.isShortLine()&&trip.getBus().getStartDirection()==1-direction) {//非短线，出车方向一致
									latestBusTrip=trip;
									break;
								}
							}
						}
					}
				}
			}
		}
		if(latestBusTrip!=null) {
			latestBusTrip=latestBusCheck(direction, latestBusTrip.getBus());
		}
		return latestBusTrip;*/
	}
	
	private void busReverseAdjust4SingleLastRound(int direction,List<Trip> busAvailable) {
		Date offDutyBeginTime=scheduleParam.getMiddleStopOffDutyBeginTime();
		Date offDutyAvgTime=DateUtil.getDateAddMinute(offDutyBeginTime, 15);
		for(int i=0;i<busAvailable.size();i++) {
			Trip tripArrival=busAvailable.get(i);
			if(tripArrival.getBus().isSingleClass()&&!tripArrival.isQuitMark()&&tripArrival.getShortLineAdjust()==null) {//单班车，没设置收工或调位
				/*if(checkBrokenPositionAfterAdjust(direction, tripArrival)) {
					continue;
				}*/
				Date obuTime=tripArrival.getNextObuTimeMin();
				if(scheduleParam.isEndDirectionTrip(tripArrival)) {//这边总站收车
					Date arrivalTimeNext=scheduleParam.getArrivalTimeNextRound(direction, obuTime);
					Integer diff=null;
					boolean quit=false;
					if(arrivalTimeNext!=null) {
						if(obuTime.before(offDutyBeginTime)&&!arrivalTimeNext.before(offDutyBeginTime)) {//最后一圈
							if(DateUtil.getMinuteInterval(obuTime, offDutyAvgTime)<DateUtil.getMinuteInterval(arrivalTimeNext, offDutyAvgTime)) {
								quit=true;
								diff=DateUtil.getMinuteInterval(obuTime, offDutyAvgTime);
							}else {
								diff=DateUtil.getMinuteInterval(arrivalTimeNext, offDutyAvgTime);
							}
						}else {
							continue;
						}
						RouteStaTurn shortAdjust=null;
						if(quit||diff>60) {//计划提前收车或收车时间晚于一个钟，安排短线任务
							for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
								Date arrivalTime=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
								int diffTemp=DateUtil.getMinuteInterval(arrivalTime, offDutyAvgTime);
								if(diffTemp<diff) {
									shortAdjust=routeStaTurn;
								}
							} 
						}
						if(shortAdjust!=null) {
							tripArrival.setShortLineAdjust(shortAdjust);
						}else if(quit) {
							tripArrival.setQuitMark(true);
						}
					}else {
						tripArrival.setQuitMark(true);
					}
				}else {//对面总站收车
					Date obuTimeNext=scheduleParam.getMinObuTimeNext(direction, obuTime);
					if(obuTimeNext!=null&&obuTimeNext.before(offDutyBeginTime)) {//开过去还没到收车开始时间
						Date obuTimeNextRound=scheduleParam.getArrivalTimeNextRound(1-direction, obuTimeNext);//到对面再跑个全程的时间
						if(obuTimeNextRound!=null&&obuTimeNextRound.after(offDutyAvgTime)) {//到对面再跑个全程会晚下班，看这边还是对面短线
							RouteStaTurn shortAdjust=null;
							int diff=DateUtil.getMinuteInterval(obuTimeNext, offDutyAvgTime);
							for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(1-direction)) {//对向短线任务
								Date arrivalTime=scheduleParam.getArrivalTime(obuTimeNext, routeStaTurn);//在对面走个短线到达时间
								int diffTemp=DateUtil.getMinuteInterval(arrivalTime, offDutyAvgTime);
								if(diffTemp<diff) {
									shortAdjust=routeStaTurn;
									diff=diffTemp;
								}
							}
							for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {//这边短线任务
								obuTimeNext=scheduleParam.getMinObuTimeNextLap(tripArrival, routeStaTurn);//先走个短线回来的发班时间
								Date arrivalTime=scheduleParam.getArrivalTime(obuTimeNext, direction);
								if(arrivalTime!=null) {
									int diffTemp=DateUtil.getMinuteInterval(arrivalTime, offDutyAvgTime);
									if(diffTemp<diff) {
										shortAdjust=routeStaTurn;
										diff=diffTemp;
									}
								}
							}
							if(shortAdjust!=null) {
								if(shortAdjust.getDirection()==direction) {//这边先走短线
									tripArrival.setShortLineAdjust(shortAdjust);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected void busReverseAdjust4Short(ScheduleHalfHour scheduleHalfHour) {
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		int direction=scheduleHalfHour.getDirection();	
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-scheduleHalfHour.getDirection());
		List<Trip> busAvailable=new ArrayList<Trip>();
		for(Trip tripArrival:bus_queue_to) {
			if(!tripArrival.isQuitMark())
				busAvailable.add(tripArrival);
		}
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		Date runTimeEndNext=null;
		if(scheduleHalfHourNext!=null)
			runTimeEndNext=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTimeEnd()).getTime();
		if(!runTime.before(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopRecoveryBeginTime(), -30))) {//单班车先调位
			for(int i=0;i<busAvailable.size();i++) {
				Trip tripArrival=busAvailable.get(i);
				if(DateUtil.isInTimeRange(tripArrival.getNextObuTimeMin(), DateUtil.getDateHM("1600"), scheduleParam.getLatePeakEndTime()))
					continue;
				Bus bus=tripArrival.getBus();
				if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==3)
					System.out.println("aaa");
				/*if(runTimeEndNext!=null&&tripArrival.getNextObuTimeMin().after(runTimeEndNext))
					break;*/
				if(tripArrival.getBus().isSingleClass()) {
					if(tripArrival.getTripBeginTime()==null) {//复行
						if(checkBrokenPositionAfterAdjust(direction, tripArrival)) {
							continue;
						}
						RouteStaTurn routeStaTurn=getShortLineAdjust4SingleClassesBusRecovery(scheduleHalfHour.getDirection(), tripArrival);
						if(routeStaTurn!=null) {
							if(i==0) {
								Trip tripLast=routeSchedule.getTripLast(scheduleHalfHour.getDirection());
								if(i<busAvailable.size()-1) {
									if(!tripLast.isShortLine()) {//上一班不是短线
										tripArrival.setShortLineAdjust(routeStaTurn);
									}
								}
								
							}else {
								Trip tripLast=busAvailable.get(i-1);
								if(tripLast.getShortLineAdjust()==null) {//上一班没有短线调位
									tripArrival.setShortLineAdjust(routeStaTurn);
								}
							}
						}
					}else if(scheduleParam.isEndDirectionTrip(tripArrival)){
						Date obuTimeNext=scheduleParam.getMinObuTimeNextRound(tripArrival);
						if(obuTimeNext==null||obuTimeNext.after(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopOffDutyBeginTime(), 60))) {//在回来过了下班时间30分钟
							if(tripArrival.getBus().getStartDirection()==1&&tripArrival.getBus().getStartOrderNumber()==5)
								System.out.println("aaa");
							RouteStaTurn routeStaTurn=getShortLineAdjust4SingleClassesBusOffDuty(scheduleHalfHour.getDirection(), tripArrival);
							if(routeStaTurn==null) {
								tripArrival.setQuitMark(true);//没有合适的短线，提前下班
							}else {
								tripArrival.setShortLineAdjust(routeStaTurn);
							}
						}
					}
				}
			}
			busReverseAdjust4SingleLastRound(direction, busAvailable);
		}
		for(int i=0;i<busAvailable.size()-1;i++) {
			Trip tripArrival=busAvailable.get(i);
			if(DateUtil.getDateHM("1624").equals(tripArrival.getTripEndTime()))
				System.out.println("aaaa");
			Bus bus=tripArrival.getBus();
			if(bus.isSingleClass()||(bus.getShiftType()!=null&&(!bus.getShiftType().getShiftType().equals(ShiftType.EVENING_SHIFT.getValue())
					||DateUtil.getDateHM(bus.getShiftType().getEndTime()).before(scheduleParam.getLatestTime(1-bus.getStartDirection())))))//过滤单班车、早班、晚班、中班等，只保留双班车和晚班车(收工时间在末班车前)
				continue;
			if(runTimeEndNext!=null&&tripArrival.getNextObuTimeMin().after(runTimeEndNext))
				break;
			RouteStaTurn routeStaTurn=getShortLineAdjust4DoubleClassesBus(scheduleHalfHour.getDirection(), tripArrival);
			if(routeStaTurn==null) {
				continue;
			}
			//tripArrival.setShortLineAdjust(null);
			if(checkBrokenPositionAfterAdjust(direction, tripArrival)) {
				continue;
			}
			if(routeStaTurn!=null) {
				if(tripArrival.getNextObuTimeMin().after(scheduleParam.getOffDutyBeginTime())) {
					if(scheduleParam.isEndDirectionTrip(tripArrival)) {
						tripArrival.setQuitMark(true);
						continue;
					}
				}
				if(i==0) {
					Trip tripLast=routeSchedule.getTripLast(scheduleHalfHour.getDirection());
					if(i<busAvailable.size()-1) {
						Trip tripNext=busAvailable.get(i+1);
						if(!tripLast.isShortLine()&&tripNext.getShortLineAdjust()==null) {//上一班不是短线.下一班不调位
							if(!(tripLast.getBus().isSingleClass()&&tripNext.getBus().isSingleClass()))//前后不是单班车，防止发短线后两台单班车走一起
								tripArrival.setShortLineAdjust(routeStaTurn);
						}
					}
				}else {
					Trip tripLast=busAvailable.get(i-1);
					if(i<busAvailable.size()-1) {
						Trip tripNext=busAvailable.get(i+1);
						if(tripLast.getShortLineAdjust()==null&&tripNext.getShortLineAdjust()==null) {//上一班没有短线调位
							if(!(tripLast.getBus().isSingleClass()&&tripNext.getBus().isSingleClass())) {//前后不是单班车，防止发短线后两台单班车走一起
								if(DateUtil.isInTimeRange(tripArrival.getNextObuTimeMin(), scheduleParam.getLatePeakBeginTime(), scheduleParam.getLatePeakEndTime())) {//高峰时候，看前后车局
									int busInterval=DateUtil.getMinuteInterval(tripLast.getNextObuTimeMin(), tripNext.getNextObuTimeMin());
									int maxInterval=scheduleParam.getMaxInterval(tripArrival.getNextObuTimeMin(), direction);
									if(busInterval<maxInterval*1.5) {//晚高峰调位，前后车距不能太大，避免停站时间过长
										tripArrival.setShortLineAdjust(routeStaTurn);
									}
								}else {
									tripArrival.setShortLineAdjust(routeStaTurn);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private RouteStaTurn getShortLineAdjust4SingleClassesBusRecovery(int direction,Trip tripArrival) {
		if(tripArrival.getBus().getStartDirection()==1&&tripArrival.getBus().getStartOrderNumber()==5)
			System.out.println("aaa");
		Date offDutyBeginTime=scheduleParam.getMiddleStopOffDutyBeginTime();
		Date offDutyEndTime=DateUtil.getDateAddMinute(offDutyBeginTime, 30);//单班车在收车开始后30分钟收完，避免单班车太晚下班
		//offDutyBeginTime=DateUtil.getDateAddMinute(offDutyBeginTime, -20);
		Date obuTime=tripArrival.getNextObuTimeMin();
		while(obuTime!=null&&obuTime.before(offDutyBeginTime)) {
			obuTime=scheduleParam.getObuTimeNextRound(direction, obuTime);
		}
		if(obuTime!=null&&DateUtil.isInTimeRange(obuTime, offDutyBeginTime, offDutyEndTime)) {//收车时间在合理范围
			return null;
		}
		System.out.println("aaa");
		for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
			obuTime=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
			while(obuTime!=null&&obuTime.before(offDutyBeginTime)) {
				obuTime=scheduleParam.getObuTimeNextRound(direction, obuTime);
			}
			if(obuTime==null)
				continue;
			if(DateUtil.isInTimeRange(obuTime, offDutyBeginTime, offDutyEndTime)) {//收车时间在合理范围
				return routeStaTurn;
			}
		}
		//收车不在开始时间后30分钟内，看哪个短线误差最小
		Integer diff=null;
		RouteStaTurn shortAdjust=null;
		Date offDutyAvg=DateUtil.getDateAddMinute(offDutyBeginTime, 15);
		for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
			obuTime=scheduleParam.getMinObuTimeNextLap(tripArrival, routeStaTurn);
			while(obuTime!=null&&obuTime.before(offDutyBeginTime)) {
				Date obuTimeNext=scheduleParam.getObuTimeNextRound(direction, obuTime);
				Integer diffTemp=null;
				if(obuTimeNext!=null) {
					if(!obuTimeNext.before(offDutyBeginTime)) {//下次发班在开始收车后
						diffTemp=Math.min(DateUtil.getMinuteInterval(obuTime, offDutyAvg), DateUtil.getMinuteInterval(obuTimeNext, offDutyAvg));
					}else {
						obuTime=obuTimeNext;
					}
				}else {
					diffTemp=DateUtil.getMinuteInterval(obuTime, offDutyAvg);
				}
				if(diffTemp!=null) {
					if(diff==null||diffTemp<diff) {
						shortAdjust=routeStaTurn;
						diff=diffTemp;
					}
					break;
				}
			}
			
		}
		if(shortAdjust!=null) {
			obuTime=tripArrival.getNextObuTimeMin();
			while(obuTime!=null&&obuTime.before(offDutyBeginTime)) {
				Date obuTimeNext=scheduleParam.getObuTimeNextRound(direction, obuTime);
				Integer diffTemp=null;
				if(obuTimeNext!=null) {
					if(!obuTimeNext.before(offDutyBeginTime)) {
						diffTemp=Math.min(DateUtil.getMinuteInterval(obuTime, offDutyAvg), DateUtil.getMinuteInterval(obuTimeNext, offDutyAvg));
					}else {
						obuTime=obuTimeNext;
					}
				}else {
					diffTemp=DateUtil.getMinuteInterval(obuTime, offDutyAvg);
				}
				if(diffTemp!=null) {//全程的时间比短线误差小
					if(diffTemp<diff) {
						shortAdjust=null;
					}
					break;
				}
			}
		}
		return shortAdjust;
	}
	
	private RouteStaTurn getShortLineAdjust4SingleClassesBusOffDuty(int direction,Trip tripArrival) {
		if(tripArrival.getBus().getStartDirection()==1&&tripArrival.getBus().getStartOrderNumber()==5)
			System.out.println("aaa");
		Date offDutyBeginTime=scheduleParam.getMiddleStopOffDutyBeginTime();
		Date offDutyEndTime=DateUtil.getDateAddMinute(offDutyBeginTime, 60);//单班车在收车开始后60分钟收完，避免单班车太晚下班
		offDutyBeginTime=DateUtil.getDateAddMinute(offDutyBeginTime, -10);
		for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
			Date obuTime=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
			if(DateUtil.isInTimeRange(obuTime, offDutyBeginTime, offDutyEndTime)) {//收车时间在合理范围
				return routeStaTurn;
			}
		}
		return null;
	}
	
	private RouteStaTurn getShortLineAdjust4DoubleClassesBus(int direction,Trip tripArrival) {
		if(scheduleParam.getRouteStaTurnList(direction).isEmpty()||tripArrival.getBus().isSingleClass())
			return null;
		Bus bus=tripArrival.getBus();
		if(!isNeed2Adjust(tripArrival,direction)) {
			return null;
		}
		if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==1){
			System.out.println("aaa");
		}
		for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
			Date arrivalTime=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
			if(!isNeed2Adjust(tripArrival.getBus(),arrivalTime,direction)) {
				return routeStaTurn;
			}
		}
		Date arrivalTime=null;
		RouteStaTurn ShortLineAdjust=null;
		for(RouteStaTurn routeStaTurn:scheduleParam.getRouteStaTurnList(direction)) {
			Date arrivalTimeTemp=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
			if(direction==bus.getStartDirection()&&!arrivalTimeTemp.before(scheduleParam.getOffDutyBeginTime())) {
				if(arrivalTime==null||arrivalTimeTemp.after(arrivalTime)) {
					arrivalTime=arrivalTimeTemp;
					ShortLineAdjust=routeStaTurn;
				}
			}
		}
		return ShortLineAdjust;
	}

	private boolean isNeed2Adjust(Bus bus, Date obuTime, int direction) {
		List<DirectionObuTimeRange> obuTimeRangeList=obuTimeRangeMap.get(bus.getStartDirection());
		for(DirectionObuTimeRange obuTimeRange:obuTimeRangeList) {
			if(direction==obuTimeRange.getDirection()&&obuTimeRange.getBeginTime()!=null&&obuTimeRange.getEndTime()!=null
				&&DateUtil.isInTimeRange(obuTime, obuTimeRange.getBeginTime(), obuTimeRange.getEndTime())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkBrokenPositionAfterAdjust(int direction,Trip tripAdjust) {
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(tripAdjust.getDirection());
		List<Trip> bus_queue_to_full=new ArrayList<Trip>();
		for(Trip tripArrival:bus_queue_to) {
			if(!tripArrival.isQuitMark()&&tripArrival.getShortLineAdjust()==null) {
				if(tripArrival!=tripAdjust) {
					bus_queue_to_full.add(tripArrival);
				}
			}
		}
		Trip tripFullLast=routeSchedule.getTripFullLast(direction);
		Date obuTimeLast=tripFullLast.getTripBeginTime();
		Date latestTime=scheduleParam.getLatestTime(direction);
		for(Trip tripArrival:bus_queue_to_full) {
			if(obuTimeLast.after(latestTime))
				break;
			int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, direction);
			Date obuTimeMax=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
			if(tripArrival.getTripEndTime()!=null) {
				if(tripArrival.isEatAfterTrip()||tripArrival.isElecSupplyAfterTrip()) {
					if(!tripArrival.getNextObuTimeMin().before(obuTimeMax))
						return true;
				}else {
					if(tripArrival.getNextObuTimeMin().equals(scheduleParam.getLatestTime(direction))) {//过来做尾车
						if(!tripArrival.getNextObuTimeMin().before(obuTimeMax))
							return true;
					}else {
						if(!tripArrival.getTripEndTime().before(obuTimeMax))
							return true;
					}
				}
				
			}
			obuTimeLast=obuTimeMax;
		}
		return false;
	}
	
	public Map<LatestBusPair, Integer> initLatestBus() {
		List<Trip> tripList=routeSchedule.getBusQueueDoubleAndEveningClasses();
		List<Bus> busQueue=new ArrayList<Bus>();
		for(Trip trip:tripList) {
			busQueue.add(trip.getBus());
		}
		Map<Integer, List<Bus>> latestBusMap=new HashMap<Integer, List<Bus>>();
		for(int i=0;i<busQueue.size();i++) {
			Bus bus=busQueue.get(i);
			Bus busNext=null;
			if(i==tripList.size()-1) {
				busNext=busQueue.get(0);
			}else {
				busNext=busQueue.get(i+1);
			}
			if(busNext.getStartDirection()!=bus.getStartDirection()) {
				List<Bus> latestBusList=latestBusMap.get(bus.getStartDirection());
				if(latestBusList==null) {
					latestBusList=new ArrayList<Bus>();
					latestBusMap.put(bus.getStartDirection(), latestBusList);
				}
				latestBusList.add(bus);
			}
		}
		Map<LatestBusPair, Integer> latestBusPairMap=new HashMap<LatestBusPair, Integer>();
		
		if(latestBusMap.isEmpty()) {//单边出车
			if(mainDirection!=Direction.NODIRECTION.getValue()) {
				Date latestTime=scheduleParam.getLatestTime(mainDirection);
				Date latestTimeReverse=scheduleParam.getLatestTime(1-mainDirection);
				Date arrivalTime=scheduleParam.getArrivalTime(latestTime, mainDirection);
				if(arrivalTime.before(latestTimeReverse)) {
					List<Trip> upTripList=routeSchedule.getTripList(Direction.UP.getValue());
					List<Trip> downTripList=routeSchedule.getTripList(Direction.DOWN.getValue());
					Trip upFirstTrip=upTripList.get(0);
					Trip downFirstTrip=downTripList.get(0);
					int startDirection=Direction.UP.getValue();
					if(upFirstTrip.getTripBeginTime().after(downFirstTrip.getTripBeginTime())) {//上行出车
						startDirection=Direction.DOWN.getValue();
					}
					int tripNumber=0;
					Bus latestBus=null;
					for(Bus bus:routeSchedule.getTripMap().keySet()) {
						List<Trip> busTripList=routeSchedule.getTripList(bus);
						Trip tripLast=busTripList.get(busTripList.size()-1);
						if(!tripLast.isShortLine()&&tripLast.getShortLineAdjust()==null) {
							int tripNumberTemp=0;
							for(Trip trip:busTripList) {
								if(!trip.isShortLine()&&trip.getDirection()==startDirection) {
									tripNumberTemp++;
								}
							}
							if(tripNumberTemp>tripNumber) {
								latestBus=bus;
							}
						}
					}
					LatestBusPair latestBusPair=new LatestBusPair(latestBus, latestBus);
					latestBusPairMap.put(latestBusPair, 0);
				}
			}
		}else {
			List<Bus> upBusList=latestBusMap.get(Direction.UP.getValue());
			List<Bus> downBusList=latestBusMap.get(Direction.DOWN.getValue());
			for(Trip trip:tripList) {//加多一次车，方便统计两个尾车之前的车
				busQueue.add(trip.getBus());
			}
			for(Bus upBus:upBusList) {
				for(Bus downBus:downBusList) {
					Integer busAdjustNum=null;
					for(Bus bus:busQueue) {
						if(bus==upBus) {
							busAdjustNum=0;
						}else {
							if(busAdjustNum!=null) {
								if(bus.getStartDirection()==Direction.UP.getValue()) {
									busAdjustNum++;
								}
								if(bus==downBus) {
									break;
								}
							}
						}
					}
					int busAdjustNumTotal=busAdjustNum;
					busAdjustNum=null;
					for(Bus bus:busQueue) {
						if(bus==downBus) {
							busAdjustNum=0;
						}else {
							if(busAdjustNum!=null) {
								if(bus.getStartDirection()==Direction.DOWN.getValue()) {
									busAdjustNum++;
								}
								if(bus==upBus) {
									break;
								}
							}
						}
					}
					busAdjustNumTotal+=busAdjustNum;
					LatestBusPair latestBusPair=new LatestBusPair(upBus, downBus);
					latestBusPairMap.put(latestBusPair, busAdjustNumTotal);
				}
			}
		}
		System.out.println("aaa");
		return latestBusPairMap;
	}
	
	@Override
	protected void checkLatestBus(Trip latestBusTrip) {
		Bus latestBusNew=null;
		Trip preLatestBusTrip=getPreLatestBusTrip(latestBusTrip);
		if(preLatestBusTrip!=null) {
			Bus latestBus=latestBusTrip.getBus();
			LatestBusTrip latestBusTripNext=null;		
			List<LatestBusTrip> latestBusMainSurplusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
			if(latestBusMainSurplusTripList.isEmpty()) {
				return;
			}
			if(latestBus==latestBusMain) {
				latestBusTripNext=latestBusMainSurplusTripList.get(0);
			}else {
				Trip latestBusTripMainLast=routeSchedule.getTripRunLast(latestBusMain);
				if(latestBusTripMainLast==null) {
					return;
				}
				Trip latestBusTripSecondaryLast=routeSchedule.getTripRunLast(latestBusSecondary);
				List<LatestBusTrip> latestBusSecondarySurplusTripList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
				if(latestBusTripSecondaryLast.getDirection()!=latestBusTripMainLast.getDirection()) {//方向不一致，剩余轮次一样
					latestBusTripNext=latestBusSecondarySurplusTripList.get(latestBusSecondarySurplusTripList.size()-latestBusMainSurplusTripList.size());
				}else {
					if(latestBusTripSecondaryLast.getNextObuTimeMin().before(latestBusTripMainLast.getNextObuTimeMin())) {//同方向，在前面要跑多一单
						latestBusTripNext=latestBusSecondarySurplusTripList.get(latestBusSecondarySurplusTripList.size()-latestBusMainSurplusTripList.size()-1);
					}else {
						if(latestBusSecondarySurplusTripList.size()-latestBusMainSurplusTripList.size()+1==latestBusSecondarySurplusTripList.size())
							return;
						latestBusTripNext=latestBusSecondarySurplusTripList.get(latestBusSecondarySurplusTripList.size()-latestBusMainSurplusTripList.size()+1);
					}
				}
			}
			if(latestBusTrip.getNextObuTimeMin().after(latestBusTripNext.getTripBeginTimeLatest())){//尾车赶不上
				latestBusNew=preLatestBusTrip.getBus();
			}
			if(latestBusNew!=null) {
				RouteStaTurn routeStaTurn=null;
				for(RouteStaTurn routeStaTurnTemp:scheduleParam.getRouteStaTurnList(1-latestBusTrip.getDirection())) {
					Date arrivalTime=scheduleParam.getArrivalTime(latestBusTrip.getNextObuTimeMin(), routeStaTurnTemp);
					if(!isNeed2Adjust(latestBus,arrivalTime,1-latestBusTrip.getDirection())) {
						routeStaTurn=routeStaTurnTemp;
						break;
					}
				}
				if(routeStaTurn!=null) {
					if(latestBus==latestBusMain) {
						latestBusMain=latestBusNew;
					}else {
						System.out.println("换尾车"+latestBusSecondary.getBusName()+"\t"+latestBusNew.getBusName()+"\t"+latestBusTrip.getTripBeginTime());
						latestBusSecondary=latestBusNew;
					}
					latestBusTrip.setShortLineAdjust(routeStaTurn);
					tripArrivalAdjustList.add(latestBusTrip);
				}
			}
		}
	}
	
	@Override
	protected Integer getDispatchInterval(int direction) {
		if(latestBusMain==null)
			return null;
		Trip tripLastFull=routeSchedule.getTripFullLast(direction);
		LatestBusTrip latestBusTripStandard=null;
		//List<Trip> latestBusSecondaryTripList=routeSchedule.getTripList(latestBusSecondary);
		Trip latestBusSecondaryTrip=routeSchedule.getBusTripFromQueue(latestBusSecondary);
		List<LatestBusTrip> latestBusMainSurplusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
		Integer maxInterval4LatestBusLatest=null;
		Bus latestBus=latestBusMain;
		List<Trip> busQueue=getBusQueue(direction);
		if(latestBusMainSurplusTripList.size()==2) {
			if(direction!=latestBusMain.getStartDirection()){
				if(latestBusSecondaryTrip.getDirection()!=direction) {
					/*List<LatestBusTrip> latestBusSecondaryList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
					latestBusTripStandard=latestBusSecondaryList.get(latestBusSecondaryList.size()-2);
					if(latestBusTripStandard.getTripBeginTime().after(latestBusSecondaryTrip.getNextObuTimeMin())) {
						int diff=DateUtil.getMinuteInterval(latestBusTripStandard.getStandardTripBeginTime(), latestBusSecondaryTrip.getNextObuTimeMin());
						Date tripBeginTime=DateUtil.getDateAddMinute(latestBusTripStandard.getStandardTripBeginTime(), -diff/2);
						latestBusTripStandard.setTripBeginTime(tripBeginTime);
					}*/
					List<Trip> arrivalTripList4Full=getB4LatestBusSecondaryTripList(busQueue);
					if(arrivalTripList4Full!=null) {
						List<LatestBusTrip> latestBusSecondaryList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
						LatestBusTrip latestBusTripSecondary=latestBusSecondaryList.get(latestBusSecondaryList.size()-2);
						maxInterval4LatestBusLatest=DateUtil.getMinuteInterval(tripLastFull.getTripBeginTime(), latestBusTripSecondary.getTripBeginTimeLatest())/arrivalTripList4Full.size();
					}
				}
			}else {
				List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
				if(DateUtil.getMinuteInterval(latestBusTripList.get(latestBusTripList.size()-2).getTripBeginTime(), latestBusSecondaryTrip.getTripBeginTime())<
					DateUtil.getMinuteInterval(latestBusTripList.get(latestBusTripList.size()-4).getTripBeginTime(), latestBusSecondaryTrip.getTripBeginTime())
					||latestBusSecondaryTrip.getDirection()==direction) {//进入最后一圈
					List<LatestBusTrip> latestBusSecondaryList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
					latestBusTripStandard=latestBusSecondaryList.get(latestBusSecondaryList.size()-1);
					
					List<Trip> arrivalTripList4Full=getB4LatestBusMainTripList(busQueue);
					List<LatestBusTrip> latestBusMainList=latestBusTripMap.get(latestBusMain.getStartDirection());
					LatestBusTrip latestBusTripMain=latestBusMainList.get(latestBusMainList.size()-2);
					maxInterval4LatestBusLatest=DateUtil.getMinuteInterval(tripLastFull.getTripBeginTime(), latestBusTripMain.getTripBeginTimeLatest())/arrivalTripList4Full.size();
				}
			}
		}else if(latestBusMainSurplusTripList.size()<2&&direction==latestBusMain.getStartDirection()) {
			List<LatestBusTrip> latestBusSecondaryList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
			latestBusTripStandard=latestBusSecondaryList.get(latestBusSecondaryList.size()-1);
			latestBus=latestBusSecondary;
		}
		List<Trip> arrivalTripList4Full=null;
		if(latestBusTripStandard!=null) {
			arrivalTripList4Full=getB4LatestBusSecondaryTripList(busQueue);
		}else {
			for(LatestBusTrip latestBusTrip:latestBusMainSurplusTripList) {
				if(latestBusTrip.getDirection()==direction) {
					latestBusTripStandard=latestBusTrip;
					break;
				}
			}
			arrivalTripList4Full=getB4LatestBusMainTripList(busQueue);
		}
		if(arrivalTripList4Full==null) {
			int interval=DateUtil.getMinuteInterval(tripLastFull.getLastTripFull().getTripBeginTime(), tripLastFull.getTripBeginTime());
			return interval;
		}else if(isLastRound(direction)){
			Date latestTime=scheduleParam.getLatestTime(direction);
			List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
			Trip tripArrivalLast=busQueueTo.get(busQueueTo.size()-1);
			if(tripArrivalLast.getTripEndTime().after(latestTime)) {
				for(Trip tripArrival:busQueueTo) {
					if(tripArrival.getBus().getStartDirection()==latestBus.getStartDirection()&&
						!arrivalTripList4Full.contains(tripArrival)) {
						arrivalTripList4Full.add(tripArrival);//把尾车后的同向车先加上，后面再过滤赶不上的车，减少反插
					}
				}
			}
			for(int i=0;i<arrivalTripList4Full.size();) {
				Trip tripArrival=arrivalTripList4Full.get(i);
				if(tripArrival.getDirection()!=direction) {
					if(tripArrival.getTripEndTime()!=null&&
						!tripArrival.getTripEndTime().before(latestTime)) {
						arrivalTripList4Full.remove(i);//过滤赶不上的车
						tripArrivalAdjustList.add(tripArrival);//防止换位到尾车前
						continue;
					}
				}
				if(tripArrival.getBus().getStartDirection()!=latestBus.getStartDirection()) {
					if(tripArrival.getDirection()!=direction) {
						tripArrival.setQuitMark(true);
						arrivalTripList4Full.remove(i);//过滤这边收车的车
						continue;
					}
				}
				i++;
			}
			
			Trip latestBusTrip=arrivalTripList4Full.get(arrivalTripList4Full.size()-1);
			if(!isLatestBus(latestBusTrip.getBus())) {
				if(latestBusTrip.getBus().getStartDirection()==latestBusMain.getStartDirection()) {
					latestBusMain=latestBusTrip.getBus();
				}else {
					latestBusSecondary=latestBusTrip.getBus();
				}
			}
			Date obuTimeLast=tripLastFull.getTripBeginTime();
			boolean isOk=true;
			for(int i=0;i<arrivalTripList4Full.size();i++) {
				int interval=DateUtil.getMinuteInterval(latestBusTripStandard.getTripBeginTime(), obuTimeLast)/(arrivalTripList4Full.size()-i);
				Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
				Trip tripArrival=arrivalTripList4Full.get(i);
				if(tripArrival.getTripEndTime()!=null&&
						!obuTime.after(tripArrival.getTripEndTime())) {
					isOk=false;
					break;
				}
				obuTimeLast=obuTime;
			}
			obuTimeLast=tripLastFull.getTripBeginTime();
			int interval=DateUtil.getMinuteInterval(latestBusTripStandard.getTripBeginTime(), obuTimeLast)/(arrivalTripList4Full.size());
			if(isOk) {
				return interval;
			}else {
				while(interval<100) {
					isOk=true;
					interval++;
					for(int i=0;i<arrivalTripList4Full.size();i++) {
						Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval*(i+1));
						Trip tripArrival=arrivalTripList4Full.get(i);
						if(tripArrival.getTripEndTime()!=null&&
								!obuTime.after(tripArrival.getTripEndTime())) {
							isOk=false;
							break;
						}
					}
					if(isOk) {
						break;
					}
				}
			}
			return interval;
		}else {
			if(arrivalTripList4Full.size()==1)
				System.out.println("aaa");
			if(latestBusTripStandard.getTripBeginTime().after(latestBusTripStandard.getStandardTripBeginTime())) {
				return 0;
			}else {
				if(latestBusTripStandard.getTripBeginTime().before(tripLastFull.getTripBeginTime()))
					return 0;
				int interval=DateUtil.getMinuteInterval(latestBusTripStandard.getTripBeginTime(), tripLastFull.getTripBeginTime())/arrivalTripList4Full.size();
				if(maxInterval4LatestBusLatest!=null) {//尾车要赶着过去做末班车，不能太晚
					if(interval>maxInterval4LatestBusLatest) {
						interval=maxInterval4LatestBusLatest;
					}
				}
				return interval;
			}
		}
	}
	

	@Override
	protected Date getObuTime(Trip tripArrival, int direction) {
		Integer interval=getDispatchInterval(direction);
		if(interval!=null) {
			Trip tripLastFull=routeSchedule.getTripFullLast(direction);
			Date obuTime=DateUtil.getDateAddMinute(tripLastFull.getTripBeginTime(), interval);
			return obuTime;
		}
		return null;
	}


	@Override
	protected boolean isLatestBusLastRound() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setLatestBus() {
		if(scheduleParam.getEndDirection()!=null&&
				scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()&&(
				latestBusMain==null||latestBusSecondary==null)) {
			Map<LatestBusPair, Integer> latestBusPairMap=initLatestBus();
			LatestBusPair latestBusPair=null;
			Integer busAdjustNum=null;
			for(Entry<LatestBusPair, Integer> entry:latestBusPairMap.entrySet()) {
				LatestBusPair latestBusPairTemp=entry.getKey();
				Integer busAdjustNumTemp=entry.getValue();
				if(latestBusPairTemp.getLatestBusUp()==null||latestBusPairTemp.getLatestBusDown()==null)
					System.out.println("aaa");
				System.out.println(latestBusPairTemp.getLatestBusUp().getStartOrderNumber()+"\t"+latestBusPairTemp.getLatestBusDown().getStartOrderNumber()+"\t"+busAdjustNumTemp);
				if(busAdjustNum==null||busAdjustNumTemp<busAdjustNum) {
					busAdjustNum=busAdjustNumTemp;
					latestBusPair=latestBusPairTemp;
				}else if(busAdjustNumTemp==busAdjustNum) {
					if(latestBusPairTemp.getLatestBusUp().getStartOrderNumber()+
						latestBusPairTemp.getLatestBusDown().getStartOrderNumber()>
						latestBusPair.getLatestBusUp().getStartOrderNumber()+
						latestBusPair.getLatestBusDown().getStartOrderNumber()) {//相同调位车数，取靠后车做尾车
						latestBusPair=latestBusPairTemp;
					}
				}
			}
			if(latestBusPair!=null) {
				if(mainDirection==Direction.DOWN.getValue()) {//下行先收车
					latestBusMain=latestBusPair.getLatestBusUp();
					latestBusSecondary=latestBusPair.getLatestBusDown();
				}else {
					latestBusMain=latestBusPair.getLatestBusDown();
					latestBusSecondary=latestBusPair.getLatestBusUp();
				}
				List<Trip> busTripList=routeSchedule.getTripList(latestBusMain);
				Trip latestBusMainTrip=null;
				if(busTripList!=null) {
					latestBusMainTrip=busTripList.get(busTripList.size()-1);
					if(latestBusMainTrip.getDirection()==Direction.NODIRECTION.getValue()) {
						latestBusMainTrip=busTripList.get(busTripList.size()-2);
					}
					if(latestBusMainTrip.isShortLine()) {
						latestBusMainTrip=latestBusMainTrip.getTurnTrip();
					}
				}else {//尾车是夜班车
					latestBusMainTrip=routeSchedule.getBusTripFromQueue(latestBusMain);
				}
				updateLatestBusTripList(latestBusMainTrip);
			}
		}
	}

	@Override
	protected void removeLatestBusTrip(Bus bus) {
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(bus.getStartDirection());
		latestBusTripList.remove(0);
	}
	
	@Override
	protected List<Trip> getTripArrivalAdjustList() {
		return tripArrivalAdjustList;
	}

}
