package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsClasses;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScheduleParamsClassesMapper {
    int insert(Map<String, Object> params);
    int delByTemplateId(Integer templateId);
    List<ScheduleParamsClasses> getByTemplateId(Integer templateId);
    List<ScheduleParamsClasses> queryByPassenger(Map<String, Object> map);
    List<ScheduleParamsClasses> queryByRoute(Map<String, Object> map);
    int insertByTemplate(@Param("updateUser") Integer updateUser, @Param("routeId") Integer routeId, @Param("templateId") Integer templateId, @Param("direction") Integer direction);

    List<ScheduleParamsClasses> getInitClassesData();

    void insetObject(ScheduleParamsClasses item);

    List<ScheduleParamsClasses> selectList();

    List<ScheduleParamsClasses> selectByRouteId(@Param("routeId") Long routeId);
}