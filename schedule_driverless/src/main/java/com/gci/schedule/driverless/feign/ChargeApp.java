package com.gci.schedule.driverless.feign;

import com.gci.schedule.driverless.bean.ForecastChargeTime;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.vo.ForecastChargeTimeVo;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

/**
 * 预估充电时长
 */
public interface ChargeApp {
    @RequestLine("POST /forecast")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    CodeResp<List<ForecastChargeTime>> getChargeTime(ForecastChargeTimeVo chargeTime);
}
