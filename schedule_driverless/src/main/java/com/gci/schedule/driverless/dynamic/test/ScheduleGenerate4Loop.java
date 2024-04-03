package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ServiceType;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.util.MinObuTimeComparator;

import java.util.*;

/**
 * 双环线排班
 * @author lindeyong
 *
 */
public class ScheduleGenerate4Loop {
	
	protected ScheduleParam scheduleParam;
	
	protected RouteSchedule routeSchedule;
	
	private Map<Integer,List<ScheduleHalfHour>> firstRoundScheduleHalfHourMap=new HashMap<Integer, List<ScheduleHalfHour>>();

	protected Map<Integer, Integer> firstRoundBusNumberMap=new HashMap<Integer, Integer>();
	
	protected Map<Integer, List<Plan>> planMap=new HashMap<Integer, List<Plan>>();
	
	private Map<Integer, List<Trip>> singleBusRecoveryListMap=new HashMap<Integer, List<Trip>>();//单班车中停队列，key：下午需要运营班次数
	
	protected Map<Integer, Date> singleBusRecoveryTimeMap=new HashMap<Integer, Date>();//单班车复行时间，key：下午需要运营班次数
	
	public ScheduleGenerate4Loop(ScheduleParam scheduleParam) {
		this.scheduleParam = scheduleParam;
		Integer busNumberPresetUp=scheduleParam.getBusNumberPresetUp();
		Integer busNumberPresetDown=scheduleParam.getBusNumberPresetDown();
		if(busNumberPresetUp!=null&&busNumberPresetDown!=null) {
			scheduleParam.setBusNumberPreset(busNumberPresetUp+busNumberPresetDown);
			Integer busNumberSinglePresetUp=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
			Integer busNumberSinglePresetDown=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
			scheduleParam.setBusNumberSingle(busNumberSinglePresetUp+busNumberSinglePresetDown);
			scheduleParam.setBusNumberSinglePreset(busNumberSinglePresetUp+busNumberSinglePresetDown);
		}
	}

	public RouteSchedule generate() {
		planMap.put(Direction.UP.getValue(), new ArrayList<Plan>());
		planMap.put(Direction.DOWN.getValue(), new ArrayList<Plan>());
		this.routeSchedule=new RouteSchedule(scheduleParam);
		scheduleParam.generateObuTime();
		if(scheduleParam.getBusNumberPreset()==null)
			routeSchedule.calculateBusConfigNew(scheduleParam);
		int wasteTimeAvg=getWasteTimeHighAvg();
		scheduleParam.initSingleBusTripNumber(wasteTimeAvg);
		procFirstRound4Double();
		initSingleBus();
		procArrivalPlan();
		return routeSchedule;
	}
	
	protected void procArrivalPlan() {
		while(true) {
			Plan planUp=getPlanNext(Direction.UP.getValue());
			Plan planDown=getPlanNext(Direction.DOWN.getValue());
			if(planUp==null&&planDown==null)
				return;
			int direction=Direction.UP.getValue();
			Plan plan=planUp;
			if(planUp==null||(planDown!=null&&planDown.getPlanTime().before(planUp.getPlanTime()))) {
				direction=Direction.DOWN.getValue();
				plan=planDown;
			}
			if(DateUtil.getDateHM("1100").equals(plan.getPlanTime()))
				System.out.println("aaa");
			if(isTimeToAdjust(plan)) {//整点调整班次
				if(DateUtil.getDateHM("0700").equals(plan.getPlanTime())) {
					System.out.println("aaa");
					//break;
				}
				classNumberAdjust(plan.getPlanTime());
				proc4LastRound(plan.getPlanTime());
			}
			plan=getPlanNext(direction);
			if(DateUtil.getDateHM("2200").equals(plan.getPlanTime()))
				System.out.println("aaa");
			Trip tripArrival=getTripArrivalRun(direction);
			Trip tripArrivalReverse=getTripArrivalRun(1-direction);
			if(tripArrival==null||tripArrivalReverse==null)
				System.out.println("aaa");
			if(tripArrival==null) {
				tripArrival=tripArrivalReverse;
			}else if(tripArrivalReverse!=null&&
				tripArrival.getNextObuTimeMin()!=null&&
				tripArrivalReverse.getNextObuTimeMin()!=null) {//首轮车已发完
				List<Trip> tripArrivalList=getTripArrivalList();
				tripArrival=tripArrivalList.get(0);
				Plan planReverse=getPlanNext(1-direction);
				if(tripArrival.getDirection()!=plan.getDirection()) {//方向不一致
					if(planReverse!=null&&plan.getPlanTime().equals(planReverse.getPlanTime())) {//
						Trip tripArrivalNext=tripArrivalList.get(1);
						if(tripArrivalNext.getDirection()==plan.getDirection()) {//下台车方向一致
							tripArrival=tripArrivalNext;
						}
					}
				}
			}
			Trip lastTripFull=routeSchedule.getTripFullLast(direction);
			Bus bus=tripArrival.getBus();
			if(bus.isHasMiddleStop()&&tripArrival.getTripBeginTime()==null) {
				addMiddleStopTrip(bus, plan.getPlanTime());
			}
			routeSchedule.setEaten(bus, tripArrival, plan.getPlanTime(), scheduleParam);
			Trip trip=new Trip(bus, direction, plan.getPlanTime(), scheduleParam, lastTripFull);
			addTrip(trip, tripArrival);
			removePlan(direction, plan);
			singleBusMiddleStop(trip);
			singleBusRecovery();
		}
	}
	
	/**
	 * 单班车中停处理
	 * @param trip
	 */
	protected void singleBusMiddleStop(Trip trip) {
		Bus bus=trip.getBus();
		if(trip.isQuitMark()&&bus.isSingleClass()&&!bus.isHasMiddleStop()){
			List<Trip> singleBusRecoveryList=singleBusRecoveryListMap.get(bus.getSingleBusTripNumberEvening());
			if(singleBusRecoveryList==null) {
				singleBusRecoveryList=new ArrayList<Trip>();
				singleBusRecoveryListMap.put(bus.getSingleBusTripNumberEvening(), singleBusRecoveryList);
			}
			singleBusRecoveryList.add(trip);
		}
	}
	
	protected void proc4LastRound(Date hour) {
		
	}
	
	protected void addTrip(Trip trip,Trip tripArrival) {
		routeSchedule.addTrip(trip);
		routeSchedule.removeTripArrival(tripArrival);
	}
	
	protected boolean isTimeToAdjust(Plan plan) {
		if(plan.getDirection()==Direction.UP.getValue()&&
				DateUtil.isWholeHour(plan.getPlanTime())) {
			return true;
		}
		return false;
	}
	
	private void addMiddleStopTrip(Bus bus,Date planTimeNext) {
		List<Trip> busTripList=routeSchedule.getTripList(bus);
		Trip busTripLast=busTripList.get(busTripList.size()-1);
		Trip trip=new Trip(bus, Direction.NODIRECTION.getValue(), DateUtil.getDateAddMinute(busTripLast.getTripEndTime(), 5));
		trip.setTripEndTime(DateUtil.getDateAddMinute(planTimeNext, -5));
		trip.setServiceType(ServiceType.SINGLE_CLASSES_MIDDLE_STOP);
		busTripList.add(trip);
	}
	
