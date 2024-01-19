package com.gci.schedule.driverless.service.schedule.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.*;
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
import com.gci.schedule.driverless.mapper.DyDriverlessConfigMapper;
import com.gci.schedule.driverless.mapper.DySchedulePlanDriverlessMapper;
import com.gci.schedule.driverless.mapper.RouteWasteTimeMapper;
import com.gci.schedule.driverless.mapper.ScheduleRouteConfigMapper;
import com.gci.schedule.driverless.service.schedule.*;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.DateUtil;
import com.gci.schedule.driverless.util.DateUtil2;
import com.gci.schedule.driverless.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    private ScheduleRouteConfigMapper scheduleRouteConfigMapper;
    @Autowired
    private RunBusService runBusService;
    @Autowired
    private ScheduleParamsSingleService scheduleParamsSingleService;
    @Autowired
    private ScheduleParamsClassesService scheduleParamsClassesService;

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
    public R generateSupportSchedule(GenerateScheduleParams2 params2) {
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(params2.getRouteId());
        record.setPlanDate(params2.getRunDate());
        record.setPlanType(params2.getPlanType());
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
        if(!CollectionUtils.isEmpty(driverlessList)){
            log.info("【排班计划】-排班计划已存在，routeId:{}",params2.getRouteId());
            return R.error("排班计划已存在");
        }
        List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getPassengerData(),DateUtil.date_sdf),params2.getRouteId().toString());
        if(CollectionUtils.isEmpty(stationPassengerList)){
            log.info("【排班计划】-客流信息不存在，routeId:{}",params2.getRouteId());
            return R.error("客流信息不存在");
        }
        Integer maxPassengerNum = stationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                .collect(Collectors.toList()).get(0).getCurpeople();
