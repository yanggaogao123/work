package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleDriverlessPlanTimePre;

import java.util.Date;
import java.util.List;

public interface ScheduleDriverlessPlanTimePreMapper {

    List<ScheduleDriverlessPlanTimePre> getByRouteAndRunDate(Long routeId, Date runDate);

}