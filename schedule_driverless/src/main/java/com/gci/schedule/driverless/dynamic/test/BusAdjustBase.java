package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.LatestBusTrip;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsAnchor;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.exception.MyException;

import java.util.*;

public abstract class BusAdjustBase {
	
	protected ScheduleParam scheduleParam;
	
	protected RouteSchedule routeSchedule;
	
	protected Bus latestBusMain;//主末班车
	
	protected Bus latestBusSecondary;//次末班车
	
	protected int mainDirection;//末班车主方向(末班车早的发班方向)
	
	protected Map<Integer,List<DirectionObuTimeRange>> obuTimeRangeMap=new HashMap<Integer, List<DirectionObuTimeRange>>();//发班时间范围（key:停场方向，value：各时段应发班方向
	
	protected Map<Integer,List<LatestBusTrip>> latestBusTripMap=new HashMap<Integer, List<LatestBusTrip>>();//尾车最晚发班班次key:停场方向，value：各时段应发班班次
	
	private List<RestTime> restTimeList=new ArrayList<RestTime>();
	
	protected boolean latestBusMatched;//末班车行向是否匹配
	
	private int retry=0;
	
	protected boolean minRestMode;//按行车时间算停站时间
	
	public BusAdjustBase(ScheduleParam scheduleParam) {
		this.scheduleParam=scheduleParam;
	}

	public BusAdjustBase(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		this.scheduleParam=scheduleParam;
		this.routeSchedule=routeSchedule;
		initRestTime();
	}

	public void setMinRestMode(boolean minRestMode) {
		this.minRestMode = minRestMode;
	}

	protected void genetateLatestBusTripList() {
		Date latestTimeUp=scheduleParam.getLatestTime(Direction.UP.getValue());
		Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
		if(!latestTimeUp.equals(latestTimeDown)) {
			int direction=Direction.UP.getValue();//默认上行为主
			if(latestTimeUp.after(latestTimeDown)) {//下行末班先发
				direction=Direction.DOWN.getValue();
			}
			genetateLatestBusTripList(direction);
			mainDirection=direction;
			if(scheduleParam.isAppearDirection(1-direction))//首班车晚的方向可以出车
				genetateLatestBusTripList(1-direction);//反向也要生成
		}else {
			mainDirection=Direction.NODIRECTION.getValue();
			Integer endDirection=scheduleParam.getEndDirection();
			if(endDirection!=null&&endDirection==Direction.DOWN.getValue()) {
				mainDirection=Direction.DOWN.getValue();
			}else if(endDirection!=null&&endDirection==Direction.UP.getValue()) {
				mainDirection=Direction.UP.getValue();
			}
			genetateLatestBusTripList(Direction.UP.getValue());
			genetateLatestBusTripList(Direction.DOWN.getValue());
		}
	} 
	
	public void initRestTime() {
		restTimeList.clear();
		List<ScheduleParamsAnchor> scheduleParamsAnchorList=scheduleParam.getScheduleParamsAnchorList();
		int runDuration=scheduleParam.getWasteTime(scheduleParam.getFirstTime(Direction.UP.getValue()), Direction.UP.getValue());
		Integer maxRestTime=10;//默认最大停站10分钟
		if(runDuration>60) {
			maxRestTime=runDuration/6;//单程大于1小时，每6分钟休1分钟
		}
		maxRestTime=maxRestTime*3;//最大停站按常规停站两倍，譬如行车时间60分钟，常规停10分钟，最大停30分钟
		if(maxRestTime>30) {
			maxRestTime=30;//最多停30分钟，防止最大停站设置过大，导致轮次不正常
		}
		for(ScheduleParamsAnchor anchor:scheduleParamsAnchorList) {
			int anchorDurationDurationMax=anchor.getAnchorDurationMax();
			if(anchorDurationDurationMax>maxRestTime) {
				anchorDurationDurationMax=maxRestTime;
			}
			if(anchorDurationDurationMax<anchor.getAnchorDurationMin()) {
				anchorDurationDurationMax=anchor.getAnchorDurationMin();
			}
			RestTime restTime=new RestTime(Integer.valueOf(anchor.getDirection()),DateUtil.getDateHM(anchor.getBeginTime()), 
					DateUtil.getDateHM(anchor.getEndTime()), (anchor.getAnchorDurationMin()+anchorDurationDurationMax)/2);
			restTimeList.add(restTime);
		}
	}

	public void initRestTimeMin() {
		restTimeList.clear();
		List<ScheduleParamsAnchor> scheduleParamsAnchorList=scheduleParam.getScheduleParamsAnchorList();
		for(ScheduleParamsAnchor anchor:scheduleParamsAnchorList) {
			RestTime restTime=new RestTime(Integer.valueOf(anchor.getDirection()),DateUtil.getDateHM(anchor.getBeginTime()), 
					DateUtil.getDateHM(anchor.getEndTime()), anchor.getAnchorDurationMin());
			restTimeList.add(restTime);
		}
	}

