package com.gci.schedule.driverless.dynamic.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.dynamic.bean.StationPassenger;
import com.gci.schedule.driverless.dynamic.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("stationPassengerTaskDynamicService")
public class StationPassengerTaskImpl implements StationPassengerTaskService {

    @Value("${bigData.service.url}")
    public String GETDATAURL;

    @Override
    public List<StationPassenger> getStationPassengerList(String date, String routeId) {//原线路客流
        JSONArray data = StationPassengerTaskService.getData(GETDATAURL, date, routeId);
        if (data == null) return null;
        if (data.isEmpty()) return null;
        List<StationPassenger> list = new ArrayList<>();
        data.stream().forEach((o) -> {
                    JSONObject reqdata = (JSONObject) o;
                    StationPassenger stationPassenger = reqdata.toJavaObject(StationPassenger.class);
                    list.add(stationPassenger);
                }
        );
        return list;
    }
}
