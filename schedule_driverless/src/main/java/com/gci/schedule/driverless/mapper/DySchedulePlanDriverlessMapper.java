package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.BusConfigure;
import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Mapper
public interface DySchedulePlanDriverlessMapper {
    int insert(DySchedulePlanDriverless record);

    void batchInsertSchedule(List<DySchedulePlanDriverless> list);

    int insertSelective(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> getScheduleList(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> getScheduleList02(DySchedulePlanDriverless record);

    List<DySchedulePlanDriverless> selectSchedulePlan (Map<String, Object> map);

    Date getMinPlanTimeByRouteIdAndPlanDate(@Param("routeId") String routeId, @Param("date")  Date date);

    BusConfigure busConfigure(Map<String, Object> map);
 }