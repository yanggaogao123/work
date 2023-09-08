package com.gci.schedule.driverless.feign;

import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.scheduleD.RouteSta;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface AptsBaseApp {
    @RequestLine("GET /routeStation/getListByRouteId/{routeId}")
    CodeResp<List<RouteSta>> getRouteStaListByRouteId(@Param("routeId") Long routeId);
}
