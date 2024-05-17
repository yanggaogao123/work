package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-10-17 上午10:58
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeRoute {
    private Long busId;
    private Long toRouteId;
    private Long userId;
}
