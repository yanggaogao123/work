package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.NotLongStationBusSum;
import com.gci.schedule.driverless.bean.scheduleD.StationBusAndPassengerInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdrealSimulateMapper {

    List<StationBusAndPassengerInfo> selectStationBusAndPassengerInfo(Map<String, Object> params);

    List<NotLongStationBusSum> getNotLongStationBusSum(Map<String, Object> params);
}