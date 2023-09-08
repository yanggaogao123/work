package com.gci.schedule.driverless.bean.common;

import com.gci.schedule.driverless.bean.RouteFirstLastSta;
import com.gci.schedule.driverless.bean.RouteSub;
import com.gci.schedule.driverless.bean.TaskType;
import com.gci.schedule.driverless.bean.bs.*;
import com.gci.schedule.driverless.bean.schedule.DyTriplogRunFirst;
import com.gci.schedule.driverless.mapper.*;
import com.gci.schedule.driverless.service.common.CacheService;
import com.gci.schedule.driverless.util.DateUtil;
import com.gci.schedule.driverless.util.LocationUtils;
import com.gci.schedule.driverless.util.SpringUtil;
import com.gci.schedule.driverless.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-05-11 10:11
 * @version: v1.0
 * @Modified by:
 **/
@Slf4j
public class BsData {

    public static BsBus getBsBus(Long busId) {
        BsBusMapper bsBusMapper = SpringUtil.getBean(BsBusMapper.class);
        return bsBusMapper.getByBusId(busId);
    }

    public static BsBus getBsBusByObuId(String obuId) {
        BsBusMapper bsBusMapper = SpringUtil.getBean(BsBusMapper.class);
        return bsBusMapper.getByObuId(obuId);
    }

    public static Long getBusIdByObuId(String obuId) {
        return getBsBusByObuId(obuId).getBusId();
    }

    public static String getObuIdByBusId(Long busId) {
        BsBus bsBus = getBsBus(busId);
        if (bsBus == null) {
            return null;
        }
        return bsBus.getObuId();
    }

    public static Long getRouteIdByBusId(Long busId) {
        BsBus bsBus = getBsBus(busId);
        if (bsBus == null) {
            return null;
        }
        return bsBus.getRouteId();
    }

    public static String getBusNameByBusId(Long busId) {
        BsBus bsBus = getBsBus(busId);
        if (bsBus == null) {
            return null;
        }
        return bsBus.getBusName();
    }


    public static BsRoute getBsRoute(Long routeId) {
        BsRouteMapper bsRouteMapper = SpringUtil.getBean(BsRouteMapper.class);
        return bsRouteMapper.getByRouteId(routeId);
    }

    public static BsRoute getBsRouteByRouteCode(String routeCode) {
        BsRouteMapper bsRouteMapper = SpringUtil.getBean(BsRouteMapper.class);
        return bsRouteMapper.getByRouteCode(routeCode);
    }

    public static Long getRouteIdByRouteCode(String routeCode) {
        BsRoute bsRoute = getBsRouteByRouteCode(routeCode);
        if (bsRoute == null) {
            return null;
        }
        return bsRoute.getRouteId();
    }

    public static String getRouteCodeByRouteId(Long routeId) {
        return getBsRoute(routeId).getRouteCode();
    }

    public static String getRouteNameByRouteId(Long routeId) {
        return getBsRoute(routeId).getRouteName();
    }

    public static String getUpFirstTimeByRouteId(Long routeId) {
        return getBsRoute(routeId).getUpFirstTime();
    }

    public static String getDownFirstTimeByRouteId(Long routeId) {
        return getBsRoute(routeId).getDownFirstTime();
    }

    public static String getUpLatestTimeByRouteId(Long routeId) {
        return getBsRoute(routeId).getUpLatestTime();
    }

    public static String getDownLatestTimeByRouteId(Long routeId) {
        return getBsRoute(routeId).getDownLatestTime();
    }

    public static Boolean isLoopRoute(Long routeId) {
        BsRoute bsRoute = getBsRoute(routeId);
        return (bsRoute.getFirstStationId() != null && bsRoute.getLastStationId() != null
                && bsRoute.getFirstStationId().equals(bsRoute.getLastStationId())) ||
                (bsRoute.getUpFirstTime() == null || bsRoute.getDownFirstTime() == null);
    }


    public static BsStation getBsStation(Long stationId) {
        if (stationId == null) {
            return null;
        }
        BsStationMapper bsStationMapper = SpringUtil.getBean(BsStationMapper.class);
        return bsStationMapper.getByStationId(stationId);
    }

