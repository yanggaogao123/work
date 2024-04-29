package com.gci.schedule.driverless.task;

import cn.hutool.core.convert.Convert;
import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.bs.TriplogSimpleDto;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessConfig;
import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;
import com.gci.schedule.driverless.bean.scheduleD.RuningScheduleVo;
import com.gci.schedule.driverless.mapper.DyDriverlessConfigMapper;
import com.gci.schedule.driverless.mapper.DySchedulePlanDriverlessMapper;
import com.gci.schedule.driverless.mapper.TriplogMapper;
import com.gci.schedule.driverless.service.common.SupportPlanService;
import com.gci.schedule.driverless.service.schedule.RunBusService;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UpdateSupportPlanTask implements Runnable{
    @Autowired
    private RunBusService runBusService;
    @Autowired
    private ScheduleServerService scheduleServerService;
    @Autowired
    private DySchedulePlanDriverlessMapper dySchedulePlanDriverlessMapper;
    @Autowired
    private DyDriverlessConfigMapper dyDriverlessConfigMapper;
    @Autowired
    private SupportPlanService supportPlanService;
    @Autowired
    private TriplogMapper triplogMapper;

    @SneakyThrows
    @Async("asyncExecutor")
    @Scheduled(cron = "0 0/5 * * * ? ")
    @Override
    public void run() {
        log.info("【更新简图支援计划】-UpdateSupportPlanTask - Starting...");
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteNameKey(null);
        if (CollectionUtils.isEmpty(configList)){
            log.info("【更新简图支援计划】- 线路配置信息不存在！");
            return;
        }
        Date planDate = DateUtil.getDailyStartDate(new Date());
        for(DyDriverlessConfig config : configList){
            Long routeId = config.getRouteId();
            DySchedulePlanDriverless record = new DySchedulePlanDriverless();
            record.setRouteId(routeId);
            record.setPlanDate(planDate);
            List<DySchedulePlanDriverless> driverlessList = dySchedulePlanDriverlessMapper.getScheduleList02(record);
            if(CollectionUtils.isEmpty(driverlessList)){
                log.info("【更新简图支援计划】- 计划不存在，routeId:{}",routeId);
                continue;
            }
            //如果预设和最优同时存在，优先拿预设
            List<DySchedulePlanDriverless> finalDriverlessList = driverlessList.stream().filter(e -> e.getPlanType().equals(2)&& Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(finalDriverlessList)){
                finalDriverlessList = driverlessList.stream().filter(e -> Objects.nonNull(e.getSupportClasses())&&e.getSupportClasses()>0).collect(Collectors.toList());
            }
            if(CollectionUtils.isEmpty(finalDriverlessList)){
                log.info("【更新简图支援计划】- 支援计划不存在，routeId:{}",routeId);
                continue;
            }
            List<RunBus> runBuses = runBusService.getByRoute(Convert.toLong(routeId));
            Map<Long,List<RunBus>> runBusMap = runBuses.stream().collect(Collectors.groupingBy(RunBus::getBusId));
            List<RuningScheduleVo> redisPlanList = supportPlanService.select(routeId);
            Map<Long,List<RuningScheduleVo>> redisPlanMap = null;
            if(!CollectionUtils.isEmpty(redisPlanList)){
                redisPlanMap = redisPlanList.stream().collect(Collectors.groupingBy(RuningScheduleVo::getScheduleId));
            }
            List<TriplogSimpleDto> tripLogList = triplogMapper.selectListByRouteId(routeId,DateUtil.getDailyStartDate(new Date()));
            Map<Long,List<TriplogSimpleDto>> tripLogMap = null;
            if(!CollectionUtils.isEmpty(tripLogList)){
                tripLogMap = tripLogList.stream().filter(e -> Objects.nonNull(e.getTriplogEndTime())).collect(Collectors.groupingBy(TriplogSimpleDto::getBusId));
            }
            //List<RuningScheduleVo> supportInfo = new ArrayList<>();
            for(DySchedulePlanDriverless driverless : finalDriverlessList){
                if(Objects.isNull(driverless.getBusId())){
                    log.info("【更新简图支援计划】- 支援计划无挂车信息，routeId:{},scheduleId:{}",routeId,driverless.getScheduleId());
                    continue;
                }
                List<RunBus> runBusList = runBusMap.get(driverless.getBusId());
                RuningScheduleVo redisVo = null;
                if(Objects.nonNull(redisPlanMap)){
                    redisVo = redisPlanMap.get(driverless.getScheduleId()).get(0);
                    if(Objects.nonNull(redisVo)&&(Objects.equals(redisVo.getStatus(),3)||Objects.equals(redisVo.getOutStatus(),2))){
                        continue;
                    }
                }
                if(CollectionUtils.isEmpty(runBusList)||!runBusList.get(0).getRouteId().equals(routeId)){
                    continue;
                }
                RunBus runBus = runBusList.get(0);
                RuningScheduleVo vo = new RuningScheduleVo();
                vo.setScheduleId(driverless.getScheduleId());
                vo.setBusId(driverless.getBusId());
                vo.setBusName(driverless.getBusName());
                vo.setDirection(driverless.getDirection());
                vo.setEmployeeName(runBus.getEmployeeName());
                vo.setInterval(driverless.getInterval());
                vo.setPlanTime(DateUtil.date2Str(driverless.getPlanTime(),DateUtil.hh_mm));
                vo.setTripEndTime(DateUtil.date2Str(driverless.getTripEndTime(),DateUtil.hh_mm));
                vo.setOutStatus(1);
                vo.setDriverlessPlanTime(driverless.getPlanTime());
                Double fullTime;
                if(runBus.getDirection().equals(driverless.getDirection())){
                    fullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), driverless.getDirection(),
                            runBus.getFirstRouteStaId(), runBus.getLastRouteStaId(), driverless.getPlanTime());
                }else {
                    fullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), driverless.getDirection(),
                            runBus.getLastRouteStaId(), runBus.getFirstRouteStaId(), driverless.getPlanTime());
                }
                vo.setFullTime(Convert.toInt(fullTime/60));
                if(Constant.RunBusStatus.DISPATCHED.equals(runBus.getRunStatus())){
                    vo.setStatus(1);
                }else if(Constant.RunBusStatus.ON_TRIP.equals(runBus.getRunStatus())){
                    Double runingFullTime = scheduleServerService.getIntersiteTime(runBus.getRouteId(), runBus.getDirection(),
                            runBus.getFirstRouteStaId(), runBus.getLastRouteStaId(), runBus.getTripBeginTime());
                    long realTripEndTime = runBus.getTripBeginTime().getTime()+Convert.toLong(runingFullTime*1000);
                    long limitTime = Math.abs(runBus.getTripBeginTime().getTime() - driverless.getPlanTime().getTime());
                    vo.setTripBeginTime(DateUtil.date2Str(runBus.getTripBeginTime(),DateUtil.hh_mm));
                    vo.setTripEndTime(DateUtil.date2Str(new Date(realTripEndTime),DateUtil.hh_mm));
                    vo.setStatus(2);
                    if(limitTime>30*60*1000){
                        RuningScheduleVo outVo = vo.clone();
                        outVo.setStatus(1);
                        outVo.setOutStatus(2);
                        vo.setPlanTime(null);
                        supportPlanService.add(outVo,routeId);
                    }
                }else {
                    //取路单的结束时间作为真实的结束时间
                    String tripEndTime = null;
                    if(Objects.nonNull(tripLogMap) && tripLogMap.containsKey(driverless.getBusId())){
                        List<TriplogSimpleDto> busTripLogList = tripLogMap.get(driverless.getBusId());
                        busTripLogList.sort(Comparator.comparing(TriplogSimpleDto::getTriplogEndTime).reversed());
                        tripEndTime = DateUtil.date2Str(busTripLogList.get(0).getTriplogEndTime(),DateUtil.hh_mm);
                    }
                    //设置执行完成状态
                    vo.setStatus(3);
                    if(Objects.nonNull(tripEndTime)){
                        vo.setTripEndTime(tripEndTime);
                    }
                    if(Objects.nonNull(redisVo)&&Objects.equals(redisVo.getStatus(),2)){
                        redisVo.setStatus(3);
                        if(Objects.nonNull(tripEndTime)){
                            redisVo.setTripEndTime(tripEndTime);
                        }
                        vo = redisVo;
                    }
                }
                supportPlanService.add(vo,routeId);
            }

        }

        log.info("【更新简图支援计划】-UpdateSupportPlanTask - Ending...");
    }

    private List<DyDriverlessConfig> getDyDriverlessConfig(){
        List<DyDriverlessConfig> configList = dyDriverlessConfigMapper.selectByRouteNameKey(null);
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
        return configList;
    }
}
