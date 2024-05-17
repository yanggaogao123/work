package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsBus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface BsBusMapper {

    @Cacheable(unless = "#result == null", cacheNames = "getBsBusByBusId")
    BsBus getByBusId(Long busId);

    @Cacheable(unless = "#result == null", cacheNames = "getBsBusByObuId")
    BsBus getByObuId(String obuId);
}