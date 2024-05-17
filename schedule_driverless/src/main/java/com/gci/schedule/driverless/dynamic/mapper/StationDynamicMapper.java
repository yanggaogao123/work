package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.Station;

public interface StationDynamicMapper {

    Station getByStationId(Long stationId);

    Double getDistace(Double fromLongituded,Double fromLatituded,Double toLongituded,Double toLatituded);
}