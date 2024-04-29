package com.gci.schedule.driverless.dynamic.test;

//import com.gci.schedule.driverless.gz.service.SimulationService;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TripRunTime {

	public static Map<String, Integer> run_time_map=new HashMap<String, Integer>();
	
	private static final Calendar calendar0=Calendar.getInstance();
	private static final Calendar calendar7=Calendar.getInstance();//早高峰开始时间
	private static final Calendar calendar730=Calendar.getInstance();//车辆最晚出车时间
	private static final Calendar calendar9=Calendar.getInstance();//早高峰结束时间
	private static final Calendar calendar1030=Calendar.getInstance();//单班车发班截止时间，发这个时间后的安排中停
	private static final Calendar calendar11=Calendar.getInstance();//吃饭开始时间
	private static final Calendar calendar1430=Calendar.getInstance();//复行开始
	private static final Calendar calendar17=Calendar.getInstance();//晚高峰开始时间
	private static final Calendar calendar19=Calendar.getInstance();//晚高峰结束时间、吃饭开始时间
	
//	public static SimulationService simulationService;
	
	static {
		calendar0.set(Calendar.HOUR_OF_DAY, 0);
		calendar0.set(Calendar.MINUTE, 0);
		calendar0.set(Calendar.SECOND, 0);
		calendar0.set(Calendar.MILLISECOND,0);
		
		calendar7.set(Calendar.HOUR_OF_DAY, 7);
		calendar7.set(Calendar.MINUTE, 0);
		calendar7.set(Calendar.SECOND, 0);
		calendar7.set(Calendar.MILLISECOND,0);
		
		calendar730.set(Calendar.HOUR_OF_DAY, 7);
		calendar730.set(Calendar.MINUTE, 30);
		calendar730.set(Calendar.SECOND, 0);
		calendar730.set(Calendar.MILLISECOND,0);
		
		calendar9.set(Calendar.HOUR_OF_DAY, 9);
		calendar9.set(Calendar.MINUTE, 0);
		calendar9.set(Calendar.SECOND, 0);
		calendar9.set(Calendar.MILLISECOND,0);
		
		calendar1030.set(Calendar.HOUR_OF_DAY, 10);
		calendar1030.set(Calendar.MINUTE, 30);
		calendar1030.set(Calendar.SECOND, 0);
		calendar1030.set(Calendar.MILLISECOND,0);
	
		calendar11.set(Calendar.HOUR_OF_DAY, 11);
		calendar11.set(Calendar.MINUTE, 0);
		calendar11.set(Calendar.SECOND, 0);
		calendar11.set(Calendar.MILLISECOND,0);
		
		calendar1430.set(Calendar.HOUR_OF_DAY, 14);
		calendar1430.set(Calendar.MINUTE, 30);
		calendar1430.set(Calendar.SECOND, 0);
		calendar1430.set(Calendar.MILLISECOND,0);
		
		calendar17.set(Calendar.HOUR_OF_DAY, 17);
		calendar17.set(Calendar.MINUTE, 0);
		calendar17.set(Calendar.SECOND, 0);
		calendar17.set(Calendar.MILLISECOND,0);
		
		calendar19.set(Calendar.HOUR_OF_DAY, 19);
		calendar19.set(Calendar.MINUTE, 0);
		calendar19.set(Calendar.SECOND, 0);
		calendar19.set(Calendar.MILLISECOND,0);
		
		/*run_time_map.put("0_24",43);
		run_time_map.put("0_25",45);
		run_time_map.put("0_26",52);
		run_time_map.put("0_27",62);
		run_time_map.put("0_28",76);
		run_time_map.put("0_29",83);
		run_time_map.put("0_30",88);
		run_time_map.put("0_31",87);
		run_time_map.put("0_32",83);
		run_time_map.put("0_33",78);
		run_time_map.put("0_34",62);
		run_time_map.put("0_35",67);
		run_time_map.put("0_36",64);
		run_time_map.put("0_37",60);
		run_time_map.put("0_38",58);
		run_time_map.put("0_39",56);
		run_time_map.put("0_40",54);
		run_time_map.put("0_41",56);
		run_time_map.put("0_42",55);
		run_time_map.put("0_43",50);
		run_time_map.put("0_44",51);
		run_time_map.put("0_45",51);
		run_time_map.put("0_46",52);
		run_time_map.put("0_47",48);
		run_time_map.put("0_48",46);
		run_time_map.put("0_49",47);
		run_time_map.put("0_50",50);
		run_time_map.put("0_51",52);
		run_time_map.put("0_52",50);
		run_time_map.put("0_53",51);
		run_time_map.put("0_54",55);
		run_time_map.put("0_55",57);
		run_time_map.put("0_56",57);
		run_time_map.put("0_57",56);
		run_time_map.put("0_58",54);
		run_time_map.put("0_59",54);
		run_time_map.put("0_60",57);
		run_time_map.put("0_61",59);
		run_time_map.put("0_62",57);
		run_time_map.put("0_63",55);
		run_time_map.put("0_64",58);
		run_time_map.put("0_65",57);
		run_time_map.put("0_66",61);
		run_time_map.put("0_67",63);
		run_time_map.put("0_68",60);
		run_time_map.put("0_69",57);
		run_time_map.put("0_70",60);
		run_time_map.put("0_71",54);
		run_time_map.put("0_72",56);
		run_time_map.put("0_73",59);
		run_time_map.put("0_74",53);
		run_time_map.put("0_75",53);
		run_time_map.put("0_76",51);
		run_time_map.put("0_77",50);
		run_time_map.put("0_78",51);
		run_time_map.put("0_79",49);
		run_time_map.put("0_80",51);
		run_time_map.put("0_81",50);
		run_time_map.put("0_82",47);
		run_time_map.put("0_83",51);
		run_time_map.put("0_84",52);
		run_time_map.put("0_85",52);
		run_time_map.put("0_86",53);
		run_time_map.put("0_87",55);
		run_time_map.put("0_88",55);
		run_time_map.put("1_24",40);
		run_time_map.put("1_25",42);
		run_time_map.put("1_26",46);
		run_time_map.put("1_27",45);
		run_time_map.put("1_28",55);
		run_time_map.put("1_29",53);
		run_time_map.put("1_30",64);
		run_time_map.put("1_31",64);
		run_time_map.put("1_32",60);
		run_time_map.put("1_33",54);
		run_time_map.put("1_34",57);
		run_time_map.put("1_35",55);
		run_time_map.put("1_36",53);
		run_time_map.put("1_37",53);
		run_time_map.put("1_38",49);
		run_time_map.put("1_39",50);
		run_time_map.put("1_40",50);
		run_time_map.put("1_41",49);
		run_time_map.put("1_42",49);
		run_time_map.put("1_43",50);
		run_time_map.put("1_44",47);
		run_time_map.put("1_45",49);
		run_time_map.put("1_46",49);
		run_time_map.put("1_47",50);
		run_time_map.put("1_48",49);
		run_time_map.put("1_49",49);
		run_time_map.put("1_50",47);
		run_time_map.put("1_51",46);
		run_time_map.put("1_52",47);
		run_time_map.put("1_53",47);
		run_time_map.put("1_54",49);
		run_time_map.put("1_55",49);
		run_time_map.put("1_56",52);
		run_time_map.put("1_57",52);
		run_time_map.put("1_58",50);
		run_time_map.put("1_59",54);
		run_time_map.put("1_60",55);
		run_time_map.put("1_61",58);
		run_time_map.put("1_62",58);
		run_time_map.put("1_63",56);
		run_time_map.put("1_64",52);
		run_time_map.put("1_65",58);
		run_time_map.put("1_66",61);
		run_time_map.put("1_67",57);
		run_time_map.put("1_68",64);
		run_time_map.put("1_69",64);
		run_time_map.put("1_70",64);
		run_time_map.put("1_71",65);
		run_time_map.put("1_72",66);
		run_time_map.put("1_73",64);
		run_time_map.put("1_74",60);
		run_time_map.put("1_75",59);
		run_time_map.put("1_76",60);
		run_time_map.put("1_77",56);
		run_time_map.put("1_78",51);
		run_time_map.put("1_79",53);
		run_time_map.put("1_80",51);
		run_time_map.put("1_81",52);
		run_time_map.put("1_82",54);
		run_time_map.put("1_83",51);
		run_time_map.put("1_84",50);
		run_time_map.put("1_85",51);
		run_time_map.put("1_86",53);
		run_time_map.put("1_87",53);
		run_time_map.put("1_88",55);*/
	}
	
	public static boolean isAfterLateStartTime(Date tripBeginTime) {
		if(tripBeginTime.after(calendar730.getTime())) {
			return true;
		}
		return false;
	}
	
	public static boolean isBeforeLateStartTime(Date tripBeginTime) {
		if(tripBeginTime.before(calendar730.getTime())) {
			return true;
		}
		return false;
	}
	
	public static boolean isAfterRecoveryTime(Date tripBeginTime) {
		if(tripBeginTime.after(calendar1430.getTime())) {
			return true;
		}
		return false;
	}
	
	public static boolean isAfterStopTime(Date tripBeginTime) {
		if(!tripBeginTime.before(calendar1030.getTime())) {
			return true;
		}
		return false;
	}
	
	public static Date getArrivalTime(Date tripBeginTime,int direction) {
		
		/*IntersiteTimeParams intersiteTimeParams = new IntersiteTimeParams();
		intersiteTimeParams.setRouteId("710");
		intersiteTimeParams.setDirection(String.valueOf(direction));
		if(direction==0) {
			intersiteTimeParams.setFromRouteStaId(1047182);
			intersiteTimeParams.setToRouteStaId(89171);
		}else {
			intersiteTimeParams.setFromRouteStaId(89172);
			intersiteTimeParams.setToRouteStaId(1047183);
		}
		intersiteTimeParams.setAdTime(tripBeginTime);
		int run_duration_double=simulationService.getIntersiteTime(intersiteTimeParams).intValue();
		int run_duration=run_duration_double/60;*/
		
		
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(tripBeginTime);
		long minute=(calendar.getTimeInMillis()-calendar0.getTimeInMillis())/1000/60;
		long run_time = (calendar.getTimeInMillis()-calendar0.getTimeInMillis())/1000/60/15;
		Integer run_duration=run_time_map.get(direction+"_"+run_time);
		if(run_duration==null)
			return null;
		if(minute%15!=0) {//不是刚好踏到点
			Integer run_duration_next=run_time_map.get(direction+"_"+(run_time+1));
			if(run_duration_next!=null) {
				Long run_duration_long=(((minute%15)/15)*(run_duration_next-run_duration)+run_duration);
				run_duration=run_duration_long.intValue();
			}
		}
		calendar.add(Calendar.MINUTE, run_duration);
		return calendar.getTime();
	}
	
	/*public static Date getMinObuTime(Bus bus,Date arrivalTime) {
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(arrivalTime);
		if(!arrivalTime.before(calendar11.getTime())&&!bus.isSingleClass()&&!bus.isLunchEaten()) {//到达时间在11点后的，肯定要安排吃饭
			calendar.add(Calendar.MINUTE, 15);
			bus.setLunchEaten(true);
			return calendar.getTime();
		}else if(!arrivalTime.before(calendar19.getTime())&&!bus.isSupperEaten()) {
			calendar.add(Calendar.MINUTE, 15);
			bus.setSupperEaten(true);
			return calendar.getTime();
		}
		if(!arrivalTime.before(calendar7.getTime())&&!arrivalTime.after(calendar9.getTime())) {//早高峰
			calendar.add(Calendar.MINUTE, 5);
		}else if(!arrivalTime.before(calendar17.getTime())&&!arrivalTime.after(calendar19.getTime())) {//晚高峰
			calendar.add(Calendar.MINUTE, 5);
		}else {//平峰
			calendar.add(Calendar.MINUTE, 10);
		}
		return calendar.getTime();
	}*/
	
	public static void setEaten(Bus bus,Date startTime) {
		
		if(!bus.isLunchEaten()&&!startTime.before(DateUtil.getCalendarHM("1100").getTime())) {//出车时间在11点后的，设置为已吃饭
			System.out.println(bus.getBusName()+"\t开车时间："+DateFormatUtil.HM.getDateString(startTime)+"设置吃早饭");
			bus.setLunchEaten(true);
		}
		if(!bus.isSupperEaten()&&!startTime.before(DateUtil.getCalendarHM("1900").getTime())) {
			System.out.println(bus.getBusName()+"\t开车时间："+DateFormatUtil.HM.getDateString(startTime)+"设置吃晚饭");
			bus.setSupperEaten(true);
		}
	}
}
