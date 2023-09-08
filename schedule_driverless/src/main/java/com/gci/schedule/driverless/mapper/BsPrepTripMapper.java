package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsPrepTrip;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BsPrepTripMapper {
    @Cacheable(unless = "#result == null", cacheNames = "selectBsPrepTripByTripdefineId")
    List<BsPrepTrip> selectByTripdefineId(Long tripdefineId);

    @Cacheable(unless = "#result == null", cacheNames = "getBsPrepTripByPrepTripId")
    BsPrepTrip getByPrepTripId(Long prepTripId);
}