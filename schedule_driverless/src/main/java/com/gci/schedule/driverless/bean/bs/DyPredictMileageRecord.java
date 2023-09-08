package com.gci.schedule.driverless.bean.bs;

import lombok.Data;

import java.util.Date;

@Data
public class DyPredictMileageRecord {
    private Long id;

    private Long routeId;

    private String routeName;

    private Long busId;

    private String busName;

    private Long dischargeStartSoc;

    private Date nowtime;

    private Long minSoc;

    private Double predictMileage;

    private Long triplogId;

    private Double totalMileage;

    //预测里程差值
    private Double predictMileageLoss;

    //can总线里程差值
    private Double totalMileageLoss;

    private String obuId;

    private String direction;

}