package com.gci.schedule.driverless.service.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.XxtForecast;
import com.gci.schedule.driverless.bean.common.ApiResp;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.feign.WxxtApp;
import com.gci.schedule.driverless.feign.WxxtApp2;
import com.gci.schedule.driverless.service.server.RedisService;
import com.gci.schedule.driverless.service.server.WxxtService;
import com.gci.schedule.driverless.util.DateUtil;
import com.gci.schedule.driverless.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class WxxtServiceImpl implements WxxtService {

    @Value("${wxxt.server.appid}")
    private String wxxtAppid;
    @Value("${wxxt.server.appkey}")
    private String wxxtAppkey;
    @Value("${forecast.route.key}")
    private String forecastRouteKey;
    @Autowired
    private RedisService redisService;

    @Autowired
    private WxxtApp wxxtApp;

    @Autowired
    private WxxtApp2 wxxtApp2;

    @Override
    public XxtForecast getForecastByRunBus(RunBus r) {

        Date now = new Date();

        if (Constant.RunBusStatus.NOT_DISPATCH.equals(r.getRunStatus())
                || Constant.RunBusStatus.UN_RUN.equals(r.getRunStatus())
                || r.getTripBeginTime() == null) {
            Date arrivalTime = now;
            if (r.getAdTime() != null && r.getCkTime() != null) {
                arrivalTime = r.getAdTime().before(r.getCkTime()) ?
                        (r.getCkTime().before(now) ? r.getCkTime() : now) : r.getAdTime();
            }
            return XxtForecast.builder()
                    .forecastType("1")
                    .orderNum(0)
                    .wxxtForecastTime(now)
                    .forecastTime(DateUtil.subSeconds(arrivalTime.getTime()))
                    .stationCount(0)
                    .predictTime(0D)
                    .build();
        }

        List<String> routeCodes = select(forecastRouteKey);
        if (!CollectionUtils.isEmpty(routeCodes) && routeCodes.contains(r.getRouteCode())) {
            return getForecast2(r, r.getLastRouteStaId());
        }
        return getForecast(r, r.getLastRouteStaId());
    }

    @Override
    public XxtForecast getForecast(RunBus runBus, Long lastRouteStaId) {

        Date now = new Date();
        Date tripBeginTime = runBus.getTripBeginTime();
        HashMap<String, Object> param = new HashMap<String, Object>() {{
            put("routeId", runBus.getRouteId());
            put("busId", runBus.getBusId());
            put("routeSubId", runBus.getRouteSubId());
            put("serviceType", runBus.getServiceType());
            put("runStatus", runBus.getRunStatus());
            put("tripStartTime", DateUtil.date2Str(tripBeginTime, DateUtil.yyyyMMdd_HHmmss));
            put("firstRouteStaId", Constant.RunBusStatus.DISPATCHED.equals(runBus.getRunStatus()) ?
                    runBus.getFirstRouteStaId() : runBus.getRouteStationId());
            put("lastRouteStaId", lastRouteStaId);
            put("adTime", DateUtil.date2Str(runBus.getAdTime(), DateUtil.yyyyMMdd_HHmmss));
        }};

        ApiResp<JSONObject> resp;
        try {
            resp = wxxtApp.getWaitTimeDd(SignUtil.toSignParam(param, wxxtAppid, wxxtAppkey));
        } catch (Exception e) {
            log.error("[行讯通预测] - 异常busName:" + runBus.getBusName(), e);
            return XxtForecast.builder()
                    .forecastType("1")
                    .orderNum(-1)
                    .forecastTime(DateUtil.getDailyStartDate(now))
                    .build();
        }

        JSONObject retData = resp.getRetData();
        String retMsg = resp.getRetMsg();

        if (retData == null || !retData.containsKey("predict_time")) {
            if (!Constant.RunBusStatus.UN_RUN.equals(runBus.getRunStatus())) {
                log.info("[行讯通预测] - 无预测数据 runBus:{} retMsg:{} retData:{}", runBus, retMsg, retData);
            }
            return XxtForecast.builder()
                    .forecastType("1")
                    .orderNum(-1)
                    .forecastTime(DateUtil.getDailyStartDate(now))
                    .build();
        }

        Double time = retData.getDouble("predict_time");
        Integer orderNum = retData.getInteger("count");
        Integer isOffLine = retData.getInteger("isOffLine");

        long wxxtForecastTs;
        if (Constant.RunBusStatus.DISPATCHED.equals(runBus.getRunStatus())) {
            wxxtForecastTs = tripBeginTime.getTime() + time.longValue() * 1000;
        } else {
            wxxtForecastTs = now.getTime() + time.longValue() * 1000;
        }
        if (time <= 0.0) {
            wxxtForecastTs = now.getTime();
        }

        return XxtForecast.builder()
                .forecastType("1")
                .orderNum(orderNum)
                .wxxtForecastTime(new Date(wxxtForecastTs))
                .forecastTime(DateUtil.subSeconds(wxxtForecastTs))
                .stationCount(orderNum)
                .predictTime(time)
                .isOffLine(isOffLine)
                .build();
    }

    @Override
    public XxtForecast getForecast2(RunBus runBus, Long lastRouteStaId) {
        log.debug("【到站预测新方法】开始--busName:{},routeId:{}", runBus.getBusName(), runBus.getRouteId());
        Date now = new Date();
        Date tripBeginTime = runBus.getTripBeginTime();
        HashMap<String, Object> param = new HashMap<String, Object>() {{
            put("routeId", runBus.getRouteId());
            put("busId", runBus.getBusId());
            put("routeSubId", runBus.getRouteSubId());
            put("serviceType", runBus.getServiceType());
            put("runStatus", runBus.getRunStatus());
            put("tripStartTime", DateUtil.date2Str(tripBeginTime, DateUtil.yyyyMMdd_HHmmss));
            put("firstRouteStaId", Constant.RunBusStatus.DISPATCHED.equals(runBus.getRunStatus()) ?
                    runBus.getFirstRouteStaId() : runBus.getRouteStationId());
            put("lastRouteStaId", lastRouteStaId);
            put("adTime", DateUtil.date2Str(runBus.getAdTime(), DateUtil.yyyyMMdd_HHmmss));
            put("routeStaId", runBus.getRouteStationId());
            put("adFlag", runBus.getAdFlag());
            put("redisTime", runBus.getRedisTime());
        }};

        ApiResp<JSONObject> resp = wxxtApp2.getWaitTimeDd2(param);
        JSONObject retData = resp.getRetData();
        String retMsg = resp.getRetMsg();
        log.debug("【到站预测新方法】结束--busName:{},routeId:{},resp:{}", runBus.getBusName(), runBus.getRouteId(), JSONObject.toJSONString(resp));
        if (retData == null || !retData.containsKey("predict_time")) {
            log.debug("【到站预测新方法】无预测数据 runBus:{} retMsg:{} retData:{}", runBus, retMsg, retData);
            return getForecast(runBus, lastRouteStaId);
        }

        Double time = retData.getDouble("predict_time");
        Integer orderNum = retData.getInteger("count");
        Integer isOffLine = retData.getInteger("isOffLine");

        long wxxtForecastTs;
        if (Constant.RunBusStatus.DISPATCHED.equals(runBus.getRunStatus())
                && tripBeginTime.after(now)) {
            wxxtForecastTs = tripBeginTime.getTime() + time.longValue() * 1000;
        } else {
            wxxtForecastTs = now.getTime() + time.longValue() * 1000;
        }
        if (time <= 0.0) {
            wxxtForecastTs = now.getTime();
        }

        return XxtForecast.builder()
                .forecastType("1")
                .orderNum(orderNum)
                .wxxtForecastTime(new Date(wxxtForecastTs))
                .forecastTime(DateUtil.subSeconds(wxxtForecastTs))
                .stationCount(orderNum)
                .predictTime(time)
                .isOffLine(isOffLine)
                .build();
    }

    private List<String> select(String key) {
        return redisService.opsForListRange(key, 0, -1);
    }

}
