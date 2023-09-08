package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-04-13 09:45
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePlan {
    private Long busId;
    private String busName;
    @JSONField(name = "planTime", format = "yyyyMMdd HHmmss")
    private Date planTime;
}
