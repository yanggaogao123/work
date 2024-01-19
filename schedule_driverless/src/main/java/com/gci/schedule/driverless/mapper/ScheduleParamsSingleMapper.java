package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamShift;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsSingle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScheduleParamsSingleMapper {
    int insert(Map<String, Object> params);
    int delByTemplateId(Integer templateId);
    List<ScheduleParamsSingle> selectList();
    List<ScheduleParamsSingle> selectByRouteId(@Param("routeId") Long routeId);
    ScheduleParamsSingle getByTemplateId(Integer templateId);
    ScheduleParamsSingle getByRoute(Map<String, Object> map);
    int insertByTemplate(@Param("updateUser")Integer updateUser , @Param("routeId") Integer routeId , @Param("templateId")Integer templateId);

    void insertObject(ScheduleParamsSingle single);

    void delShiftByTemplateId(Integer templateId);

    void saveShift(ScheduleParamShift shift);

    List<ScheduleParamShift> getShiftByTemplateId(Integer templateId);
}