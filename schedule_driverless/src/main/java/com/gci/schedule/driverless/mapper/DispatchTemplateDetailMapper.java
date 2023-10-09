package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DispatchTemplateDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface DispatchTemplateDetailMapper {
    List<DispatchTemplateDetail> selectByRouteId(@Param("routeId") Long routeId);
}