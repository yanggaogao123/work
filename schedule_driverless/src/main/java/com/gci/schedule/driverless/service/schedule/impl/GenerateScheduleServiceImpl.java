package com.gci.schedule.driverless.service.schedule.impl;

import cn.hutool.core.convert.Convert;
import com.gci.schedule.driverless.bean.*;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.Direction;
import com.gci.schedule.driverless.bean.enums.StationMark;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.feign.AptsBaseApp;
import com.gci.schedule.driverless.feign.DispatchApp;
import com.gci.schedule.driverless.mapper.DyDriverlessConfigMapper;
import com.gci.schedule.driverless.mapper.DySchedulePlanDriverlessMapper;
import com.gci.schedule.driverless.service.schedule.BigDataService;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import com.gci.schedule.driverless.service.schedule.RouteService;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsAnchorService;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
        GenerateScheduleParams params = getGenerateScheduleParams(params2);
        if(Objects.isNull(params)){
            log.info("【排班计划】-排班参数信息查询失败，routeId:{}",params.getRouteId());
            return R.error("排班参数信息查询失败");
        }

        Route route = dispatchApp.getRouteById(params.getRouteId()).getData();
        //Route route = routeService.getRouteByRouteId(params.getRouteId());
        if(Objects.isNull(route)){
            log.info("【排班计划】-排班线路信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班线路信息不存在");
        }
        List<RouteSta> routeStaList = aptsBaseApp.getRouteStaListByRouteId(params.getRouteId()).getData();
        if(CollectionUtils.isEmpty(routeStaList)){
            log.info("【排班计划】-排班线路站点信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班线路站点信息不存在");
        }

        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteId(params.getRouteId());
        if(CollectionUtils.isEmpty(configList)){
            log.info("【排班计划】-排班线路配置信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班线路配置信息不存在");
        }
        /*List<StationPassenger> stationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(params2.getPassengerData(),DateUtil.date_sdf),params.getRouteId().toString());
        if(CollectionUtils.isEmpty(stationPassengerList)){
            log.info("【排班计划】-客流信息不存在，routeId:{}",params.getRouteId());
            return R.error("客流信息不存在");
        }
        Integer maxPassengerNum = stationPassengerList.stream().sorted(Comparator.comparing(StationPassenger::getCurpeople).reversed())
                .collect(Collectors.toList()).get(0).getCurpeople();*/
        Integer maxPassengerNum = 300;

        DyDriverlessConfig config = configList.get(0);
        Integer supportBusNum = 0;
        Integer driverlessBusNum = 0;
        if(Objects.nonNull(config.getSupportPassengerNum())){
            supportBusNum = BigDecimal.valueOf((maxPassengerNum-params.getPassengerNum())/config.getSupportPassengerNum()).setScale(0,BigDecimal.ROUND_UP).intValue();
        }
        if(Objects.nonNull(config.getDriverlessPassengerNum())){
            driverlessBusNum = BigDecimal.valueOf((maxPassengerNum-params.getPassengerNum())/config.getDriverlessPassengerNum()).setScale(0,BigDecimal.ROUND_UP).intValue();
        }
        List<DySchedulePlanDriverless> scheduleList = null;
        //支援排班
        List<DySchedulePlanDriverless> supportScheduleList = null;
        if(Objects.nonNull(config.getDriverlessRouteId())&&driverlessBusNum>=1){
            //无人车支援排班
            //上行无人车支援
            Integer upDriverlessBusNum;
            //下行无人车支援
            Integer downDriverlessBusNum;
            if(params.getUpBusNum()>params.getDownBusNum()){
                upDriverlessBusNum = BigDecimal.valueOf(driverlessBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
                downDriverlessBusNum = BigDecimal.valueOf(driverlessBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
            }else {
                upDriverlessBusNum = BigDecimal.valueOf(driverlessBusNum/2).setScale(0,BigDecimal.ROUND_UP).intValue();
                downDriverlessBusNum = BigDecimal.valueOf(driverlessBusNum/2).setScale(0,BigDecimal.ROUND_DOWN).intValue();
            }
            //todo 获取无人车排班空闲时间
            List<DriverlessFreeParams> driverlessFreeList = new ArrayList<>();
            /*测试数据-开始*/
            DriverlessFreeParams freeParams01 = new DriverlessFreeParams();
            freeParams01.setBusName("1001");
            freeParams01.setBeginFreeTime(DateUtil.str2Date("2023-10-27 07:00:00",DateUtil.time_sdf));
            freeParams01.setEndFreeTime(DateUtil.str2Date("2023-10-27 09:00:00",DateUtil.time_sdf));
            DriverlessFreeParams freeParams02 = new DriverlessFreeParams();
            freeParams02.setBusName("1002");
            freeParams02.setBeginFreeTime(DateUtil.str2Date("2023-10-27 07:00:00",DateUtil.time_sdf));
            freeParams02.setEndFreeTime(DateUtil.str2Date("2023-10-27 09:00:00",DateUtil.time_sdf));
            driverlessFreeList.add(freeParams01);
            driverlessFreeList.add(freeParams02);
            /*测试数据-结束*/

            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upDriverlessBusNum,downDriverlessBusNum,driverlessFreeList);

        }else if(Objects.nonNull(config.getSupportRouteId())&&supportBusNum>=1){
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
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,upSupportBusNum,downSupportBusNum,null);
            //减车参数
            GenerateScheduleParams2 reduceParams2 = new GenerateScheduleParams2();
            reduceParams2.setRouteId(config.getSupportRouteId());
            reduceParams2.setRunDate(params2.getRunDate());
            GenerateScheduleParams reduceParams = getGenerateScheduleParams(reduceParams2);
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
            supportScheduleList = getDySchedulePlanDriverlesses(reduceParams, supportRoute, routeStaList,upReduceBusNum,downReduceBusNum,null);

        }else {
            //常规线排班
            scheduleList = getDySchedulePlanDriverlesses(params, route, routeStaList,null,null,null);
        }


        if(!CollectionUtils.isEmpty(scheduleList)){
            //插入排班计划
            scheduleList.forEach(item->{
                dySchedulePlanDriverlessMapper.insert(item);
            });
        }
        if(!CollectionUtils.isEmpty(supportScheduleList)){
            //插入支援车排班计划
            supportScheduleList.forEach(item->{
                dySchedulePlanDriverlessMapper.insert(item);
            });
        }

        return R.ok("操作成功!");
    }

    @Override
    public R getScheduleBySort(ScheduleBySortParam params) {
        GenerateScheduleParams2 params2 = new GenerateScheduleParams2();
        params2.setRunDate(params.getRunDate());
        params2.setRouteId(params.getRouteId());
        GenerateScheduleParams params3 = getGenerateScheduleParams(params2);
        DySchedulePlanDriverless record = new DySchedulePlanDriverless();
        record.setRouteId(params.getRouteId());
        record.setPlanDate(params.getRunDate());
        List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList(record);
        if(CollectionUtils.isEmpty(driverlessList)){
            log.info("排班计划信息不存在，routeId:{}",params.getRouteId());
            return R.error("排班计划信息不存在");
        }
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
            /*//如果上行早班时间晚于下行早班时间，则上行计划列表需要设置空列；
            if(params3.getUpFristDate().getTime()>params3.getDownFirstDate().getTime()){

            }*/
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
            // 如果下行早班时间晚于上行早班时间，则上行计划列表需要设置空列；
            /*if(params3.getDownFirstDate().getTime()>params3.getUpFristDate().getTime()){
                ScheduleInfo emptyScheduleInfo = new ScheduleInfo();
                emptyScheduleInfo.setServiceType(downScheduleList.get(0).getServiceType());
                emptyScheduleInfo.setServiceName(downScheduleList.get(0).getServiceName());
                emptyScheduleInfo.setDirection(Convert.toInt(downScheduleList.get(0).getDirection().equals("0")?"1":"0"));
                emptyScheduleInfo.setFirstRouteStaId(downScheduleList.get(0).getLastRouteStaId());
                emptyScheduleInfo.setLastRouteStaId(downScheduleList.get(0).getFirstRouteStaId());
                emptyScheduleInfo.setFirstRouteStaName(downScheduleList.get(0).getLastRouteStaName());
                emptyScheduleInfo.setLastRouteStaName(downScheduleList.get(0).getFirstRouteStaName());
                scheduleList.add(emptyScheduleInfo);
            }*/
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
        //上行1号车
        List<DySchedulePlanDriverless> oneUpScheduleList = upScheduleMap.get((short)1);
        oneUpScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
        //下行1号车
        List<DySchedulePlanDriverless> downUpScheduleList = downScheduleMap.get((short)1);
        downUpScheduleList.sort(Comparator.comparing(DySchedulePlanDriverless::getPlanTime));
        if(params3.getDownFirstDate().getTime()>params3.getUpFristDate().getTime()){
            FirstRouteStaInfo emptyStaInfo = new FirstRouteStaInfo();
            emptyStaInfo.setDirection(0);
            emptyStaInfo.setRouteStationId(oneUpScheduleList.get(0).getFirstRouteStaId());
            emptyStaInfo.setRouteStationName(oneUpScheduleList.get(0).getFirstRouteStaName());
            firstRouteStaList.add(emptyStaInfo);
        }
        for(DySchedulePlanDriverless schedule : downUpScheduleList){
            FirstRouteStaInfo staInfo = new FirstRouteStaInfo();
            staInfo.setDirection(Convert.toInt(schedule.getDirection()));
            staInfo.setRouteStationId(schedule.getFirstRouteStaId());
            staInfo.setRouteStationName(schedule.getFirstRouteStaName());
            firstRouteStaList.add(staInfo);
        }
        //如果下行1号末班时间小于上行1号末班时间则增加1列上行站点信息
        Date oneUpLastDate = oneUpScheduleList.get(oneUpScheduleList.size()-1).getPlanTime();
        Date oneDownLastDate = downUpScheduleList.get(downUpScheduleList.size()-1).getPlanTime();
        if(oneDownLastDate.getTime() < oneUpLastDate.getTime()){
            FirstRouteStaInfo lastStaInfo = new FirstRouteStaInfo();
            lastStaInfo.setDirection(Convert.toInt(oneUpScheduleList.get(oneUpScheduleList.size()-1).getDirection()));
            lastStaInfo.setRouteStationId(oneUpScheduleList.get(oneUpScheduleList.size()-1).getFirstRouteStaId());
            lastStaInfo.setRouteStationName(oneUpScheduleList.get(oneUpScheduleList.size()-1).getFirstRouteStaName());
            firstRouteStaList.add(lastStaInfo);
        }
        scheduleBySortInfo.setRouteId(params.getRouteId());
        scheduleBySortInfo.setFirstRouteStaList(firstRouteStaList);
        scheduleBySortInfo.setScheduleBusList(scheduleBusList);
        return R.ok().put("data",scheduleBySortInfo);
    }

    private List<DySchedulePlanDriverless> getDySchedulePlanDriverlesses(GenerateScheduleParams params, Route route, List<RouteSta> routeStaList,Integer upSupportBusNum,Integer downSupportBusNum,List<DriverlessFreeParams> driverlessFreeList) {
        RouteSta upFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta upLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.UP_LAST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downFirstRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_FIRST.getValue()))
                .collect(Collectors.toList()).get(0);
        RouteSta downLastRouteSta = routeStaList.stream().filter(r -> r.getStationMark().equals(StationMark.DOWN_LAST.getValue()))
                .collect(Collectors.toList()).get(0);

        //获取上行周转时间
        /*Double upFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.UP.getValue()),
                upFirstRouteSta.getRouteStationId(), upLastRouteSta.getRouteStationId(), params.getUpFristDate());
        //获取下行周转时间
        Double downFullTime = scheduleServerService.getIntersiteTime(params.getRouteId(), Convert.toStr(Direction.DOWN.getValue()),
                downFirstRouteSta.getRouteStationId(), downLastRouteSta.getRouteStationId(), params.getDownFirstDate());*/
        Double upFullTime = 40 * 60d;
        Double downFullTime = 40 * 60d;
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
        while (true){
            //上行首车到对面作尾车的发班时间
            Long lastDwonPlanTime = upFirstDate.getTime() + Convert.toLong(upFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //下行首车到对面作尾车的发班时间
            Long lastUpPlanTime = downFirstDate.getTime() + Convert.toLong(downFullTime)*1000 + Convert.toLong(params.getMinParkTime()*60*1000);
            //上行车排班间隔
            long upInterval = (lastUpPlanTime - upFirstDate.getTime())/params.getUpBusNum();
            //下行车排班间隔
            long downInterval = (lastDwonPlanTime - downFirstDate.getTime())/params.getDownBusNum();

            long upRemainTime = params.getUpLastDate().getTime()-upFirstDate.getTime();
            long downRemainTime = params.getDownLastDate().getTime()-upFirstDate.getTime();
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
                        scheduleList.add(schedule);

                    }
                }else {
                    //正常排班
                    int upNum = params.getUpBusNum();
                    boolean upFlag = true;
                    for(int i=0;i<upNum;i++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
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
                        scheduleList.add(schedule);
                        //支援车排班
                        if(upFlag&&Objects.nonNull(upSupportBusNum)){
                            //发班时间数，例如0600
                            Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                            if((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900)){
                                upNum += upSupportBusNum;
                                upFlag = false;
                            }
                        }
                    }
                }

            }

            //下行车排班
            if((downDirection.equals("0")&&!isBreakUp)||(downDirection.equals("1")&&!isBreakDown)){
                /*if((upRemainTime/downInterval<=(params.getDownBusNum()+params.getUpBusNum()-1)&&downDirection.equals("0"))
                        ||(downRemainTime/downInterval<=(params.getUpBusNum()+params.getDownBusNum()-1)&&downDirection.equals("1"))){
                    //尾轮排班
                    int busNum;
                    if(downDirection.equals("0")){
                        isBreakUp = true;
                        busNum = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(downInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        downInterval = BigDecimal.valueOf(params.getUpLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/downInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //downInterval = BigDecimal.valueOf((params.getUpLastDate().getTime()-upFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }else {
                        isBreakDown = true;
                        busNum = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(downInterval),BigDecimal.ROUND_DOWN).intValue()+1;
                        downInterval = BigDecimal.valueOf(params.getDownLastDate().getTime()-upFirstDate.getTime()).divide(BigDecimal.valueOf(busNum),BigDecimal.ROUND_DOWN).longValue();
                        //busNum = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/downInterval).setScale(2, RoundingMode.DOWN).intValue();
                        //downInterval = BigDecimal.valueOf((params.getDownLastDate().getTime()-downFirstDate.getTime())/busNum).setScale(2, RoundingMode.UP).longValue();
                    }
                    for(int j=0;j<=busNum;j++){
                        DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                        schedule.setRouteId(params.getRouteId());
                        schedule.setRouteCode(route.getRouteCode());
                        schedule.setPlanDate(DateUtil.getDailyStartDate(params.getRunDate()));
                        schedule.setServiceType("1");
                        schedule.setServiceName("全程");
                        schedule.setDirection(downDirection);
                        schedule.setPlanTime(new Date(upFirstDate.getTime()+downInterval*j));
                        if(j>=params.getUpBusNum()){
                            int startOrderNumber = j-params.getUpBusNum()+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.DOWN.getValue()));
                            if(j==busNum){
                                schedule.setPlanTime(params.getDownLastDate());
                            }
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.DOWN.getValue()+"000"+startOrderNumber);
                            }
                        }else {
                            int startOrderNumber = j+1;
                            schedule.setStartOrderNumber(Convert.toShort(startOrderNumber));
                            schedule.setStartDirection(Convert.toStr(Direction.UP.getValue()));
                            if(startOrderNumber>=10){
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"00"+startOrderNumber);
                            }else{
                                schedule.setBusCode(route.getRouteCode()+Direction.UP.getValue()+"000"+startOrderNumber);
                            }
                        }
                        if(j==busNum){
                            log.info("【排班计划】出车方向：下行，营运方向：{}，尾轮排班间隔:{}",downDirection,downInterval/60000);
                            if(downDirection.equals("0")){
                                schedule.setPlanTime(params.getUpLastDate());
                            }else {
                                schedule.setPlanTime(params.getDownLastDate());
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

                }*/
                //正常排班
                int downNum = params.getDownBusNum();
                boolean downFlag = true;
                for(int j=0;j<downNum;j++){
                    DySchedulePlanDriverless schedule = new DySchedulePlanDriverless();
                    schedule.setRouteId(params.getRouteId());
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
                    scheduleList.add(schedule);
                    //支援车排班
                    if(downFlag&&Objects.nonNull(downSupportBusNum)){
                        //发班时间数，例如0600
                        Integer planTimeNum = Convert.toInt(DateUtil.date2Str(schedule.getPlanTime(),DateUtil.hhmm));
                        if((planTimeNum >= 700 && planTimeNum <= 900) || (planTimeNum >= 1700 && planTimeNum <= 1900)){
                            downNum += downSupportBusNum;
                            downFlag = false;
                        }
                    }
                }

            }

            upFirstDate = new Date(lastDwonPlanTime);
            downFirstDate = new Date(lastUpPlanTime);
            upDirection = upDirection.equals(Convert.toStr(Direction.UP.getValue()))?Convert.toStr(Direction.DOWN.getValue()):Convert.toStr(Direction.UP.getValue());
            downDirection = downDirection.equals(Convert.toStr(Direction.DOWN.getValue()))?Convert.toStr(Direction.UP.getValue()):Convert.toStr(Direction.DOWN.getValue());
            /*if(upDirection.equals(Convert.toStr(Direction.UP.getValue()))){
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
            }*/
            //排班结束
            if(isBreakUp && isBreakDown){
                break;
            }
        }
        return scheduleList;
    }

    private GenerateScheduleParams getGenerateScheduleParams(GenerateScheduleParams2 params2) {
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(params2.getRunDate());
        Integer applyDay = cal.get(Calendar.DAY_OF_WEEK);
        Integer templateId = templateDetailMap.get(applyDay).get(0).getTemplateId();
        List<ScheduleParamsAnchor> anchorTemplateList = anchorMap.get(templateId);
        Double anchorDurationMin = anchorTemplateList.stream().mapToInt(ScheduleParamsAnchor::getAnchorDurationMin).average().getAsDouble();
        params.setMinParkTime((int) Math.ceil(anchorDurationMin));
        ScheduleParamsRoute scheduleParamsRoute = paramsRouteMap.get(templateId).get(0);
        params.setUpBusNum(scheduleParamsRoute.getUpBeginNum());
        params.setDownBusNum(scheduleParamsRoute.getDownBeginNum());
        params.setPassengerNum(scheduleParamsRoute.getVehicleContent());
        return params;
    }
}
