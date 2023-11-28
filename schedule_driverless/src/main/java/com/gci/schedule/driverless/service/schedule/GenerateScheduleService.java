package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.scheduleD.BusConfigure;
import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessConfig;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;

import java.util.Map;

public interface GenerateScheduleService {
    R generateSchedule(GenerateScheduleParams params);
    R generateSupportSchedule(GenerateScheduleParams2 params);
    R getScheduleBySort(ScheduleBySortParam params);

    /**
     * 获取线路配车情况
     * @param map
     * @return
     */
    BusConfigure busConfigure(Map<String, Object> map);

    R getUnionRouteInfo(Long routeId);

    DyDriverlessConfig getDyDriverlessConfig(Long routeId,Long supportRouteId,Integer isDriverless);
}
