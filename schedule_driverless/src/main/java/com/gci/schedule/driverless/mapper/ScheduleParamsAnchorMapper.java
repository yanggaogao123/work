package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsAnchor;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScheduleParamsAnchorMapper {
    List<ScheduleParamsAnchor> selectByRouteId(@Param("routeId") Long routeId);
}