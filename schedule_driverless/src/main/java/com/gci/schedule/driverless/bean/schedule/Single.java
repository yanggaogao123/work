package com.gci.schedule.driverless.bean.schedule;

import lombok.Data;

import java.util.Date;

//单班车设定
@Data
public class Single {

    private Long routeId;

    private String stopBeginTime; //中停开始时间

    private String stopEndTime; //中停截止时间

    private String runBeginTime; //复行开始时间

    private String endBeginTime; //收工开始时间

    private String earliestBusNumber; //最早车位

    private Date stopBeginTimeDate; //中停开始时间

    private Date stopEndTimeDate; //中停截止时间

    private Date runBeginTimeDate; //复行开始时间

    private Date endBeginTimeDate; //收工开始时间

    private Long templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Integer upBusCount; //上行中停车数

    private Integer downBusCount; //下行中停车数
}