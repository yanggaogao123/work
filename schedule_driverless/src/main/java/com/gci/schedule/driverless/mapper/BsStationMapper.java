package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsStation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface BsStationMapper {
    @Cacheable(unless = "#result == null", cacheNames = "getBsStationByStationId")
    BsStation getByStationId(Long stationId);
}