	private void singleBusRecovery() {
		Date singleBusRecoveryTime=null;
		List<Trip> singleBusRecoveryList=null;
		for(Integer runTripNumberEvening:singleBusRecoveryListMap.keySet()) {
			List<Trip> singleBusRecoveryListTemp=singleBusRecoveryListMap.get(runTripNumberEvening);
			if(!singleBusRecoveryListTemp.isEmpty()) {
				Date singleBusRecoveryTimeTemp=singleBusRecoveryTimeMap.get(runTripNumberEvening);
				if(singleBusRecoveryTime==null||singleBusRecoveryTimeTemp.before(singleBusRecoveryTime)) {
					singleBusRecoveryTime=singleBusRecoveryTimeTemp;
					singleBusRecoveryList=singleBusRecoveryListTemp;
				}
			}
		}
		if(singleBusRecoveryTime!=null) {
			List<Trip> tripArrivalList=getTripArrivalList();
			Trip tripArrivalLast=tripArrivalList.get(tripArrivalList.size()-1);
			if(!tripArrivalLast.getBus().isSingleClass()&&
				!tripArrivalLast.getNextObuTimeMin().before(singleBusRecoveryTime)) {
				Bus bus=singleBusRecoveryList.get(0).getBus();
				Trip trip=new Trip(bus, bus.getStartDirection(), null);
				trip.setTripEndTime(DateUtil.getDateAddMinute(tripArrivalLast.getTripEndTime(),1));
				trip.setNextObuTimeMin(DateUtil.getDateAddMinute(tripArrivalLast.getNextObuTimeMin(), 1));
				List<Trip> busQueue=routeSchedule.getBusQueue(bus.getStartDirection());
				busQueue.add(trip);//把车加入待发车队列
				bus.setHasMiddleStop(true);
				singleBusRecoveryList.remove(0);
			}
		}
		
		
	}
	
	private Map<Integer, Integer> getfirstRoundBusNumberMap(){
		Map<Integer, Integer> firstRoundBusNumberMap=new HashMap<Integer, Integer>();
		for(Integer direction:planMap.keySet()) {
			List<Trip> busQueue=routeSchedule.getBusQueue(direction);
			int firstRoundBusNumber=0;
			for(Trip tripArrival:busQueue) {
				if(!tripArrival.isQuitMark()&&
					tripArrival.getNextObuTimeMin()==null) {
					firstRoundBusNumber++;
				}
			}
			firstRoundBusNumberMap.put(direction, firstRoundBusNumber);
		}
		return firstRoundBusNumberMap;
	}
	
	private List<Trip> getTripArrivalList(){
		List<Trip> tripArrivalList=new ArrayList<Trip>();
		List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
		List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		List<Trip> busQueue=new ArrayList<Trip>();
		busQueue.addAll(busQueueUp);
		busQueue.addAll(busQueueDown);
		for(Trip tripArrival:busQueue) {
			if(!tripArrival.isQuitMark()&&
				tripArrival.getNextObuTimeMin()!=null) {
				tripArrivalList.add(tripArrival);
			}
		}
		Collections.sort(tripArrivalList, new MinObuTimeComparator());
		return tripArrivalList;
	}
	
	private int getBusRunNumber(){
		int busNumber=0;
		List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
		List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		List<Trip> busQueue=new ArrayList<Trip>();
		busQueue.addAll(busQueueUp);
		busQueue.addAll(busQueueDown);
		for(Trip tripArrival:busQueue) {
			if(!tripArrival.isQuitMark()) {
				busNumber++;
			}
		}
		return busNumber;
	}
	
	private Trip getTripArrivalRun(int direction) {
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		for(int i=0;i<busQueue.size();) {
			Trip tripArrival=busQueue.get(i);
			if(tripArrival.isQuitMark()) {
				busQueue.remove(tripArrival);
			}else {
				return tripArrival;
			}
		}
		return null;
	}
	
	/**
	 * 获取到站车计划，排除首轮出车的计划
	 * @return
	 */
	private List<Plan> getPlanArrivalList() {
		List<Plan> planList=new ArrayList<Plan>();
		Map<Integer, Integer> firstRoundBusNumberMap=new HashMap<Integer, Integer>();
		for(Integer direction:planMap.keySet()) {
			int firstRoundBusNumber=0;
			List<Trip> busQueue=routeSchedule.getBusQueue(direction);
			for(Trip tripArrival:busQueue) {
				if(!tripArrival.isQuitMark()) {
					if(tripArrival.getNextObuTimeMin()==null) {
						firstRoundBusNumber++;
					}
				}
			}
			firstRoundBusNumberMap.put(direction, firstRoundBusNumber);
			planList.addAll(planMap.get(direction));
		}
		Collections.sort(planList, new PlanTimeComparator());
		for(Integer direction:firstRoundBusNumberMap.keySet()) {
			int firstRoundBusNumber=firstRoundBusNumberMap.get(direction);
			for(int i=0;i<planList.size();) {
				Plan plan=planList.get(i);
				if(firstRoundBusNumber==0) {
					break;
				}else if(plan.getDirection()==direction){
					planList.remove(plan);
					firstRoundBusNumber--;
				}else {
					i++;
				}
			}
		}
		return planList;
	}
	
	/**
	 * 获取最后的到站车(有对应计划)
	 */
	private Trip getTripArrivalLast(List<Plan> planList,List<Trip> tripArrivalList) {
		Trip tripArrivalLast=null;
		if(planList.size()<tripArrivalList.size()){//剩余计划比车少
			tripArrivalLast=tripArrivalList.get(planList.size()-1);
		}else {
			tripArrivalLast=tripArrivalList.get(tripArrivalList.size()-1);
		}
		return tripArrivalLast;
	}
	
	/**
	 * 获取最后到站车对应的计划
	 */
	private Plan getPlanArrivalLast(List<Plan> planList,List<Trip> tripArrivalList) {
		Plan planLast=null;
		if(planList.size()<tripArrivalList.size()){//剩余计划比车少
			planLast=planList.get(planList.size()-1);
		}else {
			planLast=planList.get(tripArrivalList.size()-1);
		}
		return planLast;
	}
	/**
	 * 
	 * 时段发班数调节
	 * @param hour
	 */
	protected void classNumberAdjust(Date hour) {
		List<Trip> tripArrivalList=getTripArrivalList();
		List<Plan> planList=getPlanArrivalList();
		if(tripArrivalList.isEmpty())//全都是首轮车
			return;
		Trip tripArrivalLast=getTripArrivalLast(planList, tripArrivalList);
		Plan planLast=getPlanArrivalLast(planList, tripArrivalList);
		if(isBusCatchUpPlan(hour, tripArrivalList, planList)) {
			if(tripArrivalLast.getTripEndTime()!=null&&
				tripArrivalLast.getNextObuTimeMin().before(planLast.getPlanTime())) {
				int restTime=DateUtil.getMinuteInterval(tripArrivalLast.getTripEndTime(), planLast.getPlanTime());
				if(restTime>scheduleParam.getMaxRestTime(tripArrivalLast.getTripEndTime(), tripArrivalLast.getDirection())) {//停站过长
					classNumberIncreaseAdjust(hour);//增加班次，减少停站
				}
			}
		}else {//车位紧张，减少班次
			classNumberReduceAdjust(hour);
		}
	}
	
