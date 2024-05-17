package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-07-20 17:53
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinalTripLog {
    private Long busId;
    private String serviceType;
    private Date tripBeginTime;
}
