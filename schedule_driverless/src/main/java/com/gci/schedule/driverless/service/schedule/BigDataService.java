package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.scheduleD.StationPassenger;

import java.util.List;

public interface BigDataService {
    List<StationPassenger> getStationPassengerList(String date, String routeId);
}
