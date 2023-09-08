package com.gci.schedule.driverless.bean.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteParams {

    private Long routeId;

    private Integer singleRunMax; //单班车最大运营时长

    private Integer doubleRunMax; //双班车最大运营时长

    private String earlyEndTime; //早收开始时间

    private String upLastestBeginTime; //上行最晚出车时间

    private String downLastestBeginTime; //下行最晚出车时间

    private String upDirection; //0:上行无出车终站;1:上行有出车终站

    private String downDirection; //0:下行无出车终站;1:下行有出车终站

    private Date earlyEndTimeDate;

    private Date upLastestBeginTimeDate;

    private Date downLastestBeginTimeDate;

    private Integer vehicleContent; //车内容量

    private Integer maxBusNumber;//最大配车数

    private String endDirection;//停场方向(0:上行总站;1:下行总站;2:两边总站)

    private Integer upBeginNum;//上行停场车辆数

    private Integer downBeginNum;//下行停场车辆数

    private Long templateId; //模板Id

    private Integer isRegularBus; //是否定点班车

    private Integer isBackInsert; //是否反插

    private Integer arriveStaStopTimeUp; //上行到站即走停站时间

    private Integer arriveStaStopTimeDown;  //下行到站即走停站时间

}


