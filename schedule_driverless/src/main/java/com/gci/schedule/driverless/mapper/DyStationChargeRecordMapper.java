package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.DyStationChargeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyStationChargeRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyStationChargeRecord record);

    int insertSelective(DyStationChargeRecord record);

    DyStationChargeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyStationChargeRecord record);

    int updateByPrimaryKey(DyStationChargeRecord record);

    List<DyStationChargeRecord> getChargeStationByBusId(Long busId);
}