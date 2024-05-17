package com.gci.schedule.driverless.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-02-28 11:58 上午
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
public class MiddlePlan {
    private Date planTime;
    private Long routeSubId; //短线taskId
    private Long lastStaId; //短线终端
}
