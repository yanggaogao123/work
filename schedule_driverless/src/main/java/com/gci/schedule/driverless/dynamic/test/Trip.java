package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.RouteSta;
import com.gci.schedule.driverless.dynamic.bean.RouteStaTurn;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsDrInoutTime;
import com.gci.schedule.driverless.dynamic.enums.ServiceType;

import java.util.Calendar;
import java.util.Date;

public class Trip implements Cloneable{
	
	private Bus bus;//执行车辆
	
	private int direction;//方向
	
	private Date tripBeginTime;//班次开出时间
	
	private Date tripEndTime;//班次到达时间
	
	private Date nextObuTimeMin;//下一班最早开出时间
	
	private Date nextObuTimeMax;//下一班最晚开出时间
	
	private int runDuration;//运营时长
	
	private boolean isFirstTask;//首个任务
	
	private Trip preTrip;//车辆上一任务
	
	private Trip laterTrip;//车辆下一任务
	
	private Trip lastTrip;//线路上一班次
	
	private Trip nextTrip;//线路下一班次
	
	private boolean quitMark;//退出标识
	
	private int minRestTime;
	
	private boolean shortLine;//短线
	
	private Long firstRouteStaId;//首站站点ID
	
	private Long lastRouteStaId;//末站站点ID
	
	private String firstRouteStaName;//首站站点名称
	
	private String lastRouteStaName;//末站站点名称
	
	private Trip turnTrip;//回头班次
	
	private Integer restTime;//停站时间
	
	private boolean reCalculate;//均匀重算标识
	
	private boolean reverseInsert;//反插
	
	private RouteStaTurn shortLineAdjust;//到站后短线调位任务
	
	private boolean eatAfterTrip;//到站后安排吃饭
	
	private ServiceType serviceType;//任务类型
	
	private boolean elecSupplyAfterTrip;//到站后补电
	
	private Date firstRoundPlanTime;//首轮时间
    
    private Long firstRoundTaskId;//首轮任务

	private Long firstRouteStaIdNext;//下一轮首站站点ID

	private ScheduleParamsDrInoutTime inoutTime;

	private boolean isVirtual = false;

	public Trip() {}
	
	public Trip(Bus bus, Date tripBeginTime, ScheduleParam scheduleParam, RouteStaTurn routeStaTurn) {
		this(bus, routeStaTurn.getDirection(), tripBeginTime, scheduleParam, true, routeStaTurn.getLastRouteStaId(),null);
		tripBeginTime=DateUtil.getDateAddMinute(tripEndTime, routeStaTurn.getTurnAroundTime());
		int direction=this.direction;
		if(!scheduleParam.isLoopLine()) {
			direction=1-direction;
		}
		Trip tripBack=new Trip(bus, direction, tripBeginTime);
		tripBack.setShortLine(true);
		tripBack.setTripEndTime(scheduleParam.getArrivalTime(tripBeginTime, direction, routeStaTurn.getNextFirstRouteStaId(), scheduleParam.getLastRouteStaId(direction)));
		int restTime=scheduleParam.getMinRestTime(tripBack);
		tripBack.setNextObuTimeMin(DateUtil.getDateAddMinute(tripBack.getTripEndTime(), restTime));
		tripBack.setFirstRouteStaId(routeStaTurn.getNextFirstRouteStaId());
		tripBack.setLastRouteStaId(scheduleParam.getLastRouteStaId(direction));
		boolean quitMark=scheduleParam.isEndAfterTrip(tripBack);
		tripBack.setQuitMark(quitMark);
		turnTrip=tripBack;
	}
	
	public Trip(Bus bus, int direction, Date tripBeginTime, ScheduleParam scheduleParam, Long firstRouteStaId, Long lastRouteStaId) {
		this.bus = bus;
		this.shortLine=true;
		this.direction = direction;
		this.tripBeginTime = tripBeginTime;
		this.tripEndTime=scheduleParam.getArrivalTime(tripBeginTime, direction, firstRouteStaId, lastRouteStaId);
		this.firstRouteStaId=firstRouteStaId;
		this.lastRouteStaId=lastRouteStaId;
	}

