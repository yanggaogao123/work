package com.gci.schedule.driverless.dynamic.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.mapper.RepStationPassengerDynamicMapper;
import com.gci.schedule.driverless.dynamic.service.RepStationPassengerService;
import com.gci.schedule.driverless.dynamic.service.RouteStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("repStationPassengerDynamicService")
public class RepStationPassengerImpl implements RepStationPassengerService {

    @Autowired
    private RepStationPassengerDynamicMapper repStationPassengerDynamicMapper;

    @Autowired
    private RouteStationService routeStationDynamicService;

    @Value("${bigData.service.url}")
    public String GETDATAURL;

    @Override
    public RouteStationPassengerInfo getRouteStationPassangerInfo(String runDayNum, String direction, Long routeId) {

        List<RouteStationPassenger> list = repStationPassengerDynamicMapper.getRouteStationPassanger(runDayNum, direction, routeId);
        Map<Long, RouteSta> routeStaMap = routeStationDynamicService.getRouteStaMapByRouteId(routeId);
        for (int i = 0; i < list.size(); ) {
            RouteStationPassenger routeStationPassenger = list.get(i);
            if (routeStaMap.containsKey(routeStationPassenger.getRouteStaId())) {
                i++;
            } else {
                list.remove(i);
            }
        }
        RouteStationPassengerInfo routeStationPassengerInfo = dealWithRouteStationPassengerInfo(list, runDayNum, direction, routeId);
        return routeStationPassengerInfo;
    }

    @Override
    public RouteStationPassengerInfo dealWithRouteStationPassengerInfo(List<RouteStationPassenger> list, String runDayNum, String direction, Long routeId) {
        String result = routeStationDynamicService.getListByRouteId(Long.valueOf(routeId));
        JSONObject json = JSONObject.parseObject(result);
        JSONArray stationJsonArray = json.getJSONArray("data");

        RouteStationPassengerInfo routeStationPassengerInfo = new RouteStationPassengerInfo();
        routeStationPassengerInfo.setRouteId(routeId.longValue());
        routeStationPassengerInfo.setRunDayNum(runDayNum);
        routeStationPassengerInfo.setDirection(direction);

        List<Long> ids = repStationPassengerDynamicMapper.getRouteStationIdsByRouteId(routeId);
        //移除不在该线路站点列表的客流数据
        list.removeIf(item -> !ids.contains(item.getRouteStaId()));

        //站点信息赋值
        for (RouteStationPassenger repStationPassenger : list) {
            stationLabel:
            for (int i = 0; i < stationJsonArray.size(); i++) {
                JSONObject stationJson = stationJsonArray.getJSONObject(i);
                if (stationJson.getString("routeStationId") != null && stationJson.getString("routeStationId").equals(repStationPassenger.getRouteStaId().toString())) {
                    repStationPassenger.setRouteStationName(stationJson.getString("routeStationName"));
                    repStationPassenger.setOrderNumber(stationJson.getShort("orderNumber"));
                    break stationLabel;
                }
            }
        }

        //排序
        list.sort((o1, o2) -> {

            int flag = 0;
            if (flag == 0) {
                flag = o1.getRunTimeNum().compareTo(o2.getRunTimeNum());
            }
            if (flag == 0) {
                flag = o1.getDirection().compareTo(o2.getDirection());
            }
            if (flag == 0) {
                if (o1 != null && o1.getOrderNumber() != null && o2 != null && o2.getOrderNumber() != null) {
                    flag = o1.getOrderNumber().compareTo(o2.getOrderNumber());
                }

            }
            return flag;
        });
        //转成线路客流列表
        Short runTimeNum = null;
        List<List<RouteStationPassenger>> thisRunTimeNumList2DList = new ArrayList<>();
        List<RouteStationPassenger> thisRunTimeNumList = new ArrayList<RouteStationPassenger>();
        for (int i = 0; i < list.size(); i++) {
            Short thisRunTimeNum = list.get(i).getRunTimeNum();
            if (runTimeNum == null || !runTimeNum.equals(thisRunTimeNum)) {
                if (runTimeNum != null && !runTimeNum.equals(thisRunTimeNum)) {
                    thisRunTimeNumList2DList.add(thisRunTimeNumList);
                    thisRunTimeNumList = new ArrayList<>();
                }
                runTimeNum = thisRunTimeNum;

            }
            if (i == list.size() - 1) {
                thisRunTimeNumList2DList.add(thisRunTimeNumList);
            }
            thisRunTimeNumList.add(list.get(i));

        }
        routeStationPassengerInfo.setRouteStationPassenger2DList(thisRunTimeNumList2DList);

        return routeStationPassengerInfo;
    }

    private static final String UP_HOUR = "00-30";

    private static final String DOWN_HOUR = "30-60";

}
