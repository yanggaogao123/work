package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO: 调车位设置
 * @Date: 2020-11-23 上午9:32
 * @version: v1.0
 * @Modified by:
 **/
@Data
public class AdjustPosition {
    private Long fromRouteId;
    private Long routeId;
    private String direction;
    private Long busId;
    private String busName;
    @JSONField(name = "tripBeginTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripBeginTime;
    private Long afterBusId;
    private String afterBusName;
    private Long userId;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
