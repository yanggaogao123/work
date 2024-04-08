package com.gci.schedule.driverless.dynamic.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.dynamic.ClassesNumber;
import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.enums.Direction;
import com.gci.schedule.driverless.dynamic.enums.ServiceType;
import com.gci.schedule.driverless.dynamic.enums.ShiftType;
import com.gci.schedule.driverless.dynamic.enums.StationMark;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.mapper.*;
import com.gci.schedule.driverless.dynamic.service.*;
import com.gci.schedule.driverless.dynamic.test.*;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;
import com.gci.schedule.driverless.dynamic.util.StringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Author: allan
 * @Date: 2019/4/19 16:52
 */
@Service("schedulePlanDynamicService")
public class SchedulePlanServiceImpl implements SchedulePlanService {
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(SchedulePlanServiceImpl.class);

    @Resource
    private RouteStationService routeStationDynamicService;
    @Resource
    private SimulationService simulationDynamicService;
    @Autowired
    private SchedulePlanDynamicMapper schedulePlanDynamicMapper;
    @Autowired
    private ScheduleParamsClassesDynamicMapper scheduleParamsClassesDynamicMapper;
    @Autowired
    private ScheduleParamsEatDynamicMapper scheduleParamsEatDynamicMapper;
    @Autowired
    private ScheduleParamsAnchorDynamicMapper scheduleParamsAnchorDynamicMapper;
    @Autowired
    private ScheduleParamsSingleService scheduleParamsSingleDynamicService;
    @Autowired
    private ScheduleParamsRouteDynamicMapper scheduleParamsRouteDynamicMapper;
    @Autowired
    private RouteWasteTimeDynamicMapper routeWasteTimeDynamicMapper;
    @Autowired
    private RouteService routeDynamicService;
    @Autowired
    private RepStationPassengerService repStationPassengerDynamicService;
    @Autowired
    private ScheduleParamsAnchorService scheduleParamsAnchorDynamicService;
    @Autowired
    private RouteStaTurnDynamicMapper routeStaTurnDynamicMapper;
    @Autowired
    private ScheduleTemplateDetailDynamicMapper scheduleTemplateDetailDynamicMapper;
    @Resource
	private BusNumberConfigDynamicMapper busNumberConfigDynamicMapper;
    @Resource
    private StationPassengerTaskService stationPassengerTaskDynamicService;
	@Autowired
	private ScheduleParamsOccupancyDynamicMapper scheduleParamsOccupancyDynamicMapper;
	@Autowired
	private ScheduleRouteConfigDynamicMapper scheduleRouteConfigDynamicMapper;
	@Autowired
	private StationDynamicMapper StationDynamicMapper;
	@Autowired
	private ScheduleParamsDriverlessMapper scheduleParamsDriverlessMapper;
	@Autowired
	private ScheduleParamsDrInoutMapper scheduleParamsDrInoutMapper;
	@Autowired
	private ScheduleParamsDrRouteSubMapper scheduleParamsDrRouteSubMapper;

	long beginTimeMillis=System.currentTimeMillis();

	@Autowired
	private ScheduleParamsEnduranceService scheduleParamsEnduranceDynamicService;

	@Override
	public void generateNew(ScheduleParamPreset scheduleParamPreset) {
		beginTimeMillis=System.currentTimeMillis();
		if(scheduleParamPreset.getBusNumberPreset()!=null||
				scheduleParamPreset.getBusNumberUp()!=null
				||scheduleParamPreset.getBusNumberDown()!=null||
				scheduleParamPreset.getStartWorkRunDate()!=null) {//预设生成,看最优生成没有
			BusNumberConfig busNumberConfig= busNumberConfigDynamicMapper.selectByRouteIdAndPlanDate(scheduleParamPreset.getRouteId().intValue(), scheduleParamPreset.getRunDate());
			if(busNumberConfig==null) {
				generateOptimal(scheduleParamPreset);//先生成最优计划
			}
		}
		System.out.println("准备生成:"+(System.currentTimeMillis()-beginTimeMillis));
		SchedulePlanResult schedulePlanResult = generate(scheduleParamPreset);
		saveSchedulePlan(schedulePlanResult);
	}

	private void generateOptimal(ScheduleParamPreset scheduleParamPreset) {
		ScheduleParamPreset scheduleParamOptimal=new ScheduleParamPreset();
		scheduleParamOptimal.setRouteId(scheduleParamPreset.getRouteId());
		scheduleParamOptimal.setRunDate(scheduleParamPreset.getRunDate());
		scheduleParamOptimal.setPassengeReferenceRunDate(scheduleParamPreset.getPassengeReferenceRunDate());
		scheduleParamOptimal.setRunningTimeReferenceRunDate(scheduleParamPreset.getRunningTimeReferenceRunDate());
		scheduleParamOptimal.setTemplateId(scheduleParamPreset.getTemplateId());
		generateNew(scheduleParamOptimal);
	}