	protected boolean reduceRestTime() {
		RestTime maxRestTime=null;
		for(RestTime restTime:restTimeList) {
			if(maxRestTime==null||restTime.getRestTime()>maxRestTime.getRestTime()) {
				maxRestTime=restTime;
			}
		}
		int runDuration=scheduleParam.getWasteTime(scheduleParam.getFirstTime( maxRestTime.getDirection()), maxRestTime.getDirection());
		if(maxRestTime.getRestTime()>runDuration/6&&maxRestTime.getRestTime()>3) {
			maxRestTime.setRestTime(maxRestTime.getRestTime()-1);
			return true;
		}
		return false;
	}
	
	protected Integer getRestTime(int direction,Date tripEndTime) {
		RestTime restTime=null; 
		for(RestTime restTimeTemp:restTimeList) {
			if(restTimeTemp.getDirection()==direction&&
					DateUtil.isInTimeRange(tripEndTime, restTimeTemp.getBeginTime(), restTimeTemp.getEndTime())) {
				restTime=restTimeTemp;
				break;
			}
		}
		if(restTime==null) {
			for(RestTime restTimeTemp:restTimeList) {
				if(restTimeTemp.getDirection()==direction&&
						tripEndTime.equals(restTimeTemp.getEndTime())) {
					restTime=restTimeTemp;
					break;
				}
			}
		}
		if(!minRestMode&&restTime!=null)
			return restTime.getRestTime();
		if(!scheduleParam.isLoopLine()) {
			direction=1-direction;
		}
		Date tripBeginTime=getTripBeginTime(direction, tripEndTime);
		Integer wasteTime=DateUtil.getMinuteInterval(tripBeginTime, tripEndTime);
		Integer restTime4WasteTime=(int)Math.ceil(wasteTime/10.0);//每10分钟休息一分钟;
		if(restTime4WasteTime>10) {
			restTime4WasteTime=10;//最多10分钟
		}
		if(restTime!=null) {
			return Math.min(restTime4WasteTime, restTime.getRestTime());
		}
		return restTime4WasteTime;
	}
	
	protected void setLatestBusTripBeginTimeLatest() {
		initRestTimeMin();
		for(Integer startDirection:latestBusTripMap.keySet()) {
			List<LatestBusTrip> latestBusTripMinList=getLatestBusTripList(1-startDirection);
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(startDirection);
			for(int i=0;i<latestBusTripList.size();i++) {
				LatestBusTrip latestBusTrip=latestBusTripList.get(latestBusTripList.size()-i-1);
				if(latestBusTripMinList.size()-i>0) {
					LatestBusTrip latestBusTripMin=latestBusTripMinList.get(latestBusTripMinList.size()-i-1);
					if(latestBusTripMin.getTripBeginTime().after(latestBusTrip.getTripBeginTime())) {
						latestBusTrip.setTripBeginTimeLatest(latestBusTripMin.getTripBeginTime());
					}else {
						latestBusTrip.setTripBeginTimeLatest(latestBusTrip.getTripBeginTime());
					}
				}else {
					latestBusTrip.setTripBeginTimeLatest(latestBusTrip.getTripBeginTime());
				}
			}
		}
		initRestTime();
	}

	protected void genetateLatestBusTripList(int direction) {
		List<LatestBusTrip> latestBusTripList=getLatestBusTripList(direction);
		List<DirectionObuTimeRange> getObuTimeRangeList=getObuTimeRangeList(latestBusTripList);
		latestBusTripMap.put(1-direction,latestBusTripList);
		obuTimeRangeMap.put(1-direction, getObuTimeRangeList);
	}
	
