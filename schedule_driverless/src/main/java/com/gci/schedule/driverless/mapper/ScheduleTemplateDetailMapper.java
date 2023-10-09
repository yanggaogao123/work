package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ScheduleTemplateDetailMapper {
    List<ScheduleTemplateDetail> selectByRouteId(@Param("routeId") Long routeId);

}