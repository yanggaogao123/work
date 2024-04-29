package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.RouteStationPassenger;
import com.gci.schedule.driverless.dynamic.bean.ScheduleShiftPreset;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.util.TripBeginTimeComparator;

import java.util.*;

public class ScheduleGenerateB4Peek4Full {

	private ScheduleParam scheduleParam;
	
	private RouteSchedule routeSchedule;
	
	private Map<Integer,Integer> busNumberMap=new HashMap<Integer, Integer>();
	
	private Map<Integer,Integer> busNumberSingleMap=new HashMap<Integer, Integer>();//单班车数
	
	public ScheduleGenerateB4Peek4Full(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		this.scheduleParam=scheduleParam;
		this.routeSchedule=routeSchedule;
	}
	
	private void checkBusNumber() {
		Map<Bus, List<Trip>> tripMap=routeSchedule.getTripMap();
		int busNumberUp=0;
		int busNumberSingleUp=0;
		int busNumberDown=0;
		int busNumberSingleDown=0;
		for(Bus bus:tripMap.keySet()) {
			if(bus.getStartDirection()==Direction.UP.getValue()) {
				busNumberUp++;
				if(bus.isSingleClass()) {
					busNumberSingleUp++;
				}
			}else {
				busNumberDown++;
				if(bus.isSingleClass()) {
					busNumberSingleDown++;
				}
			}
		}
		int busNumberPresetUp=busNumberMap.get(Direction.UP.getValue());
		if(busNumberUp!=busNumberPresetUp) {
			throw new MyException("-1", "预设上行配车"+busNumberPresetUp+",实际生成配车"+busNumberUp);
		}
		int busNumberSinglePresetUp=busNumberSingleMap.get(Direction.UP.getValue());
		if(busNumberSingleUp!=busNumberSinglePresetUp) {
			throw new MyException("-1", "预设上行单班配车"+busNumberSinglePresetUp+",实际生成配车"+busNumberSingleUp);
		}
		int busNumberPresetDown=busNumberMap.get(Direction.DOWN.getValue());
		if(busNumberDown!=busNumberPresetDown) {
			throw new MyException("-1", "预设下行配车"+busNumberPresetDown+",实际生成配车"+busNumberDown);
		}
		int busNumberSinglePresetDown=busNumberSingleMap.get(Direction.DOWN.getValue());
		if(busNumberSingleDown!=busNumberSinglePresetDown) {
			throw new MyException("-1", "预设下行单班配车"+busNumberSinglePresetDown+",实际生成配车"+busNumberSingleDown);
		}
	}
	
