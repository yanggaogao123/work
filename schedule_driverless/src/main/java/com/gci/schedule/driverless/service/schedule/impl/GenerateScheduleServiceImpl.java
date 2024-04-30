package com.gci.schedule.driverless.service.schedule.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.*;
import com.gci.schedule.driverless.bean.bs.BsBus;
import com.gci.schedule.driverless.bean.bs.TriplogSimpleDto;
import com.gci.schedule.driverless.bean.common.BsData;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.Direction;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.enums.StationMark;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.feign.AptsBaseApp;
import com.gci.schedule.driverless.feign.DispatchApp;
import com.gci.schedule.driverless.mapper.*;
import com.gci.schedule.driverless.service.common.SupportPlanService;
import com.gci.schedule.driverless.service.schedule.*;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.SystemTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenerateScheduleServiceImpl implements GenerateScheduleService {

    @Autowired
    private ScheduleServerService scheduleServerService;
    @Autowired
    private DispatchApp dispatchApp;
    @Autowired
    private AptsBaseApp aptsBaseApp;
    @Autowired
    private DySchedulePlanDriverlessMapper dySchedulePlanDriverlessMapper;
    @Autowired
    private DyDriverlessConfigMapper dyDriverlessConfigMapper;
    @Autowired
    private BigDataService bigDataService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private ScheduleParamsAnchorService scheduleParamsAnchorService;
    @Autowired
    private RouteWasteTimeMapper routeWasteTimeMapper;
    @Autowired
    private BusNumberConfigService busNumberConfigService;
    @Value("${dispatchServer.server.url}")
    private String dispatchServerUrl;
    @Value("${getRunBusByRoute}")
    private String getRunBusByRoute;
    @Value("${dispatchApp.app.url01}")
    private String dispatchAppAppUrl01;
    @Value("${dispatchApp.app.url02}")
    private String dispatchAppAppUrl02;
    @Value("${newRunBus}")
    private String newRunBus;
    @Value("${getByRouteIdAndRouteNameKey}")
    private String getByRouteIdAndRouteNameKey;
    @Autowired
    private ScheduleRouteConfigMapper scheduleRouteConfigMapper;
    @Autowired
    private RunBusService runBusService;
    @Autowired
    private ScheduleParamsSingleService scheduleParamsSingleService;
    @Autowired
    private ScheduleParamsClassesService scheduleParamsClassesService;
    @Autowired
    private DyDriverlessPlanTimeMapper dyDriverlessPlanTimeMapper;
    @Autowired
    private RouteSubService routeSubService;
    @Autowired
    private DyDriverlessBusConfigMapper dyDriverlessBusConfigMapper;
    @Autowired
    private TriplogMapper triplogMapper;
    @Autowired
    private SupportPlanService supportPlanService;
    @Autowired
    private GisRoadnodeMapper gisRoadnodeMapper;

    @Override
    public R generateSchedule(GenerateScheduleParams params) {
        Route route = dispatchApp.getRouteById(params.getRouteId()).getData();
        if(Objects.isNull(route)){
            log.info("【排班计划】-排班线路信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班线路信息不存在");
        }
        List<RouteSta> routeStaList = aptsBaseApp.getRouteStaListByRouteId(params.getRouteId()).getData();
        if(CollectionUtils.isEmpty(routeStaList)){
            log.info("【排班计划】-排班线路站点信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班线路站点信息不存在");
        }

        RouteSta upFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta upLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_LAST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_LAST.getValue()))
                .collect(Collectors.toList()).get(0);

        if(Objects.isNull(params.getUpFristDate())){
            log.info("【排班计划】-上行首班时间不存在，routeId:{}",params.getRouteId());
            return R.error("上行首班时间不存在");
        }
        //获取上行周转时间
        Double upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.UP.getValue()),
                upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), params.getUpFristDate());
        //获取下行周转时间
        Double downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.DOWN.getValue()),
                downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), params.getDownFirstDate());
        Date upFirstDate = params.getUpFristDate();
        Date downFirstDate = params.getDownFirstDate();
        String upDirection = Convert.toStr(Direction.UP.getValue());
        String downDirection = Convert.toStr(Direction.DOWN.getValue());
        List<DySchedulePlanDriverless> scheduleList = new ArrayList<>();
        boolean isBreakUp = false;
        boolean isBreakDown = false;
        while (true){
            //上行首车到对面作尾车的发班时间
            Long lastDwonPlanTime = upFirstDate.getTime() + Convert.toLong(upFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //下行首车到对面作尾车的发班时间
            Long lastUpPlanTime = downFirstDate.getTime() + Convert.toLong(downFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //上行车排班间隔
            long upInterval = (lastUpPlanTime - upFirstDate.getTime())/params.getUpBusNum();
            //下行车排班间隔
            long downInterval = (lastDwonPlanTime - downFirstDate.getTime())/params.getDownBusNum();
            //上行车排班
            if(!isBreakUp){
                long upRemainTime = params.getUpLastDate().getTime()-(upFirstDate.getTime()+upInterval*(params.getUpBusNum()-1));
                if(upRemainTime/upInterval<=params.getDownBusNum()){
                    //尾轮排班
                    isBreakUp = true;
                    int upNum = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/upInterval).setScale(2, RoundingMode.DOWN).intValue();
                    upInterval = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/upNum).setScale(2, RoundingMode.UP).longValue();
                    for(int i=0;i<upNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(new Date()));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(upDirection);
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        if(i>=params.getUpBusNum()){
                            int startOrderNumber = i-params.getUpBusNum()+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                            if(i==upNum-1){
                                schedule.setPlanTime(params.getUpLastDate());
                            }
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                            }
                        }else {
                            int startOrderNumber = i+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                            }
                        }
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(upFullTime)*1000));

                        if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        }else {
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }
                        scheduleList.add(schedule);
                    }
                }else {
                    //正常排班
                    for(int i=0;i<params.getUpBusNum();i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(new Date()));
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                        int startOrderNumber = i+1;
                        schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(upFullTime)*1000));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(upDirection);
                        if(startOrderNumber>=10){
                            schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                        }else{
                            schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                        }
                        if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        }else {
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }
                        scheduleList.add(schedule);

                    }
                }


            }

            //下行车排班
            if(!isBreakDown){
                long downRemainTime = params.getDownLastDate().getTime()-(downFirstDate.getTime()+downInterval*(params.getDownBusNum()-1));
                if(downRemainTime/downInterval<=params.getUpBusNum()){
                    //尾轮排班
                    isBreakDown = true;
                    int downNum = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/downInterval).setScale(2, RoundingMode.DOWN).intValue();
                    downInterval = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/downNum).setScale(2, RoundingMode.UP).longValue();
                    for(int j=0;j<downNum;j++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(new Date()));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(downDirection);
                        schedule.setPlanTime(new Date(downFirstDate.getTime()+downInterval*j));
                        if(j>=params.getDownBusNum()){
                            int startOrderNumber = j-params.getDownBusNum()+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                            if(j==downNum-1){
                                schedule.setPlanTime(params.getDownLastDate());
                            }
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                            }
                        }else {
                            int startOrderNumber = j+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                            }
                        }
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(downFullTime)*1000));

                        if(downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))){
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }else {
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        }
                        scheduleList.add(schedule);
                    }
                }else{
                    //正常排班
                    for(int j=0;j<params.getDownBusNum();j++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(new Date()));
                        schedule.setPlanTime(new Date(downFirstDate.getTime()+downInterval*j));
                        schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                        int startOrderNumber = j+1;
                        schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(downFullTime)*1000));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(downDirection);
                        if(startOrderNumber>=10){
                            schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                        }else{
                            schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                        }
                        if(downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))){
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }else {
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        }
                        scheduleList.add(schedule);
                    }
                }

            }

            upFirstDate = new Date(lastDwonPlanTime);
            downFirstDate = new Date(lastUpPlanTime);
            upDirection = upDirection.equals(Convert.toStr(Direction.UP.getValue()))?Convert.toStr(Direction.DOWN.getValue()):Convert.toStr(Direction.UP.getValue());
            downDirection = downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))?Convert.toStr(Direction.UP.getValue()):Convert.toStr(Direction.DOWN.getValue());
            if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), upDirection,
                        upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), upFirstDate);
            }else {
                upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), upDirection,
                        downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), upFirstDate);
            }

            if(downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))){
                downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), downDirection,
                        downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), downFirstDate);
            }else {
                downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), downDirection,
                        upFirstRouteSta.getRouteStationId(), upFirstRouteSta.getRouteStationId(), downFirstDate);
            }
            //排班结束
            if(isBreakUp && isBreakDown){
                break;
            }
        }
        if(!CollectionUtils.isEmpty(scheduleList)){
            //插入排班计划
            dySchedulePlanDriverlessMapper.batchInsertSchedule(scheduleList);
        }

        return R.ok("操作成功!");
    }

    @Override
    @Transactional
    public R generateSupportSchedule(GenerateScheduleParams2 params2) {
        boolean isLoopRoute = BsData.isLoopRoute(params2.getRouteId());
        if(isLoopRoute && Objects.nonNull(params2.getBusNumberDown()) && params2.getBusNumberDown()>0){
            return R.error("换线不存在下行车辆，无法生成计划！");
        }
        long startTime = System.currentTimeMillis();
        Date preSevenDate = getPreSevenDate(params2);
        //设置默认客流和周转时间
        if(Objects.isNull(params2.getPassengerData())){
            params2.setPassengerData(preSevenDate);
        }
        if(Objects.isNull(params2.getTurnaroundData())){
            params2.setTurnaroundData(preSevenDate);
        }
        if(Objects.isNull(params2.getSupportPassengerData())){
            params2.setSupportPassengerData(preSevenDate);
        }
        if(Objects.isNull(params2.getSupportTurnaroundData())){
            params2.setSupportTurnaroundData(preSevenDate);
        }
        List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getPassengerData(),DateUtil.date_sdf),params2.getRouteId().toString());
        if(CollectionUtils.isEmpty(stationPassengerList)){
            log.info("【排班计划】-客流信息不存在，routeId:{}",params2.getRouteId());
            return R.error("客流信息不存在");
        }
        Integer maxPassengerNum = stationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                .collect(Collectors.toList()).get(0).getCurpeople();
