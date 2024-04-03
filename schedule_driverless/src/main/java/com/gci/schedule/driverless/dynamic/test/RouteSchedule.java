package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamShift;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;
import com.gci.schedule.driverless.dynamic.util.TripBeginTimeComparator;
import com.gci.schedule.driverless.dynamic.util.TripEndTimeComparator;

import java.util.*;
import java.util.stream.Collectors;

public class RouteSchedule implements Cloneable{
	
	/*private String hour;
	
	private String minute;*/
	
	private String routeName;
	
	private Map<Bus, List<Trip>> tripMap = new HashMap<Bus, List<Trip>>();//车辆发班计划
	
	private ArrayList<Trip> busQueueUp=new ArrayList<Trip>();//上行车队列
	
	private ArrayList<Trip> busQueueDown=new ArrayList<Trip>();//下行车队列
	
	private List<Trip> singleClassQueueUp=new ArrayList<Trip>();//单班车队列上行
	
	private List<Trip> singleClassQueueDown=new ArrayList<Trip>();//单班车队列下行

	private int busOrderUp=0;//上行车序
	
	private int busOrderDown=0;//下行车序
	
	private int maxClassesNumber;//加密后最大发班数
	
	private RouteSchedule routeScheduleAddClassesLastUp;//上一次加密班次时段前的发班计划(上行)
	
	private RouteSchedule routeScheduleAddClassesLastDown;//上一次加密班次时段前的发班计划(下行)
	
	private List<DispatchRule> dispatchRuleList=new ArrayList<DispatchRule>();
	
	private List<Trip> upTripList=new ArrayList<Trip>();
	
	private List<Trip> downTripList=new ArrayList<Trip>();
	
	private ScheduleParam scheduleParam;
	
	private Map<String, Integer> timeBusConfigMap=new HashMap<String, Integer>();
	
	private List<Trip> busQueueShortBackDown=new ArrayList<Trip>();//下行车队列(短线)回头车暂存
	
	private List<Trip> busQueueShortBackUp=new ArrayList<Trip>();//上行车队列(短线)回头车暂存
	
	private Trip latestBusUpLastRound;//上行尾车最后一轮班次
	
	private Trip latestBusDownLastRound;//下行尾车最后一轮班次
	
	private Map<String, Trip> singleBusAdjustMap=new HashMap<String, Trip>();//key连续单班车，Trip双班调位，打散连续单班车
	
	private Map<ScheduleParamShift, List<Bus>> shiftBusUpMap=new HashMap<ScheduleParamShift, List<Bus>>();//班别车辆
	
	private Map<ScheduleParamShift, List<Bus>> shiftBusDownMap=new HashMap<ScheduleParamShift, List<Bus>>();//班别车辆
	
	private ScheduleHalfHour currentScheduleHalfHour;//当前发班时段
	
	public RouteSchedule(ScheduleParam scheduleParam) {
		super();
		this.scheduleParam = scheduleParam;
	}
	
	public void reset() {
		upTripList.clear();
		downTripList.clear();
		busQueueUp.clear();
		busQueueDown.clear();
		busOrderUp=0;
		busOrderDown=0;
		tripMap.clear();
	}
	
	public RouteSchedule(ScheduleParam scheduleParam, int busOrderUp, int busOrderDown) {
		super();
		this.scheduleParam = scheduleParam;
		this.busOrderUp=busOrderUp;
		this.busOrderDown=busOrderDown;
	}

	public ScheduleHalfHour getCurrentScheduleHalfHour() {
		return currentScheduleHalfHour;
	}

	public void setCurrentScheduleHalfHour(ScheduleHalfHour currentScheduleHalfHour) {
		this.currentScheduleHalfHour = currentScheduleHalfHour;
	}

	public Trip getLatestBusUpLastRound() {
		return latestBusUpLastRound;
	}

	public void setLatestBusUpLastRound(Trip latestBusUpLastRound) {
		this.latestBusUpLastRound = latestBusUpLastRound;
	}

	public Trip getLatestBusDownLastRound() {
		return latestBusDownLastRound;
	}

	public void setLatestBusDownLastRound(Trip latestBusDownLastRound) {
		this.latestBusDownLastRound = latestBusDownLastRound;
	}
	
	public Trip getLatestBusLastRound(int direction) {
		if(direction==Direction.UP.getValue()) {
			return latestBusDownLastRound;
		}else {
			return latestBusUpLastRound;
		}
	}
	
	public void setLatestBusLastRound(Trip trip) {
		if(trip.getDirection()==Direction.UP.getValue()) {
			latestBusDownLastRound=trip;;
		}else {
			latestBusUpLastRound=trip;
		}
	}
	
	public Bus getLatestBus(int direction) {
		if(direction==Direction.UP.getValue()) {
			if(latestBusUpLastRound!=null)
				return latestBusUpLastRound.getBus();
		}else if(direction==Direction.DOWN.getValue()){
			if(latestBusDownLastRound!=null)
				return latestBusDownLastRound.getBus();
		}
		return null;
	}

	public String getRouteName() {
		return routeName;
	}

	public Map<Bus, List<Trip>> getTripMap() {
		return tripMap;
	}
	
	public void removeTrip(Trip trip) {
		List<Trip> busTripList=tripMap.get(trip.getBus());
		if(busTripList!=null) {
			busTripList.remove(trip);
		}
		List<Trip> tripList=getTripList(trip.getDirection());
		tripList.remove(trip);
	}
	
	public void removeTripArrival(Trip tripArrival) {
		List<Trip> busQueue=getBusQueue(tripArrival.getDirection());
		busQueue.remove(tripArrival);
	}

	/** 
	* @Title: addTrip 
	* @Description: 添加任务
	* @param @param trip    设定文件 
	* @return void    返回类型 
	* @throws 
	*/ 
	public boolean addTrip(Trip trip) {
		List<Trip> tripList=tripMap.get(trip.getBus());
		if(tripList==null) {
			tripList=new ArrayList<Trip>();
			tripMap.put(trip.getBus(), tripList);
		}
		tripList.add(trip);
		if(trip.getDirection()==Direction.UP.getValue()) {
			upTripList.add(trip);
		}else {
			downTripList.add(trip);
		}
		if(scheduleParam.isLoopLine()&&!scheduleParam.isLoopLineDouble()&&
				!scheduleParam.isTwoLoopLine()) {
			if(!trip.isShortLine())//非长线不加入队列
				busQueueDown.add(trip);
		}else {
			if(trip.getDirection()==Direction.UP.getValue()&&
					!trip.isShortLine()) {//非长线不加入队列
				busQueueUp.add(trip);
			}else if(trip.getDirection()==Direction.DOWN.getValue()&&
					!trip.isShortLine())//非长线不加入队列
				busQueueDown.add(trip);
		}
		checkTripQuit(trip);
		return true;
	}
	