	protected void initObuTimeRange() {
		for(Integer direction:latestBusTripMap.keySet()) {
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);
			List<DirectionObuTimeRange> getObuTimeRangeList=getObuTimeRangeList(latestBusTripList);
			obuTimeRangeMap.put(direction, getObuTimeRangeList);
		}
	}
	
	private int getMinRestTimeB4Latest(int direction) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		int wasteTime=scheduleParam.getWasteTime(latestTime, direction);
		int minRestTime=15;//末班车提前15分钟到
		if(wasteTime<30) {//单程少于30分钟，提前5分钟
			minRestTime=5;
		}else if(wasteTime<60) {//单程少于60分钟，提前5-10分钟
			minRestTime=(int)Math.ceil(5+(wasteTime-30)*5.0/30);
		}else {
			minRestTime=(int)Math.ceil(10+(wasteTime-60)*5.0/30);
			if(minRestTime>15) {
				minRestTime=15;
			}
		}
		if(minRestMode){
			minRestTime=(int)Math.ceil(minRestTime*0.6);
		}
		return minRestTime;
	}
	
	public List<LatestBusTrip> getLatestBusTripListOrig(int direction){
		List<LatestBusTrip> latestBusTripList=new ArrayList<LatestBusTrip>();
		Date firstTime=DateUtil.getDateHM(scheduleParam.getFirstTimeStr(direction));
		Date latestTime=DateUtil.getDateHM(scheduleParam.getLatestTimeStr(direction));
		if(latestTime.before(firstTime)) {//跨夜
			Date now=new Date();
			if(now.before(latestTime)) {
				firstTime=DateUtil.getDateAddDay(firstTime, -1);//当前时间第二天，首班时间减1天
			}else {
				latestTime=DateUtil.getDateAddDay(latestTime, 1);//当前时间当天,末班时间加1天
			}
		}
		Map<Integer, Date> latestTimeMap=new HashMap<Integer, Date>();
		latestTimeMap.put(direction, latestTime);
		if(!scheduleParam.isLoopLine()) {
			Date latestTimeReverse=DateUtil.getDateHM(scheduleParam.getLatestTimeStr(1-direction));
			Date latestTimeReverseNext=DateUtil.getDateAddDay(latestTimeReverse, 1);
			if(DateUtil.getMinuteInterval(latestTimeReverseNext, latestTime)<DateUtil.getMinuteInterval(latestTimeReverse, latestTime)) {
				latestTimeReverse=latestTimeReverseNext;
			}
			latestTimeMap.put(1-direction, latestTimeReverse);
		}
		Date adjustBeginTime=firstTime;
		int wasteTime=scheduleParam.getWasteTime(latestTime, direction);
		Date arrivalTime=DateUtil.getDateAddMinute(latestTime, wasteTime);
		int minRestTime=getMinRestTimeB4Latest(direction);
		LatestBusTrip trip=new LatestBusTrip(arrivalTime, direction, latestTime, minRestTime);
		latestBusTripList.add(trip);
		boolean hadSupper=false;
		if(scheduleParam.getSupperBeginTime(direction)==null&&scheduleParam.getSupperBeginTime(1-direction)==null) {
			hadSupper=true;
		}
		arrivalTime=DateUtil.getDateAddMinute(latestTime, -minRestTime);
		while(arrivalTime.after(adjustBeginTime)) {
			if(!scheduleParam.isLoopLine())
				direction=1-direction;
			Date tripBeginTime=getTripBeginTime(direction, arrivalTime, latestTimeMap.get(direction));
			if(!hadSupper) {
				Date supperBeginTime=scheduleParam.getSupperBeginTime(1-direction);
				if(supperBeginTime==null) {//对面不能安排吃饭
					supperBeginTime=scheduleParam.getSupperBeginTime(direction);
					if(tripBeginTime.before(supperBeginTime)&&latestBusTripList.size()>1) {//在这边吃
						latestBusTripList.remove(0);//删除对面班次
						trip=latestBusTripList.get(0);//上一班同方向
						tripBeginTime=trip.getTripBeginTime();//上上一班开车时间
						arrivalTime=DateUtil.getDateAddMinute(tripBeginTime, -scheduleParam.getSupperEatTime(direction));//上上一班应到站时间，重算对面班次
						direction=trip.getDirection();
						hadSupper=true;
						continue;
					}
					
				}else {
					if(tripBeginTime.before(supperBeginTime)) {//过了吃饭时间，在对面吃饭
						Date tripBeginTimeTemp=latestBusTripList.get(0).getTripBeginTime();//上一班开车时间
						if(!tripBeginTimeTemp.equals(scheduleParam.getLatestTime(direction))) {//防止吃饭时间设置为1分钟，造成尾车不能按时到
							arrivalTime=DateUtil.getDateAddMinute(tripBeginTimeTemp, -scheduleParam.getSupperEatTime(1-direction));//上一班应到站时间
							tripBeginTime=getTripBeginTime(direction, arrivalTime, latestTimeMap.get(direction));
							hadSupper=true;
						}
					}
				}
			}
			if(tripBeginTime.before(scheduleParam.getFirstTime(direction))) {
				break;
			}
			trip=new LatestBusTrip(arrivalTime, direction, tripBeginTime, minRestTime);
			latestBusTripList.add(0, trip);
			Integer restTime=getRestTime(direction, tripBeginTime);
			if(restTime==null)
				System.out.println("aaa");
			arrivalTime=DateUtil.getDateAddMinute(tripBeginTime, -restTime);
			System.out.println(direction+"\t"+tripBeginTime);
		}
		return latestBusTripList;
	}
	
	public List<LatestBusTrip> getLatestBusTripList(int direction) {
		List<LatestBusTrip> latestBusTripList=getLatestBusTripListOrig(direction);
		Date latestTime=scheduleParam.getLatestTime(direction);
		int minRestTime=getMinRestTimeB4Latest(direction);
		for(int i=0;i<latestBusTripList.size();) {
			LatestBusTrip trip=latestBusTripList.get(i);
			Integer restTime=scheduleParam.getLeaveImmediatelyInterval(trip.getDirection());
			if(restTime!=null) {
				latestBusTripList.remove(i);
				if(trip.getTripBeginTime().equals(latestTime)) {//末班车到站即走被删
					Date arrivalTime=scheduleParam.getArrivalTime(latestTime, trip.getDirection());
					int directionReverse=1-direction;
					Date latestTimeReverse=scheduleParam.getLatestTime(directionReverse);
					if(arrivalTime.before(latestTimeReverse)) {
						arrivalTime=scheduleParam.getArrivalTime(latestTimeReverse, directionReverse);
						trip=new LatestBusTrip(arrivalTime, directionReverse, latestTimeReverse, minRestTime);
						latestBusTripList.add(trip);
					}
				}
			}else {
				i++;
			}
		}
		Date tripBeginTimeNext=null;
		int restTimeRemainTrip=0;
		for(int i=latestBusTripList.size()-1;i>=0;i--) {
			LatestBusTrip latestBusTrip=latestBusTripList.get(i);
			if(tripBeginTimeNext!=null) {
				restTimeRemainTrip+=DateUtil.getMinuteInterval(latestBusTrip.getTripEndTime(), tripBeginTimeNext);
				latestBusTrip.setRestTimeRemainTrip(restTimeRemainTrip);
			}
			tripBeginTimeNext=latestBusTrip.getTripBeginTime();
		}
		System.out.println("aaaa");
		return latestBusTripList;
	}
	
	/**
	 * 根据到站时间倒推开出时间
	 * @param direction 发班方向
	 * @param arrivalTime 到达时间
	 * @return
	 */
	private Date getTripBeginTime(int direction,Date arrivalTime) {
		Date latestTime=scheduleParam.getLatestTime(direction);
		Date tripBeginTime=getTripBeginTime(direction, arrivalTime, latestTime);
		return tripBeginTime;
	}
	
	
	private Date getTripBeginTime(int direction,Date arrivalTime,Date latestTime) {
		int timeMinute=DateUtil.getTimeMinute(arrivalTime);
		Date tripBeginTime=DateUtil.getTimeByMinute((timeMinute/15)*15);//取前一15分钟
		if(DateUtil.getMinuteInterval(tripBeginTime, arrivalTime)>12*60) {//开始到达大于半天，可能隔天了，譬如00:05
			tripBeginTime=DateUtil.getDateAddDay(tripBeginTime, 1);
		}
		if(tripBeginTime.after(latestTime)) {
			tripBeginTime=latestTime;
		}
		Date arrivalTimeTemp=scheduleParam.getArrivalTime(tripBeginTime, direction);
		if(arrivalTimeTemp==null) {
			System.out.println("aaaa");
		}
		while(!arrivalTimeTemp.before(arrivalTime)) {
			tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, -15);
			arrivalTimeTemp=scheduleParam.getArrivalTime(tripBeginTime, direction);
		}
		/*int diff=DateUtil.getMinuteInterval(arrivalTimeTemp, arrivalTime);//到达时间差值
		tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, diff);//开出时间加上差值
*/		
		for(int i=0;i<15;i++) {
			arrivalTimeTemp=scheduleParam.getArrivalTime(tripBeginTime, direction);
			if(arrivalTimeTemp==null)
				System.out.println("aaa");
			if(!arrivalTimeTemp.before(arrivalTime)) {
				if(!arrivalTimeTemp.equals(arrivalTime)) {//结束时间不相同，取前一分钟开出
					tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, -1);
				}
				break;
			}
			if(tripBeginTime.equals((scheduleParam.getLatestTime(direction))))
				break;
			tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, 1);
		}
		return tripBeginTime;
	}
	
	private List<DirectionObuTimeRange> getObuTimeRangeList(List<LatestBusTrip> latestBusTripList) {
		List<DirectionObuTimeRange> obuTimeRangeList=new ArrayList<DirectionObuTimeRange>();
		int surplusTrip=2;
		int tripSize=latestBusTripList.size();
		for(int i=0;i<tripSize-1;i++) {
			LatestBusTrip tripBegin=latestBusTripList.get(i);
			LatestBusTrip tripEnd=latestBusTripList.get(i+1);
			DirectionObuTimeRange obuTimeRange=new DirectionObuTimeRange();
			obuTimeRange.setBeginTime(tripBegin.getTripBeginTime());
			obuTimeRange.setDirection(tripEnd.getDirection());
			if(tripEnd.getTripBeginTimeLatest()!=null)
				obuTimeRange.setEndTime(tripEnd.getTripBeginTimeLatest());
			else {
				obuTimeRange.setEndTime(tripEnd.getTripBeginTime());
			}
			obuTimeRange.setSurplusTrip(tripSize-surplusTrip);
			surplusTrip++;
			obuTimeRangeList.add(obuTimeRange);
		}
		return obuTimeRangeList;
	}
	
	public boolean isBusReverse(int startDirection,Date obuTime,int direction) {
		List<DirectionObuTimeRange> obuTimeRangeList=obuTimeRangeMap.get(startDirection);
		for(DirectionObuTimeRange obuTimeRange:obuTimeRangeList) {
			if(obuTimeRange.getDirection()==direction&&
					DateUtil.isInTimeRange(obuTime, obuTimeRange.getBeginTime(), obuTimeRange.getEndTime())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isLatestBus(Bus bus) {
		if(bus==latestBusMain||bus==latestBusSecondary) {
			return true;
		}
		return false;
	}
	
	protected abstract void setLatestBus();
	
	protected abstract Date getObuTime(Trip tripArrival,int direction);
	
	protected abstract void removeLatestBusTrip(Bus bus);
	
	protected List<Trip> getTripArrivalAdjustList() {
		return new ArrayList<Trip>();
	}
	
	protected Bus getLatestBusMain() {
		return latestBusMain;
	}
	
	protected Bus getLatestBusSecondary() {
		return latestBusSecondary;
	}
	
	protected abstract boolean isLatestBusLastRound();
	
	protected abstract void busReverseAdjust4Short(ScheduleHalfHour scheduleHalfHour);
	
	protected abstract Integer getDispatchInterval(int direction);
	
	class DirectionObuTimeRange{
		
		private int direction;
		
		private Date beginTime;
		
		private Date endTime;
		
		private Integer surplusTrip;//剩余趟次

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public Date getBeginTime() {
			return beginTime;
		}

		public void setBeginTime(Date beginTime) {
			this.beginTime = beginTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public Integer getSurplusTrip() {
			return surplusTrip;
		}

		public void setSurplusTrip(Integer surplusTrip) {
			this.surplusTrip = surplusTrip;
		}
		
	}
	
	protected void updateLatestBusTripList(Trip latestBusTrip) {
		if(!latestBusMatched) {
			LatestBusTrip latestBusTripMatch=null;
			int direction=latestBusTrip.getBus().getStartDirection();
			if(mainDirection!=Direction.NODIRECTION.getValue()) {
				direction=1-mainDirection;
			}
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);
			for(int i=0;i<latestBusTripList.size();i++) {
				LatestBusTrip trip=latestBusTripList.get(i);
				if(trip.getDirection()==1-latestBusTrip.getDirection()) {
					if(!trip.getTripBeginTime().before(latestBusTrip.getNextObuTimeMin())) {
						latestBusMatched=true;
						latestBusTripMatch=trip;
						break;
					}
				}
			}
			if(latestBusMatched) {
				int diff=DateUtil.getMinuteInterval(latestBusTrip.getNextObuTimeMin(), latestBusTripMatch.getTripBeginTime());
				int index=latestBusTripList.indexOf(latestBusTripMatch);
				if(latestBusTripList.size()-1-index>0) {
					int restTimeIncrease=diff/(latestBusTripList.size()-1-index);
					System.out.println(restTimeIncrease);
					if(restTimeIncrease>10) {//单程增加超过10分钟，看能不能减少停站时长
						latestBusMatched=false;
						latestBusMain=null;
						if(reduceRestTime()) {
							retry++;
							if(retry>100000) {
								throw new MyException("-1", "计划生成失败，请联系开发解决\t"+scheduleParam.getRouteId());
							}
							genetateLatestBusTripList();//减少停站时间，重算标准时间
							if(!latestBusTrip.isEatAfterTrip()&&!latestBusTrip.isElecSupplyAfterTrip()) {
								Date tripEndTime=latestBusTrip.getTripEndTime();
								if(tripEndTime!=null) {
									int restTime=getRestTime(latestBusTrip.getDirection(), tripEndTime);
									latestBusTrip.setNextObuTimeMin(DateUtil.getDateAddMinute(tripEndTime, restTime));
								}
							}
							updateLatestBusTripList(latestBusTrip);
							return;
						}else {//不能压缩停站时间，初始化数据
							initRestTime();
							genetateLatestBusTripList();
							latestBusTripList=latestBusTripMap.get(direction);
							for(int i=0;i<latestBusTripList.size();i++) {
								LatestBusTrip trip=latestBusTripList.get(i);
								if(trip.getDirection()==1-latestBusTrip.getDirection()) {
									if(!trip.getTripBeginTime().before(latestBusTrip.getNextObuTimeMin())) {
										latestBusMatched=true;
										latestBusTripMatch=trip;
										break;
									}
								}
							}
							diff=DateUtil.getMinuteInterval(latestBusTrip.getNextObuTimeMin(), latestBusTripMatch.getTripBeginTime());
							latestBusMain=latestBusTrip.getBus();
							latestBusMatched=true;
						}
						
					}else {
						latestBusMatched=true;
						latestBusMain=latestBusTrip.getBus();
					}
				}else {
					if(diff>20) {
						latestBusMatched=false;
						latestBusMain=null;
						return;
					}	
				}
				latestBusMain=latestBusTrip.getBus();
				setLatestBusTripBeginTimeLatest();
				if(this instanceof BusReverseAdjust4Full|| this instanceof BusAdjust4Full) {//短线调位不删除，发班是在获取对应哪个车位
					for(int i=0;i<latestBusTripList.size();) {
						LatestBusTrip trip=latestBusTripList.get(i);
						if(trip==latestBusTripMatch) {
							break;
						}
						latestBusTripList.remove(i);
					}
					for(int i=0;i<latestBusTripList.size();i++) {
						LatestBusTrip trip=latestBusTripList.get(i);
						Date tripBeginTimeOld=trip.getTripBeginTime();
						if((latestBusTripList.size()-i-1)!=0){
							int intervalDiff=diff/(latestBusTripList.size()-i);
							trip.setTripBeginTime(DateUtil.getDateAddMinute(trip.getTripBeginTime(), intervalDiff-diff));
						}
						System.out.println(tripBeginTimeOld+"====>"+trip.getTripBeginTime()+"\t"+DateUtil.getMinuteInterval(tripBeginTimeOld, trip.getTripBeginTime()));
						diff=DateUtil.getMinuteInterval(trip.getTripBeginTime(), tripBeginTimeOld);
					}
				}else {
					List<LatestBusTrip> latestBusMainSurplusTripList=getLatestBusMainSurplusTripList();
					LatestBusTrip latestBusMainTripNext=latestBusMainSurplusTripList.get(0);
					if(latestBusTrip.getNextObuTimeMin().before(latestBusMainTripNext.getStandardTripBeginTime())) {
						diff=DateUtil.getMinuteInterval(latestBusMainTripNext.getStandardTripBeginTime(), latestBusTrip.getNextObuTimeMin());
						for(int i=0;i<latestBusMainSurplusTripList.size()-1;i++) {
							LatestBusTrip latestBusMainTrip=latestBusMainSurplusTripList.get(i);
							Date tripBeginTime=DateUtil.getDateAddMinute(latestBusMainTrip.getStandardTripBeginTime(), (int)(-diff*(1-(i+1)*1.0/latestBusMainSurplusTripList.size())));
							latestBusMainTrip.setTripBeginTime(tripBeginTime);
							System.out.println(latestBusMainTrip.getStandardTripBeginTime()+"====>"+latestBusMainTrip.getTripBeginTime()+"\t"+DateUtil.getMinuteInterval(latestBusMainTrip.getStandardTripBeginTime(), latestBusMainTrip.getTripBeginTime()));
						}
					}
					latestBusTripMap.put(direction, latestBusMainSurplusTripList);
				}
				setLatestBusTripBeginTimeLatest();
			}else {
				System.out.println("aaa");
				latestBusMain=null;
			}
		}
	}

	class RestTime{
		
		private int direction;
		
		private Date beginTime;
		
		private Date endTime;
		
		private int restTime;

		public RestTime(int direction, Date beginTime, Date endTime, int restTime) {
			super();
			this.direction = direction;
			this.beginTime = beginTime;
			this.endTime = endTime;
			this.restTime = restTime;
		}

		public int getDirection() {
			return direction;
		}

		public Date getBeginTime() {
			return beginTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public int getRestTime() {
			return restTime;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public void setBeginTime(Date beginTime) {
			this.beginTime = beginTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public void setRestTime(int restTime) {
			this.restTime = restTime;
		}
		
	}

	protected List<Trip> getBusQueue(int direction){
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		List<Trip> busQueue=new ArrayList<Trip>();
		busQueue.addAll(busQueueTo);
		busQueue.addAll(busQueueFrom);
		return busQueue;
	}
	
	protected List<Trip> getB4LatestBusMainTripList(List<Trip> busQueue) {
		List<Trip> arrivalTripList4Full=new ArrayList<Trip>();
		Trip latestBusTrip=null;
		for(Trip trip:busQueue) {
			if(!trip.isQuitMark()&&trip.getShortLineAdjust()==null) {
				arrivalTripList4Full.add(trip);
			}
			if(trip.getBus()==latestBusMain) {
				latestBusTrip=trip;
				break;
			}
		}
		if(latestBusTrip==null) {
			return null;
		}
		return arrivalTripList4Full;
	}
	
	protected List<Trip> getB4LatestBusSecondaryTripList(List<Trip> busQueue) {
		List<Trip> arrivalTripList4Full=new ArrayList<Trip>();
		Trip latestBusTrip=null;
		for(Trip trip:busQueue) {
			if(!trip.isQuitMark()&&trip.getShortLineAdjust()==null) {
				arrivalTripList4Full.add(trip);
			}
			if(trip.getBus()==latestBusSecondary) {
				latestBusTrip=trip;
				break;
			}
		}
		if(latestBusTrip==null) {
			return null;
		}
		return arrivalTripList4Full;
	}
	
	protected List<Trip> getB4LatestBusMainTripList(int direction) {
		List<Trip> busQueue=getBusQueue(direction);
		List<Trip> arrivalTripList4Full=getB4LatestBusMainTripList(busQueue);
		return arrivalTripList4Full;
	}
	
	protected List<LatestBusTrip> getLatestBusMainSurplusTripList(){
		List<Trip> latestBusMainTripList=routeSchedule.getTripList(latestBusMain);
		Trip latestBusMainTrip=latestBusMainTripList.get(latestBusMainTripList.size()-1);
		if(latestBusMainTrip.getDirection()==Direction.NODIRECTION.getValue()) {
			latestBusMainTrip=latestBusMainTripList.get(latestBusMainTripList.size()-2);
		}
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusMainTrip.getBus().getStartDirection());
		Integer diff=null;
		LatestBusTrip latestBusTripStandard=null;
		for(LatestBusTrip trip:latestBusTripList) {
			if(trip.getDirection()==latestBusMainTrip.getDirection()) {
				int diffTemp=DateUtil.getMinuteInterval(trip.getTripBeginTime(), latestBusMainTrip.getTripBeginTime());
				//限制在最晚开出时间前看出，最多晚末班车预留时间的1/2，防止卡在中间，明显赶不上
				if((diff==null||diffTemp<diff)&&
						latestBusMainTrip.getTripBeginTime().before(DateUtil.getDateAddMinute(trip.getTripBeginTimeLatest(), trip.getArriveEarlyEndTrip()/2))) {
					latestBusTripStandard=trip;
					diff=diffTemp;
				}
			}
		}
		List<LatestBusTrip> latestBusMainSurplusTripList=new ArrayList<LatestBusTrip>();
		if(latestBusTripStandard!=null) {
			int index=latestBusTripList.indexOf(latestBusTripStandard);
			if(index<latestBusTripList.size()-1) {
				latestBusMainSurplusTripList.addAll(latestBusTripList.subList(index+1, latestBusTripList.size()));
				return latestBusMainSurplusTripList;
			}
			return latestBusMainSurplusTripList;
		}
		return null;
	}
	
	protected boolean isLastRound(int direction) {
		if(latestBusMain==null||latestBusSecondary==null)
			return false;
		List<LatestBusTrip> latestBusMainSurplusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
		if(latestBusMainSurplusTripList.size()==1&&direction!=latestBusMain.getStartDirection()){
			List<Trip> latestBusSecondaryTripList=routeSchedule.getTripList(latestBusSecondary);
			Trip latestBusSecondaryTrip=latestBusSecondaryTripList.get(latestBusSecondaryTripList.size()-1);
			if(latestBusSecondaryTrip.getDirection()==direction||latestBusSecondaryTrip.isShortLine())//尾车已发出过去做末班车，或者短线掉头回对面
				return true;
			List<Trip> latestBusMainTripList=routeSchedule.getTripList(latestBusMain);//过来做末班车
			Trip latestBusMainTrip=latestBusMainTripList.get(latestBusMainTripList.size()-1);
			if(latestBusSecondaryTrip.getTripBeginTime().after(latestBusMainTrip.getTripBeginTime())) {//对面尾车已发
				return true;
			}
		}else if(latestBusMainSurplusTripList.size()<2&&direction==latestBusMain.getStartDirection()) {
			return true;
		}
		return false;
	}
	
	protected boolean isLastCircle(int direction) {
		if(latestBusMain==null||latestBusSecondary==null)
			return false;
		List<LatestBusTrip> latestBusTripList=null;
		if(mainDirection==Direction.NODIRECTION.getValue()) {
			latestBusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
		}else {
			latestBusTripList=latestBusTripMap.get(1-mainDirection);
		}
		if(latestBusTripList.size()==2) {
			LatestBusTrip latestBusTrip=latestBusTripList.get(1);
			if(direction==latestBusTrip.getDirection()) {
				return true;
			}else {
				List<Trip> latestBusSecondaryTripList=routeSchedule.getRunTripList(latestBusSecondary);
				Trip latestBusSecondaryTrip=latestBusSecondaryTripList.get(latestBusSecondaryTripList.size()-1);
				if(direction==latestBusSecondaryTrip.getDirection()) {
					return true;
				}else {
					latestBusTripList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
					if(DateUtil.getMinuteInterval(latestBusTripList.get(latestBusTripList.size()-2).getTripBeginTime(), latestBusSecondaryTrip.getTripBeginTime())<
							DateUtil.getMinuteInterval(latestBusTripList.get(latestBusTripList.size()-4).getTripBeginTime(), latestBusSecondaryTrip.getTripBeginTime())) {
						return true;
					}
				}
			}
		}else if(latestBusTripList.size()==1) {
			return true;
		}
		return false;
	}

	protected boolean isAfterBusReverseBegin(Date runTime) {
		// TODO Auto-generated method stub
		return false;
	}

	protected Integer getDispatchIntervalMax(int direction) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void checkLatestBus(Trip latestBusMainTrip) {
		// TODO Auto-generated method stub
	}
	
	protected void setQuitMarkOffDutyEarly(int direction) {
		List<Trip> bus_queue_to=routeSchedule.getBusQueue(1-direction);
		if(bus_queue_to.isEmpty()||bus_queue_to.get(bus_queue_to.size()-1).getNextObuTimeMin().before(scheduleParam.getOffDutyBeginTime()))//没到收车阶段
			return;
		Trip tripLast=routeSchedule.getTripFullLast(direction);
		int maxInteval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
		int index=-1;//最后预设双班车早收索引
		for(int i=0;i<bus_queue_to.size();i++) {
			Trip trip=bus_queue_to.get(i);
			if(trip.isQuitMark()) {
				if(!trip.getBus().isSingleClass()) {
					index=i;
				}
			}
		}
		if(index==-1) {
			index=0;
		}else {
			index+=2;
		}
		for(int i=index;i<bus_queue_to.size()-1;i++) {
			Trip tripArrival=bus_queue_to.get(i);
			if(tripArrival.getShortLineAdjust()!=null||
				tripArrival.isQuitMark()) {//这个车位短线或退出
				continue;
			}
			if(!scheduleParam.isEndDirectionTrip(tripArrival)) {
				continue;
			}
			if(isLatestBus(tripArrival.getBus())) {
				continue;
			}
			Trip tripArrivalNext=bus_queue_to.get(i+1);
			if(tripArrivalNext.getShortLineAdjust()!=null||
				 tripArrivalNext.isQuitMark()) {//下个车位短线或退出
				continue;
			}
			if(i>0) {
				Trip tripArrivalPre=bus_queue_to.get(i-1);
				if(tripArrivalPre.getShortLineAdjust()!=null||
					tripArrivalPre.isQuitMark()) {//上个车位短线或退出
					continue;
				}
			}
			if(tripArrival.getTripEndTime()!=null&&
				!tripArrival.getTripEndTime().before(scheduleParam.getOffDutyBeginTime())) {//到达时间过了早收开始时间
				Trip latestBusTrip=getLatestBusTrip(direction);//计算间隔的尾车
				if(latestBusTrip!=null) {
					latestBusTrip=getLatestBusTrip(direction);//计算间隔的尾车
					if(latestBusTrip.getDirection()==direction) {
						int latestBusIndex=bus_queue_to.indexOf(latestBusTrip);
						if(i>latestBusIndex) {//在尾车后，等尾车过了在看
							break;
						}
					}
					tripArrival.setQuitMark(true);
					int intervalNew=getDispatchInterval(direction);
					if(intervalNew>maxInteval) {//减少一台车会断位，不能早收
						tripArrival.setQuitMark(false);
						break;
					}
					List<Trip> tripArrival4FullList=new ArrayList<Trip>();
					for(Trip trip:bus_queue_to) {
						if(!trip.isQuitMark()&&trip.getShortLineAdjust()==null) {
							tripArrival4FullList.add(trip);
						}
					}
					Date obuTimeLast=tripLast.getTripBeginTime();
					boolean pass=false;
					for(Trip trip:tripArrival4FullList) {
						if(trip==tripArrivalNext) {
							pass=true;
						}
						Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, intervalNew);
						if(pass&&trip.getNextObuTimeMin().after(obuTime)) {//早收有车赶不上
							tripArrival.setQuitMark(false);
							continue;
						}
						if(trip==latestBusTrip) {
							break;
						}
						obuTimeLast=obuTime;
					}
				}
			}
		}
	}
	
	protected Trip getLatestBusTrip(int direction) {
		if(latestBusMain==null)
			return null;
		List<Trip> latestBusMainTripList=routeSchedule.getTripList(latestBusMain);
		Trip latestBusMainTrip=latestBusMainTripList.get(latestBusMainTripList.size()-1);
		if(latestBusMainTrip.getDirection()==Direction.NODIRECTION.getValue()) {
			latestBusMainTrip=latestBusMainTripList.get(latestBusMainTripList.size()-2);
		}
		List<Trip> latestBusSecondaryTripList=routeSchedule.getTripList(latestBusSecondary);
		Trip latestBusSecondaryTrip=latestBusSecondaryTripList.get(latestBusSecondaryTripList.size()-1);
		List<LatestBusTrip> latestBusMainSurplusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
		if(latestBusMainSurplusTripList.size()==2&&direction!=latestBusMain.getStartDirection()){
			if(latestBusSecondaryTrip.getDirection()!=direction) {
				return latestBusSecondaryTrip;
			}
		}else if(latestBusMainSurplusTripList.size()<2&&direction==latestBusMain.getStartDirection()) {
			return latestBusSecondaryTrip;
		}
		return latestBusMainTrip;
	}
	
	protected boolean isNeed2Adjust(Trip tripArrival, int direction) {
		/*if(!isNeed2Adjust(tripArrival.getBus(), tripArrival.getNextObuTimeMin(), direction)) {
			return false;
		}*/
		Bus latestBus=null;
		Bus latestBusReverse=null;
		if(latestBusMain!=null) {
			if(latestBusMain.getStartDirection()==tripArrival.getBus().getStartDirection()) {
				latestBus=latestBusMain;
				latestBusReverse=latestBusSecondary;
			}
		}
		if(latestBusSecondary!=null) {
			if(latestBusSecondary.getStartDirection()==tripArrival.getBus().getStartDirection()) {
				latestBus=latestBusSecondary;
				latestBusReverse=latestBusMain;
			}
		}
		if(tripArrival.getBus().getStartDirection()==0&&tripArrival.getBus().getStartOrderNumber()==3) {
			System.out.println("aaa");
		}
		if(latestBus!=null&&latestBusReverse!=null) {
			Trip latestBusTrip=null;
			Trip latestBusReverseTrip=null;
			List<Trip> busQueueUp=routeSchedule.getBusQueueDoubleAndEveningClasses(Direction.UP.getValue());
			List<Trip> busQueueDown=routeSchedule.getBusQueueDoubleAndEveningClasses(Direction.DOWN.getValue());
			List<Trip> busQueue=new ArrayList<Trip>();
			busQueue.addAll(busQueueUp);
			busQueue.addAll(busQueueDown);
			for(Trip trip:busQueue) {
				if(trip.getBus()==latestBus) {
					latestBusTrip=trip;
				}else if(trip.getBus()==latestBusReverse) {
					latestBusReverseTrip=trip;
				}
			}
			busQueue.addAll(busQueueUp);
			busQueue.addAll(busQueueDown);
			int index=busQueue.indexOf(latestBusReverseTrip);
			if(index==-1)
				return false;
			busQueue=busQueue.subList(index, busQueue.size());
			index=busQueue.indexOf(latestBusTrip);
			busQueue=busQueue.subList(0, index+1);
			index=busQueue.indexOf(tripArrival);
			if(index==-1)//车辆在反向末班车和同向末班车之间
				return true;
		}
		return false;
	}
	
	/**
	 * 总站调位
	 */
	protected void terminalAdjust(int direction) {
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		for(int i=0;i<busQueueTo.size()-1;i++) {
			Trip tripArrival=busQueueTo.get(i);
			if(isLatestBus(tripArrival.getBus())) {//尾车
				Trip tripArrivalNext=busQueueTo.get(i+1);
				if(tripArrivalNext.getBus().getStartDirection()!=tripArrival.getBus().getStartDirection())
					continue;
				if(getTripArrivalAdjustList().contains(tripArrivalNext)) {//赶不上，要短线调位，不换位
					continue;
				}
				if(isNeed2Adjust(tripArrivalNext, direction)) {//尾车后一台车车位不对，前后换位
					busQueueTo.set(i, tripArrivalNext);
					busQueueTo.set(i+1, tripArrival);
					break;
				}
			}
		}
	}
	
}
