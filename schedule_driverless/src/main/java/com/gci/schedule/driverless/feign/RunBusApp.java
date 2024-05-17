package com.gci.schedule.driverless.feign;

import com.gci.schedule.driverless.bean.BusMileage;
import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.RunBusShiftType;
import com.gci.schedule.driverless.bean.common.CodeResp;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-16 11:19 上午
 * @version: v1.0
 * @Modified by:
 **/
public interface RunBusApp {

    @RequestLine("GET /runBus/getByRoute/{routeId}")
    CodeResp<List<RunBus>> getByRoute(@Param("routeId") Long routeId);

    @RequestLine("GET /runBus/getByBus/{busId}")
    CodeResp<RunBus> getByBus(@Param("busId") Long busId);

    @RequestLine("GET /shiftType/getByRoute/{routeId}")
    CodeResp<List<RunBusShiftType>> getShiftTypeByRoute(@Param("routeId") Long routeId);

    @RequestLine("GET /runBus/getBusMileageByRoute/{routeId}")
    CodeResp<List<BusMileage>> getBusMileageByRoute(@Param("routeId") Long routeId);

    @RequestLine("GET /runBus/getBusMileageByBus/{busId}")
    CodeResp<BusMileage> getBusMileageByBus(@Param("busId") Long busId);

    @RequestLine("GET /runBus/getExBus/{busId}")
    CodeResp<String> getExBus(@Param("busId") Long busId);

}
