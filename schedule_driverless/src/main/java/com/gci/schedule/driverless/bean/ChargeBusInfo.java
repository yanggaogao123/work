package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChargeBusInfo {
    private Long routeId;
    private List<ChargeTableInfo> chargeTableInfoList;
    @JSONField(name = "expireTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Boolean isExpired() {
        return expireTime == null ? new Date().getTime() > createTime.getTime() + 3 * 60 * 1000
                : new Date().getTime() > expireTime.getTime();
    }
}
