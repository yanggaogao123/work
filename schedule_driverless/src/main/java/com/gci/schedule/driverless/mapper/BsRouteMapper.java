package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsRoute;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BsRouteMapper {

    @Cacheable(unless = "#result == null", cacheNames = "getBsRouteByRouteId")
    BsRoute getByRouteId(Long routeId);

    @Cacheable(unless = "#result == null", cacheNames = "getBsRouteByRouteCode")
    BsRoute getByRouteCode(String routeCode);

    List<BsRoute> selectList();
}