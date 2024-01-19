package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.scheduleD.Route;
import com.gci.schedule.driverless.bean.scheduleD.RouteUpDownInfo;
import com.gci.schedule.driverless.bean.scheduleD.StationPassenger;
import com.gci.schedule.driverless.component.RouteListCacheComponent;
import com.gci.schedule.driverless.service.schedule.RouteService;
import com.gci.schedule.driverless.util.HttpUtils;
import com.gci.schedule.driverless.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class RouteServiceImpl implements RouteService {
    @Value("${aptsBase.server.url}")
    private String aptsBaseUrl;
    @Value("${getRouteUpDownInfo}")
    private String getRouteUpDownInfo;

    @Value("${dispatch.server.url}")
    private String dispatchServerUrl;

    @Value("${dispatchServer.route.getByRouteId}")
    private String getByRouteId;

    @Autowired
    private RouteListCacheComponent routeListCacheComponent;

    @Override
    public List<RouteUpDownInfo> getRouteUpDownInfo(Long routeId) {
        Map map = new HashMap<>();
        map.put("routeId", routeId);
        String result = null;
        try {
            result = HttpUtils.Post(aptsBaseUrl + "/" + getRouteUpDownInfo, JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.info("获取线路首末班时间请求信息，param:{},返回信息，resp:{}", JSONObject.toJSONString(map), result);
            log.error("获取线路首末班时间请求异常", e);
            e.printStackTrace();
        }
        log.info("获取线路首末班时间请求信息，param:{},返回信息，resp:{}", JSONObject.toJSONString(map), result);

        JSONObject responJson = JSONObject.parseObject(result);
        JSONArray jsonArray = responJson.getJSONArray("retData");
        /*if (responJson.getInteger("retCode") == 0) {
            JSONObject retData = responJson.getJSONObject("retData");
            jsonArray = retData.getJSONArray("list");
        }*/
        if (Objects.isNull(jsonArray)) {
            return null;
        }
        List<RouteUpDownInfo> list = new ArrayList<>();
        jsonArray.stream().forEach((o) -> {
                    JSONObject reqdata = (JSONObject) o;
                    RouteUpDownInfo routeUpDownInfo = reqdata.toJavaObject(RouteUpDownInfo.class);
                    list.add(routeUpDownInfo);
                }
        );
        return list;
    }

    @Override
    public List<Map> getRouteListNew(String organId) {
        return routeListCacheComponent.getFinalRouteList(organId);
    }

    @Override
    public Route getRouteByRouteId(Long routeId) {
        RestTemplate restTemplate = new RestTemplate();
        String result = null;
        Route route = null;
        String url = dispatchServerUrl + "/" + getByRouteId + "/" + routeId;
        try {
            result = restTemplate.getForObject(url, String.class);
            route = JsonUtil.getObjectByKey(result, Route.class, "data");
        } catch (Exception e) {
            log.error("获取线路信息异常,e:{}",e);
        }
        return route;
    }

    @Override
    public String getRouteUpDownInfo2(Long routeId) {

        /*Map map = new HashMap<>();
        map.put("routeId", routeId);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity entity = RestTemplateUtil.getHttpEntityWithJsonHendersByMap(map);
        String result = restTemplate.postForObject(aptsBaseUrl + "/" + getRouteUpDownInfo, entity, String.class);*/
        Map map = new HashMap<>();
        map.put("routeId", routeId);
        String result = null;
        try {
            result = HttpUtils.Post(aptsBaseUrl + "/" + getRouteUpDownInfo, JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.info("获取线路首末班时间请求信息，param:{},返回信息，resp:{}", JSONObject.toJSONString(map), result);
            log.error("获取线路首末班时间请求异常", e);
            e.printStackTrace();
        }
        log.info("获取线路首末班时间请求信息，param:{},返回信息，resp:{}", JSONObject.toJSONString(map), result);
        return result;
    }

}
