package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.SingleBus;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DyBusSingleMapper {

    /**
     * 更新单班车挂车的车辆ID和车辆名称
     * @param map
     */
    void updateMountBusData(Map<String, Object> map);

    List<SingleBus> listByRouteIdAndPlanDate(@Param("routeId") Long routeId, @Param("runDate") Date runDate);
}
