package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsRouteSta;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BsRouteStaMapper {

    @Cacheable(unless = "#result == null", cacheNames = "getBsRouteStaByRouteStaId")
    BsRouteSta getByRouteStaId(Long routeStaId);

    @Cacheable(unless = "#result == null", cacheNames = "getBsRouteStaByBusStopCode")
    BsRouteSta getByBusStopCode(String busStopCode);

    @Cacheable(unless = "#result == null", cacheNames = "selectBsRouteStaByRouteId")
    List<BsRouteSta> selectListByRouteId(Long routeId);
}