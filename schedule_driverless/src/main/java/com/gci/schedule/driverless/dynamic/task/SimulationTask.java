package com.gci.schedule.driverless.dynamic.task;

import com.gci.schedule.driverless.dynamic.service.RouteStationService;
import com.gci.schedule.driverless.dynamic.service.SimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@EnableScheduling
@Component
public class SimulationTask {


    protected static final Logger logger = LoggerFactory.getLogger(SimulationTask.class);

    @Resource
    private SimulationService simulationDynamicService;

    @Autowired
    private RouteStationService routeStationDynamicService;

    //指定每天1点清除—所有线路的站间耗时Map
    //@Scheduled(cron = "0 0/1 * * * ? ") //1min
    @Scheduled(cron = "0 0 1 * * ? ")
    public void delete() {
        logger.info("清除站间耗时缓存数据");
        simulationDynamicService.clearCache(null);

        //清除站点数据
        routeStationDynamicService.clearRouteStaCache();
    }


}