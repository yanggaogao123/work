package com.gci.schedule.driverless.service.schedule.impl;


import com.gci.schedule.driverless.bean.scheduleD.BusNumberConfig;
import com.gci.schedule.driverless.mapper.BusNumberConfigMapper;
import com.gci.schedule.driverless.service.schedule.BusNumberConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service("busNumberConfigService")
public class BusNumberConfigServiceImpl implements BusNumberConfigService {

	@Resource
	private BusNumberConfigMapper busNumberConfigMapper;

	@Override
	public int deleteByRouteIdAndPlanDate(Integer routeId, Date planDate) {
		return busNumberConfigMapper.deleteByRouteIdAndPlanDate(routeId,planDate);
	}

	@Override
	public int save(BusNumberConfig busNumberConfig) {
		return busNumberConfigMapper.save(busNumberConfig);
	}

	@Override
	public BusNumberConfig selectByRouteIdAndPlanDate(Integer routeId, Date planDate) {
		BusNumberConfig busNumberConfig = busNumberConfigMapper.selectByRouteIdAndPlanDate(routeId, planDate);
		if (Objects.nonNull(busNumberConfig)) {
			//班别数回显
			List<Map> result = busNumberConfigMapper.selectShiftCount(routeId, planDate);
			Map<String, Map<String,Integer>> shiftMap = new HashMap<>();
			shiftMap.put("morningClass", new HashMap<>());
			shiftMap.put("midClass", new HashMap<>());
			shiftMap.put("nightClass", new HashMap<>());
			shiftMap.put("doubleClass", new HashMap<>());
			for (Map map : result) {
				if (map.get("DIRECTION").toString().equals("0")) {
					shiftMap.get("morningClass").put("up", Integer.parseInt(map.get("MORNINGCLASS").toString()));
					shiftMap.get("midClass").put("up", Integer.parseInt(map.get("MIDCLASS").toString()));
					shiftMap.get("nightClass").put("up", Integer.parseInt(map.get("NIGHTCLASS").toString()));
					shiftMap.get("doubleClass").put("up", Integer.parseInt(map.get("DOUBLECLASS").toString()));
				} else {
					shiftMap.get("morningClass").put("down", Integer.parseInt(map.get("MORNINGCLASS").toString()));
					shiftMap.get("midClass").put("down", Integer.parseInt(map.get("MIDCLASS").toString()));
					shiftMap.get("nightClass").put("down", Integer.parseInt(map.get("NIGHTCLASS").toString()));
					shiftMap.get("doubleClass").put("down", Integer.parseInt(map.get("DOUBLECLASS").toString()));
				}
			}
			busNumberConfig.setShiftCountMap(shiftMap);
		}
		return busNumberConfig;
	}

	@Override
	public List<BusNumberConfig> selectByRouteIdListAndPlantDayRange(List<Integer> routeIdList, Date beginPlanDate, Date endPlanDate) {
		return busNumberConfigMapper.selectByRouteIdListAndPlantDayRange(routeIdList,beginPlanDate,endPlanDate);
	}

    @Override
    public void updateMountCarTemplate(Integer routeId, Date planDate, Long templateId) {
		busNumberConfigMapper.updateMountCarTemplate(routeId, planDate, templateId);
    }


}
