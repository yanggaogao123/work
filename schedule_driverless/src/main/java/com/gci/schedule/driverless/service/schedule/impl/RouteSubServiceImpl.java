package com.gci.schedule.driverless.service.schedule.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.gci.schedule.driverless.bean.RouteStationResult;
import com.gci.schedule.driverless.bean.scheduleD.BsRouteSub;
import com.gci.schedule.driverless.service.schedule.RouteSubService;
import com.gci.schedule.driverless.util.HttpUtil;
import com.gci.schedule.driverless.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteSubServiceImpl implements RouteSubService {
    @Value("${dispatch.auto.server.url}")
    private String dispatchAutoServerUrl;

    private Map<Long, String> routeSubListStr = new HashMap<>();


    private String getListStrByRouteId(Long routeId) {
        String str = routeSubListStr.get(routeId);
        if (StringUtil.isNotBlank(str)) {
            return str;
        }
        String result = HttpUtil.getString(dispatchAutoServerUrl + "/routeSub/getByRoute/"+routeId);
        routeSubListStr.put(routeId, result);
        return result;
    }

    @Override
    public List<BsRouteSub> getListByRouteId(Long routeId){
        String result = getListStrByRouteId(routeId);
        List<BsRouteSub> bsRouteSubList = JSON.parseObject(JSON.parseObject(result).getJSONArray("data").toJSONString(), new TypeReference<List<BsRouteSub>>() {});
        return bsRouteSubList;
    }
}
