package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.LogForecastTime;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogForecastTimeMapper {
    int insert(LogForecastTime record);

    int insertMany(@Param("data") List<LogForecastTime> data);
}