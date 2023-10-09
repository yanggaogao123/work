package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.scheduleD.DispatchTemplateDetail;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsAnchor;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateDetail;

import java.util.List;

public interface ScheduleParamsAnchorService {
    List<ScheduleParamsAnchor> selectParamsByRouteId(Long routeId);
    List<ScheduleTemplateDetail> selectTemplateByRouteId(Long routeId);
    List<ScheduleParamsRoute> selectScheduleParamsByRouteId(Long routeId);
}