	/**
	 * 处理到站即走
	 * @param direction
	 */
	protected void procLeaveImmediately(int direction,int leaveImmediately) {
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		for(int i=0;i<busQueueTo.size();) {
			Trip tripArrival=busQueueTo.get(i);
			Date planTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), leaveImmediately);
			planTime=planTimeAdjust(planTime);
			busQueueTo.remove(tripArrival);
			Bus bus=tripArrival.getBus();
			Trip trip=new Trip(bus, direction, planTime, scheduleParam, null);
			routeSchedule.addTrip(trip);
		}
	}
	
	private void initBusNumber() {
		if(scheduleParam.getBusNumberPreset(Direction.UP.getValue())!=null&&
			scheduleParam.getBusNumberPreset(Direction.DOWN.getValue())!=null) {//预设生成
			int busNumberUp=getBusNumberMorningPreset(Direction.UP.getValue());
			int busNumberDown=getBusNumberMorningPreset(Direction.DOWN.getValue());
			busNumberMap.put(Direction.UP.getValue(), busNumberUp);
			busNumberMap.put(Direction.DOWN.getValue(), busNumberDown);
			busNumberSingleMap.put(Direction.UP.getValue(), scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue()));
			busNumberSingleMap.put(Direction.DOWN.getValue(), scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue()));
		}else {
			int busNumber=scheduleParam.getBusNumberConfig();
			List<Date> obuTimeListUp=getObuTimeList4FirstRound(Direction.UP.getValue());
			List<Date> obuTimeListDown=getObuTimeList4FirstRound(Direction.DOWN.getValue());
			int busNumberUp=obuTimeListUp.size();
			int busNumberDown=obuTimeListDown.size();
			busNumberMap.put(Direction.UP.getValue(), busNumberUp);
			busNumberMap.put(Direction.DOWN.getValue(), busNumberDown);
			if(busNumberUp+busNumberDown<busNumber)  {//不够车，要增加班次
				int passengerUp=getPassengerFirstRound(Direction.UP.getValue());
				int tripNumberFirstRoundUp=getTripNumberFirstRound(Direction.UP.getValue());
				int passengerDown=getPassengerFirstRound(Direction.DOWN.getValue());
				int tripNumberFirstRoundDown=getTripNumberFirstRound(Direction.DOWN.getValue());
				while(busNumberUp+busNumberDown<busNumber) {
					double passengerEachBusUp=passengerUp*1.0/tripNumberFirstRoundUp;
					double passengerEachBusDown=passengerDown*1.0/tripNumberFirstRoundDown;
					if(passengerEachBusDown>passengerEachBusUp) {//下行单车人数多
						busNumberDown++;
						tripNumberFirstRoundDown++;
					}else {
						busNumberUp++;
						tripNumberFirstRoundUp++;
					}
				}
				busNumberMap.put(Direction.UP.getValue(), busNumberUp);
				busNumberMap.put(Direction.DOWN.getValue(), busNumberDown);
			}
			int busNumberSingleUnAssign=scheduleParam.getBusNumberSingle();
			int busNumberSingleUp=0;
			int busNumberSingleDown=0;
			int busNumberDoubleLeastUp=getLeastBusNumberDouble(Direction.UP.getValue());
			int busNumberDoubleLeastDown=getLeastBusNumberDouble(Direction.DOWN.getValue());
			while(busNumberSingleUnAssign>0) {
				int busNumberDoubleUp=busNumberUp-busNumberSingleUp;
				int busNumberDoubleDown=busNumberDown-busNumberSingleDown;
				if(busNumberDoubleUp<=busNumberDoubleLeastUp&&
					busNumberDoubleDown<=busNumberDoubleLeastDown) {//不能再安排单班
					break;
				}
				if(busNumberDoubleUp<=busNumberDoubleLeastUp) {//上行不能安排，只能安排下行
					busNumberSingleDown++;
				}else if(busNumberDoubleDown<=busNumberDoubleLeastDown) {//下行不能安排，只能安排上行
					busNumberSingleUp++;
				}else {
					double ratioUp=busNumberSingleUp*1.0/busNumberUp;
					double ratioDown=busNumberSingleDown*1.0/busNumberDown;
					if(ratioUp>ratioDown) {//上行单班率高，安排下行单班
						busNumberSingleDown++;
					}else {
						busNumberSingleUp++;
					}
				}
				busNumberSingleUnAssign--;
			}
			busNumberSingleMap.put(Direction.UP.getValue(), busNumberSingleUp);
			busNumberSingleMap.put(Direction.DOWN.getValue(), busNumberSingleDown);
		}
	}
	
	/**
	 * 最少配置双班车数
	 * @return
	 */
	private int getLeastBusNumberDouble(int direction) {
		int directionReverse=1-direction;
		Date latestTimeReverse=scheduleParam.getLatestTime(directionReverse);
		int wasteTime=scheduleParam.getWasteTime(latestTimeReverse, directionReverse);
		Date latestTime=scheduleParam.getLatestTime(direction);
		int maxInterval=scheduleParam.getMaxInterval(latestTimeReverse, directionReverse);
		int latestBusNumberDouble=0;
		if(DateUtil.getDateAddMinute(latestTime, -wasteTime).before(latestTimeReverse)) {
			int runTimeLastRound=DateUtil.getMinuteInterval(DateUtil.getDateAddMinute(latestTime, -wasteTime), latestTimeReverse);
			latestBusNumberDouble=(int)Math.ceil(runTimeLastRound*1.0/maxInterval);
		}
		return latestBusNumberDouble;
	}
	
	private int getTripNumberFirstRound(int direction) {
		int tripNumberFirstRound=busNumberMap.get(direction);
		Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(direction);
		if(firstPlanObuTimeLatest!=null) {
			int busNumberReverse=getBusNumberArrivalB4FirstRoundLatest(direction);
			tripNumberFirstRound+=busNumberReverse;
		}
		return tripNumberFirstRound;
	}
	
	/**
	 * 获取指定时间前的车内人数
	 * @param direction
	 * @param datetime
	 * @return
	 */
	private int getPassengerFirstRound(int direction) {
		Date datetime=scheduleParam.getFirstPlanObuTimeLatest(direction);
		if(datetime==null) {
			datetime=getObuTimeFirstBusReverse(direction);
		}
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList(direction);
		int passengerNumber=0;
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			RouteStationPassenger passenger=scheduleParam.getHighSectionPassenger(direction, scheduleHalfHour.getRunTimeDate());
			if(scheduleHalfHour.getRunTimeEndDate().before(datetime)) {
				passengerNumber+=passenger.getCurrentNumber();
			}else {
				double ratio=DateUtil.getMinuteInterval(scheduleHalfHour.getRunTimeDate(), datetime)*1.0/DateUtil.getMinuteInterval(scheduleHalfHour.getRunTimeDate(), scheduleHalfHour.getRunTimeEndDate());
				passengerNumber+=passenger.getCurrentNumber()*ratio;
				break;
			}
		}
		return passengerNumber;
	}
	
	/**
	 * 获取最晚出车时间前对面过来的车辆数
	 * @param direction
	 * @return
	 */
	private int getBusNumberArrivalB4FirstRoundLatest(int direction) {
		int directionReverse=1-direction;
		Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(direction);
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList(directionReverse);
		int busNumber=0;
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			for(Date obuTime:scheduleHalfHour.getObuTimeList()) {
				Date obuTimeNext=getObuTimeNext(directionReverse, obuTime);
				if(!obuTimeNext.before(firstPlanObuTimeLatest)) {
					break;
				}
				busNumber++;
			}
		}
		return busNumber;
	}

	public void calculate() {
		initBusNumber();
		initFirstPlan();
		initSingleBus(Direction.UP.getValue());
		initSingleBus(Direction.DOWN.getValue());
		checkBusNumber();
		Integer leaveImmediately=scheduleParam.getLeaveImmediatelyInterval(Direction.UP.getValue());
		if(leaveImmediately!=null) {
			procLeaveImmediately(Direction.UP.getValue(),leaveImmediately);
		}
		leaveImmediately=scheduleParam.getLeaveImmediatelyInterval(Direction.DOWN.getValue());
		if(leaveImmediately!=null) {
			procLeaveImmediately(Direction.DOWN.getValue(),leaveImmediately);
		}
		while(true) {
			Trip tripArrival4Up=null;
			Trip tripArrival4Down=null;
			List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
			if(!busQueueUp.isEmpty()) {
				tripArrival4Down=busQueueUp.get(0);
			}
			List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
			if(!busQueueDown.isEmpty()) {
				tripArrival4Up=busQueueDown.get(0);
			}
			int direction=Direction.UP.getValue();
			List<Trip> busQueueTo=busQueueDown;
			if(tripArrival4Up==null||(tripArrival4Down!=null&&
				tripArrival4Down.getNextObuTimeMin().before(tripArrival4Up.getNextObuTimeMin()))) {//上行没车跑或下行先到
				direction=Direction.DOWN.getValue();
				busQueueTo=busQueueUp;
			}
			Trip tripArrival=busQueueTo.get(0);
			if(DateUtil.getDateHM("0658").equals(tripArrival.getTripEndTime()))
				System.out.println("aaa");
			Trip tripLast=routeSchedule.getTripFullLast(direction);
			Date planTime=null;
			if(tripLast==null) {
				planTime=scheduleParam.getFirstTime(direction);
			}else {

				Integer intervalLast=tripLast.getInterval();
				if(intervalLast==null) {//上一班是头车
					Date firstTime=scheduleParam.getFirstTime(direction);
					intervalLast=DateUtil.getMinuteInterval(firstTime, tripArrival.getNextObuTimeMin());
				}
				planTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalLast);
				boolean isCatchUp=isBusMatchPlanTimeHinder(busQueueTo, intervalLast, tripLast.getTripBeginTime());
				if(isCatchUp) {//后面的车都赶得上
					if(planTime.after(tripArrival.getTripEndTime())) {//按前车间隔发，这台车能赶上，看要不要减少间隔
						//按前车间隔最后一台车计划时间
						Date planTimeLast4Final=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), busQueueTo.size()*intervalLast);
						Trip tripArrivalFinal=busQueueTo.get(busQueueTo.size()-1);
						int restTime=DateUtil.getMinuteInterval(tripArrivalFinal.getTripEndTime(), planTimeLast4Final);
						int maxRestTime=scheduleParam.getMaxRestTime(tripArrivalFinal.getTripEndTime(), direction);
						if(restTime>maxRestTime) {//最后一台车停站超过最大停站时间
							int intervalReduce1st=intervalLast-2;//间隔减两分钟
							isCatchUp=isBusMatchPlanTimeHinder(busQueueTo, intervalReduce1st, tripLast.getTripBeginTime());
							if(isCatchUp) {//间隔减少2分钟还能赶上
								int intervalReduce2nd=intervalReduce1st-2;//间隔再减两分钟
								isCatchUp=isBusMatchPlanTimeHinder(busQueueTo, intervalReduce2nd, tripLast.getTripBeginTime());
								if(isCatchUp) {//减4分钟还能赶上，后面来车快，重新计算间隔，不参考前面一班间隔，推倒重来
									int interval=getIntervalMin(busQueueTo, tripLast);
									planTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
								}else {//不能减两次，先降一级
									planTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalReduce1st);
								}
							}
						}
					}
				}else {//后面有车赶不上，看要不要增加间隔
					int interval=getInterval4Increase(busQueueTo, intervalLast, tripLast.getTripBeginTime());
					planTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
				}
				if(!planTime.after(tripArrival.getTripEndTime())) {//发班时间比到达时间早
					planTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
				}
			}
			planTime=planTimeAdjust(planTime);
			busQueueTo.remove(tripArrival);
			Bus bus=tripArrival.getBus();
			Trip trip=new Trip(bus, direction, planTime, scheduleParam, tripLast);
			routeSchedule.addTrip(trip);
			trip.setLastTrip(tripLast);
			int directionReverse=1-direction;
			leaveImmediately=scheduleParam.getLeaveImmediatelyInterval(directionReverse);
			if(leaveImmediately!=null) {
				procLeaveImmediately(directionReverse, leaveImmediately);
			}
			if(!planTime.before(scheduleParam.getEarlyPeakEndTime())) {
				break;
			}
		}
	}
	
	private int getIntervalMin(List<Trip> busQueueTo,Trip tripLast) {
		Integer interval=null;
		for(int i=1;i<busQueueTo.size();i++) {
			Trip tripArrivalNext=busQueueTo.get(i);
			if(tripArrivalNext.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
				int intervalTemp=(int)Math.ceil(DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripArrivalNext.getNextObuTimeMin())*1.0/(i+1));
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
		}
		if(interval==null) {//按来车方向计算不到间隔，按周转时间计算
			Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(tripLast.getDirection(), tripLast.getTripBeginTime());
			List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
			List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
			int busNumber=busQueueUp.size()+busQueueDown.size();
			interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), obuTimeNextRound)/busNumber;
		}
		return interval;
	}
	
	private int getInterval4Increase(List<Trip> busQueueTo,int interval,Date planTimeLast) {
		boolean increase=false;
		for(int i=1;i<busQueueTo.size();i++) {
			Trip tripArrivalNext=busQueueTo.get(i);
			Date planTimeNext=DateUtil.getDateAddMinute(planTimeLast, (i+1)*interval);
			if(tripArrivalNext.getNextObuTimeMin()==null)
				System.out.println("aaa");
			if(tripArrivalNext.getNextObuTimeMin().after(planTimeNext)) {
				int diff=DateUtil.getMinuteInterval(tripArrivalNext.getNextObuTimeMin(), planTimeNext);
				double increaseAvgTemp=diff*1.0/i;
				if(increaseAvgTemp>2) {//后面班次分摊超过两分钟，可能这班就需要增加
					increase=true;
					break;
				}
			}
		}
		if(increase) {
			int intervalLast=interval;
			Date planTimeArrialLast=planTimeLast;
			int increaseAdditional=0;//额外分摊
			for(int i=0;i<busQueueTo.size();i++) {
				Trip tripArrivalNext=busQueueTo.get(i);
				int intervalTemp=getIntervalIncreaseNext(intervalLast);
				Date planTimeNext=DateUtil.getDateAddMinute(planTimeArrialLast, intervalTemp);
				if(tripArrivalNext.getNextObuTimeMin().after(planTimeNext)) {
					int diff=DateUtil.getMinuteInterval(tripArrivalNext.getNextObuTimeMin(), planTimeNext);
					int increaseAdditionalTemp=diff/(i+1);
					if(increaseAdditionalTemp>increaseAdditional) {
						increaseAdditional=increaseAdditionalTemp;
					}
				}
				intervalLast=intervalTemp;
				planTimeArrialLast=planTimeNext;
			}
			interval=getIntervalIncreaseNext(interval)+increaseAdditional;
		}
		return interval;
	}
	
	private int getIntervalIncreaseNext(int intervalLast) {
		int interval=intervalLast+2;
		if(intervalLast==5) {
			interval=8;
		}
		return interval;
	}
	
	private boolean isBusMatchPlanTimeHinder(List<Trip> busQueueTo,int interval,Date planTimeLast) {
		boolean isMatched=true;
		for(int i=1;i<busQueueTo.size();i++) {
			Trip tripArrivalNext=busQueueTo.get(i);
			Date planTimeNext=DateUtil.getDateAddMinute(planTimeLast, (i+1)*interval);
			if(tripArrivalNext.getNextObuTimeMin().after(planTimeNext)) {
				isMatched=false;
				break;
			}
		}
		return isMatched;
	}
	
	/**
	 * 修正计划时间，偶数或5的倍数
	 * @param planTime
	 * @return
	 */
	private Date planTimeAdjust(Date planTime) {
		int minute=DateUtil.getMinuteOfHour(planTime);
		if(minute%2!=0&&minute%5!=0) {//时间不是偶数或5的倍数,加1分钟变成偶数
			planTime=DateUtil.getDateAddMinute(planTime, 1);
		}
		return planTime;
	}

	/**
	 * 初始化出厂
	 */
	private void initFirstPlan() {
		Map<Integer,List<Trip>> firstPlanTimeListMap=getBusFirstPlanMap();//获取出厂时间
		Map<String, List<Trip>> busTripListMap=new HashMap<String, List<Trip>>();
		for(Integer direction:firstPlanTimeListMap.keySet()) {
			List<Trip> tripList=routeSchedule.getTripList(direction);
			List<Trip> firstPlanTimeList=firstPlanTimeListMap.get(direction);
			Trip tripLast=null;
			for(Trip firstPlanTime:firstPlanTimeList) {
				Bus bus=firstPlanTime.getBus();
				String key=bus.getStartDirection()+"_"+bus.getStartOrderNumber();
				List<Trip> busTripList=busTripListMap.get(key);
				if(busTripList==null) {
					busTripList=new ArrayList<Trip>();
					busTripListMap.put(key, busTripList);
				}
				busTripList.add(firstPlanTime);
				busTripList.sort(new TripBeginTimeComparator());
				tripList.add(firstPlanTime);
				firstPlanTime.setLastTrip(tripLast);
				tripLast=firstPlanTime;
			}
		}
		for(String key:busTripListMap.keySet()) {
			List<Trip> busTripList=busTripListMap.get(key);
			Trip tripLast=busTripList.get(busTripList.size()-1);
			Bus bus=tripLast.getBus();
			for(Trip trip:busTripList) {
				trip.setBus(bus);
			}
			routeSchedule.addBusTripList(bus, busTripList);
			List<Trip> busQueue=routeSchedule.getBusQueue(tripLast.getDirection());
			busQueue.add(tripLast);
			busQueue.sort(new TripBeginTimeComparator());
		}
	}
	
	private void initSingleBus(int direction) {
		Integer singleBusNumber=busNumberSingleMap.get(direction);
		if(singleBusNumber!=null&&singleBusNumber>0) {
			List<Trip> tripList=routeSchedule.getTripList(direction);
			for(int i=tripList.size()-1;i>=0;) {
				Trip trip=tripList.get(i);
				Bus bus=trip.getBus();
				if(bus.getStartDirection()==direction) {
					bus.setSingleClass(true);
					singleBusNumber--;
					if(singleBusNumber==0) {
						break;
					}
					i=i-2;//单班隔一台
				}else {
					i--;
				}
			}
			if(singleBusNumber>0) {
				for(int i=tripList.size()-1;i>=0;i--) {
					Trip trip=tripList.get(i);
					Bus bus=trip.getBus();
					if(bus.getStartDirection()==direction
						&&!bus.isSingleClass()) {
						bus.setSingleClass(true);
						singleBusNumber--;
						if(singleBusNumber==0) {
							break;
						}
					}
				}
			}
		}
	}
	
	private Map<Integer,List<Trip>> getBusFirstPlanMap(){
		List<Trip> firstPlanTimeListUp=getFirstPlanTimeList(Direction.UP.getValue());
		List<Trip> firstPlanTimeListDown=getFirstPlanTimeList(Direction.DOWN.getValue());
		Map<Integer, List<Trip>> busFirstPlanMap=new HashMap<Integer, List<Trip>>();
		if(firstPlanTimeListUp!=null)
			busFirstPlanMap.put(Direction.UP.getValue(), firstPlanTimeListUp);
		if(firstPlanTimeListDown!=null)
			busFirstPlanMap.put(Direction.DOWN.getValue(), firstPlanTimeListDown);
		return busFirstPlanMap;
	}
	
	private int getBusNumberMorningPreset(int direction) {
		int busNumberMorning=scheduleParam.getBusNumberPreset(direction);
		List<ScheduleShiftPreset> scheduleShiftList=scheduleParam.getShiftListPreset();
		if(scheduleShiftList!=null) {
			for(ScheduleShiftPreset scheduleShift:scheduleShiftList) {
				if(scheduleShift.getShiftType()==ShiftType.EVENING_SHIFT.getValue()||
					scheduleShift.getShiftType()==ShiftType.MIDDLE_SHIFT.getValue()) {//晚班或中班
					int busNumberShift=0;
					if(direction==Direction.UP.getValue()) {
						busNumberShift=scheduleShift.getBusNumberUp();
					}else {
						busNumberShift=scheduleShift.getBusNumberDown();
					}
					busNumberMorning-=busNumberShift;
				}
			}
		}
		return busNumberMorning;
	}
	
	private Date getObuTimeNext(int direction,Date planTime) {
		Date arrivalTime=scheduleParam.getArrivalTime(planTime, direction);
		int restTime=scheduleParam.getRestTime(arrivalTime, direction);
		Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTimeNext;
	}
	
	/**
	 * 获取对面头车过来后的发班时间
	 * @return
	 */
	private Date getObuTimeFirstBusReverse(int direction) {
		int directionReverse=1-direction;
		Date firstTimeReverse=scheduleParam.getFirstTime(directionReverse);
		Date obuTimeNext=getObuTimeNext(directionReverse, firstTimeReverse);//获取对面头车到达后最计划发班时间
		return obuTimeNext;
	}
	
	/**
	 * 获取首轮计划
	 * @param direction
	 * @return
	 */
	private List<Date> getObuTimeList4FirstRound(int direction){
		Date obuTimeNext=getObuTimeFirstBusReverse(direction);
		List<Date> obuTimeList=getObuTimeList(direction, obuTimeNext);
		return obuTimeList;
	}
 	
	private List<Trip> getFirstPlanTimeList(int direction) {
		int busNumber=busNumberMap.get(direction);
		if(busNumber==0)
			return null;
		int directionReverse=1-direction;
		Date firstTimeReverse=scheduleParam.getFirstTime(directionReverse);
		List<Date> obuTimeList=getObuTimeList4FirstRound(direction);
		List<Trip> firstPlanTimeList=null;
		if(obuTimeList.size()==busNumber) {
			firstPlanTimeList=getFirstPlanTimeList(direction, obuTimeList);
		}else if(obuTimeList.size()<busNumber) {//还有车多
			Date firstTime=scheduleParam.getFirstTime(direction);
			Date obuTimeNext=getObuTimeNext(directionReverse, firstTimeReverse);//获取对面头车到达后最计划发班时间
			if(!obuTimeNext.after(firstTime)) {//对面车辆在首班车前到
				obuTimeList=new ArrayList<Date>();
				int busNumberReverse=busNumberMap.get(directionReverse);
				if(busNumberReverse==0) {//对面没有车,凸头过去
					int wasteTime=scheduleParam.getWasteTime(firstTime, direction);
					wasteTime+=5;//加5分钟停站
					Date obuTime=DateUtil.getDateAddMinute(firstTimeReverse, -wasteTime);
					obuTime=planTimeAdjust(obuTime);
					obuTimeList.add(obuTime);
					busNumber--;
				}
				Date obuTime=firstTime;
				for(int i=0;i<busNumber;i++) {
					obuTimeList.add(obuTime);
					obuTime=DateUtil.getDateAddMinute(obuTime, 5);
				}
				firstPlanTimeList=getFirstPlanTimeList(direction, obuTimeList);
			}else {
				Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(direction);
				if(firstPlanObuTimeLatest==null) {//不能反插，只能增加班次
					firstPlanTimeList=getFirstPlanTimeList(direction, busNumber, obuTimeNext);
				}else {
					if(!firstPlanObuTimeLatest.after(obuTimeNext)) {//需要在对面头车到之前发完
						firstPlanTimeList=getFirstPlanTimeList(direction, busNumber, obuTimeNext);
					}else {
						firstPlanTimeList=getFirstPlanTimeList4Late(direction, firstPlanObuTimeLatest);
					}
				}
			}
		}else {//车不够
			Date obuTimeNext=scheduleParam.getMinObuTimeNext(directionReverse, firstTimeReverse);
			obuTimeList=getObuTimeList(direction, obuTimeNext);
			if(obuTimeList.size()==busNumber) {//按最小停站计算，车刚好匹配上
				firstPlanTimeList=getFirstPlanTimeList(direction, obuTimeList);
			}else {//只能减少班次
				Date firstTime=scheduleParam.getFirstTime(direction);
				obuTimeList=new ArrayList<Date>();
				Integer interval=null;
				if(busNumber>0) {
					interval=DateUtil.getMinuteInterval(firstTime, obuTimeNext)/(busNumber);
				}
				if(busNumber>0) {
					obuTimeList.add(firstTime);
					busNumber--;
					Date obuTimeLast=firstTime;
					while(busNumber>0) {
						Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
						obuTimeList.add(obuTime);
						obuTimeLast=obuTime;
						busNumber--;
					}
				}
				firstPlanTimeList=getFirstPlanTimeList(direction, obuTimeList);
			}
		}
		return firstPlanTimeList;
	}
	
	private List<Trip> getFirstPlanTimeList(int direction,int busNumber,Date obuTimeNext){
		List<Date> obuTimeList=getFirstPlanTimeListIncrease(direction, busNumber, obuTimeNext);
		List<Trip> firstPlanTimeList=getFirstPlanTimeList(direction, obuTimeList);
		return firstPlanTimeList;
	}
	
	private List<Trip> getFirstPlanTimeList(int direction,List<Date> obuTimeList){
		int busOrderNumber=1;
		List<Trip> firstPlanTimeList=new ArrayList<Trip>();
		for(Date obuTime:obuTimeList) {
			Bus bus=new Bus(direction, busOrderNumber);
			Trip trip=new Trip(bus, direction, obuTime, scheduleParam, null);
			firstPlanTimeList.add(trip);
			busOrderNumber++;
		}
		return firstPlanTimeList;
	}
	
	/**
	 * 计算出车时间(有单班晚出)
	 * @return
	 */
	private List<Trip> getFirstPlanTimeList4Late(int direction,Date firstPlanObuTimeLatest) {
		int busNumber=busNumberMap.get(direction);
		int busNumberSingle=busNumberSingleMap.get(direction);//预设的单班车数
		int busNumberDouble=busNumber-busNumberSingle;
		int directionReverse=1-direction;
		List<ScheduleHalfHour> scheduleHalfHourReverseList=scheduleParam.getScheduleHalfHourList(directionReverse);
		List<Date> busReverseObuTimeNextList=new ArrayList<Date>();
		int busNumberReverse=busNumberMap.get(directionReverse);
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourReverseList) {
			for(Date obuTime:scheduleHalfHour.getObuTimeList()) {
				Date obuTimeNext=getObuTimeNext(directionReverse, obuTime);
				if(!obuTimeNext.after(firstPlanObuTimeLatest)) {
					Date minObuTimeNext=scheduleParam.getMinObuTimeNext(directionReverse, obuTime);
					busReverseObuTimeNextList.add(minObuTimeNext);
					busNumberReverse--;
					if(busNumberReverse==0)
						break;
				}
			}
			if(busNumberReverse==0)
				break;
		}
		int busNumberB4ObuTimeLatest=busNumber+busReverseObuTimeNextList.size();
		//计算最晚出车时间前的发班时间
		List<Date> obuTimeList=getFirstPlanTimeListIncrease(direction, busNumberB4ObuTimeLatest, firstPlanObuTimeLatest);
		int busNumberFirst=busNumberDouble;
		while(true) {
			boolean success=true;
			int busNumberLate=busNumber-busNumberFirst;
			if(busNumberLate>=busReverseObuTimeNextList.size()) {//晚出单班车不能多于反向到站车
				busNumberFirst++;
				continue;
			}
			boolean isReverseBus=true;
			int reverseBusIndex=0;
			for(int i=busNumberFirst;i<busNumberB4ObuTimeLatest;i++) {
				Date obuTime=obuTimeList.get(i);
				if(isReverseBus) {
					Date minObuTimeNext=busReverseObuTimeNextList.get(reverseBusIndex);
					if(minObuTimeNext.after(obuTime)) {//到站赶不上
						success=false;
						break;
					}
					if(busNumberLate>0) {
						isReverseBus=false;
					}
					reverseBusIndex++;
				}else {
					busNumberLate--;
					isReverseBus=true;
				}
			}
			if(!success) {
				if(busNumberFirst==busNumber) {
					break;
				}
				busNumberFirst++;
			}else {
				break;
			}
		}
		List<Trip> firstPlanTimeList=new ArrayList<Trip>();
		int busNumberLate=busNumber-busNumberFirst;
		busNumberReverse=busNumberMap.get(directionReverse);
		int busOrderNumber=1;
		int busOrderNumberReverse=1;
		for(int i=0;i<obuTimeList.size();i++) {
			Date obuTime=obuTimeList.get(i);
			obuTime=planTimeAdjust(obuTime);
			if(i<busNumberFirst||(busNumberLate>0&&(i-busNumberFirst)%2==1)) {//出车方向
				Bus bus=new Bus(direction, busOrderNumber);
				Trip trip=new Trip(bus, direction, obuTime, scheduleParam, null);
				firstPlanTimeList.add(trip);
				busOrderNumber++;
				if(i>busNumberFirst) {
					busNumberLate--;
				}
			}else {//对向车过来跑
				if(busOrderNumberReverse>busNumberReverse)
					break;
				Bus bus=new Bus(directionReverse, busOrderNumberReverse);
				Trip trip=new Trip(bus, direction, obuTime, scheduleParam, null);
				firstPlanTimeList.add(trip);
				busOrderNumberReverse++;
			}
		}
		return firstPlanTimeList;
	}
	
	private List<Date> getFirstPlanTimeListIncrease(int direction,int busNumber,Date obuTimeNext){
		List<Date> obuTimeList=getObuTimeList(direction, obuTimeNext);
		List<ScheduleHalfHour> firstRoundScheduleHalfHourList=getFirstRoundScheduleHalfHour(direction, obuTimeNext);
		int retryTime=0;
		while(obuTimeList.size()<busNumber) {
			List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(firstRoundScheduleHalfHourList);
			boolean isSuccess=false;
			int increase=1;//时段增加班次数,防止加一班没反应造成死循环
			while(!isSuccess) {
				if(retryTime>1000) {//重算超过1000次
					obuTimeList=new ArrayList<Date>();
					Date firstTime=scheduleParam.getFirstTime(direction);
					Date obuTime=firstTime;
					for(int i=0;i<busNumber;i++) {
						obuTimeList.add(obuTime);
						obuTime=DateUtil.getDateAddMinute(obuTime, 5);
					}
					break;
				}
				retryTime++;
				for(ScheduleHalfHour scheduleHalfHour:highScheduleHalfHourList) {
					DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), direction);
					Integer firstHalfClassesNumberOrgi=dispatchRule.getFirstHalfClassesNumber();
					Integer lastHalfClassesNumberOrgi=dispatchRule.getLastHalfClassesNumber();
					for(int i=0;i<increase;i++) {
						dispatchRule.classesNumberIncrease(scheduleHalfHour.getRunTime());
					}
					dispatchRule.resetScheduleHalfHourObuTimeList();
					List<Date> obuTimeListTemp=getObuTimeList(direction, obuTimeNext);
					if(obuTimeListTemp.size()<=obuTimeList.size()
						||obuTimeListTemp.size()>busNumber) {//班次没有增加，找另外时段加
						dispatchRule.setFirstHalfClassesNumber(firstHalfClassesNumberOrgi);
						dispatchRule.setLastHalfClassesNumber(lastHalfClassesNumberOrgi);
						dispatchRule.resetScheduleHalfHourObuTimeList();
						if(obuTimeListTemp.size()>busNumber) {//超过预设车辆数，增加另外半小时
							if(dispatchRule.getFirstHalfScheduleHalfHour()==scheduleHalfHour) {
								scheduleHalfHour=dispatchRule.getLastHalfScheduleHalfHour();
								if(scheduleHalfHour!=null) {
									dispatchRule.classesNumberIncrease(scheduleHalfHour.getRunTime());
									dispatchRule.resetScheduleHalfHourObuTimeList();
									obuTimeListTemp=getObuTimeList(direction, obuTimeNext);
									if(obuTimeListTemp.size()<=obuTimeList.size()||
											obuTimeListTemp.size()>busNumber) {//班次没有增加或超过配车数，找另外时段加
										dispatchRule.setFirstHalfClassesNumber(firstHalfClassesNumberOrgi);
										dispatchRule.setLastHalfClassesNumber(lastHalfClassesNumberOrgi);
										dispatchRule.resetScheduleHalfHourObuTimeList();
									}else {
										obuTimeList=obuTimeListTemp;
										isSuccess=true;
										break;
									}
								}
							}
						}
					}else {
						obuTimeList=obuTimeListTemp;
						isSuccess=true;
						break;
					}
				}
				increase++;
			}
		}
		return obuTimeList;
	}
	
	private List<Date> getObuTimeList(int direction,Date endTime){
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList(direction);
		List<Date> obuTimeList=new ArrayList<Date>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			for(Date obuTime:scheduleHalfHour.getObuTimeList()) {
				if(!obuTime.before(endTime)) {
					break;
				}
				obuTimeList.add(obuTime);
			}
		}
		return obuTimeList;
	}
	
	private List<ScheduleHalfHour> getFirstRoundScheduleHalfHour(int direction,Date endTime){
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList(direction);
		List<ScheduleHalfHour> firstRoundScheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			if(!scheduleHalfHour.getRunTimeDate().before(endTime)) {
				break;
			}
			if(!firstRoundScheduleHalfHourList.contains(scheduleHalfHour)) {
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), direction);
				if(dispatchRule.getFirstHalfScheduleHalfHour()!=null) {
					firstRoundScheduleHalfHourList.add(dispatchRule.getFirstHalfScheduleHalfHour());
				}
				if(dispatchRule.getLastHalfScheduleHalfHour()!=null) {
					firstRoundScheduleHalfHourList.add(dispatchRule.getLastHalfScheduleHalfHour());
				}
			}
		}
		return firstRoundScheduleHalfHourList;
	}
}