    public static BsStation getVStaionFix(Long stationId) {
        if (stationId == null) {
            return null;
        }
        VStaionFixMapper vStaionFixMapper = SpringUtil.getBean(VStaionFixMapper.class);
        return vStaionFixMapper.getByStationId(stationId);
    }

    public static BsRouteSta getBsRouteSta(Long routeStaId) {
        if (routeStaId == null) {
            return null;
        }
        BsRouteStaMapper bsRouteStaMapper = SpringUtil.getBean(BsRouteStaMapper.class);
        return bsRouteStaMapper.getByRouteStaId(routeStaId);
    }

    public static BsRouteSta getBsRouteStaByBusStopCode(String busStopCode) {
        if (busStopCode == null) {
            return null;
        }
        BsRouteStaMapper bsRouteStaMapper = SpringUtil.getBean(BsRouteStaMapper.class);
        return bsRouteStaMapper.getByBusStopCode(busStopCode);
    }

    public static List<BsRouteSta> selectBsRouteStaByRouteId(Long routeId) {
        if (routeId == null) {
            return null;
        }
        BsRouteStaMapper bsRouteStaMapper = SpringUtil.getBean(BsRouteStaMapper.class);
        return bsRouteStaMapper.selectListByRouteId(routeId);
    }

    public static Long getStationIdByRouteStaId(Long routeStaId) {
        return getBsRouteSta(routeStaId).getStationId();
    }

    public static Long getOrderNumberByRouteStaId(Long routeStaId) {
        BsRouteSta bsRouteSta = getBsRouteSta(routeStaId);
        if (bsRouteSta == null) {
            return null;
        }
        return getBsRouteSta(routeStaId).getOrderNumber();
    }

    public static Boolean isEndStationStopCode(String busStopCode) {
        BsRouteSta bsRouteSta = getBsRouteStaByBusStopCode(busStopCode);
        if (bsRouteSta == null) {
            return false;
        }
        String stationMark = bsRouteSta.getStationMark();
        return "0".equals(stationMark) || "2".equals(stationMark) || "3".equals(stationMark) || "5".equals(stationMark);
    }

    public static RouteFirstLastSta getRouteFirstLastSta(Long routeId) {
        List<BsRouteSta> bsRouteStas = selectBsRouteStaByRouteId(routeId);
        RouteFirstLastSta routeFirstLastSta = new RouteFirstLastSta(routeId);
        for (BsRouteSta bsRouteSta : bsRouteStas) {
            switch (bsRouteSta.getStationMark()) {
                case "0":
                    routeFirstLastSta.setUpFirstRouteStaId(bsRouteSta.getRouteStaId());
                    routeFirstLastSta.setUpFirstStationId(bsRouteSta.getStationId());
                    break;
                case "2":
                    routeFirstLastSta.setUpLastRouteStaId(bsRouteSta.getRouteStaId());
                    routeFirstLastSta.setUpLastStationId(bsRouteSta.getStationId());
                    break;
                case "3":
                    routeFirstLastSta.setDownFirstRouteStaId(bsRouteSta.getRouteStaId());
                    routeFirstLastSta.setDownFirstStationId(bsRouteSta.getStationId());
                    break;
                case "5":
                    routeFirstLastSta.setDownLastRouteStaId(bsRouteSta.getRouteStaId());
                    routeFirstLastSta.setDownLastStationId(bsRouteSta.getStationId());
                    break;
            }
        }
        return routeFirstLastSta;
    }

    public static VDispatchTask getVDispatchTask(Long routeSubId) {
        if (routeSubId == null) {
            return null;
        }
        VDispatchTaskMapper vDispatchTaskMapper = SpringUtil.getBean(VDispatchTaskMapper.class);
        VDispatchTask vDispatchTask = vDispatchTaskMapper.getByRouteSubId(routeSubId);
        return vDispatchTask == null ? new VDispatchTask() : vDispatchTask;
    }

