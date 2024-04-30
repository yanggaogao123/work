package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.RunBusShiftType;
import com.gci.schedule.driverless.feign.RunBusApp;
import com.gci.schedule.driverless.service.schedule.RunBusService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RunBusServiceImpl implements RunBusService {

    @Autowired
    private RunBusApp runBusApp;



    @Override
    public List<RunBus> getByRoute(Long routeId) {
        return runBusApp.getByRoute(routeId).getData();
    }

    @Override
    public RunBus getByBus(Long busId) {
        return runBusApp.getByBus(busId).getData();
    }

    @Override
    public List<RunBusShiftType> getShiftTypeByRoute(Long routeId) {
        return runBusApp.getShiftTypeByRoute(routeId).getData();
    }

    @Override
    public void fillingShiftType(List<RunBus> runBuses) {
        if (CollectionUtils.isEmpty(runBuses)) {
            return;
        }
        List<RunBusShiftType> busShiftTypes = getShiftTypeByRoute(runBuses.get(0).getRouteId());
        log.info("单双斑标识，busName:{},routeId:{},busShiftTypes:{}", runBuses.get(0).getBusName(), runBuses.get(0).getRouteId(), JSONObject.toJSONString(busShiftTypes));
        if (CollectionUtils.isEmpty(busShiftTypes)) {
            return;
        }
        Map<Long, RunBusShiftType> busShiftTypeMap = busShiftTypes.stream().collect(Collectors.toMap(RunBusShiftType::getBusId, t -> t));
        runBuses.stream()
                .filter(r -> busShiftTypeMap.containsKey(r.getBusId()))
                .forEach(r -> r.setShiftType(busShiftTypeMap.get(r.getBusId()).getShiftType()));
    }

}
