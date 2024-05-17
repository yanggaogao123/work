package com.gci.schedule.driverless.dynamic.service.impl;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsEndurance;
import com.gci.schedule.driverless.dynamic.mapper.ScheduleParamsEnduranceDynamicMapper;
import com.gci.schedule.driverless.dynamic.service.ScheduleParamsEnduranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pdl
 */
@Service("scheduleParamsEnduranceDynamicService")
public class ScheduleParamsEnduranceServiceImpl implements ScheduleParamsEnduranceService {

    @Autowired
    private ScheduleParamsEnduranceDynamicMapper scheduleParamsEnduranceDynamicMapper;

    @Override
    public List<ScheduleParamsEndurance> getByTemplateId(Integer templateId) {
        return scheduleParamsEnduranceDynamicMapper.getByTemplateId(templateId);
    }

    @Override
    public Integer getEnduranceMileageByTemplateId(Integer templateId) {
        return scheduleParamsEnduranceDynamicMapper.getEnduranceMileageByTemplateId(templateId);
    }

}
