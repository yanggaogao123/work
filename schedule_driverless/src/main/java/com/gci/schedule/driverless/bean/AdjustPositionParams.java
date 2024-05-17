package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdjustPositionParams {
    private Long routeId;
    private Long fromBusId;
    private Long toBusId;
}
