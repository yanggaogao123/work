package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.service.schedule.QueryMonitorInfoService;
import com.gci.schedule.driverless.util.HttpClientUtils;
import com.gci.schedule.driverless.util.HttpUtil;
import com.gci.schedule.driverless.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("queryMonitorInfoService")
public class QueryMonitorInfoServiceImpl implements QueryMonitorInfoService {
    @Value("${dispatch.auto.server.url}")
    private String DISPATCH_URL;
    @Value("${getForecastPlans}")
    private String getForecastPlans;
    @Value("${getTripParam}")
    private String getTripParam;
    @Value("${saveTripParam}")
    private String saveTripParam;
    @Value("${redispatchList}")
    private String redispatchList;
    @Value("${getRefreshTime}")
    private String getRefreshTime;
    @Value("${schedule.dispatch.mainStationTaskFromMainStation}")
    private String mainStationTaskFromMainStation;
    @Value("${reCalculByLost}")
    private String reCalculByLost;
    @Value("${routeReDispatch}")
    private String routeReDispatch;
    @Value("${getFrPlanList}")
    private String getFrPlanList;
    @Value("${saveFrPlanList}")
    private String saveFrPlanList;
    @Value("${getDistance}")
    private String getDistance;



    @Override
    public JSONArray getForecastPlan(String routeIdStr, String userId) {
        String result = HttpUtil.getString(DISPATCH_URL + getForecastPlans + "/" + routeIdStr+"/"+userId);
        JSONArray array = JSONObject.parseObject(result).getJSONArray("data");
        return array;
    }

    @Override
    public JSONObject getTripParam(String routeIdStr, String userId) {
        String result = HttpUtil.getString(DISPATCH_URL + getTripParam + "/" + routeIdStr+"/"+userId);
        JSONObject dataObj = JSONObject.parseObject(result).getJSONObject("data");
        return dataObj;
    }

    @Override
    public JSONObject redispatchList(String routeId, String userId, String userType) {
        String uri = DISPATCH_URL + redispatchList + "/" + routeId+"/"+userId+"/"+userType;
        String result = HttpUtil.getString(uri);
        log.info("重排数据返回结果:{},请求地址:{},routeId:{}",result,uri,routeId);
        JSONObject dataObj = JSONObject.parseObject(result).getJSONObject("data");
        return dataObj;
    }

    @Override
    public String getRefreshTime(String refreshTimeType) {
        String result = HttpUtil.getString(DISPATCH_URL + getRefreshTime + "/" + refreshTimeType);
        return result;
    }

    @Override
    public JSONObject dispatchTaskFromMainStation(String routeId, String direction, String sessionId, String path) {
        JSONObject result = null;
        try {
            String url = mainStationTaskFromMainStation;
            url = url.replace("{1}", routeId).replace("{2}", direction);
            result = HttpClientUtils.httpGet(url);
        } catch (Exception e) {
            log.error("【dispatchTaskFromMainStation】 exception" + e);
        }
        return result;
    }

    @Override
    public String reCalculByLost(Map<String, Object> param) {
        String result = HttpUtil.submit(DISPATCH_URL+reCalculByLost,JSONObject.toJSONString(param));
        return result;
    }

    @Override
    public String routeReDispatch(String paramJson) {
        return HttpUtil.submit(DISPATCH_URL+routeReDispatch,paramJson);
    }

    @Override
    public String getFrPlanList(String routeId) {
        return HttpUtil.getString(DISPATCH_URL + getFrPlanList + "/" + routeId);
    }

    @Override
    public String saveFrPlanList(Map<String, Object> param) {
        return HttpUtil.submit(DISPATCH_URL+saveFrPlanList,JSONObject.toJSONString(param));
    }

    @Override
    public String getDistance(String lon, String lat, String stationId) {
        return HttpUtil.getString(DISPATCH_URL + getDistance + "/" + lon+"/"+lat+"/"+stationId);
    }

    @Override
    public String saveTripParam(Map<String, Object> param) {
        return HttpUtil.submit(DISPATCH_URL+saveTripParam,JSON.toJSONString(param));
    }


}
