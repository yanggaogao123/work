package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-04-07 10:07
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskType {
    private String serviceType;
    private String serviceName;
    private String sort;
}
