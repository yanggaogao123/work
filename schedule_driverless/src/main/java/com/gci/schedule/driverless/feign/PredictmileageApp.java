package com.gci.schedule.driverless.feign;

import com.gci.schedule.driverless.bean.Predictmileage;
import com.gci.schedule.driverless.bean.common.CodeResp;
import feign.Headers;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

/**
 * 续航里程预测
 */
public interface PredictmileageApp {
    @RequestLine("POST /forecast")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    CodeResp<List<Predictmileage>> getPredictmileage(Map<String, Object> param);
}
