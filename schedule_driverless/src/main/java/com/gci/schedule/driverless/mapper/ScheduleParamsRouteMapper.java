package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface ScheduleParamsRouteMapper {
    List<ScheduleParamsRoute> selectByRouteId(@Param("routeId") Long routeId);
}