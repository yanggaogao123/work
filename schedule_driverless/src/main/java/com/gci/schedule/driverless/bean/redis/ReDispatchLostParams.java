package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO: 发班重打
 * @Date: 2021-01-16 下午1:46
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReDispatchLostParams {
    private String id;
    private String direction;
    private Long busId;
    private String busName;
    private Integer serviceType;
    private String serviceName;
    private Long taskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(name = "tripBeginTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripBeginTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(name = "newPreTripBeginTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date newPreTripBeginTime;
    // 新调度时间 格式 -> 时:分
    private String newTripBeginTime;
    private String calCulMark;
}