	public List<Trip> getTripList(Bus bus){
		return tripMap.get(bus);
	}
	
	public List<Trip> getTripList(int direction) {
		if(direction==0)
			return upTripList;
		else {
			return downTripList;
		}
	}

	public void addDispatchRule(DispatchRule dispatchRule) {
		dispatchRuleList.add(dispatchRule);
	}
	
	/*public DispatchRule getDispatchRule(String hour,String minute,int direction) {
		for(DispatchRule dispatchRule:dispatchRuleList) {
			if(hour.equals(dispatchRule.getHour())&&minute.equals(dispatchRule.getMinute())&&dispatchRule.getDirection()==direction) {
				return dispatchRule;
			}
		}
		return null;
	}*/
	
	/*public Trip getTripBeforeFirstPlanLatest(Bus bus) {
		while(bus!=null) {
			List<Trip> list=tripMap.get(bus);
			for(int i=list.size()-2;i>=0;i--) {
				Trip trip=list.get(i);
				if(trip.getTripBeginTime().before(scheduleParam.getFirstPlanObuTimeLatest())) {//早于07:30出车的发班时间
					return trip;
				}
			}
			bus=bus.getBusPre();
		}
		return null;
	}*/
	
	public Trip getTripAfterMiddleStopEnd(Bus bus) {
		List<Trip> list=tripMap.get(bus);
		Trip busTrip=null;
		for(int i=list.size()-2;i>=0;i--) {
			Trip trip=list.get(i);
			if(!trip.getTripBeginTime().before(scheduleParam.getMiddleStopEndTime())) {//早于16:00的发班时间
				busTrip=trip;
			}else {
				break;
			}
		}
		return busTrip;
	}
	
	public Trip getTripAfterMiddleStopBegin(Bus bus) {
		List<Trip> list=tripMap.get(bus);
		Trip busTrip=null;
		for(int i=list.size()-2;i>=0;i--) {
			Trip trip=list.get(i);
			if(!trip.getTripBeginTime().before(scheduleParam.getMiddleStopBeginTime())) {//早于10:30的发班时间
				busTrip=trip;
			}else {
				break;
			}
		}
		return busTrip;
	}
	
	/** 
	* @Title: getBusOrder 
	* @Description: 获取车序
	* @param @param direction
	* @param @return    设定文件 
	* @return int    返回类型 
	* @throws 
	*/ 
	public int getBusOrder(int direction) {
		if(direction==0) {
			return busOrderUp;
		}else {
			return busOrderDown;
		}
	}
	
	public int getBusNumber() {
		return getBusOrder(0)+getBusOrder(1);
	}
	
	public int newBusOrder(int direction) {
		if(direction==0) {
			busOrderUp++;
			return busOrderUp;
		}else {
			busOrderDown++;
			return busOrderDown;
		}
	}
	
	/** 
	* @Title: singleClassBusAdd 
	* @Description: 单班车入列
	* @param @param bus    设定文件 
	* @return void    返回类型 
	* @throws 
	*/ 
	public void singleClassBusAdd(Trip trip,int direction) {
		List<Trip> singleClassQueue=getSingleClassQueue(direction);
		singleClassQueue.add(trip);
	}
	
	public List<Trip> getSingleClassQueue(int direction){
		List<Trip> singleClassQueue=null;
		if(direction==0) {
			singleClassQueue=singleClassQueueUp;
		}else {
			singleClassQueue=singleClassQueueDown;
		}
		return singleClassQueue;
	}
	
	/** 
	* @Title: singleClassBuspoll 
	* @Description: 单班车出列
	* @param @param bus
	* @param @return    设定文件 
	* @return Bus    返回类型 
	* @throws 
	*/ 
	public Trip singleClassBuspoll(int direction) {
		List<Trip> singleClassQueue=getSingleClassQueue(direction);
		if(!singleClassQueue.isEmpty()) {
			Trip trip=singleClassQueue.get(0);
			singleClassQueue.remove(trip);
			return trip;
		}
		return null;
	}
	
	public Trip singleClassBusPeek(int direction) {
		List<Trip> singleClassQueue=getSingleClassQueue(direction);
		for(Trip trip:singleClassQueue) {
			if(trip.getTripEndTime()==null){
				trip.setTripEndTime(scheduleParam.getFirstTime(direction));//965单班开始时间设置太早，先设成首班车时间，避免排序报错
			}
		}
		Collections.sort(singleClassQueue, new TripEndTimeComparator());
		if(!singleClassQueue.isEmpty())
			return singleClassQueue.get(0);
		return null;
	}
	
	public void singleClassBusRemove(int direction,Trip trip) {
		List<Trip> singleClassQueue=getSingleClassQueue(direction);
		singleClassQueue.remove(trip);
	}
	
	public void singleClassBusClear() {
		singleClassQueueUp.clear();
		singleClassQueueDown.clear();
	}
	
	public int singleClassBusSize() {
		int busNumber=0;
		for(Bus bus:tripMap.keySet()) {
			if(bus.isSingleClass())
				busNumber++;
		}
		return busNumber;
	}
	
