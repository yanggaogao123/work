package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsAnchor;

import java.util.List;

public interface ScheduleParamsAnchorDynamicMapper {

    List<ScheduleParamsAnchor> getByTemplateId(Integer templateId);

}