//        Integer maxPassengerNum = 300;
        GenerateScheduleParams params = getGenerateScheduleParams(params2,stationPassengerList);
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
        DyDriverlessConfig config = getDyDriverlessConfig(params.getRouteId(),params.getSupportRouteId(),Objects.isNull(params.getSupportRouteId())?1:0);
        if(Objects.isNull(config)){
            log.info("【排班计划】-排班线路配置信息不存在，routeId:{}",params2.getRouteId());
            return R.error("排班线路配置信息不存在");
        }

        Integer supportBusNum = BigDecimal.valueOf((maxPassengerNum-params.getPassengerNum())/config.getSupportPassengerNum()).setScale(0,BigDecimal.ROUND_UP).intValue();
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
            GenerateScheduleParams reduceParamsTemp = getGenerateScheduleParams(reduceParams2,null);
            if(supportMaxPassengerNum < reduceParamsTemp.getPassengerNum()){
                supportEachOther = false;
            }
        }
        List<DySchedulePlanDriverless> scheduleList = null;
        GenerateScheduleParams reduceParams = null;
        if(config.getIsDriverless().equals(1)){
            //无人车支援排班
            //上行无人车支援
            Integer upDriverlessBusNum;
            //下行无人车支援
            Integer downDriverlessBusNum;
            if(params.getUpBusNum()>params.getDownBusNum()){
                upDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                upDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
                downDriverlessBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }
            //todo 获取无人车排班空闲时间
            List<DriverlessFreeParams> driverlessFreeList = new ArrayList<>();
            /*测试数据-开始*/
            DriverlessFreeParams freeParams01 = new DriverlessFreeParams();
            freeParams01.setBusName("1001");
            freeParams01.setBeginFreeTime(DateUtil.str2Date("2023-11-07 07:00:00",DateUtil.time_sdf));
            freeParams01.setEndFreeTime(DateUtil.str2Date("2023-11-07 09:00:00",DateUtil.time_sdf));
            DriverlessFreeParams freeParams02 = new DriverlessFreeParams();
            freeParams02.setBusName("1002");
            freeParams02.setBeginFreeTime(DateUtil.str2Date("2023-11-07 07:00:00",DateUtil.time_sdf));
            freeParams02.setEndFreeTime(DateUtil.str2Date("2023-11-07 09:00:00",DateUtil.time_sdf));
            driverlessFreeList.add(freeParams01);
            driverlessFreeList.add(freeParams02);
            /*测试数据-结束*/

            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upDriverlessBusNum,downDriverlessBusNum,driverlessFreeList, ScheduleStatus.DRIVERLESS_SCHEDULE.getValue());
            List<DySchedulePlanDriverless> commonScheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,null,null,null,ScheduleStatus.COMMON_SCHEDULE.getValue());
            if(!CollectionUtils.isEmpty(scheduleList)&&!CollectionUtils.isEmpty(commonScheduleList)){
                scheduleList.addAll(commonScheduleList);
            }
        }else if(config.getIsDriverless().equals(0) && supportEachOther){
            //常规线支援排班
            //上行支援车
            Integer upSupportBusNum;
            //下行支援车
            Integer downSupportBusNum;
            if(params.getUpBusNum()>params.getDownBusNum()){
                upSupportBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downSupportBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                upSupportBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
                downSupportBusNum = BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upSupportBusNum,downSupportBusNum,null,ScheduleStatus.SUPPORTED_SCHEDULE.getValue());
            //减车参数
            GenerateScheduleParams2 reduceParams2 = new GenerateScheduleParams2();
            reduceParams2.setRouteId(config.getSupportRouteId());
            reduceParams2.setSupportRouteId(config.getSupportRouteId());
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
            List<StationPassenger> supportStationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getSupportPassengerData(),DateUtil.date_sdf),params2.getSupportRouteId().toString());
            if(CollectionUtils.isEmpty(supportStationPassengerList)){
                log.info("【排班计划】-支援车客流信息不存在，routeId:{}",params2.getSupportRouteId());
                return R.error("支援车客流信息不存在");
            }

            reduceParams = getGenerateScheduleParams(reduceParams2,supportStationPassengerList);
            //上行减车
            Integer upReduceBusNum;
            //下行减车
            Integer downReduceBusNum;
            if(reduceParams.getUpBusNum() > reduceParams.getDownBusNum()){
                upReduceBusNum = -BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
                downReduceBusNum = -BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }else {
                upReduceBusNum = -BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downReduceBusNum = -BigDecimal.valueOf(supportBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
            }
            Route supportRoute = dispatchApp.getRouteById(reduceParams.getRouteId()).getData();
            List<RouteSta> supportRouteStaList = aptsBaseApp.getRouteStaListByRouteId(reduceParams.getRouteId()).getData();
            if(CollectionUtils.isEmpty(supportRouteStaList)){
                log.info("【排班计划】-排班支援线路站点信息不存在，routeId:{}",reduceParams.getRouteId());
                return R.error("排班支援线路站点信息不存在");
            }
            //支援排班
            List<DySchedulePlanDriverless> supportScheduleList = getDySchedulePlanDriverlesses(reduceParams, supportRoute, supportRouteStaList,upReduceBusNum,downReduceBusNum,null,ScheduleStatus.SUPPORT_SCHEDULE.getValue());
            if(!CollectionUtils.isEmpty(supportScheduleList)&&!CollectionUtils.isEmpty(scheduleList)){
                scheduleList.addAll(supportScheduleList);
            }

        }else {
            //常规线排班
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,null,null,null,ScheduleStatus.COMMON_SCHEDULE.getValue());
        }


        if(!CollectionUtils.isEmpty(scheduleList)){
            //插入排班计划
            scheduleList.forEach(item->{
                dySchedulePlanDriverlessMapper.insert(item);
            });
        }
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("mainRouteInfo",params);
        resultMap.put("subRouteInfo",reduceParams);

        return R.ok("操作成功!").put("data",resultMap);
    }

    @Override
    public R getScheduleBySort(ScheduleBySortParam params) {
        //测试数据
        params.setSupportRouteId(420l);
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
            upScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
            ScheduleInfo emptyScheduleInfo = new ScheduleInfo();
            emptyScheduleInfo.setServiceType(upScheduleList.get(0).getServiceType());
            emptyScheduleInfo.setServiceName(upScheduleList.get(0).getServiceName());
            emptyScheduleInfo.setDirection(Convert.toInt(upScheduleList.get(0).getDirection().equals("0")?"1":"0"));
            emptyScheduleInfo.setFirstRouteStaId(upScheduleList.get(0).getLastRouteStaId());
            emptyScheduleInfo.setLastRouteStaId(upScheduleList.get(0).getFirstRouteStaId());
            emptyScheduleInfo.setFirstRouteStaName(upScheduleList.get(0).getLastRouteStaName());
            emptyScheduleInfo.setLastRouteStaName(upScheduleList.get(0).getFirstRouteStaName());
            scheduleList.add(emptyScheduleInfo);
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
            }
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
            downScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
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
            }
            scheduleBusInfo.setScheduleList(scheduleList);
            scheduleBusList.add(scheduleBusInfo);
        }

        //根据上下行1号车的排班计划的首末班时间确定站点信息列表 todo
        List<FirstRouteStaInfo> firstRouteStaList = new ArrayList<>();
        //按班次遍历排班计划
        List<Integer> keys = new ArrayList<>(driverlessMap.keySet());
        Collections.sort(keys);
        for(Integer key : keys){
            FirstRouteStaInfo lastStaInfo = new FirstRouteStaInfo();
            List<DySchedulePlanDriverless> list = driverlessMap.get(key).stream().sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
            List<DySchedulePlanDriverless> supportDriverlessList = list.stream().filter(e -> Objects.nonNull(e.getSupportClasses()) && !e.getSupportClasses().equals(0)).collect(Collectors.toList());
            List<DySchedulePlanDriverless> upDriverlessList = list.stream().filter(e -> e.getStartDirection().equals("0")).collect(Collectors.toList());
            List<DySchedulePlanDriverless> downDriverlessList = list.stream().filter(e -> e.getStartDirection().equals("1")).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(supportDriverlessList)){
                lastStaInfo.setSupportClasses(supportDriverlessList.get(0).getSupportClasses());
            }
            lastStaInfo.setClasses(list.get(0).getClasses());
            lastStaInfo.setDirection(Convert.toInt(list.get(0).getDirection()));
            lastStaInfo.setRouteStationId(list.get(0).getFirstRouteStaId());
            lastStaInfo.setRouteStationName(list.get(0).getFirstRouteStaName());
            lastStaInfo.setBeginTime(DateUtil.date2Str(list.get(0).getPlanTime(),DateUtil.hh_mm));
            lastStaInfo.setEndTime(DateUtil.date2Str(list.get(list.size()-1).getPlanTime(),DateUtil.hh_mm));
            if(!CollectionUtils.isEmpty(upDriverlessList)){
                lastStaInfo.setUpInterval(upDriverlessList.get(0).getInterval());
            }
            if(!CollectionUtils.isEmpty(downDriverlessList)){
                lastStaInfo.setDownInterval(downDriverlessList.get(0).getInterval());
            }
            //停站率
            Long stopTime = list.stream().mapToInt(DySchedulePlanDriverless::getStopTime).sum()*60*1000l;
            Long runTime = list.get(list.size()-1).getPlanTime().getTime()-list.get(0).getPlanTime().getTime();
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
            //计划结束时间
            Date endPlanTime = list.get(list.size()-1).getPlanTime();
            //找出时段最多的客流
            List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(passengerData,DateUtil.date_sdf),params.getRouteId().toString());
            if(CollectionUtils.isEmpty(stationPassengerList)){
                log.info("【排班计划查询】-客流信息不存在，routeId:{},classes:{}",params.getRouteId(),list.get(0).getClasses());
                lastStaInfo.setFullPercent(0);
            }
            List<StationPassenger> finalStationPassengerList = stationPassengerList.stream().filter(e -> e.getPdate().getTime()>=startPlanTime.getTime()&&e.getPdate().getTime()<=endPlanTime.getTime()).collect(Collectors.toList());
            Map<Long,Integer> stationPassengerNumMap = finalStationPassengerList.stream().collect(Collectors.toMap(StationPassenger::getStationId, entry ->entry.getCurpeople()));
            //Optional<Entry<Long, Integer>> max0 = stationPassengerNumMap.entrySet().stream().max(Entry.comparingByValue());
            IntSummaryStatistics maxPassenger = stationPassengerNumMap.entrySet().stream().collect(Collectors.summarizingInt(Map.Entry::getValue));
            Integer fullPercent = BigDecimal.valueOf(maxPassenger.getMax()*100).divide(BigDecimal.valueOf(classes * passengerNum),BigDecimal.ROUND_HALF_UP).intValue();
