package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.bean.scheduleD.DispatchTemplateDetail;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsAnchor;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleTemplateDetail;
import com.gci.schedule.driverless.mapper.DispatchTemplateDetailMapper;
import com.gci.schedule.driverless.mapper.ScheduleParamsAnchorMapper;
import com.gci.schedule.driverless.mapper.ScheduleParamsRouteMapper;
import com.gci.schedule.driverless.mapper.ScheduleTemplateDetailMapper;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsAnchorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScheduleParamsAnchorServiceImpl implements ScheduleParamsAnchorService {
    @Autowired
    private ScheduleParamsAnchorMapper scheduleParamsAnchorMapper;
    @Autowired
    private ScheduleTemplateDetailMapper scheduleTemplateDetailMapper;
    @Autowired
    private ScheduleParamsRouteMapper scheduleParamsRouteMapper;

    @Override
    public List<ScheduleParamsAnchor> selectParamsByRouteId(Long routeId) {
        return scheduleParamsAnchorMapper.selectByRouteId(routeId);
    }

    @Override
    public List<ScheduleTemplateDetail> selectTemplateByRouteId(Long routeId) {
        return scheduleTemplateDetailMapper.selectByRouteId(routeId);
    }

    @Override
    public List<ScheduleParamsRoute> selectScheduleParamsByRouteId(Long routeId) {
        return scheduleParamsRouteMapper.selectByRouteId(routeId);
    }
}
