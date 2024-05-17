package com.gci.schedule.driverless.service.schedule.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsClasses;
import com.gci.schedule.driverless.mapper.ScheduleParamsClassesMapper;
import com.gci.schedule.driverless.mapper.ScheduleParamsRouteMapper;
import com.gci.schedule.driverless.service.schedule.RouteService;
import com.gci.schedule.driverless.service.schedule.ScheduleParamsClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("scheduleParamsClassesService")
public class ScheduleParamsClassesServiceImpl implements ScheduleParamsClassesService {


	@Autowired
	private ScheduleParamsClassesMapper scheduleParamsClassesMapper;

	@Autowired
	private RouteService routeService;

	@Autowired
	ScheduleParamsRouteMapper scheduleParamsRouteMapper;


	@Override
	public List<ScheduleParamsClasses> getByTemplateId(Integer templateId) {
		List<ScheduleParamsClasses> classes = scheduleParamsClassesMapper.getByTemplateId(templateId);
		if (classes.size() > 0) {
			Long routeId = classes.get(0).getRouteId();
			String routeUpDownInfo = null;
			try {
				routeUpDownInfo = routeService.getRouteUpDownInfo2(routeId);
			} catch (Exception e) {
				return classes;
			}
			JSONObject jsonObject = JSON.parseObject(routeUpDownInfo);
			if ("0".equals(jsonObject.get("retCode"))) {
				JSONArray retData = jsonObject.getJSONArray("retData");
				List<Map> maps = retData.toJavaList(Map.class);
				Map upMap = null, downMap = null;
				List<ScheduleParamsClasses> up = new ArrayList<>();
				List<ScheduleParamsClasses> down = new ArrayList<>();
				for (Map map : maps) {
					if (map.get("direction").toString().equals("0")) {
						//上行
						upMap = map;
					} else {
						//下行
						downMap = map;
					}
				}
				if (Objects.nonNull(upMap) && Objects.nonNull(upMap.get("firstTime"))) {
					String firstTime = upMap.get("firstTime").toString().replaceAll(":", "");
					List<ScheduleParamsClasses> upList = classes.stream().filter(item -> item.getDirection().equals("0")).collect(Collectors.toList());
					List<ScheduleParamsClasses> left = new ArrayList<>(), right = new ArrayList<>();
					for (ScheduleParamsClasses classs : upList) {
						if (Integer.parseInt(classs.getBeginTime()) >= Integer.parseInt(firstTime)) {
							left.add(classs);
						} else {
							right.add(classs);
						}
					}
					left.sort(Comparator.comparing(ScheduleParamsClasses::getIntBeginTime));
					right.sort(Comparator.comparing(ScheduleParamsClasses::getIntBeginTime));
					left.addAll(right);
					up = left;
				}
				if (Objects.nonNull(downMap) && Objects.nonNull(downMap.get("firstTime"))) {
					String firstTime = downMap.get("firstTime").toString().replaceAll(":", "");
					List<ScheduleParamsClasses> downList = classes.stream().filter(item -> item.getDirection().equals("1")).collect(Collectors.toList());
					List<ScheduleParamsClasses> left = new ArrayList<>(), right = new ArrayList<>();
					for (ScheduleParamsClasses classs : downList) {
						if (Integer.parseInt(classs.getBeginTime()) >= Integer.parseInt(firstTime)) {
							left.add(classs);
						} else {
							right.add(classs);
						}
					}
					left.sort(Comparator.comparing(ScheduleParamsClasses::getIntBeginTime));
					right.sort(Comparator.comparing(ScheduleParamsClasses::getIntBeginTime));
					left.addAll(right);
					down = left;
				}
				up.addAll(down);
				classes = up;
			}
		}
		return classes;
	}

	@Override
	public List<ScheduleParamsClasses> selectList() {
		return scheduleParamsClassesMapper.selectList();
	}

	@Override
	public List<ScheduleParamsClasses> selectByRouteId(Long routeId) {
		return scheduleParamsClassesMapper.selectByRouteId(routeId);
	}
}
