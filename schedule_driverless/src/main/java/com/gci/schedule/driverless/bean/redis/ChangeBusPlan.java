package com.gci.schedule.driverless.bean.redis;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 换车任务请求体
 *
 * @author liangzc
 * @date 2023/6/28 11:51
 **/
@Data
public class ChangeBusPlan {

    private Long busId;

    private String busName;

    private Integer changeBusTime;

    private Integer createType;

    private String direction;

    private Long firstStationId;

    private Long lastRouteStaId;

    private String remark;

    private Long routeId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date runDate;

    private Long minStop;    //最小停站时间

    private Integer enterOrder; //是否按车位发班 0:否 1:是

    private Integer twoLongOneShort; //是否两长一短

    private Integer serviceType;

    private Long taskId;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tripBeginTime;

    private Long employeeId;

    private String employeeName;
    /**
     * 换车前的吃饭数据
     */
    private EatBus eatBus;

}

