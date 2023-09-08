package com.gci.schedule.driverless.service.schedule.impl;

import cn.hutool.core.convert.Convert;
import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.Direction;
import com.gci.schedule.driverless.bean.enums.StationMark;
import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;
import com.gci.schedule.driverless.bean.scheduleD.Route;
import com.gci.schedule.driverless.bean.scheduleD.RouteSta;
import com.gci.schedule.driverless.feign.AptsBaseApp;
import com.gci.schedule.driverless.feign.DispatchApp;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
                        schedule.setTripEndTime(new Date(schedule.getPlanTime().getTime()+Convert.toLong(upFullTime)*1000));
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

            if(!isBreakDown){
                //下行车排班
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
            if(){
                break;
            }
        }

        return null;
    }
}
