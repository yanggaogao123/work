package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-04-15 16:17
 * @version: v1.0
 * @Modified by:
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastRoundBus {
    private Long routeId;
    private String direction;
    private Long busId;
    private String busName;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
}
