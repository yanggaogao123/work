package com.gci.schedule.driverless.service.schedule.impl;


import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gci.schedule.driverless.bean.AdrealInfo;
import com.gci.schedule.driverless.bean.IntersiteTimeParams;
import com.gci.schedule.driverless.bean.RouteStationResult;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.common.MyException;
import com.gci.schedule.driverless.mapper.AdrealSimulateMapper;
import com.gci.schedule.driverless.mapper.DyBusSingleMapper;
import com.gci.schedule.driverless.mapper.DySchedulePlanDriverlessMapper;
import com.gci.schedule.driverless.service.schedule.RouteStationService;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsRouteService;
import com.gci.schedule.driverless.service.schedule.ScheduleTemplateService;
import com.gci.schedule.driverless.service.schedule.SimulationService;
import com.gci.schedule.driverless.util.DateUtil;
import com.gci.schedule.driverless.util.HttpClientUtils;
import com.gci.schedule.driverless.util.StringUtil;
import javafx.scene.chart.ScatterChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service("simulationService")
public class SimulationServiceImpl implements SimulationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationServiceImpl.class);

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Map<String, Map<String, Double>> allRouteAdrealWasteTimeMap = new HashMap<String, Map<String, Double>>(); //所有线路的站间耗时Map

    @Resource
    private RouteStationService routeStationService;
    @Autowired
    private DySchedulePlanDriverlessMapper dySchedulePlanDriverlessMapper;
    @Autowired
    private ScheduleTemplateService scheduleTemplateService;
    @Autowired
    private ScheduleParamsRouteService scheduleParamsRouteService;
    @Autowired
    private AdrealSimulateMapper adrealSimulateMapper;
    @Autowired
    private DyBusSingleMapper dyBusSingleMapper;

    @Value("${bigData.service.url}")
    private String stationWasteTimeUrl;
    @Value("${stationWasteTime.appName.key}")
    private String appNameKey;
    @Value("${stationWasteTime.appName.value}")
    private String appNameValue;
    @Value("${stationWasteTime.businessID.key}")
    private String businessIDKey;
    @Value("${stationWasteTime.businessID.value}")
    private String businessIDValue;
    @Value("${stationWasteTime.page.key}")
    private String pageKey;
    @Value("${stationWasteTime.page.value}")
    private String pageValue;
    @Value("${stationWasteTime.pageSize.key}")
    private String pageSizeKey;
    @Value("${stationWasteTime.pageSize.value}")
    private String pageSizeValue;
    @Value("${stationWasteTime.reqData.key}")
    private String reqDataKey;
    @Value("${stationWasteTime.routeId.key}")
    private String routeIdKey;
    @Value("${stationWasteTime.direction.key}")
    private String directionKey;
    @Value("${stationWasteTime.weekday.key}")
    private String weekdayKey;

    @Override
    public List<AdrealInfo> adrealInfo(Long routeId,String runDateStr,Integer scheduleStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("routeId",routeId);
        map.put("scheduleStatus",scheduleStatus);
        Date runDate = null;
        try {
            runDate = dateFormat.parse(runDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        map.put("runDate", runDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(runDate);

        //车内容量start
        Integer vehicleContent = 105; //默认车内容量105个人
        List<ScheduleTemplateJoin> scheduleTemplates = scheduleTemplateService.getJoinTemplateListByRouteId(routeId.intValue());
        for (int i = 0; i < scheduleTemplates.size(); i++) {
            if (scheduleTemplates.get(i).getApplyDayJoin() != null && scheduleTemplates.get(i).getApplyDayJoin().indexOf(String.valueOf(runDate.getDay() + 1)) > -1) {
                Integer templateId = scheduleTemplates.get(i).getTemplateId();
                ScheduleParamsRoute scheduleParamsRoute = scheduleParamsRouteService.getByTemplateId(templateId);
                vehicleContent = scheduleParamsRoute.getVehicleContent();
                map.put("vehicleContent", vehicleContent);
                break;
            }
        }
        //车内容量end


        //计划读取数据库 zyj

        List<DySchedulePlanDriverless> scheduleList = dySchedulePlanDriverlessMapper.selectSchedulePlan(map);
        if (scheduleList.isEmpty()) return null;

        List<SingleBus> singleBuses = dyBusSingleMapper.listByRouteIdAndPlanDate(routeId, DateUtil.str2Date( runDateStr.substring(0, 10) + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
        List<String> collect = singleBuses.stream().map(item -> item.getStartDirection() + ":" + item.getStartOrderNumber()).collect(Collectors.toList());
        scheduleList.forEach(item -> item.setSingleBus(collect.contains(item.getStartDirection() + ":" + item.getStartOrderNumber())));

        //进出站匹配开始 zyj
        List<AdrealSimulate> adrealSimulateList = new ArrayList<>();
        Map<String, Double> adrealWasteTimeUpMap = this.getIntersiteTimeResult(routeId.toString(), "0", String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1)); //上行进出站的耗时
        Map<String, Double> adrealWasteTimeDownMap = this.getIntersiteTimeResult(routeId.toString(), "1", String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1)); //下行进出站的耗时
        //查询线路对应方向站点列表
        List<RouteStationResult> routeStationResultUpList = this.getRouteStationResult(routeId.toString(), "0");//上行
        List<RouteStationResult> routeStationResultDownList = this.getRouteStationResult(routeId.toString(), "1");//下行

        List<NotLongStationBusSum> notLongStationBusSums = adrealSimulateMapper.getNotLongStationBusSum(map);//获取非全程在某个站点开始的车辆和
        Map<String, NotLongStationBusSum> notLongStationBusSumMap = new HashMap<String, NotLongStationBusSum>(); //非全程在某个站点开始的车辆和Map组装
        Map<String, Integer> runTimeNotLongBusTotalMap = new HashMap<String, Integer>(); //某个时间段某个方向非全程总数
        for (NotLongStationBusSum notLongStationBusSum : notLongStationBusSums) {
            Integer runTimeNum = notLongStationBusSum.getRunTimeNum();
            Integer direction = notLongStationBusSum.getDirection();
            String notLongStationBusSumKey = runTimeNum + "_" + notLongStationBusSum.getFirstRouteStationId();
            notLongStationBusSumMap.put(notLongStationBusSumKey, notLongStationBusSum);

            notLongStationBusSumKey = runTimeNum + "_" + notLongStationBusSum.getLastRouteStationId();
            notLongStationBusSumMap.put(notLongStationBusSumKey, notLongStationBusSum);

            //某个时间段非全程总数
            String runTimeNotLongBusTotalKey = runTimeNum + "_" + direction;
            if (runTimeNotLongBusTotalMap.containsKey(runTimeNotLongBusTotalKey)) {
                runTimeNotLongBusTotalMap.put(runTimeNotLongBusTotalKey, runTimeNotLongBusTotalMap.get(runTimeNotLongBusTotalKey) + notLongStationBusSum.getNotLongBusNum());
            } else {
                runTimeNotLongBusTotalMap.put(runTimeNotLongBusTotalKey, notLongStationBusSum.getNotLongBusNum());
            }

        }


        System.out.println("zyj log scheduleList.size()" + scheduleList.size());
        for (int i = 0; i < scheduleList.size(); i++) {
            DySchedulePlanDriverless schedulePlan = scheduleList.get(i);
            String direction = schedulePlan.getDirection();
            Date thisTripBeginTime = schedulePlan.getPlanTime();
            Long firstRouteStaId = schedulePlan.getFirstRouteStaId(); //首站(新增:短线及区间需要)
            Long lastRouteStaId = schedulePlan.getLastRouteStaId(); //末站(新增:短线及区间需要)


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(thisTripBeginTime);

            Calendar zoomFactorCalendar = Calendar.getInstance();//比例缩放系数时间
            zoomFactorCalendar.setTime(thisTripBeginTime);

            Map<String, Double> adrealWasteTimeMap = new HashMap<String, Double>();
            List<RouteStationResult> routeStationResultList = new ArrayList<RouteStationResult>();
            if (direction.equals("0")) {
                adrealWasteTimeMap = adrealWasteTimeUpMap;
                routeStationResultList = routeStationResultUpList;
            } else {
                adrealWasteTimeMap = adrealWasteTimeDownMap;
                routeStationResultList = routeStationResultDownList;
            }
            //截取该子线路首战和末站间的站点(比如短线就要截短)
            boolean firstRouteStaIdFlag = false; //首战开始状态
            boolean lastRouteStaIdFlag = false; //末站结束状态


            Integer runTimeNum = (Integer) (schedulePlan.getPlanTime().getHours() * 2 + schedulePlan.getPlanTime().getMinutes() / 30);//时间段，6点是12，6点半是13
            Map<String, Integer> firstLastStationAppearMap = new HashMap<>(); //首末站点出现的总数Map
            Integer notLongBusSum = 0; //非全程巴士总数
            boolean buildAdrealSimulateFlag = false;//组装班次所在任务对应的站点

            //单班中停
            if ("-32".equals(schedulePlan.getServiceType())) {
                AdrealSimulate adrealSimulate = new AdrealSimulate();
                adrealSimulate.setBusId(Convert.toInt(schedulePlan.getBusId()));
                adrealSimulate.setBusName(schedulePlan.getBusName());

                adrealSimulate.setBusNameFull(schedulePlan.getBusNameFull());
                adrealSimulate.setStartDirection(schedulePlan.getStartDirection());
                adrealSimulate.setStartOrderNumber(schedulePlan.getStartOrderNumber());
                adrealSimulate.setServiceName(schedulePlan.getServiceName());
                adrealSimulate.setServiceType(schedulePlan.getServiceType());
                adrealSimulate.setRouteCode(schedulePlan.getRouteCode());
                adrealSimulate.setBusCode(schedulePlan.getBusCode());
                adrealSimulate.setRouteId(schedulePlan.getRouteId());
                adrealSimulate.setScheduleId(schedulePlan.getScheduleId());
                adrealSimulate.setStartDirection(schedulePlan.getStartDirection());
                adrealSimulate.setStartOrderNumber(schedulePlan.getStartOrderNumber());
                adrealSimulate.setRunTimeNum(runTimeNum);
                adrealSimulate.setAdFlag("1");//进/出站标识 0出站;1进站
                adrealSimulate.setAdTime(calendar.getTime());
                adrealSimulate.setDirection(Integer.valueOf(direction));//方向
                adrealSimulate.setSingleBus(schedulePlan.isSingleBus());
                adrealSimulateList.add((AdrealSimulate) adrealSimulate.clone());
                continue;
            }

            //获取耗时比例缩放系数
            IntersiteTimeParams intersiteTimeParamsThisSchedule = new IntersiteTimeParams();
            intersiteTimeParamsThisSchedule.setRouteId(schedulePlan.getRouteId().toString());
            intersiteTimeParamsThisSchedule.setAdTime(thisTripBeginTime);
            intersiteTimeParamsThisSchedule.setDirection(direction);
            intersiteTimeParamsThisSchedule.setFromRouteStaId(firstRouteStaId.intValue());
            intersiteTimeParamsThisSchedule.setToRouteStaId(lastRouteStaId.intValue());

            double intersiteTotalTime = getIntersiteTime(intersiteTimeParamsThisSchedule);//志武接口算出该班次的耗时 (s)
            double triplogTotolTime = (double) (schedulePlan.getTripEndTime().getTime() - schedulePlan.getPlanTime().getTime()) / 1000;
            //耗时比例缩放系数，保留2位小数
            double zoomFactor = 0;
            try{
                zoomFactor = triplogTotolTime / intersiteTotalTime;
                BigDecimal b = new BigDecimal(zoomFactor);
                zoomFactor = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            }catch (Exception e){
                System.out.println(intersiteTotalTime+"------"+triplogTotolTime);
                e.printStackTrace();
            }

            System.out.println("zoomFactor" + zoomFactor);
            System.out.println(intersiteTotalTime + "_" + triplogTotolTime);
            double wasteTimeTotalAll = 0.0;

            for (int j = 0; j < routeStationResultList.size() - 1; j++) {

                IntersiteTimeParams intersiteTimeParams = new IntersiteTimeParams();
                intersiteTimeParams.setFromRouteStaId(routeStationResultList.get(j).getRouteStationId().intValue());
                intersiteTimeParams.setFromRouteStaName(routeStationResultList.get(j).getRouteStationName());
                intersiteTimeParams.setToRouteStaId(routeStationResultList.get(j + 1).getRouteStationId().intValue());
                intersiteTimeParams.setToRouteStaName(routeStationResultList.get(j + 1).getRouteStationName());
                intersiteTimeParams.setDirection(direction);
                intersiteTimeParams.setAdTime(calendar.getTime());


                AdrealSimulate adrealSimulate = new AdrealSimulate();
                adrealSimulate.setBusId(Convert.toInt(schedulePlan.getBusId()));
                adrealSimulate.setBusName(schedulePlan.getBusName());

                adrealSimulate.setBusNameFull(schedulePlan.getBusNameFull());
                adrealSimulate.setStartDirection(schedulePlan.getStartDirection());
                adrealSimulate.setStartOrderNumber(schedulePlan.getStartOrderNumber());
                adrealSimulate.setServiceName(schedulePlan.getServiceName());
                adrealSimulate.setServiceType(schedulePlan.getServiceType());
                adrealSimulate.setRouteCode(schedulePlan.getRouteCode());
                adrealSimulate.setBusCode(schedulePlan.getBusCode());
                adrealSimulate.setRouteId(schedulePlan.getRouteId());
                adrealSimulate.setScheduleId(schedulePlan.getScheduleId());
                adrealSimulate.setStartDirection(schedulePlan.getStartDirection());
                adrealSimulate.setStartOrderNumber(schedulePlan.getStartOrderNumber());
                adrealSimulate.setRunTimeNum(runTimeNum);
                adrealSimulate.setSingleBus(schedulePlan.isSingleBus());

                if (j == 0) {
                    //站点匹配开始,单班中停的首末站为空
                    if (schedulePlan.getFirstRouteStaId() != null && routeStationResultList.get(j).getRouteStationId().toString().equals(schedulePlan.getFirstRouteStaId().toString())) {
                        buildAdrealSimulateFlag = true;
                    }
                    if (!buildAdrealSimulateFlag) continue;

                    //计算非全程巴士总数start
                    adrealSimulate.setNotLongBusSum(notLongBusSum);
                    Integer routeStationId = routeStationResultList.get(j).getRouteStationId().intValue();
                    //非全程车辆数的处理
                    notLongBusSum = this.notLongBusSumDeal(notLongBusSum, runTimeNum, routeStationId, firstLastStationAppearMap, notLongStationBusSumMap, adrealSimulate);
                    //计算非全程巴士总数end
                    adrealSimulate.setAdFlag("1");//进/出站标识 0出站;1进站
                    adrealSimulate.setAdTime(zoomFactorCalendar.getTime());
                    adrealSimulate.setRouteStaId(intersiteTimeParams.getFromRouteStaId().longValue());
                    adrealSimulate.setRouteStaName(intersiteTimeParams.getFromRouteStaName());
                    adrealSimulate.setDirection(Integer.valueOf(direction));//方向
                    adrealSimulate.setSingleBus(schedulePlan.isSingleBus());
                    adrealSimulateList.add((AdrealSimulate) adrealSimulate.clone());
                }

                //根据开始站点，结束站点+志武的站间耗时+该线路所有的站点算出这2个站的耗时

                //站点匹配开始,单班中停的首末站为空
                if (schedulePlan.getFirstRouteStaId() != null && routeStationResultList.get(j + 1).getRouteStationId().toString().equals(schedulePlan.getFirstRouteStaId().toString())) {
                    buildAdrealSimulateFlag = true;
                }
                if (!buildAdrealSimulateFlag) continue;


                //计算非全程巴士总数start
                adrealSimulate.setNotLongBusSum(notLongBusSum);
                Integer routeStationId = routeStationResultList.get(j + 1).getRouteStationId().intValue();
                //非全程车辆数的处理
                notLongBusSum = this.notLongBusSumDeal(notLongBusSum, runTimeNum, routeStationId, firstLastStationAppearMap, notLongStationBusSumMap, adrealSimulate);
                //计算非全程巴士总数end

                Double wasteTimeTotal = this.getWasteTimeTotal(intersiteTimeParams, adrealWasteTimeMap, routeStationResultList);
                wasteTimeTotalAll += wasteTimeTotal;
                calendar.add(Calendar.SECOND, new Double(wasteTimeTotal).intValue());
                zoomFactorCalendar.add(Calendar.SECOND, new Double(wasteTimeTotal * zoomFactor).intValue()); //加上耗时比例缩放系数
                adrealSimulate.setAdFlag("1");//进/出站标识 0出站;1进站
                adrealSimulate.setRouteStaId(intersiteTimeParams.getToRouteStaId().longValue());
                adrealSimulate.setRouteStaName(intersiteTimeParams.getToRouteStaName());
                adrealSimulate.setDirection(Integer.valueOf(direction));//方向
                adrealSimulate.setSingleBus(schedulePlan.isSingleBus());

                //站点匹配结束
                if (routeStationResultList.get(j + 1).getRouteStationId().toString().equals(schedulePlan.getLastRouteStaId().toString())) {
                    buildAdrealSimulateFlag = false;
                    adrealSimulate.setAdTime(schedulePlan.getTripEndTime());
                } else {
                    adrealSimulate.setAdTime(zoomFactorCalendar.getTime());
                }
                adrealSimulateList.add((AdrealSimulate) adrealSimulate.clone());

            }
            System.out.println(wasteTimeTotalAll);

        }
        //进出站匹配结束 zyj


        //zyj start
        List<AdrealInfo> adrealInfos = new ArrayList<>();
        List<StationBusAndPassengerInfo> stationBusAndPassengerInfos = adrealSimulateMapper.selectStationBusAndPassengerInfo(map); //获取站点车辆和车内乘客信息
        Map<String, StationBusAndPassengerInfo> stationBusAndPassengerInfoMap = new HashMap<String, StationBusAndPassengerInfo>(); //站点车辆和车内乘客信息Map组装
        for (StationBusAndPassengerInfo stationBusAndPassengerInfo : stationBusAndPassengerInfos) {
            String key = stationBusAndPassengerInfo.getScheduleId() + "_" + stationBusAndPassengerInfo.getRouteStationId();
            stationBusAndPassengerInfoMap.put(key, stationBusAndPassengerInfo);
            //System.out.println("zyj log getFullLoadRatio "+stationBusAndPassengerInfo.getFullLoadRatio());

        }

        System.out.println("zyj log adrealSimulateList.size()" + adrealSimulateList.size());
        //给内存中进出站列表排序
        Collections.sort(adrealSimulateList, new Comparator<AdrealSimulate>() {
            public int compare(AdrealSimulate e1, AdrealSimulate e2) {
                int flag = 0;
                if (flag == 0) {
                    flag = e1.getScheduleId().compareTo(e2.getScheduleId());
                }
                if (flag == 0) {
                    flag = e1.getAdTime().compareTo(e2.getAdTime());
                }
                if (flag == 0) {
                    flag = e1.getAdFlag().compareTo(e2.getAdFlag());
                }
                return flag;
            }
        });
        System.out.println("zyj log adrealSimulateList.size() 22 " + adrealSimulateList.size());
        Map<Integer, Double> fullLoadRatioMap=new HashMap<Integer, Double>();
        //组装《进出站详情》
        for (int i = 0; i < adrealSimulateList.size(); i++) {
            AdrealSimulate adrealSimulate = adrealSimulateList.get(i);
            String key = adrealSimulate.getScheduleId() + "_" + adrealSimulate.getRouteStaId();
            //System.out.println("zyj log key "+key);
            StationBusAndPassengerInfo stationBusAndPassengerInfo = stationBusAndPassengerInfoMap.get(key);
            //System.out.println("zyj log stationBusAndPassengerInfo get"+stationBusAndPassengerInfo);
            //System.out.println("zyj log adrealInfo add start ");
            AdrealInfo adrealInfo = new AdrealInfo();

            int upNumer = 0;
            int downNumer = 0;
            int currentNumer = 0;
            double fullLoadRatio = 0.00;
            //拿根据排班详情和客流表得出的客流数
            if (stationBusAndPassengerInfo != null) {
                upNumer = stationBusAndPassengerInfo.getUpNumber();
                downNumer = stationBusAndPassengerInfo.getDownNumber();
                //考虑了短线之后的车内人数
                Integer runTimeNotLongBusTotal = 0;
                String runTimeNotLongBusTotalKey = adrealSimulate.getRunTimeNum() + "_" + adrealSimulate.getDirection();
                if (runTimeNotLongBusTotalMap.containsKey(runTimeNotLongBusTotalKey)) {
                    runTimeNotLongBusTotal = runTimeNotLongBusTotalMap.get(runTimeNotLongBusTotalKey);
                }
                double currentNumerDouble = Math.ceil(stationBusAndPassengerInfo.getCurrentNumberAll() * 1.0
                        / (stationBusAndPassengerInfo.getBusNum() - (runTimeNotLongBusTotal - adrealSimulate.getNotLongBusSum())));
                currentNumer = (int) currentNumerDouble;
               /* System.out.println(""+adrealSimulate.getScheduleId()+" - "+ adrealSimulate.getRouteStaId()+" currentNumer new :"+currentNumer+" - currentNumer old :"+stationBusAndPassengerInfo.getCurrentNumber());
                System.out.println("busNum new :"+stationBusAndPassengerInfo.getBusNum()
                        +" - busNum old :"+(stationBusAndPassengerInfo.getBusNum()-adrealSimulate.getNotLongBusSum()));*/
                fullLoadRatio = Math.ceil(currentNumer * 100.0 / vehicleContent);
                if(fullLoadRatio>95) {//满载率大于95%
                	Double fullLoadRatioTemp=fullLoadRatioMap.get(currentNumer);
                	if(fullLoadRatioTemp==null) {
                		fullLoadRatioTemp=95+Math.random();
                		fullLoadRatioMap.put(currentNumer, fullLoadRatioTemp);
                	}
                	fullLoadRatio=fullLoadRatioTemp;
                }
            }
            adrealInfo.setBusNameFull(adrealSimulate.getBusNameFull());
            adrealInfo.setBusNumber(adrealSimulate.getStartOrderNumber().intValue()); //
            adrealInfo.setFirstDirection(Integer.valueOf(adrealSimulate.getStartDirection()));
            adrealInfo.setServiceType(adrealSimulate.getServiceType());
            adrealInfo.setServiceName(adrealSimulate.getServiceName());


            adrealInfo.setAdFlag(Integer.valueOf(adrealSimulate.getAdFlag()));
            adrealInfo.setAdTime(adrealSimulate.getAdTime());
            adrealInfo.setBusCode(adrealSimulate.getBusCode());
            adrealInfo.setBusId(adrealSimulate.getBusId());
            adrealInfo.setBusName(adrealSimulate.getBusName());
            adrealInfo.setScheduleId(adrealSimulate.getScheduleId().intValue());
            adrealInfo.setDirection(adrealSimulate.getDirection());//方向
            if (adrealSimulate.getRouteStaId() != null) {
                adrealInfo.setRouteStationId(adrealSimulate.getRouteStaId().intValue());
                adrealInfo.setRouteStationName(adrealSimulate.getRouteStaName());
            }


            adrealInfo.setUpNumber(upNumer);
            adrealInfo.setDownNumber(downNumer);
            adrealInfo.setCurrentNumber(currentNumer);
            adrealInfo.setFullLoadRatio(fullLoadRatio);
            adrealInfo.setSingleBus(adrealSimulate.getSingleBus());

            adrealInfos.add(adrealInfo);
            //System.out.println("zyj log adrealInfo  add end ");
        }
        System.out.println("zyj log adrealInfo.size()" + adrealInfos.size());

        //仿真获取时间
//		第一次获取9点-10点的数据
//		1.1获取开始时间在9点之前，结束在在9-10点的车的位置
//		1.2获取这些车辆在9点-10点的进出站详情
        List<AdrealInfo> adrealInfoBetweenHList = new ArrayList<>(); //车辆在目标一个小时的进出站数据
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(runDate);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date runDateNextH = calendar.getTime(); //下一个小时的时间
        int scheduleId = 0;
        for (int i = 0; i < adrealInfos.size(); i++) {
            if (scheduleId == 0 || scheduleId != adrealInfos.get(i).getScheduleId()) {
                scheduleId = adrealInfos.get(i).getScheduleId();
            }
            Date adTimeThis = adrealInfos.get(i).getAdTime();
            Date adTimeNext = null;
            if (i != adrealInfos.size() - 1) {
                if (scheduleId == adrealInfos.get(i + 1).getScheduleId()) {
                    adTimeNext = adrealInfos.get(i + 1).getAdTime();

                }
            }
            //符合范围的不删除
            if ((adTimeNext == null || !runDate.after(adTimeNext)) && (runDateNextH.after(adTimeThis))) {
                adrealInfoBetweenHList.add(adrealInfos.get(i));
            } else {
                //System.out.println( "删除不符合范围："+adrealInfos.get(i).getScheduleId() +"-"+adrealInfos.get(i).getAdTime() +"-"+adrealInfos.get(i).getRouteStationName());
            }

        }
        System.out.println("zyj log adrealInfoBetweenHList.size()" + adrealInfoBetweenHList.size());

        //按返回的《进出站详情》按进出站时间排序
        Collections.sort(adrealInfoBetweenHList, new Comparator<AdrealInfo>() {
            public int compare(AdrealInfo e1, AdrealInfo e2) {
                int flag = 0;
                if (flag == 0) {
                    flag = e1.getAdTime().compareTo(e2.getAdTime());
                }

                return flag;
            }
        });


        return adrealInfoBetweenHList;
        //adrealSimulateMapper.adrealInfo(map);
        //zyj end
    }

    //志武站间耗时返回结果
    private Map<String, Double> getIntersiteTimeResult(String routeId, String direction, String weekday) {
        Map<String, Double> adrealWasteTimeMap = new HashMap<String, Double>();
        String adrealWasteTimeKey = routeId + "_" + weekday + "_" + direction;
        if (allRouteAdrealWasteTimeMap.containsKey(adrealWasteTimeKey)) {
            adrealWasteTimeMap = allRouteAdrealWasteTimeMap.get(adrealWasteTimeKey);
            return adrealWasteTimeMap;
        }
		/*http://10.89.141.131:8080/dataservices/getData
		{
			"appName": "appName",
				"businessID": "100020",
				"page": 1,
				"pageSize": 10,
				"reqData": {"routeId": "710","direction":"0"}}*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(appNameKey, appNameValue);
        jsonObject.put(businessIDKey, businessIDValue);
        jsonObject.put(pageKey, pageValue);
        jsonObject.put(pageSizeKey, pageSizeValue);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(routeIdKey, routeId);
        jsonObject1.put(directionKey, direction);
        jsonObject1.put(weekdayKey, weekday);
        jsonObject.put(reqDataKey, jsonObject1);
        JSONObject responJson = HttpClientUtils.httpPost(stationWasteTimeUrl, jsonObject);
        if (!responJson.getString("retCode").equals("0")) {
            throw new MyException("500", "请稍后再试");
        }
        JSONObject retData = responJson.getJSONObject("retData");
        List<IntersiteTimeResult> intersiteTimeResults = JSON.parseObject(retData.getJSONArray("list").toJSONString(), new TypeReference<List<IntersiteTimeResult>>() {
        });

        //站间耗时返回结果从list 转Map
        for (IntersiteTimeResult intersiteTimeResult :
                intersiteTimeResults) {
            //key:时-分-方向-开始站台-结束站台
            String key = intersiteTimeResult.getPhour() + "_" + intersiteTimeResult.getPminute() + "_" + intersiteTimeResult.getDirection() +
                    "_" + intersiteTimeResult.getStationId() + "_" + intersiteTimeResult.getNextStationId();
            String value = intersiteTimeResult.getAvgtime();
            if(value!=null)
            	adrealWasteTimeMap.put(key, Double.valueOf(value));
        }
        if(adrealWasteTimeMap.isEmpty()) {
        	throw new MyException("-1", "获取大数据平台站间用时失败");
        }
        //加入到所有线路的站间耗时Map
        allRouteAdrealWasteTimeMap.put(adrealWasteTimeKey, adrealWasteTimeMap);
        return adrealWasteTimeMap;
    }

    //查询线路对应方向站点列表
    @Override
    public List<RouteStationResult> getRouteStationResult(String routeId, String direction) {
        String result = routeStationService.getListByRouteId(Long.valueOf(routeId));
        List<RouteStationResult> routeStationResultList = JSON.parseObject(JSON.parseObject(result).getJSONArray("data").toJSONString(), new TypeReference<List<RouteStationResult>>() {
        });
        List<String> stationMarkList = new ArrayList<>();
        if ("0".equals(direction)) {
            stationMarkList.add("0");
            stationMarkList.add("1");
            stationMarkList.add("2");
        } else if ("1".equals(direction)) {
            stationMarkList.add("3");
            stationMarkList.add("4");
            stationMarkList.add("5");
        }
        //获取对应方向的站点信息
        List<RouteStationResult> sameDirectionResultList = new ArrayList<RouteStationResult>();
        for (int i = 0; i < routeStationResultList.size(); i++) {
            if (stationMarkList.contains(routeStationResultList.get(i).getStationMark())) {
                sameDirectionResultList.add(routeStationResultList.get(i));
            }
        }
        return sameDirectionResultList;
    }

    //查询线路的中间站点列表
    private List<RouteStationResult> getBetweenRouteStationResult(List<RouteStationResult> routeStationResultList, String direction, Integer fromRouteStaId, Integer toRouteStaId) {
        boolean fromFlag = false;
        boolean toFlag = false;
        //获取从开始站点-》结束站点它们中间的站点
        List<RouteStationResult> betweenRouteStationResultList = new ArrayList<RouteStationResult>();
        for (int i = 0; i < routeStationResultList.size(); i++) {
            if (fromRouteStaId == routeStationResultList.get(i).getRouteStationId().intValue()) {
                fromFlag = true;
            }
            if (!fromFlag) {
                continue;
            }
            if (fromFlag) {
                if (toRouteStaId == routeStationResultList.get(i).getRouteStationId().intValue()) {
                    toFlag = true;
                    betweenRouteStationResultList.add(routeStationResultList.get(i));
                }
                if (toFlag) {
                    continue;
                }
            }
            betweenRouteStationResultList.add(routeStationResultList.get(i));
        }
        return betweenRouteStationResultList;
    }

    //根据开始站点，结束站点+志武的站间耗时+该线路所有的站点算出这2个站的耗时
    private Double getWasteTimeTotal(IntersiteTimeParams intersiteTimeParams, Map<String, Double> adrealWasteTimeMap, List<RouteStationResult> routeStationResultList) {
        //目标2个站点之间的站点
        List<RouteStationResult> betweenRouteStationResultList = this.getBetweenRouteStationResult(routeStationResultList, intersiteTimeParams.getDirection(), intersiteTimeParams.getFromRouteStaId(), intersiteTimeParams.getToRouteStaId());
        Date adTime = intersiteTimeParams.getAdTime();//进站时间

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(adTime);

        //折线按比例获取两个站A-Z间的耗时(B为中间站)
        for (int i = 0; i < betweenRouteStationResultList.size() - 1; i++) {
            Long stationIdA = betweenRouteStationResultList.get(i).getStationId();
            Long stationIdB = betweenRouteStationResultList.get(i + 1).getStationId();
            int hourThis = calendar.get(Calendar.HOUR_OF_DAY);
            int hourNext = 0;
            int minuteThis = calendar.get(Calendar.MINUTE);
            int minuteNext = 0;
            int minnuteRate = minuteThis % 10; //折线占比用（0-10）
            int minnute10This = (int) Math.ceil((minuteThis / 10) + 1) * 10;
            int minnute10Next = 0;
            if (minnute10This == 60) {
                hourNext = hourThis + 1;
                minnute10Next = 10;
            } else {
                hourNext = hourThis;
                minnute10Next = minnute10This + 10;
            }
            //该时间
            String keyThis = hourThis + "_" + minnute10This + "_" + intersiteTimeParams.getDirection() +
                    "_" + stationIdA + "_" + stationIdB;


            double avgtimeThis = 0.0;
            if (adrealWasteTimeMap.get(keyThis) != null) {
                avgtimeThis = adrealWasteTimeMap.get(keyThis);
            } else {
                System.out.println(keyThis);
                minnuteRate = 10;
            }

            //下个时间
            String keyNext = hourNext + "_" + minnute10Next + "_" + intersiteTimeParams.getDirection() +
                    "_" + stationIdA + "_" + stationIdB;
            double avgtimeNext = 0.0;
            if (adrealWasteTimeMap.get(keyNext) != null) {
                avgtimeNext = adrealWasteTimeMap.get(keyNext);
            } else {
                System.out.println(keyNext);
                minnuteRate = 0;

            }
            //按该时间段耗时和上一时间段的耗时，按折线来换算出平均时间
            //比如现在是 7:07分，它的折线占比：minnuteRate为7，则拿7:10的平均耗时：avgtimeThis；7:20分的的平均耗时：avgtimeNext来换算出avgtimeConvert
            //avgtimeConvert =(10-7)*avgtimeThis/10 +7*avgtimeNext/10
            int avgtimeConvert = (int) ((10 - minnuteRate) * avgtimeThis / 10 + minnuteRate * avgtimeNext / 10);
            //System.out.println(betweenRouteStationResultList.get(i).getStationId()+betweenRouteStationResultList.get(i).getRouteStationName()+":" +avgtimeConvert);
            calendar.add(Calendar.SECOND, avgtimeConvert); //时间加上耗时
        }
        Double wasteTimeTotal = Double.valueOf((calendar.getTime().getTime() - adTime.getTime()) / 1000);
        return wasteTimeTotal;
    }

    @Override
    public Double getIntersiteTime(IntersiteTimeParams intersiteTimeParams) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(intersiteTimeParams.getAdTime());
        Map<String, Double> adrealWasteTimeMap = null;
        //查询线路对应方向站点列表
        List<RouteStationResult> routeStationResultList = this.getRouteStationResult(intersiteTimeParams.getRouteId(), intersiteTimeParams.getDirection());
        if(Objects.nonNull(StringUtil.MSG_KEY)&&StringUtil.GENERATE_MSG.containsKey(StringUtil.MSG_KEY)){
            return getIntersiteTimeByStationDistance(intersiteTimeParams,routeStationResultList);
        }
        try {
            adrealWasteTimeMap = this.getIntersiteTimeResult(intersiteTimeParams.getRouteId(), intersiteTimeParams.getDirection(), String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1));
        } catch (Exception e) {
        	e.printStackTrace();
        	//获取大数据站间用时失败,则通过标2个站点之间的站间距比上总的站间距估算站间用时
            LOGGER.info("获取大数据站间用时失败,则通过标2个站点之间的站间距比上总的站间距估算站间用时");
            StringUtil.putGenerateMsg();
        	return getIntersiteTimeByStationDistance(intersiteTimeParams,routeStationResultList);
//            throw new MyException("-1", "获取大数据站间用时失败");
        }

        //根据开始站点，结束站点+志武的站间耗时+该线路所有的站点算出这2个站的耗时
        Double wasteTimeTotal = this.getWasteTimeTotal(intersiteTimeParams, adrealWasteTimeMap, routeStationResultList);
        if (intersiteTimeParams.getRouteFromId() == null || intersiteTimeParams.getRouteToId() == null || intersiteTimeParams.getWasteTime() == null) {
            return wasteTimeTotal;
        }
        intersiteTimeParams.setFromRouteStaId(intersiteTimeParams.getRouteFromId());
        intersiteTimeParams.setToRouteStaId(intersiteTimeParams.getRouteToId());
        Double allTime = this.getWasteTimeTotal(intersiteTimeParams, adrealWasteTimeMap, routeStationResultList);
        if(allTime==0){
        	throw new MyException("-1", "获取大数据全程用时为0");
        }
        return new BigDecimal(wasteTimeTotal).divide(new BigDecimal(allTime), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(intersiteTimeParams.getWasteTime().toString())).multiply(new BigDecimal("60")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Double getIntersiteTimeByStationDistance(IntersiteTimeParams intersiteTimeParams,List<RouteStationResult> routeStationResultList){
        //目标2个站点之间的站点
        List<RouteStationResult> betweenRouteStationResultList = this.getBetweenRouteStationResult(routeStationResultList, intersiteTimeParams.getDirection(), intersiteTimeParams.getFromRouteStaId(), intersiteTimeParams.getToRouteStaId());
        Double betweenRouteStationDistance = betweenRouteStationResultList.stream().mapToDouble(RouteStationResult::getStationDistance).sum();
        Double totalRouteStationDistance = routeStationResultList.stream().mapToDouble(RouteStationResult::getStationDistance).sum();

        return new BigDecimal(betweenRouteStationDistance).divide(new BigDecimal(totalRouteStationDistance), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(intersiteTimeParams.getWasteTime().toString())).multiply(new BigDecimal("60")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //非全程车辆数的处理
    //notLongBusSumDeal(notLongBusSum,runTimeNum,routeStationId,firstLastStationAppearMap,notLongStationBusSumMap,adrealSimulate)
    private Integer notLongBusSumDeal(Integer notLongBusSum, Integer runTimeNum, Integer routeStationId
            , Map<String, Integer> firstLastStationAppearMap
            , Map<String, NotLongStationBusSum> notLongStationBusSumMap
            , AdrealSimulate adrealSimulate) {
        String notLongStationBusSumKey = runTimeNum + "_" + routeStationId;
        if (notLongStationBusSumMap.containsKey(notLongStationBusSumKey)) {
            Integer notLongBusNum = notLongStationBusSumMap.get(notLongStationBusSumKey).getNotLongBusNum();//非全程巴士数
            String firstLastStationAppearKey = notLongStationBusSumMap.get(notLongStationBusSumKey).getFirstRouteStationId() + "_" + notLongStationBusSumMap.get(notLongStationBusSumKey).getLastRouteStationId();
            if (firstLastStationAppearMap.containsKey(firstLastStationAppearKey)) {
                adrealSimulate.setNotLongBusSum(notLongBusSum);//写入该半个小时内该站点非全程发班的总班次
                notLongBusSum -= notLongBusNum;
                firstLastStationAppearMap.put(firstLastStationAppearKey, firstLastStationAppearMap.get(firstLastStationAppearKey) + 1);
            } else {
                notLongBusSum += notLongBusNum;
                adrealSimulate.setNotLongBusSum(notLongBusSum);//写入该半个小时内该站点非全程发班的总班次

                firstLastStationAppearMap.put(firstLastStationAppearKey, 1);
            }
        }
        return notLongBusSum;
    }

    @Override
    public String getMinPlanTimeByRouteIdAndPlanDate(String routeId, String planDate) {
        Date date = DateUtil.str2Date(planDate, "yyyy-MM-dd");
        Date minTime = dySchedulePlanDriverlessMapper.getMinPlanTimeByRouteIdAndPlanDate(routeId, date);
        if (minTime == null) {
            return null;
        }
        return DateUtil.date2Str(minTime, "HH:mm:ss");
    }

}
