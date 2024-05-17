package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.RunBus;
import com.gci.schedule.driverless.bean.RunBusShiftType;

import java.util.List;
import java.util.Map;

public interface RunBusService {


    List<RunBus> getByRoute(Long routeId);
    RunBus getByBus(Long busId);
    List<RunBusShiftType> getShiftTypeByRoute(Long routeId);
    void fillingShiftType(List<RunBus> runBuses);

}
