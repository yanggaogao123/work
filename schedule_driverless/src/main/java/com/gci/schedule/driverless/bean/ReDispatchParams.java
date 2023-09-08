package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-01-19 下午4:17
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReDispatchParams {
    private Long routeId;
    private List<String> ids;
}
