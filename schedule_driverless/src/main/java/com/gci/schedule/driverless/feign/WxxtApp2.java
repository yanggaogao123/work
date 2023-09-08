package com.gci.schedule.driverless.feign;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.ApiResp;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

public interface WxxtApp2 {
    @RequestLine("POST")
    @Headers("Content-Type: application/json;charset=UTF-8")
    ApiResp<JSONObject> getWaitTimeDd2(Map<String, Object> param);
}