    @Override
    public SchedulePlanResult generate(ScheduleParamPreset scheduleParamPreset) {
		long beginTimeMillis=System.currentTimeMillis();
		Long routeId=scheduleParamPreset.getRouteId();
		Date runDate=scheduleParamPreset.getRunDate();
		Route route= routeDynamicService.getRouteByRouteId(routeId);
		if(route==null) {
			throw new MyException("-1", "获取线路资料失败");
		}
		scheduleParamPreset.setRoute(route);
		//根据线路Id和星期几获取模板Id zyj
        //周日：1，周一:2...
        Calendar calendar=DateUtil.getCalendar(runDate);
        Integer templateId = scheduleParamPreset.getTemplateId();
        if(templateId==null)
			templateId= scheduleTemplateDetailDynamicMapper.getTemplateIdByRouteIdAndDay(routeId.intValue(),calendar.get(Calendar.DAY_OF_WEEK));
        if(templateId==null)
			throw new MyException("-1", "没有对应排班参数模板");
        scheduleParamPreset.setTemplateId(templateId);
		if(scheduleParamPreset.getBusNumberPreset()!=null||scheduleParamPreset.getBusNumberUp()!=null
				||scheduleParamPreset.getBusNumberDown()!=null) {
			scheduleParamPreset.setCreateType(1);//预设生成
			BusNumberConfig busNumberConfig= busNumberConfigDynamicMapper.selectByRouteIdAndPlanDate(routeId.intValue(), runDate);
			if(busNumberConfig==null) {
				throw new MyException("-1", "请先生成最优计划");
			}
		}
        ScheduleParam scheduleParam=new ScheduleParam();
        scheduleParam.setRouteId(routeId);
        SchedulePlan4Mj schedulePlan4Mj=new SchedulePlan4Mj();
        scheduleParam.setSimulationService(simulationDynamicService);
        scheduleParam.setRunDate(runDate);
        //获取末班车时间
        String result=  routeDynamicService.getRouteUpDownInfo(routeId.intValue());
		JSONObject json = JSONObject.parseObject(result);
		JSONArray retData=json.getJSONArray("retData");
		for(int i=0;i<retData.size();i++) {
			JSONObject jo=retData.getJSONObject(i);
			if("0".equals(jo.getString("direction"))) {
				scheduleParam.setUpFirstTime(jo.getString("firstTime"));
				scheduleParam.setUpLatestTime(jo.getString("latestTime"));
			}else {
				scheduleParam.setDownFirstTime(jo.getString("firstTime"));
				scheduleParam.setDownLatestTime(jo.getString("latestTime"));
			}
		}
		List<ScheduleParamsAnchor> scheduleParamsAnchorList = scheduleParamsAnchorDynamicMapper.getByTemplateId(templateId);//停站时间配置
		scheduleParam.setScheduleParamsAnchorList(scheduleParamsAnchorList);
        List<ScheduleParamsEat> scheduleParamsEatList = scheduleParamsEatDynamicMapper.getByTemplateId(templateId);//吃饭配置
        scheduleParam.setScheduleParamsEatList(scheduleParamsEatList);
        ScheduleParamsSingle scheduleParamsSingle = scheduleParamsSingleDynamicService.getByTemplateId(templateId);//单班设置
        scheduleParam.setScheduleParamsSingle(scheduleParamsSingle);
        ScheduleParamsRoute scheduleParamsRoute = scheduleParamsRouteDynamicMapper.getByTemplateId(templateId);//线路设置
        scheduleParam.setScheduleParamsRoute(scheduleParamsRoute);
        List<ScheduleParamsClasses> scheduleParamsClassesList = scheduleParamsClassesDynamicMapper.getByTemplateId(templateId);
        scheduleParam.setScheduleParamsClassesList(scheduleParamsClassesList);
        Integer enduranceMileage= scheduleParamsEnduranceDynamicService.getEnduranceMileageByTemplateId(templateId);//续航里程
        scheduleParam.setEnduranceMileage(enduranceMileage);
        List<ScheduleParamsEndurance> elecSupplySettingList= scheduleParamsEnduranceDynamicService.getByTemplateId(templateId);//补电参数设置
        scheduleParam.setElecSupplySettingList(elecSupplySettingList);
        Integer vehicleContent = scheduleParamsRoute.getVehicleContent();//车内容量
        vehicleContent = (vehicleContent == null || vehicleContent == 0) ? 105 : vehicleContent;
        scheduleParam.setVehicleContent(vehicleContent);
		List<RouteSta> routeStaList = routeStationDynamicService.getRouteStaListByRouteId(routeId);
        scheduleParam.setRouteStaList(routeStaList);
        if(isLoopLine(scheduleParam)) {
			scheduleParam.setLoopLine(true);
        }else {
			Integer startDirection=null;
			if(!scheduleParam.isAppearDirection(Direction.UP.getValue())) {
				startDirection=Direction.DOWN.getValue();
			}else if(!scheduleParam.isAppearDirection(Direction.DOWN.getValue())) {
				startDirection=Direction.UP.getValue();
			}
			if(startDirection!=null) {//单边出车,看是不是凸头
				String beginTime= scheduleRouteConfigDynamicMapper.getFirstTriplogBeginTime(routeId, startDirection);
				scheduleParam.setTripBeginTimeB4FirstTime(beginTime);
			}
		}
        if(!scheduleParam.isTwoLoopLine()) {
			if(DateUtil.getDateHM(scheduleParam.getUpLatestTime()).
					before(DateUtil.getDateHM(scheduleParam.getUpFirstTime()))&&
					routeId!=3110) {
				throw new MyException("-1", "夜班车线路请选择定点班车");
			}
			if(scheduleParam.getDownLatestTime()!=null&&scheduleParam.getDownFirstTime()!=null&&
					DateUtil.getDateHM(scheduleParam.getDownLatestTime()).
							before(DateUtil.getDateHM(scheduleParam.getDownFirstTime()))&&
					routeId!=3110) {
				throw new MyException("-1", "夜班车线路请选择定点班车");
			}
		}
        //周转时间
        List<RouteWasteTime> routeWasteTimeList = null;
        if(scheduleParamPreset.getReferenceRunDate()!=null||scheduleParamPreset.getRunningTimeReferenceRunDate()!=null) {
			Date runningTimeReferenceRunDate=scheduleParamPreset.getRunningTimeReferenceRunDate();
			if(runningTimeReferenceRunDate==null) {
				runningTimeReferenceRunDate=scheduleParamPreset.getReferenceRunDate();
				scheduleParamPreset.setRunningTimeReferenceRunDate(runningTimeReferenceRunDate);
			}
			Calendar calendarMax=DateUtil.getCalendarHM("0000");
			if(!runningTimeReferenceRunDate.before(calendarMax.getTime())) {
				scheduleParamPreset.setRunningTimeReferenceRunDate(null);
				throw new MyException("-1", "周转时间参考日期有误"+DateFormatUtil.SIMPLE_DATE.getDateString(runningTimeReferenceRunDate));
			}
			routeWasteTimeList= routeWasteTimeDynamicMapper.queryByRouteHistory(routeId,runningTimeReferenceRunDate);
        }else{
			routeWasteTimeList= routeWasteTimeDynamicMapper.queryByRoute(routeId,runDate);
        }
        System.out.println("routeWasteTimeList.size()"+routeWasteTimeList.size());
        if(routeWasteTimeList.isEmpty()) {
			System.out.println("周转时间空，自动更新");
			if(scheduleParamPreset.getRunningTimeReferenceRunDate()!=null) {//按参考日期取不到
				routeWasteTimeList= routeWasteTimeDynamicMapper.queryByRoute(routeId,runDate);//取平均
			}
			if(routeWasteTimeList.isEmpty()) {
				throw new MyException("-1", "获取不到周转时间");
//				scheduleDataUpdateService.routeWasteTimeDataUpdate(null, routeId.intValue());//更新周转时间
//				routeWasteTimeList=routeWasteTimeMapper.queryByRoute(routeId,runDate);//取平均
			}
			if(routeWasteTimeList.isEmpty()) {
				if(!scheduleParam.isFixedWasteTime()) {
					Date wasteTimeRunDateRef= routeWasteTimeDynamicMapper.queryHistoryRunDate(routeId);
					if(wasteTimeRunDateRef!=null) {
						routeWasteTimeList= routeWasteTimeDynamicMapper.queryByRouteHistory(routeId,wasteTimeRunDateRef);
					}
				}
			}
        }
        scheduleParam.setRouteWasteTimeList(routeWasteTimeList);
        schedulePlan4Mj.setRouteWasteTimeList(routeWasteTimeList);
        for(RouteWasteTime routeWasteTime:routeWasteTimeList) {
			System.out.println(routeWasteTime.getRunTimeNum()+"_"+routeWasteTime.getDirection()+"\t"+routeWasteTime.getWasteTime());
        }
        RouteStationPassengerInfo passengerInfoUp=null;
        RouteStationPassengerInfo passengerInfoDown=null;
//		if(scheduleParamPreset.isCompetitiveRoute()){
//			try {
//				Map<Integer, RouteStationPassengerInfo> routeStationPassengerInfoMap = scheduleCompetePlanService.getStationPassengerList(scheduleParam,scheduleParamPreset);
//				if (routeStationPassengerInfoMap != null && routeStationPassengerInfoMap.size() > 0) {
//					passengerInfoUp = routeStationPassengerInfoMap.get(Direction.UP.getValue());
//					passengerInfoDown = routeStationPassengerInfoMap.get(Direction.DOWN.getValue());
//				}
//				if(passengerInfoUp==null&&passengerInfoDown==null) {//竞争客流取不到，取线路默认客流
//					//上行客流
//					passengerInfoUp = repStationPassengerService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.UP.getStringValue(), routeId);
//					//下行客流
//					passengerInfoDown = repStationPassengerService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.DOWN.getStringValue(), routeId);
//				}
//			} catch (MyException e) {
//				throw e;
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new MyException("-1", "系统异常");
//			}
////			return null;
//		} else
		if(scheduleParamPreset.getReferenceRunDate()!=null||scheduleParamPreset.getPassengeReferenceRunDate()!=null) {
			Date passengeReferenceRunDate=scheduleParamPreset.getPassengeReferenceRunDate();
			if(passengeReferenceRunDate==null)
				passengeReferenceRunDate=scheduleParamPreset.getReferenceRunDate();
			List<StationPassenger> stationPassengerList= stationPassengerTaskDynamicService.getStationPassengerList(DateFormatUtil.SIMPLE_DATE.getDateString(passengeReferenceRunDate), String.valueOf(routeId));
			if(stationPassengerList!=null) {
				List<RouteStationPassenger> routeStationPassengerListUp=new ArrayList<RouteStationPassenger>();
				List<RouteStationPassenger> routeStationPassengerListDown=new ArrayList<RouteStationPassenger>();
				for(StationPassenger stationPassenger:stationPassengerList) {
					RouteStationPassenger routeStationPassenger=new RouteStationPassenger();
					routeStationPassenger.setCurrentNumber(stationPassenger.getCurpeople().shortValue());
					routeStationPassenger.setDirection(stationPassenger.getDirection());
					routeStationPassenger.setDownNumber(stationPassenger.getDowncount().shortValue());
					routeStationPassenger.setRouteId(routeId);
					if(stationPassenger.getPhour()==null||stationPassenger.getPminute()==null)
						throw new MyException("-1", "获取历史客流失败");
					routeStationPassenger.setRunTimeNum((short)(stationPassenger.getPhour()*2+((stationPassenger.getPminute().indexOf("00")!=-1)?0:1)));
					routeStationPassenger.setUpNumber(stationPassenger.getUpcount().shortValue());
					for(RouteSta routeSta:routeStaList) {
						if(routeSta.getStationMark()==StationMark.UP_LAST.getValue()||routeSta.getStationMark()==StationMark.DOWN_LAST.getValue())
							continue;
						if(routeSta.getStationId().equals(stationPassenger.getStationId())) {
							routeStationPassenger.setOrderNumber(routeSta.getOrderNumber().shortValue());
							routeStationPassenger.setRouteStaId(routeSta.getRouteStationId());
							routeStationPassenger.setRouteStationName(routeSta.getRouteStationName());
							break;
						}
					}
					if(stationPassenger.getDirection().equals(Direction.UP.getStringValue())) {
						routeStationPassengerListUp.add(routeStationPassenger);
					}else if(stationPassenger.getDirection().equals(Direction.DOWN.getStringValue())) {
						routeStationPassengerListDown.add(routeStationPassenger);
					}
				}
				//上行客流
                passengerInfoUp = repStationPassengerDynamicService.dealWithRouteStationPassengerInfo(routeStationPassengerListUp, calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.UP.getStringValue(), routeId);
				//下行客流
                passengerInfoDown= repStationPassengerDynamicService.dealWithRouteStationPassengerInfo(routeStationPassengerListDown, calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.DOWN.getStringValue(), routeId);
			}else {//按指定日期取不到，取默认
				//上行客流
                passengerInfoUp = repStationPassengerDynamicService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.UP.getStringValue(), routeId);
				//下行客流
                passengerInfoDown = repStationPassengerDynamicService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.DOWN.getStringValue(), routeId);
			}
        }else {
			//上行客流
            passengerInfoUp = repStationPassengerDynamicService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.UP.getStringValue(), routeId);
			//下行客流
            passengerInfoDown = repStationPassengerDynamicService.getRouteStationPassangerInfo(calendar.get(Calendar.DAY_OF_WEEK)+"", Direction.DOWN.getStringValue(), routeId);
        }
        Map<Integer, RouteStationPassengerInfo> passengerInfoMap=new HashMap<Integer, RouteStationPassengerInfo>();
        passengerInfoMap.put(0, passengerInfoUp);
        passengerInfoMap.put(1, passengerInfoDown);
        scheduleParam.initLatePeakPassengerDirection(passengerInfoMap);
        List<RouteStaTurn> routeStaTurnList= routeStaTurnDynamicMapper.getRouteStaTurnList(routeId,templateId);//掉头短线首末站
        scheduleParam.setRouteStaTurnList(routeStaTurnList);
        //满载率
        List<ScheduleParamsAnchor> anchorParamList = scheduleParamsAnchorDynamicService.getByTemplateId(templateId);
        //最低发班要求
        //List<ScheduleParamsClasses> classesParamlist=scheduleParamsClassesService.getByTemplateId(templateId);
        boolean calculateByHour=true;//最低发班要求为一小时一班
        for(ScheduleParamsClasses scheduleParamsClasses:scheduleParam.getScheduleParamsClassesList()) {
			if(scheduleParamsClasses.getClassesNumMin()>2||
					(scheduleParamsClasses.getClassesNumMin()==0&&
							scheduleParamsClasses.getMaxDispatchInterval()<30)) {
				calculateByHour=false;
				break;
			}
        }
        if(scheduleParam.isTestLineFull())
			calculateByHour=false;
        if(scheduleParam.isLoopLineDouble()||scheduleParam.isTwoLoopLine()) {
			calculateByHour=false;
        }
        scheduleParam.setCalculateByHour(calculateByHour);
        if(scheduleParam.isTestLineFull()||!scheduleParam.isCalculateByHour()) {
			for(ScheduleParamsAnchor anchorParam:anchorParamList) {//满载率设置
				if(scheduleParam.getFirstTime(Integer.valueOf(anchorParam.getDirection()))==null) {//环线
					continue;
				}
				if(anchorParam.getBeginTime().equals("1900"))
					System.out.println("aaa");
				Calendar beginTime=DateUtil.getCalendarHM(anchorParam.getBeginTime());
				Calendar endTime=DateUtil.getCalendarHM(anchorParam.getEndTime());
				if(endTime.before(beginTime)) {
					endTime.add(Calendar.DATE, 1);
				}
				Date firstTime=scheduleParam.getFirstTime(Integer.valueOf(anchorParam.getDirection()));
				Date latestTime=scheduleParam.getLatestTime(Integer.valueOf(anchorParam.getDirection()));
				if(beginTime.getTime().before(firstTime)) {//设置的时间比头车早
					if(endTime.getTime().after(firstTime)) {
						beginTime=DateUtil.getCalendar(firstTime);//结束时段在头车后，设置开始时间为头车时间
					}else {
						continue;
					}
				}
				if(endTime.getTime().after(latestTime)) {//设置的时间比尾车晚
					if(beginTime.getTime().before(latestTime)) {
						endTime=DateUtil.getCalendar(latestTime);//结束时段在车前，设置结束时间为尾车时间
					}else {
						continue;
					}
				}
				if(!beginTime.getTime().equals(firstTime)) {//非首班时段
					int minute=beginTime.get(Calendar.MINUTE);
					if(minute!=0&&minute!=30) {//非整点或半点
						if(minute<30) {//往后推到30分
							beginTime.set(Calendar.MINUTE, 30);
						}else {
							beginTime.set(Calendar.MINUTE, 0);
							beginTime.add(Calendar.HOUR_OF_DAY, 1);//到后面整点
						}
					}
				}
				List<ScheduleParamsOccupancy> scheduleParamsOccupancyList= scheduleParamsOccupancyDynamicMapper.getByTemplateId(templateId);
				while(beginTime.before(endTime)) {
					if(beginTime.getTime().equals(DateUtil.getDateHM("2330")))
						System.out.println("aaa");
					String hmBeginTime=DateFormatUtil.HM.getDateString(beginTime.getTime());
					int minLongClassesNum=getMinLongClassesNum(beginTime, anchorParam.getDirection(),scheduleParamsClassesList, scheduleParam);
					int minute=beginTime.get(Calendar.MINUTE);
					if(minute!=0&&minute!=30) {//非整点或半点
						if(minute<30) {
							beginTime.set(Calendar.MINUTE, 0);
						}else {
							beginTime.set(Calendar.MINUTE, 30);
						}
					}
					//按半小时取客流
					RouteStationPassenger highSectionPassenger=getHighSectionPassenger(beginTime, Integer.valueOf(anchorParam.getDirection()), passengerInfoMap,schedulePlan4Mj,scheduleParam);
					if(!firstTime.before(beginTime.getTime())&&minLongClassesNum==0){
						minLongClassesNum=1;
					}
					scheduleParam.setMinLongClassesNumber(Integer.valueOf(anchorParam.getDirection()), hmBeginTime, minLongClassesNum);
					//int maxPassengerNum=highSectionPassenger.getCurrentNumber();
					//ShortLineClassesNumber shortLineClassesNumber=new ShortLineProcess().calculate(beginTime, Integer.valueOf(anchorParam.getDirection()), passengerInfoMap, vehicleContent, anchorParam.getBusOccupancy().intValue(), routeStaTurnList,minLongClassesNum);
					//shortLineClassesNumberList.add(shortLineClassesNumber);
					//int classesNum=(int)Math.ceil(maxPassengerNum/(vehicleContent*anchorParam.getBusOccupancy()*0.01));
					int classesNum=getClassesNumber(beginTime, Integer.valueOf(anchorParam.getDirection()), passengerInfoMap.get(Integer.valueOf(anchorParam.getDirection())), scheduleParamsOccupancyList, vehicleContent, anchorParam.getBusOccupancy());
					int longClassesNum=0;
					ShortLineSchedule shortLineSchedule=null;
					if(classesNum<=minLongClassesNum) {//按最低发班要求
						classesNum=minLongClassesNum;
						longClassesNum=minLongClassesNum;
					}else {//计算短线班次
						if(!routeStaTurnList.isEmpty()) {
							//shortLineSchedule=getShortClassesInfo(beginTime, Integer.valueOf(anchorParam.getDirection()), passengerInfoMap, classesNum, minLongClassesNum,vehicleContent,routeStaTurnList);
							Map<Long, Integer> classesNumberRouteStaMap=getClassesNumber4RouteSta(beginTime, Integer.valueOf(anchorParam.getDirection()), passengerInfoMap.get(Integer.valueOf(anchorParam.getDirection())), scheduleParamsOccupancyList, vehicleContent, anchorParam.getBusOccupancy());
							shortLineSchedule=getShortLineSchedule(beginTime.getTime(), Integer.valueOf(anchorParam.getDirection()), getRouteStaPassengerList(beginTime, passengerInfoMap.get(Integer.valueOf(anchorParam.getDirection()))), scheduleParam, classesNumberRouteStaMap, classesNum, minLongClassesNum);
							if(shortLineSchedule!=null)
								longClassesNum=classesNum-shortLineSchedule.getShortLineClasses();
							else {
								longClassesNum=classesNum;
							}
						}else {
							longClassesNum=classesNum;
						}
					}
					ClassesNumber classesNumber=new ClassesNumber(highSectionPassenger.getRunTimeNum(),Integer.valueOf(anchorParam.getDirection()));
					int shortClassesNumber=0;
					if(shortLineSchedule!=null)
						shortClassesNumber=shortLineSchedule.getShortLineClasses();
					EmptyBusCutOver emptyBusCutOver=getEmptyBusCutOver(beginTime, passengerInfoMap.get(Integer.valueOf(anchorParam.getDirection())), minLongClassesNum, classesNum, vehicleContent, routeStaList, routeStaTurnList);
					if(routeId==2830) {
						if(emptyBusCutOver!=null&&shortLineSchedule!=null) {
							int cutOverClassesNumber=emptyBusCutOver.getClassesNumberCutOver();
							if(cutOverClassesNumber>=shortClassesNumber) {
								emptyBusCutOver.setClassesNumberCutOver(shortClassesNumber);
								shortLineSchedule.setShortLineClasses(0);
							}else {
								shortLineSchedule.setShortLineClasses(shortClassesNumber-cutOverClassesNumber);
							}
							longClassesNum=classesNum-emptyBusCutOver.getClassesNumberCutOver()-shortLineSchedule.getShortLineClasses();
							if(longClassesNum<minLongClassesNum) {
								int diff=minLongClassesNum-longClassesNum;//增加的班次数
								longClassesNum=minLongClassesNum;
								for(int i=diff;i>0;i--) {
									if(shortLineSchedule.getShortLineClasses()>0) {//先减少短线
										shortLineSchedule.setShortLineClasses(shortLineSchedule.getShortLineClasses()-1);
									}else if(emptyBusCutOver.getClassesNumberCutOver()>0){//减完短线不够再减区间
										emptyBusCutOver.setClassesNumberCutOver(emptyBusCutOver.getClassesNumberCutOver()-1);
									}
								}
							}
							if(longClassesNum<shortLineSchedule.getShortLineClasses()) {
								int classNumberTemp=longClassesNum+shortLineSchedule.getShortLineClasses();//总站开出班次
								longClassesNum=(int)Math.ceil(classNumberTemp/2.0);
								shortLineSchedule.setShortLineClasses(classNumberTemp-longClassesNum);
							}
						}else {
							if(shortLineSchedule==null) {//后面没有掉头点
								emptyBusCutOver=null;//不做区间
							}
							if(classesNum>2*longClassesNum) {
								longClassesNum=(int)Math.ceil(classesNum/2.0);
								shortLineSchedule.setShortLineClasses(classesNum-longClassesNum);
							}
						}
					}else {
						if(classesNum>2*longClassesNum) {
							longClassesNum=(int)Math.ceil(classesNum/2.0);
							shortLineSchedule.setShortLineClasses(classesNum-longClassesNum);
						}
					}

					classesNumber.setFullClassesNumber(longClassesNum);
					classesNumber.setShortClassesNumber(shortClassesNumber);
					schedulePlan4Mj.addClassesNumber(classesNumber);
					beginTime.add(Calendar.MINUTE, 30);
					String hmEndTime=DateFormatUtil.HM.getDateString(beginTime.getTime());
					if(beginTime.getTime().after(latestTime)) {
						hmEndTime=DateFormatUtil.HM.getDateString(latestTime);
					}
					scheduleParam.addDispatchRule(hmBeginTime, hmEndTime, Integer.valueOf(anchorParam.getDirection()), longClassesNum, minLongClassesNum, shortLineSchedule, emptyBusCutOver);
				}
            }
            //printShortLine(shortLineClassesNumberList);
            //补充尾车客流
            if(scheduleParam.getLatestTime(Direction.UP.getValue())!=null)
				getHighSectionPassenger(DateUtil.getCalendar(scheduleParam.getLatestTime(Direction.UP.getValue())),Direction.UP.getValue(), passengerInfoMap,schedulePlan4Mj,scheduleParam);
            if(scheduleParam.getLatestTime(Direction.DOWN.getValue())!=null)
				getHighSectionPassenger(DateUtil.getCalendar(scheduleParam.getLatestTime(Direction.DOWN.getValue())),Direction.DOWN.getValue(), passengerInfoMap,schedulePlan4Mj,scheduleParam);
            scheduleParam.scheduleHalfHourSort();
        }
        scheduleParam.setNextScheduleHalfHour();
        for(ScheduleHalfHour scheduleHalfHour:scheduleParam.getScheduleHalfHourList()) {
			if(scheduleHalfHour.getEmptyBusCutOver()!=null&&scheduleHalfHour.getShortLineSchedule()!=null) {
				System.out.println(scheduleHalfHour.getDirection()+"\t"+scheduleHalfHour.getRunTime()+"\t"+scheduleHalfHour.getEmptyBusCutOver().getFirstRouteStaName()+"\t"+scheduleHalfHour.getShortLineSchedule().getRouteStationName()+"\t"+scheduleHalfHour.getEmptyBusCutOver().getClassesNumberCutOver());
			}
        }
        if(scheduleParam.isLoopLine()) {
			if(scheduleParamPreset.getBusNumberUp()!=null&&scheduleParamPreset.getBusNumberUp()==0) {
				if(scheduleParamPreset.getBusNumberDown()!=null&&scheduleParamPreset.getBusNumberDown()!=0) {//预设车数写到下行了
					scheduleParamPreset.setBusNumberUp(scheduleParamPreset.getBusNumberDown());
					scheduleParamPreset.setSingleBusNumberUp(scheduleParamPreset.getSingleBusNumberDown());
				}else {
					throw new MyException("-1", "环线请设置上行配车数");
				}
			}
			if(!scheduleParam.isLoopLineDouble()&&!scheduleParam.isTwoLoopLine()) {
				if(scheduleParamPreset.getBusNumberDown()!=null&&scheduleParamPreset.getBusNumberDown()>0) {
					scheduleParamPreset.setBusNumberDown(0);
					scheduleParamPreset.setSingleBusNumberDown(0);//下行重置为0
					//throw new MyException("-1", "环线请不要设置下行配车数");
				}
			}
        }
        if("0".equals(scheduleParamsRoute.getUpDirection())&&"0".equals(scheduleParamsRoute.getDownDirection())) {
			//throw new MyException("-1", "请在运营参数设置选择出车总站");
			scheduleParamsRoute.setUpDirection("1");
			if(!scheduleParam.isLoopLine())
				scheduleParamsRoute.setDownDirection("1");
        }
        if(scheduleParamPreset.getBusNumberUp()!=null&&scheduleParamPreset.getBusNumberDown()!=null
				&&scheduleParamPreset.getSingleBusNumberUp()!=null&&scheduleParamPreset.getSingleBusNumberDown()!=null) {
			if(scheduleParamPreset.getSingleBusNumberUp()>scheduleParamPreset.getBusNumberUp()) {
				scheduleParamPreset.setBusNumberUp(scheduleParamPreset.getSingleBusNumberUp()+scheduleParamPreset.getBusNumberUp());
				//throw new MyException("-1", "上行单班配车数不能大于总配车数");
			}
			if(scheduleParamPreset.getSingleBusNumberDown()>scheduleParamPreset.getBusNumberDown()) {
				scheduleParamPreset.setBusNumberDown(scheduleParamPreset.getSingleBusNumberDown()+scheduleParamPreset.getBusNumberDown());
				//throw new MyException("-1", "下行单班配车数不能大于总配车数");
			}
			if(scheduleParamPreset.getBusNumberUp()+scheduleParamPreset.getBusNumberDown()==
					scheduleParamPreset.getSingleBusNumberUp()+scheduleParamPreset.getSingleBusNumberDown()) {
				throw new MyException("-1", "系统暂不支持全单班配车排班");
			}
        }
        if(scheduleParamPreset.getBusNumberUp()!=null) {
			if(scheduleParamPreset.getBusNumberUp()>50) {
				throw new MyException("-1", "请检查预设配车数是否正确"+scheduleParamPreset.getBusNumberUp());
			}
			if(scheduleParamPreset.getSingleBusNumberUp()!=null) {
				if(scheduleParamPreset.getSingleBusNumberUp()!=0&&scheduleParam.getMiddleStopBeginOrderNumber()+scheduleParamPreset.getSingleBusNumberUp()
						>scheduleParamPreset.getBusNumberUp()+1) {
					//throw new MyException("-1", "请检查上行单班车数和单班车最早车位设置是否正确");
					//重新设置单班车最早车位
					int earliestBusNumber=scheduleParamPreset.getBusNumberUp()-scheduleParamPreset.getSingleBusNumberUp()+1;
					scheduleParam.setMiddleStopBeginOrderNumber(earliestBusNumber);
				}
			}
        }
        if(scheduleParamPreset.getBusNumberDown()!=null) {
			if(scheduleParamPreset.getBusNumberDown()>50) {
				throw new MyException("-1", "请检查预设配车数是否正确"+scheduleParamPreset.getBusNumberDown());
			}
			if(scheduleParamPreset.getSingleBusNumberDown()!=null) {
				if(scheduleParamPreset.getSingleBusNumberDown()!=0&&scheduleParam.getMiddleStopBeginOrderNumber()+scheduleParamPreset.getSingleBusNumberDown()
						>scheduleParamPreset.getBusNumberDown()+1) {
					//throw new MyException("-1", "请检查下行单班车数和单班车最早车位设置是否正确");
					//重新设置单班车最早车位
					int earliestBusNumber=scheduleParamPreset.getBusNumberDown()-scheduleParamPreset.getSingleBusNumberDown()+1;
					scheduleParam.setMiddleStopBeginOrderNumber(earliestBusNumber);
				}
			}
        }
        if(scheduleParamPreset.getShiftList()!=null) {
			for(ScheduleShiftPreset shift:scheduleParamPreset.getShiftList()) {
				if(shift.getBusNumber()!=null&&shift.getBusNumberUp()==null&&shift.getBusNumberDown()==null) {//按总车数来分配上下行车数
					if(scheduleParam.isLoopLine()) {//环线
						shift.setBusNumberUp(shift.getBusNumber());
						shift.setBusNumberDown(0);
					}else {
						ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.UP.getValue(), scheduleParam.getFirstTime(Direction.UP.getValue()));
						if(scheduleParamShift==null) {
							ShiftType shiftType=ShiftType.getShiftType(shift.getShiftType());
							throw new MyException("-1", "请设置"+(shiftType==null?shift.getShiftType():shiftType.getDesc()));
						}
						RouteStationPassenger highSectionPassengerUp=scheduleParam.getHighSectionPassenger(Direction.UP.getValue(), scheduleParamShift.getStartTime());
						RouteStationPassenger highSectionPassengerDown=scheduleParam.getHighSectionPassenger(Direction.DOWN.getValue(), scheduleParamShift.getStartTime());
						int busNumberUp=(int)Math.ceil(highSectionPassengerUp.getCurrentNumber()*shift.getBusNumber()*1.0/(highSectionPassengerUp.getCurrentNumber()+highSectionPassengerDown.getCurrentNumber()));
						shift.setBusNumberUp(busNumberUp);
						shift.setBusNumberDown(shift.getBusNumber()-busNumberUp);
						scheduleParamPreset.setBusNumberPreset(scheduleParamPreset.getBusNumberPreset()-shift.getBusNumber());
					}
				}else if((shift.getBusNumberUp()!=null&&shift.getBusNumberUp()!=0)||
						(shift.getBusNumberDown()!=null&&shift.getBusNumberDown()!=0)) {
					ScheduleParamShift scheduleParamShift=scheduleParam.getScheduleParamShift(shift.getShiftType(), Direction.UP.getValue(), scheduleParam.getFirstTime(Direction.UP.getValue()));
					if(scheduleParamShift==null) {
						ShiftType shiftType=ShiftType.getShiftType(shift.getShiftType());
						throw new MyException("-1", "请设置"+(shiftType==null?shift.getShiftType():shiftType.getDesc()));
					}
				}
            }
        }
        scheduleParam.setScheduleParamPreset(scheduleParamPreset);
        Integer busNumberPreset=scheduleParamPreset.getBusNumberPreset();//预设配车数
        if(scheduleParamPreset.getBusNumberUp()!=null&&scheduleParamPreset.getBusNumberDown()!=null) {
			int busNumberNormalUp=scheduleParamPreset.getBusNumberUp();
			int busNumberNormalDown=scheduleParamPreset.getBusNumberDown();
			if(scheduleParamPreset.getShiftList()!=null) {
				for(ScheduleShiftPreset scheduleShiftPreset:scheduleParamPreset.getShiftList()) {
					if(scheduleShiftPreset.getBusNumberUp()!=0) {
						busNumberNormalUp=busNumberNormalUp-scheduleShiftPreset.getBusNumberUp();
					}
					if(scheduleShiftPreset.getBusNumberDown()!=0) {
						busNumberNormalDown=busNumberNormalDown-scheduleShiftPreset.getBusNumberDown();
					}
				}
			}
			if(scheduleParamPreset.getSingleBusNumberUp()==null)
				scheduleParamPreset.setSingleBusNumberUp(0);
			if(scheduleParamPreset.getSingleBusNumberDown()==null)
				scheduleParamPreset.setSingleBusNumberDown(0);
			if(!scheduleParam.isDoubleFullLineGen()&&
					busNumberNormalUp!=0&&!scheduleParam.isAppearDirection(Direction.UP.getValue())) {
				//throw new MyException("-1", "上行总站设置不能出车");
				if(!scheduleParam.isLoopLine()) {
					if(busNumberNormalDown==0) {//下行不出车
						if(scheduleParam.getFirstTime(Direction.UP.getValue()).after
								(scheduleParam.getFirstTime(Direction.DOWN.getValue()))){//下行先开，重置车数到下行
							busNumberNormalDown=busNumberNormalUp;
							scheduleParamPreset.setSingleBusNumberDown(scheduleParamPreset.getSingleBusNumberUp());
							scheduleParamPreset.setSingleBusNumberUp(0);
							busNumberNormalUp=0;
						}
					}else {
						scheduleParam.setAppearDirection(Direction.UP.getValue(), true);
						scheduleParam.setIsBackInsert(0);
					}
				}else {
					scheduleParam.setAppearDirection(Direction.UP.getValue(), true);
					scheduleParam.setIsBackInsert(0);
				}
			}
			if(!scheduleParam.isDoubleFullLineGen()&&
					busNumberNormalDown!=0&&!scheduleParam.isAppearDirection(Direction.DOWN.getValue())) {
				//throw new MyException("-1", "下行总站设置不能出车");
				if(!scheduleParam.isLoopLine()) {
					if(busNumberNormalUp==0) {//上行不出车
						if(scheduleParam.getFirstTime(Direction.DOWN.getValue()).after
								(scheduleParam.getFirstTime(Direction.UP.getValue()))){//上行先开发，重置车数到上行
							busNumberNormalUp=busNumberNormalDown;
							scheduleParamPreset.setSingleBusNumberUp(scheduleParamPreset.getSingleBusNumberDown());
							scheduleParamPreset.setSingleBusNumberDown(0);
							busNumberNormalDown=0;
						}
					}else {
						scheduleParam.setAppearDirection(Direction.DOWN.getValue(), true);
						scheduleParam.setIsBackInsert(0);
					}
				}else {
					scheduleParam.setAppearDirection(Direction.DOWN.getValue(), true);
					scheduleParam.setIsBackInsert(0);
				}
			}
			if(busNumberNormalUp-scheduleParamPreset.getSingleBusNumberUp()<0) {
				throw new MyException("-1", "请输入正确的上行配车参数");
			}
			scheduleParamPreset.setBusNumberUp(busNumberNormalUp);
			if(busNumberNormalDown-scheduleParamPreset.getSingleBusNumberDown()<0) {
				throw new MyException("-1", "请输入正确的下行配车参数");
			}
			scheduleParamPreset.setBusNumberDown(busNumberNormalDown);
			busNumberPreset=scheduleParamPreset.getBusNumberUp()+scheduleParamPreset.getBusNumberDown();
			scheduleParamPreset.setBusNumberPreset(busNumberPreset);
        }
        scheduleParam.setPassengerInfoMap(passengerInfoMap);
		//计划生成
        if(scheduleParamPreset.getStartWorkRunDate()!=null) {//预设配车设置-使用日期
			List<SchedulePlan> schedulePlanList= schedulePlanDynamicMapper.getSchedulePlanList(routeId, scheduleParamPreset.getStartWorkRunDate());
			if(schedulePlanList.isEmpty())
				throw new MyException("-1", DateFormatUtil.DATE.getDateString(scheduleParamPreset.getStartWorkRunDate())+"没有排班记录");
			scheduleParam.setSchedulePlanReference(schedulePlanList);
        }

		if (scheduleParamPreset.getRouteIdDriverless() != null && scheduleParamPreset.getBusNumberDriverless() != null
				&& scheduleParamPreset.getBusNumberDriverless() > 0) {
			/**
			 * 	    @Autowired
			 *    private ScheduleParamsDriverlessMapper scheduleParamsDriverlessMapper;
			 *    @Autowired
			 *    private ScheduleParamsDrInoutMapper scheduleParamsDrInoutMapper;
			 *    @Autowired
			 *    private ScheduleParamsDrRouteSubMapper scheduleParamsDrRouteSubMapper;
			 * */
			List<ScheduleParamsDriverless> driverlessList = scheduleParamsDriverlessMapper.getByTemplateId(templateId);
			List<ScheduleParamsDrInout> inoutList = scheduleParamsDrInoutMapper.getByTemplateId(templateId);
			List<ScheduleParamsDrRouteSub> routeSubList = scheduleParamsDrRouteSubMapper.getByTemplateId(templateId);

			if (driverlessList == null || driverlessList.isEmpty()) {
				throw new MyException("-1", "无人车线路运营参数设置不能为空！");
			}
			ScheduleParamsDriverless driverless = driverlessList.get(0);
			if ((StringUtil.isEmpty(driverless.getUpFirstTime()) && StringUtil.isNotEmpty(driverless.getUpLatestTime()))
					|| (StringUtil.isNotEmpty(driverless.getUpFirstTime()) && StringUtil.isEmpty(driverless.getUpLatestTime()))) {
				throw new MyException("-1", "无人车配置上行首末班时间有误！");
			}
			if ((StringUtil.isEmpty(driverless.getDownFirstTime()) && StringUtil.isNotEmpty(driverless.getDownLatestTime()))
					|| (StringUtil.isNotEmpty(driverless.getDownFirstTime()) && StringUtil.isEmpty(driverless.getDownLatestTime()))) {
				throw new MyException("-1", "无人车配置下行首末班时间有误！");
			}
			if (StringUtil.isEmpty(driverless.getUpFirstTime()) && StringUtil.isEmpty(driverless.getUpLatestTime())
					&& StringUtil.isEmpty(driverless.getDownFirstTime()) && StringUtil.isEmpty(driverless.getDownLatestTime())) {
				throw new MyException("-1", "无人车配置上下行首末班时间有误！");
			}
			if (driverless.getVehicleContent() == null) {
				throw new MyException("-1", "无人车配置车内容量不能为空！");
			}
			if (driverless.getAnchorDurationMin() == null) {
				throw new MyException("-1", "无人车配置最小停站时间不能为空！");
			}
			if("0".equals(driverless.getUpDirection())&&"0".equals(driverless.getDownDirection())) {
				driverless.setUpDirection("1");
				if(!scheduleParam.isLoopLine()) {
					driverless.setDownDirection("1");
				}
			}
			scheduleParam.setScheduleParamsDriverless(driverless);

			if (routeSubList == null || routeSubList.isEmpty()) {
				throw new MyException("-1", "无人车配置常规公交营运任务参数配置不能为空！");
			}
			scheduleParam.setScheduleParamsDrRouteSubList(routeSubList);

			scheduleParam.setScheduleParamsDrInoutList(inoutList);

			List<ScheduleParamsDrBus> drBusList = new ArrayList<>();
			scheduleParam.setScheduleParamsDrBusList(drBusList);

			//todo 文远接口暂无数据，测试
			ScheduleParamsDrBus bus1 = new ScheduleParamsDrBus();
			drBusList.add(bus1);
			bus1.setBusNameWY("bus1");
			ScheduleParamsDrPlan bus1plan1 = new ScheduleParamsDrPlan();
			bus1.getPlanList().add(bus1plan1);
			bus1plan1.setBusNameWY("bus1");
			bus1plan1.setDispatchDate("2023-12-20");
			bus1plan1.setRouteIdWY("11");
			bus1plan1.setBeginTime("09:00");
			bus1plan1.setEndTime("10:00");
			bus1plan1.setFirstStationId(102023l);
			bus1plan1.setLastStationId(102023l);
			ScheduleParamsDrPlan bus1plan2 = new ScheduleParamsDrPlan();
			bus1.getPlanList().add(bus1plan2);
			bus1plan2.setBusNameWY("bus1");
			bus1plan2.setDispatchDate("2023-12-20");
			bus1plan2.setRouteIdWY("11");
			bus1plan2.setBeginTime("15:00");
			bus1plan2.setEndTime("17:00");
			bus1plan2.setFirstStationId(102023l);
			bus1plan2.setLastStationId(102023l);

			ScheduleParamsDrBus bus2 = new ScheduleParamsDrBus();
			drBusList.add(bus2);
			bus2.setBusNameWY("bus2");
			ScheduleParamsDrPlan bus2plan1 = new ScheduleParamsDrPlan();
			bus2.getPlanList().add(bus2plan1);
			bus2plan1.setBusNameWY("bus2");
			bus2plan1.setDispatchDate("2023-12-20");
			bus2plan1.setRouteIdWY("11");
			bus2plan1.setBeginTime("11:00");
			bus2plan1.setEndTime("14:00");
			bus2plan1.setFirstStationId(201083l);
			bus2plan1.setLastStationId(201083l);

			if (drBusList != null && !drBusList.isEmpty()) {
				if (inoutList == null || inoutList.isEmpty()) {
					throw new MyException("-1", "无人车配置自动驾驶营运任务参数配置不能为空！");
				}
			}
		}

        RouteSchedule routeSchedule=null;
        System.out.println("生成开始："+(System.currentTimeMillis()-beginTimeMillis));

		routeSchedule = new ScheduleGenerateTest(scheduleParam).generate();//todo
        if(routeSchedule.getTripMap().size()>99) {
			throw new MyException("-10", "生成计划配车超过99台，请确认排班参数设置是否正确");
        }

		SchedulePlanResult schedulePlanResult=new SchedulePlanResult();
		schedulePlanResult.setScheduleParam(scheduleParam);
		schedulePlanResult.setScheduleParamPreset(scheduleParamPreset);
		schedulePlanResult.setSchedulePlan4Mj(schedulePlan4Mj);
		schedulePlanResult.setRouteSchedule(routeSchedule);
		System.out.println("生成结束："+(System.currentTimeMillis()-beginTimeMillis));
		return schedulePlanResult;
    }

	public boolean isLoopLine(ScheduleParam scheduleParam) {
		RouteSta firstRouteSta=scheduleParam.getRouteSta(StationMark.UP_FIRST.getValue());
		RouteSta lastRouteSta=scheduleParam.getRouteSta(StationMark.UP_LAST.getValue());
		if(firstRouteSta==null||lastRouteSta==null) {
			throw new MyException("-1", "请确认是否有上行方向首末站");
		}
		if((firstRouteSta.getStationId().equals(lastRouteSta.getStationId()))||
				firstRouteSta.getRouteStationName().equals(lastRouteSta.getRouteStationName())) {
			Date firstTimeDown=scheduleParam.getFirstTime(Direction.DOWN.getValue());
			Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
			if((firstTimeDown==null&&latestTimeDown==null)||
					(firstTimeDown!=null&&firstTimeDown.equals(latestTimeDown))) {//下行首末班时间空或相同
				return true;
			}else {
				if(scheduleParam.getScheduleParamsClassesList()!=null) {
					for(ScheduleParamsClasses scheduleParamsClasses:scheduleParam.getScheduleParamsClassesList()) {
						if(Direction.DOWN.getStringValue().equals(scheduleParamsClasses.getDirection())) {
							//throw new MyException("-1", "系统暂时只支持上行单环线");
						}
					}
				}
				return true;
			}
		}else {
			Date firstTimeDown=scheduleParam.getFirstTime(Direction.DOWN.getValue());
			Date latestTimeDown=scheduleParam.getLatestTime(Direction.DOWN.getValue());
			if(firstTimeDown==null||latestTimeDown==null) {
				return true;
			}
			if(firstTimeDown!=null&&firstTimeDown.equals(latestTimeDown)) {//下行首末班时间空或相同
				Station firstStation= StationDynamicMapper.getByStationId(firstRouteSta.getStationId());
				Station lastStation= StationDynamicMapper.getByStationId(lastRouteSta.getStationId());
				if(firstStation==null||lastStation==null||
						firstStation.getLatitudedDis()==null||firstStation.getLongitudedDis()==null||
						lastStation.getLatitudedDis()==null||lastStation.getLongitudedDis()==null) {
					return false;
				}
				Double distance= StationDynamicMapper.getDistace(firstStation.getLongitudedDis(), firstStation.getLatitudedDis(), lastStation.getLongitudedDis(), lastStation.getLatitudedDis());
				if(distance<500)
					return true;
			}
		}
		return false;
	}

	private int getMinLongClassesNum(Calendar runTime,String diretiron,List<ScheduleParamsClasses> classesParamlist,ScheduleParam scheduleParam) {
		for(ScheduleParamsClasses classesParam:classesParamlist) {
			Date beginTime=DateUtil.getCalendarHM(classesParam.getBeginTime()).getTime();
			Date endTime=DateUtil.getCalendarHM(classesParam.getEndTime()).getTime();
			if(endTime.before(beginTime)) {
				endTime=DateUtil.getDateAddDay(endTime, 1);
			}
			if(diretiron.equals(classesParam.getDirection())&&DateUtil.isInTimeRange(runTime.getTime(), beginTime, endTime)){
				int minute=runTime.get(Calendar.MINUTE);
				int minClassesNum=classesParam.getClassesNumMin();
				if(minClassesNum==0) {
                    /*RouteStationPassenger highSectionPassenger=scheduleParam.getHighSectionPassenger(Integer.valueOf(diretiron), beginTime);
    				if(highSectionPassenger.getCurrentNumber()==0) {
    					minClassesNum=(int)Math.ceil(60.0/classesParam.getMaxDispatchInterval());
    				}*/
					minClassesNum=(int)Math.ceil(60.0/classesParam.getMaxDispatchInterval());//按最大间隔算班次
				}
				if(minute==0||minute==30) {//逢整点半点
					if(minClassesNum%2==0) {
						return minClassesNum/2;//最小发半数偶数，对半分
					}else {
						if(minute==0||!runTime.getTime().after(scheduleParam.getFirstTime(Integer.valueOf(diretiron)))) {//第一个时段，如6:30到7点，发3班
							return minClassesNum/2+1;//最小发半数奇数，前面多一班
						}else {
							return minClassesNum/2;//最小发半数奇数，后面少一班
						}
					}
				}else {//非整点半点，如05:50，06:20
					if(minute<30) {
						minute=30-minute;//得到半小时剩余分钟数
					}else {
						minute=60-minute;//得到半小时剩余分钟数
					}
					minClassesNum=(int)Math.ceil(minute*minClassesNum/60.0);
					return minClassesNum;
				}
			}
		}
		return 0;
	}

	private RouteStationPassenger getHighSectionPassenger(Calendar beginTime,int direction,Map<Integer, RouteStationPassengerInfo> passengerInfoMap,SchedulePlan4Mj schedulePlan4Mj,ScheduleParam scheduleParam) {
		int runTimeNum=0;
		if(beginTime.get(Calendar.MINUTE)<30) {//前30分钟
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2;
		}else {
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2+1;
		}
		RouteStationPassenger highSectionPassenger=null;
		RouteStationPassengerInfo passengerInfo=passengerInfoMap.get(direction);
		if(passengerInfo!=null) {
			for(List<RouteStationPassenger> passengerList:passengerInfo.getRouteStationPassenger2DList()) {
				boolean success=false;
				for(RouteStationPassenger passenger:passengerList) {
					if(runTimeNum!=passenger.getRunTimeNum()) {//时段不一致，找下个时段
						break;
					}
					if(highSectionPassenger==null||passenger.getCurrentNumber()>highSectionPassenger.getCurrentNumber()) {
						highSectionPassenger=passenger;
					}
					success=true;
				}
				if(success)
					break;
			}
		}
		if(highSectionPassenger==null) {
			highSectionPassenger=new RouteStationPassenger();
			short currentNumber = 0;
			highSectionPassenger.setRunTimeNum((short)runTimeNum);
			highSectionPassenger.setDirection(String.valueOf(direction));
			highSectionPassenger.setCurrentNumber(currentNumber);
			//throw new MyException("-1", "缺失客流"+DateFormatUtil.HM.getDateString(beginTime.getTime())+(direction==Direction.UP.getValue()?"上行":"下行"));
		}
		schedulePlan4Mj.addHighSectionPassenger(highSectionPassenger);
		scheduleParam.setHighSectionPassenger(direction, DateFormatUtil.HM.getDateString(beginTime.getTime()), highSectionPassenger);
		return highSectionPassenger;
	}

	private int getClassesNumber(Calendar beginTime,int direction,RouteStationPassengerInfo passengerInfo,
								 List<ScheduleParamsOccupancy> scheduleParamsOccupancyList,int vehicleContent,double busOccupancyFull) {
		Map<Long, Integer> classesNumberRouteStaMap=getClassesNumber4RouteSta(beginTime, direction, passengerInfo, scheduleParamsOccupancyList, vehicleContent, busOccupancyFull);
		int classesNumber=0;
		for(Integer classesNumberRouteSta:classesNumberRouteStaMap.values()) {
			if(classesNumberRouteSta>classesNumber) {
				classesNumber=classesNumberRouteSta;
			}
		}
		return classesNumber;
	}

	private Map<Long, Integer> getClassesNumber4RouteSta(Calendar beginTime,int direction,RouteStationPassengerInfo passengerInfo,
														 List<ScheduleParamsOccupancy> scheduleParamsOccupancyList,int vehicleContent,double busOccupancyFull) {
		Map<Long, Integer> classesNumberRouteStaMap=new HashMap<Long, Integer>();
		if(passengerInfo!=null) {
			List<RouteStationPassenger> routeStaPassengerList=getRouteStaPassengerList(beginTime, passengerInfo);
			Map<Long,Double> routeStaOccupancyMap=new HashMap<Long, Double>();
			for(RouteStationPassenger passenger:routeStaPassengerList) {//设置默认满载率
				routeStaOccupancyMap.put(passenger.getRouteStaId(), busOccupancyFull);
			}
			for(ScheduleParamsOccupancy occupancy:scheduleParamsOccupancyList) {
				if(DateUtil.isInTimeRange(beginTime.getTime(), DateUtil.getDateHM(occupancy.getBeginTime()), DateUtil.getDateHM(occupancy.getEndTime()))) {
					Double busOccupancy=null;
					for(RouteStationPassenger passenger:routeStaPassengerList) {
						if(passenger.getRouteStaId().equals(occupancy.getFromRouteStaId())) {
							busOccupancy=occupancy.getBusOccupancy().doubleValue();
						}
						if(busOccupancy!=null) {
							routeStaOccupancyMap.put(passenger.getRouteStaId(), busOccupancy);//重新设置区段满载率
						}
						if(passenger.getRouteStaId().equals(occupancy.getToRouteStaId())) {
							busOccupancy=null;
						}
					}
				}
			}
			for(RouteStationPassenger passenger:routeStaPassengerList) {
				Double busOccupancy=routeStaOccupancyMap.get(passenger.getRouteStaId());
				if(busOccupancy!=null) {
					int classesNumber=(int)Math.ceil(passenger.getCurrentNumber()/(vehicleContent*busOccupancy*0.01));
					classesNumberRouteStaMap.put(passenger.getRouteStaId(), classesNumber);
				}
			}
		}
		return classesNumberRouteStaMap;
	}

	private List<RouteStationPassenger> getRouteStaPassengerList(Calendar beginTime,RouteStationPassengerInfo passengerInfo) {
		int runTimeNum=0;
		if(beginTime.get(Calendar.MINUTE)<30) {//前30分钟
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2;
		}else {
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2+1;
		}
		List<RouteStationPassenger> routeStaPassengerList=new ArrayList<RouteStationPassenger>();
		for(List<RouteStationPassenger> passengerList:passengerInfo.getRouteStationPassenger2DList()) {
			boolean success=false;
			for(RouteStationPassenger passenger:passengerList) {
				if(runTimeNum!=passenger.getRunTimeNum()) {//时段不一致，找下个时段
					break;
				}
				routeStaPassengerList.add(passenger);
				success=true;
			}
			if(success)
				break;
		}
		return routeStaPassengerList;
	}

	private ShortLineSchedule getShortLineSchedule(Date runTime,int direction,List<RouteStationPassenger> routeStaPassengerList,ScheduleParam scheduleParam,
												   Map<Long, Integer> classesNumberRouteStaMap,int classesNum, int minLongClassesNum) {
		int longClassesNum=minLongClassesNum;
        /*if(classesNum>2*longClassesNum)//短线不能超过一半
    		longClassesNum=(int)Math.ceil(classesNum*1.0/2);*/
		List<RouteStaTurn> routeStaTurnList=scheduleParam.getRouteStaTurnList(direction);
		List<RouteStaTurn> routeStaTurnListSort=new ArrayList<RouteStaTurn>();
		for(RouteStationPassenger passenger:routeStaPassengerList) {
			for(RouteStaTurn routeStaTurn:routeStaTurnList) {
				if(passenger.getRouteStaId().equals(routeStaTurn.getLastRouteStaId())) {
					routeStaTurnListSort.add(routeStaTurn);
					break;
				}
			}
		}
		List<Integer> routeStaTurnClassesNumberList=new ArrayList<Integer>();
		int index=0;
		int classesNumberMaxSection=0;
		for(RouteStationPassenger passenger:routeStaPassengerList) {
			int classesNumberRouteSta=classesNumberRouteStaMap.get(passenger.getRouteStaId());
			if(index<routeStaTurnListSort.size()) {
				RouteStaTurn routeStaTurn=routeStaTurnListSort.get(index);
				if(passenger.getRouteStaId().equals(routeStaTurn.getLastRouteStaId())) {//调头位
					index++;
					routeStaTurnClassesNumberList.add(classesNumberMaxSection);//区段最大通过班次
					if(index<routeStaTurnListSort.size()) {//还有下个区段
						classesNumberMaxSection=classesNumberRouteSta;
					}else {//最后一个调头点
						if(classesNumberRouteSta>longClassesNum) {
							longClassesNum=classesNumberRouteSta;
						}
					}
				}else {
					if(classesNumberRouteSta>classesNumberMaxSection) {
						classesNumberMaxSection=classesNumberRouteSta;
					}
				}
			}else {
				if(classesNumberRouteSta>longClassesNum) {
					longClassesNum=classesNumberRouteSta;
				}
			}
		}
        /*if(classesNum>2*longClassesNum)//短线不能超过一半
    		longClassesNum=(int)Math.ceil(classesNum*1.0/2);*/
		RouteStaTurn routeStaTurn=null;
		if(classesNum>longClassesNum) {
			for(int i=routeStaTurnClassesNumberList.size()-1;i>=0;i--) {
				classesNumberMaxSection=routeStaTurnClassesNumberList.get(i);
				if(classesNumberMaxSection>longClassesNum) {//长线通过班次无法满足
					routeStaTurn=routeStaTurnListSort.get(i);
					break;
				}
			}
		}
		ShortLineSchedule shortLineSchedule=null;
		if(routeStaTurn!=null) {
			shortLineSchedule=new ShortLineSchedule();
			shortLineSchedule.setShortLineClasses(classesNum-longClassesNum);
			shortLineSchedule.setRouteStationName(routeStaTurn.getLastRouteStaName());
			shortLineSchedule.setRunTime(DateFormatUtil.HM.getDateString(runTime));
			shortLineSchedule.setRouteStaId(routeStaTurn.getLastRouteStaId());
			shortLineSchedule.setDirection(direction);
			shortLineSchedule.setRouteStaTurn(routeStaTurn);
		}
		return shortLineSchedule;
	}

	private EmptyBusCutOver getEmptyBusCutOver(Calendar beginTime,RouteStationPassengerInfo passengerInfo,int minLongClassesNum,int classesNumber,int vehicleContent,List<RouteSta> routeStaList,List<RouteStaTurn> routeStaTurnList) {
        /*int hour=beginTime.get(Calendar.HOUR_OF_DAY);
    	if(!(hour==6||hour==7||hour==8||hour==17||hour==18)) {
    		return null;
    	}*/
		int runTimeNum=0;
		if(beginTime.get(Calendar.MINUTE)<30) {//前30分钟
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2;
		}else {
			runTimeNum=beginTime.get(Calendar.HOUR_OF_DAY)*2+1;
		}
		if(passengerInfo!=null) {
			List<RouteStationPassenger> lowStationPassengerList=new ArrayList<RouteStationPassenger>();
			for(List<RouteStationPassenger> passengerList:passengerInfo.getRouteStationPassenger2DList()) {//看客流
				int maxCurrentNumber=0;
				RouteStationPassenger largePassenger=null;
				for(RouteStationPassenger passenger:passengerList) {
					if(runTimeNum!=passenger.getRunTimeNum()) {//时段不一致，找下个时段
						break;
					}
					double rate=passenger.getCurrentNumber()*1.0/classesNumber/vehicleContent;
					lowStationPassengerList.add(passenger);
					if(rate<0.3) {//满载率低于30%
						if(passenger.getCurrentNumber()>maxCurrentNumber) {
							maxCurrentNumber=passenger.getCurrentNumber();
						}
					}else {
						largePassenger=passenger;//第一个高于30%的站点客流
						break;
					}
				}
				for(int i=lowStationPassengerList.size()-1;i>=0;i--) {
					RouteStationPassenger passenger=lowStationPassengerList.get(i);
					for(RouteStaTurn routeStaTurn:routeStaTurnList) {
						if(routeStaTurn.getNextFirstRouteStaId().equals(passenger.getRouteStaId())) {//找到掉头位
							int longClassesNum=(int)Math.ceil(maxCurrentNumber/(vehicleContent*0.5));
							if(longClassesNum<minLongClassesNum)
								longClassesNum=minLongClassesNum;
							EmptyBusCutOver emptyBusCutOver=new EmptyBusCutOver();
							emptyBusCutOver.setFirstRouteStaId(routeStaTurn.getNextFirstRouteStaId());
							emptyBusCutOver.setFirstRouteStaName(routeStaTurn.getNextFirstRouteStaName());
							ShortLineSchedule shortLineSchedule=new ShortLineSchedule();
							shortLineSchedule.setRunTime(DateFormatUtil.HM.getDateString(beginTime.getTime()));
							shortLineSchedule.setRouteStaId(routeStaTurn.getLastRouteStaId());
							shortLineSchedule.setRouteStaTurn(routeStaTurn);//保存调头位置信息
							emptyBusCutOver.setShortLineSchedule(shortLineSchedule);
							if(classesNumber>longClassesNum) {
								shortLineSchedule.setShortLineClasses(classesNumber-longClassesNum);
							}else {
								return null;
								//shortLineSchedule.setShortLineClasses(0);//先设置为0，
							}
							emptyBusCutOver.setClassesNumberCutOver(shortLineSchedule.getShortLineClasses());
							return emptyBusCutOver;
						}
					}
				}
			}
		}
		return null;
	}

	@Transactional
	public SchedulePlan4Mj saveSchedulePlan(SchedulePlanResult schedulePlanResult) {
		ScheduleParamPreset scheduleParamPreset=schedulePlanResult.getScheduleParamPreset();
		Map<Bus, List<Trip>> tripMap=schedulePlanResult.getRouteSchedule().getTripMap();
		ScheduleParam scheduleParam=schedulePlanResult.getScheduleParam();
		SchedulePlan4Mj schedulePlan4Mj=schedulePlanResult.getSchedulePlan4Mj();
		Route route=scheduleParamPreset.getRoute();
		Long routeId=route.getRouteId();
		Date runDate=scheduleParamPreset.getRunDate();
		List<RouteSta> routeStaList=scheduleParam.getRouteStaList();
		List<SchedulePlan> planList=getSchedulePlanList(route, tripMap, scheduleParam);
		setRouteStationName(planList, routeStaList);
		long aa = System.currentTimeMillis();
		System.out.println("删除开始-非竞争"+(System.currentTimeMillis()-beginTimeMillis));
		schedulePlanDynamicMapper.delete(routeId, runDate);//删除排班记录
		System.out.println("保存开始-非竞争"+(System.currentTimeMillis()-beginTimeMillis));
		for (SchedulePlan schedulePlan : planList) {
			schedulePlanDynamicMapper.insert(schedulePlan);//插入排班记录
		}

		Integer busNumberPreset=null;
		if(scheduleParamPreset.getBusNumberOriginUp()!=null||
				scheduleParamPreset.getStartWorkRunDate()!=null) {
			busNumberPreset=tripMap.size();
		}
		if(!scheduleParamPreset.isCompetitiveRoute()&&
				busNumberPreset==null) {//没有预设配车数
			System.out.println("删除开始-最优排班");
			schedulePlanDynamicMapper.deleteOptimal(routeId, runDate);//删除最优排班
			System.out.println("保存开始-最优排班");
			for(SchedulePlan schedulePlan:planList)
				schedulePlanDynamicMapper.insertOptimal(schedulePlan);//插入最优排班
		}
//		if(!scheduleParamPreset.isCompetitiveRoute()) {
//			//删除定点线路时刻表
//			schedulePlanMapper.deleteOptimalTimetable(routeId, runDate);
//			System.out.println("保存结束"+(System.currentTimeMillis()-aa));
//		}
		//System.out.println("更新首轮开始"+(System.currentTimeMillis()-beginTimeMillis));
		//updatePlanTimeAndTask4Dispatch(routeId, runDate, tripMap);
		//System.out.println("更新首轮结束"+(System.currentTimeMillis()-beginTimeMillis));
		if(!planList.isEmpty()) {
			if(!scheduleParamPreset.isCompetitiveRoute()) {
				saveSingleBus(route, runDate, tripMap, routeStaList);
                /*updateRepSchedulePlan(route, runDate, tripMap, scheduleParam, busNumberPreset);
            	saveRepSchedulePeakHour(route, runDate, planList, busNumberPreset);*/
				//统计配车数
				saveBusNumberConfig(route, runDate, busNumberPreset, tripMap.keySet(), scheduleParam.getMountCarTemplateId());
//                //删除挂车数据
//    			schedulePlanMapper.deleteBusNumberMount(routeId, runDate);
				System.out.println("计算结束");
			}
		}
		return schedulePlan4Mj;
	}

	public List<SchedulePlan> getSchedulePlanList(Route route,Map<Bus, List<Trip>> tripMap, ScheduleParam scheduleParam){
		Long routeId=route.getRouteId();
		String routeCode=route.getRouteCode();
		Date runDate=scheduleParam.getRunDate();
		List<SchedulePlan> planList=new ArrayList<SchedulePlan>();
		List<Trip> firstRoundTripList=new ArrayList<Trip>();
//		try {
//			List<MountCarTemplateDetailVO> templateByRouteIdAndShiftTypeKey=getFirstRoundTemplate(routeId, tripMap);//获取模板首轮计划
//			firstRoundTripList = getFirstRoundTripListByTemplate(scheduleParam.getRunDate(), templateByRouteIdAndShiftTypeKey, tripMap);//更新首轮计划时间
//			if(!templateByRouteIdAndShiftTypeKey.isEmpty()) {
//				Long templateId=templateByRouteIdAndShiftTypeKey.get(0).getTemplateId();
//				scheduleParam.setMountCarTemplateId(templateId);
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//		}
		for(Bus bus:tripMap.keySet()) {
			List<Trip> list=tripMap.get(bus);
			for(Trip trip:list) {
				SchedulePlan schedulePlan=getSchedulePlan(routeId, routeCode, runDate, trip, scheduleParam);
				if(firstRoundTripList.contains(trip)) {//首轮计划，按照模板设置任务和时间
					schedulePlan.setFirstRoundPlanTime(trip.getFirstRoundPlanTime());
					schedulePlan.setFirstRoundTaskId(trip.getFirstRoundTaskId());
				}
				planList.add(schedulePlan);
				if(trip.getTurnTrip()!=null) {//回程
					schedulePlan=getSchedulePlan(routeId, routeCode, runDate, trip.getTurnTrip(),scheduleParam);
					planList.add(schedulePlan);
				}
			}
		}
		return planList;
	}

	public SchedulePlan getSchedulePlan(Long routeId, String routeCode,Date runDate,Trip trip, ScheduleParam scheduleParam) {
		Bus bus=trip.getBus();
		SchedulePlan schedulePlan = new SchedulePlan();
		int direction=trip.getDirection();
		if(direction==Direction.NODIRECTION.getValue())//中停取上行首末班时间
			direction=Direction.UP.getValue();
		Date firstTime=scheduleParam.getFirstTime(direction);
		Date tripBeginTime=trip.getTripBeginTime();
		Date tripEndTime=trip.getTripEndTime();
		if(scheduleParam.isRegularRoute()) {
			if(!trip.isShortLine()&&tripBeginTime.before(DateUtil.getDateAddMinute(firstTime, -60))) {//开车时间在零点后
				tripBeginTime=DateUtil.getDate(runDate, tripBeginTime);
				tripBeginTime=DateUtil.getDateAddMinute(tripBeginTime, 1440);
				tripEndTime=DateUtil.getDate(runDate, tripEndTime);
				tripEndTime=DateUtil.getDateAddMinute(tripEndTime, 1440);
			}else {
				tripBeginTime=DateUtil.getDate(runDate, tripBeginTime);
				tripEndTime=DateUtil.getDate(runDate, tripEndTime);
				if(tripEndTime.before(tripBeginTime)) {//到达时间在第二天
					tripEndTime=DateUtil.getDateAddMinute(tripEndTime, 1440);
				}
			}
		}else {
			Date midNight=DateUtil.getDateAddDay(DateUtil.getDateHM("0000"), 1);//隔夜零点
			if(!tripBeginTime.before(midNight)) {
				tripBeginTime=DateUtil.getDate(runDate, tripBeginTime);
				tripBeginTime=DateUtil.getDateAddDay(tripBeginTime, 1);
			}else {
				tripBeginTime=DateUtil.getDate(runDate, tripBeginTime);
			}
			if(!tripEndTime.before(midNight)) {
				tripEndTime=DateUtil.getDate(runDate, tripEndTime);
				tripEndTime=DateUtil.getDateAddDay(tripEndTime, 1);
			}else {
				tripEndTime=DateUtil.getDate(runDate, tripEndTime);
			}
		}

		schedulePlan.setPlanTime(tripBeginTime);
		schedulePlan.setTripEndTime(tripEndTime);
		schedulePlan.setDirection(String.valueOf(trip.getDirection()));
		schedulePlan.setRouteCode(routeCode);
		schedulePlan.setRouteId(routeId);
		int busNumber = bus.getStartOrderNumber();
		String startDirection = bus.getStartDirection()+"";
		String busCode = routeCode + startDirection + String.format("%04d", busNumber);
		schedulePlan.setStartOrderNumber((short) busNumber);
		schedulePlan.setStartDirection(startDirection);
		schedulePlan.setBusCode(busCode);
		schedulePlan.setPlanDate(runDate);

		if(trip.getDirection()==Direction.NODIRECTION.getValue()) {
			ServiceType serviceType=trip.getServiceType();
			if(trip.getServiceType()==null) {
				schedulePlan.setServiceType("-32");
				schedulePlan.setServiceName("单班中停");
			}else {
				schedulePlan.setServiceType(serviceType.getStringValue());
				schedulePlan.setServiceName(serviceType.getDesc());
			}
		}else {
			if(trip.isShortLine()) {
				schedulePlan.setServiceType("2");
				schedulePlan.setServiceName("短线");

			}else {
				schedulePlan.setServiceType("1");
				schedulePlan.setServiceName("全程");
			}
		}
		schedulePlan.setFirstRouteStaId(trip.getFirstRouteStaId());
		schedulePlan.setLastRouteStaId(trip.getLastRouteStaId());
		schedulePlan.setFirstRouteStaName(trip.getFirstRouteStaName());
		schedulePlan.setLastRouteStaName(trip.getLastRouteStaName());
		if(trip.getDirection()!=Direction.NODIRECTION.getValue()) {
			Double runMileage=getTripDistance(trip.getFirstRouteStaId(), trip.getLastRouteStaId(), scheduleParam.getRouteStaList());
			schedulePlan.setRunMileage(runMileage);
			ScheduleParamsAnchor scheduleParamsAnchor=scheduleParam.getScheduleParamsAnchor(direction, trip.getTripBeginTime());
			if(scheduleParamsAnchor!=null) {//设置高平低峰
				schedulePlan.setPeakType(scheduleParamsAnchor.getPeakType());
			}
		}
		return schedulePlan;
	}

	private Double getTripDistance(Long firstRouteStaId,Long lastRouteStaId,List<RouteSta> routeStaList) {
		Double distance=0.0;
		boolean pass=false;
		for(RouteSta routeSta:routeStaList) {
			if(pass) {
				if(routeSta.getStationDistance()!=null)
					distance+=routeSta.getStationDistance();
				if(routeSta.getRouteStationId().equals(lastRouteStaId))
					break;
			}else {
				if(routeSta.getRouteStationId().equals(firstRouteStaId)) {
					pass=true;
					continue;
				}
			}
		}
		return distance;
	}

	public void setRouteStationName(List<SchedulePlan> planList,List<RouteSta> routeStaList) {
		for(SchedulePlan plan:planList) {
			if(plan.getFirstRouteStaName()==null) {
				for(RouteSta routeSta:routeStaList) {
					if(routeSta.getRouteStationId().equals(plan.getFirstRouteStaId())) {
						plan.setFirstRouteStaName(routeSta.getRouteStationName());
						break;
					}
				}
			}
			if(plan.getLastRouteStaName()==null) {
				for(RouteSta routeSta:routeStaList) {
					if(routeSta.getRouteStationId().equals(plan.getLastRouteStaId())) {
						plan.setLastRouteStaName(routeSta.getRouteStationName());
						break;
					}
				}
			}
		}
	}

	private void saveSingleBus(Route route,Date runDate,Map<Bus, List<Trip>> tripMap,List<RouteSta> routeStaList) {
		schedulePlanDynamicMapper.deleteSingleBusPlan(route.getRouteId(), runDate);
		for(Bus bus:tripMap.keySet()) {
			if(bus.isSingleClass()) {
				SingleBus singleBus=new SingleBus();
				singleBus.setPlanDate(runDate);
				singleBus.setRouteCode(route.getRouteCode());
				singleBus.setRouteId(route.getRouteId());
				singleBus.setStartDirection(bus.getStartDirection());
				singleBus.setStartOrderNumber(bus.getStartOrderNumber());
				List<Trip> busTripList=tripMap.get(bus);
				for(Trip trip:busTripList) {
					if(trip.getDirection()==Direction.NODIRECTION.getValue()) {
						singleBus.setMiddleStopTime(DateUtil.getDate(runDate, trip.getTripBeginTime()));
						Long stationId=null;
						for(RouteSta routeSta:routeStaList) {
							if(routeSta.getRouteStationId().equals(trip.getFirstRouteStaId())) {
								stationId=routeSta.getStationId();
								break;
							}
						}
						if(stationId==null)
							System.out.println("aa");
						singleBus.setStationIdStop(stationId);
					}
					singleBus.setOffDutyTime(DateUtil.getDate(runDate, trip.getTripEndTime()));
				}
				if(singleBus.getStationIdStop()==null)
					continue;
				schedulePlanDynamicMapper.saveSingleBusPlan(singleBus);
			}
		}
	}

	public void saveBusNumberConfig(Route route,Date planDate,Integer busNumberPreset,Set<Bus> busList,Long mountCarTemplateId) {
		BusNumberConfig busNumberConfig= busNumberConfigDynamicMapper.selectByRouteIdAndPlanDate(route.getRouteId().intValue(), planDate);
		if(busNumberConfig==null&&busNumberPreset!=null) {
			throw new MyException("-1", "请先生成最优计划");
		}
		if(busNumberConfig==null) {
			busNumberConfig=new BusNumberConfig();
			busNumberConfig.setRouteId(route.getRouteId());
			busNumberConfig.setRouteCode(route.getRouteCode());
			busNumberConfig.setPlanDate(planDate);
		}else {
			busNumberConfigDynamicMapper.deleteByRouteIdAndPlanDate(route.getRouteId().intValue(), planDate);
		}
		int busNumberUp=0;
		int busNumberDown=0;
		int singleBusNumber=0;
		int singleBusNumberUp=0;
		int singleBusNumberDown=0;
		for(Bus bus:busList) {
			if(bus.getStartDirection()==Direction.UP.getValue()) {
				busNumberUp++;
			}else {
				busNumberDown++;
			}
			if(bus.isSingleClass()) {
				if(bus.getStartDirection()==Direction.UP.getValue()) {
					singleBusNumberUp++;
				}else if(bus.getStartDirection()==Direction.DOWN.getValue()) {
					singleBusNumberDown++;
				}
				singleBusNumber++;
			}
		}
		if(busNumberPreset==null) {
			busNumberConfig.setBusNumberOptimal(busList.size());
			if(busNumberConfig.getBusNumberOptimal()>=100)
				throw new MyException("-1", "最优计划配车数异常，请检查排班参数设置是否合理");
			busNumberConfig.setUpFirstBusNumberOptimal(busNumberUp);
			busNumberConfig.setDownFirstBusNumberOptimal(busNumberDown);
			busNumberConfig.setSingleBusNumberOptimal(singleBusNumber);
			busNumberConfig.setSingleBusNumberOptimalUp(singleBusNumberUp);
			busNumberConfig.setSingleBusNumberOptimalDown(singleBusNumberDown);
			busNumberConfig.setUpdateTimeOptimal(new Date());
			busNumberConfig.setBusNumberPreset(null);
			busNumberConfig.setUpFirstBusNumberPreset(null);
			busNumberConfig.setDownFirstBusNumberPreset(null);
			busNumberConfig.setSingleBusNumberPreset(null);
			busNumberConfig.setSingleBusNumberPresetUp(null);
			busNumberConfig.setSingleBusNumberPresetDown(null);
			busNumberConfig.setUpdateTimePreset(null);
		}else {
			busNumberConfig.setBusNumberPreset(busList.size());
			busNumberConfig.setUpFirstBusNumberPreset(busNumberUp);
			busNumberConfig.setDownFirstBusNumberPreset(busNumberDown);
			busNumberConfig.setSingleBusNumberPreset(singleBusNumber);
			busNumberConfig.setSingleBusNumberPresetUp(singleBusNumberUp);
			busNumberConfig.setSingleBusNumberPresetDown(singleBusNumberDown);
			busNumberConfig.setUpdateTimePreset(new Date());
		}
		busNumberConfig.setTemplateId(mountCarTemplateId);
		busNumberConfigDynamicMapper.save(busNumberConfig);
	}
}
