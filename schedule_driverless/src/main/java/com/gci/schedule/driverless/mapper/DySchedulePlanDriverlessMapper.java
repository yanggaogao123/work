package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Mapper
public interface DySchedulePlanDriverlessMapper {
    int insert(DySchedulePlanDriverless record);

    int updateByPrimaryKey(DySchedulePlanDriverless record);

    int deleteByPrimaryKey(Long id);

    void batchDelete(List<Long> ids);

    void batchInsertSchedule(@Param("planList") List<DySchedulePlanDriverless> planList);

    int insertSelective(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> getScheduleList(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> getScheduleList02(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> selectSchedulePlan (Map<String, Object> map);

    Date getMinPlanTimeByRouteIdAndPlanDate(@Param("routeId") String routeId, @Param("date")  Date date);

    BusConfigure busConfigure(Map<String, Object> map);

    List<DySchedulePlanDriverless> getDriverlessDetailList(DySchedulePlanDriverless record);

    List<MountCarPlan> mountCarPlan(@Param("routeId") String routeId, @Param("runMode") String runMode, @Param("referenceDate") String referenceDate, @Param("runDate") String runDate);

    List<MountCarPlan> recentRunBus(@Param("routeId") String routeId, @Param("referenceDate") String referenceDate);

    List<DispatchTask> dispatchTaskByRoute(@Param("routeId")Integer routeId);

    int saveMountCar(MountCarPlan mountCarPlan);

    Integer saveFirstRound(MountCarPlan mountCarPlan);

    void deleteDyMidwayShortStation(DyMidwayShortStation params);

    int insertDyMidwayShortStation(DyMidwayShortStation params);


    List<DispatchTask> dispatchTaskByFirstRound(@Param("routeId")String routeId,
                                                @Param("direction")String direction, @Param("referenceDate")String referenceDate,
                                                @Param("runDate")String runDate);

    List<DispatchTask> dispatchTaskByMidway(@Param("routeId") String routeId,
                                            @Param("direction") String direction, @Param("referenceDate") String referenceDate,
                                            @Param("runDate") String runDate);

}