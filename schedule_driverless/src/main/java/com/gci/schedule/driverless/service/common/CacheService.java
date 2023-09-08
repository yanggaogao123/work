package com.gci.schedule.driverless.service.common;


import com.gci.schedule.driverless.bean.TaskType;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-04-17 10:32
 * @version: v1.0
 * @Modified by:
 **/
public interface CacheService {

    void clearCache();

    void clearScheduleDataCache(Long routeId);

    void clearDispatchDataCache(Long routeId);

    void clearSubDataCache(Long routeId);

    void clearRouteDispatchModeDataCache(Long routeId);

    List<TaskType> selectTaskTypeList();
}
