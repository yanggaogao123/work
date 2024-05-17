package com.gci.schedule.driverless.mapper;


import com.gci.schedule.driverless.bean.ScheduleRouteConfigVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleRouteConfigMapper {

    List<Map<String,String>> getAllRoute();

    /**
     * 根据线路ID查询计划配置
     *
     * @param routeId 线路ID
     * @return
     */
    ScheduleRouteConfigVo getByRouteId(Integer routeId);
}



