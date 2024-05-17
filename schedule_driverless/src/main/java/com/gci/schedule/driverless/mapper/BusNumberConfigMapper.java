package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.BusNumberConfig;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BusNumberConfigMapper {

    int deleteByRouteIdAndPlanDate(@Param("routeId")Integer routeId, @Param("planDate")Date planDate);

    int save(BusNumberConfig record);

    BusNumberConfig selectByRouteIdAndPlanDate(@Param("routeId")Integer routeId, @Param("planDate")Date planDate);

    List<BusNumberConfig> selectByRouteIdListAndPlantDayRange(@Param("routeIdList")List<Integer > routeIdList, @Param("beginPlanDate")Date beginPlanDate,@Param("endPlanDate")Date endPlanDate);

    List<Map> selectShiftCount(@Param("routeId")Integer routeId, @Param("planDate")Date planDate);


    Map selectMonthlyShiftCount(Map<String, Object> map);

    void updateMountCarTemplate(Integer routeId, Date planDate, Long templateId);
}