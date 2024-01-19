package com.gci.schedule.driverless.service.schedule;


import com.gci.schedule.driverless.bean.scheduleD.BusNumberConfig;

import java.util.Date;
import java.util.List;

public interface BusNumberConfigService {

	int deleteByRouteIdAndPlanDate(Integer routeId,Date planDate);

	int save(BusNumberConfig busNumberConfig);

	BusNumberConfig selectByRouteIdAndPlanDate(Integer routeId, Date planDate);

	List<BusNumberConfig> selectByRouteIdListAndPlantDayRange(List<Integer > routeIdList, Date beginPlantDay,Date endPlantDay);

	/**
	 * 更新模板ID
	 *
	 * @param routeId
	 * @param planDate
	 * @param templateId
	 */
	void updateMountCarTemplate(Integer routeId, Date planDate, Long templateId);
}
