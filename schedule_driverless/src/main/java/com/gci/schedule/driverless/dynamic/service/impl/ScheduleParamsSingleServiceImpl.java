package com.gci.schedule.driverless.dynamic.service.impl;

import com.gci.schedule.driverless.dynamic.bean.ScheduleParamShift;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsSingle;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.mapper.ScheduleParamsSingleDynamicMapper;
import com.gci.schedule.driverless.dynamic.service.ScheduleParamsSingleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("scheduleParamsSingleDynamicService")
public class ScheduleParamsSingleServiceImpl implements ScheduleParamsSingleService {

    @Autowired
    private ScheduleParamsSingleDynamicMapper scheduleParamsSingleDynamicMapper;


    @Override
    public ScheduleParamsSingle getByTemplateId(Integer templateId) {
        ScheduleParamsSingle single = scheduleParamsSingleDynamicMapper.getByTemplateId(templateId);
        if (Objects.isNull(single)) {
            throw new MyException("500", "该模板没有对应的单班车模板数据");
        }
        List<ScheduleParamShift> shiftByTemplateId = scheduleParamsSingleDynamicMapper.getShiftByTemplateId(templateId);
        single.setShifts(shiftByTemplateId);
        return single;
    }
}