//        Integer maxPassengerNum = 300;
        List<RouteWasteTime> routeWasteTimeList = null;
        GenerateScheduleParams params = getGenerateScheduleParams(params2,stationPassengerList,routeWasteTimeList);
        if(Objects.isNull(params)){
            log.info("【排班计划】-排班参数信息查询失败，routeId:{}",params2.getRouteId());
            return R.error("排班参数信息查询失败");
        }

        Route route = dispatchApp.getRouteById(params.getRouteId()).getData();
        //Route route = routeService.getRouteByRouteId(params.getRouteId());
        if(Objects.isNull(route)){
            log.info("【排班计划】-排班线路信息不存在，routeId:{}",params2.getRouteId());
            return R.error("排班线路信息不存在");
        }
        List<RouteSta> routeStaList = aptsBaseApp.getRouteStaListByRouteId(params.getRouteId()).getData();
        if(CollectionUtils.isEmpty(routeStaList)){
            log.info("【排班计划】-排班线路站点信息不存在，routeId:{}",params2.getRouteId());
            return R.error("排班线路站点信息不存在");
        }
        //测试支援线路
        //params.setSupportRouteId(420l);
        DyDriverlessConfig config = getDyDriverlessConfig(params.getRouteId(),params.getSupportRouteId(),Objects.isNull(params.getSupportRouteId())?1:null);
        if(Objects.isNull(config)){
            log.info("【排班计划】-排班线路配置信息不存在，routeId:{}",params2.getRouteId());
            return R.error("排班线路配置信息不存在");
        }

        Integer supportBusNum = BigDecimal.valueOf(Convert.toDouble(maxPassengerNum-params.getPassengerNum())/config.getSupportPassengerNum()).setScale(0,BigDecimal.ROUND_UP).intValue();
        supportBusNum = supportBusNum>0?supportBusNum:0;
        //判断常规线混编是否互相支援
        boolean supportEachOther = true;
        if(supportBusNum<0&&config.getIsDriverless().equals(0)){
            List<StationPassenger> supportStationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getPassengerData(),DateUtil.date_sdf),config.getSupportRouteId().toString());
            if(CollectionUtils.isEmpty(supportStationPassengerList)){
                log.info("【排班计划】-支援线客流信息不存在，routeId:{}",params2.getRouteId());
                return R.error("支援线客流信息不存在");
            }
            Integer supportMaxPassengerNum = supportStationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                    .collect(Collectors.toList()).get(0).getCurpeople();
            GenerateScheduleParams2 reduceParams2 = new GenerateScheduleParams2();
            reduceParams2.setRouteId(config.getSupportRouteId());
            reduceParams2.setRunDate(params2.getRunDate());
            reduceParams2.setPassengerData(params2.getPassengerData());
            GenerateScheduleParams reduceParamsTemp = getGenerateScheduleParams(reduceParams2,null,null);
            if(supportMaxPassengerNum < reduceParamsTemp.getPassengerNum()){
                supportEachOther = false;
            }
        }
        List<DySchedulePlanDriverless> scheduleList = null;
        List<DyDriverlessPlanTime> driverlessPlanTimeList = new ArrayList<>();
        GenerateScheduleParams reduceParams = null;
        if(config.getIsDriverless().equals(1)){
            //无人车支援排班
            List<BsRouteSub> bsRouteSubsList = routeSubService.getListByRouteId(params.getRouteId());
            if(CollectionUtils.isEmpty(bsRouteSubsList)){
                log.info("【排班计划】-排班子线路信息不存在，routeId:{}",params.getRouteId());
                return R.error("排班子线路信息不存在");
            }
            //上行无人车支援
            Integer upDriverlessBusNum;
            //下行无人车支援
            Integer downDriverlessBusNum;
            if(isLoopRoute){
                upDriverlessBusNum = supportBusNum;
                downDriverlessBusNum = 0;
            }else {
                if(params.getUpBusNum()>params.getDownBusNum()){
                    upDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                    downDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                }else {
                    upDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                    downDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                }
            }

            //todo 获取无人车排班空闲时间
            DyDriverlessPlanTime planTimeRecord = new DyDriverlessPlanTime();
            planTimeRecord.setRunDate(params.getRunDate());
            planTimeRecord.setRouteId(config.getSupportRouteId());
            List<DyDriverlessPlanTime> driverlessPlanPreList = dyDriverlessPlanTimeMapper.getDriverlessPlanPreList(planTimeRecord);
            if(CollectionUtils.isEmpty(driverlessPlanPreList)){
                return R.error("不存在可支援的无人车！");
            }
            Map<Long,List<DyDriverlessPlanTime>> driverlessPlanPreMap = driverlessPlanPreList.stream().collect(Collectors.groupingBy(DyDriverlessPlanTime::getBusId));
            List<DriverlessFreeParams> driverlessFreeList = new ArrayList<>();
            for(Long key : driverlessPlanPreMap.keySet()){
                List<DyDriverlessPlanTime> busPlanPreList = driverlessPlanPreMap.get(key);
                busPlanPreList.sort(Comparator.comparing(DyDriverlessPlanTime::getTripBeginTime));
                BsBus bsBus = BsData.getBsBus(key);
                for(int i=0;i<busPlanPreList.size();i++){
                    DriverlessFreeParams freeParams = new DriverlessFreeParams();
                    if(i==0){
                        freeParams.setBeginFreeTime(params.getRunDate());
                        freeParams.setEndFreeTime(busPlanPreList.get(i).getTripBeginTime());
                        freeParams.setBusId(busPlanPreList.get(i).getBusId());
                        freeParams.setBusName(bsBus.getBusName());
                        if(busPlanPreList.size()>1){
                            DriverlessFreeParams freeParams02 = new DriverlessFreeParams();
                            freeParams02.setBeginFreeTime(busPlanPreList.get(i).getTripEndTime());
                            freeParams02.setEndFreeTime(busPlanPreList.get(i+1).getTripBeginTime());
                            freeParams02.setBusId(busPlanPreList.get(i).getBusId());
                            freeParams02.setBusName(bsBus.getBusName());
                            driverlessFreeList.add(freeParams02);
                        }
                    }else if(i==busPlanPreList.size()-1){
                        freeParams.setBeginFreeTime(busPlanPreList.get(i).getTripEndTime());
                        freeParams.setEndFreeTime(DateUtil.getDateAddDay(params.getRunDate(),1));
                        freeParams.setBusId(busPlanPreList.get(i).getBusId());
                        freeParams.setBusName(bsBus.getBusName());
                    }else {
                        freeParams.setBeginFreeTime(busPlanPreList.get(i).getTripEndTime());
                        freeParams.setEndFreeTime(busPlanPreList.get(i+1).getTripBeginTime());
                        freeParams.setBusId(busPlanPreList.get(i).getBusId());
                        freeParams.setBusName(bsBus.getBusName());
                    }
                    driverlessFreeList.add(freeParams);
                }
            }
            /*测试数据-开始*/
            /*DriverlessFreeParams freeParams01 = new DriverlessFreeParams();
            freeParams01.setBusName("1001");
            freeParams01.setBusId(1001l);
            freeParams01.setBeginFreeTime(DateUtil.str2Date("2023-11-07 07:00:00",DateUtil.time_sdf));
            freeParams01.setEndFreeTime(DateUtil.str2Date("2023-11-07 09:00:00",DateUtil.time_sdf));
            DriverlessFreeParams freeParams02 = new DriverlessFreeParams();
            freeParams02.setBusName("1002");
            freeParams02.setBusId(1002l);
            freeParams02.setBeginFreeTime(DateUtil.str2Date("2023-11-07 07:00:00",DateUtil.time_sdf));
            freeParams02.setEndFreeTime(DateUtil.str2Date("2023-11-07 09:00:00",DateUtil.time_sdf));
            driverlessFreeList.add(freeParams01);
            driverlessFreeList.add(freeParams02);*/
            /*测试数据-结束*/
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upDriverlessBusNum,downDriverlessBusNum,driverlessFreeList, ScheduleStatus.DRIVERLESS_SCHEDULE.getValue(),config,0,driverlessPlanTimeList,bsRouteSubsList,routeWasteTimeList);
            params.setSupportRouteId(null);
            List<DySchedulePlanDriverless> commonScheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,null,null,null,ScheduleStatus.COMMON_SCHEDULE.getValue(),null,0,driverlessPlanTimeList,null,routeWasteTimeList);
            if(!CollectionUtils.isEmpty(scheduleList)&&!CollectionUtils.isEmpty(commonScheduleList)){
                scheduleList.addAll(commonScheduleList);
            }
        }else if(config.getIsDriverless().equals(0)){
            //常规线支援排班
            //上行支援车
            Integer upSupportBusNum;
            //下行支援车
            Integer downSupportBusNum;
            if(params.getUpBusNum()>params.getDownBusNum()){
                upSupportBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downSupportBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                upSupportBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                downSupportBusNum = BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }
            long mainStartTime = System.currentTimeMillis();
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upSupportBusNum,downSupportBusNum,null,ScheduleStatus.SUPPORTED_SCHEDULE.getValue(),config,0,driverlessPlanTimeList,null,routeWasteTimeList);
            log.info("【排班计划】-主线路生成计划耗时:{},routeId:{}",(System.currentTimeMillis()-mainStartTime)/1000,params.getRouteId());
            //减车参数
            GenerateScheduleParams2 reduceParams2 = new GenerateScheduleParams2();
            reduceParams2.setRouteId(config.getSupportRouteId());
            reduceParams2.setSupportRouteId(config.getRouteId());
            reduceParams2.setRunDate(params2.getRunDate());
            reduceParams2.setPassengerData(params2.getSupportPassengerData());
            reduceParams2.setTurnaroundData(params2.getSupportTurnaroundData());
            reduceParams2.setTemplateId(params2.getSupportTemplateId());
            reduceParams2.setPlanType(params2.getPlanType());
            reduceParams2.setBusNumberDown(params2.getSupportBusNumberDown());
            reduceParams2.setBusNumberUp(params2.getSupportBusNumberUp());
            reduceParams2.setSingleBusUp(params2.getSupportSingleBusUp());
            reduceParams2.setSingleBusDwon(params2.getSupportSingleBusDwon());
            reduceParams2.setEarlyHalfBusUp(params2.getSupportEarlyHalfBusUp());
            reduceParams2.setEarlyHalfBusDown(params2.getSupportEarlyHalfBusDown());
            reduceParams2.setLateHalfBusUp(params2.getSupportLateHalfBusUp());
            reduceParams2.setLateHalfBusDown(params2.getSupportLateHalfBusDown());
            reduceParams2.setMiddleBusUp(params2.getSupportMiddleBusUp());
            reduceParams2.setMiddleBusDown(params2.getSupportMiddleBusDown());
            reduceParams2.setDoubleStopBusUp(params2.getSupportDoubleStopBusUp());
            reduceParams2.setDoubleStopBusDown(params2.getSupportDoubleStopBusDown());
            List<StationPassenger> supportStationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getSupportPassengerData(),DateUtil.date_sdf),params2.getSupportRouteId().toString());
            if(CollectionUtils.isEmpty(supportStationPassengerList)){
                log.info("【排班计划】-支援车客流信息不存在，routeId:{}",params2.getSupportRouteId());
                return R.error("支援车客流信息不存在");
            }
            List<RouteWasteTime> reduceRouteWasteTimeList = null;
            reduceParams = getGenerateScheduleParams(reduceParams2,supportStationPassengerList,reduceRouteWasteTimeList);
            //上行减车
            Integer upReduceBusNum;
            //下行减车
            Integer downReduceBusNum;
            if(reduceParams.getUpBusNum() > reduceParams.getDownBusNum()){
                upReduceBusNum = -BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                downReduceBusNum = -BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }else {
                upReduceBusNum = -BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downReduceBusNum = -BigDecimal.valueOf(supportBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
            }
            Route supportRoute = dispatchApp.getRouteById(reduceParams.getRouteId()).getData();
            List<RouteSta> supportRouteStaList = aptsBaseApp.getRouteStaListByRouteId(reduceParams.getRouteId()).getData();
            if(CollectionUtils.isEmpty(supportRouteStaList)){
                log.info("【排班计划】-排班支援线路站点信息不存在，routeId:{}",reduceParams.getRouteId());
                return R.error("排班支援线路站点信息不存在");
            }
            //支援排班
            long subStartTime = System.currentTimeMillis();
            List<DySchedulePlanDriverless> supportScheduleList = getDySchedulePlanDriverlesses(reduceParams, supportRoute, supportRouteStaList,upReduceBusNum,downReduceBusNum,null,ScheduleStatus.SUPPORT_SCHEDULE.getValue(),config,1,driverlessPlanTimeList,null,reduceRouteWasteTimeList);
            log.info("【排班计划】-子线路生成计划耗时:{},routeId:{}",(System.currentTimeMillis()-subStartTime)/1000,params.getRouteId());
            if(!CollectionUtils.isEmpty(supportScheduleList)&&!CollectionUtils.isEmpty(scheduleList)){
                scheduleList.addAll(supportScheduleList);
            }

        }else {
            //常规线排班
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,null,null,null,ScheduleStatus.COMMON_SCHEDULE.getValue(),null,0,driverlessPlanTimeList,null,routeWasteTimeList);
        }


        if(!CollectionUtils.isEmpty(scheduleList)){
            DySchedulePlanDriverless record = new DySchedulePlanDriverless();
            record.setRouteId(params2.getRouteId());
            record.setPlanDate(params2.getRunDate());
            record.setPlanType(params2.getPlanType());
            List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
            if(!CollectionUtils.isEmpty(driverlessList) && Objects.nonNull(params2.getSupportRouteId())){
                record.setRouteId(params2.getSupportRouteId());
                List<DySchedulePlanDriverless> supportDriverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
                if(!CollectionUtils.isEmpty(supportDriverlessList)){
                    driverlessList.addAll(supportDriverlessList);
                }
            }
            long deleteStartTime = System.currentTimeMillis();
            if(!CollectionUtils.isEmpty(driverlessList)){
                //删除旧的计划
                List<Long> ids =  driverlessList.stream().map(DySchedulePlanDriverless::getScheduleId).collect(Collectors.toList());
                int limit = 100;
                if(ids.size()>limit){
                    int part = ids.size()/limit;
                    List<Long> partList;
                    for(int i =0;i<part;i++){
                        partList = ids.subList(0,limit);
                        dySchedulePlanDriverlessMapper.batchDelete(partList);
                        partList.clear();
                    }
                    if(!ids.isEmpty()){
                        dySchedulePlanDriverlessMapper.batchDelete(ids);
                    }
                }else {
                    dySchedulePlanDriverlessMapper.batchDelete(ids);
                }

            }
            log.info("【排班计划】-删除旧线路计划耗时:{},routeId:{}",(System.currentTimeMillis()-deleteStartTime)/1000,params.getRouteId());
            //插入排班计划
            long generateStartTime = System.currentTimeMillis();
            dySchedulePlanDriverlessMapper.batchInsertSchedule(scheduleList);
            log.info("【排班计划】-生成计划耗时:{},routeId:{}",(System.currentTimeMillis()-generateStartTime)/1000,params.getRouteId());

        }
        if(!CollectionUtils.isEmpty(driverlessPlanTimeList)){
            DyDriverlessPlanTime driverlessPlan = new DyDriverlessPlanTime();
            driverlessPlan.setRouteId(params2.getRouteId());
            driverlessPlan.setRunDate(params2.getRunDate());
            List<DyDriverlessPlanTime> driverlessPlanList = dyDriverlessPlanTimeMapper.getDriverlessPlanList(driverlessPlan);
            if(!CollectionUtils.isEmpty(driverlessPlanList)){
                //删除旧的无人车计划
                List<Long> ids =  driverlessPlanList.stream().map(DyDriverlessPlanTime::getId).collect(Collectors.toList());
                int limit = 100;
                if(ids.size()>limit){
                    int part = ids.size()/limit;
                    List<Long> partList;
                    for(int i =0;i<part;i++){
                        partList = ids.subList(0,limit);
                        dyDriverlessPlanTimeMapper.batchDelete(partList);
                        partList.clear();
                    }
                    if(!ids.isEmpty()){
                        dyDriverlessPlanTimeMapper.batchDelete(ids);
                    }
                }else {
                    dyDriverlessPlanTimeMapper.batchDelete(ids);
                }

            }
            //插入无人车排班计划
            dyDriverlessPlanTimeMapper.batchInsert(driverlessPlanTimeList);
        }
        //插入车辆配置信息
        insertDriverlessBusConfig(params2, params, reduceParams);

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("mainRouteInfo",params);
        resultMap.put("subRouteInfo",reduceParams);

        log.info("【排班计划】-生成计划总耗时:{},routeId:{}",(System.currentTimeMillis()-startTime)/1000,params.getRouteId());
        return R.ok("操作成功!").put("data",resultMap);
    }

    private Date getPreSevenDate(GenerateScheduleParams2 params2) {
        // 获取当前日期前2天
        Date preTwoDate = DateUtil.getDailyStartDate(DateUtil.getDateAddDay(new Date(),-2));
        // 获取营运日期的前i*7天
        Date preSevenDate;
        int i = 0;
        while(true){
            i++;
            preSevenDate = DateUtil.getDateAddDay(params2.getRunDate(),-7*i);
            if(preSevenDate.getTime()<=preTwoDate.getTime()){
                break;
            }
        }
        return preSevenDate;
    }

    private void insertDriverlessBusConfig(GenerateScheduleParams2 params2, GenerateScheduleParams params, GenerateScheduleParams reduceParams) {
        //查询配置是否存在，存在则更新，不存在则新增
        DyDriverlessBusConfig busConfig = new DyDriverlessBusConfig();
        busConfig.setRouteId(params2.getRouteId());
        busConfig.setSupportRouteId(params2.getSupportRouteId());
        busConfig.setRunDate(params2.getRunDate());
        busConfig.setType(params2.getPlanType());
        busConfig.setTemplateId(Convert.toLong(params2.getTemplateId()));
        busConfig.setSupportTemplateId(Convert.toLong(params2.getSupportTemplateId()));
        busConfig.setMainUpBusNum(params.getUpBusNum());
        busConfig.setMainDownBusNum(params.getDownBusNum());
        busConfig.setMainSingleBusUp(params.getSingleBusUp());
        busConfig.setMainSingleBusDown(params.getSingleBusDwon());
        busConfig.setMainEarlyHalfBusUp(params.getEarlyHalfBusUp());
        busConfig.setMainEarlyHalfBusDown(params.getEarlyHalfBusDown());
        busConfig.setMainLateHalfBusUp(params.getLateHalfBusUp());
        busConfig.setMainLateHalfBusDown(params.getLateHalfBusDown());
        busConfig.setMainMiddleBusUp(params.getMiddleBusUp());
        busConfig.setMainMiddleBusDown(params.getMiddleBusDown());
        busConfig.setMainDoubleStopBusUp(params.getDoubleStopBusUp());
        busConfig.setMainDoubleStopBusDown(params.getDoubleStopBusDown());
        if(Objects.nonNull(reduceParams)){
            busConfig.setSubUpBusNum(reduceParams.getUpBusNum());
            busConfig.setSubDownBusNum(reduceParams.getDownBusNum());
            busConfig.setSubSingleBusUp(reduceParams.getSingleBusUp());
            busConfig.setSubSingleBusDown(reduceParams.getSingleBusDwon());
            busConfig.setSubEarlyHalfBusUp(reduceParams.getEarlyHalfBusUp());
            busConfig.setSubEarlyHalfBusDown(reduceParams.getEarlyHalfBusDown());
            busConfig.setSubLateHalfBusUp(reduceParams.getLateHalfBusUp());
            busConfig.setSubLateHalfBusDown(reduceParams.getLateHalfBusDown());
            busConfig.setSubMiddleBusUp(reduceParams.getMiddleBusUp());
            busConfig.setSubMiddleBusDown(reduceParams.getMiddleBusDown());
            busConfig.setSubDoubleStopBusUp(reduceParams.getDoubleStopBusUp());
            busConfig.setSubDoubleStopBusDown(reduceParams.getDoubleStopBusDown());
        }
        List<DyDriverlessBusConfig> busConfigList = dyDriverlessBusConfigMapper.getBusConfigList(busConfig);
        if(!CollectionUtils.isEmpty(busConfigList)){
            busConfig.setId(busConfigList.get(0).getId());
            dyDriverlessBusConfigMapper.updateByPrimaryKey(busConfig);
            return;
        }
        dyDriverlessBusConfigMapper.insert(busConfig);
    }

    @Override
    public R getScheduleBySort(ScheduleBySortParam params) {

        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(params.getRouteId());
        record.setPlanDate(params.getRunDate());
        record.setSupportRouteId(params.getSupportRouteId());
        record.setPlanType(params.getPlanType());
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList(record);
        if(CollectionUtils.isEmpty(driverlessList)){
            log.info("排班计划信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班计划信息不存在");
        }
        if(Objects.isNull(params.getSupportRouteId())){
            List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> ScheduleStatus.DRIVERLESS_SCHEDULE.getValue()==e.getStatus()).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(finalDriverlessList)){
                log.info("无人车排班计划信息不存在，routeId:{}",params.getRouteId());
            }else {
                driverlessList = finalDriverlessList;
            }
        }
        //根据班次分组
        Map<Integer,List<DySchedulePlanDriverless>> driverlessMap = driverlessList.stream().collect(Collectors.groupingBy(DySchedulePlanDriverless::getClasses));
        ScheduleBySortInfo scheduleBySortInfo = new ScheduleBySortInfo();

        boolean isLoopRoute = BsData.isLoopRoute(params.getRouteId());
        //上行计划
        Map<Short,List<DySchedulePlanDriverless>> upScheduleMap = driverlessList.stream().filter(e -> e.getStartDirection().equals("0")).collect(Collectors.groupingBy(DySchedulePlanDriverless::getStartOrderNumber));
        List<ScheduleBusInfo> scheduleBusList = new ArrayList<>();
        for(int i=0;i < upScheduleMap.size();i++){
            ScheduleBusInfo scheduleBusInfo = new ScheduleBusInfo();
            scheduleBusInfo.setFirstBusNumber(i+1);
            scheduleBusInfo.setFirstDirection("0");
            scheduleBusInfo.setBusNameFull("上行第"+(i+1)+"号车");
            List<ScheduleInfo> scheduleList = new ArrayList<>();
            List<DySchedulePlanDriverless> upScheduleList = upScheduleMap.get(Convert.toShort(i+1));
            upScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getTimeStamp));
            if(!isLoopRoute){
                ScheduleInfo emptyScheduleInfo = new ScheduleInfo();
                emptyScheduleInfo.setServiceType(upScheduleList.get(0).getServiceType());
                emptyScheduleInfo.setServiceName(upScheduleList.get(0).getServiceName());
                emptyScheduleInfo.setDirection(Convert.toInt(upScheduleList.get(0).getDirection().equals("0")?"1":"0"));
                emptyScheduleInfo.setFirstRouteStaId(upScheduleList.get(0).getLastRouteStaId());
                emptyScheduleInfo.setLastRouteStaId(upScheduleList.get(0).getFirstRouteStaId());
                emptyScheduleInfo.setFirstRouteStaName(upScheduleList.get(0).getLastRouteStaName());
                emptyScheduleInfo.setLastRouteStaName(upScheduleList.get(0).getFirstRouteStaName());
                scheduleList.add(emptyScheduleInfo);
            }

            List<DySchedulePlanDriverless> upScheduleList02 = upScheduleList.stream().filter(e -> Objects.nonNull(e.getPlanTime())).collect(Collectors.toList());
            upScheduleList02.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
            //行车时间
            Long tripTime = 0l;
            for(DySchedulePlanDriverless schedule : upScheduleList){
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setScheduleId(schedule.getScheduleId());
                scheduleInfo.setServiceType(schedule.getServiceType());
                scheduleInfo.setServiceName(schedule.getServiceName());
                scheduleInfo.setDirection(Convert.toInt(schedule.getDirection()));
                scheduleInfo.setFirstRouteStaId(schedule.getFirstRouteStaId());
                scheduleInfo.setLastRouteStaId(schedule.getLastRouteStaId());
                scheduleInfo.setFirstRouteStaName(schedule.getFirstRouteStaName());
                scheduleInfo.setLastRouteStaName(schedule.getLastRouteStaName());
                scheduleInfo.setTripBeginTime(schedule.getPlanTime());
                scheduleInfo.setTripEndTime(schedule.getTripEndTime());
                scheduleList.add(scheduleInfo);
                if(Objects.nonNull(schedule.getPlanTime())&&Objects.nonNull(schedule.getTripEndTime())){
                    tripTime += schedule.getTripEndTime().getTime() - schedule.getPlanTime().getTime();
                }
            }
            //营运时间
            Long runTime = 0l;
            if(!CollectionUtils.isEmpty(upScheduleList02)){
                runTime = upScheduleList02.get(upScheduleList02.size()-1).getTripEndTime().getTime() - upScheduleList02.get(0).getPlanTime().getTime();
                scheduleBusInfo.setBusId(upScheduleList02.get(0).getBusId());
                scheduleBusInfo.setBusName(upScheduleList02.get(0).getBusName());
            }

            Double totalTripTime = BigDecimal.valueOf(tripTime/60000d).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            Double totalRunTime = BigDecimal.valueOf(runTime/60000d).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            scheduleBusInfo.setTotalRunTime(totalRunTime);
            scheduleBusInfo.setTotalTripTime(totalTripTime);
            scheduleBusInfo.setScheduleList(scheduleList);
            scheduleBusList.add(scheduleBusInfo);
        }
        //下行计划
        Map<Short,List<DySchedulePlanDriverless>> downScheduleMap = driverlessList.stream().filter(e -> e.getStartDirection().equals("1")).collect(Collectors.groupingBy(DySchedulePlanDriverless::getStartOrderNumber));
        for(int i=0;i < downScheduleMap.size();i++){
            ScheduleBusInfo scheduleBusInfo = new ScheduleBusInfo();
            scheduleBusInfo.setFirstBusNumber(i+1);
            scheduleBusInfo.setFirstDirection("1");
            scheduleBusInfo.setBusNameFull("下行第"+(i+1)+"号车");
            List<ScheduleInfo> scheduleList = new ArrayList<>();
            List<DySchedulePlanDriverless> downScheduleList = downScheduleMap.get(Convert.toShort(i+1));
            downScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getTimeStamp));
            List<DySchedulePlanDriverless> downScheduleList02 = downScheduleList.stream().filter(e -> Objects.nonNull(e.getPlanTime())).collect(Collectors.toList());
            downScheduleList02.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
            //行车时间
            Long tripTime = 0l;
            for(DySchedulePlanDriverless schedule : downScheduleList){
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setScheduleId(schedule.getScheduleId());
                scheduleInfo.setServiceType(schedule.getServiceType());
                scheduleInfo.setServiceName(schedule.getServiceName());
                scheduleInfo.setDirection(Convert.toInt(schedule.getDirection()));
                scheduleInfo.setFirstRouteStaId(schedule.getFirstRouteStaId());
                scheduleInfo.setLastRouteStaId(schedule.getLastRouteStaId());
                scheduleInfo.setFirstRouteStaName(schedule.getFirstRouteStaName());
                scheduleInfo.setLastRouteStaName(schedule.getLastRouteStaName());
                scheduleInfo.setTripBeginTime(schedule.getPlanTime());
                scheduleInfo.setTripEndTime(schedule.getTripEndTime());
                scheduleList.add(scheduleInfo);
                if(Objects.nonNull(schedule.getPlanTime())&&Objects.nonNull(schedule.getTripEndTime())){
                    tripTime += schedule.getTripEndTime().getTime() - schedule.getPlanTime().getTime();
                }

            }
            //营运时间
            Long runTime = 0l;
            if(!CollectionUtils.isEmpty(downScheduleList02)){
                runTime = downScheduleList02.get(downScheduleList02.size()-1).getTripEndTime().getTime() - downScheduleList02.get(0).getPlanTime().getTime();
                scheduleBusInfo.setBusId(downScheduleList02.get(0).getBusId());
                scheduleBusInfo.setBusName(downScheduleList02.get(0).getBusName());
            }
            Double totalTripTime = BigDecimal.valueOf(tripTime/60000d).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            Double totalRunTime = BigDecimal.valueOf(runTime/60000d).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            scheduleBusInfo.setTotalRunTime(totalRunTime);
            scheduleBusInfo.setTotalTripTime(totalTripTime);
            scheduleBusInfo.setScheduleList(scheduleList);
            scheduleBusList.add(scheduleBusInfo);
        }

        //根据上下行1号车的排班计划的首末班时间确定站点信息列表 todo
        List<FirstRouteStaInfo> firstRouteStaList = new ArrayList<>();
        //按班次遍历排班计划
        List<Integer> keys = new ArrayList<>(driverlessMap.keySet());
        Collections.sort(keys);
        List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(driverlessList.get(0).getPassengerData(),DateUtil.date_sdf),params.getRouteId().toString());
        for(Integer key : keys){
            FirstRouteStaInfo lastStaInfo = new FirstRouteStaInfo();
            List<DySchedulePlanDriverless> list = driverlessMap.get(key).stream().filter(e -> Objects.nonNull(e.getPlanTime())).sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
            List<DySchedulePlanDriverless> supportDriverlessList = list.stream().filter(e -> Objects.nonNull(e.getSupportClasses()) && !e.getSupportClasses().equals(0)).collect(Collectors.toList());
            List<DySchedulePlanDriverless> upDriverlessList = list.stream().filter(e -> e.getStartDirection().equals("0")).collect(Collectors.toList());
            List<DySchedulePlanDriverless> downDriverlessList = list.stream().filter(e -> e.getStartDirection().equals("1")).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(supportDriverlessList)){
                lastStaInfo.setSupportClasses(supportDriverlessList.size());
            }
            lastStaInfo.setBusClasses(list.get(0).getClasses());
            lastStaInfo.setClasses(list.size());
            lastStaInfo.setDirection(Convert.toInt(list.get(0).getDirection()));
            lastStaInfo.setRouteStationId(list.get(0).getFirstRouteStaId());
            lastStaInfo.setRouteStationName(list.get(0).getFirstRouteStaName());
            lastStaInfo.setBeginTime(DateUtil.date2Str(list.get(0).getPlanTime(),DateUtil.hh_mm));
            if(list.size()>1){
                lastStaInfo.setEndTime(DateUtil.date2Str(list.get(list.size()-1).getPlanTime(),DateUtil.hh_mm));
            }else {
                lastStaInfo.setEndTime(DateUtil.date2Str(list.get(0).getTripEndTime(),DateUtil.hh_mm));
            }

            if(!CollectionUtils.isEmpty(upDriverlessList)){
                lastStaInfo.setUpInterval(upDriverlessList.get(0).getInterval());
            }
            if(!CollectionUtils.isEmpty(downDriverlessList)){
                lastStaInfo.setDownInterval(downDriverlessList.get(0).getInterval());
            }
            //停站率
            Long stopTime = list.stream().mapToInt(DySchedulePlanDriverless::getStopTime).sum()*60*1000l;
            Long runTime = 0l;
            for(DySchedulePlanDriverless driverless:list){
                runTime +=driverless.getTripEndTime().getTime()-driverless.getPlanTime().getTime();
            }
            Integer parkPercent = BigDecimal.valueOf(stopTime*100).divide(BigDecimal.valueOf(runTime),BigDecimal.ROUND_HALF_UP).intValue();
            lastStaInfo.setParkPercent(parkPercent);

            //满载率  时段内最高断面站点车内总人数/途经的班次数/班次核载总人数
            //途经的班次数
            Integer classes = list.size();
            //班次核载总人数
            Integer passengerNum = list.get(0).getPassengerNum();
            //客流参考日期
            Date passengerData = list.get(0).getPassengerData();
            //计划开始时间
            Date startPlanTime = list.get(0).getPlanTime();
            Integer startTimeNum = Convert.toInt(DateUtil.date2Str(startPlanTime,DateUtil.hhmm));
            //计划结束时间
            Date endPlanTime = list.get(list.size()-1).getPlanTime();
            Integer endTimeNum = Convert.toInt(DateUtil.date2Str(endPlanTime,DateUtil.hhmm));
            //找出时段最多的客流

            if(CollectionUtils.isEmpty(stationPassengerList)){
                log.info("【排班计划查询】-客流信息不存在，routeId:{},classes:{}",params.getRouteId(),list.get(0).getClasses());
                lastStaInfo.setFullPercent(0);
                firstRouteStaList.add(lastStaInfo);
                continue;
            }
            List<StationPassenger> finalStationPassengerList = stationPassengerList.stream().filter(e -> Convert.toInt(e.getPhour()+e.getPminute().split("-")[0])>=startTimeNum&&Convert.toInt(e.getPhour()+e.getPminute().split("-")[1])<=endTimeNum).collect(Collectors.toList());
            //Map<Long,Integer> stationPassengerNumMap = finalStationPassengerList.stream().collect(Collectors.toMap(StationPassenger::getStationId, entry ->entry.getCurpeople()));
            //Optional<Entry<Long, Integer>> max0 = stationPassengerNumMap.entrySet().stream().max(Entry.comparingByValue());
            IntSummaryStatistics maxPassenger = finalStationPassengerList.stream().collect(Collectors.summarizingInt(e -> e.getCurpeople()));
            Integer fullPercent = BigDecimal.valueOf(maxPassenger.getMax()*100).divide(BigDecimal.valueOf(classes * passengerNum),BigDecimal.ROUND_HALF_UP).intValue();
            log.info("满载率统计，routeId:{},fullPercent:{},maxPassenger:{},finalStationPassengerList:{}",params.getRouteId(),fullPercent,JSONObject.toJSONString(maxPassenger),JSONObject.toJSONString(finalStationPassengerList));

