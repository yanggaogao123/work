package com.gci.schedule.driverless.feign;

import com.gci.schedule.driverless.bean.ForecastNeedCharge;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.vo.ForecastNeedChargeVo;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

public interface NeedChargeApp {
    /**
     * 充电量预估
     * @param vo
     * @return
     */
    @RequestLine("POST /estimate")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    CodeResp<List<ForecastNeedCharge>> getNeedCharge(ForecastNeedChargeVo vo);
}