	public Trip(Bus bus, int direction, Date tripBeginTime, ScheduleParam scheduleParam, boolean shortLine, Long lastRouteStaId, Trip lastTripFull) {
		super();
		this.bus = bus;
		this.shortLine=shortLine;
		this.direction = direction;
		this.tripBeginTime = tripBeginTime;
		if(DateUtil.getDateHM("2223").equals(tripBeginTime))
			System.out.println("aaa");
		if(shortLine) {//短线
			RouteSta firstRouteSta=scheduleParam.getFirstRouteSta(direction);
			this.tripEndTime=scheduleParam.getArrivalTime(tripBeginTime, direction, firstRouteSta.getRouteStationId(), lastRouteStaId);
			this.nextObuTimeMin =DateUtil.getDateAdd(tripEndTime, Calendar.MINUTE, 5);//中途站休息5分钟，再调头
			this.firstRouteStaId=scheduleParam.getFirstRouteStaId(direction);
			this.lastRouteStaId=lastRouteStaId;
		}else {
			this.tripEndTime = scheduleParam.getArrivalTime(tripBeginTime, direction);
			if(tripEndTime==null)
				System.out.println("没有结束时间"+tripBeginTime+"\t"+direction);
			if(lastTripFull!=null&&tripEndTime.before(lastTripFull.getTripEndTime()))//后面发的车不能早于前车到达
				tripEndTime=lastTripFull.getTripEndTime();
			this.nextObuTimeMin = getMinObuTime(scheduleParam);
			this.nextObuTimeMax = getMaxObuTime(scheduleParam);
			this.firstRouteStaId=scheduleParam.getFirstRouteStaId(direction);
			this.lastRouteStaId=scheduleParam.getLastRouteStaId(direction);
			boolean quitMark=scheduleParam.isEndAfterTrip(this);
			setQuitMark(quitMark);
		}
		if(tripEndTime==null)
			System.out.println("aaaa");
		//this.nextObuTimeMin = TripRunTime.getMinObuTime(bus, tripEndTime);
	}
	
	public Trip(Bus bus, int direction, Date tripBeginTime, ScheduleParam scheduleParam, Trip lastTripFull) {
		this(bus, direction, tripBeginTime, scheduleParam, false, null,lastTripFull);
	}
	
	public Trip(Bus bus, int direction, Date tripBeginTime) {
		super();
		this.bus = bus;
		this.direction=direction;
		this.tripBeginTime=tripBeginTime;
	}
	
	public boolean setNextObuTimeMin(ScheduleParam scheduleParam) {
		int restTime=scheduleParam.getMinRestTime(this);
		nextObuTimeMin=DateUtil.getDateAddMinute(tripEndTime, restTime);
		return true;
	}
	
	public Date getMinObuTime(ScheduleParam scheduleParam) {
		minRestTime=scheduleParam.getMinRestTime(this);
		Date obuTime=DateUtil.getDateAdd(tripEndTime, Calendar.MINUTE, minRestTime);
		return obuTime;
	}
	
	private Date getMaxObuTime(ScheduleParam scheduleParam) {
		Date obuTime=null;
		Integer maxRestTime=scheduleParam.getMaxRestTime(tripEndTime, 1-direction);
		if(maxRestTime!=null) {
			obuTime=DateUtil.getDateAdd(tripEndTime, Calendar.MINUTE, maxRestTime);
		}
		return obuTime;
	}

	public Trip getLastTrip() {
		return lastTrip;
	}

	public void setLastTrip(Trip lastTrip) {
		this.lastTrip = lastTrip;
	}
	
	public Trip getNextTrip() {
		return nextTrip;
	}

	public Trip getNextTripFull() {
		Trip nextTrip=this.nextTrip;
		while(nextTrip!=null&&nextTrip.isShortLine()) {
			nextTrip=nextTrip.getNextTrip();
		}
		if(nextTrip==null||nextTrip.isShortLine()) {
			return null;
		}else {
			return nextTrip;
		}
	}
	
	public Trip getLastTripFull() {
		Trip lastTrip=this.lastTrip;
		while(lastTrip!=null&&lastTrip.isShortLine()) {
			lastTrip=lastTrip.getLastTrip();
		}
		if(lastTrip==null||lastTrip.isShortLine()) {
			return null;
		}else {
			return lastTrip;
		}
	}
	
	/**
	 * 获取上一班次的前一个长线任务
	 * @return
	 */
	public Trip getLastTripPreFullTrip() {
		Trip lastTrip=this.lastTrip;
		while(lastTrip!=null) {
			Trip lastTripPreTrip=lastTrip.getPreTrip();
			if(lastTripPreTrip!=null&&!lastTripPreTrip.isShortLine()) {
				return lastTripPreTrip;
			}
			lastTrip=lastTrip.getLastTrip();
		}
		return null;
	}
	
	public Trip getNextTripPreFullTrip() {
		Trip nextTrip=this.nextTrip;
		while(nextTrip!=null) {
			Trip nextTripPreTrip=nextTrip.getPreTrip();
			if(nextTripPreTrip!=null&&!nextTripPreTrip.isShortLine()) {
				return nextTripPreTrip;
			}
			nextTrip=nextTrip.getNextTrip();
		}
		return null;
	}
	
	
	public void setNextTrip(Trip nextTrip) {
		this.nextTrip = nextTrip;
	}

	public Trip getPreTrip() {
		return preTrip;
	}

	public void setPreTrip(Trip preTrip) {
		if(preTrip!=null&&preTrip.isShortLine()) {
			System.out.println("aaa");
		}
		this.preTrip = preTrip;
	}

	public boolean isFirstTask() {
		return isFirstTask;
	}

	public void setFirstTask(boolean isFirstTask) {
		this.isFirstTask = isFirstTask;
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}

	public Date getTripBeginTime() {
		return tripBeginTime;
	}

