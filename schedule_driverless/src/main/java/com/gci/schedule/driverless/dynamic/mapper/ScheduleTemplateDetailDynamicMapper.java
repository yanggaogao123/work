package com.gci.schedule.driverless.dynamic.mapper;

import org.apache.ibatis.annotations.Param;


public interface ScheduleTemplateDetailDynamicMapper {

    Integer getTemplateIdByRouteIdAndDay(@Param("routeId") Integer routeId ,@Param("applyDay")  Integer applyDay);

}