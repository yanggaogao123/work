package com.gci.schedule.driverless.service.common.impl;

import com.gci.schedule.driverless.bean.TaskType;
import com.gci.schedule.driverless.feign.DispatchApp;
import com.gci.schedule.driverless.service.common.CacheService;
import com.gci.schedule.driverless.service.server.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-04-17 10:32
 * @version: v1.0
 * @Modified by:
 **/
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private DispatchApp dispatchApp;
    @Autowired
    private RedisService redisService;

    @Override
    public void clearCache() {
        Set<String> keys = redisService.keys("dpdb.dispatch.auto.cache*");
        for (String key : keys) {
            Boolean rs = redisService.delete(key);
            log.info("[清除REDIS缓存] - 删除key:{} 结果:{}", key, rs);
        }
    }

    @Override
    public void clearScheduleDataCache(Long routeId) {
        Set<String> keys = redisService.keys("*ScheduleParams*::" + routeId);
        for (String key : keys) {
            Boolean rs = redisService.delete(key);
            log.info("[清除REDIS缓存] - routeId:{} 删除key:{} 结果:{}", routeId, key, rs);
        }
    }

    @Override
    public void clearDispatchDataCache(Long routeId) {
        Set<String> keys = redisService.keys("*DispatchParams*::" + routeId);
        for (String key : keys) {
            Boolean rs = redisService.delete(key);
            log.info("[清除REDIS缓存] - routeId:{} 删除key:{} 结果:{}", routeId, key, rs);
        }
    }

    @Override
    public void clearSubDataCache(Long routeId) {
        Set<String> keys = redisService.keys("*SubRoute*::" + routeId);
        for (String key : keys) {
            Boolean rs = redisService.delete(key);
            log.info("[清除REDIS缓存] - routeId:{} 删除key:{} 结果:{}", routeId, key, rs);
        }
    }

    @Override
    public void clearRouteDispatchModeDataCache(Long routeId) {
        Set<String> keys = redisService.keys("*routeDispatchMode*::" + routeId);
        for (String key : keys) {
            Boolean rs = redisService.delete(key);
            log.info("[清除REDIS缓存] - routeId:{} 删除key:{} 结果:{}", routeId, key, rs);
        }
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectTaskTypeList")
    @Override
    public List<TaskType> selectTaskTypeList() {
        return dispatchApp.getTaskType().getData();
    }
}
