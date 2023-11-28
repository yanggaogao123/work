package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.AdrealInfo;
import com.gci.schedule.driverless.bean.IntersiteTimeParams;
import com.gci.schedule.driverless.bean.RouteStationResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimulationService {
	public List<AdrealInfo> adrealInfo(Long routeId,String runDateStr,Integer scheduleStatus);

	/**
	 * 得到该线路在该日期的头班车时间
	 * @param routeId
	 * @param planDate
	 * @return
	 */
	String getMinPlanTimeByRouteIdAndPlanDate(String routeId, String planDate);

	Double getIntersiteTime (IntersiteTimeParams intersiteTimeParams);

	List<RouteStationResult> getRouteStationResult (String routeId, String direction);

}
