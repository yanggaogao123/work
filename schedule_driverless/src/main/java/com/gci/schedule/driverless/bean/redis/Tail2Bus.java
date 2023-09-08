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
 * @Date: 2021-01-30 下午4:58
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tail2Bus {
    private Long routeId;
    private Long busId;
    private String busName;
    private String direction;
    private Integer waitTailBus; //尾二是否等尾车再发班 1:是 0:否
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
}
