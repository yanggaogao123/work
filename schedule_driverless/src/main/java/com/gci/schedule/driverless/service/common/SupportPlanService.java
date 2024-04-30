package com.gci.schedule.driverless.service.common;

import com.gci.schedule.driverless.bean.scheduleD.RuningScheduleVo;

import java.util.List;

public interface SupportPlanService {
    boolean update(RuningScheduleVo plan,Long routeId);
    List<RuningScheduleVo> select(Long routeId);
    RuningScheduleVo get(Long routeId, Long scheduleId);
    void add(RuningScheduleVo plan,Long routeId);
    boolean delete(RuningScheduleVo plan,Long routeId);

}


