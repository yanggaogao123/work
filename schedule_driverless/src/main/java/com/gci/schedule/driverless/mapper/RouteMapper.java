package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.Route;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RouteMapper {

    List<Route> getListByOrganId(@Param("organId") String organId);
}
