package com.gci.schedule.driverless.bean.common;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.service.server.RedisService;
import com.gci.schedule.driverless.util.SpringUtil;
import com.gci.schedule.driverless.util.Util;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-05-22 16:20
 * @version: v1.0
 * @Modified by:
 **/
public class CacheUtil {

    /**
     * 是否尾二等尾车到站再发班
     *
     * @param routeId
     * @return
     */
    public static Boolean isWaitTailBus(Long routeId) {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("waitTailBus")) {
            return false;
        }
        return 1 == JSONObject.parseObject(s).getIntValue("waitTailBus");
    }

    /**
     * 是否尾二等尾车到站再发班
     *
     * @param routeId
     * @return
     */
    public static Boolean ensureTailBusByShortLine(Long routeId) {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("ensureTailBusByShortLine")) {
            return true;
        }
        return 1 == JSONObject.parseObject(s).getIntValue("ensureTailBusByShortLine");
    }

    /**
     * 获取简图最大间隔
     *
     * @param routeId
     * @return
     */
    public static Long getSysMaxInterval(Long routeId) {
        Long MAX_INTERVAL = Long.valueOf(SpringUtil.getPropertiesValue("${dispatch.auto.max.interval}"));
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("maxDispatchInterval")) {
            return MAX_INTERVAL;
        }
        return JSONObject.parseObject(s).getLong("maxDispatchInterval");
    }


    /**
     * 获取简图高峰最大间隔
     *
     * @param routeId
     * @return
     */
    public static Long getSysMaxPeakInterval(Long routeId) {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("maxPeakParamInterval")) {
            return getSysMaxInterval(routeId);
        }
        return JSONObject.parseObject(s).getLong("maxPeakParamInterval");
    }

    /**
     * 获取系统最小发班间隔
     *
     * @return
     */
    public static Long getSysMinInterval() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.min.interval}"));
    }

    /**
     * 获取断位最小停站
     *
     * @return
     */
    public static Long getBrokenMinStopMinute(Long routeId) {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("brokenMinStop")) {
            return getDefBrokenMinStopMinute();
        }
        return JSONObject.parseObject(s).getLong("brokenMinStop");
    }

    public static Long getDefBrokenMinStopMinute() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.broken.stop.minimum}"));
    }

    /**
     * 获取默认最小停站
     *
     * @return
     */
    public static Long getSysMinStopMinute() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.sys.stop.minimum}"));
    }

    /**
     * 获取默认最大停站
     *
     * @return
     */
    public static Long getSysMaxStopMinute() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.sys.stop.maxmum}"));
    }

    /**
     * 获取简图吃饭时间
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static Long getSysEatTime(Long routeId, String direction) {
        Long MIN_EAT_TIME = Long.valueOf(SpringUtil.getPropertiesValue("${dispatch.auto.sys.eat.minimum}"));
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        String name = Constant.Direction.UP.equals(direction) ? "upMinEatTime" : "downMinEatTime";
        if (s == null || !JSONObject.parseObject(s).containsKey(name)) {
            return MIN_EAT_TIME;
        }
        return JSONObject.parseObject(s).getLong(name);
    }

    public static Long getSysEatInterval() {
        return Long.valueOf(SpringUtil.getPropertiesValue("${dispatch.auto.sys.eat.interval}"));
    }


    /**
     * 均衡发班间隔计算途中车辆最小停站时间
     *
     * @return
     */
    public static Long getCalcMinStopMinute() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.calc.stop.minimum}"));
    }

    /**
     * 均衡发班间隔计算途中第一台车最小停站时间
     *
     * @return
     */
    public static Long getCalcMinStopMinute2() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.calc.stop.minimum.2}"));
    }

    /**
     * 均衡发班车辆最小停站时间
     *
     * @return
     */
    public static Long getBusMinStopMinute() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.bus.stop.minimum}"));
    }

    /**
     * 获取锁超时时间
     *
     * @return
     */
    public static Long getLockTimeout() {
        return Long.parseLong(SpringUtil.getPropertiesValue("${dispatch.auto.lock.timeout}"));
    }

    /**
     * 获取当前线路调度员
     *
     * @param routeId
     * @return
     */
    public static Long getDispatcherId(Long routeId) {
        String redisKey = SpringUtil.getPropertiesValue("${dispatch.auto.dispatcher}");
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String res = redisService.opsForHashGet(redisKey, routeId.toString());
        if (Util.isBlank(res)) {
            String userId = SpringUtil.getPropertiesValue("${dispatch.auto.plan.userid}");
            return Long.valueOf(userId);
        }
        return JSONObject.parseObject(res).getLong("userId");
    }

    /**
     * 获取首轮顶位时间（默认5分钟）
     *
     * @return
     */
    public static Long getFrReplaceTime(Long routeId) {
        Long FR_REPLACE_TIME = Long.valueOf(SpringUtil.getPropertiesValue("${dispatch.auto.fr.replace.time}"));
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        String key = SpringUtil.getPropertiesValue("${dispatch.auto.parameter.settings}");
        String s = redisService.opsForHashGet(key, routeId.toString());
        if (s == null || !JSONObject.parseObject(s).containsKey("frReplaceTime")) {
            return FR_REPLACE_TIME;
        }
        return JSONObject.parseObject(s).getLong("frReplaceTime");
    }

}
