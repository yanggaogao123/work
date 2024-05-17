package com.gci.schedule.driverless.feign;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.ApiResp;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-17 9:18 上午
 * @version: v1.0
 * @Modified by:
 **/
public interface WxxtApp {
    @RequestLine("POST /xxt-api/bus/forecast/waitTime")
    @Headers("Content-Type: application/json;charset=UTF-8")
    ApiResp<JSONObject> getWaitTime(Map<String, Object> param);

    @RequestLine("POST /forecast/waitTimeDd")
    @Headers("Content-Type: application/json;charset=UTF-8")
    ApiResp<JSONObject> getWaitTimeDd(Map<String, Object> param);
}