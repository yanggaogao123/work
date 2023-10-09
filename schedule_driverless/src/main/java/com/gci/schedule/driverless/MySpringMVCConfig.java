package com.gci.schedule.driverless;

import com.gci.schedule.driverless.interceptor.RouteInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: allan
 * @Date: 2019/4/29 15:21
 */
@SpringBootConfiguration
public class MySpringMVCConfig implements  WebMvcConfigurer {
    @Autowired
    private RouteInterceptor routeInterceptor;
    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/**");
    }*/


    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(routeInterceptor).addPathPatterns("/scheduleParams","/schedulePlanParking","/scheduleAnalogy","/scheduleTrip","/scheduleStationPassenger" ,"/websocket","/scheduleTemplate","/routeGenerate","/organTree","/routeSchedulePlan","/schedulePlanParkingNew","/tripLogParking","/scheduleRouteConfig","/scheduleParamsForYunNao","/scheduleAnalogyForYunNao","/routeGenerateForYunNao","/routeSchedulePlanForYunNao","/ruyue");
        //registry.addInterceptor(routeInterceptor).addPathPatterns("/route/getRouteList","/schedulePlanParking");
        registry.addInterceptor(routeInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/login", "/register","/stationPassenger/**","/static/**",
                        "/schedulePlanReSet/**","/scheduleTemplate/selectDetailList",
                        "/scheduleParamsAnchor/selectList","/scheduleParamsSingle/selectList",
                        "/scheduleParamsSLStaSet/selectList","/simulation/getIntersiteTime",
                        "/scheduleDispatchPlan/**","/schedule/generate*","/scheduleCompetePlan/generate*","/scheduleParamsClasses/selectList",
                        "/scheduleParamsEat/selectList", "/scheduleParamsRoute/selectList", "/dateUpdate/**");
    }

}
