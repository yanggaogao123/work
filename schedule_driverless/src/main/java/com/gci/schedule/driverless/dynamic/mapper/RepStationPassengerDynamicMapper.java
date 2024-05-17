package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.RouteStationPassenger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepStationPassengerDynamicMapper {

    List<RouteStationPassenger> getRouteStationPassanger(@Param("runDayNum")String runDayNum, @Param("direction") String direction, @Param("routeId") Long routeId);

    List<Long> getRouteStationIdsByRouteId(Long routeId);

}