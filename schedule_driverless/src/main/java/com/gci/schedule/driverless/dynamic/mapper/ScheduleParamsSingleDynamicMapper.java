package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamShift;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsSingle;

import java.util.List;

public interface ScheduleParamsSingleDynamicMapper {

    ScheduleParamsSingle getByTemplateId(Integer templateId);

    List<ScheduleParamShift> getShiftByTemplateId(Integer templateId);

}