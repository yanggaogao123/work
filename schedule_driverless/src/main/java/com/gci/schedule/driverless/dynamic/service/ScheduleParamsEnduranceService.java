package com.gci.schedule.driverless.dynamic.service;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsEndurance;

import java.util.List;

public interface ScheduleParamsEnduranceService {

    /**
     * 查询补电设置列表
     * @param templateId
     * @return
     */
    List<ScheduleParamsEndurance> getByTemplateId(Integer templateId);

    /**
     * 查询续航里程
     * @param templateId
     * @return
     */
    Integer getEnduranceMileageByTemplateId(Integer templateId);

}