	public void setTripBeginTime(Date tripBeginTime) {
		Calendar calendar=DateUtil.getCalendar(tripBeginTime);
		calendar.set(Calendar.SECOND, 0);
		this.tripBeginTime = calendar.getTime();
	}

	public Date getTripEndTime() {
		return tripEndTime;
	}

	public void setTripEndTime(Date tripEndTime) {
		this.tripEndTime = tripEndTime;
	}

	public Date getNextObuTimeMin() {
		return nextObuTimeMin;
	}

	public void setNextObuTimeMin(Date nextObuTimeMin) {
		this.nextObuTimeMin = nextObuTimeMin;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String getBusName() {
		String busName=bus.getBusName();
		return busName;
	}

	public int getRunDuration() {
		return runDuration;
	}

	public void setRunDuration(int runDuration) {
		this.runDuration = runDuration;
	}

	public boolean isQuitMark() {
		return quitMark;
	}

	public void setQuitMark(boolean quitMark) {
		this.quitMark = quitMark;
	}

	public boolean isShortLine() {
		return shortLine;
	}

	public Long getLastRouteStaId() {
		return lastRouteStaId;
	}

	public void setShortLine(boolean shortLine) {
		this.shortLine = shortLine;
	}

	public Trip getTurnTrip() {
		return turnTrip;
	}

	public void setTurnTrip(Trip turnTrip) {
		this.turnTrip = turnTrip;
	}

	public void setLastRouteStaId(Long lastRouteStaId) {
		this.lastRouteStaId = lastRouteStaId;
	}

	public Long getFirstRouteStaId() {
		return firstRouteStaId;
	}

	public void setFirstRouteStaId(Long firstRouteStaId) {
		this.firstRouteStaId = firstRouteStaId;
	}

	public Integer getRestTime() {
		return restTime;
	}

	public void setRestTime(Integer restTime) {
		this.restTime = restTime;
	}

	public String getFirstRouteStaName() {
		return firstRouteStaName;
	}

	public void setFirstRouteStaName(String firstRouteStaName) {
		this.firstRouteStaName = firstRouteStaName;
	}

	public String getLastRouteStaName() {
		return lastRouteStaName;
	}

	public void setLastRouteStaName(String lastRouteStaName) {
		this.lastRouteStaName = lastRouteStaName;
	}

	public boolean isReCalculate() {
		return reCalculate;
	}

	public void setReCalculate(boolean reCalculate) {
		this.reCalculate = reCalculate;
	}

	public Trip getLaterTrip() {
		return laterTrip;
	}

	public void setLaterTrip(Trip laterTrip) {
		this.laterTrip = laterTrip;
	}

	public Date getNextObuTimeMax() {
		return nextObuTimeMax;
	}

	public boolean isReverseInsert() {
		return reverseInsert;
	}

	public void setReverseInsert(boolean reverseInsert) {
		this.reverseInsert = reverseInsert;
	}

	public RouteStaTurn getShortLineAdjust() {
		return shortLineAdjust;
	}

	public void setShortLineAdjust(RouteStaTurn shortLineAdjust) {
		this.shortLineAdjust = shortLineAdjust;
	}

	public boolean isEatAfterTrip() {
		return eatAfterTrip;
	}

	public void setEatAfterTrip(boolean eatAfterTrip) {
		this.eatAfterTrip = eatAfterTrip;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	public boolean isElecSupplyAfterTrip() {
		return elecSupplyAfterTrip;
	}

	public void setElecSupplyAfterTrip(boolean elecSupplyAfterTrip) {
		this.elecSupplyAfterTrip = elecSupplyAfterTrip;
	}

	public Date getFirstRoundPlanTime() {
		return firstRoundPlanTime;
	}

	public void setFirstRoundPlanTime(Date firstRoundPlanTime) {
		this.firstRoundPlanTime = firstRoundPlanTime;
	}

	public Long getFirstRoundTaskId() {
		return firstRoundTaskId;
	}

	public void setFirstRoundTaskId(Long firstRoundTaskId) {
		this.firstRoundTaskId = firstRoundTaskId;
	}

	@Override
	public Object clone() {
         Trip trip = null;
         try {
        	 trip = (Trip) super.clone();
         } catch (CloneNotSupportedException e) {
        	 e.printStackTrace();
         }
         return trip;
     }

	public Integer getInterval() {
		Trip tripLast=getLastTripFull();
		if(tripLast!=null)
			return DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), tripBeginTime);
		return null;
	}

	public Long getFirstRouteStaIdNext() {
		return firstRouteStaIdNext;
	}

	public void setFirstRouteStaIdNext(Long firstRouteStaIdNext) {
		this.firstRouteStaIdNext = firstRouteStaIdNext;
	}

	public ScheduleParamsDrInoutTime getInoutTime() {
		return inoutTime;
	}

	public void setInoutTime(ScheduleParamsDrInoutTime inoutTime) {
		this.inoutTime = inoutTime;
	}

	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean virtual) {
		isVirtual = virtual;
	}
}

