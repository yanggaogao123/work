package com.gci.schedule.driverless.dynamic.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamShift;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsDrBus;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;

import java.util.Date;

/**
* <p>Title: Bus</p>
* <p>车辆实体: </p>
* <p>Company: GCI</p> 
* @author lindy
* @date 2019年5月25日 上午9:01:48
*/
public class Bus implements Cloneable{
	
	private int startDirection;//出车方向
	
	private int startOrderNumber;//车序
	
	@JsonIgnore
	private boolean isSingleClass;//单班车标识
	
	@JsonIgnore
	private boolean lunchEaten;//午餐标识
	
	@JsonIgnore
	private boolean supperEaten;//晚餐标识
	
	private Integer runTime;//工作时间
	
	private Integer shiftCode;//车辆班别编码
	
	//班别类型 0：早半班 1：晚半班 2：中班 3.双班中停
	private ScheduleParamShift shiftType;//当前班别
	
	private Date startTimeMin;//最早出车时间
	
	private boolean hasMiddleStop;//已经中停过
	
	private int endDirection;//收车方向
	
	private Long startRouteStaId;//中途出车站点
	
	private SingleBusConf singleBusConf;

	private ScheduleParamsDrBus scheduleParamsDrBus;
	
	public boolean isHasMiddleStop() {
		return hasMiddleStop;
	}

	public void setHasMiddleStop(boolean hasMiddleStop) {
		this.hasMiddleStop = hasMiddleStop;
	}

	public Bus(int startDirection, int startOrderNumber) {
		super();
		this.startDirection = startDirection;
		this.startOrderNumber = startOrderNumber;
		this.endDirection = startDirection;
		System.out.println(getBusName()+"+++++++++++++++++++++++++++++++++++++++++++++");
	}

	public Bus(int startDirection, int startOrderNumber, Date startTime) {
		super();
		this.startDirection = startDirection;
		this.startOrderNumber = startOrderNumber;
		this.endDirection = startDirection;
		TripRunTime.setEaten(this, startTime);
		System.out.println(getBusName()+"+++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	public Date getStartTimeMin() {
		return startTimeMin;
	}

	public void setStartTimeMin(Date startTimeMin) {
		this.startTimeMin = startTimeMin;
	}

	public ScheduleParamShift getShiftType() {
		return shiftType;
	}

	public void setShiftType(ScheduleParamShift shiftType) {
		this.shiftType = shiftType;
		if(shiftType!=null)
			shiftCode=shiftType.getShiftType();
	}

	public void setShiftCode(Integer shiftCode) {
		this.shiftCode = shiftCode;
	}

	public Integer getShiftCode() {
		if(shiftCode!=null)
			return shiftCode;
		if(isSingleClass)
			return ShiftType.SINGLE_SHIFT_MIDDLE_STOP.getValue();
		return ShiftType.DOUBLE_SHIFT_NOT_MIDDLE_STOP.getValue();
	}

	public int getStartDirection() {
		return startDirection;
	}

	public void setStartDirection(int startDirection) {
		this.startDirection = startDirection;
	}

	public int getStartOrderNumber() {
		return startOrderNumber;
	}

	public void setStartOrderNumber(int startOrderNumber) {
		this.startOrderNumber = startOrderNumber;
	}
	
	@JsonIgnore
	public boolean isSingleClass() {
		return isSingleClass;
	}

	public void setSingleClass(boolean isSingleClass) {
		this.isSingleClass = isSingleClass;
	}

	public boolean isLunchEaten() {
		return lunchEaten;
	}

	public void setLunchEaten(boolean lunchEaten) {
		this.lunchEaten = lunchEaten;
	}

	public boolean isSupperEaten() {
		return supperEaten;
	}

	public void setSupperEaten(boolean supperEaten) {
		this.supperEaten = supperEaten;
	}
	
	public Integer getRunTime() {
		return runTime;
	}

	public void setRunTime(Integer runTime) {
		this.runTime = runTime;
	}

	public int getEndDirection() {
		return endDirection;
	}

	public void setEndDirection(int endDirection) {
		this.endDirection = endDirection;
	}
	
	public Long getStartRouteStaId() {
		return startRouteStaId;
	}

	public void setStartRouteStaId(Long startRouteStaId) {
		this.startRouteStaId = startRouteStaId;
	}

	public ScheduleParamsDrBus getScheduleParamsDrBus() {
		return scheduleParamsDrBus;
	}

	public void setScheduleParamsDrBus(ScheduleParamsDrBus scheduleParamsDrBus) {
		this.scheduleParamsDrBus = scheduleParamsDrBus;
	}

	public void setSingleBusConf(int runTripNumberMorning, int runTripNumberEvening) {
		singleBusConf=new SingleBusConf(runTripNumberMorning, runTripNumberEvening);
	}
	
	public Integer getSingleBusTripNumberMorning() {
		if(singleBusConf!=null)
			return singleBusConf.getRunTripNumberMorning();
		return null;
	}
	
	public Integer getSingleBusTripNumberEvening() {
		if(singleBusConf!=null)
			return singleBusConf.getRunTripNumberEvening();
		return null;
	}
	
	public Integer getSingleBusTripNumber() {
		if(singleBusConf!=null)
			return singleBusConf.getRunTripNumberMorning()+singleBusConf.getRunTripNumberEvening();
		return null;
	}

	@JsonIgnore
	public String getBusName() {
		String busName="上行";
		if(startDirection==1) {
			busName="下行";
		}
		busName+=startOrderNumber+"号车";
		return busName;
	}
	
	public boolean isShiftType(ShiftType... shiftTypes) {
		for(ShiftType shiftType:shiftTypes) {
			if(getShiftCode()==shiftType.getValue()) {
				return true;
			}
		}
		return false;
	}


	@Override
	public Object clone() {
         Bus bus = null;
         try {
        	 bus = (Bus) super.clone();
         } catch (CloneNotSupportedException e) {
        	 e.printStackTrace();
         }
  
         return bus;
     }
	
	class SingleBusConf {
		
		private int runTripNumberMorning;//早上班次数
		
		private int runTripNumberEvening;//下午班次数

		public SingleBusConf(int runTripNumberMorning, int runTripNumberEvening) {
			super();
			this.runTripNumberMorning = runTripNumberMorning;
			this.runTripNumberEvening = runTripNumberEvening;
		}

		public int getRunTripNumberMorning() {
			return runTripNumberMorning;
		}

		public void setRunTripNumberMorning(int runTripNumberMorning) {
			this.runTripNumberMorning = runTripNumberMorning;
		}

		public int getRunTripNumberEvening() {
			return runTripNumberEvening;
		}

		public void setRunTripNumberEvening(int runTripNumberEvening) {
			this.runTripNumberEvening = runTripNumberEvening;
		}

	}
}

