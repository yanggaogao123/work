package com.gci.schedule.driverless.feign;


import com.gci.schedule.driverless.bean.ChangeRoute;
import com.gci.schedule.driverless.bean.Dispatch;
import com.gci.schedule.driverless.bean.NoticeReq;
import com.gci.schedule.driverless.bean.TaskType;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.scheduleD.Route;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-17 12:59 下午
 * @version: v1.0
 * @Modified by:
 **/
public interface DispatchApp {

    @RequestLine("POST /send/dispatch")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String dispatch(Dispatch dispatch);

    /**
     * 发通知指令
     *
     * @param noticeReq
     * @return
     */
    @RequestLine("POST /send/notice")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String sendNotice(NoticeReq noticeReq);

    @RequestLine("POST /update/changeRoute")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    String changeRoute(ChangeRoute dispatch);

    @RequestLine("GET /taskType/getAll")
    CodeResp<List<TaskType>> getTaskType();

    @RequestLine("GET /route/get/{routeId}")
    CodeResp<Route> getRouteById(@Param("routeId") Long routeId);
}