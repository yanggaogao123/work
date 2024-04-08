package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleParamsDriverless {

    private Long routeId;

    private Integer doubleRunMax; //无人车最大运营时长

    private String upDirection; //0:上行无出车终站;1:上行有出车终站

    private String downDirection; //0:下行无出车终站;1:下行有出车终站

    private Integer vehicleContent; //车内容量

    private Integer endDirection;//停场方向(0:上行总站;1:下行总站;2:两边总站)

    private Integer upBeginNum;//上行停场车辆数

    private Integer downBeginNum;//下行停场车辆数

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private String upFirstTime;

    private String upLatestTime;

    private String downFirstTime;

    private String downLatestTime;

    private Integer anchorDurationMin;//无人车最小停站时长

}