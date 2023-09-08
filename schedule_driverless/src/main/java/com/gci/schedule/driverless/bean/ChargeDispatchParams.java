package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.List;

@Data
public class ChargeDispatchParams {
    private List<ChargeBusParams> chargeBusParams;

    private Long routeId;

    private String remark;

    ////调度方式，1：充电调度，2：置换调度，3：什么都不选
    private String operateType;
}
