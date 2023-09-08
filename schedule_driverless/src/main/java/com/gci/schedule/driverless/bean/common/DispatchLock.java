package com.gci.schedule.driverless.bean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchLock {
    private Long routeId;
    private Long busId;
    private Long stationId;

    private Boolean lock;  //去掉锁标示
    private String clientId; //客户端Id
    private String lockKey; //锁key
}
