package com.gci.schedule.driverless.dynamic.service.impl;

import com.gci.schedule.driverless.dynamic.bean.Route;
import com.gci.schedule.driverless.dynamic.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.util.JsonUtil;
import com.gci.schedule.driverless.dynamic.util.RestTemplateUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: yaozy
 * @Description:
 * @Date: Created in 2024/03/27
 */
@Service("routeDynamicService")
public class RouteServiceImpl implements RouteService {

    protected static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);

    @Value("${aptsBase.server.url}")
    private String aptsBaseUrl;

    @Value("${dispatchServer.server.url}")
    private String dispatchServerUrl;

    @Value("${getRouteUpDownInfo}")
    private String getRouteUpDownInfo;

    @Value("${dispatchServer.route.getByRouteId}")
    private String getByRouteId;

    @Override
    public String getRouteUpDownInfo(Integer routeId) {
        Map map = new HashMap<>();
        map.put("routeId", routeId);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity entity = RestTemplateUtil.getHttpEntityWithJsonHendersByMap(map);
        String result = restTemplate.postForObject(aptsBaseUrl + "/" + getRouteUpDownInfo, entity, String.class);
        return result;
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
            logger.error(e.getMessage(), e);
            logger.error("url = " + url + " error ");
            throw new MyException("-1","获取线路资料失败；routeId="+routeId);
        }
        return route;
    }

}
