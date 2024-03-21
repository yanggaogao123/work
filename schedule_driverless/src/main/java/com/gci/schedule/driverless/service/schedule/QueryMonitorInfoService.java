package com.gci.schedule.driverless.service.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface QueryMonitorInfoService {
    JSONArray getForecastPlan(String routeIdStr, String userId);
    JSONObject getTripParam(String routeIdStr, String userId);
    String saveTripParam(Map<String, Object> param);
    JSONObject redispatchList(String routeId, String userId, String userType);
    String getRefreshTime(String refreshTimeType);
    JSONObject dispatchTaskFromMainStation(String routeId, String direction, String sessionId, String path);
    String reCalculByLost(Map<String, Object> param);
    String routeReDispatch(String paramJson);
    String getFrPlanList(String routeId);
    String saveFrPlanList(Map<String, Object> param);
    String getDistance(String lon, String lat, String stationId);

}
