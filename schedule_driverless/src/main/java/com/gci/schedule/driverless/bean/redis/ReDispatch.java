package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gci.schedule.driverless.bean.RunBus;
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
public class ReDispatch {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JSONField(name = "newTripBeginTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date newTripBeginTime;
    private String calCulMark;
    private String remark;      //备注
    private String checkRow;    //是否勾选 1-是 0-否

    @JSONField(serialize = false)
    public Boolean isReDispatched(RunBus runBus) {
        if (runBus == null
                || runBus.getTripBeginTime() == null) {
            return false;
        }
        return runBus.getTripBeginTime().equals(getTripBeginTime())
                || runBus.getTripBeginTime().equals(getNewTripBeginTime());
    }
}
