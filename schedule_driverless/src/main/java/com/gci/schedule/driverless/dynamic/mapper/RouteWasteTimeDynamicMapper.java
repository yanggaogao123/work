package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.RouteWasteTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RouteWasteTimeDynamicMapper {

    List<RouteWasteTime> queryByRoute(@Param("routeId") Long routeId, @Param("planDate")Date planDate);

    List<RouteWasteTime> queryByRouteHistory(@Param("routeId") Long routeId, @Param("runDate")Date runDate);
    
    Date queryHistoryRunDate(@Param("routeId") Long routeId);

}