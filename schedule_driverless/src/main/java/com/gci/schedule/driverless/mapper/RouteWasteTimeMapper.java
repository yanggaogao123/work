package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.RouteWasteTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RouteWasteTimeMapper {

    List<RouteWasteTime> queryByRoute(@Param("routeId") Long routeId, @Param("planDate")Date planDate);
    
    List<RouteWasteTime> queryByRouteHistory(@Param("routeId") Long routeId, @Param("runDate")Date runDate);

    List<RouteWasteTime> queryByRouteIdAndRunDayNum(@Param("routeId") Long routeId, @Param("weekday")Integer weekday);

    List<RouteWasteTime> queryByRouteIdAndRunDayAndDirection(@Param("routeId") Long routeId, @Param("weekday")Integer weekday, @Param("direction")Integer direction);

    void delete(@Param("routeId") Integer routeId);
    
    Date queryHistoryRunDate(@Param("routeId") Long routeId);
}