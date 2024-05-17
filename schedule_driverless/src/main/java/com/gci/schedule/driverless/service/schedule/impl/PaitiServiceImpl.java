package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.bean.paiti.Bus;
import com.gci.schedule.driverless.bean.paiti.Route;
import com.gci.schedule.driverless.mapper.PaitiMapper;
import com.gci.schedule.driverless.service.schedule.PaitiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaitiServiceImpl implements PaitiService {
    @Autowired
    private PaitiMapper paitiMapper;


    private static Map<String, Integer> shiftTypeMap;
    private static Map<String, Integer> directionMap;
    static {
        //0：早半班；1：晚半班；2：中班；3：双班(中停);4：单班(中停)：5:双班
        shiftTypeMap = new HashMap<>();
        shiftTypeMap.put("早半班",0);
        shiftTypeMap.put("晚半班",1);
        shiftTypeMap.put( "中班",2);
        shiftTypeMap.put("双班(中停)",3);
        shiftTypeMap.put("单班(中停)",4);
        shiftTypeMap.put("双班",5);

        directionMap = new HashMap<>();
        directionMap.put("上行",0);
        directionMap.put("下行",1);

    }

    @Override
    public Map<String, List<Bus>> queryDicheOrganIdAndBusMap() {
        List<Bus> diancheBusList = paitiMapper.queryDicheBusList();
        Map<String, List<Bus>> dicheOrganIdAndBusMap = diancheBusList.stream().collect(Collectors.groupingBy(Bus::getOrganId));
        return dicheOrganIdAndBusMap;
    }

    @Override
    public Map<String, List<Route>> queryDicheRouteCodeAndOrganIdMap() {
        List<Route> diancheRouteList = paitiMapper.queryDicheRouteList();
        Map<String, List<Route>> dicheRouteCodeAndOrganIdMap = diancheRouteList.stream().collect(Collectors.groupingBy(Route::getRouteCode));
        return dicheRouteCodeAndOrganIdMap;
    }

}
