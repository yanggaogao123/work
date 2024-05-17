package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.paiti.Bus;
import com.gci.schedule.driverless.bean.paiti.Route;

import java.util.List;
import java.util.Map;

public interface PaitiService {


    Map<String, List<Bus>> queryDicheOrganIdAndBusMap();

    Map<String, List<Route>> queryDicheRouteCodeAndOrganIdMap();

}
