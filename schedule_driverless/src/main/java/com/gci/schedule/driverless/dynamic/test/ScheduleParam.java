package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.enums.StationMark;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.service.SimulationService;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;
import com.gci.schedule.driverless.dynamic.util.MathUtil;
import com.gci.schedule.driverless.dynamic.util.StringUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ScheduleParam {
	
	private Long routeId;//线路ID
	
	private String upFirstTime;//上行首班车
	
	private String downFirstTime;//下行首班车
	
	private String upLatestTime;//上行末班车
	
	private String downLatestTime;//下行末班车
	
	private String earlyPeakBeginTime="0700";//早高峰开始时间
	
	private String earlyPeakEndTime="0900";//早高峰结束时间
	
	private String latePeakBeginTime="1700";//晚高峰开始时间
	
	private String latePeakEndTime="1900";//晚高峰结束时间
	
	private List<DispatchRule> dispatchRuleList=new ArrayList<DispatchRule>();
	
	private List<RouteWasteTime> routeWasteTimeList=new ArrayList<RouteWasteTime>();
	
	private List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
	
	private Map<ScheduleHalfHour, Integer> busConfigHalfHour=new HashMap<ScheduleHalfHour, Integer>();
	
	private int busNumberConfig;//配车数
	
	private int singleBusNumberConfig;//单班车数
	
	private SimulationService simulationService;
	
	private List<ScheduleParamsAnchor> scheduleParamsAnchorList;//停站配置
	
	private Date runDate;
	
	private List<ScheduleParamsEat> lunchEatParams=new ArrayList<ScheduleParamsEat>();
	
	private List<ScheduleParamsEat> supperEatParams=new ArrayList<ScheduleParamsEat>();
	
	private ScheduleParamsSingle scheduleParamsSingle;
	
	private ScheduleParamsRoute scheduleParamsRoute;
	
	private List<ScheduleParamsClasses> scheduleParamsClassesList;
	
	private List<RouteSta> routeStaList;
	
	private boolean isLoopLine;//环线标识
	
	private Map<Integer,Map<String, RouteStationPassenger>> highSectionPassengerMap=new HashMap<Integer, Map<String,RouteStationPassenger>>();//各时段高断面客流

	private Map<Integer,Map<String, Integer>> minLongClassesNumberMap=new HashMap<Integer, Map<String,Integer>>();//各时段最低发班要求

	private ScheduleParamPreset scheduleParamPreset;//排班配车情况
	
	private int vehicleContent;//核载人数
	
	private List<RouteStaTurn> routeStaTurnList;//短线调头信息
	
	private Integer minDoubleClassesBusNumberUp;//最小上行双班车
	
	private Integer minDoubleClassesBusNumberDown;//最小下行双班车
	
	private List<SchedulePlan> schedulePlanReference;//排班参照计划;
	
	private boolean calculateByHour;//最低发班要求一小时一班
	
	//private boolean allow2ReverseStop=true;//反收
	
	private Map<Integer, RouteStationPassengerInfo> passengerInfoMap;//各时段客流
	
	private Integer enduranceMileage;//续航里程
	
	private List<ScheduleParamsEndurance> elecSupplySettingList;//补电设置
	
	private static List<Long> routeIdTestList=new ArrayList<Long>();
	
	private static List<Long> routeIdElecSupplyList=new ArrayList<Long>();//补电
	
	private List<RouteStaCutInAndQuit> routeStaCutInAndQuitList=null;
	
	private List<TriplogTask> triplogTaskList=null;
	
	private Integer latePeakPassengerDirection;
	
	private Integer singleBusTripNumber;//单班车班次数
	
	private boolean regularRoute;//是否定点线路
	
	private String tripBeginTimeB4FirstTime;//凸头班次时间
	
	private Long mountCarTemplateId;//挂车模板ID

	private ScheduleParamsDriverless scheduleParamsDriverless;
	
	private List<ScheduleParamsDrInout> scheduleParamsDrInoutList;

	private List<ScheduleParamsDrRouteSub> scheduleParamsDrRouteSubList;

	private List<ScheduleParamsDrBus> scheduleParamsDrBusList;

	private Map<Long, RouteSta> routeStaMap;

	static {
		//二巴一分
		/*routeIdTestList.add(12630L);//652路
		routeIdTestList.add(133L);//523路
		routeIdTestList.add(416L);//830路
		routeIdTestList.add(363L);//925路
		routeIdTestList.add(366L);//929路
		//二巴二分
		routeIdTestList.add(261L);//552路
		routeIdTestList.add(294L);//886路
		routeIdTestList.add(4971L);//732路
		routeIdTestList.add(9930L);//792路
		routeIdTestList.add(262L);//126路
		//新福利
		routeIdTestList.add(12750L);//662路
		routeIdTestList.add(1392L);//184路
		routeIdTestList.add(7390L);//752路
		routeIdTestList.add(471L);//975路
		routeIdTestList.add(5031L);//729路
		
		//溢通
		routeIdTestList.add(7830L);//756路
		routeIdTestList.add(12791L);//665路
		//番广
		routeIdTestList.add(415L);//301路
		routeIdTestList.add(1010L);//303路
		routeIdTestList.add(7730L);//987路
		//花恒
		routeIdTestList.add(4472L);//702路
		routeIdTestList.add(4494L);//709路
		routeIdTestList.add(4495L);//710路
		
		//电车
		routeIdTestList.add(93L);//61路
		routeIdTestList.add(172L);//88路
		routeIdTestList.add(91L);//569路
		routeIdTestList.add(18L);//87路
		routeIdTestList.add(105L);//243路
		routeIdTestList.add(33831L);//453A路
		routeIdTestList.add(116L);//110路
		routeIdTestList.add(40352L);//669路
		routeIdTestList.add(10L);//834路
		routeIdTestList.add(11L);//837路
		routeIdTestList.add(136L);//81路
		routeIdTestList.add(102L);//86路
		routeIdTestList.add(110L);//104路
		routeIdTestList.add(12L);//539路
		routeIdTestList.add(16L);//543路
		routeIdTestList.add(10L);//834路
		routeIdTestList.add(142L);//277路
		routeIdTestList.add(169L);//544路
		routeIdTestList.add(171L);//546路
		
		
		
		//三汽
		routeIdTestList.add(40L);//140路
		routeIdTestList.add(94L);//254路
		routeIdTestList.add(130L);//85路
		routeIdTestList.add(710L);//560路
		routeIdTestList.add(275L);//239路
		routeIdTestList.add(14510L);//370路
		routeIdTestList.add(247L);//432路
		routeIdTestList.add(5950L);//327路
		routeIdTestList.add(8L);//83路
		routeIdTestList.add(124L);//535路

		//一汽
		routeIdTestList.add(306L);//406路
		routeIdTestList.add(5730L);//781路
		routeIdTestList.add(60L);//72路
		routeIdTestList.add(307L);//408路
		routeIdTestList.add(466L);//13路
		routeIdTestList.add(9610L);//489路
		routeIdTestList.add(209L);//4路
		routeIdTestList.add(197L);//524路
		routeIdTestList.add(7850L);//788路
		routeIdTestList.add(8710L);//番178路
		routeIdTestList.add(226L);//69路
		routeIdTestList.add(202L);//34路
		routeIdTestList.add(179L);//32路
		routeIdTestList.add(734L);//54路
		routeIdTestList.add(405L);//2路
		routeIdTestList.add(55L);//42路
		routeIdTestList.add(12691L);//498路
		routeIdTestList.add(48L);//16路
		routeIdTestList.add(397L);//9路
		routeIdTestList.add(232L);//131A路
		routeIdTestList.add(230L);//131B路
		routeIdTestList.add(12690L);//497路
		routeIdTestList.add(11910L);//商务专线6
		
		//其他
		routeIdTestList.add(10690L);//41路
		routeIdTestList.add(9791L);//388路
		routeIdTestList.add(98L);//101路
		routeIdTestList.add(6970L);//303A路
		routeIdTestList.add(204L);//461路
		routeIdTestList.add(12030L);//976路
		routeIdTestList.add(7511L);//711路
		routeIdTestList.add(10610L);//571路
		routeIdTestList.add(7815L);//985路
		routeIdTestList.add(112L);//106路
		
		routeIdElecSupplyList.add(710L);//560路
		routeIdElecSupplyList.add(60L);//72路
		routeIdElecSupplyList.add(94L);//254路
		routeIdElecSupplyList.add(63L);//225路
		routeIdElecSupplyList.add(5612L);//324路
		routeIdElecSupplyList.add(121L);//旅游1线
		routeIdElecSupplyList.add(122L);//旅游2线
*/	}
	
	public boolean isElecSupplyLine() {
		return routeIdElecSupplyList.contains(routeId);
	}
	
	public boolean isTestLineFull() {
		if(getRouteStaTurnList().isEmpty()) {
			if(routeIdTestList.contains(routeId)||
				(getEndDirection()!=null&&getEndDirection()==Direction.NODIRECTION.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRegularRoute() {
		return regularRoute;
	}

	public void setRegularRoute(boolean regularRoute) {
		this.regularRoute = regularRoute;
	}

	public boolean isTestLineShort() {
		if(!getRouteStaTurnList().isEmpty()) {
			if(routeIdTestList.contains(routeId)||
				(getEndDirection()!=null&&getEndDirection()!=null&&getEndDirection()==Direction.NODIRECTION.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	
	public void setPassengerInfoMap(Map<Integer, RouteStationPassengerInfo> passengerInfoMap) {
		this.passengerInfoMap = passengerInfoMap;
	}

	/*public boolean isAllow2ReverseStop() {
		return allow2ReverseStop;
	}

	public void setAllow2ReverseStop(boolean allow2ReverseStop) {
		this.allow2ReverseStop = allow2ReverseStop;
	}*/

	public boolean isCalculateByHour() {
		return calculateByHour;
	}

	public void setCalculateByHour(boolean calculateByHour) {
		this.calculateByHour = calculateByHour;
	}

	public List<SchedulePlan> getSchedulePlanReference() {
		return schedulePlanReference;
	}

	public void setSchedulePlanReference(List<SchedulePlan> schedulePlanReference) {
		this.schedulePlanReference = schedulePlanReference;
	}

	public String getUpFirstTime() {
		return upFirstTime;
	}

	public String getDownFirstTime() {
		return downFirstTime;
	}

	public String getUpLatestTime() {
		return upLatestTime;
	}

	public String getDownLatestTime() {
		return downLatestTime;
	}

	public Long getRouteId() {
		return routeId;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setMinDoubleClassesBusNumberUp(Integer minDoubleClassesBusNumberUp) {
		this.minDoubleClassesBusNumberUp = minDoubleClassesBusNumberUp;
	}

	public Integer getEnduranceMileage() {
		return enduranceMileage;
	}

	public void setEnduranceMileage(Integer enduranceMileage) {
		this.enduranceMileage = enduranceMileage;
	}

	public List<ScheduleParamsEndurance> getElecSupplySettingList() {
		return elecSupplySettingList;
	}

	public void setElecSupplySettingList(List<ScheduleParamsEndurance> elecSupplySettingList) {
		this.elecSupplySettingList = elecSupplySettingList;
	}

	public Integer getMinDoubleClassesBusNumber(int direction) {
		if(direction==Direction.UP.getValue())
			return minDoubleClassesBusNumberUp;
		if(direction==Direction.DOWN.getValue())
			return minDoubleClassesBusNumberDown;
		return null;
	}


	public void setMinDoubleClassesBusNumberDown(Integer minDoubleClassesBusNumberDown) {
		this.minDoubleClassesBusNumberDown = minDoubleClassesBusNumberDown;
	}

	public void setScheduleParamPreset(ScheduleParamPreset scheduleParamPreset) {
		this.scheduleParamPreset = scheduleParamPreset;
	}

	public int getVehicleContent() {
		return vehicleContent;
	}

	public void setVehicleContent(int vehicleContent) {
		this.vehicleContent = vehicleContent;
	}

	public Integer getBusNumberPreset() {
		return scheduleParamPreset.getBusNumberPreset();
	}

	public Integer getBusNumberPresetUp() {
		return scheduleParamPreset.getBusNumberUp();
	}

	public Integer getBusNumberPresetDown() {
		return scheduleParamPreset.getBusNumberDown();
	}
	
	public Integer getBusNumberPreset(int direction) {
		if(direction==Direction.UP.getValue()) {
			return getBusNumberPresetUp();
		}else if(direction==Direction.DOWN.getValue()) {
			return getBusNumberPresetDown();
		}
		return null;
	}
	
	public Integer getBusNumberSinglePreset(int direction) {
		if(direction==Direction.UP.getValue()) {
			return scheduleParamPreset.getSingleBusNumberUp();
		}else if(direction==Direction.DOWN.getValue()) {
			return scheduleParamPreset.getSingleBusNumberDown();
		}
		return null;
	}
	
	public Integer getBusNumberSinglePreset() {
		return scheduleParamPreset.getSingleBusNumber();
	}
	
	public void setBusNumberSinglePreset(int direction,Integer singleBusNumberPreset) {
		if(direction==Direction.UP.getValue()) {
			scheduleParamPreset.setSingleBusNumberUp(singleBusNumberPreset);
		}else if(direction==Direction.DOWN.getValue()) {
			scheduleParamPreset.setSingleBusNumberDown(singleBusNumberPreset);
		}
	}
	
	public void setBusNumberSinglePreset(int busNumberSingle) {
		scheduleParamPreset.setSingleBusNumber(busNumberSingle);
	}
	
	public void setBusNumberPreset(int busNumber) {
		scheduleParamPreset.setBusNumberPreset(busNumber);
	}
	
	public void setBusNumberPreset(int direction,int busNumber) {
		if(direction==Direction.UP.getValue()) {
			scheduleParamPreset.setBusNumberUp(busNumber);
		}else {
			scheduleParamPreset.setBusNumberDown(busNumber);
		}
	}

	public void setMinLongClassesNumber(int direction,String runTime,int minLongClassesNumber) {
		Map<String, Integer> map=minLongClassesNumberMap.get(direction);
		if(map==null) {
			map=new HashMap<String, Integer>();
			minLongClassesNumberMap.put(direction, map);
		}
		map.put(runTime, minLongClassesNumber);
	}

	public void setHighSectionPassenger(int direction,String runTime,RouteStationPassenger highSectionPassenger) {
		Map<String, RouteStationPassenger> map=highSectionPassengerMap.get(direction);
		if(map==null) {
			map=new HashMap<String, RouteStationPassenger>();
			highSectionPassengerMap.put(direction, map);
		}
		map.put(runTime, highSectionPassenger);
	}

	public void setSimulationService(SimulationService simulationService) {
		this.simulationService = simulationService;
	}
	
	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public List<ScheduleParamsAnchor> getScheduleParamsAnchorList() {
		return scheduleParamsAnchorList;
	}

	public String getTripBeginTimeB4FirstTime() {
		return tripBeginTimeB4FirstTime;
	}

	public void setTripBeginTimeB4FirstTime(String tripBeginTimeB4FirstTime) {
		this.tripBeginTimeB4FirstTime = tripBeginTimeB4FirstTime;
	}
	
	

	public Long getMountCarTemplateId() {
		return mountCarTemplateId;
	}

	public void setMountCarTemplateId(Long mountCarTemplateId) {
		this.mountCarTemplateId = mountCarTemplateId;
	}

	public void setScheduleParamsAnchorList(List<ScheduleParamsAnchor> scheduleParamsAnchorList) {
		List<ScheduleParamsAnchor> anchorList=new ArrayList<ScheduleParamsAnchor>();
		Short anchorDurationMin=60;
		Short anchorDurationMax=60;
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if(scheduleParamsAnchor.getDirection().equals(Direction.UP.getStringValue())) {
				anchorList.add(scheduleParamsAnchor);
				if(scheduleParamsAnchor.getAnchorDurationMin()<anchorDurationMin) {
					anchorDurationMin=scheduleParamsAnchor.getAnchorDurationMin();
				}
				if(scheduleParamsAnchor.getAnchorDurationMax()<anchorDurationMax) {
					anchorDurationMax=scheduleParamsAnchor.getAnchorDurationMax();
				}
			}
		}
		checkScheduleParamsAnchor(anchorList);
		for(ScheduleParamsAnchor scheduleParamsAnchor:anchorList) {//重置停站时间
			if(scheduleParamsAnchor.getAnchorDurationMin()>60) {
				scheduleParamsAnchor.setAnchorDurationMin(anchorDurationMin);
			}
			if(scheduleParamsAnchor.getAnchorDurationMax()>60) {
				scheduleParamsAnchor.setAnchorDurationMax(anchorDurationMax);
			}
		}
		anchorDurationMin=60;
		anchorDurationMax=60;
		anchorList=new ArrayList<ScheduleParamsAnchor>();
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if(scheduleParamsAnchor.getDirection().equals(Direction.DOWN.getStringValue())) {
				anchorList.add(scheduleParamsAnchor);
				if(scheduleParamsAnchor.getAnchorDurationMin()<anchorDurationMin) {
					anchorDurationMin=scheduleParamsAnchor.getAnchorDurationMin();
				}
				if(scheduleParamsAnchor.getAnchorDurationMax()<anchorDurationMax) {
					anchorDurationMax=scheduleParamsAnchor.getAnchorDurationMax();
				}
			}
		}
		checkScheduleParamsAnchor(anchorList);
		for(ScheduleParamsAnchor scheduleParamsAnchor:anchorList) {
			if(scheduleParamsAnchor.getAnchorDurationMin()>60) {
				scheduleParamsAnchor.setAnchorDurationMin(anchorDurationMin);
			}
			if(scheduleParamsAnchor.getAnchorDurationMax()>60) {
				scheduleParamsAnchor.setAnchorDurationMax(anchorDurationMax);
			}
		}
		this.scheduleParamsAnchorList = scheduleParamsAnchorList;
	}
	
	private void checkScheduleParamsAnchor(List<ScheduleParamsAnchor> scheduleParamsAnchorList) {
		if(scheduleParamsAnchorList.isEmpty())
			return;
		ScheduleParamsAnchor scheduleParamsAnchor=scheduleParamsAnchorList.get(0);
		int direction=Integer.valueOf(scheduleParamsAnchor.getDirection());
		Date firstTime=getFirstTime(direction);
		Date latestTime=getLatestTime(direction);
		if(latestTime!=null) {
			if(latestTime.before(firstTime))
				latestTime=DateUtil.getDateAdd(latestTime, Calendar.DATE, 1);
			Map<String, ScheduleParamsAnchor> scheduleParamsAnchorMap=new HashMap<String, ScheduleParamsAnchor>();
			for(ScheduleParamsAnchor paramsAnchor:scheduleParamsAnchorList) {
				scheduleParamsAnchorMap.put(paramsAnchor.getBeginTime(), paramsAnchor);
			}
			Date date=firstTime;
			int loopCount=0;
			while(date.before(latestTime)) {
				loopCount++;
				if (loopCount>100) {
					throw new MyException("-1", "缺少"+(direction==Direction.UP.getValue()?"上行":"下行")+DateFormatUtil.HM.getDateString(date)+"满载率参数设置");
				}
				scheduleParamsAnchor=scheduleParamsAnchorMap.get(DateFormatUtil.HM.getDateString(date));
				if(scheduleParamsAnchor==null) {
					if(date.equals(firstTime)) {//缺头车时段
						for(ScheduleParamsAnchor spa:scheduleParamsAnchorList) {
							if(DateUtil.getDateHM(spa.getEndTime()).after(firstTime)) {
								scheduleParamsAnchor=scheduleParamsAnchorList.get(0);
								scheduleParamsAnchor.setBeginTime(DateFormatUtil.HM.getDateString(firstTime));//延伸到头车
								break;
							}
						}
						if(date.equals(firstTime)) {//可能时段设置跨天
							for(ScheduleParamsAnchor spa:scheduleParamsAnchorList) {
								Date endTime=DateUtil.getDateHM(spa.getEndTime());
								endTime=DateUtil.getDateAddDay(endTime, 1);
								if(endTime.after(firstTime)) {
									scheduleParamsAnchor=scheduleParamsAnchorList.get(0);
									scheduleParamsAnchor.setBeginTime(DateFormatUtil.HM.getDateString(firstTime));//延伸到头车
									break;
								}
							}
						}
					}
					//throw new MyException("-1", "缺少"+(direction==Direction.UP.getValue()?"上行":"下行")+DateFormatUtil.HM.getDateString(date)+"满载率参数设置");
				}
				Date endTime=DateUtil.getDateHM(scheduleParamsAnchor.getEndTime());
				if(endTime.before(date)) {
					endTime=DateUtil.getDateAdd(endTime, Calendar.DATE, 1);//跨天
				}
				if(endTime.equals(date)) {
					throw new MyException("-1", (direction==Direction.UP.getValue()?"上行":"下行")+DateFormatUtil.HM.getDateString(date)+"满载率参数设置开始结束时间相同");
				}
				if(endTime.before(latestTime)) {
					ScheduleParamsAnchor scheduleParamsAnchorNext=scheduleParamsAnchorMap.get(DateFormatUtil.HM.getDateString(endTime));
					if(scheduleParamsAnchorNext==null) {//时段不连续，延伸到末班车
						int index=scheduleParamsAnchorList.indexOf(scheduleParamsAnchor);
						if(index==scheduleParamsAnchorList.size()-1) {//最后一个时段
							scheduleParamsAnchor.setEndTime(DateFormatUtil.HM.getDateString(latestTime));//重置结束时间为末班车时间
						}else {
							scheduleParamsAnchorNext=scheduleParamsAnchorList.get(index+1);
							scheduleParamsAnchor.setEndTime(scheduleParamsAnchorNext.getBeginTime());//重置结束时间为下个时段的开始时间
						}
						endTime=DateUtil.getDateHM(scheduleParamsAnchor.getEndTime());
						if(endTime.before(firstTime)) {
							endTime=DateUtil.getDateAddDay(endTime, 1);
						}
					}
				}
				date=endTime;
			}
		}
		
	}
	
	public double getRunMileage(Trip trip) {
		Double runMileage=null;
        if(trip.getDirection()!=Direction.NODIRECTION.getValue()) {
        	for(RouteSta routeSta:routeStaList) {
        		if(routeSta.getRouteStationId().equals(trip.getFirstRouteStaId())) {//首站开始
        			runMileage=0.0;
        			continue;
        		}
        		if(runMileage!=null) {
        			if(routeSta.getStationDistance()==null) {
        				throw new MyException("-1", "请检查站间里程");
        			}
        			runMileage+=routeSta.getStationDistance();
        		}
        		if(routeSta.getRouteStationId().equals(trip.getLastRouteStaId())) {//末站结束
        			break;
        		}
        	}
        }else {
        	runMileage=0.0;
        }
        DecimalFormat df=new DecimalFormat("#.00"); 
        runMileage=Double.valueOf(df.format(runMileage));
        return runMileage;
	}    

	public void setScheduleParamsEatList(List<ScheduleParamsEat> scheduleParamsEatList) {
		for(ScheduleParamsEat scheduleParamsEat:scheduleParamsEatList){
			Date eatBeginTime=DateUtil.getCalendarHM(scheduleParamsEat.getBeginTime()).getTime();
			if(eatBeginTime.before(DateUtil.getCalendarHM("1400").getTime())) {
				lunchEatParams.add(scheduleParamsEat);
			}else {
				supperEatParams.add(scheduleParamsEat);
			}
		}
	}
	
	public void setScheduleParamsSingle(ScheduleParamsSingle scheduleParamsSingle) {
		this.scheduleParamsSingle = scheduleParamsSingle;
	}

	public void setScheduleParamsRoute(ScheduleParamsRoute scheduleParamsRoute) {
		this.scheduleParamsRoute = scheduleParamsRoute;
	}
	
	public void setScheduleParamsClassesList(List<ScheduleParamsClasses> scheduleParamsClassesList) {
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			if(scheduleParamsClasses.getClassesNumMin()>2*60/scheduleParamsClasses.getMaxDispatchInterval()) {//最小发班数异常，重置
				scheduleParamsClasses.setClassesNumMin((short)(Math.ceil(2*60/scheduleParamsClasses.getMaxDispatchInterval())));
			}
		}
		this.scheduleParamsClassesList = scheduleParamsClassesList;
	}
	
	public List<ScheduleParamsClasses> getScheduleParamsClassesList() {
		return scheduleParamsClassesList;
	}

	public void setRouteStaList(List<RouteSta> routeStaList) {
		this.routeStaList = routeStaList;
	}
	
	public List<ScheduleShiftPreset> getShiftListPreset(){
		return scheduleParamPreset.getShiftList();
	}
	
	public int getShiftBusNumber() {
		int busNumber=0;
		if(scheduleParamPreset.getShiftList()!=null) {
			for(ScheduleShiftPreset shift:scheduleParamPreset.getShiftList()) {
				busNumber+=shift.getBusNumberUp();
				busNumber+=shift.getBusNumberDown();
			}
		}
		return busNumber;
	}
	
	public ScheduleParamShift getScheduleParamShift(int shiftType,int direction,Date date) {
		ScheduleParamShift scheduleParamShift=null;
		for(ScheduleParamShift sps:scheduleParamsSingle.getShifts()) {
			if(sps.getShiftType().equals(shiftType)) {
				Date startTime=DateUtil.getDateHM(sps.getStartTime());
				Date firstTime=getFirstTime(direction);
				if(startTime.before(firstTime))//班别开始时间早于首班车时间
					startTime=firstTime;
				if(!date.after(startTime)) {
					if(scheduleParamShift==null||
							startTime.before(DateUtil.getDateHM(scheduleParamShift.getStartTime()))) {
						scheduleParamShift=sps;
					}
				}
			}
		}
		return scheduleParamShift;
	}
	
	public boolean isEndDirectionTrip(Trip trip) {
		return isEndDirectionTrip(trip.getBus().getStartDirection(),trip.getDirection());
	}
	
	public boolean isEndDirectionTrip(int startDirection,int tripDirection) {
		if(isLoopLine) {//环线两边总站都一样
			return true;
		}
		Integer endDirection=getEndDirection();
		if(endDirection==null)//中途入场
			return true;
		if(endDirection==2) {//两边都可以停场，取出车方向
			endDirection=startDirection;
		}
		if(endDirection==tripDirection) {
			return false;
		}else {
			return true;
		}
	}
	
	public boolean isEndAfterTrip(Trip trip) {
		if(trip.getTripBeginTime()==null)
			return false;
		Bus bus=trip.getBus();
		if(bus.getStartRouteStaId()!=null)//中途出车
			return false;
		if(isEndDirectionTrip(trip)) {
			if(bus.isSingleClass()) {
				Date nextObuTimeMin=trip.getNextObuTimeMin();
				if(nextObuTimeMin==null) {
					Date arrivalTime=getArrivalTime(trip.getTripBeginTime(), trip.getDirection());
					int restTime=getMinRestTime(arrivalTime, 1-trip.getDirection());
					nextObuTimeMin=DateUtil.getDateAddMinute(arrivalTime, restTime);
				}
				if((!bus.isHasMiddleStop()&&!nextObuTimeMin.before(getMiddleStopBeginTime()))||
					!nextObuTimeMin.before(getMiddleStopOffDutyBeginTime())) {
					return true;
				}else {
					if(getEndDirection()==null) {//中途停场
						Date arrivalTime=getArrivalTimeNextRound(trip.getDirection(), trip.getTripBeginTime());
						if(arrivalTime==null)//到对面过了末班时间
							return true;
						if((!bus.isHasMiddleStop()&&!arrivalTime.before(DateUtil.getDateAddMinute(getMiddleStopBeginTime(), 60)))||
								!arrivalTime.before(DateUtil.getDateAddMinute(getMiddleStopOffDutyBeginTime(), 60))) {//在收工时间后60分钟到不了，在对面退出，自己安排短线顺载
							return true;
						}
					}else {
						int directionReverse=1-trip.getDirection();
						if(isLoopLine) {
							directionReverse=trip.getDirection();
						}
						Date obuTimeNextRound=getArrivalTimeNextRound(directionReverse, trip.getNextObuTimeMin());
						if(obuTimeNextRound==null||obuTimeNextRound.after(DateUtil.getDateAddMinute(getMiddleStopOffDutyBeginTime(), 60))){
							return true;
						}
					}
				}
			}else {
				if(bus.getShiftType()!=null&&(bus.getShiftCode()!=ShiftType.EVENING_SHIFT.getValue()||
						(bus.getShiftType()!=null&&DateUtil.getDateHM(bus.getShiftType().getEndTime()).before(getLatestTime(bus.getStartDirection()))))) {
					Date obuTime=trip.getNextObuTimeMin();
					if(!obuTime.before(DateUtil.getDateHM(bus.getShiftType().getEndTime())))
						return true;
					if(routeStaTurnList.isEmpty()) {//没有短线，不能调位
						/*int turnAroundTime=getTurnAroundTime(trip.getTripBeginTime());
						Date arrivalTime=DateUtil.getDateAddMinute(trip.getTripBeginTime(), turnAroundTime);
						if(arrivalTime.after(DateUtil.getDateHM(bus.getShiftType().getEndTime())))//回来后过了下班时间，到对面先停下来
							return true;*/
						if(getEndDirection()==null) {//中途停场,看能不能走一个来回
							Date arrivalTime=getArrivalTimeNextRound(trip.getDirection(), trip.getTripBeginTime());
							if(arrivalTime==null||arrivalTime.after(DateUtil.getDateAddMinute(DateUtil.getDateHM(bus.getShiftType().getEndTime()), 60))) {
								return true;
							}
						}else {
							int directionReverse=1-trip.getDirection();
							if(isLoopLine) {
								directionReverse=trip.getDirection();
							}
							Date arrivalTime=getArrivalTimeNextRound(directionReverse, trip.getNextObuTimeMin());//只能在对面停场，到对面再走一个来回
							if(arrivalTime==null||arrivalTime.after(DateUtil.getDateAddMinute(DateUtil.getDateHM(bus.getShiftType().getEndTime()), 60))) {
								return true;
							}
						}
					}
					
				}
			}
		}
		return false;
	}
	
	public Trip busCheckIn(Bus bus,int shiftType,Date now) {
		ScheduleParamShift scheduleParamShift=getScheduleParamShift(shiftType, bus.getStartDirection(), now);
		if(scheduleParamShift==null)
			return null;
		Date endTime=DateUtil.getDateHM(scheduleParamShift.getEndTime());
		if(endTime.before(getLatestTime(bus.getStartDirection())))
			bus.setShiftType(scheduleParamShift);
		else {
			bus.setShiftType(null);
			bus.setShiftCode(shiftType);
		}
		Date startTime=DateUtil.getDateHM(scheduleParamShift.getStartTime());
		Trip trip=new Trip();
		trip.setBus(bus);
		trip.setDirection(1-bus.getStartDirection());
		trip.setNextObuTimeMin(startTime);
		return trip;
	}
	
	public boolean isSingleBusLastTrip(Trip preTrip) {
		Date arrivalTime=getArrivalTime(preTrip.getNextObuTimeMin(), 1-preTrip.getDirection());
		if(preTrip.getBus().isSingleClass()&&!arrivalTime.before(getMiddleStopOffDutyBeginTime())) {
			return true;
		}
		return false;
	}
	
	public boolean isLoopLine() {
		return isLoopLine;
	}
	
	public boolean isDoubleFullLineGen() {
		if(!isLoopLine()
			&&getRouteStaTurnList().isEmpty()
			//&&getBusNumberPreset(Direction.UP.getValue())!=null
			//&&getBusNumberPreset(Direction.DOWN.getValue())!=null
			&&getFirstTime(Direction.UP.getValue()).before(getEarlyPeakEndTime())) {
			if(getShiftListPreset()==null) {
				return true;
			}
			boolean isBusSpecial=false;
			for(ScheduleShiftPreset shift:getShiftListPreset()) {
				if(shift.getBusNumberUp()!=null&&shift.getBusNumberUp()!=0) {
					isBusSpecial=true;
					break;
				}
				if(shift.getBusNumberDown()!=null&&shift.getBusNumberDown()!=0) {
					isBusSpecial=true;
					break;
				}
			}
			if(isBusSpecial) {
				return false;
			}
			return true;
		}
		return false;	
	}
	
	/**
	 * 是否双环线
	 * @return
	 */
	public boolean isLoopLineDouble() {
		if(isLoopLine) {
			if(upFirstTime!=null&&downFirstTime!=null&&
				!upFirstTime.equals(upLatestTime)&&!downFirstTime.equals(downLatestTime)) {
				RouteSta firstRouteStaUp=getFirstRouteSta(Direction.UP.getValue());
				RouteSta lastRouteStaUp=getLastRouteSta(Direction.UP.getValue());
				RouteSta firstRouteStaDown=getFirstRouteSta(Direction.DOWN.getValue());
				RouteSta lastRouteStaDown=getLastRouteSta(Direction.DOWN.getValue());
				if(firstRouteStaUp!=null&&lastRouteStaUp!=null&&firstRouteStaDown!=null&&lastRouteStaDown!=null) {
					if(firstRouteStaUp.getStationId().equals(firstRouteStaDown.getStationId())||
						firstRouteStaUp.getStationName().equals(firstRouteStaDown.getStationName())) {
						return true;//上下行首站相同
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 两条环线，上下行总站不一样
	 * @return
	 */
	public boolean isTwoLoopLine(){
		if(isLoopLine) {
			RouteSta firstRouteStaUp=getFirstRouteSta(Direction.UP.getValue());
			RouteSta lastRouteStaUp=getLastRouteSta(Direction.UP.getValue());
			RouteSta firstRouteStaDown=getFirstRouteSta(Direction.DOWN.getValue());
			RouteSta lastRouteStaDown=getLastRouteSta(Direction.DOWN.getValue());
			if(firstRouteStaUp!=null&&lastRouteStaUp!=null&&firstRouteStaDown!=null&&lastRouteStaDown!=null) {
				if(!firstRouteStaUp.getStationId().equals(firstRouteStaDown.getStationId())&&
					!firstRouteStaUp.getStationName().equals(firstRouteStaDown.getStationName())) {
					if(downFirstTime!=null&&downLatestTime!=null) {
						return true;//上下行首站不相同
					}
				}
			}
		}
		return false;
	}

	public void setLoopLine(boolean isLoopLine) {
		this.isLoopLine = isLoopLine;
	}

	public RouteSta getRouteSta(int stationMark) {
		for(RouteSta routeSta:routeStaList) {
			if(routeSta.getStationMark()==stationMark) {
				return routeSta;
			}
		}
		return null;
	}
	
	public List<RouteSta> getRouteStaList(int stationMark) {
		List<RouteSta> routeStaListTemp=new ArrayList<RouteSta>();
		for(RouteSta routeSta:routeStaList) {
			if(routeSta.getStationMark()==stationMark) {
				routeStaListTemp.add(routeSta);
			}
		}
		return routeStaListTemp;
	}
	
	public RouteSta getRouteSta(Long routeStaId) {
		for(RouteSta routeSta:routeStaList) {
			if(routeSta.getRouteStationId().equals(routeStaId)) {
				return routeSta;
			}
		}
		return null;
	}
	
	public RouteSta getRouteSta(int direction,Long stationId) {
		for(RouteSta routeSta:routeStaList) {
			if(routeSta.getDirection()==direction&&routeSta.getStationId().equals(stationId)) {
				return routeSta;
			}
		}
		return null;
	}
	
	public List<RouteSta> getRouteStaList() {
		return routeStaList;
	}

	public RouteSta getFirstRouteSta(int direction) {
		int stationMark;
		if(direction==Direction.UP.getValue()) {//上行
			stationMark=StationMark.UP_FIRST.getValue();
		}else {
			stationMark=StationMark.DOWN_FIRST.getValue();
		}
		RouteSta routeSta=getRouteSta(stationMark);
		return routeSta;
	}
	
	public RouteSta getLastRouteSta(int direction) {
		int stationMark;
		if(direction==Direction.UP.getValue()) {//上行
			stationMark=StationMark.UP_LAST.getValue();
		}else {
			stationMark=StationMark.DOWN_LAST.getValue();
		}
		RouteSta routeSta=getRouteSta(stationMark);
		return routeSta;
	}
	
	public Long getLastRouteStaId(int direction) {
		RouteSta routeSta=getLastRouteSta(direction);
		if(routeSta!=null)
			return routeSta.getRouteStationId();
		return null;
	}
	
	public Long getFirstRouteStaId(int direction) {
		RouteSta routeSta=getFirstRouteSta(direction);
		if(routeSta!=null)
			return routeSta.getRouteStationId();
		return null;
	}
	
	public boolean isEatAfterTrip(Trip trip) {
		Date tripEndTime=trip.getTripEndTime();
		if(tripEndTime==null) {//复行车
			return false;
		}
		Bus bus=trip.getBus();
		int direction=1-trip.getDirection();
		if(isLoopLine)
			direction=trip.getDirection();
		if(getLunchBeginTime(direction)!=null&&!tripEndTime.before(getLunchBeginTime(direction))&&!bus.isSingleClass()&&!bus.isLunchEaten()) {//到达时间在11点后的，肯定要安排吃饭
			return true;
		}else if(getSupperBeginTime(direction)!=null&&!tripEndTime.before(getSupperBeginTime(direction))&&!bus.isSingleClass()&&!bus.isSupperEaten()) {
			return true;
		}
		return false;
	}
	
	public Integer getTime2Eat(Trip tripArrival) {
		Bus bus=tripArrival.getBus();
		if(bus.isSingleClass())
			return null;
		int direction=1-tripArrival.getDirection();
		if(isLoopLine)
			direction=tripArrival.getDirection();
		if(getSupperBeginTime(direction)!=null&&!tripArrival.getBus().isSupperEaten()&&
				!tripArrival.getTripEndTime().before(getSupperBeginTime(direction))) {//到达时间在11点后的，肯定要安排吃饭
			return getSupperEatTime(direction).intValue();
		}else if(getLunchBeginTime(direction)!=null&&!tripArrival.getBus().isLunchEaten()&&
				!tripArrival.getTripEndTime().before(getLunchBeginTime(direction))){//到达时间在11点后的，肯定要安排吃饭
			return getLunchEatTime(direction).intValue();
		}
		return null;
	}
	
	public Integer getMinRestTime(Trip trip) {
		Bus bus=trip.getBus();
		Date tripEndTime=trip.getTripEndTime();
		if(tripEndTime==null) {
			if(trip.isShortLine()) {
				tripEndTime=getArrivalTime(trip.getTripBeginTime(), trip.getDirection(), trip.getFirstRouteStaId(), trip.getLastRouteStaId());
			}else {
				tripEndTime=getArrivalTime(trip.getTripBeginTime(), trip.getDirection());
			}
			trip.setTripEndTime(tripEndTime);
		}
		int direction=1-trip.getDirection();
		if(isLoopLine)
			direction=trip.getDirection();
		if(tripEndTime==null)
			return null;
		if(getLunchBeginTime(direction)!=null&&!tripEndTime.before(getLunchBeginTime(direction))&&!bus.isSingleClass()&&!bus.isLunchEaten()) {//到达时间在11点后的，肯定要安排吃饭
			trip.setEatAfterTrip(true);
			return getLunchEatTime(direction).intValue();
		}else if(getSupperBeginTime(direction)!=null&&!tripEndTime.before(getSupperBeginTime(direction))&&!bus.isSingleClass()&&!bus.isSupperEaten()) {
			trip.setEatAfterTrip(true);
			return getSupperEatTime(direction).intValue();
		}
		Integer restTime=getMinRestTime(tripEndTime,1-trip.getDirection());
		int restTimeMin=DateUtil.getMinuteInterval(trip.getTripBeginTime(), tripEndTime)/6;//每行6分钟停1分钟
		if(restTimeMin<5) {
			restTimeMin=5;
		}
		if(restTime>restTimeMin) {//修正最小停站时间，防止人工设置异常
			restTime=restTimeMin;
		}
		return restTime;
	}
	
	public Integer getRestTime(Date tripEndTime,int tripDirection) {
		Integer restTime=getLeaveImmediatelyInterval(1-tripDirection);
		if(restTime!=null)
			return restTime;
		int minRestTime=getMinRestTime(tripEndTime, 1-tripDirection);
		int maxRestTime=getMaxRestTime(tripEndTime, 1-tripDirection);
		if(minRestTime!=0) {
			if(maxRestTime/minRestTime>3) {
				maxRestTime=minRestTime*3;
			}
		}
		return (minRestTime+maxRestTime)/2;
	}
	
	public Integer getMinRestTime(int tripDirection,Date tripBeginTime,Date tripEndTime) {
		Integer restTime=getMinRestTime(tripEndTime,tripDirection);
		int restTimeMin=DateUtil.getMinuteInterval(tripBeginTime, tripEndTime)/6;//每行6分钟停1分钟
		if(restTimeMin<5) {
			restTimeMin=5;
		}
		if(restTime>restTimeMin) {//修正最小停站时间，防止人工设置异常
			restTime=restTimeMin;
		}
		return restTime;
	}
	
	public Integer getBusOccupancy(int runTimeNum,int direction) {
		int hour = runTimeNum / 2;
		String min = "00";
		if (runTimeNum % 2 != 0) {
			min = "30";
		}
		Calendar runTime = DateUtil.getCalendarHM(String.valueOf(hour),min);
		Integer defaultBusOccupancy=null;
		if(isLoopLine)
			direction=Direction.UP.getValue();
		else {
			direction=1-direction;
		}
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if(scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))) {
				defaultBusOccupancy=scheduleParamsAnchor.getBusOccupancy().intValue();
			}
			if(DateUtil.isInTimeRange(runTime.getTime(), DateUtil.getCalendarHM(scheduleParamsAnchor.getBeginTime()).getTime(),DateUtil.getCalendarHM(scheduleParamsAnchor.getEndTime()).getTime())
					&&scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))) {
				return scheduleParamsAnchor.getBusOccupancy().intValue();
			}
		}
		if(defaultBusOccupancy==null)
			System.out.println("aaaa");
		return defaultBusOccupancy;
	}
	
	public Integer getMinRestTime(Date tripEndTime,int direction) {
		Integer defaultMinRestTime=null;
		if(isLoopLine)
			direction=Direction.UP.getValue();
		else {
			if(getFirstTime(direction)==null) {//单边发车
				direction=1-direction;
			}
		}
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if(scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))) {
				defaultMinRestTime=scheduleParamsAnchor.getAnchorDurationMin().intValue();
				if(!isLoopLine) {
					if(tripEndTime.before(getFirstTime(direction))) {//到达时间比头车早，去第一个时段停站时间
						return defaultMinRestTime;
					}
				}
			}
			if(tripEndTime==null)
				System.err.println("aaaa");
			if(DateUtil.isInTimeRange(tripEndTime, DateUtil.getCalendarHM(scheduleParamsAnchor.getBeginTime()).getTime(),DateUtil.getCalendarHM(scheduleParamsAnchor.getEndTime()).getTime())
					&&scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))) {
				return scheduleParamsAnchor.getAnchorDurationMin().intValue();
			}
		}
		if(defaultMinRestTime==null){
			throw new MyException("-1", "缺失"+(direction==Direction.UP.getValue()?"上行":"下行")+DateFormatUtil.HM.getDateString(tripEndTime)+"停站时间配置");
		}
		return defaultMinRestTime;
	}
	
	public Integer getMaxRestTime(Date tripEndTime,int direction) {
		if(isLoopLine)
			direction=Direction.UP.getValue();
		Integer defaultMinRestTime=20;
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if(scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))
					&&DateUtil.isInTimeRange(tripEndTime, DateUtil.getDateHM(scheduleParamsAnchor.getBeginTime()),DateUtil.getDateHM(scheduleParamsAnchor.getEndTime()))) {
				return scheduleParamsAnchor.getAnchorDurationMax().intValue();
			}
		}
		return defaultMinRestTime;
	}
	
	public void setNextScheduleHalfHour() {
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			Date runTime=DateUtil.getDateHM(scheduleHalfHour.getRunTime());
			int direction=scheduleHalfHour.getDirection();
			ScheduleHalfHour preScheduleHalfHour=getScheduleHalfHour(DateUtil.getDateAddMinute(runTime, -30), direction);
			if(preScheduleHalfHour==null)
				preScheduleHalfHour=getScheduleHalfHour(DateFormatUtil.HM.getDateString(getFirstTime(direction)), direction);
			if(preScheduleHalfHour!=null)
				preScheduleHalfHour.setNextScheduleHalfHour(scheduleHalfHour);
			scheduleHalfHour.setPreScheduleHalfHour(preScheduleHalfHour);
		}
	}	

	public ScheduleHalfHour addDispatchRule(String hmBegin,String hmEnd,int direction,int longClassesNumber,int minLongClassesNumber,ShortLineSchedule shortLineSchedule,EmptyBusCutOver emptyBusCutOver) {
		if(hmBegin.startsWith("07")) {
			System.out.println("aa");
		}
		Calendar calendar=DateUtil.getCalendarHM(hmBegin);
		ScheduleHalfHour scheduleHalfHour=new ScheduleHalfHour(hmBegin, hmEnd, direction);
		Date firstTime=getFirstTime(direction);
		Date latestTime=getLatestTime(direction);
		Date runTimeBegin=DateUtil.getDateHM(hmBegin);
		Date runTimeEnd=DateUtil.getDateHM(hmEnd);
		if(!DateUtil.isInTimeRange(runTimeBegin, firstTime, latestTime)) {
			runTimeBegin=DateUtil.getDateAddDay(runTimeBegin, 1);
			if(DateUtil.isInTimeRange(runTimeBegin, firstTime, latestTime)) {
				scheduleHalfHour.setRunTimeDate(runTimeBegin);
			}
		}else {
			scheduleHalfHour.setRunTimeDate(runTimeBegin);
		}
		if(!DateUtil.isInTimeRange(runTimeEnd, firstTime, latestTime)&&
			!runTimeEnd.equals(latestTime)) {
			runTimeEnd=DateUtil.getDateAddDay(runTimeEnd, 1);
			if(DateUtil.isInTimeRange(runTimeEnd, firstTime, latestTime)||
				runTimeEnd.equals(latestTime)) {
				scheduleHalfHour.setRunTimeEndDate(runTimeEnd);
			}
		}else {
			scheduleHalfHour.setRunTimeEndDate(runTimeEnd);
		}
		calendar.add(Calendar.MINUTE, -30);
		ScheduleHalfHour preScheduleHalfHour=getScheduleHalfHour(DateFormatUtil.HM.getDateString(calendar.getTime()), direction);
		if(preScheduleHalfHour==null)
			preScheduleHalfHour=getScheduleHalfHour(DateFormatUtil.HM.getDateString(getFirstTime(direction)), direction);
		if(preScheduleHalfHour!=null)
			preScheduleHalfHour.setNextScheduleHalfHour(scheduleHalfHour);
		scheduleHalfHour.setShortLineSchedule(shortLineSchedule);
		scheduleHalfHour.setEmptyBusCutOver(emptyBusCutOver);
		scheduleHalfHourList.add(scheduleHalfHour);
		
		calendar=DateUtil.getCalendarHM(hmBegin);
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		DispatchRule dispatchRule=null;
		for(DispatchRule dr:dispatchRuleList) {
			if(hour==dr.getHour()&&direction==dr.getDirection()) {
				dispatchRule=dr;
				break;
			}
		}
		if(dispatchRule==null) {
			dispatchRule=new DispatchRule(hour, direction);
			dispatchRuleList.add(dispatchRule);
		}
		int minute=calendar.get(Calendar.MINUTE);
		scheduleHalfHour.setFullClassesNumber(longClassesNumber);
		if(minute<30) {//前半小时
			dispatchRule.setFirstHalfMinClassesNumber(minLongClassesNumber);//设置长线班次
			dispatchRule.setFirstHalfClassesNumber(longClassesNumber);
			dispatchRule.setFirstHalfScheduleHalfHour(scheduleHalfHour);
			dispatchRule.setRunTimeBegin(scheduleHalfHour.getRunTime());
			if(dispatchRule.getRunTimeEnd()==null)
				dispatchRule.setRunTimeEnd(scheduleHalfHour.getRunTimeEnd());
		}else {
			dispatchRule.setLastHalfMinClassesNumber(minLongClassesNumber);
			dispatchRule.setLastHalfClassesNumber(longClassesNumber);
			dispatchRule.setLastHalfScheduleHalfHour(scheduleHalfHour);
			dispatchRule.setRunTimeEnd(scheduleHalfHour.getRunTimeEnd());
			if(dispatchRule.getRunTimeBegin()==null)
				dispatchRule.setRunTimeBegin(scheduleHalfHour.getRunTime());
			if(preScheduleHalfHour!=null&&preScheduleHalfHour.getRunTime().endsWith("00")) {
				if(longClassesNumber-preScheduleHalfHour.getFullClassesNumber()==1) {//后面半小时比前面多一班，换过来保证均匀
					preScheduleHalfHour.setFullClassesNumber(longClassesNumber);
					scheduleHalfHour.setFullClassesNumber(longClassesNumber-1);
				}
			}
		}
		return scheduleHalfHour;
	}
	
	public void removeScheduleHalfHour(ScheduleHalfHour scheduleHalfHour) {
		scheduleHalfHourList.remove(scheduleHalfHour);
	}
	
	public int getWasteTimeAvg(int direction) {
		int sum=0;
		int count=0;
		for(RouteWasteTime routeWasteTime:routeWasteTimeList) {
			if(routeWasteTime.getDirection().equals(String.valueOf(direction))) {
				sum+=routeWasteTime.getWasteTime();
				count++;
			}
		}
		int avg=sum/count;
		return avg;
	}
	
	public int getWasteTime(String runTime,int direction) {
		//正点取00、15、30的最大值、半点取30、45、00+1的最大值
		int wasteTime=0;
		Calendar calendar=DateUtil.getCalendarHM(runTime);
		for(RouteWasteTime routeWasteTime:routeWasteTimeList) {
			if(routeWasteTime.getDirection().equals(String.valueOf(direction))) {
				int runTimeNum=routeWasteTime.getRunTimeNum();
				if(runTimeNum/4==calendar.get(Calendar.HOUR_OF_DAY)) {
					if(calendar.get(Calendar.MINUTE)<30) {//整点
						if(runTimeNum%4<3&&routeWasteTime.getWasteTime()>wasteTime) {//00、15、30三个取最大值
							wasteTime=routeWasteTime.getWasteTime();
						}
					}else {
						if(runTimeNum%4>1&&routeWasteTime.getWasteTime()>wasteTime) {//30、45取最大值
							wasteTime=routeWasteTime.getWasteTime();
						}
					}
				}
			}
		}
		if(calendar.get(Calendar.MINUTE)>=30) {//当前时段是30分
			int hour=calendar.get(Calendar.HOUR_OF_DAY)+1;
			for(RouteWasteTime routeWasteTime:routeWasteTimeList) {//在取下个整点运行时间比较
				if(routeWasteTime.getDirection().equals(String.valueOf(direction))&&routeWasteTime.getRunTimeNum()==hour*4) {
					if(routeWasteTime.getWasteTime()>wasteTime) {
						wasteTime=routeWasteTime.getWasteTime();
						break;
					}
				}
			}
		}
		if(wasteTime==0) {
			wasteTime=getWasteTime(calendar.getTime(), direction);
		}
		return wasteTime;
	}
	
	public RouteStaTurn getShortLineRouteStaTurn(Date runTime,int direction,int busNumber) {
		List<RouteStaTurn> routeStaTurnList=getRouteStaTurnList(direction);
		if(routeStaTurnList.isEmpty())
			return null;
		List<RouteStationPassenger> routeStationPassengerList=getRouteStationPassenger(runTime, direction);
		if(routeStationPassengerList!=null) {
			//scheduleParam.setVehicleContent
			RouteStationPassenger routeStationPassengerHigh=getHighSectionPassenger(direction, runTime);
			boolean pass=false;
			int stationNumberLow=0;
			Long lastRouteStaId=null;
			int passengerNumberLow=(int)(vehicleContent*0.3)*busNumber;
			for(RouteStationPassenger routeStationPassenger:routeStationPassengerList) {
				if(routeStationPassenger==routeStationPassengerHigh) {
					pass=true;
					continue;
				}
				if(pass) {
					if(routeStationPassenger.getCurrentNumber()<passengerNumberLow) {
						if(stationNumberLow==0) {
							lastRouteStaId=routeStationPassenger.getRouteStaId();
						}
						stationNumberLow++;
						if(stationNumberLow==5)//连续五个站低于30%
							break;
					}else {
						stationNumberLow=0;//高过30%，重新算
					}
				}
			}
			Map<Long,RouteStaTurn> routeStaTurnMap=new HashMap<Long,RouteStaTurn>();//所有调头点
			RouteStationPassenger highSectionPassengerReverse=null;
			try {
				highSectionPassengerReverse=getHighSectionPassenger(1-direction, DateFormatUtil.HM.getDateString(runTime));
			} catch (MyException e) {}
			for(RouteStaTurn routeStaTurn:routeStaTurnList) {
				if(highSectionPassengerReverse!=null&&highSectionPassengerReverse.getCurrentNumber()>routeStationPassengerHigh.getCurrentNumber()) {//反向客流大
					Long nextFirstRouteStaId=routeStaTurn.getNextFirstRouteStaId();
					RouteSta nextFirstRouteSta=getRouteSta(nextFirstRouteStaId);
					RouteSta highSectionRouteSta=getRouteSta(highSectionPassengerReverse.getRouteStaId());
					if(nextFirstRouteSta==null||highSectionRouteSta==null)
						continue;
					if(nextFirstRouteSta.getOrderNumber()>highSectionRouteSta.getOrderNumber())//在反向高断面前调头
						continue;
				}
				routeStaTurnMap.put(routeStaTurn.getLastRouteStaId(),routeStaTurn);
			}
			int stationMark=StationMark.UP_TRIP.getValue();
			if(direction==Direction.DOWN.getValue())
				stationMark=StationMark.DOWN_TRIP.getValue();
			pass=false;
			for(RouteSta routeSta:routeStaList) {
				if(routeSta.getStationMark()!=stationMark)
					continue;
				if(routeSta.getRouteStationId().equals(lastRouteStaId)) {
					pass=true;
				}
				if(pass) {
					if(routeStaTurnMap.containsKey(routeSta.getRouteStationId()))
						return routeStaTurnMap.get(routeSta.getRouteStationId());
				}
			}
		}
		return null;
	}
	
	public List<RouteStationPassenger> getRouteStationPassenger(Date runTime,int direction) {
		RouteStationPassengerInfo routeStationPassengerInfo=passengerInfoMap.get(direction);
		if(routeStationPassengerInfo!=null) {
			int runTimeNum=DateUtil.getTimeMinute(runTime)/30;
			for(List<RouteStationPassenger> routeStationPassengerList:routeStationPassengerInfo.getRouteStationPassenger2DList()) {
				for(RouteStationPassenger routeStationPassenger:routeStationPassengerList) {
					if(routeStationPassenger.getRunTimeNum()==runTimeNum) {
						return routeStationPassengerList;
					}
				}
			}
		}
		return null;
	}
	
	public int getBusNumberSingle() {
		return singleBusNumberConfig;
	}
	
	public int getBusNumberConfig() {
		return busNumberConfig;
	}
	
	public void setBusNumberSingle(int singleBusNumberConfig) {
		this.singleBusNumberConfig=singleBusNumberConfig;
	}
	
	public void setBusNumberConfig(int busNumberConfig) {
		this.busNumberConfig=busNumberConfig;
	}
	
	public int getBusConfig(ScheduleHalfHour scheduleHalfHour) {
		if(busConfigHalfHour.get(scheduleHalfHour)==null)
			System.out.println("aaaa");
		return busConfigHalfHour.get(scheduleHalfHour);
	}
	
	public void setBusConfig(ScheduleHalfHour scheduleHalfHour,int busNumber) {
		if(busConfigHalfHour==null)
			System.out.println("aaaa");
		busConfigHalfHour.put(scheduleHalfHour,busNumber);
	}
	
	public DispatchRule getDispatchRule(String hm,int direction) {
		int hour=Integer.valueOf(hm.substring(0, 2));
		for(DispatchRule dispatchRule:dispatchRuleList) {
			if(dispatchRule.getHour()==hour&&dispatchRule.getDirection()==direction) {
				return dispatchRule;
			}
		}
		return null;
	}

	public void printObuTime(int direction) {
		List<Date> timeList = new ArrayList<>();
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			if (shf.getDirection() != direction) {
				continue;
			}
			DispatchRule dispatchRule=getDispatchRule(shf.getRunTime(), shf.getDirection());
			timeList.addAll(dispatchRule.getObuTimeList(shf));
		}
		for (Date date : timeList) {
			System.out.println(direction + "\t" + DateFormatUtil.HM2.getDateString(date));
		}
	}

	public void generateObuTime() {
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			DispatchRule dispatchRule=getDispatchRule(shf.getRunTime(), shf.getDirection());
			List<Date> obuTimeList=dispatchRule.getObuTimeList(shf);
			shf.setObuTimeList(obuTimeList);
		}
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			DispatchRule dispatchRule=getDispatchRule(shf.getRunTime(), shf.getDirection());
			int classesNumber=0;
			for(Date obuTime:shf.getObuTimeList()) {
				if(DateUtil.isInTimeRange(obuTime, shf.getRunTimeDate(), shf.getRunTimeEndDate())) {
					classesNumber++;
				}
			}
			if(classesNumber==0)
				System.out.println("aaa");
			dispatchRule.setClassesNumber(shf.getRunTime(), classesNumber);
		}
	}
	
	/*public void generateObuTime(String runTime,int direction) {
		DispatchRule dispatchRule=getDispatchRule(runTime, direction);
		ScheduleHalfHour shf=getScheduleHalfHour(runTime, direction);
		List<Date> obuTimeList=dispatchRule.getObuTimeList(runTime);
		shf.setObuTimeList(obuTimeList);
		dispatchRule.setClassesNumber(shf.getRunTime(), obuTimeList.size());
	}*/
	
	public ScheduleHalfHour getScheduleHalfHourNext(String runTime,int direction) {
		boolean flag=false;
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			if(flag&&shf.getDirection()==direction)
				return shf;
			if(runTime.equals(shf.getRunTime())&&shf.getDirection()==direction) {
				flag=true;
			}
		}
		return null;
	}
	
	public ScheduleHalfHour getScheduleHalfHour(String runTime,int direction) {
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			if(runTime.equals(shf.getRunTime())&&shf.getDirection()==direction) {
				return shf;
			}
		}
		return null;
	}
	
	public ScheduleHalfHour getScheduleHalfHour(Date tripBeginTime,int direction) {
		for(ScheduleHalfHour shf:scheduleHalfHourList) {
			if(shf.getDirection()==direction) {
				Date runTimeBegin=DateUtil.getCalendarHM(shf.getRunTime()).getTime();
				Date runTimeEnd=DateUtil.getCalendarHM(shf.getRunTimeEnd()).getTime();
				if(DateUtil.isInTimeRange(tripBeginTime, runTimeBegin, runTimeEnd)) {
					return shf;
				}
			}
		}
		return null;
	}

	public List<ScheduleHalfHour> getScheduleHalfHourList() {
		return scheduleHalfHourList;
	}
	
	public List<ScheduleHalfHour> getScheduleHalfHourList(int direction) {
		List<ScheduleHalfHour> scheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		for(ScheduleHalfHour scheduleHalfHour:this.scheduleHalfHourList) {
			if(scheduleHalfHour.getDirection()==direction) {
				scheduleHalfHourList.add(scheduleHalfHour);
			}
		}
		return scheduleHalfHourList;
	}
	
	public void setScheduleHalfHourList(List<ScheduleHalfHour> scheduleHalfHourList) {
		this.scheduleHalfHourList=scheduleHalfHourList;
	}
	
	public boolean isFixedWasteTime() {
		Integer minRunDurationUp=scheduleParamsRoute.getUpDriverTimeStart();
		Integer maxRunDurationUp=scheduleParamsRoute.getUpDriverTimeEnd();
		Integer minRunDurationDown=scheduleParamsRoute.getDownDriverTimeStart();
		Integer maxRunDurationDown=scheduleParamsRoute.getDownDriverTimeEnd();
		if(isLoopLine) {
			if(isLoopLineDouble()||isTwoLoopLine()) {
				if(minRunDurationUp!=null&&minRunDurationUp.equals(maxRunDurationUp)&&
					minRunDurationDown!=null&&minRunDurationDown.equals(maxRunDurationDown)) {
					return true;
				}
			}else {
				if(minRunDurationUp!=null&&minRunDurationUp.equals(maxRunDurationUp)){
					return true;
				}
			}
		}else {
			if(minRunDurationUp!=null&&minRunDurationUp.equals(maxRunDurationUp)&&
				minRunDurationDown!=null&&minRunDurationDown.equals(maxRunDurationDown)) {
				return true;
			}
		}
		return false;
	}
	
	public void setRouteWasteTimeListOrig(List<RouteWasteTime> routeWasteTimeList) {
		RouteWasteTime routeWasteTimeLast=null;
		for(int i=routeWasteTimeList.size()-1;i>=0;i--) {
			RouteWasteTime routeWasteTime=routeWasteTimeList.get(i);
			short wasteTime=(short)(routeWasteTime.getWasteTime());
			if(routeWasteTimeLast!=null) {
				if(routeWasteTime.getDirection().equals(routeWasteTimeLast.getDirection())) {
					if(routeWasteTime.getWasteTime()-routeWasteTimeLast.getWasteTime()>10) {
						wasteTime=(short)(routeWasteTimeLast.getWasteTime()+10);
						routeWasteTime.setWasteTime(wasteTime);
					}
				}
			}
			routeWasteTime.setWasteTime(wasteTime);
			routeWasteTimeLast=routeWasteTime;
		}
		this.routeWasteTimeList = routeWasteTimeList;
	}

	public void setRouteWasteTimeList(List<RouteWasteTime> routeWasteTimeList) {
		RouteWasteTime routeWasteTimeLast=null;
		Integer minRunDurationUp=scheduleParamsRoute.getUpDriverTimeStart();
		Integer maxRunDurationUp=scheduleParamsRoute.getUpDriverTimeEnd();
		Integer minRunDurationDown=scheduleParamsRoute.getDownDriverTimeStart();
		Integer maxRunDurationDown=scheduleParamsRoute.getDownDriverTimeEnd();
		for(int i=routeWasteTimeList.size()-1;i>=0;i--) {
			RouteWasteTime routeWasteTime=routeWasteTimeList.get(i);
			short wasteTime=(short)(routeWasteTime.getWasteTime());
			if(routeWasteTimeLast!=null) {
				if(routeWasteTime.getDirection().equals(routeWasteTimeLast.getDirection())) {
					if(routeWasteTime.getWasteTime()-routeWasteTimeLast.getWasteTime()>10) {
						wasteTime=(short)(routeWasteTimeLast.getWasteTime()+10);
						routeWasteTime.setWasteTime(wasteTime);
					}
				}
			}
			if(routeWasteTime.getDirection().equals(Direction.UP.getStringValue())) {
				if(minRunDurationUp!=null&&wasteTime<minRunDurationUp) {
					wasteTime=minRunDurationUp.shortValue();
				}
				if(maxRunDurationUp!=null&&wasteTime>maxRunDurationUp) {
					wasteTime=maxRunDurationUp.shortValue();
				}
			}else {
				if(minRunDurationDown!=null&&wasteTime<minRunDurationDown) {
					wasteTime=minRunDurationDown.shortValue();
				}
				if(maxRunDurationDown!=null&&wasteTime>maxRunDurationDown) {
					wasteTime=maxRunDurationDown.shortValue();
				}
			}
			routeWasteTime.setWasteTime(wasteTime);
			routeWasteTimeLast=routeWasteTime;
		}
		this.routeWasteTimeList = routeWasteTimeList;
	}
	
	public Date getArrivalTime(Date tripBeginTime,RouteStaTurn routeStaTurn) {
		int direction=routeStaTurn.getDirection();
		Date arrivalTime=getArrivalTime(tripBeginTime, direction, getFirstRouteStaId(direction), routeStaTurn.getLastRouteStaId());
		tripBeginTime=DateUtil.getDateAddMinute(arrivalTime, routeStaTurn.getTurnAroundTime());
		if(!isLoopLine)
			direction=1-direction;
		arrivalTime=getArrivalTime(tripBeginTime, direction, routeStaTurn.getNextFirstRouteStaId(), getLastRouteStaId(direction));
		return arrivalTime;
	}
	
	public int getWasteTime(Date tripBeginTime,RouteStaTurn routeStaTurn) {
		Date arrivalTime=getArrivalTime(tripBeginTime, routeStaTurn);
		int wasteTime=DateUtil.getMinuteInterval(tripBeginTime, arrivalTime);
		return wasteTime;
	}
	
	public Integer getWasteTime(Date tripBeginTime,int direction) {
		if (tripBeginTime == null) {
			return null;
		}
		if(tripBeginTime.after(getLatestTime(direction))) {//过了末班车时间
			tripBeginTime=getLatestTime(direction);
		}
		Calendar calendar0=DateUtil.getCalendarHour("0");
		int run_time = DateUtil.getMinuteInterval(tripBeginTime, calendar0.getTime())/15;
		RouteWasteTime routeWasteTime=getRouteWasteTime(direction, run_time);
		Integer run_duration=null;
		if(routeWasteTime==null) {
			if(!tripBeginTime.after(getLatestTime(direction))) {
				RouteWasteTime routeWasteTimePre=null;
				RouteWasteTime routeWasteTimeNext=null;
				for(int i=run_time-1;i>=0;i--) {
					routeWasteTimePre=getRouteWasteTime(direction, i);
					if(routeWasteTimePre!=null) 
						break;
				}
				for(int i=run_time+1;i<96;i++) {
					routeWasteTimeNext=getRouteWasteTime(direction, i);
					if(routeWasteTimeNext!=null) 
						break;
				}
				if(routeWasteTimePre!=null&&routeWasteTimeNext!=null) {
					run_duration=(routeWasteTimePre.getWasteTime()+routeWasteTimeNext.getWasteTime())/2;
				}else if(routeWasteTimePre!=null){
					run_duration=routeWasteTimePre.getWasteTime().intValue();
				}else if(routeWasteTimeNext!=null){
					run_duration=routeWasteTimeNext.getWasteTime().intValue();
				}
			}else {
				throw new MyException("-1", "大于末班时间,tripBeginTime = "+DateFormatUtil.HM.getDateString(tripBeginTime)+" 无周转时间  direction = " + direction);
			}
		}else {
			run_duration=routeWasteTime.getWasteTime().intValue();
		}
		if(run_duration==null)
			System.out.println();
		
		long minute=DateUtil.getMinuteInterval(tripBeginTime, calendar0.getTime());
		if(minute%15!=0&&run_duration!=null) {//不是刚好踏到点
			RouteWasteTime routeWasteTimeNext=getRouteWasteTime(direction, run_time+1);
			if(routeWasteTimeNext!=null) {
				Double run_duration_long=(((minute%15)*1.00/15)*(routeWasteTimeNext.getWasteTime()-run_duration)+run_duration);
				run_duration=run_duration_long.intValue();
			}
		}
		if(run_duration==null){
			if(direction==Direction.UP.getValue()) {
				Integer minRunDurationUp=scheduleParamsRoute.getUpDriverTimeStart();
				Integer maxRunDurationUp=scheduleParamsRoute.getUpDriverTimeEnd();
				if(minRunDurationUp==maxRunDurationUp) {
					run_duration=minRunDurationUp;
				}
			}else if(direction==Direction.DOWN.getValue()) {
				Integer minRunDurationDown=scheduleParamsRoute.getDownDriverTimeStart();
				Integer maxRunDurationDown=scheduleParamsRoute.getDownDriverTimeEnd();
				if(minRunDurationDown==maxRunDurationDown) {
					run_duration=minRunDurationDown;
				}
			}
		}
		if(run_duration==null) {
			Integer diff=null;
			for(RouteWasteTime rwt:routeWasteTimeList) {
				int diffTemp=DateUtil.getMinuteInterval(DateUtil.getTimeByMinute(rwt.getRunTimeNum()*15), DateUtil.getDateHM(DateFormatUtil.HM.getDateString(tripBeginTime)));
				if(diff==null||diffTemp<diff) {
					run_duration=rwt.getWasteTime().intValue();
					diff=diffTemp;
				}
			}
		}
		if(run_duration==null){
			throw new MyException("-1", DateFormatUtil.HM.getDateString(tripBeginTime)+"周转时间不存在，请在线路管理重新同步");
		}
		return run_duration;
	}
	
	public Date getArrivalTime(Date tripBeginTime,int direction) {
		Integer run_duration=getWasteTime(tripBeginTime, direction);
		if(run_duration==null)
			return null;
		if(!isLoopLine) {//非环线
			if(tripBeginTime.equals(getFirstTime(direction))&&!isAppearDirection(1-direction)) {//头车,单边出车
				if(getFirstTime(1-direction)!=null&&getFirstTime(direction).before(getFirstTime(1-direction))) {
					int interval=DateUtil.getMinuteInterval(getFirstTime(direction), getFirstTime(1-direction));//两边头车间隔
					if(run_duration>interval) {//耗时大于间隔
						if(run_duration-interval<5) {
							run_duration=interval;
						}
					}
				}
			}
		}
		Date arrivalTime=DateUtil.getDateAddMinute(tripBeginTime, run_duration);
		return arrivalTime;
	}
	
	public int getWasteTime(Date tripBeginTime,int direction,Long firstRouteStaId,Long lastRouteStaId) {
		Integer wasteTime=null;
		if(scheduleParamPreset!=null&&scheduleParamPreset.getReferenceRunDate()!=null) {
			Date obuTime=DateUtil.getDate(scheduleParamPreset.getReferenceRunDate(), tripBeginTime);
			wasteTime=simulationService.getWasteTime(routeId, firstRouteStaId, lastRouteStaId, obuTime, null, null, null);
		}else {
			IntersiteTimeParams intersiteTimeParams=new IntersiteTimeParams();
			intersiteTimeParams.setRouteId(String.valueOf(routeId));
			intersiteTimeParams.setDirection(String.valueOf(direction));
			intersiteTimeParams.setFromRouteStaId(firstRouteStaId.intValue());
			if(lastRouteStaId==null)
				System.out.println("aaa");
			intersiteTimeParams.setToRouteStaId(lastRouteStaId.intValue());
			intersiteTimeParams.setAdTime(DateUtil.getDate(runDate, tripBeginTime));
			int wastTimeFull=getWasteTime(tripBeginTime, direction);
			intersiteTimeParams.setWasteTime(wastTimeFull);
			intersiteTimeParams.setRouteFromId(getFirstRouteStaId(direction).intValue());
			intersiteTimeParams.setRouteToId(getLastRouteStaId(direction).intValue());
			wasteTime=simulationService.getIntersiteTime(intersiteTimeParams).intValue();

		}
		wasteTime=wasteTime/60;
		return wasteTime;
	}
	
	public Date getArrivalTime(Date tripBeginTime,int direction,Long firstRouteStaId,Long lastRouteStaId) {
		if(lastRouteStaId==null)
			System.out.println("aaa");
		int wasteTime=getWasteTime(tripBeginTime, direction, firstRouteStaId, lastRouteStaId);
		//wasteTime=25*60;
		Date arrivalTime=DateUtil.getDateAddMinute(tripBeginTime, wasteTime);
		return arrivalTime;
	}
	
	private RouteWasteTime getRouteWasteTime(int direction,int run_time) {
		for(RouteWasteTime routeWasteTime:routeWasteTimeList) {
			if(routeWasteTime.getRunTimeNum()==run_time&&routeWasteTime.getDirection().equals(String.valueOf(direction))) {
				return routeWasteTime;
			}
		}
		return null;
	}
	
	public Date getLatestTime(int direction) {
		if(direction==0) {
			Calendar firstTime=DateUtil.getCalendarHM(upFirstTime);
			Calendar latestTime=DateUtil.getCalendarHM(upLatestTime);
			if(firstTime.getTime().after(latestTime.getTime())) {//末班车加一天
				latestTime.add(Calendar.DATE, 1);
			}else if(latestTime.getTime().before(DateUtil.getDateHM("0400"))) {//末班车早于0400
				latestTime.add(Calendar.DATE, 1);
			}
			return latestTime.getTime();
		}else {
			if(downLatestTime!=null) {
				Calendar firstTime=DateUtil.getCalendarHM(downFirstTime);
				Calendar latestTime=DateUtil.getCalendarHM(downLatestTime);
				if(firstTime.getTime().after(latestTime.getTime())) {//末班车加一天
					latestTime.add(Calendar.DATE, 1);
				}else if(latestTime.getTime().before(DateUtil.getDateHM("0400"))) {//末班车早于0400
					latestTime.add(Calendar.DATE, 1);
				}
				return latestTime.getTime();
			}
		}
		return null;
	}

	public Date getLatestTimeDriverless(int direction) {
		Date latestTimeMain = getLatestTime(direction);
		if (latestTimeMain == null) {
			return null;
		}
		if(direction==0 && getScheduleParamsDriverless().getUpFirstTime()!=null && getScheduleParamsDriverless().getUpLatestTime()!=null) {
			Calendar firstTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getUpFirstTime());
			Calendar latestTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getUpLatestTime());
			if(firstTime.getTime().after(latestTime.getTime())) {//末班车加一天
				latestTime.add(Calendar.DATE, 1);
			}else if(latestTime.getTime().before(DateUtil.getDateHM("0400"))) {//末班车早于0400
				latestTime.add(Calendar.DATE, 1);
			}
			return latestTime.getTime();
		} else {
			if(getScheduleParamsDriverless().getDownFirstTime()!=null && getScheduleParamsDriverless().getDownLatestTime()!=null) {
				Calendar firstTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getDownFirstTime());
				Calendar latestTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getDownLatestTime());
				if(firstTime.getTime().after(latestTime.getTime())) {//末班车加一天
					latestTime.add(Calendar.DATE, 1);
				}else if(latestTime.getTime().before(DateUtil.getDateHM("0400"))) {//末班车早于0400
					latestTime.add(Calendar.DATE, 1);
				}
				return latestTime.getTime();
			}
		}
		return latestTimeMain;
	}
	
	public String getFirstTimeStr(int direction) {
		String firstTime=null;
		if(direction==0) {
			firstTime=upFirstTime;
		}else if(direction==1){
			firstTime=downFirstTime;
		}
		if(firstTime!=null) {
			firstTime=firstTime.replaceAll(":", "");
		}
		return firstTime;
	}
	
	public String getLatestTimeStr(int direction) {
		String latestTime=null;
		if(direction==0) {
			latestTime=upLatestTime;
		}else if(direction==1){
			latestTime=downLatestTime;
		}
		if(latestTime!=null) {
			latestTime=latestTime.replaceAll(":", "");
		}
		return latestTime;
	}
	
	public Date getFirstTime(int direction) {
		Date firstTime=null;
		if(direction==0&&upFirstTime!=null) {
			firstTime=DateUtil.getCalendarHM(upFirstTime).getTime();
		}else if(direction==1&&downFirstTime!=null){
			firstTime=DateUtil.getCalendarHM(downFirstTime).getTime();
		}
		if(firstTime!=null&&firstTime.before(DateUtil.getDateHM("0400"))) {//首班车早于0400
			firstTime=DateUtil.getDateAddDay(firstTime, 1);
		}
		return firstTime;
	}

	public Date getFirstTimeDriverless(int direction) {
		Date firstTimeMain = getFirstTime(direction);
		if (firstTimeMain == null) {
			return null;
		}
		Date firstTime=null;
		if(direction==0&&getScheduleParamsDriverless().getUpFirstTime()!=null) {
			firstTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getUpFirstTime()).getTime();
		}else if(direction==1&&getScheduleParamsDriverless().getDownFirstTime()!=null){
			firstTime=DateUtil.getCalendarHM(getScheduleParamsDriverless().getDownFirstTime()).getTime();
		}
		if(firstTime!=null&&firstTime.before(DateUtil.getDateHM("0400"))) {//首班车早于0400
			firstTime=DateUtil.getDateAddDay(firstTime, 1);
		}
		if (firstTime == null) {
			firstTime = firstTimeMain;
		}
		return firstTime;
	}
	
	/*public Date getLastRoundBeginTime(int direction) {
		Calendar calendar=null;
		if(direction==0)
			calendar=DateUtil.getCalendarHM(upLatestTime);
		else {
			calendar=DateUtil.getCalendarHM(downLatestTime);
		}
		calendar.add(Calendar.MINUTE, -60);
		return calendar.getTime();
	}*/

	public Date getMiddleStopOffDutyBeginTime() {
		return DateUtil.getCalendarHM(scheduleParamsSingle.getEndBeginTime()).getTime();
	}

	public Date getMiddleStopBeginTime() {
		if(scheduleParamsSingle.getStopBeginTime()!=null)
			return DateUtil.getCalendarHM(scheduleParamsSingle.getStopBeginTime()).getTime();
		return null;
	}
	
	public void setMiddleStopBeginTime(Date middleStopBeginTime) {
		scheduleParamsSingle.setStopBeginTime(DateFormatUtil.HM.getDateString(middleStopBeginTime));
	}

	public Date getMiddleStopEndTime() {
		if(scheduleParamsSingle.getStopEndTime()!=null)
			return DateUtil.getCalendarHM(scheduleParamsSingle.getStopEndTime()).getTime();
		return null;
	}

	public int getMiddleStopBeginOrderNumber() {
		return Integer.valueOf(scheduleParamsSingle.getEarliestBusNumber());
	}
	
	public void setMiddleStopBeginOrderNumber(int earliestBusNumber) {
		scheduleParamsSingle.setEarliestBusNumber(String.valueOf(earliestBusNumber));
	}
	
	public int getMiddleStopMinimumRestTime() {
		Integer minimumRestTime=scheduleParamsSingle.getMinimumRestTime();
		if(minimumRestTime==null)
			return 120;
		return minimumRestTime;
	}
	
	public boolean isAppearDirection(int direction) {
		if(direction==Direction.UP.getValue()) {
			if("1".equals(scheduleParamsRoute.getUpDirection())) {
				return true;
			}
		}else if(direction==Direction.DOWN.getValue()) {
			if("1".equals(scheduleParamsRoute.getDownDirection())) {
				return true;
			}
		}
		return false;
	}
	
	public void setAppearDirection(int direction,boolean isAppear) {
		if(direction==Direction.UP.getValue()) {
			scheduleParamsRoute.setUpDirection(isAppear?"1":"0");
		}else if(direction==Direction.DOWN.getValue()) {
			scheduleParamsRoute.setDownDirection(isAppear?"1":"0");
		}
	}
	
	public void setIsBackInsert(Integer isBackInsert) {
		scheduleParamsRoute.setIsBackInsert(isBackInsert);
	} 

	public Date getFirstPlanObuTimeLatest(int direction) {
		if(direction==0) {
			if("1".equals(scheduleParamsRoute.getUpDirection())) {
				if(scheduleParamsRoute.getUpLastestBeginTime()!=null)
					return DateUtil.getCalendarHM(scheduleParamsRoute.getUpLastestBeginTime()).getTime();
			}
		}else {
			if("1".equals(scheduleParamsRoute.getDownDirection())) {
				if(scheduleParamsRoute.getDownLastestBeginTime()!=null)
					return DateUtil.getCalendarHM(scheduleParamsRoute.getDownLastestBeginTime()).getTime();
			}
		}
		return null;
	}
	
	public void setFirstPlanObuTimeLatest(int direction,Date date) {
		if(direction==Direction.UP.getValue()) {
			if("1".equals(scheduleParamsRoute.getUpDirection())) {
				scheduleParamsRoute.setUpLastestBeginTime(DateFormatUtil.HM.getDateString(date));
			}
		}else {
			if("1".equals(scheduleParamsRoute.getDownDirection())) {
				scheduleParamsRoute.setDownLastestBeginTime(DateFormatUtil.HM.getDateString(date));
			}
		}
	}
	
	public void setFirstPlanObuTimeLatest() {
		if(isLoopLine()) {
			Date firstTime=getFirstTime(Direction.UP.getValue());
			Date arrivalTime=getArrivalTime(firstTime, Direction.UP.getValue());
		    int restTime=getMinRestTime(arrivalTime, Direction.UP.getValue());
		    Date minObuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		    setFirstPlanObuTimeLatest(Direction.UP.getValue(), minObuTime);
		}else {
			Date firstTime=getFirstTime(Direction.DOWN.getValue());//下行首班
			if(firstTime!=null) {
				Date arrivalTime=getArrivalTime(firstTime, Direction.DOWN.getValue());
			    int restTime=getMinRestTime(arrivalTime, Direction.UP.getValue());
			    Date minObuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
			    if(minObuTime.before(getFirstTime(Direction.UP.getValue()))) {//早于对面头车
			    	minObuTime=getFirstTime(Direction.UP.getValue());
			    }
			    setFirstPlanObuTimeLatest(Direction.UP.getValue(), minObuTime);
			    
			    firstTime=getFirstTime(Direction.UP.getValue());//下行首班
			    arrivalTime=getArrivalTime(firstTime, Direction.UP.getValue());
			    restTime=getMinRestTime(arrivalTime, Direction.DOWN.getValue());
			    minObuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
			    if(minObuTime.before(getFirstTime(Direction.DOWN.getValue()))) {//早于对面头车
			    	minObuTime=getFirstTime(Direction.DOWN.getValue());
			    }
			    setFirstPlanObuTimeLatest(Direction.DOWN.getValue(), minObuTime);
			}
		}
	} 
	
	public boolean isBackInsert() {
		if(scheduleParamsRoute.getIsBackInsert()==1)
			return true;
		return false;
	}
	
	public Integer getEndDirection() {
		Integer endDirection=scheduleParamsRoute.getEndDirection();
		return endDirection;
	}
	
	public void setEndDirection(int endDirection) {
		scheduleParamsRoute.setEndDirection(endDirection);
	}

	public Date getMiddleStopRecoveryBeginTime() {
		return DateUtil.getCalendarHM(scheduleParamsSingle.getRunBeginTime()).getTime();
	}
	
	public Date getMiddleStopRecoveryBeginTime(int direction) {
		Date recoveryBeginTime=DateUtil.getCalendarHM(scheduleParamsSingle.getRunBeginTime()).getTime();
		if(scheduleParamsSingle.getRunBeginTime(direction)!=null) {
			recoveryBeginTime=scheduleParamsSingle.getRunBeginTime(direction);
		}
		return recoveryBeginTime;
	}
	
	/*public Date getMiddleStopBeginTimePre() {
		return DateUtil.getCalendarHM(middleStopBeginTimePre).getTime();
	}*/
	
	/*public Date getLunchBeginTime() {
		if(lunchEatParams!=null)
			return DateUtil.getCalendarHM(lunchEatParams.getBeginTime()).getTime();
		return null;
	}*/
	
	public Date getLunchBeginTime(int direction) {
		for(ScheduleParamsEat lunchEatParam:lunchEatParams) {
			if(lunchEatParam.getDirection().equals(String.valueOf(direction))||
				lunchEatParam.getDirection().equals(Direction.NODIRECTION.getStringValue())) {
				return DateUtil.getCalendarHM(lunchEatParam.getBeginTime()).getTime();
			}
		}
		return null;
	}
	
	/*public String getLunchDirection() {
		if(lunchEatParams!=null)
			return lunchEatParams.getDirection();
		return null;
	}*/
	
	public Date getLunchEndTime(int direction) {
		return DateUtil.getDateAdd(getLunchBeginTime(direction), Calendar.MINUTE, 60);
	}
	
	public Integer getLunchEatTime(int direction) {
		for(ScheduleParamsEat lunchEatParam:lunchEatParams) {
			if(lunchEatParam.getDirection().equals(String.valueOf(direction))||
				lunchEatParam.getDirection().equals(Direction.NODIRECTION.getStringValue())) {
				return lunchEatParam.getEatTime().intValue();
			}
		}
		return null;
	}
	
	/*public Date getLunchBeginTimePre() {
		return DateUtil.getCalendarHM(lunchBeginTimePre).getTime();
	}*/
	
	public Date getSupperBeginTime(int direction) {
		for(ScheduleParamsEat supperEatParam:supperEatParams) {
			if(supperEatParam.getDirection().equals(String.valueOf(direction))||
					supperEatParam.getDirection().equals(Direction.NODIRECTION.getStringValue())) {
				return DateUtil.getCalendarHM(supperEatParam.getBeginTime()).getTime();
			}
		}
		return null;
	}
	
	public Date getSupperEndTime(int direction) {
		Date supperBeginTime=getSupperBeginTime(direction);
		if(supperBeginTime!=null)
			return DateUtil.getDateAdd(getSupperBeginTime(direction), Calendar.MINUTE, 60);
		else {
			return null;
		}
	}
	
	public Integer getSupperEatTime(int direction) {
		for(ScheduleParamsEat supperEatParam:supperEatParams) {
			if(supperEatParam.getDirection().equals(String.valueOf(direction))||
				supperEatParam.getDirection().equals(Direction.NODIRECTION.getStringValue())) {
				return supperEatParam.getEatTime().intValue();
			}
		}
		return null;
	}
	
	/*public Date getUpLastRoundBeginTime() {
		return DateUtil.getCalendarHM(upLastRoundBeginTime).getTime();
	}
	
	public Date getDownLastRoundBeginTime() {
		return DateUtil.getCalendarHM(downLastRoundBeginTime).getTime();
	}*/
	
	public Date getOffDutyBeginTime() {
		if(scheduleParamsRoute.getEarlyEndTime()!=null)
			return DateUtil.getCalendarHM(scheduleParamsRoute.getEarlyEndTime()).getTime();
		throw new MyException("-1", "请设置最早收车时间");
	}
	
	public void setOffDutyBeginTime(Date offDutyBeginTime) {
		scheduleParamsRoute.setEarlyEndTime(DateFormatUtil.HM.getDateString(offDutyBeginTime));
	}
	
	public Integer getLeaveImmediatelyInterval(int direction) {
		if(direction==Direction.UP.getValue()) {
			return scheduleParamsRoute.getArriveStaStopTimeUp();
		}else if(direction==Direction.DOWN.getValue()) {
			return scheduleParamsRoute.getArriveStaStopTimeDown();
		}
		return null;
	}

	public Date getEarlyPeakBeginTime() {
		return DateUtil.getCalendarHM(earlyPeakBeginTime).getTime();
	}

	public Date getEarlyPeakEndTime() {
		return DateUtil.getCalendarHM(earlyPeakEndTime).getTime();
	}

	public Date getLatePeakBeginTime() {
		return DateUtil.getCalendarHM(latePeakBeginTime).getTime();
	}

	public Date getLatePeakEndTime() {
		return DateUtil.getCalendarHM(latePeakEndTime).getTime();
	}
	
	public List<ScheduleParamShift> getScheduleParamShift(){
		return scheduleParamsSingle.getShifts();
	}

	public Integer getMaxInterval(String runTime,int direction) {
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			if(scheduleParamsClasses.getDirection().equals(String.valueOf(direction))&&
				DateUtil.isInTimeRange(DateUtil.getDateHM(runTime), DateUtil.getDateHM(scheduleParamsClasses.getBeginTime()), 
						DateUtil.getDateHM(scheduleParamsClasses.getEndTime()))) {
				return scheduleParamsClasses.getMaxDispatchInterval().intValue();
			}
		}
		ScheduleParamsClasses scheduleParamsClasses=null;
		for(ScheduleParamsClasses scheduleParamsClassesTemp:scheduleParamsClassesList) {
			if(scheduleParamsClassesTemp.getDirection().equals(String.valueOf(direction))){
				if(scheduleParamsClasses==null) {
					scheduleParamsClasses=scheduleParamsClassesTemp;
				}else{
					Date a=DateUtil.getDateHM(runTime);
					Date b=DateUtil.getDateHM(scheduleParamsClassesTemp.getBeginTime());
					if(!a.before(b)){
						scheduleParamsClasses=scheduleParamsClassesTemp;
					}else {
						break;
					}
				}
			}
		}
		if(scheduleParamsClasses!=null)
			return scheduleParamsClasses.getMaxDispatchInterval().intValue();
		throw new MyException("-1", runTime+"\t"+(direction==Direction.UP.getValue()?"上行":"下行")+",请检查满载率和最低发班标准时间段设置是否缺漏");
	}
	
	public ScheduleParamsClasses getScheduleParamsClasses(Date date,int direction) {
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			if(scheduleParamsClasses.getDirection().equals(String.valueOf(direction))) {
				Date beginTime = DateUtil.getDateHM(scheduleParamsClasses.getBeginTime());
				Date endTime = DateUtil.getDateHM(scheduleParamsClasses.getEndTime());
				if(DateUtil.isInTimeRange(date, beginTime, endTime)) {
					return scheduleParamsClasses;
				}
			}
		}
		return null;
	}
	
	public List<ScheduleParamsClasses> getScheduleParamsClasses(int direction) {
		List<ScheduleParamsClasses> list=new ArrayList<ScheduleParamsClasses>();
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			if(scheduleParamsClasses.getDirection().equals(String.valueOf(direction))) {
				list.add(scheduleParamsClasses);
			}
		}
		return list;
	}
	
	public Integer getMaxInterval(Date date,int direction) {
		Date firstTime=getFirstTime(direction);
		Date latestTime=getLatestTime(direction);
		boolean isNightRoute=false;
		if(!DateFormatUtil.DATE.getDateString(firstTime).equals(DateFormatUtil.DATE.getDateString(latestTime))) {//跨天
			isNightRoute=true;
		}
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			if(scheduleParamsClasses.getDirection().equals(String.valueOf(direction))) {
				Date beginTime = DateUtil.getDateHM(scheduleParamsClasses.getBeginTime());
				Date endTime = DateUtil.getDateHM(scheduleParamsClasses.getEndTime());
				if(isNightRoute) {
					if(beginTime.before(firstTime)) {//开始时间是第二天
						beginTime=DateUtil.getDateAdd(beginTime,  Calendar.DATE, 1);
					}
					if(endTime.before(firstTime)) {//结束时间是第二天
						endTime=DateUtil.getDateAdd(endTime,  Calendar.DATE, 1);
					}
				}
				if (DateUtil.isInTimeRange(date, beginTime, endTime)) {
					return scheduleParamsClasses.getMaxDispatchInterval().intValue();
				}
			}
		}
		Integer maxInterval=null;
		for(ScheduleParamsClasses scheduleParamsClasses:scheduleParamsClassesList) {
			Date endTime=DateUtil.getDateHM(scheduleParamsClasses.getEndTime());
			if(scheduleParamsClasses.getDirection().equals(String.valueOf(direction))){
				maxInterval=scheduleParamsClasses.getMaxDispatchInterval().intValue();
				if(endTime.after(date)) {
					break;
				}
			}
		}
		if(maxInterval==null)
			throw new MyException("-1", DateFormatUtil.HM.getDateString(date)+"\t"+(direction==Direction.UP.getValue()?"上行":"下行")+",请检查满载率和最低发班标准时间段设置是否缺漏");
		return maxInterval;
	}
	
	public void setUpLatestTime(String upLatestTime) {
		this.upLatestTime = upLatestTime;
	}

	public void setDownLatestTime(String downLatestTime) {
		this.downLatestTime = downLatestTime;
	}

	public void setUpFirstTime(String upFirstTime) {
		this.upFirstTime = upFirstTime;
	}

	public void setDownFirstTime(String downFirstTime) {
		this.downFirstTime = downFirstTime;
	}

	public void scheduleHalfHourSort() {
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			DispatchRule dispatchRule=getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
			System.out.print(dispatchRule.getDirection()+"\t"+scheduleHalfHour.getRunTime()+"\t"+dispatchRule.getMinClassesNumber(scheduleHalfHour.getRunTime()));
			if(scheduleHalfHour.getShortLineSchedule()!=null) {
				System.out.println("\t"+scheduleHalfHour.getShortLineSchedule().getShortLineClasses());
			}else {
				System.out.println();
			}
		}
		Collections.sort(scheduleHalfHourList, new ScheduleHalfHourComparator());
	}

	public long getHighSectionPassenger(int direction, Date beginTime, Date endTime) {
		long passenger = 0l;
		while (beginTime.before(endTime)) {
			try {
				RouteStationPassenger high = getHighSectionPassenger(direction, beginTime);
				if (high != null && high.getCurrentNumber() != null) {
					passenger += high.getCurrentNumber();
				}
			} catch (Exception e) {

			}
			beginTime = DateUtil.getDateAddMinute(beginTime, 30);
		}
		return passenger;
	}

	public RouteStationPassenger getHighSectionPassenger(int direction,Date runTime) {
		return getHighSectionPassenger(direction,DateFormatUtil.HM.getDateString(runTime));
	}

	public RouteStationPassenger getHighSectionPassengerRealTime(int direction, Date runTime) {
		List<RouteStationPassenger> routeStationPassengerList = getRouteStationPassenger(runTime, direction);
		if(routeStationPassengerList==null || routeStationPassengerList.isEmpty()) {
			return null;
		}
		RouteStationPassenger highSectionPassenger = null;
		for(RouteStationPassenger passenger:routeStationPassengerList) {
			if(highSectionPassenger==null||passenger.getCurrentNumber()>highSectionPassenger.getCurrentNumberRealTime()) {
				highSectionPassenger=passenger;
			}
		}
		int runTimeNum = DateUtil.getTimeMinute(runTime)/30;
		if(highSectionPassenger==null) {
			highSectionPassenger=new RouteStationPassenger();
			short currentNumber = 0;
			highSectionPassenger.setRunTimeNum((short)runTimeNum);
			highSectionPassenger.setDirection(String.valueOf(direction));
			highSectionPassenger.setCurrentNumber(currentNumber);
		} else {
			if (highSectionPassenger.getCurrentNumberRealTime() > 0 && highSectionPassenger.getOrderNumber() != null) {
				double currentNumberShort = highSectionPassenger.getCurrentNumberRealTime() * 1;
				int shortTimes = 0;
				RouteSta routeSta = null;
				for (int i = routeStationPassengerList.size() - 1; i >= 0; i--) {
					RouteStationPassenger passenger = routeStationPassengerList.get(i);
					if (passenger != null && passenger.getOrderNumber() <= highSectionPassenger.getOrderNumber()) {
						break;
					}
					if (passenger != null && passenger.getCurrentNumberRealTime() <= currentNumberShort) {
						if (routeStaMap.get(passenger.getRouteStaId()) != null) {
							routeSta = routeStaMap.get(passenger.getRouteStaId());
						}
						shortTimes++;
					} else {
						shortTimes = 0;
					}
					if (shortTimes >= 5) {
						highSectionPassenger.setRouteStationPassengerTurn(passenger);
						highSectionPassenger.setRouteStaTurn(routeSta);
					}
				}
			}
		}
		return highSectionPassenger;
	}

	public void subSectionPassengerRealTime(int direction, Date runTime, Long routeStaId, Integer subPassenger) {
		List<RouteStationPassenger> routeStationPassengerList = getRouteStationPassenger(runTime, direction);
		if (routeStationPassengerList != null) {
			for (RouteStationPassenger passenger : routeStationPassengerList) {
				passenger.addSubNumber(subPassenger);
				if (routeStaId != null && routeStaId.equals(passenger.getRouteStaId())) {
					break;
				}
			}
		}
	}

	public RouteStationPassenger getHighSectionPassenger(int direction,String runTime) {
		Map<String, RouteStationPassenger> map=highSectionPassengerMap.get(direction);
		if(map==null) {
			throw new MyException("-1", "缺失高断面客流"+runTime+(direction==Direction.UP.getValue()?"上行":"下行")+"\t"+highSectionPassengerMap.size());
		}
		RouteStationPassenger highSectionPassenger=map.get(runTime);
		if(highSectionPassenger==null) {
			Calendar calendar=DateUtil.getCalendarHM(runTime);
			int minute=calendar.get(Calendar.MINUTE);
			if(minute!=0||minute!=30) {
				if(minute<30)
					calendar.set(Calendar.MINUTE, 0);
				else {
					calendar.set(Calendar.MINUTE, 30);
				}
				highSectionPassenger=map.get(DateUtil.DateFormatUtil.HM.getDateString(calendar.getTime()));
			}
			if(highSectionPassenger==null)
				throw new MyException("-1", "缺失高断面客流"+runTime+(direction==Direction.UP.getValue()?"上行":"下行"));
		}
		return highSectionPassenger;
	}
	
	public int getMinLongClassesNumber(int direction,String runTime) {
		Map<String, Integer> map=minLongClassesNumberMap.get(direction);
		Integer minLongClassesNumber=map.get(runTime);
		return minLongClassesNumber;
	}

	public List<RouteStaTurn> getRouteStaTurnList() {
		return routeStaTurnList;
	}
	
	public List<RouteStaTurn> getRouteStaTurnList(int direction) {
		List<RouteStaTurn> list=new ArrayList<RouteStaTurn>();
		for(RouteStaTurn routeStaTurn:routeStaTurnList) {
			if(routeStaTurn.getDirection()==direction) {
				list.add(routeStaTurn);
			}
		}
		return list;
	}
	
	public RouteStaTurn getRouteStaTurn4TurnAround(Long routeStaId) {
		for(RouteStaTurn routeStaTurn:routeStaTurnList) {
			if(routeStaTurn.getLastRouteStaId().equals(routeStaId)) {
				return routeStaTurn;
			}
		}
		return null;
	}

	public void setRouteStaTurnList(List<RouteStaTurn> routeStaTurnList) {
		for(int i=0;i<routeStaTurnList.size();) {
			RouteStaTurn routeStaTurn=routeStaTurnList.get(i);
			RouteSta routeStaLast=getRouteSta(routeStaTurn.getLastRouteStaId());
			if(routeStaLast==null) {
				//throw new MyException("-1", "请检查【排班参数设置-短线站点设置-"+routeStaTurn.getLastRouteStaName()+"】");
				routeStaTurnList.remove(i);
				continue;
			}
			RouteSta routeStaNext=getRouteSta(routeStaTurn.getNextFirstRouteStaId());
			if(routeStaNext==null) {
				//throw new MyException("-1", "请检查【排班参数设置-短线站点设置-"+routeStaTurn.getNextFirstRouteStaName()+"】");
				routeStaTurnList.remove(i);
				continue;
			}
			
			if(routeStaLast.getStationMark()==StationMark.UP_TRIP.getValue()) {
				routeStaTurn.setDirection(Direction.UP.getValue());
			}else if(routeStaLast.getStationMark()==StationMark.DOWN_TRIP.getValue()){
				routeStaTurn.setDirection(Direction.DOWN.getValue());
			}
			i++;
		}
		this.routeStaTurnList = routeStaTurnList;
	}
	
	public Date getObuTimeNextRound(int direction,Date obuTime) {
		if(obuTime.after(getLatestTime(direction)))
			return null;
		Date arrivalTime=getArrivalTime(obuTime, direction);
		if(arrivalTime==null)
			return null;
		int restTime=getRestTime(arrivalTime, direction);
		obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		if(!isLoopLine) {
			if(obuTime.after(getLatestTime(1-direction)))
				return null;
			if(obuTime.before(getFirstTime(1-direction)))
				obuTime=getFirstTime(1-direction);
			arrivalTime=getArrivalTime(obuTime, 1-direction);
			if(arrivalTime==null)
				return null;
			restTime=getRestTime(arrivalTime, 1-direction);
			obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		}
		return obuTime;
	}
	
	public Date getArrivalTimeNextRound(int direction,Date obuTime) {
		if(obuTime.after(getLatestTime(direction)))
			return null;
		Date arrivalTime=getArrivalTime(obuTime, direction);
		if(arrivalTime==null)
			return null;
		int restTime=getRestTime(arrivalTime, direction);
		obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		if(!isLoopLine) {
			if(obuTime.after(getLatestTime(1-direction)))
				return null;
			arrivalTime=getArrivalTime(obuTime, 1-direction);
			if(arrivalTime==null)
				return null;
		}
		return arrivalTime;
	}
	
	public Date getMinObuTimeNext(int direction,Date obuTime) {
		Date arrivalTime=getArrivalTime(obuTime, direction);
		if(arrivalTime==null)
			return null;
		int restTime=getMinRestTime(arrivalTime, 1-direction);
		obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTime;
	}
	
	public Date getMinObuTimeNext(Bus bus,int direction,Date obuTime) {
		Date arrivalTime=getArrivalTime(obuTime, direction);
		if(arrivalTime==null)
			return null;
		Trip trip=new Trip(bus, direction, obuTime);
		int restTime=getMinRestTime(trip);
		obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTime;
	}
	
	public Date getMinObuTimeNextLap(Trip trip) {
		if(trip.getNextObuTimeMin().after(getLatestTime(1-trip.getDirection()))) {//到对面过了末班车时间
			return null;
		}
		Date arrivalTime=getArrivalTime(trip.getNextObuTimeMin(), 1-trip.getDirection());
		if(arrivalTime==null)
			return null;
		Trip tripArrival=new Trip(trip.getBus(), 1-trip.getDirection(), trip.getNextObuTimeMin());
		tripArrival.setTripEndTime(arrivalTime);
		int restTime=getMinRestTime(arrivalTime, trip.getDirection());
		if(!trip.isEatAfterTrip()) {
			Integer eatTime=getTime2Eat(tripArrival);
			if(eatTime!=null)
				restTime=eatTime;
		}
		Date obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTime;
	}
	
	public Date getMinObuTimeNextLap(Trip trip,RouteStaTurn routeStaTurn) {
		if(trip.getNextObuTimeMin().after(getLatestTime(1-trip.getDirection()))) {//到对面过了末班车时间
			return null;
		}
		Date arrivalTime=getArrivalTime(trip.getNextObuTimeMin(), routeStaTurn);
		if(arrivalTime==null)
			return null;
		Trip tripArrival=new Trip(trip.getBus(), trip.getDirection(), trip.getNextObuTimeMin());
		tripArrival.setTripEndTime(arrivalTime);
		int restTime=getMinRestTime(arrivalTime, 1-trip.getDirection());
		if(!trip.isEatAfterTrip()) {
			Integer eatTime=getTime2Eat(tripArrival);
			if(eatTime!=null)
				restTime=eatTime;
		}
		Date obuTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return obuTime;
	}
	
	public Date getMinObuTimeNextRound(Trip tripArrival) {
		Date arrivalTime=getArrivalTime(tripArrival.getNextObuTimeMin(), 1-tripArrival.getDirection());
		if(arrivalTime==null)
			return null;
		int restTime=getMinRestTime(arrivalTime, tripArrival.getDirection());
		arrivalTime=getArrivalTime(DateUtil.getDateAddMinute(arrivalTime, restTime), tripArrival.getDirection());
		if(arrivalTime==null)
			return null;
		restTime=getMinRestTime(arrivalTime, 1-tripArrival.getDirection());
		arrivalTime=DateUtil.getDateAddMinute(arrivalTime, restTime);
		return arrivalTime;
	}
	
	public List<Trip> getTripListByReasoning(Date startTime, int endDirection, Date endTime){
		List<Trip> tripListReasoning=new ArrayList<Trip>();
		Date arrivalTime=endTime;
		int direction=1-endDirection;
		while(arrivalTime.after(startTime)) {
			Date tripBeginTime=getTripBeginTime(direction, arrivalTime);
			Trip trip=new Trip();
			trip.setDirection(direction);
			trip.setTripBeginTime(tripBeginTime);
			trip.setTripEndTime(arrivalTime);
			tripListReasoning.add(0, trip);
			direction=1-direction;
			Integer restTime=getRestTime(tripBeginTime, direction);
			if(restTime==null)
				System.out.println("aaa");
			arrivalTime=DateUtil.getDateAddMinute(tripBeginTime, -restTime);
			System.out.println(direction+"\t"+tripBeginTime);
		}
		return tripListReasoning;
	}
	
	private Date getTripBeginTime(int tripDirection,Date arrivalTime) {
		int timeMinute=DateUtil.getTimeMinute(arrivalTime);
		Date tripBeginTime=DateUtil.getTimeByMinute((timeMinute/15)*15);//取前一15分钟
		if(tripBeginTime.after(getLatestTime(tripDirection))) {
			tripBeginTime=getLatestTime(tripDirection);
		}
		Date arrivalTimeTemp=getArrivalTime(tripBeginTime, tripDirection);
		if(arrivalTimeTemp==null) {
			System.out.println("aaaa");
		}
		while(!arrivalTimeTemp.before(arrivalTime)) {
			tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, -15);
			arrivalTimeTemp=getArrivalTime(tripBeginTime, tripDirection);
		}
		/*int diff=DateUtil.getMinuteInterval(arrivalTimeTemp, arrivalTime);//到达时间差值
		tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, diff);//开出时间加上差值
*/		
		for(int i=0;i<15;i++) {
			if(tripBeginTime.after(getLatestTime(tripDirection))) {
				tripBeginTime=getLatestTime(tripDirection);
				break;
			}
			arrivalTimeTemp=getArrivalTime(tripBeginTime, tripDirection);
			if(!arrivalTimeTemp.before(arrivalTime)) {
				if(!arrivalTimeTemp.equals(arrivalTime)) {//结束时间不相同，取前一分钟开出
					tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, -1);
				}
				break;
			}
			tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, 1);
		}
		return tripBeginTime;
	}
	
	public boolean isFirstTripCutOverRouteSta(Long firstRouteStaId) {
		RouteSta routeSta=getRouteSta(firstRouteStaId);
		return isFirstTripCutOverRouteSta(routeSta);
	}
	
	public boolean isFirstTripCutOverRouteSta(RouteSta routeSta) {
		for(RouteStaCutInAndQuit cutInAndQuit:routeStaCutInAndQuitList) {
			if(cutInAndQuit.getFromStationIdCutIn().equals(routeSta.getStationId())) {
				return true;
			}
		}
		return false;
	}
	
	public List<RouteSta> getQuitRouteStaList(RouteSta routeSta){
		List<RouteSta> quitRouteStaList=new ArrayList<RouteSta>();
		for(RouteStaCutInAndQuit cutInAndQuit:routeStaCutInAndQuitList) {
			if(cutInAndQuit.getFromStationIdCutIn().equals(routeSta.getStationId())) {
				RouteSta quitRouteSta=getRouteSta(cutInAndQuit.getDirectionQuit(),cutInAndQuit.getToStationIdQuit());
				quitRouteStaList.add(quitRouteSta);
			}
		}
		return quitRouteStaList;
	}
	
	public boolean isQuitRouteSta(Long startRouteStaId,Long lastRouteStaId){
		RouteSta cutInRouteSta=getRouteSta(startRouteStaId);
		RouteSta lastRouteSta=getRouteSta(lastRouteStaId);
		List<RouteSta> quitRouteStaList=getQuitRouteStaList(cutInRouteSta);
		if(quitRouteStaList!=null) {
			for(RouteSta routeSta:quitRouteStaList) {
				if(routeSta==lastRouteSta) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<RouteSta> getFirstTripCutOverRouteSta(int direction){
		List<RouteSta> firstTripCutOverRouteSta=new ArrayList<RouteSta>();
		for(RouteStaCutInAndQuit cutInAndQuit:routeStaCutInAndQuitList) {
			if(cutInAndQuit.getDirectionCutIn()==direction) {
				RouteSta cutInRouteSta=getRouteSta(direction, cutInAndQuit.getFromStationIdCutIn());
				if(cutInRouteSta!=null&&!firstTripCutOverRouteSta.contains(cutInRouteSta))
					firstTripCutOverRouteSta.add(cutInRouteSta);
			}
		}
		return firstTripCutOverRouteSta;
	}
	
	public void setRouteStaCutInAndQuitList(List<RouteStaCutInAndQuit> routeStaCutInAndQuitList) {
		this.routeStaCutInAndQuitList = routeStaCutInAndQuitList;
	}

	public void setTriplogTaskList(List<TriplogTask> triplogTaskList) {
		this.triplogTaskList = triplogTaskList;
	}
	
	public TriplogTask getTriplogTaskByLastRouteStaId(Long firstRouteStaIdOld,Long lastRouteStaId) {
		RouteSta firstRouteStaOld=getRouteSta(firstRouteStaIdOld);
		TriplogTask triplogTask=null;
		for(TriplogTask triplogTaskTemp:triplogTaskList) {
			if(triplogTaskTemp.getToStationId().equals(lastRouteStaId)) {
				RouteSta firstRouteSta=getRouteSta(triplogTaskTemp.getFromStationId());
				if(firstRouteSta.getOrderNumber()<=firstRouteStaOld.getOrderNumber()) {
					if(triplogTask==null||(triplogTask!=null&&firstRouteSta.getOrderNumber()>
						getRouteSta(triplogTask.getFromStationId()).getOrderNumber())) {
						triplogTask=triplogTaskTemp;
					}
				}
			}
		}
		return triplogTask;
	}

	public RouteStaTurn getRouteStaTurnByPassenger(int direction,Date obuTime) {
		List<RouteStationPassenger> routeStationPassengerList=getRouteStationPassenger(obuTime, direction);
		RouteStationPassenger highSectionPassenger=getHighSectionPassenger(direction, obuTime);
		boolean pass=false;
		int stationNumber=0;
		Long routeStaIdTurnBack=null;
		for(RouteStationPassenger routeStationPassenger:routeStationPassengerList) {
			if(routeStationPassenger==highSectionPassenger) {
				pass=true;
			}
			if(pass) {
				double ratio=routeStationPassenger.getCurrentNumber()*1.0/highSectionPassenger.getCurrentNumber();
				if(ratio<0.3) {
					if(stationNumber==0) {
						routeStaIdTurnBack=routeStationPassenger.getRouteStaId();
					}
					stationNumber++;
				}else {
					stationNumber=0;
					routeStaIdTurnBack=null;
				}
				if(stationNumber==5) {
					break;
				}
			}
		}
		if(routeStaIdTurnBack!=null) {
			RouteStaTurn routeStaTurn=null;
			RouteSta routeSta=null;
			RouteSta routeStaTurnBack=getRouteSta(routeStaIdTurnBack);
			for(RouteStaTurn routeStaTurnTemp:getRouteStaTurnList(direction)) {
				RouteSta routeStaTemp=getRouteSta(routeStaTurnTemp.getLastRouteStaId());
				if(routeStaTemp.getOrderNumber()>=routeStaTurnBack.getOrderNumber()) {
					if(routeSta==null||routeStaTemp.getOrderNumber()<routeSta.getOrderNumber()) {
						routeStaTurn=routeStaTurnTemp;
						routeSta=routeStaTemp;
					}
				}
			}
			if(routeStaTurn!=null)
				return routeStaTurn;
		}
		return null;
	}
	
	public RouteSta getCutInRouteStaByPassenger(int direction,Date obuTime) {
		List<RouteStationPassenger> routeStationPassengerList=getRouteStationPassenger(obuTime, direction);
		RouteStationPassenger highSectionPassenger=getHighSectionPassenger(direction, obuTime);
		int highSectionIndex=routeStationPassengerList.indexOf(highSectionPassenger);
		int stationNumber=0;
		Long routeStaIdCutIn=null;
		for(int i=highSectionIndex-1;i>=0;i--) {
			RouteStationPassenger routeStationPassenger=routeStationPassengerList.get(i);
			double ratio=routeStationPassenger.getCurrentNumber()*1.0/highSectionPassenger.getCurrentNumber();
			if(ratio<0.3) {
				if(stationNumber==0) {
					routeStaIdCutIn=routeStationPassenger.getRouteStaId();
				}
				stationNumber++;
			}else {
				stationNumber=0;
				routeStaIdCutIn=null;
			}
			if(stationNumber==5) {
				break;
			}
		}
		if(routeStaIdCutIn!=null) {
			RouteSta routeSta=null;
			RouteSta routeStaCutIn=getRouteSta(routeStaIdCutIn);
			for(RouteStaTurn routeStaTurnTemp:getRouteStaTurnList(1-direction)) {
				RouteSta routeStaTemp=getRouteSta(routeStaTurnTemp.getNextFirstRouteStaId());
				if(routeStaTemp.getOrderNumber()<=routeStaCutIn.getOrderNumber()) {
					if(routeSta==null||routeStaTemp.getOrderNumber()>routeSta.getOrderNumber()) {
						routeSta=routeStaTemp;
					}
				}
			}
			return routeSta;
		}
		return null;
	}

	public int getTurnAroundTime(Date date) {
		int runTimeNum=DateUtil.getTimeMinute(date)/15;
		List<Integer> routeWasteTimeList=new ArrayList<Integer>();
		int i=0;
		while(routeWasteTimeList.size()<4) {
			RouteWasteTime wasteTime=getRouteWasteTime(Direction.UP.getValue(), runTimeNum+i);
			i++;
			if(wasteTime!=null)
				routeWasteTimeList.add(wasteTime.getWasteTime().intValue());
			else {
				if(DateUtil.getTimeByMinute((runTimeNum+i)*15).after(getLatestTime(Direction.UP.getValue()))) {
					break;
				}
			}
		}
		int wasteTimeAvgUp=MathUtil.max(routeWasteTimeList);
		Date arrivalTime=DateUtil.getDateAddMinute(date, wasteTimeAvgUp);
		int restTimeUp=0;
		int direction=Direction.DOWN.getValue();
		if(isLoopLine) {
			direction=Direction.UP.getValue();
		}
		if(getLunchBeginTime(direction)!=null) {
			if(DateUtil.isInTimeRange(arrivalTime, getLunchBeginTime(direction), DateUtil.getDateAdd(getLunchBeginTime(direction), Calendar.HOUR, 1))) {//午饭
				restTimeUp=getLunchEatTime(direction);
			}
		}if(getSupperBeginTime(direction)!=null) {
			if(DateUtil.isInTimeRange(arrivalTime, getSupperBeginTime(direction), DateUtil.getDateAdd(getSupperBeginTime(direction), Calendar.HOUR, 1))) {//晚饭
				restTimeUp=getSupperEatTime(direction);
			}
		}
		int wasteTimeAvgDown=0;
		int restTimeDown=0;
		if(!isLoopLine) {
			routeWasteTimeList=new ArrayList<Integer>();
			i=0;
			while(routeWasteTimeList.size()<4) {
				RouteWasteTime wasteTime=getRouteWasteTime(Direction.DOWN.getValue(), runTimeNum+i);
				i++;
				if(wasteTime!=null)
					routeWasteTimeList.add(wasteTime.getWasteTime().intValue());
				else {
					if(DateUtil.getTimeByMinute((runTimeNum+i)*15).after(getLatestTime(Direction.DOWN.getValue()))) {
						break;
					}
				}
			}
			wasteTimeAvgDown=MathUtil.max(routeWasteTimeList);
			if(restTimeUp==0) {//上行走完没安排吃饭
				restTimeUp=getRestTime(arrivalTime, Direction.UP.getValue());
				arrivalTime=DateUtil.getDateAddMinute(arrivalTime, restTimeUp+wasteTimeAvgDown);//走完下行到站时间
				direction=Direction.UP.getValue();
				if(getLunchBeginTime(direction)!=null) {
					if(DateUtil.isInTimeRange(arrivalTime, getLunchBeginTime(direction), DateUtil.getDateAdd(getLunchBeginTime(direction), Calendar.HOUR, 1))) {//午饭
						restTimeDown=getLunchEatTime(direction);
					}
				}if(getSupperBeginTime(direction)!=null) {
					if(DateUtil.isInTimeRange(arrivalTime, getSupperBeginTime(direction), DateUtil.getDateAdd(getSupperBeginTime(direction), Calendar.HOUR, 1))) {//晚饭
						restTimeDown=getSupperEatTime(direction);
					}
				}
			}
			if(restTimeDown==0)
				restTimeDown=getRestTime(arrivalTime, Direction.DOWN.getValue());
			
		}else {
			if(restTimeUp==0) {
				restTimeUp=getRestTime(arrivalTime, Direction.UP.getValue());
			}
		}
		int wasteTime=wasteTimeAvgUp+wasteTimeAvgDown+restTimeUp+restTimeDown;
		return wasteTime;
	}
	
	static class ScheduleHalfHourComparator implements Comparator<ScheduleHalfHour> {  
        public int compare(ScheduleHalfHour rb1, ScheduleHalfHour rb2) {// 实现接口中的方法  
        	if(rb1.getRunTime().equals(rb2.getRunTime())) {
        		return rb1.getDirection().compareTo(rb2.getDirection());
        	}
        	return rb1.getRunTimeDate().compareTo(rb2.getRunTimeDate());
        }  
    }



    //获取定点班车时刻表
	public Map<Integer,List<Date>> getTimetable(ScheduleParam scheduleParam) {
		Map<Integer, List<Date>> map = new HashMap<>();
		map.put(0,getTimetableByDirection(0,scheduleParam));
		if (!scheduleParam.isLoopLine()) {
			map.put(1,getTimetableByDirection(1,scheduleParam));
		}
		return map;
	}
	//获取对应方向的定点班车时刻表
	private List<Date> getTimetableByDirection(Integer direction,ScheduleParam scheduleParam) {
		String firstTime = null;
		String latestTime = null;
		if (direction.equals(0)) {
			firstTime  = scheduleParam.getUpFirstTime();
			latestTime = scheduleParam.getUpLatestTime();
		} else if (direction.equals(1)) {
			firstTime  = scheduleParam.getDownFirstTime();
			latestTime = scheduleParam.getDownLatestTime();
		}
		Date runDate = scheduleParam.getRunDate();
		if(!StringUtil.isEmpty(firstTime)&&!StringUtil.isEmpty(latestTime)){
			Date firstDate = DateUtil.getCalendarHM(firstTime).getTime();
			Date lastestDate = DateUtil.getCalendarHM(latestTime).getTime();
			//常规 首班车<末班车
			//夜班车 首班车>末班车
			if(firstDate.before(DateUtil.getDateHM("0400"))) {//首班车4点前，夜班车，加一天
				firstDate=DateUtil.getDateAddDay(firstDate, 1);
			}
			if (lastestDate.before(firstDate)){
				//+1天
				lastestDate = DateUtil.getDateAdd(lastestDate, Calendar.DAY_OF_MONTH, 1);
			}
			//判断对应方向是否只有一个发班间隔
			Map<Integer, Integer> map = new HashMap<>();
			map.put(0, 0);
			map.put(1, 0);
			for (ScheduleParamsClasses scheduleParamsClass : scheduleParamsClassesList) {
				Integer dir = Integer.valueOf(scheduleParamsClass.getDirection());
				Integer count = map.get(dir);
				map.put(dir, count+1);
			}
			List<Date> dateList = new ArrayList<>();
			//只有一个,直接从开始时间添加间隔,不用拆分
			Integer maxDispatchInterval = null;
			Date classPreDate = firstDate;
			Date classLastestDate = null;
			boolean addFlag = false;
			//1930-2200 60 1930 2030 2130 2230
			for(;classPreDate.before(lastestDate);) {
				dateList.add(classPreDate);
				maxDispatchInterval = getMaxInterval(classPreDate, direction);
				System.out.println("当前时间: "+DateUtil.DateFormatUtil.HM.getDateString(classPreDate)+" 最大发班间隔 : " + maxDispatchInterval);
				classPreDate = DateUtil.getDateAddMinute(classPreDate, maxDispatchInterval);
				addFlag = true;
			}
			if (addFlag) {
				//判断最后一个班次是否与末班车时间的差值大于发班间隔0.5,则多发一班
				Date lastTripDate = DateUtil.getDateAddMinute(classPreDate,-maxDispatchInterval);
				Double halfMaxDispatchInterval = maxDispatchInterval / 2.0;
				if (lastTripDate.before(lastestDate)) {
					if ((lastestDate.getTime() - lastTripDate.getTime()) >= (halfMaxDispatchInterval * 60 * 1000)) {
						dateList.add(lastestDate);
						System.out.println("差值大于等于发班间隔0.5,则多发一班 "+ direction  +" lastTripDate = " + DateFormatUtil.HM.getDateString(lastTripDate));
					}else{
						System.out.println("差值小于发班间隔0.5,则删除 "
								+DateFormatUtil.HM.getDateString(dateList.get(dateList.size() - 1))
								+"直接调整为 lastestDate = " + DateFormatUtil.HM.getDateString(lastestDate));
						dateList.remove(dateList.size() - 1);
						dateList.add(lastestDate);
					}
				}
			}else {
				if(firstTime.equals(latestTime)) {
					dateList.add(firstDate);
				}
			}
			return dateList;
		}
		return null;
	}
	
	public ScheduleParamsAnchor getScheduleParamsAnchor(int direction,Date tripBeginTime) {
		for(ScheduleParamsAnchor scheduleParamsAnchor:scheduleParamsAnchorList) {
			if((DateUtil.isInTimeRange(tripBeginTime, DateUtil.getCalendarHM(scheduleParamsAnchor.getBeginTime()).getTime(),DateUtil.getCalendarHM(scheduleParamsAnchor.getEndTime()).getTime())||
					tripBeginTime.equals(getLatestTime(direction)))//开车时间在设置时段内或末班车
					&&scheduleParamsAnchor.getDirection().equals(String.valueOf(direction))) {
				return scheduleParamsAnchor;
			}
		}
		return null;
	}
	
	public Integer getLatePeakPassengerDirection() {
		return latePeakPassengerDirection;
	}
	
	public void initLatePeakPassengerDirection(Map<Integer, RouteStationPassengerInfo> passengerInfoMap) {
		Integer passengerNumber=null;
		for(Integer direction:passengerInfoMap.keySet()) {
			RouteStationPassengerInfo passengerInfo=passengerInfoMap.get(direction);
			List<List<RouteStationPassenger>> routeStationPassengerList=passengerInfo.getRouteStationPassenger2DList();
			int passengerNumberTemp=0;
			for(List<RouteStationPassenger> list:routeStationPassengerList) {
				for(RouteStationPassenger routeStationPassenger:list) {
					if(routeStationPassenger.getRunTimeNum()>=34&&routeStationPassenger.getRunTimeNum()<38) {
						passengerNumberTemp+=routeStationPassenger.getCurrentNumber();
					}else {
						break;
					}
				}
			}
			if(passengerNumber==null) {
				passengerNumber=passengerNumberTemp;
			}else if(passengerNumberTemp>1.5*passengerNumber) {
				latePeakPassengerDirection=direction;
			}else if(passengerNumber>1.5*passengerNumberTemp) {
				latePeakPassengerDirection=1-direction;
			}
		}
	}

	public Integer getSingleBusTripNumber() {
		return singleBusTripNumber;
	}

	public void setSingleBusTripNumber(Integer singleBusTripNumber) {
		this.singleBusTripNumber = singleBusTripNumber;
	}
	
	/**
	 * 
	 * @param wasteTimeAvg 周转时间（环线为单程时间，非环线为来回时间）
	 * @return
	 */
	public int getSingleBusTripNumber(int wasteTimeAvg) {
		Integer singleRunMax=scheduleParamsRoute.getSingleRunMax();
		if(singleRunMax==null) {
			singleRunMax=600;
		}
		int singleBusTripNumber=singleRunMax/wasteTimeAvg;
		if(!isLoopLine) {
			singleBusTripNumber=singleBusTripNumber*2;
		}
		return singleBusTripNumber;
	}
	
	public void initSingleBusTripNumber(int wasteTimeAvg) {
		singleBusTripNumber=getSingleBusTripNumber(wasteTimeAvg);
	}

	public List<ScheduleHalfHour> highScheduleHalfHourSort(List<ScheduleHalfHour> scheduleHalfHourList){
		List<ScheduleHalfHour> highScheduleHalfHourList=new ArrayList<ScheduleHalfHour>();
		for(ScheduleHalfHour scheduleHalfHour:scheduleHalfHourList) {
			RouteStationPassenger highSectionPassenger=getHighSectionPassenger(scheduleHalfHour.getDirection(), scheduleHalfHour.getRunTime());
			if(scheduleHalfHour.getPlanList().isEmpty()) {
				DispatchRule dispatchRule=getDispatchRule(scheduleHalfHour.getRunTime(), scheduleHalfHour.getDirection());
				dispatchRule.getObuTimeList(scheduleHalfHour);
				System.out.println("aaa");
			}
			double passengerPerBus=1000;
			if(!scheduleHalfHour.getPlanList().isEmpty()) {
				passengerPerBus=highSectionPassenger.getCurrentNumber()/scheduleHalfHour.getPlanList().size();
			}
			System.out.println(routeId+"\t"+scheduleHalfHour.getRunTime()+"\t"+highSectionPassenger.getCurrentNumber()+"\t"+scheduleHalfHour.getPlanList().size()+"\t"+passengerPerBus);
			int index=0;
			for(int i=0;i<highScheduleHalfHourList.size();i++) {
				ScheduleHalfHour scheduleHalfHourTemp=highScheduleHalfHourList.get(i);
				RouteStationPassenger highSectionPassengerTemp=getHighSectionPassenger(scheduleHalfHourTemp.getDirection(), scheduleHalfHourTemp.getRunTime());
				double passengerPerBusTemp=highSectionPassengerTemp.getCurrentNumber()/scheduleHalfHourTemp.getPlanList().size();
				if(passengerPerBus>passengerPerBusTemp) {
					break;
				}
				index++;
			}
			highScheduleHalfHourList.add(index, scheduleHalfHour);
		}
		return highScheduleHalfHourList;
	}
	
	public void resetSingleRecoveryBeginTime(int direction) {
		Date recoveryBeginTime=getMiddleStopRecoveryBeginTime(direction);
		Date offDutyBeginTime=getMiddleStopOffDutyBeginTime();
		Date obuTime=recoveryBeginTime;
		int tripDirection=direction;
		Date arrivalTime=null;
		while(arrivalTime==null||arrivalTime.before(offDutyBeginTime)) {
			Date arrivalTimeNext=getArrivalTime(obuTime, tripDirection);
			if(arrivalTime!=null&&arrivalTimeNext!=null&&arrivalTime.before(offDutyBeginTime)&&
					arrivalTimeNext.after(offDutyBeginTime)) {
				if((DateUtil.getMinuteInterval(arrivalTime, offDutyBeginTime)<
						DateUtil.getMinuteInterval(offDutyBeginTime, arrivalTimeNext))||
					arrivalTimeNext.after(DateUtil.getDateAddMinute(offDutyBeginTime, 30))) {//再走一个来回离下班时间比少走长，防止太晚下班
					int diff=DateUtil.getMinuteInterval(arrivalTime, offDutyBeginTime);
					recoveryBeginTime=DateUtil.getDateAddMinute(recoveryBeginTime, diff);
					scheduleParamsSingle.setRunBeginTime(direction, recoveryBeginTime);
				}
				break;
			}
			arrivalTime=arrivalTimeNext;
			obuTime=getMinObuTimeNext(tripDirection, obuTime);
			if(obuTime==null)
				break;
			tripDirection=1-tripDirection;
		}
	}
	
	public boolean isInPeakTime(Date date) {
		if(DateUtil.isInTimeRange(date, getEarlyPeakBeginTime(), getEarlyPeakEndTime())||
			DateUtil.isInTimeRange(date, getLatePeakBeginTime(), getLatePeakEndTime())) {
			return true;
		}
		return false;
	}

	public ScheduleParamsDriverless getScheduleParamsDriverless() {
		return scheduleParamsDriverless;
	}

	public void setScheduleParamsDriverless(ScheduleParamsDriverless scheduleParamsDriverless) {
		this.scheduleParamsDriverless = scheduleParamsDriverless;
	}

	public List<ScheduleParamsDrInout> getScheduleParamsDrInoutList() {
		return scheduleParamsDrInoutList;
	}

	public void setScheduleParamsDrInoutList(List<ScheduleParamsDrInout> scheduleParamsDrInoutList) {
		this.scheduleParamsDrInoutList = scheduleParamsDrInoutList;
	}

	public List<ScheduleParamsDrRouteSub> getScheduleParamsDrRouteSubList() {
		return scheduleParamsDrRouteSubList;
	}

	public void setScheduleParamsDrRouteSubList(List<ScheduleParamsDrRouteSub> scheduleParamsDrRouteSubList) {
		this.scheduleParamsDrRouteSubList = scheduleParamsDrRouteSubList;
	}

	public List<ScheduleParamsDrBus> getScheduleParamsDrBusList() {
		return scheduleParamsDrBusList;
	}

	public void setScheduleParamsDrBusList(List<ScheduleParamsDrBus> scheduleParamsDrBusList) {
		this.scheduleParamsDrBusList = scheduleParamsDrBusList;
	}

	public int getBusNumberDriverlessPreset() {
		return scheduleParamPreset.getBusNumberDriverless()==null?0:scheduleParamPreset.getBusNumberDriverless();
	}

	public Long getRouteIdDriverless() {
		return scheduleParamPreset.getRouteIdDriverless();
	}

	public List<ScheduleParamsDrRouteSub> getScheduleParamsDrRouteSubList(Integer direction) {
		if (direction == null) {
			return scheduleParamsDrRouteSubList;
		}
		return scheduleParamsDrRouteSubList.stream().filter((a) -> a.getDirection() != null && a.getDirection() == direction).collect(Collectors.toList());
	}

	public Map<Long, RouteSta> getRouteStaMap() {
		return routeStaMap;
	}

	public void setRouteStaMap(Map<Long, RouteSta> routeStaMap) {
		this.routeStaMap = routeStaMap;
	}
}
