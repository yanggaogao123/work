package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessPlanTime;
import com.gci.schedule.driverless.bean.scheduleD.DySchedulePlanDriverless;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DyDriverlessPlanTimeMapper {
    int deleteByPrimaryKey(Long id);

    void batchDelete(List<Long> ids);

    void batchInsert(@Param("planList") List<DyDriverlessPlanTime> planList);

    int insert(DyDriverlessPlanTime record);

    int insertSelective(DyDriverlessPlanTime record);

    DyDriverlessPlanTime selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyDriverlessPlanTime record);

    int updateByPrimaryKey(DyDriverlessPlanTime record);

    List<DyDriverlessPlanTime> getDriverlessPlanList(DyDriverlessPlanTime record);

    List<DyDriverlessPlanTime> getDriverlessPlanPreList(DyDriverlessPlanTime record);
}