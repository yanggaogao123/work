package com.gci.schedule.driverless.dynamic.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gci.schedule.driverless.dynamic.bean.EmptyBusCutOver;
import com.gci.schedule.driverless.dynamic.bean.ShortLineSchedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleHalfHour {

	private String runTime;//时段，如5:50、6:00、6:30
	
	private String runTimeEnd;//截止时间，如22:30、22:50
	
	private Date runTimeDate;
	
	private Date runTimeEndDate;
	
	private int direction;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Date> obuTimeList;//长线班次
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ShortLineSchedule shortLineSchedule;//总站短线班次信息
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private EmptyBusCutOver emptyBusCutOver;//空车切入需求
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ScheduleHalfHour nextScheduleHalfHour;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ScheduleHalfHour preScheduleHalfHour;
	
	private int fullClassesNumber;//全程班次数
	
	@JsonIgnore
	private boolean reCalculate;//重算标识,空车切入

	public ScheduleHalfHour(String runTime, String runTimeEnd, int direction) {
		super();
		this.runTime = runTime;
		this.direction = direction;
		this.runTimeEnd=runTimeEnd;
	}

	public String getRunTime() {
		return runTime;
	}
	
	public Date getRunTimeDate() {
		if(runTimeDate==null)
			return DateUtil.getDateHM(runTime);
		return runTimeDate;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public List<Date> getObuTimeList() {
		return obuTimeList;
	}

	public void setObuTimeList(List<Date> obuTimeList) {
		this.obuTimeList = obuTimeList;
	}

	public ScheduleHalfHour getNextScheduleHalfHour() {
		return nextScheduleHalfHour;
	}

	public void setNextScheduleHalfHour(ScheduleHalfHour nextScheduleHalfHour) {
		this.nextScheduleHalfHour = nextScheduleHalfHour;
	}
	
	public ScheduleHalfHour getPreScheduleHalfHour() {
		return preScheduleHalfHour;
	}

	public void setPreScheduleHalfHour(ScheduleHalfHour preScheduleHalfHour) {
		this.preScheduleHalfHour = preScheduleHalfHour;
	}

	public String getRunTimeEnd() {
		return runTimeEnd;
	}
	
	public Date getRunTimeEndDate() {
		if(runTimeEndDate==null)
			return DateUtil.getDateHM(runTimeEnd);
		return runTimeEndDate;
	}

	public void setRunTimeDate(Date runTimeDate) {
		this.runTimeDate = runTimeDate;
	}

	public void setRunTimeEndDate(Date runTimeEndDate) {
		this.runTimeEndDate = runTimeEndDate;
	}

	public ShortLineSchedule getShortLineSchedule() {
		return shortLineSchedule;
	}

	public void setShortLineSchedule(ShortLineSchedule shortLineSchedule) {
		this.shortLineSchedule = shortLineSchedule;
	}

	public EmptyBusCutOver getEmptyBusCutOver() {
		return emptyBusCutOver;
	}

	public void setEmptyBusCutOver(EmptyBusCutOver emptyBusCutOver) {
		this.emptyBusCutOver = emptyBusCutOver;
	}

	public boolean isReCalculate() {
		return reCalculate;
	}

	public void setReCalculate(boolean reCalculate) {
		this.reCalculate = reCalculate;
	}
	
	public int getFullClassesNumber() {
		return fullClassesNumber;
	}

	public void setFullClassesNumber(int fullClassesNumber) {
		this.fullClassesNumber = fullClassesNumber;
	}

	@JsonIgnore
	public List<Plan> getPlanList(){
		List<Date> obu_time_queue_long=getObuTimeList();
		if(obu_time_queue_long==null)
			return null;
		List<Plan> planList=new ArrayList<Plan>();
		for(Date obuTime:obu_time_queue_long) {
			Plan plan=new Plan(obuTime, false, direction);
			planList.add(plan);
		}
		int shortLineClasses=0;//短线班次数
		ShortLineSchedule shortLineSchedule=getShortLineSchedule();
		//短线时间处理开始
		if(shortLineSchedule!=null){
			shortLineClasses=shortLineSchedule.getShortLineClasses();
			int index=1;
			Date endTime=DateUtil.getCalendarHM(getRunTimeEnd()).getTime();
			for(int i=0;i<shortLineClasses;i++) {
				Date obuTimeNext=endTime;
				if(index<planList.size())
					obuTimeNext=planList.get(index).getPlanTime();
				Date obuTime = new Date((planList.get(index-1).getPlanTime().getTime()+obuTimeNext.getTime())/2);
				Plan plan=new Plan(obuTime, true, direction, shortLineSchedule.getRouteStaId());
				planList.add(index,plan);
				index=index+2;				
			}
		}
		return planList;
	}

}
