package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.BusNumberConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface BusNumberConfigDynamicMapper {

    BusNumberConfig selectByRouteIdAndPlanDate(@Param("routeId")Integer routeId, @Param("planDate")Date planDate);

    int deleteByRouteIdAndPlanDate(@Param("routeId")Integer routeId, @Param("planDate")Date planDate);

    int save(BusNumberConfig record);

}