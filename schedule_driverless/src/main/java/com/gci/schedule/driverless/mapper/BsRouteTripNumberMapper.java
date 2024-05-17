package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsRouteTripNumber;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface BsRouteTripNumberMapper {
    @Cacheable(unless = "#result == null", cacheNames = "getBsRouteTripNumberByRouteId")
    BsRouteTripNumber getByRouteId(Long routeId);
}
