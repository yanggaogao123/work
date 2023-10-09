package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.scheduleD.StationPassenger;
import com.gci.schedule.driverless.service.schedule.BigDataService;
import com.gci.schedule.driverless.util.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BigDataServiceImpl implements BigDataService {
    @Value("${bigData.service.url}")
    public String GETDATAURL;

    @Override
    public List<StationPassenger> getStationPassengerList(String date, String routeId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", "appName");
        jsonObject.put("businessID", "00109");
        jsonObject.put("page", "1");
        jsonObject.put("pageSize", "10");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("routeId", routeId);
        jsonObject1.put("strDate", date);
        jsonObject.put("data", jsonObject1);
        String result = null;
        System.out.println(JSONObject.toJSONString(jsonObject));
        try {
            result = HttpUtils.Post(GETDATAURL, JSONObject.toJSONString(jsonObject));
        } catch (Exception e) {
            log.info("大数据客流请求信息，param:{},大数据客流返回信息，resp:{}", JSONObject.toJSONString(jsonObject), result);
            log.error("大数据客流请求异常", e);
            e.printStackTrace();
        }
        log.info("大数据客流请求信息，param:{},大数据客流返回信息，resp:{}", JSONObject.toJSONString(jsonObject), result);
        if (Objects.isNull(result)) {
            return null;
        }
        JSONObject responJson = JSONObject.parseObject(result);
        JSONArray jsonArray = null;
        if (responJson.getInteger("retCode") == 0) {
            JSONObject retData = responJson.getJSONObject("retData");
            jsonArray = retData.getJSONArray("list");
        }
        if (Objects.isNull(jsonArray)) {
            return null;
        }
        List<StationPassenger> list = new ArrayList<>();
        jsonArray.stream().forEach((o) -> {
                    JSONObject reqdata = (JSONObject) o;
                    StationPassenger stationPassenger = reqdata.toJavaObject(StationPassenger.class);
                    list.add(stationPassenger);
                }
        );
        return list;
    }
}
