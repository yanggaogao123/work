package com.gci.schedule.driverless.bean;

import com.gci.schedule.driverless.bean.bs.DyStationChargeRecord;
import lombok.Data;

import java.util.List;

@Data
public class ChargeTableInfo {
    //调度方式，1：充电调度，2：置换调度
    private String type;

    private Long busId;

    private String busName;

    private Double soc;

    //续航里程
    private Double predictmileage;

    //车辆剩余所需营运里程
    private Double remainRunMileage;

    private Double needSoc;

    private String direction;

    //保底电量
    private Double limitSoc;

    //保底里程
    private Double mileage;

    private String openWindowTips;

    //建议充电站点名称
    private String chargeStationName;

    List<DyStationChargeRecord> chargeRecordList;

    List<ChangeBusInfo> changeBusInfo;
}
