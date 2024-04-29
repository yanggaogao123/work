package com.gci.schedule.driverless.dynamic.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

	public static int getMinuteOfHour(Date date) {
		Calendar calendar=getCalendar(date);
		return calendar.get(Calendar.MINUTE);
	}

	public static int getTimeMinute(Date date) {
		int interval=getMinuteInterval(date, getDate(date, "0000"));
		return interval;
	}

	public static Date getTimeByMinute(int minute) {
		Calendar calendar=getCalendarHour("0");
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public static Calendar getCalendarHour(String hour) {
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar;
	}

	public static Calendar getCalendarHM(String hour,String minute) {
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
		calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar;
	}

	public static Calendar getCalendarHM(String hm) {
		String hour=null;
		String minute=null;
		if(hm.indexOf(":")!=-1) {
			String[] array=hm.split(":");
			hour=array[0];
			minute=array[1];
		}else {
			if(hm.length()==4) {
				hour=hm.substring(0,2);
				minute=hm.substring(2);
			}else if(hm.length()==3) {
				hour=hm.substring(0,1);
				minute=hm.substring(2);
			}else if(hm.length()==2) {
				hour=hm;
				minute="00";
			}else if(hm.length()==1) {
				hour=hm;
				minute="00";
			}
		}
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
		calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar;
	}

	public static Date getDateHM(String hm) {
		Calendar calendar=getCalendarHM(hm);
		return calendar.getTime();
	}

	public static Date getDateHM(Date date) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date=calendar.getTime();
		return date;
	}

	public static Calendar getCalendar(Date date) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static int getDayOfWeek(Date date) {
		Calendar calendar=getCalendar(date);
		int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	public static String getDateString(Date date, SimpleDateFormat sdf) {
		synchronized (DateUtil.class) {
			return sdf.format(date);
		}
	}

	public static Date getDate(String dateStr, String formateStr) {
		synchronized (DateUtil.class) {
			Date date = null;
			date=getDate(dateStr, new SimpleDateFormat(formateStr));
			return date;
		}
	}

	public static int getMinuteInterval(Date date1,Date date2) {
		Long interval=(date1.getTime()-date2.getTime())/60/1000;
		return Math.abs(interval.intValue());
	}

	public static int getSecondInterval(Date date1,Date date2) {
		Long interval=(date1.getTime()-date2.getTime())/1000;
		return Math.abs(interval.intValue());
	}

	public static Date getDateAdd(Date date,int field,int amount) {
		Calendar calendar=getCalendar(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	public static Date getDateAddDay(Date date,int amount) {
		return getDateAdd(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date getDateAddMinute(Date date,int amount) {
		return getDateAdd(date, Calendar.MINUTE, amount);
	}

	public static Date getDate(String dateStr, SimpleDateFormat sdf) {
		synchronized (DateUtil.class) {
			Date date = null;
			if (dateStr != null) {
				try {
					date = sdf.parse(dateStr);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return date;
		}
	}

	public static boolean isInTimeRange(Date runTime,Date beginTime,Date endTime) {
		if(!runTime.before(beginTime)&&runTime.before(endTime)) {
			return true;
		}
		return false;
	}

	public static Date getDate(Date date,Date hm) {
		return DateFormatUtil.DATE_TIME.getDate(DateFormatUtil.DATE.getDateString(date)+ DateFormatUtil.HM.getDateString(hm));
	}

	public static Date getDate(Date date,String hm) {
		return DateFormatUtil.DATE_TIME.getDate(DateFormatUtil.DATE.getDateString(date)+hm);
	}

	public static Date getDate3(Date date,String hm) {
		return DateFormatUtil.DATE_TIME3.getDate(DateFormatUtil.DATE.getDateString(date)+" "+hm);
	}

	public static Date getToday(){
		return DateFormatUtil.DATE.getDate(DateFormatUtil.DATE.getDateString(new Date()));
	}

	public static boolean isWholeHour(Date date) {
		Calendar calendar=getCalendar(date);
		if(calendar.get(Calendar.MINUTE)==0) {
			return true;
		}
		return false;
	}

	public static boolean isWholeOrHalfHour(Date date) {
		Calendar calendar=getCalendar(date);
		if(calendar.get(Calendar.MINUTE)==0||calendar.get(Calendar.MINUTE)==30) {
			return true;
		}
		return false;
	}

	public static int getRunTime(Date date,int unit) {
		int timeMinute=getTimeMinute(date);
		int runTime=timeMinute/unit;
		return runTime;
	}

	/**
	* 通过时间秒毫秒数判断两个时间的间隔
	* @param date1
	* @param date2
	* @return
	*/
	public static int getDayInterval(Date date1,Date date2){
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
		return days;
	}

	/**
	 * 获取日期
	 * @param date
	 * @return
	 */
	public static Date getRunDate(Date date) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date runDate=calendar.getTime();
		return runDate;
	}

	/**
	 * 24小时制
     *
	 * @param date
	 * @return
	 */
	public static Date getRunDateOfDay(Date date) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date runDate=calendar.getTime();
		return runDate;
	}

	public enum DateFormatUtil {
		HM("HHmm"),HM2("HH:mm"),DATE("yyyyMMdd"),DATE_TIME("yyyyMMddHHmm"),SIMPLE_DATE("yyyy-MM-dd"),DATE_TIME2("yyyy-MM-dd HH:mm"),DATE_TIME3("yyyyMMdd HH:mm")
		,DATE_TIME_SECOND("yyyy-MM-dd HH:mm:ss");

		private SimpleDateFormat sdf;

		DateFormatUtil(String str) {
			this.sdf = new SimpleDateFormat(str);
		}

		public Date getDate(String str) {
			return DateUtil.getDate(str, this.sdf);
		}

		public SimpleDateFormat getSdf() {
			return sdf;
		}

		public String getDateString(Date date) {
			if(date==null)
				return null;
			return DateUtil.getDateString(date, sdf);
		}
	}
}
