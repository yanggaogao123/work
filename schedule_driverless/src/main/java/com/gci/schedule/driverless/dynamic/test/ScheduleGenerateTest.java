package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ServiceType;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;
import com.gci.schedule.driverless.dynamic.util.MinObuTimeComparator;
import com.gci.schedule.driverless.dynamic.util.TripBeginTimeComparator;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class ScheduleGenerateTest {

	private RouteSchedule routeSchedule;
	
	private ScheduleParam scheduleParam;
	
	//private BusReverseAdjust busReverseAdjust;
	
	private BusAdjustBase busReverseAdjustBase;
	
	private ElectricitySupplement electricitySupplement;
	
	//private StoppingTimeOptimize stoppingTimeOptimize;
	
	private int retryTime;
	
	private boolean elecSupply=false;//补电标识
	
	private static final SimpleDateFormat HM_FORMAT=new SimpleDateFormat("HHmm");
	
	private boolean morningShiftProcessed=false;//早班车已加

	public ScheduleGenerateTest(ScheduleParam scheduleParam) {
		super();
		this.scheduleParam = scheduleParam;
		this.electricitySupplement=new ElectricitySupplement(scheduleParam);
	}
	
	private boolean isTripLessThan30Minute() {
		int wasteTime=scheduleParam.getWasteTime(scheduleParam.getFirstTime(Direction.UP.getValue()), Direction.UP.getValue());
		if((scheduleParam.isLoopLine()&&wasteTime<30)||
			(!scheduleParam.isLoopLine()&&wasteTime*2<30)){
			for(ScheduleParamsClasses scheduleParamsClasses:scheduleParam.getScheduleParamsClassesList()) {
				if(scheduleParamsClasses.getMaxDispatchInterval()>30) {
					scheduleParamsClasses.setMaxDispatchInterval(30L);
				}
			}
			return true;
		}
		return false;
	}

	private void printWasteTime(int direction) {
		Date firstTime = scheduleParam.getFirstTime(direction);
		Date lastTime = scheduleParam.getLatestTime(direction);
		if (firstTime != null && lastTime != null) {
			while (!firstTime.after(lastTime)) {
				Integer wasteTime = scheduleParam.getWasteTime(firstTime, direction);
				System.out.println(direction + "\t" + DateFormatUtil.HM2.getDateString(firstTime) + "\t" + wasteTime);
				firstTime = DateUtil.getDateAddMinute(firstTime, 1);
			}
		}
	}
	
	public RouteSchedule generate(){
		System.out.println("排班开始："+scheduleParam.getRouteId()+"\t"+DateFormatUtil.DATE.getDateString(scheduleParam.getRunDate()));
		routeSchedule=new RouteSchedule(scheduleParam);
		printWasteTime(Direction.UP.getValue());
		printWasteTime(Direction.DOWN.getValue());
		if(!scheduleParam.isLoopLine()) {
			if(scheduleParam.getBusNumberPresetUp()!=null&&scheduleParam.getBusNumberPresetUp()==0) {//上行不出车
				boolean morningShift=false;
				if(scheduleParam.getShiftListPreset()!=null) {
					for(ScheduleShiftPreset shift:scheduleParam.getShiftListPreset()) {
						if(shift.getShiftType()==ShiftType.MORNING_SHIFT.getValue()&&
							shift.getBusNumberUp()!=null&&shift.getBusNumberUp()!=0) {//有早班车
							morningShift=true;
							break;
						}
					}
				}
				if(!morningShift&&!scheduleParam.getFirstTime(Direction.UP.getValue()).after(scheduleParam.getFirstTime(Direction.DOWN.getValue()))) {//上行头车不晚于下行头车,上行没有双班车或早班车
					if(scheduleParam.getBusNumberPresetDown()>1) {
						scheduleParam.setBusNumberPreset(Direction.UP.getValue(), 1);//突头一台做头车
						scheduleParam.setBusNumberPreset(Direction.DOWN.getValue(), scheduleParam.getBusNumberPresetDown()-1);
					}else{
						throw new MyException("-1", "上行首班时间不晚于下行首班时间，上行预设车辆数不能为零！");
					}
				}
			}
			if(scheduleParam.getBusNumberPresetDown()!=null&&scheduleParam.getBusNumberPresetDown()==0) {
				boolean morningShift=false;
				if(scheduleParam.getShiftListPreset()!=null) {
					for(ScheduleShiftPreset shift:scheduleParam.getShiftListPreset()) {
						if(shift.getShiftType()==ShiftType.MORNING_SHIFT.getValue()&&
							shift.getBusNumberDown()!=null&&shift.getBusNumberDown()!=0) {//有早班车
							morningShift=true;
							break;
						}
					}
				}
				if(!morningShift&&!scheduleParam.getFirstTime(Direction.DOWN.getValue()).after(scheduleParam.getFirstTime(Direction.UP.getValue()))) {//下行头车不晚于上行头车
					if(scheduleParam.getBusNumberPresetUp()>1) {
						scheduleParam.setBusNumberPreset(Direction.DOWN.getValue(), 1);//突头一台做头车
						scheduleParam.setBusNumberPreset(Direction.UP.getValue(), scheduleParam.getBusNumberPresetUp()-1);
					}else{
						throw new MyException("-1", "下行首班时间不晚于上行首班时间，下行预设车辆数不能为零！");
					}
				}
			}
			if(scheduleParam.getBusNumberPresetUp()==null) {
				Date firstTimeUp=scheduleParam.getFirstTime(Direction.UP.getValue());
				Date firstTimeDown=scheduleParam.getFirstTime(Direction.DOWN.getValue());
				if(!scheduleParam.isAppearDirection(Direction.UP.getValue())&&
					!firstTimeUp.after(firstTimeDown)){//上行不能出车，并且上行不晚于下行
					scheduleParam.setAppearDirection(Direction.UP.getValue(), true);
				}
				if(!scheduleParam.isAppearDirection(Direction.DOWN.getValue())&&
						!firstTimeDown.after(firstTimeUp)){//下行不能出车，并且下行不晚于上行
					scheduleParam.setAppearDirection(Direction.DOWN.getValue(), true);
				}
			}
			if(scheduleParam.getBusNumberPresetUp()!=null&&
				scheduleParam.getBusNumberPresetDown()!=null) {
				int busNumber4Night=scheduleParam.getBusNumberPreset(Direction.UP.getValue())
					-scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue())
					+scheduleParam.getBusNumberPreset(Direction.DOWN.getValue())
					-scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
				if(scheduleParam.getShiftListPreset()!=null) {
					for(ScheduleShiftPreset shift:scheduleParam.getShiftListPreset()) {
						if(shift.getShiftType()==ShiftType.DOUBLE_SHIFT_MIDDLE_STOP.getValue()||
							shift.getShiftType()==ShiftType.EVENING_SHIFT.getValue()) {//双班中停、晚班
							busNumber4Night+=(shift.getBusNumberUp()+shift.getBusNumberDown());
						}
					}
				}
				if(busNumber4Night==0) {
					throw new MyException("-1", "预设配车数没有可执行末班车");
				}
				if(busNumber4Night==1) {
					Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
					Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
					int direction=Direction.UP.getValue();
					if(latestTimeUp.after(latestTimeDown)) {
						direction=Direction.DOWN.getValue();
					}
					Date arrivalTime=scheduleParam.getArrivalTime(scheduleParam.getLatestTime(direction), direction);
					if(arrivalTime.after(scheduleParam.getLatestTime(1-direction))) {
						throw new MyException("-1", "预设配车数收尾只有一台，无法同时执行两边总站末班车班次");
					}
				}
			}
		}else {//环线
			if(scheduleParam.getBusNumberPreset()!=null) {
				scheduleParam.setBusNumberPreset(Direction.DOWN.getValue(), 0);
				scheduleParam.setBusNumberPreset(scheduleParam.getBusNumberPreset(Direction.UP.getValue()));
			}
		}
		if(!scheduleParam.isBackInsert()&&!scheduleParam.isDoubleFullLineGen()) {
			scheduleParam.setFirstPlanObuTimeLatest();
		}
		if((!scheduleParam.isTestLineFull()&&scheduleParam.isCalculateByHour())||isTripLessThan30Minute()) {
			if(scheduleParam.isLoopLine()) {
				Integer busNumber=scheduleParam.getBusNumberPreset();
				if(busNumber==null)
					busNumber=getBusNumberEstimate4LoopLine();
				else {
					procMorningShiftBus();
					busNumber=scheduleParam.getBusNumberPreset();
				}
				calculate4LoopLine(busNumber);
			}else {
				/*if(scheduleParam.getShiftBusNumber()==0)
					scheduleParam.setAllow2ReverseStop(false);*/
				scheduleParam.generateObuTime();
				calculateByHour();
			}
		}else {
			scheduleParam.generateObuTime();
			scheduleParam.printObuTime(Direction.UP.getValue());
			scheduleParam.printObuTime(Direction.DOWN.getValue());
			routeSchedule.calculateBusConfigNew(scheduleParam);
			/*if(scheduleParam.getRouteId()==10690)
				scheduleParam.setAllow2ReverseStop(true);*/
			if(scheduleParam.getSchedulePlanReference()!=null) {//参照历史出厂时间排班
				calculateByStartWorkReference(scheduleParam.getSchedulePlanReference());
			}else {
				if(scheduleParam.isDoubleFullLineGen()) {
					System.out.println("[新算法]"+scheduleParam.getRouteId()+"\t"+scheduleParam.getRunDate());
					if(scheduleParam.isAppearDirection(Direction.UP.getValue())&&
						scheduleParam.isAppearDirection(Direction.DOWN.getValue())) {
						ScheduleGenerateB4Peek4Full scheduleGenerateB4Peek4Full=new ScheduleGenerateB4Peek4Full(scheduleParam, routeSchedule);
						scheduleGenerateB4Peek4Full.calculate();
					}else {
						ScheduleGenerateOneParkB4Peek4Full scheduleGenerateOneParkB4Peek4Full=new ScheduleGenerateOneParkB4Peek4Full(scheduleParam, routeSchedule);
						scheduleGenerateOneParkB4Peek4Full.calculate();
					}
					if(scheduleParam.isTestLineFull()) {
						busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
					}
					calculateAfterEarlyRushTime();
				}else {
					generateSchedule();	
				}
			}
			if(electricitySupplement.setNusNeed2ElecSupplyList(routeSchedule)) {
				elecSupply=true;
				for(Bus bus:routeSchedule.getTripMap().keySet()) {//初始化车辆当前班别
					ScheduleParamShift shiftType=scheduleParam.getScheduleParamShift(bus.getShiftCode(), bus.getStartDirection(), scheduleParam.getFirstTime(bus.getStartDirection()));
					bus.setShiftType(shiftType);
				}
				Map<Integer,List<Trip>> firstRoundTripMap=reCalculate4InitShift();//重算
				
				for(Integer direction:firstRoundTripMap.keySet()) {
					List<Trip> firstRoundTripList=firstRoundTripMap.get(direction);
					for(int i=0;i<firstRoundTripList.size();) {
						Trip trip=firstRoundTripList.get(i);
						ScheduleParamShift shift=trip.getBus().getShiftType();
						if(shift!=null&&(shift.getShiftType()==ShiftType.MIDDLE_SHIFT.getValue()||shift.getShiftType()==ShiftType.EVENING_SHIFT.getValue())) {//早高峰先过滤中班和晚班
							firstRoundTripList.remove(trip);
							routeSchedule.addShiftBus(trip.getBus().getShiftType(), trip.getBus());
						}else {
							i++;
						}
					}
				}
				electricitySupplement.setRouteSchedule(routeSchedule);
				calculateFromStartWorkTime(firstRoundTripMap,scheduleParam.getEarlyPeakEndTime(),false);
				if(scheduleParam.isTestLineFull()) {
					busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
				}
				if(scheduleParam.isTestLineShort()) {
					busReverseAdjustBase=new BusReverseAdjust4Short(scheduleParam, routeSchedule);
				}
				calculateAfterEarlyRushTime();
				for(Bus bus:routeSchedule.getTripMap().keySet()) {
					List<Trip> busTripList=routeSchedule.getTripList(bus);
					for(int i=0;i<busTripList.size()-1;i++) {
						Trip trip=busTripList.get(i);
						Trip tripNext=busTripList.get(i+1);
						if(trip.getTripEndTime()==null) {
							trip.setTripEndTime(tripNext.getTripBeginTime());
						}
					}
				}
			}
		}
		System.out.println("排班结束："+scheduleParam.getRouteId()+"\t"+DateFormatUtil.DATE.getDateString(scheduleParam.getRunDate()));
		checkLatestTime(Direction.UP.getValue());
		checkLatestTime(Direction.DOWN.getValue());
		resetBusStartDirectionAndOrderNumber();
		addSectionTrip();
		return routeSchedule;
	}
	
	private void addSectionTrip() {
		if(scheduleParam.getRouteId()!=2830)
			return;
		int busNumberSection=0;
		ScheduleHalfHour scheduleHalfHourMax=null;
		Map<ScheduleHalfHour, Integer> sectionTripNumberMap=new HashMap<ScheduleHalfHour, Integer>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			if(scheduleHalfHour.getEmptyBusCutOver()!=null) {
				int classesNumberSection=scheduleHalfHour.getEmptyBusCutOver().getClassesNumberCutOver();
				List<Trip> tripList=routeSchedule.getTripList(scheduleHalfHour.getDirection());
				int tripNumber=0;
				for(Trip trip:tripList) {
					if(DateUtil.isInTimeRange(trip.getTripBeginTime(), DateUtil.getDateHM(scheduleHalfHour.getRunTime()), DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd()))){
						tripNumber++;
					}
				}
				int classesNumberShort=0;
				if(scheduleHalfHour.getShortLineSchedule()!=null)
					classesNumberShort=scheduleHalfHour.getShortLineSchedule().getShortLineClasses();
				int planNumber=scheduleHalfHour.getFullClassesNumber()+classesNumberShort+classesNumberSection;
				if(tripNumber<planNumber) {
					System.out.println(scheduleHalfHour.getDirection()+"\t"+scheduleHalfHour.getRunTime()+"\t"+scheduleHalfHour.getFullClassesNumber()+"\t"+classesNumberShort+"\t"+classesNumberSection+"\t"+tripNumber+"\t"+(planNumber-tripNumber)+"\t"+scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaName()+"\t"+scheduleHalfHour.getShortLineSchedule().getRouteStationName());
					int wasteTime=scheduleParam.getWasteTime(DateUtil.getDateHM(scheduleHalfHour.getRunTime()), scheduleHalfHour.getDirection(), scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId(), scheduleHalfHour.getShortLineSchedule().getRouteStaId());
					int wasteTimeBack=scheduleParam.getWasteTime(DateUtil.getDateHM(scheduleHalfHour.getRunTime()), 1-scheduleHalfHour.getDirection(), scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId(), scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId());
					System.out.println(wasteTime+"\t"+wasteTimeBack);
					int classesNumberSectionTemp=planNumber-tripNumber;
					
					int direction=scheduleHalfHour.getDirection();
					Long firstRouteStaId=scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId();
					Long lastRouteStaId=scheduleHalfHour.getShortLineSchedule().getRouteStaId();
					Long firstRouteStaIdBack=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId();
					Long lastRouteStaIdBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId();
					int turnAroundTime=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
					int turnAroundTimeBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
					Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
					Date arrivalTime=scheduleParam.getArrivalTime(runTime, direction, firstRouteStaId, lastRouteStaId);
					Date obuTimeBack=DateUtil.getDateAddMinute(arrivalTime, turnAroundTime);
					Date arrivalTimeBack=scheduleParam.getArrivalTime(obuTimeBack, 1-direction, firstRouteStaIdBack, lastRouteStaIdBack);
					Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTimeBack, turnAroundTimeBack);
					int busNumberTemp=(int)Math.floor(DateUtil.getMinuteInterval(obuTimeNext, runTime)*classesNumberSectionTemp/30.0);
					if(!DateUtil.getDateHM(scheduleHalfHour.getRunTime()).after(DateUtil.getDateHM("0900"))
							&&busNumberTemp>busNumberSection) {
						busNumberSection=busNumberTemp;
						scheduleHalfHourMax=scheduleHalfHour;
					}
					sectionTripNumberMap.put(scheduleHalfHour, busNumberTemp);
				}
			}
		}
		if(busNumberSection>0) {
			int direction=scheduleHalfHourMax.getDirection();
			Date beginTime=DateUtil.getDateHM("0700");
			ScheduleHalfHour scheduleHalfHour=scheduleParam.getScheduleHalfHour(beginTime, direction);
			if(!sectionTripNumberMap.containsKey(scheduleHalfHour)) {
				scheduleHalfHour=scheduleHalfHourMax;
			}
			Long firstRouteStaId=scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId();
			Long lastRouteStaId=scheduleHalfHour.getShortLineSchedule().getRouteStaId();
			Long firstRouteStaIdBack=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId();
			Long lastRouteStaIdBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId();
			int turnAroundTime=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
			int turnAroundTimeBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();

			Date arrivalTime=scheduleParam.getArrivalTime(beginTime, direction, firstRouteStaId, lastRouteStaId);
			Date obuTimeBack=DateUtil.getDateAddMinute(arrivalTime, turnAroundTime);
			Date arrivalTimeBack=scheduleParam.getArrivalTime(obuTimeBack, 1-direction, firstRouteStaIdBack, lastRouteStaIdBack);
			Date obuTimeNext=DateUtil.getDateAddMinute(arrivalTimeBack, turnAroundTimeBack);
			
			Date beginTimeAft=DateUtil.getDateHM("1600");
			Date arrivalTimeAft=scheduleParam.getArrivalTime(beginTimeAft, direction, firstRouteStaId, lastRouteStaId);
			Date obuTimeBackAft=DateUtil.getDateAddMinute(arrivalTimeAft, turnAroundTime);
			Date arrivalTimeBackAft=scheduleParam.getArrivalTime(obuTimeBackAft, 1-direction, firstRouteStaIdBack, lastRouteStaIdBack);
			Date obuTimeNextAft=DateUtil.getDateAddMinute(arrivalTimeBackAft, turnAroundTimeBack);
			for(int i=0;i<busNumberSection;i++) {
				int startOrderNumber=routeSchedule.newBusOrder(direction);
				Bus bus=new Bus(direction, startOrderNumber);
				List<Trip> busTripList=new ArrayList<Trip>();
				Date obuTime=null;
				if(i==0) {
					obuTime=beginTime;
				}else {
					int interval=DateUtil.getMinuteInterval(obuTimeNext, beginTime)/(busNumberSection-i+1);
					obuTime=DateUtil.getDateAddMinute(beginTime, interval);
					beginTime=obuTime;
				}
				while(obuTime.before(DateUtil.getDateHM("1000"))) {
					scheduleHalfHour=scheduleParam.getScheduleHalfHour(obuTime, direction);
					if(!sectionTripNumberMap.containsKey(scheduleHalfHour)) {
						scheduleHalfHour=scheduleHalfHourMax;
					}
					firstRouteStaId=scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId();
					lastRouteStaId=scheduleHalfHour.getShortLineSchedule().getRouteStaId();
					firstRouteStaIdBack=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId();
					lastRouteStaIdBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId();
					turnAroundTime=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
					turnAroundTimeBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();

					Trip trip=new Trip(bus, direction, obuTime, scheduleParam, firstRouteStaId, lastRouteStaId);
					busTripList.add(trip);
					obuTimeBack=DateUtil.getDateAddMinute(trip.getTripEndTime(), turnAroundTime);
					Trip tripBack=new Trip(bus, 1-direction, obuTimeBack, scheduleParam, firstRouteStaIdBack, lastRouteStaIdBack);
					busTripList.add(tripBack);
					obuTime=DateUtil.getDateAddMinute(tripBack.getTripEndTime(), turnAroundTimeBack);
				}
				
				
				if(i==0) {
					obuTime=beginTimeAft;
				}else {
					int interval=DateUtil.getMinuteInterval(obuTimeNextAft, beginTimeAft)/(busNumberSection-i+1);
					obuTime=DateUtil.getDateAddMinute(beginTimeAft, interval);
					beginTimeAft=obuTime;
				}
				Trip lastTrip=busTripList.get(busTripList.size()-1);
				Trip middleStopTrip=new Trip(bus, Direction.NODIRECTION.getValue(), DateUtil.getDateAddMinute(lastTrip.getTripEndTime(), 5));
				middleStopTrip.setServiceType(ServiceType.SINGLE_CLASSES_MIDDLE_STOP);
				middleStopTrip.setTripEndTime(DateUtil.getDateAddMinute(obuTime, -5));
				busTripList.add(middleStopTrip);
				while(obuTime.before(DateUtil.getDateHM("1900"))) {
					scheduleHalfHour=scheduleParam.getScheduleHalfHour(obuTime, 1-direction);
					if(!sectionTripNumberMap.containsKey(scheduleHalfHour)) {
						scheduleHalfHour=scheduleHalfHourMax;
					}
					if(scheduleHalfHour.getDirection()!=direction) {//晚上空车切入
						firstRouteStaId=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId();
						lastRouteStaId=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId();
						firstRouteStaIdBack=scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId();
						lastRouteStaIdBack=scheduleHalfHour.getShortLineSchedule().getRouteStaId();
						turnAroundTime=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
						turnAroundTimeBack=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
					}else {
						firstRouteStaId=scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaId();
						lastRouteStaId=scheduleHalfHour.getShortLineSchedule().getRouteStaId();
						firstRouteStaIdBack=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getNextFirstRouteStaId();
						lastRouteStaIdBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaId();
						turnAroundTime=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
						turnAroundTimeBack=scheduleHalfHour.getEmptyBusCutOver().getShortLineSchedule().getRouteStaTurn().getTurnAroundTime();
					}
					Trip trip=new Trip(bus, direction, obuTime, scheduleParam, firstRouteStaId, lastRouteStaId);
					busTripList.add(trip);
					obuTimeBack=DateUtil.getDateAddMinute(trip.getTripEndTime(), turnAroundTime);
					Trip tripBack=new Trip(bus, 1-direction, obuTimeBack, scheduleParam, firstRouteStaIdBack, lastRouteStaIdBack);
					busTripList.add(tripBack);
					obuTime=DateUtil.getDateAddMinute(tripBack.getTripEndTime(), turnAroundTimeBack);
				}
				routeSchedule.addBusTripList(bus, busTripList);
			}
			resetBusStartDirectionAndOrderNumber();
		}
	}
	
	private void checkLatestTime(int direction) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(latestTime!=null) {
			Trip lastTripFull=routeSchedule.getTripFullLast(direction);
			if(lastTripFull!=null&&!lastTripFull.getTripBeginTime().equals(latestTime)) {//末班车未发
				//throw new MyException("-1", "缺少"+(direction==Direction.UP.getValue()?"上行":"下行")+"末班班次");
			}
		}
	}
	
	private void dealLatestBusTrip(int direction) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(latestTime!=null) {
			Trip lastTripFull=routeSchedule.getTripFullLast(direction);
			if(lastTripFull.getTripBeginTime().before(latestTime)) {//缺少尾车班次
				int directionReverse=1-direction;
				Date latestTimeReverse=scheduleParam.getLatestTime(directionReverse);
				if(latestTimeReverse==null) {//环线
					directionReverse=direction;
				}
				List<Trip> busQueueTo=routeSchedule.getBusQueue(directionReverse);
				if(!busQueueTo.isEmpty()) {
					Trip busArrival=busQueueTo.get(0);
					if(busArrival.getTripEndTime()!=null&&busArrival.getTripEndTime().before(latestTime)) {
						Trip trip=new Trip(busArrival.getBus(), direction, latestTime, scheduleParam, lastTripFull);
						routeSchedule.addTrip(trip);
						busQueueTo.remove(busArrival);
					}
				}
			}
		}
	}
	
	private List<Date> getFixedObuTimeList(int direction){
		List<Date> obuTimeList=new ArrayList<Date>();
		Date latestTime=scheduleParam.getLatestTime(direction);
		int maxInterval=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(scheduleParam.getLatestTime(1-direction), -1), 1-direction);//反方向的最大发班间隔
		Date latestObuTime=scheduleParam.getLatestTime(1-direction);
		while(true) {
			Date arrivalTime=scheduleParam.getArrivalTime(latestObuTime, 1-direction);
			int restTime=scheduleParam.getMinRestTime(arrivalTime, direction);
			Date nextObuTimeMin=DateUtil.getDateAddMinute(arrivalTime, restTime);
			if(!nextObuTimeMin.after(latestTime)) {
				obuTimeList.add(0,latestObuTime);
				break;
			}
			obuTimeList.add(0,latestObuTime);
			latestObuTime=DateUtil.getDateAddMinute(latestObuTime, -maxInterval);
		}
		return obuTimeList;
	}

	private Map<Integer, List<Trip>> getFirstRoundTripMap(List<SchedulePlan> schedulePlanList){
		Map<Bus, List<Trip>> tripMap=new HashMap<Bus, List<Trip>>();
		for(SchedulePlan schedulePlan:schedulePlanList) {
			Trip trip=new Trip();
			Bus bus=null;
			for(Bus busTemp:tripMap.keySet()) {
				if(schedulePlan.getStartDirection().equals(String.valueOf(busTemp.getStartDirection()))&&
						busTemp.getStartOrderNumber()==schedulePlan.getStartOrderNumber()) {
					bus=busTemp;
					break;
				}
			}
			if(bus==null) {
				bus=new Bus(Integer.valueOf(schedulePlan.getStartDirection()), Integer.valueOf(schedulePlan.getStartOrderNumber()));
			}
			List<Trip> busTripList=tripMap.get(bus);
			if(busTripList==null) {
				busTripList=new ArrayList<Trip>();
				tripMap.put(bus, busTripList);
			}
			trip.setBus(bus);
			trip.setTripBeginTime(DateUtil.getCalendarHM(DateFormatUtil.HM.getDateString(schedulePlan.getPlanTime())).getTime());
			trip.setDirection(Integer.valueOf(schedulePlan.getDirection()));
			busTripList.add(trip);
		}
		Trip firstBus2TripUp=null;
		Trip firstBus2TripDown=null;
		for(Bus bus:tripMap.keySet()) {
			List<Trip> busTripList=tripMap.get(bus);
			busTripListSort(busTripList);
			Trip firstRoundTrip=busTripList.get(0);
			if(DateFormatUtil.HM.getDateString(firstRoundTrip.getTripBeginTime()).
					equals(DateFormatUtil.HM.getDateString(scheduleParam.getFirstTime(firstRoundTrip.getDirection())))
					&&busTripList.size()>1) {
				if(firstRoundTrip.getDirection()==Direction.UP.getValue()) {
					firstBus2TripDown=busTripList.get(1);//回头
				}else if(firstRoundTrip.getDirection()==Direction.DOWN.getValue()) {
					firstBus2TripUp=busTripList.get(1);//回头
				}
			}
		}
		Map<Integer,List<Trip>> firstRoundTripMap=new HashMap<Integer, List<Trip>>();
		for(Bus bus:tripMap.keySet()) {
			List<Trip> busTripList=tripMap.get(bus);
			busTripListSort(busTripList);
			Trip firstRoundTrip=busTripList.get(0);
			List<Trip> firstRoundPreTripList=firstRoundTripMap.get(firstRoundTrip.getDirection());
			if(firstRoundPreTripList==null) {
				firstRoundPreTripList=new ArrayList<Trip>();
				firstRoundTripMap.put(firstRoundTrip.getDirection(), firstRoundPreTripList);
			}
			Trip preTrip=new Trip();
			preTrip.setBus(firstRoundTrip.getBus());
			preTrip.setNextObuTimeMin(firstRoundTrip.getTripBeginTime());
			if(firstRoundTrip.getDirection()==Direction.UP.getValue()){
				if(firstBus2TripUp!=null&&
						firstBus2TripUp.getTripBeginTime().before(firstRoundTrip.getTripBeginTime())) {
					preTrip.setReverseInsert(true);
				}
			}else if(firstRoundTrip.getDirection()==Direction.DOWN.getValue()) {
				if(firstBus2TripDown!=null&&
						firstBus2TripDown.getTripBeginTime().before(firstRoundTrip.getTripBeginTime())) {
					preTrip.setReverseInsert(true);
				}
			}
			System.out.println(firstRoundTrip.getBusName()+"\t"+firstRoundTrip.getTripBeginTime());
			firstRoundPreTripList.add(preTrip);
		}
		for(Integer direction:firstRoundTripMap.keySet()) {
			List<Trip> firstRoundTripList=firstRoundTripMap.get(direction);
			Collections.sort(firstRoundTripList, new MinObuTimeComparator());
		}
		return firstRoundTripMap;
	}
	
	private void calculateByStartWorkReference(List<SchedulePlan> schedulePlanList) {
		Map<Integer,List<Trip>> firstRoundTripMap=getFirstRoundTripMap(schedulePlanList);
		if(scheduleParam.isLoopLine()) {
			checkfirstRoundTrip4Loop(firstRoundTripMap.get(Direction.UP.getValue()));
			initBusQueue(Direction.UP.getValue(), firstRoundTripMap.get(Direction.UP.getValue()));
			for(Trip trip:firstRoundTripMap.get(Direction.UP.getValue())) {
				routeSchedule.addShortTripBack(trip);
			}
			calculate4LoopLine();
		}else {
			calculateFromStartWorkTime(firstRoundTripMap,scheduleParam.getEarlyPeakEndTime(),true);
			//busReverseAdjust=new BusReverseAdjust(scheduleParam,routeSchedule);
			if(scheduleParam.isTestLineFull()) {
				busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
			}
			if(scheduleParam.isTestLineShort()) {
				busReverseAdjustBase=new BusReverseAdjust4Short(scheduleParam, routeSchedule);
			}
			calculateAfterEarlyRushTime();
		}
	}
	
	private void addBusProc4LoopLine() {
		Date obuTimeLatestUp=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
		Date firstTime=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
		if(obuTimeLatestUp!=null) {
			if(obuTimeLatestUp.before(firstTime)) {
				throw new MyException("-2", "最晚出车时间不能早于首班车");
			}
			List<Trip> tripList=routeSchedule.getTripList(Direction.UP.getValue());
			routeSchedule.tripListSort(tripList);
			while(true) {
				Trip firstTrip=null;
				for(Trip trip:tripList) {
					if(trip.isFirstTask()&&trip.getTripBeginTime().after(obuTimeLatestUp)) {
						firstTrip=trip;
						break;
					}
				}
				if(firstTrip!=null) {
					Trip trip=getTripLateBus4LoopLine(firstTrip);
					tripList.add(trip);
					List<Trip> busTripList=routeSchedule.getTripList(firstTrip.getBus());
					busTripList.add(trip);
					routeSchedule.tripListSort();
					busTripListSort(busTripList);
				}else {
					break;
				}
			}
			
		}
	}
	
	private Trip getTripLateBus4LoopLine(Trip firstTrip) {
		Trip tripLast=firstTrip.getLastTripFull();
		while(tripLast.isFirstTask()||tripLast.getPreTrip().isShortLine()) {
			tripLast=tripLast.getLastTrip();//找到上一趟到站车发的班次
			if(tripLast==null) {
				break;
			}
		}
		Date obuTime=null;
		if(tripLast==null) {//前面没有到站车
			obuTime=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
		}else {
			Trip tripFront=tripLast.getPreTrip();
			Trip tripAfter=tripFront.getNextTripFull();
			if(tripFront==null||tripAfter==null)
				System.out.println("aaa");
			int interval=DateUtil.getMinuteInterval(tripFront.getTripBeginTime(), tripAfter.getTripBeginTime())/2;
			obuTime=DateUtil.getDateAddMinute(tripFront.getTripBeginTime(), interval);
		}
		Trip trip=new Trip(firstTrip.getBus(), Direction.UP.getValue(), obuTime);
		trip.setNextObuTimeMin(scheduleParam);
		return trip;
	}
	
	
	private void addBusProc() {
		if(scheduleParam.isLoopLine()) {
			addBusProc4LoopLine();
		}else {
			boolean loopUp=true;
			boolean loopdown=true;
			while(loopUp||loopdown) {
				List<Trip> upTripList=routeSchedule.getTripList(Direction.UP.getValue());
				loopUp=addBusProc(upTripList);
				List<Trip> downTripList=routeSchedule.getTripList(Direction.DOWN.getValue());
				loopdown=addBusProc(downTripList);
				retryTime++;
				if(retryTime>10000)
					throw new MyException("-2", "计划生成失败，请联系开发人员"+scheduleParam.getRouteId()+"|"+new Date());
			}
			resetTripBeginTimeByLastMinObuTime();
			resetBusStartDirectionAndOrderNumber();
			if(!scheduleParam.isLoopLine()) {
				Date obuTimeLatestUp=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
				Date obuTimeLatestDown=scheduleParam.getFirstPlanObuTimeLatest(Direction.DOWN.getValue());
				if(obuTimeLatestUp==null||obuTimeLatestDown==null) {//单边出车
					int direction=Direction.UP.getValue();
					if(obuTimeLatestUp==null) {//只能下行出车
						direction=Direction.DOWN.getValue();
					}
					for(Bus bus:routeSchedule.getTripMap().keySet()) {
						List<Trip> busTripList=routeSchedule.getTripList(bus);
						Trip firstTrip=busTripList.get(0);
						if(firstTrip.getDirection()!=direction) {
							Trip trip=new Trip(firstTrip.getBus(), direction, scheduleParam.getFirstTime(direction));
							trip.setNextObuTimeMin(scheduleParam);
							busTripList.add(0,trip);
							routeSchedule.getTripList(direction).add(trip);
						}
					}
				}
			}
		}
	}
	
	private void resetBusStartDirectionAndOrderNumber() {
		List<Trip> upStartTripList=new ArrayList<Trip>();
		List<Trip> downStartTripList=new ArrayList<Trip>();
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripMap().get(bus);
			if(busTripList.isEmpty())
				System.out.println("aaa");
			Trip startTrip=busTripList.get(0);
			for(Trip trip:busTripList) {
				if(trip.getTripBeginTime().before(startTrip.getTripBeginTime())) {
					startTrip=trip;
				}
			}
			if(startTrip.getDirection()==0) {
				upStartTripList.add(startTrip);
			}else {
				downStartTripList.add(startTrip);
			}
		}
		Collections.sort(upStartTripList, new TripBeginTimeComparator());
		Collections.sort(downStartTripList, new TripBeginTimeComparator());
		System.out.println("上行======================================");
		for(int i=0;i<upStartTripList.size();i++) {
			Trip trip=upStartTripList.get(i);
			Bus bus=trip.getBus();
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			Collections.sort(busTripList,new TripBeginTimeComparator());
			bus.setStartDirection(0);
			bus.setStartOrderNumber(i+1);
			for(int j=0;j<busTripList.size();j++) {
				Trip trip2=busTripList.get(j);
				trip2.setBus(bus);
				if(trip2.getTurnTrip()!=null)
					trip2.getTurnTrip().setBus(bus);
				if(j==0) {
					trip2.setFirstTask(true);
				}else {
					trip2.setFirstTask(false);
				}
			}
			System.out.println(trip.getDirection()+"\t"+bus.getStartOrderNumber()+"\t"+DateFormatUtil.HM.getDateString(trip.getTripBeginTime()));
		}
		System.out.println("下行======================================");
		for(int i=0;i<downStartTripList.size();i++) {
			Trip trip=downStartTripList.get(i);
			Bus bus=trip.getBus();
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			Collections.sort(busTripList,new TripBeginTimeComparator());
			bus.setStartDirection(1);
			bus.setStartOrderNumber(i+1);
			for(int j=0;j<busTripList.size();j++) {
				Trip trip2=busTripList.get(j);
				trip2.setBus(bus);
				if(trip2.getTurnTrip()!=null)
					trip2.getTurnTrip().setBus(bus);
				if(j==0) {
					trip2.setFirstTask(true);
				}else {
					trip2.setFirstTask(false);
				}
			}
			System.out.println(trip.getDirection()+"\t"+bus.getStartOrderNumber()+"\t"+DateFormatUtil.HM.getDateString(trip.getTripBeginTime()));
		}
		routeSchedule.resetBusOrder();
	}
	
	private void resetTripBeginTimeByLastMinObuTime() {
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripMap().get(bus);
			Collections.sort(busTripList, new TripBeginTimeComparator());
			Trip preTrip=null;
			for(Trip trip:busTripList) {
				if(preTrip!=null&&trip.getTripBeginTime().before(preTrip.getNextObuTimeMin())) {
					if(trip.getDirection()!=Direction.NODIRECTION.getValue()) {
						trip.setTripBeginTime(preTrip.getNextObuTimeMin());
						trip.setNextObuTimeMin(scheduleParam);
					}
				}
				preTrip=trip;
			}
		}
	}
	
	private void procMorningShiftBus() {
		if(scheduleParam.getBusNumberPreset()!=null&&!morningShiftProcessed) {
			List<ScheduleShiftPreset> shiftList=scheduleParam.getShiftListPreset();
			if(shiftList!=null) {
				for(ScheduleShiftPreset shift:shiftList) {
					if(shift.getShiftType()==ShiftType.MORNING_SHIFT.getValue()) {
						if(scheduleParam.getBusNumberPresetUp()!=null) {
							if(shift.getBusNumberUp()!=null&&shift.getBusNumberUp()!=0) {
								ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.UP.getValue(), scheduleParam.getFirstTime(Direction.UP.getValue()));
								if(!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(scheduleParam.getFirstTime(Direction.UP.getValue()))) {
									scheduleParam.setBusNumberPreset(Direction.UP.getValue(),scheduleParam.getBusNumberPresetUp()+shift.getBusNumberUp());
								}
							}
							if(shift.getBusNumberDown()!=null&&shift.getBusNumberDown()!=0) {
								ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.DOWN.getValue(), scheduleParam.getFirstTime(Direction.DOWN.getValue()));
								if(!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(scheduleParam.getFirstTime(Direction.DOWN.getValue()))) {
									scheduleParam.setBusNumberPreset(Direction.DOWN.getValue(),scheduleParam.getBusNumberPresetDown()+shift.getBusNumberDown());
								}
							}
							int busNumberPresetUp=scheduleParam.getBusNumberPresetUp();
							int busNumberPresetDown=scheduleParam.getBusNumberPresetDown();
							scheduleParam.setBusNumberPreset(busNumberPresetUp+busNumberPresetDown);
						}else {//三汽给总数
							scheduleParam.setBusNumberPreset(scheduleParam.getBusNumberPreset()+shift.getBusNumberUp()+shift.getBusNumberDown());
						}
						morningShiftProcessed=true;
					}
				}
			}
		}
	}
	
	public void generateSchedule(){
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
			if(runTime.after(DateUtil.getCalendarHM("1000").getTime()))
				break;
			if(runTime.equals(DateUtil.getCalendarHM("1000").getTime())){
				//FirstPlanLateProcess firstPlanLateProcess=new FirstPlanLateProcess(routeSchedule, scheduleParam);
				//firstPlanLateProcess.process4LoopLine();
				/*if(1==1)
					return;*/
				procMorningShiftBus();
				int busNumber=routeSchedule.getBusNumber();
				if(scheduleParam.getBusNumberPreset()==null&&busNumber<scheduleParam.getBusNumberConfig()) {//早高峰配车数达不到整天最高配车数，增加早高峰班次重算
					int minBusNumber=getBusNumberConfig(scheduleParam.getEarlyPeakEndTime());
					if(busNumber<minBusNumber) {//晚上的配车比早上大
						busNumber=minBusNumber;
						scheduleParam.setBusNumberPreset(busNumber);
					}
					Integer busNumberSinglePresetUp=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
					Integer busNumberSinglePresetDown=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
					if(busNumberSinglePresetUp!=null&&busNumberSinglePresetDown!=null) {
						if(busNumberSinglePresetUp+busNumberSinglePresetDown>scheduleParam.getBusNumberSingle()) {
							scheduleParam.setBusNumberPreset(busNumber+(busNumberSinglePresetUp+busNumberSinglePresetDown-scheduleParam.getBusNumberSingle()));//加上单班差额，防止单班设置过多没车
						}
					}
				}
				if(!scheduleParam.isLoopLine()) {
					addBusProc();
					optimizeRestTime(scheduleHalfHourList);
				}else {
					addBusProc4LoopLine();
				}
				busNumber=routeSchedule.getBusNumber();
				if(scheduleParam.getBusNumberPreset()!=null&&busNumber!=scheduleParam.getBusNumberPreset()) {
					if(busNumber<scheduleParam.getBusNumberPreset()) {//计算的配车数比预设值少，增加班次
						reCalculateByBusNumberIncrease(scheduleParam.getBusNumberPreset());
					}else if(busNumber>scheduleParam.getBusNumberPreset()){//计算的配车数比预设值多，减少班次
						boolean reCalculate=reCalculateByBusNumberReduce();
						if(reCalculate) {
							return;
						}
					}
				}
				proStartBus();//处理出场车辆数
				Map<Integer,List<Trip>> firstRoundTripMap=reCalculate4InitShift();//重算
				if(scheduleParam.isLoopLine()) {
					busNumber=firstRoundTripMap.get(Direction.UP.getValue()).size();
					checkfirstRoundTrip4Loop(firstRoundTripMap.get(Direction.UP.getValue()));
					initBusQueue(Direction.UP.getValue(), firstRoundTripMap.get(Direction.UP.getValue()));
					for(Trip trip:firstRoundTripMap.get(Direction.UP.getValue())) {
						routeSchedule.addShortTripBack(trip);
					}
					scheduleParam.setBusNumberSingle(busNumber-scheduleParam.getBusNumberConfig()+scheduleParam.getBusNumberSingle());
					calculate4LoopLine();
					return;
				}
				/*if(scheduleParam.getBusNumberPreset()!=null) {
					ScheduleGenerateB4Peek4Full scheduleGenerateB4Peek4Full=new ScheduleGenerateB4Peek4Full(scheduleParam, routeSchedule);
					scheduleGenerateB4Peek4Full.calculate();
				}else {*/
					calculateFromStartWorkTime(firstRoundTripMap,scheduleParam.getEarlyPeakEndTime(),false);
					resetBusStartDirectionAndOrderNumber();
					busNumber=routeSchedule.getTripMap().size();
					scheduleParam.setBusNumberSingle(busNumber-scheduleParam.getBusNumberConfig()+scheduleParam.getBusNumberSingle());
				//}
				break;
				//return;
			}
			calculateRushTime(scheduleHalfHour);
			//calculate(scheduleHalfHour);
		}
		//busReverseAdjust=new BusReverseAdjust(scheduleParam,routeSchedule);
		if(scheduleParam.isTestLineFull()) {
			busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
		}
		if(scheduleParam.isTestLineShort()) {
			busReverseAdjustBase=new BusReverseAdjust4Short(scheduleParam, routeSchedule);
			//stoppingTimeOptimize=new StoppingTimeOptimize(scheduleParam, routeSchedule, busReverseAdjustBase);
		}
		boolean success=calculateAfterEarlyRushTime();
		if(!success) {
			reCalculate(scheduleParam.getEarlyPeakEndTime());//重算
			calculateAfterEarlyRushTime();
		}
	}
	
	private void checkfirstRoundTrip4Loop(List<Trip> firstTripList) {

		Date obuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
		Date arrivalTime=scheduleParam.getArrivalTime(scheduleParam.getFirstTime(Direction.UP.getValue()), Direction.UP.getValue());
		if(arrivalTime.after(obuTimeLatest)) {
			Date obuTimeLast=null;
			for(int i=0;i<firstTripList.size();i++) {
				Trip firstTrip=firstTripList.get(i);
				if(obuTimeLast==null) {
					obuTimeLast=scheduleParam.getFirstTime(Direction.UP.getValue());
				}else {
					int interval=(int)Math.ceil(DateUtil.getMinuteInterval(obuTimeLast, obuTimeLatest)*1.0/(firstTripList.size()-i));
					obuTimeLast=DateUtil.getDateAddMinute(obuTimeLast, interval);
				}
				firstTrip.setNextObuTimeMin(obuTimeLast);
				System.out.println(obuTimeLast);
			}
		}
		System.out.println("aaa");
	}
	
	private void initBusQueue4LoopLineAfterEarlyPeek() {
		Date firstTime=scheduleParam.getFirstTime(Direction.UP.getValue());
		Date firstBusArrivalTime=scheduleParam.getArrivalTime(firstTime, Direction.UP.getValue());
		int restTime=scheduleParam.getMinRestTime(firstBusArrivalTime, Direction.UP.getValue());
		Date minObuTime=DateUtil.getDateAddMinute(firstBusArrivalTime, restTime);
		int busNum=scheduleParam.getBusNumberConfig();
		if(scheduleParam.getBusNumberPresetUp()!=null) {
			busNum=scheduleParam.getBusNumberPresetUp();
		}
		int interval=(int)Math.ceil(DateUtil.getMinuteInterval(minObuTime, firstTime)*1.0/busNum);
		
		List<Trip> bus_to_queue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		Bus firstBus=new Bus(Direction.UP.getValue(), routeSchedule.newBusOrder(Direction.UP.getValue()));
		Trip firstTrip=new Trip(firstBus, Direction.UP.getValue(), firstTime);
		firstTrip.setNextObuTimeMin(scheduleParam);
		firstTrip.setFirstRouteStaId(scheduleParam.getFirstRouteStaId(Direction.UP.getValue()));
		firstTrip.setLastRouteStaId(scheduleParam.getFirstRouteStaId(Direction.UP.getValue()));
		for(int i=1;i<busNum;i++) {
			Bus bus=new Bus(Direction.UP.getValue(), routeSchedule.newBusOrder(Direction.UP.getValue()));
			Trip trip=new Trip();
			trip.setBus(bus);
			trip.setNextObuTimeMin(DateUtil.getDateAddMinute(firstTime, i*interval));
			bus_to_queue.add(trip);
		}
		routeSchedule.addTrip(firstTrip);
	}
	
	private boolean shortLineAdjust(int direction,Integer intervalAvg) {
		if(busReverseAdjustBase.getLatestBusMain()!=null)
			return false;//尾车确定后，不再调车位，防止把车调反了
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		Trip tripArrival=bus_queue_to.get(0);
		Bus bus=tripArrival.getBus();
		Trip tripFullLast=routeSchedule.getTripFullLast(direction);
		if(tripArrival.getNextObuTimeMin().before(scheduleParam.getLatePeakEndTime())&&!bus.isSingleClass()&&intervalAvg!=null&&
				tripArrival.getBus()!=busReverseAdjustBase.getLatestBusMain()&&tripArrival.getBus()!=busReverseAdjustBase.getLatestBusSecondary()) {
			if(!tripFullLast.getBus().isSingleClass()||(bus_queue_to.size()>1&&!bus_queue_to.get(1).getBus().isSingleClass())) {//前后车不能同时是单班车
				if(bus_queue_to.size()>2) {
					Date tripEndTime=null;
					Date tripBeginTime=null;
					Trip trip3=bus_queue_to.get(2);
					if(trip3.getTripEndTime()==null) {
						if(bus_queue_to.size()>3) {
							Trip trip4=bus_queue_to.get(3);
							tripEndTime=trip4.getTripEndTime();
							tripBeginTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), (int)(4*intervalAvg));
						}
					}else {
						tripEndTime=trip3.getTripEndTime();
						tripBeginTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), (int)(3*intervalAvg));
					}
					if(tripEndTime!=null&&tripEndTime.before(tripBeginTime)) {
						int restTime=DateUtil.getMinuteInterval(tripEndTime, tripBeginTime);
						int maxRestTime=scheduleParam.getMaxRestTime(tripEndTime, direction);
						if(restTime>maxRestTime&&restTime>=3*intervalAvg) {//停站时间超过发班间隔3倍
							List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
							RouteStaTurn shortLineAdjust=null;
							Date arrivalTime=null;
							for(RouteStaTurn routeStaTurn:routeStaTurnList) {//找最早回来的调头点
								Date arrivalTimeTemp=scheduleParam.getArrivalTime(tripArrival.getNextObuTimeMin(), routeStaTurn);
								if(!scheduleParam.isTestLineShort()||!busReverseAdjustBase.isBusReverse(bus.getStartDirection(), arrivalTimeTemp, direction)) {
									if(arrivalTime==null||arrivalTimeTemp.before(arrivalTime)) {
										shortLineAdjust=routeStaTurn;
										arrivalTime=arrivalTimeTemp;
									}
								}
							}
							if(shortLineAdjust!=null) {
								tripArrival.setShortLineAdjust(shortLineAdjust);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private void calculateNormalTime4ShortLine(ScheduleHalfHour scheduleHalfHour) {
		if(scheduleHalfHour.getRunTime().equals("2100"))
			System.out.println("aaa");
		int direction=scheduleHalfHour.getDirection();
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		Trip lastTrip=routeSchedule.getTripLast(direction);
		if(lastTrip.getTripBeginTime().equals(scheduleParam.getLatestTime(direction)))
			return;
		busReverseAdjustBase.setLatestBus();
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		busReverseAdjustBase.terminalAdjust(direction);
		if(!elecSupply) {//第二次算补电是不在选择单班车，用第一次的
			setQuitMark4MiddleStop(scheduleHalfHour, bus_queue_to, busReverseAdjustBase.getLatestBusMain());
		}
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(runTime.after(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(), Calendar.MINUTE, -60))) {//准备单班复行
			singleClassRecovery(lastTrip, bus_queue_to);
			//singleClassRecoveryNormalNew(direction);
		}
		if(bus_queue_to.isEmpty())
			return;
		if(scheduleParam.getEndDirection()!=null&&
				scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {//双边总站停场才用调位
			busReverseAdjustBase.busReverseAdjust4Short(scheduleHalfHour);
		}
		//stoppingTimeOptimize.setShortLineAdjust(scheduleHalfHour);
		List<Trip> busAdjustList=new ArrayList<Trip>();
		for(Trip trip:bus_queue_to) {
			if(!trip.isQuitMark()) {
				if(trip.getShortLineAdjust()!=null) {
					busAdjustList.add(trip);
				}
			}
		}
		for(Trip trip:bus_queue_to) {
			ScheduleParamShift shift=trip.getBus().getShiftType();
			if(busReverseAdjustBase.isLatestBus(trip.getBus()))
				continue;
			if(trip.getShortLineAdjust()==null&&shift!=null&&shift.getEndTime()!=null&&scheduleParam.isEndDirectionTrip(trip)) {
				if(DateUtil.getDateHM(shift.getEndTime()).before(scheduleParam.getLatestTime(1-trip.getBus().getStartDirection()))) {//下班时间在末班前才短线调位
					Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(direction, trip.getNextObuTimeMin());
					if(obuTimeNextRound!=null&&
							!obuTimeNextRound.before(DateUtil.getDateAddMinute(DateUtil.getDateHM(shift.getEndTime()), 60))) {//再走全程太晚下班了
						busAdjustList.add(trip);
					}
				}
			}
		}
		for(Trip trip:busAdjustList) {//先移除
			bus_queue_to.remove(trip);
			electricitySupplement.setBusElecSupply(trip);
		}
		if(scheduleParam.getEndDirection()!=null)//中途出场的先不早收
			busReverseAdjustBase.setQuitMarkOffDutyEarly(direction);
		electricitySupplement.setBusElecSupply(direction, bus_queue_to);
		lastTrip=routeSchedule.getTripFullLast(direction);
		if(bus_queue_to.isEmpty())
			return;
		Trip tripArrival=bus_queue_to.get(0);
		List<Trip> tripLongList=new ArrayList<Trip>();
		Integer intervalAvg=busReverseAdjustBase.getDispatchInterval(direction);
		while(tripArrival.getNextObuTimeMin().before(runTimeEnd)||
				tripArrival.getNextObuTimeMin().equals(latestTime)||
				(tripArrival.getNextObuTimeMin().after(latestTime)&&lastTrip.getTripBeginTime().before(latestTime))) {
			if(DateUtil.getDateHM("2222").equals(tripArrival.getTripEndTime())) {
				System.out.println("aaa");
			}
			lastTrip=routeSchedule.getTripFullLast(direction);
			if(lastTrip.getTripBeginTime().equals(latestTime)) {
				break;
			}
			if(tripArrival.getTripEndTime()!=null&&!tripArrival.getTripEndTime().before(latestTime)){//过滤晚于末班车时间，防止后面有车可以发
				bus_queue_to.remove(tripArrival);
				if(bus_queue_to.isEmpty())
					break;
				tripArrival=bus_queue_to.get(0);
				continue;
			}
			if(tripArrival.isQuitMark()&&bus_queue_to.size()>1) {
				boolean isLastRound=false;
				for(Trip tripArrivalNext:bus_queue_to) {
					if(tripArrivalNext.getTripEndTime()!=null&&
						tripArrivalNext.getTripEndTime().after(latestTime)) {
						isLastRound=true;
						break;
					}
				}
				if(isLastRound) {
					List<Trip> busQueueToNotQuit=new ArrayList<Trip>();
					for(Trip tripArrivalNext:bus_queue_to) {
						if(!tripArrivalNext.isQuitMark()&&
								((tripArrivalNext.getTripEndTime()!=null&&
							tripArrivalNext.getTripEndTime().before(latestTime))||
								!tripArrival.getNextObuTimeMin().after(latestTime))){
							busQueueToNotQuit.add(tripArrivalNext);
						}
					}
					if(busQueueToNotQuit.isEmpty()) {
						tripArrival.setQuitMark(false);//保尾车
						Trip trip=new Trip(tripArrival.getBus(), direction, latestTime, scheduleParam, lastTrip);
						lastTrip=routeSchedule.getTripLast(direction);//重新获取上一班，有可能是短线
						routeSchedule.addTrip(trip);
						tripLongList.add(trip);
						trip.setLastTrip(lastTrip);
						trip.setPreTrip(tripArrival);
						bus_queue_to.remove(tripArrival);
						if(bus_queue_to.isEmpty())
							break;
						tripArrival=bus_queue_to.get(0);
						continue;
					}
				}
			}
			if(!tripArrival.isQuitMark()) {
				if(shortLineAdjust(direction, intervalAvg)) {//总站车太密，短线调位
					boolean shortLineAdjust=true;
					if(!scheduleParam.isEndDirectionTrip(tripArrival)) {//对面入场
						Bus bus=tripArrival.getBus();
						Date endTime=null;
						if(bus.isSingleClass()||bus.getShiftType()!=null) {
							if(bus.isSingleClass()) {
								if(bus.isHasMiddleStop()) {
									endTime=scheduleParam.getMiddleStopOffDutyBeginTime();
								}else {
									endTime=scheduleParam.getMiddleStopBeginTime();
								}
							}else {
								endTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
							}
							Date obuTimeNext=scheduleParam.getMinObuTimeNext(direction, tripArrival.getNextObuTimeMin());
							if(obuTimeNext!=null&&!obuTimeNext.before(endTime)) {
								shortLineAdjust=false;
							}
						}
					}
					if(shortLineAdjust) {
						bus_queue_to.remove(tripArrival);
						busAdjustList.add(tripArrival);
						Collections.sort(busAdjustList, new MinObuTimeComparator());
						tripArrival=bus_queue_to.get(0);
						continue;
					}
				}
				Bus bus=tripArrival.getBus();
				List<Trip> busAvailable=new ArrayList<Trip>();
				for(Trip tripTemp:bus_queue_to) {//过滤退出车辆
					if(!tripTemp.isQuitMark())
						busAvailable.add(tripTemp);
				}
				if(DateUtil.getDateHM("2223").equals(tripArrival.getTripEndTime()))
					System.out.println("aaa");
				Date obuTime=null;
				Integer interval=null;
				if(busReverseAdjustBase.isLastRound(direction)) {
					interval=busReverseAdjustBase.getDispatchInterval(direction);
				}else {
					if(busReverseAdjustBase.isLastCircle(direction)) {
						interval=busReverseAdjustBase.getDispatchInterval(direction);
						for(int i=0;i<busAvailable.size();i++) {
							Trip tripArrivalTemp=busAvailable.get(i);
							if(tripArrivalTemp.getTripEndTime()!=null) {
								Date obuTimeMin=DateUtil.getDateAddMinute(tripArrivalTemp.getTripEndTime(), 1);
								if(obuTimeMin.after(lastTrip.getTripBeginTime())) {
									int intervalTemp=DateUtil.getMinuteInterval(lastTrip.getTripBeginTime(), obuTimeMin)/(i+1);
									if(intervalTemp>interval) {
										interval=intervalTemp;
									}
								}
							}
						}
					}else {
						boolean isLastRound=false;
						if(busReverseAdjustBase.getLatestBusMain()==null&&
							busReverseAdjustBase.getLatestBusSecondary()==null) {//还没定尾车，看是不是最后一轮
							for(int i=busAvailable.size()-1;i>=0;i--) {
								Trip tripTemp=busAvailable.get(i);
								if(tripTemp.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
									isLastRound=true;
									break;
								}
							}
							if(isLastRound) {
								List<Trip> tripArrivalRunList=new ArrayList<Trip>();
								for(Trip tripTemp:busAvailable) {
									if(!tripTemp.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
										tripArrivalRunList.add(tripTemp);
									}
								}
								tripArrivalRunList.get(tripArrivalRunList.size()-1).setNextObuTimeMin(scheduleParam.getLatestTime(direction));
								if(tripArrivalRunList.size()>1) {
									tripArrivalRunList.remove(0);
									interval=getObuTimeInterval(tripArrivalRunList, lastTrip);
								}else {
									interval=DateUtil.getMinuteInterval(lastTrip.getTripBeginTime(), scheduleParam.getLatestTime(direction));
								}
							}
							
						}
						if(!isLastRound) {
							interval=getObuTimeIntervalNew(busAvailable, lastTrip);//按最小停站时间计算的间隔
							if(interval==null&&intervalAvg!=null)//后面5台到站车比当前最近发班车辆早到
								interval=intervalAvg;
							if(intervalAvg!=null&&interval<intervalAvg) {
								interval=intervalAvg;
							}
							int maxInterval=scheduleParam.getMaxInterval(lastTrip.getTripBeginTime(), direction);
							if(interval==null) {
								interval=getObuTimeIntervalNew(busAvailable, lastTrip);//按最小停站时间计算的间隔
								if(interval==null&&intervalAvg!=null)//后面5台到站车比当前最近发班车辆早到
									interval=intervalAvg;
								if(intervalAvg!=null&&interval<intervalAvg) {
									interval=intervalAvg;
								}
							}
							if(interval>maxInterval*1.2) {//超过最大间隔1.2
								if(!busReverseAdjustBase.isLastCircle(direction)) {
									List<Date> minObuTimeNextList=new ArrayList<Date>(); 
									for(Trip trip:busAvailable) {
										if(trip.getTripEndTime()!=null&&!tripArrival.isElecSupplyAfterTrip()&&
											!tripArrival.isEatAfterTrip()) {
											minObuTimeNextList.add(DateUtil.getDateAddMinute(trip.getTripEndTime(), 1));
										}else {
											minObuTimeNextList.add(trip.getNextObuTimeMin());
										}
									}
									double ratio=1.0;
									while(ratio<1.5) {
										boolean isOk=true;
										Date obuTimeLast=lastTrip.getTripBeginTime();
										for(Date minObuTimeNext:minObuTimeNextList) {
											int maxIntervalTemp=scheduleParam.getMaxInterval(obuTimeLast, direction);
											Date obuTimeTemp=DateUtil.getDateAddMinute(obuTimeLast, (int)Math.ceil(maxIntervalTemp*ratio));
											if(obuTimeTemp.before(minObuTimeNext)) {
												isOk=false;
												break;
											}
											obuTimeLast=obuTimeTemp;
										}
										if(isOk) {
											break;
										}else {
											ratio+=0.1;
										}
									}
									interval=(int)Math.ceil(maxInterval*ratio);
								}
							}
						}
					}
				}
				if(interval==null)
					System.out.println("aaa");
				obuTime=DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval);
				if(tripArrival.getTripEndTime()!=null&&obuTime.before(tripArrival.getNextObuTimeMin())) {
					if(tripArrival.isElecSupplyAfterTrip()) {
						obuTime=tripArrival.getNextObuTimeMin();
					}else if(obuTime.before(tripArrival.getTripEndTime())){
						obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
					}
				}
				if(busReverseAdjustBase.getLatestBusMain()!=tripArrival.getBus()&&obuTime.after(runTimeEnd))
					break;
				if(obuTime.after(latestTime))
					System.out.println("aaaa");
				routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
				if(bus.isSingleClass()&&!bus.isHasMiddleStop()&&obuTime.after(scheduleParam.getMiddleStopBeginTime())) {
					bus.setHasMiddleStop(true);
					routeSchedule.singleClassBusAdd(tripArrival, direction);
					bus_queue_to.remove(tripArrival);
					if(bus_queue_to.isEmpty())
						break;
					tripArrival=bus_queue_to.get(0);
					continue;
				}
				if(bus_queue_to.size()>1) {
					Trip tripArrialNext=bus_queue_to.get(1);
					if(tripArrialNext.getTripEndTime()!=null&&!tripArrialNext.getTripEndTime().before(latestTime)) {
						obuTime=latestTime;
					}
				}
				Trip trip=new Trip(bus, direction, obuTime, scheduleParam, lastTrip);
				lastTrip=routeSchedule.getTripLast(direction);//重新获取上一班，有可能是短线
				routeSchedule.addTrip(trip);
				tripLongList.add(trip);
				trip.setLastTrip(lastTrip);
				trip.setPreTrip(tripArrival);
				addMiddleStopTrip(trip);
				bus_queue_to.remove(tripArrival);
				if(bus==busReverseAdjustBase.getLatestBusMain()) {
					busReverseAdjustBase.removeLatestBusTrip(bus);
				}
				if(busReverseAdjustBase.isLatestBus(bus)) {
					busReverseAdjustBase.checkLatestBus(trip);
				}
				lastTrip=trip;
				if(!obuTime.before(latestTime))
					break;
			}else {
				bus_queue_to.remove(tripArrival);
				Bus bus=tripArrival.getBus();
				if(bus.isSingleClass()&&!bus.isHasMiddleStop()) {
					bus.setHasMiddleStop(true);
					routeSchedule.singleClassBusAdd(tripArrival, direction);
				}else if(bus.getShiftType()!=null) {
					Trip trip=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), tripArrival.getNextObuTimeMin());
					if(trip!=null)
						routeSchedule.addShortTripBack(trip);
				}
			}
			if(bus_queue_to.isEmpty())
				break;
			tripArrival=bus_queue_to.get(0);
		}		
		if(!busAdjustList.isEmpty()) {
			if(!tripLongList.isEmpty()) {
				busAdjustProc(direction, tripLongList, busAdjustList);
			}
			for(Trip trip:busAdjustList) {
				boolean success=false;
				for(int i=0;i<bus_queue_to.size();i++) {
					if(trip.getNextObuTimeMin().before(bus_queue_to.get(i).getNextObuTimeMin())) {
						bus_queue_to.add(i, trip);
						success=true;
						break;
					}
				}
				if(!success)
					bus_queue_to.add(trip);
			}
		}
		routeSchedule.tripListSort(routeSchedule.getTripList(direction));
	}
	
	private List<Trip> busAdjustProc(int direction,List<Trip> tripLongList,List<Trip> busAdjustList) {
		List<Trip> tripShortList=new ArrayList<Trip>();
		Trip firstTrip=tripLongList.get(0);
		if(!firstTrip.getLastTrip().isShortLine()) {//上时段最后一班不是短线
			tripLongList.add(0, firstTrip.getLastTrip());
		}
		for(int i=0;i<tripLongList.size()-1;) {
			Trip tripLongLast=tripLongList.get(i);
			Trip tripLongNext=tripLongList.get(i+1);
			Trip busAdjust=busAdjustList.get(0);
			if(DateUtil.getDateHM("1916").equals(busAdjust.getTripEndTime()))
				System.out.println("aaa");
			int interval=(int)Math.ceil(DateUtil.getMinuteInterval(tripLongLast.getTripBeginTime(), tripLongNext.getTripBeginTime())*2.0/3);
			Date maxObuTime=DateUtil.getDateAddMinute(tripLongLast.getTripBeginTime(), interval);
			Date minObuTime=busAdjust.getNextObuTimeMin();
			if(busAdjust.getTripEndTime()!=null&&!busAdjust.isElecSupplyAfterTrip()) {
				int minRestTime=scheduleParam.getMinRestTime(busAdjust.getTripEndTime(), direction);
				if(!busAdjust.isEatAfterTrip()) {
					minRestTime=Math.min(minRestTime, 3);//最小停站3分钟
				}else {
					minRestTime=15;//最小吃15分钟
				}
				minObuTime=DateUtil.getDateAddMinute(busAdjust.getTripEndTime(), minRestTime);
			}
			if(!minObuTime.after(maxObuTime)) {//最早发班时间在2/3前
				Bus bus = busAdjust.getBus();
				interval=(int)Math.ceil(DateUtil.getMinuteInterval(tripLongLast.getTripBeginTime(), tripLongNext.getTripBeginTime())*1.0/2);
				Date obuTime=DateUtil.getDateAddMinute(tripLongLast.getTripBeginTime(), interval);//居中时间
				if(minObuTime.after(obuTime)) {//最早发班时间在1/2后
					obuTime=minObuTime;
				}
				if(scheduleParam.isEndDirectionTrip(busAdjust)) {//这边可以收车
					if(bus.getShiftType()!=null) {
						Date endTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
						if(!obuTime.before(endTime)) {
							busAdjust.setQuitMark(true);
						}
					}
				}
				Trip tripShort=null;
				if(!busAdjust.isQuitMark()) {
					routeSchedule.setEaten(bus, busAdjust, obuTime, scheduleParam);
					if(busAdjust.getShortLineAdjust()!=null) {
						tripShort=getTrip4ShortLine(busAdjust.getShortLineAdjust(), bus, obuTime);
					}else {
						Date endTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
						RouteStaTurn routeStaTurn=getShortLineAdjust(direction, bus, obuTime, endTime);
						if(routeStaTurn!=null) {
							tripShort=getTrip4ShortLine(routeStaTurn, bus, obuTime);
						}else {
							busAdjust.setQuitMark(true);
						}
					}
				}
				if(tripShort!=null) {
					if(bus.getShiftType()!=null&&bus.getShiftType().getEndTime()!=null&&
							!tripShort.getTurnTrip().getNextObuTimeMin().before(DateUtil.getDateHM(bus.getShiftType().getEndTime()))) {
						tripShort.getTurnTrip().setQuitMark(true);
					}
					tripLongNext.setLastTrip(tripShort);
					tripShort.setLastTrip(tripLongLast);
					tripShort.setPreTrip(busAdjust);
					routeSchedule.addTrip(tripShort);
					tripShortList.add(tripShort);
					if(!tripShort.getTurnTrip().isQuitMark()) {
						routeSchedule.addShortTripBack(tripShort.getTurnTrip());
					}else {
						ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(bus.getShiftType().getShiftType(), bus.getStartDirection(), DateUtil.getDateHM(bus.getShiftType().getEndTime()));
						if(scheduleParamShift!=null) {
							routeSchedule.addShiftBus(scheduleParamShift, bus);
						}
					}
					addMiddleStopTrip(tripShort);
					i++;
				}
				if(busAdjust.isQuitMark()) {
					if(bus.getShiftType()!=null) {
						Trip trip=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), busAdjust.getNextObuTimeMin());
						if(trip!=null)
							routeSchedule.addShortTripBack(trip);
					}
				}
				busAdjustList.remove(busAdjust);
				
				if(busAdjustList.isEmpty()) {
					break;
				}
			}else {
				i++;
			}
		}
		return tripShortList;
	}
	
	private void beforeLastRoundOptimise4ShortLine(int direction) {
		List<Trip> tripList=routeSchedule.getTripList(direction);
		List<Trip> tripListReverse=routeSchedule.getTripList(1-direction);
		Trip latestTrip=tripList.get(tripList.size()-1);
		Trip latestTripReverse=tripListReverse.get(tripListReverse.size()-1);
		List<Trip> tripListLastRound=new ArrayList<Trip>();
		Trip tripFullLast=null;
 		for(int i=tripList.size()-1;i>=0;i--) {
 			Trip trip=tripList.get(i);
 			if(trip.isShortLine())
 				continue;
 			tripFullLast=trip;
 			if(trip.getTripBeginTime().before(scheduleParam.getLatePeakEndTime())||
 				(i!=tripList.size()-1&&trip.getBus()==latestTrip.getBus())) {//晚高峰结束前或最后一圈
 				break;
 			}
 			tripListLastRound.add(0, trip);
		}
 		boolean b4LastRound=false;
 		for(Trip trip:tripListLastRound) {
 			if(trip.getBus()==latestTripReverse.getBus()) {//定位到对面尾车
 				b4LastRound=true;
 				break;
 			}
 		}
 		if(!b4LastRound)
 			return;
 		Date latestTime=scheduleParam.getLatestTime(direction);
 		Integer intervalLast=null;
		while(!tripListLastRound.isEmpty()) {
			Trip trip=tripListLastRound.get(0);
			if(trip.getPreTrip().getTripEndTime().equals(DateUtil.getDateHM("2007")))
				System.out.println("aaa");
			if(trip.getBus()==latestTripReverse.getBus()) {
				System.out.println("aaa");
			}
			int interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/tripListLastRound.size();
			for(int i=0;i<tripListLastRound.size();i++) {
				Trip tripNext=tripListLastRound.get(i);
				if(tripNext.getPreTrip().getNextObuTimeMin().after(tripFullLast.getTripBeginTime())) {
					int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), tripNext.getPreTrip().getNextObuTimeMin())/(i+1);
					if(intervalTemp>interval) {
						interval=intervalTemp;
					}
				}
			}
			int maxInterval=scheduleParam.getMaxInterval(tripFullLast.getTripBeginTime(), direction);
			
			
			if(interval>maxInterval) {
				Date obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
				int maxIntervalNext=scheduleParam.getMaxInterval(obuTime, direction);
				maxInterval=Math.max(maxInterval, maxIntervalNext);//看会不会跨边界
				if(interval>maxInterval) {
					interval=0;
					for(int i=0;i<tripListLastRound.size();i++) {
						Trip tripNext=tripListLastRound.get(i);
						if(tripNext.getPreTrip().getTripEndTime().after(tripFullLast.getTripBeginTime())) {
							int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), tripNext.getPreTrip().getTripEndTime())/(i+1);
							if(intervalTemp>interval) { 
								interval=intervalTemp;
							}
						}
					}
					if(interval<maxInterval)
						interval=maxInterval;
				}
			}else {
				if(!trip.getPreTrip().getTripEndTime().before(scheduleParam.getOffDutyBeginTime())) {
					int busNumber=(int)Math.ceil(DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)*1.0/maxInterval);
					if(tripListLastRound.size()>1) {
						Trip tripNext=tripListLastRound.get(1);
						int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/busNumber;
						Date obuTimeTemp=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), intervalTemp);
						if(tripNext.getNextObuTimeMin().before(obuTimeTemp)) {
							while(trip!=null) {
								routeSchedule.removeTrip(trip);
								trip=trip.getLaterTrip();
							}
							continue;
						}
					}
				}else {
					if(scheduleParam.getOffDutyBeginTime().before(latestTime)) {
						Integer maxRestInterval=null;//按最大停站时间计算的发班间隔
						for(int i=0;i<tripListLastRound.size();i++) {
							Trip tripNext=tripListLastRound.get(i);
							if(!tripNext.getPreTrip().getTripEndTime().before(scheduleParam.getOffDutyBeginTime())) {
								break;
							}
							Date obuTime=null;
							if(tripNext.getPreTrip().isEatAfterTrip()) {
								obuTime=tripNext.getPreTrip().getNextObuTimeMin();
							}else {
								int maxRestTime=scheduleParam.getMaxRestTime(tripNext.getPreTrip().getTripEndTime(), direction);
								obuTime=DateUtil.getDateAddMinute(tripNext.getPreTrip().getTripEndTime(), maxRestTime);
							}
							if(obuTime.after(tripFullLast.getTripBeginTime())) {
								int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), obuTime)/(i+1);
								if(maxRestInterval==null||intervalTemp<maxRestInterval) {
									maxRestInterval=intervalTemp;
								}
							}
						}
						if(maxRestInterval==null) {//后面的车可以开始早收
							if(intervalLast!=null)
								interval=intervalLast;//用上一班的间隔
						}else if(maxRestInterval!=null&&maxRestInterval>interval) {//可以拉大间隔放
							if(maxRestInterval>maxInterval)
								maxRestInterval=maxInterval;
							interval=maxRestInterval;
						}
					}
				}
			}
			Date obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
			intervalLast=interval;
			Trip preTrip=trip.getPreTrip();
			if(!preTrip.getTripEndTime().before(obuTime)) {
				obuTime=DateUtil.getDateAddMinute(preTrip.getTripEndTime(), 1);
			}
			Date arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);
			Trip laterTrip=trip.getLaterTrip();
			if(laterTrip!=null&&scheduleParam.getLatestTime(laterTrip.getDirection()).equals(laterTrip.getTripBeginTime())) {//下一趟做尾车
				if(!arrivalTime.before(scheduleParam.getLatestTime(laterTrip.getDirection()))) {
					int diff=DateUtil.getMinuteInterval(scheduleParam.getLatestTime(laterTrip.getDirection()), arrivalTime);
					obuTime=DateUtil.getDateAddMinute(obuTime, -diff-1);
					arrivalTime=DateUtil.getDateAddMinute(arrivalTime, -diff-1);
				}
			}
			trip.setTripBeginTime(obuTime);
			trip.setTripEndTime(arrivalTime);
			if(arrivalTime==null)
				System.out.println("aaa");
			int restTime=scheduleParam.getMinRestTime(arrivalTime, 1-direction);
			trip.setNextObuTimeMin(DateUtil.getDateAddMinute(arrivalTime, restTime));
			tripFullLast=trip;
			tripListLastRound.remove(trip);
			if(trip.getBus()==latestTripReverse.getBus()) {//过去下行开末班车
				break;
			}
		}
	}
	
	private void lastRoundOptimise4ShortLine(int direction) {
		List<Trip> tripList=routeSchedule.getTripList(direction);
		List<Trip> tripListReverse=routeSchedule.getTripList(1-direction);
		Trip latestTripReverse=tripListReverse.get(tripListReverse.size()-1);
		Trip tripFullLast=null;
		List<Trip> tripListLastRound=new ArrayList<Trip>();
		boolean lastRound=false;
 		for(int i=tripList.size()-1;i>=0;i--) {
 			Trip trip=tripList.get(i);
 			if(trip.isShortLine())
 				continue;
 			tripFullLast=trip;
 			if(trip.getBus()==latestTripReverse.getBus()) {//对面尾车在7点后开出
 				lastRound=true;
 			}
 			if(trip.getTripBeginTime().before(scheduleParam.getLatePeakEndTime())||
 				(trip.getBus()==latestTripReverse.getBus())) {//反向末班车后
 				break;
 			}
 			tripListLastRound.add(0, trip);
		}
 		if(!lastRound)
 			return;
 		List<Trip> busQueue=new ArrayList<Trip>();
 		for(Trip trip:tripListLastRound) {
 			Trip preTrip=trip.getPreTrip();
 			if(preTrip.getTripEndTime()==null||preTrip.getTripEndTime().before(scheduleParam.getLatestTime(direction)))
 				busQueue.add(preTrip);
 			else {
 				routeSchedule.removeTrip(trip);
 			}
 		}
 		Date latestTime=scheduleParam.getLatestTime(direction);
 		Date obuTimeLast=tripFullLast.getTripBeginTime();
 		for(int i=0;i<busQueue.size();) {
 			int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, direction);
 			Trip tripArrival=busQueue.get(i);
 			int restTime=scheduleParam.getMinRestTime(tripArrival.getTripEndTime(), direction);
 			int busNumber=(int)Math.ceil(DateUtil.getMinuteInterval(obuTimeLast, latestTime)*1.0/maxInterval);
 			busNumber=Math.min(busNumber, busQueue.size());
 			List<Date> obuTimeList=new ArrayList<Date>();
 			Date obuTimeLastTemp=obuTimeLast;
 			for(int j=0;j<busNumber;j++) {
 				int interval=DateUtil.getMinuteInterval(obuTimeLastTemp, latestTime)/(busNumber-j);
 				Date obuTime=DateUtil.getDateAddMinute(obuTimeLastTemp, interval);
 				obuTimeList.add(obuTime);
 				obuTimeLastTemp=obuTime;
 			}
 			Date obuTime=obuTimeList.get(0);
 			if(busQueue.size()>obuTimeList.size()) {
 				if(!tripArrival.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime())) {//过了最早收车时间
 					boolean quit=true;
 	 				for(int j=0;j<obuTimeList.size();j++) {
 	 					if(DateUtil.getDateAddMinute(busQueue.get(j+1).getTripEndTime(), restTime).after(obuTimeList.get(j))) {//后面有车赶不上
 	 						quit=false;
 	 						break;
 	 					}
 	 				}
 	 				if(quit) {
 	 					busQueue.remove(tripArrival);
 	 					routeSchedule.removeTrip(tripArrival.getLaterTrip());
 	 					continue;
 	 				}
 				}else {
 					if(scheduleParam.getOffDutyBeginTime().before(scheduleParam.getLatestTime(direction))) {
 						Trip tripB4OffDutyBeginTime=null;
 						int index=-1;
 						for(int j=0;j<busQueue.size();j++) {
 							if(busQueue.get(j).getNextObuTimeMin().after(scheduleParam.getOffDutyBeginTime())) {
 								break;
 							}
 							tripB4OffDutyBeginTime=busQueue.get(j);
 							index=j;
 						}
 						while(busNumber<=busQueue.size()) {
 							obuTimeLastTemp=obuTimeLast;
 							obuTimeList.clear();
 							if(busNumber<=index)//防止过小，后面溢出
 								busNumber=index+1;
 							//计算剩余班次
 							for(int j=0;j<busNumber;j++) {
 				 				int interval=DateUtil.getMinuteInterval(obuTimeLastTemp, latestTime)/(busNumber-j);
 				 				Date obuTimeTemp=DateUtil.getDateAddMinute(obuTimeLastTemp, interval);
 				 				obuTimeList.add(obuTimeTemp);
 				 				obuTimeLastTemp=obuTimeTemp;
 				 			}
 							Date obuTimeB4OffDutyBeginTime=obuTimeList.get(index);
 							if(obuTimeB4OffDutyBeginTime.after(tripB4OffDutyBeginTime.getNextObuTimeMin())&&
 								DateUtil.getMinuteInterval(tripB4OffDutyBeginTime.getTripEndTime(), obuTimeB4OffDutyBeginTime)>
 									scheduleParam.getMaxRestTime(tripB4OffDutyBeginTime.getTripEndTime(), direction)) {//超过最大停站时间，加一班车
 								busNumber++;//增加班次
 							}else {
 								break;
 							}
 						}
 						int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTime)/busNumber;
	 		 			obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
 					}else {//最晚出车时间大于等于末班车时间
 						int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTime)/busQueue.size();
 		 				obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
 					}
 				}
 			}
 			Trip trip=tripArrival.getLaterTrip();
 			if(trip==null) {
 				trip=new Trip();
 				trip.setBus(tripArrival.getBus());
 				trip.setDirection(direction);
 			}
 			if(!obuTime.equals(scheduleParam.getLatestTime(direction))) {//非尾车
 				if(obuTime.before(tripArrival.getNextObuTimeMin())) {
 					Date maxObuTime=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
 					if(!tripArrival.getNextObuTimeMin().after(maxObuTime)) {
 						obuTime=tripArrival.getNextObuTimeMin();
 					}else {
 						if(tripArrival.getTripEndTime()!=null) {
 							int minRestTime=Math.min(scheduleParam.getMinRestTime(tripArrival.getTripEndTime(), direction), 3);
 							Date minObuTime=DateUtil.getDateAddMinute(obuTimeLast, minRestTime);
 							if(minObuTime.before(maxObuTime)) {//休息3分钟不断位，按最大间隔发
 								obuTime=maxObuTime;
 							}else {
 								obuTime=minObuTime;
 							}
 						}
 					}
 				}
 			}
 			if(tripArrival.getTripEndTime()==null||tripArrival.getTripEndTime().before(latestTime)) {
 				if(obuTime.after(latestTime)) {
 					obuTime=latestTime;
 				}else {
 					if(tripArrival.getTripEndTime()!=null&&!obuTime.after(tripArrival.getTripEndTime()))
 		 				obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
 				}
 				trip.setTripBeginTime(obuTime);
 	 			Date arrivalTime=scheduleParam.getArrivalTime(trip.getTripBeginTime(), direction);
 	 			trip.setTripEndTime(arrivalTime);
 	 			obuTimeLast=trip.getTripBeginTime();
 	 			busQueue.remove(tripArrival);
 	 			tripFullLast=trip;
 			}else {//赶不上末班车
 				tripFullLast.setTripBeginTime(latestTime);
 				tripFullLast.setTripEndTime(scheduleParam.getArrivalTime(latestTime, direction));
 				obuTime=latestTime;
 			}
 			if(obuTime.equals(scheduleParam.getLatestTime(direction))) {//末班车已发，把多余的车删掉
 				for(int j=0;j<busQueue.size();j++) {
 					routeSchedule.removeTrip(busQueue.get(j).getLaterTrip());
 				}
 				break;
 			}
 		}
 		
 		/*for(int i=0;i<busQueue.size();) {
 			Trip tripArrival=busQueue.get(i);
 			if(!tripArrival.getTripEndTime().before(scheduleParam.getOffDutyBeginTime())) {//过了最早收车时间
 				
 				boolean offDuty=true;
 				List<Trip> busQueueTemp=new ArrayList<Trip>();
 				for(Trip tripTemp:busQueue) {
 					if(tripTemp!=tripArrival) {//收起一台车看看
 						busQueueTemp.add(tripTemp);
 					}
 				}
 				for(int j=0;j<busQueueTemp.size();j++) {
 					int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTime)/(busQueueTemp.size()-j);
 					
 					if(interval>maxInterval) {//收车后，发班间隔过大
 						offDuty=false;
						break;
 					}
 					Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
 					Trip tripArrivalTemp=busQueueTemp.get(j);
 					int maxRestTime=scheduleParam.getMaxRestTime(tripArrivalTemp.getTripEndTime(), direction);
 					if(!obuTime.before(DateUtil.getDateAddMinute(tripArrivalTemp.getTripEndTime(), scheduleParam.getMinRestTime(tripArrivalTemp.getTripEndTime(), direction)))) {
 						int restTime=DateUtil.getMinuteInterval(tripArrivalTemp.getTripEndTime(), obuTime);
 						if(restTime>=maxRestTime) {//收车后，停站时间过长
 							offDuty=false;
 							break;
 						}
 					}else {//后车赶不上，不能收车
 						offDuty=false;
							break;
 					}
 					obuTimeLast=obuTime;
 				}
 				if(offDuty) {
 					busQueue.remove(tripArrival);
 					Trip trip=tripArrival.getLaterTrip();
 					routeSchedule.removeTrip(trip);
 				}else {
 					i++;
 				}
 			}else {
 				i++;
 			}
 		}
 		Date obuTimeLast=tripFullLast.getTripBeginTime();
 		for(int i=0;i<busQueue.size();i++) {
 			Trip tripArrival=busQueue.get(i);
 			int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTime)/(busQueue.size()-i);
 			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
 			if(!tripArrival.getTripEndTime().before(obuTime)) {
 				obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
 			}
 			Trip trip=tripArrival.getLaterTrip();
 			trip.setTripBeginTime(obuTime);
 			Date arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);
 			trip.setTripEndTime(arrivalTime);
 			obuTimeLast=obuTime;
 		}*/
 		
 		/*
 		
 		
		while(!tripListLastRound.isEmpty()) {
			Trip trip=tripListLastRound.get(0);
			int maxInterval=scheduleParam.getMaxInterval(tripFullLast.getTripBeginTime(), direction);
			Date obuTime=null;
			if(!DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), maxInterval).before(scheduleParam.getOffDutyBeginTime())) {
				int busNumber=(int)Math.ceil(DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)*1.0/maxInterval);//所需车数
				if(busNumber==0) {
					tripListLastRound.remove(trip);
					routeSchedule.removeTrip(trip);
					continue;
				}
				if(busNumber==1)
					System.out.println("aaa");
				int interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/busNumber;
				obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
				if(tripListLastRound.size()>1) {
					Trip tripNext=tripListLastRound.get(1);
					if(!tripNext.getPreTrip().getNextObuTimeMin().after(obuTime)) {
						tripListLastRound.remove(trip);
						routeSchedule.removeTrip(trip);
						continue;
					}
				}
			}else {
				int interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/tripListLastRound.size();
				for(int i=0;i<tripListLastRound.size();i++) {
					Trip tripNext=tripListLastRound.get(i);
					if(!tripNext.getPreTrip().isEatAfterTrip()&&tripNext.getPreTrip().getNextObuTimeMin().after(tripFullLast.getTripBeginTime())) {
						int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), tripNext.getPreTrip().getNextObuTimeMin())/(i+1);
						if(intervalTemp>interval) {
							interval=intervalTemp;
						}
					}
				}
				Integer maxRestInterval=null;//按最大停站时间计算的发班间隔
				for(int i=0;i<tripListLastRound.size();i++) {
					Trip tripNext=tripListLastRound.get(i);
					if(!tripNext.getPreTrip().getTripEndTime().before(scheduleParam.getOffDutyBeginTime())) {
						break;
					}
					if(tripNext.getPreTrip().getNextObuTimeMin().after(tripFullLast.getTripBeginTime())) {
						int maxRestTime=scheduleParam.getMaxRestTime(tripNext.getPreTrip().getTripEndTime(), direction);
						int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), DateUtil.getDateAddMinute(tripNext.getPreTrip().getTripEndTime(), maxRestTime))/(i+1);
						if(maxRestInterval==null||intervalTemp<maxRestInterval) {
							maxRestInterval=intervalTemp;
						}
					}
				}
				if(maxRestInterval==null)
					System.out.println("aaa");
				if(maxRestInterval>interval) {//看能不能拉大间隔
					interval=maxRestInterval;
					if(interval>maxInterval) {//按最大停站会断位，进行修正
						int busNumber=(int)Math.ceil(DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)*1.0/maxInterval);//所需车数
						if(busNumber==0) {
							tripListLastRound.remove(trip);
							routeSchedule.removeTrip(trip);
							continue;
						}
						interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/busNumber;
					}
				}
				obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
			}
			Trip preTrip=trip.getPreTrip();
			if(!preTrip.getTripEndTime().before(obuTime)) {
				obuTime=DateUtil.getDateAddMinute(preTrip.getTripEndTime(), 1);
			}
			Date arrivalTime=scheduleParam.getArrivalTime(obuTime, direction);
			trip.setTripBeginTime(obuTime);
			trip.setTripEndTime(arrivalTime);
			tripFullLast=trip;
			tripListLastRound.remove(trip);
		}*/
	}
	
	private void calculateNormalTime4FullLine(ScheduleHalfHour scheduleHalfHour) {
		int direction=scheduleHalfHour.getDirection();
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		if(runTime.equals(DateUtil.getDateHM("2130")))
			System.out.println("aaa");
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		if(bus_queue_to.isEmpty())
			return;
		Date latestTime=scheduleParam.getLatestTime(direction);
		Trip lastTripFull=routeSchedule.getTripFullLast(direction);
		if(lastTripFull!=null&&latestTime.equals(lastTripFull.getTripBeginTime()))
			return;
		busReverseAdjustBase.setLatestBus();//设置尾车
		if(!elecSupply) {//第二次算补电是不在选择单班车，用第一次的
			setQuitMark4MiddleStop(scheduleHalfHour, bus_queue_to, busReverseAdjustBase.getLatestBusMain());
		}
		//setQuitMarkOffDutyEarly(direction,busReverseAdjustBase.getLatestBusMain());
		if(scheduleParam.getEndDirection()!=null)//中途出场的先不早收
			setQuitMarkOffDutyEarlyNew(direction);
		if(runTime.after(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(), Calendar.MINUTE, -60))) {//准备单班复行
			singleClassRecovery(lastTripFull, bus_queue_to);
		}
		electricitySupplement.setBusElecSupply(direction, bus_queue_to);
		Trip tripArrival=bus_queue_to.get(0);
		while(tripArrival.getNextObuTimeMin().before(runTimeEnd)||
				tripArrival.getNextObuTimeMin().equals(latestTime)||
				(scheduleHalfHour.getNextScheduleHalfHour()==null&&
				tripArrival.getTripEndTime()!=null&&tripArrival.getTripEndTime().before(latestTime))) {//最后一个时段，到站车在末班车时间前到
			if(!tripArrival.isQuitMark()) {
				if(DateUtil.getDateHM("2016").equals(tripArrival.getTripEndTime()))
					System.out.println("aaa");
				if(lastTripFull.getTripBeginTime().equals(latestTime))
					break;
				List<Trip> busAvailable=new ArrayList<Trip>();
				for(Trip tripTemp:bus_queue_to) {//过滤退出车辆
					if(!tripTemp.isQuitMark())
						busAvailable.add(tripTemp);
				}
				Date obuTime=busReverseAdjustBase.getObuTime(tripArrival,direction);
				if(obuTime==null) {
					if(busAvailable.get(busAvailable.size()-1).getNextObuTimeMin().after(latestTime)
							&&busReverseAdjustBase.getLatestBusMain()==null) {//最后半圈，没有尾车
						List<Trip> busAvailableLastRound=new ArrayList<Trip>();
						for(Trip tripTemp:busAvailable) {//过滤退出车辆
							if(tripTemp.getNextObuTimeMin().after(latestTime)) {
								if(!busAvailableLastRound.isEmpty()) {
									int interval=DateUtil.getMinuteInterval(lastTripFull.getTripBeginTime(), latestTime)/busAvailableLastRound.size();
									int maxInterval=scheduleParam.getMaxInterval(lastTripFull.getTripBeginTime(), direction);
									if(interval>maxInterval) {//断位，看能不能加一台车
										if(tripTemp.getTripEndTime()==null||tripTemp.getTripEndTime().before(latestTime)) {
											tripTemp.setNextObuTimeMin(latestTime);
										}
									}
								}else {
									if(tripTemp.getTripEndTime().before(latestTime)) {
										busAvailableLastRound.add(tripTemp);
									}
								}
								break;
							}else {
								busAvailableLastRound.add(tripTemp);
							}
						}
						busAvailable=busAvailableLastRound;
					}
					
					//busAvailable.remove(0);
					if(busAvailable.size()==1) {//看对面车能不能过来做尾车
						List<Trip> busQueue=routeSchedule.getBusQueue(direction);
						Date minObuTimeNext=null;
						for(Trip tripTemp:busQueue) {
							if(!tripTemp.isQuitMark()) {
								minObuTimeNext=scheduleParam.getMinObuTimeNext(1-direction, tripTemp.getNextObuTimeMin());
								break;
							}
						}
						if(minObuTimeNext==null||minObuTimeNext.after(latestTime)) {
							obuTime=latestTime;
						}
					}
					if(obuTime==null){
						Integer interval=null;
						interval=getObuTimeIntervalNew(busAvailable, lastTripFull);
						/*if(!busReverseAdjustBase.isLatestBusLastRound()&&bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(latestTime)) {//非最后一轮
							interval=getObuTimeIntervalNew(busAvailable, lastTripFull);
						}else {
							interval=getObuTimeInterval4LastRound(direction,bus_queue_to);
						}*/
						int maxInterval=scheduleParam.getMaxInterval(lastTripFull.getTripBeginTime(), direction);
						if(interval==null||interval>maxInterval)
							interval=getInterval4ExceedMaxInterval(direction, lastTripFull, busAvailable);
						if(lastTripFull.getBus().isSingleClass()||tripArrival.getBus().isSingleClass()) {
							if(runTime.before(scheduleParam.getLatePeakEndTime())) {
								//interval=interval*2/3;
							}else {
								//interval=interval/2;
							}
						}
						obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
						if(busAvailable.size()>1&&busAvailable.get(1).getTripEndTime()!=null
								&&busAvailable.get(1).getTripEndTime().after(latestTime)) {
							obuTime=latestTime;
						}
						if(obuTime.after(latestTime))
							System.out.println("aaa");
					}
				}else {
					if(obuTime.after(latestTime))
						System.out.println("aaa");
					if(tripArrival.isElecSupplyAfterTrip()&&tripArrival.getNextObuTimeMin().after(obuTime))
						obuTime=tripArrival.getNextObuTimeMin();
				}
				if(!obuTime.equals(latestTime)&&obuTime.before(tripArrival.getNextObuTimeMin())) {//尾车不再看是否改时间
					/*if(tripArrival.isEatAfterTrip()) {
						obuTime=tripArrival.getNextObuTimeMin();
					}else */
					if(tripArrival.getTripEndTime()!=null){
						if(busReverseAdjustBase.isLatestBusLastRound()) {
							if(!obuTime.after(tripArrival.getTripEndTime())) {//发班时间在到达时间前
								obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 2);//两分钟即走
							}
							if(obuTime.after(latestTime))
								obuTime=latestTime;
						}else {
							int maxInterval=scheduleParam.getMaxInterval(lastTripFull.getTripBeginTime(), direction);
							Date maxObuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), maxInterval);
							if(tripArrival.getNextObuTimeMin().after(maxObuTime)) {
								obuTime=null;
								if(tripArrival.isEatAfterTrip()) {//吃饭后断位
									busAvailable.remove(tripArrival);
									int restTime=scheduleParam.getMinRestTime(tripArrival.getTripEndTime(), direction);
									tripArrival.setEatAfterTrip(false);//先不吃饭
									tripArrival.setNextObuTimeMin(DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), restTime));
									if(!tripArrival.getNextObuTimeMin().after(maxObuTime)) {//不吃饭的话不会断位
										Integer interval=getObuTimeInterval(busAvailable, lastTripFull);//重算间隔
										if(interval!=null&&interval<=maxInterval) {//间隔小于最大发班间隔
											Date obuTimeTemp=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
											if(obuTimeTemp.before(tripArrival.getNextObuTimeMin())) {//发班时间在最小发班时间前
												obuTime=tripArrival.getNextObuTimeMin();
											}else {
												obuTime=obuTimeTemp;
											}
										}
									}
								}
								if(obuTime==null) {
									int restTime=Math.min(scheduleParam.getMinRestTime(tripArrival.getTripEndTime(), direction), 3);//至少停站3分钟
									Date minObuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), restTime);
									if(minObuTime.after(maxObuTime)) {
										obuTime=minObuTime;
									}else {
										obuTime=maxObuTime;
									}
								}
							}else {
								obuTime=tripArrival.getNextObuTimeMin();
							}
						}
					}
				}
				if(busReverseAdjustBase.getLatestBusMain()!=tripArrival.getBus()&&obuTime.after(runTimeEnd))
					break;
				if(obuTime.after(latestTime))
					break;
				Bus bus=tripArrival.getBus();
				routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
				Trip trip=new Trip(bus, direction, obuTime, scheduleParam, lastTripFull);
				if(busReverseAdjustBase.isLatestBusLastRound()) {
					Date latestTimeReverse=scheduleParam.getLatestTime(1-direction);
					if(!obuTime.equals(latestTime)&&obuTime.before(latestTimeReverse)&&
						(bus==busReverseAdjustBase.getLatestBusMain()||bus==busReverseAdjustBase.getLatestBusSecondary())) {//看能不能过对面发末班时间
						if(!trip.getTripEndTime().before(latestTimeReverse)) {
							int diff=DateUtil.getMinuteInterval(trip.getTripEndTime(), latestTimeReverse);
							Date obuTimeTemp=DateUtil.getDateAddMinute(obuTime, -diff-1);
							if(tripArrival.getTripEndTime()==null||obuTimeTemp.after(tripArrival.getTripEndTime())) {
								trip.setTripBeginTime(obuTimeTemp);
								trip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), -diff-1));
								trip.setNextObuTimeMin(latestTimeReverse);
							}
						}
					}
				}
				routeSchedule.addTrip(trip);
				if(trip.getTripBeginTime().before(lastTripFull.getTripBeginTime())) {//发班时间比前一班早
					List<Trip> busQueue=routeSchedule.getBusQueue(direction);
					busQueue.remove(trip);
					for(int i=0;i<busQueue.size();i++) {
						Trip tripTemp=busQueue.get(i);
						if(trip.getNextObuTimeMin().before(tripTemp.getNextObuTimeMin())) {//插入到对应位置，防止没末班车
							busQueue.add(i, trip);
							break;
						}
					}
					if(!busQueue.contains(trip))
						busQueue.add(trip);
					routeSchedule.tripListSort();
				}else {
					trip.setLastTrip(lastTripFull);
					lastTripFull=trip;
				}
				trip.setPreTrip(tripArrival);
				addMiddleStopTrip(trip);
				bus_queue_to.remove(tripArrival);
				if(obuTime.after(scheduleParam.getLatestTime(direction)))
					break;
			}else {
				bus_queue_to.remove(tripArrival);
				Bus bus=tripArrival.getBus();
				if(bus.isSingleClass()) {
					if(!bus.isHasMiddleStop()) {
						routeSchedule.singleClassBusAdd(tripArrival, direction);
						bus.setHasMiddleStop(true);
					}
				}else if(bus.getShiftType()!=null) {
					Trip trip=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), tripArrival.getNextObuTimeMin());
					if(trip!=null)
						routeSchedule.addShortTripBack(trip);
				}
			}
			if(bus_queue_to.isEmpty())
				break;
			tripArrival=bus_queue_to.get(0);
		}
		if(scheduleHalfHour.getNextScheduleHalfHour()==null) {//最后一个时段
			lastTripFull=routeSchedule.getTripFullLast(direction);
			if(lastTripFull.getTripBeginTime().before(latestTime)) {//末班车没发
				for(int i=0;i<bus_queue_to.size();i++) {
					tripArrival=bus_queue_to.get(i);
					if(tripArrival.getBus()==busReverseAdjustBase.getLatestBusMain()||
						tripArrival.getBus()==busReverseAdjustBase.getLatestBusSecondary()) {//找到末班车
						if(tripArrival==null||tripArrival.getTripEndTime().before(latestTime)) {
							Trip trip=new Trip(tripArrival.getBus(), direction, latestTime, scheduleParam, lastTripFull);
							routeSchedule.addTrip(trip);
							break;
						}
					}
				}
			}
		}
	}
	
	private boolean calculateAfterEarlyRushTime() {
		if(scheduleParam.isLoopLine()&&scheduleParam.getFirstTime(Direction.UP.getValue()).after(scheduleParam.getEarlyPeakEndTime())) {//过了早高峰
			initBusQueue4LoopLineAfterEarlyPeek();
		}
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		if(scheduleParam.getEndDirection()!=null&&(scheduleParam.getLeaveImmediatelyInterval(Direction.UP.getValue())!=null||scheduleParam.getLeaveImmediatelyInterval(Direction.DOWN.getValue())!=null)) {
			ScheduleNormalTime scheduleNormalTime=new ScheduleNormalTime(routeSchedule, scheduleParam);
			scheduleNormalTime.calculate();
		}else {
			if(!scheduleParam.isLoopLine()) {
				scheduleParam.resetSingleRecoveryBeginTime(Direction.UP.getValue());
				scheduleParam.resetSingleRecoveryBeginTime(Direction.DOWN.getValue());
			}
			Integer diretion=null;
			for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
				Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
				if(!runTime.before(scheduleParam.getEarlyPeakEndTime())){//过滤早高峰时段
					if(scheduleHalfHour.getDirection()==diretion) {//连续同一方向，发对向末班车先
						supplyLatestTime(1-diretion);//发末班车
					}
					calculate(scheduleHalfHour);
					diretion=scheduleHalfHour.getDirection();
					if(scheduleParam.getRouteId()==10&&!runTime.before(DateUtil.getDateHM("1300"))) {
						ScheduleNormalTime scheduleNormalTime=new ScheduleNormalTime(routeSchedule, scheduleParam);
						scheduleNormalTime.calculate();
						break;
					}
				}
			}
		}
		dealLatestBusTrip(Direction.UP.getValue());
		dealLatestBusTrip(Direction.DOWN.getValue());
		if((scheduleParam.isTestLineShort()||scheduleParam.isTestLineFull())
				&&scheduleParam.getEndDirection()==null&&
				routeSchedule.getTripLast(Direction.UP.getValue()).getBus()!=
				routeSchedule.getTripLast(Direction.DOWN.getValue()).getBus()) {
			busTripListSort();
			beforeLastRoundOptimise4ShortLine(Direction.UP.getValue());
			beforeLastRoundOptimise4ShortLine(Direction.DOWN.getValue());
			lastRoundOptimise4ShortLine(Direction.UP.getValue());
			lastRoundOptimise4ShortLine(Direction.DOWN.getValue());
			/*LastRoundOptimisePro lastRoundOptimisePro=new LastRoundOptimisePro(scheduleParam, routeSchedule);
			lastRoundOptimisePro.calculate4StopNoDirection();*/
		}
		//checkLatestTime(Direction.UP.getValue());//发末班车
		//checkLatestTime(Direction.DOWN.getValue());//发末班车
		if(scheduleParam.getBusNumberConfig()!=routeSchedule.getBusNumber()) {
			System.out.println("计划配车"+scheduleParam.getBusNumberConfig()+";实际配车"+routeSchedule.getBusNumber());
			//System.out.println(1/0);
		}
		System.out.println("停站时间检测开始");
		checkRestTime(routeSchedule.getTripList(Direction.UP.getValue()));
		checkRestTime(routeSchedule.getTripList(Direction.DOWN.getValue()));
		System.out.println("停站时间监测结束");
		Map<ScheduleHalfHour, Integer> classesNumberMap=new HashMap<ScheduleHalfHour, Integer>();
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripMap().get(bus);
			for(Trip trip:busTripList) {
				if(!trip.isShortLine()) {
					ScheduleHalfHour scheduleHalfHour=scheduleParam.getScheduleHalfHour(trip.getTripBeginTime(), trip.getDirection());
					Integer classesNumber=classesNumberMap.get(scheduleHalfHour);
					if(classesNumber==null) {
						classesNumber=1;
					}else {
						classesNumber++;
					}
					classesNumberMap.put(scheduleHalfHour, classesNumber);
				}
			}
		}
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			System.out.println(scheduleHalfHour.getRunTime()+"\t"+scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection()).getMinClassesNumber(scheduleHalfHour.getRunTime())+"\t"+classesNumberMap.get(scheduleHalfHour));
		}
		return true;
	}
	
	private void supplyLatestTime(int direction) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(latestTime!=null) {
			Trip lastTripFull=routeSchedule.getTripFullLast(direction);
			if(lastTripFull!=null&&!lastTripFull.getTripBeginTime().equals(latestTime)) {//末班车未发
				List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
				if(!bus_queue_to.isEmpty()) {
					Trip tripArrival=bus_queue_to.get(0);
					if(tripArrival.getTripEndTime()!=null&&tripArrival.getTripEndTime().before(latestTime)) {
						Trip trip=new Trip(tripArrival.getBus(), direction, latestTime,scheduleParam,lastTripFull);
						trip.setLastTrip(lastTripFull);
						lastTripFull.setNextTrip(trip);
						routeSchedule.addTrip(trip);
						trip.setPreTrip(tripArrival);
						bus_queue_to.remove(tripArrival);
						System.out.println(trip.getBusName()+"\t"+HM_FORMAT.format(trip.getTripBeginTime())+"\t"+(tripArrival.getTripEndTime()==null?"":HM_FORMAT.format(tripArrival.getTripEndTime()))+"\t"+(tripArrival.getNextObuTimeMin()==null?"":HM_FORMAT.format(tripArrival.getNextObuTimeMin())));
					}else {
						lastTripFull.setTripBeginTime(latestTime);
						lastTripFull.setNextObuTimeMin(scheduleParam);
					}
				}
			}
		}
	}
	
	private int getBusNumberConfig(Date runTimeBegin) {
		int busNumber=0;
		Map<String, Integer> timeBusConfigMap=routeSchedule.getTimeBusConfigMap();
		for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			if(!DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime().before(runTimeBegin)) {
				Integer busNumberTemp=timeBusConfigMap.get(scheduleHalfHour.getRunTime());
				if(busNumberTemp==null) {
					System.out.println("=========="+scheduleHalfHour.getDirection()+"\t"+scheduleHalfHour.getRunTime());
					continue;
				}
				if(busNumberTemp>busNumber) {
					busNumber=timeBusConfigMap.get(scheduleHalfHour.getRunTime());
				}
			}
		}
		return busNumber;
	}
	
	private void calculate(ScheduleHalfHour scheduleHalfHour) {
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		System.out.println("========================================计算"+(scheduleHalfHour.getDirection()==0?"上行":"下行")+"开始"+scheduleHalfHour.getRunTime());
		if(runTime.before(scheduleParam.getEarlyPeakEndTime())) {//早高峰结束前
			calculateRushTime(scheduleHalfHour);
		}else {
			if(scheduleParam.isLoopLine()) {
				calculateNormalTime4LoopLine(scheduleHalfHour);
			}else {
				if(scheduleParam.isTestLineFull()&&!runTime.before(DateUtil.getDateHM("0900"))) {
					calculateNormalTime4FullLine(scheduleHalfHour);
				}else if(scheduleParam.isTestLineShort()&&!runTime.before(DateUtil.getDateHM("0900"))){
					calculateNormalTime4ShortLine(scheduleHalfHour);
				}else {
					if(isLastRound()) {
						procLastRound();
					}else {
						calculateNormalTime(scheduleHalfHour);
						if(!scheduleParam.isLoopLine())
							procLatestBusReverse(scheduleHalfHour);
					}
				}
			}
		}
		System.out.println("========================================计算"+(scheduleHalfHour.getDirection()==0?"上行":"下行")+"结束");
	}
	
	private void procLatestBusReverse(ScheduleHalfHour scheduleHalfHour) {
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		int index=scheduleHalfHourList.indexOf(scheduleHalfHour);
		ScheduleHalfHour scheduleHalfHourReversePre=null;
		int direction=1-scheduleHalfHour.getDirection();
		for(int i=index-1;i>=0;i--) {
			ScheduleHalfHour shh=scheduleHalfHourList.get(i);
			if(shh.getDirection()==direction) {
				scheduleHalfHourReversePre=shh;
				break;
			}
		}
		if(scheduleHalfHourReversePre.getNextScheduleHalfHour()==null) {//反向已发完
			Trip tripFullLast=routeSchedule.getTripFullLast(direction);
			Date latestTime=scheduleParam.getLatestTime(direction);
			if(tripFullLast!=null&&!tripFullLast.getTripBeginTime().equals(latestTime)) {//没有末班车
				calculateNormalTime(scheduleHalfHourReversePre);
			}
		}
		if(scheduleHalfHour.getNextScheduleHalfHour()==null) {//可能反向末班车过来做末班车
			Trip tripFullLast=routeSchedule.getTripFullLast(scheduleHalfHour.getDirection());
			Date latestTime=scheduleParam.getLatestTime(scheduleHalfHour.getDirection());
			if(tripFullLast!=null&&!tripFullLast.getTripBeginTime().equals(latestTime)) {//没有末班车
				calculateNormalTime(scheduleHalfHour);
			}
		}
	}
	
	private void procLastRound() {
		while(true) {
			retryTime++;
			if(retryTime>10000)
				throw new MyException("-2", "计划生成失败，请联系开发人员"+scheduleParam.getRouteId()+"|"+new Date());
			if(routeSchedule.getTripFullLast(Direction.UP.getValue()).getTripBeginTime().
					equals(scheduleParam.getLatestTime(Direction.UP.getValue()))&&
				routeSchedule.getTripFullLast(Direction.DOWN.getValue()).getTripBeginTime().
					equals(scheduleParam.getLatestTime(Direction.DOWN.getValue()))) {
				return;
			}
			Trip tripArrivalDown=null;
			Trip tripArrivalUp=null;
			List<Trip> busQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
			if(!busQueueDown.isEmpty()) {
				tripArrivalDown=busQueueDown.get(0);
				if(tripArrivalDown.isQuitMark()) {
					busQueueDown.remove(tripArrivalDown);
					continue;
				}
			}
			List<Trip> busQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
			if(!busQueueUp.isEmpty()) {
				tripArrivalUp=busQueueUp.get(0);
				if(tripArrivalUp.isQuitMark()) {
					busQueueUp.remove(tripArrivalUp);
					continue;
				}
			}
			int direction=Direction.UP.getValue();
			List<Trip> busQueueTo=new ArrayList<Trip>();
			Trip tripArrival=null;
			List<Trip> busQueue=null;
			if(tripArrivalDown==null||(tripArrivalUp!=null&&tripArrivalUp.getNextObuTimeMin().before(tripArrivalDown.getNextObuTimeMin())&&
					!routeSchedule.getTripFullLast(Direction.DOWN.getValue()).getTripBeginTime().//上行到站车早于下行到站车，并且下行末班车没发
					equals(scheduleParam.getLatestTime(Direction.DOWN.getValue())))
					||routeSchedule.getTripFullLast(Direction.UP.getValue()).getTripBeginTime().
					equals(scheduleParam.getLatestTime(Direction.UP.getValue()))) {//上行末班车已发
				direction=Direction.DOWN.getValue();
				busQueueTo.addAll(busQueueUp);
				busQueueTo.addAll(busQueueDown);
				tripArrival=tripArrivalUp;
				busQueue=busQueueUp;
			}else {
				busQueueTo.addAll(busQueueDown);
				busQueueTo.addAll(busQueueUp);
				tripArrival=tripArrivalDown;
				busQueue=busQueueDown;
			}
			Trip tripFullLast=routeSchedule.getTripFullLast(direction);
			Date latestTime=scheduleParam.getLatestTime(direction);
			if(tripArrival==null) {
				tripFullLast.setTripBeginTime(latestTime);
				tripFullLast.setTripEndTime(scheduleParam.getArrivalTime(latestTime, direction));
				continue;
			}
			if(DateUtil.getDateHM("1858").equals(tripArrival.getTripEndTime()))
				System.out.println("aaa");
			int busNumber=0;
			List<Trip> busQueueTo4LastRound=new ArrayList<Trip>();
			for(Trip tripArrivalTemp:busQueueTo) {
				if(tripArrivalTemp.getDirection()==direction) {
					if(!tripArrivalTemp.isQuitMark()&&!tripArrivalTemp.isShortLine()){
						Date obuTimeNextRound=null;
						if(tripArrivalTemp.getTripBeginTime()==null){//对面复行车
							obuTimeNextRound=scheduleParam.getMinObuTimeNext(1-direction, tripArrivalTemp.getNextObuTimeMin());
						}else {
							obuTimeNextRound=scheduleParam.getObuTimeNextRound(tripArrivalTemp.getDirection(),tripArrivalTemp.getTripBeginTime());
						}
						if(obuTimeNextRound!=null&&
							!obuTimeNextRound.after(latestTime)) {
							busNumber++;
							busQueueTo4LastRound.add(tripArrivalTemp);
						}
					}
				}else {
					if(!tripArrivalTemp.isQuitMark()) {
						if(!tripArrivalTemp.getNextObuTimeMin().after(latestTime)) {
							busNumber++;
							busQueueTo4LastRound.add(tripArrivalTemp);
						}else if(tripArrivalTemp.getTripEndTime()!=null&&
								tripArrivalTemp.getTripEndTime().before(latestTime)) {
							busQueueTo4LastRound.add(tripArrivalTemp);
						}
					}
				}
			}
			if(busNumber==0){
				if(!tripArrival.getTripEndTime().after(latestTime)) {
					busNumber=1;
					busQueueTo4LastRound.add(tripArrival);
				}else {
					tripFullLast.setTripBeginTime(latestTime);
					tripFullLast.setTripEndTime(scheduleParam.getArrivalTime(latestTime, direction));
					List<Trip> busTripList=routeSchedule.getTripList(tripFullLast.getBus());
					Trip busTripLast=busTripList.get(busTripList.size()-1);
					if(busTripLast!=tripFullLast) {//在对面发班了
						busTripList.remove(busTripLast);//删除对面班次
						busQueue=routeSchedule.getBusQueue(busTripLast.getDirection());//从对面方向队列删除
						busQueue.remove(busTripLast);
						busQueue=routeSchedule.getBusQueue(tripFullLast.getDirection());
						busQueue.add(tripFullLast);
					}
					continue;
				}
			}
			if(busNumber==1)
				System.out.println("aaa");
			int maxInterval=scheduleParam.getMaxInterval(tripFullLast.getTripBeginTime(), direction);
			int intervalAvg=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/busNumber;
			if(intervalAvg>maxInterval) {//出现断位，看能不能多一台车
				if(busQueueTo4LastRound.size()>busNumber) {//后面一台车来的及，压缩停站
					busNumber++;//加上一台车
					intervalAvg=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/busNumber;
				}
			}else {
				busQueueTo4LastRound=busQueueTo4LastRound.subList(0, busNumber);
			}
			int interval=intervalAvg;//平均间隔
			List<Trip> busQueueArrival=routeSchedule.getBusQueue(1-direction);
			List<Trip> busAvailable=new ArrayList<Trip>();
			for(Trip trip:busQueueArrival) {
				if(trip.getNextObuTimeMin().after(latestTime))
					break;
				if(!trip.isQuitMark()&&trip.getShortLineAdjust()==null)
					busAvailable.add(trip);
			}
			Integer intervalMin=null;
			for(int i=0;i<busAvailable.size()&&i<6&&i<busNumber;i++) {
				Trip trip=busAvailable.get(i);
				if(trip.getNextObuTimeMin().after(tripFullLast.getTripBeginTime())) {
					int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), trip.getNextObuTimeMin())/(i+1);
					if(intervalMin==null||intervalTemp>intervalMin) {
						intervalMin=intervalTemp;
					}
				}
			}
			if(busNumber<6) {//剩余5台车一下，尽量按平均发
				interval=intervalAvg;
				intervalMin=null;
				for(int i=0;i<busAvailable.size()&&i<busNumber;i++) {
					Trip trip=busAvailable.get(i);
					Date planTimeMin=null;
					if(trip.getTripEndTime()==null) {
						planTimeMin=trip.getNextObuTimeMin();
					}else {
						planTimeMin=DateUtil.getDateAddMinute(trip.getTripEndTime(), 5);
					}
					if(trip.getNextObuTimeMin().after(tripFullLast.getTripBeginTime())) {
						int intervalTemp=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), planTimeMin)/(i+1);
						if(intervalMin==null||intervalTemp>intervalMin) {
							intervalMin=intervalTemp;
						}
					}
				}
				if(intervalMin!=null&&intervalMin>interval) {
					interval=intervalMin;
				}
			}else {
				if(intervalMin!=null) {
					if(intervalMin>maxInterval) {//断位压缩停站
						interval=getInterval4ExceedMaxInterval(direction, tripFullLast, busAvailable);
					}else if(intervalAvg<intervalMin) {
						interval=intervalMin;
					}
				}
			}
			Date obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
			if(tripArrival.isElecSupplyAfterTrip()) {
				if(obuTime.before(tripArrival.getNextObuTimeMin()))
					obuTime=tripArrival.getNextObuTimeMin();
			}else if(tripArrival.getTripEndTime()!=null&&!obuTime.after(tripArrival.getTripEndTime())&&obuTime.before(latestTime))
				obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
			if(obuTime.after(latestTime)) {
				if(tripArrival.getTripEndTime()!=null) {
					if(!tripArrival.getTripEndTime().after(obuTime)) {
						obuTime=latestTime;
					}
				}else {
					obuTime=latestTime;
				}
			}
			routeSchedule.setEaten(tripArrival.getBus(), tripArrival, obuTime, scheduleParam);
			Trip trip=new Trip(tripArrival.getBus(), direction, obuTime,scheduleParam,tripFullLast);
			Date arrivalTime=trip.getTripEndTime();
			int directionReverse=1-direction;
			Date latestTimeReverse=scheduleParam.getLatestTime(directionReverse);
			if(!arrivalTime.before(latestTimeReverse)) {//到站时间过了对面末班车
				List<Trip> tripListReverse=routeSchedule.getTripList(directionReverse);
				boolean isLatestBusPlaned=false;//对面末班是否已发标识
				if(!tripListReverse.isEmpty()) {
					Trip lastTripReverse=tripListReverse.get(tripListReverse.size()-1);
					if(lastTripReverse.getTripBeginTime().equals(latestTimeReverse)) {
						isLatestBusPlaned=true;
					}
				}
				if(!isLatestBusPlaned) {//对面尾车还没安排
					List<Trip> busQueueTemp=routeSchedule.getBusQueue(direction);
					if(busQueueTemp.isEmpty()) {//没车过去做尾车
						int diff=DateUtil.getMinuteInterval(arrivalTime, latestTimeReverse)+1;
						obuTime=DateUtil.getDateAddMinute(obuTime, -diff);
						arrivalTime=DateUtil.getDateAddMinute(arrivalTime, -diff);
						trip.setTripBeginTime(obuTime);
						trip.setTripEndTime(arrivalTime);
					}
				}
			}
			trip.setPreTrip(tripArrival);
			routeSchedule.addTrip(trip);
			addMiddleStopTrip(trip);
			busQueue.remove(tripArrival);
		}
	}
	
	private boolean isLastRound() {
		/*if(routeSchedule.getBusQueueDoubleAndEveningClasses().size()>10)//如果超过10台车，按半小时发
			return false;*/
		Trip tripFullLast=routeSchedule.getTripFullLast(Direction.UP.getValue());
		Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(Direction.UP.getValue(), tripFullLast.getTripBeginTime());
		if(obuTimeNextRound!=null&&!obuTimeNextRound.after(scheduleParam.getLatestTime(Direction.UP.getValue()))) {
			return false;
		}
		tripFullLast=routeSchedule.getTripFullLast(Direction.DOWN.getValue());
		obuTimeNextRound=scheduleParam.getObuTimeNextRound(Direction.DOWN.getValue(), tripFullLast.getTripBeginTime());
		if(obuTimeNextRound!=null&&!obuTimeNextRound.after(scheduleParam.getLatestTime(Direction.DOWN.getValue()))) {
			return false;
		}
		return true;
	}
	
	private boolean reCalculateByBusNumberReduce() {
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		Double maxLoad=null;
		ScheduleHalfHour scheduleHalfHourLowest=null;
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			int direction=scheduleHalfHour.getDirection();
			String runTime=scheduleHalfHour.getRunTime();
			if(!DateUtil.getCalendarHM(runTime).getTime().before(DateUtil.getDateHM("1000")))
				break;
			int minLongClassesNumber=scheduleParam.getMinLongClassesNumber(direction, runTime);//最低发班班次
			DispatchRule dispatchRule=scheduleParam.getDispatchRule(runTime, direction);
			int longClassesNumber=dispatchRule.getClassesNumber(runTime);//长线班次
			ShortLineSchedule shortLineSchedule=scheduleHalfHour.getShortLineSchedule();
			if(shortLineSchedule!=null||longClassesNumber>minLongClassesNumber) {//有短线或班次大于最低标准
				Calendar calendar=DateUtil.getCalendarHM(runTime);
				int minute=calendar.get(Calendar.MINUTE);
				if(minute!=0&&minute!=30) {//非整点或半点
            		if(minute<30) {
            			calendar.set(Calendar.MINUTE, 0);
            		}else {
            			calendar.set(Calendar.MINUTE, 30);
            		}
            	}
				int passengerNumber=scheduleParam.getHighSectionPassenger(direction, DateFormatUtil.HM.getDateString(calendar.getTime())).getCurrentNumber();
				int classesNumber=longClassesNumber;
				if(shortLineSchedule!=null) {
					classesNumber=longClassesNumber+shortLineSchedule.getShortLineClasses();
				}
				double maxLoadTemp=passengerNumber*1.0/classesNumber;
				if(maxLoad==null||maxLoadTemp<maxLoad) {
					if(shortLineSchedule!=null||dispatchRule.getMinClassesNumber(runTime)>1) {//可以减少短线或长线
						maxLoad=maxLoadTemp;
						scheduleHalfHourLowest=scheduleHalfHour;
					}
				}
			}
		}
		if(scheduleHalfHourLowest!=null) {//找到可以缩减的时段
			ShortLineSchedule shortLineSchedule=scheduleHalfHourLowest.getShortLineSchedule();
			if(shortLineSchedule!=null) {//先减少短线
				int classesNumber=shortLineSchedule.getShortLineClasses();
				classesNumber=classesNumber-1;
				if(classesNumber>0) {
					shortLineSchedule.setShortLineClasses(classesNumber);//还有短线，重设
				}else {
					scheduleHalfHourLowest.setShortLineSchedule(null);
				}
			}else {
				int direction=scheduleHalfHourLowest.getDirection();
				String runTime=scheduleHalfHourLowest.getRunTime();
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(runTime, direction);
				int longClassesNumber=dispatchRule.getClassesNumber(runTime);//长线班次
				dispatchRule.setClassesNumber(scheduleHalfHourLowest.getRunTime(), longClassesNumber-1);
			}
			routeSchedule=new RouteSchedule(scheduleParam);
			scheduleParam.generateObuTime();
			routeSchedule.calculateBusConfigNew(scheduleParam);
			generateSchedule();
		}else {//降低发班标准
			//找到最低发班数最大，满载率最低的时段
			int maxLongClassesNumber=2;//半小时至少发两班才能减少
			Double fullLoadRate=null;
			for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
				int direction=scheduleHalfHour.getDirection();
				String runTime=scheduleHalfHour.getRunTime();
				if(!DateUtil.getCalendarHM(runTime).getTime().before(DateUtil.getDateHM("1000")))
					break;
				if(scheduleHalfHour.getShortLineSchedule()!=null)
					System.out.println("aaa");
				int longClassesNumber=scheduleParam.getMinLongClassesNumber(direction, runTime);//最低发班班次
				if(longClassesNumber<maxLongClassesNumber) {
					continue;
				}
				maxLongClassesNumber=longClassesNumber;
				Calendar calendar=DateUtil.getCalendarHM(runTime);
				int minute=calendar.get(Calendar.MINUTE);
				if(minute!=0&&minute!=30) {//非整点或半点
            		if(minute<30) {
            			calendar.set(Calendar.MINUTE, 0);
            		}else {
            			calendar.set(Calendar.MINUTE, 30);
            		}
            	}
				int passengerNumber=scheduleParam.getHighSectionPassenger(direction, DateFormatUtil.HM.getDateString(calendar.getTime())).getCurrentNumber();
				double maxLoadTemp=passengerNumber*1.0/longClassesNumber;
				if(fullLoadRate==null||maxLoadTemp<fullLoadRate) {
					fullLoadRate=maxLoadTemp;
					scheduleHalfHourLowest=scheduleHalfHour;
				}
			}
			routeSchedule=new RouteSchedule(scheduleParam);
			if(scheduleHalfHourLowest!=null) {
				int direction=scheduleHalfHourLowest.getDirection();
				String runTime=scheduleHalfHourLowest.getRunTime();
				DispatchRule dispatchRule=scheduleParam.getDispatchRule(runTime, direction);
				int longClassesNumber=scheduleParam.getMinLongClassesNumber(direction, runTime);//最低发班班次
				dispatchRule.setMinClassesNumber(scheduleHalfHourLowest.getRunTime(), longClassesNumber-1);
				dispatchRule.setClassesNumber(scheduleHalfHourLowest.getRunTime(), longClassesNumber-1);
				scheduleParam.setMinLongClassesNumber(direction, runTime, longClassesNumber-1);
				scheduleParam.generateObuTime();
				routeSchedule.calculateBusConfig(scheduleParam);
				generateSchedule();
			}else {
				if(scheduleParam.isLoopLine()) {
					procMorningShiftBus();
					int busNumber=scheduleParam.getBusNumberPreset();
					calculate4LoopLine(busNumber);
				}else {
					calculateByHour();
				}
			}
		}
		return true;
	}
	
	private void proLastRound4LoopLine() {
		routeSchedule.tripListSort();
		int direction=Direction.UP.getValue();
		List<Trip> tripList=routeSchedule.getTripList(direction);
		List<Trip> lastRoundTripList=new ArrayList<Trip>();
		Trip latestBusTrip=null;
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			if(latestBusTrip!=null&&latestBusTrip.getBus()==trip.getBus()) {
				break;
			}
			if(!trip.isShortLine()&&trip.getDirection()==direction) {
				lastRoundTripList.add(0, trip);
				if(latestBusTrip==null)
					latestBusTrip=trip;
			}
		}
		Trip trip=lastRoundTripList.get(0);
		Trip lastTrip=trip.getLastTripFull();
		Date latestTime=scheduleParam.getLatestTime(direction);
		for(int i=0;i<lastRoundTripList.size();i++) {
			trip=lastRoundTripList.get(i);
			int interval=DateUtil.getMinuteInterval(lastTrip.getTripBeginTime(), latestTime)/(lastRoundTripList.size()-i);
			Date obuTime=DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval);
			if(obuTime.after(trip.getTripBeginTime())) {
				trip.setTripBeginTime(obuTime);
				trip.setTripEndTime(scheduleParam.getArrivalTime(obuTime, direction));
			}
			lastTrip=trip;
		}
	}
	
	private void  calculate4LoopLine(int busNumber) {
		if(busNumber==0)
			throw new MyException("-1", "环线出车数为零，请检查预设配车数和班别设置");
		Date firstTime=scheduleParam.getFirstTime(Direction.UP.getValue());
		Date arrivalTime=scheduleParam.getArrivalTime(firstTime, Direction.UP.getValue());
		Date nextObuTimeMin=DateUtil.getDateAddMinute(arrivalTime, scheduleParam.getMinRestTime(arrivalTime, Direction.UP.getValue()));
		int interval=DateUtil.getMinuteInterval(nextObuTimeMin, firstTime)/busNumber;
		routeSchedule=new RouteSchedule(scheduleParam);
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		for(int i=0;i<busNumber;i++) {
			int orderNumber=routeSchedule.newBusOrder(Direction.UP.getValue());
			Bus bus=new Bus(Direction.UP.getValue(), orderNumber);
			Trip trip=new Trip();
			trip.setBus(bus);
			trip.setNextObuTimeMin(DateUtil.getDateAddMinute(firstTime, i*interval));
			bus_queue_to.add(trip);
		}
		if(scheduleParam.getShiftListPreset()!=null) {
			//initTrip4Shift();
			Map<Integer, List<Trip>> firstRoundTripMap=new HashMap<Integer, List<Trip>>();
			firstRoundTripMap.put(Direction.UP.getValue(), bus_queue_to);
			initTrip4Shift(firstRoundTripMap);
		}
		Integer busNumberSinglePreset=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
		if(busNumberSinglePreset!=null&&busNumberSinglePreset!=0) {
			setSingleBus(bus_queue_to, busNumberSinglePreset);
		}
		Trip tripLast=null;
		Date latestTime=scheduleParam.getLatestTime(Direction.UP.getValue());
		while(tripLast==null||tripLast.getTripBeginTime().before(latestTime)) {
			bus_queue_to=routeSchedule.getBusQueue(Direction.DOWN.getValue());
			if(bus_queue_to.isEmpty()) {
				break;
			}
			Trip tripArrival=bus_queue_to.get(0);
			Bus bus=tripArrival.getBus();
			if(bus.isSingleClass()&&tripArrival.isQuitMark()) {
				if(!bus.isHasMiddleStop()) {
					routeSchedule.singleClassBusAdd(tripArrival, Direction.UP.getValue());
					bus.setHasMiddleStop(true);
				}
				bus_queue_to.remove(tripArrival);
				continue;
			}
			if(tripLast!=null&&tripLast.getTripBeginTime().after(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(), Calendar.MINUTE, -60))) {//准备单班复行
				singleClassRecovery(tripLast, bus_queue_to);
			}
			Date obuTime=null;
			if(tripLast==null) {
				obuTime=firstTime;
			}else if(bus_queue_to.size()==1) {//只有一台车
				obuTime=tripArrival.getNextObuTimeMin();
			}else {
				interval=0;
				for(int i=1;i<bus_queue_to.size();i++) {
					Trip tripTemp=bus_queue_to.get(i);
					if(tripTemp.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
						int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripTemp.getNextObuTimeMin())/(i+1);
						if(intervalTemp>interval)
							interval=intervalTemp;
					}
				}
				obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
				if(tripArrival.getPreTrip()==null&&tripArrival.getBus().getShiftType()!=null) {
					if(obuTime.before(tripArrival.getNextObuTimeMin()))
						obuTime=tripArrival.getNextObuTimeMin();
				}else {
					if(tripArrival.getTripEndTime()!=null) {
						if(obuTime.before(tripArrival.getNextObuTimeMin()))
							obuTime=tripArrival.getNextObuTimeMin();
					}
				}
				if(bus_queue_to.get(1).getNextObuTimeMin().after(latestTime)) {//下班车过了末班车
					obuTime=latestTime;
				}
			}
			if(obuTime.after(latestTime)) {
				if(tripArrival.getTripEndTime().before(latestTime)) {
					obuTime=latestTime;
				}else {
					tripLast.setTripBeginTime(latestTime);
					arrivalTime=scheduleParam.getArrivalTime(latestTime, Direction.UP.getValue());
					tripLast.setTripEndTime(arrivalTime);
					proLastRound4LoopLine();
					return;
				}
				
			}
			routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
			Trip trip=new Trip(bus, Direction.UP.getValue(), obuTime, scheduleParam, tripLast);
			trip.setFirstRouteStaId(scheduleParam.getFirstRouteStaId(Direction.UP.getValue()));
			trip.setLastRouteStaId(scheduleParam.getLastRouteStaId(Direction.UP.getValue()));
			trip.setNextObuTimeMin(scheduleParam);
			routeSchedule.addTrip(trip);
			bus_queue_to.remove(tripArrival);
			if(tripArrival.getTripEndTime()==null)
				addMiddleStopTrip(trip);
			tripLast=trip;
			if(bus.getShiftType()!=null) {
				ScheduleParamShift shift=bus.getShiftType();
				if(!trip.getNextObuTimeMin().before(DateUtil.getDateAddMinute(DateUtil.getDateHM(shift.getEndTime()), -20))) {
					Trip tripRecovery=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), DateUtil.getDateHM(shift.getEndTime()));
					if(tripRecovery!=null) {
						if(!tripRecovery.getNextObuTimeMin().after(trip.getNextObuTimeMin())) {
							trip.setQuitMark(false);
						}else {
							routeSchedule.addShortTripBack(tripRecovery);
						}
					}
					bus_queue_to.remove(trip);
				}
			}
		}
		proLastRound4LoopLine();
	}
	
	
	
	
	private void reCalculateByBusNumberIncrease(int busNumberPlan) {
		int busNumber=routeSchedule.getBusNumber();
		while(busNumber<busNumberPlan) {
			HighSectionPassenger highSectionPassengerUp=getMaxLoadFactorTime(Direction.UP.getValue());
			HighSectionPassenger highSectionPassengerDown=getMaxLoadFactorTime(Direction.DOWN.getValue());
			HighSectionPassenger highSectionPassenger=highSectionPassengerUp;
			if(!scheduleParam.isLoopLine()) {
				if(highSectionPassengerUp==null) {
					highSectionPassenger=highSectionPassengerDown;
				}else if(highSectionPassengerDown!=null){
					if(highSectionPassengerDown.getHighSectionPassenger()/highSectionPassengerDown.getClassesNumber()>
						highSectionPassengerUp.getHighSectionPassenger()/highSectionPassengerUp.getClassesNumber()) {
						highSectionPassenger=highSectionPassengerDown;
					}
				}
			}
			int direction=highSectionPassenger.getDirection();
			List<Trip> tripList=routeSchedule.getTripList(highSectionPassenger.getDirection());
			routeSchedule.tripListSort(tripList);
			Date runTime=DateUtil.getCalendarHM(highSectionPassenger.getRunTime()).getTime();
			Date runTimeEnd=DateUtil.getDateAdd(runTime, Calendar.MINUTE, 30);
			List<Trip> tripListTemp=new ArrayList<Trip>();
			for(Trip trip:tripList) {
				if(DateUtil.isInTimeRange(trip.getTripBeginTime(), runTime, runTimeEnd)&&!trip.isShortLine()) {
					tripListTemp.add(trip);
				}
			}
			if(tripListTemp.isEmpty())
				System.out.println("aaa");
			Trip lastTrip=tripListTemp.get(0).getLastTrip();
			Trip nextTrip=tripListTemp.get(tripListTemp.size()-1).getNextTrip();
			Date lastTripBeginTime=null;//上一时段最后发班时间
			if(lastTrip==null) {//第一台是头车
				lastTripBeginTime=tripListTemp.get(0).getTripBeginTime();
				lastTrip=tripListTemp.get(0);
				tripListTemp.remove(0);
			}else {
				lastTripBeginTime=lastTrip.getTripBeginTime();
			}
			if(nextTrip==null){
				nextTrip=tripListTemp.get(tripListTemp.size()-1);
				tripListTemp.remove(nextTrip);
			}
			Long interval=(nextTrip.getTripBeginTime().getTime()-lastTripBeginTime.getTime())/60/1000/(tripListTemp.size()+2);
			Date obuTime=DateUtil.getDateAdd(lastTripBeginTime, Calendar.MINUTE, interval.intValue());
			int startOrderNumber=routeSchedule.newBusOrder(direction);
			Bus bus=new Bus(direction, startOrderNumber,obuTime);
			Trip trip=new Trip(bus, direction, obuTime, scheduleParam,lastTrip);
			trip.setFirstTask(true);
			routeSchedule.addTrip(trip);
			lastTripBeginTime=obuTime;
			for(int i=0;i<tripListTemp.size();i++) {
				interval=(nextTrip.getTripBeginTime().getTime()-lastTripBeginTime.getTime())/60/1000/((tripListTemp.size()-i)+1);
				obuTime=DateUtil.getDateAdd(lastTripBeginTime, Calendar.MINUTE, interval.intValue());
				trip=tripListTemp.get(i);
				trip.setTripBeginTime(obuTime);
				trip.setNextObuTimeMin(scheduleParam);
				lastTripBeginTime=obuTime;
			}
			routeSchedule.tripListSort(tripList);
			addBusProc();
			reCalculate(DateUtil.getDateHM("1000"));
			//busNumber=routeSchedule.getTripMap().size();
			busNumber++;
		}
	}
	
	private void proStartBus() {
		Integer busNumberPresetUp=scheduleParam.getBusNumberPresetUp();
		Integer busNumberPresetDown=scheduleParam.getBusNumberPresetDown();
		if(busNumberPresetUp!=null&&busNumberPresetDown!=null&&
				routeSchedule.getTripMap().size()==busNumberPresetUp+busNumberPresetDown) {//配车数与预设一致
			resetBusStartDirectionAndOrderNumber();
			int busNumberUp=0;
			for(Bus bus:routeSchedule.getTripMap().keySet()) {
				if(bus.getStartDirection()==Direction.UP.getValue()) {
					busNumberUp++;
				}
			}
			if(busNumberUp==busNumberPresetUp)//车数一致
				return;
			List<Trip> tripList=null;
			if(busNumberUp>busNumberPresetUp) {//当前上行出车数大于预设值
				tripList=routeSchedule.getTripList(Direction.UP.getValue());
			}else {
				tripList=routeSchedule.getTripList(Direction.DOWN.getValue());
			}
			Trip lastBusFirstTrip=null;//最后出场车辆
			for(Trip trip:tripList) {
				if(trip.isFirstTask()) {
					lastBusFirstTrip=trip;
				}
			}
			retryTime++;
			if(retryTime>10000)
				throw new MyException("-2", "计划生成失败，请联系开发人员"+scheduleParam.getRouteId()+"|"+new Date());
			proLateStartBus(tripList, lastBusFirstTrip);//把最后出场的车辆安排到对面出场
			reCalculate(DateUtil.getDateHM("1000"));
			proStartBus();
		}
	}
	
	private void resetFirstRoundMinObuTime(int direction) {
		Date firstBusReverseArrivalTime=scheduleParam.getArrivalTime(scheduleParam.getFirstTime(1-direction), 1-direction);//反向头车到达时间
		int restTime=scheduleParam.getMinRestTime(firstBusReverseArrivalTime, direction);
		Date firstBusMinObuTime=DateUtil.getDateAdd(firstBusReverseArrivalTime, Calendar.MINUTE, restTime);//上行头车到下行最早发班时间
		int firstRoundBusNumber=0;
		List<Trip> firstRoundTripList=new ArrayList<Trip>();
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			busTripListSort(busTripList);
			if(busTripList.isEmpty())
				System.out.println("aaa");
			Trip firstRoundTrip=busTripList.get(0);
			if(firstRoundTrip.getTripBeginTime().before(firstBusMinObuTime)&&firstRoundTrip.getDirection()==direction) {
				if(!firstRoundTrip.isShortLine())
					firstRoundBusNumber++;
				firstRoundTripList.add(firstRoundTrip);
			}
		}
		if(firstRoundBusNumber==0)
			return;
		Date firstTime=scheduleParam.getFirstTime(direction);
		Long interval=(firstBusMinObuTime.getTime()-firstTime.getTime())/1000/60/firstRoundBusNumber;
		routeSchedule.tripListSort(firstRoundTripList);
		Date obuTime=scheduleParam.getFirstTime(direction);
		for(Trip firstRoundTrip:firstRoundTripList) {
			firstRoundTrip.setTripBeginTime(obuTime);
			if(firstRoundTrip.isShortLine()||(firstRoundTrip.getNextTrip()!=null&&firstRoundTrip.getNextTrip().isShortLine())) {
				obuTime=DateUtil.getDateAdd(obuTime, Calendar.MINUTE, interval.intValue()/2);
			}else {
				obuTime=DateUtil.getDateAdd(obuTime, Calendar.MINUTE, interval.intValue());
			}
		}
	}
	
	private void proceFirstBus(int direction) {
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date firstTimeReverse=scheduleParam.getFirstTime(1-direction);
		Date arrivalTime=scheduleParam.getArrivalTime(firstTimeReverse, 1-direction);//到达这边总站的时间
		if(arrivalTime.after(firstTime)) {
			Bus firstBus=null;
			for(Bus bus:routeSchedule.getTripMap().keySet()) {
				List<Trip> busTripList=routeSchedule.getTripList(bus);
				busTripListSort(busTripList);
				Trip firstRoundTrip=busTripList.get(0);
				if(firstRoundTrip.getDirection()==direction) {
					firstBus=firstRoundTrip.getBus();
					break;
				}
			}
			if(firstBus==null) {//没头车
				List<Trip> firstRoundTripListReverse=new ArrayList<Trip>();
				for(Bus bus:routeSchedule.getTripMap().keySet()) {
					List<Trip> busTripList=routeSchedule.getTripList(bus);
					busTripListSort(busTripList);
					Trip firstRoundTrip=busTripList.get(0);
					if(firstRoundTrip.getDirection()==1-direction) {
						firstRoundTripListReverse.add(firstRoundTrip);
					}
				}
				routeSchedule.tripListSort(firstRoundTripListReverse);
				if(!firstRoundTripListReverse.isEmpty()) {
					Trip trip=firstRoundTripListReverse.get(firstRoundTripListReverse.size()-1);
					Bus bus=trip.getBus();
					bus.setStartDirection(direction);
					bus.setStartOrderNumber(1);
					trip.setTripBeginTime(firstTime);
					trip.setDirection(direction);
				}
			}
		}
	}
	
	private Map<Integer, List<Trip>> getFirstRoundTripMap(){
		Map<Integer,List<Trip>> firstRoundTripMap=new HashMap<Integer, List<Trip>>();
		if(!scheduleParam.isLoopLine()) {
			if(scheduleParam.isAppearDirection(Direction.UP.getValue())&&scheduleParam.isAppearDirection(Direction.DOWN.getValue())) {
				resetFirstRoundMinObuTime(Direction.UP.getValue());//上行首轮间隔
				resetFirstRoundMinObuTime(Direction.DOWN.getValue());//下行首轮间隔
			}else if(!scheduleParam.isAppearDirection(Direction.UP.getValue())&&
				scheduleParam.getBusNumberPresetUp()==null) {//上行不能出车
				proceFirstBus(Direction.UP.getValue());
			}else if(!scheduleParam.isAppearDirection(Direction.DOWN.getValue())&&
					scheduleParam.getBusNumberPresetDown()==null) {
				proceFirstBus(Direction.DOWN.getValue());
			}
		}
		
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(!elecSupply)
				bus.setSingleClass(false);
			bus.setLunchEaten(false);
			bus.setSupperEaten(false);
			bus.setHasMiddleStop(false);
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			busTripListSort(busTripList);
			if(bus.getStartOrderNumber()==16)
				System.out.println("aa");
			Trip firstRoundTrip=busTripList.get(0);
			List<Trip> firstRoundPreTripList=firstRoundTripMap.get(firstRoundTrip.getDirection());
			if(firstRoundPreTripList==null) {
				firstRoundPreTripList=new ArrayList<Trip>();
				firstRoundTripMap.put(firstRoundTrip.getDirection(), firstRoundPreTripList);
			}
			Trip preTrip=new Trip();
			preTrip.setBus(firstRoundTrip.getBus());
			Date minObuTime=firstRoundTrip.getTripBeginTime();
			if(!scheduleParam.isBackInsert()) {
				if(scheduleParam.getFirstPlanObuTimeLatest(firstRoundTrip.getDirection())==null) {
					System.out.println("aaa");
				}
				Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(firstRoundTrip.getDirection());
				if(firstPlanObuTimeLatest!=null&&firstRoundTrip.getTripBeginTime().after(firstPlanObuTimeLatest)) {
					minObuTime=scheduleParam.getFirstPlanObuTimeLatest(firstRoundTrip.getDirection());
				}
			}
			preTrip.setNextObuTimeMin(minObuTime);
			preTrip.setFirstTask(true);
			System.out.println(firstRoundTrip.getBusName()+"\t"+firstRoundTrip.getTripBeginTime());
			firstRoundPreTripList.add(preTrip);
		}
		for(List<Trip> tripList:firstRoundTripMap.values()) {
			Collections.sort(tripList, new MinObuTimeComparator());
		}
		if(scheduleParam.getRouteStaTurnList().isEmpty()) {
			int direction=Direction.DOWN.getValue();
			if(scheduleParam.isLoopLine()) {
				direction=Direction.UP.getValue();
			}
			List<Trip> firstRoundTripList=firstRoundTripMap.get(Direction.UP.getValue());
			if(firstRoundTripList!=null&&!firstRoundTripList.isEmpty()) {
				firstRoundObuTimeOptimise(firstRoundTripList, direction);
			}
			if(!scheduleParam.isLoopLine()) {
				direction=Direction.UP.getValue();
				firstRoundTripList=firstRoundTripMap.get(Direction.DOWN.getValue());
				if(firstRoundTripList!=null&&!firstRoundTripList.isEmpty()) {
					firstRoundObuTimeOptimise(firstRoundTripList, direction);
				}
			}
		}
		return firstRoundTripMap;
	}
	
	private void firstRoundObuTimeOptimise(List<Trip> firstRoundTripList,int toDirection) {
		Date firstTime=scheduleParam.getFirstTime(toDirection);
		Date firstBusReverseObuTime=scheduleParam.getMinObuTimeNext(toDirection, firstTime);
		List<Trip> busQueue=new ArrayList<Trip>();
		for(Trip trip:firstRoundTripList) {
			if(trip.getNextObuTimeMin().before(firstBusReverseObuTime)) {
				busQueue.add(trip);
			}
		}
		Trip trip=new Trip();
		trip.setNextObuTimeMin(firstBusReverseObuTime);
		busQueue.add(trip);
		for(int i=1;i<busQueue.size()-1;i++) {
			trip=busQueue.get(i);
			Trip tripLast=busQueue.get(i-1);
			int interval=(int)Math.ceil(DateUtil.getMinuteInterval(tripLast.getNextObuTimeMin(), firstBusReverseObuTime)*1.0/(busQueue.size()-i));
			Date obuTime=DateUtil.getDateAddMinute(tripLast.getNextObuTimeMin(), interval);
			trip.setNextObuTimeMin(obuTime);
		}
	}
	
	private void reCalculate(Date endTime) {
		Map<Integer,List<Trip>> firstRoundTripMap=getFirstRoundTripMap();
		int busOrderUp=routeSchedule.getBusOrder(Direction.UP.getValue());
		int busOrderDown=routeSchedule.getBusOrder(Direction.DOWN.getValue());
		routeSchedule=new RouteSchedule(scheduleParam,busOrderUp,busOrderDown);
		calculateFromStartWorkTime(firstRoundTripMap,endTime,false);
	}

	private Map<Integer,List<Trip>> reCalculate4InitShift() {
		Map<Integer,List<Trip>> firstRoundTripMap=getFirstRoundTripMap();
		int busOrderUp=routeSchedule.getBusOrder(Direction.UP.getValue());
		int busOrderDown=routeSchedule.getBusOrder(Direction.DOWN.getValue());
		routeSchedule=new RouteSchedule(scheduleParam,busOrderUp,busOrderDown);
		if(scheduleParam.getShiftListPreset()!=null) {//初始化班别
			if(!elecSupply)//之前已经算过了
				initTrip4Shift(firstRoundTripMap);
		}
		resetFirstRoundObuTimeMin(Direction.UP.getValue(), firstRoundTripMap.get(Direction.UP.getValue()));
		if(!scheduleParam.isLoopLine())
			resetFirstRoundObuTimeMin(Direction.DOWN.getValue(), firstRoundTripMap.get(Direction.DOWN.getValue()));
		if(!elecSupply) {//之前已经算过了
			if(scheduleParam.getBusNumberPreset()!=null&&
					(!scheduleParam.getRouteStaTurnList().isEmpty()||scheduleParam.isLoopLine())) {
				List<Trip> firstTripListUp=firstRoundTripMap.get(Direction.UP.getValue());
				List<Trip> firstTripListDown=firstRoundTripMap.get(Direction.DOWN.getValue());
				Integer busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
				if(busNumberSingle!=null&&busNumberSingle!=0&&firstTripListUp!=null) {
					Collections.sort(firstTripListUp, new MinObuTimeComparator());
					setSingleBus(firstTripListUp, busNumberSingle);
				}
				busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
				if(busNumberSingle!=null&&busNumberSingle!=0&&firstTripListDown!=null) {
					Collections.sort(firstTripListDown, new MinObuTimeComparator());
					setSingleBus(firstTripListDown, busNumberSingle);
				}
			}
			if(scheduleParam.getBusNumberPresetUp()==null&&
				scheduleParam.getBusNumberPresetDown()==null){
				resetFirstRoundTime(firstRoundTripMap);
			}
		}
		return firstRoundTripMap;
	}
	
	private void resetFirstRoundTime(Map<Integer,List<Trip>> firstRoundTripMap) {
		if(firstRoundTripMap.size()<2) {
			return;
		}
		Map<Integer, Date> minObuTimeNextMap=new HashMap<Integer, Date>();
		for(Integer direction:firstRoundTripMap.keySet()) {
			Date minObuTimeNext=scheduleParam.getMinObuTimeNext(direction, scheduleParam.getFirstTime(direction));
			minObuTimeNextMap.put(1-direction, minObuTimeNext);
		}
		while(scheduleParam.getBusNumberSingle()>0) {
			Trip singleBusTrip=null;
			for(Integer direction:firstRoundTripMap.keySet()) {
				Date minObuTimeNext=minObuTimeNextMap.get(direction);
				List<Trip> firstRoundTripList=firstRoundTripMap.get(direction);
				for(int i=firstRoundTripList.size()-1;i>=0;i--) {
					Trip firstRoundTrip=firstRoundTripList.get(i);
					if(!firstRoundTrip.getBus().isSingleClass()&&
						firstRoundTrip.getBus().getStartOrderNumber()>=scheduleParam.getMiddleStopBeginOrderNumber()
						&&firstRoundTrip.getNextObuTimeMin().after(minObuTimeNext)) {
						if(singleBusTrip==null) {
							singleBusTrip=firstRoundTrip;
						}else {
							if(DateUtil.getMinuteInterval(scheduleParam.getFirstTime(direction), firstRoundTrip.getNextObuTimeMin())>
								DateUtil.getMinuteInterval(scheduleParam.getFirstTime(1-direction), singleBusTrip.getNextObuTimeMin())) {
								singleBusTrip=firstRoundTrip;
							}
						}
						break;
					}
				}
			}
			if(singleBusTrip!=null) {
				singleBusTrip.getBus().setSingleClass(true);
				scheduleParam.setBusNumberSingle(scheduleParam.getBusNumberSingle()-1);
			}else {
				break;
			}
		}
		Map<Integer, List<Trip>> firstRoundDoubleTripMap=new HashMap<Integer, List<Trip>>();
		for(Integer direction:firstRoundTripMap.keySet()) {
			Date minObuTimeNext=minObuTimeNextMap.get(direction);
			List<Trip> firstRoundTripList=firstRoundTripMap.get(direction);
			List<Trip> firstRoundDoubleTripList=new ArrayList<Trip>();
			for(Trip firstRoundTrip:firstRoundTripList) {
				if(!firstRoundTrip.getBus().isSingleClass()) {
					firstRoundDoubleTripList.add(firstRoundTrip);
				}
			}
			firstRoundDoubleTripMap.put(direction, firstRoundDoubleTripList);
			Date obuTime=scheduleParam.getFirstTime(direction);
			for(int i=0;i<firstRoundDoubleTripList.size();i++) {
				Trip firstRoundDoubleTrip=firstRoundDoubleTripList.get(i);
				firstRoundDoubleTrip.setNextObuTimeMin(obuTime);
				int interval=(int)Math.ceil(DateUtil.getMinuteInterval(obuTime, minObuTimeNext)*1.0/(firstRoundDoubleTripList.size()-i));
				obuTime=DateUtil.getDateAddMinute(obuTime, interval);
			}
			System.out.println("aaa");
		}
		while(scheduleParam.getBusNumberSingle()>0) {
			Trip singleBusTrip=null; 
			for(Integer direction:firstRoundDoubleTripMap.keySet()) {
				List<Trip> firstRoundDoubleTripList=firstRoundDoubleTripMap.get(direction);
				Integer singleClassLastIndex=null;
				for(int i=firstRoundDoubleTripList.size()-1;i>=0;i--) {
					Trip firstRoundTrip=firstRoundDoubleTripList.get(i);
					if(firstRoundTrip.getBus().isSingleClass()) {
						singleClassLastIndex=i;
					}
				}
				Trip firstRoundTrip=null;
				if(singleClassLastIndex==null) {
					firstRoundTrip=firstRoundDoubleTripList.get(firstRoundDoubleTripList.size()-1);
				}else {
					if(singleClassLastIndex>2) {
						firstRoundTrip=firstRoundDoubleTripList.get(singleClassLastIndex-2);
					}
				}
				if(firstRoundTrip!=null) {
					if(firstRoundTrip.getBus().getStartOrderNumber()<scheduleParam.getMiddleStopBeginOrderNumber()) {
						firstRoundTrip=null;
					}
				}
				if(singleBusTrip==null) {
					singleBusTrip=firstRoundTrip;
				}else if(firstRoundTrip!=null){
					if(DateUtil.getMinuteInterval(scheduleParam.getFirstTime(direction), firstRoundTrip.getNextObuTimeMin())>
						DateUtil.getMinuteInterval(scheduleParam.getFirstTime(singleBusTrip.getDirection()), singleBusTrip.getNextObuTimeMin())) {
						singleBusTrip=firstRoundTrip;
					}
				}
			}
			if(singleBusTrip!=null) {
				singleBusTrip.getBus().setSingleClass(true);
				scheduleParam.setBusNumberSingle(scheduleParam.getBusNumberSingle()-1);
			}else{
				scheduleParam.setBusNumberSingle(0);
			}
		}
	}
	
	private void setSingleBus(Map<Integer, List<Trip>> firstRoundTripMap,int busNumberSingle) {
		int times=0;
		int minOrderNumber=scheduleParam.getMiddleStopBeginOrderNumber();
		Integer singleBusIndexUp=null;
		Integer singleBusIndexDown=null;
		List<Trip> firstTripListUp=firstRoundTripMap.get(Direction.UP.getValue());
		List<Trip> firstTripListDown=firstRoundTripMap.get(Direction.DOWN.getValue());
		if(firstTripListUp!=null&&!firstTripListUp.isEmpty()) {
			singleBusIndexUp=firstTripListUp.size()-1;
		}
		if(firstTripListDown!=null&&!firstTripListDown.isEmpty()) {
			singleBusIndexDown=firstTripListDown.size()-1;
		}
		while(times++<100) {
			if(busNumberSingle==0)
				return;
			Trip singleBusUp=null;
			Trip singleBusDown=null;
			Trip singleBus=null;
			if(singleBusIndexUp!=null&&singleBusIndexUp>=minOrderNumber-1) {
				singleBusUp=firstTripListUp.get(singleBusIndexUp);
			}
			if(singleBusIndexDown!=null&&singleBusIndexDown>=minOrderNumber-1) {
				singleBusDown=firstTripListDown.get(singleBusIndexDown);
			}
			if(singleBusUp==null&&singleBusDown==null) {
				break;
			}else if(singleBusUp!=null&&singleBusDown!=null) {
				singleBus=singleBusUp;
				if(singleBusDown.getNextObuTimeMin().after(singleBusUp.getNextObuTimeMin())) {
					singleBus=singleBusDown;
				}
			}else if(singleBusUp==null) {
				singleBus=singleBusDown;
			}else if(singleBusDown==null) {
				singleBus=singleBusUp;
			}
			if(singleBus==singleBusUp) {
				singleBusIndexUp=singleBusIndexUp-2;
			}else {
				singleBusIndexDown=singleBusIndexDown-2;
			}
			singleBus.getBus().setSingleClass(true);
			busNumberSingle--;
		}
			
	}
	
	private void setSingleBus(List<Trip> firstTripList,int busNumberSingle) {
		int minOrderNumber=scheduleParam.getMiddleStopBeginOrderNumber();
		for(int i=firstTripList.size()-1;i>=minOrderNumber-1;) {
			Trip firstTrip=firstTripList.get(i);
			Bus bus=firstTrip.getBus();
			if(bus.getShiftType()!=null) {//已设置班别
				i--;
				continue;
			}
			bus.setSingleClass(true);
			busNumberSingle--;
			i=i-2;
			if(busNumberSingle==0)
				break;
		}
		if(busNumberSingle>0) {
			for(int i=firstTripList.size()-1;i>=0;i--) {
				Trip firstTrip=firstTripList.get(i);
				Bus bus=firstTrip.getBus();
				if(bus.getShiftType()!=null) {//已设置班别
					continue;
				}
				if(!bus.isSingleClass()) {
					bus.setSingleClass(true);
					busNumberSingle--;
					if(busNumberSingle==0)
						break;
				}
			}
		}
	}
	
	private void resetFirstRoundObuTimeMin(int direction,List<Trip> firstRoundTripList) {
		if(firstRoundTripList==null)
			return;
		int reverseDirection=1-direction;
		if(scheduleParam.isLoopLine()) {
			reverseDirection=direction;
		}
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date firstTimeReverse=scheduleParam.getFirstTime(reverseDirection);
		Date arrivalTime=scheduleParam.getArrivalTime(firstTimeReverse, reverseDirection);
		Date nextObuTimeMin=DateUtil.getDateAddMinute(arrivalTime, scheduleParam.getMinRestTime(arrivalTime, direction));
		for(int i=firstRoundTripList.size()-1;i>0;i--) {
			Trip trip=firstRoundTripList.get(i);
			if(trip.getNextObuTimeMin().after(nextObuTimeMin))
				continue;
			if(nextObuTimeMin.before(firstTime))
				break;
			int maxInteval=scheduleParam.getMaxInterval(nextObuTimeMin, direction);
			nextObuTimeMin=DateUtil.getDateAddMinute(nextObuTimeMin, -maxInteval);
			Bus bus=trip.getBus();
			bus.setStartTimeMin(nextObuTimeMin);
			if(trip.getNextObuTimeMin().before(nextObuTimeMin)) {
				trip.setNextObuTimeMin(nextObuTimeMin);
			}
		}
	}
	
	private void initTrip4NotMorningShift(int direction,int shiftType,int busNumber) {
		Date firstTime=scheduleParam.getFirstTime(direction);
		for(int i=0;i<busNumber;i++) {
			int startOrderNumber=routeSchedule.newBusOrder(direction);
			Bus bus=new Bus(direction, startOrderNumber);
			Trip trip=scheduleParam.busCheckIn(bus, shiftType,firstTime);
			if(trip!=null) {
				ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shiftType, bus.getStartDirection(), firstTime);
				routeSchedule.addShiftBus(scheduleParamShift, bus);
			}
		}
	}
	
	private void initTrip4Shift(Map<Integer,List<Trip>> firstRoundTripMap) {
		for(ScheduleShiftPreset shiftPreset:scheduleParam.getShiftListPreset()) {
			int busNumber=shiftPreset.getBusNumberUp();
			if(busNumber!=0) {
				Date firstTime=scheduleParam.getFirstTime(Direction.UP.getValue());
				ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shiftPreset.getShiftType(), Direction.UP.getValue(), firstTime);
				if(shiftPreset.getShiftType()==ShiftType.MORNING_SHIFT.getValue()&&
						!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(firstTime)) {//早班，并且一开始就可以做早班
					List<Trip> firstTripList=firstRoundTripMap.get(Direction.UP.getValue());
					for(int i=0;i<firstTripList.size();i=i+2) {
						Trip firstTrip=firstTripList.get(i);
						firstTrip.getBus().setShiftType(scheduleParamShift);
						busNumber--;
						if(busNumber==0)
							break;
					}
					if(busNumber>0) {
						for(int i=0;i<firstTripList.size();i++) {
							Trip firstTrip=firstTripList.get(i);
							Bus bus=firstTrip.getBus();
							if(bus.getShiftType()==null) {
								bus.setShiftType(scheduleParamShift);
								busNumber--;
								if(busNumber==0)
									break;
							}
						}
					}
				}else {
					initTrip4NotMorningShift(Direction.UP.getValue(), shiftPreset.getShiftType(), busNumber);
				}
			}
			busNumber=shiftPreset.getBusNumberDown();
			if(busNumber!=0) {
				Date firstTime=scheduleParam.getFirstTime(Direction.DOWN.getValue());
				ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shiftPreset.getShiftType(), Direction.DOWN.getValue(), firstTime);
				if(shiftPreset.getShiftType()==ShiftType.MORNING_SHIFT.getValue()&&
						!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(firstTime)) {
					List<Trip> firstTripList=firstRoundTripMap.get(Direction.DOWN.getValue());
					for(int i=0;i<firstTripList.size();i=i+2) {
						Trip firstTrip=firstTripList.get(i);
						firstTrip.getBus().setShiftType(scheduleParamShift);
						busNumber--;
						if(busNumber==0)
							break;
					}
					if(busNumber>0) {
						for(int i=0;i<firstTripList.size();i++) {
							Trip firstTrip=firstTripList.get(i);
							Bus bus=firstTrip.getBus();
							if(bus.getShiftType()==null) {
								bus.setShiftType(scheduleParamShift);
								busNumber--;
								if(busNumber==0)
									break;
							}
						}
					}
				}else {
					initTrip4NotMorningShift(Direction.DOWN.getValue(), shiftPreset.getShiftType(), busNumber);
				}
			}
		}
	}
	
	private void calculateFromStartWorkTime(Map<Integer,List<Trip>> firstRoundTripMap,Date endTime,boolean fixStartWorkTime) {
		Trip firstRoundLastTripUp=null;
		Trip firstRoundLastTripDown=null;
		int busNumber=0;
		for(Integer direction:firstRoundTripMap.keySet()) {
			List<Trip> firstRoundPreTripList=firstRoundTripMap.get(direction);
			busNumber+=firstRoundPreTripList.size();
			Collections.sort(firstRoundPreTripList, new MinObuTimeComparator());
			for(Trip trip:firstRoundPreTripList) {
				if(trip.isReverseInsert())
					break;
				if(direction==Direction.UP.getValue())
					firstRoundLastTripUp=trip;
				else if(direction==Direction.DOWN.getValue())
					firstRoundLastTripDown=trip;
			}
		}
		initBusQueue(Direction.UP.getValue(), firstRoundTripMap.get(Direction.UP.getValue()));
		initBusQueue(Direction.DOWN.getValue(), firstRoundTripMap.get(Direction.DOWN.getValue()));
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			System.out.println(scheduleHalfHour.getDirection()+scheduleHalfHour.getRunTime());
			int direction=scheduleHalfHour.getDirection();
			Date runTimeEnd=DateUtil.getCalendarHM(scheduleHalfHour.getRunTimeEnd()).getTime();
			if(runTimeEnd.after(endTime)) {//只计算早高峰
				if(busNumber<=routeSchedule.getTripMap().size())
					return;
				else {					
					System.out.println("aaa");
				}
			}
			List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
			//处理首轮出车开始
			List<Trip> firstRoundPreTripList=firstRoundTripMap.get(scheduleHalfHour.getDirection());
			if(firstRoundPreTripList==null)
				firstRoundPreTripList=new ArrayList<Trip>();
			if(fixStartWorkTime) {//固定车序
				Trip firstTripTo=null;
				for(Trip trip:bus_queue_to) {
					if(trip.getTripBeginTime()!=null&&trip.getTripBeginTime().equals(scheduleParam.getFirstTime(1-direction))) {
						firstTripTo=trip;
						break;
					}
				}
				if(firstTripTo!=null) {
					Trip firstRoundLastTrip=firstRoundLastTripUp;
					if(direction==Direction.DOWN.getValue());
						firstRoundLastTrip=firstRoundLastTripDown;
					if(firstRoundLastTrip!=null&&firstTripTo.getNextObuTimeMin().before(firstRoundLastTrip.getNextObuTimeMin())) {
						firstTripTo.setNextObuTimeMin(firstRoundLastTrip.getNextObuTimeMin());
					}
				}
			}
			Trip lastTripTo=null;//最后对向来车
			for(Trip trip:bus_queue_to) {
				if(trip.getTripBeginTime()!=null) {
					lastTripTo=trip;
				}
			}
			if(lastTripTo!=null) {//对向头车已开出
				for(int i=0;i<firstRoundPreTripList.size();) {//把首轮车加入到站队列
					Trip firstRoundPreTrip=firstRoundPreTripList.get(i);
					if(lastTripTo.getNextObuTimeMin()==null)
						System.out.println("aa");
					if(!firstRoundPreTrip.getNextObuTimeMin().after(lastTripTo.getNextObuTimeMin())) {//首轮车在到站队列最后一台车前到
						for(int j=0;j<bus_queue_to.size();j++) {
							Trip tripTo=bus_queue_to.get(j);
							if(!firstRoundPreTrip.getNextObuTimeMin().after(tripTo.getNextObuTimeMin())) {//找到对应位置插入
								bus_queue_to.add(j, firstRoundPreTrip);
								firstRoundPreTripList.remove(firstRoundPreTrip);
								break;
							}
						}
					}else {
						break;
					}
				}
			}else {//对面车没发出
				int directionReverse=1-direction;
				if(scheduleParam.isLoopLine()) {
					directionReverse=direction;
				}
				Date firstBusReverseArrivalTime=scheduleParam.getArrivalTime(scheduleParam.getFirstTime(directionReverse), directionReverse);//反向头车到达时间
				int restTime=scheduleParam.getMinRestTime(firstBusReverseArrivalTime, direction);
				Date firstBusReverseMinObuTime=DateUtil.getDateAdd(firstBusReverseArrivalTime, Calendar.MINUTE, restTime);//反向头车最早发班时间
				for(int i=0;i<firstRoundPreTripList.size();){
					Trip firstRoundPreTrip=firstRoundPreTripList.get(i);
					if(firstRoundPreTrip.getNextObuTimeMin().after(firstBusReverseMinObuTime)) {//只加反向头车到之前的车
						break;
					}
					bus_queue_to.add(firstRoundPreTrip);
					firstRoundPreTripList.remove(firstRoundPreTrip);
				}
			}
			//处理首轮出车结束
			//重算时段班次
			Integer interval=scheduleParam.getLeaveImmediatelyInterval(direction);
			if(interval==null) {
				reCalculate(scheduleHalfHour, bus_queue_to);
			}else {
				reCalculateLeaveImmediately(scheduleHalfHour.getDirection(), bus_queue_to, interval);
			}
		}
		//如果是到站即走，先发回去
		int direction=Direction.UP.getValue();
		Integer interval=scheduleParam.getLeaveImmediatelyInterval(direction);
		if(!scheduleParam.isLoopLine()&&interval!=null) {//到对面到站即走，先发了
			List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
			reCalculateLeaveImmediately(direction, bus_queue_to, interval);
		}
		direction=Direction.DOWN.getValue();
		interval=scheduleParam.getLeaveImmediatelyInterval(direction);
		if(!scheduleParam.isLoopLine()&&interval!=null) {//到对面到站即走，先发了
			List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
			reCalculateLeaveImmediately(direction, bus_queue_to, interval);
		}
	}
	
	private void initBusQueue(int direction,List<Trip> firstRoundPreTripList) {
		if(firstRoundPreTripList==null)
			return;
		int directionReverse=1-direction;
		if(scheduleParam.isLoopLine()) {
			directionReverse=direction;
		}
		Date firstBusReverseArrivalTime=scheduleParam.getArrivalTime(scheduleParam.getFirstTime(directionReverse), directionReverse);//反向头车到达时间
		int restTime=scheduleParam.getMinRestTime(firstBusReverseArrivalTime, direction);
		Date firstBusReverseMinObuTime=DateUtil.getDateAdd(firstBusReverseArrivalTime, Calendar.MINUTE, restTime);//反向头车最早发班时间
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		for(int i=0;i<firstRoundPreTripList.size();){
			Trip firstRoundPreTrip=firstRoundPreTripList.get(i);
			if(firstRoundPreTrip.getNextObuTimeMin().after(firstBusReverseMinObuTime)) {//只加反向头车到之前的车
				break;
			}
			bus_queue_to.add(firstRoundPreTrip);
			firstRoundPreTripList.remove(firstRoundPreTrip);
		}
	}
	
	private void reCalculateLeaveImmediately(int direction, List<Trip> bus_queue_to,int interval) {
		Trip lastTripFull=routeSchedule.getTripFullLast(direction);
		Date firstTime=scheduleParam.getFirstTime(direction);
		while(!bus_queue_to.isEmpty()) {
			Trip tripArrival=bus_queue_to.get(0);
			if(!tripArrival.isQuitMark()) {
				Date obuTime=null;
				Date obuTimeLast=null;
				if(lastTripFull!=null) {
					obuTimeLast=lastTripFull.getTripBeginTime();
				}
				if(obuTimeLast!=null&&obuTimeLast.before(firstTime)) {
					obuTimeLast=null;
				}
				if(obuTimeLast==null) {
					obuTime=firstTime;
					if(bus_queue_to.size()>1) {
						Trip tripArrivalNext=bus_queue_to.get(1);
						if(tripArrivalNext.getTripEndTime()!=null&&!DateUtil.getDateAddMinute(tripArrivalNext.getTripEndTime(), interval).after(firstTime)) {//下台车做头车
							obuTime=null;
						}
					}
				}
				if(obuTime==null){
					if(tripArrival.getTripEndTime()!=null)
						obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), interval);
					else
						obuTime=tripArrival.getNextObuTimeMin();
				}
				Date latestTime=scheduleParam.getLatestTime(direction);
				if(tripArrival.getTripEndTime()!=null&&tripArrival.getTripEndTime().before(latestTime)&&bus_queue_to.size()>1&&bus_queue_to.get(1).getTripEndTime()!=null) {
					if(bus_queue_to.get(1).getTripEndTime().after(DateUtil.getDateAddMinute(latestTime, -interval))) {
						obuTime=latestTime;
					}
				}
				if(obuTime.after(latestTime)) {
					break;
				}
				Trip trip=new Trip(tripArrival.getBus(), direction, obuTime, scheduleParam, lastTripFull);
				trip.setPreTrip(tripArrival);
				trip.setLastTrip(routeSchedule.getTripLast(direction));
				routeSchedule.addTrip(trip);
				addMiddleStopTrip(trip);
				lastTripFull=trip;
			}
			bus_queue_to.remove(tripArrival);
		}
	}
	
	private int getBusNumberArrival(Date runTimeEnd,List<Trip> bus_queue_to) {
		int busNumber=0;
		for(Trip tripArrival:bus_queue_to) {
			if(tripArrival.getNextObuTimeMin().before(runTimeEnd)) {
				busNumber++;
			}
		}
		return busNumber;
	}
	
	private List<Trip> getShortBusTripListNew(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to,RouteStaTurn routeStaTurn){
		if(scheduleHalfHour.getRunTime().equals("0630")) {
			System.out.println("aaa");
		}
		int direction=scheduleHalfHour.getDirection();
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		int busNumber=getBusNumberArrival(runTimeEnd, bus_queue_to);
		DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), direction);
		int minClassesNumber=dispatchRule.getMinClassesNumber(scheduleHalfHour.getRunTime());
		if(busNumber<=minClassesNumber)//不够车，不能发短线
			return new ArrayList<Trip>();
		int busNumberSurplus=busNumber-minClassesNumber;
		int shortClassesNum=busNumberSurplus/2;
		if(!scheduleParam.isLoopLine()) {
			int longClassesNum=busNumber-shortClassesNum;
			Date runTimeBegin=runTime;
			if(runTimeBegin.before(scheduleParam.getFirstTime(direction)))
				runTimeBegin=scheduleParam.getFirstTime(direction);
			while(shortClassesNum>0) {
				Date arrivalTimeBegin=scheduleParam.getArrivalTime(runTimeBegin, direction);
				Date arrivalTimeEnd=scheduleParam.getArrivalTime(runTimeEnd, direction);
				int interval=DateUtil.getMinuteInterval(arrivalTimeBegin, arrivalTimeEnd)/longClassesNum;
				if(arrivalTimeBegin.before(scheduleParam.getFirstTime(1-direction)))
					break;
				int maxInterval=Math.max(scheduleParam.getMaxInterval(arrivalTimeBegin, 1-direction), scheduleParam.getMaxInterval(arrivalTimeEnd, 1-direction));
				if(interval>maxInterval) {
					shortClassesNum--;
					longClassesNum++;
				}else {
					break;
				}
			}
		}
		Trip lastTrip=routeSchedule.getTripLast(direction);
		Date lastObuTime=null;
		int index=0;
		if(lastTrip==null||lastTrip.isShortLine()) {
			index=1;
			if(lastTrip==null)
				lastObuTime=scheduleParam.getFirstTime(direction);
			else {
				lastObuTime=bus_queue_to.get(0).getNextObuTimeMin();
			}
		}else {
			lastObuTime=lastTrip.getTripBeginTime();
		}
		Map<Trip, Integer> intervalMap=new HashMap<Trip, Integer>();
		for(;index<bus_queue_to.size();index++) {
			Trip trip=bus_queue_to.get(index);
			if(!trip.getNextObuTimeMin().before(runTimeEnd)||trip==bus_queue_to.get(bus_queue_to.size()-1))//如果后半小时车没到，最后一台不能发短线
				break;
			int interval=DateUtil.getMinuteInterval(lastObuTime, trip.getNextObuTimeMin());
			lastObuTime=trip.getNextObuTimeMin();
			Trip lastTripTemp=null;
			Trip nextTripTemp=null;
			if(index==0) {
				lastTripTemp=lastTrip;
			}else {
				lastTripTemp=bus_queue_to.get(index-1);
			}
			nextTripTemp=bus_queue_to.get(index+1);
			if(lastTripTemp.getBus().isSingleClass()&&nextTripTemp.getBus().isSingleClass()&&
					lastTripTemp.getBus().getStartDirection()==nextTripTemp.getBus().getStartDirection()) {//前后车是同方向单班车
				if(trip.getBus().getStartDirection()!=lastTripTemp.getBus().getStartDirection()||
						!trip.getBus().isSingleClass()) {//中间这台是不同方向的车或者不是单班车，不能抽调，否则会连续
					continue;
				}
			}
			intervalMap.put(trip, interval);
		}
		List<Trip> tripListLong=new ArrayList<Trip>();
		for(Trip trip:bus_queue_to) {
			tripListLong.add(trip);
		}
		List<Trip> shortTripList=new ArrayList<Trip>();
		for(int i=0;i<shortClassesNum;i++) {
			Trip tripShort=null;
			Integer interval=null;
			for(Trip trip:intervalMap.keySet()) {//找到间隔最小的
				int intervalTemp=intervalMap.get(trip);
				if(interval==null||intervalTemp<interval) {
					tripShort=trip;
					interval=intervalTemp;
				}else if(intervalTemp==interval) {
					index=bus_queue_to.indexOf(tripShort);
					int indexTemp=bus_queue_to.indexOf(trip);
					if(runTimeEnd.before(scheduleParam.getEarlyPeakBeginTime())) {
						if(indexTemp>index) {//7点前短线往后放
							tripShort=trip;
							interval=intervalTemp;
						}
					}else {
						if(indexTemp<index) {//7点后短线往前放
							tripShort=trip;
							interval=intervalTemp;
						}
					}
					
				}
			}
			if(tripShort!=null) {
				int shortIndex=tripListLong.indexOf(tripShort);
				tripListLong.remove(tripShort);
				Trip tripFullLast=routeSchedule.getTripFullLast(direction);
				if(tripFullLast==null) {
					index=1;
					lastObuTime=scheduleParam.getFirstTime(direction);
				}else {
					index=0;
					lastObuTime=tripFullLast.getTripBeginTime();
				}
				boolean exceedMaxInterval=false;
				Date latestTime=scheduleParam.getLatestTime(direction);
				for(;index<tripListLong.size();index++) {
					if(lastObuTime.after(latestTime))
						break;
					int maxInterval=scheduleParam.getMaxInterval(lastObuTime, direction);
					lastObuTime=DateUtil.getDateAddMinute(lastObuTime, maxInterval);
					Trip trip=tripListLong.get(index);
					if(trip.getNextObuTimeMin().after(lastObuTime)) {
						exceedMaxInterval=true;
						break;
					}
				}
				if(exceedMaxInterval) {//发完短线，会断位，放回去
					tripListLong.add(shortIndex, tripShort);
				}else {
					shortIndex=bus_queue_to.indexOf(tripShort);
					if(shortIndex>0) {
						intervalMap.remove(bus_queue_to.get(shortIndex-1));//删去前面班次，不能连续短线
					}
					if(shortIndex<bus_queue_to.size()-2) {
						intervalMap.remove(bus_queue_to.get(shortIndex+1));//删去后面班次，不能连续短线
					}
					shortTripList.add(tripShort);
				}
				intervalMap.remove(tripShort);
			}else {
				break;
			}
		}
		for(int i=0;i<shortTripList.size();) {
			Trip trip=shortTripList.get(i);
			Date arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), routeStaTurn);
			if(arrivalTime.after(DateUtil.getDateHM("0830"))) {
				shortTripList.remove(i);
			}else {
				i++;
			}
		}
		return shortTripList;
	}
	
	private void reCalculate(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {
		int direction=scheduleHalfHour.getDirection();
		//checkSingleBusContinuity(direction);
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		List<Trip> shortBusList=new ArrayList<Trip>();
		//if(scheduleHalfHour.getShortLineSchedule()!=null&&scheduleHalfHour.getShortLineSchedule().getShortLineClasses()!=0)
		int busNumber=getBusNumberArrival(runTimeEnd, bus_queue_to);
		if(scheduleHalfHour.getRunTime().equals("0700")) {
			System.out.println("aaa");
		}
		RouteStaTurn routeStaTurn=scheduleParam.getShortLineRouteStaTurn(runTime, direction, busNumber);
		if(routeStaTurn!=null)
			shortBusList=getShortBusTripListNew(scheduleHalfHour, bus_queue_to, routeStaTurn);
		List<Trip> busQueueTo4LongLine=new ArrayList<Trip>();//走长线的车辆
		for(Trip trip:bus_queue_to) {
			if(!shortBusList.contains(trip)) {//获取非短线车辆
				busQueueTo4LongLine.add(trip);
			}
		}
		Trip lastTripFull=routeSchedule.getTripFullLast(direction);
		Map<Bus,Trip> tripMapLong=getTripMap4LongLine(scheduleHalfHour, busQueueTo4LongLine, lastTripFull);//长线班次
		List<Trip> tripRemoveList=new ArrayList<Trip>();
		List<Trip> tripAddList=new ArrayList<Trip>();
		for(Trip tripArrival:bus_queue_to) {
			Trip trip=tripMapLong.get(tripArrival.getBus());
			if(trip!=null) {
				if(!trip.getTripBeginTime().before(runTimeEnd))
					break;
				tripAddList.add(trip);
				lastTripFull=trip;
			}else if(shortBusList.contains(tripArrival)) {
				int index=bus_queue_to.indexOf(tripArrival);
				Trip tripArrivalNext=bus_queue_to.get(index+1);
				Trip tripFullNext=tripMapLong.get(tripArrivalNext.getBus());
				if(tripFullNext==null||!tripFullNext.getTripBeginTime().before(runTimeEnd))
					break;
				int interval=DateUtil.getMinuteInterval(lastTripFull.getTripBeginTime(), tripFullNext.getTripBeginTime())/2;
				Date obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
				if(tripArrival.getTripEndTime()!=null&&obuTime.before(tripArrival.getNextObuTimeMin()))
					obuTime=tripArrival.getNextObuTimeMin();
				if(!obuTime.before(runTimeEnd)) {
					break;
				}
				Trip tripShort=getTrip4ShortLine(routeStaTurn, tripArrival.getBus(), obuTime);
				tripAddList.add(tripShort);
			}else {
				break;
			}
			tripRemoveList.add(tripArrival);
		}
		for(Trip tripArrival:tripRemoveList) {
			bus_queue_to.remove(tripArrival);
		}
		for(Trip trip:tripAddList) {
			routeSchedule.addTrip(trip);
			if(trip.getTurnTrip()!=null) {
				routeSchedule.addShortTripBack(trip.getTurnTrip());
			}
			busTripListSort(routeSchedule.getTripList(trip.getBus()));
		}
		routeSchedule.tripListSort(routeSchedule.getTripList(direction));
	}
	
	private Map<Bus,Trip> getTripMap4LongLine(ScheduleHalfHour scheduleHalfHour,List<Trip> busQueueTo4LongLine,Trip lastTripFull) {
		int direction=scheduleHalfHour.getDirection();
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		Date runTimeEnd=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTimeEnd()).getTime();
		Map<Bus,Trip> tripMapLong=new HashMap<Bus,Trip>();
		if(scheduleHalfHour.getRunTime().equals("0600")&&scheduleHalfHour.getDirection()==1)
			System.out.println("aaa");
		Integer intervalReverse=null;
		Trip lastTripFullReverse=routeSchedule.getTripFullLast(1-direction);
		if(lastTripFullReverse!=null&&lastTripFullReverse.getLastTripFull()!=null) {
			intervalReverse=DateUtil.getMinuteInterval(lastTripFullReverse.getTripBeginTime(), lastTripFullReverse.getLastTripFull().getTripBeginTime());
		}else {
			List<Trip> busQueue=routeSchedule.getBusQueue(direction);
			if(busQueue.size()>1) {
				intervalReverse=DateUtil.getMinuteInterval(busQueue.get(0).getNextObuTimeMin(), busQueue.get(1).getNextObuTimeMin());
			}
		}
		while(true) {
			List<Trip> bus_queue_to_long=new ArrayList<Trip>();
			for(Trip trip:busQueueTo4LongLine) {
				bus_queue_to_long.add(trip);
				if(bus_queue_to_long.size()==6)//用后面5台车平均
					break;
			}
			if(bus_queue_to_long.isEmpty())
				break;
			Trip tripArrival=bus_queue_to_long.get(0);
			if(intervalReverse!=null&&bus_queue_to_long.size()<6&&!scheduleParam.isLoopLine()
					&&(scheduleParam.getRouteStaTurnList(direction).isEmpty()||
					bus_queue_to_long.get(bus_queue_to_long.size()-1).getNextObuTimeMin().before(runTimeEnd))) {//没有短线或
				List<Trip> bus_queue_to_reverse=routeSchedule.getBusQueue(direction);
				Date obuTimeReverse=DateUtil.getDateAddMinute(scheduleParam.getFirstTime(1-direction), -intervalReverse);
				if(lastTripFullReverse!=null) {
					obuTimeReverse=lastTripFullReverse.getTripBeginTime();
				}
				for(Trip trip:bus_queue_to_reverse) {
					if(!trip.isQuitMark()) {
						obuTimeReverse=DateUtil.getDateAddMinute(obuTimeReverse, intervalReverse);
						Trip tripTemp=new Trip(trip.getBus(), 1-direction, obuTimeReverse);
						Date arrivalTime=scheduleParam.getArrivalTime(tripTemp.getTripBeginTime(), tripTemp.getDirection());
						int restTime=scheduleParam.getMinRestTime(arrivalTime, direction);
						tripTemp.setTripEndTime(arrivalTime);
						tripTemp.setNextObuTimeMin(DateUtil.getDateAddMinute(arrivalTime, restTime));
						bus_queue_to_long.add(tripTemp);
						if(bus_queue_to_long.size()==6)
							break;
					}
				}
			}
			if(!tripArrival.getNextObuTimeMin().before(runTimeEnd)) {//在时段截止时间后到，退出
				break;
			}
			Date obuTime=null;
			if(lastTripFull==null) {//头车
				obuTime=scheduleParam.getFirstTime(direction);
			}else {
				int maxInterval=scheduleParam.getMaxInterval(lastTripFull.getTripBeginTime(),scheduleHalfHour.getDirection());
				Integer interval=null;
				for(int i=1;i<bus_queue_to_long.size();i++) {
					Trip tripTemp=bus_queue_to_long.get(i);
					if(tripTemp.getNextObuTimeMin().after(lastTripFull.getTripBeginTime())) {
						int intervalTemp=DateUtil.getMinuteInterval(tripTemp.getNextObuTimeMin(),lastTripFull.getTripBeginTime())/(i+1);
						if(lastTripFull.getTripBeginTime().before(scheduleParam.getEarlyPeakBeginTime())) {
							intervalTemp=(int)Math.ceil(DateUtil.getMinuteInterval(tripTemp.getNextObuTimeMin(),lastTripFull.getTripBeginTime())*1.0/(i+1));
						}
						if(interval==null||intervalTemp>interval)
							interval=intervalTemp;
					}
				}
				if(interval!=null) {
					if(interval>maxInterval)
						interval=getInterval4ExceedMaxInterval(direction, lastTripFull, bus_queue_to_long);
					else {
						/*if(lastTripFull.getTripBeginTime().before(scheduleParam.getEarlyPeakBeginTime())) {//早高峰前
							int maxIntervalEarlyPeak=scheduleParam.getMaxInterval(scheduleParam.getEarlyPeakBeginTime(), direction);
							if(interval>maxIntervalEarlyPeak) {//间隔大于早高峰最大
								interval=maxInterval;
							}
						}*/
						if(lastTripFull.getLastTripFull()!=null) {
							Date obuTimeTemp=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
							if(DateUtil.isInTimeRange(obuTimeTemp, DateUtil.getDateAddMinute(scheduleParam.getEarlyPeakBeginTime(), -30), scheduleParam.getEarlyPeakBeginTime())) {//早高峰30分钟前屯车
								int intervalLast=DateUtil.getMinuteInterval(lastTripFull.getLastTripFull().getTripBeginTime(), lastTripFull.getTripBeginTime());
								if(interval<intervalLast)
									interval=intervalLast;
							}
						}
					}
				}
				if(interval!=null) {
					obuTime=DateUtil.getDateAdd(lastTripFull.getTripBeginTime(), Calendar.MINUTE, interval.intValue());
				}else {
					obuTime=tripArrival.getNextObuTimeMin();
					if(obuTime.before(lastTripFull.getTripBeginTime())||
						DateUtil.getMinuteInterval(lastTripFull.getTripBeginTime(), obuTime)<3) {
						obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), 3);
					}
				}
				if(tripArrival.getTripEndTime()!=null&&!obuTime.after(tripArrival.getTripEndTime())) {
					obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);//停站一分钟即走
				}
				/*if(tripArrival.getTripBeginTime()==null) {
					Date startTimeMin=tripArrival.getBus().getStartTimeMin();
					if(startTimeMin!=null&&obuTime.before(startTimeMin)) {
						obuTime=tripArrival.getBus().getStartTimeMin();
					}
				}*/
				/*if(tripArrival.getTripBeginTime()!=null&&obuTime.before(tripArrival.getNextObuTimeMin())) {//非首轮，发班时间在最早发班时间前
					obuTime=tripArrival.getNextObuTimeMin();
				}
				interval=DateUtil.getMinuteInterval(lastTripFull.getTripBeginTime(), obuTime);
				if(interval>maxInterval) {
					obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), maxInterval);
					if(tripArrival.getTripEndTime()!=null&&tripArrival.getNextObuTimeMin().after(obuTime)) {
						Date minObuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 2);//停站两分钟即走
						if(minObuTime.after(obuTime))
							obuTime=minObuTime;
					}
				}*/
				if(!obuTime.before(runTimeEnd))	
					break;
			}
			
			Trip trip=new Trip(tripArrival.getBus(), direction, obuTime,scheduleParam,lastTripFull);
			trip.setPreTrip(tripArrival);
			trip.setLastTrip(lastTripFull);
			tripArrival.setLaterTrip(trip);
			tripMapLong.put(trip.getBus(),trip);
			if(tripArrival.getTripEndTime()!=null) {
				int restTime=DateUtil.getMinuteInterval(trip.getTripBeginTime(), tripArrival.getTripEndTime());
				if(restTime>20)
					System.out.println("aa");
			}
			busQueueTo4LongLine.remove(tripArrival);
			lastTripFull=trip;
		}
		return tripMapLong;
	}
	
	private Trip getTrip4ShortLine(RouteStaTurn routeStaTurn,Bus bus,Date obuTime) {
		int direction=routeStaTurn.getDirection();
		Trip trip=new Trip(bus, direction, obuTime, scheduleParam, true, routeStaTurn.getLastRouteStaId(),null);
		trip.setLastRouteStaName(routeStaTurn.getLastRouteStaName());
		Trip tripBack=null;
		RouteSta lastRouteSta=null;
		if(scheduleParam.isLoopLine()) {
			tripBack=new Trip(bus, direction, DateUtil.getDateAdd(trip.getTripEndTime(), Calendar.MINUTE, routeStaTurn.getTurnAroundTime()));
			lastRouteSta=scheduleParam.getLastRouteSta(direction);
		}else {
			tripBack=new Trip(bus, 1-direction, DateUtil.getDateAdd(trip.getTripEndTime(), Calendar.MINUTE, routeStaTurn.getTurnAroundTime()));
			lastRouteSta=scheduleParam.getLastRouteSta(1-direction);
		}
		if(lastRouteSta==null)
			System.out.println("aaa");
		Date tripEndTime=scheduleParam.getArrivalTime(tripBack.getTripBeginTime(), tripBack.getDirection(), routeStaTurn.getNextFirstRouteStaId(), lastRouteSta.getRouteStationId());
		tripBack.setTripEndTime(tripEndTime);
		Date nextObuTimeMin=tripBack.getMinObuTime(scheduleParam);
		tripBack.setNextObuTimeMin(nextObuTimeMin);
		tripBack.setShortLine(true);
		trip.setNextObuTimeMin(nextObuTimeMin);
		tripBack.setFirstRouteStaId(routeStaTurn.getNextFirstRouteStaId());
		tripBack.setLastRouteStaId(scheduleParam.getLastRouteSta(tripBack.getDirection()).getRouteStationId());
		tripBack.setFirstRouteStaName(routeStaTurn.getNextFirstRouteStaName());
		trip.setTurnTrip(tripBack);//缓存回程
		return trip;
	}
	
	private HighSectionPassenger getMaxLoadFactorTime(int direction) {
		List<Trip> tripList=routeSchedule.getTripList(direction);
		routeSchedule.tripListSort(tripList);
		HighSectionPassenger highSectionPassenger=null;
		String runTimeLast=null;
		double maxLoad=0;
		int classesNumber=0;
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date peekEndTime=null;
		if(firstTime!=null&&firstTime.before(scheduleParam.getEarlyPeakEndTime())) {
			peekEndTime=scheduleParam.getEarlyPeakEndTime();
		}
		for(Trip trip:tripList) {
			if(peekEndTime!=null&&!trip.getTripBeginTime().before(peekEndTime))
				break;
			if(trip.getTripBeginTime().before(firstTime))//过滤突头
				continue;
			String runTime=getRunTime(trip.getTripBeginTime());
			if(!runTime.equals(runTimeLast)) {
				if(runTimeLast!=null) {
					int passengerNumber=scheduleParam.getHighSectionPassenger(direction, runTimeLast).getCurrentNumber();
					double maxLoadTemp=passengerNumber*1.0/classesNumber;
					if(maxLoadTemp>=maxLoad) {
						maxLoad=maxLoadTemp;
						highSectionPassenger=new HighSectionPassenger(runTimeLast, direction, passengerNumber, classesNumber);
					}
					classesNumber=0;
				}
				runTimeLast=runTime;
			}
			classesNumber++;
		}
		if(classesNumber!=0) {//最后一个时段
			int passengerNumber=scheduleParam.getHighSectionPassenger(direction, runTimeLast).getCurrentNumber();
			double maxLoadTemp=passengerNumber*1.0/classesNumber;
			if(maxLoadTemp>maxLoad) {
				maxLoad=maxLoadTemp;
				highSectionPassenger=new HighSectionPassenger(runTimeLast, direction, passengerNumber, classesNumber);
			}
		}
		return highSectionPassenger;
	}
	
	private String getRunTime(Date tripBeginTime) {
		Calendar calendar=DateUtil.getCalendar(tripBeginTime);
		if(calendar.get(Calendar.MINUTE)<30) {
			calendar.set(Calendar.MINUTE, 0);
		}else {
			calendar.set(Calendar.MINUTE, 30);
		}
		String runTime=DateFormatUtil.HM.getDateString(calendar.getTime());
		return runTime;
	}
	
	private void optimizeRestTime(List<ScheduleHalfHour> scheduleHalfHourList) {
		System.out.println("停站时间优化开始");
		//初始化车辆和任务
		routeSchedule.singleClassBusClear();
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			bus.setSingleClass(false);
			bus.setLunchEaten(false);
			//System.out.println(routeSchedule.getTripMap().get(bus).get(0).getTripBeginTime());
		}
		List<Trip> tripList=routeSchedule.getTripList(0);
		for(Trip trip:tripList) {
			trip.setQuitMark(false);
			trip.setReCalculate(false);
		}
		tripList=routeSchedule.getTripList(1);
		for(Trip trip:tripList) {
			trip.setQuitMark(false);
			trip.setReCalculate(false);
		}
		/*if(scheduleParam.getRouteId()==445) {
			optimizeRestTimeNew();
		}else {
			//第一次调整车位和间隔
			for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
				Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
				if(!runTime.before(DateUtil.getDateAdd(scheduleParam.getEarlyPeakEndTime(), Calendar.HOUR_OF_DAY, 1)))
					break;
				tripList=routeSchedule.getTripList(scheduleHalfHour.getDirection());
				optimizeRestTime(tripList,scheduleHalfHour);
			}
			resetBusStartDirectionAndOrderNumber();
			List<Trip> tripListUp=routeSchedule.getTripList(0);
			Collections.sort(tripListUp, new TripBeginTimeComparator());
			List<Trip> tripListDown=routeSchedule.getTripList(1);
			Collections.sort(tripListDown, new TripBeginTimeComparator());
			optimizeRestTime();
		}*/
		optimizeRestTimeNew();
		resetBusStartDirectionAndOrderNumber();
		busTripListSort();
		routeSchedule.tripListSort();
		System.out.println("停站时间优化结束");
	}
	
	private void busTripListSort() {
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			busTripListSort(busTripList);
		}
	}
	
	private void busTripListSort(List<Trip> busTripList) {
		Collections.sort(busTripList, new TripBeginTimeComparator());
		Trip preTrip=null;
		for(Trip trip:busTripList) {
			trip.setPreTrip(null);
			trip.setLaterTrip(null);
			trip.setFirstTask(false);
		}
		for(Trip trip:busTripList) {
			if(trip.getDirection()==Direction.NODIRECTION.getValue()) {
				preTrip=null;
				continue;
			}
			if(preTrip!=null&&preTrip.getTurnTrip()!=null)
				preTrip=preTrip.getTurnTrip();
			trip.setPreTrip(preTrip);
			if(preTrip!=null)
				preTrip.setLaterTrip(trip);
			preTrip=trip;
		}
		if(!busTripList.isEmpty()) {
			Trip firstTrip=busTripList.get(0);
			firstTrip.setFirstTask(true);
		}
	}
	
	public Trip getLastTrip(List<Trip> tripList,Date endTime) {
		for(Trip trip:tripList) {
			if(!trip.getTripBeginTime().before(endTime)) {
				return trip.getLastTrip();
			}
		}
		return null;
	}
	
	private void checkRestTime(List<Trip> tripList) {
		Collections.sort(tripList, new TripBeginTimeComparator());
		for(int i=tripList.size()-1;i>0;i--) {
			tripList.get(i).setLastTrip(tripList.get(i-1));
		}
		System.out.println(tripList.size());
		for(Trip trip:tripList) {
			Trip lastTrip=trip.getLastTrip();
			if(lastTrip!=null) {
				//System.out.println(lastTrip.getDirection()+"\t"+DateFormatUtil.HM.getDateString(lastTrip.getTripBeginTime()));
				Trip lastTripPre=lastTrip.getPreTrip();
				if(lastTripPre!=null&&lastTripPre.getTripEndTime()!=null) {
					Long interval=(trip.getTripBeginTime().getTime()-lastTrip.getTripBeginTime().getTime())/1000/60;
					Long restTime=(lastTrip.getTripBeginTime().getTime()-lastTripPre.getTripEndTime().getTime())/1000/60;
					if(restTime>2*interval) {
						System.out.println(trip.getDirection()+"\t"+DateFormatUtil.HM.getDateString(lastTrip.getTripBeginTime()));
						/*lastTrip=lastTrip.getLastTrip();
						while(lastTrip.isFirstTask()) {
							if(lastTrip.getLastTrip()==null)//到头车了
								return;
							lastTrip=lastTrip.getLastTrip();
						}
						interval=0L;
						Date beginTime=lastTrip.getTripBeginTime();
						for(int i=0;i<4;i++) {
							if(lastTrip.getNextTrip()==null) {
								System.out.println("bbbb"+lastTrip.getTripBeginTime());
								return;
							}
							lastTrip = lastTrip.getNextTrip();
							if(lastTrip.getPreTrip()!=null) {
								Long intervalTemp=(lastTrip.getPreTrip().getNextObuTimeMin().getTime()-beginTime.getTime())/(i+1)/1000/60;
								if(intervalTemp>interval)
									interval=intervalTemp;
							}
						}
						System.out.println(interval);*/
					}
				}
			}
		}
	}
	
	private void optimizeRestTimeNew() {
		busTripListSort();
		routeSchedule.tripListSort();
		List<Trip> tripListUp=routeSchedule.getTripList(Direction.UP.getValue());
		List<Trip> tripListDown=routeSchedule.getTripList(Direction.UP.getValue());
		List<Trip> tripList=new ArrayList<Trip>();
		tripList.addAll(tripListUp);
		tripList.addAll(tripListDown);
		Collections.sort(tripList, new TripBeginTimeComparator());
		for(Trip trip:tripList) {
			if(!trip.isShortLine()) {
				Trip lastTripFull=trip.getLastTripFull();
				Date obuTime=null;
				if(lastTripFull!=null) {
					List<Trip> bus_queue_to=new ArrayList<Trip>();
					Trip tripFullNext=trip;
					while((tripFullNext=tripFullNext.getNextTripFull())!=null){
						Trip preTrip=tripFullNext.getPreTrip();
						if(preTrip==null) {
							preTrip=new Trip();
							preTrip.setNextObuTimeMin(tripFullNext.getTripBeginTime());
						}else if(!preTrip.isReCalculate()) {//对面没发
							break;
						}
						bus_queue_to.add(preTrip);
					}
					Integer interval=getObuTimeInterval(bus_queue_to, lastTripFull);
					if(interval!=null) {
						obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
						Trip preTrip=trip.getPreTrip();
						if(preTrip!=null&&preTrip.getNextObuTimeMin().after(obuTime)) {
							obuTime=preTrip.getNextObuTimeMin();
						}
						trip.setTripBeginTime(obuTime);
						trip.setNextObuTimeMin(scheduleParam);
					}
					Trip lastTrip=trip.getLastTrip();
					if(lastTrip!=null&&lastTrip.isShortLine()) {
						interval=DateUtil.getMinuteInterval(trip.getTripBeginTime(), lastTripFull.getTripBeginTime())/2;
						obuTime=DateUtil.getDateAddMinute(lastTripFull.getTripBeginTime(), interval);
						trip=lastTrip;
						Trip preTrip=trip.getPreTrip();
						if(preTrip!=null&&preTrip.getNextObuTimeMin().after(obuTime)) {
							obuTime=preTrip.getNextObuTimeMin();
						}
						trip.setTripBeginTime(obuTime);
						Date arrivalTime=scheduleParam.getArrivalTime(obuTime, trip.getDirection(), trip.getFirstRouteStaId(), trip.getLastRouteStaId());
						Trip turnTrip=trip.getTurnTrip();
						int restTime=DateUtil.getMinuteInterval(trip.getTripEndTime(), turnTrip.getTripBeginTime());
						trip.setTripEndTime(arrivalTime);
						turnTrip.setTripBeginTime(DateUtil.getDateAddMinute(arrivalTime, restTime));
						arrivalTime=scheduleParam.getArrivalTime(turnTrip.getTripBeginTime(), turnTrip.getDirection(),turnTrip.getFirstRouteStaId(),turnTrip.getLastRouteStaId());
						turnTrip.setTripEndTime(arrivalTime);
						restTime=scheduleParam.getMinRestTime(arrivalTime, trip.getDirection());
						trip.setNextObuTimeMin(DateUtil.getDateAddMinute(arrivalTime, restTime));
					}
				}
			}
		}
	}
	

	private void proLateStartBus(List<Trip> tripList,Trip trip) {
		busTripListSort();
		routeSchedule.tripListSort();
		Queue<Trip> addBusTrip=new LinkedList<Trip>();
		addBusTrip.add(trip);
		trip.setFirstTask(false);
		System.out.println("晚出车辆："+trip.getBusName()+"\t"+trip.getTripBeginTime());
		if(trip.getBus().getStartDirection()==0&&trip.getBus().getStartOrderNumber()==5)
			System.out.println("aaa");
		Trip preTrip=null;
		Trip nextTrip=null;//下一班非出厂车
		boolean isBeforeFirstBus=false;
		int i=0;
		for(;i<tripList.size();i++) {
			if(tripList.get(i)==trip) {
				break;
			}
		}
		if(i>0) {
			preTrip=tripList.get(i-1);//前一班次
			//找下一班非出厂车，把晚出出厂车加入队列
			for(int j=i+1;j<tripList.size();j++) {
				Trip nextTrpTemp=tripList.get(j);
				if(!nextTrpTemp.isFirstTask()) {
					if(nextTrpTemp.getPreTrip()==null)
						System.out.println("aaa");
					if(!nextTrpTemp.getPreTrip().isShortLine()) {
						nextTrip=nextTrpTemp;
						break;
					}
				}else {
					addBusTrip.add(nextTrpTemp);
					nextTrpTemp.setFirstTask(false);
				}
			}
			//找到前一班非出厂班次
			while(preTrip.isFirstTask()||preTrip.getPreTrip().isShortLine()) {
				preTrip=preTrip.getLastTrip();
				if(preTrip==null)//
					break;
			}
			
		}
		//找对面插入区间
		if(preTrip==null) {//没找到上一个非首轮班次
			System.out.println("aa");
			List<Trip> tripListReverse=routeSchedule.getTripList(1-trip.getDirection());//取反向发班队列
			if(scheduleParam.isLoopLine()) {//环线
				tripListReverse=routeSchedule.getTripList(trip.getDirection());
			}
			preTrip=tripListReverse.get(0);
			nextTrip=tripListReverse.get(1);
			isBeforeFirstBus=true;
		}else {
			preTrip=preTrip.getPreTrip();
			if(nextTrip==null){
				nextTrip=preTrip.getNextTripFull();
			}else {
				nextTrip=nextTrip.getPreTrip();
			}
		}
		Trip lastTrip=preTrip.getLastTrip();//看能不能拿两台平均
		if(lastTrip==null) {//preTrip是头车
			if(nextTrip==null)
				System.out.println("aaa");
			System.out.println("插入开始班次：\t"+preTrip.getBusName()+"\t"+preTrip.getTripBeginTime()+"\t"+preTrip.getDirection());
			System.out.println("插入截止班次：\t"+nextTrip.getBusName()+"\t"+nextTrip.getTripBeginTime()+"\t"+nextTrip.getDirection());
			Long interval=(nextTrip.getTripBeginTime().getTime()-preTrip.getTripBeginTime().getTime())/(addBusTrip.size()+1)/60/1000;
			Calendar calendar=DateUtil.getCalendar(preTrip.getTripBeginTime());
			if(isBeforeFirstBus) {
				for(int j=0;j<addBusTrip.size()+1;j++) {
					trip=addBusTrip.poll();
					//calendar.add(Calendar.MINUTE, interval.intValue());
					Date obuTime=calendar.getTime();
					if(obuTime.after(trip.getTripBeginTime())) {//倒推时间晚于上一班开车时间，删掉第一趟，直接在对面出
						routeSchedule.removeTrip(trip);
						List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
						if(!busTripList.isEmpty()) {
							busTripList.get(0).setFirstTask(true);
						}
					}else {
						Trip tripAdd=new Trip(trip.getBus(), preTrip.getDirection(), obuTime, scheduleParam,null);
						tripAdd.setFirstTask(true);
						trip.setPreTrip(tripAdd);
						List<Trip> routeTripList=routeSchedule.getTripList(tripAdd.getDirection());
						routeTripList.add(tripAdd);
						List<Trip> busTripList=routeSchedule.getTripList(tripAdd.getBus());
						busTripList.add(tripAdd);
						Collections.sort(busTripList, new TripBeginTimeComparator());
					}
					calendar.add(Calendar.MINUTE, interval.intValue());
				}
				List<Trip> busTripList=routeSchedule.getTripList(preTrip.getBus());//头车计划
				//Collections.sort(busTripList,new TripBeginTimeComparator());
				for(Trip tripTemp:busTripList) {
					if(tripTemp==preTrip) {
						tripTemp.setTripBeginTime(calendar.getTime());
						tripTemp.setNextObuTimeMin(scheduleParam);
					}
				}
			}else {
				for(int j=0;j<addBusTrip.size()+1;j++) {
					trip=addBusTrip.poll();
					calendar.add(Calendar.MINUTE, interval.intValue());
					Trip tripAdd=new Trip(trip.getBus(), preTrip.getDirection(), calendar.getTime(), scheduleParam,null);
					tripAdd.setFirstTask(true);
					trip.setPreTrip(tripAdd);
					List<Trip> routeTripList=routeSchedule.getTripList(tripAdd.getDirection());
					routeTripList.add(tripAdd);
					List<Trip> busTripList=routeSchedule.getTripList(tripAdd.getBus());
					busTripList.add(tripAdd);
					Collections.sort(busTripList, new TripBeginTimeComparator());
				}
			}
		}else {
			if(nextTrip==null) {
				int index=tripList.indexOf(lastTrip);
				int fullTripNumber=0;
				while(fullTripNumber<2) {
					nextTrip=tripList.get(index+1);
					if(!nextTrip.isShortLine()) {
						fullTripNumber++;
					}
				}
				
			}
			System.out.println("插入开始班次：\t"+lastTrip.getBusName()+"\t"+lastTrip.getTripBeginTime()+"\t"+lastTrip.getDirection());
			System.out.println("插入截止班次：\t"+nextTrip.getBusName()+"\t"+nextTrip.getTripBeginTime()+"\t"+nextTrip.getDirection());
			Long interval=(nextTrip.getTripBeginTime().getTime()-lastTrip.getTripBeginTime().getTime())/(addBusTrip.size()+2)/60/1000;
			System.out.println("计算间隔："+interval);
			Calendar calendar=DateUtil.getCalendar(lastTrip.getTripBeginTime());
			boolean preTripPlaned=false;
			for(int j=0;j<addBusTrip.size()+1;j++) {
				calendar.add(Calendar.MINUTE, interval.intValue());//计算发班时间
				if(!preTripPlaned) {//前车未排
					if(preTrip.isFirstTask()) {//前车出厂，什么时间都可以
						preTrip.setTripBeginTime(calendar.getTime());
						preTrip.setTripEndTime(scheduleParam.getArrivalTime(preTrip.getTripBeginTime(), preTrip.getDirection()));
						preTrip.setNextObuTimeMin(scheduleParam);
						System.out.println("更改班次："+preTrip.getBusName()+"\t"+DateFormatUtil.HM.getDateString(preTrip.getTripBeginTime())+"\t"+preTrip.getDirection());
						preTripPlaned=true;
					}else {//前车非出厂
						if(preTrip.getPreTrip()==null)
							System.out.println("aaa");
						if(!preTrip.getPreTrip().getNextObuTimeMin().after(calendar.getTime())) {//早到
							preTrip.setTripBeginTime(calendar.getTime());
							preTrip.setTripEndTime(scheduleParam.getArrivalTime(preTrip.getTripBeginTime(), preTrip.getDirection()));
							preTrip.setNextObuTimeMin(scheduleParam);
							System.out.println("更改班次："+preTrip.getBusName()+"\t"+DateFormatUtil.HM.getDateString(preTrip.getTripBeginTime())+"\t"+preTrip.getDirection());
							preTripPlaned=true;
						}else {
							trip=addBusTrip.poll();
							trip=new Trip(trip.getBus(), preTrip.getDirection(), calendar.getTime(), scheduleParam,null);
							trip.setFirstTask(true);
							addBusTrip.add(trip);
						}
					}
				}else {
					trip=addBusTrip.poll();
					Trip tripAdd=new Trip(trip.getBus(), preTrip.getDirection(), calendar.getTime(), scheduleParam,null);
					tripAdd.setFirstTask(true);
					trip.setPreTrip(tripAdd);
					addBusTrip.add(tripAdd);
				}
			}
			if(!preTrip.isFirstTask()) {//非首班车，后面班次可能要换位
				List<Trip> preBusTripList=routeSchedule.getTripList(preTrip.getBus());//获取前车发班计划
				int beginIndex=0;
				for(int j=0;j<preBusTripList.size();j++) {
					if(preBusTripList.get(j).getTripBeginTime().equals(preTrip.getTripBeginTime())) {
						beginIndex=j;
						break;
					}
				}
				List<Trip> firstTripList=new ArrayList<Trip>();
				firstTripList.addAll(addBusTrip);
				firstTripList.add(preTrip);
				Collections.sort(firstTripList, new TripBeginTimeComparator());
				if(!firstTripList.get(0).equals(preTrip)) {//前车插了班次，把前车班次分成两段
					List<Trip> preBusTripList_1=new ArrayList<Trip>();
					for(int j=0;j<beginIndex+1;j++) {
						preBusTripList_1.add(preBusTripList.get(j));
					}
					List<Trip> preBusTripList_2=preBusTripList.subList(beginIndex+1,preBusTripList.size());
					List<List<Trip>> busTripList_2=new ArrayList<List<Trip>>();
					busTripList_2.add(preBusTripList_2);//先加前车的后段车次
					for(Trip trip2:addBusTrip) {
						//if(obuTimeLatest.equals(scheduleParam.getFirstPlanObuTimeLatest(trip2.getDirection()))) {//早上出车
							List<Trip> busTripList=routeSchedule.getTripList(trip2.getBus());
							busTripList_2.add(busTripList);//再加晚出车的车次
						/*}else {//单班车复行
							List<Trip> busTripList=new ArrayList<Trip>();
							for(Trip trip3:routeSchedule.getTripList(trip2.getBus())) {
								if(trip3.getTripBeginTime().after(scheduleParam.getMiddleStopRecoveryBeginTime())) {//复行后的班次
									busTripList.add(trip3);
								}
							}
							busTripList_2.add(busTripList);
						}*/
					}
					for(int j=0;j<firstTripList.size();j++) {
						Trip trip2=firstTripList.get(j);
						List<Trip> busTripList=busTripList_2.get(j);//获取对应车位的后端班次
						Bus bus=trip2.getBus();
						if(bus.equals(preTrip.getBus())) {//这台是前车
							busTripList.addAll(preBusTripList_1);//取回前端车次
						}else {
							//if(obuTimeLatest.equals(scheduleParam.getFirstPlanObuTimeLatest())) {//早上出车
								busTripList.add(trip2);//早上出车加个班次就可以
							/*}else {//单班车复行
								List<Trip> busTripListOld=routeSchedule.getTripList(bus);
								for(Trip trip3:busTripListOld) {
									if(trip3.getTripBeginTime().before(scheduleParam.getMiddleStopRecoveryBeginTime())) {//复行后的班次
										busTripList.add(trip3);//把早上班次加回去
									}
								}
								busTripList.add(trip2);
							}*/
							List<Trip> routeTripList=routeSchedule.getTripList(trip2.getDirection());
							routeTripList.add(trip2);
							System.out.println("更改班次："+trip2.getBusName()+"\t"+DateFormatUtil.HM.getDateString(trip2.getTripBeginTime())+"\t"+trip2.getDirection());
						}
						for(Trip trip3:busTripList) {
							trip3.setBus(bus);//重置车辆
						}
						Collections.sort(busTripList, new TripBeginTimeComparator());
						routeSchedule.getTripMap().put(bus, busTripList);
					}
				}else {
					for(Trip trip2:addBusTrip) {
						List<Trip> routeTripList=routeSchedule.getTripList(trip2.getDirection());
						routeTripList.add(trip2);
						List<Trip> busTripList=routeSchedule.getTripList(trip2.getBus());
						busTripList.add(trip2);
						Collections.sort(busTripList, new TripBeginTimeComparator());
						System.out.println("更改班次："+trip2.getBusName()+"\t"+DateFormatUtil.HM.getDateString(trip2.getTripBeginTime())+"\t"+trip2.getDirection());
					}
				}
			}else {
				for(Trip trip2:addBusTrip) {
					List<Trip> routeTripList=routeSchedule.getTripList(trip2.getDirection());
					routeTripList.add(trip2);
					List<Trip> busTripList=routeSchedule.getTripList(trip2.getBus());
					busTripList.add(trip2);
					Collections.sort(busTripList, new TripBeginTimeComparator());
					System.out.println("更改班次："+trip2.getBusName()+"\t"+DateFormatUtil.HM.getDateString(trip2.getTripBeginTime())+"\t"+trip2.getDirection());
				}
			}
		}
		for(Trip tripTemp:tripList) {
			if(tripTemp!=trip&&tripTemp.getTripBeginTime().equals(trip.getTripBeginTime())) {
				System.out.println("aaa");
			}
		}
		Collections.sort(tripList, new TripBeginTimeComparator());
		List<Trip> routeTripList=routeSchedule.getTripList(Direction.UP.getValue());
		Collections.sort(routeTripList, new TripBeginTimeComparator());
		for(int j=1;j<routeTripList.size();j++) {
			Trip trip2=routeTripList.get(j);
			trip2.setLastTrip(routeTripList.get(j-1));
		}
		routeTripList=routeSchedule.getTripList(Direction.DOWN.getValue());
		Collections.sort(routeTripList, new TripBeginTimeComparator());
		for(int j=1;j<routeTripList.size();j++) {
			Trip trip2=routeTripList.get(j);
			trip2.setLastTrip(routeTripList.get(j-1));
		}
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			List<Trip> busTripList=routeSchedule.getTripList(bus);
			Collections.sort(busTripList, new TripBeginTimeComparator());
			preTrip=null;
			for(int j=0;j<busTripList.size();j++) {
				Trip trip2=busTripList.get(j);
				if(preTrip!=null&&preTrip.isShortLine()) {//上一班是短线
					trip2.setPreTrip(preTrip.getTurnTrip());
				}else {
					trip2.setPreTrip(preTrip);
				}
				if(j==0) {
					trip2.setFirstTask(true);
				}else {
					trip2.setFirstTask(false);
				}
				preTrip=trip2;
			}
		}
	}
	
	private boolean addBusProc(List<Trip> tripList) {
		Collections.sort(tripList, new TripBeginTimeComparator());
		for(Trip trip:tripList) {
			if(DateFormatUtil.HM.getDateString(trip.getTripBeginTime()).equals("0750")) {
				System.out.println("aaa");
			}
			if(trip.isFirstTask()) {
				Date obuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(trip.getDirection());
				Date firstTime=scheduleParam.getFirstTime(trip.getDirection());
				if(obuTimeLatest!=null&&firstTime!=null&&obuTimeLatest.before(firstTime)) {
					throw new MyException("-1", "最晚出车时间不能早于首班车时间");
				}
				if(obuTimeLatest!=null&&!trip.getTripBeginTime().after(obuTimeLatest)){//早上晚出
					continue;
				}
				if(!scheduleParam.isLoopLine()) {
					if(trip.getDirection()==Direction.UP.getValue()) {
						firstTime=scheduleParam.getFirstTime(Direction.DOWN.getValue());
					}else {
						firstTime=scheduleParam.getFirstTime(Direction.UP.getValue());
					}
					Date arrivalTime=scheduleParam.getArrivalTime(firstTime, 1-trip.getDirection());
					int restTime=scheduleParam.getMinRestTime(arrivalTime, trip.getDirection());
					Date minObuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
					if(minObuTime.after(trip.getTripBeginTime())) {//对面头车到前就要发
						if(obuTimeLatest!=null)
							trip.setTripBeginTime(obuTimeLatest);//按最晚出车时间发
						else {//这边不出车
							if(scheduleParam.getFirstTime(1-trip.getDirection()).
								after(scheduleParam.getFirstTime(trip.getDirection()))) {
								throw new MyException("-10", (trip.getDirection()==0?"上行":"下行")+"头车时间早，请设置总站可出车");
							}
							Trip tripAdd=new Trip(trip.getBus(), 1-trip.getDirection(), scheduleParam.getFirstTime(1-trip.getDirection()));
							arrivalTime=scheduleParam.getArrivalTime(tripAdd.getTripBeginTime(), tripAdd.getDirection());
							tripAdd.setTripEndTime(arrivalTime);
							tripAdd.setNextObuTimeMin(scheduleParam.getFirstTime(trip.getDirection()));
							routeSchedule.getTripList(trip.getBus()).add(0,tripAdd);
							busTripListSort(routeSchedule.getTripList(trip.getBus()));
							routeSchedule.getTripList(1-trip.getDirection()).add(0,tripAdd);
						}
						return true;
					}
				}
				proLateStartBus(tripList, trip);
				return true;
			}
		}
		return false;
	}
	
	private Integer getClassesFullInterval(String runTime,Trip tripLast,List<Trip> bus_queue_to) {
		int direction=tripLast.getDirection();
		double num=0;
		List<Integer> intervalList=new ArrayList<Integer>();
		Integer interval=null;
		Trip latestBusTrip=null;
		Date latestTime=scheduleParam.getLatestTime(direction);
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.getNextObuTimeMin().after(latestTime)&&i>0) {
				latestBusTrip=bus_queue_to.get(i-1);
				latestBusTrip.setNextObuTimeMin(latestTime);//设置末班车
				break;
			}
		}
		for(int i=0;i<bus_queue_to.size();i++) {
			if(i==6) {
				break;
			}
			Trip trip=bus_queue_to.get(i);
			if(trip.getNextObuTimeMin().after(latestTime)) {
				break;
			}
			Trip tripNext=new Trip(trip.getBus(), direction, trip.getNextObuTimeMin());
			tripNext.setNextObuTimeMin(scheduleParam);
			if(scheduleParam.isEndAfterTrip(tripNext)) {//单班车收车
				//num=num+0.75;
				num++;
			}else {
				Trip lastTrip=trip.getLastTrip();
				if(lastTrip!=null) {
					if(lastTrip.getNextObuTimeMin().after(latestTime)) {
						break;
					}
					tripNext=new Trip(lastTrip.getBus(), direction, lastTrip.getNextObuTimeMin());
					tripNext.setNextObuTimeMin(scheduleParam);
					if(scheduleParam.isEndAfterTrip(tripNext)) {//上一班单班收车
						//num=num+0.75;
						num++;
					}else {
						num++;
					}
				}else {
					num++;
				}
			}
			if(trip.getTripBeginTime()!=null) {
				int intervalTemp=0;
				if(trip.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
					intervalTemp=(int)(DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), tripLast.getTripBeginTime())/num);
				}
				intervalList.add(intervalTemp);
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
		}
		if(interval==null) {//算上复行车
			for(int i=0;i<bus_queue_to.size();i++) {
				Trip trip=bus_queue_to.get(i);
				if(trip.getNextObuTimeMin().after(latestTime)) {
					break;
				}
				int intervalTemp=0;
				if(trip.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
					intervalTemp=(int)(DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), tripLast.getTripBeginTime())/(i+1));
				}
				intervalList.add(intervalTemp);
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
		}
		/*if(!scheduleParam.isLoopLine()&&interval!=null) {
			if(bus_queue_to.size()<6&&latestBusTrip==null) {//全部车加起来也没有6台.尾车未确定，看前一台是不是尾车
				Date obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
				Date obuTimeNextRound=scheduleParam.getObuTimeNextRound(direction, obuTime);
				if(obuTimeNextRound==null||obuTimeNextRound.after(latestTime)) {
					int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/bus_queue_to.size();
					if(intervalTemp>interval) {
						interval=intervalTemp;
					}
				}
			}
			
		}*/
		
		return interval;
	}
	
	private void  checkBusDistance() {
		List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		Trip tripLast=routeSchedule.getTripFullLast(Direction.UP.getValue());
		if(tripLast==null)
			return;
		Date lastObuTime=tripLast.getTripBeginTime();
		int intervalSum=0;
		int busNumber=0;
		Map<Trip, Integer> intervalTripMap=new HashMap<Trip, Integer>();
		Trip intervalMaxTrip=null;
		Integer intervalMax=null;
		Trip shortAdjustTrip=null; 
		for(Trip trip:busQueue) {
			if(trip.isQuitMark())
				continue;
			if(trip.getShortLineAdjust()!=null||trip.isShortLine())
				return;
			int interval=DateUtil.getMinuteInterval(lastObuTime, trip.getNextObuTimeMin());
			intervalTripMap.put(trip, interval);//车距差
			if(intervalMax==null||interval>intervalMax) {
				intervalMax=interval;
				intervalMaxTrip=trip;
			}
			intervalSum+=interval;
			busNumber++;
			lastObuTime=trip.getNextObuTimeMin();
		}
		if(busNumber==0) {
			return;
		}
		int intervalAvg=intervalSum/busNumber;
		if(intervalMax>2*intervalAvg) {//最大车距大于平均车距2倍
			if(!intervalMaxTrip.getNextObuTimeMin().before(scheduleParam.getLatestTime(Direction.UP.getValue()))) {
				return;
			}
			Date endTime=DateUtil.getDateAddMinute(intervalMaxTrip.getNextObuTimeMin(), -intervalMax/4);
			Date beginTime=DateUtil.getDateAddMinute(endTime, -intervalMax/2);//在最大车距的中点正负1/4范围内
			List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(Direction.UP.getValue());
			for(Trip trip:busQueue) {
				if(trip.isQuitMark())
					continue;
				if(trip==intervalMaxTrip)
					break;
				for(RouteStaTurn routeStaTurn:routeStaTurnList) {
					Date arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), routeStaTurn);
					int restTime=scheduleParam.getMinRestTime(arrivalTime,1-trip.getDirection());
					Date nextObuTimeMin=DateUtil.getDateAddMinute(arrivalTime, restTime);
					if(DateUtil.isInTimeRange(nextObuTimeMin, beginTime, endTime)) {
						if(shortAdjustTrip==null||intervalTripMap.get(trip)>intervalTripMap.get(shortAdjustTrip)){//优先车距差大的
							if(shortAdjustTrip!=null)
								shortAdjustTrip.setShortLineAdjust(null);
							shortAdjustTrip=trip;
							shortAdjustTrip.setShortLineAdjust(routeStaTurn);
						}
						
					}
				}
			}
		}
		System.out.println(intervalTripMap.get(shortAdjustTrip));
		System.out.println("aaa");
	}
	
	private void setShortBus4LoopLine(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {
		int direction=scheduleHalfHour.getDirection();
		Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
		Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
		int busNumber=getBusNumberArrival(runTimeEnd, bus_queue_to);
		RouteStaTurn routeStaTurn=scheduleParam.getShortLineRouteStaTurn(runTime, direction, busNumber);
		if(routeStaTurn!=null) {
			List<Trip> shortBusList=getShortBusTripListNew(scheduleHalfHour, bus_queue_to, routeStaTurn);
			for(Trip trip:shortBusList) {
				trip.setShortLineAdjust(routeStaTurn);
			}
		}
	}
	
	private void calculate4LoopLine() {
		List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			calculateNormalTime4LoopLine(scheduleHalfHour);
		}
	}
	
	private void calculateNormalTime4LoopLine(ScheduleHalfHour scheduleHalfHour) {
		if(scheduleHalfHour.getRunTime().equals("1700"))
			System.out.println("aaa");
		int direction=scheduleHalfHour.getDirection();
		Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
		Trip tripLast=routeSchedule.getTripFullLast(direction);
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		Date beginTimeNext=null;
		if(scheduleHalfHourNext!=null) {
			beginTimeNext=DateUtil.getDateHM(scheduleHalfHourNext.getRunTime());
		}else {
			beginTimeNext=scheduleParam.getLatestTime(direction);
		}
		if(runTime.after(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(), Calendar.MINUTE, -60))) {//准备单班复行
			singleClassRecovery(tripLast, bus_queue_to);
		}
		electricitySupplement.setBusElecSupply(direction, bus_queue_to);
		setShortBus4LoopLine(scheduleHalfHour, bus_queue_to);
		checkBusDistance();
		if(bus_queue_to.isEmpty())
			return;
		Trip tripArrival=bus_queue_to.get(0);
		while(!tripArrival.getNextObuTimeMin().after(beginTimeNext)) {
			bus_queue_to=routeSchedule.getBusQueue(1-direction);
			if(tripLast!=null) {//检查够不够车，单班车收车，晚班接不上
				int maxInterval=scheduleParam.getMaxInterval(runTime, direction);
				int busNumberMin=(int)Math.ceil(DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripLast.getNextObuTimeMin())*1.0/maxInterval);
				int busNumber=0;
				for(Trip trip:bus_queue_to) {
					if(!trip.isQuitMark()) {
						busNumber++;
					}
				}
				if(busNumber<busNumberMin) {
					for(Trip trip:bus_queue_to) {
						if(trip.getBus().isSingleClass()&&(
								!trip.getBus().isHasMiddleStop()&&trip.getNextObuTimeMin().before(scheduleParam.getMiddleStopEndTime())||
								(trip.getBus().isHasMiddleStop()&&trip.getNextObuTimeMin().before(scheduleParam.getMiddleStopOffDutyBeginTime())))) {
							trip.setQuitMark(false);
							busNumber++;
							if(busNumber==busNumberMin)
								break;
						}
					}
				}
			}
			tripLast=routeSchedule.getTripFullLast(direction);
			setQuitMark4MiddleStop(scheduleHalfHour, bus_queue_to, null);
			setQuitMarkOffDutyEarly4LoopLine();
			if(tripArrival.isQuitMark()) {
				Bus bus=tripArrival.getBus();
				ScheduleParamShift shift=bus.getShiftType();
				if(shift!=null) {
					Trip tripRecovery=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(),DateUtil.getDateHM(shift.getEndTime()));
					if(tripRecovery!=null) {
						if(!tripRecovery.getNextObuTimeMin().after(tripArrival.getNextObuTimeMin())) {//过了复行时间，不退出
							tripArrival.setQuitMark(false);
						}else {
							routeSchedule.addShortTripBack(tripRecovery);
						}
					}
				}else if(bus.isSingleClass()){
					if(!bus.isHasMiddleStop()) {
						routeSchedule.singleClassBusAdd(tripArrival, direction);
						bus.setHasMiddleStop(true);
					}
				}
			}
			if(!tripArrival.isQuitMark()) {
				Date obuTime=null;
				Trip tripArrivalNext=null;
				for(int i=1;i<bus_queue_to.size();i++) {
					Trip trip=bus_queue_to.get(i);
					if(!trip.isQuitMark()) {
						tripArrivalNext=trip;
						break;
					}
				}
				if(tripArrivalNext==null)
					System.out.println("aaa");
				Date latestTime=scheduleParam.getLatestTime(direction);
				if(bus_queue_to.size()==2) {//两台车
					if(tripLast!=null) {
						if(tripLast.getLastTripFull()!=null) {
							if(DateUtil.getDateHM("2109").equals(tripArrival.getTripEndTime()))
								System.out.println("aaa");
							Integer interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripLast.getLastTripFull().getTripBeginTime());
							obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
							if(obuTime.after(latestTime))
								System.out.println("aaa");
							Date arrivalTime=scheduleParam.getArrivalTime(obuTime, Direction.UP.getValue());
							int restTime=scheduleParam.getMinRestTime(arrivalTime, Direction.UP.getValue());
							arrivalTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
							Date obuTimeNext=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), 2*interval);
							Date arrivalTimeNext=scheduleParam.getArrivalTime(obuTimeNext, Direction.UP.getValue());
							obuTimeNext=DateUtil.getDateAddMinute(arrivalTime, restTime);
							interval=null;
							if(arrivalTime.before(latestTime)&&arrivalTimeNext.after(latestTime)) {//这台车回来可以做尾车
								interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/3;
							}else if(arrivalTime.after(latestTime)&&bus_queue_to.get(1).getTripEndTime().before(latestTime)) {//下台车回来做尾车
								interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/2;
								int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
								if(interval>maxInterval) {
									int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/3;
									Date obuTimeTemp=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalTemp);
									while(obuTimeTemp.after(tripArrival.getTripEndTime())) {
										arrivalTime=scheduleParam.getArrivalTime(obuTimeTemp, direction);
										if(arrivalTime.before(latestTime)) {
											interval=intervalTemp;
											break;
										}
										intervalTemp--;
										obuTimeTemp=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalTemp);
									}
								}
							}
							if(interval!=null)
								obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
							else if(bus_queue_to.get(1).getTripEndTime().after(latestTime)) {
								obuTime=latestTime;
							}else {
								obuTime=null;
							}
						}
					}
				}
				if(obuTime==null&&!bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(latestTime)) {
					int busNumber=0;
					Trip latestBusTrip=null;
					for(Trip tripArrivalTemp:bus_queue_to) {
						if(!tripArrivalTemp.isQuitMark()&&!tripArrivalTemp.getNextObuTimeMin().after(latestTime)) {
							busNumber++;
							latestBusTrip=tripArrivalTemp;
						}
					}
					int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busNumber;
					int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
					if(interval>maxInterval) {//断位，把末班车时间前到的车加上
						int index=bus_queue_to.indexOf(latestBusTrip);
						for(int i=index+1;i<bus_queue_to.size();i++) {
							Trip tripArrivalTemp=bus_queue_to.get(i);
							if(!tripArrivalTemp.isQuitMark()) {
								if(tripArrivalTemp.getTripEndTime()==null)
									System.out.println("aaa");
								if(tripArrivalTemp.getTripEndTime()==null||tripArrivalTemp.getTripEndTime().before(latestTime)) {
									latestBusTrip.setNextObuTimeMin(scheduleParam);
									tripArrivalTemp.setNextObuTimeMin(latestTime);
									busNumber++;
									interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busNumber;
								}
								break;
							}
						}
					}
					obuTime=DateUtil.getDateAdd(tripLast.getTripBeginTime(), Calendar.MINUTE, interval);
					if(tripArrival.getTripEndTime()!=null&&obuTime.before(tripArrival.getTripEndTime())) {
						obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);//比发班时间晚，即走
					}
				}
				if(obuTime==null) {

					if(tripLast==null) {
						obuTime=scheduleParam.getFirstTime(Direction.UP.getValue());
					}else {
						List<Trip> bus_queue_to_temp=new ArrayList<Trip>();
						for(Trip trip:bus_queue_to) {
							if(!trip.isQuitMark()&&trip.getShortLineAdjust()==null) {
								bus_queue_to_temp.add(trip);
							}
						}
						if(bus_queue_to_temp.size()==1) {
							
							Date arrivalTimeNextRound=scheduleParam.getArrivalTimeNextRound(direction, tripArrival.getNextObuTimeMin());
							if(!arrivalTimeNextRound.before(latestTime)) {//回来过时
								obuTime=latestTime;
							}else {
								int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
								if(tripArrival.getTripEndTime()==null) {
									obuTime=tripArrival.getNextObuTimeMin();
								}else {
									int restTime=scheduleParam.getRestTime(tripArrival.getTripEndTime(), direction);
									obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), restTime);
								}
								Date maxObuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), maxInterval);
								if(obuTime.after(maxObuTime)) {
									if(tripArrival.getTripEndTime()!=null) {
										if(tripArrival.getTripEndTime().before(maxObuTime)) {
											obuTime=maxObuTime;
										}else {
											obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
										}
									}else {
										obuTime=maxObuTime;
									}
								}
							}
						}else {
							Integer interval=getClassesFullInterval(scheduleHalfHour.getRunTime(), tripLast, bus_queue_to_temp);
							if(tripArrival.getShortLineAdjust()!=null) {//短线调位
								interval=interval/2;
							}
							if(interval==null) {
								System.out.println("aaa");
							}
							obuTime=DateUtil.getDateAdd(tripLast.getTripBeginTime(), Calendar.MINUTE, interval);
						}
						
					}
					if(tripArrival.getBus().getStartOrderNumber()==2)
						System.out.println("aa");
					if(tripArrival.getTripBeginTime()!=null) {
						if(tripArrival.getShortLineAdjust()==null) {
							int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
							Date maxObuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), maxInterval);
							if(obuTime.after(maxObuTime)) {
								if(tripArrival.getTripEndTime().before(maxObuTime))
									obuTime=maxObuTime;
								else {
									obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
								}
							}else {
								if(obuTime.before(tripArrival.getNextObuTimeMin())) {
									if(tripArrival.getTripEndTime().after(maxObuTime)) {
										obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
									}else if(tripArrival.getNextObuTimeMin().after(maxObuTime)){
										obuTime=maxObuTime;
									}else {
										obuTime=tripArrival.getNextObuTimeMin();
									}
								}
							}
						}else {//短线调位
							if(obuTime.before(tripArrival.getNextObuTimeMin()))
								obuTime=tripArrival.getNextObuTimeMin();
						}
					}
				}
				if(scheduleHalfHourNext!=null&&!obuTime.before(beginTimeNext)) {
					break;
				}
				Bus bus=tripArrival.getBus();
				if(tripArrival.getTripBeginTime()==null) {
					List<Trip> busTripList=routeSchedule.getTripList(bus);
					if(busTripList!=null) {
						Trip preTrip=busTripList.get(busTripList.size()-1);
						if(preTrip.getTurnTrip()!=null)//上一班短线
							preTrip=preTrip.getTurnTrip();
						Trip middleStopTrip=new Trip(bus, Direction.NODIRECTION.getValue(), DateUtil.getDateAdd(preTrip.getTripEndTime(), Calendar.MINUTE, 5));//中停前一班次结束加5分钟作为中停开始时间;
						middleStopTrip.setFirstRouteStaId(scheduleParam.getFirstRouteStaId(direction));
						middleStopTrip.setLastRouteStaId(scheduleParam.getFirstRouteStaId(direction));
						preTrip=busTripList.get(busTripList.size()-1);//获取中停后的班次
						middleStopTrip.setTripEndTime(DateUtil.getDateAdd(obuTime, Calendar.MINUTE, -5));//中停复行开始前5分钟作为中停结束时间;	
						busTripList.add(middleStopTrip);
					}
				}
				routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
				Trip trip=null;
				if(tripArrival.getShortLineAdjust()!=null) {
					RouteStaTurn routeStaTurn=tripArrival.getShortLineAdjust();
					trip=new Trip(bus, obuTime, scheduleParam, routeStaTurn);
				}else {
					trip=new Trip(bus, direction, obuTime,scheduleParam,tripLast);
				}
				tripLast=routeSchedule.getTripLast(direction);
				trip.setLastTrip(tripLast);
				if(tripLast!=null)
					tripLast.setNextTrip(trip);
				routeSchedule.addTrip(trip);
				if(tripArrival.getTripBeginTime()!=null)
					trip.setPreTrip(tripArrival);
				if(trip.getTurnTrip()!=null) {
					if(trip.getTripEndTime()==null)
						System.out.println("aaa");
					routeSchedule.addShortTripBack(trip.getTurnTrip());
				}
				System.out.println(trip.getBusName()+"\t"+HM_FORMAT.format(trip.getTripBeginTime())+"\t"+(tripArrival.getTripEndTime()==null?"":HM_FORMAT.format(tripArrival.getTripEndTime()))+"\t"+(tripArrival.getNextObuTimeMin()==null?"":HM_FORMAT.format(tripArrival.getNextObuTimeMin())));
			}
			bus_queue_to.remove(tripArrival);
			if(bus_queue_to.isEmpty()) {
				return;
			}
			tripArrival=bus_queue_to.get(0);
		}
		if(scheduleHalfHourNext==null) {//最后一个时段
			tripLast=routeSchedule.getTripFullLast(direction);
			if(tripLast.getTripBeginTime().before(beginTimeNext)) {//最后一班车不是末班车
				if(tripArrival!=null&&tripArrival.getTripEndTime().before(beginTimeNext)) {//到站车在末班时间前到，加一班
					Trip trip=new Trip(tripArrival.getBus(), direction, beginTimeNext,scheduleParam,tripLast);
					trip.setLastTrip(tripLast);
					if(tripLast!=null)
						tripLast.setNextTrip(trip);
					routeSchedule.addTrip(trip);
				}else {
					tripLast.setTripBeginTime(beginTimeNext);
					tripLast.setNextObuTimeMin(scheduleParam);
				}
			}
		}
	}
	
	private void setQuitMarkOffDutyEarly4LoopLine() {
		Trip tripFullLast=routeSchedule.getTripFullLast(Direction.UP.getValue());
		if(tripFullLast==null)
			return;
		if(!tripFullLast.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime())) {//上一台发的车回来可以收车
			Date latestTime=scheduleParam.getLatestTime(Direction.UP.getValue());
			int maxInterval=scheduleParam.getMaxInterval(tripFullLast.getTripBeginTime(), Direction.UP.getValue());
			maxInterval*=0.8;
			int busNumber=(int)Math.ceil(DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)*1.0/maxInterval);
			List<Date> obuTimeList=new ArrayList<Date>();
			Date obuTimeLast=tripFullLast.getTripBeginTime();
			for(int i=busNumber;i>0;i--) {
				int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTime)/i;
				Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
				obuTimeList.add(obuTime);
				obuTimeLast=obuTime;
			}
			List<Trip> busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
			List<Trip> busQueue4Full=new ArrayList<Trip>();
			for(Trip tripArrival:busQueue) {
				if(!tripArrival.getBus().isSingleClass()&&tripArrival.getShortLineAdjust()==null)
					busQueue4Full.add(tripArrival);
			}
			int beginIndex=-1;
			for(int i=0;i<busQueue4Full.size();i++) {
				Trip tripArrival=busQueue4Full.get(i);
				if(tripArrival.isQuitMark()) {
					beginIndex=i;
				}
			}
			if(beginIndex!=-1) {
				beginIndex=beginIndex+2;
				if(!obuTimeList.isEmpty()) {
					obuTimeList.remove(0);
				}
			}else {
				beginIndex=0;
			}
			for(int i=beginIndex;i<busQueue4Full.size()-1;i++) {
				Trip trip=busQueue4Full.get(i);
				Trip tripNext=busQueue4Full.get(i+1);
				if(obuTimeList.isEmpty())
					break;
				Date obuTime=obuTimeList.get(0);
				if(!tripNext.getNextObuTimeMin().after(obuTime)&&!trip.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime())) {//后一台车在发班时间前到
					trip.setQuitMark(true);
					i++;//隔一台退出
				}
				obuTimeList.remove(0);
			}
		}
	}
	
	private List<Trip> getBusAdjustList(int direction){
		List<Trip> shortLineAdjust=new ArrayList<Trip>();
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		Date middleStopBeginTimeEnd=scheduleParam.getMiddleStopEndTime();
		Date singleBusOffDutyEnd=DateUtil.getDateAddMinute(scheduleParam.getMiddleStopOffDutyBeginTime(), 60);
		if(!scheduleParam.getRouteStaTurnList(direction).isEmpty()) {//可以短线调位
			Trip tripLast=routeSchedule.getTripFullLast(direction);
			Date obuTime=tripLast.getTripBeginTime();
			for(int i=0;i<bus_queue_to.size()-1;i++) {
				Trip trip=bus_queue_to.get(i);
				Trip tripNext=bus_queue_to.get(i+1);
				if(trip.isQuitMark()) {
					continue;
				}
				if(DateUtil.getDateHM("1057").equals(trip.getTripEndTime()))
					System.out.println("aa");
				if(obuTime.after(scheduleParam.getLatestTime(direction)))
					break;
				int maxInterval=scheduleParam.getMaxInterval(obuTime, direction);
				Date obuTimeTemp=DateUtil.getDateAddMinute(obuTime, maxInterval);
				if(trip.getTripBeginTime()!=null) {
					if(scheduleParam.isEndDirectionTrip(trip)) {//这边停场
						//if(!bus_queue_to.get(i+1).getNextObuTimeMin().after(obuTimeTemp)) {
							Date arrivalTime=scheduleParam.getObuTimeNextRound(direction, trip.getNextObuTimeMin());
							if(scheduleParam.getEndDirection()==null) {
								arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), direction);
							}
							if(arrivalTime!=null) {
								if(trip.getBus().isSingleClass()) {
									if(((trip.getNextObuTimeMin().before(middleStopBeginTimeEnd)&&arrivalTime.after(middleStopBeginTimeEnd))||
											arrivalTime.after(singleBusOffDutyEnd))) {//过了下班时间
										if(i==0) {
											Trip tripLastTemp=routeSchedule.getTripLast(direction);
											if(tripLastTemp.isShortLine()) {
												continue;
											}
										}else {
											Trip tripLastTemp=bus_queue_to.get(i-1);
											if(shortLineAdjust.contains(tripLastTemp)) {//上一班调位了
												continue;
											}
										}
										shortLineAdjust.add(trip);
									}
								}else {
									Bus bus=trip.getBus();
									ScheduleParamShift shift=bus.getShiftType();
									if(shift!=null) {//非双班或单班车
										Date endTime=DateUtil.getDateHM(shift.getEndTime());
										if(arrivalTime.after(DateUtil.getDateAddMinute(endTime, 30))) {//回来过了下班时间
											if((tripNext.getTripEndTime()==null&&!tripNext.getNextObuTimeMin().after(obuTimeTemp))||
												(tripNext.getTripEndTime()!=null&&tripNext.getTripEndTime().before(obuTimeTemp))) {//后面短线先不发短线
												shortLineAdjust.add(trip);
											}
										}
									}
								}
							}
						//}
					}else {//对面停场
						Bus bus=trip.getBus();
						if(bus.isSingleClass()&&scheduleParam.getRouteStaTurnList(1-direction).isEmpty()) {//对面没得调位
							Date offDutyBeginTime=scheduleParam.getMiddleStopBeginTime();
							Date offDutyEndTime=scheduleParam.getMiddleStopEndTime();
							if(bus.isHasMiddleStop()) {
								offDutyBeginTime=scheduleParam.getMiddleStopOffDutyBeginTime();
								offDutyEndTime=DateUtil.getDateAddMinute(offDutyBeginTime, 60);
							}
							Date arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), direction);
							if(arrivalTime.before(offDutyBeginTime)) {
								arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, trip.getNextObuTimeMin());//走一个全程回来的时间
								if(arrivalTime!=null) {
									arrivalTime=scheduleParam.getArrivalTime(arrivalTime, direction);//到对面的时间
									if(arrivalTime!=null&&arrivalTime.after(offDutyEndTime)) {//过了下班时间，提前调位
										List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
										for(RouteStaTurn routeStaTurn:routeStaTurnList) {
											arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), routeStaTurn);
											arrivalTime=scheduleParam.getArrivalTime(arrivalTime, direction);//到对面的时间
											if(DateUtil.isInTimeRange(arrivalTime, offDutyBeginTime, offDutyEndTime)) {
												shortLineAdjust.add(trip);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
				if(!shortLineAdjust.contains(trip)) {
					obuTime=obuTimeTemp;
				}	
			}
		}
		return shortLineAdjust;
	}
	
	private List<Trip> getDoubleClassesBusInterval() {
		List<Trip> bus_queue_up=routeSchedule.getBusQueue(Direction.UP.getValue());
		List<Trip> bus_queue_down=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		List<Trip> bus_queue=new ArrayList<Trip>();
		bus_queue.addAll(bus_queue_up);
		bus_queue.addAll(bus_queue_down);
		Map<Trip,Integer> busQueueMap=new HashMap<Trip,Integer>();
		Trip lastTrip=null;
		List<Trip> busQueue=new ArrayList<Trip>();
		for(Trip trip:bus_queue) {
			if(!trip.getBus().isSingleClass()) {
				if(lastTrip!=null&&lastTrip.getDirection()==trip.getDirection()) {
					int interval=DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), lastTrip.getNextObuTimeMin());
					Integer index=null;
					for(int i=0;i<busQueue.size();i++) {
						Trip tripTemp=busQueue.get(i);
						if(interval<busQueueMap.get(tripTemp)) {
							index=i;
						}else {
							break;
						}
					}
					if(index==null) {
						index=0;
					}
					busQueue.add(index, trip);
					busQueueMap.put(trip, interval);
				}
				lastTrip=trip;
			}
		}
		return busQueue;
	}
	
	private void calculateNormalTime(ScheduleHalfHour scheduleHalfHour) {
		int direction=scheduleHalfHour.getDirection();
		//new ScheduleNormalTime(routeSchedule, scheduleParam).calculateNormalTimeB4MiddleStop(direction);
		Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
		Trip tripLast=routeSchedule.getTripFullLast(direction);
		routeSchedule.setCurrentScheduleHalfHour(scheduleHalfHour);
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		if(bus_queue_to.isEmpty())
			return;
		if(scheduleHalfHour.getRunTime().startsWith("1700")) {
			System.out.println("aaa");
			//busReverseAdjust.getLatestBus(routeSchedule);
		}
		if(tripLast!=null&&!elecSupply) {
			setQuitMark(scheduleHalfHour, bus_queue_to);
		}
		if(runTime.after(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(direction), Calendar.MINUTE, -60))) {//准备单班复行
			singleClassRecovery(tripLast, bus_queue_to);
		}
		Integer leaveImmediately=scheduleParam.getLeaveImmediatelyInterval(direction);
		if(leaveImmediately!=null) {
			reCalculateLeaveImmediately(scheduleHalfHour.getDirection(), bus_queue_to, leaveImmediately);
			return;
		}
		/*if(DateUtil.isInTimeRange(runTime, scheduleParam.getLatePeakBeginTime(), scheduleParam.getLatePeakEndTime())) {
			reCalculate(scheduleHalfHour, bus_queue_to);
			return;
		}*/
		/*if(!isSingleBusMoreThanDouble()) {
			busReverseAdjust(scheduleHalfHour);
		}*/
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		DispatchRule dispatchRule=scheduleParam.getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
		Date beginTimeNext=null;
		if(scheduleHalfHourNext!=null&&(scheduleHalfHourNext.getRunTimeEnd().endsWith("00")||scheduleHalfHourNext.getRunTimeEnd().endsWith("30"))) {//过滤下时段末班车
			beginTimeNext=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTime()).getTime();
			if(routeSchedule.getLatestBusLastRound(direction)==null&&scheduleParam.getRouteStaTurnList().isEmpty()) {
				Date endTimeNext=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTimeEnd()).getTime();
				if(bus_queue_to.size()==0)
					System.out.println("aaa");
				if(!bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(endTimeNext)) {//看要给下个时段留多少台车
					List<Trip> bus_queue_to_temp=new ArrayList<Trip>();//过滤退出车辆
					for(Trip trip:bus_queue_to) {
						if(!trip.isQuitMark()) {
							bus_queue_to_temp.add(trip);
						}
					}
					busQueueNextHalfSupply(bus_queue_to_temp, scheduleHalfHourNext);
					DispatchRule dispatchRuleNext=scheduleParam.getDispatchRule(scheduleHalfHourNext.getRunTime(), scheduleHalfHourNext.getDirection());
					int minClassesNumber=dispatchRule.getMinClassesNumber(scheduleHalfHour.getRunTime());
					if(minClassesNumber!=0) {
						int minClassesNumberNext=dispatchRuleNext.getMinClassesNumber(scheduleHalfHourNext.getRunTime());
						for(int i=bus_queue_to_temp.size()-1;i>=minClassesNumber;i--) {
							Trip trip=bus_queue_to_temp.get(i);
							if(trip.getNextObuTimeMin().before(endTimeNext)) {//在下个时段截止前到
								if(trip.getNextObuTimeMin().before(beginTimeNext)) {//在下个时段开始前到，由于下一个时段班次不够，加到下个班次
									trip.setNextObuTimeMin(beginTimeNext);
								}
								minClassesNumberNext--;
								if(minClassesNumberNext==0)
									break;
							}
						}
					}
				}
			}
		}else {
			beginTimeNext=scheduleParam.getLatestTime(direction);
		}
		List<Trip> tripList=new ArrayList<Trip>();
		if(bus_queue_to.isEmpty())
			return;
		List<Trip> bus_queue_to_temp=null;
		if(tripLast==null)
			System.out.println("aaa");
		List<Trip> shortLineAdjust=new ArrayList<Trip>();
		if(!scheduleParam.getRouteStaTurnList().isEmpty()&&scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue())!=null&&
				scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue())!=null) {//单班车短线调位
			Date obuTime=tripLast.getTripBeginTime();
			for(int i=0;i<bus_queue_to.size();i++) {
				Trip trip=bus_queue_to.get(i);
				Bus bus=trip.getBus();
				if(!obuTime.before(scheduleParam.getLatestTime(direction)))
					break;
				int maxInterval=scheduleParam.getMaxInterval(obuTime, direction);
				Date obuTimeTemp=DateUtil.getDateAddMinute(obuTime, maxInterval);
				if(trip.getBus().isSingleClass()) {
					if(scheduleParam.isEndAfterTrip(trip)) {
						boolean quit=true;
						if(i<bus_queue_to.size()-1) {
							Trip tripNext=bus_queue_to.get(i+1);
							if(tripNext.getNextObuTimeMin().after(obuTimeTemp)) {
								quit=false;
							}
						}else {
							quit=false;
						}
						//quit=true;
						if(!quit) {
							Date arrivalTime=scheduleParam.getObuTimeNextRound(direction, trip.getNextObuTimeMin());
							if(arrivalTime!=null&&DateUtil.isInTimeRange(arrivalTime, scheduleParam.getMiddleStopEndTime(), scheduleParam.getMiddleStopOffDutyBeginTime())) {//再回来过了下班时间
								quit=true;
							}
						}
						if(quit) {
							if(!bus.isHasMiddleStop()) {
								trip.getBus().setHasMiddleStop(true);
								routeSchedule.singleClassBusAdd(trip, direction);
							}
							trip.setQuitMark(true);
						}else {
							trip.setQuitMark(false);
						}
					}
				}else if(bus.getShiftType()!=null) {
					Date endTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
					if(scheduleParam.isEndDirectionTrip(trip)&&!trip.getNextObuTimeMin().before(endTime)) {
						trip.setQuitMark(true);
						Trip tripRecovery=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(),endTime);
						if(tripRecovery!=null) {
							if(!tripRecovery.getNextObuTimeMin().after(trip.getNextObuTimeMin())) {
								trip.setQuitMark(false);
							}/*else {
								routeSchedule.addShortTripBack(tripRecovery);//注释，等退出处理，防止插入两次
							}*/
						}
					}
				}
				if(!trip.isQuitMark())
					obuTime=obuTimeTemp;
			}
			shortLineAdjust=getBusAdjustList(direction);
		}
		for(Trip tripArrival:bus_queue_to) {
			if(shortLineAdjust.contains(tripArrival))
				continue;
			if(tripArrival.getShortLineAdjust()!=null) {
				shortLineAdjust.add(tripArrival);
			}else if(!tripArrival.isQuitMark()&&tripArrival.getTripBeginTime()!=null
					&&scheduleParam.isEndDirectionTrip(tripArrival)){
				if(DateUtil.getDateHM("0934").equals(tripArrival.getTripEndTime()))
					System.out.println("aaa");
				Date endTime=null;
				Bus bus=tripArrival.getBus();
				if(bus.isSingleClass()) {
					if(!bus.isHasMiddleStop()) {
						endTime=scheduleParam.getMiddleStopBeginTime();
					}else {
						endTime=scheduleParam.getMiddleStopOffDutyBeginTime();
					}
				}else if(bus.getShiftType()!=null) {
					ScheduleParamShift shift=bus.getShiftType();
					if(shift.getEndTime()!=null) {
						endTime=DateUtil.getDateHM(shift.getEndTime());
					}
				}
				if(endTime!=null) {
					Date obuTimeNextRound=null;
					if(scheduleParam.getEndDirection()==null) {
						obuTimeNextRound=scheduleParam.getMinObuTimeNext(direction, tripArrival.getNextObuTimeMin());
					}else {
						obuTimeNextRound=scheduleParam.getObuTimeNextRound(direction, tripArrival.getNextObuTimeMin());
					}
					if(obuTimeNextRound!=null&&
							!obuTimeNextRound.before(DateUtil.getDateAddMinute(endTime, 60))) {//再走全程太晚下班了
						if(!scheduleParam.getRouteStaTurnList(direction).isEmpty()) {//有短线兜回来
							shortLineAdjust.add(tripArrival);
						}else {
							boolean quitMark=false;
							Trip tripArrivalNext=null;
							boolean pass=false;
							int busNumber=0;
							for(Trip tripArrivalTemp:bus_queue_to) {
								if(tripArrivalTemp==tripArrival) {
									pass=true;
									continue;
								}
								if(tripArrivalTemp.isQuitMark()||
									tripArrivalTemp.getShortLineAdjust()!=null) {
									continue;
								}
								busNumber++;
								if(pass) {
									tripArrivalNext=tripArrivalTemp;
									break;
								}
							}
							if(tripArrivalNext!=null) {
								int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripArrivalNext.getNextObuTimeMin());
								int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
								if(interval<=maxInterval*busNumber) {
									quitMark=true;
								}
							}
							if(quitMark) {
								tripArrival.setQuitMark(true);//没有就提早下班
								if(bus.getShiftType()!=null) {
									Trip tripRecovery=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(),endTime);
									if(tripRecovery!=null) {
										if(!tripRecovery.getNextObuTimeMin().after(tripArrival.getNextObuTimeMin())) {
											tripArrival.setShortLineAdjust(null);
											tripArrival.setQuitMark(false);
										}/*else {
											routeSchedule.addShortTripBack(tripRecovery);//注释，等退出处理，防止插入两次
										}*/
									}
								}
							}
						}
					}
				}
			}
		}
		Collections.sort(shortLineAdjust, new MinObuTimeComparator());
		for(Trip trip:shortLineAdjust) {
			bus_queue_to.remove(trip);
		}
		if(!scheduleParam.getRouteStaTurnList().isEmpty()&&runTime.before(scheduleParam.getMiddleStopRecoveryBeginTime())) {
			List<Trip> singleBusTripList=new ArrayList<Trip>();//单班车连续数
			Integer endDirection=null;
			List<Trip> busQueue=new ArrayList<Trip>();
			busQueue.addAll(bus_queue_to);
			List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
			List<Trip> busAdjustList=getBusAdjustList(1-direction);//获取对面短线调位
			for(Trip trip:busQueueFrom) {
				if(!busAdjustList.contains(trip)) {
					busQueue.add(trip);
				}
			}
			for(Trip tripArrival:busQueue) {
				if(tripArrival.getBus().getStartDirection()==1&&tripArrival.getBus().getStartOrderNumber()==15) {
					System.out.println("aaa");
				}
				if(!tripArrival.getBus().isSingleClass()) {//有双班车，重置
					singleBusTripList.clear();
					continue;
				}
				if(scheduleParam.getEndDirection()!=null&&
						scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {//哪里出哪里收
					if(endDirection!=null&&tripArrival.getBus().getStartDirection()!=endDirection) {//方向与前车不一致
						if(tripArrival.getBus().isSingleClass()||tripArrival.isQuitMark())//反向单班车或到站退出，继续数
							continue;
						singleBusTripList.clear();
					}
					endDirection=tripArrival.getBus().getStartDirection();
					singleBusTripList.add(tripArrival);
				}else {
					singleBusTripList.add(tripArrival);
				}
				if(singleBusTripList.size()>2) {//连续单班车超过两台
					for(int i=0;i<singleBusTripList.size();) {
						Trip trip=singleBusTripList.get(i);
						if((trip.isQuitMark()&&trip.isShortLine())||trip.getBus().isHasMiddleStop()) {//短线回头收工
							singleBusTripList.remove(trip);
						}else {
							i++;
						}
					}
					if(singleBusTripList.size()>2) {
						System.out.println("aaa");
						int index=busQueue.indexOf(tripArrival);
						if(index<busQueue.size()-1) {
							Trip tripNext=busQueue.get(index+1);
							if(tripNext.getDirection()==tripArrival.getDirection()) {
								if(!tripNext.getBus().isSingleClass()&&tripNext.getBus().getStartDirection()!=tripArrival.getBus().getStartDirection()) {
									if(bus_queue_to.contains(tripArrival)) {
										index=bus_queue_to.indexOf(tripArrival);
										bus_queue_to.set(index, tripNext);
										bus_queue_to.set(index+1, tripArrival);
									}else {
										index=busQueueFrom.indexOf(tripArrival);
										busQueueFrom.set(index, tripNext);
										busQueueFrom.set(index+1, tripArrival);
									}
								}
							}
						}
						if(routeSchedule.isHadSingleBusAdjust(singleBusTripList)) {
							endDirection=null;
							singleBusTripList.clear();
						}else {
							Date arrivalTimeBegin=singleBusTripList.get(0).getTripEndTime();
							if(arrivalTimeBegin==null)
								arrivalTimeBegin=singleBusTripList.get(0).getNextObuTimeMin();
							Date arrivalTimeEnd=null;
							int tripDirection=singleBusTripList.get(0).getDirection();
							if(singleBusTripList.get(singleBusTripList.size()-1).getDirection()!=tripDirection) {
								arrivalTimeEnd=scheduleParam.getArrivalTime(singleBusTripList.get(singleBusTripList.size()-1).getNextObuTimeMin(), tripDirection);
							}else {
								arrivalTimeEnd=singleBusTripList.get(singleBusTripList.size()-1).getTripEndTime();
								if(arrivalTimeEnd==null)
									arrivalTimeEnd=singleBusTripList.get(singleBusTripList.size()-1).getNextObuTimeMin();
							}
							Date arrivalTimeReverseBegin=null;
							Date arrivalTimeReverseEnd=null;
							if(!singleBusTripList.get(0).isQuitMark()) {
								arrivalTimeReverseBegin=scheduleParam.getArrivalTime(arrivalTimeBegin, 1-tripDirection);
								arrivalTimeReverseEnd=scheduleParam.getArrivalTime(arrivalTimeEnd, 1-tripDirection);
							}
							boolean success=false;
							List<Trip> doubleClassesInterval=getDoubleClassesBusInterval();//双班车车距
							for(Trip trip:doubleClassesInterval) {
								Date obuTimeTemp=trip.getNextObuTimeMin();
								int directionTemp=1-trip.getDirection();
								List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(directionTemp);
								for(RouteStaTurn routeStaTurn:routeStaTurnList) {
									if(directionTemp==tripDirection) {
										if(arrivalTimeReverseBegin!=null) {
											Date arrivalTimeTemp=scheduleParam.getArrivalTime(obuTimeTemp, routeStaTurn);
											if(DateUtil.isInTimeRange(arrivalTimeTemp, arrivalTimeReverseBegin, arrivalTimeReverseEnd)) {
												if(directionTemp==direction) {
													trip.setShortLineAdjust(routeStaTurn);
													routeSchedule.setSingleBusAdjust(singleBusTripList, trip);
													if(bus_queue_to.contains(trip)) {
														shortLineAdjust.add(trip);
														bus_queue_to.remove(trip);
													}
													endDirection=null;
													singleBusTripList.clear();
													success=true;
													break;
												}
											}
										}
									}else {
										Date arrivalTimeTemp=scheduleParam.getArrivalTime(obuTimeTemp, routeStaTurn);
										if(DateUtil.isInTimeRange(arrivalTimeTemp, arrivalTimeBegin, arrivalTimeEnd)) {
											if(directionTemp==direction) {
												trip.setShortLineAdjust(routeStaTurn);
												routeSchedule.setSingleBusAdjust(singleBusTripList, trip);
												if(bus_queue_to.contains(trip)) {
													shortLineAdjust.add(trip);
													bus_queue_to.remove(trip);
												}
												endDirection=null;
												singleBusTripList.clear();
												success=true;
												break;
											}
										}
									}
								}
								if(success)
									break;
							}
						}
					}
				}
			}
		}
		if(bus_queue_to.isEmpty())
			return;
		electricitySupplement.setBusElecSupply(direction, bus_queue_to);
		Trip tripArrival=bus_queue_to.get(0);
		//while(!tripArrival.getNextObuTimeMin().after(beginTimeNext)) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		while(tripArrival.getNextObuTimeMin().before(beginTimeNext)||
				tripArrival.getNextObuTimeMin().equals(latestTime)||
				(scheduleHalfHour.getNextScheduleHalfHour()==null&&
				tripArrival.getTripEndTime()!=null&&tripArrival.getTripEndTime().before(latestTime))) {
			Bus bus=tripArrival.getBus();
			if(DateUtil.getDateHM("1021").equals(tripArrival.getTripEndTime())) {
				System.out.println("aaa");
			}
			if(tripArrival.isQuitMark()) {
				bus_queue_to.remove(tripArrival);
				if(tripArrival.getBus().isSingleClass()) {
					if(!tripArrival.getBus().isHasMiddleStop()) {
				
						tripArrival.getBus().setHasMiddleStop(true);
						List<Trip> singleClassQueue=routeSchedule.getSingleClassQueue(direction);
						if(!singleClassQueue.contains(tripArrival)) {
							routeSchedule.singleClassBusAdd(tripArrival, direction);
						}
					}
				}else if(bus.getShiftType()!=null) {
					Trip trip=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), tripArrival.getNextObuTimeMin());
					if(trip!=null)
						routeSchedule.addShortTripBack(trip);
				}
				if(bus_queue_to.isEmpty())
					break;
				tripArrival=bus_queue_to.get(0);
			}else {
				Date obuTime=tripArrival.getNextObuTimeMin();
				if(tripLast!=null) {
					if(!tripLast.getTripBeginTime().before(scheduleParam.getLatestTime(direction))) {
						return;
					}
					setLatestBusLastRound();
					bus_queue_to_temp=new ArrayList<Trip>();
					//Date latestTime=scheduleParam.getLatestTime(direction);
					boolean lastRound=false; 
					for(Trip trip:bus_queue_to) {
						if(!trip.isQuitMark()&&(trip.getTripEndTime()==null||trip.getTripEndTime().before(latestTime))) {
							bus_queue_to_temp.add(trip);
						}
						if(trip.getTripEndTime()!=null&&!trip.getTripEndTime().before(latestTime)) {
							lastRound=true;
						}
					}
					Trip latestBusLastRound=routeSchedule.getLatestBusLastRound(direction);//已确定对面末班车在这边的班次
					if(latestBusLastRound!=null) {
						if(tripLast.getBus()==latestBusLastRound.getBus()) {
							//routeSchedule.setLatestBusLastRound(tripLast);//设置最后一圈
							//latestBusLastRound=tripLast;
							//tripLast.setNextObuTimeMin(scheduleParam.getLatestTime(1-direction));
						}
					}
					/*if(bus_queue_to_temp.size()>2&&//注释掉，否则尾车有问题
							tripLast.getTripBeginTime().after(scheduleParam.getLatePeakEndTime())&&
							scheduleParam.getRouteId()!=294&&latestBusLastRound!=null&&
							(tripLast.getTripBeginTime().before(latestBusLastRound.getTripBeginTime())//对面末班车最后一圈还没发
									||routeSchedule.getLatestBus(direction)!=null)) {//末班车已确定
						setQuitMarkOffDutyEarlyNew(scheduleHalfHour, bus_queue_to);
						if(tripArrival.isQuitMark()) {
							bus_queue_to.remove(tripArrival);
							if(bus_queue_to.isEmpty())
								break;
							tripArrival=bus_queue_to.get(0);
						}
						if(!tripLast.getTripBeginTime().before(latestBusLastRound.getTripBeginTime())) {
							obuTime=getObuTime4LastRound(direction);
						}else {
							if(tripArrival.getBus()==latestBusLastRound.getBus()) {
								obuTime=latestBusLastRound.getTripBeginTime();
							}else {
								obuTime=getObuTime4BeforeLastRound(direction,bus_queue_to);
							}
						}
						if(obuTime==null)
							System.out.println("aaa");
						if(tripArrival.getTripBeginTime()!=null) {
							if(obuTime.before(tripArrival.getNextObuTimeMin())) {
								obuTime=tripArrival.getNextObuTimeMin();
							}
						}
					}else {*/
					obuTime=null;
					if(lastRound&&bus_queue_to_temp.size()==1) {
						obuTime=latestTime;
					}else {
						if(bus_queue_to_temp.size()<6&&!scheduleParam.isLoopLine()) {//回来的车少于5台，补回来
							List<Trip> bus_queue_from=routeSchedule.getBusQueue(direction);
							lastRound=false;
							for(Trip trip:bus_queue_from) {
								if(trip.isQuitMark())
									continue;
								Bus busTemp=new Bus(trip.getBus().getStartDirection(), trip.getBus().getStartOrderNumber());
								BeanUtils.copyProperties(trip.getBus(), busTemp);
								Date tripBeginTime=trip.getNextObuTimeMin();
								/*if(interval!=null&&tripFullTemp!=null)
									tripBeginTime=DateUtil.getDateAdd(tripFullTemp.getTripBeginTime(), Calendar.MINUTE, interval);*/
								if(tripBeginTime.after(scheduleParam.getLatestTime(1-direction))){//到对面站的时间晚于对面末班车时间
									lastRound=true;
									break;
								}
								Trip tripVirtual=new Trip(trip.getBus(),1-direction,tripBeginTime);
								Date tripEndTime=scheduleParam.getArrivalTime(tripBeginTime, 1-direction);
								if(tripEndTime==null)
									break;
								tripVirtual.setTripEndTime(tripEndTime);
								if(scheduleParam.isEatAfterTrip(trip)) {
									int minRestTime=scheduleParam.getMinRestTime(tripEndTime, 1-tripVirtual.getDirection());
									tripVirtual.setNextObuTimeMin(DateUtil.getDateAdd(tripEndTime, Calendar.MINUTE, minRestTime));
								}else {
									tripVirtual.setNextObuTimeMin(scheduleParam);
								}
								if(trip.getBus().getShiftType()!=null&&scheduleParam.isEndDirectionTrip(tripVirtual)&&
									!tripVirtual.getNextObuTimeMin().before(DateUtil.getDateHM(trip.getBus().getShiftType().getEndTime()))) {
									continue;//回来要退出
								}else if(scheduleParam.isEndAfterTrip(tripVirtual)){
									continue;//回来要退出
								}
								if(tripVirtual.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {//回来过时
									int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), scheduleParam.getLatestTime(direction))/bus_queue_to_temp.size();
									int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
									if(intervalTemp>maxInterval&&
											tripVirtual.getTripEndTime().before(scheduleParam.getLatestTime(direction))) {//断位，加一台车
										tripVirtual.setNextObuTimeMin(scheduleParam.getLatestTime(direction));
										bus_queue_to_temp.add(tripVirtual);
									}
									lastRound=true;
									break;
								}
								bus_queue_to_temp.add(tripVirtual);
								if(bus_queue_to_temp.size()==6)
									break;
							}
							if(lastRound) {
								bus_queue_to_temp.get(bus_queue_to_temp.size()-1).setNextObuTimeMin(scheduleParam.getLatestTime(direction));
							}
						}
						if((bus_queue_to_temp.size()>1&&bus_queue_to_temp.get(1).getNextObuTimeMin().after(scheduleParam.getLatestTime(direction)))||
								tripArrival.getNextObuTimeMin().equals(scheduleParam.getLatestTime(direction))) {//下一班晚于末班车
							obuTime=scheduleParam.getLatestTime(direction);
							if(bus_queue_to_temp.size()>1&&bus_queue_to_temp.get(1).getTripEndTime().before(scheduleParam.getLatestTime(direction))) {
								int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), scheduleParam.getLatestTime(direction));
								int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
								if(interval>maxInterval) {//断位，加一台车
									bus_queue_to_temp.get(1).setNextObuTimeMin(scheduleParam.getLatestTime(direction));
									obuTime=null;//重算
								}
							}
						}
						if(obuTime==null){
							Integer interval=getClassesFullInterval(scheduleHalfHour.getRunTime(), tripLast, bus_queue_to_temp);
							if(interval==null) {
								obuTime=tripArrival.getNextObuTimeMin();
							}else {
								int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
								if(interval>maxInterval) {
									lastRound=false;
									for(Trip tripArrivalTemp:bus_queue_to_temp) {
										if(tripArrivalTemp.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
											lastRound=true;
											break;
										}
									}
									if(!lastRound)
										interval=getInterval4ExceedMaxInterval(direction, tripLast, bus_queue_to_temp);
								}else {
									if(DateUtil.isInTimeRange(tripLast.getTripBeginTime(), scheduleParam.getLatePeakBeginTime(), scheduleParam.getLatePeakEndTime())&&
											latestTime.after(scheduleParam.getLatePeakEndTime())) {//晚高峰
										if(tripLast.getLastTripFull()!=null) {
											int intervalLast=DateUtil.getMinuteInterval(tripLast.getLastTripFull().getTripBeginTime(), tripLast.getTripBeginTime());
											if(interval<intervalLast) {//间隔下降
												int planNumber=scheduleHalfHour.getPlanList().size();
												ScheduleHalfHour scheduleHalfHourNextTemp=scheduleHalfHourNext;
												while(scheduleHalfHourNextTemp!=null&&
														DateUtil.getDateHM(scheduleHalfHourNextTemp.getRunTime()).before(scheduleParam.getLatePeakEndTime())) {
													int planNumberNext=scheduleHalfHourNextTemp.getPlanList().size();
													if(planNumberNext>planNumber) {
														interval=intervalLast;
														if(interval>maxInterval)
															interval=maxInterval;
														break;
													}
													scheduleHalfHourNextTemp=scheduleHalfHourNextTemp.getNextScheduleHalfHour();
												}
											}
											if(!DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalLast).after(runTime))
												interval=intervalLast;
											obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
											if(DateUtil.isInTimeRange(tripLast.getTripBeginTime(), runTime, beginTimeNext)) {
												if(!scheduleHalfHour.getObuTimeList().isEmpty()) {
													int intervalAvg=DateUtil.getMinuteInterval(runTime, beginTimeNext)/scheduleHalfHour.getObuTimeList().size();
													if(interval>intervalAvg) {//保证时段班次数
														List<Trip> arrivalRunTimeList=new ArrayList<Trip>();
														for(Trip trip:bus_queue_to_temp) {
															if(trip.getNextObuTimeMin().before(beginTimeNext)) {
																arrivalRunTimeList.add(trip);
															}
														}
														int intervalMin=getClassesFullInterval(scheduleHalfHour.getRunTime(), tripLast, arrivalRunTimeList);
														if(intervalMin<=intervalAvg) {
															if(intervalAvg>interval)//防止放快后面断位
																interval=intervalAvg;
														}else {
															interval=intervalMin;
														}
													}
												}
											}
											if(interval<intervalLast&&((intervalLast-interval)*1.0/intervalLast)>0.2) {//
												interval=(int)Math.ceil(intervalLast*0.8);
												if(interval>maxInterval) {
													int intervalAvg=getClassesFullInterval(scheduleHalfHour.getRunTime(), tripLast, bus_queue_to_temp);
													if(intervalAvg<maxInterval) {
														interval=maxInterval;
													}else {
														interval=intervalAvg;
													}
													
												}
											}
										}
									}else if(scheduleParam.getLatePeakPassengerDirection()!=null&&
											scheduleParam.getLatePeakPassengerDirection()==direction&&
											DateUtil.isInTimeRange(scheduleParam.getLatePeakBeginTime(), tripLast.getTripBeginTime(), tripLast.getTripEndTime())) {
										int intervalLast=tripLast.getInterval();
										if(!scheduleHalfHour.getObuTimeList().isEmpty()){
											int intervalAvg=DateUtil.getMinuteInterval(runTime, beginTimeNext)/scheduleHalfHour.getObuTimeList().size();
											if(tripLast.getTripBeginTime().before(runTime)) {
												if(!scheduleHalfHour.getPreScheduleHalfHour().getObuTimeList().isEmpty())
													intervalAvg=DateUtil.getMinuteInterval(DateUtil.getDateHM(scheduleHalfHour.getPreScheduleHalfHour().getRunTime()), DateUtil.getDateHM(scheduleHalfHour.getPreScheduleHalfHour().getRunTimeEnd()))/scheduleHalfHour.getPreScheduleHalfHour().getObuTimeList().size();
											}
											if(interval<intervalAvg) {//间隔大于所需间隔，按所需间隔发
												int intervalMin=interval;
												interval=intervalAvg;
												List<Trip> busQueue=routeSchedule.getBusQueue(direction);
												List<Trip> busQueueTemp=new ArrayList<Trip>();
												for(int i=busQueue.size()-1;i>=0;i--) {
													Trip tripTemp=busQueue.get(i);
													if(!tripTemp.isQuitMark())
														busQueueTemp.add(tripTemp);
													if(busQueueTemp.size()==5) {
														break;
													}
												}
												if(busQueueTemp.size()>1) {
													int intervalReverse=DateUtil.getMinuteInterval(busQueueTemp.get(0).getNextObuTimeMin(), busQueueTemp.get(busQueueTemp.size()-1).getNextObuTimeMin())/(busQueueTemp.size()-1);
													if(tripLast.getNextObuTimeMin().before(scheduleParam.getLatestTime(1-direction))) {
														int maxIntervalReverse=scheduleParam.getMaxInterval(tripLast.getNextObuTimeMin(), 1-direction);
														if(intervalReverse>maxIntervalReverse*1.2) {
															interval=(int)Math.ceil(intervalLast*0.8);
															if(interval<intervalMin) {
																interval=intervalMin;
															}
														}
													}
												}
												if(tripLast.getBus().isSingleClass()||tripArrival.getBus().isSingleClass()) {//单班车看会不会间隔过大
													if(interval>maxInterval*0.8) {
														interval=(int)Math.ceil(maxInterval*0.8);
														if(interval>intervalMin) {
															interval=intervalMin;
														}
													}
												}
												
												if(!tripLast.isEatAfterTrip()) {
													int busNumber=0;
													for(int i=0;i<busQueueTemp.size();i++) {
														Trip tripArrivalTemp=busQueueTemp.get(i);
														Date obuTimeTemp=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), (i+1)*interval);
														if(obuTimeTemp.before(scheduleParam.getLatestTime(1-direction))) {
															Trip tripTemp=new Trip(tripArrivalTemp.getBus(), direction, obuTimeTemp, scheduleParam, null);
															if(tripTemp.isQuitMark()) {
																continue;
															}
															busNumber++;
															if(!tripArrival.isEatAfterTrip()&&tripTemp.isEatAfterTrip()) {//第一台吃饭的车
																int intervalReverse=DateUtil.getMinuteInterval(tripLast.getNextObuTimeMin(), tripTemp.getNextObuTimeMin())/(busNumber+1);
																int maxIntervalReverse=scheduleParam.getMaxInterval(tripLast.getNextObuTimeMin(), 1-direction);
																if(intervalReverse>maxIntervalReverse*1.2) {
																	interval=(int)Math.ceil(intervalLast*0.8);
																	if(interval<intervalMin) {
																		interval=intervalMin;
																	}
																}
																break;
															}
														}
													}
												}
											}
										}
										if(interval>intervalLast&&((interval-intervalLast)*1.0/intervalLast)>0.2) {//
											interval=(int)Math.ceil(intervalLast*1.2);
										}
										if(interval>maxInterval)
											interval=maxInterval;
									}
								}	
								if(interval<maxInterval&&!lastRound) {//非最后一轮发班间隔小于最大间隔
									interval=getIntervalCorrect4BusArrival(direction, bus_queue_to_temp, interval);
								}
								obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
								if((busReverseAdjustBase==null||!busReverseAdjustBase.isLatestBus(bus))&&//尾车不调短线
									!DateUtil.isInTimeRange(tripLast.getNextObuTimeMin(), scheduleParam.getLatePeakBeginTime(), scheduleParam.getLatePeakEndTime())||
									(scheduleParam.getLatePeakPassengerDirection()!=null&&scheduleParam.getLatePeakPassengerDirection()==direction)) {//过对面不是
									if(tripArrival.getTripEndTime()!=null&&
										obuTime.after(tripArrival.getTripEndTime())) {
										List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
										if(!routeStaTurnList.isEmpty()) {
											if(bus_queue_to_temp.size()>1&&bus_queue_to.contains(bus_queue_to_temp.get(1))) {
												if(!tripLast.getBus().isSingleClass()&&!bus_queue_to_temp.get(1).getBus().isSingleClass()) {//前后车不是单班车
													RouteStaTurn routeStaTurn=getRouteStaTurn4Rest2Long(direction, interval, bus_queue_to_temp);//停站过长，当前车发短线
													if(routeStaTurn!=null) {
														bus_queue_to.remove(tripArrival);
														tripArrival.setShortLineAdjust(routeStaTurn);
														shortLineAdjust.add(tripArrival);
														tripArrival=bus_queue_to_temp.get(1);
														if(DateUtil.getDateHM("1733").equals(tripArrival.getTripEndTime()))
															System.out.println("aaa");
														bus=tripArrival.getBus();
													}
												}
											}
										}
									}
								}
							}
						}
					}
					//}
					if(obuTime==null)
						break;
					/*if(tripArrival.getTripBeginTime()!=null) {
						if(obuTime.before(tripArrival.getNextObuTimeMin())) {
							obuTime=tripArrival.getNextObuTimeMin();
						}
					}*/
					if(scheduleParam.getRouteId()==179) {
						Date minObuTime=getMinObuTime4Supper(direction);
						if(minObuTime!=null) {
							if(obuTime.before(minObuTime))
								obuTime=minObuTime;
						}
					}
					/*Date maxObuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), maxInterval);
					if(obuTime.after(maxObuTime)) {//超过最大发班间隔
						if(tripArrival.getTripEndTime()!=null&&!tripArrival.getTripEndTime().before(maxObuTime)) {//断位才到
							obuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);//停站一分钟
						}else {
							obuTime=maxObuTime;
						}
					}*/
					if(tripArrival.getTripEndTime()!=null) {
						Date minObuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);//最少停站1分钟
						if(tripArrival.isElecSupplyAfterTrip()) {
							minObuTime=tripArrival.getNextObuTimeMin();
						}
						if(obuTime.before(minObuTime))
							obuTime=minObuTime;
					}else {
						if(bus.getShiftCode()==ShiftType.SINGLE_SHIFT_MIDDLE_STOP.getValue()) {//单班车
							if(bus.isHasMiddleStop()&&obuTime.before(scheduleParam.getMiddleStopRecoveryBeginTime())) {//防止9点后出的单班车
								obuTime=scheduleParam.getMiddleStopRecoveryBeginTime();
							}
						}else {
							if(obuTime.before(tripArrival.getNextObuTimeMin()))
								obuTime=tripArrival.getNextObuTimeMin();
						}
					}
					if(scheduleHalfHourNext!=null&&!obuTime.before(beginTimeNext)) {
						break;
					}
				}
				if(obuTime.after(scheduleParam.getLatestTime(direction)))
					break;
				bus_queue_to.remove(tripArrival);
				routeSchedule.setEaten(tripArrival.getBus(), tripArrival, obuTime, scheduleParam);
				Trip trip=new Trip(bus, direction, obuTime,scheduleParam,tripLast);
				if(!scheduleParam.isLoopLine()&&trip.isEatAfterTrip()&&
						scheduleParam.getLunchBeginTime(direction)==null
						&&scheduleParam.getSupperBeginTime(direction)==null&&
						!obuTime.equals(scheduleParam.getLatestTime(direction))) {//只能在对面吃饭，尽快走
					
					
					Trip tripFullLast=routeSchedule.getTripFullLast(1-direction);//对站最后一台已发
					List<Trip> busQueueTo=routeSchedule.getBusQueue(direction);
					Date obuTimeLast=tripFullLast.getTripBeginTime();
					for(Trip tripArrivalTemp:busQueueTo) {
						if(!tripArrivalTemp.isQuitMark()) {
							if(obuTimeLast.after(scheduleParam.getLatestTime(1-direction))) {
								obuTimeLast=null;
								break;
							}
							int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, 1-direction);
							obuTimeLast=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
						}
					}
					if(obuTimeLast!=null) {
						if(obuTimeLast.before(scheduleParam.getLatestTime(1-direction))) {
							int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, 1-direction);
							Date obuTimeTemp=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
							if(trip.getNextObuTimeMin().after(obuTimeTemp)) {//去对面太晚了
								int diff=DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), obuTimeTemp);
								obuTimeTemp=DateUtil.getDateAddMinute(obuTime, -diff);
								Date obuTimeNew=null;
								if(tripArrival.getTripEndTime()==null||(!tripArrival.isElecSupplyAfterTrip()&&obuTimeTemp.after(tripArrival.getTripEndTime()))) {
									obuTimeNew=obuTimeTemp;
								}else if(tripArrival.isElecSupplyAfterTrip()&&obuTime.after(tripArrival.getNextObuTimeMin())) {
									obuTimeNew=tripArrival.getNextObuTimeMin();
									diff=DateUtil.getMinuteInterval(obuTime, obuTimeNew);
								}else {
									obuTimeNew=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
								}
								if(obuTimeNew!=null) {
									if(!obuTimeNew.after(tripLast.getTripBeginTime())) {
										int intervalLast=tripLast.getInterval();
										obuTimeNew=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), intervalLast/2);
									}
									if(bus_queue_to.size()>1) {
										Trip tripArrivalNext=bus_queue_to.get(1);
										if(DateUtil.getDateAddMinute(obuTimeNew, maxInterval).before(tripArrivalNext.getNextObuTimeMin())) {
											obuTimeNew=DateUtil.getDateAddMinute(tripArrivalNext.getNextObuTimeMin(), -maxInterval);
											if(DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), maxInterval).before(obuTimeNew)) {
												obuTimeNew=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), maxInterval);
											}
										}
									}
									if(tripArrival.isElecSupplyAfterTrip()&&obuTimeNew.before(tripArrival.getNextObuTimeMin()))
										obuTimeNew=tripArrival.getNextObuTimeMin();
									else if(tripArrival.getTripEndTime()!=null&&obuTimeNew.before(tripArrival.getTripEndTime())){
										obuTimeNew=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 1);
									}
									diff=DateUtil.getMinuteInterval(obuTime, obuTimeNew);
									obuTime=obuTimeNew;
									trip.setTripBeginTime(obuTime);
									trip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), -diff));
									trip.setNextObuTimeMin(DateUtil.getDateAddMinute(trip.getNextObuTimeMin(), -diff));
								}
							}
						}
					}
				}
				trip.setLastTrip(tripLast);
				if(tripLast!=null)
					tripLast.setNextTrip(trip);
				routeSchedule.addTrip(trip);
				//if(tripArrival.getTripBeginTime()!=null)//过滤复行
				trip.setPreTrip(tripArrival);
				System.out.println(trip.getBusName()+"\t"+HM_FORMAT.format(trip.getTripBeginTime())+"\t"+(tripArrival.getTripEndTime()==null?"":HM_FORMAT.format(tripArrival.getTripEndTime()))+"\t"+(tripArrival.getNextObuTimeMin()==null?"":HM_FORMAT.format(tripArrival.getNextObuTimeMin())));
				tripLast=trip;
				tripList.add(trip);
				/*if(trip.isQuitMark()&&bus.getShiftType()!=null) {
					trip=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(), trip.getNextObuTimeMin());
					if(trip!=null)
						routeSchedule.addShortTripBack(trip);
				}*///注释，等退出再处理
				bus_queue_to_temp.remove(tripArrival);
				if(bus_queue_to.isEmpty())
					break;
				tripArrival=bus_queue_to.get(0);
			}
		}
		
		if(!shortLineAdjust.isEmpty()) {
			Collections.sort(shortLineAdjust, new MinObuTimeComparator());
			for(Trip tripLong:tripList) {
				if(shortLineAdjust.isEmpty())
					break;
				/*int planNumber=scheduleHalfHour.getPlanList().size();
				int tripNumber=0;
				for(Trip trip:routeSchedule.getTripList(direction)) {
					if(DateUtil.isInTimeRange(trip.getTripBeginTime(), runTime, beginTimeNext)) {
						tripNumber++;
					}
				}
				if(tripNumber>=planNumber&&scheduleHalfHourNext!=null) {//本时段班次够
					List<Date> nextObuTimeMinList=new ArrayList<Date>();
					if(!bus_queue_to.isEmpty()&&!bus_queue_to.get(bus_queue_to.size()-1).
							getNextObuTimeMin().before(DateUtil.getDateHM(scheduleHalfHourNext.getRunTimeEnd()))) {
						for(Trip trip:bus_queue_to) {
							if(!trip.isQuitMark()&&trip.getNextObuTimeMin().before(DateUtil.getDateHM(scheduleHalfHourNext.getRunTimeEnd()))){
								nextObuTimeMinList.add(trip.getNextObuTimeMin());
							}
						}
					}else {
						for(Trip trip:bus_queue_to) {
							nextObuTimeMinList.add(trip.getNextObuTimeMin());
						}
						List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
						for(Trip trip:busQueueFrom) {
							Date nextObuTimeMin=scheduleParam.getObuTimeNextRound(direction, trip.getTripBeginTime()!=null?trip.getTripBeginTime():trip.getNextObuTimeMin());
							if(!trip.isQuitMark()&&nextObuTimeMin.before(DateUtil.getDateHM(scheduleHalfHourNext.getRunTimeEnd())))
								nextObuTimeMinList.add(nextObuTimeMin);
						}
					}	
					int planNumberNext=scheduleHalfHourNext.getPlanList().size();
					int tripArrivalNumber=nextObuTimeMinList.size();
					for(Trip trip:shortLineAdjust) {
						if(trip.getNextObuTimeMin().before(DateUtil.getDateHM(scheduleHalfHourNext.getRunTimeEnd()))) {
							tripArrivalNumber++;
						}
					}
					if(tripArrivalNumber<=planNumberNext) {
						break;
					}
				}
				*/
				tripArrival=shortLineAdjust.get(0);
				if(DateUtil.getDateHM("1709").equals(tripArrival.getTripEndTime()))
					System.out.println("aaa");
				if(tripArrival.isQuitMark()) {
					shortLineAdjust.remove(tripArrival);
					continue;
				}
				Bus bus = tripArrival.getBus();
				Trip lastTrip=tripLong.getLastTrip();
				if(!lastTrip.isShortLine()) {
					int interval=DateUtil.getMinuteInterval(lastTrip.getTripBeginTime(), tripLong.getTripBeginTime())/2;
					Date obuTime=DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval);
					/*if(obuTime.before(runTime)) {//发班时间早于时段开始时间
						int tripArrivalNumber=0;
						for(Trip trip:shortLineAdjust) {
							if(trip.getNextObuTimeMin().before(DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd()))) {
								tripArrivalNumber++;
							}
						}
						if(tripNumber+tripArrivalNumber<=planNumber) {//班次可能不够
							continue;
						}
					} */
					if(tripArrival.getTripEndTime()==null||(!tripArrival.isElecSupplyAfterTrip()&&obuTime.after(tripArrival.getTripEndTime()))
							||(tripArrival.isElecSupplyAfterTrip()&&!obuTime.before(tripArrival.getNextObuTimeMin()))) {
						if(tripArrival.getBus().isSingleClass()&&tripArrival.getTripEndTime()==null&&
								obuTime.before(scheduleParam.getMiddleStopRecoveryBeginTime())) {//单班车过早复行
							continue;
						}
						Trip tripShort=null;
						Date endTime=null;
						if(tripArrival.getShortLineAdjust()!=null) {
							tripShort=getTrip4ShortLine(tripArrival.getShortLineAdjust(), bus, obuTime);
						}else {
							if(bus.isSingleClass()) {
								if(lastTrip.getTripBeginTime().before(scheduleParam.getMiddleStopRecoveryBeginTime())) {
									endTime=scheduleParam.getMiddleStopBeginTime();
								}else {
									endTime=scheduleParam.getMiddleStopOffDutyBeginTime();
								}
							}else {
								ScheduleParamShift shift=bus.getShiftType();
								if(shift==null)
									System.out.println("aaa");
								endTime=DateUtil.getDateHM(shift.getEndTime());
							}
							RouteStaTurn routeStaTurn=getShortLineAdjust(direction, bus, obuTime, endTime);
							if(routeStaTurn!=null) {
								tripShort=getTrip4ShortLine(routeStaTurn, bus, obuTime);
							}
						}
						if(tripShort!=null) {
							if(endTime==null||tripShort.getTripBeginTime().before(endTime)) {//开车时间在收工时间前
								routeSchedule.addTrip(tripShort);
								if(endTime==null||tripShort.getTurnTrip().getNextObuTimeMin().before(endTime)) {
									routeSchedule.addShortTripBack(tripShort.getTurnTrip());
								}else {
									if(bus.getShiftType()!=null) {
										ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(bus.getShiftType().getShiftType(), bus.getStartDirection(), endTime);
										if(scheduleParamShift!=null) {
											routeSchedule.addShiftBus(scheduleParamShift, bus);
										}
									}else if(bus.isSingleClass()&&endTime.equals(scheduleParam.getMiddleStopBeginTime())) {
										bus.setHasMiddleStop(true);
										routeSchedule.singleClassBusAdd(tripArrival, direction);
									}
								}
								lastTrip.setNextTrip(tripShort);
								tripLong.setLastTrip(tripShort);
								tripShort.setLastTrip(lastTrip);
								tripShort.setNextTrip(tripLong);
								tripShort.setPreTrip(tripArrival);
								addMiddleStopTrip(tripShort);
							}else {
								if(bus.isSingleClass()) {
									if(!bus.isHasMiddleStop()) {
										bus.setHasMiddleStop(true);
										routeSchedule.singleClassBusAdd(tripArrival, direction);
									}
								}else {
									tripArrival.setQuitMark(true);
									Trip tripRecovery=scheduleParam.busCheckIn(bus, bus.getShiftType().getShiftType(),endTime);
									if(tripRecovery!=null) {
										if(!tripRecovery.getNextObuTimeMin().after(tripArrival.getNextObuTimeMin())) {
											tripArrival.setShortLineAdjust(null);
											tripArrival.setQuitMark(false);
										}else {
											routeSchedule.addShortTripBack(tripRecovery);
										}
									}
								}
							}
							shortLineAdjust.remove(tripArrival);
						}
					}
				}
			}
			if(!shortLineAdjust.isEmpty()){
				for(Trip trip:shortLineAdjust) {
					boolean success=false;
					for(int i=0;i<bus_queue_to.size();i++) {
						if(trip.getNextObuTimeMin().before(bus_queue_to.get(i).getNextObuTimeMin())) {
							bus_queue_to.add(i, trip);
							success=true;
							break;
						}
					}
					if(!success)
						bus_queue_to.add(trip);
				}
			}
		}
		for(Trip trip:tripList) {//统一设置吃饭
			if(trip.getBus().getStartDirection()==0&&trip.getBus().getStartOrderNumber()==6) {
				System.out.println("aaa");
			}
			routeSchedule.setEaten(trip.getBus(), trip.getPreTrip(), trip.getTripBeginTime(), scheduleParam);
			addMiddleStopTrip(trip);
		}
		routeSchedule.tripListSort();
	}
	
	private int getIntervalCorrect4BusArrival(int direction,List<Trip> busQueue4Full,int interval) {
		Trip lastFullTrip=routeSchedule.getTripFullLast(direction);
		int diff=0;
		Date obuTimeLast=DateUtil.getDateAddMinute(lastFullTrip.getTripBeginTime(), interval);
		for(int i=1;i<busQueue4Full.size();i++) {
			if(!obuTimeLast.before(scheduleParam.getLatestTime(direction))) {
				break;
			}
			int maxInterval=scheduleParam.getMaxInterval(obuTimeLast, direction);
			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
			Trip tripArrival=busQueue4Full.get(i);
			if(tripArrival.getNextObuTimeMin().after(obuTime)) {
				diff+=DateUtil.getMinuteInterval(tripArrival.getNextObuTimeMin(), obuTime);
			}
			obuTimeLast=obuTime;
		}
		interval+=diff;
		int maxInterval=scheduleParam.getMaxInterval(lastFullTrip.getTripBeginTime(), direction);
		if(interval>maxInterval)//保证这班不断位
			interval=maxInterval;
		return interval;
	}
	
	private RouteStaTurn getShortLineAdjust(int direction,Bus bus,Date obuTime,Date endTime) {
		RouteStaTurn shortLineAdjust=null;
		Date arrivalTime=null;
		List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
		for(RouteStaTurn routeStaTurn:routeStaTurnList) {
			Trip tripShortTemp=getTrip4ShortLine(routeStaTurn, bus, obuTime);
			Date arrivalTimeTemp=tripShortTemp.getTurnTrip().getTripEndTime();
			if(shortLineAdjust==null) {
				shortLineAdjust=routeStaTurn;
				arrivalTime=arrivalTimeTemp;
			}else {
				if(arrivalTimeTemp.before(endTime)) {
					if(arrivalTimeTemp.after(arrivalTime)) {
						shortLineAdjust=routeStaTurn;
						arrivalTime=tripShortTemp.getTurnTrip().getTripEndTime();
					}
				}else {//截止时间后到
					if(DateUtil.getMinuteInterval(arrivalTimeTemp, endTime)<DateUtil.getMinuteInterval(arrivalTime, endTime)) {
						shortLineAdjust=routeStaTurn;
						arrivalTime=tripShortTemp.getTurnTrip().getTripEndTime();
					}
				}
				
			}
		}
		return shortLineAdjust;
	}
	
	private void addMiddleStopTrip(Trip trip) {
		int direction=trip.getDirection();
		Trip preTrip=trip.getPreTrip();
		if(preTrip!=null&&preTrip.getTripBeginTime()==null)//把单班复行的前一趟设置成null
			trip.setPreTrip(null);
		if(trip.getPreTrip()==null) {
			List<Trip> busTripList=routeSchedule.getTripMap().get(trip.getBus());
			if(busTripList!=null&&busTripList.size()>1) {
				Collections.sort(busTripList,new TripBeginTimeComparator());
				preTrip=busTripList.get(busTripList.size()-2);//获取中停前的班次
				if(preTrip.getTurnTrip()!=null) {//短线调头
					preTrip=preTrip.getTurnTrip();
				}
				if(preTrip.getTripEndTime()==null) {//最后一单补电
					if(preTrip.getServiceType()==ServiceType.ELECTRICITY_SUPPLEMENT) {
						preTrip.setTripEndTime(DateUtil.getDateAddMinute(preTrip.getTripBeginTime(), 30));
					}
				}
				Trip middleStopTrip=new Trip(trip.getBus(), Direction.NODIRECTION.getValue(), DateUtil.getDateAdd(preTrip.getTripEndTime(), Calendar.MINUTE, 5));//中停前一班次结束加5分钟作为中停开始时间;
				middleStopTrip.setFirstRouteStaId(scheduleParam.getFirstRouteStaId(direction));
				middleStopTrip.setLastRouteStaId(scheduleParam.getFirstRouteStaId(direction));
				preTrip=busTripList.get(busTripList.size()-1);//获取中停后的班次
				middleStopTrip.setTripEndTime(DateUtil.getDateAdd(preTrip.getTripBeginTime(), Calendar.MINUTE, -5));//中停复行开始前5分钟作为中停结束时间;	
				busTripList.add(busTripList.size()-2,middleStopTrip);
			}
		}
	}
	
	private void singleClassRecovery(Trip tripLast,List<Trip> bus_queue_to) {
		Integer busNumberPreset=scheduleParam.getBusNumberPreset();
		Integer busNumberSinglePresetUp=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
		Integer busNumberSinglePresetDown=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
		List<ScheduleShiftPreset> shiftList=scheduleParam.getShiftListPreset();
		int shiftBusNumber=0;
		if(shiftList!=null) {
			for(ScheduleShiftPreset shift:shiftList) {
				if(shift.getBusNumber()!=null) {
					shiftBusNumber+=shift.getBusNumber();
				}else {
					if(shift.getBusNumberUp()!=null) {
						shiftBusNumber+=shift.getBusNumberUp();
					}
					if(shift.getBusNumberDown()!=null) {
						shiftBusNumber+=shift.getBusNumberDown();
					}
				}
			}
		}
		if(busNumberPreset!=null&&busNumberSinglePresetUp!=null&&busNumberSinglePresetDown!=null&&
				busNumberSinglePresetUp+busNumberSinglePresetDown==busNumberPreset&&shiftBusNumber==0) {
			singleClassRecovery4All(tripLast, bus_queue_to);
		}else {
			//singleClassRecoveryNew(tripLast.getDirection());
			singleClassRecoveryNormal(tripLast, bus_queue_to);
		}
	}
	
	private void singleClassRecovery4All(Trip tripLast,List<Trip> bus_queue_to) {
		int direction=tripLast.getDirection();
		List<Trip> singleClassQueue=routeSchedule.getSingleClassQueue(direction);
		List<Trip> recoveryBusList=new ArrayList<Trip>();
		List<Trip> quitBusList=new ArrayList<Trip>();
		for(Trip trip:singleClassQueue) {
			Trip tripQuit=null;
			for(Trip tripArrival:bus_queue_to) {
				if(!tripArrival.getBus().isHasMiddleStop()&&scheduleParam.isEndDirectionTrip(tripArrival)) {
					int restTime=DateUtil.getMinuteInterval(trip.getTripEndTime(), tripArrival.getNextObuTimeMin());
					if(restTime>=scheduleParam.getMiddleStopMinimumRestTime()) {
						tripArrival.setQuitMark(true);
						tripArrival.getBus().setHasMiddleStop(true);
						tripQuit=tripArrival;
						quitBusList.add(tripQuit);
						break;
					}
				}
			}
			if(tripQuit!=null) {
				Trip tripRecovery=new Trip();
				tripRecovery.setBus(trip.getBus());
				tripRecovery.setNextObuTimeMin(tripQuit.getNextObuTimeMin());
				int index=bus_queue_to.indexOf(tripQuit);
				bus_queue_to.add(index+1, tripRecovery);
				recoveryBusList.add(trip);
			}else {
				break;
			}
		}
		for(Trip trip:recoveryBusList) {
			singleClassQueue.remove(trip);
		}
		for(Trip trip:quitBusList) {
			singleClassQueue.add(trip);
		}
		
		boolean allHasMiddleStop=true;
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(!bus.isHasMiddleStop()) {
				allHasMiddleStop=false;
				break;
			}
		}
		if(allHasMiddleStop) {
			recoveryBusList.clear();
			for(Trip trip:singleClassQueue) {
				Trip tripTemp=null;
				for(Trip tripArrival:bus_queue_to) {
					int restTime=DateUtil.getMinuteInterval(trip.getTripEndTime(), tripArrival.getNextObuTimeMin());
					if(restTime>=scheduleParam.getMiddleStopMinimumRestTime()) {
						tripTemp=tripArrival;
						break;
					}
				}
				if(tripTemp!=null) {
					Trip tripRecovery=new Trip();
					tripRecovery.setBus(trip.getBus());
					tripRecovery.setNextObuTimeMin(tripTemp.getNextObuTimeMin());
					int index=bus_queue_to.indexOf(tripTemp);
					if(tripTemp.getTripBeginTime()==null) {//防止早停晚开
						bus_queue_to.add(index+1, tripRecovery);
					}else {
						bus_queue_to.add(index, tripRecovery);
					}
					recoveryBusList.add(trip);
				}else {
					break;
				}
			}
			for(Trip trip:recoveryBusList) {
				singleClassQueue.remove(trip);
			}
		}
	}
	
	private boolean isSingleBusMoreThanDouble() {
		int singleBusNumber=0;
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.isSingleClass())
				singleBusNumber++;
		}
		if(routeSchedule.getTripMap().size()<2*singleBusNumber) {
			return true;
		}
		return false;
	}
	
	private void singleClassRecoveryNormal(Trip tripLast,List<Trip> bus_queue_to) {
		int singleBusNumber=0;
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.isSingleClass())
				singleBusNumber++;
		}
		if(singleBusNumber==0)
			return;
		int separate=1;
		boolean singleBusMore=false;
		if(isSingleBusMoreThanDouble()) {//单班车超过双班车
			singleBusMore=true;
		}/*else if(routeSchedule.getTripMap().size()/singleBusNumber>2) {
			separate=2;
		}*/
		int direction=tripLast.getDirection();
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
			Date recoveryBeginTime=scheduleParam.getMiddleStopRecoveryBeginTime(direction);
			for(;j<bus_queue_to.size();j++) {
				Trip trip=bus_queue_to.get(j);
				if(j>0) {
					lastTripObuTime=bus_queue_to.get(j-1).getNextObuTimeMin();
				}
				if(!trip.getNextObuTimeMin().before(recoveryBeginTime)) {
					//Date nextObuTimeMin=new Date((lastTripObuTime.getTime()+trip.getNextObuTimeMin().getTime())/2);
					Date nextObuTimeMin=DateUtil.getDateAddMinute(lastTripObuTime, DateUtil.getMinuteInterval(lastTripObuTime, trip.getNextObuTimeMin())/2);
					if(nextObuTimeMin.before(recoveryBeginTime))
						continue;
					Trip preTrip=routeSchedule.singleClassBusPeek(direction);
					if(preTrip==null)
						break;
					if(preTrip.getTripEndTime()==null)
						System.out.println("aaa");
					int restTime=DateUtil.getMinuteInterval(nextObuTimeMin, preTrip.getTripEndTime());
					int middleStopRestTimeMin=scheduleParam.getMiddleStopMinimumRestTime();
					if(restTime<middleStopRestTimeMin)//没休息够
						continue;
					/*if(!scheduleParam.isLoopLine()&&scheduleParam.getRouteStaTurnList().isEmpty()&&scheduleParam.getEndDirection()!=null) {//没有短线，总站停场
						Date endTime=scheduleParam.getMiddleStopOffDutyBeginTime();
						int endDirection=preTrip.getBus().getStartDirection();
						List<Trip> tripListReasoning=scheduleParam.getTripListByReasoning(nextObuTimeMin, endDirection, endTime);
						boolean recovery=false;
						for(int i=tripListReasoning.size()-2;i>=0;i--,i--) {
							Trip tripReference=tripListReasoning.get(i);
							if(DateUtil.isInTimeRange(nextObuTimeMin, DateUtil.getDateAddMinute(tripReference.getTripBeginTime(), -10), DateUtil.getDateAddMinute(tripReference.getTripBeginTime(), 60))) {//提前10分钟
								recovery=true;
							}
						}
						if(!recovery) {
							continue;
						}//防止推迟过了1700
					}*/
					routeSchedule.singleClassBusRemove(direction, preTrip);
					Bus bus=preTrip.getBus();
					if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==9)
						System.out.println("aaa");
					trip=new Trip();
					trip.setBus(bus);
					trip.setNextObuTimeMin(nextObuTimeMin);
					bus_queue_to.add(j, trip);
					
					if(singleBusMore) {//单班车过半，有设置短线的一次复行三台（短-长-短），没有短线的一次复行两台
						List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
						if(!routeStaTurnList.isEmpty()) {
							trip.setShortLineAdjust(routeStaTurnList.get(0));
						}
						
						preTrip=routeSchedule.singleClassBusPeek(direction);
						if(preTrip==null)
							break;
						restTime=DateUtil.getMinuteInterval(nextObuTimeMin, preTrip.getTripEndTime());
						middleStopRestTimeMin=scheduleParam.getMiddleStopMinimumRestTime();
						if(restTime<middleStopRestTimeMin)//没休息够
							continue;
						routeSchedule.singleClassBusRemove(direction, preTrip);
						bus=preTrip.getBus();
						if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==9)
							System.out.println("aaa");
						trip=new Trip();
						trip.setBus(bus);
						trip.setNextObuTimeMin(nextObuTimeMin);
						bus_queue_to.add(j+1, trip);
						j++;
						
						if(!routeStaTurnList.isEmpty()) {
							preTrip=routeSchedule.singleClassBusPeek(direction);
							if(preTrip==null)
								break;
							restTime=DateUtil.getMinuteInterval(nextObuTimeMin, preTrip.getTripEndTime());
							middleStopRestTimeMin=scheduleParam.getMiddleStopMinimumRestTime();
							if(restTime<middleStopRestTimeMin)//没休息够
								continue;
							routeSchedule.singleClassBusRemove(direction, preTrip);
							bus=preTrip.getBus();
							if(bus.getStartDirection()==0&&bus.getStartOrderNumber()==9)
								System.out.println("aaa");
							trip=new Trip();
							trip.setBus(bus);
							trip.setNextObuTimeMin(nextObuTimeMin);
							trip.setShortLineAdjust(routeStaTurnList.get(0));
							bus_queue_to.add(j+1, trip);
							j++;
						}
						
						
					}
					j=j+separate;//隔台复行
					if(singleClassQueue.isEmpty())
						break;
				}
			}
		
		}
	}

	private Trip getTripVirtual(Trip trip) {
		Bus busTemp=new Bus(trip.getBus().getStartDirection(), trip.getBus().getStartOrderNumber());
		BeanUtils.copyProperties(trip.getBus(), busTemp);
		Date tripBeginTime=trip.getNextObuTimeMin();
		if(tripBeginTime.after(scheduleParam.getLatestTime(1-trip.getDirection()))){//到对面站的时间晚于对面末班车时间
			return null;
		}
		Trip tripVirtual=new Trip(trip.getBus(),1-trip.getDirection(),tripBeginTime);
		Date tripEndTime=scheduleParam.getArrivalTime(tripBeginTime, 1-trip.getDirection());
		if(tripEndTime==null)
			return null;
		tripVirtual.setTripEndTime(tripEndTime);
		tripVirtual.setNextObuTimeMin(scheduleParam);
		return tripVirtual;
	}
	
	private void setQuitMarkOffDutyEarlyNew(int direction) {
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(bus_queue_to.isEmpty()||bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime()))//没到收车阶段
			return;
		Trip tripLast=routeSchedule.getTripFullLast(direction);
		Date obuTimeLast=tripLast.getTripBeginTime();
		Date obuTimeLastTemp=obuTimeLast;
		boolean lastRound=false;
		int maxInteval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
		if(!bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(latestTime)) {//准备发最后半圈
			int busNumberMin=(int)Math.ceil(DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)*1.0/maxInteval);//最少车数
			maxInteval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busNumberMin;//重算间隔，防止车不够
			lastRound=true;
		}
		int index=0;//最后预设双班车早收索引
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark()) {
				if(!trip.getBus().isSingleClass()) {
					index=i;
					obuTimeLast=obuTimeLastTemp;
				}
			} else {
				if(trip.getShortLineAdjust()==null) {
					if(!lastRound&&obuTimeLastTemp.before(latestTime))
						maxInteval=scheduleParam.getMaxInterval(obuTimeLastTemp, direction);
					obuTimeLastTemp=DateUtil.getDateAddMinute(obuTimeLastTemp, maxInteval);
				}
			}
		}
		if(index!=0) {//已设置过双班早收，获取下一个可以早收的车位
			boolean run=false;
			for(int i=index+1;i<bus_queue_to.size()-1;i++) {
				Trip trip=bus_queue_to.get(i);
				if(!trip.isQuitMark()) {
					if(run) {
						Trip tripNext=bus_queue_to.get(i+1);
						if(!tripNext.isQuitMark()) {
							index=i;
							break;
						}
					}else {
						run=true;
					}
				}else {
					run=false;
				}
			}
		}
		
		for(int i=index;i<bus_queue_to.size()-1;i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark()) {
				boolean run=false;
				for(int j=i+1;j<bus_queue_to.size()-1;j++) {
					Trip tripTemp=bus_queue_to.get(j);
					if(!tripTemp.isQuitMark()) {
						if(run) {
							i=j;
							trip=bus_queue_to.get(i);
							break;
						}else {
							run=true;
						}
					}else {
						run=false;
					}
				}
			}
			if(!lastRound&&obuTimeLast.before(latestTime))
				maxInteval=scheduleParam.getMaxInterval(obuTimeLast, direction);
			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, maxInteval);
			Trip tripNext=bus_queue_to.get(i+1);
			if(tripNext.isQuitMark()||trip.getBus()==busReverseAdjustBase.getLatestBusMain()||
					trip.getBus()==busReverseAdjustBase.getLatestBusSecondary()||!scheduleParam.isEndDirectionTrip(trip)||
					trip.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime())) {//下一台车退出，或者收车方向不对，或者还没到收车时间，这台车不能安排早收
				obuTimeLast=obuTime;
				continue;
			}
			List<Trip> busTripList=routeSchedule.getTripList(trip.getBus());
			boolean eleSupplement=false;
			for(Trip tripTemp:busTripList) {
				if(tripTemp.getServiceType()==ServiceType.ELECTRICITY_SUPPLEMENT) {//补过电，先不早收
					eleSupplement=true;
					break;
				}
			}
			if(eleSupplement) {
				obuTimeLast=obuTime;
				continue;
			}
			
			boolean quit=false;
			for(int j=i+1;j<bus_queue_to.size();j++) {
				tripNext=bus_queue_to.get(j);
				if(tripNext.isQuitMark()||tripNext.getShortLineAdjust()!=null)
					continue;
				if(tripNext.getNextObuTimeMin().after(obuTime)) {
					quit=false;
					break;
				}else {
					quit=true;
				}
				if(!lastRound&&obuTimeLast.before(latestTime))
					maxInteval=scheduleParam.getMaxInterval(obuTimeLast, direction);
				obuTime=DateUtil.getDateAddMinute(obuTime, maxInteval);
			}
			if(quit) {
				trip.setQuitMark(true);
				i++;
			}
		}
	}
	
	private void setQuitMarkOffDutyEarlyNew(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {
		int direction=scheduleHalfHour.getDirection();
		Trip latestBusLastRound=routeSchedule.getLatestBusLastRound(direction);
		if(latestBusLastRound==null)
			return;
		int maxInterval=scheduleParam.getMaxInterval(latestBusLastRound.getTripBeginTime(), direction);
		Trip tripLast=routeSchedule.getTripLast(direction);
		if(tripLast.getTripBeginTime().before(latestBusLastRound.getTripBeginTime()))
			return;
		if(tripLast.getTripBeginTime().equals(DateUtil.getDateHM("2152")))
			System.out.println("aaa");
		if(latestBusLastRound!=null) {
			if(!tripLast.getTripBeginTime().before(latestBusLastRound.getTripBeginTime())){//对面末班车最后一圈已发
				int quitLastIndex=-1;//获取之前设置的最后一个退出车辆
				for(int i=0;i<bus_queue_to.size();i++) {
					if(bus_queue_to.get(i).isQuitMark()) {
						quitLastIndex=i;
					}
				}
				Date obuTimeLast=tripLast.getTripBeginTime();
				if(quitLastIndex!=-1) {
					for(int i=0;i<quitLastIndex;i++) {
						if(!bus_queue_to.get(i).isQuitMark()) {
							obuTimeLast=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
						}
					}
					quitLastIndex++;
				}else {
					quitLastIndex=0;
				}
				List<Date> obuTimeList=new ArrayList<Date>();
				List<Trip> tripList=new ArrayList<Trip>();
				Date obuTime=obuTimeLast;
				for(int i=quitLastIndex;i<bus_queue_to.size();i++) {
					if(!bus_queue_to.get(i).getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
						if(!bus_queue_to.get(i).isQuitMark())
							tripList.add(bus_queue_to.get(i));
					}
					Date obuTimeNext=DateUtil.getDateAddMinute(obuTime, maxInterval);
					if(obuTimeNext.after(scheduleParam.getLatestTime(direction))) {
						if(!obuTime.equals(scheduleParam.getLatestTime(direction))) {
							obuTime=scheduleParam.getLatestTime(direction);
							obuTimeList.add(obuTime);
						}
						break;
					}
					obuTime=obuTimeNext;
					obuTimeList.add(obuTime);
				}
				int index=0;
				for(int i=quitLastIndex;i<tripList.size()-1;i++) {
					Trip trip=tripList.get(i);
					Bus latestBusUp=routeSchedule.getLatestBus(Direction.UP.getValue());
					Bus latestBusDown=routeSchedule.getLatestBus(Direction.DOWN.getValue());
					if(trip.getBus().equals(latestBusUp)||trip.getBus().equals(latestBusDown))//末班车不能早收
						continue;
					if(!scheduleParam.isEndDirectionTrip(trip))
						continue;
					if(!tripList.get(i+1).getNextObuTimeMin().after(obuTimeList.get(index))) {//当前车抽停，下一台车不断位
						boolean quit=true;
						if(trip.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime())) {
							quit=false;
						}
						int busNum=0;
						for(Trip tripArrival:tripList) {
							if(!tripArrival.isQuitMark())
								busNum++;
						}
						if(busNum<=obuTimeList.size()) {//车刚好
							break;
						}
						for(int j=i+2,z=1;j<tripList.size();j++,z++) {
							if(tripList.get(j).getNextObuTimeMin().after(obuTimeList.get(index+z))) {//判断其他会不会断位
								quit=false;
								break;
							}
						}
						if(quit) {
							trip.setQuitMark(true);
						}else {
							index++;
						}
					}else {
						index++;
					}
				}
			}
		}
	}
	
	private void setQuitMarkOffDutyEarly(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {//早收处理
		if(scheduleHalfHour.getDirection()==0&&scheduleHalfHour.getRunTime().equals("2100"))
			System.out.println("aaaa");
		Trip tripLast=routeSchedule.getTripLast(scheduleHalfHour.getDirection());
		Trip tripLastFull=routeSchedule.getTripFullLast(scheduleHalfHour.getDirection());
		List<Trip> bus_queue_to_temp=new ArrayList<Trip>();
		int direction=scheduleHalfHour.getDirection();
		for(Trip trip:bus_queue_to) {
			if(!trip.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction)))
				bus_queue_to_temp.add(trip);
		}
		if(!scheduleParam.isLoopLine()) {
			Trip tripLastNext=getTripVirtual(tripLast);
			if(tripLastNext!=null&&tripLastNext.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
				List<Trip> bus_queue_from=routeSchedule.getBusQueue(scheduleHalfHour.getDirection());//反向
				for(Trip trip:bus_queue_from) {
					if(trip.isQuitMark())
						continue;
					Trip tripVirtual=getTripVirtual(trip);
					if(tripVirtual!=null&&!tripVirtual.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction)))
						bus_queue_to_temp.add(tripVirtual);
				}
				Trip trip=bus_queue_to_temp.get(bus_queue_to_temp.size()-1);
				trip.setNextObuTimeMin(scheduleParam.getLatestTime(direction));
			}
		}
		int maxInterval=scheduleParam.getMaxInterval(scheduleHalfHour.getRunTime(),scheduleHalfHour.getDirection());
		Date lastObuTime=tripLastFull.getTripBeginTime();
		Calendar calendar=DateUtil.getCalendar(lastObuTime);
		for(int i=0;i<bus_queue_to_temp.size()-1;i++) {
			Trip trip=bus_queue_to_temp.get(i);
			if(trip.isQuitMark())
				continue;
			calendar.add(Calendar.MINUTE, maxInterval);
			if(trip.getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime()))//在早收时间前到，不能停
				continue;
			if(!scheduleParam.isEndDirectionTrip(trip)) {
				continue;
			}
			
			List<Trip> bus_queue_to_available=new ArrayList<Trip>();
			for(int j=i+1;j<bus_queue_to_temp.size();j++) {
				Trip tripTemp=bus_queue_to_temp.get(j);
				if(!tripTemp.isQuitMark())
					bus_queue_to_available.add(tripTemp);
			}
			boolean quit=false;
			if(!bus_queue_to_available.isEmpty()) {
				quit=true;
				Calendar cal=DateUtil.getCalendar(calendar.getTime());
				for(int j=0;j<bus_queue_to_available.size();j++) {
					Trip tripTemp=bus_queue_to_available.get(j);
					if(!tripTemp.getNextObuTimeMin().after(cal.getTime())) {
						cal.add(Calendar.MINUTE, maxInterval);
					}else {
						quit=false;//退出会断位
						break;
					}
				}
			}
			if(quit){
				if(i>0) {
					tripLast=bus_queue_to_temp.get(i-1);
				}
				if(tripLast.isQuitMark())//不能连续退出
					continue;
				Trip tripQuit=trip;
				Calendar cal=DateUtil.getCalendar(calendar.getTime());
				for(int j=i+1;j<bus_queue_to_temp.size()-1;j++) {
					trip=bus_queue_to_temp.get(j);
					Trip tripNext=bus_queue_to_temp.get(j+1);
					if(trip.isQuitMark())
						continue;
					cal.add(Calendar.MINUTE, maxInterval);
					if(!tripNext.getNextObuTimeMin().after(cal.getTime())) {
						if(trip.getBus().getStartOrderNumber()<tripQuit.getBus().getStartOrderNumber()) {
							tripLast=bus_queue_to_temp.get(j-1);
							if(!tripLast.isQuitMark()&&scheduleParam.isEndDirectionTrip(trip))//前一台不是退出
								tripQuit=trip;
						}
					}
				}
				tripQuit.setQuitMark(true);
			}
		}
	}
	
	private int getBusNumber(int direction) {
		int busNumber=0;
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.getStartDirection()==direction) {
				busNumber++;
			}
		}
		return busNumber;
	}
	
	private void setQuitMarkMiddleStop4Preset(int direction,int singleClassesBusUp,int singleClassesBusDown,List<Trip> bus_queue_to) {
		int index=0;//单班车索引
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark()) {//找到最后的退出索引
				index=i+2;
			}
		}
		for(;index<bus_queue_to.size();) {
			Trip trip=bus_queue_to.get(index);
			if(trip.getTripEndTime()!=null&&!trip.getTripEndTime().before(scheduleParam.getMiddleStopRecoveryBeginTime())) {//过了中停时间
				break;
			}
			if(singleClassesBusUp==scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue())&&
					singleClassesBusDown==scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue()))//数量匹配
				break;
			if(trip.getBus().isSingleClass()&&trip.getBus().isHasMiddleStop()) {//防止两次单班退出
				index++;
				continue;
			}
			if(trip.getBus().getShiftCode()!=ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP.getValue()) {
				index++;
				continue;
			}
			if(trip.getBus().getStartDirection()==Direction.UP.getValue()) {//无需再中停
				if(singleClassesBusUp==scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue())) {
					index++;
					continue;
				}
			}else {
				if(singleClassesBusDown==scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue())) {
					index++;
					continue;
				}
			}
			if(trip.getBus().getShiftType()==null&&scheduleParam.isEndDirectionTrip(trip)&&
					(busReverseAdjustBase==null||(trip.getBus()!=busReverseAdjustBase.getLatestBusMain()&&
							trip.getBus()!=busReverseAdjustBase.getLatestBusSecondary()))&&
					trip.getBus().getStartOrderNumber()>=scheduleParam.getMiddleStopBeginOrderNumber()&&
					!trip.getNextObuTimeMin().before(scheduleParam.getMiddleStopBeginTime())) {
				boolean isAllSingleBus=false;
				if(trip.getBus().getStartDirection()==Direction.UP.getValue()) {
					if(scheduleParam.getBusNumberPresetUp()==scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue())) {
						isAllSingleBus=true;
					}
				}else {
					if(scheduleParam.getBusNumberPresetDown()==scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue())) {
						isAllSingleBus=true;
					}
				}
				if(isAllSingleBus) {
					if(singleClassesBusUp+singleClassesBusDown>=scheduleParam.getBusNumberPreset()/2) {//达到一半
						return;
					}
				}
				Bus bus=trip.getBus();
				bus.setSingleClass(true);
				bus.setHasMiddleStop(true);
				trip.setQuitMark(true);
				index+=2;
				if(trip.getBus().getStartDirection()==Direction.UP.getValue())
					singleClassesBusUp++;
				else {
					singleClassesBusDown++;
				}
				routeSchedule.singleClassBusAdd(trip,direction);
			}else {
				index++;
			}
		}
	}
	
	private void setQuitMarkMiddleStop4Preset(int direction,int singleClassesBus,List<Trip> bus_queue_to) {
		int index=0;//单班车索引
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark()) {//找到最后的退出索引
				index=i+2;
			}
		}
		for(;index<bus_queue_to.size();) {
			Trip trip=bus_queue_to.get(index);
			if(trip.getTripEndTime()!=null&&!trip.getTripEndTime().before(scheduleParam.getMiddleStopRecoveryBeginTime())) {//过了中停时间
				break;
			}
			if(singleClassesBus==scheduleParam.getBusNumberSinglePreset())//数量匹配
				break;
			if(trip.getBus().isSingleClass()&&trip.getBus().isHasMiddleStop()) {//防止两次单班退出
				index++;
				continue;
			}
			if(trip.getBus().getShiftCode()==ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP.getValue()&&scheduleParam.isEndDirectionTrip(trip)&&
					trip.getBus().getStartOrderNumber()>=scheduleParam.getMiddleStopBeginOrderNumber()&&
					!trip.getNextObuTimeMin().before(scheduleParam.getMiddleStopBeginTime())) {
				Bus bus=trip.getBus();
				bus.setSingleClass(true);
				bus.setHasMiddleStop(true);
				trip.setQuitMark(true);
				index+=2;
				singleClassesBus++;
				routeSchedule.singleClassBusAdd(trip,direction);
			}else {
				index++;
			}
		}
	}
	
	private void setQuitMarkMiddleStop4Preset(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {
		Trip tripArrival=bus_queue_to.get(bus_queue_to.size()-1);
		if(!tripArrival.getNextObuTimeMin().before(scheduleParam.getMiddleStopBeginTime())&&
				tripArrival.getNextObuTimeMin().before(scheduleParam.getMiddleStopRecoveryBeginTime())) {
			int singleClassesBusUp=getSingleClassesBusList(Direction.UP.getValue()).size();
			int singleClassesBusDown=getSingleClassesBusList(Direction.DOWN.getValue()).size();
			Integer singleClassesPreset=scheduleParam.getBusNumberSinglePreset();//总单班车数
			Integer singleClassesPresetUp=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());//上行单班车数
			Integer singleClassesPresetDown=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());//下行单班车数
			if(singleClassesPreset!=null&&singleClassesPresetUp==null&&singleClassesPresetDown==null) {//按总单班车数计算
				setQuitMarkMiddleStop4Preset(scheduleHalfHour.getDirection(), singleClassesBusUp+singleClassesBusDown, bus_queue_to);
			}else {
				setQuitMarkMiddleStop4Preset(scheduleHalfHour.getDirection(), singleClassesBusUp, singleClassesBusDown, bus_queue_to);
			}
		}
	}
	
	private List<Bus> getSingleClassesBusList(int direction) {
		List<Bus> singleClassesBusList=new ArrayList<Bus>();
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==direction)
					singleClassesBusList.add(bus);
			}
		}
		List<Trip> busQueue=routeSchedule.getBusQueue(direction);
		for(Trip trip:busQueue) {
			Bus bus=trip.getBus();
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==direction&&
						!singleClassesBusList.contains(bus))
					singleClassesBusList.add(bus);
			}
		}
		busQueue=routeSchedule.getBusQueueShort(direction);
		for(Trip trip:busQueue) {
			Bus bus=trip.getBus();
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==direction&&
						!singleClassesBusList.contains(bus))
					singleClassesBusList.add(bus);
			}
		}
		busQueue=routeSchedule.getBusQueue(1-direction);
		for(Trip trip:busQueue) {
			Bus bus=trip.getBus();
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==direction&&
						!singleClassesBusList.contains(bus))
					singleClassesBusList.add(bus);
			}
		}
		busQueue=routeSchedule.getBusQueueShort(1-direction);
		for(Trip trip:busQueue) {
			Bus bus=trip.getBus();
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==direction&&
						!singleClassesBusList.contains(bus))
					singleClassesBusList.add(bus);
			}
		}
		return singleClassesBusList;
	}
	
	private void setQuitMarkMiddleStop4Optimal(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to, Bus busException) {
		Trip tripLast=routeSchedule.getTripLast(scheduleHalfHour.getDirection());
		Trip tripLastFull=routeSchedule.getTripFullLast(scheduleHalfHour.getDirection());
		if(tripLastFull==null)
			return;
		Date lastObuTime=tripLastFull.getTripBeginTime();
		if(scheduleHalfHour.getDirection()==1) {
			System.out.println("aaa");
		}
		int maxInterval=scheduleParam.getMaxInterval(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
		for(int i=0;i<bus_queue_to.size()-1;i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark())
				continue;
			if(!scheduleParam.isEndDirectionTrip(trip)) {
				continue;
			}
			if(trip.getBus().isSingleClass())
				continue;
			Integer busNumberSinglePreset=scheduleParam.getBusNumberSinglePreset(scheduleHalfHour.getDirection());
			if(busNumberSinglePreset!=null) {
				if(routeSchedule.getSingleClassQueue(scheduleHalfHour.getDirection()).size()==busNumberSinglePreset) {//该方向中停车等于预设值
					break;
				}
			}else {
				if(routeSchedule.singleClassBusSize()>=scheduleParam.getBusNumberSingle()) {
					break;
				}
			}
			Integer minDoubleClassesBusNumber=scheduleParam.getMinDoubleClassesBusNumber(scheduleHalfHour.getDirection());
			if(minDoubleClassesBusNumber!=null) {
				int busNumber=getBusNumber(scheduleHalfHour.getDirection());
				if(routeSchedule.getSingleClassQueue(scheduleHalfHour.getDirection()).size()==busNumber-minDoubleClassesBusNumber) {//该方向中停车等于预设值
					break;
				}
			}
			lastObuTime=DateUtil.getDateAddMinute(lastObuTime, maxInterval);
			if(!isLatestBus(trip.getBus())&&trip.getBus()!=busException&&trip.getBus().getStartOrderNumber()>=scheduleParam.getMiddleStopBeginOrderNumber()&&!trip.getNextObuTimeMin().before(scheduleParam.getMiddleStopBeginTime())&&
					trip.getTripEndTime()!=null&&!trip.getTripEndTime().after(scheduleParam.getMiddleStopEndTime())) {
				Trip tripNext=bus_queue_to.get(i+1);
				if(!tripNext.getNextObuTimeMin().after(lastObuTime)&&
						((i==0&&!tripLast.getBus().isSingleClass())||(i>0&&!bus_queue_to.get(i-1).getBus().isSingleClass()))
						&&!bus_queue_to.get(i+1).getBus().isSingleClass()){//这台车退出，下台车不会断位，并且前一台不是单班车
					boolean canPause=true;
					Date date=lastObuTime;
					for(int j=i+2;j<bus_queue_to.size();j++) {
						Trip tripTemp=bus_queue_to.get(j);
						if(tripTemp.isQuitMark())
							continue;
						date=DateUtil.getDateAddMinute(date, maxInterval);
						if(tripTemp.getNextObuTimeMin().after(date)) {//中停后，后面的车跟不上
							canPause=false;
							break;
						}
					}
					if(canPause) {
						trip.setQuitMark(true);//10点到11点，设置退出标识
						Bus bus=trip.getBus();
						bus.setSingleClass(true);
						//bus.setHasMiddleStop(true);//处理退出时在设置
						//routeSchedule.singleClassBusAdd(trip,scheduleHalfHour.getDirection());
						i++;
					}
				}
			}
		}
	}
	
	private void setQuitMark4MiddleStop(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to,Bus busException) {
		Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
		if(scheduleParam.getMiddleStopBeginTime()!=null&&scheduleParam.getMiddleStopEndTime()!=null) {
			Date middleStopLastRound=DateUtil.getDateAdd(scheduleParam.getMiddleStopBeginTime(), Calendar.MINUTE, -60);
			Date middleStopEndTime=scheduleParam.getMiddleStopEndTime();
			if(scheduleParam.getBusNumberPresetUp()!=null||
					scheduleParam.getBusNumberPresetDown()!=null||
					scheduleParam.getBusNumberSinglePreset()!=null) {
				if(scheduleParam.getBusNumberSinglePreset()!=null||
						scheduleParam.getRouteStaTurnList().isEmpty()) {//没有短线，前面没设置单班车
					setQuitMarkMiddleStop4Preset(scheduleHalfHour, bus_queue_to);
				}
			}else {
				if(DateUtil.isInTimeRange(runTime, middleStopLastRound, middleStopEndTime)) {//中停前一个小时
					setQuitMarkMiddleStop4Optimal(scheduleHalfHour, bus_queue_to, busException);
				}
			}
		}
	}
	
	private void setQuitMark(ScheduleHalfHour scheduleHalfHour,List<Trip> bus_queue_to) {
		Date runTime=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime()).getTime();
		setQuitMark4MiddleStop(scheduleHalfHour, bus_queue_to, null);
		Date offDutyBeginTime=scheduleParam.getOffDutyBeginTime();
		if(offDutyBeginTime!=null) {
			Date offDutyLastRound=DateUtil.getDateAdd(offDutyBeginTime, Calendar.MINUTE, -60);
			if(!runTime.before(offDutyLastRound)) {//早收前一个小时
				if(scheduleParam.isLoopLine()) {
					setQuitMarkOffDutyEarly(scheduleHalfHour, bus_queue_to);
				}else {
					setQuitMarkOffDutyEarlyNew(scheduleHalfHour, bus_queue_to);
				}
			}
		}
	}
	
	private void setLatestBusLastRound() {
		if(scheduleParam.getEndDirection()==null||
				scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {
			setLatestBusLastRound(Direction.UP.getValue());
			setLatestBusLastRound(Direction.DOWN.getValue());
		}
	}
	
	private void setLatestBusLastRound(int direction) {
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		List<Date> fixedObuTimeList=getFixedObuTimeList(1-direction);//收车倒推班次
		if(!fixedObuTimeList.isEmpty()&&bus_queue_to.size()>1) {
			Date lastRoundBeginTime=fixedObuTimeList.get(0);
			if(!bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(lastRoundBeginTime)) {//到站车有进入最后一轮范围
				if(routeSchedule.getLatestBusLastRound(direction)==null) {
					List<Trip> bus_queue_available=new ArrayList<Trip>();
					for(Trip trip:bus_queue_to) {
						if(!trip.isQuitMark()&&!trip.getBus().isSingleClass())
							bus_queue_available.add(trip);
					}
					Bus bus=null;
					if(bus_queue_available.isEmpty())
						return;
					if(bus_queue_available.get(0).getNextObuTimeMin().after(lastRoundBeginTime)) {//第一个到站车过了时间
						Trip tripLast=routeSchedule.getTripFullLast(direction);
						while(tripLast.isQuitMark()) {
							tripLast=tripLast.getLastTripFull();
						}
						bus=tripLast.getBus();
					}else {
						if(bus_queue_available.get(bus_queue_available.size()-1).getNextObuTimeMin().equals(lastRoundBeginTime)) {//最后一台可以做末班车
							bus=bus_queue_available.get(bus_queue_available.size()-1).getBus();
						}else {
							for(int i=1;i<bus_queue_available.size();i++) {
								if(!bus_queue_available.get(i-1).getNextObuTimeMin().after(lastRoundBeginTime)
										&&bus_queue_available.get(i).getNextObuTimeMin().after(lastRoundBeginTime)) {
									bus=bus_queue_available.get(i-1).getBus();
									/*if(i>1) {
										int interval=DateUtil.getMinuteInterval(bus_queue_available.get(i-2).getNextObuTimeMin(), bus_queue_available.get(i).getNextObuTimeMin())/2;
										Date nextObuTimeMin=DateUtil.getDateAddMinute(bus_queue_available.get(i-2).getNextObuTimeMin(), interval);
										if(nextObuTimeMin.before(bus_queue_available.get(i-1).getNextObuTimeMin())) {
											nextObuTimeMin=bus_queue_available.get(i-1).getNextObuTimeMin();
										}
										if(nextObuTimeMin.after(lastRoundBeginTime)) {
											nextObuTimeMin=lastRoundBeginTime;
										}
										lastRoundBeginTime=nextObuTimeMin;
									}*/
									break;
								}
							}
							if(bus==null) {
								Trip trip=bus_queue_available.get(bus_queue_available.size()-1);
								if(!trip.getNextObuTimeMin().after(lastRoundBeginTime)) {
									bus=trip.getBus();
								}
							}
						}
					}
					if(bus==null)
						System.out.println("aaa");
					Trip trip=new Trip(bus, direction, lastRoundBeginTime);
					routeSchedule.setLatestBusLastRound(trip);
				}
			}
		}
		Trip latestBusLastRound=routeSchedule.getLatestBusLastRound(direction);
		if(latestBusLastRound!=null) {
			List<Trip> bus_queue_from=routeSchedule.getBusQueue(direction);
			boolean pass=false;
			for(Trip trip:bus_queue_from) {
				if(trip.getBus()==latestBusLastRound.getBus()) {//对面尾车已开出
					pass=true;
					continue;
				}
				if(pass) {
					if(!trip.isQuitMark()&&!trip.getNextObuTimeMin().after(scheduleParam.getLatestTime(1-direction))) {
						latestBusLastRound.setBus(trip.getBus());
					}
				}
			}
		}
	}

	private void busQueueNextHalfSupply(List<Trip> bus_queue_to,ScheduleHalfHour scheduleHalfHour) {
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		if(scheduleHalfHourNext==null)
			return;
		Date endTimeNext=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTimeEnd()).getTime();
		if(bus_queue_to.isEmpty()) {
			return;
		}
		Trip tripLast=bus_queue_to.get(bus_queue_to.size()-1);
		if(tripLast.getNextObuTimeMin().before(endTimeNext)) {//不够车,如发上行7:00时，7:30到8:00时段要对向7:00后发的车
			List<ScheduleHalfHour> scheduleHalfHourList=scheduleParam.getScheduleHalfHourList();
			boolean past=false;
			ScheduleHalfHour scheduleHalfHourReverse=null;//反向的下个时段
			for(ScheduleHalfHour shh:scheduleHalfHourList) {
				if(shh==scheduleHalfHour) {
					past=true;
					continue;
				}
				if(past) {
					scheduleHalfHourReverse=shh;
					break;
				}
			}
			if(scheduleParam.isLoopLine()||scheduleHalfHourReverse.getDirection()!=scheduleHalfHour.getDirection()) {
				List<Date> obuTimeListReverse=scheduleHalfHourReverse.getObuTimeList();
				for(Date obuTime:obuTimeListReverse) {
					Trip trip=new Trip();
					Date tripEndTime=scheduleParam.getArrivalTime(obuTime, scheduleHalfHourReverse.getDirection());
					int minRestTime=scheduleParam.getMinRestTime(tripEndTime, 1-scheduleHalfHourReverse.getDirection());
					trip.setNextObuTimeMin(DateUtil.getDateAdd(tripEndTime, Calendar.MINUTE, minRestTime));
					bus_queue_to.add(trip);
				}
			}
		}
	}
	
	private void calculateRushTime(ScheduleHalfHour scheduleHalfHour) {
		if(scheduleHalfHour.getDirection()==1)
			System.out.println("aaaa");
		ScheduleHalfHour scheduleHalfHourNext=scheduleHalfHour.getNextScheduleHalfHour();
		int direction=scheduleHalfHour.getDirection();
		Date obuTimeLast=routeSchedule.getObuTimeFullLast(direction);
		ArrayList<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		//按到站时间排序
		for(int i=0;i<bus_queue_to.size();i++) {
			for(int j=i;j<bus_queue_to.size();j++) {
				if(bus_queue_to.get(i).getNextObuTimeMin().after(bus_queue_to.get(j).getNextObuTimeMin())) {
					Trip tempTrip=bus_queue_to.get(i);
					bus_queue_to.set(i, bus_queue_to.get(j));
					bus_queue_to.set(j, tempTrip);
				}
			}
		}
		Calendar calendar=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime());
		Date beginTime=calendar.getTime();
		Date endTime;
		if(scheduleHalfHourNext==null) {//没有下个时段
			endTime=scheduleParam.getLatestTime(direction);
		}else {
			endTime=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTime()).getTime();
		}
		List<Plan> planList=scheduleHalfHour.getPlanList();
		for(int i=0;i<planList.size();i++) {//发班时间优化加车
			Date obu_time=planList.get(i).getPlanTime();
			if(i<bus_queue_to.size()) {
				Trip trip=bus_queue_to.get(i);
				if(trip.getNextObuTimeMin().after(obu_time)) {
					/*Date obu_time_next=null;
					if(i==planList.size()-1) {//最后一个
						obu_time_next=scheduleHalfHourNext.getObuTimeList().get(0);
					}else {
						obu_time_next=planList.get(i+1).getPlanTime();
					}
					if(!trip.getNextObuTimeMin().before(obu_time_next)||
							trip.getNextObuTimeMin().getTime()-obu_time.getTime()>obu_time_next.getTime()-trip.getNextObuTimeMin().getTime()
							||routeSchedule.getBusNumber()<scheduleParam.getBusNumberConfig()) {//车辆到站时间超过发班时间的1/2
*/						trip=new Trip();
						trip.setNextObuTimeMin(beginTime);
						bus_queue_to.add(i,trip);
					//}
				}
			}else {//后面没车了
				Trip trip=new Trip();
				trip.setNextObuTimeMin(beginTime);
				bus_queue_to.add(i,trip);
			}
		}
		//计算可用车数
		int busAvailable=0;
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.getNextObuTimeMin().before(endTime)) {
				busAvailable++;
			}else {
				break;
			}
		}
		if(busAvailable>planList.size()) {//本时段可用车辆大于发班数,可否加密班次
			int addBusNumNext=0;//本时段推迟到后半小时发的车辆数
			ArrayList<Trip> bus_queue_to_bak=(ArrayList<Trip>)bus_queue_to.clone();
			int index=busAvailable;
			List<Date> obu_time_list_temp=DispatchRule.getObuTimeList(scheduleHalfHour.getRunTime(), scheduleHalfHour.getRunTimeEnd(), obuTimeLast,busAvailable);
			calendar.setTime(obu_time_list_temp.get(obu_time_list_temp.size()-1));
			if(scheduleHalfHourNext==null)
				System.out.println("aaa");
			int maxInterval=scheduleParam.getMaxInterval(scheduleHalfHourNext.getRunTime(), scheduleHalfHourNext.getDirection());
			calendar.add(Calendar.MINUTE, maxInterval);
			busQueueNextHalfSupply(bus_queue_to_bak, scheduleHalfHourNext);//补充下半小时时段到站车辆
			Date endTimeNext=DateUtil.getCalendarHM(scheduleHalfHourNext.getRunTimeEnd()).getTime();
			//计算推迟的发车数
			while(calendar.getTime().before(endTimeNext)) {//断位加车情况
				if(index<bus_queue_to_bak.size()) {
					Trip trip=bus_queue_to_bak.get(index);
					if(trip.getNextObuTimeMin().after(calendar.getTime())) {
						trip=new Trip();
						trip.setNextObuTimeMin(endTime);
						bus_queue_to_bak.add(index,trip);
						addBusNumNext++;
					}
					index++;
				}else {
					addBusNumNext++;
				}
				calendar.add(Calendar.MINUTE, maxInterval);
			}
			if(scheduleHalfHour.getRunTime().startsWith("1800")){
				System.out.println("aa");
			}
			//下个时段是否均匀校验
			List<Date> obu_time_queue_next=scheduleHalfHourNext.getObuTimeList();
			index=busAvailable;
			
			for(int i=0;i<obu_time_queue_next.size();i++) {
				long interval=0;
				Date obu_time=obu_time_queue_next.get(i);
				if(index<bus_queue_to_bak.size()) {
					Trip trip=bus_queue_to_bak.get(index);
					if(trip.getNextObuTimeMin().after(obu_time)) {
						Date obu_time_next=null;
						if(i<obu_time_queue_next.size()-1)
							obu_time_next=obu_time_queue_next.get(i+1);
						if(obu_time_next!=null) {
							interval=obu_time_next.getTime()-obu_time.getTime();
							/*if(!trip.getNextObuTimeMin().before(obu_time_next)||
									trip.getNextObuTimeMin().getTime()-obu_time.getTime()>obu_time_next.getTime()-trip.getNextObuTimeMin().getTime()) {*/
							if(trip.getNextObuTimeMin().after(obu_time)) {
								addBusNumNext++;//赶不上，本时段加一台车到下一时段
							}else {
								index++;
							}
						}else {//下个时段最后一个班次
							if((trip.getNextObuTimeMin().getTime()-obu_time.getTime())*2>interval) {
								addBusNumNext++;
							}
						}
					}else {
						index++;
					}
				}else {//没车了
					addBusNumNext++;
				}
			}
			busAvailable=busAvailable-addBusNumNext;
			if(busAvailable>planList.size()) {
				if(scheduleParam.getRouteStaTurnList().isEmpty()) {
					Trip lastTrip=routeSchedule.getTripLast(direction);
					Date runTimeEnd=DateUtil.getDateHM(scheduleHalfHour.getRunTimeEnd());
					int classesNumber=planList.size();
					while(classesNumber<busAvailable) {
						classesNumber++;
						Integer interval=null;
						Date lastObuTime=null;
						if(lastTrip==null) {
							lastObuTime=scheduleParam.getFirstTime(direction);
							interval=DateUtil.getMinuteInterval(runTimeEnd, lastObuTime)/classesNumber;
							lastObuTime=DateUtil.getDateAddMinute(lastObuTime, -interval);
						}else {
							lastObuTime=lastTrip.getTripBeginTime();
							interval=DateUtil.getMinuteInterval(runTimeEnd, lastObuTime)/(classesNumber+1);
						}
						boolean success=true;
						List<Plan> planListTemp=new ArrayList<Plan>();
						for(int i=0;i<classesNumber;i++) {
							Date obuTime=DateUtil.getDateAddMinute(lastObuTime, (i+1)*interval);
							if(bus_queue_to.get(i).getTripEndTime()!=null&&
									bus_queue_to.get(i).getNextObuTimeMin().after(obuTime)) {//赶不上
								success=false;
								break;
							}
							Plan plan=new Plan(obuTime, false, direction);
							planListTemp.add(plan);
						}
						if(success) {
							planList=planListTemp;
						}
					}
				}else {
					int shortLineClasses=scheduleHalfHour.getShortLineSchedule()==null?0:scheduleHalfHour.getShortLineSchedule().getShortLineClasses();
					int classesNumber = busAvailable-shortLineClasses;//设置长线班次
					List<Date> obu_time_queue=getObuTimeList(scheduleHalfHour,classesNumber);//重算长线班次
					scheduleHalfHour.setObuTimeList(obu_time_queue);
					planList=scheduleHalfHour.getPlanList();
				}
			}
		}
		//如果后面一趟加车，这趟车不能赶到，换位
		for(int i=0;i<planList.size()-1;i++) {
			Trip tripNext=bus_queue_to.get(i+1);
			if(tripNext.getBus()==null) {//后面是新加的车
				Trip trip=bus_queue_to.get(i);	
				if(trip.getBus()!=null) {//当前车不是加车
					Date obu_time=planList.get(i).getPlanTime();
					if(trip.getNextObuTimeMin().after(obu_time)) {//当前车可以发这个班次，但不能准点发车
						bus_queue_to.set(i, tripNext);//加车换到前面
						bus_queue_to.set(i+1, trip);
					}
				}
			}
		}
		for(int i=0;i<planList.size();i++) {
			Plan plan=planList.get(i);
			Date obu_time=plan.getPlanTime();
			Trip busTrip=bus_queue_to.get(0);
			/*if(i<planList.size()-1) {
				Date obu_time_next=planList.get(i+1).getPlanTime();
				Trip busTripNext=bus_queue_to.get(1);
				if(busTripNext.getNextObuTimeMin().after(obu_time_next)) {//下一班迟到，拉平均发
					Date date=new Date((tripLast.getTripBeginTime().getTime()+busTripNext.getNextObuTimeMin().getTime())/2);
					calendar=DateUtil.getCalendar(date);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					obu_time=calendar.getTime();
				}
			}*/
			Date arrivalTime=busTrip.getTripEndTime();
			Date minObuTime=busTrip.getNextObuTimeMin();
			if(busTrip.getTripEndTime()!=null&&
					busTrip.getNextObuTimeMin().after(obu_time)) {//到站时间在调度时间后
				obu_time=busTrip.getNextObuTimeMin();
			}
			/*if(tripLast!=null) {
				calendar.setTime(obu_time);
				calendar.add(Calendar.MINUTE, -scheduleParam.getMaxInterval(scheduleHalfHour));
				if(tripLast.getTripBeginTime().before(calendar.getTime())) {
					long difference=calendar.getTimeInMillis()-tripLast.getTripBeginTime().getTime();
					tripLast.setTripBeginTime(calendar.getTime());//前一班发班时间往后推
					tripLast.setTripEndTime(new Date(tripLast.getTripEndTime().getTime()+difference));
					tripLast.setNextObuTimeMin(new Date(tripLast.getNextObuTimeMin().getTime()+difference));
					System.out.println(tripLast.getBusName()+"断位押后\t"+HM_FORMAT.format(tripLast.getTripBeginTime())+"\t"+HM_FORMAT.format(tripLast.getTripEndTime())+"\t"+HM_FORMAT.format(tripLast.getNextObuTimeMin()));
				}
			}*/
			Bus bus=busTrip.getBus();
			if(bus==null) {//新加车
				/*if(tripLast!=null&&i<planList.size()-1) {
					Date obu_time_next=planList.get(i+1).getPlanTime();
					if(obu_time_next.getTime()-obu_time.getTime()>2*(obu_time.getTime()-tripLast.getTripBeginTime().getTime())) {//时间太靠前
						obu_time=new Date((obu_time_next.getTime()+tripLast.getTripBeginTime().getTime())/2);//拉均匀发
					}
				}*/
				int startOrderNumber=routeSchedule.newBusOrder(direction);
				bus=new Bus(direction, startOrderNumber,obu_time);
				minObuTime=null;
			}else{
				routeSchedule.setEaten(bus, busTrip, obu_time, scheduleParam);
			}
			/*if(tripLast!=null) {//上一班次
				Trip tripLast2=tripLast.getLastTrip();//上上班次
				if(tripLast2!=null) {
					long interval=obu_time.getTime()-tripLast.getTripBeginTime().getTime();
					long intervalLast=tripLast.getTripBeginTime().getTime()-tripLast2.getTripBeginTime().getTime();
					if(interval*2>intervalLast*3) {//间隔相差1.5倍以上
						interval=(interval+intervalLast)/2;
						obuTimeLast=new Date(tripLast2.getTripBeginTime().getTime()+interval);
						Trip tripPre=tripLast.getPreTrip();
						if(tripPre!=null) {//非首个任务
							if(obuTimeLast.before(tripPre.getNextObuTimeMin()))//
								obuTimeLast=tripPre.getNextObuTimeMin();
						}
						tripLast.setTripBeginTime(obuTimeLast);
						tripLast.setNextObuTimeMin(scheduleParam);
					}
				}
			}*/
			Trip trip=new Trip(bus, direction, obu_time,scheduleParam,plan.isShortLine(),plan.getLastRouteStaId(),null);
			if(busTrip.getTripBeginTime()==null) {
				trip.setFirstTask(true);
			}else {
				trip.setPreTrip(busTrip);
			}
			Trip lastTrip=routeSchedule.getTripLast(direction);
			trip.setLastTrip(lastTrip);
			bus_queue_to.remove(busTrip);
			routeSchedule.addTrip(trip);
			System.out.println(trip.getBusName()+"\t"+HM_FORMAT.format(trip.getTripBeginTime())+"\t"+(arrivalTime==null?"":HM_FORMAT.format(arrivalTime))+"\t"+(minObuTime==null?"":HM_FORMAT.format(minObuTime)));
			if(plan.isShortLine()) {//把回程暂存到反方向队列
				RouteStaTurn routeStaTurn=scheduleHalfHour.getShortLineSchedule().getRouteStaTurn();
				trip.setLastRouteStaName(routeStaTurn.getLastRouteStaName());
				int directionBack=1-direction;
				if(scheduleParam.isLoopLine()) {
					directionBack=direction;
				}
				Trip tripBack=new Trip(bus, directionBack, DateUtil.getDateAdd(trip.getTripEndTime(), Calendar.MINUTE, scheduleHalfHour.getShortLineSchedule().getRouteStaTurn().getTurnAroundTime()));
				RouteSta lastRouteSta=scheduleParam.getLastRouteSta(directionBack);
				Date tripEndTime=scheduleParam.getArrivalTime(tripBack.getTripBeginTime(), directionBack, routeStaTurn.getNextFirstRouteStaId(), lastRouteSta.getRouteStationId());
				tripBack.setTripEndTime(tripEndTime);
				Date nextObuTimeMin=tripBack.getMinObuTime(scheduleParam);
				tripBack.setNextObuTimeMin(nextObuTimeMin);
				tripBack.setShortLine(true);
				trip.setNextObuTimeMin(nextObuTimeMin);
				routeSchedule.addShortTripBack(tripBack);
				tripBack.setFirstRouteStaId(routeStaTurn.getNextFirstRouteStaId());
				tripBack.setLastRouteStaId(scheduleParam.getLastRouteSta(tripBack.getDirection()).getRouteStationId());
				tripBack.setFirstRouteStaName(routeStaTurn.getNextFirstRouteStaName());
				trip.setTurnTrip(tripBack);//缓存回程
			}
		}
		for(int i=0;i<bus_queue_to.size();) {
			Trip trip=bus_queue_to.get(i);
			if(trip.getBus()==null){
				bus_queue_to.remove(i);//删除没用到的车
			}else {
				i++;
			}
		}
	}
	
	private List<Date> getObuTimeList(ScheduleHalfHour scheduleHalfHour,int classesNumber){
		List<Date> obuTimeList=new ArrayList<Date>();
		Calendar calendar=DateUtil.getCalendarHM(scheduleHalfHour.getRunTime());
		Calendar calendarEnd=DateUtil.getCalendarHM(scheduleHalfHour.getRunTimeEnd());
		while(classesNumber>0) {
			obuTimeList.add(calendar.getTime());
			Double interval=Math.ceil((calendarEnd.getTimeInMillis()-calendar.getTimeInMillis())/60/1000.0/classesNumber);
			calendar.add(Calendar.MINUTE, interval.intValue());
			classesNumber--;
		}
		return obuTimeList;
	}
	
	private Integer getObuTimeInterval(List<Trip> bus_queue_to,Trip lastTrip) {
		Integer interval=null;
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.getTripBeginTime()==null)//复行车
				continue;
			if(trip.getNextObuTimeMin().after(lastTrip.getTripBeginTime())) {
				int intervalTemp=DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), lastTrip.getTripBeginTime())/(i+2);
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
		}
		return interval;
	}
	
	
	
	
	
	private Integer getObuTimeIntervalNew(List<Trip> bus_queue_to,Trip lastTrip) {
		int direction=lastTrip.getDirection();
		Integer interval=null;
		boolean lastRound=false;
		if(bus_queue_to.size()<5) {
			Integer intervalReverse=null;
			Trip lastTripFullReverse=routeSchedule.getTripFullLast(1-direction);
			if(lastTripFullReverse!=null&&lastTripFullReverse.getLastTripFull()!=null) {
				intervalReverse=DateUtil.getMinuteInterval(lastTripFullReverse.getTripBeginTime(), lastTripFullReverse.getLastTripFull().getTripBeginTime());
			}
			List<Trip> bus_queue_to_reverse=routeSchedule.getBusQueue(direction);
			Date obuTimeReverse=lastTripFullReverse.getTripBeginTime();
			Integer leaveImmediately=scheduleParam.getLeaveImmediatelyInterval(1-direction);//对面到站即走
			for(Trip trip:bus_queue_to_reverse) {
				if(!trip.isQuitMark()) {
					if(leaveImmediately!=null) {
						obuTimeReverse=DateUtil.getDateAddMinute(trip.getTripEndTime(), leaveImmediately);
					}else {
						if(intervalReverse==null)
							System.out.println("aaaa");
						obuTimeReverse=DateUtil.getDateAddMinute(obuTimeReverse, intervalReverse);//加间隔
					}
					if(obuTimeReverse.after(scheduleParam.getLatestTime(1-direction))) {//到对面不能再回来
						lastRound=true;
						break;
					}
					Trip tripTemp=new Trip(trip.getBus(), 1-direction, obuTimeReverse);
					Date arrivalTime=scheduleParam.getArrivalTime(tripTemp.getTripBeginTime(), tripTemp.getDirection());
					if(arrivalTime==null)
						break;
					int restTime=scheduleParam.getMinRestTime(arrivalTime, direction);
					tripTemp.setTripEndTime(arrivalTime);
					if(!trip.isEatAfterTrip()) {
						Integer eatTime=scheduleParam.getTime2Eat(tripTemp);
						if(eatTime!=null)
							restTime=eatTime;
					}
					tripTemp.setNextObuTimeMin(DateUtil.getDateAddMinute(arrivalTime, restTime));
					if(tripTemp.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {//回来过了时间
						lastRound=true;
						break;
					}
					bus_queue_to.add(tripTemp);
					if(bus_queue_to.size()==5)
						break;
				}
			}
			if(lastRound) {
				for(int i=bus_queue_to.size()-1;i>=0;i--) {
					Trip tripTemp=bus_queue_to.get(i);
					if(!tripTemp.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
						tripTemp.setNextObuTimeMin(scheduleParam.getLatestTime(direction));
						break;
					}
				}
			}
		}else {
			Trip latestBusTrip=bus_queue_to.get(bus_queue_to.size()-1);
			if(latestBusTrip.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
				List<Trip> busAvailable=new ArrayList<Trip>();
				for(Trip trip:bus_queue_to) {
					if(!trip.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
						busAvailable.add(trip);
					}
				}
				if(busAvailable.isEmpty()) {
					Trip tripNext=bus_queue_to.get(0);
					if(tripNext.getTripEndTime().before(scheduleParam.getLatestTime(direction))) {
						busAvailable.add(tripNext);
					}
				}
				bus_queue_to=busAvailable;
				latestBusTrip=bus_queue_to.get(bus_queue_to.size()-1);
				latestBusTrip.setNextObuTimeMin(scheduleParam.getLatestTime(direction));
				lastRound=true;
			}
			
		}
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.getTripBeginTime()==null)//复行车
				continue;
			if(lastTrip.getTripBeginTime().before(scheduleParam.getLatePeakBeginTime())&&i>5&&interval!=null)
				break;
			if(trip.getNextObuTimeMin().after(lastTrip.getTripBeginTime())&&
					!trip.getNextObuTimeMin().after(scheduleParam.getLatestTime(direction))) {
				int intervalTemp=DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), lastTrip.getTripBeginTime())/(i+1);
				if(interval==null||intervalTemp>interval) {
					interval=intervalTemp;
				}
			}
			if(i==5&&interval!=null)
				break;
		}
		if(!busReverseAdjustBase.isLastCircle(direction)) {
			if(interval!=null&&!lastRound) {
				Date latestTime=scheduleParam.getLatestTime(direction);
				int maxInterval=scheduleParam.getMaxInterval(lastTrip.getTripBeginTime(), direction);
				if(DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval).after(latestTime))
					return interval;
				int maxIntervalNext=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval), direction);
				maxInterval=Math.max(maxInterval, maxIntervalNext);
				if(interval>maxInterval) {
					interval=maxInterval;
					int increment=0;
					for(int i=0;i<10;i++){//最多尝试10次
						Date obuTimeLast=lastTrip.getTripBeginTime();
						boolean fail=false;
						for(int j=0;j<bus_queue_to.size()&&j<6;j++) {
							Trip trip=bus_queue_to.get(j);
							maxInterval=scheduleParam.getMaxInterval(obuTimeLast, direction);
							if(DateUtil.getDateAddMinute(obuTimeLast, maxInterval+increment).after(latestTime))
								break;
							maxIntervalNext=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(obuTimeLast, maxInterval+increment), direction);
							maxInterval=Math.max(maxInterval, maxIntervalNext);
							Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, maxInterval);
							if(trip.getTripEndTime()!=null) {
								if(!trip.getTripEndTime().before(obuTime)) {
									increment++;
									fail=true;
									break;
								}
							}
							if(obuTime.after(latestTime))
								break;
							obuTimeLast=obuTime;
						}
						if(!fail) {
							maxInterval=scheduleParam.getMaxInterval(lastTrip.getTripBeginTime(), direction);
							maxIntervalNext=scheduleParam.getMaxInterval(DateUtil.getDateAddMinute(lastTrip.getTripBeginTime(), interval), direction);
							maxInterval=Math.max(maxInterval, maxIntervalNext);
							interval=maxInterval+increment;
							break;
						}
					}
					
				}
			}
		}
		return interval;
	}
	
	private int getBusNumberEstimate4LoopLine() {
		Date firstTime=scheduleParam.getFirstTime(Direction.UP.getValue());
		Date latestTime=scheduleParam.getLatestTime(Direction.UP.getValue());
		Date runTime=firstTime;
		int busNumber=0;
		while(runTime.before(latestTime)) {
			int turnAroundTime=scheduleParam.getTurnAroundTime(runTime);
			System.out.println(turnAroundTime);
			int maxInterval=scheduleParam.getMaxInterval(DateFormatUtil.HM.getDateString(runTime), Direction.UP.getValue());
			int busNumberTemp=(int)Math.ceil(turnAroundTime*1.0/maxInterval);
			if(busNumberTemp>busNumber) {
				busNumber=busNumberTemp;
			}
			runTime=DateUtil.getDateAddMinute(runTime, 60);
		}
		return busNumber;
	}
	
	private Map<Integer, Integer> getBusNumberEstimate() {
		Date firstTimeUp=scheduleParam.getFirstTime(Direction.UP.getValue());
		Date firstTimeDown=scheduleParam.getFirstTime(Direction.DOWN.getValue());
		Date runTimeBegin=firstTimeUp;
		if(firstTimeDown.after(firstTimeUp)) {
			runTimeBegin=firstTimeDown;
		}
		Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
		Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
		Date runTimeEnd=latestTimeUp;
		if(latestTimeDown.before(latestTimeUp)) {
			runTimeEnd=latestTimeDown;
		}
		Date runTime=runTimeBegin;
		int busNumber=0;
		while(runTime.before(runTimeEnd)) {
			int turnAroundTime=scheduleParam.getTurnAroundTime(runTime);
			System.out.println(turnAroundTime);
			int maxInterval=scheduleParam.getMaxInterval(DateFormatUtil.HM.getDateString(runTime), Direction.UP.getValue());
			int busNumberTemp=(int)Math.ceil(turnAroundTime*1.0/maxInterval);
			if(busNumberTemp>busNumber) {
				busNumber=busNumberTemp;
			}
			runTime=DateUtil.getDateAddMinute(runTime, 60);
		}
		int minBusNumberUp=0;
		int minBusNumberDown=0;
		int direction=Direction.DOWN.getValue();
		Date firstBusArrivalTime=scheduleParam.getArrivalTime(firstTimeDown, direction);
		int minRestTime=scheduleParam.getMinRestTime(firstBusArrivalTime, 1-direction);
		Date minObuTimeNext=DateUtil.getDateAddMinute(firstBusArrivalTime, minRestTime);//下行首班车到对面最早发班时间
		int firstRoundTimeUp=DateUtil.getMinuteInterval(minObuTimeNext, firstTimeUp);
		if(minObuTimeNext.after(firstTimeUp)) {//下行首班车车过去接不上
			Date obuTime=firstTimeUp;
			while(obuTime.before(minObuTimeNext)) {
				minBusNumberUp++;
				obuTime=DateUtil.getDateAddMinute(obuTime, scheduleParam.getMaxInterval(obuTime, 1-direction));
			}
		}
		direction=Direction.UP.getValue();
		firstBusArrivalTime=scheduleParam.getArrivalTime(firstTimeUp, direction);
		minRestTime=scheduleParam.getMinRestTime(firstBusArrivalTime, 1-direction);
		minObuTimeNext=DateUtil.getDateAddMinute(firstBusArrivalTime, minRestTime);//上行首班车到对面最早发班时间
		int firstRoundTimeDown=DateUtil.getMinuteInterval(minObuTimeNext, firstTimeDown);
		if(minObuTimeNext.after(firstTimeDown)) {
			Date obuTime=firstTimeDown;
			while(obuTime.before(minObuTimeNext)) {
				minBusNumberDown++;//下行需要出车数
				obuTime=DateUtil.getDateAddMinute(obuTime, scheduleParam.getMaxInterval(obuTime, 1-direction));
			}
		}
		Map<Integer, Integer> map=new HashMap<Integer, Integer>();
		while(minBusNumberUp+minBusNumberDown<busNumber) {//没够车
			if(firstRoundTimeUp*1.0/minBusNumberUp<firstRoundTimeDown*1.0/minBusNumberDown) {//下行间隔大
				minBusNumberDown++;
			}else {
				minBusNumberUp++;
			}
		}
		map.put(Direction.UP.getValue(), minBusNumberUp);
		map.put(Direction.DOWN.getValue(), minBusNumberDown);
		return map;
	}
	
	private Date getObuTimeNext(Date lastObuTime,int direction) {
		int busNumber=0;
		List<Trip> busQueue=routeSchedule.getBusQueue(Direction.UP.getValue());
		for(Trip trip:busQueue) {
			if(!trip.isQuitMark()) {
				busNumber++;
			}
		}
		busQueue=routeSchedule.getBusQueue(Direction.DOWN.getValue());
		for(Trip trip:busQueue) {
			if(!trip.isQuitMark()) {
				busNumber++;
			}
		}
		if(busNumber==0) {
			System.out.println("aaa");
		}
		int interval=scheduleParam.getTurnAroundTime(lastObuTime)/busNumber;
		int maxInterval=scheduleParam.getMaxInterval(lastObuTime, direction);
		if(interval>maxInterval) {
			Date arrivalTime=scheduleParam.getArrivalTime(lastObuTime, direction);
			int restTime=scheduleParam.getMinRestTime(arrivalTime, 1-direction);
			if(arrivalTime.before(scheduleParam.getLatestTime(1-direction))) {
				Date arrivalTimeTemp=scheduleParam.getArrivalTime(DateUtil.getDateAddMinute(arrivalTime, restTime), 1-direction);
				if(arrivalTimeTemp==null){
					arrivalTimeTemp=scheduleParam.getArrivalTime(scheduleParam.getLatestTime(1-direction), 1-direction);
				}
				arrivalTime=arrivalTimeTemp;
				restTime=scheduleParam.getMinRestTime(arrivalTime, direction);
				interval=DateUtil.getMinuteInterval(lastObuTime, DateUtil.getDateAddMinute(arrivalTime, restTime))/busNumber;
				if(interval<maxInterval) {
					interval=maxInterval;
				}
			}
		}
		if(interval%5!=0) {
			interval=(interval/5+1)*5;
		}
		Date obuTimeNext=DateUtil.getDateAddMinute(lastObuTime, interval);
		return obuTimeNext;
	}
	
	private int getMinDoubleClassesBusNumber() {
		List<ScheduleParamsClasses> paramsClassesList=scheduleParam.getScheduleParamsClassesList();
		int doubleClassesBusNumber=0;
		for(ScheduleParamsClasses paramsClasses:paramsClassesList) {
			if(paramsClasses.getDirection().equals(Direction.UP.getStringValue())) {
				Date runTimeEnd=DateUtil.getDateHM(paramsClasses.getEndTime());
				Date runTime=DateUtil.getDateHM(paramsClasses.getBeginTime());
				if(!paramsClasses.getBeginTime().endsWith("00")) {
					Calendar calendar=DateUtil.getCalendar(runTime);
					calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY+1));
					calendar.set(Calendar.MINUTE, 0);
				}
				while(runTime.before(runTimeEnd)) {
					if(scheduleParam.getMiddleStopBeginTime()!=null&&
							runTime.before(scheduleParam.getMiddleStopBeginTime())) {
						runTime=DateUtil.getDateAddMinute(runTime, 60);
						continue;
					}
					Date obuTimeNext=scheduleParam.getObuTimeNextRound(Direction.UP.getValue(), runTime);
					if(obuTimeNext==null||obuTimeNext.after(scheduleParam.getMiddleStopRecoveryBeginTime())) {
						break;
					}
					int interval=DateUtil.getMinuteInterval(runTime, obuTimeNext);
					int busNumber=(int)Math.ceil(interval*1.0*paramsClasses.getClassesNumMin()/60);
					if(busNumber>doubleClassesBusNumber) {
						doubleClassesBusNumber=busNumber;
					}
					runTime=DateUtil.getDateAddMinute(runTime, 60);
				}
			}
		}
		return doubleClassesBusNumber;
	}
	
	private void calculateByHour(){
		int busNumberUp=0;
		int busNumberDown=0;
		if(!morningShiftProcessed&&scheduleParam.getShiftListPreset()!=null) {//没处理过早班，把早班加入
			for(ScheduleShiftPreset shift:scheduleParam.getShiftListPreset()) {
				if(shift.getShiftType()==ShiftType.MORNING_SHIFT.getValue()) {
					if(scheduleParam.getBusNumberPresetUp()!=null) {
						if(shift.getBusNumberUp()!=null&&shift.getBusNumberUp()!=0) {
							ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.UP.getValue(), scheduleParam.getFirstTime(Direction.UP.getValue()));
							if(!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(scheduleParam.getFirstTime(Direction.UP.getValue()))) {
								scheduleParam.setBusNumberPreset(Direction.UP.getValue(),scheduleParam.getBusNumberPresetUp()+shift.getBusNumberUp());
							}
						}
						if(shift.getBusNumberDown()!=null&&shift.getBusNumberDown()!=0) {
							ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.DOWN.getValue(), scheduleParam.getFirstTime(Direction.DOWN.getValue()));
							if(!DateUtil.getDateHM(scheduleParamShift.getStartTime()).after(scheduleParam.getFirstTime(Direction.DOWN.getValue()))) {
								scheduleParam.setBusNumberPreset(Direction.DOWN.getValue(),scheduleParam.getBusNumberPresetDown()+shift.getBusNumberDown());
							}
						}
						int busNumberPresetUp=scheduleParam.getBusNumberPresetUp();
						int busNumberPresetDown=scheduleParam.getBusNumberPresetDown();
						scheduleParam.setBusNumberPreset(busNumberPresetUp+busNumberPresetDown);
						morningShiftProcessed=true;
					}
				}
			}
		}
		Integer busNumber=scheduleParam.getBusNumberPreset();
		if(scheduleParam.getSchedulePlanReference()!=null) {//参照历史出厂时间排班
			Map<Integer,List<Trip>> firstRoundTripMap=getFirstRoundTripMap(scheduleParam.getSchedulePlanReference());
			List<Trip> firstRoundTripListUp=firstRoundTripMap.get(Direction.UP.getValue());
			if(firstRoundTripListUp!=null)
				busNumberUp=firstRoundTripListUp.size();
			List<Trip> firstRoundTripListDown=firstRoundTripMap.get(Direction.DOWN.getValue());
			if(firstRoundTripListDown!=null)
				busNumberDown=firstRoundTripListDown.size();
			busNumber=busNumberUp+busNumberDown;
			Integer busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
			if(firstRoundTripListUp!=null) {
				if(busNumberSingle!=null&&busNumberSingle!=0)
					setSingleBus(firstRoundTripListUp, busNumberSingle);
				Collections.sort(firstRoundTripListUp, new MinObuTimeComparator());
				routeSchedule.getBusQueue(Direction.DOWN.getValue()).addAll(firstRoundTripListUp);
			}
			busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());
			if(firstRoundTripListDown!=null) {
				if(busNumberSingle!=null&&busNumberSingle!=0)
					setSingleBus(firstRoundTripListDown, busNumberSingle);
				Collections.sort(firstRoundTripListDown, new MinObuTimeComparator());
				routeSchedule.getBusQueue(Direction.UP.getValue()).addAll(firstRoundTripListDown);
			}
		}else{
			if(busNumber==null) {
				Map<Integer,Integer> map=getBusNumberEstimate();
				busNumberUp=map.get(Direction.UP.getValue());
				busNumberDown=map.get(Direction.DOWN.getValue());
				if(!scheduleParam.isAppearDirection(Direction.UP.getValue())) {
					busNumberDown+=busNumberUp;
					busNumberUp=0;
				}if(!scheduleParam.isAppearDirection(Direction.DOWN.getValue())) {
					busNumberUp+=busNumberDown;
					busNumberDown=0;
				}
				busNumber=busNumberUp+busNumberDown;
				int doubleClassesBusNumber=getMinDoubleClassesBusNumber();
				if(doubleClassesBusNumber!=0) {
					scheduleParam.setBusNumberSingle(busNumber-doubleClassesBusNumber);
				}
			}else {
				if(scheduleParam.getBusNumberPresetUp()==null) {//按预设总车数排
					Map<Integer,Integer> map=getBusNumberEstimate();
					busNumberUp=map.get(Direction.UP.getValue());
					busNumberDown=map.get(Direction.DOWN.getValue());
					busNumberUp=(int)((busNumberUp*1.0/(busNumberUp+busNumberDown))*busNumber);
					busNumberDown=busNumber-busNumberUp;
				}else {
					busNumberUp=scheduleParam.getBusNumberPresetUp();
					busNumberDown=scheduleParam.getBusNumberPresetDown();
				}
			}
			Date firstTimeUp=scheduleParam.getFirstTime(Direction.UP.getValue());
			Date firstTimeDown=scheduleParam.getFirstTime(Direction.DOWN.getValue());
			Map<Integer, List<Trip>> firstRoundTripMap=new HashMap<Integer, List<Trip>>();
			if(busNumberDown!=0) {
				Date arrivalTime=scheduleParam.getArrivalTime(firstTimeUp, Direction.UP.getValue());
				int restTime=scheduleParam.getMinRestTime(arrivalTime, Direction.DOWN.getValue());
				int wasteTime=DateUtil.getMinuteInterval(firstTimeDown, arrivalTime)+restTime;
				Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(Direction.DOWN.getValue());
				List<Trip> firstTripList=new ArrayList<Trip>();
				for(int i=0;i<busNumberDown;i++) {
					Bus bus=new Bus(Direction.DOWN.getValue(), routeSchedule.newBusOrder(Direction.DOWN.getValue()));
					Trip trip=new Trip();
					trip.setBus(bus);
					firstTripList.add(trip);
					trip.setNextObuTimeMin(DateUtil.getDateAddMinute(firstTimeDown, wasteTime*i/busNumberDown));
					if(firstPlanObuTimeLatest!=null&&trip.getNextObuTimeMin().after(firstPlanObuTimeLatest))
						trip.setNextObuTimeMin(firstPlanObuTimeLatest);
					routeSchedule.getBusQueue(Direction.UP.getValue()).add(trip);
				}
				firstRoundTripMap.put(Direction.DOWN.getValue(), routeSchedule.getBusQueue(Direction.UP.getValue()));
			}
			if(busNumberUp!=0) {
				Date arrivalTime=scheduleParam.getArrivalTime(firstTimeDown, Direction.DOWN.getValue());
				int restTime=scheduleParam.getMinRestTime(arrivalTime, Direction.UP.getValue());
				int wasteTime=DateUtil.getMinuteInterval(firstTimeUp, arrivalTime)+restTime;
				Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(Direction.UP.getValue());
				List<Trip> firstTripList=new ArrayList<Trip>();
				for(int i=0;i<busNumberUp;i++) {
					Bus bus=new Bus(Direction.UP.getValue(), routeSchedule.newBusOrder(Direction.UP.getValue()));
					Trip trip=new Trip();
					trip.setBus(bus);
					firstTripList.add(trip);
					trip.setNextObuTimeMin(DateUtil.getDateAddMinute(firstTimeUp, wasteTime*i/busNumberUp));
					if(firstPlanObuTimeLatest!=null&&trip.getNextObuTimeMin().after(firstPlanObuTimeLatest))
						trip.setNextObuTimeMin(firstPlanObuTimeLatest);
					routeSchedule.getBusQueue(Direction.DOWN.getValue()).add(trip);
				}
				firstRoundTripMap.put(Direction.UP.getValue(), routeSchedule.getBusQueue(Direction.DOWN.getValue()));
			}
			if(scheduleParam.getShiftListPreset()!=null) {//初始化班别
				initTrip4Shift(firstRoundTripMap);
			}
			Integer busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.DOWN.getValue());  
			if(busNumberSingle!=null&&busNumberSingle!=0&&!scheduleParam.getRouteStaTurnList().isEmpty()) {
				List<Trip> firstTripList=firstRoundTripMap.get(Direction.DOWN.getValue());
				Collections.sort(firstTripList, new MinObuTimeComparator());
				setSingleBus(firstTripList, busNumberSingle);
			}
			busNumberSingle=scheduleParam.getBusNumberSinglePreset(Direction.UP.getValue());
			if(busNumberSingle!=null&&busNumberSingle!=0&&!scheduleParam.getRouteStaTurnList().isEmpty()) {
				List<Trip> firstTripList=firstRoundTripMap.get(Direction.UP.getValue());
				Collections.sort(firstTripList, new MinObuTimeComparator());
				setSingleBus(firstTripList, busNumberSingle);
			}
			if(scheduleParam.getBusNumberPreset()==null) {//最优排班
				setSingleBus(firstRoundTripMap,scheduleParam.getBusNumberSingle());
			}
		}
		Integer direction;
		Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
		boolean upEnd=false;
		boolean downEnd=false;
		Bus latestBusUp=null;
		Bus latestBusDown=null;
		while(true) {
			Trip lastTripUp=routeSchedule.getTripLast(Direction.UP.getValue());
			direction=null;
			if(upEnd&&downEnd) {
				break;
			}
			Integer intervalUp=scheduleParam.getLeaveImmediatelyInterval(Direction.UP.getValue());
			Integer intervalDown=scheduleParam.getLeaveImmediatelyInterval(Direction.DOWN.getValue());
			if(intervalUp!=null) {//上行到站即走，发下行就可以
				if(downEnd) {//下行已发完，发上行，防止上行晚收车
					direction=Direction.UP.getValue();
				}else {
					direction=Direction.DOWN.getValue();
				}
			}
			if(intervalDown!=null) {//下行到站即走，发上行就可以
				direction=Direction.UP.getValue();
				List<Trip> bus_to_queue=routeSchedule.getBusQueue(1-direction);
				if(bus_to_queue.isEmpty()) {//车都从下行出场
					bus_to_queue=routeSchedule.getBusQueue(direction);
					direction=Direction.DOWN.getValue();
					if(bus_to_queue.isEmpty())
						break;
					for(Trip tripArrival:bus_to_queue) {
						Trip trip=new Trip(tripArrival.getBus(), direction, tripArrival.getNextObuTimeMin(), scheduleParam, routeSchedule.getTripFullLast(direction));
						routeSchedule.addTrip(trip);
					}
					bus_to_queue.clear();
					continue;
				}
			}
			if(direction==null) {
				direction=Direction.UP.getValue();
				if(!downEnd) {
					if(lastTripUp!=null&&!lastTripUp.getTripBeginTime().before(latestTimeUp)) {//上行已发完
						direction=Direction.DOWN.getValue();
					}else {
						List<Trip> busToQueueUp=routeSchedule.getBusQueue(Direction.UP.getValue());
						List<Trip> busToQueueDown=routeSchedule.getBusQueue(Direction.DOWN.getValue());
						if(busToQueueDown.isEmpty()) {//下行没车
							direction=Direction.DOWN.getValue();
						}else {
							if(!busToQueueUp.isEmpty()) {
								Trip tripArrival4Up=busToQueueDown.get(0);
								Trip tripArrival4Down=busToQueueUp.get(0);
								if(tripArrival4Up.getNextObuTimeMin().after(tripArrival4Down.getNextObuTimeMin())) {//上行车先到，发下行
									direction=Direction.DOWN.getValue();
								}
							}
						}
					}
				}
			}
			
			Trip tripLastReverse=routeSchedule.getTripLast(1-direction);
			List<Trip> bus_to_queue=routeSchedule.getBusQueue(1-direction);
			if(tripLastReverse!=null&&tripLastReverse.getTripBeginTime().equals(scheduleParam.getLatestTime(1-direction))) {//对面末班车已发
				Date latestTime=scheduleParam.getLatestTime(direction);
				for(int i=bus_to_queue.size()-1;i>=0;i--) {
					Trip tripArrival=bus_to_queue.get(i);
					if(!tripArrival.getNextObuTimeMin().after(latestTime)) {
						tripArrival.setNextObuTimeMin(latestTime);
						break;
					}
				}
			}
			Trip tripLast=routeSchedule.getTripFullLast(direction);
			if(tripLast==null) {
				if(intervalUp!=null||intervalDown!=null) {//到站即走
					bus_to_queue=routeSchedule.getBusQueue(direction);
					if(!bus_to_queue.isEmpty()) {
						while(!bus_to_queue.isEmpty()) {//把车开到非到站即走总站先
							Trip tripArraval=bus_to_queue.get(0);
							Trip trip=new Trip(tripArraval.getBus(), 1-direction, tripArraval.getNextObuTimeMin(), scheduleParam, tripLast);
							routeSchedule.addTrip(trip);
							trip.setLastTrip(tripLast);
							bus_to_queue.remove(tripArraval);
							tripLast=trip;
						}
						continue;
					}else {
						bus_to_queue=routeSchedule.getBusQueue(1-direction);
					}
				}
			}
			if(bus_to_queue.isEmpty())
				break;
			if(tripLast!=null) {
				Calendar calendar=DateUtil.getCalendar(tripLast.getTripBeginTime());
				if(calendar.get(Calendar.MINUTE)<30)
					calendar.set(Calendar.MINUTE, 0);
				else {
					calendar.set(Calendar.MINUTE, 30);
				}
				Date runTime=calendar.getTime();
				calendar.add(Calendar.MINUTE, 30);
				Date runTimeEnd=calendar.getTime();
				ScheduleHalfHour scheduleHalfHour=new ScheduleHalfHour(DateFormatUtil.HM.getDateString(runTime), DateFormatUtil.HM.getDateString(runTimeEnd), direction);
				setQuitMark(scheduleHalfHour, bus_to_queue);
				if(!runTime.before(DateUtil.getDateAdd(scheduleParam.getMiddleStopRecoveryBeginTime(), Calendar.MINUTE, -60))) {//准备单班复行
					singleClassRecovery(tripLast, bus_to_queue);
				}
			}
			Trip tripArrival=bus_to_queue.get(0);
			if(tripArrival.isQuitMark()) {
				bus_to_queue.remove(tripArrival);
				Bus bus=tripArrival.getBus();
				if(bus.isSingleClass()) {
					if(!bus.isHasMiddleStop()) {//中停
						bus.setHasMiddleStop(true);
						routeSchedule.singleClassBusAdd(tripArrival, direction);
					}
				}else if(bus.getShiftType()!=null) {//设置晚班
					ScheduleParamShift shift=bus.getShiftType();
					Trip tripRecovery=scheduleParam.busCheckIn(bus, shift.getShiftType(),DateUtil.getDateHM(shift.getEndTime()));
					if(tripRecovery!=null) {
						if(!tripRecovery.getNextObuTimeMin().after(tripArrival.getNextObuTimeMin())) {//到达时间超过复行时间，不退出
							tripArrival.setQuitMark(false);
						}else {
							routeSchedule.addShortTripBack(tripRecovery);
						}
					}
				}
				if(tripArrival.isQuitMark()) {
					bus_to_queue.remove(tripArrival);
					continue;
				}
			}
			Date latestTime=scheduleParam.getLatestTime(direction);
			Bus bus=tripArrival.getBus();
			if(tripArrival.getTripEndTime()!=null&&tripArrival.getTripEndTime().equals(DateUtil.getDateHM("2043")))
				System.out.println("aaa");
			Date obuTime=null;
			if(tripLast==null) {//头车
				obuTime=scheduleParam.getFirstTime(direction);
			}else {
				if(DateUtil.getDateHM("2100").equals(tripArrival.getTripEndTime())) {
					System.out.println("aaa");
				}
				if(bus_to_queue.size()>1) {
					Date minObuTimeNextLap=scheduleParam.getMinObuTimeNextLap(tripLast);
					if(minObuTimeNextLap==null||minObuTimeNextLap.after(latestTime)) {//上台车过来做不了尾车,最后一轮
						Bus latestBus=latestBusUp;
						if(direction==Direction.DOWN.getValue()) {
							latestBus=latestBusDown;
						}
						Integer interval=null;
						if(latestBus==null) {
							Trip latestBusTrip=null;
							List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(direction);
							for(Trip trip:busQueue) {
								minObuTimeNextLap=scheduleParam.getMinObuTimeNextLap(trip);
								if(minObuTimeNextLap==null||minObuTimeNextLap.after(latestTime)) {
									break;
								}
								latestBusTrip=trip;
							}
							if(latestBusTrip==null) {
								busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(1-direction);
								for(Trip trip:busQueue) {
									if(trip.getNextObuTimeMin().after(latestTime)) {
										break;
									}
									latestBusTrip=trip;
								}
								if(latestBusTrip==null) {//只要末班车前到都可以做尾车
									for(Trip trip:busQueue) {
										if(trip.getTripEndTime().after(latestTime)) {
											break;
										}
										latestBusTrip=trip;
									}
								}
							}
							int busRunLastLap=0;
							busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(1-direction);
							List<Trip> busQueueReverse=routeSchedule.getBusQueueDoubleAndEveningClasses(direction);
							busQueue.addAll(busQueueReverse);
							for(Trip trip:busQueue) {
								busRunLastLap++;
								if(trip==latestBusTrip) {
									break;
								}
							}
							if(busRunLastLap==0) {
								busQueue=routeSchedule.getBusQueue(direction);
								for(Trip trip:busQueue) {
									minObuTimeNextLap=scheduleParam.getMinObuTimeNextLap(trip);
									if(minObuTimeNextLap==null||minObuTimeNextLap.after(latestTime)) {
										break;
									}
									latestBusTrip=trip;
								}
								if(latestBusTrip==null) {
									busQueue=routeSchedule.getBusQueue(1-direction);
									for(Trip trip:busQueue) {
										if(trip.getNextObuTimeMin().after(latestTime)) {
											break;
										}
										latestBusTrip=trip;
									}
								}
								if(latestBusTrip!=null) {
									busQueue=routeSchedule.getBusQueue(1-direction);
									busQueueReverse=routeSchedule.getBusQueue(direction);
									busQueue.addAll(busQueueReverse);
									for(Trip trip:busQueue) {
										busRunLastLap++;
										if(trip==latestBusTrip) {
											break;
										}
									}
								}
							}
							interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busRunLastLap;
							int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
							if(interval>maxInterval) {//大于最小发班间隔，需要增加车次
								busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(1-direction);
								Trip tripNext=null;
								int index=busQueue.indexOf(latestBusTrip);
								if(index!=-1) {
									if(index<busQueue.size()-1) {
										tripNext=busQueue.get(index+1);
										if(tripNext.getTripEndTime()==null||tripNext.getTripEndTime().before(latestTime)){
											latestBusTrip=tripNext;
										}
									}
									if(tripNext==null) {
										busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(direction);
										if(!busQueue.isEmpty()) {
											index=busQueue.indexOf(latestBusTrip);
											if(index!=-1) {
												if(index<busQueue.size()-1) {
													tripNext=busQueue.get(index+1);
												}
											}else {
												tripNext=busQueue.get(0);
											}
											if(tripNext!=null) {
												Date arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, tripNext.getTripBeginTime());
												if(arrivalTime!=null&&arrivalTime.before(latestTime)) {
													latestBusTrip=tripNext;
												}
											}
										}
									}
									/*if(tripNext!=null) {
										if(!tripNext.getTripEndTime().after(latestTime)){
											latestBusTrip=tripNext;
										}
									}*/
								}
							}
							if(latestBusTrip==null) {
								Trip lastTripFull=routeSchedule.getTripFullLast(direction);
								lastTripFull.setTripBeginTime(latestTime);
								lastTripFull.setTripEndTime(scheduleParam.getArrivalTime(latestTime, direction));
								if(direction==Direction.UP.getValue()) {
									upEnd=true;
								}else {
									downEnd=true;
								}
								continue;
							}
							if(direction==Direction.UP.getValue()) {
								latestBusUp=latestBusTrip.getBus();
							}else {
								latestBusDown=latestBusTrip.getBus();
							}
							latestBus=latestBusTrip.getBus();
						}else {
							int busRunLastLap=0;
							List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses(1-direction);
							List<Trip> busQueueReverse=routeSchedule.getBusQueueDoubleAndEveningClasses(direction);
							busQueue.addAll(busQueueReverse);
							for(Trip trip:busQueue) {
								busRunLastLap++;
								if(trip.getBus()==latestBus) {
									break;
								}
							}
							if(busRunLastLap==0) {
								busQueue=routeSchedule.getBusQueue(1-direction);
								busQueueReverse=routeSchedule.getBusQueue(direction);
								busQueue.addAll(busQueueReverse);
								for(Trip trip:busQueue) {
									busRunLastLap++;
									if(trip.getBus()==latestBus) {
										break;
									}
								}
							}
							interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busRunLastLap;
						}
						obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
					}else {
						Integer interval=null;
						if(tripArrival.getBus().getStartOrderNumber()==5) {
							System.out.println("aaa");
						}
						List<Trip> busAvailable=new ArrayList<Trip>();
						for(Trip trip:bus_to_queue) {
							if(trip.isQuitMark()||trip.getShortLineAdjust()!=null) {
								continue;
							}
							busAvailable.add(trip);
						}
						if(busAvailable.size()<6) {//防止最后一台车退出造成断位
							List<Trip> busQueue=routeSchedule.getBusQueue(direction);
							for(Trip trip:busQueue) {
								if(!trip.isQuitMark()) {
									Trip tripTemp=new Trip(trip.getBus(), 1-direction, null);
									Date minObuTimeNext=scheduleParam.getMinObuTimeNextLap(trip);
									if(minObuTimeNext!=null) {
										tripTemp.setNextObuTimeMin(minObuTimeNext);
										if(!isQuitAfterTrip(tripTemp))
											busAvailable.add(tripTemp);
									}
									if(busAvailable.size()==6)
										break;
								}
							}
						}
						for(int i=0;i<busAvailable.size();i++) {
							Trip trip=busAvailable.get(i);
							if(!trip.getNextObuTimeMin().after(latestTime)&&
									trip.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
								int intervalTemp=DateUtil.getMinuteInterval(trip.getNextObuTimeMin(), tripLast.getTripBeginTime())/(i+1);
								if(interval==null||intervalTemp>interval) {
									interval=intervalTemp;
								}
							}
						}
						if(tripArrival.getNextObuTimeMin().equals(latestTime)) {
							obuTime=latestTime;
						}else {
							if(tripArrival.getShortLineAdjust()!=null) {
								Trip tripLast2=tripLast.getLastTrip();
								if(!tripLast2.isShortLine()) {
									int intervalLast=DateUtil.getMinuteInterval(tripLast2.getTripBeginTime(), tripLast.getTripBeginTime());
									obuTime=DateUtil.getDateAddMinute(tripLast2.getTripBeginTime(), intervalLast*2/3);
									Date minObuTime=tripArrival.getNextObuTimeMin();
									if(tripArrival.getTripEndTime()!=null) {
										minObuTime=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 3);
									}
									if(minObuTime.after(obuTime)) {
										obuTime=null;
									}else {
										obuTime=DateUtil.getDateAddMinute(tripLast2.getTripBeginTime(), intervalLast/2);
										if(minObuTime.after(obuTime)) {
											obuTime=minObuTime;
										}
									}
								}
							}
							if(obuTime==null) {
								if(interval!=null) {
									if(tripArrival.getShortLineAdjust()!=null)
										interval=interval/2;
									obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
								}else {
									obuTime=getObuTimeNext(tripLast.getTripBeginTime(),direction);
								}
							}
						}
					}
				}else {//只有一台车
					if(latestTime.equals(tripLast.getTripBeginTime()))
						System.out.println("aaa");
					obuTime=getObuTimeNext(tripLast.getTripBeginTime(),direction);
				}
			}
			if(obuTime.before(tripArrival.getNextObuTimeMin())) {
				if(tripLast==null) {//头车
					if(tripArrival.getTripEndTime()!=null) {//对面过来的车
						if(tripArrival.getTripEndTime().after(scheduleParam.getFirstTime(direction))) {//比首班车时间晚到
							obuTime=tripArrival.getTripEndTime();//到站即走
						}
					}
				}else {
					//obuTime=tripArrival.getNextObuTimeMin();
					if(tripArrival.getTripEndTime()!=null) {
						if(tripArrival.isEatAfterTrip()) {
							obuTime=tripArrival.getNextObuTimeMin();
						}else {
							Date obuTimeMin=DateUtil.getDateAddMinute(tripArrival.getTripEndTime(), 3);
							if(obuTime.before(obuTimeMin))
								obuTime=obuTimeMin;
						}
					}else {
						Date firstPlanObuTimeLatest=scheduleParam.getFirstPlanObuTimeLatest(direction);
						if(firstPlanObuTimeLatest==null||tripArrival.getNextObuTimeMin().after(firstPlanObuTimeLatest)) {
							obuTime=tripArrival.getNextObuTimeMin();
						}
					}
				}
			}
			if(tripArrival.getTripEndTime()==null) {
				System.out.println("aa");
			}
			if(scheduleParam.isTestLineFull()&&!obuTime.before(scheduleParam.getEarlyPeakEndTime())) {
				busReverseAdjustBase=new BusReverseAdjust4Full(scheduleParam, routeSchedule);
				calculateAfterEarlyRushTime();
				return;
			}
			if((scheduleParam.getRouteId()==363L||scheduleParam.getRouteId()==327L||scheduleParam.getRouteId()==8430L)&&!obuTime.before(scheduleParam.getEarlyPeakEndTime())) {
				ScheduleNormalTime scheduleNormalTime=new ScheduleNormalTime(routeSchedule, scheduleParam);
				scheduleNormalTime.calculate();
				return;
			}
			bus_to_queue.remove(tripArrival);
			if(bus.getShiftType()!=null&&!obuTime.before(DateUtil.getDateHM(bus.getShiftType().getEndTime()))) {//过了下班时间不再发短线
				if(scheduleParam.isEndDirectionTrip(tripArrival)) {
					tripArrival.setQuitMark(true);
					continue;
				}
			}
			if(obuTime.equals(latestTime))
				System.out.println("aaa");
			if(!obuTime.before(latestTime)) {
				if(direction==Direction.UP.getValue()) {
					upEnd=true;
				}else if(direction==Direction.DOWN.getValue()) {
					downEnd=true;
				}
				if(obuTime.after(latestTime))
					continue;
			}
			if(scheduleParam.getSchedulePlanReference()!=null&&tripArrival.getTripEndTime()==null) {//按历史出车日期排班
				List<Trip> busTripList=routeSchedule.getTripList(tripArrival.getBus());
				if(busTripList==null||busTripList.isEmpty()) {
					if(obuTime.before(tripArrival.getNextObuTimeMin())) {
						obuTime=tripArrival.getNextObuTimeMin();
					}
				}
			}
			routeSchedule.setEaten(bus, tripArrival, obuTime, scheduleParam);
			Trip trip=null;
			if(tripArrival.getShortLineAdjust()==null) {
				trip=new Trip(bus, direction, obuTime, scheduleParam, null);
				Integer latestBusDirection=null;
				if(bus==latestBusUp) {
					latestBusDirection=Direction.UP.getValue();
				}else if(bus==latestBusDown) {
					latestBusDirection=Direction.DOWN.getValue();
				}
				if(latestBusDirection!=null&&direction!=latestBusDirection) {//过去做尾车
					if(!trip.getTripEndTime().before(scheduleParam.getLatestTime(latestBusDirection))) {
						int diff=DateUtil.getMinuteInterval(trip.getTripEndTime(), scheduleParam.getLatestTime(latestBusDirection));
						obuTime=DateUtil.getDateAddMinute(obuTime, -diff-1);
						if(obuTime.after(tripLast.getTripBeginTime())) {
							trip.setTripBeginTime(obuTime);
							trip.setTripEndTime(DateUtil.getDateAddMinute(trip.getTripEndTime(), -diff-1));
						}
					}
				}
			}else {
				trip=getTrip4ShortLine(tripArrival.getShortLineAdjust(), bus, obuTime);
				Trip tripBack=trip.getTurnTrip();
				tripBack.setQuitMark(true);
				routeSchedule.addShortTripBack(tripBack);
			}		
			trip.setLastTrip(tripLast);
			if(tripLast!=null)
				tripLast.setNextTrip(trip);
			System.out.println(bus.getBusName()+"\t"+direction+"\t"+obuTime);
			Integer interval=scheduleParam.getLeaveImmediatelyInterval(1-trip.getDirection());//到站即走
			routeSchedule.addTrip(trip);
			if(tripArrival.getTripEndTime()==null) {
				addMiddleStopTrip(trip);
			}
			if(bus.getShiftType()!=null) {
				if(scheduleParam.isEndDirectionTrip(trip)) {//方向一致
					Date arrivalTime=scheduleParam.getObuTimeNextRound(direction, trip.getNextObuTimeMin());
					Date endTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
					if(arrivalTime!=null&&arrivalTime.after(DateUtil.getDateAddMinute(endTime, 30))) {//再回来时间超下班时间过半小时
						RouteStaTurn routeStaTurn=getShortLineAdjust(tripArrival.getDirection(), bus, trip.getNextObuTimeMin(), endTime);
						if(tripLast!=null&&tripLast.getShortLineAdjust()==null)
							trip.setShortLineAdjust(routeStaTurn);
					}
				}
			}
			if(interval!=null) {//到站即走
				List<Trip> busQueueTo=routeSchedule.getBusQueue(trip.getDirection());
				busQueueTo.remove(trip);
				Trip tripRecovery=routeSchedule.singleClassBusPeek(1-trip.getDirection());
				if(tripRecovery!=null) {//单班复行
					tripLast=routeSchedule.getTripFullLast(1-trip.getDirection());
					obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), interval);
					obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), obuTime)/2);
					if(!obuTime.before(scheduleParam.getMiddleStopRecoveryBeginTime())) {
						tripRecovery=new Trip(tripRecovery.getBus(), 1-trip.getDirection(), obuTime, scheduleParam, routeSchedule.getTripFullLast(1-trip.getDirection()));//把车开回去先
						routeSchedule.addTrip(tripRecovery);
						routeSchedule.singleClassBuspoll(1-trip.getDirection());
						addMiddleStopTrip(tripRecovery);
					}
				}
				if(!trip.isQuitMark()) {
					if(!busQueueTo.isEmpty()) {
						for(Trip tripArrivalTemp:busQueueTo) {
							Trip tripBack=new Trip(tripArrivalTemp.getBus(), 1-trip.getDirection(), tripArrivalTemp.getNextObuTimeMin(), scheduleParam, routeSchedule.getTripFullLast(1-trip.getDirection()));//把头车开回去先
							routeSchedule.addTrip(tripBack);
						}
						busQueueTo.clear();
					}
					obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), interval);
					latestTime=scheduleParam.getLatestTime(1-trip.getDirection());
					if(obuTime.after(latestTime)) {
						tripLast=routeSchedule.getTripFullLast(1-trip.getDirection());
						if(!tripLast.getTripBeginTime().equals(latestTime)) {
							tripLast.setTripBeginTime(latestTime);
							tripLast.setTripEndTime(scheduleParam.getArrivalTime(latestTime, 1-trip.getDirection()));
							tripLast.setNextObuTimeMin(scheduleParam);
						}
						continue;
					}
					
					if(obuTime.before(scheduleParam.getFirstTime(1-trip.getDirection()))){//到站即走比首班车早
						List<Trip> busQueue=routeSchedule.getBusQueue(1-trip.getDirection());
						if(!busQueue.isEmpty()) {
							Trip tripNext=busQueue.get(0);
							Date arrivalTimeNext=scheduleParam.getMinObuTimeNext(direction, tripNext.getNextObuTimeMin());
							if(arrivalTimeNext.after(scheduleParam.getFirstTime(1-trip.getDirection()))) {//下一台车赶不上首班车
								obuTime=scheduleParam.getFirstTime(1-trip.getDirection());
							}
						}else {
							obuTime=scheduleParam.getFirstTime(1-trip.getDirection());
						}
					}
					Trip tripBack=new Trip(bus, 1-trip.getDirection(), obuTime, scheduleParam, routeSchedule.getTripFullLast(1-trip.getDirection()));
					routeSchedule.addTrip(tripBack);
				}else {
					if(bus.isSingleClass()) {
						if(!bus.isHasMiddleStop()) {
							routeSchedule.singleClassBusAdd(trip, 1-trip.getDirection());
							bus.setHasMiddleStop(true);
						}
					}
				}
			}
		}	
		busTripListSort();
		routeSchedule.tripListSort();
		procLatestTrip(Direction.UP.getValue());
		routeSchedule.tripListSort();
		procLatestTrip(Direction.DOWN.getValue());
	}
	
	private Date getMinObuTime4Supper(int direction) {
		if(scheduleParam.getSupperEatTime(direction)==null)//不在这边吃饭
			return null;
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		List<Trip> busAvailable=new ArrayList<Trip>();
		for(int i=0;i<busQueueTo.size();i++) {
			Trip tripArrival=busQueueTo.get(i);
			if(!tripArrival.isQuitMark()) {
				busAvailable.add(tripArrival);
			}
		}
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		for(int i=0;i<busQueueFrom.size();i++) {
			Trip trip=busQueueFrom.get(i);
			if(!trip.isQuitMark()) {
				if(trip.getBus().isSingleClass()) {
					Date arrivalTime=null;
					if(trip.getTripBeginTime()!=null) {
						arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, trip.getTripBeginTime());//回来的时间
					}else {
						arrivalTime=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), 1-direction);//对面过来到达时间
					}
					if(arrivalTime!=null) {
						arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, arrivalTime);//再回来的时间，看回来能不能放长线
						if(arrivalTime!=null) {
							if(!arrivalTime.before(scheduleParam.getMiddleStopOffDutyBeginTime())) {//回到这边收车或发短线，再发长线会过收工时间
								continue;
							}
						}else {
							continue;
						}
					}else {
						continue;
					}
				}
				Trip tripTemp=new Trip();
				tripTemp.setBus(trip.getBus());
				if(trip.getTripBeginTime()!=null) {
					Date arrivalTime=scheduleParam.getArrivalTimeNextRound(direction, trip.getTripBeginTime());//在回来的时间
					tripTemp.setTripEndTime(arrivalTime);
				}
				busAvailable.add(tripTemp);
			}
		}
		Date obuTimeMin=null;
		for(int i=0;i<busAvailable.size();i++) {
			Trip trip=busAvailable.get(i);
			Bus bus=trip.getBus();
			if(!bus.isSupperEaten()&&trip.getTripEndTime()!=null&&
				!trip.getTripEndTime().before(scheduleParam.getSupperBeginTime(direction))) {//没吃晚饭，到达时间在开始吃饭时间后
				Date obuTimeMinTemp=DateUtil.getDateAddMinute(trip.getTripEndTime(), scheduleParam.getSupperEatTime(direction));
				if(DateUtil.getDateHM("1805").equals(trip.getTripEndTime()))
					System.out.println("aaa");
				if(obuTimeMinTemp.after(scheduleParam.getLatestTime(direction))) {
					break;
				}
				for(int j=0;j<i;j++) {
					int maxInteval=scheduleParam.getMaxInterval(obuTimeMinTemp, direction);
					obuTimeMinTemp=DateUtil.getDateAddMinute(obuTimeMinTemp, -maxInteval);
					int maxIntevalTemp=scheduleParam.getMaxInterval(obuTimeMinTemp, direction);
					if(maxIntevalTemp!=maxInteval) {
						obuTimeMinTemp=DateUtil.getDateAddMinute(obuTimeMinTemp, maxInteval-maxIntevalTemp);
					}
				}
				if(obuTimeMin==null||obuTimeMinTemp.after(obuTimeMin)) {
					obuTimeMin=obuTimeMinTemp;
				}
			}
		}
		
		/*int busNumber=0;
		Date obuTimeMin=null;
		for(int i=0;i<busAvailable.size();i++) {
			Trip trip=busAvailable.get(i);
			if(trip.getTripEndTime()!=null) {
				if(!trip.getTripEndTime().before(scheduleParam.getSupperBeginTime(direction))) {//回来的第一部吃饭车
					obuTimeMin=DateUtil.getDateAddMinute(trip.getTripEndTime(), scheduleParam.getSupperEatTime(direction));
					break;
				}
			}
			busNumber++;
		}
		if(obuTimeMin!=null) {
			for(int i=0;i<busNumber;i++) {
				int maxInteval=scheduleParam.getMaxInterval(obuTimeMin, direction);
				obuTimeMin=DateUtil.getDateAddMinute(obuTimeMin, -maxInteval);
				int maxIntevalTemp=scheduleParam.getMaxInterval(obuTimeMin, direction);
				if(maxIntevalTemp!=maxInteval) {
					obuTimeMin=DateUtil.getDateAddMinute(obuTimeMin, maxInteval-maxIntevalTemp);
				}
			}
		}*/
		return obuTimeMin;
	}
	
	private boolean isQuitAfterTrip(Trip tripArrival) {
		Bus bus=tripArrival.getBus();
		Date offDutyTime=null;
		if(bus.isSingleClass()) {
			if(bus.isHasMiddleStop()) {
				offDutyTime=scheduleParam.getMiddleStopOffDutyBeginTime();
			}else {
				offDutyTime=scheduleParam.getMiddleStopBeginTime();
			}
		}else if(bus.getShiftType()!=null) {
			offDutyTime=DateUtil.getDateHM(bus.getShiftType().getEndTime());
		}
		if(offDutyTime!=null) {
			if(scheduleParam.isEndDirectionTrip(tripArrival)&&
					!tripArrival.getNextObuTimeMin().before(offDutyTime)) {
				return true;
			}
		}
		return false;
	}
	
	private void procLatestTrip(int direction) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		List<Trip> tripList=routeSchedule.getTripList(direction);
		Trip latestTrip=tripList.get(tripList.size()-1);
		if(latestTrip.getTripBeginTime().before(latestTime)) {
			if(latestTrip.getPreTrip()!=null) {
				Trip nextTrip=latestTrip.getPreTrip().getNextTrip();
				if(nextTrip!=null&&nextTrip.getTripEndTime().before(latestTime)) {
					Trip trip=new Trip(nextTrip.getBus(), direction, latestTime, scheduleParam, latestTrip);
					routeSchedule.addTrip(trip);
					routeSchedule.tripListSort();
					latestTrip=trip;
				}else {
					latestTrip.setTripBeginTime(latestTime);
					Date arrivalTime=scheduleParam.getArrivalTime(latestTime, direction);
					latestTrip.setTripEndTime(arrivalTime);
					latestTrip.setNextObuTimeMin(scheduleParam);
				}
			}
			Integer interval=scheduleParam.getLeaveImmediatelyInterval(direction);
			if(interval!=null) {
				int busNumber=0;
				List<Trip> lastRoundTripList=new ArrayList<Trip>();
				for(int i=tripList.size()-1;i>=0&&busNumber<5;i--) {
					Trip trip=tripList.get(i);
					if(trip!=latestTrip&&trip.getBus()==latestTrip.getBus()) {//到了上一轮
						break;
					}
					busNumber++;
					lastRoundTripList.add(trip);
				}
				for(int i=lastRoundTripList.size()-1;i>=0;i--) {
					Trip trip=lastRoundTripList.get(i);
					Trip tripFullLast=trip.getLastTripFull();
					if(tripFullLast==null)
						System.out.println("aaa");
					interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestTime)/(i+1);
					Date obuTime=DateUtil.getDateAddMinute(tripFullLast.getTripBeginTime(), interval);
					if(obuTime.after(trip.getTripBeginTime())) {
						trip.setTripBeginTime(obuTime);
						trip.setTripEndTime(scheduleParam.getArrivalTime(obuTime, direction));
					}
				}
			}
		}
	}
	
	private int getInterval4ExceedMaxInterval(int direction,Trip lastTripFull,List<Trip> busQueue4Long) {
		int maxInterval=scheduleParam.getMaxInterval(lastTripFull.getTripBeginTime(), direction);
		int interval=maxInterval;
		int increment=0;
		Date latestTime=scheduleParam.getLatestTime(direction);
		for(int i=0;i<10;i++) {
			Date obuTimeLast=lastTripFull.getTripBeginTime();
			boolean onTime=true;
			for(int j=0;j<busQueue4Long.size();j++) {
				Trip tripTemp=busQueue4Long.get(j);
				if(obuTimeLast.after(latestTime)) {
					break;
				}
				int maxIntervalTemp=scheduleParam.getMaxInterval(obuTimeLast, direction)+increment;
				Date obuTimeTemp=DateUtil.getDateAddMinute(obuTimeLast, maxIntervalTemp);
				Date obuTimeMin=null;
				if(tripTemp.isEatAfterTrip()||tripTemp.isElecSupplyAfterTrip()) {
					obuTimeMin=tripTemp.getNextObuTimeMin();
				}else if(tripTemp.getTripEndTime()!=null){
					obuTimeMin=DateUtil.getDateAddMinute(tripTemp.getTripEndTime(), 1);
				}
				if(obuTimeMin!=null&&
					obuTimeMin.after(obuTimeTemp)) {
					increment++;
					onTime=false;
					break;
				}
				obuTimeLast=obuTimeTemp;
			}
			if(onTime) {
				interval=maxInterval+increment;
				break;
			}
		}
		return interval;
	}
	
	private RouteStaTurn getRouteStaTurn4Rest2Long(int direction, int interval, List<Trip> busQueue4Long) {
		int maxBusInterval=0;
		int maxBusIntervalIndex=-1;
		for(int i=1;i<busQueue4Long.size();i++) {
			Trip tripArrival=busQueue4Long.get(i);
			Trip tripArrivalLast=busQueue4Long.get(i-1);
			if(tripArrival.getNextObuTimeMin().after(tripArrivalLast.getNextObuTimeMin())) {
				int busInterval=DateUtil.getMinuteInterval(tripArrivalLast.getNextObuTimeMin(), tripArrival.getNextObuTimeMin());
				if(busInterval>maxBusInterval) {
					maxBusInterval=busInterval;
					maxBusIntervalIndex=i;
				}
			}
		}
		Trip tripFullLast=routeSchedule.getTripFullLast(direction);
		Date obuTimeLast=tripFullLast.getTripBeginTime();
		boolean adjust=false;
		for(int i=0;i<maxBusIntervalIndex;i++) {
			Trip tripArrival=busQueue4Long.get(i);
			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
			if(tripArrival.getTripEndTime()!=null) {
				int restTime=DateUtil.getMinuteInterval(tripArrival.getTripEndTime(), obuTime);
				if(!tripArrival.isElecSupplyAfterTrip()&&!tripArrival.isEatAfterTrip()&&restTime>30) {//停站超过30分钟，短线调位
					adjust=true;
				}
			}
			obuTimeLast=obuTime;
		}
		if(!adjust)
			return null;
		RouteStaTurn routeStaTurnShortest=null;
		List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
		for(RouteStaTurn routeStaTurnTemp:routeStaTurnList) {
			if(routeStaTurnShortest==null||scheduleParam.getRouteSta(routeStaTurnTemp.getLastRouteStaId()).getOrderNumber()<
					scheduleParam.getRouteSta(routeStaTurnShortest.getLastRouteStaId()).getOrderNumber()) {
				routeStaTurnShortest=routeStaTurnTemp;
			}
		}
		Trip tripArrival=busQueue4Long.get(0);
		Date nextObuTimeMin=scheduleParam.getMinObuTimeNextLap(tripArrival, routeStaTurnShortest);
		obuTimeLast=tripFullLast.getTripBeginTime();
		RouteStaTurn routeStaTurn=null;
		for(int i=0;i<maxBusIntervalIndex;i++) {
			Trip tripArrivalNext=busQueue4Long.get(i+1);
			Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
			if(tripArrivalNext.getNextObuTimeMin().after(obuTime)&&tripArrivalNext.getTripEndTime()!=null) {//非复行车赶不上
				if(nextObuTimeMin.after(obuTime)) {
					return null;
				}else {
					routeStaTurn=routeStaTurnShortest;
					break;
				}
			}
			obuTimeLast=obuTime;
		}
		if(routeStaTurn==null) {//不一定要发最短
			routeStaTurn=routeStaTurnShortest;
			Trip maxBusIntervalTrip=busQueue4Long.get(maxBusIntervalIndex);
			for(RouteStaTurn routeStaTurnTemp:routeStaTurnList) {
				if(scheduleParam.getRouteSta(routeStaTurnTemp.getLastRouteStaId()).getOrderNumber()>
						scheduleParam.getRouteSta(routeStaTurnShortest.getLastRouteStaId()).getOrderNumber()) {
					 nextObuTimeMin=scheduleParam.getMinObuTimeNextLap(tripArrival, routeStaTurnTemp);
					 if(nextObuTimeMin.before(maxBusIntervalTrip.getNextObuTimeMin())) {
						 routeStaTurn=routeStaTurnTemp;
					 }
				}
			}
		}
		return routeStaTurn;
	}
	
	public boolean isLatestBus(Bus bus) {
		if(busReverseAdjustBase!=null&&busReverseAdjustBase.isLatestBus(bus)) {
			return true;
		}
		return false;
	}
	
}
