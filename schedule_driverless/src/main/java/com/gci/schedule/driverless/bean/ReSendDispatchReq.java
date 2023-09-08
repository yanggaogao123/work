package com.gci.schedule.driverless.bean;

import lombok.Data;

/**
 * @author liangzc
 * @date 2023/7/3 15:32
 **/
@Data
public class ReSendDispatchReq {

    private Integer busId;

    private String obuId;

    private String startTime;

    private Long taskId;

    private String mark;

    private String planDate;

    private Long routeId;

    private String routeCode;

    private String direction;

}
