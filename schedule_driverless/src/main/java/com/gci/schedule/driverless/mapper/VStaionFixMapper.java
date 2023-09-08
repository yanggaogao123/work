package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsStation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-11-26 下午1:51
 * @version: v1.0
 * @Modified by:
 **/
@Repository
public interface VStaionFixMapper {
    @Cacheable(unless = "#result == null", cacheNames = "getVStaionFixByStationId")
    BsStation getByStationId(Long stationId);
}
