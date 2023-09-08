package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.DyShortTaskRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyShortTaskRecordMapper {
    List<DyShortTaskRecord> getShortTaskByStationId(Long routeId, Long stationId);
}
