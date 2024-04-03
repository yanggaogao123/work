package com.gci.schedule.driverless.dynamic.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.dynamic.bean.StationPassenger;
import com.gci.schedule.driverless.dynamic.util.HttpClientUtils;

import java.util.*;

public interface StationPassengerTaskService {

    static JSONArray getData(String url, String date, String routeId) {//原线路客流
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", "appName");
        jsonObject.put("businessID", "00109");
        jsonObject.put("page", "1");
        jsonObject.put("pageSize", "10");
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("routeId", routeId);
        jsonObject1.put("strDate", date);
        jsonObject.put("data", jsonObject1);
        try {
            JSONObject responJson = HttpClientUtils.httpPost(url, jsonObject);
            if (responJson.getInteger("retCode") == 0) {
                JSONObject retData = responJson.getJSONObject("retData");
                return retData.getJSONArray("list");
            }else {
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }

    List<StationPassenger> getStationPassengerList(String date, String routeId);

}
