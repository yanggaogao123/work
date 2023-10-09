package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.mapper.ScheduleRouteConfigMapper;
import com.gci.schedule.driverless.service.schedule.ScheduleRouteConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pdl
 */
@Slf4j
@Service
public class ScheduleRouteConfigServiceImpl implements ScheduleRouteConfigService {

    @Autowired
    private ScheduleRouteConfigMapper scheduleRouteConfigMapper;


    @Override
    public List<Map<String, String>> getAllRoute() {
        return scheduleRouteConfigMapper.getAllRoute();
    }



}
