package com.gci.schedule.driverless.dynamic.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.dynamic.bean.ScheduleParamsAnchor;
import com.gci.schedule.driverless.dynamic.mapper.ScheduleParamsAnchorDynamicMapper;
import com.gci.schedule.driverless.dynamic.mapper.ScheduleParamsRouteDynamicMapper;
import com.gci.schedule.driverless.dynamic.service.RouteService;
import com.gci.schedule.driverless.dynamic.service.ScheduleParamsAnchorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("scheduleParamsAnchorDynamicService")
public class ScheduleParamsAnchorServiceImpl implements ScheduleParamsAnchorService {

    @Autowired
    private ScheduleParamsAnchorDynamicMapper scheduleParamsAnchorDynamicMapper;

    @Autowired
    private RouteService routeDynamicService;

    @Autowired
    ScheduleParamsRouteDynamicMapper scheduleParamsRouteDynamicMapper;

    @Override
    public List<ScheduleParamsAnchor> getByTemplateId(Integer templateId) {
        List<ScheduleParamsAnchor> anchors = scheduleParamsAnchorDynamicMapper.getByTemplateId(templateId);
        if (anchors.size() > 0) {
            Long routeId = anchors.get(0).getRouteId();
            String routeUpDownInfo = null;
            try {
                routeUpDownInfo = routeDynamicService.getRouteUpDownInfo(Integer.valueOf(String.valueOf(routeId)));
            } catch (Exception e) {
                return anchors;
            }
            JSONObject jsonObject = JSON.parseObject(routeUpDownInfo);
            if ("0".equals(jsonObject.get("retCode"))) {
                JSONArray retData = jsonObject.getJSONArray("retData");
                List<Map> maps = retData.toJavaList(Map.class);
                Map upMap = null, downMap = null;
                List<ScheduleParamsAnchor> up;
                List<ScheduleParamsAnchor> down;
                for (Map map : maps) {
                    if (map.get("direction").toString().equals("0")) {
                        //上行
                        upMap = map;
                    } else {
                        //下行
                        downMap = map;
                    }
                }
                List<ScheduleParamsAnchor> upList = anchors.stream().filter(item -> item.getDirection().equals("0")).collect(Collectors.toList());
                if (Objects.nonNull(upMap) && Objects.nonNull(upMap.get("firstTime"))) {
                    String firstTime = upMap.get("firstTime").toString().replaceAll(":", "");
                    List<ScheduleParamsAnchor> left = new ArrayList<>(), right = new ArrayList<>();
                    for (ScheduleParamsAnchor anchor : upList) {
                        if (Integer.parseInt(anchor.getBeginTime()) >= Integer.parseInt(firstTime)) {
                            left.add(anchor);
                        } else {
                            right.add(anchor);
                        }
                    }
                    left.sort(Comparator.comparing(ScheduleParamsAnchor::getIntBeginTime));
                    right.sort(Comparator.comparing(ScheduleParamsAnchor::getIntBeginTime));
                    left.addAll(right);
                    up = left;
                } else {
                    up = upList;
                }

                List<ScheduleParamsAnchor> downList = anchors.stream().filter(item -> item.getDirection().equals("1")).collect(Collectors.toList());
                if (Objects.nonNull(downMap) && Objects.nonNull(downMap.get("firstTime"))) {
                    String firstTime = downMap.get("firstTime").toString().replaceAll(":", "");
                    List<ScheduleParamsAnchor> left = new ArrayList<>(), right = new ArrayList<>();
                    for (ScheduleParamsAnchor anchor : downList) {
                        if (Integer.parseInt(anchor.getBeginTime()) >= Integer.parseInt(firstTime)) {
                            left.add(anchor);
                        } else {
                            right.add(anchor);
                        }
                    }
                    left.sort(Comparator.comparing(ScheduleParamsAnchor::getIntBeginTime));
                    right.sort(Comparator.comparing(ScheduleParamsAnchor::getIntBeginTime));
                    left.addAll(right);
                    down = left;
                } else {
                    down = downList;
                }
                up.addAll(down);
                anchors = up;
            }
        }
        return anchors;
    }
}
