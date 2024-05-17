package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO: 出场计划实体类
 * @Date: 2020-07-13 15:11
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParkingPlan {
    private Long routeId;
    private String direction;
    private Long busId;
    private Long firstStationId;
    private Double latitude;
    private Double longitude;
    @JSONField(name = "midwayPlanTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;
    private Long taskId;
    private String parkingPlan;
}
