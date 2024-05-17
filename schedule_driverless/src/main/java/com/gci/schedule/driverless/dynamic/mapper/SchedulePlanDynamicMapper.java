package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SchedulePlanDynamicMapper {

    List<SchedulePlan> getSchedulePlanList(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    int delete(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    void insert(SchedulePlan record);

    int deleteOptimal(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    void insertOptimal(SchedulePlan record);

    void deleteOptimalTimetable(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    void deleteSingleBusPlan(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    void saveSingleBusPlan(SingleBus singleBus);

}