//            Integer fullPercent = BigDecimal.valueOf(100*10*100).divide(BigDecimal.valueOf(classes * passengerNum),BigDecimal.ROUND_HALF_UP).intValue();
            lastStaInfo.setFullPercent(fullPercent);

            firstRouteStaList.add(lastStaInfo);
        }

        scheduleBySortInfo.setRouteId(params.getRouteId());
        scheduleBySortInfo.setFirstRouteStaList(firstRouteStaList);
        scheduleBySortInfo.setScheduleBusList(scheduleBusList);
        return R.ok().put("data",scheduleBySortInfo);
    }

    private List<DySchedulePlanDriverless> getDySchedulePlanDriverlesses(GenerateScheduleParams params, Route route, List<RouteSta> routeStaList,Integer upSupportBusNum,Integer downSupportBusNum,List<DriverlessFreeParams> driverlessFreeList,int status) {
        RouteSta upFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta upLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_LAST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_LAST.getValue()))
                .collect(Collectors.toList()).get(0);
        Double distance=getTripDistance(upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), routeStaList);
        //获取上行周转时间
        Double upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.UP.getValue()),
                upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), params.getUpFristDate());
        //获取下行周转时间
        Double downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.DOWN.getValue()),
                downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), params.getDownFirstDate());
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
            Long lastDwonPlanTime = upFirstDate.getTime() + Convert.toLong(upFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //下行首车到对面作尾车的发班时间
            Long lastUpPlanTime = downFirstDate.getTime() + Convert.toLong(downFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //上行车排班间隔
            long upInterval = (lastUpPlanTime - upFirstDate.getTime())/params.getUpBusNum();
            int upIntervalM = BigDecimal.valueOf(upInterval).divide(BigDecimal.valueOf(1000*60),BigDecimal.ROUND_HALF_UP).intValue();
            //下行车排班间隔
            long downInterval = (lastDwonPlanTime - downFirstDate.getTime())/params.getDownBusNum();
            int downIntervalM = BigDecimal.valueOf(downInterval).divide(BigDecimal.valueOf(1000*60),BigDecimal.ROUND_HALF_UP).intValue();

            long upRemainTime = params.getUpLastDate().getTime()-upFirstDate.getTime();
            long downRemainTime = params.getDownLastDate().getTime()-upFirstDate.getTime();

            classes +=1;
            //上行车排班
            if((upDirection.equals("0")&&!isBreakUp)||(upDirection.equals("1")&&!isBreakDown)){
                if((upRemainTime/upInterval<=(params.getDownBusNum()+params.getUpBusNum()-1)&&upDirection.equals("0"))
                     ||(downRemainTime/upInterval<=(params.getUpBusNum()+params.getDownBusNum()-1)&&upDirection.equals("1"))){
                    //尾轮排班
                    int busNum;
                    if(upDirection.equals("0")){
                        isBreakUp = true;
                        busNum = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(upInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        upInterval = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/upInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //upInterval = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }else {
                        isBreakDown = true;
                        busNum = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(upInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        upInterval = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/upInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //upInterval = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }
                    for(int i=0;i<=busNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setSupportRouteId(params.getSupportRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(upDirection);
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        if(i>=params.getUpBusNum()){
                            int startOrderNumber = i-params.getUpBusNum()+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
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
                        if(i==busNum){
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
                        }else {
                            schedule.setFirstRouteStaId(downFirstRouteSta.getRouteStationId());
                            schedule.setLastRouteStaId(downLastRouteSta.getRouteStationId());
                            schedule.setFirstRouteStaName(downFirstRouteSta.getRouteStationName());
                            schedule.setLastRouteStaName(downLastRouteSta.getRouteStationName());
                        }
                        schedule.setClasses(classes+1);
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
                    boolean upFlag = true;

                    for(int i=0;i<upNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setSupportRouteId(params.getSupportRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                        schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                        int startOrderNumber = 0;
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+upInterval*i));
                        if(!CollectionUtils.isEmpty(driverlessFreeList) && i>=params.getUpBusNum()){
                            for(DriverlessFreeParams freeParams: driverlessFreeList){
                                if(freeParams.getBeginFreeTime().getTime()<=schedule.getPlanTime().getTime()
                                    && freeParams.getEndFreeTime().getTime()>=schedule.getPlanTime().getTime()
                                    && !downDriverlessMap.containsKey(freeParams.getBusName())){

                                    schedule.setBusName(freeParams.getBusName());
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
                            if(Objects.isNull(schedule.getBusName())){
                                continue;
                            }
                        }else {
                            startOrderNumber = i+1;
                            upMaxOrderNumber = startOrderNumber;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                        }

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
                        schedule.setClasses(classes+1);
                        if(upFlag){
                            schedule.setSupportClasses(0);
                        }else {
                            schedule.setSupportClasses(upSupportClasses);
                        }
                        schedule.setInterval(upIntervalM);
                        schedule.setStopTime(params.getMinParkTime());
                        schedule.setPassengerNum(params.getPassengerNum());
                        schedule.setPassengerData(params.getPassengerData());
                        schedule.setStatus(status);
                        schedule.setFullTime(upFullTime);
                        schedule.setPlanType(params.getPlanType());
                        schedule.setRunMileage(distance);
                        scheduleList.add(schedule);
                        //支援车排班
                        //发班时间数，例如0600
                        Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                        if(upFlag&&Objects.nonNull(upSupportBusNum)){
                            if((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900)){
                                upNum += upSupportBusNum;
                                upFlag = false;
                                if(upSupportClasses==0){
                                    if(downSupportClasses!=0){
                                        upSupportClasses = downSupportClasses+2;
                                    }else {
                                        upSupportClasses +=2;
                                    }
                                }else if(upSupportClasses>downSupportClasses){
                                    upSupportClasses +=1;
                                }else {
                                    upSupportClasses = downSupportClasses+2;
                                }
                            }
                        }
                        //单班车排班
                        if(Objects.nonNull(params.getSingleBusUp())&&Objects.nonNull(params.getSingleBeginTime())){
                            if(planTimeNum>=Convert.toInt(params.getSingleBeginTime())&&planTimeNum<=Convert.toInt(params.getSingleEndTime())){
                                upNum += params.getSingleBusUp();
                            }
                        }
                        //早半班车排班
                        if(Objects.nonNull(params.getEarlyHalfBusUp())&&Objects.nonNull(params.getEarlyBeginTime())){
                            if(planTimeNum>=Convert.toInt(params.getEarlyBeginTime())&&planTimeNum<=Convert.toInt(params.getEarlyEndTime())){
                                upNum += params.getEarlyHalfBusUp();
                            }
                        }
                    }
                }

            }

            //下行车排班
            if((downDirection.equals("0")&&!isBreakUp)||(downDirection.equals("1")&&!isBreakDown)){
                //正常排班
                int downNum = params.getDownBusNum();
                boolean downFlag = true;
                for(int j=0;j<downNum;j++){
                    DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                    schedule.setRouteId(params.getRouteId());
                    schedule.setSupportRouteId(params.getSupportRouteId());
                    schedule.setRouteCode(route.getRouteCode());
                    schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                    schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                    int startOrderNumber = 0;
                    schedule.setPlanTime(new Date(downFirstDate.getTime()+downInterval*j));
                    if(!CollectionUtils.isEmpty(driverlessFreeList) && j>=params.getDownBusNum()){
                        for(DriverlessFreeParams freeParams: driverlessFreeList){
                            if(freeParams.getBeginFreeTime().getTime()<=schedule.getPlanTime().getTime()
                                    && freeParams.getEndFreeTime().getTime()>=schedule.getPlanTime().getTime()
                                    && !upDriverlessMap.containsKey(freeParams.getBusName())){

                                schedule.setBusName(freeParams.getBusName());
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
                        if(Objects.isNull(schedule.getBusName())){
                            continue;
                        }
                    }else {
                        startOrderNumber = j+1;
                        downMaxOrderNumber = startOrderNumber;
                        schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                    }
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
                    schedule.setClasses(classes);
                    if(downFlag){
                        schedule.setSupportClasses(0);
                    }else {
                        schedule.setSupportClasses(downSupportClasses);
                    }
                    schedule.setInterval(downIntervalM);
                    schedule.setStopTime(params.getMinParkTime());
                    schedule.setPassengerNum(params.getPassengerNum());
                    schedule.setPassengerData(params.getPassengerData());
                    schedule.setStatus(status);
                    schedule.setFullTime(downFullTime);
                    schedule.setPlanType(params.getPlanType());
                    schedule.setRunMileage(distance);
                    scheduleList.add(schedule);
                    //支援车排班
                    //发班时间数，例如0600
                    Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                    if(downFlag&&Objects.nonNull(downSupportBusNum)){
                        if((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900)){
                            downNum += downSupportBusNum;
                            downFlag = false;
                            if(upSupportClasses==0){
                                downSupportClasses += 1;
                            }else if(upSupportClasses>downSupportClasses){
                                downSupportClasses = upSupportClasses-1;
                            }else {
                                downSupportClasses += 1;
                            }
                        }
                    }
                    //单班车排班
                    if(Objects.nonNull(params.getSingleBusDwon())&&Objects.nonNull(params.getSingleBeginTime())){
                        if(planTimeNum>=Convert.toInt(params.getSingleBeginTime())&&planTimeNum<=Convert.toInt(params.getSingleEndTime())){
                            downNum += params.getSingleBusDwon();
                        }
                    }
                    //早半班车排班
                    if(Objects.nonNull(params.getEarlyHalfBusDown())&&Objects.nonNull(params.getEarlyBeginTime())){
                        if(planTimeNum>=Convert.toInt(params.getEarlyBeginTime())&&planTimeNum<=Convert.toInt(params.getEarlyEndTime())){
                            downNum += params.getEarlyHalfBusDown();
                        }
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
                        upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), downFirstDate);
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

    private GenerateScheduleParams getGenerateScheduleParams(GenerateScheduleParams2 params2,List<StationPassenger> stationPassengerList) {
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
        }

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
            Integer halfHourClasses = BigDecimal.valueOf(maxPassengerNum/scheduleParamsRoute.getVehicleContent()).setScale(0,BigDecimal.ROUND_UP).intValue();
            //获取通用周转时间
            Calendar wasteCal = Calendar.getInstance();
            wasteCal.setTime(params2.getPassengerData());
            Integer wasteApplyDay = wasteCal.get(Calendar.DAY_OF_WEEK);
            if(Objects.nonNull(params2.getTurnaroundData())){
                Calendar wasteCal02 = Calendar.getInstance();
                wasteCal02.setTime(params2.getTurnaroundData());
                wasteApplyDay = wasteCal02.get(Calendar.DAY_OF_WEEK);
            }
            List<RouteWasteTime> routeWasteTimeList = routeWasteTimeMapper.queryByRouteIdAndRunDayNum(params2.getRouteId(),wasteApplyDay);
            List<RouteWasteTime> upRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("0")).collect(Collectors.toList());
            List<RouteWasteTime> downRouteWasteTimeList = routeWasteTimeList.stream().filter(e -> e.getDirection().equals("1")).collect(Collectors.toList());
            Optional<Integer> upReduce = upRouteWasteTimeList.stream().map(RouteWasteTime::getWasteTimeInt).reduce(Integer::max);
            Optional<Integer> downReduce = downRouteWasteTimeList.stream().map(RouteWasteTime::getWasteTimeInt).reduce(Integer::max);

            Integer totalTime = upReduce.get()+downReduce.get()+ Convert.toInt(anchorDurationMin)*2;
            //总配车数
            List<ScheduleParamsClasses> scheduleParamsClassesList = paramsClassesMap.get(templateId);
            Double avgClassesNumMin = scheduleParamsClassesList.stream().mapToDouble(ScheduleParamsClasses::getClassesNumMin).average().orElse(0D);
            Integer totalBusNum;
            if(avgClassesNumMin>2*halfHourClasses){
                totalBusNum = BigDecimal.valueOf(totalTime/60d*avgClassesNumMin).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                totalBusNum = BigDecimal.valueOf(totalTime/60d*2*halfHourClasses).setScale(0,BigDecimal.ROUND_UP).intValue();
            }

            //分配上行车数
            Integer upBusNum;
            Integer downBusNum;
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
            //分配上下行单班车数
            Integer singleBusUp = 0;
            Integer singleBusDown = 0;
            if(Objects.nonNull(scheduleParamsSingle.getRunBeginTime())){
                Integer endBeginTime = Convert.toInt(scheduleParamsSingle.getEndBeginTime());
                Integer runBeginTime = Convert.toInt(scheduleParamsSingle.getRunBeginTime());
                Integer upFirstTime = Convert.toInt(params.getUpFristTime());
                Integer upLastTime = Convert.toInt(params.getUpLastTime());
                Integer totalSingleBusNum = BigDecimal.valueOf((endBeginTime-runBeginTime)/Convert.toDouble((upLastTime-upFirstTime))*totalBusNum).setScale(0,BigDecimal.ROUND_UP).intValue();
                if(Convert.toInt(params.getUpFristTime()) > Convert.toInt(params.getDownFirstTime())){
                    singleBusUp = BigDecimal.valueOf(totalSingleBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                    singleBusDown = BigDecimal.valueOf(totalSingleBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                }else {
                    singleBusUp = BigDecimal.valueOf(totalSingleBusNum/2d).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                    singleBusDown = BigDecimal.valueOf(totalSingleBusNum/2d).setScale(0,BigDecimal.ROUND_UP).intValue();
                }
            }

            params.setUpBusNum(upBusNum-singleBusUp);
            params.setDownBusNum(downBusNum-singleBusDown);
            params.setSingleBusUp(singleBusUp);
            params.setSingleBusDwon(singleBusDown);
        }
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
    public R getRuningScheduleDetail(Map<String, Object> params) {
        Long routeId = Convert.toLong(params.get("routeId"));
        Date runDate = DateUtil.str2Date((String) params.get("runDate"),DateUtil.date_sdf);
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(routeId);
        record.setPlanDate(runDate);
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
        if(CollectionUtils.isEmpty(driverlessList)){
            return R.error("排班计划不存在");
        }
        List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(finalDriverlessList)){
            finalDriverlessList = driverlessList;
        }
        List<RuningScheduleVo> result = new ArrayList<>();
        for(DySchedulePlanDriverless driverless : finalDriverlessList){
            RunBus runBus = runBusService.getByBus(driverless.getBusId());
            RuningScheduleVo vo = new RuningScheduleVo();
            vo.setBusId(driverless.getBusId());
            vo.setBusName(driverless.getBusName());
            vo.setDirection(driverless.getDirection());
            vo.setEmployeeName(runBus.getEmployeeName());
            vo.setInterval(driverless.getInterval());
            vo.setTripBeginTime(DateUtil.date2Str(driverless.getPlanTime(),DateUtil.hh_mm));
            vo.setTripEndTime(DateUtil.date2Str(driverless.getTripEndTime(),DateUtil.hh_mm));
            Double fullTime;
            if(runBus.getDirection().equals(driverless.getDirection())){
                fullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), driverless.getDirection(),
                        runBus.getFirstRouteStaId(), runBus.getLastRouteStaId(), driverless.getPlanTime());
            }else {
                fullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), driverless.getDirection(),
                        runBus.getLastRouteStaId(), runBus.getFirstRouteStaId(), driverless.getPlanTime());
            }
            vo.setFullTime(Convert.toInt(fullTime/60));
            Double runingFullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), runBus.getDirection(),
                    runBus.getFirstRouteStaId(), runBus.getLastRouteStaId(), runBus.getTripBeginTime());
            long realTripEndTime = runBus.getTripBeginTime().getTime()+Convert.toLong(runingFullTime*1000);
            if(realTripEndTime<driverless.getPlanTime().getTime()){
                vo.setStatus(1);
            }else if((runBus.getTripBeginTime().getTime()>=driverless.getPlanTime().getTime()&&runBus.getTripBeginTime().getTime()<=driverless.getTripEndTime().getTime())
                    ||(realTripEndTime>=driverless.getPlanTime().getTime()&&realTripEndTime<=driverless.getTripEndTime().getTime())){
                vo.setStatus(2);
                vo.setRealTripBeginTime(DateUtil.date2Str(runBus.getTripBeginTime(),DateUtil.hh_mm));
            }else {
                vo.setStatus(3);
            }
            result.add(vo);
        }

        return R.ok("成功").put("data",result);
    }

    @Override
    public R getRuningScheduleConfig(Map<String, Object> params) {
        Long routeId = Convert.toLong(params.get("routeId"));
        Date runDate = DateUtil.str2Date((String) params.get("runDate"),DateUtil.date_sdf);
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(routeId);
        record.setPlanDate(runDate);
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
        if(CollectionUtils.isEmpty(driverlessList)){
            return R.error("排班计划不存在");
        }
        List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(finalDriverlessList)){
            finalDriverlessList = driverlessList;
        }
        List<DySchedulePlanDriverless> totalDriverlessList = finalDriverlessList.stream().sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
        finalDriverlessList = finalDriverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())).sorted(Comparator.comparing(DySchedulePlanDriverless::getPlanTime)).collect(Collectors.toList());
        //支援总班次数
        Integer totalSupportClasses = finalDriverlessList.size();
        //支援开始时间
        String supportBeginTime = DateUtil.date2Str(finalDriverlessList.get(0).getPlanTime(),DateUtil.hh_mm);
        //支援结束时间
        String supportEndTime = DateUtil.date2Str(finalDriverlessList.get(finalDriverlessList.size()-1).getPlanTime(),DateUtil.hh_mm);

        List<RunBus> runBusList = runBusService.getByRoute(routeId);

        //总配车数
        Integer totalBusNum = runBusList.size();
        //双班车
        Integer doubleBusNum = runBusList.stream().filter(e -> e.isDoubleShift()).collect(Collectors.toList()).size();
        //单班车
        Integer singleBusNum = runBusList.size() - doubleBusNum;

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
        if(Objects.nonNull(params.get("supportRouteId"))){
            Long supportRouteId = Convert.toLong(params.get("supportRouteId"));
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

            List<RunBus> supportRunBusList = runBusService.getByRoute(routeId);

            //总配车数
            Integer supportTotalBusNum = supportRunBusList.size();
            //双班车
            Integer supportDoubleBusNum = supportRunBusList.stream().filter(e -> e.isDoubleShift()).collect(Collectors.toList()).size();
            //单班车
            Integer supportSingleBusNum = supportRunBusList.size() - doubleBusNum;

            subMap.put("upBeginTime",supportUpBeginTime);
            subMap.put("upEndTime",supportUpEndTime);
            subMap.put("downBeginTime",supportDownBeginTime);
            subMap.put("downEndTime",supportDownEndTime);
            subMap.put("totalBusNum",supportTotalBusNum);
            subMap.put("doubleBusNum",supportDoubleBusNum);
            subMap.put("singleBusNum",supportSingleBusNum);
            subMap.put("totalSupportClasses",totalSupportClasses);
            subMap.put("supportBeginTime",supportBeginTime);
            subMap.put("supportEndTime",supportEndTime);
        }
        Date lastPlanTime = totalDriverlessList.get(totalDriverlessList.size()-1).getPlanTime();
        Integer totalClasses = totalDriverlessList.size();
        Double totalRunMileage = totalDriverlessList.get(0).getRunMileage()*totalDriverlessList.size();
        Integer totalDuration = 0;
        for(DySchedulePlanDriverless driverless : totalDriverlessList){
            totalDuration += BigDecimal.valueOf(driverless.getFullTime()/60 + driverless.getStopTime()).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        totalDuration = BigDecimal.valueOf(totalDuration/60).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
        Map<String,Object> titleMap = new HashMap<>();
        titleMap.put("lastPlanTime",DateUtil.date2Str(lastPlanTime,DateUtil.time_sdf));
        titleMap.put("totalClasses",totalClasses);
        titleMap.put("totalRunMileage",totalRunMileage);
        titleMap.put("totalDuration",totalDuration);

        Map<String,Object> result = new HashMap<>();
        result.put("mainMap",mainMap);
        result.put("subMap",subMap);
        result.put("titleMap",titleMap);
        return R.ok("成功").put("data",result);
    }
}
