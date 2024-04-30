package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;

import java.util.List;
import java.util.Map;

public interface GenerateScheduleService {
    R generateSchedule(GenerateScheduleParams params);
    R generateSupportSchedule(GenerateScheduleParams2 params);
    R getScheduleBySort(ScheduleBySortParam params);
    R getBusConfig(GenerateScheduleParams2 params);

    /**
     * 获取线路配车情况
     * @param map
     * @return
     */
    BusConfigure busConfigure(Map<String, Object> map);

    R getUnionRouteInfo(Long routeId);

    DyDriverlessConfig getDyDriverlessConfig(Long routeId,Long supportRouteId,Integer isDriverless);

    R getDriverlessRoute();

    R getScheduleDetaiList(ScheduleBySortParam params);

    List<MountCarPlan> mountCarPlan(String routeId, String runMode, String referenceDate, String runDate);

    List<MountCarPlan> recentRunBus(String routeId, String referenceDate);

    String runBus(String routeId);

    int saveMountCar(List<MountCarPlan> list, String userId, String userName) throws Exception;

    R runBusAndInfoByRouteNewRunBus(String routeId);

    List<DispatchTask> dispatchTask(String type, String routeId, String direction, String referenceDate, String runDate);

    R getMonitorInfo(Map<String, Object> params) throws CloneNotSupportedException;

    R getRuningScheduleConfig(Map<String, Object> params);
    String getByRouteIdAndRouteNameKey(String routeId, String routeNameKey, String page);

    R getByRouteNameKey(Map<String, Object> json);

    R getOneHourSupportPlan(Map<String, Object> params);

    R getSchedulePlanDetail(Map<String, Object> params);
    R getGisRoadInfo(Map<String, Object> params);
}
