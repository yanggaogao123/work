package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsEndurance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScheduleParamsEnduranceDynamicMapper {

    List<ScheduleParamsEndurance> getByTemplateId(@Param("templateId") Integer templateId);

    Integer getEnduranceMileageByTemplateId(@Param("templateId")Integer templateId);

}