    public static Long getFirstStationIdByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getFirstStationId();
    }

    public static Long getLastStationIdByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getLastStationId();
    }

    public static Long getFirstRouteStaIdByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getFirstRouteStaId();
    }

    public static Long getLastRouteStaIdByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getLastRouteStaId();
    }

    public static Long getRouteIdByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getRouteId();
    }

    public static String getDirectionByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getDirection();
    }

    public static String getServiceTypeByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getServiceType();
    }

    public static String getRouteSubNameByRouteSubId(Long routeSubId) {
        return getVDispatchTask(routeSubId).getRouteSubName();
    }

    public static List<RouteSub> selectRouteSubByRouteId(Long routeId) {
        VDispatchTaskMapper vDispatchTaskMapper = SpringUtil.getBean(VDispatchTaskMapper.class);
        List<RouteSub> res = new ArrayList<>();
        List<VDispatchTask> vDispatchTasks = vDispatchTaskMapper.selectByRouteId(routeId);
        for (VDispatchTask t : vDispatchTasks) {
            res.add(RouteSub.builder()
                    .routeId(t.getRouteId())
                    .direction(t.getDirection())
                    .serviceType(t.getServiceType())
                    .routeSubId(t.getRouteSubId())
                    .routeSubName(t.getRouteSubName())
                    .firstStationId(t.getFirstStationId())
                    .lastStationId(t.getLastStationId())
                    .firstRouteStaId(t.getFirstRouteStaId())
                    .lastRouteStaId(t.getLastRouteStaId())
                    .build());
        }
        return res;
    }

    public static RouteSub getRouteSubByRouteSubId(Long routeSubId) {
        VDispatchTask t = getVDispatchTask(routeSubId);
        if (t == null) {
            return null;
        }
        return RouteSub.builder()
                .routeId(t.getRouteId())
                .direction(t.getDirection())
                .serviceType(t.getServiceType())
                .routeSubId(t.getRouteSubId())
                .routeSubName(t.getRouteSubName())
                .firstStationId(t.getFirstStationId())
                .lastStationId(t.getLastStationId())
                .firstRouteStaId(t.getFirstRouteStaId())
                .lastRouteStaId(t.getLastRouteStaId())
                .build();
    }

    public static String getServiceName(String serviceType) {
        CacheService cacheService = SpringUtil.getBean(CacheService.class);
        List<TaskType> taskTypes = cacheService.selectTaskTypeList();
        for (TaskType taskType : taskTypes) {
            if (serviceType.equals(taskType.getServiceType())) {
                return taskType.getServiceName();
            }
        }
        return "";
    }

    public static BsRouteTripNumber getBsRouteTripNumber(Long routeId) {
        BsRouteTripNumberMapper vDispatchTaskMapper = SpringUtil.getBean(BsRouteTripNumberMapper.class);
        return vDispatchTaskMapper.getByRouteId(routeId);
    }

    public static List<BsPrepTrip> selectBsPrepTripByTripdefineId(Long tripdefineId) {
        if (tripdefineId == null) {
            return null;
        }
        BsPrepTripMapper bsPrepTripMapper = SpringUtil.getBean(BsPrepTripMapper.class);
        return bsPrepTripMapper.selectByTripdefineId(tripdefineId);
    }

    public static Boolean isGoBackTrip(Long tripdefineId) {
        return selectBsPrepTripByTripdefineId(tripdefineId).size() > 1;
    }

    public static Boolean isGoBackTripByPrepTripId(Long prepTripId) {
        BsPrepTrip bsPrepTrip = getBsPrepTrip(prepTripId);
        if (bsPrepTrip == null) {
            return false;
        }
        return isGoBackTrip(bsPrepTrip.getTripdefineId());
    }

    public static boolean isLastPrepTrip(Long prepTripId, Long firstStationId, Long lastStationId) {
        BsPrepTrip bsPrepTrip = getBsPrepTrip(prepTripId);
        if (bsPrepTrip == null) {
            return true;
        }
        List<BsPrepTrip> prepTrips = selectBsPrepTripByTripdefineId(bsPrepTrip.getTripdefineId());
        BsPrepTrip lastTrip = prepTrips.get(prepTrips.size() - 1);
        return firstStationId.equals(lastTrip.getFirstStationId())
                && lastStationId.equals(lastTrip.getLastStationId());
    }

    public static BsPrepTrip getBsPrepTrip(Long prepTripId) {
        if (prepTripId == null) {
            return null;
        }
        BsPrepTripMapper bsPrepTripMapper = SpringUtil.getBean(BsPrepTripMapper.class);
        return bsPrepTripMapper.getByPrepTripId(prepTripId);
    }

    public static Long getTripdefineIdByPrepTripId(Long prepTripId) {
        BsPrepTrip bsPrepTrip = getBsPrepTrip(prepTripId);
        if (bsPrepTrip == null) {
            return null;
        }
        return bsPrepTrip.getTripdefineId();
    }

    public static List<BsSysDdl> selectSysDdlBySysType(String sysType) {
        BsSysDdlMapper bsSysDdlMapper = SpringUtil.getBean(BsSysDdlMapper.class);
        return bsSysDdlMapper.selectSysDdlBySysType(sysType);
    }

    public static BsSysDdl getBsSysDdl(String keyWord) {
        List<BsSysDdl> bsSysDdls = selectSysDdlBySysType("dispatchAuto");
        Map<String, List<BsSysDdl>> map = bsSysDdls.stream().collect(Collectors.groupingBy(BsSysDdl::getKeyWord));
        if (map.containsKey(keyWord)) {
            return map.get(keyWord).get(0);
        }
        return null;
    }

    public static List<DyTriplogRunFirst> selectDyTriplogRunFirstList() {
        DyTriplogRunFirstMapper dyTriplogRunFirstMapper = SpringUtil.getBean(DyTriplogRunFirstMapper.class);
        return dyTriplogRunFirstMapper.selectDyTriplogRunFirstList();
    }

    public static DyTriplogRunFirst getDyTriplogRunFirst(Long employeeId) {
        List<DyTriplogRunFirst> dyTriplogRunFirsts = selectDyTriplogRunFirstList();
        Map<Long, List<DyTriplogRunFirst>> map = dyTriplogRunFirsts.stream().collect(Collectors.groupingBy(DyTriplogRunFirst::getEmployeeId));
        if (map.containsKey(employeeId)) {
            return map.get(employeeId).get(0);
        }
        return null;
    }

    public static List<DyChargeConfig> selectDyChargeConfigList() {
        DyChargeConfigMapper dyChargeConfigMapper = SpringUtil.getBean(DyChargeConfigMapper.class);
        return dyChargeConfigMapper.selectAll();
    }

    public static Boolean isChargeConfig(String busName) {
        List<DyChargeConfig> dyChargeConfigs = selectDyChargeConfigList();
        Map<String, List<DyChargeConfig>> map = dyChargeConfigs.stream().collect(Collectors.groupingBy(DyChargeConfig::getBusName));
        return map.containsKey(busName);
    }

    public static DyChargeConfig getChargeConfigByBusName(String busName) {
        List<DyChargeConfig> dyChargeConfigs = selectDyChargeConfigList();
        Map<String, List<DyChargeConfig>> map = dyChargeConfigs.stream().collect(Collectors.groupingBy(DyChargeConfig::getBusName));
        return map.containsKey(busName) ? map.get(busName).get(0) : null;
    }

    public static List<DyChangeTimeWindow> selectDyChangeTimeWindowList() {
        DyChangeTimeWindowMapper dyChangeTimeWindowMapper = SpringUtil.getBean(DyChangeTimeWindowMapper.class);
        return dyChangeTimeWindowMapper.selectAll();
    }

    public static List<DyChangeTimeWindow> getChangeTimeWindowByRouteId(Long routeId){
        List<DyChangeTimeWindow> dyChangeTimeWindows = selectDyChangeTimeWindowList();
        Map<Long, List<DyChangeTimeWindow>> map = dyChangeTimeWindows.stream().collect(Collectors.groupingBy(DyChangeTimeWindow::getRouteId));
        return map.containsKey(routeId) ? map.get(routeId) : null;
    }

    public static List<DyChangeTimeWindow> getChangeTimeWindowByStationIds(Long routeId,List<Long> stationIds){
        List<DyChangeTimeWindow> dyChangeTimeWindows = getChangeTimeWindowByRouteId(routeId);
        if(CollectionUtils.isEmpty(dyChangeTimeWindows)){
            return null;
        }
        List<DyChangeTimeWindow> result = dyChangeTimeWindows.stream().filter(e -> stationIds.contains(e.getStationId())).collect(Collectors.toList());
        return result;
    }

    /**
     * 头车时间
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static String getFirstTimeHHmm(Long routeId, String direction) {
        if (Constant.Direction.UP.equals(direction)) {
            return getUpFirstTimeByRouteId(routeId);
        }
        if (Constant.Direction.DOWN.equals(direction)) {
            return getDownFirstTimeByRouteId(routeId);
        }
        return null;
    }

    public static boolean isFirstTime(Long routeId, String direction, Date planTime) {
        Long firstTimeTs = getFirstTimeTs(routeId, direction);
        return firstTimeTs != null && firstTimeTs == planTime.getTime();
    }

    public static Long getMinFirstTimeTs(Long routeId) {
        Long upFirstTimeTs = getFirstTimeTs(routeId, Constant.Direction.UP);
        Long downFirstTimeTs = getFirstTimeTs(routeId, Constant.Direction.DOWN);
        if (upFirstTimeTs == null) {
            return downFirstTimeTs;
        }
        if (downFirstTimeTs != null) {
            return Math.min(upFirstTimeTs, downFirstTimeTs);
        } else {
            return upFirstTimeTs;
        }
    }

    public static Long getFirstTimeTs(Long routeId, String direction) {
        Date firstTime = getFirstTime(routeId, direction);
        return firstTime == null ? null : firstTime.getTime();
    }

    public static Date getFirstTime(Long routeId, String direction) {
        // 日:0600  夜:2200
        String fStr = getFirstTimeHHmm(routeId, direction);
        // 日:2200  夜:0100
        String lStr = getLatestTimeHHmm(routeId, direction);
        if (Util.isBlank(fStr) || Util.isBlank(lStr)) {
            return null;
        }
        return getFirstTime(fStr, lStr);
    }

    public static Date getFirstTime(String fStr, String lStr) {
        Date dateTime = new Date();
        Date date = DateUtil.getDailyStartDate(dateTime);
        long fTs;
        long lTs;
        try {
            fTs = DateUtil.hhmm2Ts(fStr);
            lTs = DateUtil.hhmm2Ts(lStr);
        } catch (Exception e) {
            log.error("[获取首班时间] - fStr:" + fStr + " lStr:" + lStr, e);
            throw e;
        }
        if (fTs > lTs) { //跨天(夜班车)
            long maxTs = 24 * 3600 * 1000;
            long hmTs = DateUtil.hhmm2Ts(DateUtil.date2Str(dateTime, DateUtil.hhmm));
            if ((hmTs >= fTs - 2 * 60 * 60 * 1000 && hmTs <= maxTs)
                    || (hmTs > lTs + 2 * 60 * 60 * 1000 && hmTs < fTs - 2 * 60 * 60 * 1000)) {
                fTs = date.getTime() + fTs;
            } else {
                fTs = DateUtil.getDateAddDay(date, -1).getTime() + fTs;
            }
        } else {
            fTs = date.getTime() + fTs;
        }
        return new Date(fTs);
    }

    /**
     * 末班时间
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static String getLatestTimeHHmm(Long routeId, String direction) {
        if (Constant.Direction.UP.equals(direction)) {
            return getUpLatestTimeByRouteId(routeId);
        }
        if (Constant.Direction.DOWN.equals(direction)) {
            return getDownLatestTimeByRouteId(routeId);
        }
        return null;
    }

    public static boolean isLatestTime(Long routeId, String direction, Date planTime) {
        Long latestTime = getLatestTimeTs(routeId, direction, planTime);
        return latestTime != null && latestTime == planTime.getTime();
    }

    public static Long getMaxLatestTimeTs(Long routeId) {
        Long upLatestTimeTs = getLatestTimeTs(routeId, Constant.Direction.UP);
        Long downLatestTimeTs = getLatestTimeTs(routeId, Constant.Direction.DOWN);
        if (upLatestTimeTs == null) {
            return downLatestTimeTs;
        }
        if (downLatestTimeTs != null) {
            return Math.max(upLatestTimeTs, downLatestTimeTs);
        } else {
            return upLatestTimeTs;
        }
    }

    public static Long getLatestTimeTs(Long routeId, String direction) {
        return getLatestTimeTs(routeId, direction, new Date());
    }

    public static Long getLatestTimeTs(Long routeId, String direction, Date dateTime) {
        Date latestTime = getLatestTime(routeId, direction, dateTime);
        return latestTime == null ? null : latestTime.getTime();
    }

    public static Date getLatestTime(Long routeId, String direction) {
        return getLatestTime(routeId, direction, new Date());
    }

    public static Date getLatestTime(Long routeId, String direction, Date dateTime) {
        // 日:0600  夜:2200
        String fStr = getFirstTimeHHmm(routeId, direction);
        // 日:2200  夜:0100
        String lStr = getLatestTimeHHmm(routeId, direction);
        if (Util.isBlank(fStr) || Util.isBlank(lStr)) {
            return null;
        }
        return getLatestTime(fStr, lStr, dateTime);
    }

    public static Date getLatestTime(String fStr, String lStr) {
        return getLatestTime(fStr, lStr, new Date());
    }

    public static Date getLatestTime(String fStr, String lStr, Date dateTime) {
        Date date = DateUtil.getDailyStartDate(dateTime);
        long fTs;
        long lTs;
        try {
            fTs = DateUtil.hhmm2Ts(fStr);
            lTs = DateUtil.hhmm2Ts(lStr);
        } catch (Exception e) {
            log.error("[获取末班时间] - fStr:" + fStr + " lStr:" + lStr, e);
            throw e;
        }
        if (fTs > lTs) { //跨天(夜班车)
            long maxTs = 24 * 3600 * 1000;
            long hmTs = DateUtil.hhmm2Ts(DateUtil.date2Str(dateTime, DateUtil.hhmm));
            if ((hmTs >= fTs - 2 * 60 * 60 * 1000 && hmTs <= maxTs)
                    || (hmTs > lTs + 2 * 60 * 60 * 1000 && hmTs < fTs - 2 * 60 * 60 * 1000)) {
                lTs = DateUtil.getDateAddDay(date, 1).getTime() + lTs;
            } else {
                lTs = date.getTime() + lTs;
            }
        } else {
            lTs = date.getTime() + lTs;
        }
        return new Date(lTs);
    }

    public static RouteSub getRouteSub(Long routeSubId) {
        if (getRouteSubByRouteSubId(routeSubId) != null) {
            return getRouteSubByRouteSubId(routeSubId);
        }
        if (getTripdefineIdByPrepTripId(routeSubId) != null) {
            Long tripDefineId = getTripdefineIdByPrepTripId(routeSubId);
            return getRouteSubByRouteSubId(tripDefineId);
        }
        return null;
    }

    public static List<RouteSub> getShortRouteSub(Long routeId, Long firstStationId) {
        return selectRouteSubByRouteId(routeId).stream()
                .filter(r -> isSameStation(routeId, firstStationId, r.getFirstStationId())
                        && (Constant.ServiceType.SHORT_LINE.equals(r.getServiceType())
                        || Constant.ServiceType.SHORT_FAST_LINE.equals(r.getServiceType())
                        || Constant.ServiceType.FULL_FAST_LINE.equals(r.getServiceType())))
                .collect(Collectors.toList());
    }

    public static RouteSub getFullRouteSub(Long routeId, String direction) {
        List<RouteSub> routeSubs = selectRouteSubByRouteId(routeId)
                .stream().filter(r -> direction.equals(r.getDirection())
                        && Constant.ServiceType.FULL_LINE.equals(r.getServiceType()))
                .collect(Collectors.toList());
        if (routeSubs.isEmpty()) {
            return null;
        }
        return routeSubs.get(0);
    }

    public static List<RouteSub> getAllFullRouteSub(Long routeId, String direction) {
        List<RouteSub> routeSubs = selectRouteSubByRouteId(routeId)
                .stream().filter(r -> direction.equals(r.getDirection())
                        && Constant.ServiceType.FULL_LINE.equals(r.getServiceType()))
                .collect(Collectors.toList());
        if (routeSubs.isEmpty()) {
            return null;
        }
        return routeSubs;
    }

    public static List<RouteSub> getOneWayShortSubs(Long routeId) {
        return selectRouteSubByRouteId(routeId).stream()
                .filter(r -> !isGoBackTrip(r.getRouteSubId())
                        && (Constant.ServiceType.SHORT_LINE.equals(r.getServiceType())
                        || Constant.ServiceType.SHORT_FAST_LINE.equals(r.getServiceType())
                        || Constant.ServiceType.SECTION_LINE.equals(r.getServiceType())))
                .collect(Collectors.toList());
    }

    public static String getStationName(Long stationId) {
        BsStation station = getStation(stationId);
        if (station == null) {
            return "";
        }
        return station.getStationName();
    }

    public static BsStation getStation(Long stationId) {
        if (getBsStation(stationId) != null) {
            return getBsStation(stationId);
        } else {
            return getVStaionFix(stationId);
        }
    }

    public static Long getFirstStationId(Long routeId, String direction) {
        if (Constant.Direction.UP.equals(direction)) {
            return getRouteFirstLastSta(routeId).getUpFirstStationId();
        }
        if (Constant.Direction.DOWN.equals(direction)) {
            return getRouteFirstLastSta(routeId).getDownFirstStationId();
        }
        return null;
    }

    public static Long getLastStationId(Long routeId, String direction) {
        if (Constant.Direction.UP.equals(direction)) {
            return getRouteFirstLastSta(routeId).getUpLastStationId();
        }
        if (Constant.Direction.DOWN.equals(direction)) {
            return getRouteFirstLastSta(routeId).getDownLastStationId();
        }
        return null;
    }

    public static Long getFirstRouteStaId(Long routeId, String direction) {
        return Constant.Direction.UP.equals(direction) ?
                getRouteFirstLastSta(routeId).getUpFirstRouteStaId()
                : getRouteFirstLastSta(routeId).getDownFirstRouteStaId();
    }

    public static Long getLastRouteStaId(Long routeId, String direction) {
        return Constant.Direction.UP.equals(direction) ?
                getRouteFirstLastSta(routeId).getUpLastRouteStaId()
                : getRouteFirstLastSta(routeId).getDownLastRouteStaId();
    }

    /**
     * 获取反方向
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static String getRDirection(Long routeId, String direction) {
        return isLoopRoute(routeId) ? direction :
                Constant.Direction.UP.equals(direction) ? Constant.Direction.DOWN : Constant.Direction.UP;
    }

    public static String getDirection(String stationMark) {
        if ("0".equals(stationMark) || "1".equals(stationMark) || "2".equals(stationMark)) {
            return Constant.Direction.UP;
        }
        if ("3".equals(stationMark) || "4".equals(stationMark) || "5".equals(stationMark)) {
            return Constant.Direction.DOWN;
        }
        return "";
    }


    /**
     * 是否是总站
     *
     * @param routeId
     * @param firstStationId
     * @return
     */
    public static Boolean isTerminusStation(Long routeId, Long firstStationId) {
        if (firstStationId == null) {
            return false;
        }
        RouteFirstLastSta sta = getRouteFirstLastSta(routeId);
        Util.checkNotNull(sta, "首末站数据有误");
        return firstStationId.equals(sta.getUpFirstStationId())
                || firstStationId.equals(sta.getUpLastStationId())
                || firstStationId.equals(sta.getDownFirstStationId())
                || firstStationId.equals(sta.getDownLastStationId());
    }

    public static Boolean isTerminusStationByDistance(Long routeId, Long firstStationId) {
        if (firstStationId == null) {
            return false;
        }
        RouteFirstLastSta sta = getRouteFirstLastSta(routeId);
        Util.checkNotNull(sta, "首末站数据有误");
        return isSameStation(firstStationId, sta.getUpFirstStationId())
                || isSameStation(firstStationId, sta.getUpLastStationId())
                || isSameStation(firstStationId, sta.getDownFirstStationId())
                || isSameStation(firstStationId, sta.getDownLastStationId());
    }

    /**
     * 是否同个站点
     *
     * @param routeId
     * @param stationId
     * @param firstStationId
     * @return
     */
    public static Boolean isSameStation(Long routeId, Long stationId, Long firstStationId) {
        if (stationId == null || firstStationId == null) {
            return false;
        }
        if (stationId.equals(firstStationId)) {
            return true;
        }
        RouteFirstLastSta sta = getRouteFirstLastSta(routeId);
        if (isLoopRoute(routeId)) {
            if (sta.getUpLastStationId() != null
                    && stationId.equals(sta.getUpLastStationId())) {
                return firstStationId.equals(sta.getUpFirstStationId());
            }
        } else {
            if (sta.getUpLastStationId() != null
                    && sta.getUpLastStationId().equals(stationId)) {
                return firstStationId.equals(sta.getDownFirstStationId());
            } else if (sta.getDownLastStationId() != null
                    && sta.getDownLastStationId().equals(stationId)) {
                return firstStationId.equals(sta.getUpFirstStationId());
            }
        }
        return false;
    }

    /**
     * 是否同个站点(通过距离判断)
     *
     * @param stationId
     * @param firstStationId
     * @return
     */
    public static Boolean isSameStation(Long stationId, Long firstStationId) {
        BsStation station = getStation(stationId);
        BsStation firstStation = getStation(firstStationId);
        if (station == null || firstStation == null) {
            return false;
        }
        return checkDistance(2, station.getLatitudedDis(), station.getLongitudedDis(),
                firstStation.getLatitudedDis(), firstStation.getLongitudedDis());
    }

    public static Boolean isSameStation(Double lat, Double lon, Long firstStationId, Integer n) {
        BsStation firstStation = getStation(firstStationId);
        if (firstStation == null) {
            return false;
        }
        return checkDistance(n, lat, lon, firstStation.getLatitudedDis(), firstStation.getLongitudedDis());
    }

    private static Boolean checkDistance(Integer n, Double lat, Double lon, Double targerLat, Double targerLon) {
        if (lat == null || lon == null
                || targerLat == null || targerLon == null) {
            return false;
        }
        Double distance = LocationUtils.getDistance(lat, lon, targerLat, targerLon);
        Double busStationDistance = n * Double.parseDouble(SpringUtil.getPropertiesValue("${dispatch.auto.fence.radius}"));
        return distance <= busStationDistance;
    }

    /**
     * 是否在运营时间内
     *
     * @return
     */
    public static boolean inRunTime(Long routeId) {
        return inRunTime(routeId, Constant.Direction.UP) || inRunTime(routeId, Constant.Direction.DOWN);
    }

    public static boolean inRunTime(Long routeId, String direction) {
        String fStr = getFirstTimeHHmm(routeId, direction);
        String lStr = getLatestTimeHHmm(routeId, direction);
        if (fStr == null || lStr == null) {
            return false;
        }
        Date dateTime = new Date();
        Date date = DateUtil.getDailyStartDate(dateTime);

        long fTs = DateUtil.hhmm2Ts(fStr);
        long lTs = DateUtil.hhmm2Ts(lStr);

        if (fTs > lTs) { //跨天(夜班车)

            long maxTs = 24 * 3600 * 1000;
            long hmTs = DateUtil.hhmm2Ts(DateUtil.date2Str(dateTime, DateUtil.hhmm));

            if ((hmTs >= fTs && hmTs <= maxTs) || (hmTs > lTs && hmTs < fTs)) {
                fTs = date.getTime() + fTs;
                lTs = DateUtil.getDateAddDay(date, 1).getTime() + lTs;
            } else {
                fTs = DateUtil.getDateAddDay(date, -1).getTime() + fTs;
                lTs = date.getTime() + lTs;
            }

        } else {
            fTs = date.getTime() + fTs;
            lTs = date.getTime() + lTs;
        }
        fTs = fTs - 60 * 60 * 1000;
        lTs = lTs + 2 * 60 * 60 * 1000;
        return dateTime.getTime() >= fTs && dateTime.getTime() <= lTs;
    }

    /**
     * 当前时是否在[首班时间-2小时，首班时间+3小时]内
     *
     * @return
     */
    public static boolean inLatestOutRunTime(Long routeId) {
        return inLatestOutRunTime(routeId, Constant.Direction.UP)
                || inLatestOutRunTime(routeId, Constant.Direction.DOWN);
    }

    public static boolean inLatestOutRunTime(Long routeId, String direction) {
        Date dateTime = new Date();
        Long firstTimeTs = getFirstTimeTs(routeId, direction);
        return firstTimeTs != null
                && dateTime.getTime() >= firstTimeTs - 2 * 60 * 60 * 1000
                && dateTime.getTime() <= firstTimeTs + 3 * 60 * 60 * 1000;
    }
}