	/**
	 * 减少时段班次
	 * @param hour
	 */
	private void classNumberReduceAdjust(Date hour) {
		List<Trip> tripArrivalList=getTripArrivalList();
		if(tripArrivalList.isEmpty())//全都是首轮车
			return;
		List<Plan> planList=getPlanArrivalList();
		Plan planLast=getPlanArrivalLast(planList, tripArrivalList);
		Date endTime=DateUtil.getDateAddMinute(hour, 60);
		boolean adjust=true;
		if(isBusCatchUpPlan(hour, tripArrivalList, planList))
			adjust=false;
		if(adjust) {	
			Date runTime=hour;
			List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
			while(runTime.before(planLast.getPlanTime())) {
				for(Integer direction:planMap.keySet()) {
					if(planMap.get(direction).isEmpty())
						continue;
					ScheduleHalfHour scheduleHalfHour=scheduleParam.getScheduleHalfHour(runTime, direction);
					if(scheduleHalfHour!=null) {
						scheduleHalfHourList.add(scheduleHalfHour);
						if(scheduleHalfHour.getNextScheduleHalfHour()!=null) {
							scheduleHalfHourList.add(scheduleHalfHour.getNextScheduleHalfHour());
						}
					}
				}
				runTime=DateUtil.getDateAddMinute(runTime, 60);
			}
			List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(scheduleHalfHourList);
			ScheduleHalfHour lowScheduleHalfHour=getLowScheduleHalfHour(highScheduleHalfHourList);
			if(lowScheduleHalfHour==null) {//不能保证半小时一班
				intervalAdjustGreaterThan30Minute(hour);
				return;
			}
			DispatchRule dispatchRule=scheduleParam.getDispatchRule(lowScheduleHalfHour.getRunTime(), lowScheduleHalfHour.getDirection());
			int classesNumberOrig=dispatchRule.getClassesNumber(lowScheduleHalfHour.getRunTime());
			if(classesNumberOrig==1)
				System.out.println("aaa");
			dispatchRule.setClassesNumber(lowScheduleHalfHour.getRunTime(), classesNumberOrig-1);
			
			ScheduleHalfHour firstHalfScheduleHalfHour=dispatchRule.getFirstHalfScheduleHalfHour();
			if(firstHalfScheduleHalfHour!=null) {
				List<Date> obuTimeList=dispatchRule.getObuTimeList(firstHalfScheduleHalfHour);//重新计划班次计划
				firstHalfScheduleHalfHour.setObuTimeList(obuTimeList);
			}
			ScheduleHalfHour lastHalfScheduleHalfHour=dispatchRule.getLastHalfScheduleHalfHour();
			if(lastHalfScheduleHalfHour!=null) {
				List<Date> obuTimeList=dispatchRule.getObuTimeList(lastHalfScheduleHalfHour);//重新计划班次计划
				lastHalfScheduleHalfHour.setObuTimeList(obuTimeList);
			}
			Date beginTime=firstHalfScheduleHalfHour.getRunTimeDate();
			endTime=DateUtil.getDateAddMinute(firstHalfScheduleHalfHour.getRunTimeDate(), 60);
			for(Integer direction:planMap.keySet()) {
				if(direction==lowScheduleHalfHour.getDirection()) {
					planList=planMap.get(direction);
					for(int i=0;i<planList.size();) {//清除时段计划
						Plan plan=planList.get(i);
						if(DateUtil.isInTimeRange(plan.getPlanTime(), beginTime, endTime)) {
							planList.remove(plan);
						}else {
							Date latestTime=scheduleParam.getLatestTime(plan.getDirection());
							if(endTime.equals(latestTime)&&plan.getPlanTime().equals(latestTime)) {//末班车是整点，删除，避免插入新计划时重复
								planList.remove(plan);
							}else {
								i++;
							}
						}
					}
					planList.addAll(firstHalfScheduleHalfHour.getPlanList());
					if(lastHalfScheduleHalfHour!=null)
						planList.addAll(lastHalfScheduleHalfHour.getPlanList());
					Collections.sort(planList, new PlanTimeComparator());
				}
			}
			classNumberReduceAdjust(hour);
		}
	}
	
	private ScheduleHalfHour getLowScheduleHalfHour(List<ScheduleHalfHour> highScheduleHalfHourList) {
		ScheduleHalfHour lowScheduleHalfHour=null;
		for(int i=highScheduleHalfHourList.size()-1;i>=0;i--) {
			ScheduleHalfHour scheduleHalfHour=highScheduleHalfHourList.get(i);
			DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
			if((dispatchRule.getFirstHalfScheduleHalfHour()==scheduleHalfHour&&
					dispatchRule.getFirstHalfClassesNumber()>dispatchRule.getFirstHalfMinClassesNumber())||
					(dispatchRule.getLastHalfScheduleHalfHour()==scheduleHalfHour&&
					dispatchRule.getLastHalfClassesNumber()>dispatchRule.getLastHalfMinClassesNumber())) {
				lowScheduleHalfHour=scheduleHalfHour;//保证最低发班要求
				break;
			}
		}
		if(lowScheduleHalfHour==null) {
			for(int i=0;i<highScheduleHalfHourList.size();i++) {
				ScheduleHalfHour scheduleHalfHour=highScheduleHalfHourList.get(i);
				if(lowScheduleHalfHour==null||
					DateUtil.getMinuteInterval(scheduleHalfHour.getRunTimeDate(), scheduleHalfHour.getRunTimeEndDate())/scheduleHalfHour.getObuTimeList().size()<
					DateUtil.getMinuteInterval(lowScheduleHalfHour.getRunTimeDate(), lowScheduleHalfHour.getRunTimeEndDate())/lowScheduleHalfHour.getObuTimeList().size()) {
					DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
					int classesNumberOrig=dispatchRule.getClassesNumber(scheduleHalfHour.getRunTime());
					if(classesNumberOrig>1) {
						lowScheduleHalfHour=scheduleHalfHour;
					}
				}
			}
		}
		return lowScheduleHalfHour;
	}
	
