package com.gci.schedule.driverless.service.server;


import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.XxtForecast;

public interface WxxtService {
    XxtForecast getForecastByRunBus(RunBus runBus);

    XxtForecast getForecast(RunBus runBus, Long lastRouteStaId);

    XxtForecast getForecast2(RunBus runBus, Long lastRouteStaId);
}
