package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.scheduleD.BsRouteSub;

import java.util.List;

public interface RouteSubService {
    List<BsRouteSub> getListByRouteId(Long routeId);
}
