package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.LatestBusTrip;
import com.gci.schedule.driverless.dynamic.enums.Direction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusReverseAdjust4Full extends BusAdjustBase{
	
	private boolean latestBusLastRound;//末班车最后一轮
	
	private int retry=0;
	
	private boolean latestBusAdjust;//尾车已发调位第一班，防止突变断位
	
	public BusReverseAdjust4Full(ScheduleParam scheduleParam) {
		super(scheduleParam);
	}
	
	public BusReverseAdjust4Full(ScheduleParam scheduleParam, RouteSchedule routeSchedule) {
		super(scheduleParam, routeSchedule);
		genetateLatestBusTripList();
		if(scheduleParam.getEndDirection()!=null&&
			scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {//设置两边停场
			resetEndDirection();
		}
	}
	
	private void resetEndDirection() {
		int busNumberUp=0;
		int busNumberDown=0;
		for(Bus bus:routeSchedule.getTripMap().keySet()) {
			if(bus.getStartDirection()==Direction.UP.getValue()) {
				busNumberUp++;
			}else {
				busNumberDown++;
			}
		}
		if(busNumberUp==0) {
			scheduleParam.setEndDirection(Direction.DOWN.getValue());
		}else if(busNumberDown==0) {
			scheduleParam.setEndDirection(Direction.UP.getValue());
		}
	}
	
	@Override
	public void setLatestBus() {
		if(scheduleParam.getEndDirection()!=null/*&&
				scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()&&
				latestBusMain==null*/) {
			if(latestBusMain!=null&&latestBusMain.isSingleClass()) {//尾车变单班车，重新选
				latestBusMain=null;
			}
			if(latestBusMain!=null) {
				List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses();
				Trip latestBusTrip=null;
				for(Trip trip:busQueue) {
					if(trip.getBus()==latestBusMain) {
						latestBusTrip=trip;
						break;
					}
				}
				if(latestBusTrip==null){//可能在充电
					if(routeSchedule.getTripInBusQueueShortBack(latestBusMain)!=null) {
						return;
					}
				}
				int direction=latestBusMain.getStartDirection();
				if(mainDirection!=Direction.NODIRECTION.getValue()) {
					direction=1-mainDirection;
				}
				List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);
				if(!latestBusTripList.isEmpty()) {
					LatestBusTrip latestBusTripRefer=latestBusTripList.get(0);
					if(latestBusTrip.getNextObuTimeMin().after(latestBusTripRefer.getTripBeginTimeLatest())) {
						int diff=DateUtil.getMinuteInterval(latestBusTrip.getNextObuTimeMin(), latestBusTripRefer.getTripBeginTimeLatest());
						if(latestBusTrip.getTripEndTime()!=null&&latestBusTrip.getTripEndTime().before(latestBusTripRefer.getTripBeginTimeLatest())) {
							diff=0;
						}
						if(diff>latestBusTripRefer.getArriveEarlyEndTrip()/2) {//晚到时间超过末班车提前时间的一半，重新设置末班车
							/*genetateLatestBusTripList(1-direction);
							latestBusMain=null;
							latestBusMatched=false;*/
							Trip tripLast=latestBusTrip.getLastTripFull();
							while(tripLast!=null) {
								if(!tripLast.isQuitMark()&&!tripLast.getBus().isSingleClass()&&tripLast.getBus().getStartDirection()==latestBusMain.getStartDirection()&&
									!tripLast.getNextObuTimeMin().after(DateUtil.getDateAddMinute(latestBusTripRefer.getTripBeginTimeLatest(), latestBusTripRefer.getArriveEarlyEndTrip()/2))) {
									latestBusMain=tripLast.getBus();//换前边的做尾车
									break;
								}
								tripLast=tripLast.getLastTripFull();
							}
						}
					}
				}
				
			}
			if(latestBusMain!=null)
				return;
			if(scheduleParam.getEndDirection()!=Direction.NODIRECTION.getValue()) {//单边停场
				Trip firstTrip=null;
				List<Trip> busQueue=routeSchedule.getBusQueueDoubleAndEveningClasses();
				for(Trip trip:busQueue) {
					Bus bus=trip.getBus();
					List<Trip> busTripList=routeSchedule.getTripList(bus);
					if(busTripList==null){//夜班车
						continue;
					}
					Trip firstTripTemp=busTripList.get(0);
					if(firstTrip==null) {
						firstTrip=firstTripTemp;
					}else {
						if(firstTrip.getDirection()==scheduleParam.getEndDirection()) {
							if(bus.getStartDirection()==scheduleParam.getEndDirection()) {//同方向
								if(firstTripTemp.getTripBeginTime().before(firstTrip.getTripBeginTime())) {
									firstTrip=firstTripTemp;
								}
							}else {
								Date arrivalTime=scheduleParam.getArrivalTime(firstTrip.getTripBeginTime(), firstTrip.getDirection());
								if(firstTripTemp.getTripBeginTime().before(arrivalTime)) {//提前过对面
									firstTrip=firstTripTemp;
								}
							}
						}else {
							if(bus.getStartDirection()==scheduleParam.getEndDirection()) {
								Date arrivalTime=scheduleParam.getArrivalTime(firstTripTemp.getTripBeginTime(), firstTripTemp.getDirection());
								if(arrivalTime.before(firstTrip.getTripBeginTime())) {
									firstTrip=firstTripTemp;
								}
							}else {
								if(firstTripTemp.getTripBeginTime().before(firstTrip.getTripBeginTime())) {
									firstTrip=firstTripTemp;
								}
							}
						}
					}
				}
				if(firstTrip==null)
					return;
				Trip firstBusTrip=null;
				for(Trip trip:busQueue) {
					if(trip.getBus()==firstTrip.getBus()) {
						firstBusTrip=trip;
						break;
					}
				}
				busQueue.addAll(busQueue);
				int beginIndex=busQueue.indexOf(firstBusTrip);
				int endIndex=busQueue.lastIndexOf(firstBusTrip);
				busQueue=busQueue.subList(beginIndex, endIndex);
				//找出收车顺序，看哪台适合做尾车（665）
				for(int i=busQueue.size()-1;i>=0;i--) {
					Trip latestBusTrip=busQueue.get(i);
					latestBusMain=latestBusTrip.getBus();
					updateLatestBusTripList(latestBusTrip);
					if(latestBusMain!=null) {
						break;
					}
				}
				
			}else {//两边总站停场
				int direction=Direction.UP.getValue();
				if(mainDirection!=Direction.NODIRECTION.getValue()) {
					direction=mainDirection;
				}
				Trip latestBusTrip=getLatestBusTrip4Full(direction);
				if(latestBusTrip!=null) {
					if(latestBusTrip.getBus()!=latestBusMain) {
						latestBusMain=latestBusTrip.getBus();
						updateLatestBusTripList(latestBusTrip);
					}
				}
				latestBusTrip=getLatestBusTrip4Full(1-direction);
				//updateLatestBusTripList(latestBusTrip);
				if(latestBusTrip!=null) {
					if(latestBusTrip.getBus()!=latestBusSecondary) {
						latestBusSecondary=latestBusTrip.getBus();
					}
				}
			}
		}
		if(scheduleParam.getSingleBusTripNumber()==null){
			if(latestBusMain!=null) {
				int tripNum=0;
				List<Trip> busTripList=routeSchedule.getTripList(latestBusMain);
				for(Trip trip:busTripList) {
					if(trip.getDirection()!=Direction.NODIRECTION.getValue()) {
						tripNum++;
					}
				}
				int direction=latestBusMain.getStartDirection();
				if(mainDirection!=Direction.NODIRECTION.getValue()) {
					direction=1-mainDirection;
				}
				List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(direction);
				tripNum+=latestBusTripList.size();
				int runTime=DateUtil.getMinuteInterval(scheduleParam.getFirstTime(latestBusMain.getStartDirection()), scheduleParam.getLatestTime(latestBusMain.getStartDirection()));
				int wasteTimeAvg=runTime/tripNum;
				scheduleParam.initSingleBusTripNumber(wasteTimeAvg);
			}
		}
		System.out.println("aaa");
	}
	
	private Trip getLatestBusTrip4Full(int direction) {
		int lastRoundDirection=1-direction;
		List<Trip> tripListUp=routeSchedule.getBusQueueDoubleAndEveningClasses(Direction.UP.getValue());
		List<Trip> tripListDown=routeSchedule.getBusQueueDoubleAndEveningClasses(Direction.DOWN.getValue());
		List<Trip> tripList=new ArrayList<Trip>();
		tripList.addAll(tripListUp);
		tripList.addAll(tripListDown);
		tripList.addAll(tripListUp);//加两次从后面往前计算连续台数
		tripList.addAll(tripListDown);
		int busNumber=tripListUp.size()+tripListDown.size();
		System.out.println("=================================");
		int maxSameDirectionNumber=0;
		Trip latestBusTrip=null;
		for(int i=busNumber;i<tripList.size();i++) {
			Trip trip=tripList.get(i);
			int startDirection=trip.getBus().getStartDirection();
			if(startDirection==lastRoundDirection) {
				int sameDirectionNumber=0;
				for(int j=i;j>=0;j--) {
					Trip tripTemp=tripList.get(j);
					if(tripTemp.getBus().getStartDirection()==startDirection) {
						sameDirectionNumber++;
					}else {
						if(sameDirectionNumber>maxSameDirectionNumber) {
							maxSameDirectionNumber=sameDirectionNumber;
							latestBusTrip=trip;
						}
						System.out.println(trip.getBusName()+"\t"+sameDirectionNumber);
						break;
					}
				}
			}
		}
		int beginIndex=tripList.indexOf(latestBusTrip);
		int endIndex=tripList.lastIndexOf(latestBusTrip);
		if(endIndex==-1)//单边出车
			return null;
		tripList=tripList.subList(beginIndex+1, endIndex);
		Trip firstBusTrip=null;
		for(int i=tripList.size()-1;i>=0;i--) {
			Trip trip=tripList.get(i);
			if(trip.getBus().getStartDirection()==latestBusTrip.getBus().getStartDirection()) {
				firstBusTrip=trip;
			}else {
				break;
			}
		}
		//解决下行一台车插在上行车中间
		endIndex=tripList.indexOf(firstBusTrip);
		if(endIndex!=-1) {
			System.out.println("aa");
			tripList=tripList.subList(0, endIndex-1);//等到反向队列
			int reverseDirectionBusNumber=0;
			int sameDirectionBusNumber=0;
			Trip latestBusTripTemp=null;
			for(int i=0;i<tripList.size();i++) {
				Trip trip=tripList.get(i);
				if(trip.getBus().getStartDirection()!=latestBusTrip.getBus().getStartDirection()) {
					if(sameDirectionBusNumber==0) {
						reverseDirectionBusNumber++;
					}else {
						break;
					}
				}else {
					sameDirectionBusNumber++;
					latestBusTripTemp=trip;
				}
			}
			if(latestBusTripTemp!=null&&sameDirectionBusNumber>=reverseDirectionBusNumber) {//反插的车比同向的少，可以让反插的早收
				latestBusTrip=latestBusTripTemp;
			}
		}
		return latestBusTrip;
	}
	
	private Date getObuTimeB4LastRound(int direction) {
		if(latestBusMain==null)
			return null;
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		List<Trip> busQueue=new ArrayList<Trip>();
		for(Trip trip:busQueueTo) {
			if(!trip.isQuitMark())
				busQueue.add(trip);
		}
		for(Trip trip:busQueueFrom) {
			if(!trip.isQuitMark())
				busQueue.add(trip);
		}
		Trip latestBusTrip=null;
		int busNumber=0;
		Trip firstEatTrip=null;
		for(Trip trip:busQueue) {
			busNumber++;
			if(trip.getBus()==latestBusMain) {
				latestBusTrip=trip;
				break;
			}
			if(firstEatTrip==null&&busQueueTo.contains(trip)&&trip.isEatAfterTrip()) {//找到第一个吃饭车位
				firstEatTrip=trip;
			}
		}
		if(!latestBusMatched) {
			if(latestBusTrip!=null)
				updateLatestBusTripList(latestBusTrip);
			if(!latestBusMatched)
				return null;
		}
		if(busQueueTo.get(0)==latestBusTrip)
			System.out.println("aaa");
		if(latestBusTrip!=null) {
			int latestBusDrection=latestBusTrip.getBus().getStartDirection();
			if(mainDirection!=Direction.NODIRECTION.getValue()) {
				latestBusDrection=1-mainDirection;
			}
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusDrection);
			if(latestBusTripList.isEmpty())//对面尾车已发
				return null;
			LatestBusTrip latestBusTripReference=latestBusTripList.get(0);
			if(latestBusTripReference.getDirection()==latestBusTrip.getDirection()) {//方向错位了
				latestBusTripList.remove(0);
				latestBusTripReference=latestBusTripList.get(0);
			}
			if(latestBusTrip.getDirection()==direction) {
				if(latestBusTripList.size()>1) {
					latestBusTripReference=latestBusTripList.get(1);
				}
			}
			Date latestBusObuTime=latestBusTripReference.getTripBeginTime();
			Trip tripLast=routeSchedule.getTripLast(direction);
			if(DateUtil.getDateHM("1440").equals(tripLast.getTripBeginTime()))
				System.out.println("aaa");
			if(busNumber==1) {//发对应尾车班次
				latestBusTripList.remove(0);
				if(!latestBusTripList.isEmpty()) {
					if(latestBusObuTime.before(latestBusTrip.getNextObuTimeMin())){//落后标准时间
						Date obuTimeNext=latestBusTripList.get(0).getTripBeginTime();
						int diff=DateUtil.getMinuteInterval(latestBusObuTime, latestBusTrip.getNextObuTimeMin());
						int runDuration=DateUtil.getMinuteInterval(latestBusObuTime, obuTimeNext);
						if(latestBusTripList.get(0).getDirection()==latestBusTripReference.getDirection()) {//方向相同，取单程时间
							runDuration=runDuration/2;
						}
						if(diff>runDuration/2) {//超过1/4圈，重新计算班次
							latestBusMatched=false;
						}
					}
				}
			}
			if(latestBusTrip.getDirection()!=direction) {//尾车过来这边
				if(!latestBusLastRound) {
					if(latestBusObuTime.before(latestBusTrip.getNextObuTimeMin())) {
						if(latestBusTrip.isEatAfterTrip()) {//吃饭时段
							latestBusObuTime=latestBusTrip.getNextObuTimeMin();
						}else {
							if(latestBusTrip.getTripEndTime()!=null) {
								if(!latestBusTrip.getTripEndTime().before(latestBusObuTime)) {
									latestBusObuTime=DateUtil.getDateAddMinute(latestBusTrip.getTripEndTime(), 1);
								}
							}
						}
					}
				}
			}
			int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestBusObuTime)/busNumber;
			int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
			if(!latestBusAdjust&&interval>maxInterval*1.2) {//刚开始调位，车数可能突变
				if(tripLast.getLastTripFull()!=null) {
					int intervalLast=DateUtil.getMinuteInterval(tripLast.getLastTripFull().getTripBeginTime(), tripLast.getTripBeginTime());
					interval=(int)Math.ceil(intervalLast*1.2);
					if(interval>maxInterval*1.2) {
						interval=(int)Math.ceil(maxInterval*1.2);
					}
				}else {
					Integer intervalMin=getIntervalMin(direction);
					if(intervalMin!=null) {
						if(intervalMin<maxInterval*1.2) {
							interval=(int)Math.ceil(maxInterval*1.2);
						}else {
							interval=intervalMin;
						}
					}
				}
			}
			if(busNumber==1)
				latestBusAdjust=true;
			Integer intervalMin=getIntervalMin(direction);
			if(!latestBusLastRound&&intervalMin!=null&&intervalMin>interval) {
				for(Trip trip:busQueueTo) {
					if(trip.isElecSupplyAfterTrip()) {
						interval=intervalMin;
						break;
					}
				}
				if(firstEatTrip==null) {
					if(intervalMin>maxInterval) {//最小发班间隔达到最大间隔，按最小发班间隔发
						interval=intervalMin;
					}
				}
			}
			if(firstEatTrip!=null&&firstEatTrip.getNextObuTimeMin().after(tripLast.getTripBeginTime())) {
				busNumber=busQueueTo.indexOf(firstEatTrip)+1;
				int interval4Eat=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), firstEatTrip.getNextObuTimeMin())/busNumber;
				if(interval4Eat>interval)//吃饭间隔大于平均间隔
					interval=interval4Eat;
			}
			Integer intervalTemp=getInterval4LatestBusSecondary(direction);
			if(intervalTemp!=null&&intervalTemp<interval) {
				interval=intervalTemp;
			}
			Date obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
			/*Integer restTime=scheduleParam.getLeaveImmediatelyInterval(1-direction);//到对面到站即走
			if(restTime==null) {//非到站即走
				if(latestBusTripList.size()==1)
					latestBusLastRound=true;
			}else {
				if(latestBusTripList.size()==0)
					latestBusLastRound=true;
			}*/
			if(latestBusTripList.size()==1) {
				LatestBusTrip latestBusTripNext=latestBusTripList.get(0);
				if(latestBusTripNext.getDirection()!=latestBusMain.getStartDirection()) {
					latestBusLastRound=true;
				}
			}		
			if(latestBusTripList.isEmpty()) {
				latestBusLastRound=true;
			}
			return obuTime;
		}
		return null;
	}
	
	private Integer getInterval4LatestBusSecondary(int direction) {
		if(mainDirection!=Direction.NODIRECTION.getValue())
			return null;
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		Trip latestBusSecondaryTrip=null;
		for(Trip tripArrival:busQueueTo) {
			if(tripArrival.getBus()==latestBusMain) {
				return null;
			}else if(tripArrival.getBus()==latestBusSecondary) {
				latestBusSecondaryTrip=tripArrival;
				break;
			}
			
		}
		if(latestBusSecondaryTrip!=null) {
			List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(latestBusMain.getStartDirection());
			int tripRemainLatestBusMain=latestBusTripList.size();
			latestBusTripList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
			if(latestBusTripList.size()-tripRemainLatestBusMain<0||tripRemainLatestBusMain==0)
				return null;
			LatestBusTrip latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain);
			if(scheduleParam.getLeaveImmediatelyInterval(1-direction)!=null) {//对面到站即走
				latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain+1);
			}else {
				if(latestBusTrip.getDirection()!=direction) {
					if(latestBusTripList.size()-tripRemainLatestBusMain-1<0)
						return null;
					latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain-1);
				}
			}
			int busNumberFull=0;
			for(Trip tripArrival:busQueueTo) {
				if(!tripArrival.isQuitMark()) {
					busNumberFull++;
				}
				if(tripArrival.getBus()==latestBusSecondary) {
					break;
				}
			}
			Trip tripFullLast=routeSchedule.getTripFullLast(direction);
			int interval=DateUtil.getMinuteInterval(tripFullLast.getTripBeginTime(), latestBusTrip.getTripBeginTimeLatest())/busNumberFull;
			return interval;
		}
		return null;
	}
	
	private Integer getIntervalMin(int direction) {
		Trip tripLast=routeSchedule.getTripLast(direction);
		List<Trip> busQueue=routeSchedule.getBusQueue(1-direction);
		Integer interval=null;
		int busNum=2;
		for(int i=1;i<busQueue.size();i++) {
			Trip trip=busQueue.get(i);
			if(trip.isQuitMark())
				continue;
			if(trip.getTripEndTime()!=null) {
				Date nextObuTimeMin=trip.getNextObuTimeMin();
				if(nextObuTimeMin.after(scheduleParam.getLatestTime(direction)))
					continue;
				if(!trip.isEatAfterTrip()&&!trip.isElecSupplyAfterTrip()) {
					nextObuTimeMin=DateUtil.getDateAddMinute(trip.getTripEndTime(), 3);
				}
				if(tripLast.getTripBeginTime().before(nextObuTimeMin)) {
					int intervalTemp=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), nextObuTimeMin)/busNum;
					if(interval==null||intervalTemp>interval) {
						interval=intervalTemp;
					}
				}
			}
			if(busNum==6)
				break;
			busNum++;
		}
		if(interval==null)
			System.out.println("aaa");
		return interval;
	}
	
	private Date getObuTime4LastRound(int direction) {
		List<Trip> busQueueFrom=routeSchedule.getBusQueue(direction);
		List<Trip> busQueueTo=routeSchedule.getBusQueue(1-direction);
		List<Trip> busQueue=new ArrayList<Trip>();
		busQueue.addAll(busQueueTo);
		busQueue.addAll(busQueueFrom);
		Trip tripLast=routeSchedule.getTripLast(direction);
		Trip latestBusTrip=null;
		int busNumber=0;
		Date latestTime=scheduleParam.getLatestTime(direction);
		if(tripLast!=null&&tripLast.getTripBeginTime().equals(latestTime)) {
			return null;
		}
		if(scheduleParam.getEndDirection()==Direction.NODIRECTION.getValue()) {
			Bus latestBus=null;
			if(mainDirection==Direction.NODIRECTION.getValue()) {//末班车时间相同
				if(direction==Direction.UP.getValue()) {
					latestBus=latestBusMain;
				}else {
					latestBus=latestBusSecondary;
				}
			}else {
				if(direction==mainDirection) {
					latestBus=latestBusMain;
				}else {
					latestBus=latestBusSecondary;
				}
			}
			if(latestBus!=null) {
				for(Trip trip:busQueue) {
					if(trip.isQuitMark()) {
						continue;
					}
					if(trip.getDirection()!=direction) {
						if(trip.getTripEndTime()!=null&&
								!trip.getTripEndTime().before(latestTime)) {//过来的车过了末班车时间
							break;
						}
					}
					busNumber++;
					latestBusTrip=trip;
					if(trip.getBus()==latestBus) {
						break;
					}
				}
			}
		}else{
			//单边停场
			Date latestTimeReverse=scheduleParam.getLatestTime(1-direction);
			Date arrivalTime=scheduleParam.getArrivalTime(latestTimeReverse, 1-direction);
			if(!arrivalTime.before(latestTime)) {//对面末班车过来赶不及做这边末班车
				Trip tripLastReverse=routeSchedule.getTripLast(1-direction);
				int busNumberLastRound=0;
				for(Trip trip:busQueueFrom) {
					busNumberLastRound++;
					if(trip.getBus()==latestBusMain) {
						break;
					}
				}
				Date obuTimeLast=tripLastReverse.getTripBeginTime();
				for(Trip trip:busQueueFrom) {
					if(busNumberLastRound==0)
						break;
					int interval=DateUtil.getMinuteInterval(obuTimeLast, latestTimeReverse)/busNumberLastRound;
					Date obuTime=DateUtil.getDateAddMinute(obuTimeLast, interval);
					if(trip.getTripEndTime()!=null&&!obuTime.after(trip.getTripEndTime())) {
						obuTime=DateUtil.getDateAddMinute(trip.getTripEndTime(), 1);
					}
					trip.setNextObuTimeMin(obuTime);//设置对面
					obuTimeLast=obuTime;
					busNumberLastRound--;
				}
			}
			for(Trip trip:busQueue) {
				if(trip.isQuitMark()) {
					continue;
				}
				if(trip.getDirection()!=direction) {//到站方向
					if(trip.getTripEndTime()==null||trip.getTripEndTime().before(latestTime)) {
						latestBusTrip=trip;
						busNumber++;
					}else {
						break;
					}
				}else {
					 if(arrivalTime.before(latestTime)) {//同一台车可以做两边末班车
						 latestBusTrip=trip;
						 busNumber++;
					 }else {
						 Date arrivalTemp=scheduleParam.getArrivalTime(trip.getNextObuTimeMin(), 1-direction);
						 if(arrivalTemp!=null&&arrivalTemp.before(latestTime)) {
							 latestBusTrip=trip;
							 busNumber++;
						 }
					 }
				}
				if(trip.getBus()==latestBusMain) {
					 break;
				 }
			}
		}
		Date obuTime=null;
		if(latestBusTrip!=null) {
			if(latestBusTrip.getDirection()!=direction) {
				latestBusTrip.setNextObuTimeMin(latestTime);
			}
			int interval=DateUtil.getMinuteInterval(tripLast.getTripBeginTime(), latestTime)/busNumber;
			if(busNumber==1&&latestBusMain!=null) {
				if(mainDirection==Direction.NODIRECTION.getValue()) {
					List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(1-latestBusMain.getStartDirection());
					if(!latestBusTripList.isEmpty())
						latestBusTripList.remove(0);
				}else if(mainDirection!=Direction.NODIRECTION.getValue()) {
					List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(1-mainDirection);
					if(!latestBusTripList.isEmpty())
						latestBusTripList.remove(0);
				}
			}
			if(busNumber>1) {
				Integer intervalMin=getIntervalMin(direction);
				if(intervalMin!=null&&intervalMin>interval) {
					interval=intervalMin;
					int maxInterval=scheduleParam.getMaxInterval(tripLast.getTripBeginTime(), direction);
					if(interval>maxInterval) {//最小发班间隔达到最大间隔，按最小发班间隔发
						interval=maxInterval;
					}
				}
			}
			obuTime=DateUtil.getDateAddMinute(tripLast.getTripBeginTime(), interval);
		}
		return obuTime;
	}
	
	@Override
	public Date getObuTime(Trip tripArrival,int direction) {
		Date obuTime=null;
		if(!isLatestBusLastRound()) {
			obuTime=getObuTimeB4LastRound(direction);
		}else {
			obuTime=getObuTime4LastRound(direction);
		}
		if(obuTime!=null&&tripArrival.getBus()==latestBusSecondary)
			obuTime=getObuTime4LatestBusSecondary(direction, tripArrival.getTripEndTime(), obuTime);
		return obuTime;
	}
	
	private Date getObuTime4LatestBusSecondary(int direction,Date tripEndTime, Date obuTime) {
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(1-latestBusSecondary.getStartDirection());
		int tripRemainLatestBusMain=latestBusTripList.size();
		latestBusTripList=latestBusTripMap.get(latestBusSecondary.getStartDirection());
		if(latestBusTripList.size()-tripRemainLatestBusMain<0||tripRemainLatestBusMain==0)
			return obuTime;
		LatestBusTrip latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain);
		if(scheduleParam.getLeaveImmediatelyInterval(1-direction)!=null) {
			if(latestBusTripList.size()-tripRemainLatestBusMain+1<latestBusTripList.size())
				latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain+1);
		}else {
			if(latestBusTrip.getDirection()!=direction) {
				if(latestBusTripList.size()-tripRemainLatestBusMain-1<0)
					return obuTime;
				latestBusTrip=latestBusTripList.get(latestBusTripList.size()-tripRemainLatestBusMain-1);
			}
		}
		if(obuTime.after(latestBusTrip.getTripBeginTimeLatest())) {
			if(tripEndTime==null||tripEndTime.before(latestBusTrip.getTripBeginTimeLatest())) {
				obuTime=latestBusTrip.getTripBeginTimeLatest();
			}else {
				obuTime=DateUtil.getDateAddMinute(tripEndTime, 1);
			}
		}
		return obuTime;
	}
	
	@Override
	protected boolean isLatestBusLastRound() {
		return latestBusLastRound;
	}

	@Override
	protected void busReverseAdjust4Short(ScheduleHalfHour scheduleHalfHour) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Integer getDispatchInterval(int direction) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean isAfterBusReverseBegin(Date runTimeEnd) {
		int direction=Direction.UP.getValue();
		if(mainDirection!=Direction.NODIRECTION.getValue())
			direction=mainDirection;
		List<LatestBusTrip> latestBusTripList=latestBusTripMap.get(1-direction);
		Date busReverseBeginTime=DateUtil.getDateHM("1300");
		if(latestBusTripList.size()>=10) {//大于5个来回,预留一个来回可能赶不上
			LatestBusTrip latestBusTripBegin=latestBusTripList.get(latestBusTripList.size()-8);
			Date busReverseBeginTimeTemp=latestBusTripBegin.getStandardTripBeginTime();
			if(busReverseBeginTimeTemp.before(busReverseBeginTime))
				busReverseBeginTime=busReverseBeginTimeTemp;
		}else {
			busReverseBeginTime=DateUtil.getDateHM("0900");
		}
		if(!busReverseBeginTime.after(runTimeEnd)) {
			return true;
		}
		return false;
	}

	@Override
	protected void removeLatestBusTrip(Bus bus) {
		// TODO Auto-generated method stub
		
	}

}
