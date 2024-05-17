package com.gci.schedule.driverless.bean.bs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-08-31 15:34
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefuelDto {
    private Long routeId;
    private Long busId;
    private Long routeSubId;
}