	private ArrayList<Trip> getBusQueueUp() {
		if(!busQueueShortBackUp.isEmpty()) {
			for(int i=0;i<busQueueShortBackUp.size();i++) {
				Trip tripShort=busQueueShortBackUp.get(i);
				for(int j=0;j<busQueueUp.size();j++) {
					Trip tripLong=busQueueUp.get(j);
					if(tripShort.getNextObuTimeMin().before(tripLong.getNextObuTimeMin())) {//短线车在长线车前到
						busQueueUp.add(j,tripShort);
						busQueueShortBackUp.remove(tripShort);
						i--;
						break;
					}
				}
			}
		}
		int direction=Direction.DOWN.getValue();
		Trip tripLastFull=getTripFullLast(direction);
		if(!busQueueUp.isEmpty()&&tripLastFull!=null) {
			Trip tripLast=busQueueUp.get(busQueueUp.size()-1);//到站队列最后一台车
			for(ScheduleParamShift shift:shiftBusDownMap.keySet()) {
				List<Bus> busList=shiftBusDownMap.get(shift);
				if(busList.isEmpty())
					continue;
				if(DateUtil.getDateHM(shift.getStartTime()).before(tripLast.getNextObuTimeMin())) {//到了插入车辆时间
					int index=-2;//最后一台索引
					for(int i=0;i<busQueueUp.size();i++) {
						Trip trip=busQueueUp.get(i);
						if(trip.getBus().getShiftType()==shift&&trip.getTripBeginTime()==null) {
							index=i;
						}
					}
					//index+=2;//隔一台插入
					int maxInterval=scheduleParam.getMaxInterval(tripLast.getNextObuTimeMin(), direction);
					if(index==-2) {
						if(tripLastFull.getBus().getShiftType()==shift) {//隔一台插入
							if(DateUtil.getMinuteInterval(tripLastFull.getTripBeginTime(), busQueueUp.get(0).getNextObuTimeMin())<maxInterval) {
								index=1;
							}else {
								index=0;//断位插入相同班别
							}
						}else {
							index=0;
						}
					}else {
						if(index<busQueueUp.size()-1) {
							if((DateUtil.getMinuteInterval(busQueueUp.get(index).getNextObuTimeMin(), busQueueUp.get(index+1).getNextObuTimeMin())<maxInterval)||//隔一台最小停站不断位
								(busQueueUp.get(index+1).getTripEndTime()!=null&&
								(busQueueUp.get(index+1).getTripEndTime().before(busQueueUp.get(index).getNextObuTimeMin())||
								DateUtil.getMinuteInterval(busQueueUp.get(index).getNextObuTimeMin(), busQueueUp.get(index+1).getTripEndTime())<maxInterval))) {//隔一台不断位
								index=index+2;//不断位隔一台
							}else {
								index=index+1;
							}
						}else {
							index=index+2;
						}
					}
					for(int i=index;i<busQueueUp.size();i++) {
						Date obuTimeLast=null;
						if(i==0) {
							obuTimeLast=tripLastFull.getTripBeginTime();
						}else {
							if(busQueueUp.get(i-1).isQuitMark()) {//前一台车退出
								if(i>1) {
									if(busQueueUp.get(i-2).getBus().getShiftCode()==shift.getShiftType()) {//前前台车班别一样，再隔一台，避免一起抽停
										continue;
									}
								}
							}
							obuTimeLast=busQueueUp.get(i-1).getNextObuTimeMin();
						}
						Date obuTimeNext=busQueueUp.get(i).getNextObuTimeMin();
						Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, Math.min((int)Math.ceil(DateUtil.getMinuteInterval(obuTimeLast, obuTimeNext)/2.0),maxInterval));
						if(!obuTime.before(DateUtil.getDateHM(shift.getStartTime()))) {
							Trip trip=new Trip();
							Bus bus=busList.get(0);
							bus.setShiftType(shift);
							trip.setBus(bus);
							trip.setDirection(Direction.UP.getValue());
							trip.setNextObuTimeMin(obuTime);
							busList.remove(bus);
							busQueueUp.add(i, trip);
							if(busList.isEmpty()) {
								break;
							}
							if(DateUtil.getMinuteInterval(obuTime, busQueueUp.get(i+1).getNextObuTimeMin())<maxInterval)//不断位，隔一台
								i++;
						}
					}
				}
			}
		}
		if(busQueueUp.isEmpty()&&currentScheduleHalfHour!=null) {//没车，看能不能出晚班车
			for(ScheduleParamShift shift:shiftBusDownMap.keySet()) {
				List<Bus> busList=shiftBusDownMap.get(shift);
				if(busList.isEmpty()) {
					continue;
				}
				if(!DateUtil.getDateHM(currentScheduleHalfHour.getRunTime()).before(DateUtil.getDateHM(shift.getStartTime()))){
					Date obuTime=null;
					if(tripLastFull!=null) {
						int maxInterval=scheduleParam.getMaxInterval(tripLastFull.getTripBeginTime(), direction);
						obuTime=DateUtil.getDateAddMinute(tripLastFull.getTripBeginTime(), maxInterval);
					}else {
						obuTime=DateUtil.getDateHM(shift.getStartTime());
					}
					int maxInterval=scheduleParam.getMaxInterval(obuTime, direction);
					for(Bus bus:busList) {
						Trip trip=new Trip();
						bus.setShiftType(shift);
						trip.setBus(bus);
						trip.setDirection(Direction.UP.getValue());
						trip.setNextObuTimeMin(obuTime);
						obuTime=DateUtil.getDateAddMinute(obuTime, maxInterval);
						busQueueUp.add(trip);
					}
					busList.clear();
				}
			}
		}
		return busQueueUp;
	}

	private ArrayList<Trip> getBusQueueDown() {
		if(!busQueueShortBackDown.isEmpty()) {
			for(int i=0;i<busQueueShortBackDown.size();i++) {
				Trip tripShort=busQueueShortBackDown.get(i);
				for(int j=0;j<busQueueDown.size();j++) {
					Trip tripLong=busQueueDown.get(j);
					if(!tripShort.getNextObuTimeMin().after(tripLong.getNextObuTimeMin())) {//短线车在长线车前到
						busQueueDown.add(j,tripShort);
						busQueueShortBackDown.remove(tripShort);
						i--;
						break;
					}
				}
			}
		}
		int direction=Direction.UP.getValue();
		Trip tripLastFull=getTripFullLast(direction);
		if(!busQueueDown.isEmpty()&&tripLastFull!=null) {
			Trip tripLast=busQueueDown.get(busQueueDown.size()-1);//到站队列最后一台车
			for(ScheduleParamShift shift:shiftBusUpMap.keySet()) {
				List<Bus> busList=shiftBusUpMap.get(shift);
				if(busList.isEmpty())
					continue;
				if(DateUtil.getDateHM(shift.getStartTime()).before(tripLast.getNextObuTimeMin())) {//到了插入车辆时间
					int index=-2;//最后一台索引
					for(int i=0;i<busQueueDown.size();i++) {
						Trip trip=busQueueDown.get(i);
						if(trip.getBus().getShiftCode()==shift.getShiftType()&&trip.getTripBeginTime()==null) {
							index=i;
						}
					}
					int maxInterval=scheduleParam.getMaxInterval(tripLast.getNextObuTimeMin(), direction);
					//index+=2;//隔一台插入
					if(index==-2) {
						if(tripLastFull.getBus().getShiftType()==shift) {//隔一台插入
							if(DateUtil.getMinuteInterval(tripLastFull.getTripBeginTime(), busQueueDown.get(0).getNextObuTimeMin())<maxInterval) {
								index=1;
							}else {
								index=0;//断位插入相同班别
							}
						}else {
							index=0;
						}
					}else {
						if(index<busQueueDown.size()-1) {
							if(DateUtil.getMinuteInterval(busQueueDown.get(index).getNextObuTimeMin(), busQueueDown.get(index+1).getNextObuTimeMin())<maxInterval) {
								index=index+2;//不断位隔一台
							}else {
								index=index+1;
							}
						}else {
							index=index+2;
						}
					}
					for(int i=index;i<busQueueDown.size();i++) {
						Date obuTimeLast=null;
						if(i==0) {
							obuTimeLast=tripLastFull.getTripBeginTime();
						}else {
							obuTimeLast=busQueueDown.get(i-1).getNextObuTimeMin();
						}
						Date obuTimeNext=busQueueDown.get(i).getNextObuTimeMin();
						Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, Math.min((int)Math.ceil(DateUtil.getMinuteInterval(obuTimeLast, obuTimeNext)/2.0),maxInterval));
						if(!DateUtil.getDateHM(shift.getStartTime()).after(obuTimeNext)) {
							Trip trip=new Trip();
							Bus bus=busList.get(0);
							bus.setShiftType(shift);
							trip.setBus(bus);
							trip.setDirection(Direction.DOWN.getValue());
							if(obuTime.before(DateUtil.getDateHM(shift.getStartTime())))
								obuTime=DateUtil.getDateHM(shift.getStartTime());
							trip.setNextObuTimeMin(obuTime);
							busList.remove(bus);
							busQueueDown.add(i, trip);
							if(busList.isEmpty()) {
								break;
							}
							if(DateUtil.getMinuteInterval(obuTime, busQueueDown.get(i+1).getNextObuTimeMin())<maxInterval)//不断位，隔一台
								i++;
						}
					}
				}
			}
		}
		if(busQueueDown.isEmpty()&&!upTripList.isEmpty()&&scheduleParam.isLoopLine()) {//环线，没车了
			ScheduleParamShift shiftRecovery=null;
			for(ScheduleParamShift shift:shiftBusUpMap.keySet()) {
				List<Bus> busList=shiftBusUpMap.get(shift);
				if(busList.isEmpty())
					continue;
				if(shiftRecovery==null||DateUtil.getDateHM(shift.getStartTime()).before(DateUtil.getDateHM(shiftRecovery.getStartTime()))) {
					shiftRecovery=shift;
				}
			}
			if(shiftRecovery!=null) {
				List<Bus> busList=shiftBusUpMap.get(shiftRecovery);
				for(Bus bus:busList) {
					Trip trip=new Trip();
					trip.setBus(bus);
					trip.setDirection(Direction.DOWN.getValue());
					trip.setNextObuTimeMin(DateUtil.getDateHM(shiftRecovery.getStartTime()));
					busQueueDown.add(trip);
				}
				shiftBusUpMap.remove(shiftRecovery);
			}
		}
		if(busQueueDown.isEmpty()&&currentScheduleHalfHour!=null) {//没车，看能不能出晚班车
			for(ScheduleParamShift shift:shiftBusUpMap.keySet()) {
				List<Bus> busList=shiftBusUpMap.get(shift);
				if(!busList.isEmpty()&&
						!DateUtil.getDateHM(currentScheduleHalfHour.getRunTime()).before(DateUtil.getDateHM(shift.getStartTime()))){
					Date obuTime=null;
					if(tripLastFull!=null) {
						int maxInterval=scheduleParam.getMaxInterval(tripLastFull.getTripBeginTime(), direction);
						obuTime=DateUtil.getDateAddMinute(tripLastFull.getTripBeginTime(), maxInterval);
					}else {
						obuTime=DateUtil.getDateHM(shift.getStartTime());
					}
					int maxInterval=scheduleParam.getMaxInterval(obuTime, direction);
					for(Bus bus:busList) {
						Trip trip=new Trip();
						bus.setShiftType(shift);
						trip.setBus(bus);
						trip.setDirection(Direction.DOWN.getValue());
						trip.setNextObuTimeMin(obuTime);
						obuTime=DateUtil.getDateAddMinute(obuTime, maxInterval);
						busQueueDown.add(trip);
					}
					busList.clear();
				}
			}
		}
		return busQueueDown;
	}

	public void setBusQueueUp(ArrayList<Trip> busQueueUp) {
		this.busQueueUp = busQueueUp;
	}

	public void setBusQueueDown(ArrayList<Trip> busQueueDown) {
		this.busQueueDown = busQueueDown;
	}
	
	public ArrayList<Trip> getBusQueue(int direction){
		ArrayList<Trip> busQueue=null;
		if(direction==0&&(!scheduleParam.isLoopLine()||scheduleParam.isTwoLoopLine()||scheduleParam.isLoopLineDouble())) {
			busQueue = getBusQueueUp();
		}else {
			busQueue = getBusQueueDown();
		}
		for(int i=0;i<busQueue.size()-1;i++) {
			Trip trip=busQueue.get(i);
			Trip tripNext=busQueue.get(i);
			if(trip.isEatAfterTrip()&&tripNext.getNextObuTimeMin().before(trip.getNextObuTimeMin())) {//前车吃饭，后车不吃饭，调位
				busQueue.set(i, tripNext);
				busQueue.set(i+1, trip);
			}
		}
		return busQueue;
	}
	
	public List<Trip> getBusQueueDoubleAndEveningClasses(int direction){
		List<Trip> bus_queue_to=null;
		List<Trip> busQueueShortBack=null;
		if(direction==0&&!scheduleParam.isLoopLine()) {
			bus_queue_to=getBusQueueUp();
			busQueueShortBack=busQueueShortBackUp;
		}else {
			bus_queue_to=getBusQueueDown();
			busQueueShortBack=busQueueShortBackDown;
		}
		List<Trip> busQueueDouble=new ArrayList<Trip>();
		for(Trip tripArrival:bus_queue_to) {
			Bus bus=tripArrival.getBus();
			if(!bus.isSingleClass()&&!tripArrival.isQuitMark()&&(bus.getShiftType()==null||!DateUtil.getDateHM(bus.getShiftType().getEndTime()).before(scheduleParam.getLatestTime(bus.getStartDirection()))
					||(bus.getShiftType().getShiftType().equals(ShiftType.EVENING_SHIFT.getValue()))))//可以做对面末班车
				busQueueDouble.add(tripArrival);
		}
		for(Trip tripArrival:busQueueShortBack) {
			Bus bus=tripArrival.getBus();
			if(!bus.isSingleClass()&&!tripArrival.isQuitMark()&&(bus.getShiftType()==null||!DateUtil.getDateHM(bus.getShiftType().getEndTime()).before(scheduleParam.getLatestTime(bus.getStartDirection()))
					||(bus.getShiftType().getShiftType().equals(ShiftType.EVENING_SHIFT.getValue()))))//可以做对面末班车
				busQueueDouble.add(tripArrival);
		}
		return busQueueDouble;
	}
	
	public List<Trip> getBusQueueDoubleAndEveningClasses(){
		List<Trip> busQueueUp=getBusQueueDoubleAndEveningClasses(Direction.UP.getValue());
		List<Trip> busQueueDown=getBusQueueDoubleAndEveningClasses(Direction.DOWN.getValue());
		List<Trip> busQueueDouble=new ArrayList<Trip>();
		busQueueDouble.addAll(busQueueUp);
		busQueueDouble.addAll(busQueueDown);
		return busQueueDouble;
	}
	
	public List<Trip> getBusQueueShort(int direction){
		if(direction==0&&!scheduleParam.isLoopLine()) {
			return busQueueShortBackUp;
		}else {
			return busQueueShortBackDown;
		}
	}
	
	public Trip getTripInBusQueueShortBack(Bus bus) {
		for(Trip trip:busQueueShortBackUp) {
			if(trip.getBus()==bus) {
				return trip;
			}
		}
		for(Trip trip:busQueueShortBackDown) {
			if(trip.getBus()==bus) {
				return trip;
			}
		}
		return null;
	}

	/*public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}*/

	public Date getObuTimeFullLast(int direction) {
		Trip tripFullLast=getTripFullLast(direction);
		if(tripFullLast!=null)
			return tripFullLast.getTripBeginTime();
		return null;
	}
	
	public Trip getTripLast(int direction) {
		return getTripLast(direction,false);
	}
	
	public Trip getTripFullLast(int direction) {
		Trip tripFullLast=getTripLast(direction, true);
		return tripFullLast;
	}
	
	private Trip getTripLast(int direction,boolean isFull) {
		List<Trip> tripList=getTripList(direction);
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			if(!isFull||(isFull&&!trip.isShortLine())) {
				return trip;
			}
		}
		return null;
	}

	public RouteSchedule getRouteScheduleAddClassesLastUp() {
		return routeScheduleAddClassesLastUp;
	}

	public void setRouteScheduleAddClassesLastUp(RouteSchedule routeScheduleAddClassesLastUp) {
		this.routeScheduleAddClassesLastUp = routeScheduleAddClassesLastUp;
	}

	public RouteSchedule getRouteScheduleAddClassesLastDown() {
		return routeScheduleAddClassesLastDown;
	}

	public void setRouteScheduleAddClassesLastDown(RouteSchedule routeScheduleAddClassesLastDown) {
		this.routeScheduleAddClassesLastDown = routeScheduleAddClassesLastDown;
	}

	public int getMaxClassesNumber() {
		return maxClassesNumber;
	}

	public void setMaxClassesNumber(int maxClassesNumber) {
		this.maxClassesNumber = maxClassesNumber;
	}
	
	private int getRunningTime(ScheduleHalfHour scheduleHalfHour) {
		Integer wasteTime=scheduleParam.getWasteTime(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
		Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
		Date arrivalTime=scheduleParam.getArrivalTime(runTime, scheduleHalfHour.getDirection());
		//Date runTimeArrival=DateUtil.getDateAdd(runTime, Calendar.MINUTE, 30);
		int direction=1-scheduleHalfHour.getDirection();
		Integer restTime=scheduleParam.getLeaveImmediatelyInterval(1-scheduleHalfHour.getDirection());
		if(restTime==null) {
			//午饭
			if(scheduleParam.getLunchBeginTime(direction)!=null&&!runTime.after(scheduleParam.getLunchBeginTime(direction))&&arrivalTime.after(scheduleParam.getLunchBeginTime(direction))){
				restTime=scheduleParam.getLunchEatTime(direction);
			}//晚饭
			else if(scheduleParam.getSupperBeginTime(direction)!=null&&!runTime.after(scheduleParam.getSupperBeginTime(direction))&&arrivalTime.after(scheduleParam.getSupperBeginTime(direction))){
				restTime=scheduleParam.getSupperEatTime(direction);
			}else {
				restTime=scheduleParam.getRestTime(arrivalTime, scheduleHalfHour.getDirection());
			}
		}
		wasteTime+=restTime;
		return wasteTime;
	}
	
	public void calculateBusConfig(ScheduleParam scheduleParam) {
		System.out.println("计算配车开始===============================================================");
		System.out.println("半小时发班数开始");
		Map<ScheduleHalfHour, List<Date>> scheduleHalfHourPlanTimeMap=new HashMap<ScheduleHalfHour, List<Date>>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			List<Plan> planList=scheduleHalfHour.getPlanList();
			List<Date> planTimeList=planList.stream().map(Plan::getPlanTime).collect(Collectors.toList());
			scheduleHalfHourPlanTimeMap.put(scheduleHalfHour, planTimeList);
			System.out.println(scheduleHalfHour.getRunTime()+"_"+scheduleHalfHour.getDirection()+"\t"+planTimeList.size());;
		}
		System.out.println("半小时发班数结束");
		int doubleBusNumber=0;
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			if(scheduleHalfHour.getRunTime().equals("1700")) {
				System.out.println("aa");
			}
			Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
			int wasteTime=getRunningTime(scheduleHalfHour);
			/*Date obuTime=DateUtil.getDateAddMinute(runTime, wasteTime);
			ScheduleHalfHour scheduleHalfHourReverse=scheduleParam.getScheduleHalfHour(obuTime, 1-scheduleHalfHour.getDirection());
			if(scheduleHalfHourReverse!=null) {
				int wasteTimeReverse=getRunningTime(scheduleHalfHourReverse);
				wasteTime+=wasteTimeReverse;
			}*/
			System.out.print(scheduleHalfHour.getRunTime()+"_"+scheduleHalfHour.getDirection()+"\t运行时间："+wasteTime);
			int busNumber=getBusNumber(scheduleHalfHour, wasteTime, scheduleHalfHourPlanTimeMap);
			System.out.println("\t配车数"+busNumber);
			scheduleParam.setBusConfig(scheduleHalfHour, busNumber);
			Integer busNumberBak=timeBusConfigMap.get(scheduleHalfHour.getRunTime());
			if(busNumberBak!=null) {
				timeBusConfigMap.put(scheduleHalfHour.getRunTime(), busNumberBak+busNumber);
			}else {
				timeBusConfigMap.put(scheduleHalfHour.getRunTime(), busNumber);
			}
			Date arrivalTime=scheduleParam.getArrivalTime(runTime, scheduleHalfHour.getDirection());
			if((arrivalTime.after(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopBeginTime(), 30))&&
					!arrivalTime.after(scheduleParam.getMiddleStopRecoveryBeginTime()))||
					arrivalTime.after(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopOffDutyBeginTime(), 30))) {
				Date obuTime=DateUtil.getDateAddMinute(runTime, wasteTime);
				ScheduleHalfHour scheduleHalfHourReverse=scheduleParam.getScheduleHalfHour(obuTime, 1-scheduleHalfHour.getDirection());
				if(scheduleHalfHourReverse!=null) {
					int wasteTimeReverse=getRunningTime(scheduleHalfHourReverse);
					wasteTime+=wasteTimeReverse;
				}
				int doubleBusNumberTemp=getBusNumber(scheduleHalfHour, wasteTime, scheduleHalfHourPlanTimeMap);
				if(doubleBusNumberTemp>doubleBusNumber)
					doubleBusNumber=doubleBusNumberTemp;
			}
		}
	
		System.out.println("计算配车结束===============================================================");
		System.out.println("分时段配车数===============================================================");
		Set<String> set=timeBusConfigMap.keySet();
		Object[] arr=set.toArray();
        Arrays.sort(arr);
        int maxBusNumber=0;
        /*Date singleClassesStopLastRound=DateUtil.getDateAdd(getMiddleStopBeginTime(), Calendar.MINUTE, -60);
        Date singleClassesOffDutyLastRound=DateUtil.getDateAdd(getMiddleStopOffDutuBeginTime(), Calendar.MINUTE, -60);*/
		for(Object runTime:arr) {
			int busNumber=timeBusConfigMap.get(runTime);
			System.out.println(runTime+"\t"+busNumber);
			if(busNumber>maxBusNumber)
				maxBusNumber=busNumber;
			/*if((!DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1000"))&&DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1500")))||
					!DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1800"))) {*///10点到15点，18点后算所需双班车
			/*Date runTimeDate=DateUtil.getCalendarHM(runTime.toString()).getTime();
			if(scheduleParam.getMiddleStopBeginTime()==null||scheduleParam.getMiddleStopRecoveryBeginTime()==null)
				continue;
			if(DateUtil.isInTimeRange(runTimeDate, scheduleParam.getMiddleStopBeginTime(), scheduleParam.getMiddleStopRecoveryBeginTime())||
					!runTimeDate.before(DateUtil.getDateAdd(scheduleParam.getMiddleStopOffDutyBeginTime(), Calendar.MINUTE, 60))) {//双班车运营时段-单班中停之前或复行手工
				if(busNumber>doubleBusNumber) {
					 doubleBusNumber=busNumber;
				}
			}*/
		}
		if(maxBusNumber==0) {
			throw new MyException("-1", "请确认是否已同步客流数据");
		}
		System.out.println("总配车数:"+maxBusNumber+"台");
		System.out.println("单班车数:"+(maxBusNumber-doubleBusNumber)+"台");
		scheduleParam.setBusNumberConfig(maxBusNumber);
		scheduleParam.setBusNumberSingle(maxBusNumber-doubleBusNumber);
	}
	
	public void calculateBusConfigNew(ScheduleParam scheduleParam) {
		System.out.println("计算配车开始===============================================================");
		System.out.println("半小时发班数开始");
		Map<ScheduleHalfHour, List<Date>> scheduleHalfHourPlanTimeMap=new HashMap<ScheduleHalfHour, List<Date>>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			List<Plan> planList=scheduleHalfHour.getPlanList();
			List<Date> planTimeList=planList.stream().map(Plan::getPlanTime).collect(Collectors.toList());
			scheduleHalfHourPlanTimeMap.put(scheduleHalfHour, planTimeList);
			System.out.println(scheduleHalfHour.getRunTime()+"_"+scheduleHalfHour.getDirection()+"\t"+planTimeList.size());;
		}
		System.out.println("半小时发班数结束");
		int doubleBusNumber=0;
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			if(scheduleHalfHour.getRunTime().equals("1700")) {
				System.out.println("aa");
			}
			if(scheduleParam.getLeaveImmediatelyInterval(scheduleHalfHour.getDirection())!=null) {//到站即走
				continue;
			}
			Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
			int wasteTime=getRunningTime(scheduleHalfHour);
			Date obuTime=DateUtil.getDateAddMinute(runTime, wasteTime);
			Integer busNumber=null;
			if(scheduleParam.isLoopLineDouble()) {
				busNumber=getBusNumber(scheduleHalfHour, wasteTime, scheduleHalfHourPlanTimeMap);
				ScheduleHalfHour scheduleHalfHourReverse=scheduleParam.getScheduleHalfHour(scheduleHalfHour.getRunTime(), 1-scheduleHalfHour.getDirection());
				if(scheduleHalfHourReverse!=null) {
					int busNumberReverse=getBusNumber(scheduleHalfHourReverse, wasteTime, scheduleHalfHourPlanTimeMap);
					busNumber+=busNumberReverse;
				}
			}else {
				ScheduleHalfHour scheduleHalfHourReverse=scheduleParam.getScheduleHalfHour(obuTime, 1-scheduleHalfHour.getDirection());
				if(scheduleHalfHourReverse!=null&&!scheduleParam.isLoopLine()) {
					int wasteTimeReverse=getRunningTime(scheduleHalfHourReverse);
					wasteTime+=wasteTimeReverse;
				}
				busNumber=getBusNumber(scheduleHalfHour, wasteTime, scheduleHalfHourPlanTimeMap);
			}
			System.out.print(scheduleHalfHour.getRunTime()+"_"+scheduleHalfHour.getDirection()+"\t运行时间："+wasteTime);
			System.out.println("\t配车数"+busNumber);
			scheduleParam.setBusConfig(scheduleHalfHour, busNumber);
			Integer busNumberBak=timeBusConfigMap.get(scheduleHalfHour.getRunTime());
			if(busNumberBak==null||busNumber>busNumberBak) {
				timeBusConfigMap.put(scheduleHalfHour.getRunTime(), busNumber);
			}
			Date arrivalTime=scheduleParam.getArrivalTime(runTime, scheduleHalfHour.getDirection());
			if((arrivalTime.after(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopBeginTime(), 30))&&
					!arrivalTime.after(scheduleParam.getMiddleStopRecoveryBeginTime()))||
					arrivalTime.after(DateUtil.getDateAddMinute(scheduleParam.getMiddleStopOffDutyBeginTime(), 30))) {
				if(busNumber>doubleBusNumber)
					doubleBusNumber=busNumber;
			}
		}
	
		System.out.println("计算配车结束===============================================================");
		System.out.println("分时段配车数===============================================================");
		Set<String> set=timeBusConfigMap.keySet();
		Object[] arr=set.toArray();
        Arrays.sort(arr);
        int maxBusNumber=0;
        /*Date singleClassesStopLastRound=DateUtil.getDateAdd(getMiddleStopBeginTime(), Calendar.MINUTE, -60);
        Date singleClassesOffDutyLastRound=DateUtil.getDateAdd(getMiddleStopOffDutuBeginTime(), Calendar.MINUTE, -60);*/
		for(Object runTime:arr) {
			int busNumber=timeBusConfigMap.get(runTime);
			System.out.println(runTime+"\t"+busNumber);
			if(busNumber>maxBusNumber)
				maxBusNumber=busNumber;
			/*if((!DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1000"))&&DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1500")))||
					!DateUtil.getCalendarHM(runTime.toString()).before(DateUtil.getCalendarHM("1800"))) {*///10点到15点，18点后算所需双班车
			/*Date runTimeDate=DateUtil.getCalendarHM(runTime.toString()).getTime();
			if(scheduleParam.getMiddleStopBeginTime()==null||scheduleParam.getMiddleStopRecoveryBeginTime()==null)
				continue;
			if(DateUtil.isInTimeRange(runTimeDate, scheduleParam.getMiddleStopBeginTime(), scheduleParam.getMiddleStopRecoveryBeginTime())||
					!runTimeDate.before(DateUtil.getDateAdd(scheduleParam.getMiddleStopOffDutyBeginTime(), Calendar.MINUTE, 60))) {//双班车运营时段-单班中停之前或复行手工
				if(busNumber>doubleBusNumber) {
					 doubleBusNumber=busNumber;
				}
			}*/
		}
		if(maxBusNumber==0) {
			throw new MyException("-1", "请确认是否已同步客流数据");
		}
		System.out.println("总配车数:"+maxBusNumber+"台");
		System.out.println("单班车数:"+(maxBusNumber-doubleBusNumber)+"台");
		scheduleParam.setBusNumberConfig(maxBusNumber);
		scheduleParam.setBusNumberSingle(maxBusNumber-doubleBusNumber);
	}
	
	
	private int getBusNumber(ScheduleHalfHour scheduleHalfHour,int wasteTime,Map<ScheduleHalfHour, List<Date>> scheduleHalfHourPlanTimeMap) {
		Double ratio=wasteTime*1.00/30;
		double ratioInteger=Math.floor(ratio);
		double ratioDecimal=ratio-ratioInteger;
		int busNumber=0;
		if(ratioInteger>=1) {
			busNumber=scheduleHalfHourPlanTimeMap.get(scheduleHalfHour).size();
			ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
			for(int i=1;i<ratioInteger;i++) {
				if(scheduleHalfHourNext!=null) {
					busNumber+=scheduleHalfHourPlanTimeMap.get(scheduleHalfHourNext).size();
					scheduleHalfHourNext=scheduleHalfHourNext.getNextScheduleHalfHour();
				}
			}
			if(scheduleHalfHourNext!=null)
				busNumber+=Math.ceil(ratioDecimal*scheduleHalfHourPlanTimeMap.get(scheduleHalfHourNext).size());
		}else {
			busNumber+=Math.ceil(ratioDecimal*scheduleHalfHourPlanTimeMap.get(scheduleHalfHour).size());
		}
		return busNumber;
	}
	
	public Map<String, Integer> getTimeBusConfigMap() {
		return timeBusConfigMap;
	}

	public void setEaten(Bus bus,Trip trip,Date tripBeginTimeNext,ScheduleParam scheduleParam) {
		if(trip==null)
			return;
		Date arrivalTime=trip.getTripEndTime();
		if(arrivalTime==null)
			return;
		int diretion=1-trip.getDirection();
		if(scheduleParam.isLoopLine())
			diretion=trip.getDirection();
		if(scheduleParam.getLunchBeginTime(diretion)!=null&&!tripBeginTimeNext.before(DateUtil.getDateAddMinute(scheduleParam.getLunchBeginTime(diretion), -30))&&!bus.isSingleClass()&&!bus.isLunchEaten()) {//到达时间在11点后的，肯定要安排吃饭
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(arrivalTime);
			calendar.add(Calendar.MINUTE, scheduleParam.getLunchEatTime(diretion));
			if(!tripBeginTimeNext.before(calendar.getTime())) {
				bus.setLunchEaten(true);
			}
		}
		if(scheduleParam.getSupperBeginTime(diretion)!=null&&!tripBeginTimeNext.before(DateUtil.getDateAddMinute(scheduleParam.getSupperBeginTime(diretion), -30))&&!bus.isSupperEaten()&&!bus.isSupperEaten()) {
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(arrivalTime);
			calendar.add(Calendar.MINUTE, scheduleParam.getSupperEatTime(diretion));
			if(!tripBeginTimeNext.before(calendar.getTime())) {
				System.out.println(bus.getBusName()+"\t到站时间："+DateFormatUtil.HM.getDateString(arrivalTime)+"\t开车时间："+DateFormatUtil.HM.getDateString(tripBeginTimeNext)+"设置吃晚饭");
				bus.setSupperEaten(true);
			}
		}
	}
	
	public void addShortTripBack(Trip trip) {
		if(!scheduleParam.isLoopLine()&&trip.getDirection()==0) {
			for(Trip tripRecovery:busQueueShortBackUp) {
				if(tripRecovery.getBus()==trip.getBus()) {
					return;
				}
			}
			busQueueShortBackUp.add(trip);
		}else {
			for(Trip tripRecovery:busQueueShortBackUp) {
				if(tripRecovery.getBus()==trip.getBus()) {
					return;
				}
			}
			busQueueShortBackDown.add(trip);
		}
	}
	
	public void addMiddleStopTrip(Trip trip) {
		int direction=trip.getDirection();
		List<Trip> busTripList=tripMap.get(trip.getBus());
		if(busTripList!=null&&busTripList.size()>0) {
			Collections.sort(busTripList,new TripBeginTimeComparator());
			Trip preTrip=busTripList.get(busTripList.size()-1);//获取中停前的班次
			if(preTrip.getTurnTrip()!=null) {//短线调头
				preTrip=preTrip.getTurnTrip();
			}
			Trip middleStopTrip=new Trip(trip.getBus(), Direction.NODIRECTION.getValue(), DateUtil.getDateAdd(preTrip.getTripEndTime(), Calendar.MINUTE, 5));//中停前一班次结束加5分钟作为中停开始时间;
			middleStopTrip.setFirstRouteStaId(scheduleParam.getFirstRouteStaId(direction));
			middleStopTrip.setLastRouteStaId(scheduleParam.getFirstRouteStaId(direction));
			middleStopTrip.setTripEndTime(DateUtil.getDateAdd(trip.getTripBeginTime(), Calendar.MINUTE, -5));//中停复行开始前5分钟作为中停结束时间;	
			busTripList.add(middleStopTrip);
		}	
	}
	
	public void setSingleBusAdjust(List<Trip> singleBusTripList,Trip doubleClassTrip) {
		StringBuffer sb=new StringBuffer();
		for(Trip trip:singleBusTripList) {
			sb.append(trip.getBusName()+"|");
		}
		singleBusAdjustMap.put(sb.toString(), doubleClassTrip);
	}
	
	public boolean isHadSingleBusAdjust(List<Trip> singleBusTripList) {
		StringBuffer sb=new StringBuffer();
		for(Trip trip:singleBusTripList) {
			sb.append(trip.getBusName()+"|");
		}
		return singleBusAdjustMap.containsKey(sb.toString());
	}
	
	public void addShiftBus(ScheduleParamShift shift,Bus bus){
		Map<ScheduleParamShift, List<Bus>> shiftBusMap=null;
		if(bus.getStartDirection()==Direction.UP.getValue()) {
			shiftBusMap=shiftBusUpMap;
		}else {
			shiftBusMap=shiftBusDownMap;
		}
		List<Bus> busList=shiftBusMap.get(shift);
		if(busList==null) {
			busList=new ArrayList<Bus>();
			shiftBusMap.put(shift, busList);
		}
		busList.add(bus);
	}
	
	public void resetBusOrder() {
		int busOrderUp=0;
		int busOrderDown=0;
		for(Bus bus:tripMap.keySet()) {
			if(bus.getStartDirection()==Direction.UP.getValue()) {
				busOrderUp++;
			}else {
				busOrderDown++;
			}
		}
		this.busOrderUp=busOrderUp;
		this.busOrderDown=busOrderDown;
	}

	public void clearTripPlan(){
		this.tripMap =  new HashMap<Bus, List<Trip>>();
		this.upTripList = new ArrayList<>();
		this.downTripList=new ArrayList<Trip>();
		busQueueUp=new ArrayList<Trip>();//上行车队列
		busQueueDown=new ArrayList<Trip>();//下行车队列
	}
	
	public List<Trip> getRunTripList(Bus bus) {
		List<Trip> busTripList=getTripList(bus);
		if(busTripList==null)
			return null;
		List<Trip> runTripList=new ArrayList<Trip>();
		for(Trip trip:busTripList) {
			if(trip.getDirection()!=Direction.NODIRECTION.getValue()) {
				runTripList.add(trip);
			}
		}
		return runTripList;
	}
	
	public Trip getTripRunLast(Bus bus) {
		List<Trip> runTripList=getRunTripList(bus);
		if(runTripList==null)
			return null;
		Trip tripRunLast=runTripList.get(runTripList.size()-1);
		return tripRunLast;
	}
	
	public void addBusTripList(Bus bus,List<Trip> busTripList) {
		if(tripMap.containsKey(bus))
			System.out.println("aaa");
		tripMap.put(bus, busTripList);
	}
	
	public void checkTripQuit(Trip trip) {
		Bus bus=trip.getBus();
		if(bus.isSingleClass()) {//单班车退出
			if(bus.getSingleBusTripNumber()!=null) {//按走了班次抽停
				List<Trip> runTripList=getRunTripList(bus);
				if(bus.isHasMiddleStop()) {//中停过
					if(runTripList.size()<bus.getSingleBusTripNumber()&&
						trip.getTripEndTime().before(scheduleParam.getMiddleStopOffDutyBeginTime())) {
						trip.setQuitMark(false);
					}else {
						if(scheduleParam.isEndDirectionTrip(trip)) {
							trip.setQuitMark(true);
						}else {
							trip.setQuitMark(false);
						}
					}
				}else {
					if(runTripList.size()<bus.getSingleBusTripNumberMorning()) {
						trip.setQuitMark(false);
					}else {
						if(scheduleParam.isEndDirectionTrip(trip)) {
							trip.setQuitMark(true);
						}else {
							trip.setQuitMark(false);
						}
					}
				}
			}else {
				if(bus.isHasMiddleStop()&&trip.isQuitMark()) {
					if(scheduleParam.getSingleBusTripNumber()!=null) {
						List<Trip> runTripList=getRunTripList(bus);
						if(runTripList.size()<scheduleParam.getSingleBusTripNumber()&&
							trip.getTripEndTime().before(scheduleParam.getMiddleStopOffDutyBeginTime())) {
							trip.setQuitMark(false);
						}
					}
				}
			}
		}
	}
	
	public Trip getBusTripFromQueue(Bus bus) {
		List<Trip> busQueue=getBusQueue(Direction.UP.getValue());
		for(Trip trip:busQueue) {
			if(bus==trip.getBus()) {
				return trip;
			}
		}
		busQueue=getBusQueue(Direction.DOWN.getValue());
		for(Trip trip:busQueue) {
			if(bus==trip.getBus()) {
				return trip;
			}
		}
		return null;
	}
	
	public void tripListSort(List<Trip> tripList) {
		Collections.sort(tripList, new TripBeginTimeComparator());
		Trip lastTrip=null;
		for(Trip trip:tripList) {
			trip.setLastTrip(null);
			trip.setNextTrip(null);
		}
		for(Trip trip:tripList) {
			trip.setLastTrip(lastTrip);
			if(lastTrip!=null)
				lastTrip.setNextTrip(trip);
			lastTrip=trip;
		}
	}
	
	public void tripListSort() {
		List<Trip> tripList=getTripList(Direction.UP.getValue());
		tripListSort(tripList);
		tripList=getTripList(Direction.DOWN.getValue());
		tripListSort(tripList);
	}
	
}