	private void intervalAdjustGreaterThan30Minute(Date hour) {
		Date obuTimeNext=null;
		for(int direction:planMap.keySet()) {
			//Date obuTimeNextTemp=scheduleParam.getObuTimeNextRound(direction, hour);
			Date arrivalTime=scheduleParam.getArrivalTime(hour, direction);
			int restTime=scheduleParam.getMinRestTime(direction, hour, arrivalTime);
			if(scheduleParam.getLunchBeginTime(direction)!=null&&
				DateUtil.isInTimeRange(scheduleParam.getLunchBeginTime(direction), hour, arrivalTime)) {
				restTime=scheduleParam.getLunchEatTime(direction);
			}else if(scheduleParam.getSupperBeginTime(direction)!=null&&
				DateUtil.isInTimeRange(scheduleParam.getSupperBeginTime(direction), hour, arrivalTime)) {
				restTime=scheduleParam.getSupperEatTime(direction);
			}
			Date obuTimeNextTemp=DateUtil.getDateAddMinute(arrivalTime, restTime);
			if(obuTimeNext==null||obuTimeNextTemp.after(obuTimeNext)) {
				obuTimeNext=obuTimeNextTemp;
			}
		}
		int busNumber=getBusRunNumber();
		int intervalAvg=DateUtil.getMinuteInterval(hour, obuTimeNext)/busNumber/planMap.size();
		int[] intervalArray=new int[] {30,40,45,60,90,120};
		Integer interval=null;
		for(int intervalTemp:intervalArray) {
			if(intervalTemp>=intervalAvg) {
				interval=intervalTemp;
				break;
			}
		}
		boolean adjust2Latest=false;
		Map<Integer, List<Date>> adjustPlanTimeMap=new HashMap<Integer, List<Date>>();
		for(int direction:planMap.keySet()) {
			List<Date> planTimeList=new ArrayList<Date>();
			for(int i=0;i<100;i++) {
				Date planTime=DateUtil.getDateAddMinute(hour, i*interval);
				planTimeList.add(planTime);
				if(i!=0&&DateUtil.isWholeHour(planTime)) {//回到整点
					break;
				}
				if(planTime.after(scheduleParam.getLatestTime(direction))) {
					adjust2Latest=true;
				}
			}
			if(DateUtil.getMinuteInterval(planTimeList.get(planTimeList.size()-1), scheduleParam.getLatestTime(direction))<
				DateUtil.getMinuteInterval(hour, planTimeList.get(planTimeList.size()-1))) {//调整完后距离末班时间太近
				adjust2Latest=true;
			}
			if(adjust2Latest) {
				break;
			}
			adjustPlanTimeMap.put(direction, planTimeList);
		}
		if(!adjust2Latest) {
			for(int direction:adjustPlanTimeMap.keySet()) {
				List<Plan> planList=planMap.get(direction);
				List<Date> adjustPlanTimeList=adjustPlanTimeMap.get(direction);
				int fromIndex=0;
				for(int i=0;i<planList.size();i++) {
					if(planList.get(i).getPlanTime().equals(adjustPlanTimeList.get(adjustPlanTimeList.size()-1))) {
						fromIndex=i;
						break;
					}
				}
				planList=planList.subList(fromIndex+1, planList.size());
				List<Plan> adjustPlanList=new ArrayList<Plan>();
				for(Date planTime:adjustPlanTimeList) {
					Plan plan=new Plan(planTime, direction);
					adjustPlanList.add(plan);
				}
				planList.addAll(0,adjustPlanList);
				System.out.println("aaa");
				planMap.put(direction, planList);
			}
		}else {
			adjustPlanTimeMap=new HashMap<Integer, List<Date>>();
			for(int direction:planMap.keySet()) {
				Date latestTime=scheduleParam.getLatestTime(direction);
				int planNumber=DateUtil.getMinuteInterval(hour, latestTime)/intervalAvg;
				List<Date> adjustPlanTimeList=new ArrayList<Date>();
				adjustPlanTimeList.add(hour);
				Date planTimeLast=hour;
				while(planNumber>0) {
					interval=DateUtil.getMinuteInterval(planTimeLast, latestTime)/planNumber;
					Date planTime=DateUtil.getDateAddMinute(planTimeLast, interval);
					adjustPlanTimeList.add(planTime);
					planTimeLast=planTime;
					planNumber--;
				}
				adjustPlanTimeMap.put(direction, adjustPlanTimeList);
			}
			for(int direction:adjustPlanTimeMap.keySet()) {
				List<Date> adjustPlanTimeList=adjustPlanTimeMap.get(direction);
				List<Plan> planList=new ArrayList<Plan>();
				for(Date planTime:adjustPlanTimeList) {
					Plan plan=new Plan(planTime, direction);
					planList.add(plan);
				}
				planMap.put(direction, planList);
			}
		}
	}
	
	
	private void intervalAdjustGreaterThan30Minute4FirstRound(int direction,int busNumber) {
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date arrivalTime=scheduleParam.getArrivalTime(firstTime, direction);
		int restTime=scheduleParam.getMinRestTime(direction, firstTime, arrivalTime);
		Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTime, restTime);
		int intervalAvg=DateUtil.getMinuteInterval(firstTime, obuTimeNext)/busNumber;
		List<Date> adjustPlanTimeList=new ArrayList<Date>();
		if(DateUtil.isWholeHour(firstTime)) {//首班车整点
			int[] intervalArray=new int[] {40,45,60};
			Integer interval=null;
			for(int intervalTemp:intervalArray) {
				if(intervalTemp>=intervalAvg) {
					interval=intervalTemp;
					break;
				}
			}
			for(int i=0;i<100;i++) {
				Date planTime=DateUtil.getDateAddMinute(firstTime, i*interval);
				adjustPlanTimeList.add(planTime);
				if(i!=0&&DateUtil.isWholeHour(planTime)) {//回到整点
					break;
				}
			}
		}else {//首班车非整点
			int minute=60-DateUtil.getMinuteOfHour(firstTime);
			Double planNumber=null;
			int hour=0;
			for(int i=0;i<3;i++) {//三个小时内会回到整点，看哪个时间比较合适
				Date endTime=DateUtil.getDateAddMinute(firstTime, minute+(i+1)*60);
				if(i>0&&endTime.after(scheduleParam.getEarlyPeakEndTime())) {//以早高峰结束为节点
					break;
				}
				double planNumberTemp=(minute+(i+1)*60)*1.0/intervalAvg;
				if(planNumber==null) {
					planNumber=planNumberTemp;
					hour=i+1;
				}else {
					
					if((planNumberTemp-Math.floor(planNumberTemp))/Math.floor(planNumberTemp)<
						(planNumber-Math.floor(planNumber))/Math.floor(planNumber)) {
						hour=i+1;
						planNumber=planNumberTemp;
					}
				}
			}
			minute=minute+hour*60;
			Date endTime=DateUtil.getDateAddMinute(firstTime, minute);
			int planNumberInt=minute/intervalAvg;
			adjustPlanTimeList=initPlanTimeFirstRound(direction, planNumberInt, endTime);
			for(int i=1;i<10;i++) {//尝试加密班次
				List<Date> adjustPlanTimeListTemp=initPlanTimeFirstRound(direction, planNumberInt+i, endTime);
				if(checkPlanTimeFirstRound(direction, busNumber, adjustPlanTimeListTemp)) {
					adjustPlanTimeList=adjustPlanTimeListTemp;
				}else {
					break;
				}
			}
		}
		int fromIndex=0;
		List<Plan> planList=planMap.get(direction);
		for(int i=0;i<planList.size();i++) {
			if(planList.get(i).getPlanTime().equals(adjustPlanTimeList.get(adjustPlanTimeList.size()-1))) {
				fromIndex=i;
				break;
			}
		}
		planList=planList.subList(fromIndex+1, planList.size());
		List<Plan> adjustPlanList=new ArrayList<Plan>();
		for(Date planTime:adjustPlanTimeList) {
			Plan plan=new Plan(planTime, direction);
			adjustPlanList.add(plan);
		}
		planList.addAll(0,adjustPlanList);
		planMap.put(direction, planList);
	}
	
	private List<Date> initPlanTimeFirstRound(int direction,int planNumber,Date endTime){
		List<Date> adjustPlanTimeList=new ArrayList<Date>();
		Date firstTime=scheduleParam.getFirstTime(direction);
		adjustPlanTimeList.add(firstTime);
		Date planTimeLast=firstTime;
		while(planNumber>0) {
			int interval=DateUtil.getMinuteInterval(planTimeLast, endTime)/planNumber;
			Date planTime=DateUtil.getDateAddMinute(planTimeLast, interval);
			adjustPlanTimeList.add(planTime);
			planNumber--;
			planTimeLast=planTime;
		}
		return adjustPlanTimeList;
	}
	
	private boolean checkPlanTimeFirstRound(int direction,int busNumber,List<Date> planTimeList) {
		int round=(int)Math.ceil(planTimeList.size()*1.0/busNumber);
		boolean match=true;
		for(int i=0;i<busNumber;i++) {
			List<Date> busPlanTimeList=new ArrayList<Date>();
			for(int j=0;j<round;j++) {
				int index=j*busNumber+i;
				if(index<planTimeList.size()) {
					busPlanTimeList.add(planTimeList.get(index));
				}
			}
			for(int j=1;j<busPlanTimeList.size();j++) {
				Date planTime=busPlanTimeList.get(j);
				Date planTimePre=busPlanTimeList.get(j-1);
				Date minObuTimeNext=scheduleParam.getMinObuTimeNext(direction, planTimePre);
				if(planTime.before(minObuTimeNext)) {
					match=false;
					break;
				}
			}
			if(!match) {
				break;
			}
		}
		return match;
	}
	
	
	protected List<ScheduleHalfHour> getScheduleHalfHourList(Date hour) {
		ScheduleHalfHour scheduleHalfHourUp=scheduleParam.getScheduleHalfHour(hour, Direction.UP.getValue());
		ScheduleHalfHour scheduleHalfHourDown=scheduleParam.getScheduleHalfHour(hour, Direction.DOWN.getValue());
		List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		if(scheduleHalfHourUp!=null){
			scheduleHalfHourList.add(scheduleHalfHourUp);
			if(scheduleHalfHourUp.getNextScheduleHalfHour()!=null) {
				scheduleHalfHourList.add(scheduleHalfHourUp.getNextScheduleHalfHour());
			}
		}
		if(scheduleHalfHourDown!=null) {
			scheduleHalfHourList.add(scheduleHalfHourDown);
			if(scheduleHalfHourDown.getNextScheduleHalfHour()!=null) {
				scheduleHalfHourList.add(scheduleHalfHourDown.getNextScheduleHalfHour());
			}
		}
		return scheduleHalfHourList;
	}
	
	/**
	 * 增加时段班次
	 * @param hour
	 */
	private void classNumberIncreaseAdjust(Date hour) {
		List<Trip> tripArrivalList=getTripArrivalList();
		if(tripArrivalList.isEmpty())
			return;
		List<Plan> planList=getPlanArrivalList();
		Map<Integer, Integer> firstRoundBusNumberMap=getfirstRoundBusNumberMap();
		Trip tripArrivalLast=getTripArrivalLast(planList, tripArrivalList);
		Plan planLast=getPlanArrivalLast(planList, tripArrivalList);
		if(tripArrivalLast.getTripEndTime()!=null&&
			tripArrivalLast.getTripEndTime().before(planLast.getPlanTime())) {
			int restTime=DateUtil.getMinuteInterval(tripArrivalLast.getTripEndTime(), planLast.getPlanTime());
			if(restTime>scheduleParam.getMaxRestTime(tripArrivalLast.getTripEndTime(), tripArrivalLast.getDirection())) {//停站过长
				List<ScheduleHalfHour> scheduleHalfHourList=getScheduleHalfHourList(hour);
				if(scheduleHalfHourList.isEmpty())
					return;
				List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(scheduleHalfHourList);
				ScheduleHalfHour highScheduleHalfHour=highScheduleHalfHourList.get(0);
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(highScheduleHalfHour.getRunTime(), highScheduleHalfHour.getDirection());
				Map<ScheduleHalfHour, Integer> classesNumberOrigMap=new HashMap<ScheduleHalfHour, Integer>();
				ScheduleHalfHour firstHalfScheduleHalfHour=dispatchRule.getFirstHalfScheduleHalfHour();
				if(firstHalfScheduleHalfHour!=null) {
					classesNumberOrigMap.put(firstHalfScheduleHalfHour, dispatchRule.getFirstHalfClassesNumber());
				}
				ScheduleHalfHour lastHalfScheduleHalfHour=dispatchRule.getLastHalfScheduleHalfHour();
				if(lastHalfScheduleHalfHour!=null) {
					classesNumberOrigMap.put(lastHalfScheduleHalfHour, dispatchRule.getLastHalfClassesNumber());
				}
				int classesNumberOrig=dispatchRule.getClassesNumber(highScheduleHalfHour.getRunTime());
				dispatchRule.setClassesNumber(highScheduleHalfHour.getRunTime(), classesNumberOrig+1);//加一班车
				if(firstHalfScheduleHalfHour!=null) {
					List<Date> obuTimeList=dispatchRule.getObuTimeList(firstHalfScheduleHalfHour);//重新计划班次计划
					firstHalfScheduleHalfHour.setObuTimeList(obuTimeList);
				}
				if(lastHalfScheduleHalfHour!=null) {
					List<Date> obuTimeList=dispatchRule.getObuTimeList(lastHalfScheduleHalfHour);//重新计划班次计划
					lastHalfScheduleHalfHour.setObuTimeList(obuTimeList);
				}
				Date endTime=DateUtil.getDateAddMinute(hour, 60);
				for(int i=0;i<planList.size();) {//清除时段计划
					Plan plan=planList.get(i);
					if(DateUtil.isInTimeRange(plan.getPlanTime(), hour, endTime)) {
						planList.remove(plan);
					}else {
						Date latestTime=scheduleParam.getLatestTime(plan.getDirection());
						if(endTime.equals(latestTime)&&plan.getPlanTime().equals(latestTime)) {//末班车是整点，删除，避免插入新计划时重复
							planList.remove(plan);
						}else {
							i++;
						}
					}
				}
				for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
					planList.addAll(scheduleHalfHour.getPlanList());
				}
				Collections.sort(planList, new PlanTimeComparator());
				for(Integer direction:firstRoundBusNumberMap.keySet()) {
					int firstRoundBusNumber=firstRoundBusNumberMap.get(direction);
					for(int i=0;i<planList.size();) {
						Plan plan=planList.get(i);
						if(firstRoundBusNumber==0) {
							break;
						}else if(plan.getDirection()==direction){
							planList.remove(plan);
							firstRoundBusNumber--;
						}else {
							i++;
						}
					}
				}
				boolean recovery=false;
				if(!checkTripArrivalAndPlan(tripArrivalList,planList))
					recovery=true;
				if(!recovery) {
					if(planList.size()<tripArrivalList.size()){//剩余计划比车少
						if(planList.isEmpty())
							System.out.println("aaa");
						tripArrivalLast=tripArrivalList.get(planList.size()-1);
						planLast=planList.get(planList.size()-1);
					}else {
						tripArrivalLast=tripArrivalList.get(tripArrivalList.size()-1);
						planLast=planList.get(tripArrivalList.size()-1);
					}
					if(tripArrivalLast.getTripEndTime()!=null) {
						if(tripArrivalLast.getTripEndTime().before(planLast.getPlanTime())) {
							restTime=DateUtil.getMinuteInterval(tripArrivalLast.getTripEndTime(), planLast.getPlanTime());
							if(restTime<scheduleParam.getRestTime(tripArrivalLast.getTripEndTime(), tripArrivalLast.getDirection())) {
								recovery=true;
							}
						}else {
							recovery=true;
						}
					}
					if(!recovery) {
						if(!isBusCatchUpPlan(hour, tripArrivalList, planList)) {
							recovery=true;
						}
					}
				}
				if(recovery) {
					dispatchRule.setClassesNumber(highScheduleHalfHour.getRunTime(), classesNumberOrig);
					ScheduleHalfHour scheduleHalfHour=dispatchRule.getFirstHalfScheduleHalfHour();
					List<Date> obuTimeList=dispatchRule.getObuTimeList(scheduleHalfHour);//重新计划班次计划
					scheduleHalfHour.setObuTimeList(obuTimeList);
					scheduleHalfHour=dispatchRule.getLastHalfScheduleHalfHour();
					obuTimeList=dispatchRule.getObuTimeList(scheduleHalfHour);//重新计划班次计划
					scheduleHalfHour.setObuTimeList(obuTimeList);
				}else {
					for(Integer direction:planMap.keySet()) {
						planList=planMap.get(direction);
						for(int i=0;i<planList.size();) {
							Plan plan=planList.get(i);
							if(DateUtil.isInTimeRange(plan.getPlanTime(), hour, endTime)) {
								planList.remove(plan);
							}else {
								Date latestTime=scheduleParam.getLatestTime(plan.getDirection());
								if(endTime.equals(latestTime)&&plan.getPlanTime().equals(latestTime)) {//末班车是整点，删除，避免插入新计划时重复
									planList.remove(plan);
								}else {
									i++;
								}
							}
						}
					}
					for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
						planList=planMap.get(scheduleHalfHour.getDirection());
						planList.addAll(scheduleHalfHour.getPlanList());
						Collections.sort(planList, new PlanTimeComparator());
					}
					classNumberIncreaseAdjust(hour);
				}
			}
		}
	}
	
	private boolean checkTripArrivalAndPlan(List<Trip> tripArrivalList,List<Plan> planList) {
		boolean match=true;
		for(int i=0;i<tripArrivalList.size();i++) {
			Trip tripArrival=tripArrivalList.get(i);
			if(i==planList.size()) {//车比计划多
				break;
			}
			Plan plan=planList.get(i);
			if(tripArrival.getNextObuTimeMin()!=null&&
				tripArrival.getNextObuTimeMin().after(plan.getPlanTime())) {
				match=false;
				break;
			}
		}
		return match;
	}
	
	/**
	 * 车辆是否可按计划执行
	 * @param hour
	 * @param tripArrivalList
	 * @param planList
	 * @return
	 */
	private boolean isBusCatchUpPlan(Date hour,List<Trip> tripArrivalList,List<Plan> planList) {
		Plan planLast=getPlanArrivalLast(planList, tripArrivalList);
		Trip tripArrivalLast=tripArrivalList.get(tripArrivalList.size()-1);
		Date endTime=DateUtil.getDateAddMinute(hour, 60);
		boolean adjust=false;
		if(planLast.getPlanTime().after(endTime)) {//计划过了本时段
			if(tripArrivalLast.getTripEndTime()!=null&&
					tripArrivalLast.getNextObuTimeMin().after(planLast.getPlanTime())) {
				adjust=true;
			}
			if(!adjust) {
				List<Plan> hourPlanList=new ArrayList<Plan>();
				for(Plan plan:planList) {
					if(DateUtil.isInTimeRange(plan.getPlanTime(), hour, endTime)||
						plan.getPlanTime().equals(endTime)) {
						hourPlanList.add(plan);
					}
				}
				if(!checkTripArrivalAndPlan(tripArrivalList, hourPlanList)) {//时段有车跟不上
					adjust=true;
				}
			}
		}else {
			if(!checkTripArrivalAndPlan(tripArrivalList, planList)) {//时段有车跟不上
				checkTripArrivalAndPlan(tripArrivalList, planList);
				adjust=true;
			}
			if(!adjust) {
				List<Plan> planSurplusList=new ArrayList<Plan>();//时段剩余班次(含下个时段整点)
				for(int i=tripArrivalList.size();i<planList.size();i++) {
					Plan plan=planList.get(i);
					if(!plan.getPlanTime().after(endTime)) {
						planSurplusList.add(plan);
					}else {
						break;
					}
				}
				List<Plan> planListFirstRound=new ArrayList<Plan>();
				planListFirstRound.addAll(getPlanListFirstRound(Direction.UP.getValue()));
				planListFirstRound.addAll(getPlanListFirstRound(Direction.DOWN.getValue()));
				List<Date> minObuTimeNextList=new ArrayList<Date>();
				for(Plan plan:planListFirstRound) {
					Date minObuTimeNext=getMinObuTimeNext(plan);
					minObuTimeNextList.add(minObuTimeNext);
				}
				for(int i=0;i<tripArrivalList.size()&&i<planList.size();i++) {
					Plan plan=planList.get(i);
					Trip tripArrival=tripArrivalList.get(i);
					Bus bus=tripArrival.getBus();
					if(bus.isSingleClass()) {
						if(isSingleBusQuitAfterNextTrip(bus, plan)) {
							continue;
						}
					}
					Date minObuTimeNext=null;
					if(tripArrival.isEatAfterTrip()) {//本次到站安排吃饭，不用再安排吃饭
						minObuTimeNext=getMinObuTimeNext(plan);
					}else {
						minObuTimeNext=getMinObuTimeNext(bus, plan);
					}
					
					if(minObuTimeNext!=null)
						minObuTimeNextList.add(minObuTimeNext);
				}
				Collections.sort(minObuTimeNextList);
				for(int i=0;i<planSurplusList.size();i++) {
					if(i<minObuTimeNextList.size()) {
						Date minObuTimeNext=minObuTimeNextList.get(i);
						if(minObuTimeNext.after(planSurplusList.get(i).getPlanTime())) {
							adjust=true;
							break;
						}
					}else {
						adjust=true;
						break;
					}
				}
			}	
		}
		if(adjust)
			return false;
		return true;
	}
	
	protected boolean isSingleBusQuitAfterNextTrip(Bus bus, Plan plan) {
		List<Trip> busTripList=routeSchedule.getRunTripList(bus);
		if(!bus.isHasMiddleStop()) {
			if(busTripList.size()+1>=bus.getSingleBusTripNumberMorning()) {
				return true;
			}
		}else {
			if(busTripList.size()+1>=bus.getSingleBusTripNumber()) {
				return true;
			}
			Date arrivalTime=scheduleParam.getArrivalTime(plan.getPlanTime(), plan.getDirection());
			if(arrivalTime==null||arrivalTime.after(scheduleParam.getMiddleStopOffDutyBeginTime())) {
				return true;
			}
		}
		return false;
	}
	
	protected Date getMinObuTimeNext(Plan plan) {
		Date minObuTimeNext=scheduleParam.getMinObuTimeNext(plan.getDirection(), plan.getPlanTime());
		return minObuTimeNext;
	}
	
	protected Date getMinObuTimeNext(Bus bus, Plan plan) {
		Date minObuTimeNext=scheduleParam.getMinObuTimeNext(bus, plan.getDirection(), plan.getPlanTime());
		return minObuTimeNext;
	}
	
	private List<Plan> getPlanListFirstRound(int direction){
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		List<Plan> planList=planMap.get(direction);
		List<Plan> planListFirstRound=new ArrayList<Plan>();
		for(int i=0;i<busQueue.size();i++) {
			Trip tripArrival=busQueue.get(i);
			if(tripArrival.getNextObuTimeMin()!=null)
				break;
			if(planList.isEmpty())
				System.out.println("aaa");
			Plan plan=planList.get(i);
			planListFirstRound.add(plan);
		}
		return planListFirstRound;
	}
	
	private Plan getPlanNext(int direction) {
		List<Plan> planQueue=planMap.get(direction);
		if(planQueue==null||planQueue.isEmpty())
			return null;
		return planQueue.get(0);
	}
	
	private void removePlan(int direction,Plan plan) {
		List<Plan> planQueue=planMap.get(direction);
		planQueue.remove(plan);
	}
	/**
	 * 跟进预设车辆数调整首轮班次
	 */
	protected void firstRoundPlanAdjust(int direction) {
		int busNumber=getFirstRoundBusNumber(direction);
		int busNumberPreset=firstRoundBusNumberMap.get(direction);
		if(busNumber<busNumberPreset) {
			firstRoundPlanAdjustIncrease(direction);
			initPlanQueue(direction);
		}else if(busNumber>busNumberPreset){
			busNumber=getFirstRoundBusNumberMin(direction);
			if(busNumber>busNumberPreset) {
				firstRoundPlanAdjustReduce(direction);
			}
		}
	}
	
	private void firstRoundPlanAdjustIncrease(int direction) {
		int busNumber=getFirstRoundBusNumber(direction);
		int busNumberPreset=firstRoundBusNumberMap.get(direction);
		while(busNumber<busNumberPreset) {
			List<ScheduleHalfHour> firstRoundScheduleHalfHourList=firstRoundScheduleHalfHourMap.get(direction);
			List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(firstRoundScheduleHalfHourList);
			for(int i=0;i<highScheduleHalfHourList.size();i++) {
				ScheduleHalfHour scheduleHalfHour=highScheduleHalfHourList.get(i);
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), direction);
				Integer firstHalfClassesNumberOrgi=dispatchRule.getFirstHalfClassesNumber();
				Integer lastHalfClassesNumberOrgi=dispatchRule.getLastHalfClassesNumber();
				dispatchRule.classesNumberIncrease(scheduleHalfHour.getRunTime());
				dispatchRule.resetScheduleHalfHourObuTimeList();
				int busNumberTemp=getFirstRoundBusNumber(direction);
				if(busNumberTemp<=busNumber) {
					dispatchRule.setFirstHalfClassesNumber(firstHalfClassesNumberOrgi);
					dispatchRule.setLastHalfClassesNumber(lastHalfClassesNumberOrgi);
					dispatchRule.resetScheduleHalfHourObuTimeList();
				}else {
					busNumber=busNumberTemp;
					break;
				}
			}
		}
	}
	
	private void firstRoundPlanAdjustReduce(int direction) {
		int busNumber=getFirstRoundBusNumberMin(direction);//减少班次，按最少停站计算所需车数
		int busNumberPreset=firstRoundBusNumberMap.get(direction);
		boolean breakMinClassesNumber=false;
		while(busNumber>busNumberPreset) {
			List<ScheduleHalfHour> firstRoundScheduleHalfHourList=firstRoundScheduleHalfHourMap.get(direction);
			List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(firstRoundScheduleHalfHourList);
			boolean success=false;
			for(int i=highScheduleHalfHourList.size()-1;i>=0;i--) {
				ScheduleHalfHour scheduleHalfHour=highScheduleHalfHourList.get(i);
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), direction);
				Integer firstHalfClassesNumberOrgi=dispatchRule.getFirstHalfClassesNumber();
				Integer lastHalfClassesNumberOrgi=dispatchRule.getLastHalfClassesNumber();
				if(breakMinClassesNumber||dispatchRule.greaterThanMinClassesNumber(scheduleHalfHour.getRunTime())) {
					if(dispatchRule.getClassesNumber(scheduleHalfHour.getRunTime())>1) {
						dispatchRule.classesNumberReduce(scheduleHalfHour.getRunTime());
						dispatchRule.resetScheduleHalfHourObuTimeList();
						int busNumberTemp=getFirstRoundBusNumberMin(direction);
						if(busNumberTemp>=busNumber) {
							dispatchRule.setFirstHalfClassesNumber(firstHalfClassesNumberOrgi);
							dispatchRule.setLastHalfClassesNumber(lastHalfClassesNumberOrgi);
							dispatchRule.resetScheduleHalfHourObuTimeList();
						}else {
							busNumber=busNumberTemp;
							success=true;
							break;
						}
					}
				}
			}
			if(!success) {//都不行，需要突破最低发班标准
				if(!breakMinClassesNumber) {
					breakMinClassesNumber=true;
				}else {
					break;
				}
			}
		}
		if(busNumber>busNumberPreset) {
			intervalAdjustGreaterThan30Minute4FirstRound(direction, busNumberPreset);
		}else {
			initPlanQueue(direction);
		}
	}
	
	protected void procFirstRound4Double() {
		Integer firstRoundBusNumberUp=null;
		Integer firstRoundBusNumberDown=null;
		if(scheduleParam.getBusNumberPreset()==null) {
			firstRoundBusNumberUp=getFirstRoundBusNumber(Direction.UP.getValue());
			firstRoundBusNumberDown=getFirstRoundBusNumber(Direction.DOWN.getValue());
		}else {
			firstRoundBusNumberUp=scheduleParam.getBusNumberPresetUp();
			if(firstRoundBusNumberUp==0) {
				throw new MyException("-1", "双环线上行预设配车数不能为零");
			}
			firstRoundBusNumberDown=scheduleParam.getBusNumberPresetDown();
			if(firstRoundBusNumberDown==0) {
				throw new MyException("-1", "双环线下行预设配车数不能为零");
			}
		}
		firstRoundBusNumberMap.put(Direction.UP.getValue(), firstRoundBusNumberUp);
		firstRoundBusNumberMap.put(Direction.DOWN.getValue(), firstRoundBusNumberDown);
		if(scheduleParam.getBusNumberPreset()==null) {
			int busNumber=firstRoundBusNumberUp+firstRoundBusNumberDown;
			while(busNumber<scheduleParam.getBusNumberConfig()) {
				firstRoundBusNumberAdjust();
				busNumber=firstRoundBusNumberMap.get(Direction.UP.getValue())+firstRoundBusNumberMap.get(Direction.DOWN.getValue());
			}
		}
		initPlanQueue(Direction.UP.getValue());
		initPlanQueue(Direction.DOWN.getValue());
		if(scheduleParam.getBusNumberPreset()!=null) {
			firstRoundPlanAdjust(Direction.UP.getValue());
			firstRoundPlanAdjust(Direction.DOWN.getValue());
		}
		for(Integer direction:firstRoundBusNumberMap.keySet()) {
			int firstRoundBusNumber=firstRoundBusNumberMap.get(direction);
			List<Trip> busQueue=routeSchedule.getBusQueue(direction);
			for(int i=0;i<firstRoundBusNumber;i++) {
				int startOrderNumber=routeSchedule.newBusOrder(direction);
				Bus bus=new Bus(direction, startOrderNumber);
				Trip trip=new Trip(bus, direction, null);
				busQueue.add(trip);//把车加入待发车队列
			}
		}
	}

	protected void initSingleBus() {
		int wasteTimeAvg=getWasteTimeHighAvg();
		int busNumberSingle=scheduleParam.getBusNumberSingle();
		Integer runTripNumberMorningLast=null;//上一个单班车早上趟次数
		while(busNumberSingle>0) {
			Trip singleBusTrip=null;
			Integer singleBusIndexSeparateUp=getSingleBusIndexSeparate(Direction.UP.getValue());
			Integer singleBusIndexSeparateDown=getSingleBusIndexSeparate(Direction.DOWN.getValue());
			if(singleBusIndexSeparateUp!=null&&singleBusIndexSeparateDown!=null) {//上下行都可以隔开
				Plan planUp=planMap.get(Direction.UP.getValue()).get(singleBusIndexSeparateUp);
				Plan planDown=planMap.get(Direction.DOWN.getValue()).get(singleBusIndexSeparateDown);
				if(planDown.getPlanTime().after(planUp.getPlanTime())) {//晚出的优先做单班
					List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
					singleBusTrip=busQueue.get(singleBusIndexSeparateDown);
				}else {
					List<Trip> busQueue=routeSchedule.getBusQueue(Direction.UP.getValue());
					singleBusTrip=busQueue.get(singleBusIndexSeparateUp);
				}
			}else if(singleBusIndexSeparateUp!=null&&singleBusIndexSeparateDown==null) {
				List<Trip> busQueue=routeSchedule.getBusQueue(Direction.UP.getValue());
				singleBusTrip=busQueue.get(singleBusIndexSeparateUp);
			}else if(singleBusIndexSeparateUp==null&&singleBusIndexSeparateDown!=null) {
				List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
				singleBusTrip=busQueue.get(singleBusIndexSeparateDown);
			}else {
				Integer singleBusIndexSeriesUp=getSingleBusIndexSeries(Direction.UP.getValue());
				Integer singleBusIndexSeriesDown=getSingleBusIndexSeries(Direction.DOWN.getValue());
				if(singleBusIndexSeriesUp!=null&&singleBusIndexSeriesDown!=null) {//
					Plan planUp=planMap.get(Direction.UP.getValue()).get(singleBusIndexSeriesUp);
					Plan planDown=planMap.get(Direction.DOWN.getValue()).get(singleBusIndexSeriesDown);
					if(planDown.getPlanTime().after(planUp.getPlanTime())) {//晚出的优先做单班
						List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
						singleBusTrip=busQueue.get(singleBusIndexSeriesDown);
					}else {
						List<Trip> busQueue=routeSchedule.getBusQueue(Direction.UP.getValue());
						singleBusTrip=busQueue.get(singleBusIndexSeriesUp);
					}
				}else if(singleBusIndexSeriesUp!=null&&singleBusIndexSeriesDown==null) {
					List<Trip> busQueue=routeSchedule.getBusQueue(Direction.UP.getValue());
					singleBusTrip=busQueue.get(singleBusIndexSeriesUp);
				}else if(singleBusIndexSeriesUp==null&&singleBusIndexSeriesDown!=null) {
					List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
					singleBusTrip=busQueue.get(singleBusIndexSeriesDown);
				}
			}
			if(singleBusTrip!=null) {
				Bus singleBus=singleBusTrip.getBus();
				singleBus.setSingleClass(true);
				setSingleBusConf(singleBus, runTripNumberMorningLast, wasteTimeAvg);
				runTripNumberMorningLast=singleBus.getSingleBusTripNumberMorning();
				busNumberSingle--;
			}else {
				break;
			}
		}
	}
	
	protected void setSingleBusConf(Bus singleBus,Integer runTripNumberMorningLast,int wasteTimeAvg) {
		int tripNumber=scheduleParam.getSingleBusTripNumber();
		if(tripNumber%2==0) {//班次数偶数，早晚对半
			singleBus.setSingleBusConf(tripNumber/2, tripNumber/2);
		}else {
			int runTripNumberMorning=tripNumber/2;//早上少一班
			if(runTripNumberMorningLast!=null&&runTripNumberMorningLast==runTripNumberMorning) {
				runTripNumberMorning++;
			}
			singleBus.setSingleBusConf(runTripNumberMorning, tripNumber-runTripNumberMorning);
		}
		int runTripNumberEvening=singleBus.getSingleBusTripNumberEvening();
		Date singleBusRecoveryTime=singleBusRecoveryTimeMap.get(runTripNumberEvening);
		if(singleBusRecoveryTime==null) {
			singleBusRecoveryTime=DateUtil.getDateAddMinute(scheduleParam.getLatePeakEndTime(), -runTripNumberEvening*wasteTimeAvg);
			singleBusRecoveryTimeMap.put(runTripNumberEvening, singleBusRecoveryTime);
		}
	}
	
	/**
	 * 连续车位单班车
	 * @param direction
	 * @return
	 */
	protected Integer getSingleBusIndexSeries(int direction) {
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		Integer singleIndex=null;
		for(int i=busQueue.size()-1;i>=0;i--) {
			Trip trip=busQueue.get(i);
			if(!trip.getBus().isSingleClass()) {//从后找第一台非单班
				singleIndex=i;
				break;
			}
		}
		int beginOrderNumber=scheduleParam.getMiddleStopBeginOrderNumber();
		if(singleIndex!=null&&singleIndex>=beginOrderNumber-1) {
			return singleIndex;
		}
		return null; 
	}
	
	/**
	 * 隔开车位单班车
	 * @param direction
	 * @return
	 */
	protected Integer getSingleBusIndexSeparate(int direction) {
		int beginOrderNumber=scheduleParam.getMiddleStopBeginOrderNumber();
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		Trip tripSingleLast=null;//最早的单班车
		for(int i=busQueue.size()-1;i>=0;i--) {
			Trip trip=busQueue.get(i);
			if(trip.getBus().isSingleClass()) {
				tripSingleLast=trip;
			}
		}
		Integer index=null;
		if(tripSingleLast!=null) {
			index=busQueue.indexOf(tripSingleLast);
			index=index-2;//隔一台
		}else {
			index=busQueue.size()-1;
		}
		if(index>=beginOrderNumber-1) {
			return index;
		}
		return null;
	}
	
	protected void initPlanQueue(int direction) {
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList(direction);
		List<Plan> planQueue=new ArrayList<Plan>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			planQueue.addAll(scheduleHalfHour.getPlanList());
		}
		planMap.put(direction, planQueue);
	}
	
	protected void firstRoundBusNumberAdjust() {
		List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		for(Integer direction:firstRoundScheduleHalfHourMap.keySet()) {
			List<ScheduleHalfHour> firstRoundScheduleHalfHourList=firstRoundScheduleHalfHourMap.get(direction);
			scheduleHalfHourList.addAll(firstRoundScheduleHalfHourList);
		}
		List<ScheduleHalfHour> highScheduleHalfHourList=scheduleParam.highScheduleHalfHourSort(scheduleHalfHourList);
		for(ScheduleHalfHour highScheduleHalfHour:highScheduleHalfHourList) {
			DispatchRule dispatchRule=scheduleParam.getDispatchRule(highScheduleHalfHour.getRunTime(), highScheduleHalfHour.getDirection());
			Map<ScheduleHalfHour, Integer> classesNumberOrigMap=new HashMap<ScheduleHalfHour, Integer>();
			ScheduleHalfHour firstHalfScheduleHalfHour=dispatchRule.getFirstHalfScheduleHalfHour();
			if(firstHalfScheduleHalfHour!=null) {
				classesNumberOrigMap.put(firstHalfScheduleHalfHour, dispatchRule.getFirstHalfClassesNumber());
			}
			ScheduleHalfHour lastHalfScheduleHalfHour=dispatchRule.getLastHalfScheduleHalfHour();
			if(lastHalfScheduleHalfHour!=null) {
				classesNumberOrigMap.put(lastHalfScheduleHalfHour, dispatchRule.getLastHalfClassesNumber());
			}
			int classesNumberOrig=dispatchRule.getClassesNumber(highScheduleHalfHour.getRunTime());
			dispatchRule.setClassesNumber(highScheduleHalfHour.getRunTime(), classesNumberOrig+1);//加一班车
			if(firstHalfScheduleHalfHour!=null) {
				List<Date> obuTimeList=dispatchRule.getObuTimeList(firstHalfScheduleHalfHour);//重新计划班次计划
				firstHalfScheduleHalfHour.setObuTimeList(obuTimeList);
			}
			if(lastHalfScheduleHalfHour!=null) {
				List<Date> obuTimeList=dispatchRule.getObuTimeList(lastHalfScheduleHalfHour);//重新计划班次计划
				lastHalfScheduleHalfHour.setObuTimeList(obuTimeList);
			}
			int busNumber=getFirstRoundBusNumber(highScheduleHalfHour.getDirection());
			int busNumberOrgi=firstRoundBusNumberMap.get(highScheduleHalfHour.getDirection());
			if(busNumber==busNumberOrgi) {//增加班次没增加配车，恢复原来班次
				for(ScheduleHalfHour scheduleHalfHour:classesNumberOrigMap.keySet()) {
					dispatchRule.setClassesNumber(scheduleHalfHour.getRunTime(), classesNumberOrigMap.get(scheduleHalfHour));
				}
				for(ScheduleHalfHour scheduleHalfHour:classesNumberOrigMap.keySet()) {
					List<Date> obuTimeList=dispatchRule.getObuTimeList(scheduleHalfHour);//重新计划班次计划
					scheduleHalfHour.setObuTimeList(obuTimeList);
				}
			}else {
				firstRoundBusNumberMap.put(highScheduleHalfHour.getDirection(), busNumber);
				break;
			}
		}
	}
	
	protected Date getObuTimeNext(int direction,Date obuTime) {
		Date arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);
		int restTime=scheduleParam.getRestTime(arrivalTime, direction);
		Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTimeNext;
	}
	
	private int getFirstRoundBusNumberMin(int direction) {
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date arrivalTime=scheduleParam.getArrivalTime(firstTime, direction);
		int restTime=scheduleParam.getMinRestTime(direction, firstTime, arrivalTime);
		Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTime, restTime);
		int busNumber=getFirstRoundBusNumber(direction,obuTimeNext);
		return busNumber;
	}
	
	private int getFirstRoundBusNumber(int direction,Date obuTimeNext) {
		List<ScheduleHalfHour> firstRoundScheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		int busNumber=0;
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList(direction)) {
			if(!scheduleHalfHour.getRunTimeDate().before(obuTimeNext)) {
				break;
			}
			firstRoundScheduleHalfHourList.add(scheduleHalfHour);
			List<Date> obuTimeList=scheduleHalfHour.getObuTimeList();
			for(Date obuTime:obuTimeList) {
				if(obuTime.before(obuTimeNext)) {
					busNumber++;
				}else {
					break;
				}
			}
		}
		firstRoundScheduleHalfHourMap.put(direction, firstRoundScheduleHalfHourList);
		return busNumber;
	}
	
	protected int getFirstRoundBusNumber(int direction) {
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date obuTimeNext=getObuTimeNext(direction, firstTime);
		int busNumber=getFirstRoundBusNumber(direction,obuTimeNext);
		return busNumber;
	}
	
	/**
	 * 高峰周转时间
	 * @return
	 */
	private int getWasteTimeHighAvg() {
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
			Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(Direction.UP.getValue(), runTime);
			int wasteTime=DateUtil.getMinuteInterval(runTime, obuTimeNextRound);
			wasteTimeSum+=wasteTime;
			obuTimeNextRound=scheduleParam.getObuTimeNextRound(Direction.DOWN.getValue(), runTime);
			wasteTime=DateUtil.getMinuteInterval(runTime, obuTimeNextRound);
			wasteTimeSum+=wasteTime;
		}
		int wasteTimeAvg=wasteTimeSum/highRushTimeList.size()/2;
		return wasteTimeAvg;
	}
}
