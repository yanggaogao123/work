package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.ScheduleShiftPreset;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.util.TripBeginTimeComparator;

import java.util.*;

/**
 * 单边出车
 * @author lindeyong
 *
 */
public class ScheduleGenerateOneParkB4Peek4Full {

	private ScheduleParam scheduleParam;
	
	private RouteSchedule routeSchedule;

	private Integer startDirection;//出车方向
	
	private Map<Integer,Integer> busNumberMap=new HashMap<Integer, Integer>();
	
	private Map<Integer,Integer> busNumberSingleMap=new HashMap<Integer, Integer>();//单班车数
	
	public ScheduleGenerateOneParkB4Peek4Full(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		this.scheduleParam=scheduleParam;
		this.routeSchedule=routeSchedule;
		startDirection=Direction.UP.getValue();
		if(!scheduleParam.isAppearDirection(Direction.UP.getValue())) {
			startDirection=Direction.DOWN.getValue();
		}
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
	
	/**
	 * 获取每个车位第一个班次
	 * @return
	 */
	private Map<Integer, List<Trip>> getFirstTripListMap(){
		Map<Bus, List<Trip>> tripMap=routeSchedule.getTripMap();
		Map<Integer, List<Trip>> firstTripListMap=new HashMap<Integer, List<Trip>>();
		for(Bus bus:tripMap.keySet()) {
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			Trip firstTrip=busTripList.get(0);
			List<Trip> firstTripList=firstTripListMap.get(firstTrip.getDirection());
			if(firstTripList==null) {
				firstTripList=new ArrayList<Trip>();
				firstTripListMap.put(firstTrip.getDirection(), firstTripList);
			}
			firstTripList.add(firstTrip);
		}
		for(List<Trip> firstTripList:firstTripListMap.values()) {
			Collections.sort(firstTripList, new TripBeginTimeComparator());
		}
		return firstTripListMap;
	}
	
	private void resetStartDirectionAndOrderNumber() {
		Map<Integer, List<Trip>> firstTripListMap=getFirstTripListMap();
		for(Integer startDirection:firstTripListMap.keySet()) {
			List<Trip> firstTripList=firstTripListMap.get(startDirection);
			int startOrderNumber=0;
			for(Trip firstTrip:firstTripList) {
				Bus bus=firstTrip.getBus();
				bus.setStartDirection(startDirection);
				bus.setStartOrderNumber(++startOrderNumber);
			}
		}
	}
	
	private void initBusNumber() {
		if(scheduleParam.getBusNumberPreset(Direction.UP.getValue())!=null&&
			scheduleParam.getBusNumberPreset(Direction.DOWN.getValue())!=null) {//预设生成
			int busNumberUp=getBusNumberMorning(Direction.UP.getValue());
			int busNumberDown=getBusNumberMorning(Direction.DOWN.getValue());
			busNumberMap.put(Direction.UP.getValue(), busNumberUp);
			busNumberMap.put(Direction.DOWN.getValue(), busNumberDown);
			busNumberSingleMap.put(Direction.UP.getValue(), scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue()));
			busNumberSingleMap.put(Direction.DOWN.getValue(), scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue()));
		}else {
			int busNumber=scheduleParam.getBusNumberConfig();
			busNumberMap.put(startDirection, busNumber);
			busNumberMap.put(1-startDirection, 0);
			int busNumberSingle=scheduleParam.getBusNumberSingle();
			busNumberSingleMap.put(startDirection, busNumberSingle);
			busNumberSingleMap.put(1-startDirection, 0);
		}
	}
	
	public void calculate() {
		initBusNumber();
		int busNumberUp=busNumberMap.get(Direction.UP.getValue());
		int busNumberDown=busNumberMap.get(Direction.DOWN.getValue());
		int busNumber=busNumberUp+busNumberDown;
		if(busNumber==1) {//预设只有一台车，发头车
			int startDirection=Direction.UP.getValue();
			if(busNumberUp==0) {
				startDirection=Direction.DOWN.getValue();
			}
			int startOrderNumber=routeSchedule.newBusOrder(startDirection);
			Bus bus=new Bus(startDirection, startOrderNumber);
			Date firstTime=scheduleParam.getFirstTime(startDirection);
			Trip trip=new Trip(bus, startDirection, firstTime, scheduleParam, null);
			routeSchedule.addTrip(trip);
		}else {
			initFirstPlan();
		}
		resetStartDirectionAndOrderNumber();
		routeSchedule.tripListSort();
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
			if(DateUtil.getDateHM("0715").equals(tripArrival.getTripEndTime()))
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
			if(tripLast!=null) {
				int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), planTime);
				planTime=planTimeAdjust(tripLast.getTripBeginTime(),interval);
			}
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
	
	private Date planTimeAdjust(Date planTimeLast,int interval) {
		Date planTime=DateUtil.getDateAddMinute(planTimeLast, interval);
		int minute=DateUtil.getMinuteOfHour(planTime);
		if(minute%2==0)
			return planTime;
		if(minute%5==0) {//05、15
			if(interval==15) {//间隔15分钟，防止出现25、36、48、00
				return planTime;
			}
		}
		planTime=DateUtil.getDateAddMinute(planTime, 1);
		return planTime;
	}
	
	private Date planTimeAdjust(Date planTimeLast,double intervalDouble) {
		int interval=(int)Math.ceil(intervalDouble);
		if(scheduleParam.isInPeakTime(planTimeLast)) {
			interval=(int)Math.floor(intervalDouble);
		}
		int minute=DateUtil.getMinuteOfHour(planTimeLast);
		if(minute%10==0) {//10、20、30
			if(interval%2==1&&interval%5!=0) {//间隔基数，不是5分倍数，
				if(scheduleParam.isInPeakTime(planTimeLast)) {
					interval--;//高峰，先小后大
				}else {
					interval++;
				}
			}
		}else if(minute%5==0){//15、25、35
			if(interval%2==0&&interval%5!=0) {//间隔偶数，不是5分倍数，
				if(scheduleParam.isInPeakTime(planTimeLast)) {
					interval--;//高峰，先小后大
				}else {
					interval++;
				}
			}
		}else if(minute%2==0) {//02、04、06、08
			if(interval%2==1) {
				if(scheduleParam.isInPeakTime(planTimeLast)) {
					interval--;//高峰，先小后大
				}else {
					interval++;
				}
			}
		}else {//01、03、07
			if(interval%2==0) {
				if(scheduleParam.isInPeakTime(planTimeLast)) {
					interval--;//高峰，先小后大
				}else {
					interval++;
				}
			}
		}
		Date planTime=DateUtil.getDateAddMinute(planTimeLast, interval);
		return planTime;
	}
	
	/**
	 * 初始化出厂
	 */
	private void initFirstPlan() {
		int directionReverse=1-startDirection;
		int busNumberUp=busNumberMap.get(Direction.UP.getValue());
		int busNumberDown=busNumberMap.get(Direction.DOWN.getValue());
		int busNumber=busNumberUp+busNumberDown;
		Date firstTime=scheduleParam.getFirstTime(startDirection);
		Date firstTimeReverse=scheduleParam.getFirstTime(directionReverse);
		Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(startDirection);
		boolean addTripB4FirstTime=true;
		Date beginTime=firstTime;
		if(scheduleParam.getTripBeginTimeB4FirstTime()==null) {//按路单没有凸头
			Date arrivalTime=scheduleParam.getArrivalTime(firstTime, startDirection);
			if(!firstTimeReverse.before(DateUtil.getDateAddMinute(arrivalTime, -5))) {//预留5分钟，防止首班误差
				addTripB4FirstTime=false;
				Date firstBusObuTimeNextRound=scheduleParam.getObuTimeNextRound(startDirection, beginTime);
				int intervalAvg=(int)Math.ceil(DateUtil.getMinuteInterval(beginTime, firstBusObuTimeNextRound)*1.0/busNumber);
				Date firstPlanObuTimeLatestCal=DateUtil.getDateAddMinute(firstBusObuTimeNextRound, -intervalAvg);
				if(firstPlanObuTimeLatest==null||firstPlanObuTimeLatest.after(firstPlanObuTimeLatestCal)) {
					firstPlanObuTimeLatest=firstPlanObuTimeLatestCal;
				}
			}
		}
		while(addTripB4FirstTime) {
			Date firstBusObuTimeNextRound=scheduleParam.getObuTimeNextRound(startDirection, beginTime);
			int intervalAvg=(int)Math.ceil(DateUtil.getMinuteInterval(beginTime, firstBusObuTimeNextRound)*1.0/busNumber);
			Date firstPlanObuTimeLatestCal=DateUtil.getDateAddMinute(firstBusObuTimeNextRound, -intervalAvg);
			if(firstPlanObuTimeLatest==null||firstPlanObuTimeLatest.after(firstPlanObuTimeLatestCal)) {
				firstPlanObuTimeLatest=firstPlanObuTimeLatestCal;
			}
			List<Date> planTimeList=new ArrayList<Date>();
			planTimeList.add(beginTime);
			Date planTimeLast=beginTime;
			for(int i=busNumber-1;i>0;i--) {
				intervalAvg=DateUtil.getMinuteInterval(planTimeLast, firstPlanObuTimeLatest)/i;
				Date planTime=DateUtil.getDateAddMinute(planTimeLast, intervalAvg);
				planTime=planTimeAdjust(planTime);
				planTimeList.add(planTime);
			}
			Date planTime4FirstTimeReverse=null;
			for(int i=0;i<planTimeList.size();i++) {
				Date planTime=planTimeList.get(i);
				Date planTimeReverseMin=null;
				if(i==0) {
					Date arrivalTime=scheduleParam.getArrivalTime(beginTime, startDirection);
					planTimeReverseMin=DateUtil.getDateAddMinute(arrivalTime, 1);
				}else {
					planTimeReverseMin=scheduleParam.getMinObuTimeNext(startDirection, planTime);
				}
				if(!planTimeReverseMin.after(firstTimeReverse)) {
					planTime4FirstTimeReverse=planTime;
				}else {
					break;
				}
			}
			if(planTime4FirstTimeReverse!=null) {//对面首班有车执行
				Date planTimeFirstReverse=null;
				if(planTime4FirstTimeReverse.equals(beginTime)) {//这边头车做对面头车
					planTimeFirstReverse=firstTimeReverse;
				}else {
					planTimeFirstReverse=scheduleParam.getMinObuTimeNext(startDirection, beginTime);
				}
				//跑一圈回来最早发班时间
				Date planTimeNextMin=scheduleParam.getMinObuTimeNext(directionReverse, planTimeFirstReverse);
				int maxInterval=scheduleParam.getMaxInterval(firstPlanObuTimeLatest, startDirection);
				Date planTimeNextLatest=DateUtil.getDateAddMinute(firstPlanObuTimeLatest, maxInterval);
				if(!planTimeNextMin.after(planTimeNextLatest)) {
					addTripB4FirstTime=false;
					break;
				}
			}
			beginTime=DateUtil.getDateAddMinute(beginTime, -5);
		}
		int addTripBusNumber=0;
		if(!beginTime.equals(firstTime)) {
			int intervalAvg=(int)Math.ceil(DateUtil.getMinuteInterval(beginTime, firstPlanObuTimeLatest)*1.0/(busNumber-1));
			if(intervalAvg!=0)
				addTripBusNumber=DateUtil.getMinuteInterval(beginTime, firstTime)/intervalAvg;
			if(addTripBusNumber==0)//可能是0.9
				addTripBusNumber=1;
			if(DateUtil.getMinuteInterval(beginTime, firstTime)/addTripBusNumber>intervalAvg*1.5) {
				addTripBusNumber++;
			}
			Date planTime=beginTime;
			for(int i=addTripBusNumber;i>0;i--) {
				int orderNumber=routeSchedule.newBusOrder(startDirection);
				Bus bus=new Bus(startDirection, orderNumber);
				Trip trip=new Trip(bus, startDirection, planTime, scheduleParam, null);
				routeSchedule.addTrip(trip);
				intervalAvg=DateUtil.getMinuteInterval(planTime, firstTime)/i;
				planTime=DateUtil.getDateAddMinute(planTime, intervalAvg);
				planTime=planTimeAdjust(planTime);
			}
			busNumber=busNumber-addTripBusNumber;
		}
		Date planTime=firstTime;
		firstPlanObuTimeLatest=planTimeAdjust(firstPlanObuTimeLatest);
		for(int i=busNumber;i>0;i--) {
			int orderNumber=routeSchedule.newBusOrder(startDirection);
			Bus bus=new Bus(startDirection, orderNumber);
			Trip trip=new Trip(bus, startDirection, planTime, scheduleParam, null);
			routeSchedule.addTrip(trip);
			if(i>1) {
				double intervalAvg=DateUtil.getMinuteInterval(planTime, firstPlanObuTimeLatest)*1.0/(i-1);
				planTime=planTimeAdjust(planTime,intervalAvg);
			}
		}
		List<Trip> busQueue=routeSchedule.getBusQueue(startDirection);
		for(int i=0;i<busQueue.size()-1;) {
			Trip tripArrival=busQueue.get(i);
			Trip tripArrivalNext=busQueue.get(i+1);
			planTime=firstTimeReverse;
			if(!tripArrivalNext.getNextObuTimeMin().after(firstTimeReverse)) {//下一台车可以做对面头车
				planTime=tripArrival.getNextObuTimeMin();
			}
			Bus bus=tripArrival.getBus();
			Trip trip=new Trip(bus, directionReverse, planTime, scheduleParam, null);
			routeSchedule.addTrip(trip);
			busQueue.remove(tripArrival);
			if(planTime.equals(firstTimeReverse)) {
				break;
			}
		}
		int busNumberReverse=busNumberMap.get(directionReverse);
		if(busNumberReverse!=0) {//副站设置出车数
			if(busNumberReverse<=addTripBusNumber) {//凸头车少于对面设置出车数
				List<Trip> tripList=routeSchedule.getTripList(startDirection);
				for(int i=0;i<busNumberReverse;i++) {
					Trip trip=tripList.get(0);
					List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
					busTripList.remove(trip);
					tripList.remove(trip);
					if(busTripList.isEmpty()) {//对面出车足够，不用凸头
						routeSchedule.reset();
						initFirstTrip(Direction.UP.getValue());
						initFirstTrip(Direction.DOWN.getValue());
					}
				}
			}else {
				routeSchedule.reset();
				initFirstTrip(Direction.UP.getValue());
				initFirstTrip(Direction.DOWN.getValue());
			}
		}
	}
	
	/**
	 * 按对面来车初始化发班时间
	 * @param direction
	 */
	private void initFirstTrip(int direction) {
		int directionReverse=1-direction;
		Date firstTimeReverse=scheduleParam.getFirstTime(directionReverse);
		Date firstBusReverseObuTimeNext=scheduleParam.getMinObuTimeNext(directionReverse, firstTimeReverse);//对面头车过来发班时间
		Date firstTime=scheduleParam.getFirstTime(direction);
		int busNumber=busNumberMap.get(direction);
		Date planTime=firstTime;
		for(int i=0;i<busNumber;i++) {
			int startOrderNumber=routeSchedule.newBusOrder(direction);
			Bus bus=new Bus(direction, startOrderNumber);
			Trip trip=new Trip(bus, direction, planTime, scheduleParam, null);
			routeSchedule.addTrip(trip);
			double intervalAvg=DateUtil.getMinuteInterval(planTime, firstBusReverseObuTimeNext)*1.0/(busNumber-i);
			planTime=planTimeAdjust(planTime, intervalAvg);
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
	
	private int getBusNumberMorning(int direction) {
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
	
}
