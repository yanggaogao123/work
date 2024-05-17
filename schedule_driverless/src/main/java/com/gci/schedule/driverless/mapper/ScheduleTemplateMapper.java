package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateJoin;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ScheduleTemplateMapper {
    List<ScheduleTemplateJoin> getJoinTemplateListByRouteId(@Param("routeId") Integer routeId);
}