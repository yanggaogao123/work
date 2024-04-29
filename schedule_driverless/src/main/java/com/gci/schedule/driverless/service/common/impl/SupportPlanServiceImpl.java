package com.gci.schedule.driverless.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.scheduleD.RuningScheduleVo;
import com.gci.schedule.driverless.service.common.SupportPlanService;
import com.gci.schedule.driverless.service.server.RedisService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class SupportPlanServiceImpl implements SupportPlanService {

    public static final String supportPlanKey = "driverless.support.plan.route_id=";

    @Autowired
    private RedisService redisService;

    private String getKey(Long routeId) {
        return supportPlanKey+routeId;
    }
    @Override
    public boolean update(RuningScheduleVo plan,Long routeId) {
        return redisService.opsForHashPut(getKey(routeId),
                plan.getScheduleId().toString(), JSONObject.toJSONString(plan));
    }

    @Override
    public List<RuningScheduleVo> select(Long routeId) {
        List<RuningScheduleVo> result = new ArrayList<>();
        Map<String, String> map = redisService.opsForHashEntries(getKey(routeId));
        if (!CollectionUtils.isEmpty(map)) {
            List<String> planList = new ArrayList<>(map.values());
            Long dailyStartTime = DateUtil.getDailyStartTime(new Date());
            planList.forEach(plan -> {
                RuningScheduleVo vo = JSONObject.parseObject(plan, RuningScheduleVo.class);
                if(Objects.nonNull(vo)){
                    if ( Objects.isNull(vo.getDriverlessPlanTime())
                            || vo.getDriverlessPlanTime().getTime() < dailyStartTime) {
                        delete(vo,routeId);
                    } else {
                        result.add(vo);
                    }
                }
            });
        }
        return result;
    }

    @Override
    public RuningScheduleVo get(Long routeId, Long scheduleId) {
        List<RuningScheduleVo> plans = select(routeId);
        if (plans.isEmpty()) {
            return null;
        }
        for (RuningScheduleVo plan : plans) {
            if (plan.getScheduleId().equals(scheduleId)) {
                return plan;
            }
        }
        return null;
    }

    @Override
    public void add(RuningScheduleVo plan,Long routeId) {
        redisService.opsForHashPut(getKey(routeId), plan.getScheduleId().toString(), JSONObject.toJSONString(plan));

    }

    @Override
    public boolean delete(RuningScheduleVo plan, Long routeId) {
        return redisService.opsForHashDelete(getKey(routeId), plan.getScheduleId().toString());
    }
}
