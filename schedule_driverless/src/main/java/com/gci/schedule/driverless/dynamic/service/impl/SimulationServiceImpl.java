package com.gci.schedule.driverless.dynamic.service.impl;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.service.RouteStationService;
import com.gci.schedule.driverless.dynamic.service.SimulationService;
import com.gci.schedule.driverless.dynamic.test.DateUtil;
import com.gci.schedule.driverless.dynamic.util.HttpClientUtils;


@Service("simulationDynamicService")
public class SimulationServiceImpl implements SimulationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationServiceImpl.class);

    private Map<String, Map<String, Double>> allRouteAdrealWasteTimeMap = new HashMap<String, Map<String, Double>>(); //所有线路的站间耗时Map
    
    private Map<String, List<RouteWasteTime>> allRouteFullWasteTimeMap = new HashMap<String, List<RouteWasteTime>>(); //所有线路的全程耗时Map

    @Resource
    private RouteStationService routeStationDynamicService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${bigData.service.url}")
    private String stationWasteTimeUrl;
    @Value("${stationWasteTime.appName.key}")
    private String appNameKey;
    @Value("${stationWasteTime.appName.value}")
    private String appNameValue;
    @Value("${stationWasteTime.businessID.key}")
    private String businessIDKey;
    @Value("${stationWasteTime.businessID.value}")
    private String businessIDValue;
    @Value("${stationWasteTime.page.key}")
    private String pageKey;
    @Value("${stationWasteTime.page.value}")
    private String pageValue;
    @Value("${stationWasteTime.pageSize.key}")
    private String pageSizeKey;
    @Value("${stationWasteTime.pageSize.value}")
    private String pageSizeValue;
    @Value("${stationWasteTime.reqData.key}")
    private String reqDataKey;
    @Value("${stationWasteTime.routeId.key}")
    private String routeIdKey;
    @Value("${stationWasteTime.direction.key}")
    private String directionKey;
    @Value("${stationWasteTime.weekday.key}")
    private String weekdayKey;


    //志武站间耗时返回结果
    private Map<String, Double> getIntersiteTimeResult(String routeId, String direction, String weekday) {
        Map<String, Double> adrealWasteTimeMap = new HashMap<String, Double>();
        String adrealWasteTimeKey = routeId + "_" + weekday + "_" + direction;
        if (allRouteAdrealWasteTimeMap.containsKey(adrealWasteTimeKey)) {
            adrealWasteTimeMap = allRouteAdrealWasteTimeMap.get(adrealWasteTimeKey);
            return adrealWasteTimeMap;
        }
		/*http://10.89.141.131:8080/dataservices/getData
		{
			"appName": "appName",
				"businessID": "100020",
				"page": 1,
				"pageSize": 10,
				"reqData": {"routeId": "710","direction":"0"}}*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(appNameKey, appNameValue);
        jsonObject.put(businessIDKey, businessIDValue);
        jsonObject.put(pageKey, pageValue);
        jsonObject.put(pageSizeKey, pageSizeValue);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(routeIdKey, routeId);
        jsonObject1.put(directionKey, direction);
        jsonObject1.put(weekdayKey, weekday);
        jsonObject.put(reqDataKey, jsonObject1);
        JSONObject responJson = HttpClientUtils.httpPost(stationWasteTimeUrl, jsonObject);
        if (!responJson.getString("retCode").equals("0")) {
            throw new MyException("500", "请稍后再试");
        }
        JSONObject retData = responJson.getJSONObject("retData");
        List<IntersiteTimeResult> intersiteTimeResults = JSON.parseObject(retData.getJSONArray("list").toJSONString(), new TypeReference<List<IntersiteTimeResult>>() {
        });

        //站间耗时返回结果从list 转Map
        for (IntersiteTimeResult intersiteTimeResult :
                intersiteTimeResults) {
            //key:时-分-方向-开始站台-结束站台
            String key = intersiteTimeResult.getPhour() + "_" + intersiteTimeResult.getPminute() + "_" + intersiteTimeResult.getDirection() +
                    "_" + intersiteTimeResult.getStationId() + "_" + intersiteTimeResult.getNextStationId();
            String value = intersiteTimeResult.getAvgtime();
            if(value!=null)
            	adrealWasteTimeMap.put(key, Double.valueOf(value));
        }
        if(adrealWasteTimeMap.isEmpty()) {
        	throw new MyException("-1", "获取大数据平台站间用时失败");
        }
        //加入到所有线路的站间耗时Map
        allRouteAdrealWasteTimeMap.put(adrealWasteTimeKey, adrealWasteTimeMap);
        return adrealWasteTimeMap;
    }

    //查询线路对应方向站点列表
    public List<RouteStationResult> getRouteStationResult(String routeId, String direction) {
        String result = routeStationDynamicService.getListByRouteId(Long.valueOf(routeId));
        List<RouteStationResult> routeStationResultList = JSON.parseObject(JSON.parseObject(result).getJSONArray("data").toJSONString(), new TypeReference<List<RouteStationResult>>() {
        });
        List<String> stationMarkList = new ArrayList<>();
        if ("0".equals(direction)) {
            stationMarkList.add("0");
            stationMarkList.add("1");
            stationMarkList.add("2");
        } else if ("1".equals(direction)) {
            stationMarkList.add("3");
            stationMarkList.add("4");
            stationMarkList.add("5");
        }
        //获取对应方向的站点信息
        List<RouteStationResult> sameDirectionResultList = new ArrayList<RouteStationResult>();
        for (int i = 0; i < routeStationResultList.size(); i++) {
            if (stationMarkList.contains(routeStationResultList.get(i).getStationMark())) {
                sameDirectionResultList.add(routeStationResultList.get(i));
            }
        }
        return sameDirectionResultList;
    }

    //查询线路的中间站点列表
    private List<RouteStationResult> getBetweenRouteStationResult(List<RouteStationResult> routeStationResultList, String direction, Integer fromRouteStaId, Integer toRouteStaId) {
        boolean fromFlag = false;
        boolean toFlag = false;
        //获取从开始站点-》结束站点它们中间的站点
        List<RouteStationResult> betweenRouteStationResultList = new ArrayList<RouteStationResult>();
        for (int i = 0; i < routeStationResultList.size(); i++) {
            if (fromRouteStaId == routeStationResultList.get(i).getRouteStationId().intValue()) {
                fromFlag = true;
            }
            if (!fromFlag) {
                continue;
            }
            if (fromFlag) {
                if (toRouteStaId == routeStationResultList.get(i).getRouteStationId().intValue()) {
                    toFlag = true;
                    betweenRouteStationResultList.add(routeStationResultList.get(i));
                }
                if (toFlag) {
                    continue;
                }
            }
            
            betweenRouteStationResultList.add(routeStationResultList.get(i));
        }
        return betweenRouteStationResultList;
    }

    //根据开始站点，结束站点+志武的站间耗时+该线路所有的站点算出这2个站的耗时
    private Double getWasteTimeTotal(IntersiteTimeParams intersiteTimeParams, Map<String, Double> adrealWasteTimeMap, List<RouteStationResult> routeStationResultList) {
        //目标2个站点之间的站点
        List<RouteStationResult> betweenRouteStationResultList = this.getBetweenRouteStationResult(routeStationResultList, intersiteTimeParams.getDirection(), intersiteTimeParams.getFromRouteStaId(), intersiteTimeParams.getToRouteStaId());
        Date adTime = intersiteTimeParams.getAdTime();//进站时间

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(adTime);

        //折线按比例获取两个站A-Z间的耗时(B为中间站)
        for (int i = 0; i < betweenRouteStationResultList.size() - 1; i++) {
            Long stationIdA = betweenRouteStationResultList.get(i).getStationId();
            Long stationIdB = betweenRouteStationResultList.get(i + 1).getStationId();
            int hourThis = calendar.get(Calendar.HOUR_OF_DAY);
            int hourNext = 0;
            int minuteThis = calendar.get(Calendar.MINUTE);
            int minuteNext = 0;
            int minnuteRate = minuteThis % 10; //折线占比用（0-10）
            int minnute10This = (int) Math.ceil((minuteThis / 10) + 1) * 10;
            int minnute10Next = 0;
            if (minnute10This == 60) {
                hourNext = hourThis + 1;
                minnute10Next = 10;
            } else {
                hourNext = hourThis;
                minnute10Next = minnute10This + 10;
            }
            //该时间
            String keyThis = hourThis + "_" + minnute10This + "_" + intersiteTimeParams.getDirection() +
                    "_" + stationIdA + "_" + stationIdB;


            double avgtimeThis = 0.0;
            if (adrealWasteTimeMap.get(keyThis) != null) {
                avgtimeThis = adrealWasteTimeMap.get(keyThis);
            } else {
                System.out.println(keyThis);
                minnuteRate = 10;
            }

            //下个时间
            String keyNext = hourNext + "_" + minnute10Next + "_" + intersiteTimeParams.getDirection() +
                    "_" + stationIdA + "_" + stationIdB;
            double avgtimeNext = 0.0;
            if (adrealWasteTimeMap.get(keyNext) != null) {
                avgtimeNext = adrealWasteTimeMap.get(keyNext);
            } else {
                System.out.println(keyNext);
                minnuteRate = 0;

            }
            //按该时间段耗时和上一时间段的耗时，按折线来换算出平均时间
            //比如现在是 7:07分，它的折线占比：minnuteRate为7，则拿7:10的平均耗时：avgtimeThis；7:20分的的平均耗时：avgtimeNext来换算出avgtimeConvert
            //avgtimeConvert =(10-7)*avgtimeThis/10 +7*avgtimeNext/10
            int avgtimeConvert = (int) ((10 - minnuteRate) * avgtimeThis / 10 + minnuteRate * avgtimeNext / 10);
            //System.out.println(betweenRouteStationResultList.get(i).getStationId()+betweenRouteStationResultList.get(i).getRouteStationName()+":" +avgtimeConvert);
            calendar.add(Calendar.SECOND, avgtimeConvert); //时间加上耗时
        }
        Double wasteTimeTotal = Double.valueOf((calendar.getTime().getTime() - adTime.getTime()) / 1000);
        return wasteTimeTotal;
    }

    @Override
    public Double getIntersiteTime(IntersiteTimeParams intersiteTimeParams) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(intersiteTimeParams.getAdTime());
        Map<String, Double> adrealWasteTimeMap = null;
        //查询线路对应方向站点列表
        List<RouteStationResult> routeStationResultList = this.getRouteStationResult(intersiteTimeParams.getRouteId(), intersiteTimeParams.getDirection());
        if(Objects.nonNull(StringUtil.MSG_KEY)&&StringUtil.GENERATE_MSG.containsKey(StringUtil.MSG_KEY)){
            Double distance = getIntersiteTimeByStationDistance(intersiteTimeParams,routeStationResultList);
            if(distance!=null) {
            	return
                        distance;
            }
        }
        try {
            adrealWasteTimeMap = this.getIntersiteTimeResult(intersiteTimeParams.getRouteId(), intersiteTimeParams.getDirection(), String.valueOf(cal.get(Calendar.DAY_OF_WEEK) - 1));
        } catch (Exception e) {
        	e.printStackTrace();
        	//获取大数据站间用时失败,则通过标2个站点之间的站间距比上总的站间距估算站间用时
            LOGGER.info("获取大数据站间用时失败,则通过标2个站点之间的站间距比上总的站间距估算站间用时");
            StringUtil.putGenerateMsg();
        	return getIntersiteTimeByStationDistance(intersiteTimeParams,routeStationResultList);
//            throw new MyException("-1", "获取大数据站间用时失败");
        }

        //根据开始站点，结束站点+志武的站间耗时+该线路所有的站点算出这2个站的耗时
        Double wasteTimeTotal = this.getWasteTimeTotal(intersiteTimeParams, adrealWasteTimeMap, routeStationResultList);
        if (intersiteTimeParams.getRouteFromId() == null || intersiteTimeParams.getRouteToId() == null || intersiteTimeParams.getWasteTime() == null) {
            return wasteTimeTotal;
        }
        intersiteTimeParams.setFromRouteStaId(intersiteTimeParams.getRouteFromId());
        intersiteTimeParams.setToRouteStaId(intersiteTimeParams.getRouteToId());
        Double allTime = this.getWasteTimeTotal(intersiteTimeParams, adrealWasteTimeMap, routeStationResultList);
        if(allTime==0){
        	throw new MyException("-1", "获取大数据全程用时为0");
        }
        return new BigDecimal(wasteTimeTotal).divide(new BigDecimal(allTime), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(intersiteTimeParams.getWasteTime().toString())).multiply(new BigDecimal("60")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Double getIntersiteTimeByStationDistance(IntersiteTimeParams intersiteTimeParams,List<RouteStationResult> routeStationResultList){
        //目标2个站点之间的站点
        if(intersiteTimeParams.getWasteTime()==null) {
        	return null;
        }
        for(RouteStationResult routeStationResult:routeStationResultList) {
        	if(routeStationResult.getStationDistance()==null) {
        		return null;
        	}
        }
    	List<RouteStationResult> betweenRouteStationResultList = this.getBetweenRouteStationResult(routeStationResultList, intersiteTimeParams.getDirection(), intersiteTimeParams.getFromRouteStaId(), intersiteTimeParams.getToRouteStaId());
        for(RouteStationResult routeStationResult:betweenRouteStationResultList) {
        	if(routeStationResult.getStationDistance()==null) {
        		return null;
        	}
        }
        Double betweenRouteStationDistance = betweenRouteStationResultList.stream().mapToDouble(RouteStationResult::getStationDistance).sum();
        Double totalRouteStationDistance = routeStationResultList.stream().mapToDouble(RouteStationResult::getStationDistance).sum();
        if(totalRouteStationDistance==0) {
        	return null;
        }
        return new BigDecimal(betweenRouteStationDistance).divide(new BigDecimal(totalRouteStationDistance), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(intersiteTimeParams.getWasteTime().toString())).multiply(new BigDecimal("60")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Value("${bigData.service.url}")
    public String dataUrl;

    public List<TripLogSimpleVo> getTripLogSimpleInfoByDataUrl(long routeId, long fromRouteStaId, long toRouteStaId, Date tripLogBeginTime) {
        String dateString = DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        JSONObject params = new JSONObject();
        params.put("businessID", "00125");
        params.put("page", 1);
        params.put("pageSize", 10);
        params.put("appName", "appName");

        JSONObject reqData = new JSONObject();
        reqData.put("route_id", String.valueOf(routeId));
        reqData.put("triplog_begin_time", dateString);
        reqData.put("from_route_sta_id", String.valueOf(fromRouteStaId));
        reqData.put("to_route_sta_id", String.valueOf(toRouteStaId));
        params.put("data", reqData);

        JSONObject result = HttpClientUtils.httpPost(dataUrl, params);
        if (result.getInteger("retCode") != 0) {
            LOGGER.error("历史周转时间接口调用失败 msg={}", result.getString("retMsg"));
            throw new MyException("500", "历史周转时间接口调用失败," + result.getString("retMsg"));
        }
        JSONObject retData = result.getJSONObject("retData");
        JSONArray list = retData.getJSONArray("list");
        List<TripLogSimpleVo> tripLogSimpleVos = list.toJavaList(TripLogSimpleVo.class);
        return tripLogSimpleVos;
    }

    @Override
    public Integer getWasteTime(long routeId, long fromRouteStaId, long toRouteStaId, Date tripLogBeginTime, Long routeFromId, Long routeToId, Integer wasteTime) {
        List<TripLogSimpleVo> list = this.getTripLogSimpleInfoByDataUrl(routeId, fromRouteStaId, toRouteStaId, tripLogBeginTime);
        if (list.size() < 1) {
            LOGGER.error("大数据00125接口调用成功，但没有数据 fromRouteStaId={},toRouteStaId={},tripLogBeginTime={}",fromRouteStaId,toRouteStaId, DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","接口调用成功，但没有数据，fromRouteStaId：" + fromRouteStaId + " toRouteStaId：" + toRouteStaId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }
        List<Date> fromList = list.stream().filter(item -> item.getRouteStaId() == fromRouteStaId).map(TripLogSimpleVo::getDepartTime).collect(Collectors.toList());
        if (fromList.size() < 1 || fromList.get(0) == null) {
            LOGGER.error("大数据00125接口调用成功，起始站点没有departTime, fromRouteStaId={},toRouteStaId={},tripLogBeginTime={}",fromRouteStaId,toRouteStaId,DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","起始站点没有departTime, fromRouteStaId：" + fromRouteStaId + " toRouteStaId：" + toRouteStaId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }

        Date departDate = fromList.get(0);

        List<Date> toList = list.stream().filter(item -> item.getRouteStaId() == toRouteStaId).map(TripLogSimpleVo::getArrivalTime).collect(Collectors.toList());
        if (toList.size() < 1 || toList.get(0) == null) {
            LOGGER.error("大数据00125接口调用成功，末尾站点没有arrivalTime, fromRouteStaId={},toRouteStaId={},tripLogBeginTime={}",fromRouteStaId,toRouteStaId,DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","末尾站点没有arrivalTime, fromRouteStaId：" + fromRouteStaId + " toRouteStaId：" + toRouteStaId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }

        Date arriveDate = toList.get(0);

        long stamp = arriveDate.getTime() - departDate.getTime();

        if (routeFromId == null || routeToId == null || wasteTime == null) {
            return (int) Math.ceil(stamp / 1000);
        }

        list = this.getTripLogSimpleInfoByDataUrl(routeId, routeFromId, routeToId, tripLogBeginTime);
        if (list.size() < 1) {
            LOGGER.error("大数据00125接口调用成功，但没有数据 routeFromId={},routeToId={},tripLogBeginTime={}",routeFromId,routeToId, DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","接口调用成功，但没有数据，routeFromId：" + routeFromId + " routeToId：" + routeToId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }
        fromList = list.stream().filter(item -> item.getRouteStaId() == routeFromId.longValue()).map(TripLogSimpleVo::getDepartTime).collect(Collectors.toList());
        if (fromList.size() < 1 || fromList.get(0) == null) {
            LOGGER.error("大数据00125接口调用成功，起始站点没有departTime, routeFromId={},routeToId={},tripLogBeginTime={}",routeFromId,routeToId,DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","起始站点没有departTime, routeFromId：" + routeFromId + " routeToId：" + routeToId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }

        departDate = fromList.get(0);

        toList = list.stream().filter(item -> item.getRouteStaId() == routeToId.longValue()).map(TripLogSimpleVo::getArrivalTime).collect(Collectors.toList());
        if (toList.size() < 1 || toList.get(0) == null) {
            LOGGER.error("大数据00125接口调用成功，末尾站点没有arrivalTime, routeFromId={},routeToId={},tripLogBeginTime={}",routeFromId,routeToId,DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            throw new MyException("500", "该日期数据异常，请选择其它日期","末尾站点没有arrivalTime, routeFromId：" + routeFromId + " routeToId：" + routeToId + " tripLogBeginTime:" + DateUtil.getDateString(tripLogBeginTime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        }
        arriveDate = toList.get(0);

        long all = arriveDate.getTime() - departDate.getTime();

        double result = new BigDecimal(stamp).divide(new BigDecimal(all), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(wasteTime)).doubleValue();

        return (int) Math.ceil(result / 1000);
    }
    
    @Override
    public void clearCache(Long routeId) {
    	if(routeId==null) {
    		allRouteAdrealWasteTimeMap.clear();
    		allRouteFullWasteTimeMap.clear();
    	}else {
    		for(int i=1;i<=7;i++) {
    			for(int j=0;j<=1;j++) {
    				String key=routeId+"_"+i+"_"+j;
        			allRouteAdrealWasteTimeMap.remove(key);
    			}
    		}
    	}
    }
}