//            Integer fullPercent = BigDecimal.valueOf(100*10*100).divide(BigDecimal.valueOf(classes * passengerNum),BigDecimal.ROUND_HALF_UP).intValue();
            lastStaInfo.setFullPercent(fullPercent);
            firstRouteStaList.add(lastStaInfo);
        }

        scheduleBySortInfo.setRouteId(params.getRouteId());
        scheduleBySortInfo.setFirstRouteStaList(firstRouteStaList);
        scheduleBySortInfo.setScheduleBusList(scheduleBusList);
        return R.ok().put("data",scheduleBySortInfo);
    }

    @Override
    public R getBusConfig(GenerateScheduleParams2 params) {
        DyDriverlessBusConfig busConfig = new DyDriverlessBusConfig();
        busConfig.setRouteId(params.getRouteId());
        busConfig.setSupportRouteId(params.getSupportRouteId());
        busConfig.setRunDate(params.getRunDate());
        busConfig.setType(params.getPlanType());
        List<DyDriverlessBusConfig> busConfigList = dyDriverlessBusConfigMapper.getBusConfigList(busConfig);
        if(CollectionUtils.isEmpty(busConfigList)){
            log.info("计划不存在，routeId:{}",params.getRouteId());
            return R.error("计划不存在");
        }
        List<DyDriverlessBusConfig> optimalBusConfigList = busConfigList.stream().filter(e -> e.getType().equals(1)).collect(Collectors.toList());
        List<DyDriverlessBusConfig> presetsBusConfigList = busConfigList.stream().filter(e -> e.getType().equals(2)).collect(Collectors.toList());
        List<Integer> mainShifts = scheduleParamsSingleService.getShiftsTypeByRouteIdAndPlanDate(Convert.toInt(params.getRouteId()), params.getRunDate(),params.getTemplateId());
        List<Integer> subShifts = null;
        if(Objects.nonNull(params.getSupportRouteId())&&Objects.nonNull(params.getSupportTemplateId())){
            subShifts = scheduleParamsSingleService.getShiftsTypeByRouteIdAndPlanDate(Convert.toInt(params.getSupportRouteId()), params.getRunDate(),params.getSupportTemplateId());
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("optimalInfo",CollectionUtils.isEmpty(optimalBusConfigList)?"":optimalBusConfigList.get(0));
        resultMap.put("presetsInfo",CollectionUtils.isEmpty(presetsBusConfigList)?"":presetsBusConfigList.get(0));
        resultMap.put("mainShifts",CollectionUtils.isEmpty(mainShifts)?null:mainShifts);
        resultMap.put("subShifts",CollectionUtils.isEmpty(subShifts)?null:subShifts);
        return R.ok("操作成功").put("data",resultMap);
    }

    /**
     *
     * @param params
     * @param route
     * @param routeStaList
     * @param upSupportBusNum
     * @param downSupportBusNum
     * @param driverlessFreeList
     * @param status
     * @param config
     * @param scheduleType 排班类型，0：排主线路，1：排子线路
     * @param driverlessPlanTimeList
     * @param bsRouteSubList
     * @return
     */
    private List<DySchedulePlanDriverless> getDySchedulePlanDriverlesses(GenerateScheduleParams params, Route route, List<RouteSta> routeStaList,Integer upSupportBusNum,Integer downSupportBusNum,List<DriverlessFreeParams> driverlessFreeList
            ,int status,DyDriverlessConfig config,Integer scheduleType,List<DyDriverlessPlanTime> driverlessPlanTimeList,List<BsRouteSub> bsRouteSubList,List<RouteWasteTime> routeWasteTimeList) {
        RouteSta upFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta upLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_LAST.getValue()))
                .collect(Collectors.toList()).get(0);
        //获取上行周转时间
        Double upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.UP.getValue()),
                upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), params.getUpFristDate());
        RouteSta downFirstRouteSta = null;
        RouteSta downLastRouteSta = null;
        Double downFullTime = 0d;

        //todo 优化周转时间的统计
        if(!CollectionUtils.isEmpty(routeWasteTimeList)){
            List<RouteWasteTime> upRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("0")).collect(Collectors.toList());
            List<RouteWasteTime> downRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("1")).collect(Collectors.toList());

        }
        boolean isLoopRoute = BsData.isLoopRoute(params.getRouteId());
        if(!isLoopRoute){
            downFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_FIRST.getValue()))
                    .collect(Collectors.toList()).get(0);
            downLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_LAST.getValue()))
                    .collect(Collectors.toList()).get(0);
            //获取下行周转时间
            downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.DOWN.getValue()),
                    downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), params.getDownFirstDate());
        }

        Double distance=getTripDistance(upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), routeStaList);


        log.info("【排班计划】-上下行周转时间，upFullTime:{},downFullTime:{},isLoopRoute:{},upBusNum:{},singleBusUp:{},upSupportBusNum:{},downSupportBusNum:{}",upFullTime,downFullTime,isLoopRoute,params.getUpBusNum(),params.getSingleBusUp(),upSupportBusNum,downSupportBusNum);
        /*Double upFullTime = 40 * 60d;
        Double downFullTime = 40 * 60d;*/
        Date upFirstDate = params.getUpFristDate();
        Date downFirstDate = params.getDownFirstDate();
        String upDirection = Convert.toStr(Direction.UP.getValue());
        String downDirection = Convert.toStr(Direction.DOWN.getValue());
        List<DySchedulePlanDriverless> scheduleList = new ArrayList<>();
        boolean isBreakUp = false;
        boolean isBreakDown = false;
        Map<String,Object> upDriverlessMap = new HashMap<>();
        Map<String,Object> downDriverlessMap = new HashMap<>();
        Integer upMaxOrderNumber=0;
        Integer downMaxOrderNumber=0;
        //班次
        int classes = 0;
        int upSupportClasses = 0;
        int downSupportClasses = 0;
        while (true){
            //上行首车到对面作尾车的发班时间
            Long lastDwonPlanTime = 0l;
            //下行首车到对面作尾车的发班时间
            Long lastUpPlanTime = 0l;
            //上行车排班间隔
            long upInterval = 0;
            int upIntervalM = 0;
            //下行车排班间隔
            long downInterval = 0;
            int downIntervalM = 0;

            long upRemainTime = 0;
            long downRemainTime = 0;
            if(!isLoopRoute){
                //上行首车到对面作尾车的发班时间
                lastDwonPlanTime = upFirstDate.getTime() + Convert.toLong(upFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
                //下行首车到对面作尾车的发班时间
                lastUpPlanTime = downFirstDate.getTime() + Convert.toLong(downFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
                //上行车排班间隔
                upInterval = (lastUpPlanTime - upFirstDate.getTime())/params.getUpBusNum();
                upIntervalM = BigDecimal.valueOf(upInterval).divide(BigDecimal.valueOf(1000*60),BigDecimal.ROUND_HALF_UP).intValue();
                //下行车排班间隔
                downInterval = (lastDwonPlanTime - downFirstDate.getTime())/params.getDownBusNum();
                downIntervalM = BigDecimal.valueOf(downInterval).divide(BigDecimal.valueOf(1000*60),BigDecimal.ROUND_HALF_UP).intValue();

                upRemainTime = params.getUpLastDate().getTime()-upFirstDate.getTime();
                downRemainTime = params.getDownLastDate().getTime()-upFirstDate.getTime();
            }else {
                isBreakDown = true;
                //首车到对面作尾车的发班时间
                lastUpPlanTime = upFirstDate.getTime() + Convert.toLong(upFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
                //上行车排班间隔
                upInterval = (lastUpPlanTime - upFirstDate.getTime())/params.getUpBusNum();
                upIntervalM = BigDecimal.valueOf(upInterval).divide(BigDecimal.valueOf(1000*60),BigDecimal.ROUND_HALF_UP).intValue();
                upRemainTime = params.getUpLastDate().getTime()-upFirstDate.getTime();
            }

            classes +=1;
            //上行车排班
            if((upDirection.equals("0")&&!isBreakUp)||(upDirection.equals("1")&&!isBreakDown)){
                if(((Convert.toDouble(upRemainTime)/Convert.toDouble(upInterval)< 1d ||(upRemainTime/upInterval)<=(params.getDownBusNum()+params.getUpBusNum()-1))&&upDirection.equals("0"))
                     ||(!isLoopRoute&&downRemainTime/upInterval<=(params.getUpBusNum()+params.getDownBusNum()-1)&&upDirection.equals("1"))){
                    log.info("upRemainTime:{},upInterval:{},flag1:{},flag2:{},flag3:{]",upRemainTime,upInterval,Convert.toDouble(upRemainTime)/Convert.toDouble(upInterval)<= 1,(upRemainTime/upInterval)<=(params.getDownBusNum()+params.getUpBusNum()-1),((Convert.toDouble(upRemainTime)/Convert.toDouble(upInterval)<= 1 ||(upRemainTime/upInterval)<=(params.getDownBusNum()+params.getUpBusNum()-1))&&upDirection.equals("0")));
                    //尾轮排班
                    int busNum;
                    if(upDirection.equals("0")){
                        isBreakUp = true;
                        busNum = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(upInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        if(busNum<=0){
                            continue;
                        }
                        upInterval = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/upInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //upInterval = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }else {
                        isBreakDown = true;
                        busNum = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(upInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        if(busNum<=0){
                            continue;
                        }
                        upInterval = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/upInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //upInterval = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }
                    for(int i=0;i<busNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setSupportRouteId(params.getSupportRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(upDirection);
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        schedule.setTimeStamp(new Date(upFirstDate.getTime()+upInterval*i).getTime());
                        if(i>=params.getUpBusNum()){
                            int startOrderNumber = i-params.getUpBusNum()+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            if(isLoopRoute){
                                schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                                if(startOrderNumber>=10){
                                    schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                                }else{
                                    schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                                }
                            }else {
                                schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                                if(startOrderNumber>=10){
                                    schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                                }else{
                                    schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                                }
                            }
                        }else {
                            int startOrderNumber = i+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                            }
                        }
                        if(i==busNum-1){
                            log.info("【排班计划】出车方向：上行，营运方向：{}，尾轮排班间隔:{}",upDirection,upInterval/60000);
                            if(upDirection.equals("0")){
                                schedule.setPlanTime(params.getUpLastDate());
                            }else {
                                schedule.setPlanTime(params.getDownLastDate());
                            }
                        }
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(upFullTime)*1000));

                        if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        }else if(!isLoopRoute){
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }
                        schedule.setClasses(isLoopRoute?classes:classes+1);
                        schedule.setSupportClasses(0);
                        schedule.setInterval(upIntervalM);
                        schedule.setStopTime(params.getMinParkTime());
                        schedule.setPassengerNum(params.getPassengerNum());
                        schedule.setPassengerData(params.getPassengerData());
                        schedule.setStatus(status);
                        schedule.setFullTime(upFullTime);
                        schedule.setPlanType(params.getPlanType());
                        schedule.setRunMileage(distance);
                        scheduleList.add(schedule);

                    }
                }else {
                    //正常排班
                    int upNum = params.getUpBusNum();
                    //单班车序
                    int singleBusUpNum = 0;
                    if(Objects.nonNull(params.getSingleBusUp())){
                        singleBusUpNum = params.getUpBusNum()+params.getSingleBusUp();
                        upNum = singleBusUpNum;
                    }
                    //早半班车序
                    int earlyHalfBusUpNum = 0;
                    if(Objects.nonNull(params.getEarlyHalfBusUp())){
                        earlyHalfBusUpNum = params.getUpBusNum()+Convert.toInt(params.getSingleBusUp(),0)+params.getEarlyHalfBusUp();
                        upNum = singleBusUpNum;
                    }
                    //晚半班车序
                    int lateHalfBusUpNum = 0;
                    if(Objects.nonNull(params.getLateHalfBusUp())){
                        lateHalfBusUpNum = params.getUpBusNum()+Convert.toInt(params.getSingleBusUp(),0)+Convert.toInt(params.getEarlyHalfBusUp(),0)+params.getLateHalfBusUp();
                        upNum = lateHalfBusUpNum;
                    }
                    //中班车序
                    int middleBusUpNum = 0;
                    if(Objects.nonNull(params.getMiddleBusUp())){
                        middleBusUpNum = params.getUpBusNum()+Convert.toInt(params.getSingleBusUp(),0)+Convert.toInt(params.getEarlyHalfBusUp(),0)+Convert.toInt(params.getLateHalfBusUp(),0)+params.getMiddleBusUp();
                        upNum = middleBusUpNum;
                    }
                    //双班中停车序
                    int doubleStopBusUpNum = 0;
                    if(Objects.nonNull(params.getDoubleStopBusUp())){
                        doubleStopBusUpNum = params.getUpBusNum()+Convert.toInt(params.getSingleBusUp(),0)+Convert.toInt(params.getEarlyHalfBusUp(),0)+Convert.toInt(params.getLateHalfBusUp(),0)+Convert.toInt(params.getMiddleBusUp(),0)+params.getDoubleStopBusUp();
                        upNum = doubleStopBusUpNum;
                    }
                    //支援车序
                    int supportBusNum = 0;
                    if(Objects.nonNull(upSupportBusNum) && upSupportBusNum!=0){
                        supportBusNum = params.getUpBusNum()+Convert.toInt(params.getSingleBusUp(),0)+Convert.toInt(params.getEarlyHalfBusUp(),0)+Convert.toInt(params.getLateHalfBusUp(),0)+Convert.toInt(params.getMiddleBusUp(),0)+Convert.toInt(params.getDoubleStopBusUp(),0)+upSupportBusNum;
                        upNum = supportBusNum;
                    }

                    for(int i=0;i<upNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        DyDriverlessPlanTime dyDriverlessPlanTime;
                        schedule.setRouteId(params.getRouteId());
                        schedule.setSupportRouteId(params.getSupportRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                        schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        schedule.setTimeStamp(new Date(upFirstDate.getTime()+upInterval*i).getTime());
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(upFullTime)*1000));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(upDirection);
                        schedule.setInterval(upIntervalM);
                        schedule.setStopTime(params.getMinParkTime());
                        schedule.setPassengerNum(params.getPassengerNum());
                        schedule.setPassengerData(params.getPassengerData());
                        schedule.setStatus(status);
                        schedule.setFullTime(upFullTime);
                        schedule.setPlanType(params.getPlanType());
                        schedule.setRunMileage(distance);
                        schedule.setClasses(isLoopRoute?classes:classes+1);
                        schedule.setSupportClasses(0);

                        Long firstStationId = 0l;
                        Long lastStationId = 0l;
                        if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                            schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                            firstStationId = upFirstRouteSta.getStationId();
                            lastStationId = upLastRouteSta.getStationId();
                        }else if(!isLoopRoute){
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                            firstStationId = downFirstRouteSta.getStationId();
                            lastStationId = downLastRouteSta.getStationId();
                        }
                        int startOrderNumber = 0;
                        //支援车排班
                        //发班时间数，例如0600
                        Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                        //判断线路支援车出车线路总站和计划线路总站是否一致
                        Long startStationId = schedule.getFirstRouteStaId();
                        if(Objects.nonNull(config)&&Objects.nonNull(config.getMainStartStationId())&&Objects.equals(scheduleType,0)){
                            startStationId = config.getMainStartStationId();
                        }else if(Objects.nonNull(config)&&Objects.nonNull(config.getSubStartStationId())&&Objects.equals(scheduleType,1)){
                            startStationId = config.getSubStartStationId();
                        }
                        //不存在支援车排班则相关参数置空或者设置0
                        if(Objects.nonNull(upSupportBusNum)&&i>=supportBusNum-upSupportBusNum&&i<=supportBusNum-1){
                            if(Objects.nonNull(config)
                                    &&(config.getIsDriverless().equals(1)||Objects.equals(startStationId,schedule.getFirstRouteStaId()))
                                    &&((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900))){
                                schedule.setSupportClasses(1);
                                if(!CollectionUtils.isEmpty(driverlessFreeList) && i>=params.getUpBusNum()&&i>=supportBusNum-upSupportBusNum){
                                    Long finalFirstStationId = firstStationId;
                                    Long finalLastStationId = lastStationId;
                                    List<BsRouteSub> finalBsRouteSubList = bsRouteSubList.stream().filter(e -> e.getFirstStationId().equals(finalFirstStationId)&&e.getLastStationId().equals(finalLastStationId)).collect(Collectors.toList());
                                    for(DriverlessFreeParams freeParams: driverlessFreeList){
                                        if(freeParams.getBeginFreeTime().getTime()<=schedule.getPlanTime().getTime()
                                                && freeParams.getEndFreeTime().getTime()>=schedule.getTripEndTime().getTime()
                                                && !downDriverlessMap.containsKey(freeParams.getBusName())){
                                            //设置无人车排班计划
                                            dyDriverlessPlanTime = new DyDriverlessPlanTime();
                                            dyDriverlessPlanTime.setBusId(freeParams.getBusId());
                                            dyDriverlessPlanTime.setRouteId(params.getRouteId());
                                            dyDriverlessPlanTime.setRunDate(params.getRunDate());
                                            dyDriverlessPlanTime.setTripBeginTime(schedule.getPlanTime());
                                            dyDriverlessPlanTime.setTripEndTime(schedule.getTripEndTime());
                                            if(!CollectionUtils.isEmpty(finalBsRouteSubList)){
                                                dyDriverlessPlanTime.setTaskId(finalBsRouteSubList.get(0).getRouteSubId());
                                            }
                                            driverlessPlanTimeList.add(dyDriverlessPlanTime);
                                            schedule.setBusName(freeParams.getBusName());
                                            schedule.setBusId(freeParams.getBusId());
                                            if(upDriverlessMap.containsKey(freeParams.getBusName())){
                                                schedule.setStartOrderNumber(Convert.toShort(upDriverlessMap.get(freeParams.getBusName())));
                                            }else {
                                                upMaxOrderNumber += 1;
                                                schedule.setStartOrderNumber(Convert.toShort(upMaxOrderNumber));
                                                upDriverlessMap.put(freeParams.getBusName(),upMaxOrderNumber);
                                            }
                                            startOrderNumber = Convert.toInt(schedule.getStartOrderNumber());
                                            break;
                                        }
                                    }

                                }else {
                                    startOrderNumber = i+1;
                                    upMaxOrderNumber = startOrderNumber;
                                    schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                                }
                            }else {
                                schedule.setPlanTime(null);
                                schedule.setTripEndTime(null);
                                schedule.setInterval(0);
                                schedule.setStopTime(0);
                                schedule.setFullTime(0d);
                                schedule.setRunMileage(0d);

                                startOrderNumber = i+1;
                                upMaxOrderNumber = startOrderNumber;
                                schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            }
                        }else {
                            startOrderNumber = i+1;
                            upMaxOrderNumber = startOrderNumber;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                        }

                        if(startOrderNumber>=10){
                            schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                        }else{
                            schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                        }

                        //不存在单班车排班则相关参数置空或设置0
                        if(Objects.nonNull(params.getSingleBusUp())&&Objects.nonNull(params.getSingleBeginTime())&&i>=singleBusUpNum-params.getSingleBusUp()&&i<=singleBusUpNum-1){
                            if(planTimeNum<Convert.toInt(params.getSingleBeginTime())||planTimeNum>Convert.toInt(params.getSingleEndTime())){
                                schedule.setPlanTime(null);
                                schedule.setTripEndTime(null);
                                schedule.setInterval(0);
                                schedule.setStopTime(0);
                                schedule.setFullTime(0d);
                                schedule.setRunMileage(0d);
                            }
                        }
                        //不存在早半班车排班则相关参数置空或设置0
                        if(Objects.nonNull(params.getEarlyHalfBusUp())&&Objects.nonNull(params.getEarlyBeginTime())&&i>=earlyHalfBusUpNum-params.getEarlyHalfBusUp()&&i<=earlyHalfBusUpNum-1){
                            if(planTimeNum<Convert.toInt(params.getEarlyBeginTime())||planTimeNum>Convert.toInt(params.getEarlyEndTime())){
                                schedule.setPlanTime(null);
                                schedule.setTripEndTime(null);
                                schedule.setInterval(0);
                                schedule.setStopTime(0);
                                schedule.setFullTime(0d);
                                schedule.setRunMileage(0d);
                            }
                        }
                        //定点排班设置
                        if(!CollectionUtils.isEmpty(params.getRegularParamsList())){
                            Integer regularTimeNum = Convert.toInt(DateUtil.date2Str(new Date(upFirstDate.getTime()+upInterval*(i+1)),DateUtil.hhmm));
                            for(ScheduleParamsClasses regularParams : params.getRegularParamsList()){
                                if(regularTimeNum >= Convert.toInt(regularParams.getBeginTime()) && regularTimeNum <= Convert.toInt(regularParams.getEndTime())){
                                    Integer intervalM = BigDecimal.valueOf(60d/regularParams.getClassesNumMin()).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                                    upIntervalM = intervalM > Convert.toInt(regularParams.getMaxDispatchInterval())?Convert.toInt(regularParams.getMaxDispatchInterval()):intervalM;
                                    upInterval = upIntervalM*60*1000l;

                                }
                            }
                        }
                        scheduleList.add(schedule);
                    }
                }

            }

            //下行车排班
            if(!isLoopRoute &&((downDirection.equals("0")&&!isBreakUp)||(downDirection.equals("1")&&!isBreakDown))){
                //正常排班
                int downNum = params.getDownBusNum();
                //单班车序
                int singleBusDownNum = 0;
                if(Objects.nonNull(params.getSingleBusDwon())){
                    singleBusDownNum = params.getDownBusNum()+params.getSingleBusDwon();
                    downNum = singleBusDownNum;
                }
                //早半班车序
                int earlyHalfBusDownNum = 0;
                if(Objects.nonNull(params.getEarlyHalfBusDown())){
                    earlyHalfBusDownNum = params.getDownBusNum()+Convert.toInt(params.getSingleBusDwon(),0)+params.getEarlyHalfBusDown();
                    downNum = earlyHalfBusDownNum;
                }
                //晚半班车序
                int lateHalfBusDownNum = 0;
                if(Objects.nonNull(params.getLateHalfBusDown())){
                    lateHalfBusDownNum = params.getDownBusNum()+Convert.toInt(params.getSingleBusDwon(),0)+Convert.toInt(params.getEarlyHalfBusDown(),0)+params.getLateHalfBusDown();
                    downNum = lateHalfBusDownNum;
                }
                //中班车序
                int middleBusDownNum = 0;
                if(Objects.nonNull(params.getMiddleBusDown())){
                    middleBusDownNum = params.getDownBusNum()+Convert.toInt(params.getSingleBusDwon(),0)+Convert.toInt(params.getEarlyHalfBusDown(),0)+Convert.toInt(params.getLateHalfBusDown(),0)+params.getMiddleBusDown();
                    downNum = middleBusDownNum;
                }
                //双班中停车序
                int doubleStopBusDownNum = 0;
                if(Objects.nonNull(params.getDoubleStopBusDown())){
                    doubleStopBusDownNum = params.getDownBusNum()+Convert.toInt(params.getSingleBusDwon(),0)+Convert.toInt(params.getEarlyHalfBusDown(),0)+Convert.toInt(params.getLateHalfBusDown(),0)+Convert.toInt(params.getMiddleBusDown(),0)+params.getDoubleStopBusDown();
                    downNum = doubleStopBusDownNum;
                }
                //支援车序
                int supportBusNum = 0;
                if(Objects.nonNull(downSupportBusNum) && downSupportBusNum!=0){
                    supportBusNum = params.getDownBusNum()+Convert.toInt(params.getSingleBusDwon(),0)+Convert.toInt(params.getEarlyHalfBusDown(),0)+Convert.toInt(params.getLateHalfBusDown(),0)+Convert.toInt(params.getMiddleBusDown(),0)+Convert.toInt(params.getDoubleStopBusDown(),0)+downSupportBusNum;
                    downNum = supportBusNum;
                }
                for(int j=0;j<downNum;j++){
                    DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                    DyDriverlessPlanTime dyDriverlessPlanTime;
                    schedule.setRouteId(params.getRouteId());
                    schedule.setSupportRouteId(params.getSupportRouteId());
                    schedule.setRouteCode(route.getRouteCode());
                    schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                    schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                    schedule.setPlanTime(new Date(downFirstDate.getTime()+downInterval*j));
                    schedule.setTimeStamp(new Date(downFirstDate.getTime()+downInterval*j).getTime());
                    schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(downFullTime)*1000));
                    schedule.setServiceType("1");
                    schedule.setServiceName("全程");
                    schedule.setDirection(downDirection);
                    schedule.setInterval(downIntervalM);
                    schedule.setStopTime(params.getMinParkTime());
                    schedule.setPassengerNum(params.getPassengerNum());
                    schedule.setPassengerData(params.getPassengerData());
                    schedule.setStatus(status);
                    schedule.setFullTime(downFullTime);
                    schedule.setPlanType(params.getPlanType());
                    schedule.setRunMileage(distance);
                    schedule.setClasses(classes);
                    schedule.setSupportClasses(0);
                    Long firstStationId =0l;
                    Long lastStationId =0l;
                    if(downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))){
                        schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                        schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                        schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                        schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        firstStationId = downFirstRouteSta.getStationId();
                        lastStationId = downLastRouteSta.getStationId();
                    }else {
                        schedule.setFirstRouteStaId(upFirstRouteSta.getRouteStationId());
                        schedule.setLastRouteStaId(upLastRouteSta.getRouteStationId());
                        schedule.setFirstRouteStaName(upFirstRouteSta.getRouteStationName());
                        schedule.setLastRouteStaName(upLastRouteSta.getRouteStationName());
                        firstStationId = upFirstRouteSta.getStationId();
                        lastStationId = upLastRouteSta.getStationId();
                    }
                    int startOrderNumber = 0;
                    //支援车排班
                    //发班时间数，例如0600
                    Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                    //判断线路支援车出车线路总站和计划线路总站是否一致
                    Long startStationId = schedule.getFirstRouteStaId();
                    if(Objects.nonNull(config)&&Objects.equals(scheduleType,0)){
                        startStationId = config.getMainStartStationId();
                    }else if(Objects.nonNull(config)&&Objects.equals(scheduleType,1)){
                        startStationId = config.getSubStartStationId();
                    }
                    //不存在支援车排班则相关参数置空或者设置0
                    if(Objects.nonNull(downSupportBusNum)&&j>=supportBusNum-downSupportBusNum&&j<=supportBusNum-1){
                        if(Objects.nonNull(config)
                                &&(config.getIsDriverless().equals(1)||Objects.equals(startStationId,schedule.getFirstRouteStaId()))
                                &&((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900))){

                            schedule.setSupportClasses(1);
                            if(!CollectionUtils.isEmpty(driverlessFreeList) && j>=params.getDownBusNum()){
                                Long finalFirstStationId = firstStationId;
                                Long finalLastStationId = lastStationId;
                                List<BsRouteSub> finalBsRouteSubList = bsRouteSubList.stream().filter(e -> e.getFirstStationId().equals(finalFirstStationId)&&e.getLastStationId().equals(finalLastStationId)).collect(Collectors.toList());
                                for(DriverlessFreeParams freeParams: driverlessFreeList){
                                    if(freeParams.getBeginFreeTime().getTime()<=schedule.getPlanTime().getTime()
                                            && freeParams.getEndFreeTime().getTime()>=schedule.getTripEndTime().getTime()
                                            && !upDriverlessMap.containsKey(freeParams.getBusName())){
                                        //设置无人车排班计划
                                        dyDriverlessPlanTime = new DyDriverlessPlanTime();
                                        dyDriverlessPlanTime.setBusId(freeParams.getBusId());
                                        dyDriverlessPlanTime.setRouteId(params.getRouteId());
                                        dyDriverlessPlanTime.setRunDate(params.getRunDate());
                                        dyDriverlessPlanTime.setTripBeginTime(schedule.getPlanTime());
                                        dyDriverlessPlanTime.setTripEndTime(schedule.getTripEndTime());
                                        if(!CollectionUtils.isEmpty(finalBsRouteSubList)){
                                            dyDriverlessPlanTime.setTaskId(finalBsRouteSubList.get(0).getRouteSubId());
                                        }
                                        driverlessPlanTimeList.add(dyDriverlessPlanTime);
                                        schedule.setBusName(freeParams.getBusName());
                                        schedule.setBusId(freeParams.getBusId());
                                        if(downDriverlessMap.containsKey(freeParams.getBusName())){
                                            schedule.setStartOrderNumber(Convert.toShort(downDriverlessMap.get(freeParams.getBusName())));
                                        }else {
                                            downMaxOrderNumber += 1;
                                            schedule.setStartOrderNumber(Convert.toShort(downMaxOrderNumber));
                                            downDriverlessMap.put(freeParams.getBusName(),downMaxOrderNumber);
                                        }
                                        startOrderNumber = Convert.toInt(schedule.getStartOrderNumber());
                                        break;
                                    }
                                }
                            }else {
                                startOrderNumber = j+1;
                                downMaxOrderNumber = startOrderNumber;
                                schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            }
                        }else {
                            schedule.setPlanTime(null);
                            schedule.setTripEndTime(null);
                            schedule.setInterval(0);
                            schedule.setStopTime(0);
                            schedule.setFullTime(0d);
                            schedule.setRunMileage(0d);

                            startOrderNumber = j+1;
                            downMaxOrderNumber = startOrderNumber;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                        }
                    }else {
                        startOrderNumber = j+1;
                        downMaxOrderNumber = startOrderNumber;
                        schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                    }

                    if(startOrderNumber>=10){
                        schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                    }else{
                        schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                    }

                    //不存在单班车排班则相关参数置空或设置0
                    if(Objects.nonNull(params.getSingleBusDwon())&&Objects.nonNull(params.getSingleBeginTime())&&j>=singleBusDownNum-params.getSingleBusDwon()&&j<=singleBusDownNum-1){
                        if(planTimeNum<Convert.toInt(params.getSingleBeginTime())||planTimeNum>Convert.toInt(params.getSingleEndTime())){
                            schedule.setPlanTime(null);
                            schedule.setTripEndTime(null);
                            schedule.setInterval(0);
                            schedule.setStopTime(0);
                            schedule.setFullTime(0d);
                            schedule.setRunMileage(0d);
                        }
                    }
                    //不存在早半班车排班则相关参数置空或设置0
                    if(Objects.nonNull(params.getEarlyHalfBusDown())&&Objects.nonNull(params.getEarlyBeginTime())&&j>=earlyHalfBusDownNum-params.getEarlyHalfBusDown()&&j<=earlyHalfBusDownNum-1){
                        if(planTimeNum<Convert.toInt(params.getEarlyBeginTime())||planTimeNum>Convert.toInt(params.getEarlyEndTime())){
                            schedule.setPlanTime(null);
                            schedule.setTripEndTime(null);
                            schedule.setInterval(0);
                            schedule.setStopTime(0);
                            schedule.setFullTime(0d);
                            schedule.setRunMileage(0d);
                        }
                    }
                    //定点排班设置
                    if(!CollectionUtils.isEmpty(params.getRegularParamsList())){
                        Integer regularTimeNum = Convert.toInt(DateUtil.date2Str(new Date(downFirstDate.getTime()+downInterval*(j+1)),DateUtil.hhmm));
                        for(ScheduleParamsClasses regularParams : params.getRegularParamsList()){
                            if(regularTimeNum >= Convert.toInt(regularParams.getBeginTime()) && regularTimeNum <= Convert.toInt(regularParams.getEndTime())){
                                Integer intervalM = BigDecimal.valueOf(60d/regularParams.getClassesNumMin()).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                                downIntervalM = intervalM > Convert.toInt(regularParams.getMaxDispatchInterval())?Convert.toInt(regularParams.getMaxDispatchInterval()):intervalM;
                                downInterval = downIntervalM*60*1000l;
                            }
                        }
                    }
                    scheduleList.add(schedule);
                }

            }


            if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
                upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), upDirection,
                        upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), upFirstDate);
            }else if(!isLoopRoute){
                upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), upDirection,
                        downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), upFirstDate);
            }

            if(!isLoopRoute){
                upFirstDate = new Date(lastDwonPlanTime);
                downFirstDate = new Date(lastUpPlanTime);
                upDirection = upDirection.equals(Convert.toStr(Direction.UP.getValue()))?Convert.toStr(Direction.DOWN.getValue()):Convert.toStr(Direction.UP.getValue());
                downDirection = downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))?Convert.toStr(Direction.UP.getValue()):Convert.toStr(Direction.DOWN.getValue());
                if(downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))){
                    downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), downDirection,
                            downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), downFirstDate);
                }else {
                    downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), downDirection,
                            upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), downFirstDate);
                }
            }else {
                upFirstDate = new Date(lastUpPlanTime);
            }

            //排班结束
            if(isBreakUp && isBreakDown){
                break;
            }
        }
        return scheduleList;
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

    private GenerateScheduleParams getGenerateScheduleParams(GenerateScheduleParams2 params2,List<StationPassenger> stationPassengerList,List<RouteWasteTime> routeWasteTimeList) {
        GenerateScheduleParams params = new GenerateScheduleParams();
        params.setRouteId(params2.getRouteId());
        params.setRunDate(params2.getRunDate());
        //线路首末班时间参数
        List<RouteUpDownInfo> routeUpDownInfoList = routeService.getRouteUpDownInfo(params2.getRouteId());
        if(CollectionUtils.isEmpty(routeUpDownInfoList)){
            log.info("【排班计划】-排班线路首末班时间参数不存在，routeId:{}",params.getRouteId());
            return null;
        }
        RouteUpDownInfo upInfo = routeUpDownInfoList.stream().filter(r -> r.getDirection().equals(0)).collect(Collectors.toList()).get(0);
        RouteUpDownInfo downInfo = routeUpDownInfoList.stream().filter(r -> r.getDirection().equals(1)).collect(Collectors.toList()).get(0);
        params.setUpFristTime(upInfo.getFirstTimeNumStr());
        params.setUpLastTime(upInfo.getLatestTimeNumStr());
        params.setDownFirstTime(downInfo.getFirstTimeNumStr());
        params.setDownLastTime(downInfo.getLatestTimeNumStr());

        //线路停站时间参数
        List<ScheduleParamsAnchor> anchorList = scheduleParamsAnchorService.selectParamsByRouteId(params2.getRouteId());
        if(CollectionUtils.isEmpty(anchorList)){
            log.info("【排班计划】-排班线路停站时间参数不存在，routeId:{}",params.getRouteId());
            return null;
        }
        Map<Integer,List<ScheduleParamsAnchor>> anchorMap = anchorList.stream().collect(Collectors.groupingBy(ScheduleParamsAnchor::getTemplateId));
        //线路配车参数
        List<ScheduleParamsRoute> paramsRouteList = scheduleParamsAnchorService.selectScheduleParamsByRouteId(params2.getRouteId());
        if(CollectionUtils.isEmpty(paramsRouteList)){
            log.info("【排班计划】-排班线路配车参数不存在，routeId:{}",params.getRouteId());
            return null;
        }
        Map<Integer,List<ScheduleParamsRoute>> paramsRouteMap = paramsRouteList.stream().collect(Collectors.groupingBy(ScheduleParamsRoute::getTemplateId));
        //排班模板参数
        List<ScheduleTemplateDetail> templateDetailList = scheduleParamsAnchorService.selectTemplateByRouteId(params2.getRouteId());
        if(CollectionUtils.isEmpty(templateDetailList)){
            log.info("【排班计划】-排班线路模板参数不存在，routeId:{}",params.getRouteId());
            return null;
        }
        Map<Integer,List<ScheduleTemplateDetail>> templateDetailMap = templateDetailList.stream().collect(Collectors.groupingBy(ScheduleTemplateDetail::getApplyDay));
        //行业服务参数
        List<ScheduleParamsClasses> paramsClassesList = scheduleParamsClassesService.selectByRouteId(params2.getRouteId());
        if(CollectionUtils.isEmpty(paramsClassesList)){
            log.info("【排班计划】-行业服务参数不存在，routeId:{}",params.getRouteId());
            return null;
        }
        Map<Integer,List<ScheduleParamsClasses>> paramsClassesMap = paramsClassesList.stream().collect(Collectors.groupingBy(ScheduleParamsClasses::getTemplateId));

        Calendar cal = Calendar.getInstance();
        cal.setTime(params2.getRunDate());
        Integer applyDay = cal.get(Calendar.DAY_OF_WEEK);
        Integer templateId = Objects.isNull(params2.getTemplateId())?templateDetailMap.get(applyDay).get(0).getTemplateId():params2.getTemplateId();
        List<ScheduleParamsAnchor> anchorTemplateList = anchorMap.get(templateId);
        Double anchorDurationMin = anchorTemplateList.stream().mapToInt(ScheduleParamsAnchor::getAnchorDurationMin).average().getAsDouble();
        params.setMinParkTime((int) Math.ceil(anchorDurationMin));
        ScheduleParamsRoute scheduleParamsRoute = paramsRouteMap.get(templateId).get(0);
        params.setUpBusNum(scheduleParamsRoute.getUpBeginNum());
        params.setDownBusNum(scheduleParamsRoute.getDownBeginNum());
        if(Objects.nonNull(params2.getBusNumberUp())&&Objects.nonNull(params2.getBusNumberDown())){
            params.setUpBusNum(params2.getBusNumberUp());
            params.setDownBusNum(params2.getBusNumberDown());
        }
        params.setPassengerNum(scheduleParamsRoute.getVehicleContent());
        params.setPassengerData(params2.getPassengerData());
        params.setSupportRouteId(params2.getSupportRouteId());
        params.setPlanType(params2.getPlanType());
        //设置定点服务参数
        List<ScheduleParamsClasses> scheduleParamsClassesList = paramsClassesMap.get(templateId);
        List<ScheduleParamsClasses> finalScheduleParamsClassesList = null;
        if(!CollectionUtils.isEmpty(scheduleParamsClassesList)){
            finalScheduleParamsClassesList = scheduleParamsClassesList.stream().filter(e -> e.getIsRegularBus().equals(1)).collect(Collectors.toList());
        }
        params.setRegularParamsList(finalScheduleParamsClassesList);
        //单班别参数设置
        ScheduleParamsSingle scheduleParamsSingle = scheduleParamsSingleService.getByTemplateId(templateId);
        params.setSingleBusUp(params2.getSingleBusUp());
        params.setSingleBusDwon(params2.getSingleBusDwon());
        params.setSingleBeginTime(scheduleParamsSingle.getRunBeginTime());
        params.setSingleEndTime(scheduleParamsSingle.getEndBeginTime());
        if(!CollectionUtils.isEmpty(scheduleParamsSingle.getShifts())){
            Map<Integer,List<ScheduleParamShift>> shiftMap = scheduleParamsSingle.getShifts().stream().collect(Collectors.groupingBy(ScheduleParamShift::getShiftType));
            if(shiftMap.containsKey(0)){
                //早半班设置
                params.setEarlyHalfBusUp(params2.getEarlyHalfBusUp());
                params.setEarlyHalfBusDown(params2.getEarlyHalfBusDown());
                params.setEarlyBeginTime(shiftMap.get(0).get(0).getStartTime());
                params.setEarlyEndTime(shiftMap.get(0).get(0).getEndTime());
            }
            if(shiftMap.containsKey(1)){
                //晚半班设置
                params.setLateHalfBusUp(params2.getLateHalfBusUp());
                params.setLateHalfBusDown(params2.getLateHalfBusDown());
                params.setLateBeginTime(shiftMap.get(1).get(0).getStartTime());
                params.setLateEndTime(shiftMap.get(1).get(0).getEndTime());
            }
            if(shiftMap.containsKey(2)){
                //中班设置
                params.setMiddleBusUp(params2.getMiddleBusUp());
                params.setMiddleBusDown(params2.getMiddleBusDown());
                params.setMiddleBeginTime(shiftMap.get(2).get(0).getStartTime());
                params.setMiddleEndTime(shiftMap.get(2).get(0).getEndTime());
            }
            if(shiftMap.containsKey(3)){
                //双班中停设置
                params.setDoubleStopBusUp(params2.getDoubleStopBusUp());
                params.setDoubleStopBusDown(params2.getDoubleStopBusDown());
                params.setDoubleStopBeginTime(shiftMap.get(3).get(0).getStartTime());
                params.setDoubleStopEndTime(shiftMap.get(3).get(0).getEndTime());
            }

        }
        //获取通用周转时间
        Calendar wasteCal = Calendar.getInstance();
        wasteCal.setTime(params2.getPassengerData());
        Integer wasteApplyDay = wasteCal.get(Calendar.DAY_OF_WEEK);
        if(Objects.nonNull(params2.getTurnaroundData())){
            Calendar wasteCal02 = Calendar.getInstance();
            wasteCal02.setTime(params2.getTurnaroundData());
            wasteApplyDay = wasteCal02.get(Calendar.DAY_OF_WEEK);
        }
        routeWasteTimeList = routeWasteTimeMapper.queryByRouteIdAndRunDayNum(params2.getRouteId(),wasteApplyDay);

        if(Objects.equals(params2.getPlanType(),1) && Objects.nonNull(stationPassengerList)){
            //最优计划参数配置
            /**
             * 1、每半小时大数据最大客流数据除以车内容量获取半小时的班次数
             * 2、获取来回周转时间加两边停站时间内的班次数作为总配车数
             * 3、分配上下行车数，早发的车多分配，多分配的配车数根据多发的时间算出客流数得出班次数作为需要多发的配车数，剩余的车辆数均匀分配
             */
            //半小时班次数
            Integer maxPassengerNum = stationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                    .collect(Collectors.toList()).get(0).getCurpeople();
            Integer hourClasses = BigDecimal.valueOf(Convert.toDouble(maxPassengerNum)/Convert.toDouble(scheduleParamsRoute.getVehicleContent())*2).setScale(0,BigDecimal.ROUND_UP).intValue();
            log.info("【排班计划】-半小时班次数，routeId:{},hourClasses:{}",params2.getRouteId(),hourClasses);

            List<RouteWasteTime> upRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("0")).collect(Collectors.toList());
            List<RouteWasteTime> downRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("1")).collect(Collectors.toList());
            Optional<Integer> upReduce = upRouteWasteTimeList.stream().map(RouteWasteTime::getWasteTimeInt).reduce(Integer::max);
            Optional<Integer> downReduce = downRouteWasteTimeList.stream().map(RouteWasteTime::getWasteTimeInt).reduce(Integer::max);
            boolean isLoopRoute = BsData.isLoopRoute(params2.getRouteId());
            Integer totalTime = upReduce.get()+(isLoopRoute?0:downReduce.get()) + Convert.toInt(anchorDurationMin)*2;
            //总配车数
            Double avgClassesNumMin = scheduleParamsClassesList.stream().mapToDouble(ScheduleParamsClasses::getClassesNumMin).average().orElse(0D);
            Integer totalBusNum;
            if(avgClassesNumMin>hourClasses){
                totalBusNum = BigDecimal.valueOf(totalTime/60d*avgClassesNumMin).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                totalBusNum = BigDecimal.valueOf(totalTime/60d*hourClasses).setScale(0,BigDecimal.ROUND_UP).intValue();
            }
            log.info("【排班计划】-总配车数，routeId:{},totalBusNum:{}",params2.getRouteId(),totalBusNum);



            //分配上行车数
            Integer upBusNum = 0;
            Integer downBusNum = 0;
            if(isLoopRoute){
                upBusNum = totalBusNum;
            }else {
                if(Convert.toInt(params.getUpFristTime()) > Convert.toInt(params.getDownFirstTime())){
                    List<StationPassenger> downStationPassengerList = stationPassengerList.stream().filter(e -> e.getDirection().equals("1") && Convert.toInt(e.getPhour()+e.getPminute().split("-")[0]) >= Convert.toInt(params.getDownFirstTime()) && Convert.toInt(e.getPhour()+e.getPminute().split("-")[1]) <= Convert.toInt(params.getUpFristTime())).collect(Collectors.toList());
                    Integer downPassengerNum = downStationPassengerList.stream().mapToInt(StationPassenger::getCurpeople).sum();
                    Integer extralBusNum01 = BigDecimal.valueOf(downPassengerNum/scheduleParamsRoute.getVehicleContent()).setScale(0,BigDecimal.ROUND_UP).intValue();
                    Integer extralBusNum02 = BigDecimal.valueOf((Convert.toInt(params.getUpFristTime()) - Convert.toInt(params.getDownFirstTime()))/60d*avgClassesNumMin).setScale(0,BigDecimal.ROUND_UP).intValue();
                    Integer extralBusNum = extralBusNum01>extralBusNum02?extralBusNum01:extralBusNum02;
                    upBusNum = BigDecimal.valueOf((totalBusNum-extralBusNum)/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                    downBusNum = BigDecimal.valueOf((totalBusNum-extralBusNum)/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue() + extralBusNum;
                }else {
                    List<StationPassenger> upStationPassengerList = stationPassengerList.stream().filter(e -> e.getDirection().equals("0") && Convert.toInt(e.getPhour()+e.getPminute().split("-")[0]) >= Convert.toInt(params.getDownFirstTime()) && Convert.toInt(e.getPhour()+e.getPminute().split("-")[1]) <= Convert.toInt(params.getUpFristTime())).collect(Collectors.toList());
                    Integer upPassengerNum = upStationPassengerList.stream().mapToInt(StationPassenger::getCurpeople).sum();
                    Integer extralBusNum01 = BigDecimal.valueOf(upPassengerNum/scheduleParamsRoute.getVehicleContent()).setScale(0,BigDecimal.ROUND_UP).intValue();
                    Integer extralBusNum02 = BigDecimal.valueOf((Convert.toInt(params.getDownFirstTime()) - Convert.toInt(params.getUpFristTime()))/60d*avgClassesNumMin).setScale(0,BigDecimal.ROUND_UP).intValue();
                    Integer extralBusNum = extralBusNum01>extralBusNum02?extralBusNum01:extralBusNum02;
                    upBusNum = BigDecimal.valueOf((totalBusNum-extralBusNum)/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue() + extralBusNum;
                    downBusNum = BigDecimal.valueOf((totalBusNum-extralBusNum)/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                }
            }

            //分配上下行单班车数
            Integer singleBusUp = 0;
            Integer singleBusDown = 0;
            //todo 环形暂不舍单班，后续调整
            if(Objects.nonNull(scheduleParamsSingle.getRunBeginTime())&&!BsData.isLoopRoute(params2.getRouteId())){
                Integer endBeginTime = Convert.toInt(scheduleParamsSingle.getEndBeginTime());
                Integer runBeginTime = Convert.toInt(scheduleParamsSingle.getRunBeginTime());
                Integer upFirstTime = Convert.toInt(params.getUpFristTime());
                Integer upLastTime = Convert.toInt(params.getUpLastTime());
                Integer totalSingleBusNum = BigDecimal.valueOf((endBeginTime-runBeginTime)/Convert.toDouble((upLastTime-upFirstTime))*totalBusNum).setScale(0,BigDecimal.ROUND_UP).intValue();
                singleBusUp = BigDecimal.valueOf(totalSingleBusNum/Convert.toDouble(totalBusNum)*upBusNum).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
                singleBusDown = totalSingleBusNum-singleBusUp;
            }

            params.setUpBusNum(upBusNum-singleBusUp);
            params.setDownBusNum(downBusNum-singleBusDown);
            params.setSingleBusUp(singleBusUp);
            params.setSingleBusDwon(singleBusDown);
        }
        log.info("【排班计划】-参数设置，routeId:{},params:{}",params2.getRouteId(),JSONObject.toJSONString(params));
        return params;
    }


    @Override
    public BusConfigure busConfigure(Map<String, Object> map) {
        return dySchedulePlanDriverlessMapper.busConfigure(map);
    }

    @Override
    public R getUnionRouteInfo(Long routeId){
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteId(routeId);
        return R.ok().put("data",configList);
    }

    @Override
    public DyDriverlessConfig getDyDriverlessConfig(Long routeId, Long supportRouteId,Integer isDriverless) {
        DyDriverlessConfig configRecord = new DyDriverlessConfig();
        configRecord.setRouteId(routeId);
        configRecord.setSupportRouteId(supportRouteId);
        configRecord.setIsDriverless(isDriverless);
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteIdAndSupportId(configRecord);
        if(CollectionUtils.isEmpty(configList)){
            return null;
        }
        return configList.get(0);
    }

    @Override
    public R getDriverlessRoute(){
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.getDriverlessRoute();
        return R.ok().put("data",configList);
    }

    @Override
    public R getScheduleDetaiList(ScheduleBySortParam params) {
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(params.getRouteId());
        record.setPlanDate(params.getRunDate());
        List<DySchedulePlanDriverless> detailList = dySchedulePlanDriverlessMapper.getDriverlessDetailList(record);
        if(CollectionUtils.isEmpty(detailList)){
            return R.error("排班计划信息不存在");
        }
        List<Schedule> scheduleList = new ArrayList<>();
        for(DySchedulePlanDriverless driverless : detailList){
            Schedule schedule = new Schedule();
            schedule.setBusCode(driverless.getBusCode());
            schedule.setBusId(Convert.toInt(driverless.getBusId()));
            schedule.setBusName(driverless.getBusName());
            schedule.setBusNumber(Convert.toLong(driverless.getStartOrderNumber()));
            schedule.setDirection(driverless.getDirection());
            schedule.setFirstDirection(driverless.getStartDirection());
            schedule.setFirstRouteStaId(driverless.getFirstRouteStaId());
            schedule.setFirstRouteStaName(driverless.getFirstRouteStaName());
            schedule.setLastRouteStaId(driverless.getLastRouteStaId());
            schedule.setLastRouteStaName(driverless.getLastRouteStaName());
            schedule.setRunMileage(driverless.getRunMileage());
            schedule.setScheduleId(driverless.getScheduleId());
            schedule.setServiceName(driverless.getServiceName());
            schedule.setServiceType(driverless.getServiceType());
            schedule.setTripBeginTime(driverless.getPlanTime());
            schedule.setTripEndTime(driverless.getTripEndTime());
            scheduleList.add(schedule);
        }
        return R.ok().put("data",scheduleList);
    }

    @Override
    public List<MountCarPlan> mountCarPlan(String routeId, String runMode, String referenceDate, String runDate) {
        List<MountCarPlan> carPlans = dySchedulePlanDriverlessMapper.mountCarPlan(routeId, runMode, referenceDate, runDate).stream()
                .filter(t -> Objects.nonNull(t.getScheduleId())).collect(Collectors.toList());

        /*MountCarPlanVO mountCarPlanVO = new MountCarPlanVO();
        mountCarPlanVO.setMountCarPlans(carPlans);
        Date runDateD = DateUtil.str2Date(runDate,DateUtil.date_sdf);
        BusNumberConfig busNumberConfig = busNumberConfigService.selectByRouteIdAndPlanDate(Integer.valueOf(routeId), runDateD);
        if (Objects.nonNull(busNumberConfig) && Objects.nonNull(busNumberConfig.getTemplateId())) {
            mountCarPlanVO.setTemplateId(busNumberConfig.getTemplateId());
        }*/
        return carPlans;
    }

    @Override
    public List<MountCarPlan> recentRunBus(String routeId, String referenceDate) {
        return dySchedulePlanDriverlessMapper.recentRunBus(routeId, referenceDate);
    }

    @Override
    public String runBus(String routeId) {

        String result = HttpUtil.getString(dispatchServerUrl + "/" + getRunBusByRoute + "/" + routeId);
        //String result = restTemplate.getForObject(dispatchServerUrl + "/" + getRunBusByRoute+"/"+routeId, String
        // .class);
        return result;
    }

    @Transactional
    @Override
    public int saveMountCar(@NotNull List<MountCarPlan> list, String userId, String userName) throws Exception {
        int i = 0;
        MountCarPlan firstMountCarPlan = list.get(0);
        ScheduleRouteConfigVo scheduleRouteConfigVo =
                scheduleRouteConfigMapper.getByRouteId(firstMountCarPlan.getRouteId());
        Integer busOrganId = null;
        String routeName = null;
        if (Objects.nonNull(scheduleRouteConfigVo)) {
            busOrganId = scheduleRouteConfigVo.getOrganId();
            routeName = scheduleRouteConfigVo.getRouteName();
        }

        // 查询旧计划列表
        String referenceDate = DateUtil2.getDateString(DateUtil2.getDateAddDay(firstMountCarPlan.getPlanDate(), -1),
                new SimpleDateFormat("yyyy-MM-dd"));
        String runDate = DateUtil2.getDateString(firstMountCarPlan.getPlanDate(), new SimpleDateFormat("yyyy-MM-dd"));
        List<MountCarPlan> oldPlanList = dySchedulePlanDriverlessMapper.mountCarPlan(firstMountCarPlan.getRouteId() + "",
                firstMountCarPlan.getRunMode() + "", referenceDate, runDate);

        //判断任务id是否在该线路上
        List<DispatchTask> dispatchTasks = dySchedulePlanDriverlessMapper.dispatchTaskByRoute(list.get(0).getRouteId());
        Map<Long, Long> tasks = dispatchTasks.stream().collect(Collectors.toMap(DispatchTask::getRouteSubId,
                DispatchTask::getRouteSubId));
        for (int j = 0; j < list.size(); j++) {
            DyMidwayShortStation dyMidwayShortStation = list.get(j).getDyMidwayShortStation();
            //判断出场任务id
            if (dyMidwayShortStation != null) {
                if (!tasks.containsKey(dyMidwayShortStation.getTaskId())) {
                    throw new Exception("第" + (j + 1) + "行所选出场任务Id不是该线路，请刷新页面再操作！");
                }
            }
            //判断首轮任务id
            if (list.get(j).getFirstRoundTaskId() != null) {
                if (!tasks.containsKey(list.get(j).getFirstRoundTaskId())) {
                    throw new Exception("第" + (j + 1) + "行所选首轮任务Id不是该线路，请刷新页面再操作！");
                }
            }
        }

        //判断是否为定点班车, 添加时刻表校验
        /*List<SchedulePlan> timetableList = getTimetableList(firstMountCarPlan.getPlanDate(),
                firstMountCarPlan.getRouteId());
        if (!CollectionUtils.isEmpty(timetableList)) {
            StringBuffer buffer = new StringBuffer();
            //上行
            List<SchedulePlan> upList =
                    timetableList.stream().filter(t -> "0".equals(t.getDirection())).collect(Collectors.toList());
            Map<Date, SchedulePlan> upMap = upList.stream().collect(Collectors.toMap(SchedulePlan::getPlanTime,
                    Function.identity()));
            for (MountCarPlan mountCarPlan :
                    list.stream().filter(t -> "0".equals(t.getBusCode().substring(5, 6))).collect(Collectors.toList())) {
                if (mountCarPlan.getFirstRoundPlanTime() != null && !upMap.containsKey(mountCarPlan.getFirstRoundPlanTime())) {
                    buffer.append(DateUtil.getDateString(mountCarPlan.getFirstRoundPlanTime(), new SimpleDateFormat(
                            "HH:mm")));
                    buffer.append(",");
                }
            }
            //下行
            List<SchedulePlan> downList =
                    timetableList.stream().filter(t -> "1".equals(t.getDirection())).collect(Collectors.toList());
            Map<Date, SchedulePlan> downMap = downList.stream().collect(Collectors.toMap(SchedulePlan::getPlanTime,
                    Function.identity()));
            for (MountCarPlan mountCarPlan :
                    list.stream().filter(t -> "1".equals(t.getBusCode().substring(5, 6))).collect(Collectors.toList())) {
                if (mountCarPlan.getFirstRoundPlanTime() != null && !downMap.containsKey(mountCarPlan.getFirstRoundPlanTime())) {
                    buffer.append(DateUtil.getDateString(mountCarPlan.getFirstRoundPlanTime(), new SimpleDateFormat(
                            "HH:mm")));
                    buffer.append(",");
                }
            }
            if (!StringUtil.isEmpty(buffer.toString())) {
                buffer.deleteCharAt(buffer.length() - 1);
                throw new Exception("[" + buffer.toString() + "]不是时刻表时间");
            }
        }*/

        //清空该线路其他车辆挂车情况
        MountCarPlan delMountCarPlan = new MountCarPlan();
        delMountCarPlan.setBusId(null);
        delMountCarPlan.setBusName(null);
        delMountCarPlan.setFirstRoundPlanTime(null);
        delMountCarPlan.setFirstRoundTaskId(null);
        delMountCarPlan.setSyncPlan(0);//同步
        delMountCarPlan.setPlanDate(firstMountCarPlan.getPlanDate());
        delMountCarPlan.setRouteId(firstMountCarPlan.getRouteId());
        //清空挂车信息（busId+busName）
        i += dySchedulePlanDriverlessMapper.saveMountCar(delMountCarPlan);
        //清空首轮信息（发班时间+任务+syncPlan）
        i += dySchedulePlanDriverlessMapper.saveFirstRound(delMountCarPlan);

        //清空该线路其他车辆中途出场
        DyMidwayShortStation delDyMidwayShortStation = new DyMidwayShortStation();
        delDyMidwayShortStation.setRunDate(firstMountCarPlan.getPlanDate());
        delDyMidwayShortStation.setRouteId(firstMountCarPlan.getRouteId() == null ? null :
                Long.valueOf(firstMountCarPlan.getRouteId()));
        dySchedulePlanDriverlessMapper.deleteDyMidwayShortStation(delDyMidwayShortStation);


        //判断是否为定点班车,更新时刻表计划挂车
        /*if (!CollectionUtils.isEmpty(timetableList)) {
            try {
                i += schedulePlanMapper.saveMountCarByTimetable(delMountCarPlan);
                i += schedulePlanMapper.saveFirstRoundByTimetable(delMountCarPlan);
                for (MountCarPlan mountCarPlan : list) {
                    i += schedulePlanMapper.saveMountCarByTimetable(mountCarPlan);
                    //添加只修改首轮发班时间，任务方法
                    i += schedulePlanMapper.saveFirstRoundByTimetable(mountCarPlan);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        //更新挂车信息（busId+busName）
        for (MountCarPlan mountCarPlan : list) {
            i += dySchedulePlanDriverlessMapper.saveMountCar(mountCarPlan);
        }
        //更新首轮信息（发班时间+任务+syncPlan）
        for (MountCarPlan mountCarPlan : list) {
            if (mountCarPlan.getBusId() == null) {
                mountCarPlan.setSyncPlan(1);
            }
            i += dySchedulePlanDriverlessMapper.saveFirstRound(mountCarPlan);
        }
        // 判断是否环线
//        boolean loopRouteFlag = loopRouteFlag(firstMountCarPlan.getRouteId());
//		//若当前计划syncPlan=1，则对应回程syncPlan=1
//		for (MountCarPlan mountCarPlan : list) {
//			if (mountCarPlan.getSyncPlan() != null && mountCarPlan.getSyncPlan() == 1 && !loopRouteFlag) {
//				schedulePlanMapper.setNextSyncPlanFalse(mountCarPlan);
//			}
//		}
        for (MountCarPlan mountCarPlan : list) {
            //添加中途出场
            DyMidwayShortStation dyMidwayShortStation = mountCarPlan.getDyMidwayShortStation();
            if (dyMidwayShortStation != null) {
                dySchedulePlanDriverlessMapper.insertDyMidwayShortStation(dyMidwayShortStation);
            }
        }
//		//统计挂车数量
//		schedulePlanMapper.saveBusNumberMountOptimal(firstMountCarPlan.getRouteId(), firstMountCarPlan.getPlanDate());
//		schedulePlanMapper.saveBusNumberMountPreset(firstMountCarPlan.getRouteId(), firstMountCarPlan.getPlanDate());

        // 抛出挂车记录事件
        /*HangBusEventContext context = new HangBusEventContext();
        context.setBusOrganId(busOrganId.longValue());
        context.setRouteId(firstMountCarPlan.getRouteId());
        context.setRouteName(routeName);
        context.setRunDate(firstMountCarPlan.getPlanDate());
        context.setOldList(oldPlanList);
        context.setNewList(list);
        context.setUserId(userId);
        context.setUserName(userName);
        context.setDispatchTasks(dispatchTasks);
        applicationEventPublisher.publishEvent(new HangBusRecordEvent(this, context));

        busNumberConfigService.updateMountCarTemplate(firstMountCarPlan.getRouteId(), firstMountCarPlan.getPlanDate(), null);*/

        return i;
    }

    @Override
    public R runBusAndInfoByRouteNewRunBus(String routeId) {
        String result = HttpUtil.getString(dispatchAppAppUrl01 + "/" + newRunBus + "/" + routeId);
        if(Objects.isNull(result)){
            result = HttpUtil.getString(dispatchAppAppUrl02+ "/" + newRunBus + "/" + routeId);
        }
        JSONObject dataObj = JSONObject.parseObject(result).getJSONObject("data");
        JSONArray runBusArray = dataObj.getJSONArray("runBus");
        JSONArray infoArray = dataObj.getJSONArray("info");
        List<RunBus> runBuses = runBusService.getByRoute(Convert.toLong(routeId));

        for (int i = 0 ; i < infoArray.size() ; i++){
            JSONObject jsonObject = (JSONObject) infoArray.get(i);
            String status = jsonObject.getString("status");
            String direction = jsonObject.getString("direction");
            String busId = jsonObject.getString("busId");
            if(status.equals(2)){
                //找出前一辆途中车
                List<RunBus> onTripBusList = runBuses.stream().filter(e ->
                        Objects.equals(e.getRunStatus(), Constant.RunBusStatus.ON_TRIP) && Objects.equals(e.getDirection(), direction))
                        .sorted(Comparator.comparing(RunBus::getTripBeginTime).reversed()).collect(Collectors.toList());
                RunBus runBus = runBuses.stream().filter(e -> e.getBusId().equals(Convert.toLong(busId))).collect(Collectors.toList()).get(0);
                jsonObject.put("tripBeginTime",DateUtil.date2Str(runBus.getTripBeginTime(),DateUtil.hh_mm));
                int interval = BigDecimal.valueOf((runBus.getTripBeginTime().getTime()-onTripBusList.get(0).getTripBeginTime().getTime())/60000).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
                jsonObject.put("interval",interval);
            }else {
                jsonObject.put("tripBeginTime","");
                jsonObject.put("interval","");
            }
        }

        return R.ok("成功").put("info",infoArray).put("runBus",runBusArray);
    }

    @Override
    public List<DispatchTask> dispatchTask(String type, String routeId, String direction, String referenceDate,
                                           String runDate) {
        if ("1".equals(type)) {
            return dySchedulePlanDriverlessMapper.dispatchTaskByFirstRound(routeId, direction, referenceDate, runDate);
        }
        if ("0".equals(type)) {
            return dySchedulePlanDriverlessMapper.dispatchTaskByMidway(routeId, direction, referenceDate, runDate);
        }
        return null;
    }

    @Override
    public R getMonitorInfo(Map<String, Object> params) throws CloneNotSupportedException {
        Long routeId = Convert.toLong(params.get("routeId"));
        Date runDate = DateUtil.str2Date((String) params.get("runDate"),DateUtil.date_sdf);

        //组装runbus和info
        String result = HttpUtil.getString(dispatchAppAppUrl01 + "/" + newRunBus + "/" + routeId);
        if(Objects.isNull(result)){
            result = HttpUtil.getString(dispatchAppAppUrl02+ "/" + newRunBus + "/" + routeId);
        }
        List<RunBus> runBuses = runBusService.getByRoute(Convert.toLong(routeId));
        JSONObject dataObj = JSONObject.parseObject(result).getJSONObject("data");
        JSONArray runBusArray = dataObj.getJSONArray("runBus");
        JSONArray infoArray = dataObj.getJSONArray("info");
        List<TriplogSimpleDto> triplogList = triplogMapper.selectByCurrentDay(routeId,DateUtil.getDailyStartDate(new Date()));
        for (int i = 0 ; i < infoArray.size() ; i++){
            JSONObject jsonObject = (JSONObject) infoArray.get(i);
            String status = jsonObject.getString("status");
            String type = jsonObject.getString("type");
            String direction = jsonObject.getString("direction");
            String busId = jsonObject.getString("busId");
            jsonObject.put("tripBeginTime",null);
            jsonObject.put("interval",null);
            jsonObject.put("msgName",jsonObject.getString("statusName"));
            if(Objects.equals(type,"0")){//调度指令
                //找出前一辆途中车
                RunBus runBus = runBuses.stream().filter(e -> e.getBusId().equals(Convert.toLong(busId))).collect(Collectors.toList()).get(0);
                List<RunBus> onTripBusList = runBuses.stream().filter(e ->
                        (Objects.equals(e.getRunStatus(), Constant.RunBusStatus.ON_TRIP)||Objects.equals(e.getRunStatus(), Constant.RunBusStatus.DISPATCHED))
                                && Objects.equals(e.getDirection(), direction) && runBus.getTripBeginTime().getTime()>e.getTripBeginTime().getTime())
                        .sorted(Comparator.comparing(RunBus::getTripBeginTime).reversed()).collect(Collectors.toList());
                Date preTripBeginTime = null;
                if(CollectionUtils.isEmpty(onTripBusList)){
                    List<TriplogSimpleDto> tempTriplogList = triplogList.stream().filter(e ->e.getFromStationId().equals(runBus.getFirstStationId())
                            &&e.getToStationId().equals(runBus.getLastStationId())
                            &&e.getTriplogBeginTime().getTime()<runBus.getTripBeginTime().getTime()
                            &&e.getDirection().equals(runBus.getDirection())).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(tempTriplogList)){
                        log.error("路单信息不存在！routeId:{}",runBus.getRouteId());
                        continue;
                    }
                    tempTriplogList.sort(Comparator.comparing(TriplogSimpleDto::getTriplogBeginTime).reversed());
                    preTripBeginTime = tempTriplogList.get(0).getTriplogBeginTime();
                }else {
                    preTripBeginTime = onTripBusList.get(0).getTripBeginTime();
                }
                log.info("简图监控信息，同意调度，触发成功，busId:{},preTripBeginTime:{},runBus:{},onTripBusList:{}",runBus.getBusId(),preTripBeginTime,JSONObject.toJSONString(runBus),JSONObject.toJSONString(onTripBusList));
                jsonObject.put("tripBeginTime",DateUtil.date2Str(runBus.getTripBeginTime(),DateUtil.hh_mm));
                int interval = BigDecimal.valueOf((runBus.getTripBeginTime().getTime()-preTripBeginTime.getTime())/60000).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
                jsonObject.put("interval",interval);
                if(Objects.equals(status,"4")){
                    jsonObject.put("msgName","已下发");
                    if(Objects.equals(runBus.getRunStatus(), Constant.RunBusStatus.ON_TRIP)){
                        jsonObject.put("msgName","执行任务");
                    }
                }
            }
        }

        //支援计划信息
        List<RuningScheduleVo> supportInfo = supportPlanService.select(routeId);



        return R.ok("成功").put("info",infoArray).put("runBus",runBusArray).put("supportInfo",supportInfo);
    }

    @Override
    public R getRuningScheduleConfig(Map<String, Object> params) {
        Long routeId = Convert.toLong(params.get("routeId"));
        Long supportRouteId = Convert.toLong(params.get("supportRouteId"),null);
        Date runDate = DateUtil.str2Date((String) params.get("runDate"),DateUtil.date_sdf);
        Integer planType = Convert.toInt(params.get("planType"));
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(routeId);
        record.setPlanDate(runDate);
        record.setPlanType(planType);
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
        //支援总班次数
        Integer totalSupportClasses = 0;
        //总班次数
        Integer totalClasses = 0;

        Date lastPlanTime=null;
        //支援开始时间
        String supportBeginTime = null;
        //支援结束时间
        String supportEndTime = null;

        Integer subTotalClasses=0;

        Integer subTotalSupportClasses=0;

        DyDriverlessConfig dyDriverlessConfig = getDyDriverlessConfig(routeId,supportRouteId,Objects.isNull(supportRouteId)?1:null);
        if(Objects.isNull(dyDriverlessConfig)){
            return R.error("排班线路配置信息不存在");
        }

        if(!CollectionUtils.isEmpty(driverlessList)){
            if(Objects.nonNull(supportRouteId)&&dyDriverlessConfig.getIsDriverless().equals(0)){
                DySchedulePlanDriverless supportRecord = new DySchedulePlanDriverless();
                supportRecord.setRouteId(supportRouteId);
                supportRecord.setPlanDate(runDate);
                supportRecord.setPlanType(planType);
                List<DySchedulePlanDriverless> supportDriverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(supportRecord);
                List<DySchedulePlanDriverless> finalSupportDriverlessList = supportDriverlessList;
                if(Objects.isNull(planType)){
                    //简图监控
                    //如果预设和最优同时存在，优先拿预设
                    finalSupportDriverlessList = supportDriverlessList.stream().filter(e -> e.getPlanType().equals(2)).collect(Collectors.toCollection(ArrayList::new));
                    if(CollectionUtils.isEmpty(finalSupportDriverlessList)){
                        finalSupportDriverlessList = supportDriverlessList;
                    }
                }
                log.info("支援计划信息，supportRouteId:{},supportDriverlessList:{}",supportRouteId,JSONObject.toJSONString(supportDriverlessList));
                if(!CollectionUtils.isEmpty(finalSupportDriverlessList)){
                    finalSupportDriverlessList = finalSupportDriverlessList.stream().filter(e -> Objects.nonNull(e.getPlanTime())).collect(Collectors.toCollection(ArrayList::new));
                    subTotalClasses = finalSupportDriverlessList.size();
                    finalSupportDriverlessList = finalSupportDriverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()!=0).sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
                    subTotalSupportClasses = finalSupportDriverlessList.size();
                }
            }

            List<DySchedulePlanDriverless> finalDriverlessList;
            if(Objects.nonNull(planType)){
                //仿真
                finalDriverlessList = driverlessList.stream().filter(e -> Objects.nonNull(e.getPlanTime())).collect(Collectors.toCollection(ArrayList::new));
            }else {
                //简图监控
                //如果预设和最优同时存在，优先拿预设
                finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)&&Objects.nonNull(e.getPlanTime())).collect(Collectors.toCollection(ArrayList::new));
                if(CollectionUtils.isEmpty(finalDriverlessList)){
                    finalDriverlessList = driverlessList.stream().filter(e -> Objects.nonNull(e.getPlanTime())).collect(Collectors.toCollection(ArrayList::new));
                }
            }

            List<DySchedulePlanDriverless> totalDriverlessList = finalDriverlessList.stream().sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
            log.info("总计划信息，routeId:{},totalDriverlessList:{}",routeId,JSONObject.toJSONString(totalDriverlessList));
            lastPlanTime = totalDriverlessList.get(totalDriverlessList.size()-1).getPlanTime();
            totalClasses = totalDriverlessList.size();
            finalDriverlessList = finalDriverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(finalDriverlessList)){
                totalSupportClasses = finalDriverlessList.size();
                supportBeginTime = DateUtil.date2Str(finalDriverlessList.get(0).getPlanTime(),DateUtil.hh_mm);
                supportEndTime = DateUtil.date2Str(finalDriverlessList.get(finalDriverlessList.size()-1).getPlanTime(),DateUtil.hh_mm);
            }
        }else {
            log.info("排班计划信息不存在，routeId:{},supportRouteId:{}",routeId,supportRouteId);
        }

        List<RunBus> runBusList = runBusService.getByRoute(routeId);
        runBusService.fillingShiftType(runBusList);

        //总配车数
        Integer totalBusNum = runBusList.size();
        //单班车
        Integer singleBusNum = runBusList.stream().filter(e -> e.isSingleShiftMiddleStop()).collect(Collectors.toList()).size();
        //双班车
        Integer doubleBusNum = runBusList.size() - singleBusNum;
        //非营运车
        Long nonRunBusNum = runBusList.stream().filter(e -> Constant.RunBusStatus.UN_RUN.equals(e.getRunStatus())).count();
        //营运车
        Long runBusNum = runBusList.stream().filter(e -> !Constant.RunBusStatus.UN_RUN.equals(e.getRunStatus())).count();


        //线路首末班时间参数
        List<RouteUpDownInfo> routeUpDownInfoList = routeService.getRouteUpDownInfo(routeId);
        if(CollectionUtils.isEmpty(routeUpDownInfoList)){
            log.info("首末班时间参数不存在，routeId:{}",routeId);
            return R.error("首末班时间参数不存在");
        }
        RouteUpDownInfo upInfo = routeUpDownInfoList.stream().filter(r -> r.getDirection().equals(0)).collect(Collectors.toList()).get(0);
        RouteUpDownInfo downInfo = routeUpDownInfoList.stream().filter(r -> r.getDirection().equals(1)).collect(Collectors.toList()).get(0);
        //上行时间
        String upBeginTime = upInfo.getFirstTime();
        String upEndTime = upInfo.getLatestTime();
        //下行时间
        String downBeginTime = downInfo.getFirstTime();
        String downEndTime = downInfo.getLatestTime();

        Map<String,Object> mainMap = new HashMap<>();
        Map<String,Object> subMap = new HashMap<>();
        mainMap.put("upBeginTime",upBeginTime);
        mainMap.put("upEndTime",upEndTime);
        mainMap.put("downBeginTime",downBeginTime);
        mainMap.put("downEndTime",downEndTime);
        mainMap.put("totalBusNum",totalBusNum);
        mainMap.put("doubleBusNum",doubleBusNum);
        mainMap.put("singleBusNum",singleBusNum);
        mainMap.put("totalSupportClasses",totalSupportClasses);
        mainMap.put("supportBeginTime",supportBeginTime);
        mainMap.put("supportEndTime",supportEndTime);
        mainMap.put("nonRunBusNum",nonRunBusNum);
        mainMap.put("runBusNum",runBusNum);
        if(Objects.nonNull(supportRouteId)&&dyDriverlessConfig.getIsDriverless().equals(0)){
            //线路首末班时间参数
            List<RouteUpDownInfo> supportRouteUpDownInfoList = routeService.getRouteUpDownInfo(supportRouteId);
            if(CollectionUtils.isEmpty(supportRouteUpDownInfoList)){
                log.info("支援线路首末班时间参数不存在，routeId:{}",routeId);
                return R.error("支援线路首末班时间参数不存在");
            }
            RouteUpDownInfo supportUpInfo = supportRouteUpDownInfoList.stream().filter(r -> r.getDirection().equals(0)).collect(Collectors.toList()).get(0);
            RouteUpDownInfo supportDownInfo = supportRouteUpDownInfoList.stream().filter(r -> r.getDirection().equals(1)).collect(Collectors.toList()).get(0);
            //上行时间
            String supportUpBeginTime = supportUpInfo.getFirstTime();
            String supportUpEndTime = supportUpInfo.getLatestTime();
            //下行时间
            String supportDownBeginTime = supportDownInfo.getFirstTime();
            String supportDownEndTime = supportDownInfo.getLatestTime();

            List<RunBus> supportRunBusList = runBusService.getByRoute(supportRouteId);
            runBusService.fillingShiftType(supportRunBusList);

            //总配车数
            Integer supportTotalBusNum = supportRunBusList.size();
            //单班车
            Integer supportSingleBusNum = supportRunBusList.stream().filter(e -> e.isSingleShiftMiddleStop()).collect(Collectors.toList()).size();
            //双班车
            Integer supportDoubleBusNum = supportRunBusList.size() - supportSingleBusNum;
            //非营运车
            Long supportNonRunBusNum = supportRunBusList.stream().filter(e -> Constant.RunBusStatus.UN_RUN.equals(e.getRunStatus())).count();
            //营运车
            Long supportRunBusNum = supportRunBusList.stream().filter(e -> !Constant.RunBusStatus.UN_RUN.equals(e.getRunStatus())).count();;

            subMap.put("upBeginTime",supportUpBeginTime);
            subMap.put("upEndTime",supportUpEndTime);
            subMap.put("downBeginTime",supportDownBeginTime);
            subMap.put("downEndTime",supportDownEndTime);
            subMap.put("totalBusNum",supportTotalBusNum);
            subMap.put("doubleBusNum",supportDoubleBusNum);
            subMap.put("singleBusNum",supportSingleBusNum);
            subMap.put("totalSupportClasses",subTotalSupportClasses);
            subMap.put("supportBeginTime",supportBeginTime);
            subMap.put("supportEndTime",supportEndTime);
            subMap.put("nonRunBusNum",supportNonRunBusNum);
            subMap.put("runBusNum",supportRunBusNum);
        }

        /*Double totalRunMileage = totalDriverlessList.get(0).getRunMileage()*totalDriverlessList.size();
        Integer totalDuration = 0;
        for(DySchedulePlanDriverless driverless : totalDriverlessList){
            totalDuration += BigDecimal.valueOf(driverless.getFullTime()/60 + driverless.getStopTime()).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        totalDuration = BigDecimal.valueOf(totalDuration/60).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();*/
        Map<String,Object> titleMap = new HashMap<>();
        titleMap.put("lastPlanTime",Objects.isNull(lastPlanTime)?null:DateUtil.date2Str(lastPlanTime,DateUtil.time_sdf));
        titleMap.put("totalClasses",totalClasses);
        titleMap.put("totalSupportClasses",totalSupportClasses);
        titleMap.put("subTotalClasses",subTotalClasses);
        titleMap.put("subTotalSupportClasses",subTotalSupportClasses);
        /*titleMap.put("totalRunMileage",Math.round(totalRunMileage));
        titleMap.put("totalDuration",totalDuration);*/

        Map<String,Object> result = new HashMap<>();
        result.put("mainMap",mainMap);
        if(dyDriverlessConfig.getIsDriverless().equals(1)){
            result.put("subMap",mainMap);
        }else {
            result.put("subMap",subMap);
        }
        result.put("titleMap",titleMap);
        return R.ok("成功").put("data",result);
    }

    @Override
    public String getByRouteIdAndRouteNameKey(String routeId, String routeNameKey, String page) {
        if (StringUtil.isBlank(routeId) || StringUtil.isBlank(routeNameKey) || StringUtil.isBlank(page)) {
            return null;
        }
        String url = dispatchServerUrl + "/" + getByRouteIdAndRouteNameKey + "/" + routeId + '/' + routeNameKey + '/' + page;
        log.info("getByRouteIdAndRouteNameKey - url: {}", url);
        String result = HttpUtil.getString(url);
        return result;
    }

    @Override
    public R getByRouteNameKey(Map<String, Object> json) {
        String routeNameKey = Convert.toStr(json.get("routeNameKey"),null);
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteNameKey(routeNameKey);
        if(!CollectionUtils.isEmpty(configList)){
            List<String> keyList = new ArrayList<>();
            Iterator it = configList.iterator();
            while(it.hasNext()){
                DyDriverlessConfig config = (DyDriverlessConfig) it.next();
                String key1 = config.getRouteName()+config.getSupportRouteName();
                String key2 = config.getSupportRouteName()+config.getRouteName();
                if(keyList.contains(key1)||keyList.contains(key2)){
                    it.remove();
                }else {
                    keyList.add(key1);
                }
            }
        }

        return R.ok().put("data",configList);
    }

    @Override
    public R getOneHourSupportPlan(Map<String, Object> params) {
        Long routeId = Convert.toLong(params.get("routeId"));
        Long supportRouteId = Convert.toLong(params.get("supportRouteId"),null);
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(routeId);
        record.setPlanTime(DateUtil.getDateAddMinute(new Date(),60));
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getOneHourSupportPlan(record);
        List<DySchedulePlanDriverless> supportDriverlessList = null;
        if(Objects.nonNull(supportRouteId)){
            DySchedulePlanDriverless supportRecord = new DySchedulePlanDriverless();
            supportRecord.setRouteId(supportRouteId);
            supportRecord.setPlanTime(DateUtil.getDateAddMinute(new Date(),60));
            supportDriverlessList = dySchedulePlanDriverlessMapper.getOneHourSupportPlan(supportRecord);
        }
        if(CollectionUtils.isEmpty(driverlessList)){
            log.info("大屏支援计划不存在，routeId:{}",routeId);
            return R.error("大屏支援计划不存在");
        }
        //如果预设和最优同时存在，优先拿预设
        List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)).collect(Collectors.toList());
        List<DySchedulePlanDriverless> finalSupportDriverlessList = null;
        if(CollectionUtils.isEmpty(finalDriverlessList)){
            //最优支援计划
            finalDriverlessList = driverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(supportDriverlessList)){
                finalSupportDriverlessList = supportDriverlessList.stream().filter(e -> e.getPlanType().equals(1)&&Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            }
        }else {
            //预设支援计划
            finalDriverlessList = finalDriverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(supportDriverlessList)){
                finalSupportDriverlessList = supportDriverlessList.stream().filter(e -> e.getPlanType().equals(2)&&Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            }
        }
        List<DySchedulePlanDriverless> resultList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(finalDriverlessList)){
            resultList.addAll(finalDriverlessList);
        }
        if(!CollectionUtils.isEmpty(finalSupportDriverlessList)){
            resultList.addAll(finalSupportDriverlessList);
        }


        return R.ok("成功").put("data",resultList);
    }

    @Override
    public R getSchedulePlanDetail(Map<String, Object> params) {
        Long routeId = Convert.toLong(params.get("routeId"));
        Long supportRouteId = Convert.toLong(params.get("supportRouteId"),null);
        Long scheduleId = Convert.toLong(params.get("scheduleId"));
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(routeId);
        record.setSupportRouteId(supportRouteId);
        record.setPlanDate(DateUtil.getDailyStartDate(new Date()));
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
        //如果预设和最优同时存在，优先拿预设
        List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(finalDriverlessList)){
            //最优支援计划
            finalDriverlessList = driverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
        }else {
            //预设支援计划
            finalDriverlessList = finalDriverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
        }
        finalDriverlessList.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
        List<DySchedulePlanDriverless> curDriverlessList = finalDriverlessList.stream().filter(e -> e.getScheduleId().equals(scheduleId)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(curDriverlessList)){
            return R.error("当前计划不存在！");
        }
        //当前计划
        DySchedulePlanDriverless curPlan = curDriverlessList.get(0);

        DyDriverlessConfig configRecord = new DyDriverlessConfig();
        configRecord.setRouteId(routeId);
        configRecord.setSupportRouteId(supportRouteId);
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteIdAndSupportId(configRecord);
        if(CollectionUtils.isEmpty(configList)){
            return R.error("线路配置信息不存在！");
        }
        List<RouteSta> routeStaList = aptsBaseApp.getRouteStaListByRouteId(routeId).getData();
        if(CollectionUtils.isEmpty(routeStaList)){
            log.info("大屏-排班线路站点信息不存在，routeId:{}",routeId);
            return R.error("排班线路站点信息不存在");
        }
        RunBus runBus = runBusService.getByBus(curPlan.getBusId());
        Map<String,Object> result = new HashMap<>();
        result.put("routeName",configList.get(0).getRouteName());
        result.put("supportRouteName",configList.get(0).getSupportRouteName());
        long supported = finalDriverlessList.stream().filter(e -> e.getPlanTime().getTime()<=new Date().getTime()).count();
        long unsupported = finalDriverlessList.size()-supported;
        result.put("supported",supported);
        result.put("unsupported",unsupported);
        String supportStartTime = DateUtil.date2Str(finalDriverlessList.get(0).getPlanTime(),DateUtil.hh_mm);
        String supportEndTime = DateUtil.date2Str(finalDriverlessList.get(finalDriverlessList.size()-1).getTripEndTime(),DateUtil.hh_mm);
        result.put("supportStartTime",supportStartTime);
        result.put("supportEndTime",supportEndTime);
        result.put("curBusName",curPlan.getBusName());
        result.put("curStartTime",DateUtil.date2Str(curPlan.getPlanTime(),DateUtil.hh_mm));
        result.put("curEndTime",DateUtil.date2Str(curPlan.getTripEndTime(),DateUtil.hh_mm));
        result.put("firstRouteStaName",curPlan.getFirstRouteStaName());
        RouteSta routeSta = null;
        if(curPlan.getDirection().equals("0")){
            routeSta = routeStaList.stream().filter(e -> (e.getStationMark().equals(0)||e.getStationMark().equals(1)||e.getStationMark().equals(2))
                     && e.getRouteStationId().equals(curPlan.getFirstRouteStaId())).collect(Collectors.toList()).get(0);
        }else {
            routeSta = routeStaList.stream().filter(e -> (e.getStationMark().equals(3)||e.getStationMark().equals(4)||e.getStationMark().equals(5))
                    && e.getRouteStationId().equals(curPlan.getFirstRouteStaId())).collect(Collectors.toList()).get(0);
        }
        result.put("serviceType",curPlan.getServiceType());
        result.put("serviceName",(curPlan.getDirection().equals("0")?"上行-":"下行-")+curPlan.getServiceName());
        result.put("supportPassengerNum",configList.get(0).getSupportPassengerNum());
        result.put("soc",runBus.getSoc());
        result.put("employeeName",runBus.getEmployeeName());
        Double distance = LocationUtils.getDistance(runBus.getStationId(),routeSta.getStationId());
        result.put("distance",Math.round(distance/1000));
        long arriveTimeNum = BigDecimal.valueOf(distance/30000*60*60*1000).setScale(2,RoundingMode.HALF_UP).longValue()+ new Date().getTime();
        Date arriveDate = new Date(arriveTimeNum);
        String arriveTime = DateUtil.date2Str(arriveDate,DateUtil.hh_mm);
        result.put("arriveTime",arriveTime);
        result.put("supportPlanList",finalDriverlessList);
        return R.ok("成功").put("data",result);
    }

    @Override
    public R getGisRoadInfo(Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();

        List<GisRoadInfo> gis = null;
        List<Map<String,Object>> station = null;
        try{
            //切换数据源apts
            DataSourceUtil.setDB("db2");
            Long routeId = Convert.toLong(params.get("routeId"));
            String direction = Convert.toStr(params.get("direction"));
            gis = gisRoadnodeMapper.selectByRouteIdAndDir(routeId,direction);
            station = gisRoadnodeMapper.selectStationByRouteIdAndDir(routeId,direction);
            if(!CollectionUtils.isEmpty(gis)){
                for(GisRoadInfo info : gis){
                    if(Objects.isNull(info)||Objects.isNull(info.getLongitude())||Objects.isNull(info.getLatitude())){
                        continue;
                    }
                    double[] infoTrans = CoordinateTool.gps84_To_Gcj02(info.getLongitude(),info.getLatitude());
                    info.setLongitude(infoTrans[0]);
                    info.setLatitude(infoTrans[1]);
                }
            }
            if(!CollectionUtils.isEmpty(station)){
                for(Map<String,Object> map : station){
                    if(Objects.isNull(map)){
                        continue;
                    }
                    double LATITUDED = Convert.toDouble(map.get("LATITUDED"));
                    double LONGITUDED = Convert.toDouble(map.get("LONGITUDED"));
                    double[] mapTrans = LngLonUtil.gps84_To_Gcj02(LONGITUDED,LATITUDED);
                    map.put("LONGITUDED",mapTrans[0]);
                    map.put("LATITUDED",mapTrans[1]);
                }
            }
            result.put("gis",gis);
            result.put("station",station);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }finally {
            DataSourceUtil.clearDB();
            DataSourceUtil.setDB("db1");
        }
        return R.ok("成功").put("data",result);
    }
}
