package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.VDispatchTask;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-01-22 下午4:36
 * @version: v1.0
 * @Modified by:
 **/
@Repository
public interface VDispatchTaskMapper {
    @Cacheable(unless = "#result == null", cacheNames = "getVDispatchTaskByRouteSubId")
    VDispatchTask getByRouteSubId(Long routeSubId);

    @Cacheable(unless = "#result == null", cacheNames = "selectVDispatchTaskByRouteId")
    List<VDispatchTask> selectByRouteId(Long routeId);
}
