package com.gci.schedule.driverless.dynamic.test;

import com.gci.schedule.driverless.dynamic.bean.ShortLineSchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DispatchRule implements Cloneable{

	private int hour;//小时
	
	private String runTimeBegin;//时段，如5:50、6:00、6:30
	
	private String runTimeEnd;//截止时间，如22:30、22:50
	
	private int direction;
	
	private Integer firstHalfClassesNumber;//前半小时长线发班数
	
	private Integer lastHalfClassesNumber;//后半小时长线发班数
	
	private Integer firstHalfMinClassesNumber;//前半小时长线最小发班数
	
	private Integer lastHalfMinClassesNumber;//后半小时长线最小发班数
	
	private ScheduleHalfHour firstHalfScheduleHalfHour;
	
	private ScheduleHalfHour lastHalfScheduleHalfHour;
	
	public DispatchRule(int hour, int direction) {
		super();
		this.hour = hour;
		this.direction = direction;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public Integer getFirstHalfClassesNumber() {
		return firstHalfClassesNumber;
	}
	
	public boolean greaterThanMinClassesNumber(String hm) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			if(lastHalfClassesNumber>lastHalfMinClassesNumber) {
				return true;
			}
		}else {
			if(firstHalfClassesNumber>firstHalfMinClassesNumber) {
				return true;
			}
		}
		return false;
	}
	
	public void classesNumberReduce(String hm) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			lastHalfClassesNumber--;
		}else {
			firstHalfClassesNumber--;
		}
	}
	
	public void classesNumberIncrease(String hm) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			lastHalfClassesNumber++;
		}else {
			firstHalfClassesNumber++;
		}
		if(firstHalfClassesNumber!=null&&lastHalfClassesNumber!=null) {
			if(firstHalfClassesNumber+lastHalfClassesNumber==4&&
					firstHalfClassesNumber!=lastHalfClassesNumber) {//出现前三后一,改成前二后二
				if(runTimeBegin.endsWith("00")&&runTimeEnd.endsWith("00")) {//整个小时才前二后二
					firstHalfClassesNumber=2;
					lastHalfClassesNumber=2;
				}
			}
		}
	}
	
	public void resetScheduleHalfHourObuTimeList() {
		if(firstHalfScheduleHalfHour!=null) {
			firstHalfScheduleHalfHour.setObuTimeList(getObuTimeList(firstHalfScheduleHalfHour));
		}
		if(lastHalfScheduleHalfHour!=null) {
			lastHalfScheduleHalfHour.setObuTimeList(getObuTimeList(lastHalfScheduleHalfHour));
		}
	}
	
	public void setClassesNumber(String hm,int classesNumber) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			lastHalfClassesNumber=classesNumber;
		}else {
			if(runTimeBegin.endsWith("00")&&runTimeEnd.endsWith("00")) {
				if(classesNumber==3&&lastHalfClassesNumber==1) {//原来3班20分钟，如果前半小时改成3班，会导致后半小时30分钟一班，所以按一小时4班算
					lastHalfClassesNumber=2;
					firstHalfClassesNumber=2;
					if(firstHalfScheduleHalfHour.getShortLineSchedule()!=null) {
						int shortLineClasses=firstHalfScheduleHalfHour.getShortLineSchedule().getShortLineClasses();
						if(shortLineClasses>firstHalfClassesNumber) {//短线多过长线
							firstHalfClassesNumber=shortLineClasses;
						}
					}
					List<Date> obuTimeList=getObuTimeList(firstHalfScheduleHalfHour);
					firstHalfScheduleHalfHour.setObuTimeList(obuTimeList);
					obuTimeList=getObuTimeList(lastHalfScheduleHalfHour);
					lastHalfScheduleHalfHour.setObuTimeList(obuTimeList);
					return;
				}
			}
			firstHalfClassesNumber=classesNumber;
		}
	}
	
	public void setMinClassesNumber(String hm,int minClassesNumber) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			lastHalfMinClassesNumber=minClassesNumber;
		}else {
			firstHalfMinClassesNumber=minClassesNumber;
		}
	}
	
	public int getMinClassesNumber(String hm) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			return lastHalfMinClassesNumber;
		}else {
			return firstHalfMinClassesNumber;
		}
	}
	
	public Integer getClassesNumber(String hm) {
		String minute=hm.substring(hm.length()-2);
		if(Integer.valueOf(minute)>=30) {
			return lastHalfClassesNumber;
		}else {
			return firstHalfClassesNumber;
		}
	}

	public void setFirstHalfClassesNumber(Integer firstHalfClassesNumber) {
		this.firstHalfClassesNumber = firstHalfClassesNumber;
	}

	public Integer getLastHalfClassesNumber() {
		return lastHalfClassesNumber;
	}

	public void setLastHalfClassesNumber(Integer lastHalfClassesNumber) {
		this.lastHalfClassesNumber = lastHalfClassesNumber;
	}

	public Integer getFirstHalfMinClassesNumber() {
		return firstHalfMinClassesNumber;
	}

	public void setFirstHalfMinClassesNumber(Integer firstHalfMinClassesNumber) {
		this.firstHalfMinClassesNumber = firstHalfMinClassesNumber;
	}

	public Integer getLastHalfMinClassesNumber() {
		return lastHalfMinClassesNumber;
	}

	public void setLastHalfMinClassesNumber(int lastHalfMinClassesNumber) {
		this.lastHalfMinClassesNumber = lastHalfMinClassesNumber;
	}
	
	public ArrayList<Date> getObuTimeList(ScheduleHalfHour scheduleHalfHour){
		/*String beginTime=scheduleHalfHour.getRunTime();
		String endTime=scheduleHalfHour.getRunTimeEnd();*/
		Calendar calendar=DateUtil.getCalendar(scheduleHalfHour.getRunTimeDate());
		int minute=calendar.get(Calendar.MINUTE);
		ArrayList<Date> obuTimeList=new ArrayList<Date>();
		if(runTimeBegin.endsWith("00")&&runTimeEnd.endsWith("00")&&Math.abs(lastHalfClassesNumber-firstHalfClassesNumber)==1) {//完整的一个小时
			if((firstHalfClassesNumber==firstHalfMinClassesNumber&&lastHalfClassesNumber==lastHalfMinClassesNumber)||
				firstHalfClassesNumber+lastHalfClassesNumber==3||firstHalfClassesNumber+lastHalfClassesNumber==5) {
				ArrayList<Date> obuTimeListTemp=new ArrayList<Date>();
				calendar=DateUtil.getCalendar(scheduleHalfHour.getRunTimeDate());
				calendar.set(Calendar.MINUTE, 0);
				Date runTimeEnd=DateUtil.getDateAddMinute(calendar.getTime(), 60);
				int classesNumber=lastHalfClassesNumber+firstHalfClassesNumber;
				for(int i=classesNumber;i>0;i--) {
					obuTimeListTemp.add(calendar.getTime());
					Long interval=(runTimeEnd.getTime()-calendar.getTimeInMillis())/i/60/1000;
					calendar.add(Calendar.MINUTE, interval.intValue());
				}
				Date runTimeBegin=scheduleHalfHour.getRunTimeDate();
				runTimeEnd=scheduleHalfHour.getRunTimeEndDate();
				for(Date obuTime:obuTimeListTemp) {
					if(DateUtil.isInTimeRange(obuTime, runTimeBegin, runTimeEnd)) {
						obuTimeList.add(obuTime);
					}
				}
				ShortLineSchedule shortLineSchedule=scheduleHalfHour.getShortLineSchedule();
				if(shortLineSchedule!=null) {
					if(obuTimeList.size()<shortLineSchedule.getShortLineClasses()) {//生成的长线比短线少
						obuTimeList.clear();
					}
				}
				ScheduleHalfHour scheduleHalfHourRelate=null;
				if(minute==0) {
					scheduleHalfHourRelate=scheduleHalfHour.getNextScheduleHalfHour();
				}else {
					scheduleHalfHourRelate=scheduleHalfHour.getPreScheduleHalfHour();
				}
				if(scheduleHalfHourRelate!=null) {
					shortLineSchedule=scheduleHalfHourRelate.getShortLineSchedule();
					if(shortLineSchedule!=null) {
						if(classesNumber-obuTimeList.size()<shortLineSchedule.getShortLineClasses()) {//生成的长线比短线少
							obuTimeList.clear();
						}
					}
				}
				if(!obuTimeList.isEmpty()) {
					if(minute==0) {
						firstHalfClassesNumber=obuTimeList.size();
						lastHalfClassesNumber=classesNumber-firstHalfClassesNumber;
						if(lastHalfClassesNumber==0)
							System.out.println("aaa");
					}else {
						lastHalfClassesNumber=obuTimeList.size();
						firstHalfClassesNumber=classesNumber-lastHalfClassesNumber;
					}
				}
			}
		}
		if(obuTimeList.isEmpty()){
			Integer classesNumber=firstHalfClassesNumber;
			Integer minClassesNumber=firstHalfMinClassesNumber;
			if(minute>=30) {//后半小时
				classesNumber=lastHalfClassesNumber;
				minClassesNumber=lastHalfMinClassesNumber;
			}
			if(classesNumber==null)
				System.out.println("aaaa");
			if(scheduleHalfHour.getNextScheduleHalfHour()==null&&
				!DateUtil.isWholeOrHalfHour(scheduleHalfHour.getRunTimeEndDate())&&
				classesNumber>minClassesNumber) {//最后一个时段，末班车不是整点或半点，班次减一，末班算一班
				classesNumber--;
			}
			calendar=DateUtil.getCalendar(scheduleHalfHour.getRunTimeDate());
			Calendar calendarEnd=DateUtil.getCalendar(scheduleHalfHour.getRunTimeEndDate());
			while(classesNumber>0) {
				obuTimeList.add(calendar.getTime());
				Double interval=Math.ceil((calendarEnd.getTimeInMillis()-calendar.getTimeInMillis())/60/1000.0/classesNumber);
				calendar.add(Calendar.MINUTE, interval.intValue());
				classesNumber--;
			}
		}
		if(scheduleHalfHour.getNextScheduleHalfHour()==null) {
			obuTimeList.add(scheduleHalfHour.getRunTimeEndDate());
		}
		return obuTimeList;
	}
	
	public static ArrayList<Date> getObuTimeList(String beginTimeStr,String endTimeStr,Date lastObuTime,int classesNumber){
		ArrayList<Date> obuTimeList=new ArrayList<Date>();
		Calendar calendar=DateUtil.getCalendarHM(beginTimeStr);
		Date endTime=DateUtil.getCalendarHM(endTimeStr).getTime();
		if(lastObuTime==null) {
			lastObuTime=DateUtil.getCalendarHM(beginTimeStr).getTime();
			obuTimeList.add(lastObuTime);
			classesNumber--;
		}
		for(int i=0;i<classesNumber;i++) {
			Long interval=(endTime.getTime()-lastObuTime.getTime())/(classesNumber+1-i)/60/1000;
			calendar.setTime(lastObuTime);
			calendar.add(Calendar.MINUTE, interval.intValue());
			lastObuTime=calendar.getTime();
			obuTimeList.add(lastObuTime);
		}
		return obuTimeList;
	}
	
	public ScheduleHalfHour getFirstHalfScheduleHalfHour() {
		return firstHalfScheduleHalfHour;
	}

	public void setFirstHalfScheduleHalfHour(ScheduleHalfHour firstHalfScheduleHalfHour) {
		this.firstHalfScheduleHalfHour = firstHalfScheduleHalfHour;
	}

	public ScheduleHalfHour getLastHalfScheduleHalfHour() {
		return lastHalfScheduleHalfHour;
	}

	public void setLastHalfScheduleHalfHour(ScheduleHalfHour lastHalfScheduleHalfHour) {
		this.lastHalfScheduleHalfHour = lastHalfScheduleHalfHour;
	}

	public static boolean isPeak(String runTime){
		if(runTime.startsWith("07")||runTime.startsWith("07")||runTime.startsWith("17")||runTime.startsWith("18")) {
			return true;
		}
		return false;
	}
	
	public boolean isPeak() {
		if(hour==7||hour==8||hour==17||hour==18) {
			return true;
		}
		return false;
	}
	
	public String getRunTimeBegin() {
		return runTimeBegin;
	}

	public void setRunTimeBegin(String runTimeBegin) {
		this.runTimeBegin = runTimeBegin;
	}

	public String getRunTimeEnd() {
		return runTimeEnd;
	}

	public void setRunTimeEnd(String runTimeEnd) {
		this.runTimeEnd = runTimeEnd;
	}

	@Override
	public Object clone() {
         DispatchRule dispatchRule = null;
         try {
        	 dispatchRule = (DispatchRule) super.clone();
         } catch (CloneNotSupportedException e) {
        	 e.printStackTrace();
         }
         return dispatchRule;
     }
}

