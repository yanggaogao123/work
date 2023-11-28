package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateJoin;
import com.gci.schedule.driverless.mapper.ScheduleTemplateMapper;
import com.gci.schedule.driverless.service.schedule.ScheduleTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("scheduleTemplateService")
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService {


    @Autowired
    private ScheduleTemplateMapper scheduleTemplateMapper;

    @Override
    public List<ScheduleTemplateJoin> getJoinTemplateListByRouteId(Integer routeId) {
        return scheduleTemplateMapper.getJoinTemplateListByRouteId(routeId);
    }

}
