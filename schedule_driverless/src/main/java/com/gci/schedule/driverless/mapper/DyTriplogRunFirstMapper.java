package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.schedule.DyTriplogRunFirst;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyTriplogRunFirstMapper {
    List<DyTriplogRunFirst> selectByEmployeeId(Long employeeId);

    @Cacheable(unless = "#result == null", cacheNames = "selectDyTriplogRunFirstList")
    List<DyTriplogRunFirst> selectDyTriplogRunFirstList();
}
