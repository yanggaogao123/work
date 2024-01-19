package com.gci.schedule.driverless.service.schedule.impl;

import cn.hutool.core.convert.Convert;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.ScheduleCountParam;
import com.gci.schedule.driverless.bean.vo.ScheduleCountParam02;
import com.gci.schedule.driverless.mapper.DySchedulePlanDriverlessMapper;
import com.gci.schedule.driverless.service.schedule.BigDataService;
import com.gci.schedule.driverless.service.schedule.ScheduleCountService;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsAnchorService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ObjectStreamClass;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ScheduleCountServiceImpl implements ScheduleCountService {
    @Autowired
    private ScheduleParamsAnchorService scheduleParamsAnchorService;
    @Autowired
    private BigDataService bigDataService;
    @Autowired
    private DySchedulePlanDriverlessMapper dySchedulePlanDriverlessMapper;

    @Override
    public R getScheduleCountResult(ScheduleCountParam param) {
        List<Integer> timeList = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23);
        Collections.sort(timeList);
        ScheduleCountParam02 mainAnchorParams = getAnchorParams(param.getRouteId(),param.getRunDate());
        if(Objects.isNull(mainAnchorParams)){
            log.info("【仿真统计】-排班线路参数不存在，routeId:{}",param.getRouteId());
            return R.error("排班线路参数不存在");
        }
        ScheduleCountParam02 subAnchorParams = null;
        if(Objects.nonNull(param.getSupportRouteId())){
            subAnchorParams = getAnchorParams(param.getSupportRouteId(),param.getRunDate());
            if(Objects.isNull(subAnchorParams)){
                log.info("【仿真统计】-排班支援线路参数不存在，routeId:{},supportRouteId:{}",param.getRouteId(),param.getSupportRouteId());
                return R.error("排班支援线路参数不存在");
            }
        }
        DySchedulePlanDriverless mainRecord = new DySchedulePlanDriverless();
        DySchedulePlanDriverless subRecord = new DySchedulePlanDriverless();
        mainRecord.setRouteId(param.getRouteId());
        mainRecord.setPlanDate(param.getRunDate());
        mainRecord.setPlanType(param.getPlanType());
        subRecord.setPlanDate(param.getRunDate());
        if(Objects.nonNull(param.getSupportRouteId())){
            mainRecord.setStatus(ScheduleStatus.SUPPORTED_SCHEDULE.getValue());
            subRecord.setRouteId(param.getSupportRouteId());
            subRecord.setStatus(ScheduleStatus.SUPPORT_SCHEDULE.getValue());
        }else {
            mainRecord.setStatus(ScheduleStatus.DRIVERLESS_SCHEDULE.getValue());
            subRecord.setRouteId(param.getRouteId());
            subRecord.setStatus(ScheduleStatus.COMMON_SCHEDULE.getValue());
        }

        List<DySchedulePlanDriverless> mainScheduleList = dySchedulePlanDriverlessMapper.getScheduleList02(mainRecord);
        List<DySchedulePlanDriverless> subScheduleList = dySchedulePlanDriverlessMapper.getScheduleList02(subRecord);
        if(CollectionUtils.isEmpty(mainScheduleList)||CollectionUtils.isEmpty(subScheduleList)){
            log.info("【仿真统计】- 排班计划信息不存在，routeId:{}",param.getRouteId());
            return R.error("排班计划信息不存在");
        }
        List<StationPassenger> mainStationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(mainScheduleList.get(0).getPassengerData(),DateUtil.date_sdf),param.getRouteId().toString());
        List<StationPassenger> subStationPassengerList = bigDataService.getStationPassengerList(DateUtil.date2Str(subScheduleList.get(0).getPassengerData(),DateUtil.date_sdf),param.getRouteId().toString());
        if(CollectionUtils.isEmpty(mainStationPassengerList)||CollectionUtils.isEmpty(subStationPassengerList)){
            log.info("【仿真统计】-客流信息不存在，routeId:{}",param.getRouteId());
            return R.error("客流信息不存在");
        }
        List<StationPassenger> upMainStationPassengerList = mainStationPassengerList.stream().filter(e -> e.getDirection().equals("0")).collect(Collectors.toList());
        List<StationPassenger> downMainStationPassengerList = mainStationPassengerList.stream().filter(e -> e.getDirection().equals("1")).collect(Collectors.toList());
        List<StationPassenger> upSubStationPassengerList = subStationPassengerList.stream().filter(e -> e.getDirection().equals("0")).collect(Collectors.toList());
        List<StationPassenger> downSubStationPassengerList = subStationPassengerList.stream().filter(e -> e.getDirection().equals("1")).collect(Collectors.toList());
        Map<Short,List<StationPassenger>> upMainStationPassengerMap = upMainStationPassengerList.stream().collect(Collectors.groupingBy(StationPassenger::getPhour));
        Map<Short,List<StationPassenger>> downMainStationPassengerMap = downMainStationPassengerList.stream().collect(Collectors.groupingBy(StationPassenger::getPhour));
        Map<Short,List<StationPassenger>> upSubStationPassengerMap = upSubStationPassengerList.stream().collect(Collectors.groupingBy(StationPassenger::getPhour));
        Map<Short,List<StationPassenger>> downSubStationPassengerMap = downSubStationPassengerList.stream().collect(Collectors.groupingBy(StationPassenger::getPhour));

        //时段班次核载人数
        Map<String,Object> mainUploadPeopleNumMap = new HashMap<>();
        Map<String,Object> mainDownloadPeopleNumMap = new HashMap<>();
        Map<String,Object> subUploadPeopleNumMap = new HashMap<>();
        Map<String,Object> subDownloadPeopleNumMap = new HashMap<>();

        //时段客流人数
        Map<String,Object> mainUpPassengerMap = new HashMap<>();
        Map<String,Object> mainDownPassengerMap = new HashMap<>();
        Map<String,Object> subUpPassengerMap = new HashMap<>();
        Map<String,Object> subDownPassengerMap = new HashMap<>();

        //时段最高车内人数
        Map<String,Object> mainUpMaxPeopleMap = new HashMap<>();
        Map<String,Object> mainDownMaxPeopleMap = new HashMap<>();
        Map<String,Object> subUpMaxPeopleMap = new HashMap<>();
        Map<String,Object> subDownMaxPeopleMap = new HashMap<>();

        //时段发车班次
        Map<String,Object> mainUpClassesMap = new HashMap<>();
        Map<String,Object> mainDownClassesMap = new HashMap<>();
        Map<String,Object> subUpClassesMap = new HashMap<>();
        Map<String,Object> subDownClassesMap = new HashMap<>();

        //时段班次满载率
        Map<String,Object> mainUpFullPercentMap = new HashMap<>();
        Map<String,Object> mainDownFullPercentMap = new HashMap<>();
        Map<String,Object> subUpFullPercentMap = new HashMap<>();
        Map<String,Object> subDownFullPercentMap = new HashMap<>();

        //时段平均间隔
        Map<String,Object> mainUpIntervalMap = new HashMap<>();
        Map<String,Object> mainDownIntervalMap = new HashMap<>();
        Map<String,Object> subUpIntervalMap = new HashMap<>();
        Map<String,Object> subDownIntervalMap = new HashMap<>();

        //时段平均周转时间
        Map<String,Object> mainUpIntersiteMap = new HashMap<>();
        Map<String,Object> mainDownIntersiteMap = new HashMap<>();
        Map<String,Object> subUpIntersiteMap = new HashMap<>();
        Map<String,Object> subDownIntersiteMap = new HashMap<>();

        //报表内容统计
        Map<String,Object> mainUpWordMap = new HashMap<>();
        Map<String,Object> mainDownWordMap = new HashMap<>();

        Map<String,Object> subUpWordMap = new HashMap<>();
        Map<String,Object> subDownWordMap = new HashMap<>();

        for(Integer timeNum : timeList){
            //线路时段班次核载人数
            List<ScheduleParamsAnchor> mainAnchorList = mainAnchorParams.getAnchorList();
            ScheduleParamsRoute mainScheduleParamsRoute = mainAnchorParams.getScheduleParamsRoute();
            List<ScheduleParamsAnchor> finalMainUpAnchorList = mainAnchorList.stream().filter(e -> e.getDirection().equals("0")&&Objects.nonNull(e.getIntbeginTime())&&e.getIntbeginTime()<=timeNum*100
                    &&Objects.nonNull(e.getIntendTime())&&e.getIntendTime()>=(timeNum+1)*100).collect(Collectors.toList());
            List<ScheduleParamsAnchor> finalMainDownAnchorList = mainAnchorList.stream().filter(e -> e.getDirection().equals("1")&&Objects.nonNull(e.getIntbeginTime())&&e.getIntbeginTime()<=timeNum*100
                    &&Objects.nonNull(e.getIntendTime())&&e.getIntendTime()>=(timeNum+1)*100).collect(Collectors.toList());
            Integer mainUploadPeopleNum = 0;
            Integer mainDownloadPeopleNum = 0;
            if(!CollectionUtils.isEmpty(finalMainUpAnchorList)){
                ScheduleParamsAnchor mainUpAnchor = finalMainUpAnchorList.get(0);
                mainUploadPeopleNum = BigDecimal.valueOf(mainUpAnchor.getBusOccupancy()*mainScheduleParamsRoute.getVehicleContent()).divide(BigDecimal.valueOf(100),BigDecimal.ROUND_HALF_UP).intValue();
            }
            if(!CollectionUtils.isEmpty(finalMainDownAnchorList)){
                ScheduleParamsAnchor mainDownAnchor = finalMainDownAnchorList.get(0);
                mainDownloadPeopleNum = BigDecimal.valueOf(mainDownAnchor.getBusOccupancy()*mainScheduleParamsRoute.getVehicleContent()).divide(BigDecimal.valueOf(100),BigDecimal.ROUND_HALF_UP).intValue();
            }
            mainUploadPeopleNumMap.put(timeNum.toString(),mainUploadPeopleNum);
            mainDownloadPeopleNumMap.put(timeNum.toString(),mainDownloadPeopleNum);
            //支援线路时段班次核载人数
            Integer subUploadPeopleNum = 0;
            Integer subDownloadPeopleNum = 0;
            if(Objects.isNull(subAnchorParams)){
                subUploadPeopleNum = mainUploadPeopleNum;
                subDownloadPeopleNum = mainDownloadPeopleNum;
                subUploadPeopleNumMap.put(timeNum.toString(),mainUploadPeopleNum);
                subDownloadPeopleNumMap.put(timeNum.toString(),mainDownloadPeopleNum);
            }else {
                List<ScheduleParamsAnchor> subAnchorList = subAnchorParams.getAnchorList();
                ScheduleParamsRoute subScheduleParamsRoute = subAnchorParams.getScheduleParamsRoute();
                List<ScheduleParamsAnchor> finalSubUpAnchorList = subAnchorList.stream().filter(e -> e.getDirection().equals("0")&&Objects.nonNull(e.getIntbeginTime())&&e.getIntbeginTime()<=timeNum*100
                        &&Objects.nonNull(e.getIntendTime())&&e.getIntendTime()>=(timeNum+1)*100).collect(Collectors.toList());
                List<ScheduleParamsAnchor> finalSubDownAnchorList = subAnchorList.stream().filter(e -> e.getDirection().equals("1")&&Objects.nonNull(e.getIntbeginTime())&&e.getIntbeginTime()<=timeNum*100
                        &&Objects.nonNull(e.getIntendTime())&&e.getIntendTime()>=(timeNum+1)*100).collect(Collectors.toList());

                if(!CollectionUtils.isEmpty(finalSubUpAnchorList)){
                    ScheduleParamsAnchor subUpAnchor = finalSubUpAnchorList.get(0);
                    subUploadPeopleNum = BigDecimal.valueOf(subUpAnchor.getBusOccupancy()*subScheduleParamsRoute.getVehicleContent()).divide(BigDecimal.valueOf(100),BigDecimal.ROUND_HALF_UP).intValue();

                }
                if(!CollectionUtils.isEmpty(finalSubDownAnchorList)){
                    ScheduleParamsAnchor subDownAnchor = finalSubDownAnchorList.get(0);
                    subDownloadPeopleNum = BigDecimal.valueOf(subDownAnchor.getBusOccupancy()*subScheduleParamsRoute.getVehicleContent()).divide(BigDecimal.valueOf(100),BigDecimal.ROUND_HALF_UP).intValue();
                }
                subUploadPeopleNumMap.put(timeNum.toString(),subUploadPeopleNum);
                subDownloadPeopleNumMap.put(timeNum.toString(),subDownloadPeopleNum);
            }

            //线路时段客流人数,时段最高车内人数
            Integer upMainMax = 0;
            Integer downMainMax = 0;
            Integer mainUpPassenger = 0;
            Integer mainDownPassenger = 0;
            if(upMainStationPassengerMap.containsKey(Convert.toShort(timeNum))){
                mainUpPassenger = upMainStationPassengerMap.get(Convert.toShort(timeNum)).stream().mapToInt(StationPassenger::getCurpeople).sum();
                Optional<Integer> max = upMainStationPassengerMap.get(Convert.toShort(timeNum)).stream().map(StationPassenger::getCurpeople).reduce(Integer::max);
                upMainMax = max.get();
            }
            if(downMainStationPassengerMap.containsKey(Convert.toShort(timeNum))){
                mainDownPassenger = downMainStationPassengerMap.get(Convert.toShort(timeNum)).stream().mapToInt(StationPassenger::getCurpeople).sum();
                Optional<Integer> max = downMainStationPassengerMap.get(Convert.toShort(timeNum)).stream().map(StationPassenger::getCurpeople).reduce(Integer::max);
                downMainMax = max.get();
            }
            mainUpPassengerMap.put(timeNum.toString(),mainUpPassenger);
            mainUpMaxPeopleMap.put(timeNum.toString(),upMainMax);
            mainDownPassengerMap.put(timeNum.toString(),mainDownPassenger);
            mainDownMaxPeopleMap.put(timeNum.toString(),downMainMax);

            //支援线路时段客流人数,时段最高车内人数
            Integer upSubMax = 0;
            Integer downSubMax = 0;
            Integer subUpPassenger = 0;
            Integer subDownPassenger = 0;
            if(upSubStationPassengerMap.containsKey(Convert.toShort(timeNum))){
                subUpPassenger = upSubStationPassengerMap.get(Convert.toShort(timeNum)).stream().mapToInt(StationPassenger::getCurpeople).sum();
                Optional<Integer> max = upSubStationPassengerMap.get(Convert.toShort(timeNum)).stream().map(StationPassenger::getCurpeople).reduce(Integer::max);
                upSubMax = max.get();
            }
            if(downSubStationPassengerMap.containsKey(Convert.toShort(timeNum))){
                subDownPassenger = downSubStationPassengerMap.get(Convert.toShort(timeNum)).stream().mapToInt(StationPassenger::getCurpeople).sum();
                Optional<Integer> max = downSubStationPassengerMap.get(Convert.toShort(timeNum)).stream().map(StationPassenger::getCurpeople).reduce(Integer::max);
                downSubMax = max.get();
            }
            subUpMaxPeopleMap.put(timeNum.toString(),subUpPassenger);
            subUpMaxPeopleMap.put(timeNum.toString(),upSubMax);
            subDownPassengerMap.put(timeNum.toString(),subDownPassenger);
            subDownMaxPeopleMap.put(timeNum.toString(),downSubMax);



            //线路时段发车班次
            Long mainUpClasses = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).count();
            Long mainDownClasses = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).count();
            mainUpClassesMap.put(timeNum.toString(),mainUpClasses);
            mainDownClassesMap.put(timeNum.toString(),mainDownClasses);
            //支援线路时段发车班次
            Long subUpClasses = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).count();
            Long subDownClasses = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).count();
            subUpClassesMap.put(timeNum.toString(),subUpClasses);
            subDownClassesMap.put(timeNum.toString(),subDownClasses);

            //线路时段平均间隔
            Double mainUpInterval = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getInterval).average().orElse(0D);
            Double mainDownInterval = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getInterval).average().orElse(0D);
            mainUpIntervalMap.put(timeNum.toString(),mainUpInterval);
            mainDownIntervalMap.put(timeNum.toString(),mainDownInterval);

            //支援线路时段平均间隔
            Double subUpInterval = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getInterval).average().orElse(0D);
            Double subDownInterval = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getInterval).average().orElse(0D);
            subUpIntervalMap.put(timeNum.toString(),subUpInterval);
            subDownIntervalMap.put(timeNum.toString(),subDownInterval);

            //线路时段平均周转时间
            Double mainUpIntersite = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getFullTime).average().orElse(0D);
            Double mainDownIntersite = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getFullTime).average().orElse(0D);
            mainUpIntersiteMap.put(timeNum.toString(),Math.round(mainUpIntersite));
            mainDownIntersiteMap.put(timeNum.toString(),Math.round(mainDownIntersite));

            //支援线路时段平均周转时间
            Double subUpIntersite = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getFullTime).average().orElse(0D);
            Double subDownIntersite = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getFullTime).average().orElse(0D);
            subUpIntersiteMap.put(timeNum.toString(),Math.round(subUpIntersite));
            subDownIntersiteMap.put(timeNum.toString(),Math.round(subDownIntersite));

            //线路时段班次满载率
            Integer upMainfullPercent = 0;
            Integer downMainfullPercent = 0;
            if(mainUpClasses!=0){
                upMainfullPercent = BigDecimal.valueOf(upMainMax * 100).divide(BigDecimal.valueOf(mainUpClasses * mainScheduleList.get(0).getPassengerNum()),BigDecimal.ROUND_HALF_UP).intValue();
            }
            if(mainDownClasses!=0){
                downMainfullPercent = BigDecimal.valueOf(downMainMax * 100).divide(BigDecimal.valueOf(mainDownClasses * mainScheduleList.get(0).getPassengerNum()),BigDecimal.ROUND_HALF_UP).intValue();

            }
            mainUpFullPercentMap.put(timeNum.toString(),upMainfullPercent);
            mainDownFullPercentMap.put(timeNum.toString(),downMainfullPercent);

            //支援线路时段班次满载率
            Integer upSubfullPercent = 0;
            Integer downSubfullPercent = 0;
            if(subUpClasses!=0){
                upSubfullPercent = BigDecimal.valueOf(upSubMax * 100).divide(BigDecimal.valueOf(subUpClasses * subScheduleList.get(0).getPassengerNum()),BigDecimal.ROUND_HALF_UP).intValue();

            }
            if(subDownClasses!=0){
                downSubfullPercent = BigDecimal.valueOf(downSubMax * 100).divide(BigDecimal.valueOf(subDownClasses * mainScheduleList.get(0).getPassengerNum()),BigDecimal.ROUND_HALF_UP).intValue();

            }
            subUpFullPercentMap.put(timeNum.toString(),upSubfullPercent);
            subDownFullPercentMap.put(timeNum.toString(),downSubfullPercent);

            //表格内容
            Map<String,Object> mainUpWordRowMap = new HashMap<>();
            Map<String,Object> mainDownWordRowMap = new HashMap<>();
            Map<String,Object> subUpWordRowMap = new HashMap<>();
            Map<String,Object> subDownWordRowMap = new HashMap<>();
            Long mainUpSupportClasses = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0") && !e.getSupportClasses().equals(0)).count();
            Long mainDownSupportClasses = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1") && !e.getSupportClasses().equals(0)).count();
            Long subUpSupportClasses = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0") && !e.getSupportClasses().equals(0)).count();
            Long subDownSupportClasses = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1") && !e.getSupportClasses().equals(0)).count();
            //总班次
            mainUpWordRowMap.put("upClasses",mainUpClasses);
            mainDownWordRowMap.put("downClasses",mainDownClasses);
            subUpWordRowMap.put("upClasses",subUpClasses);
            subDownWordRowMap.put("downClasses",mainDownClasses);
            //无车或支援班次
            mainUpWordRowMap.put("upSupportClasses",mainUpSupportClasses);
            mainDownWordRowMap.put("downSupportClasses",mainDownSupportClasses);
            subUpWordRowMap.put("upSupportClasses",subUpSupportClasses);
            subDownWordRowMap.put("downSupportClasses",subDownSupportClasses);
            //总客流人次
            mainUpWordRowMap.put("upPassenger",mainUpPassenger);
            mainDownWordRowMap.put("downPassenger",mainDownPassenger);
            subUpWordRowMap.put("upPassenger",subUpPassenger);
            subDownWordRowMap.put("downPassenger",subDownPassenger);
            //最高车内人数
            mainUpWordRowMap.put("upMax",upMainMax);
            mainDownWordRowMap.put("downMax",downMainMax);
            subUpWordRowMap.put("upMax",upSubMax);
            subDownWordRowMap.put("downMax",downSubMax);
            //班次车流人数：时段内班次数*车厢核载人数*目标满载率（如75%）
            mainUpWordRowMap.put("uploadPeopleClasses",mainUploadPeopleNum*mainUpClasses);
            mainDownWordRowMap.put("downloadPeopleClasses",mainDownloadPeopleNum*mainDownClasses);
            subUpWordRowMap.put("uploadPeopleClasses",subUploadPeopleNum*subUpClasses);
            subDownWordRowMap.put("downloadPeopleClasses",subDownloadPeopleNum*subDownClasses);
            //平均单程时间
            mainUpWordRowMap.put("upIntersite",mainUpIntersite);
            mainDownWordRowMap.put("downIntersite",mainDownIntersite);
            subUpWordRowMap.put("upIntersite",subUpIntersite);
            subDownWordRowMap.put("downIntersite",subDownIntersite);
            //平均发班间隔
            mainUpWordRowMap.put("upInterval",mainUpInterval);
            mainDownWordRowMap.put("downInterval",mainDownInterval);
            subUpWordRowMap.put("upInterval",subUpInterval);
            subDownWordRowMap.put("downInterval",subDownInterval);
            //平均停站时间
            Double mainUpStopTime = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getStopTime).average().orElse(0D);
            Double mainDownStopTime = mainScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getStopTime).average().orElse(0D);
            Double subUpStopTime = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("0")).mapToDouble(DySchedulePlanDriverless::getStopTime).average().orElse(0D);
            Double subDownStopTime = subScheduleList.stream().filter(e -> e.getPlanTimeInt()>=timeNum*100 && e.getPlanTimeInt()<=(timeNum+1)*100 && e.getDirection().equals("1")).mapToDouble(DySchedulePlanDriverless::getStopTime).average().orElse(0D);
            mainUpWordRowMap.put("upStopTime",mainUpStopTime);
            mainDownWordRowMap.put("downStopTime",mainDownStopTime);
            subUpWordRowMap.put("upStopTime",subUpStopTime);
            subDownWordRowMap.put("downStopTime",subDownStopTime);

            mainUpWordMap.put(timeNum.toString(),mainUpWordRowMap);
            mainDownWordMap.put(timeNum.toString(),mainDownWordRowMap);
            subUpWordMap.put(timeNum.toString(),subUpWordRowMap);
            subDownWordMap.put(timeNum.toString(),subDownWordRowMap);

        }
        ScheduleCountVo vo = new ScheduleCountVo();
        vo.setMainUploadPeopleNumMap(mainUploadPeopleNumMap);
        vo.setMainDownloadPeopleNumMap(mainDownloadPeopleNumMap);
        vo.setSubUploadPeopleNumMap(subUploadPeopleNumMap);
        vo.setSubDownloadPeopleNumMap(subDownloadPeopleNumMap);
        vo.setMainUpPassengerMap(mainUpPassengerMap);
        vo.setMainDownPassengerMap(mainDownPassengerMap);
        vo.setSubUpPassengerMap(subUpPassengerMap);
        vo.setSubDownPassengerMap(subDownPassengerMap);
        vo.setMainUpMaxPeopleMap(mainUpMaxPeopleMap);
        vo.setMainDownMaxPeopleMap(mainDownMaxPeopleMap);
        vo.setSubUpMaxPeopleMap(subUpMaxPeopleMap);
        vo.setSubDownMaxPeopleMap(subDownMaxPeopleMap);
        vo.setMainUpClassesMap(mainUpClassesMap);
        vo.setMainDownClassesMap(mainDownClassesMap);
        vo.setSubUpClassesMap(subUpClassesMap);
        vo.setSubDownClassesMap(subDownClassesMap);
        vo.setMainUpFullPercentMap(mainUpFullPercentMap);
        vo.setMainDownFullPercentMap(mainDownFullPercentMap);
        vo.setSubUpFullPercentMap(subUpFullPercentMap);
        vo.setSubDownFullPercentMap(subDownFullPercentMap);
        vo.setMainUpIntervalMap(mainUpIntervalMap);
        vo.setMainDownIntervalMap(mainDownIntervalMap);
        vo.setSubUpIntervalMap(subUpIntervalMap);
        vo.setSubDownIntervalMap(subDownIntervalMap);
        vo.setMainUpIntersiteMap(mainUpIntersiteMap);
        vo.setMainDownIntersiteMap(mainDownIntersiteMap);
        vo.setSubUpIntersiteMap(subUpIntersiteMap);
        vo.setSubDownIntersiteMap(subDownIntersiteMap);
        vo.setMainUpWordMap(mainUpWordMap);
        vo.setMainDownWordMap(mainDownWordMap);
        vo.setSubUpWordMap(subUpWordMap);
        vo.setSubDownWordMap(subDownWordMap);
        return R.ok().put("data",vo);
    }

    private ScheduleCountParam02 getAnchorParams(Long routeId,Date runDate) {
        ScheduleCountParam02 param02 = new ScheduleCountParam02();
        //线路停站时间参数
        List<ScheduleParamsAnchor> anchorList = scheduleParamsAnchorService.selectParamsByRouteId(routeId);
        if(CollectionUtils.isEmpty(anchorList)){
            log.info("【仿真统计】-排班线路停站时间参数不存在，routeId:{}",routeId);
            return null;
        }
        Map<Integer,List<ScheduleParamsAnchor>> anchorMap = anchorList.stream().collect(Collectors.groupingBy(ScheduleParamsAnchor::getTemplateId));
        //线路配车参数
        List<ScheduleParamsRoute> paramsRouteList = scheduleParamsAnchorService.selectScheduleParamsByRouteId(routeId);
        if(CollectionUtils.isEmpty(paramsRouteList)){
            log.info("【仿真统计】-排班线路配车参数不存在，routeId:{}",routeId);
            return null;
        }
        Map<Integer,List<ScheduleParamsRoute>> paramsRouteMap = paramsRouteList.stream().collect(Collectors.groupingBy(ScheduleParamsRoute::getTemplateId));
        //排班模板参数
        List<ScheduleTemplateDetail> templateDetailList = scheduleParamsAnchorService.selectTemplateByRouteId(routeId);
        if(CollectionUtils.isEmpty(templateDetailList)){
            log.info("【仿真统计】-排班线路模板参数不存在，routeId:{}",routeId);
            return null;
        }
        Map<Integer,List<ScheduleTemplateDetail>> templateDetailMap = templateDetailList.stream().collect(Collectors.groupingBy(ScheduleTemplateDetail::getApplyDay));
        Calendar cal = Calendar.getInstance();
        cal.setTime(runDate);
        Integer applyDay = cal.get(Calendar.DAY_OF_WEEK);
        Integer templateId = templateDetailMap.get(applyDay).get(0).getTemplateId();
        List<ScheduleParamsAnchor> anchorTemplateList = anchorMap.get(templateId);
        ScheduleParamsRoute scheduleParamsRoute = paramsRouteMap.get(templateId).get(0);
        param02.setAnchorList(anchorTemplateList);
        param02.setScheduleParamsRoute(scheduleParamsRoute);
        return param02;
    }
}
